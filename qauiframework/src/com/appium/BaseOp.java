package com.appium;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;

/**
 * 操作封装
 *
 */
public abstract class BaseOp implements Operation {
	Logger logger = LoggerFactory.getLogger(BaseOp.class);

	protected String udid;
	protected UiWatcher uiWatcher = null;
	protected boolean UiWatcherFlag = true;// 是否执行UiWatcher标志
	protected boolean tempUiWatcherFlag = true;// 临时停止UiWatcher标志
	protected boolean ForcestopFlag = false;// 强制停止标志
	protected boolean AutoMultipleFlag = true;// 是否自适应坐标标志
	protected SceneLogUtil oplog;
	Map<String, String> capabilityMap;
	protected Translation translation;
	protected double resolution_width_multiple = 1D;
	protected double resolution_hight_multiple = 1D;
	Map<String, Object> elementMap = new HashMap<>();

	public BaseOp(Map<String, String> capabilityMap, File reportFolder, SceneLogUtil oplog, Object baseShot,
			Translation translation) {
		// TODO Auto-generated constructor stub
		this.capabilityMap = capabilityMap;
		this.udid = capabilityMap.get(Cparams.udid);
		this.oplog = oplog;
		this.translation = translation;
		elementMap.put("Shot", baseShot);
		elementMap.put("SceneLogUtil", oplog);
		elementMap.put("Translation", translation);
		elementMap.put("resolution_width_multiple", resolution_width_multiple);
		elementMap.put("resolution_hight_multiple", resolution_hight_multiple);
		elementMap.put("udid", this.udid);
	}

	@Override
	public void registerUiWatcher(String name, UiWatcher uiWatcher) {
		this.uiWatcher = uiWatcher;
		UiWatcherFlag = true;
		oplog.logInfo("注册观察者:" + name);
	}

	@Override
	public boolean runUiWatcher() {
		oplog.logInfo("UiWatcher:开始处理异常...");
		UiWatcherFlag = false;
		boolean status = uiWatcher.checkForCondition();
		UiWatcherFlag = true;
		return status;
	}

	@Override
	public void delUiWatcher() {
		this.UiWatcherFlag = false;
		this.uiWatcher = null;
	}

	/**
	 * 得到UiWatcherFlag,当为true的时候才执行UiWatcher
	 * 
	 * @return
	 */
	protected boolean getUiWatcherFlag() {
		return UiWatcherFlag;
	}

	/**
	 * 设置UiWatcherFlag,当为true的时候才执行UiWatcher
	 * 
	 * @param uiWatcherFlag
	 */
	protected void setUiWatcherFlag(boolean uiWatcherFlag) {
		UiWatcherFlag = uiWatcherFlag;
	}

	/**
	 * 获取元素通用组件
	 * 
	 * @return
	 */
	protected Map<String, Object> getElementMap() {
		return elementMap;
	}

	/**
	 * 得到x倍数后的x坐标
	 * 
	 * @param x
	 * @return
	 */
	protected abstract int X_multiple(int x);

	/**
	 * 得到y倍数后的y坐标
	 * 
	 * @param y
	 * @return
	 */
	protected abstract int Y_multiple(int y);

	/**
	 * 得到X坐标倍数
	 * 
	 * @return
	 */
	protected double getResolution_width_multiple() {
		return resolution_width_multiple;
	}

	/**
	 * 得到Y坐标倍数
	 * 
	 * @return
	 */
	protected double getResolution_hight_multiple() {
		return resolution_hight_multiple;
	}

	/**
	 * 等待新窗口(原理: 界面元素不再变化)
	 * 
	 * @return
	 */
	public abstract <T> T waitForNewWindow();

	/**
	 * 系统休眠.最大时长为设置的capabilityMap中的newCommandTimeout时间减去30秒.
	 * 
	 * @param millisecond 毫秒
	 */
	public void sysSleep(long millisecond) {
		try {
			int maxtime = Integer.parseInt(capabilityMap.get("newCommandTimeout"));
			if (millisecond > (maxtime - 30) * 1000) {
				millisecond = (maxtime - 30) * 1000;
				oplog.logWarn("默认最大线程休眠时长" + (maxtime - 30) + "秒");
			}
			if (millisecond > 0) {
				oplog.logInfo("线程休眠" + millisecond + "毫秒");
				Thread.sleep(millisecond);
			} else {
				oplog.logError("休眠时间可能会导致与Appium服务器断开连接,请重新设置!");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}

	/**
	 * 少于UNTILTIME毫秒,则等待直到等到UNTILTIME毫秒
	 * 
	 * @param stime
	 * @param etime
	 */
	protected void waittime(long stime) {
		long dif = TimeUtil.getTime() - stime;
		if (dif < Cconfig.UNTILTIME) {
			sysSleep(Cconfig.UNTILTIME - dif);
		}
	}
}
