package com.task;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appium.AndroidAssistant;
import com.appium.AndroidCheck;
import com.appium.AndroidOp;
import com.appium.ClassAssert;
import com.appium.ClassLog;
import com.appium.ClassParams;
import com.appium.Translation;
import com.bean.TestCaseBean;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.HelperUtil;
import com.helper.TimeUtil;
import com.log.CusLogCapture;
import com.log.LogcatMonitor;
import com.log.SceneLogUtil;
import com.notification.MailUtil;
import com.notification.WeChatMessage;
import com.report.AndroidSceneReport;
import com.report.MixReport;
import com.review.getscreen.AndroidRecord;
import com.review.getscreen.AndroidShot;
import com.review.replay.CreateReplay;
import com.viewer.main.MainRun;

import io.appium.java_client.android.AndroidDriver;

public abstract class BaseAndroidScene {
	Logger logger = LoggerFactory.getLogger(BaseAndroidScene.class);
	// 公开方法
	/**
	 * OP类(operation)主要包含各种查找元素/操作设备等方法
	 */
	protected AndroidOp OP;// 操作类
	/**
	 * 主要用于断言,如果断言失败,则会抛出异常ClassAssertException,该条用例测试结果失败
	 */
	protected ClassAssert ASSERT;// 断言类
	/**
	 * 主要用于日志打印
	 */
	protected ClassLog LOG;// 日志类
	/**
	 * 主要用于截图/录像/设备参数查询等辅助
	 */
	protected AndroidAssistant HELP;// 辅助类
	/**
	 * 主要用于检查页面元素等,检查的是当前页面的元素控件XML,判断该XML中有没有定义的值
	 */
	protected AndroidCheck CHECK;// 检查类
	/**
	 * 主要用于获取xml中params设定的参数
	 */
	protected ClassParams PARAMS;// 参数类
	// 内部方法
	protected AndroidDriver<WebElement> driver;
	private String udid;
	private SceneLogUtil oplog;
	private Map<String, String> capabilityMap;
	private Map<String, String> configMap;
	private Map<TestCaseBean, Boolean> caserunMap;
	// 私有方法
	private AndroidShot Shot;
	private LogcatMonitor logcatMonitor;
	private CusLogCapture cusLogCapture;
	// 报告
	private File reportFolder;
	private File reportFile;// HTML文件
	private AndroidSceneReport sceneReport = new AndroidSceneReport();
	private int PassCaseCount = 0;
	private int FailCaseCount = 0;
	private long startTime;// 开始时间
	private long endTime;// 结束时间
	private StringBuffer emailBuf;
	private MixReport mixReport;
	// 视频
	private AndroidRecord androidRecord;
	private CreateReplay createReplay;
	private int startpic = 1;

	private List<String> varsclass;// 接口列表
	private Translation translation;

	private WeChatMessage weChatMessage;// 微信消息发送
	private String scenecount;// 场景计数

	public BaseAndroidScene() {
		// TODO Auto-generated constructor stub

	}

	/**
	 * 初始化
	 * 
	 * @param driver
	 * @param capabilityMap
	 * @param configMap
	 * @param caserunMap
	 * @param oplog
	 * @param mixReport
	 * @param Catalog
	 */
	public void init(AndroidDriver<WebElement> driver, Map<String, String> capabilityMap, Map<String, String> configMap,
			Map<TestCaseBean, Boolean> caserunMap, SceneLogUtil oplog, MixReport mixReport, File Catalog) {
		this.driver = driver;
		this.capabilityMap = capabilityMap;
		this.configMap = configMap;
		this.caserunMap = caserunMap;
		this.oplog = oplog;
		this.mixReport = mixReport;
		this.udid = capabilityMap.get(Cparams.udid);
		oplog.clearCaseBuf();

		scenecount = configMap.get(Cparams.taskcount);
		File apkfile = new File(configMap.get(Cparams.app));
		if (apkfile.exists() && apkfile.getName().toLowerCase().endsWith(".apk")) {
			reportFolder = new File(Catalog.getAbsolutePath() + "/" + scenecount + "-" + this.getClass().getSimpleName()
					+ "-" + apkfile.getName());
			if (reportFolder.exists()) {
				reportFolder = new File(Catalog.getAbsolutePath() + "/" + scenecount + "-"
						+ this.getClass().getSimpleName() + "-" + apkfile.getName() + "-" + TimeUtil.getTime4File());
			}
		} else {
			reportFolder = new File(
					Catalog.getAbsolutePath() + "/" + scenecount + "-" + this.getClass().getSimpleName());
			if (reportFolder.exists())
				reportFolder = new File(Catalog.getAbsolutePath() + "/" + scenecount + "-"
						+ this.getClass().getSimpleName() + "-" + TimeUtil.getTime4File());
		}
		reportFolder.mkdirs();
		// if(this.driver==null){//强制重新初始化driver
		// oplog.logWarn("重新尝试初始化Appium driver...");
		// Driver d=new
		// Driver(configMap.get(Cparams.appiumserverurl),capabilityMap,Cconfig.ANDROID);
		// this.driver=(AndroidDriver<WebElement>) d.getAndroidDriver();
		// if(this.driver==null)oplog.logError("重新尝试初始化Appium driver失败...");
		// }
	}

