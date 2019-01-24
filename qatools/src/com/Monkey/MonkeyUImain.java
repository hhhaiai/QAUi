package com.Monkey;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.StringUtil;
import com.Viewer.MainRun;

public class MonkeyUImain extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1636961156386366466L;
	Logger logger = LoggerFactory.getLogger(MonkeyUImain.class);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("ssSSS");
	JRadioButton rdbtnCustomizeMonkey;
	JRadioButton rdbtnPackageMonkey;
	JButton btnResetCustomize;
	JFormattedTextField formattedTextFieldTime;
	JFormattedTextField formattedTextFieldSeed;
	String monkeyradio = "Packages";
	JTextArea textAreaCustomize;
	MonkeyActive monkeyacitve = new MonkeyActive(this);
	MonkeyStop monkeystop = new MonkeyStop(this);
	MonkeyPackage monkeypackage = new MonkeyPackage();
	MonkeyMonitorUI monkeymonitorui = new MonkeyMonitorUI(this);
	JScrollPane scrollPaneTxt;

	MonkeylogUI monkeylogUI;

	String packageSelected;
	JComboBox<String> packagelist;
	MonkeyUImain monkeyUImain;
	String udid;

	/**
	 * Create the panel.
	 */
	public MonkeyUImain(String udid) {
		this.udid = udid;
		setSize(740, 500);
		setLocation(0, 100);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		monkeyUImain = this;
		monkeylogUI = new MonkeylogUI(udid);
		// 安装应用列表
		packagelist = new JComboBox<String>();
		packagelist.setBounds(32, 35, 356, 25);
		packagelist.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == 1) { // 插入
					// 过滤选中项
					String[] tempsplit = packagelist.getSelectedItem().toString().split("=");
					packageSelected = tempsplit[2].substring(0, tempsplit[2].length() - 7);
					logger.info("select monkey package=" + packageSelected);
				} else {// 拔出
					packageSelected = null;
					logger.info("select monkey package=null");
				}
			}
		});
		;
		add(packagelist);

		// Active
		JButton btnActive = new JButton("激活");
		btnActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// device null
				if (!CheckUE.checkDevice(udid)) {
					JOptionPane.showMessageDialog(monkeyUImain, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
					logger.info("monkey active button: no devices");
					return;
				}
				// running
				if (monkeyacitve.getActiveMonkeythreadrun()) {
					logger.info("getActiveMonkeythreadrun =true");
					JOptionPane.showMessageDialog(monkeyUImain, "QATools正在努力工作中,请稍后再试...", "消息",
							JOptionPane.ERROR_MESSAGE);
					logger.info("monkey active button: running");
					return;
				}
				// time null
				if (formattedTextFieldTime.getText().equals("")) {
					JOptionPane.showMessageDialog(monkeyUImain, "间隔时间应该在500毫秒到5000毫秒之间.", "消息",
							JOptionPane.ERROR_MESSAGE);
					logger.info("monkey active button: time=0");
					return;
				}
				// time
				if (Long.parseLong(formattedTextFieldTime.getText()) > 5000
						|| Long.parseLong(formattedTextFieldTime.getText()) < 500) {
					JOptionPane.showMessageDialog(monkeyUImain, "间隔时间应该在500毫秒到5000毫秒之间.", "消息",
							JOptionPane.ERROR_MESSAGE);
					logger.info("monkey active button: time >5000 or <500");
					return;
				}
				// check monkey run
				if (CheckUE.checkMonkeyrun(udid)) {
					MainRun.mainFrame.progressBarmain.setValue(0);// ******************
					JOptionPane.showMessageDialog(monkeyUImain, "Monkey正在运行!", "消息", JOptionPane.ERROR_MESSAGE);
					logger.info("Monkey is running!");
					return;
				}
				// check sim
				if (CheckUE.checkSIMstatus(udid)) {
					int confirm = JOptionPane.showConfirmDialog(monkeyUImain, "检测到设备有SIM卡,是否继续?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logger.info("UE has sim and continue");
					} else {
						MainRun.mainFrame.progressBarmain.setValue(0);// ******************
						logger.info("UE has sim and not to continue");
						return;
					}
				}
				// check log
				if (!CheckUE.checklog(udid).equals(StringUtil.Logs_Run)) {
					int confirm = JOptionPane.showConfirmDialog(monkeyUImain, "未检测到日记在运行,是否继续?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logger.info("UE has no log and continue");
					} else {
						MainRun.mainFrame.progressBarmain.setValue(0);// ******************
						logger.info("UE has no log and not to continue");
						return;
					}
				}
				// run
				StringBuffer packages = new StringBuffer();
				if (monkeyradio.equals("Packages")) {
					if (packageSelected == null || packageSelected.equals("")) {
						JOptionPane.showMessageDialog(monkeyUImain, "请选择一个应用进行Monkey测试.", "消息",
								JOptionPane.ERROR_MESSAGE);
						logger.info("Pls select packages to active monkey");
						return;
					}
					packages.append("-p " + packageSelected + " ");

				} else if (monkeyradio.equals("Customize")) {
					packages.append(textAreaCustomize.getText());
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_Customize", textAreaCustomize.getText());
				}
				monkeyacitve.run(formattedTextFieldSeed.getText(), formattedTextFieldTime.getText(), monkeyradio,
						packages.toString());
				logger.info("monkey active button");
			}
		});
		btnActive.setBounds(515, 101, 100, 25);
		add(btnActive);

		// Stop
		JButton btnStop = new JButton("停止");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// device null
				if (!CheckUE.checkDevice(udid)) {
					JOptionPane.showMessageDialog(monkeyUImain, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
					logger.info("monkey stop button: no devices");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(monkeyUImain, "是否停止Monkey?", "确认",
						JOptionPane.YES_NO_OPTION);
				if (confirm == 0) {
					monkeystop.run();
				} else {
					logger.info("monkey stop button: no");
					return;
				}
				logger.info("monkey stop button: yes");
			}
		});
		btnStop.setBounds(515, 136, 100, 25);
		add(btnStop);

		// 过滤包名设置
		JButton btn_FilterPackages = new JButton("应用设置");
		btn_FilterPackages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MonkeyFilterPackagesUI monkeyFilterPackagesUI = new MonkeyFilterPackagesUI(monkeyUImain);
				monkeyFilterPackagesUI.setVisible(true);
				logger.info("btn_FilterPackages button");
			}
		});
		btn_FilterPackages.setBounds(515, 206, 100, 25);
		add(btn_FilterPackages);

		// package monkey
		rdbtnPackageMonkey = new JRadioButton("安装应用");
		rdbtnPackageMonkey.setForeground(Color.RED);
		rdbtnPackageMonkey.setSelected(true);
		rdbtnPackageMonkey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				monkeyradio = "Packages";
				rdbtnPackageMonkey.setForeground(Color.RED);
				rdbtnCustomizeMonkey.setForeground(Color.BLACK);
				btnResetCustomize.setVisible(false);
				logger.info("select Packages option in MonkeyUI");
			}

		});
		rdbtnPackageMonkey.setBounds(6, 6, 149, 23);
		add(rdbtnPackageMonkey);

		// customize
		rdbtnCustomizeMonkey = new JRadioButton("自定义");
		rdbtnCustomizeMonkey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				monkeyradio = "Customize";
				rdbtnPackageMonkey.setForeground(Color.BLACK);
				rdbtnCustomizeMonkey.setForeground(Color.RED);
				btnResetCustomize.setVisible(true);
				logger.info("select Customize option in MonkeyUI");
			}

		});
		rdbtnCustomizeMonkey.setBounds(6, 89, 164, 23);
		add(rdbtnCustomizeMonkey);

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnPackageMonkey);
		group.add(rdbtnCustomizeMonkey);

		// Time intervals
		formattedTextFieldTime = new JFormattedTextField();
		formattedTextFieldTime.setText("1000");
		formattedTextFieldTime.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() >= KeyEvent.VK_0 && e.getKeyChar() <= KeyEvent.VK_9)
						|| e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB
						|| e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE
						|| e.getKeyChar() == KeyEvent.VK_LEFT || e.getKeyChar() == KeyEvent.VK_RIGHT
						|| e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					return;
				} else {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});

		formattedTextFieldTime.setBounds(568, 35, 80, 21);
		add(formattedTextFieldTime);
		// Seed
		formattedTextFieldSeed = new JFormattedTextField();
		formattedTextFieldSeed.setText(sDateFormat.format(new Date()));
		formattedTextFieldSeed.setBounds(568, 66, 80, 21);
		formattedTextFieldSeed.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() >= KeyEvent.VK_0 && e.getKeyChar() <= KeyEvent.VK_9)
						|| e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB
						|| e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE
						|| e.getKeyChar() == KeyEvent.VK_LEFT || e.getKeyChar() == KeyEvent.VK_RIGHT
						|| e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					return;
				} else {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(formattedTextFieldSeed);

		JLabel labelSeed = new JLabel("种子:");
		labelSeed.setBounds(515, 70, 50, 15);
		add(labelSeed);
		JLabel lblIntervals = new JLabel("时间间隔:                      ms");
		lblIntervals.setBounds(515, 38, 181, 15);
		add(lblIntervals);

		// customize txt
		/// textAreaCustomize = new
		// JTextArea(QAToolsRun.dbhandle.getSingleLineValue("MonkeyMonitorSettings",
		// "diymonkey"));
		textAreaCustomize = new JTextArea(com.Viewer.MainRun.paramsBean.getMonkey_Customize());
		textAreaCustomize.setWrapStyleWord(true);
		textAreaCustomize.setLineWrap(true);
		scrollPaneTxt = new JScrollPane(textAreaCustomize);
		scrollPaneTxt.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneTxt.setBounds(32, 118, 356, 78);
		add(scrollPaneTxt);
		// Monkey monitor
		JButton btnMonitor = new JButton("监控");
		// btnMonitor.setForeground(Color.RED);
		btnMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// time null
				if (formattedTextFieldTime.getText().equals("")) {
					JOptionPane.showMessageDialog(monkeyUImain, "间隔时间应该在500毫秒到5000毫秒之间.", "消息",
							JOptionPane.ERROR_MESSAGE);
					logger.info("monkey monitor button: time=0");
					return;
				}
				// time
				if (Long.parseLong(formattedTextFieldTime.getText()) > 5000
						|| Long.parseLong(formattedTextFieldTime.getText()) < 500) {
					JOptionPane.showMessageDialog(monkeyUImain, "间隔时间应该在500毫秒到5000毫秒之间.", "消息",
							JOptionPane.ERROR_MESSAGE);
					logger.info("monkey monitor button: time >5000 or <500");
					return;
				}
				monkeymonitorui.setVisible(true);
				logger.info("monkey monitor button");
			}
		});
		btnMonitor.setBounds(515, 171, 100, 25);
		add(btnMonitor);
		// clear Customize button
		btnResetCustomize = new JButton("重置");
		btnResetCustomize.setVisible(false);
		btnResetCustomize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String diystr = "-p 'package' -p 'package' --ignore-crashes --ignore-timeouts --ignore-security-exceptions --ignore-native-crashes --monitor-native-crashes";
				if (!textAreaCustomize.getText().equals(diystr)) {
					int confirm = JOptionPane.showConfirmDialog(monkeyUImain, "是否恢复默认自定义值?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						textAreaCustomize.setText(diystr);
						com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_Customize", diystr);
					} else {
						logger.info("no restore textAreaCustomize txt");
					}
				}
				logger.info("reset Customize button button");
			}
		});
		btnResetCustomize.setBounds(313, 206, 75, 25);
		add(btnResetCustomize);

		// setlist
		setlistpackageAPP();
	}

	// set listmode
	public void setlistpackageAPP() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				packagelist.removeAllItems();
				ArrayList<String> arrayAPP = monkeypackage.getPMlistAPP(udid);
				for (String str : arrayAPP) {
					packagelist.addItem(str);
				}
			}
		});
	}

	// getseed
	public JFormattedTextField getformattedTextFieldSeed() {
		return formattedTextFieldSeed;
	}

	// get monkeymonitorui
	public MonkeyMonitorUI getmonkeymonitorui() {
		return monkeymonitorui;
	}

	// get monkey info
	public String[] getMonkeyInfo() {
		String[] str = new String[4];
		str[0] = formattedTextFieldSeed.getText();
		str[1] = formattedTextFieldTime.getText();
		str[2] = monkeyradio;
		StringBuffer packages = new StringBuffer();
		if (monkeyradio.equals("Packages")) {
			if (packageSelected == null || packageSelected.equals("")) {
				logger.info("Pls select packages to active monkey");
				return str;
			}
			packages.append("-p " + packageSelected + " ");
		} else if (monkeyradio.equals("Customize")) {
			packages.append(textAreaCustomize.getText());
		}
		str[3] = packages.toString();
		return str;
	}
}
