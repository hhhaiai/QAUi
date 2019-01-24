package com.task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appium.Driver;
import com.bean.TestCaseBean;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.log.SceneLogUtil;
import com.report.MixReport;
import com.viewer.main.CheckPC;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class SceneTask {
	Logger logger = LoggerFactory.getLogger(SceneTask.class);
	SceneLogUtil oplog;
	String udid;
	String deviceOS;
	MixReport mixReport;
	File catalog;
	AndroidDriver<WebElement> androidDriver;
	IOSDriver<WebElement> iosDriver;
	boolean force_stop = false;
	boolean first_run = true;// 当initdirver为true且多任务时,仅在第一次卸载appium apks

	Map<Object, TestCaseBean> failMap = new LinkedHashMap<>();// 存储失败项,用于重跑

	public SceneTask(SceneLogUtil oplog, String udid, String deviceOS, File catalog, MixReport mixReport) {
		// TODO Auto-generated constructor stub
		this.oplog = oplog;
		this.deviceOS = deviceOS;
		this.udid = udid;
		this.mixReport = mixReport;
		this.catalog = catalog;
	}

	/**
	 * 运行单个场景
	 * 
	 * @param sceneMap
	 * @return 场景序号
	 */
	public int runTask(Map<String, Object> sceneMap, int taskcount) {
		// 得到场景运行信息
		Map<String, String> configMap = new HashMap<>();
		Map<String, String> capabilityMap = null;
		Map<TestCaseBean, Boolean> scene_caseruninfo = null;
		for (Entry<String, Object> scene : sceneMap.entrySet()) {
			if (scene.getKey().equals(Cparams.capability)) {
				capabilityMap = (Map<String, String>) scene.getValue();
			} else if (scene.getKey().equals(Cparams.caseruninfo)) {
				scene_caseruninfo = (Map<TestCaseBean, Boolean>) scene.getValue();
			} else {
				configMap.put(scene.getKey(), (String) scene.getValue());
			}
		}
		// 强制停止标志位
		if (force_stop) {
			oplog.logTask(configMap.get(Cparams.name) + "未运行,因已经强制停止!");
			return taskcount;
		}
		// 安装包
		String apps = configMap.get(Cparams.apps);
		boolean reverse = false;// #反转标志位
		if (apps.startsWith("#")) {
			reverse = true;
			configMap.put(Cparams.apps, apps.substring(1));
			apps = configMap.get(Cparams.apps);
		}
		File appFolderAndFile = new File(apps);// 单个安装或者目录
		if (appFolderAndFile.exists() && appFolderAndFile.isDirectory()) {
			File[] appfiles = appFolderAndFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					if (name.toLowerCase().endsWith(configMap.get("os").equals(Cconfig.ANDROID) ? ".apk" : ".ipa"))
						return true;
					return false;
				}
			});
			oplog.logTask("开始安装包测试,共" + appfiles.length + "个...");
			List<File> filelist = new ArrayList<>();
			for (File appfile : appfiles)
				filelist.add(appfile);
			if (reverse)
				Collections.reverse(filelist);
			int count = 0;
			for (File appfile : filelist) {
				count++;
				oplog.logTask(count + ". " + appfile.getName());
			}
			count = 0;
			for (File appfile : filelist) {
				count++;
				oplog.logTask("第" + count + "个安装包,安装包地址:" + appfile.getAbsolutePath());
				capabilityMap.put(Cparams.app, appfile.getAbsolutePath().replace("\\", "/"));
				configMap.put(Cparams.app, appfile.getAbsolutePath().replace("\\", "/"));
				// 运行场景
				taskcount++;
				configMap.put(Cparams.taskcount, taskcount + "");
				if (!force_stop)
					runScene(configMap, capabilityMap, scene_caseruninfo, catalog, mixReport);
			}
		} else {
			if (appFolderAndFile.exists() && appFolderAndFile.isFile()) {
				capabilityMap.put(Cparams.app, appFolderAndFile.getAbsolutePath().replace("\\", "/"));
				configMap.put(Cparams.app, appFolderAndFile.getAbsolutePath().replace("\\", "/"));
			} else if (capabilityMap.get("app") != null && !capabilityMap.get("app").equals("")) {// 当安装包为空,capability中app不为空时
				configMap.put(Cparams.app, capabilityMap.get("app").replace("\\", "/"));
			}
			// 运行场景
			taskcount++;
			configMap.put(Cparams.taskcount, taskcount + "");
			if (!force_stop)
				runScene(configMap, capabilityMap, scene_caseruninfo, catalog, mixReport);
		}
		return taskcount;
	}

	/**
	 * 通过反射运行场景
	 * 
	 * @param scene_name
	 * @param configMap
	 * @param capabilityMap
	 */
	public void runScene(Map<String, String> configMap, Map<String, String> capabilityMap,
			Map<TestCaseBean, Boolean> scene_caseruninfo, File catalog, MixReport mixReport) {
		Class<?> clazz;
		String scene_name = configMap.get(Cparams.name);
		try {
			oplog.logTask("开始执行场景: " + scene_name + "...");
			oplog.logTask("UDID=" + capabilityMap.get(Cparams.udid) + ",版本=" + capabilityMap.get("platformVersion"));
			clazz = Class.forName(scene_name);
			// btn_starttask.setEnabled(false);
			if (!initDriver(configMap, capabilityMap))// 初始化driver
				return;
			FactoryScene.init();// 初始化场景工厂
			if (deviceOS.equals(Cconfig.ANDROID)) {
				BaseAndroidScene androidScene = (BaseAndroidScene) clazz.getConstructor().newInstance();
				androidScene.init(androidDriver, capabilityMap, configMap, scene_caseruninfo, oplog, mixReport,
						catalog);
				// androidDriver=androidScene.getDriver();
				// btn_starttask.setEnabled(true);
				int flag = FactoryScene.RunTest(androidScene);
			} else {
				BaseIOSScene iosScene = (BaseIOSScene) clazz.getConstructor().newInstance();
				iosScene.init(iosDriver, capabilityMap, configMap, scene_caseruninfo, oplog, mixReport, catalog);
				// iosDriver=iosScene.getDriver();
				// btn_starttask.setEnabled(true);
				int flag = FactoryScene.RunTest(iosScene);
			}
		} catch (ClassNotFoundException e) {
			logger.error("EXCEPTION", e);
			oplog.logTask("未找到类名: " + scene_name + ",执行失败!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
			oplog.logTask("场景名称=" + scene_name + ",执行失败!");
		}
		// btn_starttask.setEnabled(true);
	}

	/**
	 * 初始化driver
	 * 
	 * @param configMap
	 * @param capabilityMap
	 */
	private boolean initDriver(Map<String, String> configMap, Map<String, String> capabilityMap) {
		if (deviceOS.equals(Cconfig.ANDROID)) {
			if (configMap.get(Cparams.initdriver).equals("true")) {
				quitDriver();
				// oplog.logTask("停止appium残留进程...");
				// AndroidInfo.stopApp(udid, "io.appium.uiautomator2.server");
				// AndroidInfo.stopApp(udid, "io.appium.uiautomator2.server.test");

				// 多任务时仅执行一次卸载操作,避免出错及浪费时间
				if (first_run) {
					List<String> list = new ArrayList<>();
					list.add(Cconfig.APPIUM_IO_ANDROID_IME_PACKAGENAME);
					list.add(Cconfig.APPIUM_IO_SETTINGS_PACKAGENAME);
					list.add(Cconfig.APPIUM_IO_UIAUTOMATOR2_SERVEER_PACKAGENAME);
					list.add(Cconfig.APPIUM_IO_UIAUTOMATOR2_SERVEER_TEST_PACKAGENAME);
					list.add(Cconfig.APPIUM_IO_UNLOCK_PACKAGENAME);
					for (String packagename : list) {
						if (AndroidInfo.uninstallApp(udid, packagename)) {
							oplog.logTask("卸载应用成功:" + packagename);
						}
					}
					first_run = false;
				}
			} else {
				if (androidDriver != null) {
					oplog.logTask("无需重新初始化Appium Android driver!");
					return true;
				}
			}
			if (configMap.get(Cparams.setdevice).equals("true")) {
				setDevice();
			}
			oplog.logTask("开始初始化Appium Android driver...");
			Driver d = new Driver(configMap.get(Cparams.appiumserverurl), capabilityMap, Cconfig.ANDROID, oplog);
			configMap.put(Cparams.initdriverdone, "true");
			androidDriver = d.getAndroidDriver();
			if (androidDriver == null) {
				if (CheckPC.checkAppiumServerUrl(configMap.get(Cparams.appiumserverurl)))
					oplog.logError("无法连接appium server:" + configMap.get(Cparams.appiumserverurl));
				oplog.logError("初始化Appium Android driver 失败");
				return false;
			} else {
				oplog.logTask("Appium Android driver初始化成功");
			}
		} else {
			if (configMap.get(Cparams.initdriver).equals("true")) {
				quitDriver();
			} else {
				if (iosDriver != null) {
					oplog.logTask("无需重新初始化Appium iOS driver!");
					return true;
				}
			}
			oplog.logTask("开始初始化Appium iOS driver...");
			Driver d = new Driver(configMap.get(Cparams.appiumserverurl), capabilityMap, Cconfig.IOS, oplog);
			configMap.put(Cparams.initdriverdone, "true");
			iosDriver = d.getIOSDriver();
			if (iosDriver == null) {
				if (CheckPC.checkAppiumServerUrl(configMap.get(Cparams.appiumserverurl)))
					oplog.logError("无法连接appium server:" + configMap.get(Cparams.appiumserverurl));
				oplog.logError("初始化Appium iOS driver 失败");
				return false;
			} else {
				oplog.logTask("Appium iOS driver初始化成功");
			}
		}
		return true;
	}

	/**
	 * 结束driver
	 */
	public void quitDriver() {
		if (androidDriver != null) {
			try {
				oplog.logTask("结束Android Appium Session...");
				androidDriver.quit();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("EXCEPTION", e);
				oplog.logError("终止appium driver出现异常,可能已被强制停止!");
			} finally {
				androidDriver = null;
			}
		}
		if (iosDriver != null) {
			try {
				oplog.logTask("结束iOS Appium Session...");
				iosDriver.quit();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("EXCEPTION", e);
				oplog.logError("终止appium driver出现异常,可能已被强制停止!");
			} finally {
				iosDriver = null;
			}
		}
	}

	/**
	 * 还原输入法
	 */
	private void restoreInputMethod() {
		String input_method = AndroidInfo.getDefaultInputMethod(udid);
		logger.info("default input method:" + input_method);
		if (input_method.equals(Cconfig.APPIUM_INPUT_METHOD_NAME)) {
			List<String> defaultList = AndroidInfo.getInputMethodList(udid);
			if (defaultList.contains(Cconfig.APPIUM_INPUT_METHOD_NAME)) {
				defaultList.remove(Cconfig.APPIUM_INPUT_METHOD_NAME);
			}
			List<String> inputList = new ArrayList<>();
			// com.baidu.input_huawei/.ImeService
			// com.sohu.inputmethod.sogou.xiaomi/.SogouIME
			// com.baidu.input_mi/.ImeService
			inputList.add("com.sohu.inputmethod.sogouoem/.SogouIME");
			inputList.add("com.sohu.inputmethod.sogou");
			inputList.add("com.baidu");
			boolean isok = false;
			for (String str : inputList) {
				if (defaultList.contains(str)) {
					if (AndroidInfo.setDefaultInputMethod(udid, str)) {
						oplog.logTask("切换输入法为: " + str);
						isok = true;
					} else {
						logger.info("change input method error:" + str);
					}
					break;
				}
			}
			if (!isok && defaultList.size() > 0) {
				if (AndroidInfo.setDefaultInputMethod(udid, defaultList.get(0))) {
					oplog.logTask("切换输入法为: " + defaultList.get(0));
				} else {
					logger.info("change first input method error:" + defaultList.get(0));
				}
			}
		}
	}

	/**
	 * 设置设备系统
	 */
	private void setDevice() {
		if (!AndroidInfo.getWifiOn(udid) && AndroidInfo.setWifiOn(udid, true)) {
			oplog.logTask("设置:打开WIFI");
		}
		if (!AndroidInfo.getAutoTime(udid) && AndroidInfo.setAutoTime(udid, true)) {
			oplog.logTask("设置:自动获取时间");
		}
		if (!AndroidInfo.getAutoTimeZone(udid) && AndroidInfo.setAutoTimeZone(udid, true)) {
			oplog.logTask("设置:自动获取时区");
		}
		if (AndroidInfo.getScreenOffTimeout(udid) < 300 && AndroidInfo.setScreenOffTimeout(udid, 600)) {
			oplog.logTask("设置:设置屏幕休眠为10分钟");
		}
		if (!AndroidInfo.getScreenBrightnessMode(udid) && AndroidInfo.setScreenBrightnessMode(udid, true)) {
			oplog.logTask("设置:自动调整亮度");
		}
		restoreInputMethod();
	}

	/**
	 * 强制停止标志
	 * 
	 * @param forcestop
	 */
	public void setForceStop(boolean stop) {
		this.force_stop = stop;
		FactoryScene.force_stop = stop;
	}
}
