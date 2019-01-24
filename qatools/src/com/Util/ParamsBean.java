package com.Util;

public class ParamsBean {
	String Base_firstInit;

	String Logs_crashPath;
	String Logs_inputcmd;
	String Logs_highlight;

	String Monkey_filterPackages;
	String Monkey_Customize;
	String Monkey_arow;
	String Monkey_arowword;
	String Monkey_showduplicate;
	String Monkey_isreconnect;

	String androidSDK;
	String androidSDK_adb;
	String MACcmd;
	String reportPath;

	String IOS_Logs_highlight;
	String IOS_Logs_inputcmd;
	String IOS_Logs_App_highlight;
	String IOS_Logs_App_inputcmd;
	String IOS_Logs_App_path;

	String Live_username;
	String Live_password;
	String Live_Server;
	String Live_videopath;
	boolean Live_loop;
	String Live_config_title;
	String Live_config_coverUrl;
	String Live_config_platform;
	boolean Live_config_showlocation;
	String Live_config_tags;

	String Bigdata_filter_event;
	String Bigdata_note_path;

	String Performance_packagename;
	String Performance_xAxis_num;

	String PicInspect_folder;
	String PicInspect_zoom;

	String DataHandel_filepath;

	// base
	public String getBase_firstInit() {
		return Base_firstInit;
	}

	public void setBase_firstInit(String base_firstInit) {
		Base_firstInit = base_firstInit;
	}

	// Logs
	public String getLogs_crashPath() {
		return Logs_crashPath;
	}

	public void setLogs_crashPath(String logs_crashPath) {
		Logs_crashPath = logs_crashPath;
	}

	public String getLogs_inputcmd() {
		return Logs_inputcmd;
	}

	public void setLogs_inputcmd(String logs_inputcmd) {
		Logs_inputcmd = logs_inputcmd;
	}

	public String getLogs_highlight() {
		return Logs_highlight;
	}

	public void setLogs_highlight(String logs_highlight) {
		Logs_highlight = logs_highlight;
	}

	// Monkey
	public String getMonkey_filterPackages() {
		return Monkey_filterPackages;
	}

	public void setMonkey_filterPackages(String monkey_filterPackages) {
		Monkey_filterPackages = monkey_filterPackages;
	}

	public String getMonkey_Customize() {
		return Monkey_Customize;
	}

	public void setMonkey_Customize(String monkey_Customize) {
		Monkey_Customize = monkey_Customize;
	}

	public String getMonkey_arow() {
		return Monkey_arow;
	}

	public void setMonkey_arow(String monkey_arow) {
		Monkey_arow = monkey_arow;
	}

	public String getMonkey_arowword() {
		return Monkey_arowword;
	}

	public void setMonkey_arowword(String monkey_arowword) {
		Monkey_arowword = monkey_arowword;
	}

	public String getMonkey_showduplicate() {
		return Monkey_showduplicate;
	}

	public void setMonkey_showduplicate(String monkey_showduplicate) {
		Monkey_showduplicate = monkey_showduplicate;
	}

	public String getMonkey_isreconnect() {
		return Monkey_isreconnect;
	}

	public void setMonkey_isreconnect(String monkey_isreconnect) {
		Monkey_isreconnect = monkey_isreconnect;
	}

	public String ReadParams() {

		return "Params: " + "Base_firstInit=" + getBase_firstInit() + ",Monkey_filterPackages="
				+ getMonkey_filterPackages() + ",Monkey_arow=" + getMonkey_arow() + ",Monkey_arowword="
				+ getMonkey_arowword() + ",Monkey_showduplicate=" + getMonkey_showduplicate() + ",Monkey_isreconnect="
				+ getMonkey_isreconnect() + "Logs_crashPath=" + getLogs_crashPath();
	}

	public String getAndroidSDK() {
		return androidSDK;
	}

	public void setAndroidSDK(String androidSDK) {
		this.androidSDK = androidSDK;
	}

	public String getMACcmd() {
		return MACcmd;
	}

	public void setMACcmd(String mACcmd) {
		MACcmd = mACcmd;
	}

	public String getAndroidSDK_adb() {
		return androidSDK_adb;
	}

