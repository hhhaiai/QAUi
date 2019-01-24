package com.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CIOSCMD;
import com.constant.Cparams;
import com.helper.CMDUtil;
import com.review.getscreen.IOSShot;
import com.viewer.main.MainRun;

public class IdevicesyslogMonitor {
	Logger logger = LoggerFactory.getLogger(IdevicesyslogMonitor.class);
	String udid;
	SceneLogUtil oplog;
	IOSShot iosShot;
	File IdevicessyslogFolder;
	String idevicesyslogtag;

	public IdevicesyslogMonitor(Map<String, String> capabilityMap, Map<String, String> configMap, SceneLogUtil oplog,
			File reportFolder, IOSShot iosShot) {
		// TODO Auto-generated constructor stub
		this.iosShot = iosShot;
		this.oplog = oplog;
		this.udid = capabilityMap.get(Cparams.udid);
		this.idevicesyslogtag = configMap.get(Cparams.idevicesyslogtag);

		IdevicessyslogFolder = new File(reportFolder.getAbsolutePath() + "/Logs");
		if (!IdevicessyslogFolder.exists()) {
			IdevicessyslogFolder.mkdirs();
		}
	}

	/**
	 * 开始执行命令监控日记
	 * 
	 * @param commands
	 * @return
	 */
	public void StartLogMonitor() {
		try {
			Process p;
			ProcessBuilder pb;

			if (MainRun.settingsBean.getSystem() == 0) {
				p = Runtime.getRuntime().exec(
						MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.IDEVICESYSLOG.replaceAll("#udid#", udid));
			} else {
				List<String> list = new ArrayList<String>();
				list.add("/bin/sh");
				list.add("-c");
				list.add(MainRun.sysConfigBean.getMACcmd() + "/" + CIOSCMD.IDEVICESYSLOG.replaceAll("#udid#", udid));
				pb = new ProcessBuilder(list);
				p = pb.start();
			}
			IdevicesyslogThread outputStream = new IdevicesyslogThread(p.getInputStream(), udid, oplog,
					IdevicessyslogFolder, iosShot, idevicesyslogtag);
			Thread thread = new Thread(outputStream);
			thread.start();
			// p.waitFor();
		} catch (IOException e) {
			logger.error("Exception", e);
		}
	}

	/**
	 * 停止监控日记
	 */
	public void StopLogMonitor() {
		List<String> list = CMDUtil.returnlist(CIOSCMD.IDEVICESYSLOG_PID, CIOSCMD.SYSCMD, true);
		for (String str : list) {
			String[] strings = str.split("\\s+");
			if (strings.length == 2 && strings[1].equals(MainRun.sysConfigBean.getMACcmd() + "/idevicesyslog")) {
				CMDUtil.execcmd(CIOSCMD.IDEVICESYSLOG_STOP.replaceAll("#pid#", strings[0]), CIOSCMD.SYSCMD, true);
				logger.info("摧毁idevicesyslog,pid=" + str);
			}
		}
	}
}

/**
 * 捕获idevcessys流,用于确认异常发生
 *
 */
class IdevicesyslogThread implements Runnable {
	Logger logger = LoggerFactory.getLogger(IdevicesyslogThread.class);
	InputStream stream;
	FileOutputStream applogStream;
	FileOutputStream syslogStream;
	BufferedReader br;
	String udid;
	SceneLogUtil oplog;
	File IdevicessyslogFolder;
	IOSShot iosShot;
	String idevicesyslogtag;

	public IdevicesyslogThread(InputStream stream, String udid, SceneLogUtil oplog, File IdevicessyslogFolder,
			IOSShot iosShot, String idevicesyslogtag) {
		this.stream = stream;
		this.udid = udid;
		this.oplog = oplog;
		this.IdevicessyslogFolder = IdevicessyslogFolder;
		this.iosShot = iosShot;
		this.idevicesyslogtag = idevicesyslogtag;
	}

	public void run() {
		try {
			StringBuffer crashbuf = new StringBuffer();
			boolean flag = false;
			String logTAG = "";
			boolean AppCrashTAG = false;
			br = new BufferedReader(new InputStreamReader(this.stream));
			String line = br.readLine();
			applogStream = new FileOutputStream(IdevicessyslogFolder.getAbsolutePath() + "/idevicesyslog_Applog.txt",
					true);
			syslogStream = new FileOutputStream(IdevicessyslogFolder.getAbsolutePath() + "/idevicesyslog_Syslog.txt",
					true);
			String[] linestrs;// Jul 31 08:44:08 PG-157 Camera360[3232] <Error>: ImageIO:
								// CGImageSourceCreateWithData data parameter is nil
			while (line != null) {
				linestrs = line.split("\\s+");
				syslogStream.write((line + "\n").getBytes("UTF-8"));
				syslogStream.flush();
				if (linestrs.length > 4) {
					if (!idevicesyslogtag.equals("") && linestrs[4].contains(idevicesyslogtag)) {
						applogStream.write((line + "\n").getBytes("UTF-8"));
						applogStream.flush();
					}
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			logger.error("Exception", e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
				if (br != null) {
					br.close();
				}
				if (applogStream != null) {
					applogStream.flush();
					applogStream.close();
				}
				if (syslogStream != null) {
					syslogStream.flush();
					syslogStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
			logger.info("idevicesyslog监控已经停止");
		}
	}
}
