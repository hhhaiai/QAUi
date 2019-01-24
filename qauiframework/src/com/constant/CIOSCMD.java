package com.constant;

public interface CIOSCMD {
	int SYSCMD = 1;
	int CUSCMD = 2;
	/**
	 * iOS命令
	 */
	// 截图
	String SCREEN_CAP_IOS = "idevicescreenshot -u #udid# \"#savepath#\"";
	// syslog
	String IDEVICESYSLOG = "idevicesyslog -u #udid#";

	String IDEVICEID = "idevice_id -l";// 设备列表

	String IDEVICESYSLOG_PID = "ps -A|grep idevicesyslog|grep -v grep|awk '{print $1,$4}'";

	String IDEVICESYSLOG_STOP = "kill -9 #pid#";

	String IDEVICEINSTALLER = "ideviceinstaller -u #udid# -l";// 应用列表
	// info命令
	String INFO_VERSION_IOS = "ideviceinfo -u #udid# -k ProductVersion";

	String INFO_DEVICENAME_IOS = "ideviceinfo -u #udid# -k DeviceName";

	String INFO_DEVICECOLOR_IOS = "ideviceinfo -u #udid# -k DeviceColor";

	// String INFO_PRODUCT_IOS="ideviceinfo -u #udid# -k ProductType|awk -F'[,]'
	// '{print $1}'";
	String INFO_PRODUCT_IOS = "ideviceinfo -u #udid# -k ProductType";

	String INFO_TimeZone_IOS = "ideviceinfo -u #udid# -k TimeZone";
}
