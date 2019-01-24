package com.Performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Util.TimeUtil;

public class LogsHandel {
	Logger logger = LoggerFactory.getLogger(LogsHandel.class);

	public void run() {

		write(AnalysisIOS(new File("/Users/auto/Desktop18-08-09--03-02-55-192.log"), "08-09 11:15:51",
				"08-09 11:45:58"), "/Users/auto/Desktop/test/log.txt");
		write(AnalysisIOS(new File("/Users/auto/Deskt-08-09--03-02-55-192.log"), "08-09 11:50:19", "08-09 12:20:33"),
				"/Users/auto/Desktop/test/log1.txt");
	}

	// [08-08 11:43:45:696][D] Main-Thread -[PTMonitor:117]:
	// [Performance]应用内存:142MB,剩余内存:577MB,应用CPU:45.70%,系统CPU:,帧率:26.00,发送数据:,接收数据:,电量:,美妆:,滤镜:,磨皮:,贴纸:
	private List<Map<String, String>> AnalysisIOS(File file, String stime, String etime) {
		BufferedReader reader = null;
		List<Map<String, String>> list = new ArrayList<>();
		String dateformat = "MM-dd HH:mm:ss";
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String time = null;
				String items = null;
				Matcher matcher = Pattern.compile("\\[([\\d:\\s-]+)\\][\\S\\s]+\\[Performance\\]([\\S\\s]+)")
						.matcher(line);
				if (matcher.find()) {
					time = matcher.group(1);
					items = matcher.group(2);
					if (TimeUtil.getTime(dateformat + ":SSS", time) >= TimeUtil.getTime(dateformat, stime)
							&& TimeUtil.getTime(dateformat + ":SSS", time) <= TimeUtil.getTime(dateformat, etime)) {
						Map<String, String> map = new LinkedHashMap<>();
						map.put("time", time);
						for (String item : items.split(",")) {
							String[] strings = item.split(":");// 应用内存:142MB
							if (strings.length == 2) {
								String name = strings[0];
								String namevalue = strings[1];
								if (!namevalue.equals("")) {
									Matcher value_matcher = Pattern.compile("([\\d.]{0,})([\\S\\s]{0,})")
											.matcher(namevalue);
									String value = null;
									String unit = null;
									if (value_matcher.find()) {
										value = value_matcher.group(1);
										unit = value_matcher.group(2);
										map.put(name, value);
										map.put(name + "单位", unit);
									}
								}
							}
						}
						list.add(map);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Exception", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
		return list;
	}

	private List<Map<String, String>> avgdata(List<Map<String, String>> list, int times) {
		List<Map<String, String>> result_list = new ArrayList<>();
		int count = 0;
		float sum = 0;
		String name = "";
		for (Map<String, String> map : list) {
			count++;
			// time,08-08
			// 15:02:26:650,应用内存,169,应用内存单位,MB,剩余内存,230,剩余内存单位,MB,应用CPU,46.30,应用CPU单位,%,帧率,26.00,帧率单位,
			for (Entry<String, String> entry : map.entrySet()) {
				name = entry.getKey();
				if (HelperUtil.isNumber(entry.getValue())) {
					sum += Float.parseFloat(entry.getValue());
				}
			}
			map.put(name, sum + "");
		}

		return result_list;
	}

	private void write(List<Map<String, String>> list, String filepath) {
		for (Map<String, String> map : list) {
			StringBuffer buffer = new StringBuffer();
			for (Entry<String, String> entry : map.entrySet()) {
				buffer.append(entry.getKey() + "," + entry.getValue() + ",");
			}
			HelperUtil.file_write_line(filepath, buffer.toString().substring(0, buffer.toString().length() - 1) + "\n",
					true);
		}
	}
}
