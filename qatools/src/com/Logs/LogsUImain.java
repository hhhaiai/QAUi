package com.Logs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.StringUtil;

public class LogsUImain extends JPanel {
	/**
	* 
	*/
	private static final long serialVersionUID = 7850223885612598015L;
	Logger logger = LoggerFactory.getLogger(LogsUImain.class);
	JComboBox<String> deviceslist; //
	JLabel lblDevicestatus;//
	JLabel lblDeviceInfo;//
	JCheckBox chckbxdel;
	boolean dellog = true;
	LogsActive logsActive = new LogsActive();
	LogsStop logsStop = new LogsStop();

	LogshappentimeUI happentimeUI;
	LogsUImain logsUImain = this;
	String udid;
	String logstatus = StringUtil.Logs_Nodeivce;

	/**
	 * Create the panel.
	 */
	public LogsUImain(String udid) {
		this.udid = udid;
		setSize(750, 200);
		setLocation(0, 0);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		happentimeUI = new LogshappentimeUI(udid);
		// 激活按钮
		JButton btnActive = new JButton("激活");
		btnActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// acivelogthreadrun true
				if (logsActive.getActivelogthreadrun()) {
					logger.info("activelogthreadrun =true");
					JOptionPane.showMessageDialog(logsUImain, "QATools正在努力工作中,请稍后再试...", "Message",
							JOptionPane.ERROR_MESSAGE);
					logger.info("active log button running");
					return;
				}
				// check log status
				if (logstatus.equals(StringUtil.Logs_NotRun)) {
					if (logsActive.checklogfolder()) {
						logsStop.del();
						logsActive.start(udid);
					} else {
						logsActive.start(udid);
					}
				} else if (logstatus.equals(StringUtil.Logs_Run)) {
					logger.info("Logs are running");
					int confirm = JOptionPane.showConfirmDialog(logsUImain, "检测到日记已在运行,是否继续?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logsActive.start(udid);
					} else {
						return;
					}
				} else if (logstatus.equals(StringUtil.Logs_Nodeivce)) {
					logger.info("No devices checked");
					JOptionPane.showMessageDialog(logsUImain, "未检测到设备!", "Message", JOptionPane.ERROR_MESSAGE);
				} else if (logstatus.equals(StringUtil.Logs_RepeatRun)) {
					logger.info("Active Repeat!");
					JOptionPane.showMessageDialog(logsUImain, "检测到日记被重复激活!", "Message", JOptionPane.ERROR_MESSAGE);
				}
				logger.info("active log button");
			}
		});
		btnActive.setBounds(45, 55, 100, 25);
		add(btnActive);

		// 提取按钮
		JButton btnGet = new JButton("提取");
		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				happentimeUI.updatetime();
				happentimeUI.setVisible(true);
				logger.info("qcom get log button");
			}
		});
		btnGet.setBounds(45, 89, 100, 25);
		add(btnGet);

		// 停止按钮
		JButton btnStop = new JButton("停止");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (logsStop.getstoplogthreadrun()) {
					logger.info("stoplogthreadrun =true");
					JOptionPane.showMessageDialog(logsUImain, "QATools正在努力工作中,请稍后再试...", "Message",
							JOptionPane.ERROR_MESSAGE);
					logger.info("stop and del log button1");
					return;
				}
				// check devices
				if (!CheckUE.checkDevice(udid)) {
					JOptionPane.showMessageDialog(logsUImain, "未检测到设备!", "Messge", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!dellog) {
					int confirm = JOptionPane.showConfirmDialog(logsUImain, "是否停止运行日记?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logsStop.run(udid, dellog);
					} else {
						return;
					}

				} else {
					int confirm = JOptionPane.showConfirmDialog(logsUImain, "是否停止并删除日记?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logsStop.run(udid, dellog);
					} else {
						return;
					}
				}
				logger.info("stop and del log button2");
			}
		});
		btnStop.setBounds(45, 126, 100, 25);
		add(btnStop);
		// 删除复选框
		chckbxdel = new JCheckBox("并删除");
		chckbxdel.setSelected(true);
		chckbxdel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxdel.isSelected()) {
					dellog = true;
				} else {
					dellog = false;
				}
			}
		});
		chckbxdel.setBounds(155, 125, 95, 23);
		add(chckbxdel);

		// 崩溃日记按钮
		JButton btnCatCrash = new JButton("崩溃日记");
		btnCatCrash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LogsCatCrashUI logsCatCrashUI = new LogsCatCrashUI(udid);
				logsCatCrashUI.setVisible(true);

				logger.info("btnCatCrash button");
			}
		});
		btnCatCrash.setBounds(334, 55, 100, 25);
		add(btnCatCrash);

		// 检查LOGCAT日记,高亮指定字符串
		JButton btnCheckLogs = new JButton("高亮日记");
		btnCheckLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LogsCheckLogsUI logsCheckLogsUI = new LogsCheckLogsUI(udid);
				logsCheckLogsUI.setVisible(true);

				logger.info("btnCheckLogs button");
			}
		});
		btnCheckLogs.setBounds(334, 89, 100, 25);
		add(btnCheckLogs);

		JLabel lblOthers = new JLabel("日记扩展");
		lblOthers.setBounds(284, 30, 54, 15);
		add(lblOthers);

		// 清除应用数据
		JButton btnClearApp = new JButton("清除应用数据");
		btnClearApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logger.info("press btnclearapp button");
				LogsHelper logsHelper = new LogsHelper();
				JOptionPane.showMessageDialog(logsUImain, "清除应用数据:" + logsHelper.clearApp(udid), "Messge",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnClearApp.setBounds(543, 55, 133, 25);
		add(btnClearApp);

		JLabel lblhelp = new JLabel("辅助功能");
		lblhelp.setBounds(494, 30, 54, 15);
		add(lblhelp);

		JLabel lbl_logstatus = new JLabel("Logcat状态: ");
		lbl_logstatus.setBounds(6, 27, 150, 16);
		add(lbl_logstatus);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				logstatus = CheckUE.checklog(udid);
				lbl_logstatus.setText("<html>Logcat状态: " + logstatus + "</html>");
			}
		}, 500, 800);
	}
}
