package com.IOSLogs;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.constant.CIOSCMD;
import com.constant.Cconfig;

public class LogsCheckSysLogs {
	Logger logger = LoggerFactory.getLogger(LogsCheckSysLogs.class);
	JTextArea textAreaShowlog;
	Highlighter highLighter;

	Map<String, DefaultHighlighter.DefaultHighlightPainter> highlightMap;
	ActiveCheckLogsThread activeCheckLogsThread;
	String udid;

	public LogsCheckSysLogs(JTextArea textAreaShowlog, String udid) {
		// TODO Auto-generated constructor stub
		// 初始化
		this.udid = udid;
		this.textAreaShowlog = textAreaShowlog;
		highLighter = textAreaShowlog.getHighlighter();
		setMap_highlight(MainRun.paramsBean.getIOS_Logs_highlight());
	}

	public void run(String filter) {
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
//		List<String> list=Excute.returnlist(Ccmd.IDEVICESYSLOG_PID, Ccmd.SYSCMD, true);
//		for( String str : list){
//			Excute.execcmd(Ccmd.IDEVICESYSLOG_STOP.replaceAll("#pid#", str), Ccmd.SYSCMD, true);
//			logger.info("摧毁idevicesyslog,pid="+str);
//		}
		if (activeCheckLogsThread != null) {
			activeCheckLogsThread.isstop = true;
		}
	}

	// 开始执行
	class ActiveCheckLogsThread implements Runnable {
		String filter;
		JTextArea textAreaShowlog;
		public boolean isstop = false;

		public ActiveCheckLogsThread(String filter, JTextArea textAreaShowlog) {
			this.filter = filter;
			this.textAreaShowlog = textAreaShowlog;
		}

		public void run() {
			Process p = null;
			ProcessBuilder pb = null;
			BufferedReader br = null;
			try {
				if (MainRun.OStype == Cconfig.WINDOWS) {
					p = Runtime.getRuntime().exec(CIOSCMD.IDEVICESYSLOG.replace("#udid#", udid));
				} else {
					List<String> list = new ArrayList<String>();
					list.add("/bin/sh");
					list.add("-c");
					list.add(MainRun.paramsBean.getMACcmd() + "/" + CIOSCMD.IDEVICESYSLOG.replace("#udid#", udid));
					pb = new ProcessBuilder(list);
					p = pb.start();
				}
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = br.readLine();
				String[] linestrs;
				while (!isstop && line != null) {
					linestrs = line.split("\\s+");
					if (linestrs.length > 4) {
						if (filter.equals("") || line.contains(filter)) {
							textAreaShowlog.append(line + "\n");
							addcolor(line + "\n");
						}
					}
					line = br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			} finally {
				try {
					if (p != null) {
						p.destroy();
					}
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
				logger.info("idevicesyslog监控已经停止");
			}

		}
	}
}
