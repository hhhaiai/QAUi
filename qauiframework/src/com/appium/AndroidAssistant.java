package com.appium;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.helper.ADBUtil;
import com.helper.AndroidInfo;
import com.helper.HelperUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidRecord;
import com.review.getscreen.AndroidShot;

public class AndroidAssistant extends BaseAssistant {
	Logger logger = LoggerFactory.getLogger(AndroidAssistant.class);
	AndroidShot Shot;
	AndroidRecord androidRecord;
	float sTraficRx = -1, sTraficTx = -1, sTraficRx_wlan = -1, sTraficTx_wlan = -1, sTraficRx_rmnet = -1,
			sTraficTx_rmnet = -1;// 流量初始标记
	float tempTraficRx = -1, tempTraficTx = -1, tempTraficRx_wlan = -1, tempTraficTx_wlan = -1, tempTraficRx_rmnet = -1,
			tempTraficTx_rmnet = -1;// 流量临时标记

	public AndroidAssistant(Map<String, String> capabilityMap, SceneLogUtil oplog, Object baseShot,
			AndroidRecord androidRecord) {
		super(capabilityMap, oplog, baseShot);
		// TODO Auto-generated constructor stub
		this.udid = capabilityMap.get(Cparams.udid);
		Shot = (AndroidShot) baseShot;
		this.androidRecord = androidRecord;
	}

	@Override
	public File getDefalutScreenShotFolder() {
		return Shot.getDefalutCustomerScreenShotPath();
	}

	@Override
	public String ReportVideoScreenShot(String name, String text) {
		if (!ScreenShotSwitch)
			return null;
		String path = Shot.drawText(name, text);
		oplog.logInfo("自定义报告视频截图:" + path);
		return path;
	}

	@Override
	public void ScreenShotSwitch(boolean open) {
		this.ScreenShotSwitch = open;
	}

	@Override
	public String ScreenShotWithFlag(String name, String text) {
		// TODO Auto-generated method stub
		if (!ScreenShotSwitch)
			return null;
		String path = Shot.drawTextByCustomer(name, text, null);
		oplog.logInfo("自定义截图:" + path);
		return path;
	}

	@Override
	public String ScreenShotWithFlag(String name, String str, String picpath) {
		// TODO Auto-generated method stub
		if (!ScreenShotSwitch)
			return null;
		String path = Shot.drawTextByCustomer(name, str, picpath);
		oplog.logInfo("自定义截图:" + path);
		return path;
	}

	@Override
	public String ScreenShot(String name, String picpath) {
		// TODO Auto-generated method stub
		if (!ScreenShotSwitch)
			return null;
		String path = Shot.ScreenShotByCustomer(name, picpath);
		oplog.logInfo("自定义无标记截图:" + path);
		return path;
	}

	@Override
	public String ScreenShot(String name) {
		// TODO Auto-generated method stub
		if (!ScreenShotSwitch)
			return null;
		String path = Shot.ScreenShotByCustomer(name, null);
		oplog.logInfo("自定义无标记截图:" + path);
		return path;
	}

	@Override
	public int getDevice_width() {
		return Shot.getDevice_width();
	}

	@Override
	public int getDevice_hight() {
		return Shot.getDevice_hight();
	}

	@Override
	public int getZoom() {
		return Shot.getZoom();
	}

	/**
	 * 得到内存大小 MB
	 * 
	 * @param udid
	 * @return 失败返回-1
	 */
	public int getDeviceMemroy() {
		return AndroidInfo.getDeviceMemroy(udid);
	}

	/**
	 * 获取设置的应用路径
	 * 
	 * @return
	 */
	public String getAppPath() {
		return capabilityMap.get("app");
	}