	public void setAndroidSDK_adb(String androidSDK_adb) {
		this.androidSDK_adb = androidSDK_adb;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public String getIOS_Logs_highlight() {
		return IOS_Logs_highlight;
	}

	public void setIOS_Logs_highlight(String iOS_Logs_highlight) {
		IOS_Logs_highlight = iOS_Logs_highlight;
	}

	public String getIOS_Logs_inputcmd() {
		return IOS_Logs_inputcmd;
	}

	public void setIOS_Logs_inputcmd(String iOS_Logs_inputcmd) {
		IOS_Logs_inputcmd = iOS_Logs_inputcmd;
	}

	public String getLive_username() {
		return Live_username;
	}

	public void setLive_username(String live_username) {
		Live_username = live_username;
	}

	public String getLive_password() {
		return Live_password;
	}

	public void setLive_password(String live_password) {
		Live_password = live_password;
	}

	public String getLive_Server() {
		return Live_Server;
	}

	public void setLive_Server(String live_Server) {
		Live_Server = live_Server;
	}

	public String getLive_videopath() {
		return Live_videopath;
	}

	public void setLive_videopath(String live_videopath) {
		Live_videopath = live_videopath;
	}

	public boolean isLive_loop() {
		return Live_loop;
	}

	public void setLive_loop(boolean live_loop) {
		Live_loop = live_loop;
	}

	public String getLive_config_title() {
		return Live_config_title;
	}

	public void setLive_config_title(String live_config_title) {
		Live_config_title = live_config_title;
	}

	public String getLive_config_coverUrl() {
		return Live_config_coverUrl;
	}

	public void setLive_config_coverUrl(String live_config_coverUrl) {
		Live_config_coverUrl = live_config_coverUrl;
	}

	public String getLive_config_platform() {
		return Live_config_platform;
	}

	public void setLive_config_platform(String live_config_platform) {
		Live_config_platform = live_config_platform;
	}

	public String getLive_config_tags() {
		return Live_config_tags;
	}

	public void setLive_config_tags(String live_config_tags) {
		Live_config_tags = live_config_tags;
	}

	public boolean isLive_config_showlocation() {
		return Live_config_showlocation;
	}

	public void setLive_config_showlocation(boolean live_config_showlocation) {
		Live_config_showlocation = live_config_showlocation;
	}

	// bigdata
	public String getBigdata_filter_event() {
		return Bigdata_filter_event;
	}

	public void setBigdata_filter_event(String bigdata_filter_event) {
		Bigdata_filter_event = bigdata_filter_event;
	}

	public String getBigdata_note_path() {
		return Bigdata_note_path;
	}

	public void setBigdata_note_path(String bigdata_note_path) {
		Bigdata_note_path = bigdata_note_path;
	}

	public String getIOS_Logs_App_highlight() {
		return IOS_Logs_App_highlight;
	}

	public void setIOS_Logs_App_highlight(String iOS_Logs_App_highlight) {
		IOS_Logs_App_highlight = iOS_Logs_App_highlight;
	}

	public String getIOS_Logs_App_inputcmd() {
		return IOS_Logs_App_inputcmd;
	}

	public void setIOS_Logs_App_inputcmd(String iOS_Logs_App_inputcmd) {
		IOS_Logs_App_inputcmd = iOS_Logs_App_inputcmd;
	}

	public String getIOS_Logs_App_path() {
		return IOS_Logs_App_path;
	}

	public void setIOS_Logs_App_path(String iOS_Logs_App_path) {
		IOS_Logs_App_path = iOS_Logs_App_path;
	}

	public String getPerformance_packagename() {
		return Performance_packagename;
	}

	public void setPerformance_packagename(String performance_packagename) {
		Performance_packagename = performance_packagename;
	}

	public String getPerformance_xAxis_num() {
		return Performance_xAxis_num;
	}

	public void setPerformance_xAxis_num(String performance_xAxis_num) {
		Performance_xAxis_num = performance_xAxis_num;
	}

	public String getPicInspect_folder() {
		return PicInspect_folder;
	}

	public void setPicInspect_folder(String picInspect_folder) {
		PicInspect_folder = picInspect_folder;
	}

	public String getPicInspect_zoom() {
		return PicInspect_zoom;
	}

	public void setPicInspect_zoom(String picInspect_zoom) {
		PicInspect_zoom = picInspect_zoom;
	}

	public String getDataHandel_filepath() {
		return DataHandel_filepath;
	}

	public void setDataHandel_filepath(String dataHandel_filepath) {
		DataHandel_filepath = dataHandel_filepath;
	}

}
