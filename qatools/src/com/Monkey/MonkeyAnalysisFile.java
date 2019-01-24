package com.Monkey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class MonkeyAnalysisFile {
	Logger logger = LoggerFactory.getLogger(MonkeyAnalysisFile.class);
	boolean monkeyanalysisthreadrun = false;
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss MMdd-yyyy");

	int arow;// 分析行数
	int arowword;// 每行分析字数
	int arowcount;// 分析行数计数
	int crashcount;
	String filepath;
	List<String> crashlist = new ArrayList<String>();// crash string after substring
	boolean startprint;
	boolean logbymonitor;
	boolean showduplicate;
	StringBuffer crashstrbuffer = new StringBuffer();// 分析后crash字符串
	StringBuffer crashstrOriginalbuffer = new StringBuffer();// 分析后crash字符串
	List<String> crashnumlist = new ArrayList<String>();// crash issue num
	List<String> crashOriginallist = new ArrayList<String>();// crash string before substring
	MonkeyUImain monkeyUImain;

	public MonkeyAnalysisFile(MonkeyUImain monkeyUImain) {
		// TODO Auto-generated constructor stub
		this.monkeyUImain = monkeyUImain;
	}

	public void run(int arow, int aorwword, boolean showduplicate) {
		this.arow = arow;
		this.arowword = aorwword;
		this.showduplicate = showduplicate;
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		MonkeyAnalysisThread monkeyanalysisthread = new MonkeyAnalysisThread();
		new Thread(monkeyanalysisthread).start();
		logger.info("start monkey analysis...");
	}

	// set as original
	public void settooriginal() {
		// this.arow=arow; //分析行数
		// this.arowword=arowword;//每行分析字数
		arowcount = 0;// 分析行数计数
		crashcount = 0;
		startprint = false;
		logbymonitor = false;// just for time judge
		crashlist.clear();
		crashnumlist.clear();
		crashOriginallist.clear();
		crashstrbuffer.setLength(0);
	}

	// read log and create temp file
	public void readlogfile(String filePath) {
		File file = new File(filePath);
		File tempfile = new File(filePath + "_all");
		if (!tempfile.exists()) {
			try {
				tempfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			tempfile.delete();
			if (!tempfile.exists()) {
				try {
					tempfile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// long line = 1;
			// 一次读入一行，直到读入null为文件结束
			// if this log is created by monkey monitor
			if ((tempString = reader.readLine()) != null) {
				if (tempString.contains("This log created by Monkey Monitor of ThenTools")) {
					logbymonitor = true;
				}
			}
			while ((tempString = reader.readLine()) != null) {
				// System.out.println(line + ": " + tempString);
				if (tempString.contains("CRASH:")) { // #Intent
					if (arowcount >= 1) {// 处理第二个crash行数小于arow
						crashlist.add(crashstrbuffer.toString());// 添加处理过的crash字符串到list
						crashOriginallist.add(crashstrOriginalbuffer.toString());// 添加原始的crash字符串到list
					}
					startprint = true;
					arowcount = 0;// 处理第二个crash行数小于arow
					crashcount++;
					crashstrbuffer.setLength(0);// 清空crash字符串
					crashstrbuffer.append("*****************************************" + "Issue Num=" + crashcount
							+ "*****************************************\n");
					crashstrOriginalbuffer.setLength(0);
					crashstrOriginalbuffer.append("*****************************************" + "Issue Num="
							+ crashcount + "*****************************************\n");
					HelperUtil.file_write_line(tempfile.getAbsolutePath(), "*****************************************"
							+ "Issue Num=" + crashcount + "*****************************************", true);
				}

				if (startprint) {
					crashstrOriginalbuffer.append(tempString + "\n");// 添加分析前的Crash字符串
					HelperUtil.file_write_line(tempfile.getAbsolutePath(), tempString, true);// 完整打印crash
					// 去掉括号里面的
					String[] spitstr = tempString.split("\\(");
					tempString = spitstr[0];
					// 判断是否有时间轴21个字符,截取前arowword个字符
					if (tempString.length() > arowword && logbymonitor) {
						tempString = tempString.substring(21, arowword); // 21 for time count
					} else if (tempString.length() > arowword && !logbymonitor) {
						tempString = tempString.substring(0, arowword);
					}
					// 去掉Build 行
					if (!tempString.contains("// Build")) {
						crashstrbuffer.append(tempString + "\n");// 添加分析后的crash字符串
					}
					arowcount++;
					// 达到分析行数
					if (arowcount >= arow) {
						startprint = false;
						arowcount = 0;
						crashlist.add(crashstrbuffer.toString());// 添加处理过的crash字符串到list
						crashOriginallist.add(crashstrOriginalbuffer.toString());// 添加原始的crash字符串到list
						// System.out.println(crashstrbuffer.toString());
					}
				}
				// line++;
			}
		} catch (IOException e) {
			logger.error("Exception", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Exception", e);
				}
			}
		}
	}

	// compare list, set repeat as "",get issue num
	public void comparelist(List<String> crashlist) {
		String tempi, tempj;
		for (int i = 0; i < crashlist.size(); i++) {
			if (crashlist.get(i).equals("")) {// 遇到空白直接跳过
				continue;
			}
			tempi = crashlist.get(i);
			for (int j = i + 1; j < crashlist.size(); j++) {
				tempj = crashlist.get(j);
				if (tempi.substring(tempi.indexOf("\n") + 1).equals(tempj.substring(tempj.indexOf("\n") + 1))) {
					crashlist.set(j, "");// 重复的设置为空白
				}
			}
		}
		// 提取不重复的issue num
		for (int i = 0; i < crashlist.size(); i++) {
			tempi = crashlist.get(i);
			if (!tempi.equals("")) {
				crashnumlist.add(tempi.substring(tempi.indexOf("=") + 1, tempi.indexOf("*", tempi.indexOf("="))));
			}
		}
	}

	// get original crash string via issue num
	public void getOriginalIssue(List<String> crashnumlist, List<String> crashOriginallist) {
		String tempi, tempj;
		boolean isok = false;
		for (int i = 0; i < crashOriginallist.size(); i++) {
			tempi = crashOriginallist.get(i);// 得到original issue
			tempi = tempi.substring(tempi.indexOf("=") + 1, tempi.indexOf("*", tempi.indexOf("=")));

			for (int j = 0; j < crashnumlist.size(); j++) {
				tempj = crashnumlist.get(j);// 得到issue num
				if (tempi.equals(tempj)) {
					isok = true;
					break;// 发现有相同的,跳出
				}
				isok = false;
			}
			if (!isok) {
				crashOriginallist.set(i, "");// 重复的设置为空白
			}
		}
	}

	// print to UI
	public void printtoUI(List<String> crashOriginallist) {
		String tempi;
		File tempfile = new File(filepath + "_filter");
		if (!tempfile.exists()) {
			try {
				tempfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			tempfile.delete();
			if (!tempfile.exists()) {
				try {
					tempfile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// print
		monkeyUImain.getmonkeymonitorui().gettextAreaShowlog().setText("");// clear
		int showcount = 0;
		for (int i = 0; i < crashOriginallist.size(); i++) {
			if (!crashOriginallist.get(i).equals("")) {
				showcount++;
			}
		}
		if (!showduplicate) {
			monkeyUImain.getmonkeymonitorui().gettextAreaShowlog().append(sDateFormat.format(new Date()) + ":  "
					+ "The result of filter duplication issues, total=" + showcount + " issues\n");
		} else {
			monkeyUImain.getmonkeymonitorui().gettextAreaShowlog().append(sDateFormat.format(new Date()) + ":  "
					+ "The result of all issues, total=" + showcount + " issues\n");
		}
		for (int i = 0; i < crashOriginallist.size(); i++) {
			tempi = crashOriginallist.get(i);
			if (!tempi.equals("")) {
				monkeyUImain.getmonkeymonitorui().gettextAreaShowlog().append(tempi);// 打印到UI
				tempi = tempi.replaceAll("\n", "\r\n");
				HelperUtil.file_write_line(tempfile.getAbsolutePath(), tempi, true);// 过滤后的打印到文件
			}
		}

	}

	// select file
	public String selectfile(String latestlog) {
		String path = "";
		JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile + "/MonkeyMonitor");
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.log";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".log");
			}
		});
		fileChooser.setSelectedFile(new File(latestlog));
		if (fileChooser.showOpenDialog(null) != 0) {
			logger.info("No file select");
			filepath = path;
			return path;
		}
		File file = fileChooser.getSelectedFile();
		if (!file.exists()) {
			logger.info("Invalid file, Pls select correct file!");
			JOptionPane.showMessageDialog(monkeyUImain, "无效的文件,请选择重新选择!", "消息", JOptionPane.ERROR_MESSAGE);
			filepath = path;
			return path;
		} else {
			path = file.getAbsolutePath();
		}
		logger.info("select file: " + path);
		filepath = path;
		return path;
	}

	public boolean getmonkeyanalysisthreadrun() {
		return monkeyanalysisthreadrun;
	}

	class MonkeyAnalysisThread implements Runnable {

		public MonkeyAnalysisThread() {

		}

		public void run() {
			monkeyanalysisthreadrun = true;
			// 恢复默认设置
			settooriginal();
			// 读取log文件,各种分析
			readlogfile(filepath);
			MainRun.mainFrame.progressBarmain.setValue(30);// ******************
			if (!showduplicate) {
				// 比较临时list,得到num
				comparelist(crashlist);
				MainRun.mainFrame.progressBarmain.setValue(50);// ******************
				// 使用得到的num,得到原始issue
				getOriginalIssue(crashnumlist, crashOriginallist);
			}
			MainRun.mainFrame.progressBarmain.setValue(70);// ******************
			// 将原始issue打印到UI
			printtoUI(crashOriginallist);
			MainRun.mainFrame.progressBarmain.setValue(100);// ******************
			JOptionPane.showMessageDialog(monkeyUImain, "分析Monkey日记文件完成!", "消息", JOptionPane.INFORMATION_MESSAGE);
			monkeyanalysisthreadrun = false;
		}
	}
}
