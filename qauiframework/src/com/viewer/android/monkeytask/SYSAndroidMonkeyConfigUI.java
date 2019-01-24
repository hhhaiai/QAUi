package com.viewer.android.monkeytask;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.HelperUtil;
import com.viewer.main.MainRun;
import com.viewer.scenetask.GridBox;
import com.viewer.scenetask.TextShowBoxUI;
import com.viewer.wechat.MemberListUI;

import javafx.application.Platform;

public class SYSAndroidMonkeyConfigUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5176335784060205144L;
	Logger logger = LoggerFactory.getLogger(SYSAndroidMonkeyConfigUI.class);
	String udid;
	JFormattedTextField TF_seed;
	JFormattedTextField TF_intervaltime;
	JFormattedTextField TF_runtime;

	JCheckBox check_diymonkeycmd;
	TextShowBoxUI textShowBoxUI;
	JComboBox<String> comboBox_email_send;
	JFormattedTextField TF_email_to;
	JFormattedTextField TF_email_cc;
	JComboBox<String> comboBox_wechat_send;
	JComboBox<String> comboBox_apppackage;
	Map<String, String> monkeyConfigMap;

	// 布局
	GridBox gridBox = new GridBox();
	String wechat_people_list;

	/**
	 * Create the panel.
	 */
	public SYSAndroidMonkeyConfigUI(String udid) {
		setSize(450, 450);
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * init
		 */
		this.udid = udid;
		monkeyConfigMap = MainRun.androidConfigBean.getMonkey_sys();
		int GridW = 2;

		JLabel lbl_apppackage = new JLabel("应用包名");
		add(lbl_apppackage, gridBox.resetGridX().autoGridY().resetGridWH());

		comboBox_apppackage = new JComboBox<>();
		add(comboBox_apppackage, gridBox.autoGridX().setGridWH(GridW, 1));

		JLabel lbl_params = new JLabel("种子");
		add(lbl_params, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_seed = new JFormattedTextField();
		TF_seed.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_seed.setToolTipText("Monkey种子");
		add(TF_seed, gridBox.autoGridX().setGridWH(GridW, 1));

		JLabel lbl_intervaltime = new JLabel("时间间隔(毫秒)");
		add(lbl_intervaltime, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_intervaltime = new JFormattedTextField();
		TF_intervaltime.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_intervaltime.setToolTipText("Monkey每次操作的时间间隔,单位毫秒");
		add(TF_intervaltime, gridBox.autoGridX().setGridWH(GridW, 1));

		JLabel lbl_runtime = new JLabel("运行时间(分钟)");
		add(lbl_runtime, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_runtime = new JFormattedTextField();
		TF_runtime.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_runtime.setToolTipText("Monkey运行时间,到期自动停止");
		add(TF_runtime, gridBox.autoGridX().setGridWH(GridW, 1));

		JLabel lbl_stopmonkey = new JLabel("主动停止");
		add(lbl_stopmonkey, gridBox.resetGridX().autoGridY().resetGridWH());
		/**
		 * 设置主动停止按钮
		 */
		JButton btn_stopmonkey = new JButton("停止");
		btn_stopmonkey.setToolTipText("主动停止Monkey");
		btn_stopmonkey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_stopmonkey button");
				if (AndroidInfo.checkIsAlive(udid, Cconfig.MONKEY_ANDROID_SYS_PACKAGE_NAME)) {
					if (AndroidInfo.stopApp(udid, Cconfig.MONKEY_ANDROID_SYS_PACKAGE_NAME)) {
						logger.info("kill android sys monkey by shell stop ");
						JOptionPane.showMessageDialog(null, "手动停止Monkey成功", "消息", JOptionPane.INFORMATION_MESSAGE,
								MainRun.settingsBean.getLogo());
					} else {
						logger.info("kill android sys monkey by shell stop ");
						JOptionPane.showMessageDialog(null, "手动停止Monkey失败", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
					}
				} else {
					logger.info("android sys monkey has been stopped");
					JOptionPane.showMessageDialog(null, "Monkey未执行", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
				}
			}
		});
		add(btn_stopmonkey, gridBox.autoGridX().setGridWH(GridW, 1));

		JLabel lbl_diymonekycmd = new JLabel("自定义命令");
		add(lbl_diymonekycmd, gridBox.resetGridX().autoGridY().resetGridWH());
		check_diymonkeycmd = new JCheckBox("");
		check_diymonkeycmd.setToolTipText("勾选后执行自定义Monkey命令");
		add(check_diymonkeycmd, gridBox.autoGridX());

		/**
		 * 设置自定义命令按钮
		 */
		JButton btn_diymonkeycmd = new JButton("自定义");
		btn_diymonkeycmd.setToolTipText("自定义Monkey命令,满足你的一切需求");
		btn_diymonkeycmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_diymonkeycmd button");
				textShowBoxUI.setVisible(true);
			}
		});
		add(btn_diymonkeycmd, gridBox.autoGridX());

		JLabel lbl_analysislog = new JLabel("分析日志");
		add(lbl_analysislog, gridBox.resetGridX().autoGridY().resetGridWH());
		/**
		 * 选择需要分析的文件
		 */
		JButton btn_analysislog = new JButton("分析");
		btn_analysislog.setToolTipText("分析过滤重复日志");
		btn_analysislog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_analysislog button");
				File[] files = selectLogFile();
				if (files.length > 0) {
					SYSAndroidAnalysis sysAndroidAnalysis = new SYSAndroidAnalysis(monkeyConfigMap);
					sysAndroidAnalysis.start(files);
				}

			}
		});
		add(btn_analysislog, gridBox.autoGridX().setGridWH(GridW, 1));

		JLabel lbl_analysislogsetting = new JLabel("Monkey设置");
		add(lbl_analysislogsetting, gridBox.resetGridX().autoGridY().resetGridWH());
		/**
		 * 设置分析日志
		 */
		JButton btn_analysislogsetting = new JButton("设置");
		btn_analysislogsetting.setToolTipText("Monkey日志分析");
		btn_analysislogsetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_analysislogsetting button");
				SYSAndroidSettingBoxUI settingBoxUI = new SYSAndroidSettingBoxUI(monkeyConfigMap);
				settingBoxUI.setVisible(true);
			}
		});
		add(btn_analysislogsetting, gridBox.autoGridX().setGridWH(GridW, 1));

		// ************************
		JLabel lbl_email_title = new JLabel("邮件配置");
		lbl_email_title.setForeground(Color.BLUE);
		add(lbl_email_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_email_send = new JLabel("发送邮件");
		add(lbl_email_send, gridBox.resetGridX().autoGridY());

		comboBox_email_send = new JComboBox<>();
		add(comboBox_email_send, gridBox.autoGridX());

		JLabel lbl_email_to = new JLabel("发送到");
		add(lbl_email_to, gridBox.resetGridX().autoGridY());

		TF_email_to = new JFormattedTextField();
		TF_email_to.setToolTipText("发送给");
		add(TF_email_to, gridBox.autoGridX());

		JLabel lbl_email_cc = new JLabel("抄送到");
		add(lbl_email_cc, gridBox.resetGridX().autoGridY());

		TF_email_cc = new JFormattedTextField();
		TF_email_cc.setToolTipText("抄送给");
		add(TF_email_cc, gridBox.autoGridX());
		// android wechat
		JLabel lbl_wechat_title = new JLabel("微信通知");
		lbl_wechat_title.setForeground(Color.BLUE);
		add(lbl_wechat_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_wechat_send = new JLabel("发送微信");
		add(lbl_wechat_send, gridBox.resetGridX().autoGridY());

		comboBox_wechat_send = new JComboBox<>();
		add(comboBox_wechat_send, gridBox.autoGridX());

		JLabel lbl_wechat_people_list = new JLabel("人员设置");
		add(lbl_wechat_people_list, gridBox.resetGridX().autoGridY());

		JButton btn_wechat_people_list = new JButton("设置");
		btn_wechat_people_list.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				logger.info("press btn_wechat_people_list button");
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						MemberListUI memberListUI = new MemberListUI(wechat_people_list) {

							@Override
							public boolean confirm() {
								// TODO Auto-generated method stub
								wechat_people_list = getContorller().getPeopleList();
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
		add(btn_wechat_people_list, gridBox.autoGridX());

		InitValue();
	}

	/**
	 * 初始化参数值
	 */
	private void InitValue() {
		String packages = monkeyConfigMap.get(Cparams.monkey_sys_packages);
		if (packages.matches(Cconfig.REGEX_FORMAT)) {
			for (String item : packages.split(";")) {
				String nickname = item.split("=")[0];
				String packagename = item.split("=")[1];
				comboBox_apppackage
						.addItem("<html><font color=\"#FF0000\">" + nickname + "</font>=" + packagename + "</html>");
			}
		}
		TF_seed.setText(new SimpleDateFormat("ssSSS").format(new Date()));
		TF_intervaltime.setText("1000");
		TF_runtime.setText("30");
		check_diymonkeycmd.setSelected(false);

		textShowBoxUI = new TextShowBoxUI("自定义命令", "编辑框", monkeyConfigMap.get(Cparams.monkey_sys_customize).toString(),
				450, 300) {
			private static final long serialVersionUID = 6408724260802766424L;

			@Override
			protected boolean confirmButton() {
				// TODO Auto-generated method stub
				logger.info("press textShowBoxUI button");
				Map<String, String> map = new HashMap<>();
				map.put(Cparams.monkey_sys_customize, getText());
				MainRun.androidXmlParse.writeMonkeyMap(map);
				return true;
			}

			@Override
			protected boolean cancelButton() {
				return true;
			}
		};

		// email
		Map<String, String> emailMap = MainRun.androidConfigBean.getEmail();
		comboBox_email_send.addItem("true");
		comboBox_email_send.addItem("false");
		comboBox_email_send.setSelectedItem(emailMap.get(Cparams.send));
		TF_email_to.setText(emailMap.get(Cparams.to));
		TF_email_cc.setText(emailMap.get(Cparams.cc));
		// wechat
		wechat_people_list = MainRun.androidConfigBean.getWechat().get(Cparams.people_list);
		comboBox_wechat_send.addItem("true");
		comboBox_wechat_send.addItem("false");
		comboBox_wechat_send.setSelectedItem(MainRun.androidConfigBean.getWechat().get(Cparams.send));
	}

	/**
	 * 根据场景设置参数
	 * 
	 * @param monkeyMap
	 */
	public void setValueByScene(Map<String, Object> monkeyMap) {
		comboBox_apppackage.setSelectedItem(
				"<html><font color=\"#FF0000\">" + monkeyMap.get(Cparams.monkey_sys_appnickname).toString() + "</font>="
						+ monkeyMap.get(Cparams.monkey_sys_apppackagename).toString() + "</html>");
		TF_seed.setText(monkeyMap.get(Cparams.monkey_sys_seed).toString());
		TF_intervaltime.setText(monkeyMap.get(Cparams.monkey_sys_intervaltime).toString());
		TF_runtime.setText(monkeyMap.get(Cparams.monkey_sys_runtime).toString());
		check_diymonkeycmd.setSelected(monkeyMap.get(Cparams.monkey_sys_runcustomize).equals("true"));
		// email
		comboBox_email_send.setSelectedItem(monkeyMap.get(Cparams.email_send));
		TF_email_to.setText(monkeyMap.get(Cparams.email_to).toString());
		TF_email_cc.setText(monkeyMap.get(Cparams.email_cc).toString());
		// wechat
		wechat_people_list = (String) monkeyMap.get(Cparams.wechat_people_list);
		comboBox_wechat_send.setSelectedItem(monkeyMap.get(Cparams.wechat_send));
	}

	/**
	 * 返回当前monkey设置参数
	 * 
	 * @return
	 */
	public Map<String, Object> getMonkeyMap() {
		Map<String, Object> monkeyMap = new HashMap<>();
		monkeyMap.put(Cparams.type, Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
		monkeyMap.put(Cparams.monkey_sys_runtime, TF_runtime.getText());
		monkeyMap.put(Cparams.monkey_sys_seed, TF_seed.getText());
		monkeyMap.put(Cparams.monkey_sys_intervaltime, TF_intervaltime.getText());
		Matcher matcher = Pattern.compile("<html><font color=\"#FF0000\">(.*?)</font>=(.*?)</html>")
				.matcher(comboBox_apppackage.getSelectedItem().toString());
		String packages = null;
		String nickname = null;
		if (matcher.find()) {
			nickname = matcher.group(1);
			packages = matcher.group(2);
		}
		monkeyMap.put(Cparams.monkey_sys_apppackagename, packages);
		monkeyMap.put(Cparams.monkey_sys_appnickname, nickname);
		monkeyMap.put(Cparams.udid, udid);
		monkeyMap.put(Cparams.monkey_sys_runcustomize, check_diymonkeycmd.isSelected() ? "true" : "false");
		monkeyMap.put(Cparams.monkey_sys_customize, textShowBoxUI.getText());
		monkeyMap.put(Cparams.monkey_sys_ignore_crashes, monkeyConfigMap.get(Cparams.monkey_sys_ignore_crashes));
		monkeyMap.put(Cparams.monkey_sys_ignore_timeouts, monkeyConfigMap.get(Cparams.monkey_sys_ignore_timeouts));
		monkeyMap.put(Cparams.monkey_sys_ignore_security_exceptions,
				monkeyConfigMap.get(Cparams.monkey_sys_ignore_security_exceptions));
		monkeyMap.put(Cparams.monkey_sys_ignore_native_crashes,
				monkeyConfigMap.get(Cparams.monkey_sys_ignore_native_crashes));
		monkeyMap.put(Cparams.monkey_sys_monitor_native_crashes,
				monkeyConfigMap.get(Cparams.monkey_sys_monitor_native_crashes));
		monkeyMap.put(Cparams.monkey_sys_pct_touch, monkeyConfigMap.get(Cparams.monkey_sys_pct_touch));
		monkeyMap.put(Cparams.monkey_sys_pct_motion, monkeyConfigMap.get(Cparams.monkey_sys_pct_motion));
		monkeyMap.put(Cparams.monkey_sys_pct_trackball, monkeyConfigMap.get(Cparams.monkey_sys_pct_trackball));
		monkeyMap.put(Cparams.monkey_sys_pct_nav, monkeyConfigMap.get(Cparams.monkey_sys_pct_nav));
		monkeyMap.put(Cparams.monkey_sys_pct_majornav, monkeyConfigMap.get(Cparams.monkey_sys_pct_majornav));
		monkeyMap.put(Cparams.monkey_sys_pct_syskeys, monkeyConfigMap.get(Cparams.monkey_sys_pct_syskeys));
		monkeyMap.put(Cparams.monkey_sys_pct_appswitch, monkeyConfigMap.get(Cparams.monkey_sys_pct_appswitch));
		monkeyMap.put(Cparams.monkey_sys_pct_anyevent, monkeyConfigMap.get(Cparams.monkey_sys_pct_anyevent));
		// email
		monkeyMap.put(Cparams.email_send, comboBox_email_send.getSelectedItem());
		monkeyMap.put(Cparams.email_to, TF_email_to.getText());
		monkeyMap.put(Cparams.email_cc, TF_email_cc.getText());
		monkeyMap.put(Cparams.email_smtp, MainRun.androidConfigBean.getEmail().get(Cparams.smtp));
		monkeyMap.put(Cparams.email_account, MainRun.androidConfigBean.getEmail().get(Cparams.account));
		monkeyMap.put(Cparams.email_password, MainRun.androidConfigBean.getEmail().get(Cparams.password));
		// wechat
		monkeyMap.put(Cparams.wechat_people_list, wechat_people_list);
		monkeyMap.put(Cparams.wechat_send, comboBox_wechat_send.getSelectedItem());
		return monkeyMap;
	}

	/**
	 * 选择日志文件,多选
	 * 
	 * @return
	 */
	private File[] selectLogFile() {
		File[] selectfiles = {};
		JFileChooser fileChooser = new JFileChooser(MainRun.settingsBean.getUiReportPath());
		// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.txt|*.log";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".txt") | ext.endsWith(".log");
			}
		});
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			logger.info("No file selected.");
			return selectfiles;
		}
		selectfiles = fileChooser.getSelectedFiles();
		logger.info("select " + selectfiles.length + " files.");
		return selectfiles;
	}
}
