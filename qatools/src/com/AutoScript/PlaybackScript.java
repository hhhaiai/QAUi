package com.AutoScript;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.ChimpBridge;
import com.Viewer.MainRun;

public class PlaybackScript {
	Logger logger = LoggerFactory.getLogger(PlaybackScript.class);
	boolean playbackscriptthreadrun;
	JTextArea textAreaShowScript;
	ChimpBridge chimpbridge = new ChimpBridge();
	double xD, yD;
	int checkline = 0;
	int totalline = 0;
	boolean Cancelplayback = false;
	Thread threadplaybackscript;
	AutoScriptUImain autoScriptUImain;

	public PlaybackScript(AutoScriptUImain autoScriptUImain) {
		// TODO Auto-generated constructor stub
		this.autoScriptUImain = autoScriptUImain;
	}

	public void run() {
		File file = new File(MainRun.QALogfile + "/Script/ScreenCap");
		if (!file.exists()) {
			file.mkdirs();
		}
		// 线程启动
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		PlaybackScriptThread playbackscriptthread = new PlaybackScriptThread();
		threadplaybackscript = new Thread(playbackscriptthread);
		threadplaybackscript.start();
		logger.info("record script thread start");
	}

	// 执行
	public void Translate(String allinfo) {
		boolean startloop = false;
		int times = 0;
		StringBuffer strbuf = new StringBuffer();

		String[] info = allinfo.split("\n");
		totalline = info.length;
		chimpbridge.Wake(0 + "");// wake
		for (String line : info) {
			if (!Cancelplayback) {
				checkline++;
				// 处理每一行文字
				line = line.replaceAll("\\(|\\)|ms|\n|\\=", "");
				line = line.replaceAll(":", ",");
				String[] action = line.split(",");
				// 开始执行
				if (!startloop) {// 执行不是循环的语句
					if (action[0].equals("Tap")) {
						chimpbridge.Tap(action[1], action[2], action[3]);
					} else if (action[0].equals("Long tap")) {
						chimpbridge.LongTap(action[1], action[2], action[3], action[4]);
					} else if (action[0].equals("Drag")) {
						chimpbridge.Drag(action[1], action[2], action[3], action[4], action[5]);
					} else if (action[0].equals("Sleep")) {
						chimpbridge.Sleep(action[1]);
					} else if (action[0].equals("Press")) {
						chimpbridge.Pressbutton(action[1], action[2]);
					} else if (action[0].equals("Screen Cap")) {
						chimpbridge.Screencap(action[1]);
					} else if (action[0].equals("Reboot")) {
						chimpbridge.Reboot(action[1]);
					} else if (action[0].equals("Active Log")) {
						chimpbridge.Startlog();
					} else if (action[0].equals("Wake")) {
						chimpbridge.Wake(action[1]);
					} else if (action[0].equals("Type")) {
						chimpbridge.Type(action[1], action[2]);
					} else if (action[0].equals("StartActivity")) {
						chimpbridge.StartActivity(action[1], action[2]);
					} else if (action[0].startsWith("**") && action[0].endsWith("**")) {

					} else if (action[0].equals("Test")) {
						chimpbridge.Test(action[1]);
					} else if (action[0].equals("Start Loop")) {
						startloop = true;// 设置标记
						times = Integer.parseInt(action[1]);// 循环次数
						strbuf.setLength(0);// 清除旧录制
					}
				} else if (startloop) {// start loop
					if (action[0].equals("Tap")) {
						chimpbridge.Tap(action[1], action[2], action[3]);
					} else if (action[0].equals("Long tap")) {
						chimpbridge.LongTap(action[1], action[2], action[3], action[4]);
					} else if (action[0].equals("Drag")) {
						chimpbridge.Drag(action[1], action[2], action[3], action[4], action[5]);
					} else if (action[0].equals("Sleep")) {
						chimpbridge.Sleep(action[1]);
					} else if (action[0].equals("Press")) {
						chimpbridge.Pressbutton(action[1], action[2]);
					} else if (action[0].equals("Screen Cap")) {
						chimpbridge.Screencap(action[1]);
					} else if (action[0].equals("Reboot")) {
						chimpbridge.Reboot(action[1]);
					} else if (action[0].equals("Wake")) {
						chimpbridge.Wake(action[1]);
					} else if (action[0].equals("Active Log")) {
						chimpbridge.Startlog();
					} else if (action[0].equals("Type")) {
						chimpbridge.Type(action[1], action[2]);
					} else if (action[0].equals("StartActivity")) {
						chimpbridge.StartActivity(action[1], action[2]);
					} else if (action[0].startsWith("**") && action[0].endsWith("**")) {

					} else if (action[0].equals("Start Loop")) {

					} else if (action[0].equals("Test")) {
						chimpbridge.Test(action[1]);
					} else if (action[0].equals("End Loop")) {// end loop1
						startloop = false;
						if (!execLoop(strbuf.toString(), times - 1)) { // 执行循环-1
							break;
						}
					}
					strbuf.append(line + "\n");// 将循环1中的语句加入新建语句中

				} // end
			}
		}
	}

