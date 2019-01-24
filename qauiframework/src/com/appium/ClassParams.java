package com.appium;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.HelperUtil;
import com.log.SceneLogUtil;

public class ClassParams {
	Logger logger = LoggerFactory.getLogger(ClassParams.class);
	SceneLogUtil oplog;
	String params;
	Map<String, String> strMap = new HashMap<>();
	Map<String, Integer> intMap = new HashMap<>();
	Map<String, Double> doubleMap = new HashMap<>();

	public ClassParams(SceneLogUtil oplog, String params) {
		this.oplog = oplog;
		this.params = params.trim();
		setParams();
	}

	/**
	 * 设置场景参数
	 */
	private void setParams() {
		if (!params.equals("") && !params.matches(Cconfig.REGEX_FORMAT)) {
			oplog.logError("场景参数错误:" + params);
		} else {
			logger.info("参数:" + params);
			for (String str : params.split(";")) {
				String[] strings = str.split("=");
				if (strings.length == 2) {
					strings[0] = strings[0].trim();
					strings[1] = strings[1].trim();
					if (HelperUtil.isInteger(strings[1])) {
						intMap.put(strings[0], Integer.parseInt(strings[1]));
						oplog.logInfo("整数参数:" + strings[0] + "=" + strings[1]);
					} else if (HelperUtil.isDecimal(strings[1])) {
						doubleMap.put(strings[0], Double.parseDouble(strings[1]));
						oplog.logInfo("小数参数:" + strings[0] + "=" + strings[1]);
					} else {
						if (strings[1].equals("\"\"")) {// 当参数值为""时,置空
							strings[1] = "";
						}
						strMap.put(strings[0], strings[1]);
						oplog.logInfo("字符串参数:" + strings[0] + "=" + strings[1]);
					}
				}
			}
		}
	}

	/**
	 * 获取整数
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInt(String key) {
		if (intMap.get(key) == null)
			oplog.logError("无整数参数:" + key);
		return intMap.get(key);
	}

	/**
	 * 获取字符串
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		if (strMap.get(key) == null)
			oplog.logError("无字符串参数:" + key);
		return strMap.get(key);
	}

	/**
	 * 获取小数
	 * 
	 * @param key
	 * @return
	 */
	public Double getDouble(String key) {
		if (doubleMap.get(key) == null)
			oplog.logError("无小数参数:" + key);
		return doubleMap.get(key);
	}
}
