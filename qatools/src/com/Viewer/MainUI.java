package com.Viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.AutoScript.AutoScriptUI;
import com.DataHandel.DataHandelMainFXUI;
import com.GetScreen.GetScreenUI;
import com.IOSGetScreen.IOSGetScreenUI;
import com.IOSLogs.IOSLogsUI;
import com.Logs.LogsUI;
import com.Monkey.MonkeyUI;
import com.More.SysConfigBoxUI;
import com.Performance.PerformanceUI;
import com.PicContrast.PicContrastUI;
import com.PicInspect.PicInspectMainFXUI;
import com.Util.AndroidInfo;
import com.constant.Cconfig;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class MainUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -392596953740704076L;
	Logger logger = LoggerFactory.getLogger(MainUI.class);
	private JPanel contentPane;
	JComboBox<String> deviceslist; // 设备列表
	JLabel lblDevicestatus;// 设备状态显示
	JLabel lblUItitle;
	// JLabel lblBarstatus;//bar
	public JProgressBar progressBarmain;

	long itemchange = 0;

	boolean performanceUIload = false;

	/**
	 * Create the frame.
	 */
	public MainUI() {
		// 窗体参数
		ImageIcon imagetemp = new ImageIcon(getClass().getResource("/Resources/logo.jpg"));
		com.Viewer.MainRun.imagelogo = imagetemp.getImage();
		setResizable(false);
		setTitle("QATools " + com.Viewer.MainRun.Version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 600);
		getContentPane().setLayout(null);
		contentPane = new JPanel();
		contentPane.setLocation(0, 0);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setSize(750, 556);
		setIconImage(com.Viewer.MainRun.imagelogo);
		getContentPane().add(contentPane, BorderLayout.CENTER);

		// 设备列表
		deviceslist = new JComboBox<String>();
		deviceslist.setBounds(54, 37, 540, 45);
		// deviceslist.addActionListener(new ActionListener(){
		// public void actionPerformed(ActionEvent e) {
		// // deviceslist = (JComboBox)e.getSource();
		// String str = (String)deviceslist.getSelectedItem();
		// mainRun.selectedID=str;
		// com.Main.ThenToolsRun.logger.log(Level.INFO,"select devices ID="+str);
		// }
		// });
		// devices list
		deviceslist.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getStateChange() == 1) { // 插入
					if (System.currentTimeMillis() - itemchange < 1000) {
						// itemchange=System.currentTimeMillis();
						deviceslist.setSelectedIndex(0);
						JOptionPane.showMessageDialog(MainRun.mainFrame, "请不要快速切换设备!", "消息", JOptionPane.ERROR_MESSAGE);
						//
						String str = (String) deviceslist.getSelectedItem();
						MainRun.selectedID = str;
						if (MainRun.selectedID.equals("")) {
							MainRun.selectedID = null;
						}
						logger.info("plug in ID=" + str + " 1000");
						itemchange = System.currentTimeMillis();
					} else {
						String str = (String) deviceslist.getSelectedItem();
						MainRun.selectedID = str;
						if (MainRun.selectedID.equals("")) {
							MainRun.selectedID = null;
						}
						logger.info("plug in devices ID=" + str);
						itemchange = System.currentTimeMillis();
					}
					if (MainRun.selectedID != null) {
						if (MainRun.selectedID.length() > 35) {
							MainRun.selectedOS = Cconfig.IOS;
						} else {
							MainRun.selectedOS = Cconfig.ANDROID;
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									// adb shell 初始化
									AndroidInfo.setPSversion(MainRun.selectedID);
									AndroidInfo.setPMversion(MainRun.selectedID);
									AndroidInfo.setTOPversion(MainRun.selectedID);
								}
							}).start();
						}
					} else {
						MainRun.selectedOS = "";
					}
				} else {// 拔出
					logger.info("cancel one devices");
				}
			}

		});
		contentPane.add(deviceslist);

		JLabel lblDevicesList = new JLabel("设备列表:");
		lblDevicesList.setBounds(10, 10, 148, 15);
		contentPane.add(lblDevicesList);

		lblDevicestatus = new JLabel("未检测到设备...");
		lblDevicestatus.setBounds(54, 94, 534, 45);
		contentPane.add(lblDevicestatus);
		lblDevicestatus.setVerticalAlignment(SwingConstants.TOP);

		// Progressbar
		progressBarmain = new JProgressBar();
		progressBarmain.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		progressBarmain.setBounds(110, 143, 484, 30);
		contentPane.add(progressBarmain);
		progressBarmain.setStringPainted(true);
		progressBarmain.setMinimum(0);
		progressBarmain.setMaximum(100);

		JLabel lblPragressbar = new JLabel("进度条:");
		lblPragressbar.setBounds(54, 148, 85, 25);
		contentPane.add(lblPragressbar);

		// lblBarstatus = new JLabel("");
		// lblBarstatus.setBounds(495, 33, 182, 25);
		// contentPane.add(lblBarstatus);
		// lbl open log
		JLabel lbldebug = new JLabel("");
		lbldebug.setBounds(54, 188, 600, 323);
		contentPane.add(lbldebug);
		lbldebug.setIcon(new ImageIcon(getClass().getResource("/Resources/biglogo.png")));
		lbldebug.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseClicked(MouseEvent arg0) {

			}
		});
		progressBarmain.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				int value = progressBarmain.getValue();
				if (arg0.getSource() == progressBarmain) {
					if (value == 0) {
						progressBarmain.setBackground(Color.RED);
						// lblBarstatus.setText("<html><font color=\"#FF0000\">失败!</font></html>");
					} else if (value == 100) {
						progressBarmain.setBackground(new Color(238, 238, 238));
						progressBarmain.setForeground(new Color(51, 153, 255));
						// lblBarstatus.setText("<html><font color=\"#09F7F7\">完成!</font></html>");
					} else if (value == 10) {
						progressBarmain.setBackground(new Color(238, 238, 238));
						progressBarmain.setForeground(new Color(51, 153, 255));
						// lblBarstatus.setText("努力中...");
					}
				}
			}

		});

		// 菜单栏
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// Android下拉菜单
		JMenu mnAndroid = new JMenu("Android");
		menuBar.add(mnAndroid);

		JMenuItem mntmLogs = new JMenuItem("日志");
		mntmLogs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				logger.info("go to logs_UImain by menu");
				if (MainRun.selectedOS.equals(Cconfig.ANDROID)) {
					LogsUI logsUI = new LogsUI(MainRun.selectedID);
					logsUI.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(contentPane, "仅Android设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
				}
			}

		});

		JMenuItem mntmGetscreen = new JMenuItem("屏幕获取");
		mntmGetscreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				logger.info("go to getscreen_UImain by menu");
				if (MainRun.selectedOS.equals(Cconfig.ANDROID)) {
					GetScreenUI getScreenUI = new GetScreenUI(MainRun.selectedID);
					getScreenUI.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(contentPane, "仅Android设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		JMenuItem mntmMonkey = new JMenuItem("Monkey");
		mntmMonkey.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				logger.info("go to MonkeyUImain by menu");
				if (MainRun.selectedOS.equals(Cconfig.ANDROID)) {
					MonkeyUI monkeyUI = new MonkeyUI(MainRun.selectedID);
					monkeyUI.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(contentPane, "仅Android设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
				}
			}

		});

		JMenuItem mntmAutoScript = new JMenuItem("自动化录制");
		mntmAutoScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				logger.info("go to autoscriptUImain by menu");
				if (MainRun.selectedOS.equals(Cconfig.ANDROID)) {
					AutoScriptUI autoScriptUI = new AutoScriptUI(MainRun.selectedID);
					autoScriptUI.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(contentPane, "仅Android设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
				}
			}

		});

		JMenuItem mntmPerformance = new JMenuItem("性能监控");
		mntmPerformance.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				logger.info("go to performance by menu");
				if (MainRun.selectedOS.equals(Cconfig.ANDROID)) {
					PerformanceUI performanceUI = new PerformanceUI(MainRun.selectedID);
					performanceUI.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(contentPane, "仅Android设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
				}
//				if (!performanceUIload) {
//					performanceUIload = true;
//					Application.launch(PerformanceFXUIAPP.class, new String[] { "--device=" + MainRun.selectedID });//Application launch must not be called more than once
//				} else {
//
//				}
			}
		});
		//
		JFXPanel jfxPanel = new JFXPanel();
		jfxPanel.setBounds(0, 0, 5, 5);
		getContentPane().add(jfxPanel);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				jfxPanel.setScene(new Scene(new AnchorPane()));
			}
		});

		// IOS下拉菜单
		JMenu mnIos = new JMenu("IOS");
		menuBar.add(mnIos);

		JMenuItem mntmIOSLogs = new JMenuItem("日志");
		mntmIOSLogs.addActionListener(e -> {
			logger.info("go to mntmIOSLogs by menu");
			if (MainRun.selectedOS.equals(Cconfig.IOS)) {
				IOSLogsUI logsUI = new IOSLogsUI(MainRun.selectedID);
				logsUI.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(contentPane, "仅iOS设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
			}
		});
		JMenuItem menuIOSGetscreen = new JMenuItem("屏幕获取");
		menuIOSGetscreen.addActionListener(e -> {
			logger.info("go to menuIOSGetscreen by menu");
			if (MainRun.selectedOS.equals(Cconfig.IOS)) {
				IOSGetScreenUI iosGetScreenUI = new IOSGetScreenUI(MainRun.selectedID);
				iosGetScreenUI.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(contentPane, "仅iOS设备才能使用", "警告", JOptionPane.WARNING_MESSAGE);
			}
		});

		// 辅助下拉菜单
		JMenu mnAssist = new JMenu("辅助");
		menuBar.add(mnAssist);

		JMenuItem mntmPicContrast = new JMenuItem("图片对比");
		mntmPicContrast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				logger.info("go to mntmPicContrast by menu");
				PicContrastUI picContrastUI = new PicContrastUI();
				picContrastUI.setVisible(true);
			}

		});

		JMenuItem mntmPicInspect = new JMenuItem("图片查看器");
		mntmPicInspect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				logger.info("go to mntmPicInspect by menu");
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						PicInspectMainFXUI picInspectMainFXUI = new PicInspectMainFXUI();
						picInspectMainFXUI.show();
					}
				});
			}

		});

		JMenuItem mntmDataHandel = new JMenuItem("数据分析");
		mntmDataHandel.addActionListener(e -> {
			logger.info("go to mntmDataHandel by menu");
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					DataHandelMainFXUI dataHandelMainFXUI = new DataHandelMainFXUI(MainRun.selectedID);
					dataHandelMainFXUI.show();
				}
			});

		});
		// 更多下拉菜单
		JMenu mnMore = new JMenu("更多");
		menuBar.add(mnMore);
		JMenuItem menuConfig = new JMenuItem("设置");
		menuConfig.addActionListener(e -> {
			SysConfigBoxUI sysConfigBoxUI = new SysConfigBoxUI();
			sysConfigBoxUI.setVisible(true);
		});
		JMenuItem menuAbout = new JMenuItem("关于");
		menuAbout.addActionListener(e -> {
			JOptionPane.showMessageDialog(contentPane, "QATools " + MainRun.Version, "Message",
					JOptionPane.INFORMATION_MESSAGE);
		});

		JMenuItem menuTest = new JMenuItem("测试勿点");
		menuTest.addActionListener(e -> {
			// Test.run();
			JOptionPane.showMessageDialog(contentPane, "测试完成", "Message", JOptionPane.INFORMATION_MESSAGE);
		});

		// android菜单排序
		mnAndroid.add(mntmLogs);
		mnAndroid.add(mntmGetscreen);
		mnAndroid.add(mntmMonkey);
		mnAndroid.add(mntmAutoScript);
		mnAndroid.add(mntmPerformance);
		// IOS菜单排序
		mnIos.add(mntmIOSLogs);
		mnIos.add(menuIOSGetscreen);
		// 辅助菜单排序
		mnAssist.add(mntmPicContrast);
		mnAssist.add(mntmDataHandel);
		mnAssist.add(mntmPicInspect);
		// 更多菜单排序
		// mnMore.add(menuTest);
		mnMore.add(menuConfig);
		mnMore.add(menuAbout);

	}

	// 设备列表按钮
	public JComboBox<String> getDeviceslist() {
		return deviceslist;
	}

	// 设备状态
	public JLabel getDevicestatus() {
		return lblDevicestatus;
	}

	/**
	 * 界面切换
	 * 
	 * @param name
	 */
//	private void UIchange(String name) {
//		if (iosLogsUImain == null) {
//			iosLogsUImain = new IOSLogsUImain();
//			getContentPane().add(iosLogsUImain);
//			UIlist.put(Cconfig.iosLogsUImain, iosLogsUImain);
//		}
//		UIchange(Cconfig.iosLogsUImain);
//		for (Entry<String, JPanel> entry : UIlist.entrySet()) {
//			if (entry.getKey().equals(name)) {
//				if (entry.getValue() != null)
//					entry.getValue().setVisible(true);
//			}
//		}
//		for (Entry<String, JPanel> entry : UIlist.entrySet()) {
//			if (!entry.getKey().equals(name)) {
//				if (entry.getValue() != null)
//					entry.getValue().setVisible(false);
//			}
//		}
//		// 辅助
//		lblUItitle.setText(name + ":");
//		contentPane.setVisible(true);
//	}
}
