package com.GetScreen;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class GetScreenUI extends JFrame {

	/**
	 * Create the frame.
	 */
	public GetScreenUI(String udid) {
		setBounds(100, 100, 750, 550);
		setResizable(false);
		setTitle("Android屏幕获取 udid=" + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		GetScreenUImain getScreenUImain = new GetScreenUImain(udid);
		setContentPane(getScreenUImain);
	}

}
