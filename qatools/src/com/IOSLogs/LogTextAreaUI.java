package com.IOSLogs;

import com.Viewer.MainRun;

public class LogTextAreaUI extends TextShowUI {

	public LogTextAreaUI(int width, int hight) {
		super(width, hight);
		// TODO Auto-generated constructor stub
		setSaveLogFilePath(MainRun.QALogfile);
	}

}
