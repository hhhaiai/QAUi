package com.GetScreen;

import java.io.File;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Viewer.MainRun;

public class ScreenEdit {
	Logger logger = LoggerFactory.getLogger(ScreenEdit.class);

	// use picture edit by windows
	public void EditByWin(String udid, String filepath) {
		if (filepath.endsWith(".png")) {
			File file = new File(filepath);
			if (file.exists()) {
				MainRun.mainFrame.progressBarmain.setValue(10);// ******************
				if (MainRun.OStype == 0) {
					Excute.execcmd(udid, "start mspaint.exe " + filepath, 1, true);
				} else {
					MainRun.mainFrame.progressBarmain.setValue(0);// ******************
					JOptionPane.showMessageDialog(null, "本地图片编辑仅支持Windows系统!", "消息", JOptionPane.ERROR_MESSAGE);
				}
				MainRun.mainFrame.progressBarmain.setValue(100);// ******************
			}
		} else {
			logger.info("pls save image first! " + filepath);
			JOptionPane.showMessageDialog(null, "请先保存图片!", "消息", JOptionPane.ERROR_MESSAGE);
		}
	}

}
