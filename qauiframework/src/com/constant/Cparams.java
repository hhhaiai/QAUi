package com.constant;

public interface Cparams {
	/**
	 * 通用参数
	 */
	String type = "type";
	String taskcount = "taskcount";
	String udid = "udid";
	int android_configcount = 30;// 26
	int ios_configcount = 30;// 26
	String tasks_config = "tasks_config";
	/**
	 * 微信通知参数
	 *
	 */
	String wechat_send = "wechat_send";
	String wechat_people_list = "wechat_people_list";
	// XML
	// String send="send";
	String people_list = "people_list";
	String corpid = "corpid";
	String corpsecret = "corpsecret";
	String agentid = "agentid";
	/**
	 * 邮件参数
	 */
	String email_send = "email_send";// 发送者
	String email_to = "email_to";// 接收者
	String email_cc = "email_cc";// 抄送者
	String email_subject = "email_subject";
	String email_smtp = "email_smtp";
	String email_account = "email_account";
	String email_password = "email_password";
	// XML
	String send = "send";
	String to = "to";
	String cc = "cc";
	String smtp = "smtp";
	String account = "account";
	String password = "password";
	/**
	 * 场景设置参数
	 */
	// XML
	String appid = "appid";
	String capability = "capability";
	String params = "params";
	String screenshot = "screenshot";
	String syscrash = "syscrash";
	String appcrash = "appcrash";
	String userlogcatch = "userlogcatch";
	String initdriver = "initdriver";
	String apps = "apps";
	String note = "note";
	String desc = "desc";
	String name = "name";
	String idevicesyslogtag = "idevicesyslogtag";
	String setdevice = "setdevice";
	String app = "app";// apk路径

	String caseruninfo = "caseruninfo";
	String appiumserverurl = "appiumserverurl";
	String run = "run";
	String initdriverdone = "initdriverdone";// 本次场景是否初始化过appium driver
	/**
	 * monkey设置参数
	 */
	// String monkey_type="monkey_type";
	// XML
	String monkey_sys_packages = "packages";
	String monkey_sys_analysis_show = "analysis_show";
	String monkey_sys_analysis_arow = "analysis_arow";
	String monkey_sys_analysis_arowword = "analysis_arowword";
	String monkey_sys_analysis_showduplicate = "analysis_showduplicate";
	String monkey_sys_isreconnect = "isreconnect";// NA
	String monkey_sys_customize = "customize";
	String monkey_sys_pct_touch = "pct_touch";
	String monkey_sys_pct_motion = "pct_motion";
	String monkey_sys_pct_trackball = "pct_trackball";
	String monkey_sys_pct_nav = "pct_nav";
	String monkey_sys_pct_majornav = "pct_majornav";
	String monkey_sys_pct_syskeys = "pct_syskeys";
	String monkey_sys_pct_appswitch = "pct_appswitch";
	String monkey_sys_pct_anyevent = "pct_anyevent";
	String monkey_sys_ignore_crashes = "ignore_crashes";
	String monkey_sys_ignore_timeouts = "ignore_timeouts";
	String monkey_sys_ignore_security_exceptions = "ignore_security_exceptions";
	String monkey_sys_ignore_native_crashes = "ignore_native_crashes";
	String monkey_sys_monitor_native_crashes = "monitor_native_crashes";
	// String monkey_sys_runcmd="sys_runcmd";
	String monkey_sys_runtime = "runtime";
	String monkey_sys_intervaltime = "intervaltime";
	String monkey_sys_seed = "seed";
	String monkey_sys_apppackagename = "apppackage";
	String monkey_sys_appnickname = "appnickname";
	String monkey_sys_runcustomize = "runcustomize";

}
