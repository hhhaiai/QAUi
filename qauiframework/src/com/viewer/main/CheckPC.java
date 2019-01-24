package com.viewer.main;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.helper.CMDUtil;

public class CheckPC {
	static Logger logger = LoggerFactory.getLogger(CheckPC.class);

	/**
	 * 检查所有项
	 * 
	 * @return
	 */
	public static String checkAll() {
		StringBuffer checkresult = new StringBuffer();

//		checkresult.append(checkAppiumServerUrl(MainRun.sysConfigBean.getAppiumServerUrl())?
//				"Appium服务器: "+MainRun.sysConfigBean.getAppiumServerUrl()+" 正常"
//				:"Appium服务器: "+MainRun.sysConfigBean.getAppiumServerUrl()+" 异常");
//		checkresult.append("\n");
		checkresult.append(!checkFFMPEG() ? "请将相应的ffmpeg文件放置在extraBin文件夹中!\n" : "");
		checkresult.append(checkAndroidSDK_adb(MainRun.sysConfigBean.getAndroidSDK_adb())
				? "AndroidSDK路径: " + MainRun.sysConfigBean.getAndroidSDK_adb() + " 正常"
				: "AndroidSDK路径: " + MainRun.sysConfigBean.getAndroidSDK_adb() + " 异常");
		checkresult.append("\n");
		if (MainRun.settingsBean.getSystem() == Cconfig.MAC) {
			String result = checkLibimobiledevice(MainRun.sysConfigBean.getMACcmd());
			checkresult.append(result.equals("") ? "libimobiledevice检查: " + result + " 正常"
					: "libimobiledevice检查: " + result + " 异常");
			checkresult.append("\n");
		}

		if (checkresult.toString().contains("异常")) {
			checkresult.append("请到菜单->系统设置,进行相关项配置!");
			checkresult.append("\n");
		}
		return checkresult.toString();
	}

	/**
	 * 检查libimoiledevice文件是否存在
	 * 
	 * @param MACcmdurl
	 * @return
	 */
	public static String checkLibimobiledevice(String MACcmdurl) {
		StringBuffer stringBuffer = new StringBuffer();

		File idevicescreenshot = new File(MACcmdurl + "/idevicescreenshot");
		File idevicesyslog = new File(MACcmdurl + "/idevicesyslog");
		File idevice_id = new File(MACcmdurl + "/idevice_id");
		File ideviceinfo = new File(MACcmdurl + "/ideviceinfo");
		// File ideviceinstaller=new File(MACcmdurl+"/ideviceinstaller");
		if (!idevicescreenshot.exists())
			stringBuffer.append("idevicescreenshot,");
		if (!idevicesyslog.exists())
			stringBuffer.append("idevicesyslog,");
		if (!idevice_id.exists())
			stringBuffer.append("idevice_id,");
		if (!ideviceinfo.exists())
			stringBuffer.append("ideviceinfo,");
		logger.info("Check libimobiledevice: " + stringBuffer.toString() + " is not ok");
		return stringBuffer.toString().equals("") ? "" : stringBuffer.substring(0, stringBuffer.length() - 1);
	}

	/**
	 * 检查AndroidSDK路径下的ADB是否正常
	 * 
	 * @param sdkadburl
	 * @return
	 */
	public static boolean checkAndroidSDK_adb(String sdkadburl) {
		File file = new File(sdkadburl);
		if (file.exists()) {
			List<String> list = CMDUtil.returnlist(sdkadburl + " devices", CAndroidCMD.SYSCMD, true);
			for (String str : list) {
				if (str.contains("List of devices attached")) {
					logger.info("Check AndroidSDK_adb: " + sdkadburl + " is ok");
					return true;
				}
			}
		}
		logger.info("Check AndroidSDK_adb: " + sdkadburl + " is not ok");
		return false;
	}

	/**
	 * 检查appium服务器是否打开
	 * 
	 * @param serverurl
	 * @return
	 */
	public static boolean checkAppiumServerUrl(String serverurl) {
		if (!serverurl.toLowerCase().endsWith("/wd/hub"))
			return false;
		Socket client = null;
		try {
			URL url = new URL(serverurl);
			client = new Socket(url.getHost(), url.getPort());
			logger.info("Check appium server: " + serverurl + " is ok...");
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
			}
		}
		logger.info("Check appium server: " + serverurl + " is not ok...");
		return false;
	}

	/**
	 * 插件FFMPEG是否存在
	 * 
	 * @return
	 */
	public static boolean checkFFMPEG() {
		String filename = "";
		if (MainRun.settingsBean.getSystem() == Cconfig.LINUX) {
			filename = "ffmpeg_linux";
		} else if (MainRun.settingsBean.getSystem() == Cconfig.MAC) {
			filename = "ffmpeg_mac";
		} else if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
			filename = "ffmpeg.exe";
		}
		File file = new File(MainRun.settingsBean.getExtraBinlocation() + "/ffmpeg/" + filename);
		if (!file.exists() || !file.isFile()) {
			return false;
		}
		return true;
	}
}
