package com.DataHandel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Util.TimeUtil;
import com.constant.Cconfig;
import com.general.LineChartFXUI;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class DataHandel {
	Logger logger = LoggerFactory.getLogger(DataHandel.class);
	LineChartFXUI lineChartFXUI;
	String[] seriesnames;// 折线名称
	Pattern data_format;// 数据格式
	int group_num;// 数据组个数
	String group_type;// 数据组提取类型
	int group_interval;// 间隔时间
	String type;// 类型
	long drawing_x_count = 0;
	// data
	Map<String, Map<String, Number>> groupMap = new HashMap<>();
	int group_num_count = 0;// 数据组计数
	long stime_interval = 0;// 间隔起始时间
	// Statistics
	Map<String, Map<String, Number>> statisticsMap = new LinkedHashMap<>();
	boolean isstatistics = false;

	public DataHandel(LineChartFXUI lineChartFXUI) {
		// TODO Auto-generated constructor stub
		this.lineChartFXUI = lineChartFXUI;
	}

	/**
	 * 设置,并初始化
	 * 
	 * @param settingsMap
	 */
	public void setSettingsMap(Map<String, String> settingsMap) {
		// lineChartFXUI.clearSeries();
		// init
		statisticsMap.clear();
		groupMap.clear();
		seriesnames = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_NAME).split(";");
		data_format = Pattern.compile(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_REGEX));
		group_num = Integer.parseInt(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_NUM));
		group_type = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_TYPE);
		group_interval = Integer.parseInt(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_INTERVAL));
		type = settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_TYPE);
		drawing_x_count = 0;
		for (String name : seriesnames) {// 创建新折线
			if (!name.equals(Cconfig.DATAHANDEL_SETTINGS_NAME_INVALID)
					&& !name.equals(Cconfig.DATAHANDEL_SETTINGS_NAME_FLAG)
					&& !name.equals(Cconfig.DATAHANDEL_SETTINGS_NAME_TIME)) {
				Series<String, Number> series = new XYChart.Series<String, Number>();
				lineChartFXUI.addSeries(series, name);
				groupMap.put(name, new LinkedHashMap<String, Number>());// 增加一组数据组列表
				Map<String, Number> map = new HashMap<>();
				map.put("count", 0);
				map.put("avg", 0);
				map.put("max", Double.MIN_VALUE);
				map.put("min", Double.MAX_VALUE);
				map.put("sum", 0);
				statisticsMap.put(name, map);// 统计map
			}
		}
	}

	/**
	 * 处理每一行数据,生成折线图,并统计数据
	 * 
	 * @param line
	 * @return
	 */
	public boolean handelLine(String time, Matcher matcher) {
		long etime_interval = TimeUtil.getTime();
		if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR)) {
			if (etime_interval - stime_interval < group_interval) {// 小于间隔时间不计算
				return true;
			}
		} else if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING)) {
			if (time == null) {
				time = drawing_x_count + "";
			}
		}
		if (matcher.find(0)) {// 数据提取
			for (int i = 0; i < matcher.groupCount(); i++) {
				String name = seriesnames[i];
				if (!name.equals(Cconfig.DATAHANDEL_SETTINGS_NAME_INVALID)
						&& !name.equals(Cconfig.DATAHANDEL_SETTINGS_NAME_FLAG)
						&& !name.equals(Cconfig.DATAHANDEL_SETTINGS_NAME_TIME)) {// 排除无效参数
					String valuestr = matcher.group(i + 1);
					if (!HelperUtil.isNumber(valuestr)) {
						// 特殊处理
						if (valuestr.matches("\\d+s\\d+")) {// 处理 xxxsxxx
							String[] strings = valuestr.split("s");
							valuestr = (Integer.parseInt(strings[0]) * 1000 + Integer.parseInt(strings[1])) + "";
						} else {
							return false;// 括号中提取的信息非数字
						}
					}
					double value = Double.parseDouble(valuestr);
					groupMap.get(name).put(time, Double.parseDouble(valuestr));
					// 统计数据获取
					if (isstatistics) {
						int count = statisticsMap.get(name).get("count").intValue();
						// double avg = statisticsMap.get(name).get("avg").doubleValue();
						double max = statisticsMap.get(name).get("max").doubleValue();
						double min = statisticsMap.get(name).get("min").doubleValue();
						double sum = statisticsMap.get(name).get("sum").doubleValue();
						statisticsMap.get(name).put("count", (count + 1));
//						if (avg != 0) {
//							statisticsMap.get(name).put("avg",
//									new BigDecimal(Double.toString(avg)).add(new BigDecimal(Double.toString(value)))
//											.divide(new BigDecimal(Double.toString(2D))).doubleValue());
//						} else {// 第一次
//							statisticsMap.get(name).put("avg", sum/count);
//						}
						if (value > max) {
							statisticsMap.get(name).put("max", value);
						}
						if (value < min) {
							statisticsMap.get(name).put("min", value);
						}
						statisticsMap.get(name).put("sum", sum + value);// 可能会达到最大值或者最小值
						statisticsMap.get(name).put("avg", (sum + value) / (count + 1));
					}
				}
			}
			group_num_count++;// 数据组获取一次数据,+1
			if (group_num == group_num_count) {// 数据计算满
				StringBuffer buf = new StringBuffer();
				buf.append("当前");
				for (Entry<String, Map<String, Number>> entry : groupMap.entrySet()) {
					String name = entry.getKey();
					String x = "";
					double y = 0;
					if (group_type.equals("avg")) {
						double sum = 0;
						for (Entry<String, Number> subentry : entry.getValue().entrySet()) {
							sum += subentry.getValue().doubleValue();
							x = subentry.getKey();// 最后一个的
						}
						y = sum / entry.getValue().size();
						y = HelperUtil.getDoubleDecimal(y, 3);
					} else if (group_type.equals("min")) {
						y = Double.MAX_VALUE;
						for (Entry<String, Number> subentry : entry.getValue().entrySet()) {
							if (subentry.getValue().doubleValue() < y) {
								y = subentry.getValue().doubleValue();
							}
							x = subentry.getKey();
						}
					} else if (group_type.equals("max")) {
						y = Double.MIN_VALUE;
						for (Entry<String, Number> subentry : entry.getValue().entrySet()) {
							if (subentry.getValue().doubleValue() > y) {
								y = subentry.getValue().doubleValue();
							}
							x = subentry.getKey();
						}
					}
					lineChartFXUI.addData(name, x, y);// 向折线添加数据,x选取最后一个
					// System.out.println(name + "," + x + "," + y);
					buf.append(name + "=" + y + ",");
				}
				lineChartFXUI.setNoteInfo(buf.toString().substring(0, buf.toString().length() - 1));// 注释信息
				lineChartFXUI.writeSaveData();// 写入数据
				drawing_x_count++;// draw类型时,X轴坐标+1
				// 重置数据
				group_num_count = 0;
				groupMap.entrySet().forEach(e -> e.getValue().clear());
				stime_interval = etime_interval;// 间隔时间到后开始取样,取样时间不计入间隔时间,之后再开始重新计时.如果find后计时,会导致可能采样数不够,间隔时间又大于采样时间的问题
			}
		}
		return true;
	}

	/**
	 * 是否开始统计
	 * 
	 * @param isstatistics
	 */
	public void setIsstatistics(boolean isstatistics) {
		this.isstatistics = isstatistics;
	}

	/**
	 * 清空统计数据
	 */
	public void clearStatisticsData() {
		for (Entry<String, Map<String, Number>> entry : statisticsMap.entrySet()) {
			entry.getValue().put("count", 0);
			entry.getValue().put("avg", 0);
			entry.getValue().put("max", Double.MIN_VALUE);
			entry.getValue().put("min", Double.MAX_VALUE);
			entry.getValue().put("sum", 0);
		}
	}

	/**
	 * 获取统计数据
	 * 
	 * @return
	 */
	public String getStatisticsInfo() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<String, Map<String, Number>> entry : statisticsMap.entrySet()) {
			int count = entry.getValue().get("count").intValue();
			if (count == 0) {// 无数据就返回空白
				return "等待捕捉数据...";
			}
			double avg = entry.getValue().get("avg").doubleValue();
			double max = entry.getValue().get("max").doubleValue();
			double min = entry.getValue().get("min").doubleValue();
			double sum = entry.getValue().get("sum").doubleValue();
			buffer.append("" + entry.getKey() + "\n");
			buffer.append("  统计次数=" + count + "\n");
			buffer.append("  最大值=" + (max == Double.MAX_VALUE ? "+∞" : max) + ",");
			buffer.append("  最小值=" + (min == (Double.MIN_VALUE + 999999999) ? "-∞" : min) + "\n");
			buffer.append("  平均值=" + HelperUtil.getDoubleDecimal(avg, 3) + ",");
			buffer.append("  累计(求和)=" + (sum == Double.MAX_VALUE ? "+∞" : HelperUtil.getDoubleDecimal(sum, 3)) + "\n");// 可以无穷小
		}
		// System.out.println(buffer.toString());
		return buffer.toString();
	}

	/**
	 * 获取统计map
	 * 
	 * @return
	 */
	public Map<String, Map<String, Number>> getStatisticsMap() {
		return statisticsMap;
	}
}
