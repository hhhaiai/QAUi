package com.Monkey;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.StringUtil;
import com.Viewer.MainRun;

public class MonkeyMonitorUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3997906954775152472L;
	Logger logger = LoggerFactory.getLogger(MonkeyMonitorUI.class);
	private JPanel contentPane;
	JTextArea textAreaShowlog;
	JScrollPane scrollPaneTxt;
	// MonkeyAnalysisFile monkeyanalysisfile=new MonkeyAnalysisFile();
	MonkeyAnalysisTEMP monkeyanalysis;
	MonkeyMonitorSettingsUI monkeymonitorsetUI = new MonkeyMonitorSettingsUI();
	MonkeylogUI monkeylogUI;
	JLabel lblLogpath;
	private JButton btnAnalysis;
	String logfilepath;
	private JLabel lblSelect;

	boolean isstart = false;
	JButton btnActive;
	MonkeyUImain monkeyUImain;
	MonkeyMonitor monkeymonitor;
	MonkeyStop monkeystop;

	/**
	 * Create the frame.
	 */
	public MonkeyMonitorUI(MonkeyUImain monkeyUImain) {
		setBounds(100, 100, 750, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("Monkey监控");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);
		this.monkeyUImain = monkeyUImain;
		monkeymonitor = new MonkeyMonitor(monkeyUImain);
		monkeystop = new MonkeyStop(monkeyUImain);
		monkeyanalysis = new MonkeyAnalysisTEMP(monkeyUImain);
		monkeylogUI = new MonkeylogUI(monkeyUImain.udid);
		// button active
		btnActive = new JButton("激活");
		btnActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isstart) {
					// device null
					if (MainRun.selectedID == null) {
						JOptionPane.showMessageDialog(contentPane, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("monkey monitor active button: no devices");
						return;
					}
					// running
					if (monkeymonitor.getActiveMonkeythreadrun()) {
						logger.info("getActiveMonkeythreadrun =true");
						JOptionPane.showMessageDialog(contentPane, "QATools正在努力工作中,请稍后再试...", "消息",
								JOptionPane.ERROR_MESSAGE);
						logger.info("monkey monitor active button: running");
						return;
					}
					if (monkeyUImain.getMonkeyInfo()[3] == null) {
						JOptionPane.showMessageDialog(monkeyUImain, "请选择一个应用!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("monkey monitor active button");
						return;
					}
					if (CheckUE.checkMonkeyrun(monkeyUImain.udid)) {
						MainRun.mainFrame.progressBarmain.setValue(0);// ******************
						JOptionPane.showMessageDialog(monkeyUImain, "Monkey监控正在运行中...", "消息",
								JOptionPane.ERROR_MESSAGE);
						logger.info("Monkey Monitor is running!");
						return;
					}
					// check sim
					if (CheckUE.checkSIMstatus(monkeyUImain.udid)) {
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
					if (!CheckUE.checklog(monkeyUImain.udid).equals(StringUtil.Logs_Run)) {
						int confirm = JOptionPane.showConfirmDialog(monkeyUImain, "未检测到日记运行,是否继续?", "确认",
								JOptionPane.YES_NO_OPTION);
						if (confirm == 0) {
							logger.info("UE has no log and continue");
						} else {
							MainRun.mainFrame.progressBarmain.setValue(0);// ******************
							logger.info("UE has no log and not to continue");
							return;
						}
					}
					monkeymonitor.run(monkeyUImain.getMonkeyInfo(), monkeymonitorsetUI.getIsreconnect());
					// change
					isstart = true;
					btnActive.setText("停止");
					btnActive.setForeground(Color.RED);
					logger.info("monkey monitor active button");
				} else {
					int confirm = JOptionPane.showConfirmDialog(contentPane, "是否停止Monkey?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						monkeystop.run();
						monkeymonitor.cancelbatterytimer();
						monkeymonitor.setTerminated(true);
						// change
						isstart = false;
						btnActive.setText("激活");
						btnActive.setForeground(Color.BLACK);
					} else {
						logger.info("monkey monitor stop button: no");
						return;
					}
					logger.info("monkey monitor stop button: yes");
				}
			}
		});
		btnActive.setBounds(634, 457, 100, 25);
		contentPane.add(btnActive);

		// textAreaShowlog
		textAreaShowlog = new JTextArea("");
		textAreaShowlog.setWrapStyleWord(true);
		textAreaShowlog.setLineWrap(true);
		scrollPaneTxt = new JScrollPane(textAreaShowlog);
		scrollPaneTxt.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneTxt.setBounds(10, 35, 724, 358);
		contentPane.add(scrollPaneTxt);
		// 行号
