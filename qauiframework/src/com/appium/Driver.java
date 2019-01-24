package com.appium;

import java.net.URL;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.ADBUtil;
import com.helper.AndroidInfo;
import com.log.SceneLogUtil;
import com.xq.XQAndroidDriver;
import com.xq.XQAndroidElement;
import com.xq.XQAndroidOp;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class Driver {
	Logger logger = LoggerFactory.getLogger(Driver.class);
	protected AndroidDriver<WebElement> androiddriver = null;
	protected IOSDriver<WebElement> iosdriver = null;
	SceneLogUtil oplog;
	String udid;

	/**
	 * 初始化Appium Android driver
	 * http://javadox.com/io.appium/java-client/1.2.1/io/appium/java_client/AppiumDriver.html
	 */
	public Driver(String serverurl, Map<String, String> capabilityMap, String os, SceneLogUtil oplog) {
		this.oplog = oplog;
		this.udid = capabilityMap.get(Cparams.udid);
		// 启动appium
		DesiredCapabilities capabilities = new DesiredCapabilities();
		for (Map.Entry<String, String> entry : capabilityMap.entrySet()) {
			logger.info(entry.getKey() + " = " + entry.getValue());
			if (!entry.getValue().trim().equals("")) {
				if (entry.getValue().equals("true")) {
					capabilities.setCapability(entry.getKey(), true);
					continue;
				} else if (entry.getValue().equals("false")) {
					capabilities.setCapability(entry.getKey(), false);
					continue;
				} else if (entry.getKey().equals("wdaLocalPort")) {
					capabilities.setCapability(entry.getKey(), Integer.parseInt(entry.getValue()));
					continue;
				} else {
					capabilities.setCapability(entry.getKey(), entry.getValue());
					continue;
				}
			}
		}
		// 以io.appium.settings/io.appium.settings.Settings初始化
		if (os.equals(Cconfig.ANDROID)) {
			if (Cconfig.APPIUM_REPACLE_DRIVER && !capabilityMap.get("automationName").toLowerCase().equals("appium")) {// 5.0以下不支持
				logger.info("appium android driver replace to " + Cconfig.APPIUM_IO_SETTINGS_PACKAGENAME);
				capabilities.setCapability("appPackage", Cconfig.APPIUM_IO_SETTINGS_PACKAGENAME);
				capabilities.setCapability("appActivity", Cconfig.APPIUM_IO_SETTINGS_ACTIVITY);
				capabilities.setCapability("app", "");
				// 不重置settings应用
				capabilities.setCapability("noReset", true);
				capabilities.setCapability("autoLaunch", false);
				// 判断是否重置目标应用,只有当autoLauch=false和noReset=true时才不会重置应用
				// if (capabilityMap.get("noReset").equals("false") ||
				// capabilityMap.get("autoLaunch").equals("true")) {
				// AndroidInfo.clearApp(capabilityMap.get("udid"),
				// capabilityMap.get("appPackage"));
				// }
			}
			// 处理弹框提示
			XQAndroidOp XQOP = new XQAndroidOp(new XQAndroidDriver(capabilityMap.get("udid"), null, oplog, null, null));
			InitDriverRunnable initDriverRunnable = new InitDriverRunnable(XQOP);
			new Thread(initDriverRunnable).start();
			try {
				Thread.sleep(5000);// 等待XQ驱动获取页面正常.
				logger.info("appium android driver init start");
				androiddriver = new AndroidDriver<WebElement>(new URL(serverurl), capabilities);
				logger.info("appium android driver init finished");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
				logger.error("初始化失败");
				if (androiddriver != null) {
					androiddriver.quit();
					androiddriver = null;
				}
			} finally {
				oplog.logTask("自动安装共点击次数=" + initDriverRunnable.setEnd(true));
			}
		} else {
			try {
				logger.info("appium ios driver init start");
				iosdriver = new IOSDriver<WebElement>(new URL(serverurl), capabilities);
				logger.info("appium ios driver init finished");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
				logger.error("初始化失败");
				if (iosdriver != null) {
					iosdriver.quit();
					iosdriver = null;
				}
			}
		}
		// driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
	}

	/**
	 * 返回android driver
	 */
	public AndroidDriver<WebElement> getAndroidDriver() {
		return androiddriver;
	}

	/**
	 * 返回ios driver
	 */
	public IOSDriver<WebElement> getIOSDriver() {
		return iosdriver;
	}

	class InitDriverRunnable implements Runnable {
		boolean end = false;
		XQAndroidOp XQOP;
		int normal_count = 0;// 页面解析成功点击次数
		int unnormal_count = 0;// 页面解析失败点击次数

		public InitDriverRunnable(XQAndroidOp XQOP) {
			// TODO Auto-generated constructor stub
			this.XQOP = XQOP;
		}

		@Override
		public void run() {
			// io.appium.uiautomator2.server
			// io.appium.uiautomator2.server.test
			// io.appium.settings/.Settings
			// io.appium.unlock/.Unlock
			// io.appium.android.ime/.UnicodeIME

			XQAndroidElement ele = null;// 保存识别到的按钮,然后在获取xml失败时使用
			while (!end && !installDone()) {
				if (XQOP.getPage()) {
					XQAndroidElement xqAndroidElement = XQOP.findElement("android.widget.Button",
							".*[删除|安装|确定|允许|同意|确认].*");
					if (xqAndroidElement.exist()) {
						xqAndroidElement.click();
						ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", CAndroidCMD.KEYCODE_BACK));// 用于退出无关应用
						normal_count++;
						ele = xqAndroidElement;
					}
				} else {
					if (ele != null) {
						ele.click();
						unnormal_count++;
						ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", CAndroidCMD.KEYCODE_BACK));// 用于退出无关应用
					}
				}
			}
			logger.info("init driver by XQOP end: normal_count=" + normal_count + ",unnormal_count=" + unnormal_count);
		}

		public int setEnd(boolean end) {
			this.end = end;
			return normal_count + unnormal_count;
		}

		private boolean installDone() {
			return AndroidInfo.isAppInstall(udid, Cconfig.APPIUM_IO_ANDROID_IME_PACKAGENAME)
					&& AndroidInfo.isAppInstall(udid, Cconfig.APPIUM_IO_SETTINGS_PACKAGENAME)
					&& AndroidInfo.isAppInstall(udid, Cconfig.APPIUM_IO_UIAUTOMATOR2_SERVEER_PACKAGENAME)
					&& AndroidInfo.isAppInstall(udid, Cconfig.APPIUM_IO_UIAUTOMATOR2_SERVEER_TEST_PACKAGENAME)
					&& AndroidInfo.isAppInstall(udid, Cconfig.APPIUM_IO_UNLOCK_PACKAGENAME);
		}
	}
}
