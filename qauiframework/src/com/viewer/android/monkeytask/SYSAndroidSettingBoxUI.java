package com.viewer.android.monkeytask;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.HelperUtil;
import com.viewer.main.MainRun;
import com.viewer.scenetask.GridBox;

public class SYSAndroidSettingBoxUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(SYSAndroidSettingBoxUI.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -6927153762903327316L;
	private JPanel contentPane;
	// 布局
	GridBox gridBox = new GridBox();
	JFormattedTextField TF_analysis_show;
	JFormattedTextField TF_analysis_arow;
	JFormattedTextField TF_analysis_arowword;
	JFormattedTextField TF_pct_touch;
	JFormattedTextField TF_pct_motion;
	JFormattedTextField TF_pct_trackball;
	JFormattedTextField TF_pct_nav;
	JFormattedTextField TF_pct_majornav;
	JFormattedTextField TF_pct_syskeys;
	JFormattedTextField TF_pct_appswitch;
	JFormattedTextField TF_pct_anyevent;
	JCheckBox check_ignore_crashes;
	JCheckBox check_ignore_timeouts;
	JCheckBox check_ignore_security_exceptions;
	JCheckBox check_ignore_native_crashes;
	JCheckBox check_monitor_native_crashes;
	JCheckBox check_analysis_showduplicate;
	JButton btnOK;
	JButton btnCancel;

	Map<String, String> monkeyConfigMap;

	/**
	 * Create the frame.
	 */
	public SYSAndroidSettingBoxUI(Map<String, String> monkeyConfigMap) {
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("Monkey设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());
		/**
		 * init
		 */
		this.monkeyConfigMap = monkeyConfigMap;
		JScrollPane configScroll = new JScrollPane(ConfigJPanel());
		configScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		configScroll.setBounds(25, 10, 450, 300);
		contentPane.add(configScroll);

		// 确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				// 百分比之和不能超过100
				if ((Integer.parseInt(TF_pct_touch.getText()) + Integer.parseInt(TF_pct_motion.getText())
						+ Integer.parseInt(TF_pct_trackball.getText()) + Integer.parseInt(TF_pct_nav.getText())
						+ Integer.parseInt(TF_pct_majornav.getText()) + Integer.parseInt(TF_pct_syskeys.getText())
						+ Integer.parseInt(TF_pct_appswitch.getText())
						+ Integer.parseInt(TF_pct_anyevent.getText())) > 100) {
					logger.info("monkey percent sum >100");
					JOptionPane.showMessageDialog(null, "事件百分比之和不能超过100", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				writeValue();
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
			logger.info("press reset button");
			// monkey
			check_ignore_crashes.setSelected(true);
			check_ignore_timeouts.setSelected(true);
			check_ignore_security_exceptions.setSelected(true);
			check_ignore_native_crashes.setSelected(true);
			check_monitor_native_crashes.setSelected(true);
			TF_pct_touch.setText(Cconfig.MONKEY_ANDROID_SYS_pct_touch);
			TF_pct_motion.setText(Cconfig.MONKEY_ANDROID_SYS_pct_motion);
			TF_pct_trackball.setText(Cconfig.MONKEY_ANDROID_SYS_pct_trackball);
			TF_pct_nav.setText(Cconfig.MONKEY_ANDROID_SYS_pct_nav);
			TF_pct_majornav.setText(Cconfig.MONKEY_ANDROID_SYS_pct_majornav);
			TF_pct_syskeys.setText(Cconfig.MONKEY_ANDROID_SYS_pct_syskeys);
			TF_pct_appswitch.setText(Cconfig.MONKEY_ANDROID_SYS_pct_appswitch);
			TF_pct_anyevent.setText(Cconfig.MONKEY_ANDROID_SYS_pct_anyevent);
			// 分析
			check_analysis_showduplicate.setSelected(false);
			TF_analysis_show.setText(Cconfig.MONKEY_ANDROID_SYS_analysis_show);
			TF_analysis_arow.setText(Cconfig.MONKEY_ANDROID_SYS_analysis_arow);
			TF_analysis_arowword.setText(Cconfig.MONKEY_ANDROID_SYS_analysis_arowword);
		});
		btnDefault.setBounds(25, 347, 100, 25);
		contentPane.add(btnDefault);

		InitValue();
	}

	/**
	 * 初始化参数
	 */
	private void InitValue() {
		// monkey
		check_ignore_crashes
				.setSelected(monkeyConfigMap.get(Cparams.monkey_sys_ignore_crashes).equals("true") ? true : false);
		check_ignore_timeouts
				.setSelected(monkeyConfigMap.get(Cparams.monkey_sys_ignore_timeouts).equals("true") ? true : false);
		check_ignore_security_exceptions.setSelected(
				monkeyConfigMap.get(Cparams.monkey_sys_ignore_security_exceptions).equals("true") ? true : false);
		check_ignore_native_crashes.setSelected(
				monkeyConfigMap.get(Cparams.monkey_sys_ignore_native_crashes).equals("true") ? true : false);
		check_monitor_native_crashes.setSelected(
				monkeyConfigMap.get(Cparams.monkey_sys_monitor_native_crashes).equals("true") ? true : false);
		TF_pct_touch.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_touch));
		TF_pct_motion.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_motion));
		TF_pct_trackball.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_trackball));
		TF_pct_nav.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_nav));
		TF_pct_majornav.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_majornav));
		TF_pct_syskeys.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_syskeys));
		TF_pct_appswitch.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_appswitch));
		TF_pct_anyevent.setText(monkeyConfigMap.get(Cparams.monkey_sys_pct_anyevent));
		// 分析
		check_analysis_showduplicate.setSelected(
				monkeyConfigMap.get(Cparams.monkey_sys_analysis_showduplicate).equals("true") ? true : false);
		TF_analysis_show.setText(monkeyConfigMap.get(Cparams.monkey_sys_analysis_show));
		TF_analysis_arow.setText(monkeyConfigMap.get(Cparams.monkey_sys_analysis_arow));
		TF_analysis_arowword.setText(monkeyConfigMap.get(Cparams.monkey_sys_analysis_arowword));
	}

	/**
	 * 保存并写入参数
	 */
	private void writeValue() {
		// monkey
		monkeyConfigMap.put(Cparams.monkey_sys_ignore_crashes, check_ignore_crashes.isSelected() ? "true" : "false");
		monkeyConfigMap.put(Cparams.monkey_sys_ignore_timeouts, check_ignore_timeouts.isSelected() ? "true" : "false");
		monkeyConfigMap.put(Cparams.monkey_sys_ignore_security_exceptions,
				check_ignore_security_exceptions.isSelected() ? "true" : "false");
		monkeyConfigMap.put(Cparams.monkey_sys_ignore_native_crashes,
				check_ignore_native_crashes.isSelected() ? "true" : "false");
		monkeyConfigMap.put(Cparams.monkey_sys_monitor_native_crashes,
				check_monitor_native_crashes.isSelected() ? "true" : "false");
		monkeyConfigMap.put(Cparams.monkey_sys_pct_touch, TF_pct_touch.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_motion, TF_pct_motion.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_trackball, TF_pct_trackball.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_nav, TF_pct_nav.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_majornav, TF_pct_majornav.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_syskeys, TF_pct_syskeys.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_appswitch, TF_pct_appswitch.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_pct_anyevent, TF_pct_anyevent.getText());
		// 分析
		monkeyConfigMap.put(Cparams.monkey_sys_analysis_show, TF_analysis_show.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_analysis_showduplicate,
				check_analysis_showduplicate.isSelected() ? "true" : "false");
		monkeyConfigMap.put(Cparams.monkey_sys_analysis_arow, TF_analysis_arow.getText());
		monkeyConfigMap.put(Cparams.monkey_sys_analysis_arowword, TF_analysis_arowword.getText());
		MainRun.androidXmlParse.writeMonkeyMap(monkeyConfigMap);
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

		int gridW = 3;
		// Monkey设置
		JLabel lbl_monkey = new JLabel("Monkey设置");
		lbl_monkey.setForeground(Color.BLUE);
		contentPane.add(lbl_monkey, gridBox.resetGridX().autoGridY().resetGridWH());
		// 异常处理
		JLabel lbl_ignore_crashes = new JLabel("ignore-crashes");
		contentPane.add(lbl_ignore_crashes, gridBox.resetGridX().autoGridY().resetGridWH());

		check_ignore_crashes = new JCheckBox();
		check_ignore_crashes.setToolTipText("忽略崩溃");
		contentPane.add(check_ignore_crashes, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ignore_timeouts = new JLabel("ignore-timeouts");
		contentPane.add(lbl_ignore_timeouts, gridBox.resetGridX().autoGridY().resetGridWH());

		check_ignore_timeouts = new JCheckBox();
		check_ignore_timeouts.setToolTipText("忽略超时");
		contentPane.add(check_ignore_timeouts, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ignore_security_exceptions = new JLabel("ignore-security-exceptions");
		contentPane.add(lbl_ignore_security_exceptions, gridBox.resetGridX().autoGridY().resetGridWH());

		check_ignore_security_exceptions = new JCheckBox();
		check_ignore_security_exceptions.setToolTipText("忽略安全异常");
		contentPane.add(check_ignore_security_exceptions, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_ignore_native_crashes = new JLabel("ignore-native-crashes");
		contentPane.add(lbl_ignore_native_crashes, gridBox.resetGridX().autoGridY().resetGridWH());

		check_ignore_native_crashes = new JCheckBox();
		check_ignore_native_crashes.setToolTipText("忽略本地方法的异常");
		contentPane.add(check_ignore_native_crashes, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_monitor_native_crashes = new JLabel("monitor-native-crashes");
		contentPane.add(lbl_monitor_native_crashes, gridBox.resetGridX().autoGridY().resetGridWH());

		check_monitor_native_crashes = new JCheckBox();
		check_monitor_native_crashes.setToolTipText("跟踪本地方法的崩溃问题");
		contentPane.add(check_monitor_native_crashes, gridBox.autoGridX().setGridWH(gridW, 1));
		// 事件百分比
		JLabel lbl_pct_touch = new JLabel("pct-touch");
		contentPane.add(lbl_pct_touch, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_touch = new JFormattedTextField();
		TF_pct_touch.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_touch.setToolTipText("触摸事件。即在某一位置的Down-Up（手指的放下和抬起）事件。Down（ACTION_DOWN）和Up（ACTION_UP）的坐标临近，但并非相同。");
		contentPane.add(TF_pct_touch, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_motion = new JLabel("pct-motion");
		contentPane.add(lbl_pct_motion, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_motion = new JFormattedTextField();
		TF_pct_motion.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_motion.setToolTipText("动作事件。以Down（ACTION_DOWN）开始，Up（ACTION_UP）结尾，中间至少有一次Move（ACTION_MOVE）");
		contentPane.add(TF_pct_motion, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_trackball = new JLabel("pct-trackball");
		contentPane.add(lbl_pct_trackball, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_trackball = new JFormattedTextField();
		TF_pct_trackball.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_trackball.setToolTipText("轨迹球事件。即单纯的Move（ACTION_MOVE）");
		contentPane.add(TF_pct_trackball, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_nav = new JLabel("pct-nav");
		contentPane.add(lbl_pct_nav, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_nav = new JFormattedTextField();
		TF_pct_nav.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_nav.setToolTipText("基本导航事件。即来自于方向输入设备的上下左右操作。");
		contentPane.add(TF_pct_nav, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_majornav = new JLabel("pct-majornav");
		contentPane.add(lbl_pct_majornav, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_majornav = new JFormattedTextField();
		TF_pct_majornav.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_majornav.setToolTipText("主导航事件。即Navigation Bar的确认，菜单，返回键等。");
		contentPane.add(TF_pct_majornav, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_syskeys = new JLabel("pct-syskeys");
		contentPane.add(lbl_pct_syskeys, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_syskeys = new JFormattedTextField();
		TF_pct_syskeys.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_syskeys.setToolTipText("系统按键事件。即系统保留按键，如HOME键，BACK键，拨号键，挂断键，音量键等。");
		contentPane.add(TF_pct_syskeys, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_appswitch = new JLabel("pct-appswitch");
		contentPane.add(lbl_pct_appswitch, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_appswitch = new JFormattedTextField();
		TF_pct_appswitch.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_appswitch.setToolTipText("应用启动事件");
		contentPane.add(TF_pct_appswitch, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_pct_anyevent = new JLabel("pct-anyevent");
		contentPane.add(lbl_pct_anyevent, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_pct_anyevent = new JFormattedTextField();
		TF_pct_anyevent.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_pct_anyevent.setToolTipText("其他未提及事件。该事件可能包含其他上述事件。");
		contentPane.add(TF_pct_anyevent, gridBox.autoGridX().setGridWH(gridW, 1));
		// 分析设置
		JLabel lbl_analysis = new JLabel("分析设置");
		lbl_analysis.setForeground(Color.BLUE);
		contentPane.add(lbl_analysis, gridBox.resetGridX().autoGridY().resetGridWH());

		JLabel lbl_analysis_showduplicate = new JLabel("显示重复CRASH");
		contentPane.add(lbl_analysis_showduplicate, gridBox.resetGridX().autoGridY().resetGridWH());

		check_analysis_showduplicate = new JCheckBox("");
		contentPane.add(check_analysis_showduplicate, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_analysis_show = new JLabel("显示行数");
		contentPane.add(lbl_analysis_show, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_analysis_show = new JFormattedTextField();
		TF_analysis_show.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_analysis_show.setToolTipText("每个CRASH信息显示的行数");
		contentPane.add(TF_analysis_show, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_analysis_arow = new JLabel("分析行数");
		contentPane.add(lbl_analysis_arow, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_analysis_arow = new JFormattedTextField();
		TF_analysis_arow.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_analysis_arow.setToolTipText("分析Crash日志的行数");
		contentPane.add(TF_analysis_arow, gridBox.autoGridX().setGridWH(gridW, 1));

		JLabel lbl_analysis_arowword = new JLabel("每行分析字数");
		contentPane.add(lbl_analysis_arowword, gridBox.resetGridX().autoGridY().resetGridWH());

		TF_analysis_arowword = new JFormattedTextField();
		TF_analysis_arowword.addKeyListener(HelperUtil.Listener_inputNumbers());
		TF_analysis_arowword.setToolTipText("每行需求分析的字数");
		contentPane.add(TF_analysis_arowword, gridBox.autoGridX().setGridWH(gridW, 1));

		return contentPane;
	}
}
