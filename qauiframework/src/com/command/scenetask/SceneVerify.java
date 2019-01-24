package com.command.scenetask;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.TestCaseBean;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.IOSInfo;
import com.helper.TimeUtil;
import com.task.FactoryScene;
import com.viewer.main.MainRun;

public class SceneVerify {
	Logger logger = LoggerFactory.getLogger(SceneVerify.class);
	String udid;
	String deviceOS;
	String scenename;
	Map<String, Object> sceneMap = new HashMap<>();

	/**
	 * 获取任务
	 * 
	 * @param udid
	 * @param deviceOS
	 * @param scenename
	 * @param cmdMap
	 * @return
	 */
	public Map<String, Object> VerifyScene(String udid, String deviceOS, String scenename, Map<String, String> cmdMap) {
		this.udid = udid;
		this.deviceOS = deviceOS;
		this.scenename = scenename.split("\\s+")[0];
		if (!getParamsFromXML())
			return null;
		if (!getParamsFromInput(cmdMap))
			return null;
		return sceneMap;
	}

	/**
	 * 从XML得到参数,装入sceneMap
	 * 
	 * @param cmdMap
	 * @return
	 */
	public boolean getParamsFromXML() {
		if (deviceOS == null || udid == null || scenename == null) {
			print("主参数os或udid或scene缺失!");
			return false;
		}
		Map<String, Object> config = new HashMap<>();// 所有场景初始值
		String version = "";
		String deviceName = "";
		if (deviceOS.equals(Cconfig.ANDROID)) {
			version = AndroidInfo.getVersion(udid);
			deviceName = udid;
			MainRun.androidConfigBean.getScene().entrySet().forEach(i -> config.put(i.getKey(), i.getValue()));
		} else {
			deviceName = IOSInfo.getProduct(udid);
			version = IOSInfo.getVersion(udid);
			MainRun.iosConfigBean.getScene().entrySet().forEach(i -> config.put(i.getKey(), i.getValue()));
		}
		Map<String, Object> OriginalsceneMap = null;
		for (Entry<String, Object> entry : config.entrySet()) {
			if (scenename.equals(entry.getKey())) {
				OriginalsceneMap = (Map<String, Object>) entry.getValue();
				break;
			}
		}
		if (OriginalsceneMap == null) {
			print("未找到场景:" + scenename + ",请检查参数scene!");
			return false;
		}
		// capability
		Map<String, String> capabilityMap = (Map<String, String>) OriginalsceneMap.get("capability");
		capabilityMap.put(Cparams.udid, udid);
		capabilityMap.put("deviceName", deviceName);
		capabilityMap.put("platformVersion", version);
		Map<String, String> map = new LinkedHashMap<>();// 指向不同内存地址
		for (Entry<String, String> entry : capabilityMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		sceneMap.put(Cparams.capability, map);
		// config params
		for (Entry<String, Object> entry : OriginalsceneMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				sceneMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		// sceneMap.put(Cparams.screenshot,
		// (String)OriginalsceneMap.get(Cparams.screenshot));
		// sceneMap.put(Cparams.syscrash,
		// (String)OriginalsceneMap.get(Cparams.syscrash));
		// sceneMap.put(Cparams.appcrash,
		// (String)OriginalsceneMap.get(Cparams.appcrash));
		// sceneMap.put(Cparams.userlogcatch,
		// (String)OriginalsceneMap.get(Cparams.userlogcatch));
		// sceneMap.put(Cparams.initdriver,
		// (String)OriginalsceneMap.get(Cparams.initdriver));
		// sceneMap.put(Cparams.email_send,
		// (String)OriginalsceneMap.get(Cparams.email_send));
		// sceneMap.put(Cparams.email_to,
		// (String)OriginalsceneMap.get(Cparams.email_to));
		// sceneMap.put(Cparams.email_cc,
		// (String)OriginalsceneMap.get(Cparams.email_cc));
		// sceneMap.put(Cparams.email_smtp,
		// (String)OriginalsceneMap.get(Cparams.email_smtp));
		// sceneMap.put(Cparams.email_account,
		// (String)OriginalsceneMap.get(Cparams.email_account));
		// sceneMap.put(Cparams.email_password,
		// (String)OriginalsceneMap.get(Cparams.email_password));
		// sceneMap.put(Cparams.name, (String)OriginalsceneMap.get(Cparams.name));
		// sceneMap.put(Cparams.desc,(String)OriginalsceneMap.get(Cparams.desc));
		// sceneMap.put(Cparams.params,(String)OriginalsceneMap.get(Cparams.params));
		/**
		 * 系统区分点
		 */
		// if(this.deviceOS.equals(Cconfig.ANDROID)){
		//
		// }else{
		// sceneMap.put(Cparams.idevicesyslogtag,
		// (String)OriginalsceneMap.get(Cparams.idevicesyslogtag));
		// }
		sceneMap.put("os", this.deviceOS);
		sceneMap.put(Cparams.note, "");
		Map<TestCaseBean, Boolean> caserunMap = new LinkedHashMap<>();
		for (TestCaseBean testCaseBean : FactoryScene.getCase(scenename)) {
			if (testCaseBean.getNo() >= 1) {
				caserunMap.put(testCaseBean, true);
			} else {
				caserunMap.put(testCaseBean, false);
			}
		}
		sceneMap.put(Cparams.caseruninfo, caserunMap);
		sceneMap.put(Cparams.apps, "");
		sceneMap.put("mode", "");
		sceneMap.put(Cparams.appiumserverurl, MainRun.sysConfigBean.getAppiumServerUrl());
		sceneMap.put(Cparams.run, "CMD");// 运行方式
		return true;
	}

	/**
	 * 从用户输入配置参数
	 * 
	 * @param cmdMap
	 * @return
	 */
	public boolean getParamsFromInput(Map<String, String> cmdMap) {
		String key;
		String value;
		for (Entry<String, String> entry : cmdMap.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			switch (key) {
			case Cparams.caseruninfo:
				Map<TestCaseBean, Boolean> caserunMap = (Map<TestCaseBean, Boolean>) sceneMap.get(key);
				StringBuffer invalid_caseBuf = new StringBuffer();
				for (String no : value.split(",")) {
					boolean validcase = false;
					for (Entry<TestCaseBean, Boolean> caserun : caserunMap.entrySet()) {
						if (caserun.getKey().getNo() == Integer.parseInt(no) && Integer.parseInt(no) != 0) {
							validcase = true;
						}
					}
					if (!validcase)
						invalid_caseBuf.append(no + ",");
				}
				if (invalid_caseBuf.toString().length() > 0) {
					print("无效的用例序号:"
							+ invalid_caseBuf.toString().substring(0, invalid_caseBuf.toString().length() - 1));
					print(scenename + "用例列表如下:");
					caserunMap.entrySet().forEach(e -> {
						print("用例序号" + e.getKey().getNo() + ":" + e.getKey().getName() + ",执行次数="
								+ e.getKey().getRuntime());
					});
					return false;
				}
				for (Entry<TestCaseBean, Boolean> caserun : caserunMap.entrySet()) {
					boolean validcase = false;
					for (String no : value.split(",")) {
						if (caserun.getKey().getNo() == Integer.parseInt(no)) {
							validcase = true;
						}
					}
					if (!validcase)
						caserun.setValue(false);
				}
				continue;
			case Cparams.capability:
				Map<String, String> map = (Map<String, String>) sceneMap.get(Cparams.capability);
				for (String param : value.split(";")) {
					map.put(param.split("=")[0], param.split("=")[1]);
				}
				sceneMap.put(Cparams.capability, map);
				continue;
			default:
				break;
			}
			// logger.info("!!!!!"+key+","+value);
			sceneMap.put(key, value);
		}
		return true;
	}

	/**
	 * 打印日志
	 * 
	 * @param text
	 */
	public void print(String text) {
		System.out.println(TimeUtil.getTime4Log() + " [CHECK]:" + text);
	}
}
