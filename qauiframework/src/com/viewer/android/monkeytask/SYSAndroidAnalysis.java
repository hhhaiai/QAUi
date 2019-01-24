package com.viewer.android.monkeytask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.viewer.scenetask.TextShowBoxUI;

public class SYSAndroidAnalysis {
	Logger logger = LoggerFactory.getLogger(SYSAndroidAnalysis.class);
	int show = 35;// 显示行数
	int arow = 40;// 分析行数
	int arowword = 80;// 每行分析字数
	int showVSarow = 40;// show和arow谁大就是谁
	boolean showduplicate = false;// 是否显示重复CRASH
	boolean isrunning = false;

	public SYSAndroidAnalysis(Map<String, String> monkeyConfigMap) {
		// TODO Auto-generated constructor stub
		show = Integer.parseInt(monkeyConfigMap.get(Cparams.monkey_sys_analysis_show));
		arow = Integer.parseInt(monkeyConfigMap.get(Cparams.monkey_sys_analysis_arow));
		arowword = Integer.parseInt(monkeyConfigMap.get(Cparams.monkey_sys_analysis_arowword));
		showduplicate = monkeyConfigMap.get(Cparams.monkey_sys_analysis_showduplicate).equals("true") ? true : false;
		if (show < 5)
			show = 5;
		if (arow < 5)
			arow = 5;
		if (arowword < 10)
			arowword = 10;
		showVSarow = show > arow ? show : arow;
	}

	/**
	 * 开始进行日志分析
	 * 
	 * @param files
	 */
	public void start(File[] files) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				logger.info("start analysis monkey log");
				isrunning = true;
				StringBuffer stringBuffer = showResult(files);
				TextShowBoxUI textShowBoxUI = new TextShowBoxUI("分析结果", "结果", stringBuffer.toString(), 800, 650) {
					private static final long serialVersionUID = 6408724260802766424L;

					@Override
					protected boolean confirmButton() {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					protected boolean cancelButton() {
						return true;
					}
				};
				textShowBoxUI.setVisible(true);
				isrunning = false;
				logger.info("end analysis monkey log");
			}
		}).start();
	}

	/**
	 * 是否正在运行中
	 * 
	 * @return
	 */
	public boolean getIsrunning() {
		return isrunning;
	}

	/**
	 * 读取一个日志文件,得到CRASH列表
	 * 
	 * @param file
	 * @return
	 */
	private ArrayList<String> readLogFile(File file) {
		BufferedReader reader = null;
		ArrayList<String> list = new ArrayList<>();
		boolean crashflag = false;
		StringBuffer crashBuf = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains(Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG)) {
					crashflag = true;
					crashBuf.append(
							"**********CRASH序号=" + (list.size() + 1) + ",来自" + file.getAbsolutePath() + "**********\n");// 文件标记
				}
				if (crashflag) {
					if (showVSarow < crashBuf.toString().split("\n").length) {
						list.add(crashBuf.toString());
						crashflag = false;
						crashBuf.setLength(0);
					} else {
						crashBuf.append(line + "\n");
					}
				}
			}
		} catch (IOException e) {
			logger.error("Exception", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
		return list;
	}

	/**
	 * 过滤CRASH信息
	 * 
	 * @param list
	 */
	private ArrayList<String> getFilterLog(ArrayList<String> list) {
		ArrayList<String> filterlist = new ArrayList<>();
		StringBuffer stringBuffer = new StringBuffer();
		for (String lines : list) {
			stringBuffer.setLength(0);
			for (String line : lines.split("\n")) {
				if (line.startsWith("*****"))
					continue;
				if (arow >= stringBuffer.toString().split("\n").length) {
					line = line.replaceAll("\\d+", "");// 去掉所有数字
					stringBuffer.append(line.substring(0, line.length() > arowword ? arowword : line.length()));
				}
			}
			filterlist.add(stringBuffer.toString());
		}
		return filterlist;
	}

	/**
	 * 过滤后的列表
	 * 
	 * @param crashlist
	 * @param crashfilterlist
	 * @return
	 */
	private ArrayList<String> comparelist(List<String> crashlist, List<String> crashfilterlist) {
		ArrayList<String> showlist = new ArrayList<>();
		String tempi, tempj;
		for (int i = 0; i < crashfilterlist.size(); i++) {
			if (crashfilterlist.get(i).equals("")) {// 遇到空白直接跳过
				continue;
			}
			tempi = crashfilterlist.get(i);
			for (int j = i + 1; j < crashfilterlist.size(); j++) {
				tempj = crashfilterlist.get(j);
				if (tempi.equals(tempj)) {
					crashfilterlist.set(j, "");// 重复的设置为空白
				}
			}
		}
		// 提取不重复的issue num
		for (int i = 0; i < crashfilterlist.size(); i++) {
			tempi = crashlist.get(i);
			if (!tempi.equals("")) {
				showlist.add(tempi);
			}
		}
		return showlist;
	}

	/**
	 * 显示列表
	 * 
	 * @param files
	 * @return
	 */
	private StringBuffer showResult(File[] files) {
		StringBuffer stringBuffer = new StringBuffer();
		ArrayList<String> crashlist = new ArrayList<>();// 所有crash列表
		ArrayList<String> crashfilterlist = new ArrayList<>();// 所有crash列表,过滤后的
		stringBuffer.append("Monkey分析结果如下:\n");
		stringBuffer.append("分析设置:\n");
		stringBuffer.append("显示重复CRASH:" + showduplicate + "\n");
		stringBuffer.append("显示行数:" + show + "\n");
		stringBuffer.append("分析行数:" + arow + "\n");
		stringBuffer.append("每行分析字数:" + arowword + "\n");
		for (File file : files) {
			ArrayList<String> temp = readLogFile(file);
			stringBuffer.append("文件:" + file.getAbsolutePath() + ",共发现" + temp.size() + "个CRASH\n");
			for (String crashinfo : temp) {
				crashlist.add(crashinfo);
			}
		}
		crashfilterlist = getFilterLog(crashlist);
		stringBuffer.append("共分析" + files.length + "个文件,发现" + crashlist.size() + "个CRASH\n\n");
		if (!showduplicate) {
			for (String str : comparelist(crashlist, crashfilterlist)) {
				stringBuffer.append(str + "\n");
			}
		} else {
			for (String str : crashlist) {
				stringBuffer.append(str + "\n");
			}
		}
		return stringBuffer;
	}
}
