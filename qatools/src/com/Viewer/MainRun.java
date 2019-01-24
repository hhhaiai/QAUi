package com.Viewer;

import java.awt.Font;
import java.awt.Image;
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

import com.More.CheckPC;
import com.More.SysConfigBoxUI;
import com.Util.AdbBridge;
import com.Util.CheckUE;
import com.Util.HelperUtil;
import com.Util.Log4j2Util;
import com.Util.ParamsBean;
import com.Util.ParamsUtil;
import com.Util.XMLOperationUtil;

public class MainRun {
	public static String Version = "V2.1115";
	public static Image imagelogo;
	public static XMLOperationUtil xmlOperationUtil;
	public static ParamsBean paramsBean;
	public static String selectedID = null;
	public static String selectedOS = "";
	public static MainUI mainFrame;
	public static AdbBridge adbBridge;
	public static String extraBinlocation = System.getProperty("user.dir") + "/extraBin";
	public static String datalocation = System.getProperty("user.dir") + "/Data";
	public static String QALogfile = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()
			+ "/QAToolsLogs";
	public static int OStype = 0;// 0=win 1=mac 2=linux

	public static void main(String[] args) {
		// 初始化log4j2
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
		String timekey = sdf.format(new Date());

		Log4j2Util log4j2Util = new Log4j2Util();
		if (!log4j2Util.InitLog4j2(timekey)) {
			System.out.println("Init Logger System fail, pls retry!");
			return;
		}
		Logger logger = LoggerFactory.getLogger(MainRun.class);
		logger.info("QA Tools " + Version + ", design by Then.");
		// Test
		// Test.run();
		// return;
		// 判断是否路径是否存在空格或者中文
		if (System.getProperty("user.dir").contains(" ") || HelperUtil.hasChinese(System.getProperty("user.dir"))) {
			JOptionPane.showMessageDialog(null, "请将本工具文件夹放在没有空格或中文的路径中使用...", "消息", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			return;
		}
		// 判断系统类型
		String OSname = System.getProperty("os.name");
		if (OSname.toLowerCase().indexOf("windows") > -1) {
			OStype = 0;
			InitGlobalFont(new Font("微软雅黑", Font.PLAIN, 12));
		} else if (OSname.toLowerCase().indexOf("mac") > -1) {
			OStype = 1;
			QALogfile = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()
					+ "/Desktop/QAToolsLogs";
		} else {
			OStype = 2;
		}
		logger.info("System type=" + OSname);
		// 初始化XML
		xmlOperationUtil = new XMLOperationUtil();
		// 初始化ADB桥接
		adbBridge = new AdbBridge();
		// 初始化 Params
		ParamsUtil paramsUtil = new ParamsUtil();
		paramsBean = new ParamsBean();
		if (!paramsUtil.InitParams(paramsBean)) {
			logger.error("Config.xml has wrong items, Stop!");
			return;
		} else {
			if (!paramsBean.getReportPath().equals("")) {// 设置报告保存路径
				QALogfile = paramsBean.getReportPath() + "/QAToolsLogs";
			} else {
				paramsBean.setReportPath(QALogfile.substring(0, QALogfile.length() - "QAToolsLogs".length()));
			}
			logger.info("报告保存路径" + QALogfile);
		}

		// create folder in desktop
		File file = new File(QALogfile);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 初始化主界面
		mainFrame = new MainUI();
		// check ue and pc
		CheckUE checkue = new CheckUE();
		checkue.run();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				CheckPC.checkAll();
				String check = CheckPC.checkAll();
				if (check.contains("异常")) {
					JOptionPane.showMessageDialog(null, check, "消息", JOptionPane.ERROR_MESSAGE);
					SysConfigBoxUI sysConfigBoxUI = new SysConfigBoxUI();
					sysConfigBoxUI.setVisible(true);
				}
			}
		}).start();
		// 显示主界面
		mainFrame.setVisible(true);
		// test
//		OverlapImage overlapImage = new OverlapImage();
//		overlapImage.start();
	}

	// 变更全局字体
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
