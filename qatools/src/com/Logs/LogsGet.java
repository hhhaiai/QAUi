package com.Logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class LogsGet {
	Logger logger = LoggerFactory.getLogger(LogsGet.class);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy,MM,dd");
	private SimpleDateFormat sDateFormatnone = new SimpleDateFormat("HHmmss");
	String PCtime;
	String Mainlog;// path
	String happentime;
	String nonetime;
	String Otherlog;// path
	String logfolder;// path
	String ANRlog;// path
	String errorgetlog;// getlog错误信息
	String timeornone;
	boolean getlogthreadrun = false;
	String udid;

	// Helper helper=new Helper();
	public LogsGet(String udid) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
	}

	public void run(String timeornone, String happentime, boolean iscompression, boolean isdeluseless) {
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
		logger.info("get log run()");
		// 线程启动
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		GetlogThread getlogthread = new GetlogThread(iscompression, timeornone, isdeluseless);
		new Thread(getlogthread).start();
	}

	public boolean filepathexist(String timeornone, String happentime) {
		this.happentime = happentime;
		this.timeornone = timeornone;
		PCtime = sDateFormat.format(new Date());
		String[] PCtimearray = PCtime.split(",");
		PCtime = PCtimearray[0] + "Y" + PCtimearray[1] + "M" + PCtimearray[2] + "D";
		nonetime = sDateFormatnone.format(new Date());

		if (timeornone.equals("None")) {
			logfolder = MainRun.QALogfile + "/AndroidLogs/PCtime" + PCtime + "/CatchLog_nonetime_" + nonetime;
			Mainlog = logfolder + "/Logcat/";
			ANRlog = logfolder + "/ANR";
			Otherlog = logfolder + "/ANR/others";
		} else {
			logfolder = MainRun.QALogfile + "/AndroidLogs/PCtime" + PCtime + "/CatchLog_happentime_" + happentime;
			Mainlog = logfolder + "/Logcat/";
			ANRlog = logfolder + "/ANR";
			Otherlog = logfolder + "/ANR/others";
		}
		File file = new File(Mainlog);
		if (file.exists()) {
			logger.info(file.getAbsolutePath() + " exists!");
			return true;
		} else {
			file.mkdirs();
		}
		return false;
	}

	public boolean checkhappentime() {
		// happentime=Mon+Day+"_"+Hour+"H"+Min+"M";0309_18h00m 03-09 20:14
		String time = happentime.substring(0, 2) + "-" + happentime.substring(2, 4) + " " + happentime.substring(5, 7)
				+ ":" + happentime.substring(8, 10);
//		String[] result;
//		if(QAToolsRun.OStype==0){
//			result=Excute.execcmd("for /r \""+Mainlog+"\" %i in (*) do (findstr /r /c:\""+time+":[0-9][0-9]\" \"%i\")", 1, true);
//		}else{
//			result=Excute.execcmd("for file in `find \""+Mainlog+"\" -name *.txt`; do grep \""+time+":[0-9][0-9]\" $file; done", 1, true);
//			//for file in `find /Users/Then/QALogs/AndroidLogs/PCtime2017Y04M23D/CatchLog_nonetime_181025/Logcat -name *.txt`; do cat $file|grep "04-23 18:10:[0-9][0-9]"; done
//		}
//		Pattern p=Pattern.compile(time+":[0-9][0-9].[0-9][0-9][0-9]");//03-09 22:04:03.779 
//		Matcher m=p.matcher(result[0].toString());
//		//com.Main.ThenToolsRun.logger.log(Level.INFO,result[0].toString());
//		logger.info("check happentime "+time+" end");
//		return m.find();
		File floder = new File(Mainlog);
		for (File file : floder.listFiles()) {
			if (file.isDirectory()) {
				if (searchFileinFloder(file.getAbsolutePath(), time + ":")) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean searchFileinFloder(String path, String keyword) {
		File floder = new File(path);
		for (File file : floder.listFiles()) {
			if (file.isFile()) {
				InputStreamReader read = null;
				BufferedReader bufferedReader = null;
				String lineTxt = null;
				String code = "gb2312";
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(file);
					byte[] head = new byte[3];
					fileInputStream.read(head);
					if (head[0] == -1 && head[1] == -2)
						code = "UTF-16";
					if (head[0] == -2 && head[1] == -1)
						code = "Unicode";
					if (head[0] == -17 && head[1] == -69 && head[2] == -65)
						code = "UTF-8";
					read = new InputStreamReader(fileInputStream, code);
					bufferedReader = new BufferedReader(read);
					while ((lineTxt = bufferedReader.readLine()) != null) {
						if (lineTxt.contains(keyword)) {
							logger.info("find " + keyword + " in " + file.getAbsolutePath() + " with code=" + code);
							return true;
						}
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} finally {
					try {
						if (read != null) {
							read.close();
						}
						if (bufferedReader != null) {
							bufferedReader.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception", e);
					}
				}

			}
		}
		return false;
	}

	public String getlog() {
		MainRun.mainFrame.progressBarmain.setValue(15);// ******************
		String returnstr = "";
		// main
		// Excute.pullfile("/sdcard/CatchLog/.", Mainlog);
		Excute.execcmd(udid, "pull /sdcard/CatchLog/. " + Mainlog + "", 3, true);// 1
		if (timeornone.equals("Time")) {
			if (!checkhappentime()) {
				returnstr = "nohappentime";
				return returnstr;
			}
		}

		// Moneky
		// Excute.pullfile("/sdcard/monkeylog.txt", logfolder);
		Excute.execcmd(udid, "pull /sdcard/monkeylog.txt \"" + logfolder + "\"", 3, true);// 3
		// anr
		// Excute.pullfile("/data/anr", ANRlog);
		Excute.execcmd(udid, "pull /data/anr \"" + ANRlog + "\"", 3, true);// 6

		MainRun.mainFrame.progressBarmain.setValue(20);// ******************

		MainRun.mainFrame.progressBarmain.setValue(30);// ******************
		// systemstatus
		Excute.execcmd(udid, "shell ps -t >\"" + Otherlog + "\"/ps.txt", 3, true);// 12
		Excute.execcmd(udid, "shell top -t -m 5 -n 2 >\"" + Otherlog + "\"/top.txt", 3, true);// 13
		MainRun.mainFrame.progressBarmain.setValue(40);// ******************
		Excute.execcmd(udid, "shell cat /proc/meminfo >\"" + Otherlog + "\"/meminfo.txt", 3, true);// 14
		MainRun.mainFrame.progressBarmain.setValue(50);// ******************
		Excute.execcmd(udid, "shell getprop >\"" + Otherlog + "\"/getprop.txt", 3, true);// 18
		Excute.execcmd(udid, "shell service list >\"" + Otherlog + "\"/servicelist.txt", 3, true);// 19
		MainRun.mainFrame.progressBarmain.setValue(60);// ******************

		Excute.execcmd(udid, "shell pm list package >\"" + Otherlog + "\"/pmlist.txt", 3, true);// 22

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

	public String geterrorgetlog() {
		return errorgetlog;
	}

	class GetlogThread implements Runnable {
		// boolean output=false;
		boolean iscompression;
		boolean isdeluseless;
		String timeornone;

		public GetlogThread(boolean iscompression, String timeornone, boolean isdeluseless) {
			this.iscompression = iscompression;
			this.isdeluseless = isdeluseless;
			this.timeornone = timeornone;
		}

		public void run() {
			getlogthreadrun = true;
			errorgetlog = getlog();
			// nohappentime
			if (errorgetlog.equals("nohappentime")) {
				MainRun.mainFrame.progressBarmain.setValue(0);// ******************
				String time = happentime.substring(0, 2) + "-" + happentime.substring(2, 4) + " "
						+ happentime.substring(5, 7) + ":" + happentime.substring(8, 10);
				JOptionPane.showMessageDialog(null, "未在日记中检测到时间:" + time + ",请重新输入正确的问题发生时间点!", "消息",
						JOptionPane.ERROR_MESSAGE);
				logger.info("No happentime : " + time + " in CatchLog, pls try correct time again! ");
				// File file=new File(logfolder);
				HelperUtil.delFolder(logfolder);
				getlogthreadrun = false;
			} else {
				// 压缩文件25
				if (timeornone.equals("Time") && isdeluseless) {
					del_uselessFolder();// 过滤多余日记
				}
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
					// com.Main.ThenToolsRun.logger.log(Level.INFO,targetfilestr.toString()+"
					// "+sourcefilestr.toString()+" "+str.length);
					if (!compression(targetfilestr.toString(), sourcefilestr.toString())) {
						MainRun.mainFrame.progressBarmain.setValue(0);// ******************
						logger.info("Compression logs failed");
						JOptionPane.showMessageDialog(null, "压缩日记失败!", "消息", JOptionPane.ERROR_MESSAGE);
					} else {
						logger.info("Compression logs success");
					}
				}
				MainRun.mainFrame.progressBarmain.setValue(100);// ******************
				logger.info("GetlogThread end,all is ok!");
				getlogthreadrun = false;
				JOptionPane.showMessageDialog(null, "日记保存在" + logfolder + " ", "消息", JOptionPane.INFORMATION_MESSAGE);
			}

		}
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
			String[] result = Excute.execcmd(udid, "zip -r " + tregetfilepath + ".zip " + sourcefilepath, 1, true);
			if (result[0].contains("zip error")) {
				isok = false;
			} else {
				isok = true;
			}
		}
		return isok;
	}

	// get ue current time
	public String[] getUEtime() {
		String[] time = new String[8];
		List<String> timelist = Excute.returnlist2(udid, "date");
		for (String str : timelist) {
			if (str.equals("")) {
				continue;
			}
			time = str.split("\\s+|:");
			// Tue Apr 5 17:40:59 IST 2016
			String Mon = time[1];
			switch (Mon) {
			case "Jan":
				Mon = "01";
				break;
			case "Feb":
				Mon = "02";
				break;
			case "Mar":
				Mon = "03";
				break;
			case "Apr":
				Mon = "04";
				break;
			case "May":
				Mon = "05";
				break;
			case "Jun":
				Mon = "06";
				break;
			case "Jul":
				Mon = "07";
				break;
			case "Aug":
				Mon = "08";
				break;
			case "Sep":
				Mon = "09";
				break;
			case "Oct":
				Mon = "10";
				break;
			case "Nov":
				Mon = "11";
				break;
			case "Dec":
				Mon = "12";
				break;
			default:
				Mon = "00";
				break;
			}
			time[1] = Mon;
			String Day = time[2];
			switch (Day) {
			case "1":
				Day = "01";
				break;
			case "2":
				Day = "02";
				break;
			case "3":
				Day = "03";
				break;
			case "4":
				Day = "04";
				break;
			case "5":
				Day = "05";
				break;
			case "6":
				Day = "06";
				break;
			case "7":
				Day = "07";
				break;
			case "8":
				Day = "08";
				break;
			case "9":
				Day = "09";
				break;
			}
			time[2] = Day;
		}
		return time;
	}

	// 只保留最新日期文件夹
	public void del_uselessFolder() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
		File Mainfile = new File(Mainlog);
		long datetemp = 0, datelatest = 0;
		for (File file : Mainfile.listFiles()) {
			try {
				datetemp = sFormat.parse(file.getName().substring(6, file.getName().length() - 7)).getTime();
				if (datelatest <= datetemp) {
					datelatest = datetemp;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}

		for (File file : Mainfile.listFiles()) {
			try {
				if (datelatest != sFormat.parse(file.getName().substring(6, file.getName().length() - 7)).getTime()) {
					HelperUtil.delFolder(file.getAbsolutePath());
					logger.info("del userless folder=" + file.getAbsolutePath());
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
		del_uselessFile(new File(Mainlog + "/PCtime" + sFormat.format(datelatest) + "_active"));
	}

	// 删除大于发生时间半小时以上的日记
	public void del_uselessFile(File latestFolder) {
		String Mon = happentime.substring(0, 2);// happentime 0503_11H24M
		String Day = happentime.substring(2, 4);
		String Hour = happentime.substring(5, 7);
		String Min = happentime.substring(8, 10);
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
		long datetemp = 0, datelatest = 0;
		for (File file : latestFolder.listFiles()) {
			if (!file.getName().contains("logcatpid")) {
				try {
					datelatest = sFormat
							.parse(file.getName().substring(0, 4) + Mon + Day + "_" + Hour + "_" + Min + "_00")
							.getTime();
					datetemp = sFormat.parse(file.getName().substring(0, 17)).getTime();// 20170428_14_32_54events_log.txt
					if (Math.abs(datelatest - datetemp) > 1000 * 60 * 30) {
						file.delete();
						logger.info("del userless file=" + file.getAbsolutePath());
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
			}
		}
	}

}
