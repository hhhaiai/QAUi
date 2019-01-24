package com.viewer.main;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.helper.HelperUtil;
import com.task.ReadScene;
import com.viewer.scenetask.GridBox;
import com.viewer.wechat.MemberListUI;

import javafx.application.Platform;

public class SysConfigBoxUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(SysConfigBoxUI.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 8517102059308833966L;
	private JPanel contentPane;

	JFormattedTextField TF_reportPath;
	JFormattedTextField TF_appiumServerUrl;
	JFormattedTextField TF_waitforElement;
	JFormattedTextField TF_picTargetHight;
	JFormattedTextField TF_QAreporter_url;
	// JFormattedTextField TF_picFont;
	// JFormattedTextField TF_picOval;
	JFormattedTextField TF_waitAfterOperation;
	JRadioButton font_radiobutton_S;
	JRadioButton font_radiobutton_L;
	JRadioButton font_radiobutton_M;
	// android
	JFormattedTextField TF_androidsdk;
	JComboBox<String> comboBox_android_screenout;
	// ios
	JFormattedTextField TF_maccmd;
	// JFrame
	JButton btnOK;
	JButton btnCancel;

	JComboBox<String> comboBox_android_email_send;
	JFormattedTextField TF_android_email_to;
	JFormattedTextField TF_android_email_cc;
	JFormattedTextField TF_android_email_smtp;
	JFormattedTextField TF_android_email_account;
	JPasswordField TFPW_android_email_password;
	JComboBox<String> comboBox_android_wechat_send;

	JComboBox<String> comboBox_ios_email_send;
	JFormattedTextField TF_ios_email_to;
	JFormattedTextField TF_ios_email_cc;
	JFormattedTextField TF_ios_email_smtp;
	JFormattedTextField TF_ios_email_account;
	JFormattedTextField TF_ios_email_password;
	JPasswordField TFPW_ios_email_password;
	JComboBox<String> comboBox_ios_wechat_send;

	JFormattedTextField TF_wechat_name;
	JPasswordField TFPW_agentid;
	JPasswordField TFPW_corpid;
	JPasswordField TFPW_corpsecret;

	String ios_wechat_people_list = "";
	String android_wechat_people_list = "";

	/**
	 * Create the frame.
	 */
	public SysConfigBoxUI() {
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("系统设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());

		JScrollPane configScroll = new JScrollPane(ConfigJPanel());
		configScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		configScroll.setBounds(25, 10, 450, 300);
		contentPane.add(configScroll);

		// 确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				if (!HelperUtil.checkEmail(TF_android_email_to.getText())
						|| !HelperUtil.checkEmail(TF_android_email_cc.getText())) {
					JOptionPane.showMessageDialog(contentPane, "Android邮箱格式错误,请检查!", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				if (!HelperUtil.checkEmail(TF_ios_email_to.getText())
						|| !HelperUtil.checkEmail(TF_ios_email_cc.getText())) {
					JOptionPane.showMessageDialog(contentPane, "iOS邮箱格式错误,请检查!", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				setSysconfig();
				// 检查环境
				String check = CheckPC.checkAll();
				if (check.contains("异常"))
					JOptionPane.showMessageDialog(contentPane, check, "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());

				dispose();
			}
		});
		btnOK.setBounds(271, 347, 100, 25);
		contentPane.add(btnOK);
		// 取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press cancel button");

				dispose();
			}
		});
		btnCancel.setBounds(383, 347, 100, 25);
		contentPane.add(btnCancel);

		JButton btnDefault = new JButton("恢复默认");
		btnDefault.addActionListener(e -> {
			TF_appiumServerUrl.setText("http://0.0.0.0:4723/wd/hub");
			TF_reportPath.setText("");
			TF_waitforElement.setText("3");
			// TF_picFont.setText("36");
			font_radiobutton_M.setSelected(true);
			// TF_picOval.setText("25");
			TF_picTargetHight.setText("854");
			TF_waitAfterOperation.setText("0");
			TF_QAreporter_url.setText("");
		});
		btnDefault.setBounds(25, 347, 100, 25);
		contentPane.add(btnDefault);
	}

	/**
	 * 文件夹选择
	 * 
	 * @return
	 */
	private File selectFloder() {
		JFileChooser fileChooser = new JFileChooser("");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
		if (fileChooser.showOpenDialog(null) != 0) {
			logger.info("No floder selected.");
			return null;
		}
		return fileChooser.getSelectedFile();
	}

	/**
	 * 修改系统设置
	 */
	private void setSysconfig() {
		MainRun.sysXmlParse.writeAppiumServerUrl(TF_appiumServerUrl.getText());
		MainRun.sysXmlParse.writeReportPath(TF_reportPath.getText());
		if (font_radiobutton_S.isSelected()) {
			MainRun.sysXmlParse.writePicFont("1");
		} else if (font_radiobutton_L.isSelected()) {
			MainRun.sysXmlParse.writePicFont("3");
		} else {
			MainRun.sysXmlParse.writePicFont("2");
		}

		// MainRun.sysXmlParse.writePicOval(TF_picOval.getText());
		MainRun.sysXmlParse.writePicTargetHight(TF_picTargetHight.getText());
		MainRun.sysXmlParse.writeWaitforElement(TF_waitforElement.getText());
		MainRun.sysXmlParse.writeAndroidSDK(TF_androidsdk.getText());
		MainRun.sysXmlParse.writeMACcmd(TF_maccmd.getText());
		MainRun.sysXmlParse.writeWaitAfterOperation(TF_waitAfterOperation.getText());
		MainRun.sysXmlParse.writeQAreporter_url(TF_QAreporter_url.getText());
		MainRun.sysXmlParse.writeAndroidScreenOut((String) comboBox_android_screenout.getSelectedItem());

		Map<String, String> android_emailMap = new HashMap<>();
		android_emailMap.put(Cparams.send, (String) comboBox_android_email_send.getSelectedItem());
		android_emailMap.put(Cparams.to, TF_android_email_to.getText());
		android_emailMap.put(Cparams.cc, TF_android_email_cc.getText());
		android_emailMap.put(Cparams.smtp, TF_android_email_smtp.getText());
		android_emailMap.put(Cparams.account, TF_android_email_account.getText());
		android_emailMap.put(Cparams.password, String.valueOf(TFPW_android_email_password.getPassword()));
		MainRun.androidXmlParse.writeEmailMap(android_emailMap);
		Map<String, String> android_wechat = new HashMap<>();
		android_wechat.put(Cparams.send, (String) comboBox_android_wechat_send.getSelectedItem());
		android_wechat.put(Cparams.people_list, android_wechat_people_list);
		MainRun.androidXmlParse.writeWechatMap(android_wechat);

		Map<String, String> ios_emailMap = new HashMap<>();
		ios_emailMap.put(Cparams.send, (String) comboBox_ios_email_send.getSelectedItem());
		ios_emailMap.put(Cparams.to, TF_ios_email_to.getText());
		ios_emailMap.put(Cparams.cc, TF_ios_email_cc.getText());
		ios_emailMap.put(Cparams.smtp, TF_ios_email_smtp.getText());
		ios_emailMap.put(Cparams.account, TF_ios_email_account.getText());
		ios_emailMap.put(Cparams.password, String.valueOf(TFPW_ios_email_password.getPassword()));
		MainRun.iosXmlParse.writeEmailMap(ios_emailMap);
		Map<String, String> ios_wechat = new HashMap<>();
		ios_wechat.put(Cparams.send, (String) comboBox_ios_wechat_send.getSelectedItem());
		ios_wechat.put(Cparams.people_list, ios_wechat_people_list);
		MainRun.iosXmlParse.writeWechatMap(ios_wechat);
		// sys wechat
		Map<String, String> sys_wechat = new HashMap<>();
		sys_wechat.put(Cparams.agentid, String.valueOf(TFPW_agentid.getPassword()));
		sys_wechat.put(Cparams.corpid, String.valueOf(TFPW_corpid.getPassword()));
		sys_wechat.put(Cparams.corpsecret, String.valueOf(TFPW_corpsecret.getPassword()));
		sys_wechat.put(Cparams.name, TF_wechat_name.getText());
		MainRun.sysXmlParse.writeWechatMap(sys_wechat);
		// 刷新scene xml
		ReadScene readScene = new ReadScene();
		MainRun.androidConfigBean.setScene(readScene.getAndroidSceneMap());
		MainRun.iosConfigBean.setScene(readScene.getIOSSceneMap());
	}

	/**
	 * 设置界面
	 * 
	 * @return
	 */
	private JPanel ConfigJPanel() {
		JPanel contentPane = new JPanel();
		contentPane.setSize(600, 800);
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		// 布局
		GridBox gridBox = new GridBox();
		int gridW = 3;

		JLabel lbl_config = new JLabel("通用设置");
		lbl_config.setForeground(Color.BLUE);
		contentPane.add(lbl_config, gridBox.resetGridX().autoGridY().resetGridWH());

		JLabel lbl_appiumServer = new JLabel("Appium服务器地址");
		contentPane.add(lbl_appiumServer, gridBox.resetGridX().autoGridY().resetGridWH());

		JButton btn_appiumServer = new JButton("检测");
		btn_appiumServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_appiumServer button");
				String result = CheckPC.checkAppiumServerUrl(TF_appiumServerUrl.getText()) ? "连接正常" : "无法连接";
				JOptionPane.showMessageDialog(null, result, "消息", JOptionPane.INFORMATION_MESSAGE,
						MainRun.settingsBean.getLogo());
			}
		});
		contentPane.add(btn_appiumServer, gridBox.autoGridX().setGridWH(gridW, 1));

		TF_appiumServerUrl = new JFormattedTextField();
		TF_appiumServerUrl.setText(MainRun.sysConfigBean.getAppiumServerUrl());
		TF_appiumServerUrl.setToolTipText("Appium服务器的完整地址,如http://0.0.0.0:4723/wd/hub");
		contentPane.add(TF_appiumServerUrl, gridBox.resetGridX().autoGridY().setGridWH(gridW + 1, 1));
		gridBox.resetGridWH();

		JLabel lbl_reportPath = new JLabel("报告保存路径");
		contentPane.add(lbl_reportPath, gridBox.resetGridX().autoGridY().resetGridWH());

		JButton btn_reportPath = new JButton("选择");
		btn_reportPath.addActionListener(e -> {
			logger.info("press btn_apks button");
			File file = selectFloder();
			if (file != null)
				TF_reportPath.setText(file.getAbsolutePath());
		});
		contentPane.add(btn_reportPath, gridBox.autoGridX().setGridWH(gridW, 1));

		TF_reportPath = new JFormattedTextField();
		TF_reportPath.setText(MainRun.sysConfigBean.getReportPath());
		TF_reportPath.setToolTipText("测试报告保存地址,不填默认在桌面/QAUiReport");
		contentPane.add(TF_reportPath, gridBox.resetGridX().autoGridY().setGridWH(gridW + 1, 1));
		gridBox.resetGridWH();

		JLabel lbl_waitforElement = new JLabel("查找元素等待时间");
		contentPane.add(lbl_waitforElement, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_waitforElement = new JFormattedTextField();
		TF_waitforElement.setText(MainRun.sysConfigBean.getWaitforElement() + "");
		TF_waitforElement.setToolTipText("查找元素默认等待时间,单位秒,超过该时间则抛出找不到元素错误");
		TF_waitforElement.addKeyListener(HelperUtil.Listener_inputNumbers());
		contentPane.add(TF_waitforElement, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_waitAfterOperation = new JLabel("每次操作后等待时间");
		contentPane.add(lbl_waitAfterOperation, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_waitAfterOperation = new JFormattedTextField();
		TF_waitAfterOperation.setText(MainRun.sysConfigBean.getWaitAfterOperation() + "");
		TF_waitAfterOperation.setToolTipText("每次操作后默认等待时间,单位毫秒");
		TF_waitAfterOperation.addKeyListener(HelperUtil.Listener_inputNumbers());
		contentPane.add(TF_waitAfterOperation, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_QAreporter_url = new JLabel("服务器URL");
		contentPane.add(lbl_QAreporter_url, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_QAreporter_url = new JFormattedTextField();
		TF_QAreporter_url.setText(MainRun.sysConfigBean.getQAreporter_url() + "");
		TF_QAreporter_url.setToolTipText("服务器URL,如果未架设则不填");
		contentPane.add(TF_QAreporter_url, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android = new JLabel("Android设置");
		lbl_android.setForeground(Color.BLUE);
		contentPane.add(lbl_android, gridBox.resetGridX().autoGridY().resetGridWH());

		JLabel lbl_androidsdk = new JLabel("SDK或ADB路径");
		contentPane.add(lbl_androidsdk, gridBox.resetGridX().autoGridY().resetGridWH());

		JButton btn_androidsdk = new JButton("选择");
		btn_androidsdk.addActionListener(e -> {
			logger.info("press btn_androidsdk button");
			File file = selectFloder();
			if (file != null)
				TF_androidsdk.setText(file.getAbsolutePath());
		});
		contentPane.add(btn_androidsdk, gridBox.autoGridX().setGridWH(gridW, 1));

		TF_androidsdk = new JFormattedTextField();
		TF_androidsdk.setText(MainRun.sysConfigBean.getAndroidSDK());
		TF_androidsdk.setToolTipText("设置android SDK 路径根目录或者adb目录");
		contentPane.add(TF_androidsdk, gridBox.resetGridX().autoGridY().setGridWH(gridW + 1, 1));
		gridBox.resetGridWH();

		JLabel lbl_android_screenout = new JLabel("所有任务完成后灭屏");
		contentPane.add(lbl_android_screenout, gridBox.resetGridX().autoGridY().resetGridWH());

		comboBox_android_screenout = new JComboBox<>();
		comboBox_android_screenout.addItem("true");
		comboBox_android_screenout.addItem("false");
		comboBox_android_screenout.setSelectedItem(MainRun.sysConfigBean.getAndroidScreenOut());
		contentPane.add(comboBox_android_screenout, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_email_send = new JLabel("发送邮件");
		contentPane.add(lbl_android_email_send, gridBox.resetGridX().autoGridY().resetGridWH());

		comboBox_android_email_send = new JComboBox<>();
		comboBox_android_email_send.addItem("true");
		comboBox_android_email_send.addItem("false");
		comboBox_android_email_send.setSelectedItem(MainRun.androidConfigBean.getEmail().get(Cparams.send));
		contentPane.add(comboBox_android_email_send, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_email_to = new JLabel("发送到");
		contentPane.add(lbl_android_email_to, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_android_email_to = new JFormattedTextField();
		TF_android_email_to.setToolTipText("发送给");
		TF_android_email_to.setText(MainRun.androidConfigBean.getEmail().get(Cparams.to));
		contentPane.add(TF_android_email_to, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_email_cc = new JLabel("抄送到");
		contentPane.add(lbl_android_email_cc, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_android_email_cc = new JFormattedTextField();
		TF_android_email_cc.setToolTipText("抄送给");
		TF_android_email_cc.setText(MainRun.androidConfigBean.getEmail().get(Cparams.cc));
		contentPane.add(TF_android_email_cc, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_email_smtp = new JLabel("SMTP服务器");
		contentPane.add(lbl_android_email_smtp, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_android_email_smtp = new JFormattedTextField();
		TF_android_email_smtp.setToolTipText("SMTP服务器地址");
		TF_android_email_smtp.setText(MainRun.androidConfigBean.getEmail().get(Cparams.smtp));
		contentPane.add(TF_android_email_smtp, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_email_account = new JLabel("邮箱账号");
		contentPane.add(lbl_android_email_account, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_android_email_account = new JFormattedTextField();
		TF_android_email_account.setToolTipText("发件人邮箱账号");
		TF_android_email_account.setText(MainRun.androidConfigBean.getEmail().get(Cparams.account));
		contentPane.add(TF_android_email_account, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_email_password = new JLabel("邮箱密码");
		contentPane.add(lbl_android_email_password, gridBox.resetGridX().autoGridY().resetGridWH());

		TFPW_android_email_password = new JPasswordField();
		TFPW_android_email_password.setToolTipText("发件人邮箱密码");
		TFPW_android_email_password.setText(MainRun.androidConfigBean.getEmail().get(Cparams.password));
		contentPane.add(TFPW_android_email_password, gridBox.autoGridX().setGridWH(gridW, 1));
		// android wechat
		JLabel lbl_android_wechat_send = new JLabel("发送微信");
		contentPane.add(lbl_android_wechat_send, gridBox.resetGridX().autoGridY().resetGridWH());

		comboBox_android_wechat_send = new JComboBox<>();
		comboBox_android_wechat_send.addItem("true");
		comboBox_android_wechat_send.addItem("false");
		comboBox_android_wechat_send.setSelectedItem(MainRun.androidConfigBean.getWechat().get(Cparams.send));
		contentPane.add(comboBox_android_wechat_send, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_android_wechat_people_list = new JLabel("人员设置");
		contentPane.add(lbl_android_wechat_people_list, gridBox.resetGridX().autoGridY().resetGridWH());

		android_wechat_people_list = MainRun.androidConfigBean.getWechat().get(Cparams.people_list);
		JButton btn_android_wechat_people_list = new JButton("设置");
		btn_android_wechat_people_list.setToolTipText("勾选人员后,将发送微信消息");
		btn_android_wechat_people_list.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				logger.info("press btn_android_wechat_people_list button");
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						MemberListUI memberListUI = new MemberListUI(android_wechat_people_list) {

							@Override
							public boolean confirm() {
								// TODO Auto-generated method stub
								android_wechat_people_list = getContorller().getPeopleList();
								return true;
							}

							@Override
							public boolean cancel() {
								// TODO Auto-generated method stub
								return true;
							}

						};
						memberListUI.show();
					}
				});

			}
		});
		contentPane.add(btn_android_wechat_people_list, gridBox.autoGridX());

		JLabel lbl_IOS = new JLabel("iOS设置(需要Mac操作系统)");
		lbl_IOS.setForeground(Color.BLUE);
		contentPane.add(lbl_IOS, gridBox.resetGridX().autoGridY().resetGridWH());

		JLabel lbl_maccmd = new JLabel("mac cmd路径");
		contentPane.add(lbl_maccmd, gridBox.resetGridX().autoGridY().resetGridWH());

		JButton btn_maccmd = new JButton("选择");
		btn_maccmd.addActionListener(e -> {
			logger.info("press btn_maccmd button");
			File file = selectFloder();
			if (file != null)
				TF_maccmd.setText(file.getAbsolutePath());
		});
		contentPane.add(btn_maccmd, gridBox.autoGridX().setGridWH(gridW, 1));

		TF_maccmd = new JFormattedTextField();
		TF_maccmd.setText(MainRun.sysConfigBean.getMACcmd());
		TF_maccmd.setToolTipText("设置MAC cmd目录,如/usr/local/bin");
		contentPane.add(TF_maccmd, gridBox.resetGridX().autoGridY().setGridWH(gridW + 1, 1));
		gridBox.resetGridWH();

		JLabel lbl_ios_email_send = new JLabel("发送邮件");
		contentPane.add(lbl_ios_email_send, gridBox.resetGridX().autoGridY().resetGridWH());

		comboBox_ios_email_send = new JComboBox<>();
		comboBox_ios_email_send.addItem("true");
		comboBox_ios_email_send.addItem("false");
		comboBox_ios_email_send.setSelectedItem(MainRun.iosConfigBean.getEmail().get(Cparams.send));
		contentPane.add(comboBox_ios_email_send, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ios_email_to = new JLabel("发送到");
		contentPane.add(lbl_ios_email_to, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_ios_email_to = new JFormattedTextField();
		TF_ios_email_to.setToolTipText("发送给");
		TF_ios_email_to.setText(MainRun.iosConfigBean.getEmail().get(Cparams.to));
		contentPane.add(TF_ios_email_to, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ios_email_cc = new JLabel("抄送到");
		contentPane.add(lbl_ios_email_cc, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_ios_email_cc = new JFormattedTextField();
		TF_ios_email_cc.setToolTipText("抄送给");
		TF_ios_email_cc.setText(MainRun.iosConfigBean.getEmail().get(Cparams.cc));
		contentPane.add(TF_ios_email_cc, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ios_email_smtp = new JLabel("SMTP服务器");
		contentPane.add(lbl_ios_email_smtp, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_ios_email_smtp = new JFormattedTextField();
		TF_ios_email_smtp.setToolTipText("SMTP服务器地址");
		TF_ios_email_smtp.setText(MainRun.iosConfigBean.getEmail().get(Cparams.smtp));
		contentPane.add(TF_ios_email_smtp, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ios_email_account = new JLabel("邮箱账号");
		contentPane.add(lbl_ios_email_account, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_ios_email_account = new JFormattedTextField();
		TF_ios_email_account.setToolTipText("发件人邮箱账号");
		TF_ios_email_account.setText(MainRun.iosConfigBean.getEmail().get(Cparams.account));
		contentPane.add(TF_ios_email_account, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ios_email_password = new JLabel("邮箱密码");
		contentPane.add(lbl_ios_email_password, gridBox.resetGridX().autoGridY().resetGridWH());

		TFPW_ios_email_password = new JPasswordField();
		TFPW_ios_email_password.setToolTipText("发件人邮箱密码");
		TFPW_ios_email_password.setText(MainRun.iosConfigBean.getEmail().get(Cparams.password));
		contentPane.add(TFPW_ios_email_password, gridBox.autoGridX().setGridWH(gridW, 1));
		// ios wechat
		JLabel lbl_ios_wechat_send = new JLabel("发送微信");
		contentPane.add(lbl_ios_wechat_send, gridBox.resetGridX().autoGridY().resetGridWH());

		comboBox_ios_wechat_send = new JComboBox<>();
		comboBox_ios_wechat_send.addItem("true");
		comboBox_ios_wechat_send.addItem("false");
		comboBox_ios_wechat_send.setSelectedItem(MainRun.iosConfigBean.getWechat().get(Cparams.send));
		contentPane.add(comboBox_ios_wechat_send, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ios_wechat_people_list = new JLabel("人员设置");
		contentPane.add(lbl_ios_wechat_people_list, gridBox.resetGridX().autoGridY().resetGridWH());

		ios_wechat_people_list = MainRun.iosConfigBean.getWechat().get(Cparams.people_list);
		JButton btn_ios_wechat_people_list = new JButton("设置");
		btn_ios_wechat_people_list.setToolTipText("勾选人员后,将发送微信消息");
		btn_ios_wechat_people_list.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				logger.info("press btn_wechat_people_list button");
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						MemberListUI memberListUI = new MemberListUI(ios_wechat_people_list) {

							@Override
							public boolean confirm() {
								// TODO Auto-generated method stub
								ios_wechat_people_list = getContorller().getPeopleList();
								return true;
							}

							@Override
							public boolean cancel() {
								// TODO Auto-generated method stub
								return true;
							}

						};
						memberListUI.show();
					}
				});

			}
		});
		contentPane.add(btn_ios_wechat_people_list, gridBox.autoGridX());

		JLabel lbl_report = new JLabel("报告设置");
		lbl_report.setForeground(Color.BLUE);
		contentPane.add(lbl_report, gridBox.resetGridX().autoGridY().resetGridWH());

		JLabel lbl_picTargetHight = new JLabel("截图分辨率-高");
		contentPane.add(lbl_picTargetHight, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_picTargetHight = new JFormattedTextField();
		TF_picTargetHight.setText(MainRun.sysConfigBean.getPicTargetHight() + "");
		TF_picTargetHight.setToolTipText("步骤异常等截图的分辨率的高(宽等比例调整),0为不调整,默认854(480P)");
		TF_picTargetHight.addKeyListener(HelperUtil.Listener_inputNumbers());
		contentPane.add(TF_picTargetHight, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_picFont = new JLabel("图片标记大小");
		contentPane.add(lbl_picFont, gridBox.resetGridX().autoGridY().resetGridWH());

		// TF_picFont = new JFormattedTextField();
		// TF_picFont.setText(MainRun.sysConfigBean.getPicFont()+"");
		// TF_picFont.setToolTipText("标记图片时文字的字体大小,默认36");
		// TF_picFont.addKeyListener(inputNumbers());
		// contentPane.add(TF_picFont,gridBox.autoGridX());

		font_radiobutton_S = new JRadioButton("小");
		contentPane.add(font_radiobutton_S, gridBox.autoGridX());
		font_radiobutton_M = new JRadioButton("中");
		contentPane.add(font_radiobutton_M, gridBox.autoGridX());
		font_radiobutton_L = new JRadioButton("大");
		contentPane.add(font_radiobutton_L, gridBox.autoGridX());

		ButtonGroup font_gourp = new ButtonGroup();
		font_gourp.add(font_radiobutton_S);
		font_gourp.add(font_radiobutton_M);
		font_gourp.add(font_radiobutton_L);
		if (MainRun.sysConfigBean.getPicFont() == 1) {
			font_radiobutton_S.setSelected(true);
		} else if (MainRun.sysConfigBean.getPicFont() == 3) {
			font_radiobutton_L.setSelected(true);
		} else {
			font_radiobutton_M.setSelected(true);
		}

		JLabel lbl_wechat = new JLabel("Wechat设置");
		lbl_android.setForeground(Color.BLUE);
		contentPane.add(lbl_wechat, gridBox.resetGridX().autoGridY().resetGridWH());

		JLabel lbl_wechat_name = new JLabel("企业名称");
		contentPane.add(lbl_wechat_name, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_wechat_name = new JFormattedTextField();
		TF_wechat_name.setToolTipText("企业名称显示,企业微信API文档参考https://work.weixin.qq.com/api/doc");
		TF_wechat_name.setText(MainRun.sysConfigBean.getWechat().get(Cparams.name));
		contentPane.add(TF_wechat_name, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_corpid = new JLabel("corpid");
		contentPane.add(lbl_corpid, gridBox.resetGridX().autoGridY().resetGridWH());

		TFPW_corpid = new JPasswordField();
		TFPW_corpid.setToolTipText("企业ID");
		TFPW_corpid.setText(MainRun.sysConfigBean.getWechat().get(Cparams.corpid));
		contentPane.add(TFPW_corpid, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_corpsecret = new JLabel("corpsecret");
		contentPane.add(lbl_corpsecret, gridBox.resetGridX().autoGridY().resetGridWH());

		TFPW_corpsecret = new JPasswordField();
		TFPW_corpsecret.setToolTipText("应用的凭证密钥");
		TFPW_corpsecret.setText(MainRun.sysConfigBean.getWechat().get(Cparams.corpsecret));
		contentPane.add(TFPW_corpsecret, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_agentid = new JLabel("agentid");
		contentPane.add(lbl_agentid, gridBox.resetGridX().autoGridY().resetGridWH());

		TFPW_agentid = new JPasswordField();
		TFPW_agentid.setToolTipText("应用ID");
		TFPW_agentid.setText(MainRun.sysConfigBean.getWechat().get(Cparams.agentid));
		contentPane.add(TFPW_agentid, gridBox.autoGridX().setGridWH(gridW, 1));
		return contentPane;
	}

}
