package com.viewer.android.monkeytask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.TimeUtil;
import com.log.LogcatMonitor;
import com.log.SceneLogUtil;
import com.notification.MailUtil;
import com.notification.WeChatMessage;
import com.report.MixReport;
import com.report.MonkeyAndroidSYSReport;
import com.viewer.main.MainRun;

public class SYSAndroidMonkey {
	Logger logger = LoggerFactory.getLogger(SYSAndroidMonkey.class);
	File reportFolder;
	File reportFile;
	String udid;
	SceneLogUtil oplog;
	Map<String, Object> monkeyMap;
	FileOutputStream monkeylogStream = null;
	MonkeyAndroidSYSReport monkeyReport = new MonkeyAndroidSYSReport();
	StringBuffer emailBuf;
	Long startTime, endTime;
	int crashcount = 0;
	LogcatMonitor logcatMonitor;
	MixReport mixReport;
	boolean isstop = false;
	WeChatMessage weChatMessage;

	public SYSAndroidMonkey(Map<String, Object> monkeyMap, SceneLogUtil oplog, File catalog, MixReport mixReport) {
		this.monkeyMap = monkeyMap;
		this.oplog = oplog;
		this.mixReport = mixReport;
		udid = monkeyMap.get(Cparams.udid).toString();
		oplog.clearCaseBuf();
		reportFolder = new File(catalog.getAbsolutePath() + "/" + monkeyMap.get(Cparams.taskcount).toString() + "-"
				+ Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
		reportFolder.mkdirs();
		logcatMonitor = new LogcatMonitor(udid, monkeyMap.get(Cparams.monkey_sys_apppackagename).toString(), false,
				false, null, oplog, reportFolder, null);
		weChatMessage = new WeChatMessage(oplog, (String) monkeyMap.get(Cparams.wechat_people_list));
	}

	/**
	 * 生成报告
	 */
	private void CreateReport() {
		reportFile = monkeyReport.start(reportFolder, "Android系统Monkey");
		emailBuf = monkeyReport.getEmailBuf();
		monkeyReport.WriteMonkeyResult(crashcount, startTime, endTime);
		monkeyReport.WriteHorizontalInfo("设备信息", "deviceTable", AndroidInfo.getDeviceInfo(udid), true);
		monkeyReport.WriteVerticalInfo("Monkey配置", "monkeyTable", getMonkeyConfigInfo(), true);
		monkeyReport.end();
		oplog.logInfo("报告生成完成!");
		oplog.logInfo(
				"[TASKDONE]" + Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS + "-执行完成," + Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG
						+ "出现" + crashcount + "次,用时:" + TimeUtil.getUseTime(startTime, endTime));
		// wechat
		weChatMessage.msmMonkeyResult(AndroidInfo.getModel(udid), Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS,
				Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "出现" + crashcount + "次",
				TimeUtil.getUseTime(startTime, endTime), reportFile);

		mixReport.WriteDeviceInfo(AndroidInfo.getDeviceInfo(udid));
		mixReport.WriteMonkeyAndroidSYSResult(monkeyMap.get(Cparams.taskcount).toString(), reportFolder,
				Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS, crashcount, startTime, endTime);
		if (crashcount > 0)
			mixReport.WriteInfoBuf(monkeyReport.getFailBuf());
	}

	/**
	 * monkey运行信息
	 * 
	 * @return
	 */
	private Map<String, String> getMonkeyConfigInfo() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("应用包名", monkeyMap.get(Cparams.monkey_sys_apppackagename).toString());
		map.put("应用昵称", monkeyMap.get(Cparams.monkey_sys_appnickname).toString());
		map.put("执行时间", monkeyMap.get(Cparams.monkey_sys_runtime).toString() + "分钟");
		map.put("种子seed", monkeyMap.get(Cparams.monkey_sys_seed).toString());
		map.put("执行间隔", monkeyMap.get(Cparams.monkey_sys_intervaltime).toString() + "毫秒");
		map.put("Monkey命令", packageCommond());
		map.put("发送邮件", monkeyMap.get(Cparams.email_send).toString());
		if (monkeyMap.get(Cparams.email_send).equals("true")) {
			map.put("收件人", monkeyMap.get(Cparams.email_to).toString());
			map.put("抄送", monkeyMap.get(Cparams.email_cc).toString());
		}
		map.put("启动方式", monkeyMap.get(Cparams.run).toString());
		return map;
	}

