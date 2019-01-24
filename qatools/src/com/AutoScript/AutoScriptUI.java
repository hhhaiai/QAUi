package com.AutoScript;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class AutoScriptUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 588811437968763335L;

	public AutoScriptUI(String udid) {
		// TODO Auto-generated constructor stub
		setBounds(100, 100, 750, 550);
		setResizable(false);
		setTitle("Android脚本录制 udid=" + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		AutoScriptUImain autoScriptUImain = new AutoScriptUImain(udid);
		setContentPane(autoScriptUImain);
	}
}