	/**
	 * 停止应用
	 * 
	 * @return
	 */
	public boolean stopApp() {
		return AndroidInfo.stopApp(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 得到应用包名
	 * 
	 * @return
	 */
	public String getAppPackage() {
		return capabilityMap.get("appPackage");
	}

	/**
	 * 得到应用启动Activity名
	 * 
	 * @return
	 */
	public String getAppActivity() {
		return capabilityMap.get("appActivity");
	}

	/**
	 * 得到应用当前的PID
	 * 
	 * @return
	 */
	public String getAppPID() {
		return AndroidInfo.getAppPID(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 得到应用当前的UID
	 * 
	 * @return
	 */
	public String getAppUID() {
		return AndroidInfo.getAppUID(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 检查应用是否在运行
	 * 
	 * @return
	 */
	public boolean checkOnSurface() {
		return AndroidInfo.checkOnSurface(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 检查应用是否存活
	 * 
	 * @return
	 */
	public boolean checkIsAlive() {
		return AndroidInfo.checkIsAlive(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 返回ro.build.product信息
	 * 
	 * @param udid
	 * @return
	 */
	public String getProduct() {
		return AndroidInfo.getProduct(udid);
	}

	/**
	 * 获取系统语言
	 * 
	 * @return
	 */
	public String getLanguage() {
		return AndroidInfo.getLanguage(udid);
	}

	/**
	 * 返回ro.product.model信息
	 * 
	 * @param udid
	 * @return
	 */
	public String getModel() {
		return AndroidInfo.getModel(udid);
	}

	/**
	 * 返回ro.product.manufacturer信息
	 * 
	 * @param udid
	 * @return
	 */
	public String getManufacturer() {
		return AndroidInfo.getManufacturer(udid);
	}

	/**
	 * 返回ro.build.version.sdk信息
	 * 
	 * @param udid
	 * @return
	 */
	public String getSDKVersion() {
		return AndroidInfo.getSDKVersion(udid);
	}

	/**
	 * 返回ro.build.version.release信息
	 * 
	 * @param udid
	 * @return
	 */
	public String getVersion() {
		return AndroidInfo.getVersion(udid);
	}

	/**
	 * 获取电池电量
	 * 
	 * @param udid
	 * @return
	 */
	public int getBatteryLevel() {
		return AndroidInfo.getBatteryLevel(udid);
	}

	/**
	 * 获取当前系统CPU使用率(top -n 1 -m 1)<br>
	 * 
	 * @return 单位: % (出错则返回-1)
	 */
	public float getSysCpuRate() {
		return AndroidInfo.getSysCpuRate(udid);
	}

	/**
	 * 获取当前应用主程序CPU使用率(top -n 1|grep pid)
	 * 
	 * @return 单位: % (出错则返回-1)
	 */
	public float getAppCpuRate() {
		return AndroidInfo.getAppCpuRate(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 获取系统内存使用率(dumpsys meminfo|grep RAM:)<br>
	 * 
	 * @return 单位: 0=实用内存MB,1=空闲内存MB,2=使用率%(出错则返回-1)
	 */
	public float[] getSysMemRate() {
		return AndroidInfo.getSysMemSize(udid);
	}

	/**
	 * 获取应用占用内存大小(dumpsys meminfo package|grep TOTAL)
	 * 
	 * @return 单位:MB (出错则返回-1)
	 */
	public int getAppMemSize() {
		return AndroidInfo.getAppMemSize(udid, capabilityMap.get("appPackage"));
	}

	/**
	 * 得到设备系统时间
	 * 
	 * @return
	 */
	public long getDeviceTime() {
		return AndroidInfo.getDeviceTime(udid);
	}

	/**
	 * 开启流量统计
	 */
	public boolean openTraficStatistics() {
		float[] trafic = AndroidInfo.getAppTraffic(udid, capabilityMap.get("appPackage"));
		sTraficRx = trafic[0];
		sTraficTx = trafic[1];
		sTraficRx_wlan = trafic[2];
		sTraficTx_wlan = trafic[3];
		sTraficRx_rmnet = trafic[4];
		sTraficTx_rmnet = trafic[5];
		tempTraficRx = sTraficRx;
		tempTraficTx = sTraficTx;
		tempTraficRx_wlan = sTraficRx_wlan;
		tempTraficTx_wlan = sTraficTx_wlan;
		tempTraficRx_rmnet = sTraficRx_rmnet;
		tempTraficTx_rmnet = sTraficTx_rmnet;
		if (sTraficRx == -1 || sTraficTx == -1 || sTraficRx_wlan == -1 || sTraficTx_wlan == -1 || sTraficRx_rmnet == -1
				|| sTraficTx_rmnet == -1) {
			oplog.logWarn("开启流量查询异常");
		} else {
			oplog.logCustomer("开启流量统计...");
			return false;
		}
		return true;
	}

	/**
	 * 显示从开始流量统计或上一次显示流量统计后的流量计数(需提前开启openTraficStatistics方法)
	 * 
	 * @return 0总接收rx_MB,1总发送tx_MB,2wlan接收rx_MB_wlan,3wlan发送tx_MB_wlan,4移动接收rx_MB_rmnet,5移动发送tx_MB_rmnet
	 *         (出错则返回-1D)
	 */
	public float[] showTraficStatistics() {
		String str = null;
		float[] result = new float[] { -1f, -1f, -1f, -1f, -1f, -1f };
		float[] trafic = AndroidInfo.getAppTraffic(udid, capabilityMap.get("appPackage"));
		if (tempTraficRx != -1 && tempTraficTx != -1) {
			DecimalFormat df = new DecimalFormat("0.00");
			float TraficRx = trafic[0];
			float TraficTx = trafic[1];
			float TraficRx_wlan = trafic[2];
			float TraficTx_wlan = trafic[3];
			float TraficRx_rmnet = trafic[4];
			float TraficTx_rmnet = trafic[5];
			float difTraficRx = TraficRx - tempTraficRx;
			float difTraficTx = TraficTx - tempTraficTx;
			float difTraficRx_wlan = TraficRx_wlan - tempTraficRx_wlan;
			float difTraficTx_wlan = TraficTx_wlan - tempTraficTx_wlan;
			float difTraficRx_rmnet = TraficRx_rmnet - tempTraficRx_rmnet;
			float difTraficTx_rmnet = TraficTx_rmnet - tempTraficTx_rmnet;
			str = "下载流量:" + HelperUtil.getFloatDecimal(difTraficRx, 2) + "MB,上传流量:"
					+ HelperUtil.getFloatDecimal(difTraficTx, 2) + "MB" + "(wlan:"
					+ HelperUtil.getFloatDecimal(difTraficRx_wlan, 2) + "↓/"
					+ HelperUtil.getFloatDecimal(difTraficTx_wlan, 2) + "↑,rmnet:"
					+ HelperUtil.getFloatDecimal(difTraficRx_rmnet, 2) + "↓/"
					+ HelperUtil.getFloatDecimal(difTraficTx_rmnet, 2) + "↑)";
			tempTraficRx = TraficRx;
			tempTraficTx = TraficTx;
			tempTraficRx_wlan = TraficRx_wlan;
			tempTraficTx_wlan = TraficTx_wlan;
			tempTraficRx_rmnet = TraficRx_rmnet;
			tempTraficTx_rmnet = TraficTx_rmnet;
			result = new float[] { HelperUtil.getFloatDecimal(difTraficRx, 2),
					HelperUtil.getFloatDecimal(difTraficTx, 2), HelperUtil.getFloatDecimal(difTraficRx_wlan, 2),
					HelperUtil.getFloatDecimal(difTraficTx_wlan, 2), HelperUtil.getFloatDecimal(difTraficRx_rmnet, 2),
					HelperUtil.getFloatDecimal(difTraficTx_rmnet, 2) };
		} else {
			str = "查询流量异常";
		}
		oplog.logCustomer(str);
		return result;
	}

	/**
	 * 显示从开始流量统计到现在的累计流量计数(需提前开启openTraficStatistics方法)
	 * 
	 * @return 0总接收rx_MB,1总发送tx_MB,2wlan接收rx_MB_wlan,3wlan发送tx_MB_wlan,4移动接收rx_MB_rmnet,5移动发送tx_MB_rmnet
	 *         (出错则返回-1D)
	 */
	public float[] showTotalTraficStatistics() {
		String str = null;
		float[] result = new float[] { -1f, -1f, -1f, -1f, -1f, -1f };
		float[] trafic = AndroidInfo.getAppTraffic(udid, capabilityMap.get("appPackage"));
		if (sTraficRx != -1 && sTraficTx != -1) {
			DecimalFormat df = new DecimalFormat("0.00");
			float TraficRx = trafic[0];
			float TraficTx = trafic[1];
			float TraficRx_wlan = trafic[2];
			float TraficTx_wlan = trafic[3];
			float TraficRx_rmnet = trafic[4];
			float TraficTx_rmnet = trafic[5];
			float difTraficRx = TraficRx - sTraficRx;
			float difTraficTx = TraficTx - sTraficTx;
			float difTraficRx_wlan = TraficRx_wlan - sTraficRx_wlan;
			float difTraficTx_wlan = TraficTx_wlan - sTraficTx_wlan;
			float difTraficRx_rmnet = TraficRx_rmnet - sTraficRx_rmnet;
			float difTraficTx_rmnet = TraficTx_rmnet - sTraficTx_rmnet;
			str = "下载流量:" + HelperUtil.getFloatDecimal(difTraficRx, 2) + "MB,上传流量:"
					+ HelperUtil.getFloatDecimal(difTraficTx, 2) + "MB" + "(wlan:"
					+ HelperUtil.getFloatDecimal(difTraficRx_wlan, 2) + "↓/"
					+ HelperUtil.getFloatDecimal(difTraficTx_wlan, 2) + "↑,rmnet:"
					+ HelperUtil.getFloatDecimal(difTraficRx_rmnet, 2) + "↓/"
					+ HelperUtil.getFloatDecimal(difTraficTx_rmnet, 2) + "↑)";
			result = new float[] { HelperUtil.getFloatDecimal(difTraficRx, 2),
					HelperUtil.getFloatDecimal(difTraficTx, 2), HelperUtil.getFloatDecimal(difTraficRx_wlan, 2),
					HelperUtil.getFloatDecimal(difTraficTx_wlan, 2), HelperUtil.getFloatDecimal(difTraficRx_rmnet, 2),
					HelperUtil.getFloatDecimal(difTraficTx_rmnet, 2) };
		} else {
			str = "查询总流量异常";
		}
		oplog.logCustomer(str);
		return result;
	}

	/**
	 * 屏幕录制,最长时间3分钟
	 * 
	 * @param filename 视频命名
	 */
	public void screenRecord(String filename) {
		androidRecord.start(filename, null);
	}

	/**
	 * 停止屏幕录制
	 */
	public void stopScreenRecord() {
		androidRecord.stop();
	}

	/**
	 * 屏幕录制,最长时间3分钟
	 * 
	 * @param filename 视频命令
	 * @param path     自定义视频保存路径
	 */
	public void screenRecord(String filename, String path) {
		androidRecord.start(filename, path);
	}

	/**
	 * 执行adb shell命令
	 * 
	 * @param command
	 * @return
	 */
	public StringBuffer executeShell(String command) {
		return ADBUtil.execcmd(udid, command);
	}

	/**
	 * 执行adb shell命令
	 * 
	 * @param command
	 * @return 返回以\n分割的列表
	 */
	public List<String> executeShellAndReturnList(String command) {
		return ADBUtil.returnlist(udid, command);
	}

	/**
	 * 从设备存储拉出文件到PC
	 * 
	 * @param source 设备源文件绝对路径
	 * @param target 本地保存文件绝对路径
	 * @return
	 */
	public boolean pullfile(String source, String target) {
		return ADBUtil.pullfile(udid, source, target);
	}

	// settings
	/**
	 * 获取屏幕亮度模式
	 * 
	 * @param udid
	 * @return true=自动 false=手动
	 */
	public boolean getScreenBrightnessMode() {
		return AndroidInfo.getScreenBrightnessMode(udid);
	}

	/**
	 * 设置屏幕亮度模式是否为自动
	 * 
	 * @param udid
	 * @param on   自动=true,手动=false
	 * @return
	 */
	public boolean setScreenBrightnessMode(boolean on) {
		return AndroidInfo.setScreenBrightnessMode(udid, on);
	}

	/**
	 * 获取屏幕亮度值
	 * 
	 * @param udid
	 * @return -1=获取错误
	 */
	public int getScreenBrightness() {
		return AndroidInfo.getScreenBrightness(udid);
	}

	/**
	 * 设置屏幕亮度值
	 * 
	 * @param udid
	 * @param value
	 * @return
	 */
	public boolean setScreenBrightness(int value) {
		return AndroidInfo.setScreenBrightness(udid, value);
	}

	/**
	 * 获取屏幕休眠时间,单位秒
	 * 
	 * @param udid
	 * @return -1为获取错误
	 */
	public int getScreenOffTimeout() {
		return AndroidInfo.getScreenOffTimeout(udid);
	}

	/**
	 * 设置屏幕休眠时间
	 * 
	 * @param udid
	 * @param value 单位秒
	 * @return
	 */
	public boolean setScreenOffTimeout(int value) {
		return AndroidInfo.setScreenOffTimeout(udid, value);
	}

	/**
	 * 是否为自动获取时间
	 * 
	 * @param udid
	 * @return
	 */
	public boolean getAutoTime() {
		return AndroidInfo.getAutoTime(udid);
	}

	/**
	 * 设置自动获取时间
	 * 
	 * @param udid
	 * @param on
	 * @return
	 */
	public boolean setAutoTime(boolean on) {
		return AndroidInfo.setAutoTime(udid, on);
	}

	/**
	 * 是否为自动获取时区
	 * 
	 * @param udid
	 * @return
	 */
	public boolean getAutoTimeZone() {
		return AndroidInfo.getAutoTimeZone(udid);
	}

	/**
	 * 设置自动获取时区
	 * 
	 * @param udid
	 * @param on
	 * @return
	 */
	public boolean setAutoTimeZone(boolean on) {
		return AndroidInfo.setAutoTimeZone(udid, on);
	}

	/**
	 * 获取wifi是否为开
	 * 
	 * @param udid
	 * @return
	 */
	public boolean getWifiOn() {
		return AndroidInfo.getWifiOn(udid);
	}

	/**
	 * 设置wifi开关,部分机型无效
	 * 
	 * @param udid
	 * @param on
	 * @return
	 */
	@Deprecated
	public boolean setWifiOn(boolean on) {
		return AndroidInfo.setWifiOn(udid, on);
	}
}
