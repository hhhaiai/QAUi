package com.config;

import java.io.File;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.SysConfigBean;
import com.constant.Cconfig;
import com.viewer.main.MainRun;

public class SysXmlParse extends ConfigXmlParse {
	Logger logger = LoggerFactory.getLogger(SysXmlParse.class);
	SysConfigBean config = new SysConfigBean();

	public SysXmlParse() {
		// TODO Auto-generated constructor stub
		setDoc(MainRun.settingsBean.getDatalocation() + "/sysconfig.xml");
		readConfig();
	}

	@Override
	public void readConfig() {
		config.setAndroidSDK(getStringByXpath("//androidSDK", 0).replaceAll("\\\\", "/"));
		if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
			File file = new File(config.getAndroidSDK() + "/adb.exe");
			if (file.exists()) {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/adb.exe");
			} else {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/platform-tools/adb.exe");
			}
		} else {
			File file = new File(config.getAndroidSDK() + "/adb");
			if (file.exists()) {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/adb");
			} else {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/platform-tools/adb");
			}
		}
		config.setAndroidScreenOut(getStringByXpath("//androidScreenOut", 0));
		config.setMACcmd(getStringByXpath("//MACcmd", 0).replaceAll("\\\\", "/"));
		config.setPicFont(Integer.parseInt(getStringByXpath("//picFont", 0)));
		// config.setPicOval(Integer.parseInt(getStringByXpath("//picOval",0)));
		config.setPicTargetHight(Integer.parseInt(getStringByXpath("//picTargetHight", 0)));
		config.setAppiumServerUrl(getStringByXpath("//appiumServerUrl", 0));
		config.setReportPath(getStringByXpath("//reportPath", 0).replaceAll("\\\\", "/"));
		File reportfile = new File(config.getReportPath());
		if (!reportfile.exists()) {// 当报告路径不存在时,设置为""
			config.setReportPath("");
		}

		config.setCheckOnSurface(Integer.parseInt(getStringByXpath("//checkOnSurface", 0)));
		config.setWaitforElement(Integer.parseInt(getStringByXpath("//waitforElement", 0)));
		config.setWaitAfterOperation(Integer.parseInt(getStringByXpath("//waitAfterOperation", 0)));
		config.setVerQAUiFramework(getStringByXpath("//version/verQAUiFramework", 0));
		config.setVerQAUpdateTool(getStringByXpath("//version/verQAUpdateTool", 0));

		String QAreporter_url = getStringByXpath("//QAreporter_url", 0).trim();
		if (!QAreporter_url.equals("") && !QAreporter_url.startsWith("http"))
			QAreporter_url = "http://" + QAreporter_url;
		if (!QAreporter_url.equals("") && !QAreporter_url.endsWith("/"))
			QAreporter_url = QAreporter_url + "/";
		config.setQAreporter_url(QAreporter_url);

		config.setWechat(getMapByXpath("//wechat", 0));

	}

	@Override
	public SysConfigBean getConfigBean() {
		// TODO Auto-generated method stub
		return config;
	}

	/**
	 * 保存wechat设置
	 * 
	 * @param map
	 */
	public void writeWechatMap(Map<String, String> map) {
		config.setWechat(changeMapByXPath("//wechat", 0, map));
	}

	/**
	 * 写入并设置系统参数
	 * 
	 * @param value
	 */
	public void writeQAreporter_url(String value) {
		if (!value.trim().equals("") && !value.startsWith("http"))
			value = "http://" + value;
		if (!value.trim().equals("") && !value.endsWith("/"))
			value = value + "/";
		MainRun.sysConfigBean.setQAreporter_url(changeStringByXPath("//QAreporter_url", 0, value));
	}

	public void writeWaitAfterOperation(String value) {
		MainRun.sysConfigBean
				.setWaitAfterOperation(Integer.parseInt(changeStringByXPath("//waitAfterOperation", 0, value)));
	}

	public void writeReportPath(String value) {
		File file = new File(value);
		if (file.exists() && file.isDirectory()) {
			MainRun.sysConfigBean.setReportPath(changeStringByXPath("//reportPath", 0, value.replaceAll("\\\\", "/")));
			MainRun.settingsBean.setUiReportPath(value + "/QAUiReport");
		} else {
			MainRun.sysConfigBean.setReportPath(changeStringByXPath("//reportPath", 0, ""));
			if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
				MainRun.settingsBean.setUiReportPath(
						FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/QAUiReport");
			} else {
				MainRun.settingsBean
						.setUiReportPath(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()
								+ "/Desktop/QAUiReport");
			}
		}
	}

	public void writeAppiumServerUrl(String value) {
		MainRun.sysConfigBean
				.setAppiumServerUrl(changeStringByXPath("//appiumServerUrl", 0, value.replaceAll("\\\\", "/")));
	}

	public void writeWaitforElement(String value) {
		MainRun.sysConfigBean.setWaitforElement(Integer.parseInt(changeStringByXPath("//waitforElement", 0, value)));
	}

	public void writePicTargetHight(String value) {
		MainRun.sysConfigBean.setPicTargetHight(Integer.parseInt(changeStringByXPath("//picTargetHight", 0, value)));
	}

	public void writePicFont(String value) {
		MainRun.sysConfigBean.setPicFont(Integer.parseInt(changeStringByXPath("//picFont", 0, value)));
	}

	// public void writePicOval(String value){
	// MainRun.sysConfigBean.setPicOval(Integer.parseInt(changeStringByXPath("//picOval",
	// 0, value)));
	// }
	public void writeAndroidSDK(String value) {
		MainRun.sysConfigBean.setAndroidSDK(changeStringByXPath("//androidSDK", 0, value.replaceAll("\\\\", "/")));
		if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
			File file = new File(config.getAndroidSDK() + "/adb.exe");
			if (file.exists()) {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/adb.exe");
			} else {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/platform-tools/adb.exe");
			}
		} else {
			File file = new File(config.getAndroidSDK() + "/adb");
			if (file.exists()) {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/adb");
			} else {
				config.setAndroidSDK_adb(config.getAndroidSDK() + "/platform-tools/adb");
			}
		}
	}

	public void writeAndroidScreenOut(String value) {
		MainRun.sysConfigBean.setAndroidScreenOut(changeStringByXPath("//androidScreenOut", 0, value));
	}

	public void writeMACcmd(String value) {
		MainRun.sysConfigBean.setMACcmd(changeStringByXPath("//MACcmd", 0, value.replaceAll("\\\\", "/")));
	}

	public void writeVerQAUiFramework(String value) {
		MainRun.sysConfigBean.setVerQAUiFramework(changeStringByXPath("//version/verQAUiFramework", 0, value));
	}

	public void writeVerQAUpdateTool(String value) {
		MainRun.sysConfigBean.setVerQAUpdateTool(changeStringByXPath("//version/verQAUpdateTool", 0, value));
	}
}
