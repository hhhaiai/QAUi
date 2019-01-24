package com.command.scenetask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.TimeUtil;
import com.viewer.main.MainRun;

public class SYSAndroidMonkeyVerify {
	Logger logger = LoggerFactory.getLogger(SYSAndroidMonkeyVerify.class);
	String udid;
	String deviceOS;
	String packagename;
	Map<String, Object> monkeyMap = new HashMap<>();

	public SYSAndroidMonkeyVerify() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取任务
	 * 
	 * @param udid
	 * @param deviceOS
	 * @param cmdMap
	 * @return
	 */
	public Map<String, Object> VerifyMonkey(String udid, String deviceOS, String packagename,
			Map<String, String> cmdMap) {
		this.udid = udid;
		this.deviceOS = deviceOS;
		this.packagename = packagename.split("\\s+")[0];
		if (!getParamsFromXML())
			return null;
		if (!getParamsFromInput(cmdMap))
			return null;
		return monkeyMap;
	}

	/**
	 * 从XML获取初始值
	 * 
	 * @return
	 */
	public boolean getParamsFromXML() {
		logger.info("get params from xml to init task map");
		if (deviceOS == null || udid == null) {
			print("主参数os或udid缺失");
			return false;
		}
		if (!AndroidInfo.isAppInstall(udid, packagename)) {
			print("应用未安装:" + packagename);
			return false;
		}
		monkeyMap.put(Cparams.run, "CMD");// 运行方式
		monkeyMap.put(Cparams.type, Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
		monkeyMap.put(Cparams.monkey_sys_runtime, 30);
		monkeyMap.put(Cparams.monkey_sys_seed, new SimpleDateFormat("ssSSS").format(new Date()));
		monkeyMap.put(Cparams.monkey_sys_intervaltime, 1000);

		for (Entry<String, String> entry : MainRun.androidConfigBean.getMonkey_sys().entrySet()) {
			monkeyMap.put(entry.getKey(), entry.getValue());
		}

//		monkeyMap.put(Cparams.monkey_sys_ignore_crashes,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_ignore_crashes));
//		monkeyMap.put(Cparams.monkey_sys_ignore_timeouts,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_ignore_timeouts));
//		monkeyMap.put(Cparams.monkey_sys_ignore_security_exceptions,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_ignore_security_exceptions));
//		monkeyMap.put(Cparams.monkey_sys_ignore_native_crashes,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_ignore_native_crashes));
//		monkeyMap.put(Cparams.monkey_sys_monitor_native_crashes,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_monitor_native_crashes));
//		monkeyMap.put(Cparams.monkey_sys_pct_touch,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_touch));
//		monkeyMap.put(Cparams.monkey_sys_pct_motion,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_motion));
//		monkeyMap.put(Cparams.monkey_sys_pct_trackball,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_trackball));
//		monkeyMap.put(Cparams.monkey_sys_pct_nav,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_nav));
//		monkeyMap.put(Cparams.monkey_sys_pct_majornav,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_majornav));
//		monkeyMap.put(Cparams.monkey_sys_pct_syskeys,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_syskeys));
//		monkeyMap.put(Cparams.monkey_sys_pct_appswitch,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_appswitch));
//		monkeyMap.put(Cparams.monkey_sys_pct_anyevent,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_pct_anyevent));
//		monkeyMap.put(Cparams.monkey_sys_customize,
//				MainRun.androidConfigBean.getMonkey_sys().get(Cparams.monkey_sys_customize));

		monkeyMap.put(Cparams.monkey_sys_apppackagename, packagename);
		monkeyMap.put(Cparams.monkey_sys_appnickname, "");
		monkeyMap.put(Cparams.udid, udid);
		monkeyMap.put(Cparams.monkey_sys_runcustomize, "false");
		// email
		monkeyMap.put(Cparams.email_send, MainRun.androidConfigBean.getEmail().get(Cparams.send));
		monkeyMap.put(Cparams.email_to, MainRun.androidConfigBean.getEmail().get(Cparams.to));
		monkeyMap.put(Cparams.email_cc, MainRun.androidConfigBean.getEmail().get(Cparams.cc));
		monkeyMap.put(Cparams.email_smtp, MainRun.androidConfigBean.getEmail().get(Cparams.smtp));
		monkeyMap.put(Cparams.email_account, MainRun.androidConfigBean.getEmail().get(Cparams.account));
		monkeyMap.put(Cparams.email_password, MainRun.androidConfigBean.getEmail().get(Cparams.password));
		// wechat
		monkeyMap.put(Cparams.wechat_people_list, MainRun.androidConfigBean.getWechat().get(Cparams.people_list));
		monkeyMap.put(Cparams.wechat_send, MainRun.androidConfigBean.getWechat().get(Cparams.send));
		return true;
	}

	/**
	 * 从用户输入配置参数
	 * 
	 * @param cmdMap
	 * @return
	 */
	public boolean getParamsFromInput(Map<String, String> cmdMap) {
		logger.info("get params from input cmd map");
		String key;
		String value;
		for (Entry<String, String> entry : cmdMap.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			switch (key) {
			case Cparams.monkey_sys_customize:
				monkeyMap.put(Cparams.monkey_sys_runcustomize, "true");
				break;
			default:
				break;
			}
			// logger.info("!!!!!"+key+","+value);
			monkeyMap.put(key, value);
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