//		LineNumberHeaderView lineNumberHeader = new LineNumberHeaderView();
//		scrollPaneTxt.setRowHeaderView(lineNumberHeader);
		// log path lable
		lblLogpath = new JLabel("---");
		lblLogpath.setBounds(71, 10, 612, 15);
		contentPane.add(lblLogpath);

		// Analysis button
		btnAnalysis = new JButton("分析");
		btnAnalysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// no selected
				if (lblSelect.getText().equals("---")) {
					logger.info("no selected file to analysis");
					JOptionPane.showMessageDialog(contentPane, "请先选择一个文件...", "消息", JOptionPane.ERROR_MESSAGE);
					logger.info("monkey monitor analysis button: no file selected");
					return;
				}
				// running
				if (monkeyanalysis.getmonkeyanalysisthreadrun()) {
					logger.info("getmonkeyanalysisthreadrun =true");
					JOptionPane.showMessageDialog(contentPane, "QATools正在努力工作中,请稍后再试...", "消息",
							JOptionPane.ERROR_MESSAGE);
					logger.info("monkey monitor analysis button: running");
					return;
				} else {
					monkeyanalysis.run(monkeymonitorsetUI.getArow(), monkeymonitorsetUI.getArowword(),
							monkeymonitorsetUI.getShowduplicate());
				}
				logger.info("monkey monitor analysis button");
			}
		});
		btnAnalysis.setBounds(53, 492, 100, 25);
		contentPane.add(btnAnalysis);
		// lbl select string
		lblSelect = new JLabel("---");
		lblSelect.setVerticalAlignment(SwingConstants.TOP);
		lblSelect.setBounds(71, 403, 652, 44);
		contentPane.add(lblSelect);

		// analysis Settings
		JButton btnSettings = new JButton("设置");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				monkeymonitorsetUI.initvalue();
				monkeymonitorsetUI.setVisible(true);
				logger.info("monkey monitor settings button");
			}
		});
		btnSettings.setBounds(176, 492, 100, 25);
		contentPane.add(btnSettings);

		// select
		JButton btnSelect = new JButton("选择文件");
		btnSelect.setToolTipText("可以选择多个文件一起分析");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String latestlog = lblLogpath.getText();
				if (latestlog.length() > 4) {
					latestlog = latestlog.substring(3, latestlog.length());
				} else {
					latestlog = "";
				}
				File[] selectfile = monkeyanalysis.selectfile(latestlog);
				if (selectfile != null) {
					if (selectfile.length > 1) {
						StringBuffer showfile = new StringBuffer();
						showfile.append(selectfile[0].getParent() + "/<font color=\"#FF0000\" size=10>"
								+ selectfile.length + "</font> files selected");
						lblSelect.setText("<html>" + showfile.toString() + "</html>");
					} else if (selectfile.length == 1) {
						lblSelect.setText(selectfile[0].getAbsolutePath());
					}
				}
				logger.info("monkey monitor select folder button");
			}
		});
		btnSelect.setBounds(53, 457, 100, 25);
		contentPane.add(btnSelect);
		// lbl filpath
		JLabel lblLogFilePath = new JLabel("Log路径:");
		lblLogFilePath.setBounds(10, 10, 91, 15);
		contentPane.add(lblLogFilePath);
		// lbl select files
		JLabel lblSelectFiles = new JLabel("分析文件:");
		lblSelectFiles.setBounds(10, 403, 62, 15);
		contentPane.add(lblSelectFiles);

		// notes
//		JLabel lblNotes = new JLabel(getString("lblNotes"));
//		lblNotes.setBounds(580, 497, 164, 20);
//		contentPane.add(lblNotes);

		// battery button
		JButton btnBattery = new JButton("查看电量");
		btnBattery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = monkeymonitor.getstrbattery();
				if (str != null && !str.equals("")) {
					int count = 0;
					String[] splitstr = str.split("\n");
					for (String line : splitstr) {
						if (!line.contains("not run")) {
							count++;
						}
					}
					double time = count * 0.5;
					JOptionPane.showMessageDialog(contentPane,
							"Monkey总共运行约" + time + "小时.\n" + monkeymonitor.getstrbattery(), "消息",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(contentPane, "Pls active first!", "消息",
							JOptionPane.INFORMATION_MESSAGE);
				}
				logger.info("monkey monitor battery button");
			}
		});
		btnBattery.setBounds(176, 457, 100, 25);
		contentPane.add(btnBattery);

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				logger.info("windows closing");
				monkeymonitor.cancelbatterytimer();
				monkeymonitor.setTerminated(true);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	// get textAreaShowlog
	public JTextArea gettextAreaShowlog() {
		return textAreaShowlog;
	}

	// get lable log path
	public JLabel getlblLogpath() {
		return lblLogpath;
	}

	// get lable select
	public JLabel getlblSelect() {
		return lblSelect;
	}

}
