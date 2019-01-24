package com.report;

import java.io.File;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.HelperUtil;
import com.helper.TimeUtil;

public class MonkeyAndroidSYSReport extends BaseTaskReport {
	Logger logger = LoggerFactory.getLogger(MonkeyAndroidSYSReport.class);
	StringBuffer crashBuf = new StringBuffer();
	int crashcount = 0;

	public MonkeyAndroidSYSReport() {
		// TODO Auto-generated constructor stub
		addMonkeyHeader();
	}

	/**
	 * 添加logcat日记
	 */
	public void WriteLogcatWindow() {
		StringBuffer Buf = new StringBuffer();
		Buf.append("<h2>Logcat日志:</h2>");
		Buf.append("<a class='" + class_button
				+ "'  href='./Logs/logcat_Applog.txt' target='logcatbox' onclick=showApplog()>显示应用日志</a><br>");
		Buf.append("<a class='" + class_button
				+ "'  href='./Logs/logcat_Syslog.txt' target='logcatbox' onclick=showSyslog()>显示系统日志</a><br>");
		Buf.append(
				"<div class='logPoplayer' style='z-index:9001;position:fixed;display:none;width:100%;height:500px;top:0;background-color: white;border: 2px solid black;'>");
		Buf.append("<div>");
		Buf.append("<span>logcat日志:</span>");
		Buf.append("<button style='position: absolute;right:15px;' class='" + class_button
				+ "' onclick=closelogPoplayer()>关闭</button>");
		Buf.append(
				"<iframe name='logcatbox' style='position: absolute;top: 20px;left: 0px;width:100%;height:475px;overflow:scroll;border:none;'></iframe>");
		Buf.append("</div>");
		Buf.append("</div>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
	}

	/**
	 * 添加结果汇总
	 */
	public String WriteMonkeyResult(int crashcount, long starttime, long endtime) {
		StringBuffer Buf = new StringBuffer();
		Buf.append("<h2>测试结果:</h2>");
		Buf.append("<table border='1' class='MonkeyResultTable'>");
		Buf.append("<tr>");
		Buf.append("<th>崩溃数量</th>");
		Buf.append("<th>开始时间</th>");
		Buf.append("<th>结束时间</th>");
		Buf.append("<th>用时</th>");
		Buf.append("</tr>");
		Buf.append("<tr>");
		Buf.append("<td>" + crashcount + "</td>");
		Buf.append("<td>" + TimeUtil.getTime(starttime) + "</td>");
		Buf.append("<td>" + TimeUtil.getTime(endtime) + "</td>");
		Buf.append("<td>" + TimeUtil.getUseTime(starttime, endtime) + "</td>");
		Buf.append("</tr>");
		Buf.append("</table>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		emailBuf.append(Buf.toString());
		return Buf.toString();
	}

	/**
	 * 添加用例表格标题
	 */
	public void addMonkeyHeader() {
		crashBuf.append("<h2>Monkey测试详情:</h2>");
		crashBuf.append("<table border='1' class='allcaseTable'>");
		crashBuf.append("<tr>");
		crashBuf.append("<th style='width:5%;'>序号</th>");
		crashBuf.append("<th style='width:10%;'>设备时间</th>");
		crashBuf.append("<th style='width:10%;'>PC时间</th>");
		crashBuf.append("<th style='width:10%;'>Monkey运行时间</th>");
		crashBuf.append("<th style='width:65%;'>" + Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "信息</th>");
		crashBuf.append("</tr>");

	}

	/**
	 * 添加单条crash信息
	 * 
	 * @param crashcount
	 * @param uetime
	 * @param pctime
	 * @param monkeytime
	 * @param crashinfo
	 */
	public void addMonkeyCrashLine(int crashcount, String uetime, String pctime, String monkeytime, String crashinfo) {
		Long showid = TimeUtil.getTime();
		crashBuf.append("<tr>");
		crashBuf.append("<td>" + crashcount + "</td>");
		crashBuf.append("<td>" + uetime + "</td>");
		crashBuf.append("<td>" + pctime + "</td>");
		crashBuf.append("<td>已运行" + monkeytime + "</td>");
		crashBuf.append("<td>");
		crashBuf.append("<div>" + analysisCrashInfo(crashinfo) + "</div>");
		if (!crashinfo.equals("")) {
			crashBuf.append(
					"<button id='" + showid + "_text' class='" + class_button + "' onclick=show(id)>点击展开</button>");
			crashBuf.append("<div class='hideText' style='display:none'>" + StringEscapeUtils.escapeHtml4(crashinfo)
					+ "</div>");
			showid++;
		}
		crashBuf.append("</td>");
		crashBuf.append("</tr>");
	}

	/**
	 * 得到crash信息
	 * 
	 * @return
	 */
	public StringBuffer getFailBuf() {
		return crashBuf;
	}

	/**
	 * 提取crash标题
	 * 
	 * @param crashinfo
	 * @return
	 */
	private String analysisCrashInfo(String crashinfo) {
		for (String line : crashinfo.split("\n")) {
			if (line.contains("Long Msg:")) {
				return line;
			}
		}
		return Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "信息:";
	}

	/**
	 * 写入崩溃信息
	 * 
	 * @param reportFile
	 */
	public String WriteCrashCaseBuf() {
		crashBuf.append("</table>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), crashBuf.toString(), true);
		emailBuf.append(crashBuf.toString());
		return crashBuf.toString();
	}

	@Override
	String TestNoteInfo() {
		StringBuffer Buf = new StringBuffer();
		return Buf.toString();
	}

	@Override
	String getHeaderCSS() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public File start(File reportFolder, String title) {
		File file = create(new File(reportFolder.getAbsolutePath() + "/测试结果-Report.html"), title);
		WriteHeader();
		return file;
	}

	@Override
	public void end() {
		WriteCrashCaseBuf();
		WriteLogcatWindow();
		WriteShowallButton();
		WriteVideoWindow();
		WriteLink2Mixreport();
		WriteTail();
		addEmailHeaderNote();
	}
}