	/**
	 * 开始执行
	 */
	public void start() {
		startTime = TimeUtil.getTime();
		for (Entry<String, String> entry : getMonkeyConfigInfo().entrySet()) {
			oplog.logInfo(entry.getKey() + ":" + entry.getValue());
		}
		if (!AndroidInfo.isAppInstall(udid, monkeyMap.get(Cparams.monkey_sys_apppackagename).toString())) {
			oplog.logError("未安装应用:" + monkeyMap.get(Cparams.monkey_sys_apppackagename).toString());
			return;
		}
		logcatMonitor.StartLogMonitor();
		runMonkey();
		endTime = TimeUtil.getTime();
	}

	/**
	 * 结束收尾
	 */
	public void end() {
		CreateReport();
		logcatMonitor.StopLogMonitor();
		sendEmail();
	}

	/**
	 * 设定时间后,使用命令停止Monkey
	 */
	private boolean stopMonkey() {
		if (AndroidInfo.stopApp(udid, Cconfig.MONKEY_ANDROID_SYS_PACKAGE_NAME)) {
			logger.info("kill android sys monkey by shell stop ");
			return true;
		} else {
			logger.info("kill android sys monkey by shell stop ");
			return false;
		}
	}

	/**
	 * 命令开始运行
	 */
	public void runMonkey() {
		if (MainRun.adbBridge.getDevice(udid) != null) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isstop = true;
				}
			}, Long.parseLong(monkeyMap.get(Cparams.monkey_sys_runtime).toString()) * 60 * 1000);
			logger.info("monkey run time="
					+ Long.parseLong(monkeyMap.get(Cparams.monkey_sys_runtime).toString()) * 60 * 1000);
			oplog.logInfo("开始执行Monkey...");
			try {
				monkeylogStream = new FileOutputStream(reportFolder.getAbsolutePath() + "/Monkey_log.txt", true);
				MainRun.adbBridge.getDevice(udid).executeShellCommand(packageCommond(), new MultiLineReceiver() {
					boolean crashflag = false;
					int preview = 20;
					StringBuffer crashlogBuf = new StringBuffer();
					String time;

					@Override
					public boolean isCancelled() {
						// TODO Auto-generated method stub
						return isstop;
					}

					@Override
					public void processNewLines(String[] lines) {
						// TODO Auto-generated method stub
						try {
							for (String line : lines) {
								monkeylogStream.write((TimeUtil.getTime4Log() + " " + line + "\n").getBytes("UTF-8"));
								if (line.contains(Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG)) {
									crashcount++;
									crashflag = true;
									time = TimeUtil.getTime_Millisecond(AndroidInfo.getDeviceTime(udid));
									oplog.logWarn("发现第" + crashcount + "个CRASH!");
									oplog.logInfo("当前设备系统时间:" + time);
									monkeylogStream.write(("发现第" + crashcount + "个CRASH!").getBytes("UTF-8"));
									monkeylogStream.write(("当前设备系统时间:" + time).getBytes("UTF-8"));
								}
								if (crashflag) {// 收集crash信息
									if (preview < crashlogBuf.toString().split("\n").length) {
										crashflag = false;
										monkeyReport.addMonkeyCrashLine(crashcount, time, TimeUtil.getTime4Log(),
												TimeUtil.getUseTime(startTime, TimeUtil.getTime()),
												crashlogBuf.toString());// 添加报告
										oplog.logError(crashlogBuf.toString());
										crashlogBuf.setLength(0);
									} else {
										crashlogBuf.append(line + "\n");
									}
								}
							}
							monkeylogStream.flush();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							logger.error("Exception", e);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Exception", e);
						}
					}

				}, 99999999, TimeUnit.SECONDS);// Long.parseLong(monkeyMap.get(Cparams.monkey_sys_runtime).toString())*60
				logger.info("run android sys monkey end. isstop=" + isstop);
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			} catch (AdbCommandRejectedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			} catch (ShellCommandUnresponsiveException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			} finally {
				if (monkeylogStream != null) {
					try {
						monkeylogStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception", e);
					}
				}
				if (timer != null) {
					timer.cancel();
				}
				logger.info("finally stop monkey...");
				if (stopMonkey()) {
					oplog.logInfo("Monkey已停止...");
				} else {
					oplog.logError("Monkey停止失败");
				}
			}
		} else {
			oplog.logError("can't find deivce:" + udid);
		}
	}

	/**
	 * 组装monkey命令
	 * 
	 * @return
	 */
	private String packageCommond() {
		String seed = monkeyMap.get(Cparams.monkey_sys_seed).toString();
		String intervaltime = monkeyMap.get(Cparams.monkey_sys_intervaltime).toString();
		String packages = monkeyMap.get(Cparams.monkey_sys_apppackagename).toString();
		String cmd = "";
		if (monkeyMap.get(Cparams.monkey_sys_runcustomize).equals("true")) {
			cmd = monkeyMap.get(Cparams.monkey_sys_customize).toString();
		} else {
			cmd = "monkey -s " + seed + " -p " + packages
					+ (monkeyMap.get(Cparams.monkey_sys_ignore_crashes).toString().equals("true") ? " --ignore-crashes"
							: " ")
					+ (monkeyMap.get(Cparams.monkey_sys_ignore_timeouts).toString().equals("true")
							? " --ignore-timeouts"
							: " ")
					+ (monkeyMap.get(Cparams.monkey_sys_ignore_security_exceptions).toString().equals("true")
							? " --ignore-security-exceptions"
							: " ")
					+ (monkeyMap.get(Cparams.monkey_sys_ignore_native_crashes).toString().equals("true")
							? " --ignore-native-crashes"
							: " ")
					+ (monkeyMap.get(Cparams.monkey_sys_monitor_native_crashes).toString().equals("true")
							? " --monitor-native-crashes"
							: " ")
					// 0-15 0表示事件类型,15表示默认百分比
					// 触摸事件。即在某一位置的Down-Up（手指的放下和抬起）事件。Down（ACTION_DOWN）和Up（ACTION_UP）的坐标临近，但并非相同。
					+ " --pct-touch " + monkeyMap.get(Cparams.monkey_sys_pct_touch).toString()
					// 1-10动作事件。以Down（ACTION_DOWN）开始，Up（ACTION_UP）结尾，中间至少有一次Move（ACTION_MOVE）
					+ " --pct-motion " + monkeyMap.get(Cparams.monkey_sys_pct_motion).toString()
					// 3-15
					// 轨迹球事件。即单纯的Move（ACTION_MOVE）
					+ " --pct-trackball " + monkeyMap.get(Cparams.monkey_sys_pct_trackball).toString()
					// 6-25基本导航事件。即来自于方向输入设备的上下左右操作。
					+ " --pct-nav " + monkeyMap.get(Cparams.monkey_sys_pct_nav).toString()
					// 7-15主导航事件。即Navigation,Bar的确认，菜单，返回键等。
					+ " --pct-majornav " + monkeyMap.get(Cparams.monkey_sys_pct_majornav).toString()
					// 8-2系统按键事件。即系统保留按键，如HOME键，BACK键，拨号键，挂断键，音量键等。
					+ " --pct-syskeys " + monkeyMap.get(Cparams.monkey_sys_pct_syskeys).toString()
					// 9-2 应用启动事件
					+ " --pct-appswitch " + monkeyMap.get(Cparams.monkey_sys_pct_appswitch).toString()
					// 11-13其他未提及事件。该事件可能包含其他上述事件。
					+ " --pct-anyevent " + monkeyMap.get(Cparams.monkey_sys_pct_anyevent).toString()
					+ " -v -v -v --throttle " + intervaltime + " 1200000000";
		}
		logger.info("Monkey命令:" + cmd);
		return cmd;
	}

	/**
	 * 发送邮件
	 */
	private void sendEmail() {
		// Map<String, String> map=MainRun.androidConfigBean.getEmail();
		if (monkeyMap.get(Cparams.email_send).equals("true")) {
			// ui config
			String to = monkeyMap.get(Cparams.email_to).toString();
			String copyto = monkeyMap.get(Cparams.email_cc).toString();
			// xml config
			String subject = TimeUtil.getTime4Log() + "," + Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS + ",崩溃" + crashcount
					+ "个,用时:" + TimeUtil.getUseTime(startTime, endTime);
			String smtp = monkeyMap.get(Cparams.email_smtp).toString();
			String from = monkeyMap.get(Cparams.email_account).toString();
			String username = monkeyMap.get(Cparams.email_account).toString();
			String password = monkeyMap.get(Cparams.email_password).toString();
			String htmlreport = null;
			MailUtil mailUtil = new MailUtil(smtp, oplog);
			if (MainRun.sysConfigBean.getQAreporter_url().equals("")) {
				htmlreport = reportFile.getAbsolutePath();
			}
			if (mailUtil.SendMails(smtp, from, to, copyto, subject, emailBuf.toString(), username, password,
					htmlreport)) {
				oplog.logInfo("报告邮件发送成功.");
			} else {
				oplog.logInfo("报告邮件发送失败.");
			}
		} else {
			oplog.logInfo("不发送报告邮件.");
		}
	}
}
