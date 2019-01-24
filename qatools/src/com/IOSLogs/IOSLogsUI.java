package com.IOSLogs;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class IOSLogsUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3359534525662464273L;

	public IOSLogsUI(String udid) {
		// TODO Auto-generated constructor stub
		setBounds(100, 100, 750, 200);
		setResizable(false);
		setTitle("iOS日志工具 udid=" + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		IOSLogsUImain iosLogsUImain = new IOSLogsUImain(udid);
		setContentPane(iosLogsUImain);
	}
}
