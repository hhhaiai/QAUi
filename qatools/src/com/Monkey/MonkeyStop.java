package com.Monkey;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.AndroidInfo;
import com.Util.CheckUE;
import com.Viewer.MainRun;

public class MonkeyStop {
	Logger logger = LoggerFactory.getLogger(MonkeyStop.class);
	MonkeyUImain monkeyUImain;

	public MonkeyStop(MonkeyUImain monkeyUImain) {
		// TODO Auto-generated constructor stub
		this.monkeyUImain = monkeyUImain;
	}

	public void run() {
		MainRun.mainFrame.progressBarmain.setValue(10);
		Stop();
		// check monkey status
		if (!CheckUE.checkMonkeyrun(monkeyUImain.udid)) {
			logger.info("MonkeyStop check no monkey run  ");
			JOptionPane.showMessageDialog(monkeyUImain.getmonkeymonitorui(), "停止Monkey成功!", "消息",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			logger.info("MonkeyStop check  monkey run");
			JOptionPane.showMessageDialog(monkeyUImain.getmonkeymonitorui(), "停止Monkey失败,请检查设备!", "消息",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void Stop() {
//		List<String> list = Excute.returnlist2(monkeyUImain.udid, "ps |grep \"com.android.commands.monkey\"");
//		for (String str : list) {
//			if (str.equals("")) {
//				continue;
//			}
//			if (str.contains(" ")) {
//				String[] strArray = str.split("\\s+");
//				Excute.execcmd2(monkeyUImain.udid, "kill " + strArray[1]);
//				logger.info("adb shell kill " + strArray[1]);
//			}
//			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
//		}
		boolean kill = AndroidInfo.killApp(monkeyUImain.udid, "com.android.commands.monkey");
		MainRun.mainFrame.progressBarmain.setValue(100);// ******************
		logger.info("Stop monkey in phone:" + kill);
	}
}
