package com.appium;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.PicturesUtil;
import com.log.SceneLogUtil;

/**
 * 
 * 辅助类,如截图,图片对比等等
 *
 */

public abstract class BaseAssistant {
	Logger logger = LoggerFactory.getLogger(BaseAssistant.class);
	SceneLogUtil oplog;
	String udid;
	Map<String, String> capabilityMap;
	boolean ScreenShotSwitch = true;

	public BaseAssistant(Map<String, String> capabilityMap, SceneLogUtil oplog, Object baseShot) {
		// TODO Auto-generated constructor stub
		this.oplog = oplog;
		this.capabilityMap = capabilityMap;
	}

	/**
	 * 返回设备udid
	 * 
	 * @return
	 */
	public String getUDID() {
		return udid;
	}

	/**
	 * 返回Appium启动设置Capability
	 * 
	 * @return Map<String,String>
	 */
	public Map<String, String> getCapabilityMap() {
		return capabilityMap;
	}

	/**
	 * 全局自定义截图开关
	 * 
	 * @param ScreenShotSwitch
	 */
	public abstract void ScreenShotSwitch(boolean open);

	/**
	 * 截图
	 * 
	 * @param name 文件名称标记
	 * @param str  文字标记,null为不标记
	 * @return String 图片绝对路径
	 */
	public abstract String ScreenShotWithFlag(String name, String str);

	/**
	 * 截图
	 * 
	 * @param name    文件名称标记
	 * @param str     文字标记,null为不标记
	 * @param picpath 自定义图片保存路径的目录
	 * @return String 图片绝对路径
	 */
	public abstract String ScreenShotWithFlag(String name, String str, String picpath);

	/**
	 * 截图
	 * 
	 * @param name 文件名称标记
	 * @return
	 */
	public abstract String ScreenShot(String name);

	/**
	 * 截图
	 * 
	 * @param name    文件名称标记
	 * @param picpath 自定义图片保存路径
	 * @return
	 */
	public abstract String ScreenShot(String name, String picpath);

	/**
	 * 截图,并加入到测试报告视频中
	 * 
	 * @param name 文件名称标记
	 * @param text 文字标记,null为不标记
	 * @return
	 */
	public abstract String ReportVideoScreenShot(String name, String text);

	/**
	 * 得到设备的宽(iOS为point,Android为像素点)
	 * 
	 * @return
	 */
	public abstract int getDevice_width();

	/**
	 * 得到设备的高(iOS为point,Android为像素点)
	 * 
	 * @return
	 */
	public abstract int getDevice_hight();

	/**
	 * 屏幕分辨率放大倍数
	 * 
	 * @return
	 */
	public abstract int getZoom();

	/**
	 * 得到默认截图文件夹
	 * 
	 * @return
	 */
	public abstract File getDefalutScreenShotFolder();

	/**
	 * 裁剪图片
	 * 
	 * @param srcpath
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param savepath
	 * @return
	 */
	public boolean cutPicture(String srcpath, int x, int y, int width, int height, String savepath) {
		return PicturesUtil.cutPicture(srcpath, x, y, width, height, savepath);
	}
}
