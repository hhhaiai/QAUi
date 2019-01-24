package com.Logs;

import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Viewer.MainRun;

public class LogsStop {
	Logger logger = LoggerFactory.getLogger(LogsStop.class);
	public boolean stoplogthreadrun = false;
	String udid;

	public void run(String udid, boolean dellog) {
		// 线程启动
		this.udid = udid;
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		StoplogThread stoplogthread = new StoplogThread(dellog);
		new Thread(stoplogthread).start();
	}

	public void stop() {
		List<String> list = Excute.returnlist2(udid, "ps |grep logcat");
		for (String str : list) {
			if (str.equals("")) {
				continue;
			}
			if (str.contains(" ")) {
				String[] strArray = str.split("\\s+");
				Excute.execcmd2(udid, "kill " + strArray[1]);
				logger.info("adb shell kill " + strArray[1]);
			}
			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
		}
		MainRun.mainFrame.progressBarmain.setValue(100);// ******************
		logger.info("Stop catchlog in phone");
	}

	public void del() {
		Excute.execcmd2(udid, "rm -rf /sdcard/CatchLog");
		logger.info("del CatchLog folder in phone");
	}

	public boolean getstoplogthreadrun() {
		return stoplogthreadrun;
	}

	class StoplogThread implements Runnable {
		// boolean output=false;
		boolean dellog = false;

		public StoplogThread(boolean dellog) {
			this.dellog = dellog;
		}

		public void run() {
			stoplogthreadrun = true;
			if (!dellog) {
				stop();
				JOptionPane.showMessageDialog(null, "停止日记成功!", "消息", JOptionPane.INFORMATION_MESSAGE);
				logger.info("stop log ");

			} else {
				stop();
				del();
				JOptionPane.showMessageDialog(null, "停止并删除日记成功!", "消息", JOptionPane.INFORMATION_MESSAGE);
				logger.info("stop and del log ");
			}
			stoplogthreadrun = false;
		}
	}
}
