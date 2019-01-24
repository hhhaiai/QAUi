package com.Monkey;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class MonkeyGet {
	Logger logger = LoggerFactory.getLogger(MonkeyGet.class);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy,MM,dd");
	private SimpleDateFormat sDateFormatget = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	String PCtime;
	String Mainlog;// path
	String gettime;
	String Otherlog;// path
	String logfolder;// path
	boolean getlogthreadrun = false;
	String udid;

	public MonkeyGet(String udid) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
	}

	public void run(boolean iscompression) {
		File logfolderfile = new File(logfolder);
		if (logfolderfile.exists()) {
			HelperUtil.delFolder(logfolder);
			logger.info(logfolderfile.getAbsolutePath() + " Del!");
		}
		File file = new File(Mainlog);
		if (file.exists()) {
			logger.info(file.getAbsolutePath() + " exists!");
		} else {
			file.mkdirs();
		}
		File Otherlogfile = new File(Otherlog);
		if (Otherlogfile.exists()) {
			logger.info(Otherlogfile.getAbsolutePath() + " exists!");
		} else {
			Otherlogfile.mkdirs();
		}
		logger.info("Monkey get log run()");
		// 线程启动
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		GetlogThread getlogthread = new GetlogThread(iscompression);
		new Thread(getlogthread).start();
	}

	// Stop monkey
	public void Stop() {
		List<String> list = Excute.returnlist2(udid, "ps |grep \"com.android.commands.monkey\"");
		for (String str : list) {
			if (str.equals("")) {
				continue;
			}
			if (str.contains(" ")) {
				String[] strArray = str.split("\\s+");
				Excute.execcmd2(udid, "kill " + strArray[1]);
				logger.info("adb shell kill " + strArray[1]);
			}
		}
		MainRun.mainFrame.progressBarmain.setValue(20);// ******************
		logger.info("Stop monkey in phone");
	}

	// create files
	public boolean filepathexist() {
		PCtime = sDateFormat.format(new Date());
		String[] PCtimearray = PCtime.split(",");
		PCtime = PCtimearray[0] + "Y" + PCtimearray[1] + "M" + PCtimearray[2] + "D";
		gettime = sDateFormatget.format(new Date());

		logfolder = MainRun.QALogfile + "/Monkey/PCtime" + PCtime + "/Qcom_Monkey_" + gettime;
		Mainlog = logfolder + "/CatchLog/";
		Otherlog = logfolder + "/Otherlog";

		File file = new File(Mainlog);
		if (file.exists()) {
			logger.info(file.getAbsolutePath() + " exists!");
			return true;
		} else {
			file.mkdirs();
		}
		return false;
	}

	public String getlog() {
		MainRun.mainFrame.progressBarmain.setValue(15);// ******************
		String returnstr = "";
		// main
		Excute.execcmd(udid, "pull /sdcard/CatchLog \"" + Mainlog + "\"", 3, true);// 1

		// BT
		Excute.execcmd(udid, "pull /sdcard/btsnoop_hci.log \"" + Mainlog + "\"", 3, true);// 2
		// Moneky
		Excute.execcmd(udid, "pull /sdcard/monkeylog.txt \"" + Mainlog + "\"", 3, true);// 3
		// recovery root
		Excute.execcmd(udid, "pull /cache/recovery/ \"" + Otherlog + "\"", 3, true);// 4
		// systemreboot root
		Excute.execcmd(udid, "pull /sys/fs/pstore \"" + Otherlog + "\"", 3, true);// 5
		// anr
		Excute.execcmd(udid, "pull /data/anr \"" + Otherlog + "\"", 3, true);// 6
		// root
		Excute.execcmd(udid, "pull /data/tombstones \"" + Otherlog + "\"tombstones", 3, true);// 7

		MainRun.mainFrame.progressBarmain.setValue(20);// ******************

		Excute.execcmd(udid, "pull /data/rtt_dump* \"" + Otherlog + "\"", 3, true);// 8
		Excute.execcmd(udid, "pull /data/aee_exp \"" + Otherlog + "\"data_aee_exp", 3, true);// 9
		Excute.execcmd(udid, "pull /data/mobilelog \"" + Otherlog + "\"data_mobilelog", 3, true);// 10
		Excute.execcmd(udid, "pull /data/core \"" + Otherlog + "\"data_core", 3, true);// 11
		MainRun.mainFrame.progressBarmain.setValue(30);// ******************
		// systemstatus
		Excute.execcmd(udid, "shell ps -t >\"" + Otherlog + "\"/ps.txt", 3, true);// 12
		Excute.execcmd(udid, "shell top -t -m 5 -n 2 >\"" + Otherlog + "\"/top.txt", 3, true);// 13
		MainRun.mainFrame.progressBarmain.setValue(40);// ******************
		Excute.execcmd(udid, "shell cat /proc/meminfo >\"" + Otherlog + "\"/meminfo.txt", 3, true);// 14
		MainRun.mainFrame.progressBarmain.setValue(50);// ******************
		Excute.execcmd(udid, "shell cat /proc/buddyinfo >\"" + Otherlog + "\"/buddyinfo.txt", 3, true);// 15
		Excute.execcmd(udid, "shell cat /proc/sched_debug >\"" + Otherlog + "\"/sched+debug.txt", 3, true);// 16
		Excute.execcmd(udid, "shell cat proc/interrupts >\"" + Otherlog + "\"/interrupts.txt", 3, true);// 17
		Excute.execcmd(udid, "shell getprop >\"" + Otherlog + "\"/getprop.txt", 3, true);// 18
		Excute.execcmd(udid, "shell service list >\"" + Otherlog + "\"/servicelist.txt", 3, true);// 19
		MainRun.mainFrame.progressBarmain.setValue(60);// ******************
		Excute.execcmd(udid, "shell dumpsys >\"" + Otherlog + "\"/dumpsys.txt", 3, true);// 20
		MainRun.mainFrame.progressBarmain.setValue(70);// ******************
		Excute.execcmd(udid, "shell dumpstate >\"" + Otherlog + "\"/dumpstate.txt", 3, true);// 21
		MainRun.mainFrame.progressBarmain.setValue(80);// ******************
		Excute.execcmd(udid, "shell pm list package >\"" + Otherlog + "\"/pmlist.txt", 3, true);// 22

		// version23
		List<String> versionlist = Excute.returnlist2(udid, "getprop");
		for (String str : versionlist) {
			if (str.equals("")) {
				continue;
			}
			if (str.contains("internal.version")) {
				// logger.info(str);
				String[] version = str.split(": ");
				Excute.execcmd(udid, "echo internal version >\"" + logfolder + "\"/Version" + version[1] + ".txt", 1,
						true);
			}
		}
		// getdmesg24
		List<String> timelist = Excute.returnlist2(udid, "date");
		for (String str : timelist) {
			if (str.equals("")) {
				continue;
			}
			logger.info("UE time is " + str + " when get dmesg log");
			String[] time = str.split("\\s+|:");
			Excute.execcmd(udid, "shell dmesg >\"" + Mainlog + "\"dmesg_UEtime_" + time[7] + time[1] + time[2] + "_"
					+ time[3] + "h" + time[4] + "m" + time[5] + "s.txt", 3, true);

		}
		MainRun.mainFrame.progressBarmain.setValue(90);// ******************
		logger.info("get catchlog from phone");
		returnstr = "OK";
		return returnstr;
	}

	public String getMainlog() {
		return Mainlog;
	}

	public boolean getGetlogthreadrun() {
		return getlogthreadrun;
	}

	// 压缩
	public boolean compression(String tregetfilepath, String sourcefilepath) {
		boolean isok = false;
		if (MainRun.OStype == 0) {
			String[] result = Excute.execcmd(udid,
					MainRun.extraBinlocation + "/7za.exe a " + tregetfilepath + ".7z " + sourcefilepath, 1, true);
			if (result[0].contains("Everything is Ok")) {
				isok = true;
			} else {
				isok = false;
			}
		} else {
			String[] result = Excute.execcmd(udid, "zip -r " + sourcefilepath + " " + tregetfilepath, 1, true);
			if (result[0].contains("zip error")) {
				isok = false;
			} else {
				isok = true;
			}
		}

		return isok;

	}

	class GetlogThread implements Runnable {
		boolean iscompression;

		public GetlogThread(boolean iscompression) {
			this.iscompression = iscompression;
		}

		public void run() {
			getlogthreadrun = true;
			getlog();
			// 压缩文件25
			if (iscompression) {
				logger.info("start to compression log folder");
				String[] str = Mainlog.split("/|\\\\");
				// C:\Users\Then\Desktop\ThenLog\Qcom\PCtime2016Y03M14D\CatchLog_nonetime_000029
				StringBuffer sourcefilestr = new StringBuffer();
				StringBuffer targetfilestr = new StringBuffer();
				for (int i = 0; i < str.length - 1; i++) {
					sourcefilestr.append(str[i] + "/");
				}
				for (int i = 0; i < str.length - 2; i++) {
					targetfilestr.append(str[i] + "/");
				}
				targetfilestr.append(str[str.length - 2]);
				// logger.info(targetfilestr.toString()+" "+sourcefilestr.toString()+"
				// "+str.length);
				if (!compression(targetfilestr.toString(), sourcefilestr.toString())) {
					MainRun.mainFrame.progressBarmain.setValue(0);// ******************
					logger.info("Compression logs failed by 7z");
					JOptionPane.showMessageDialog(null, "7z压缩日记失败!", "消息", JOptionPane.ERROR_MESSAGE);
				} else {
					logger.info("Compression logs success by 7z");
				}
			}
			MainRun.mainFrame.progressBarmain.setValue(100);// ******************
			logger.info("GetlogThread end,all is ok!");
			getlogthreadrun = false;
			JOptionPane.showMessageDialog(null, "日记保存在" + Mainlog.substring(0, Mainlog.length() - 10) + " ", "消息",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