	// 循环执行
	public boolean execLoop(String allinfo, int count) {
		if (allinfo == null || allinfo.equals("")) {
			ScriptError("LoopError1", null);
			return false;
		}
		String[] info = allinfo.split("\n");
		for (int i = 0; i < count; i++) {
			for (String line : info) {
				if (!Cancelplayback) {
					// 处理每一行文字
					line = line.replaceAll("\\(|\\)|ms|\n|\\=", "");
					line = line.replaceAll(":", ",");
					String[] action = line.split(",");
					// 开始执行
					if (action[0].equals("Tap")) {
						chimpbridge.Tap(action[1], action[2], action[3]);
					} else if (action[0].equals("Long tap")) {
						chimpbridge.LongTap(action[1], action[2], action[3], action[4]);
					} else if (action[0].equals("Drag")) {
						chimpbridge.Drag(action[1], action[2], action[3], action[4], action[5]);
					} else if (action[0].equals("Sleep")) {
						chimpbridge.Sleep(action[1]);
					} else if (action[0].equals("Press")) {
						chimpbridge.Pressbutton(action[1], action[2]);
					} else if (action[0].equals("Screen Cap")) {
						chimpbridge.Screencap(action[1]);
					} else if (action[0].equals("Active Log")) {
						chimpbridge.Startlog();
					} else if (action[0].equals("Wake")) {
						chimpbridge.Wake(action[1]);
					} else if (action[0].equals("StartActivity")) {
						chimpbridge.StartActivity(action[1], action[2]);
					} else if (action[0].startsWith("**") && action[0].endsWith("**")) {

					} else if (action[0].equals("Reboot")) {
						chimpbridge.Reboot(action[1]);
					} else if (action[0].equals("Type")) {
						chimpbridge.Type(action[1], action[2]);
					} else if (action[0].equals("Test")) {
						chimpbridge.Test(action[1]);
					} else if (action[0].equals("Start Loop")) {

					}
				}
			}
		}
		return true;
	}

