package com.Logs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class LogsActive {
	Logger logger = LoggerFactory.getLogger(LogsActive.class);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	// private SimpleDateFormat phoneDateFormat = new SimpleDateFormat("mm_ss_SSS");

	String PCtime;
	String adbfile;
	String whatlog = "null";
	public boolean activelogthreadrun = false;
	String udid;

	public void start(String udid) {
		this.udid = udid;
		PCtime = sDateFormat.format(new Date());
		adbfile = "/sdcard/CatchLog/PCtime" + PCtime + "_active";

		// 线程启动
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		ActivelogThread activelogthread = new ActivelogThread();
		new Thread(activelogthread).start();
		logger.info("active log");

	}

	public void activeadb() {
//			Excute.execcmd("mkdir -p "+adbfile,2,true);
//			String cmd_logcat="echo while true;do Datelogcat=`date +%Y%m%d_%H_%M_%S`;"
//					+ "logcat -v threadtime ^>"+adbfile+"/${Datelogcat}main_system_log.txt ^&"
//					+ " echo $! ^>"+adbfile+"/logcatpid.txt;"
//					+ "logcat -v threadtime -b radio ^>"+adbfile+"/${Datelogcat}radio_log.txt ^&"
//					+ " echo $! ^>^>"+adbfile+"/logcatpid.txt;"
//					+ "logcat -v threadtime -b events ^>"+adbfile+"/${Datelogcat}events_log.txt ^& "
//					+ "echo $! ^>^>"+adbfile+"/logcatpid.txt;"
//					+ " sleep 1800;"
//					+ "kill -9 `cat "+adbfile+"/logcatpid.txt`;"
//					+ "done ^& >"+QAToolsRun.datalocation+"/tempdata";
		// Excute.execcmd(cmd_logcat,1,true);

		Excute.execcmd2(udid, "mkdir -p " + adbfile);
		String cmd_logcat = "while true;do Datelogcat=`date +%Y%m%d_%H_%M_%S`;" + "logcat -v threadtime >" + adbfile
				+ "/${Datelogcat}main_system_log.txt &" + " echo $! >" + adbfile + "/logcatpid.txt;"
				+ "logcat -v threadtime -b radio >" + adbfile + "/${Datelogcat}radio_log.txt &" + " echo $! >>"
				+ adbfile + "/logcatpid.txt;" + "logcat -v threadtime -b events >" + adbfile
				+ "/${Datelogcat}events_log.txt & " + "echo $! >>" + adbfile + "/logcatpid.txt;" + " sleep 1800;"
				+ "kill -9 `cat " + adbfile + "/logcatpid.txt`;" + "done & ";
		HelperUtil.file_write_all(MainRun.datalocation + "/tempdata", cmd_logcat, false, true);
//			try {
//				if(QAToolsRun.getdevices.getDevice()!=null){
//					QAToolsRun.getdevices.getDevice().executeShellCommand(cmd_logcat, new MultiLineReceiver(){
//						@Override
//						public boolean isCancelled() {
//							// TODO Auto-generated method stub
//							return false;
//						}
//						@Override
//						public void processNewLines(String[] arg0) {
//							// TODO Auto-generated method stub
//					        for(String line:arg0) { //打印
//					        	System.out.println(line);
//					        }
//						}
//					},1,TimeUnit.SECONDS);
//				}
//			} catch (TimeoutException | AdbCommandRejectedException
//					| ShellCommandUnresponsiveException | IOException e) {
//				// TODO Auto-generated catch block
//				logger.error("Exception",e);
//			}
	}

	public boolean checklogfile() {
		boolean exist = false;
		String adbresult = Excute.execcmd2(udid, "ls " + adbfile).toString();
		if (adbresult.toString().equals("")) {
			// com.Main.ThenToolsRun.logger.log(Level.INFO,"Check file ok
			// "+adbresult[0].toString()+" "+dmesgresult[0].toString()+"
			// "+kmsgresult[0].toString());
			MainRun.mainFrame.progressBarmain.setValue(0);// ******************
			logger.info("Check file not ok: " + adbresult.toString());
			JOptionPane.showMessageDialog(null, "没有在设备中检测到日记文件!", "消息", JOptionPane.ERROR_MESSAGE);
		} else {
			exist = true;
		}

		return exist;
	}

	public boolean checklogfolder() {
		boolean exist;
		if (Excute.execcmd2(udid, "cd /sdcard/CatchLog/").toString().toString().equals("")) {
			// com.Main.ThenToolsRun.logger.log(Level.INFO,"Check file ok
			// "+adbresult[0].toString()+" "+dmesgresult[0].toString()+"
			// "+kmsgresult[0].toString());
			exist = true;
		} else {
			exist = false;
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
			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
			whatlog = " adb ";
			Excute.execcmd(udid, "shell <" + MainRun.datalocation + "/tempdata", 3, false);
			MainRun.mainFrame.progressBarmain.setValue(70);// ******************
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
			Excute.execcmd(udid, "echo \"\">" + MainRun.datalocation + "/tempdata", 1, true);
			if (checklogfile()) {
				MainRun.mainFrame.progressBarmain.setValue(100);// ******************
				logger.info("Active " + whatlog + " logs for successfully!");
				JOptionPane.showMessageDialog(null, "激活 " + whatlog + "日记成功!", "消息", JOptionPane.INFORMATION_MESSAGE);
			} else {
				MainRun.mainFrame.progressBarmain.setValue(0);// ******************
				JOptionPane.showMessageDialog(null, "激活 " + whatlog + "日记失败,请重试!", "消息", JOptionPane.ERROR_MESSAGE);
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
