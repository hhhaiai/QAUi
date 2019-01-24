package com.task;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.log.SceneLogUtil;
import com.report.MixReport;
import com.viewer.android.monkeytask.SYSAndroidMonkey;

public class MonkeyTask {
	Logger logger = LoggerFactory.getLogger(MonkeyTask.class);
	SceneLogUtil oplog;
	String udid;
	String deviceOS;
	MixReport mixReport;
	File catalog;

	boolean force_stop = false;

	public MonkeyTask(SceneLogUtil oplog, String udid, String deviceOS, File catalog, MixReport mixReport) {
		// TODO Auto-generated constructor stub
		this.oplog = oplog;
		this.deviceOS = deviceOS;
		this.udid = udid;
		this.mixReport = mixReport;
		this.catalog = catalog;
	}

	/**
	 * 运行单个场景
	 * 
	 * @param sceneMap
	 * @return 场景序号
	 */
	public int runTask(Map<String, Object> monkeyMap, int taskcount) {
		if (force_stop) {
			oplog.logTask(monkeyMap.get(Cparams.type) + "(" + monkeyMap.get(Cparams.monkey_sys_seed) + ")未运行,因已经强制停止!");
			return taskcount;
		}
		taskcount++;
		monkeyMap.put(Cparams.taskcount, taskcount);
		if (monkeyMap.get(Cparams.type).equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {
			oplog.logTask("开始执行Android SYS Monkey...");
			SYSAndroidMonkey sysAndroidMonkey = new SYSAndroidMonkey(monkeyMap, oplog, catalog, mixReport);
			sysAndroidMonkey.start();
			sysAndroidMonkey.end();
			oplog.logTask("执行Android SYS Monkey结束...");
		} else {

		}

		return taskcount;
	}

	/**
	 * 强制停止标志
	 * 
	 * @param forcestop
	 */
	public void setForceStop(boolean stop) {
		this.force_stop = stop;
	}

	/**
	 * 停止系统monkey
	 */
	public void quitSysMonkey() {
		if (AndroidInfo.checkIsAlive(udid, Cconfig.MONKEY_ANDROID_SYS_PACKAGE_NAME)) {
			if (AndroidInfo.stopApp(udid, Cconfig.MONKEY_ANDROID_SYS_PACKAGE_NAME)) {// stopApp不起作用
				logger.info("kill android sys monkey by shell stop ");
				oplog.logInfo("停止Monkey成功");
			} else {
				logger.info("kill android sys monkey by shell stop ");
				oplog.logError("停止Monkey失败");
			}
		} else {
			logger.info("android sys monkey has been stopped");
		}

	}

}