	/**
	 * 设置udid
	 * 
	 * @param udid
	 */
	public void setUdid(String udid) {
		this.udid = udid;
	}

	/**
	 * 获取udid
	 * 
	 * @return
	 */
	public String getUdid() {
		return udid;
	}

	/**
	 * 设置接口列表
	 * 
	 * @param varsclass
	 */
	public void setVarsclass(List<String> varsclass) {
		this.varsclass = varsclass;
	}

	/**
	 * 获取oplog
	 * 
	 * @return
	 */
	public SceneLogUtil getOpLogUtil() {
		return oplog;
	}

	/**
	 * 得到appium driver
	 * 
	 * @return
	 */
	public AndroidDriver<WebElement> getDriver() {
		return driver;
	}

	/**
	 * 系统准备工作
	 */
	@SuppressWarnings("unused")
	private void BeforeRun() {
		startTime = new Date().getTime();

		oplog.logInfo("本场景测试报告存储在" + reportFolder.getAbsolutePath());
		createReplay = new CreateReplay(reportFolder);
		ASSERT = new ClassAssert(oplog);
		cusLogCapture = new CusLogCapture(udid, Cconfig.ANDROID, oplog, reportFolder);
		LOG = new ClassLog(oplog, cusLogCapture);
		PARAMS = new ClassParams(oplog, configMap.get(Cparams.params));
		translation = new Translation(varsclass);
		Shot = new AndroidShot(configMap.get(Cparams.screenshot), driver, udid, reportFolder);
		androidRecord = new AndroidRecord(udid, oplog, reportFolder, Shot.getDevice_width(), Shot.getDevice_hight());// 屏幕录制
		HELP = new AndroidAssistant(capabilityMap, oplog, Shot, androidRecord);
		CHECK = new AndroidCheck(driver, Shot, oplog, translation);
		logcatMonitor = new LogcatMonitor(udid, capabilityMap.get("appPackage"),
				configMap.get(Cparams.syscrash).equals("true") ? true : false,
				configMap.get(Cparams.appcrash).equals("true") ? true : false, configMap.get(Cparams.userlogcatch),
				oplog, reportFolder, Shot);
		OP = new AndroidOp(driver, capabilityMap, reportFolder, oplog, Shot, translation);
		// wechat
		weChatMessage = new WeChatMessage(oplog, configMap.get(Cparams.wechat_people_list));
		// 安装应用
		File appfile = new File(configMap.get(Cparams.app));//
		if (Cconfig.APPIUM_REPACLE_DRIVER) {
			if (appfile.exists() && appfile.isFile()) {
				OP.uninstallApp(capabilityMap.get("appPackage"));
				OP.installApp(appfile.getAbsolutePath(), false);
			} else {
				if (capabilityMap.get("noReset").equals("false")) {
					OP.clearApp();
				}
			}
			if (capabilityMap.get("autoLaunch") != null && capabilityMap.get("autoLaunch").equals("false")) {
				logger.info("autoLaunch is false,will not launch app!");
			} else {
				OP.launchApp();
			}
		} else {
			if (configMap.get(Cparams.initdriverdone).equals("true")) {// 初始化时appium安装APK

			} else {
				if (appfile.exists() && appfile.isFile()) {
					OP.uninstallApp(capabilityMap.get("appPackage"));
					OP.installApp(appfile.getAbsolutePath(), false);
				}
				if (capabilityMap.get("autoLaunch") != null && capabilityMap.get("autoLaunch").equals("false")) {
					logger.info("autoLaunch is false,will not launch app!");
				} else {
					OP.launchApp();
				}
			}
		}
		logcatMonitor.StartLogMonitor();
	}

	/**
	 * 系统收尾工作
	 */
	@SuppressWarnings("unused")
	private void AfterRun(List<String> failcaseList) {
		// 关闭
		cusLogCapture.captureStop();
		HELP.stopScreenRecord();
		OP.closeApp();
		oplog.logInfo("停止监控logcat日记");
		logcatMonitor.StopLogMonitor();

		endTime = new Date().getTime();
		CreateReport(failcaseList);

		sendEmail();

		// 公开方法
		OP = null;
		ASSERT = null;
		LOG = null;
		HELP = null;
		PARAMS = null;
		// 内部方法

		// 私有方法
		Shot = null;
		logcatMonitor = null;
		// 报告
		sceneReport = null;
		// 视频
		androidRecord = null;
		createReplay = null;
		// 强制清理内存
		// System.gc();
	}

