package com.More;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CMDUtil;
import com.Viewer.MainRun;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;

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
		checkresult.append(checkAndroidSDK_adb(MainRun.paramsBean.getAndroidSDK_adb())
				? "AndroidSDK路径: " + MainRun.paramsBean.getAndroidSDK_adb() + " 正常"
				: "AndroidSDK路径: " + MainRun.paramsBean.getAndroidSDK_adb() + " 异常");
		checkresult.append("\n");
		String result = checkLibimobiledevice(MainRun.paramsBean.getMACcmd());
		checkresult.append(
				result.equals("") ? "libimobiledevice检查: " + result + " 正常" : "libimobiledevice检查: " + result + " 异常");
		checkresult.append("\n");
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
		File idevicescreenshot = null;
		File idevicesyslog = null;
		File idevice_id = null;
		File ideviceinfo = null;
		if (MainRun.OStype == Cconfig.WINDOWS) {
			idevicescreenshot = new File(MACcmdurl + "/idevicescreenshot.exe");
			idevicesyslog = new File(MACcmdurl + "/idevicesyslog.exe");
			idevice_id = new File(MACcmdurl + "/idevice_id.exe");
			ideviceinfo = new File(MACcmdurl + "/ideviceinfo.exe");
		} else {
			idevicescreenshot = new File(MACcmdurl + "/idevicescreenshot");
			idevicesyslog = new File(MACcmdurl + "/idevicesyslog");
			idevice_id = new File(MACcmdurl + "/idevice_id");
			ideviceinfo = new File(MACcmdurl + "/ideviceinfo");
		}

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
			String[] results = CMDUtil.execcmd(sdkadburl + " version", CAndroidCMD.SYSCMD, true);
			logger.info(results[0] + "," + results[1]);
			if (results[0].contains("Android Debug Bridge version")
					|| results[1].contains("Android Debug Bridge version")) {
				logger.info("Check AndroidSDK_adb: " + sdkadburl + " is ok");
				return true;
			}
//			List<String> list = CMDUtil.returnlist(sdkadburl + " devices", CAndroidCMD.SYSCMD, true);
//			for (String str : list) {
//				if (str.contains("List of devices attached")) {
//					logger.info("Check AndroidSDK_adb: " + sdkadburl + " is ok");
//					return true;
//				} else {
//					logger.error(str);
//				}
//			}
		}
		logger.info("Check AndroidSDK_adb: " + sdkadburl + " is not ok");
		return false;
	}

}
