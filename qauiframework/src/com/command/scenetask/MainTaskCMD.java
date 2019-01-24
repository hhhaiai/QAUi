package com.command.scenetask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.HelperUtil;
import com.helper.IOSInfo;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;
import com.task.GetTaskFromNoteXML;
import com.task.TaskRunner;
import com.viewer.main.CheckPC;
import com.viewer.main.MainRun;

public class MainTaskCMD {
	Logger logger = LoggerFactory.getLogger(MainTaskCMD.class);
	String udid;
	String deviceOS;
	String mode = "normal";
	SceneLogUtil oplog;
	Map<Long, Map<String, Object>> tasksMap = new LinkedHashMap<>();

	/**
	 * 主入口
	 * 
	 * @param cmds
	 */
	public void start(String[] cmds) {
		// -udid xxx -os android -mode simple -mixemail_send xxxx
		// -scene xxx --apps xx --email_send xxxx -scene xxx --emailcc xxxx
		ArrayList<String> cmdlist = new ArrayList<>();
		for (String cmd : cmds) {
			// System.out.println(cmd);
			cmdlist.add(cmd);
		}
		Iterator<String> iterator = cmdlist.iterator();
		Map<String, Map<String, String>> cmdsMap = new LinkedHashMap<>();
		List<Map<String, Object>> notesceneList = null;// 从notexml得到的场景任务
		Map<String, String> mixNotificationMap = new HashMap<>();
		String taskname = "";
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (key.startsWith("-")) {
				String value = iterator.hasNext() ? iterator.next() : "";
				key = key.substring(1).toLowerCase();
				if (key.startsWith("-")) {
					if (!checkSecondaryParams(key, value))
						return;
				} else {
					if (!checkMainParams(key, value))
						return;
				}

				if (key.equals("scene")) {
					taskname = value + " " + TimeUtil.getNanoTime();
					Map<String, String> map = new HashMap<String, String>();
					map.put(Cparams.type, Cconfig.TASK_TYPE_SCENE);
					cmdsMap.put(taskname, map);
				} else if (key.equals("monkey_sys")) {
					taskname = value + " " + TimeUtil.getNanoTime();
					Map<String, String> map = new HashMap<String, String>();
					map.put(Cparams.type, Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
					cmdsMap.put(taskname, map);
				} else if (key.startsWith("-")) {// --副参数
					if (cmdsMap.get(taskname) != null)
						cmdsMap.get(taskname).put(key.substring(1), value);
				} else if (key.startsWith("mix")) {
					mixNotificationMap.put(key, value);
				} else if (key.equals("mode")) {
					mode = value;
				} else if (key.equals("notexml")) {
					GetTaskFromNoteXML getTaskFromNoteXML = new GetTaskFromNoteXML(udid, deviceOS, value);
					notesceneList = getTaskFromNoteXML.getSceneMapList();
				}
			} else {
				print("参数错误:" + key);
				return;
			}
		}
		if (notesceneList != null) {
			for (Map<String, Object> sceneMap : notesceneList) {
				sceneMap.put(Cparams.run, "CMD");
				tasksMap.put(TimeUtil.getNanoTime(), sceneMap);
			}
		} else {
			for (Entry<String, Map<String, String>> entry : cmdsMap.entrySet()) {
				if (entry.getValue().get(Cparams.type).equals(Cconfig.TASK_TYPE_SCENE)) {
					SceneVerify sceneVerify = new SceneVerify();
					Map<String, Object> sceneMap = sceneVerify.VerifyScene(udid, deviceOS, entry.getKey(),
							entry.getValue());
					if (sceneMap != null)
						tasksMap.put(TimeUtil.getNanoTime(), sceneMap);
				} else if (entry.getValue().get(Cparams.type).equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {
					SYSAndroidMonkeyVerify sysAndroidMonkeyVerify = new SYSAndroidMonkeyVerify();
					Map<String, Object> monkeyMap = sysAndroidMonkeyVerify.VerifyMonkey(udid, deviceOS, entry.getKey(),
							entry.getValue());
					if (monkeyMap != null)
						tasksMap.put(TimeUtil.getNanoTime(), monkeyMap);
				}
			}
		}
		oplog = new SceneLogUtil(udid, null);
		if (tasksMap.size() == 0) {
			oplog.logError("未检测到需要执行的任务,请检查!");
			return;
		}
		// 运行场景
		oplog.setPrint(mode.equals("simple") ? false : true);
		oplog.logTask("开始执行计划任务,共" + tasksMap.size() + "个...");
		for (Entry<Long, Map<String, Object>> entry : tasksMap.entrySet()) {
			if (entry.getValue().get(Cparams.type).equals(Cconfig.TASK_TYPE_SCENE)) {
				oplog.logTask("任务名-场景:" + entry.getValue().get(Cparams.name).toString());
			} else if (entry.getValue().get(Cparams.type).equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {
				oplog.logTask("任务名:" + Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
			}
		}
		TaskRunner taskRunner = new TaskRunner(oplog, udid, deviceOS);// 场景运行
		Map<String, String> taskConfigMap = taskRunner.getTaskConfigMap();
		setMixReportParams(mixNotificationMap, taskConfigMap);// 先改变值后初始化
		taskRunner.initRunner();
		for (Entry<Long, Map<String, Object>> entry : tasksMap.entrySet()) {
			taskRunner.runTask(entry.getValue());
		}
		taskRunner.endRunner();
	}

	/**
	 * 检查主参数是否正确
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean checkMainParams(String key, String value) {
		String tempvalue = value;
		switch (key) {
		case "help":
			showHelpInfo();
			return false;
		case Cparams.udid:
			List<String> list = AndroidInfo.getDevices();
			if (MainRun.settingsBean.getSystem() == Cconfig.MAC)
				IOSInfo.getDevices().forEach(str -> list.add(str));
			for (String str : list) {
				if (str.contains(value)) {
					this.udid = value;
					return true;
				}
			}
			print("设备udid:" + value + "无效");
			break;
		case "os":
			if (value.equals(Cconfig.ANDROID.toLowerCase())) {
				this.deviceOS = Cconfig.ANDROID;
				return true;
			} else if (value.equals(Cconfig.IOS.toLowerCase())) {
				this.deviceOS = Cconfig.IOS;
				return true;
			}
			print("设备os仅支持Android或iOS");
			break;
		case "scene":
			// verify中判断
			return true;
		case "monkey_sys":
			// verify中判断
			return true;
		case "mode":
			if (value.equals("simple") | value.equals("normal"))
				return true;
			break;
		case "notexml":
			File file = new File(value);
			if (file.exists() && file.isFile() && file.getName().endsWith(".xml"))
				return true;
			break;
		// taskConfigMap
		case "mixemail_send":
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case "mixemail_to":
			if (HelperUtil.checkEmail(value))
				return true;
			print("email格式错误");
			break;
		case "mixemail_cc":
			if (HelperUtil.checkEmail(value))
				return true;
			print("email格式错误");
			break;
		case "mixemail_subject":
			return true;
		case "mixemail_smtp":
			return true;
		case "mixemail_account":
			if (HelperUtil.checkEmail(value))
				return true;
			print("email格式错误");
			break;
		case "mixemail_password":
			return true;
		// wechat
		case "mixwechat_send":
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case "mixwechat_people_list":
			if (value.matches(Cconfig.REGEX_FORMAT))
				return true;
			break;
		default:
			break;
		}
		print("主参数错误:-" + key + " " + tempvalue + "");
		return false;
	}

	/**
	 * 检查副参数是否正确
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean checkSecondaryParams(String key, String value) {
		String tempvalue = value;
		if (key.startsWith("-"))
			key = key.substring(1);
		switch (key) {
		// android monkey sys
		case Cparams.monkey_sys_seed:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_intervaltime:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_customize:
			break;
		case Cparams.monkey_sys_runtime:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_touch:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_motion:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_trackball:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_nav:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_majornav:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_syskeys:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_appswitch:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_pct_anyevent:
			if (HelperUtil.isInteger(value))
				return true;
			break;
		case Cparams.monkey_sys_ignore_crashes:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.monkey_sys_ignore_timeouts:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.monkey_sys_ignore_security_exceptions:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.monkey_sys_ignore_native_crashes:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.monkey_sys_monitor_native_crashes:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		// 副参数
		case Cparams.capability:
			if (value.matches(Cconfig.REGEX_FORMAT))
				return true;
			break;
		case Cparams.appiumserverurl:
			if (CheckPC.checkAppiumServerUrl(value))
				return true;
			print("Appium服务器地址无效");
			break;
		case Cparams.params:
			if (value.matches(Cconfig.REGEX_FORMAT))
				return true;
			break;
		case Cparams.screenshot:
			if (deviceOS.equals(Cconfig.ANDROID)) {
				if (value.equals(Cconfig.SCREENSHOT_ADB) || value.equals(Cconfig.SCREENSHOT_APPIUM)
						|| value.equals(Cconfig.SCREENSHOT_DDMLIB) || value.equals(Cconfig.SCREENSHOT_NONE))
					return true;
			} else {
				if (value.equals(Cconfig.SCREENSHOT_APPIUM) || value.equals(Cconfig.SCREENSHOT_APPIUM)
						|| value.equals(Cconfig.SCREENSHOT_NONE))
					return true;
			}
		case Cparams.syscrash:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.appcrash:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.setdevice:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.userlogcatch:
			if (value.matches(Cconfig.REGEX_FORMAT))
				return true;
			break;
		case Cparams.initdriver:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.email_send:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		case Cparams.email_to:
			if (HelperUtil.checkEmail(value))
				return true;
			print("email格式错误");
			break;
		case Cparams.email_cc:
			if (HelperUtil.checkEmail(value))
				return true;
			print("email格式错误");
			break;
		case Cparams.email_account:
			if (HelperUtil.checkEmail(value))
				return true;
			print("email格式错误");
			break;
		case Cparams.email_smtp:
			return true;
		case Cparams.email_password:
			return true;
		case Cparams.apps:
			File file = new File(value.startsWith("#") ? value.substring(1) : value);
			if (file.exists() && file.isDirectory())
				return true;
			print("安装包地址错误");
			break;
		case Cparams.caseruninfo:
			if (value.matches("^([1-9][0-9]{0,},?)+$"))
				return true;
			break;
		case Cparams.note:
			return true;
		case Cparams.desc:
			return true;
		case Cparams.idevicesyslogtag:
			return true;
		// wechat
		case Cparams.wechat_people_list:
			if (value.matches(Cconfig.REGEX_FORMAT))
				return true;
			break;
		case Cparams.wechat_send:
			if (value.equals("true") || value.equals("false"))
				return true;
			break;
		default:
			break;
		}
		print("场景参数错误:-" + key + " " + tempvalue + "");
		return false;
	}

	/**
	 * 更新汇总报告参数
	 */
	private void setMixReportParams(Map<String, String> mixNotificationMap, Map<String, String> taskConfigMap) {
		if (mixNotificationMap.get("mixemail_subject") != null)
			taskConfigMap.put(Cparams.email_subject, (String) mixNotificationMap.get("mixemail_subject"));
		if (mixNotificationMap.get("mixemail_send") != null)
			taskConfigMap.put(Cparams.email_send, (String) mixNotificationMap.get("mixemail_send"));
		if (mixNotificationMap.get("mixemail_to") != null)
			taskConfigMap.put(Cparams.email_to, (String) mixNotificationMap.get("mixemail_to"));
		if (mixNotificationMap.get("mixemail_cc") != null)
			taskConfigMap.put(Cparams.email_cc, (String) mixNotificationMap.get("mixemail_cc"));
		if (mixNotificationMap.get("mixemail_smtp") != null)
			taskConfigMap.put(Cparams.email_smtp, (String) mixNotificationMap.get("mixemail_smtp"));
		if (mixNotificationMap.get("mixemail_account") != null)
			taskConfigMap.put(Cparams.email_account, (String) mixNotificationMap.get("mixemail_account"));
		if (mixNotificationMap.get("mixemail_password") != null)
			taskConfigMap.put(Cparams.email_password, (String) mixNotificationMap.get("mixemail_password"));
		if (mixNotificationMap.get("mixwechat_send") != null)
			taskConfigMap.put(Cparams.wechat_send, (String) mixNotificationMap.get("mixwechat_send"));
		if (mixNotificationMap.get("mixwechat_people_list") != null)
			taskConfigMap.put(Cparams.wechat_people_list, (String) mixNotificationMap.get("mixwechat_people_list"));
	}

	/**
	 * 显示帮助信息
	 */
	public void showHelpInfo() {
		StringBuffer helpBuf = new StringBuffer();
		helpBuf.append("\n*********欢迎使用QAUIFramework " + MainRun.Version + "*********\n");
		helpBuf.append("命令介绍:\n");
		helpBuf.append("-help 使用帮助\n");
		helpBuf.append("*********必选参数*********\n");
		helpBuf.append("-udid 设备识别码\n");
		helpBuf.append("-os Android或者iOS\n");
		helpBuf.append("-scene 场景名,比如com.test.android.渠道包\n");
		helpBuf.append("*********可选参数*********\n");
		helpBuf.append("-monkey_sys 应用包名(仅支持Android)\n");
		helpBuf.append("-notexml note.xml文件绝对路径(不能与-scene同时使用)\n");
		helpBuf.append("-mode 不打印步骤信息:simple\n");
		helpBuf.append("-mixemail_subject 汇总报告-邮件标题,备注信息\n");
		helpBuf.append("-mixemail_send 汇总报告-是否发送邮件true/false\n");
		helpBuf.append("-mixemail_to 汇总报告-收件人\n");
		helpBuf.append("-mixemail_cc 汇总报告-抄送人\n");
		helpBuf.append("-mixemail_smtp 汇总报告-邮箱SMTP服务器\n");
		helpBuf.append("-mixemail_account 汇总报告-发件人邮箱账号\n");
		helpBuf.append("-mixemail_password 汇总报告-发件人邮箱密码\n");
		helpBuf.append("-mixwechat_send 微信消息-是否发送true/false\n");
		helpBuf.append("-mixwechat_people_list 微信消息-人员列表,格式\"name=userid;name=userid;\"(必须加上双引号)\n");
		helpBuf.append("*********-monkey_sys可选配置参数*********\n");
		helpBuf.append("--seed 种子\n");
		helpBuf.append("--intervaltime xx毫秒,操作间隔时间\n");
		helpBuf.append("--runtime xx分钟,运行时间\n");
		helpBuf.append("--customize 自定义命令(必须加上双引号)\n");
		helpBuf.append("--ignore_crashes 忽略崩溃true/false\n");
		helpBuf.append("--ignore_timeouts 忽略超时true/false\n");
		helpBuf.append("--ignore_security_exceptions 忽略安全异常true/false\n");
		helpBuf.append("--ignore_native_crashes 忽略本地异常true/false\n");
		helpBuf.append("--monitor_native_crashes 跟踪本地异常true/false\n");
		helpBuf.append("--pct_touch 触摸事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_touch + "\n");
		helpBuf.append("--pct_motion 动作事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_motion + "\n");
		helpBuf.append("--pct_trackball 轨迹球事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_trackball + "\n");
		helpBuf.append("--pct_nav 基本导航事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_nav + "\n");
		helpBuf.append("--pct_majornav 主导航事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_majornav + "\n");
		helpBuf.append("--pct_syskeys 系统按键事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_syskeys + "\n");
		helpBuf.append("--pct_appswitch 应用启动事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_appswitch + "\n");
		helpBuf.append("--pct_anyevent 其他未提及事件,百分比,默认" + Cconfig.MONKEY_ANDROID_SYS_pct_anyevent + "\n");
		helpBuf.append("--email_send 是否发送邮件true/false\n");
		helpBuf.append("--email_to 收件人\n");
		helpBuf.append("--email_cc 抄送人\n");
		helpBuf.append("--email_smtp 邮箱SMTP服务器\n");
		helpBuf.append("--email_account 发件人邮箱账号\n");
		helpBuf.append("--email_password 发件人邮箱密码\n");
		helpBuf.append("--wechat_send 微信消息-是否发送true/false\n");
		helpBuf.append("--wechat_people_list 微信消息-人员列表,格式\"name=userid;name=userid;\"(必须加上双引号)\n");
		helpBuf.append("*********场景-scene可选配置参数*********\n");
		helpBuf.append("--capability 调整Appium capability参数,格式:\"param1=a;param2=b\"(必须加上双引号)\n");
		helpBuf.append("--params 设置参数,格式:\"param1=a;param2=b\"(必须加上双引号)\n");
		helpBuf.append("--appiumserverurl Appium服务地址\n");
		helpBuf.append("--apps 安装包文件夹或单个安装文件绝对路径,文件夹带#为倒序执行\n");
		helpBuf.append("--caseruninfo 执行用例序号,如101,102\n");
		helpBuf.append("--note 备注\n");
		helpBuf.append("--desc 描述\n");
		helpBuf.append("--screenshot Android参数" + Cconfig.SCREENSHOT_ADB + "/" + Cconfig.SCREENSHOT_APPIUM + "/"
				+ Cconfig.SCREENSHOT_DDMLIB + "/" + Cconfig.SCREENSHOT_NONE + ",iOS参数"
				+ Cconfig.SCREENSHOT_IDEVICESREENSHOT + "/" + Cconfig.SCREENSHOT_APPIUM + "/" + Cconfig.SCREENSHOT_NONE
				+ "\n");
		helpBuf.append("--syscrash 捕获系统异常true/false\n");
		helpBuf.append("--appcrash 捕获应用异常true/false\n");
		helpBuf.append("--setdeivce 自动设置设备参数(打开WIFI,自动获取时间时区,自动亮度,5分钟休眠,非Appium输入法)true/false\n");
		helpBuf.append("--userlogcatch 自定义日志捕获,格式:\"param1=a;param2=b\"(必须加上双引号)\n");
		helpBuf.append("--idevicesyslogtag iOS捕获应用异常标志\n");
		helpBuf.append("--initdriver 强制重置Appium会话true/false\n");
		helpBuf.append("--email_send 是否发送邮件true/false\n");
		helpBuf.append("--email_to 收件人\n");
		helpBuf.append("--email_cc 抄送人\n");
		helpBuf.append("--email_smtp 邮箱SMTP服务器\n");
		helpBuf.append("--email_account 发件人邮箱账号\n");
		helpBuf.append("--email_password 发件人邮箱密码\n");
		helpBuf.append("--wechat_send 微信消息-是否发送true/false\n");
		helpBuf.append("--wechat_people_list 微信消息-人员列表,格式\"name=userid;name=userid;\"(必须加上双引号)\n");
		helpBuf.append("\n*********欢迎反馈Bugs及建议.O(∩_∩)O*********\n");
		print(helpBuf.toString());
	}

	/**
	 * 打印日志
	 * 
	 * @param text
	 */
	public void print(String text) {
		System.out.println(TimeUtil.getTime4Log() + " [CHECK]:" + text);
	}
}