	/**
	 * 生成报告
	 */
	private void CreateReport(List<String> failcaseList) {
		reportFile = sceneReport.start(reportFolder, this.getClass().getName());
		emailBuf = sceneReport.getEmailBuf();
		sceneReport.WriteSceneResult(PassCaseCount, FailCaseCount, startTime, endTime);
		Map<String, String> deviceinfoMap = AndroidInfo.getDeviceInfo(udid);
		// 设备时间与系统时间判定.
		deviceinfoMap.put("时间对比", "device:" + TimeUtil.getTime("yyyy-MM-dd HH:mm:ss.SSS", HELP.getDeviceTime()) + ",PC:"
				+ TimeUtil.getTime("yyyy-MM-dd HH:mm:ss.SSS"));
		sceneReport.WriteHorizontalInfo("设备信息", "deviceTable", deviceinfoMap, true);
		if (capabilityMap.get("app") != null && !capabilityMap.get("app").equals("")) {
			sceneReport.WriteVerticalInfo("权限列表", "permissionTable",
					AndroidInfo.getAppPermission(capabilityMap.get("app")), false);
			// sceneReport.WriteHorizontalInfo("应用包信息", "apkinfoTable",
			// AndroidInfo.getAPKInfo(capabilityMap.get("app")),
			// true);
		}
		sceneReport.WriteHorizontalInfo("应用包信息", "apkinfoTable",
				AndroidInfo.getPackageInfo(udid, capabilityMap.get("appPackage")), true);
		sceneReport.WriteVerticalInfo("场景参数配置", "sceneTable", getSceneConfigInfo(), true);
		sceneReport.WriteVerticalInfo("Appium参数配置:", "appiumTable", capabilityMap, false);
		sceneReport.end();
		oplog.logInfo("报告生成完成!");
		oplog.logInfo("[TASKDONE]场景-" + this.getClass().getSimpleName() + "执行完成,用例通过:" + PassCaseCount + "条,失败:"
				+ FailCaseCount + "条,用时:" + TimeUtil.getUseTime(startTime, endTime));
		// wechat 场景完成消息
		weChatMessage.msmSceneResult(AndroidInfo.getModel(udid), scenecount + "-" + this.getClass().getSimpleName(),
				"总用例" + (PassCaseCount + FailCaseCount) + "条,成功" + PassCaseCount + "条,失败" + FailCaseCount + "条",
				TimeUtil.getUseTime(startTime, endTime), reportFile);
		// mixReport
		mixReport.WriteDeviceInfo(deviceinfoMap);
		mixReport.WriteSceneResult(configMap.get(Cparams.taskcount), reportFolder,
				this.getClass().getSimpleName() + " " + new File(configMap.get("app")).getName(), PassCaseCount,
				FailCaseCount, startTime, endTime, sceneReport.getStepcount(), sceneReport.getWarncount(),
				sceneReport.getErrorcount());
		if (FailCaseCount > 0 && failcaseList.size() > 0)
			mixReport.WriteSceneFailNote(configMap, capabilityMap, failcaseList);
		if (FailCaseCount > 0)
			mixReport.WriteInfoBuf(sceneReport.getFailBuf());
	}

	/**
	 * 得到场景设置信息
	 * 
	 * @return
	 */
	private Map<String, String> getSceneConfigInfo() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("场景描述", configMap.get(Cparams.desc));
		StringBuffer caserunBuf = new StringBuffer();
		caserunMap.entrySet().forEach(e -> {
			if (e.getKey().getNo() >= 1 && e.getValue()) {
				caserunBuf.append(e.getKey().getNo() + ",");
			}
		});
		int invalid_case = 0;
		for (Entry<TestCaseBean, Boolean> entry : caserunMap.entrySet())
			if (entry.getKey().getNo() < 1)
				invalid_case++;
		if (caserunBuf.length() > 0) {
			map.put("执行用例",
					HelperUtil.getStringShowCount(caserunBuf.toString(), ",") == caserunMap.size() - invalid_case
							? "所有用例"
							: caserunBuf.toString().substring(0, caserunBuf.toString().length() - 1));
		} else {
			map.put("执行用例", "无");
		}
		map.put("Appium服务器地址", configMap.get(Cparams.appiumserverurl));
		map.put("强制初始化Session", configMap.get(Cparams.initdriver));
		map.put("截图方式", configMap.get(Cparams.screenshot));
		map.put("捕获系统异常", configMap.get(Cparams.syscrash));
		map.put("捕获应用异常", configMap.get(Cparams.appcrash));
		if (!configMap.get(Cparams.params).equals(""))
			map.put("参数", configMap.get(Cparams.params));
		if (!configMap.get(Cparams.note).equals(""))
			map.put("备注", configMap.get(Cparams.note));
		map.put("发送邮件", configMap.get(Cparams.email_send));
		if (configMap.get(Cparams.email_send).equals("true")) {
			map.put("收件人", configMap.get(Cparams.email_to));
			map.put("抄送", configMap.get(Cparams.email_cc));
		}
		map.put("启动方式", configMap.get(Cparams.run));
		if (!configMap.get(Cparams.apps).equals(""))
			map.put("安装包地址", configMap.get(Cparams.apps));

