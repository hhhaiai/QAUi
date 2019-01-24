package com.DataHandel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.TimeUtil;
import com.constant.Cconfig;
import com.general.LineChartFXUI;

import javafx.application.Platform;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class DataDistribution {
	Logger logger = LoggerFactory.getLogger(DataDistribution.class);
	VBox vbox_chart;
	String flag;// 标识
	String[] seriesnames;// 折线名称
	Pattern data_format;// 数据格式
	int group_num;// 数据组个数
	String group_type;// 数据组提取类型
	int group_interval;// 间隔时间
	String type;// 类型

	List<TitledPane> titledPanesList;
	Map<String, DataHandel> dataHandelMap = new LinkedHashMap<>();
	Map<String, String> settingsMap;
	boolean isstatistics = false;
	// drawing
	Map<String, long[]> drawing_timeMap = new LinkedHashMap<>();;// 时间段map
	SimpleDateFormat drawing_timeformat;

	public DataDistribution(VBox vbox_chart, List<TitledPane> titledPanesList) {
		// TODO Auto-generated constructor stub
		this.vbox_chart = vbox_chart;
		this.titledPanesList = titledPanesList;
	}

	/**
	 * 设置,并初始化
	 * 
	 * @param settingsMap
	 */
	public void setSettingsMap(Map<String, String> settingsMap) {
		this.settingsMap = settingsMap;
		dataHandelMap.clear();
		drawing_timeformat = null;
		drawing_timeMap.clear();
		// init
		flag = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_FLAG);
		seriesnames = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_NAME).split(";");
		data_format = Pattern.compile(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_REGEX));
		group_num = Integer.parseInt(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_NUM));
		group_type = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_TYPE);
		group_interval = Integer.parseInt(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_INTERVAL));
		type = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_TYPE);
		if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING)) {
			String[] timeperoids = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_DRAWING_TIMEPERIOD).split(";");
			String timeformat = timeperoids[0];
			if (!timeformat.startsWith("!") && timeperoids.length > 1) {// 时间段设置有内容才解析
				drawing_timeformat = new SimpleDateFormat(timeformat);
				for (int i = 1; i < timeperoids.length; i++) {
					String timeperoid = timeperoids[i];
					String[] times = timeperoid.split("/");
					String start = times[0];
					String end = times[1];
					try {
						long s = drawing_timeformat.parse(start).getTime();
						long e = drawing_timeformat.parse(end).getTime();
						long temp = 0;
						if (e < s) {// 如果开始时间大于结束时间,则互换.
							temp = e;
							e = s;
							s = temp;
						}
						drawing_timeMap.put("时间段" + i + "=" + timeperoid, new long[] { s, e });
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						logger.error("EXCEPTION", e);
					}
				}
			}
		} else if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR)) {

		}

	}

	/**
	 * 分析line,发送折线图
	 * 
	 * @param line
	 */
	public boolean sendLine(String line) {
		String time = null;
		Matcher matcher = data_format.matcher(line);
		if (matcher.find()) {// 数据提取
			StringBuffer nameBuf = new StringBuffer();
			for (int i = 0; i < matcher.groupCount(); i++) {
				if (seriesnames[i].equals(Cconfig.DATAHANDEL_SETTINGS_NAME_FLAG)) {
					nameBuf.append("/" + matcher.group(i + 1));// 标识组成
				} else if (seriesnames[i].equals(Cconfig.DATAHANDEL_SETTINGS_NAME_TIME)) {// 正则提取时间,X轴显示
					time = matcher.group(i + 1);
				}
			}
			if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING)) {
				if (time != null && drawing_timeMap.size() > 0) {// 是否根据时间段绘图
					boolean isvalid = false;
					try {
						long current = drawing_timeformat.parse(time).getTime();
						for (Entry<String, long[]> entry : drawing_timeMap.entrySet()) {
							if (entry.getValue()[0] <= current && entry.getValue()[1] > current) {// 如果日志时间处于设置的时间段,则在折线图名称后增加时间段
								drawChart(matcher, nameBuf.toString() + "--" + entry.getKey(), time);
								isvalid = true;
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						return false;
					}
					return isvalid;// 运行完直接下一条数据,如果false则表示不再时间段内
				}
			} else if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR)) {
				if (time == null) {// 监控模式下,没有设置time提取正则时,自动使用PC时间
					time = TimeUtil.getTime("HH:mm:ss");
				}
			}
			String valuestr = nameBuf.toString().equals("") ? "无标识" : nameBuf.toString();
			drawChart(matcher, valuestr, time);// 普通执行处理line
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 创建折线
	 * 
	 * @param line
	 * @param valuestr
	 * @param time
	 */
	private void drawChart(Matcher matcher, String valuestr, String time) {
		if (dataHandelMap.get(valuestr) != null) {// 已有标记组
			dataHandelMap.get(valuestr).handelLine(time, matcher);
		} else {// 发现新的标记组
			LineChartFXUI lineChartFXUI = new LineChartFXUI(850, 300);
			// lineChartFXUI.setTitle(valuestr);
			DataHandel dataHandel = new DataHandel(lineChartFXUI);
			if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING)) {
				lineChartFXUI.setMax_xAxis(Integer.MAX_VALUE);// X轴最大数量
				lineChartFXUI.getLineChart().setVerticalGridLinesVisible(false);// 取消垂直网格线
			} else if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR)) {

			}
			dataHandel.setSettingsMap(settingsMap);
			dataHandel.setIsstatistics(isstatistics);// 是否已经开始统计
			dataHandelMap.put(valuestr, dataHandel);// 存入以标识为KEY的折线图控制MAP
			TitledPane titledPane = new TitledPane();
			titledPane.setText("[" + flag + "](" + dataHandelMap.size() + ")" + valuestr);
			// titledPane.setTooltip(new Tooltip(valuestr));
			titledPane.setExpanded(false);
			titledPanesList.add(titledPane);
//			titledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {// 每次只展开一个界面
//
//				@Override
//				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//					// TODO Auto-generated method stub
//					if (newValue) {
//						for (TitledPane t : titledPanesList) {
//							if (!t.getText().equals(titledPane.getText())) {
//								t.setExpanded(false);
//							}
//						}
//					}
//				}
//			});
			titledPane.setContent(lineChartFXUI);
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					vbox_chart.getChildren().add(titledPane);
				}
			});
			dataHandel.handelLine(time, matcher);// 最后处理数据
		}
	}

	/**
	 * 是否开始统计
	 * 
	 * @param isstatistics
	 */
	public void setIsstatistics(boolean isstatistics) {
		this.isstatistics = isstatistics;
		for (Entry<String, DataHandel> entry : dataHandelMap.entrySet()) {
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
		stringBuffer.append("[" + flag + "]\n");
		int count = 0;
		for (Entry<String, DataHandel> entry : dataHandelMap.entrySet()) {
			count++;
			stringBuffer.append("(" + count + ")" + entry.getKey() + "\n");
			stringBuffer.append(entry.getValue().getStatisticsInfo());
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}

	/**
	 * 清空统计数据
	 */
	public void clearStatisticsData() {
		for (Entry<String, DataHandel> entry : dataHandelMap.entrySet()) {
			entry.getValue().clearStatisticsData();
		}
	}

	/**
	 * 得到数据控制器
	 * 
	 * @return
	 */
	public Map<String, DataHandel> getDataHandelMap() {
		return dataHandelMap;
	}
}
