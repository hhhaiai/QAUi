package com.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CIOSCMD;
import com.log.SceneLogUtil;
import com.viewer.main.MainRun;

import io.appium.java_client.ios.IOSDriver;

public class IOSInfo {
	static Logger logger = LoggerFactory.getLogger(IOSInfo.class);

	/**
	 * 得到设备信息
	 * 
	 * @return
	 */
	public static Map<String, String> getDeviceInfo(String udid) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("机型", getProduct(udid));
		map.put("版本", getVersion(udid));
		map.put("名称", getDeviceName(udid));
		map.put("时区", getTimeZone(udid));
		int hight = 0, width = 0, pointhight = 0, pointwidth = 0;
		map.put("分辨率", hight + "x" + width);
		map.put("点阵", pointhight + "x" + pointwidth);
		return map;
	}

	/**
	 * 机型
	 * 
	 * @param udid
	 * @return
	 */
	public static String getProduct(String udid) {
		String product = CMDUtil.execcmd(
				MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.INFO_PRODUCT_IOS.replaceAll("#udid#", udid),
				CIOSCMD.SYSCMD, true)[0].trim();
		switch (product) {
		case "iPhone4,1":
			product = "iPhone4S";
			break;
		case "iPhone5,2":
			product = "iPhone5";
			break;
		case "iPhone6,1":
			product = "iPhone5S";
			break;
		case "iPhone7,2":
			product = "iPhone6";
			break;
		case "iPhone8,1":
			product = "iPhone6S";
			break;
		case "iPhone9,1":
			product = "iPhone7";
			break;
		case "iPhone9,2":
			product = "iPhone7Puls";
			break;
		case "iPhone10,1":
			product = "iPhone8";
			break;
		case "iPhone10,2":
			product = "iPhone8Plus";
			break;
		case "?????iPhone11,1":
			product = "iPhoneX";
			break;
		default:
			break;
		}
		return product;
	}

	/**
	 * IOS 版本
	 * 
	 * @param udid
	 * @return
	 */
	public static String getVersion(String udid) {
		return CMDUtil.execcmd(
				MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.INFO_VERSION_IOS.replaceAll("#udid#", udid),
				CIOSCMD.SYSCMD, true)[0].trim();
	}

	/**
	 * 设备名称
	 * 
	 * @param udid
	 * @return
	 */
	public static String getDeviceName(String udid) {
		return CMDUtil.execcmd(
				MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.INFO_DEVICENAME_IOS.replaceAll("#udid#", udid),
				CIOSCMD.SYSCMD, true)[0].trim();
	}

	/**
	 * 设备颜色
	 * 
	 * @param udid
	 * @return
	 */
	public static String getDeviceColor(String udid) {
		return CMDUtil.execcmd(
				MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.INFO_DEVICECOLOR_IOS.replaceAll("#udid#", udid),
				CIOSCMD.SYSCMD, true)[0].trim();
	}

	/**
	 * 设备时区
	 * 
	 * @param udid
	 * @return
	 */
	public static String getTimeZone(String udid) {
		return CMDUtil.execcmd(
				MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.INFO_TimeZone_IOS.replaceAll("#udid#", udid),
				CIOSCMD.SYSCMD, true)[0].trim();
	}

	/**
	 * 得到设备列表
	 * 
	 * @return
	 */
	public static List<String> getDevices() {
		List<String> list = new ArrayList<>();
		for (String str : CMDUtil.returnlist(MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.IDEVICEID,
				CIOSCMD.SYSCMD, true)) {
			if (list.contains(str))
				continue;
			list.add(str.trim());
		}
		return list;
	}

	/**
	 * 等待新窗口(原理: 界面元素不再变化)
	 * 
	 * @param driver
	 * @param oplog
	 */
	public static String waitForNewWindow(IOSDriver<WebElement> driver, SceneLogUtil oplog) {
		String[] temp = getPageSource(driver);
		String[] pagesource = { "", "" };
		int count = 0;
		for (int j = 0; j < 10; j++) {
			count++;
			// try {Thread.sleep(500);} catch (InterruptedException e) {}
			pagesource = getPageSource(driver);
			if (temp[1].equals(pagesource[1])) {
				break;
			} else {
				temp = pagesource;
			}
		}
		if (count > 3)
			logger.info("OP waitForNewWindow: " + count);
		if (count > 8)
			oplog.logWarn("等待新窗口次数过多: " + count);
		return temp[0];
	}

	/**
	 * 简化页面元素
	 * 
	 * @param driver
	 *            驱动
	 * @return 0=详细页面,1=简化页面
	 */
	public static String[] getPageSource(IOSDriver<WebElement> driver) {
		String page = driver.getPageSource();
		return new String[] { page, page.replaceAll("\\d+", "") };
	}
}
