package com.IOSLogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOSLogsUImain extends JPanel {
	/**
	* 
	*/
	private static final long serialVersionUID = 7850223885612598015L;
	Logger logger = LoggerFactory.getLogger(IOSLogsUImain.class);
	String udid;

	/**
	 * Create the panel.
	 */
	public IOSLogsUImain(String udid) {
		this.udid = udid;
		setSize(740, 200);
		setLocation(0, 100);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);

		// 检查LOGCAT日记,高亮指定字符串
		JButton btnCheckSysLogs = new JButton("系统日记");
		btnCheckSysLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("btnCheckLogs button");
				LogsCheckSysLogsUI logsCheckLogsUI = new LogsCheckSysLogsUI(udid);
				logsCheckLogsUI.setVisible(true);
			}
		});
		btnCheckSysLogs.setBounds(60, 62, 100, 25);
		add(btnCheckSysLogs);

		JLabel lblOthers = new JLabel("日记扩展");
		lblOthers.setBounds(6, 6, 54, 15);
		add(lblOthers);

		JButton btnCheckAppLogs = new JButton("应用日记");
		btnCheckAppLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btnCheckAppLogs button");
				LogsCheckAppLogsUI logsCheckAppLogsUI = new LogsCheckAppLogsUI(udid);
				logsCheckAppLogsUI.setVisible(true);
			}
		});
		btnCheckAppLogs.setBounds(60, 25, 100, 25);
		add(btnCheckAppLogs);

	}
}
