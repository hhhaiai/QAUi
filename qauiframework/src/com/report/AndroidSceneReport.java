package com.report;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.HelperUtil;

public class AndroidSceneReport extends BaseSceneReport{
	Logger logger=LoggerFactory.getLogger(AndroidSceneReport.class);
	
	/**
	 * 添加logcat日记
	 */
	public void WriteLogcatWindow(){
		StringBuffer Buf=new StringBuffer();
		Buf.append("<h2>Logcat日志:</h2>");
		Buf.append("<a class='"+class_button+"'  href='./Logs/logcat_Applog.txt' target='logcatbox' onclick=showApplog()>显示应用日志</a><br>");
		Buf.append("<a class='"+class_button+"'  href='./Logs/logcat_Syslog.txt' target='logcatbox' onclick=showSyslog()>显示系统日志</a><br>");
		Buf.append("<div class='logPoplayer' style='z-index:9001;position:fixed;display:none;width:100%;height:500px;top:0;background-color: white;border: 2px solid black;'>");
		Buf.append("<div>");
		Buf.append("<span>logcat日志:</span>");
		Buf.append("<button style='position: absolute;right:15px;' class='"+class_button+"' onclick=closelogPoplayer()>关闭</button>");
		Buf.append("<iframe name='logcatbox' style='position: absolute;top: 20px;left: 0px;width:100%;height:475px;overflow:scroll;border:none;'></iframe>");
		Buf.append("</div>");
		Buf.append("</div>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
	}

	@Override
	String getHeaderCSS() {
		// TODO Auto-generated method stub
		return "";
	}
	@Override
	public File start(File reportFolder,String title) {
		File file=create(new File(reportFolder.getAbsolutePath()+"/测试结果-Report.html"),title);
		WriteHeader();
		return file;
	}
	@Override
	public void end() {
		WriteFailCaseBuf();
		WriteAllCaseBuf();
		WriteLogcatWindow();
		WriteShowallButton();
		WriteVideoWindow();
		WriteLink2Mixreport();
		WriteTail();
		addEmailHeaderNote();
	}
}
