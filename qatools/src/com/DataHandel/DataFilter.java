package com.DataHandel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class DataFilter {
	Logger logger = LoggerFactory.getLogger(DataFilter.class);

	List<Map<String, String>> settingsMapList;
	VBox vbox_chart;
	Map<String, DataDistribution> dataDistributionMap = new HashMap<>();
	boolean isstatistics = false;
	List<TitledPane> titledPanesList = new ArrayList<>();// Accordion accordion;动态生成不好使用

	public DataFilter(VBox vbox_chart) {
		// TODO Auto-generated constructor stub
		this.vbox_chart = vbox_chart;
	}

	/**
	 * 设置,并初始化
	 * 
	 * @param settingsMapList
	 */
	public void setSettingsMapList(List<Map<String, String>> settingsMapList) {
		this.settingsMapList = settingsMapList;
		dataDistributionMap.clear();
		titledPanesList.clear();
		vbox_chart.getChildren().clear();
		for (Map<String, String> map : settingsMapList) {
			if (map.get(Cconfig.DATAHANDEL_SETTINGS_EFFECTIVE).equals("true")) {
				DataDistribution dataDistribution = new DataDistribution(vbox_chart, titledPanesList);
				dataDistribution.setIsstatistics(isstatistics);
				dataDistribution.setSettingsMap(map);
				dataDistributionMap.put(map.get(Cconfig.DATAHANDEL_SETTINGS_FLAG), dataDistribution);
			}
		}
	}

	/**
	 * 过滤行数据
	 * 
	 * @param line
	 * @return
	 */
	public boolean filterLine(String line) {
		boolean isok = false;
		for (Entry<String, DataDistribution> entry : dataDistributionMap.entrySet()) {
			if (entry.getValue().sendLine(line)) {
				isok = true;
			}
		}
		return isok;
	}

	/**
	 * 是否开始统计
	 * 
	 * @param isstatistics
	 */
	public void setIsstatistics(boolean isstatistics) {
		this.isstatistics = isstatistics;
		for (Entry<String, DataDistribution> entry : dataDistributionMap.entrySet()) {
			entry.getValue().setIsstatistics(isstatistics);
		}
	}

	/**
	 * 获取统计数据
	 * 
	 * @return
	 */
	public String getStatisticsInfo() {
		StringBuffer stringBuffer = new StringBuffer();
		for (Entry<String, DataDistribution> entry : dataDistributionMap.entrySet()) {
			stringBuffer.append(entry.getValue().getStatisticsInfo());
		}
		return stringBuffer.toString();
	}

	/**
	 * 清空统计数据
	 */
	public void clearStatisticsData() {
		for (Entry<String, DataDistribution> entry : dataDistributionMap.entrySet()) {
			entry.getValue().clearStatisticsData();
		}
	}

	/**
	 * 是否展开
	 * 
	 * @param expanded
	 */
	public void setExpanded(boolean isexpanded) {
		for (int i = 0; i < titledPanesList.size(); i++) {
			titledPanesList.get(i).setExpanded(isexpanded);
		}
	}

	public void sortTitledPanes() {
		ObservableList<Node> list = vbox_chart.getChildren();

	}

	/**
	 * 清除,用来避免出错
	 */
	public void clear() {
		titledPanesList.clear();
		dataDistributionMap.clear();
	}

	/**
	 * 搜索指定折线图并展开,收起其它
	 * 
	 * @param key
	 */
	public void searchTitledPane(String key) {
		List<String> namelist = new ArrayList<>();
		int count = 0;
		for (int i = 0; i < titledPanesList.size(); i++) {
			String name = titledPanesList.get(i).getText();
			if (name.contains(key)) {
				count++;
				namelist.add(name + "," + count);
			}
		}
		if (namelist.size() != 0) {
			String name = AlertBoxFXUI.showOptionDialog("请选择图表名称", "共搜索到" + titledPanesList.size() + "个图表", namelist);
			if (name != null) {
				for (int i = 0; i < titledPanesList.size(); i++) {
					if (titledPanesList.get(i).getText().equals(name.split(",")[0])) {
						titledPanesList.get(i).setExpanded(true);
					} else {
						titledPanesList.get(i).setExpanded(false);
					}
				}
			}
		} else {
			AlertBoxFXUI.showMessageDialog("抱歉", "未搜索到包含\"" + key + "\"的图表");
		}

	}

	/**
	 * 得到统计数据表格
	 * 
	 * @return
	 */
	public String getStatisticsForm() {
		StringBuffer reportBuf = new StringBuffer();
		for (Entry<String, DataDistribution> entry_filter : dataDistributionMap.entrySet()) {
			String flag = entry_filter.getKey();
			int numcount = 0;
			for (Entry<String, DataHandel> entry_distribution : entry_filter.getValue().getDataHandelMap().entrySet()) {
				String name_flag = entry_distribution.getKey();
				numcount++;
				for (Entry<String, Map<String, Number>> entry_handel : entry_distribution.getValue().getStatisticsMap()
						.entrySet()) {
					String name_chart = entry_handel.getKey();
					int count = entry_handel.getValue().get("count").intValue();
					double avg = entry_handel.getValue().get("avg").doubleValue();
					double max = entry_handel.getValue().get("max").doubleValue();
					double min = entry_handel.getValue().get("min").doubleValue();
					reportBuf.append(flag + "," + numcount + "," + name_flag + "," + name_chart + "," + count + ",avg,"
							+ HelperUtil.getDoubleDecimal(avg, 3) + ",end\n");
					reportBuf.append(flag + "," + numcount + "," + name_flag + "," + name_chart + "," + count + ",max,"
							+ max + ",end\n");
					reportBuf.append(flag + "," + numcount + "," + name_flag + "," + name_chart + "," + count + ",min,"
							+ min + ",end\n");
				}
			}
		}
		return reportBuf.toString();
	}

	/**
	 * 按照asc2排序
	 * 
	 * @return
	 */
	public String sortStatisticsForm() {
		Map<String, String> map = new LinkedHashMap<>();
		for (String line : getStatisticsForm().split("\n")) {
			String[] values = line.split(",");
			map.put(values[0] + values[2] + values[3] + values[4] + values[5], line);
		}

		List<Entry<String, String>> infoIds = new ArrayList<Entry<String, String>>(map.entrySet());
		// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		StringBuffer buffer = new StringBuffer();
		for (Entry<String, String> entry : infoIds) {
			buffer.append(entry.getValue() + "\n");
		}
		return buffer.toString();
	}
}
