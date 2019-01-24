package com.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.ADBUtil;
import com.helper.AndroidInfo;
import com.helper.IOSInfo;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;
import com.notification.MailUtil;
import com.notification.WeChatMessage;
import com.notification.WeChatUtil;
import com.report.MixReport;
import com.viewer.main.MainRun;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class TaskRunner {
	Logger logger = LoggerFactory.getLogger(TaskRunner.class);
	SceneLogUtil oplog;
	String udid;
	String deviceOS;
	String previous_task;
	AndroidDriver<WebElement> androidDriver;
	IOSDriver<WebElement> iosDriver;
	MixReport mixReport;
	File catalog;
	int taskcount = 0;
	Map<String, String> taskConfigMap;

	SceneTask sceneTask;
	MonkeyTask monkeyTask;

	public TaskRunner(SceneLogUtil oplog, String udid, String deviceOS) {
		this.oplog = oplog;
		this.deviceOS = deviceOS;
		this.udid = udid;
		initTaskConfigMap();
		// adb shell 初始化
		if (deviceOS.equals(Cconfig.ANDROID)) {
			AndroidInfo.setPSversion(udid);
			AndroidInfo.setPMversion(udid);
			AndroidInfo.setTOPversion(udid);
		}
	}

	/**
	 * 初始化
	 */
	public void initRunner() {
		initCatalog();
		initMixReport();
		sceneTask = new SceneTask(oplog, udid, deviceOS, catalog, mixReport);
		monkeyTask = new MonkeyTask(oplog, udid, deviceOS, catalog, mixReport);
		WeChatUtil.getInstance().timingAccess_token();// 初始化wechat token
	}

	/**
	 * 运行结束
	 */
	public void endRunner() {
		// 还原参数
		taskcount = 0;
		quit(false);

		endMixReport();
		oplog.logTask("测试结果统计[TAG]" + mixReport.getResult());
		oplog.logTask("测试结果汇总生成完成.");
		sendEmail();
		sendWechatMessage();
		sceneTask.quitDriver();// 结束driver
		oplog.logTask("所有计划任务执行完成,请查看结果报告.");
		oplog.logTask("测试报告存储[TAG]" + catalog.getAbsolutePath());
		if (deviceOS.equals(Cconfig.ANDROID)) {
			oplog.logTask("运行完成,关闭手机屏幕");
			ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", CAndroidCMD.KEYCODE_POWER));
		}
	}

	/**
	 * 设置报告目录并置空场景数量
	 * 
	 * @return
	 */
	private void initCatalog() {
		taskcount = 0;
		catalog = new File(
				MainRun.settingsBean.getUiReportPath() + "/" + deviceOS + "/" + TimeUtil.getTime4File() + "-" + udid);
		if (!catalog.exists())
			catalog.mkdirs();
	}

	/**
	 * 运行单个任务
	 * 
	 * @param sceneMap
	 */
	public void runTask(Map<String, Object> taskMap) {
		if (taskMap.get(Cparams.type).equals(Cconfig.TASK_TYPE_SCENE)) {// 场景
			taskcount = sceneTask.runTask(taskMap, taskcount);
			previous_task = Cconfig.TASK_TYPE_SCENE;
		} else if (taskMap.get(Cparams.type).equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {// monkey
			if (previous_task != null && previous_task.equals(Cconfig.TASK_TYPE_SCENE)) {
				sceneTask.quitDriver();// 如果上一个任务是场景,则先结束掉appium driver
			}
			taskcount = monkeyTask.runTask(taskMap, taskcount);
			previous_task = Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS;
		}
	}

	/**
	 * 得到报告目录
	 * 
	 * @return
	 */
	public File getCatalog() {
		return catalog;
	}

	/**
	 * 得到任务配置
	 * 
	 * @return
	 */
	public Map<String, String> getTaskConfigMap() {
		return taskConfigMap;
	}

	/**
	 * 强制停止标志
	 * 
	 * @param forcestop
	 */
	public void quit(boolean stop) {
		sceneTask.setForceStop(stop);
		monkeyTask.setForceStop(stop);
		if (stop) {
			sceneTask.quitDriver();
			if (deviceOS.equals(Cconfig.ANDROID)) {
				monkeyTask.quitSysMonkey();
			}
		}
	}

	/**
	 * 初始化汇总报告
	 * 
	 * @return
	 */
	private void initMixReport() {
		mixReport = new MixReport();
		mixReport.start(catalog, taskConfigMap.get(Cparams.email_subject));
	}

	/**
	 * 结束汇总报告
	 */
	public void endMixReport() {
		mixReport.changeTile(taskConfigMap.get(Cparams.email_subject));
		mixReport.end();
	}

	/**
	 * 获取任务配置初始数据
	 */
	private void initTaskConfigMap() {
		taskConfigMap = new HashMap<>();
		Map<String, String> emailMap;
		Map<String, String> wechatMap;
		if (deviceOS.equals(Cconfig.ANDROID)) {
			emailMap = MainRun.androidConfigBean.getEmail();
			wechatMap = MainRun.androidConfigBean.getWechat();
		} else {
			emailMap = MainRun.iosConfigBean.getEmail();
			wechatMap = MainRun.iosConfigBean.getWechat();
		}
		taskConfigMap.put(Cparams.email_send, emailMap.get(Cparams.send));
		taskConfigMap.put(Cparams.email_to, emailMap.get(Cparams.to));
		taskConfigMap.put(Cparams.email_cc, emailMap.get(Cparams.cc));
		taskConfigMap.put(Cparams.email_smtp, emailMap.get(Cparams.smtp));
		taskConfigMap.put(Cparams.email_account, emailMap.get(Cparams.account));
		taskConfigMap.put(Cparams.email_password, emailMap.get(Cparams.password));
		taskConfigMap.put(Cparams.email_subject, "测试结果汇总");
		// wechat
		taskConfigMap.put(Cparams.wechat_send, wechatMap.get(Cparams.send));
		taskConfigMap.put(Cparams.wechat_people_list, wechatMap.get(Cparams.people_list));
	}

	/**
	 * 发送邮件
	 */
	public void sendEmail() {
		if (taskConfigMap.get(Cparams.email_send).equals("true")) {
			// ui config
			String to = taskConfigMap.get(Cparams.email_to);
			String copyto = taskConfigMap.get(Cparams.email_cc);
			// xml config
			String subject = TimeUtil.getTime(TimeUtil.getTime()) + " " + taskConfigMap.get(Cparams.email_subject)
					+ ",用时=" + mixReport.getUsetime();
			String smtp = taskConfigMap.get(Cparams.email_smtp).toString();
			String from = taskConfigMap.get(Cparams.email_account).toString();
			String username = taskConfigMap.get(Cparams.email_account).toString();
			String password = taskConfigMap.get(Cparams.email_password).toString();
			String htmlreport = null;
			MailUtil mailUtil = new MailUtil(smtp, oplog);
			if (MainRun.sysConfigBean.getQAreporter_url().trim().equals("")) {
				htmlreport = mixReport.getReportFile().getAbsolutePath();
			}
			if (mailUtil.SendMails(smtp, from, to, copyto, subject, mixReport.getEmailBuf().toString(), username,
					password, htmlreport)) {
				oplog.logTask("汇总报告邮件发送成功.");
			} else {
				oplog.logTask("汇总报告邮件发送失败.");
			}
		} else {
			oplog.logTask("不发送汇总报告邮件.");
		}
	}

	/**
	 * 发送汇总报告微信消息
	 */
	public void sendWechatMessage() {
		if (taskConfigMap.get(Cparams.wechat_send).equals("true")) {
			WeChatMessage weChatMessage = new WeChatMessage(oplog, taskConfigMap.get(Cparams.wechat_people_list));
			if (deviceOS.equals(Cconfig.ANDROID)) {
				weChatMessage.msmMixSceneResult(AndroidInfo.getModel(udid), taskConfigMap.get(Cparams.email_subject),
						mixReport.getResult(), mixReport.getUsetime(), mixReport.getReportFile());
			} else {
				weChatMessage.msmMixSceneResult(IOSInfo.getProduct(udid), taskConfigMap.get(Cparams.email_subject),
						mixReport.getResult(), mixReport.getUsetime(), mixReport.getReportFile());
			}
			oplog.logTask("发送汇总报告微信消息完成.");
		} else {
			oplog.logTask("不发送汇总报告微信消息");
		}
	}
}
