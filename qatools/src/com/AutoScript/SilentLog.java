package com.AutoScript;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Viewer.MainRun;

public class SilentLog {
	Logger logger = LoggerFactory.getLogger(SilentLog.class);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	String PCtime;
	String adbfile;
	String kmsgfile;
	String dmesgfile;
	String whatlog = "null";
	public boolean activelogthreadrun = false;
	boolean hasdmesg;
	String udid;

	public void start(String udid) {
		this.udid = udid;
		PCtime = sDateFormat.format(new Date());
		adbfile = "/sdcard/CatchLog/PCtime" + PCtime + "/adb";
		kmsgfile = "/sdcard/CatchLog/PCtime" + PCtime + "/kmsg";
		dmesgfile = "/sdcard/CatchLog/PCtime" + PCtime + "/dmesg";

		// 线程启动
		ActivelogThread activelogthread = new ActivelogThread();
		new Thread(activelogthread).start();
		logger.info("active silent log");
	}

	public boolean checkdmesg() {
		hasdmesg = true;
		String userdebuglist = Excute.execcmd2(udid, "dmesg |grep \"klogctl: Operation not permitted\"").toString();
		// logger.info(userdebuglist[0]);
		if (userdebuglist.contains("klogctl: Operation not permitted")) {
			hasdmesg = false;
			logger.info("can't get dmesg without root");
		}
		return hasdmesg;
	}

	public void activeadb() {
		Excute.execcmd(udid, "mkdir -p " + adbfile, 2, true);
		String cmd_logcat = "echo while true;do Datelogcat=`date +%Y%m%d_%H_%M_%S`;" + "logcat -v threadtime ^>"
				+ adbfile + "/${Datelogcat}main_system_log.txt ^&" + " echo $! ^>" + adbfile + "/logcatpid.txt;"
				+ "logcat -v threadtime -b radio ^>" + adbfile + "/${Datelogcat}radio_log.txt ^&" + " echo $! ^>^>"
				+ adbfile + "/logcatpid.txt;" + "logcat -v threadtime -b events ^>" + adbfile
				+ "/${Datelogcat}events_log.txt ^& " + "echo $! ^>^>" + adbfile + "/logcatpid.txt;" + " sleep 1800;"
				+ "kill -9 `cat " + adbfile + "/logcatpid.txt`;" + "done ^& >" + MainRun.datalocation + "/tempdata";
		Excute.execcmd(udid, cmd_logcat, 1, true);
		// logger.info("active adb");
	}

	public void activekmsg() {
		Excute.execcmd(udid, "mkdir -p " + kmsgfile, 2, true);
		String cmd_kmsg = "echo while true;do Datekmsg=`date +%Y%m%d_%H_%M_%S`;" + "cat /proc/kmsg ^>" + kmsgfile
				+ "/${Datekmsg}_kmsg.txt ^& " + "echo $! ^>" + kmsgfile + "/kmsgpid.txt; " + "sleep 1800; "
				+ "kill -9 `cat " + kmsgfile + "/kmsgpid.txt`;" + "done ^& >>" + MainRun.datalocation + "/tempdata";
		Excute.execcmd(udid, cmd_kmsg, 1, true);
		// logger.info("active kernel_kmsg");
	}

	public void activedmesg() {
		Excute.execcmd(udid, "mkdir -p " + dmesgfile, 2, true);
		String cmd_dmesg = "echo while true;do Datedmesg=`date +%Y%m%d_%H_%M_%S`;" + "dmesg^>" + dmesgfile
				+ "/${Datedmesg}_dmesg.txt;" + "sleep 120;" + "done ^& >>" + MainRun.datalocation + "/tempdata";
		Excute.execcmd(udid, cmd_dmesg, 1, true);
		// logger.info("active kernel_dmesg");
	}

	public boolean checklogfile() {
		boolean exist = false;
		String adbresult = Excute.execcmd2(udid, "cd " + adbfile).toString();
		String dmesgresult = Excute.execcmd2(udid, "cd " + dmesgfile).toString();
		String kmsgresult = Excute.execcmd2(udid, "cd " + kmsgfile).toString();
		if (hasdmesg) {
			if (adbresult.toString().equals("")
					&& (dmesgresult.toString().equals("") || kmsgresult.toString().equals(""))) {
				exist = true;
			} else {
				logger.info("Check file not ok: " + adbresult.toString() + "  " + dmesgresult.toString() + "   "
						+ kmsgresult.toString());
			}
		} else {
			if (adbresult.toString().equals("")) {
				exist = true;
			} else {
				logger.info("Check file not ok: " + adbresult.toString() + "  " + dmesgresult.toString() + "   "
						+ kmsgresult.toString());
			}
		}
		return exist;
	}

	public boolean getActivelogthreadrun() {
		return activelogthreadrun;
	}

	class ActivelogThread implements Runnable {

		public ActivelogThread() {

		}

		public void run() {
			activelogthreadrun = true;
			activeadb();
			if (checkdmesg()) {
				activedmesg();
				whatlog = " adb & dmesg";
			} else {
				whatlog = " adb ";
			}
			Excute.execcmd(udid, "shell <" + MainRun.datalocation + "/tempdata", 3, false);
			if (checklogfile()) {
				logger.info("Active " + whatlog + " logs with silent for successfully!");
			}
			File file = new File(MainRun.datalocation + "/tempdata");
			if (file.exists()) {
				file.deleteOnExit();
				logger.info("del tempdata when exit!");
			}
			activelogthreadrun = false;
		}
	}

}