		map.put("微信提醒", configMap.get(Cparams.wechat_send));
		if (configMap.get(Cparams.wechat_send).equals("true")) {
			String people_list = configMap.get(Cparams.wechat_people_list);
			StringBuffer buffer = new StringBuffer();
			for (String people : people_list.split(";")) {
				if (people.contains("=")) {
					buffer.append(people.split("=")[0] + ";");
				}
			}
			map.put("通知人员", buffer.toString());
		}
		return map;
	}

	/**
	 * 创建视频
	 */
	@SuppressWarnings("unused")
	private String createCaseVideo() {
		String videopath = null;
		if (Shot.getPiccount() < startpic)
			return videopath;// 当前CASE未截图.
		videopath = createReplay.createVideo(startpic, Shot.getPiccount());
		startpic = Shot.getPiccount() + 1;
		return videopath;
	}

	/**
	 * 发送邮件
	 */
	private void sendEmail() {
		// Map<String, String> map=MainRun.androidConfigBean.getEmail();
		if (configMap.get(Cparams.email_send).equals("true")) {
			// ui config
			String to = configMap.get(Cparams.email_to);
			String copyto = configMap.get(Cparams.email_cc);
			// xml config
			String subject = TimeUtil.getTime4Log() + ",场景-" + scenecount + "-" + this.getClass().getSimpleName()
					+ ",通过:" + PassCaseCount + "条,失败:" + FailCaseCount + "条,用时:"
					+ TimeUtil.getUseTime(startTime, endTime);
			String smtp = configMap.get(Cparams.email_smtp).toString();
			String from = configMap.get(Cparams.email_account).toString();
			String username = configMap.get(Cparams.email_account).toString();
			String password = configMap.get(Cparams.email_password).toString();
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

	/**
	 * 所有测试用例之前执行的方法
	 */
	public abstract void setup();

	/**
	 * 所有测试用例之后执行的方法
	 */
	public abstract void teardown();

	/**
	 * 每条测试用例之前执行的方法
	 * 
	 * @param lastcaseresult 上调用例执行结果
	 */
	public abstract void beforeTest(boolean lastcaseresult);

	/**
	 * 每条测试用例之后执行的方法
	 * 
	 * @param thiscaseresult 本条用例执行结果
	 */
	public abstract void afterTest(boolean thiscaseresult);

	/**
	 * 得到配置参数MAP
	 * 
	 * @return
	 */
	public Map<String, String> getConfigMap() {
		return configMap;
	}

	/**
	 * 得到执行用例信息
	 * 
	 * @return
	 */
	public Map<TestCaseBean, Boolean> getCaserunMap() {
		return caserunMap;
	}

	/**
	 * 用例报告模块
	 */
	@SuppressWarnings("unused")
	private void addCaseReportLine(String methodname, String casename, String desc, boolean pass, String videopath,
			String usetime) {
		if (pass) {
			if (!casename.equals("setup") && !casename.equals("teardown"))
				PassCaseCount++;
		} else {
			if (!casename.equals("setup") && !casename.equals("teardown"))
				FailCaseCount++;
		}
		sceneReport.addCaseLine(casename, HelperUtil.linebreak2br(desc), pass,
				HelperUtil.linebreak2br(oplog.getResultBuf().toString()),
				HelperUtil.linebreak2br(oplog.getStepsbuf().toString()),
				HelperUtil.linebreak2br(oplog.getLogcatBuf().toString()),
				HelperUtil.linebreak2br(oplog.getCustomerBuf().toString()), videopath, usetime);
		if (!pass) {
			// wechat
			weChatMessage.msmFailCase(AndroidInfo.getModel(udid), scenecount + "-" + this.getClass().getSimpleName(),
					casename, oplog.getStepsbuf().toString(), usetime, videopath);
		}
		oplog.clearCaseBuf();
	}
}
