package com.Logs;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;

public class LogsCatCrash {
	Logger logger = LoggerFactory.getLogger(LogsCatCrash.class);
	String udid;

	public LogsCatCrash(String udid) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
	}

	// 获取路径列表
	public ArrayList<String> getCrashPath() {
		ArrayList<String> arrayList = new ArrayList<>();
		String[] strings = com.Viewer.MainRun.paramsBean.getLogs_crashPath().split(";");
		for (String str : strings) {
			if (str.contains(",")) {
				arrayList.add("<html><font color=\"#FF0000\">" + str.split(",")[0] + "</font>=" + str.split(",")[1]
						+ "</html>");
			} else {
				continue;
			}
		}
		return arrayList;
	}

	// 获取路径下文件
	public ArrayList<String> getFilelist(String logpath) {
		ArrayList<String> list = new ArrayList<>();
		for (String str : (ArrayList<String>) Excute.returnlist2(udid, "ls -l " + logpath)) {
			if (str.contains("No such file or directory")) {
				list.clear();
				return list;
			} else if (str.startsWith("total")) {
				continue;
			} else {
				String[] strs = str.split("\\s+");
				if (str.startsWith("d")) {
					list.add("[文件夹]" + strs[strs.length - 1]);
				} else {
					list.add(strs[strs.length - 1]);
				}
			}
		}
		return list;
	}

	// 查看文件内容
	public void getFile_txt(final String filepath, final String filename, final JTextArea textAreaShowlog) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textAreaShowlog.setText("该文件大于4MB,读取会非常缓慢,请耐心等待!预计几分钟!");
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textAreaShowlog.setText(Excute.execcmd2(udid, "cat \"" + filepath + "/" + filename + "\"").toString());
				// highlight
				Highlighter highLighter = textAreaShowlog.getHighlighter();
				String text = textAreaShowlog.getText();
				DefaultHighlighter.DefaultHighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(
						Color.RED);
				String keyWord = "Exception";
				int pos = 0;
				while ((pos = text.indexOf(keyWord, pos)) >= 0) {
					try {
						highLighter.addHighlight(pos, pos + keyWord.length(), p);
						pos += keyWord.length();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
