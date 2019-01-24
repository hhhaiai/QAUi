package com.IOSLogs;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.DataHandel.IOSIfuseContainer;
import com.Viewer.MainRun;

public class LogsCheckAppLogs {
	Logger logger = LoggerFactory.getLogger(LogsCheckAppLogs.class);
	JTextArea textAreaShowlog;
	Highlighter highLighter;

	Map<String, DefaultHighlighter.DefaultHighlightPainter> highlightMap;
	ActiveCheckLogsThread activeCheckLogsThread;
	String logpath = "";
	String packagename = "";
	String udid;

	public LogsCheckAppLogs(String udid, JTextArea textAreaShowlog) {
		// TODO Auto-generated constructor stub
		// 初始化
		this.udid = udid;
		this.textAreaShowlog = textAreaShowlog;
		highLighter = textAreaShowlog.getHighlighter();
		setMap_highlight(MainRun.paramsBean.getIOS_Logs_App_highlight());
	}

	public void run(String filter) {
		// 线程启动
		activeCheckLogsThread = new ActiveCheckLogsThread(udid, filter, textAreaShowlog);
		new Thread(activeCheckLogsThread).start();
		logger.info("active check logs");
	}

	public void setLogPath(String logpath) {
		this.logpath = logpath;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public File getFile_log() {
		return activeCheckLogsThread.getFile_log();
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
			activeCheckLogsThread.stop();
		}
	}

	// 开始执行
	class ActiveCheckLogsThread implements Runnable {
		String filter;
		JTextArea textAreaShowlog;
		IOSIfuseContainer ifuseContainer = null;
		String udid;

		public ActiveCheckLogsThread(String udid, String filter, JTextArea textAreaShowlog) {
			this.udid = udid;
			this.filter = filter;
			this.textAreaShowlog = textAreaShowlog;
		}

		public void run() {
			ifuseContainer = new IOSIfuseContainer(udid) {

				@Override
				public void readline(String line) {
					// TODO Auto-generated method stub
					if (filter.equals("") || line.contains(filter)) {
						textAreaShowlog.append(line + "\n");
						addcolor(line + "\n");
					}
				}
			};
			ifuseContainer.setIOSpackagename(packagename);
			ifuseContainer.setLogpath(logpath);
			int i = ifuseContainer.monitorLog(true);
			if (i == -1) {
				JOptionPane.showMessageDialog(null, "未找到日志文件,请先启动应用后重试!", "消息", JOptionPane.ERROR_MESSAGE);
			} else if (i == -2) {
				JOptionPane.showMessageDialog(null, "挂载应用文件失败!", "消息", JOptionPane.ERROR_MESSAGE);
			}

		}

		public void stop() {
			if (ifuseContainer != null)
				ifuseContainer.stop();
		}

		public File getFile_log() {
			return ifuseContainer.getFile_log();
		}
	}

}
