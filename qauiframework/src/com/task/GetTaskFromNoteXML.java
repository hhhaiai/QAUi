package com.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.TestCaseBean;
import com.config.XmlParse;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.IOSInfo;
import com.viewer.main.MainRun;

public class GetTaskFromNoteXML extends XmlParse {
	Logger logger = LoggerFactory.getLogger(GetTaskFromNoteXML.class);
	String deviceOS;
	String udid;
	String version = "";
	String deviceName = "";

	public GetTaskFromNoteXML(String udid, String deviceOS, String configpath) {
		// TODO Auto-generated constructor stub
		this.deviceOS = deviceOS;
		this.udid = udid;
		setDoc(configpath);
	}

	/**
	 * 得到场景配置列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getSceneMapList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map<String, Object> map : getDataFromNote()) {
			list.add(getSceneMap(map));
		}
		return list;
	}

	/**
	 * 从Note取得数据
	 * 
	 * @return
	 */
	private List<Map<String, Object>> getDataFromNote() {
		List<Map<String, String>> configMapList = getListMapByXpath("/root/failcase/item", -1, "configMap");
		List<Map<String, String>> capabilityMapList = getListMapByXpath("/root/failcase/item", -1, "capabilityMap");
		List<List<String>> methodList = getListListByXpath("/root/failcase/item", -1, "method");
		logger.info("configMapList size=" + configMapList.size());
		logger.info("capabilityMapList size=" + capabilityMapList.size());
		logger.info("methodList size=" + methodList.size());
		List<Map<String, Object>> dataList = new ArrayList<>();
		if (configMapList.size() > 0 && configMapList.size() == capabilityMapList.size()
				&& capabilityMapList.size() == configMapList.size()) {
			for (int i = 0; i < configMapList.size(); i++) {
				if (configMapList.get(i).size() <= (deviceOS.equals(Cconfig.ANDROID) ? Cparams.android_configcount
						: Cparams.ios_configcount)) {// config总数量,为了兼容旧的,所以小于等于
					Map<String, Object> map = new HashMap<>();
					map.put("config", configMapList.get(i));
					map.put("capability", capabilityMapList.get(i));
					map.put("method", methodList.get(i));
					dataList.add(map);
				}
				logger.info("xml configMap size=" + configMapList.get(i).size() + " and configcount "
						+ (deviceOS.equals(Cconfig.ANDROID) ? "android=" + Cparams.android_configcount
								: "ios=" + Cparams.ios_configcount));
			}
		}
		return dataList;
	}

	/**
	 * 返回当前任务参数设置
	 * 
	 * @param dataMap
	 * @return
	 */
	private Map<String, Object> getSceneMap(Map<String, Object> dataMap) {
		Map<String, String> configMap = (Map<String, String>) dataMap.get("config");
		Map<String, String> capabilityMap = (Map<String, String>) dataMap.get("capability");
		List<String> methodList = (List<String>) dataMap.get("method");
		Map<String, Object> sceneMap = new HashMap<>();
		/**
		 * 系统区分点
		 */
		HashMap<String, Object> config = new HashMap<>();
		if (deviceOS.equals(Cconfig.ANDROID)) {
			this.version = AndroidInfo.getVersion(udid);
			this.deviceName = udid;
			MainRun.androidConfigBean.getScene().entrySet().forEach(i -> config.put(i.getKey(), i.getValue()));
		} else {
			this.deviceName = IOSInfo.getProduct(udid);
			this.version = IOSInfo.getVersion(udid);
			MainRun.iosConfigBean.getScene().entrySet().forEach(i -> config.put(i.getKey(), i.getValue()));
		}
		for (Entry<String, Object> entry : config.entrySet()) {// 原始config导入
			if (((String) configMap.get(Cparams.name)).equals(entry.getKey())) {
				((Map<String, Object>) entry.getValue()).entrySet()
						.forEach(i -> sceneMap.put(i.getKey(), i.getValue()));
				break;
			}
		}
		for (Entry<String, String> entry : configMap.entrySet()) {// note config覆盖导入
			sceneMap.put(entry.getKey(), entry.getValue());
		}

		Map<String, String> map_capability = new LinkedHashMap<>();// capability导入
		for (Entry<String, String> entry : capabilityMap.entrySet()) {
			map_capability.put(entry.getKey(), entry.getValue());
		}
		// 设备为当前机器
		map_capability.put("deviceName", deviceName);
		map_capability.put("platformVersion", version);
		map_capability.put(Cparams.udid, udid);
		sceneMap.put(Cparams.capability, map_capability);

		Map<TestCaseBean, Boolean> map_caseruninfo = new LinkedHashMap<>();// caseruninfo 导入
		List<TestCaseBean> testcasesList = FactoryScene.getCase(configMap.get(Cparams.name));
		for (TestCaseBean testCaseBean : testcasesList) {
			if (methodList.contains(testCaseBean.getMethodName())) {
				map_caseruninfo.put(testCaseBean, true);
			} else {
				map_caseruninfo.put(testCaseBean, false);
			}
		}
		sceneMap.put(Cparams.caseruninfo, map_caseruninfo);

		return sceneMap;
	}
}
