package com.Monkey;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class MonkeyUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7183221673326862243L;

	public MonkeyUI(String udid) {
		// TODO Auto-generated constructor stub
		setBounds(100, 100, 750, 550);
		setResizable(false);
		setTitle("Android Monkey udid=" + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		MonkeyUImain monkeyUImain = new MonkeyUImain(udid);
		setContentPane(monkeyUImain);
	}
}
