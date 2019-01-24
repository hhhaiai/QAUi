package com.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.android.ddmlib.IDevice;
import com.constant.CIOSCMD;
import com.constant.Cconfig;

public class CheckUE {
	Logger logger = LoggerFactory.getLogger(CheckUE.class);

	static String[] IDlist = { "" };
	static JComboBox<String> deviceslist;
	static JLabel lblDevicestatus;
	static JLabel lblDeviceInfo;
	static boolean nodevice;
	static IDevice[] devices;
	static IDevice device;

	// mainUI frame = new mainUI();

	public void run() {
		if (!MainRun.adbBridge.initialize()) {
			MainRun.adbBridge.initialize();
		}
		lblDevicestatus = MainRun.mainFrame.getDevicestatus();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						logger.error("Exception", e);
					}
					checkstatus(MainRun.selectedID);
					nodevice = true;
					if (!Arrays.equals(IDlist, getdeviceslist())) {
						IDlist = getdeviceslist();
						deviceslist = MainRun.mainFrame.getDeviceslist();
						deviceslist.removeAllItems();
						for (String str : IDlist) {
							deviceslist.addItem(str);
							logger.info("update devices ID=" + str);
							nodevice = false;
						}
						if (nodevice) {
							MainRun.selectedID = null;
							logger.info("select devices ID=" + MainRun.selectedID);
						}
					}
				}
			}
		}).start();
	}

	// 设备信息显示
	public static void checkstatus(String udid) {
		String str = "";
		if (udid == null) {
			str = "未检测到设备!";
		} else {
			if (udid.length() > 35) {
				str = "机型: " + getIOSProduct(udid) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;版本: " + getIOSVersion(udid);
			} else {
				String api = checkAPI(udid);
				str = "版本: " + api2version(api) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;API=" + api;
			}
		}
		lblDevicestatus.setText("<html>" + str + "</html>");

	}

	// get deivces list
	public static String[] getdeviceslist() {
		ArrayList<String> array = new ArrayList<String>();
		devices = MainRun.adbBridge.getDevices();
		if (devices != null) {
			for (int i = 0; i < devices.length; ++i) {
				if (devices[i].isOnline()) {
					array.add(devices[i].toString());
				}
			}
		}
		for (String str : getIOSDevices()) {
			array.add(str);
		}
		String[] str = (String[]) array.toArray(new String[0]);
		return str;
	}

	/**
	 * IOS得到设备列表
	 * 
	 * @return
	 */
	public static List<String> getIOSDevices() {
		List<String> list = new ArrayList<>();
		for (String str : Excute.returnlist("",
				(MainRun.OStype == Cconfig.WINDOWS ? "" : MainRun.paramsBean.getMACcmd() + "/") + CIOSCMD.IDEVICEID,
				CIOSCMD.SYSCMD, true)) {
			if (list.contains(str))
				continue;
			list.add(str.trim());
		}
		return list;
	}

	/**
	 * 机型
	 * 
	 * @param udid
	 * @return
	 */
	public static String getIOSProduct(String udid) {
		return Excute.execcmd("", (MainRun.OStype == Cconfig.WINDOWS ? "" : MainRun.paramsBean.getMACcmd() + "/")
				+ CIOSCMD.INFO_PRODUCT_IOS.replaceAll("#udid#", udid), CIOSCMD.SYSCMD, true)[0].trim();
	}

	/**
	 * IOS 版本
	 * 
	 * @param udid
	 * @return
	 */
	public static String getIOSVersion(String udid) {
		return Excute.execcmd("", (MainRun.OStype == Cconfig.WINDOWS ? "" : MainRun.paramsBean.getMACcmd() + "/")
				+ CIOSCMD.INFO_VERSION_IOS.replaceAll("#udid#", udid), CIOSCMD.SYSCMD, true)[0].trim();
	}

	public static String checklog(String udid) {
		String logstatus = StringUtil.Logs_Nodeivce;
		String cmd = "ps|grep '^[shell^|root].*logcat$'";
		if (AndroidInfo.deviceInfoMap.get(udid).get(AndroidInfo.VERSION_PS) == 1) {

		} else {
			cmd = "ps -a|grep '^[shell^|root].*logcat$'";
		}
		List<String> list = Excute.returnlist2(udid, cmd);
		// List<String> list=Excute.returnlist("ps|grep '^[shell^|root].*logcat$'",
		// 2,true);
		int logcount = 0;
		for (String str : list) {
			// com.Main.ThenToolsRun.logger.log(Level.INFO,str+111);
			if (str.equals("") || str.contains("00000000")) {// �ų�ϵͳLogcat
				continue;
			}
			logcount++;
		}
		// com.Main.ThenToolsRun.logger.log(Level.INFO,count);
		if (logcount > 5) {
			logstatus = StringUtil.Logs_RepeatRun;
		} else if (logcount >= 3) {
			logstatus = StringUtil.Logs_Run;
		} else if (logcount < 3 && MainRun.selectedID != null) {
			logstatus = StringUtil.Logs_NotRun;
		} else if (MainRun.selectedID == null) {
			logstatus = StringUtil.Logs_Nodeivce;
		}

		return logstatus;
	}

	public static String checkAPI(String udid) {
		String api = StringUtil.Logs_Nodeivce;
		if (MainRun.adbBridge.getDevice(udid) != null) {
			api = MainRun.adbBridge.getDevice(udid).getProperty("ro.build.version.sdk");
		}
		if (api.equals("")) {
			return StringUtil.Logs_Nodeivce;
		} else {
			return api;
		}
	}

	public static boolean checkDevice(String udid) {
		if (udid != null) {
			for (String str : IDlist) {
				if (str.equals(udid)) {
					return true;
				}
			}
		}
		return false;
	}

	// ~~~~~~~~~~
	public static boolean checkSIMstatus(String udid) {
		boolean hassim = false;
		String SIM1 = Excute.execcmd2(udid, "getprop gsm.sim.state").toString();
		String SIM2 = Excute.execcmd2(udid, "getprop gsm.sim.state.2").toString();
		if (SIM1.contains("READY") || SIM2.contains("READY")) {
			hassim = true;
		}
		return hassim;
	}

	// ~~~~~~~
	public static boolean checkMonkeyrun(String udid) {
//		boolean isrun = false;
//		String cmd = "ps |grep \"com.android.commands.monkey\"";
//		if (AndroidInfo.deviceInfoMap.get(udid).get(AndroidInfo.VERSION_PS) == 1) {
//
//		} else {
//			cmd = "ps -a|grep \"com.android.commands.monkey\"";
//		}
//		List<String> list = Excute.returnlist2(udid, cmd);
//		for (String str : list) {
//			if (str.equals("")) {
//				continue;
//			}
//			isrun = true;
//		}
//		return isrun;
		return AndroidInfo.getAppPID(udid, "com.android.commands.monkey").equals("") ? false : true;
	}

	public static String api2version(String api) {
		String version;
		switch (api.replace("\n", "")) {
		case "1":
			version = "Android 1.0";
			break;
		case "2":
			version = "Android 1.1";
			break;
		case "3":
			version = "Android 1.5";
			break;
		case "4":
			version = "Android 1.6";
			break;
		case "5":
			version = "Android 2.0";
			break;
		case "6":
			version = "Android 2.0.1";
			break;
		case "7":
			version = "Android 2.1";
			break;
		case "8":
			version = "Android 2.2.x";
			break;
		case "9":
			version = "Android 2.3.x";
			break;
		case "10":
			version = "Android 2.3.x";
			break;
		case "11":
			version = "Android 3.0";
			break;
		case "12":
			version = "Android 3.1";
			break;
		case "13":
			version = "Android 3.2.x";
			break;
		case "14":
			version = "Android 4.0.x";
			break;
		case "15":
			version = "Android 4.0.x";
			break;
		case "16":
			version = "Android 4.1.x";
			break;
		case "17":
			version = "Android 4.2.x";
			break;
		case "18":
			version = "Android 4.3.x";
			break;
		case "19":
			version = "Android 4.4";
			break;
		case "20":
			version = "Android 4.4w";
			break;
		case "21":
			version = "Android 5.0";
			break;
		case "22":
			version = "Android 5.1";
			break;
		case "23":
			version = "Android 6.0";
			break;
		case "24":
			version = "Android 7.0";
			break;
		case "25":
			version = "Android 7.1.x";
			break;
		case "26":
			version = "Android 8.0";
			break;
		case "27":
			version = "Android 8.1";
			break;
		default:
			version = StringUtil.Unknow;
			break;
		}

		return version;
	}
}
