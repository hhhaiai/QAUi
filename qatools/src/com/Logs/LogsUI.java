package com.Logs;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class LogsUI extends JFrame {

	/**
	 * Create the frame.
	 */
	public LogsUI(String udid) {
		setBounds(100, 100, 750, 200);
		setResizable(false);
		setTitle("Android日志工具 udid=" + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		LogsUImain logsUImain = new LogsUImain(udid);
		setContentPane(logsUImain);
	}

}
