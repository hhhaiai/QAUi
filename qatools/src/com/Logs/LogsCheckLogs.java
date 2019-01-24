package com.Logs;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Viewer.MainRun;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

public class LogsCheckLogs {
	Logger logger = LoggerFactory.getLogger(LogsCheckLogs.class);
	JTextArea textAreaShowlog;
	Highlighter highLighter;

	Map<String, DefaultHighlighter.DefaultHighlightPainter> highlightMap;
	ActiveCheckLogsThread activeCheckLogsThread;
	String udid;

	public void run(String udid, String filter, JTextArea textAreaShowlog) {
		// 初始化
		this.udid = udid;
		this.textAreaShowlog = textAreaShowlog;
		highLighter = textAreaShowlog.getHighlighter();
		setMap_highlight(MainRun.paramsBean.getLogs_highlight());
		// 线程启动
		activeCheckLogsThread = new ActiveCheckLogsThread(filter, textAreaShowlog);
		new Thread(activeCheckLogsThread).start();
		logger.info("active check logs");
	}

	/**
	 * 给日志添加颜色
	 */
	private void addcolor(String text) {
		int lastpos = textAreaShowlog.getText().length() - text.length();// 得到上次标记最后的位置index
		for (Entry<String, DefaultHighlighter.DefaultHighlightPainter> entry : highlightMap.entrySet()) {
			int pos = 0;
			while ((pos = text.indexOf(entry.getKey(), pos)) >= 0) {
				try {
					highLighter.addHighlight(lastpos + pos, lastpos + pos + entry.getKey().length(), entry.getValue());//
					pos += entry.getKey().length();
				} catch (BadLocationException e) {
					logger.error("Exception", e);
				}
			}
		}
	}

	// 高亮参数
	public void setMap_highlight(String highlightTXT) {
		highLighter.removeAllHighlights();
		String[] strings = highlightTXT.split(";");
		highlightMap = new HashMap<>();
		for (String str : strings) {
			if (str.contains(",")) {
				highlightMap.put(str.split(",")[0],
						new DefaultHighlighter.DefaultHighlightPainter(new Color(Integer.parseInt(str.split(",")[1]))));
			}
		}
	}

	/**
	 * 停止监控日记
	 */
	public void StopLogMonitor() {
		if (activeCheckLogsThread != null) {
			activeCheckLogsThread.isstop = true;
		}
	}

	// 开始执行
	class ActiveCheckLogsThread implements Runnable {
		String cmd;
		JTextArea textAreaShowlog;
		public boolean isstop = false;

		public ActiveCheckLogsThread(String cmd, JTextArea textAreaShowlog) {
			this.cmd = cmd;
			this.textAreaShowlog = textAreaShowlog;
		}

		public void run() {
			try {
				if (MainRun.adbBridge.getDevice(udid) != null) {
					Excute.execcmd(udid, "logcat -c", 2, true);
					MainRun.adbBridge.getDevice(udid).executeShellCommand(cmd, new MultiLineReceiver() {
						@Override
						public boolean isCancelled() {
							// TODO Auto-generated method stub
							if (isstop) {
								return true;
							} else {
								return false;
							}
						}

						@Override
						public void processNewLines(String[] arg0) {
							// TODO Auto-generated method stub
							for (String line : arg0) { // 将输出的数据缓存起来
								textAreaShowlog.append(line + "\n");
								addcolor(line + "\n");
								// System.out.println(line);
							}
						}
					}, 999999999, TimeUnit.SECONDS);
				}
			} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
					| IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}

		}
	}
}