	// check allinfo txt
	// 正则表达式
	public boolean checkScript(String allinfo) {
		checkline = 0;
		totalline = 0;
		String[] info = allinfo.split("\n");
		totalline = info.length;
		int checkloop = 0;
		Pattern Tap = Pattern.compile("Tap,[0-9]{1,4},[0-9]{1,4},[0-9]*");
		Pattern Longtap = Pattern.compile("Long tap,[0-9]{1,4},[0-9]{1,4},[0-9]*,[0-9]*");
		Pattern Drag = Pattern.compile("Drag,[0-9]{1,4},[0-9]{1,4},[0-9]{1,4},[0-9]{1,4},[0-9]*");
		Pattern Sleep = Pattern.compile("Sleep,[0-9]*");
		Pattern Press = Pattern.compile("Press,(HOME|BACK|MENU|POWER|VOLUME_UP|VOLUME_DOWN),[0-9]*");
		Pattern StartLoop = Pattern.compile("Start Loop,[0-9]*");
		Pattern EndLoop = Pattern.compile("End Loop");
		Pattern ScreenCap = Pattern.compile("Screen Cap,[0-9]*");
		Pattern StartLog = Pattern.compile("Active Log");
		Pattern Reboot = Pattern.compile("Reboot,[0-9]*");
		Pattern Wake = Pattern.compile("Wake,[0-9]*");
		Pattern Type = Pattern.compile("Type,.*,[0-9]*");
		Pattern StartActivity = Pattern.compile("StartActivity,.*,[0-9]*");
		Matcher isok;
		// 遍历
		for (String line : info) {
			checkline++;
			// 处理每一行文字
			line = line.replaceAll("\\(|\\)|ms|\n|\\=", "");
			line = line.replaceAll(":", ",");
			String[] action = line.split(",");
			// System.out.println(line);
			if (action[0].equals("Tap")) {
				isok = Tap.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Long tap")) {
				isok = Longtap.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Drag")) {
				isok = Drag.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Sleep")) {
				isok = Sleep.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Press")) {
				isok = Press.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Screen Cap")) {
				isok = ScreenCap.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Wake")) {
				isok = Wake.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Type")) {
				isok = Type.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("StartActivity")) {
				isok = StartActivity.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Reboot")) {
				isok = Reboot.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].startsWith("**") && action[0].endsWith("**")) {

			} else if (action[0].equals("Active Log")) {
				isok = StartLog.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
			} else if (action[0].equals("Start Loop")) {
				isok = StartLoop.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
				checkloop++;
			} else if (action[0].equals("End Loop")) {
				isok = EndLoop.matcher(line);
				if (!isok.matches()) {
					ScriptError("UnknowLine", action[0]);
					return false;
				}
				checkloop--;
			} else if (action[0].equals("Test")) {

			} else {
				ScriptError("UnknowLine", action[0]);
				return false;
			}
			// check loop
			if (checkloop > 1 || checkloop < 0) {
				ScriptError("LoopError2", null);
				return false;
			}
		}
		// check
		if (checkloop != 0) {
			ScriptError("LoopError3", null);
			return false;
		}
		return true;
	}

	// Error prompt
	public void ScriptError(String error, String info) {
		switch (error) {
		case "LoopError1":
			logger.info("Script: line=" + checkline + ", info=loop1 info is null");
			JOptionPane.showMessageDialog(autoScriptUImain, "脚本: 行=" + checkline + ",信息=循环信息为空", "消息",
					JOptionPane.ERROR_MESSAGE);
			break;
		case "LoopError2":
			logger.info("Script: line=" + checkline + ", info=loop2 order error");
			JOptionPane.showMessageDialog(autoScriptUImain, "脚本: 行=" + checkline + ",信息=循环关键字排序错误", "消息",
					JOptionPane.ERROR_MESSAGE);
			break;
		case "LoopError3":
			logger.info("Script: line=" + checkline + ", info=loop3 count error");
			JOptionPane.showMessageDialog(autoScriptUImain, "脚本: 行=" + checkline + ",信息=循环次数错误", "消息",
					JOptionPane.ERROR_MESSAGE);
			break;
		case "UnknowLine":
			logger.info("Script: line=" + checkline + ", Unknow line=" + info);
			JOptionPane.showMessageDialog(autoScriptUImain, "脚本: 行=" + checkline + ",未知行=" + info, "消息",
					JOptionPane.ERROR_MESSAGE);
			break;
		case "UnknowError":
			logger.info("Script: line=" + checkline + ", Unknow Error");
			JOptionPane.showMessageDialog(autoScriptUImain, "脚本: 行=" + checkline + ",未知错误", "消息",
					JOptionPane.ERROR_MESSAGE);
			break;
		}
		chimpbridge.setOK(false);
	}

	// set textAreaShowScript
	public void settextAreaShowScript(JTextArea textAreaShowScript) {
		this.textAreaShowScript = textAreaShowScript;
	}

	// cancle playbakc
	public void Cancelplayback() {
		threadplaybackscript.interrupt();
		Cancelplayback = true;
		chimpbridge.setOK(false);
		chimpbridge.cancel();
	}

	// set double xD,yD;
	public void setxD(double xD) {
		this.xD = xD;
	}

	public void setyD(double yD) {
		this.yD = yD;
	}

	// get playbackscriptthreadrun
	public boolean getPlaybackscriptthreadrun() {
		return playbackscriptthreadrun;
	}

	// Thread
	class PlaybackScriptThread implements Runnable {

		public PlaybackScriptThread() {

		}

		public void run() {
			playbackscriptthreadrun = true;
			if (!checkScript(textAreaShowScript.getText())) {
				logger.info("Script has error=" + checkline);
				MainRun.mainFrame.progressBarmain.setValue(0);// ******************
				if (autoScriptUImain.getisstartplayback()) {
					autoScriptUImain.getbtnbtnPlayback().doClick();
				}
				playbackscriptthreadrun = false;
				return;
			}
			Cancelplayback = false;
			chimpbridge.connect(autoScriptUImain.udid);
			checkline = 0;
			totalline = 0;
			chimpbridge.setOK(true);
			Translate(textAreaShowScript.getText());// do
			if (checkline == totalline) {
				logger.info("Script: " + totalline + " lines done");
				MainRun.mainFrame.progressBarmain.setValue(100);// ******************
				JOptionPane.showMessageDialog(autoScriptUImain, "脚本: " + totalline + "行执行完成.", "消息",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("Script: line=" + checkline + " fail,total=" + totalline);
				MainRun.mainFrame.progressBarmain.setValue(0);// ******************
				JOptionPane.showMessageDialog(autoScriptUImain, "脚本: 行=" + checkline + "执行失败,总共=" + totalline, "消息",
						JOptionPane.ERROR_MESSAGE);
			}
			Cancelplayback();
			if (autoScriptUImain.getisstartplayback()) {
				autoScriptUImain.getbtnbtnPlayback().doClick();
			}
			playbackscriptthreadrun = false;
			logger.info("playback script ok");
		}
	}
}
