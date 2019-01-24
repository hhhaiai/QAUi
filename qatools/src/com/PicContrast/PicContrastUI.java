package com.PicContrast;

import javax.swing.JFrame;

import com.Viewer.MainRun;

public class PicContrastUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2872915284319142802L;

	public PicContrastUI() {
		// TODO Auto-generated constructor stub
		setBounds(100, 100, 750, 550);
		setResizable(false);
		setTitle("图片对比");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		PicContrastUImain picContrastUImain = new PicContrastUImain();
		setContentPane(picContrastUImain);
	}
}
