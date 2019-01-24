package com.IOSGetScreen;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class IOSGetScreenUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6555557362946543143L;

	public IOSGetScreenUI(String udid) {
		// TODO Auto-generated constructor stub
		setBounds(100, 100, 750, 550);
		setResizable(false);
		setTitle("iOS屏幕获取 udid=" + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		IOSGetScreenUImain getScreenUImain = new IOSGetScreenUImain(udid);
		setContentPane(getScreenUImain);
	}
}
