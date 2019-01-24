package com.bean;

import java.util.Map;

public class SysConfigBean {
	private int picTargetHight;
	private String appiumServerUrl;
	private String reportPath;
	private int checkOnSurface;
	private int waitforElement;
	private int picFont;
	private int picOval;
	private String androidSDK;
	private String androidSDK_adb;
	private String MACcmd;
	private int waitAfterOperation;
	private String verQAUiFramework;
	private String verQAUpdateTool;
	private String QAreporter_url;
	private String androidScreenOut;

	Map<String, String> wechat;

	public String getQAreporter_url() {
		return QAreporter_url;
	}

	public void setQAreporter_url(String qAreporter_url) {
		this.QAreporter_url = qAreporter_url;
	}

	public int getPicTargetHight() {
		return picTargetHight;
	}

	public void setPicTargetHight(int picTargetHight) {
		this.picTargetHight = picTargetHight;
	}

	public String getAppiumServerUrl() {
		return appiumServerUrl;
	}

	public void setAppiumServerUrl(String appiumServerUrl) {
		this.appiumServerUrl = appiumServerUrl;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public int getCheckOnSurface() {
		return checkOnSurface;
	}

	public void setCheckOnSurface(int checkOnSurface) {
		this.checkOnSurface = checkOnSurface;
	}

	public int getWaitforElement() {
		return waitforElement;
	}

	public void setWaitforElement(int waitforElement) {
		this.waitforElement = waitforElement;
	}

	public int getPicFont() {
		return picFont;
	}

	public void setPicFont(int picFont) {
		this.picFont = picFont;
	}

	public int getPicOval() {
		return picOval;
	}

	public void setPicOval(int picOval) {
		this.picOval = picOval;
	}

	public String getAndroidSDK() {
		return androidSDK;
	}

	public void setAndroidSDK(String androidSDK) {
		this.androidSDK = androidSDK;
	}

	public String getAndroidSDK_adb() {
		return androidSDK_adb;
	}

	public void setAndroidSDK_adb(String androidSDK_adb) {
		this.androidSDK_adb = androidSDK_adb;
	}

	public String getMACcmd() {
		return MACcmd;
	}

	public void setMACcmd(String MACcmd) {
		this.MACcmd = MACcmd;
	}

	public int getWaitAfterOperation() {
		return waitAfterOperation;
	}

	public void setWaitAfterOperation(int waitAfterOperation) {
		this.waitAfterOperation = waitAfterOperation;
	}

	public String getVerQAUiFramework() {
		return verQAUiFramework;
	}

	public void setVerQAUiFramework(String verQAUiFramework) {
		this.verQAUiFramework = verQAUiFramework;
	}

	public String getVerQAUpdateTool() {
		return verQAUpdateTool;
	}

	public void setVerQAUpdateTool(String verQAUpdateTool) {
		this.verQAUpdateTool = verQAUpdateTool;
	}

	public String getAndroidScreenOut() {
		return androidScreenOut;
	}

	public void setAndroidScreenOut(String androidScreenOut) {
		this.androidScreenOut = androidScreenOut;
	}

	public Map<String, String> getWechat() {
		return wechat;
	}

	public void setWechat(Map<String, String> wechat) {
		this.wechat = wechat;
	}

}
