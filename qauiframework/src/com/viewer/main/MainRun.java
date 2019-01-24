package com.viewer.main;

import java.awt.Font;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.AndroidConfigBean;
import com.bean.IOSConfigBean;
import com.bean.SettingsBean;
import com.bean.SysConfigBean;
import com.command.scenetask.MainTaskCMD;
import com.config.AndroidXmlParse;
import com.config.IOSXmlParse;
import com.config.SysXmlParse;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.helper.AdbBridge;
import com.helper.CMDUtil;
import com.helper.HelperUtil;
import com.log.Log4j2Util;
import com.task.ReadScene;

public class MainRun {
	public final static String Version = "V2.1031.1";
	public final static String AppiumServerVersion = "1.9.1";
	public final static String JavaClientVersion = "6.1.0";
	public static MainUI mainUI;
	public static SettingsBean settingsBean;
	public static AndroidConfigBean androidConfigBean;
	public static AndroidXmlParse androidXmlParse;
	public static SysXmlParse sysXmlParse;
	public static SysConfigBean sysConfigBean;
	public static IOSXmlParse iosXmlParse;
	public static IOSConfigBean iosConfigBean;
	public static AdbBridge adbBridge;
	public static int count = 0;

	public static void Run(String[] args) {
		// TODO Auto-generated method stub
		boolean runcmd = false;
		if (args.length > 0)
			runcmd = true;
		// 初始化log4j2
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
		String timekey = sdf.format(new Date());

		Log4j2Util log4j2Util = new Log4j2Util();
		if (!log4j2Util.InitLog4j2(timekey, runcmd)) {
			System.out.println("Init Logger System fail, pls retry!");
			JOptionPane.showMessageDialog(null, "Log4j2初始化失败,请重试...", "消息", JOptionPane.ERROR_MESSAGE, null);
			return;
		}
		Logger logger = LoggerFactory.getLogger(MainRun.class);
		logger.info("QA Ui Framework " + Version + "");
		// 清理log4j2日志
		logger.info("clear log4j files: "
				+ HelperUtil.clearFiles(new File(System.getProperty("user.dir") + "/Logs"), "Framework", 30));
		logger.info("clear runlog files: "
				+ HelperUtil.clearFiles(new File(System.getProperty("user.dir") + "/Logs"), "Runlog", 30));
		// 判断是否路径是否存在空格或者中文
		if (System.getProperty("user.dir").contains(" ") || HelperUtil.hasChinese(System.getProperty("user.dir"))) {
			JOptionPane.showMessageDialog(null, "请将本工具文件夹放在没有空格或中文的路径中使用...", "消息", JOptionPane.ERROR_MESSAGE, null);
			System.exit(0);
			return;
		}
		// 参数设置
		settingsBean = new SettingsBean();
		settingsBean.setExtraBinlocation(System.getProperty("user.dir") + "/extraBin");
		settingsBean.setDatalocation(System.getProperty("user.dir") + "/Data");
		settingsBean.setUiReportPath(
				FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/QAUiReport");
		// 初始化adb
		adbBridge = new AdbBridge();
		if (!adbBridge.initialize())
			adbBridge.initialize();

		// 判断系统类型
		String OSname = System.getProperty("os.name");
		if (OSname.toLowerCase().indexOf("windows") > -1) {
			settingsBean.setSystem(Cconfig.WINDOWS);
			if (!runcmd)
				InitGlobalFont(new Font("微软雅黑", Font.PLAIN, 12));
		} else if (OSname.toLowerCase().indexOf("mac") > -1) {
			settingsBean.setSystem(Cconfig.MAC);
			settingsBean.setUiReportPath(
					FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/Desktop/QAUiReport");
		} else {
			settingsBean.setSystem(Cconfig.LINUX);
			// settingsBean.setLogfile(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"/Desktop/QALogs");
		}
		logger.info("System type=" + OSname);

		// config.xml获取
		androidXmlParse = new AndroidXmlParse();
		sysXmlParse = new SysXmlParse();
		iosXmlParse = new IOSXmlParse();
		androidConfigBean = androidXmlParse.getConfigBean();
		sysConfigBean = sysXmlParse.getConfigBean();
		iosConfigBean = iosXmlParse.getConfigBean();

		if (!sysConfigBean.getReportPath().equals("")) {// 设置报告保存路径
			settingsBean.setUiReportPath(sysConfigBean.getReportPath() + "/QAUiReport");
		}
		sysXmlParse.writeVerQAUiFramework(Version);// 写入当前版本号
		logger.info("日记保存路径为" + settingsBean.getUiReportPath());
		// 读取scene xml
		ReadScene readScene = new ReadScene();
		MainRun.androidConfigBean.setScene(readScene.getAndroidSceneMap());
		MainRun.iosConfigBean.setScene(readScene.getIOSSceneMap());
		// 初始化ADB
		if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
			// CMDUtil.execcmd(MainRun.settingsBean.getExtraBinlocation()+"/adb.exe
			// devices",CCmd.SYSCMD,false);
		} else {
			// MAC 需要ADB5037端口激活
			CMDUtil.execcmd("chmod 777 " + MainRun.settingsBean.getExtraBinlocation() + "/aapt", CAndroidCMD.SYSCMD,
					true); // 复制后的aapt没有执行权限
			// CMDUtil.execcmd("chmod 777
			// "+MainRun.settingsBean.getExtraBinlocation()+"/adb",CCmd.SYSCMD,true);
			// //复制后的adb没有执行权限
			CMDUtil.execcmd("chmod 777 " + MainRun.settingsBean.getExtraBinlocation() + "/ffmpeg/ffmpeg",
					CAndroidCMD.SYSCMD, true); // 复制后的ffmpeg没有执行权限
			CMDUtil.execcmd(MainRun.sysConfigBean.getAndroidSDK_adb() + " devices", CAndroidCMD.SYSCMD, true);
		}
		if (runcmd) {
			// 命令行启动
			logger.info("start with CMD");
			try {
				MainTaskCMD mainTaskCMD = new MainTaskCMD();
				mainTaskCMD.start(args);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception", e);
			} finally {
				System.gc();
				System.exit(0);
			}
			return;
		} else {
			// GUI启动
			logger.info("start with GUI");
			mainUI = new MainUI();
			mainUI.setVisible(true);
			logger.info("show with GUI end");
		}
		// test
	}

	/**
	 * 变更全局字体
	 * 
	 * @param font
	 */
	private static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}
}
