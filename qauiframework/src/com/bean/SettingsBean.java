package com.bean;

import javax.swing.ImageIcon;

public class SettingsBean {
	int System;//0=win,1=mac,other=linux
	String extraBinlocation;
	String UiReportPath;
	String Datalocation;
	ImageIcon logo;
	
	public int getSystem() {
		return System;
	}
	public void setSystem(int system) {
		System = system;
	}
	public String getExtraBinlocation() {
		return extraBinlocation;
	}
	public void setExtraBinlocation(String extraBinlocation) {
		this.extraBinlocation = extraBinlocation;
	}
	public String getDatalocation() {
		return Datalocation;
	}
	public void setDatalocation(String datalocation) {
		Datalocation = datalocation;
	}
	public String getUiReportPath() {
		return UiReportPath;
	}
	public void setUiReportPath(String uiReportPath) {
		UiReportPath = uiReportPath;
	}
	public ImageIcon getLogo() {
		return logo;
	}
	public void setLogo(ImageIcon logo) {
		this.logo = logo;
	}

	
}
