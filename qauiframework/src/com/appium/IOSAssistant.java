package com.appium;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.helper.IOSInfo;
import com.log.SceneLogUtil;
import com.review.getscreen.IOSShot;

public class IOSAssistant extends BaseAssistant {
	Logger logger = LoggerFactory.getLogger(IOSAssistant.class);
	IOSShot Shot;

	public IOSAssistant(Map<String, String> capabilityMap, SceneLogUtil oplog, Object baseShot) {
		super(capabilityMap, oplog, baseShot);
		// TODO Auto-generated constructor stub
		this.udid = capabilityMap.get(Cparams.udid);
		Shot = (IOSShot) baseShot;
	}

	@Override
	public File getDefalutScreenShotFolder() {
		return Shot.getDefalutCustomerScreenShotPath();
	}

	@Override
	public void ScreenShotSwitch(boolean open) {
		this.ScreenShotSwitch = open;
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
	public String ScreenShotWithFlag(String name, String str) {
		// TODO Auto-generated method stub
		if (!ScreenShotSwitch)
			return null;
		String path = Shot.drawTextByCustomer(name, str, null);
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
	 * 得到屏幕宽,像素点
	 * 
	 * @return
	 */
	public int getDeviceWidth() {
		return Shot.getDevice_width() * Shot.getZoom();
	}

	/**
	 * 得到屏幕高,像素点
	 * 
	 * @return
	 */
	public int getDeviceHight() {
		return Shot.getDevice_hight() * Shot.getZoom();
	}

	/**
	 * 机型
	 * 
	 * @param udid
	 * @return
	 */
	public String getProduct() {
		return IOSInfo.getProduct(udid);
	}

	/**
	 * IOS 版本
	 * 
	 * @param udid
	 * @return
	 */
	public String getVersion() {
		return IOSInfo.getVersion(udid);
	}

	/**
	 * 设备名称
	 * 
	 * @param udid
	 * @return
	 */
	public String getDeviceName() {
		return IOSInfo.getDeviceName(udid);
	}

	/**
	 * 设备颜色
	 * 
	 * @param udid
	 * @return
	 */
	public String getDeviceColor() {
		return IOSInfo.getDeviceColor(udid);
	}

	/**
	 * 设备时区
	 * 
	 * @param udid
	 * @return
	 */
	public String getTimeZone() {
		return IOSInfo.getTimeZone(udid);
	}

}
