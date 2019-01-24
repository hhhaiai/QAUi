package com.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.helper.ADBUtil;
import com.helper.AndroidInfo;
import com.helper.TimeUtil;
import com.review.getscreen.AndroidShot;
import com.viewer.main.MainRun;

public class LogcatMonitor {
	Logger logger = LoggerFactory.getLogger(LogcatMonitor.class);
	String udid;
	String packagename;
	SceneLogUtil oplog;
	Timer getpid_timer;
	File logcatFolder;
	AndroidShot androidShot;
	// LogcatMonitorThread outputStream;
	boolean syscrash;
	boolean appcrash;
	FileOutputStream applogStream;
	FileOutputStream syslogStream;
	String pid = null;// 应用pid
	boolean isstop = false;
	HashMap<String, String> userlogcatchMap = new HashMap<>();

	public LogcatMonitor(String udid, String packagename, boolean syscrash, boolean appcrash, String userlogcatch,
			SceneLogUtil oplog, File reportFolder, AndroidShot androidShot) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
		this.syscrash = syscrash;
		this.appcrash = appcrash;
		this.packagename = packagename;
		this.oplog = oplog;
		this.androidShot = androidShot;
		if (userlogcatch == null) {
			logger.info("no userlogcatch info");
		} else if (!userlogcatch.equals("") && !userlogcatch.matches(Cconfig.REGEX_FORMAT)) {
			oplog.logError("自定义日志捕获格式错误:" + userlogcatch);
		} else {
			for (String str : userlogcatch.split(";")) {
				String[] strings = str.split("=");
				if (strings.length == 2) {
					userlogcatchMap.put(strings[0].trim(), strings[1].trim());
				}
			}
		}
		logcatFolder = new File(reportFolder.getAbsolutePath() + "/Logs");
		if (!logcatFolder.exists()) {
			logcatFolder.mkdirs();
		}
	}

	/**
	 * 开始执行命令监控日记
	 * 
	 * @param commands
	 * @return
	 */
	public void StartLogMonitor() {
		oplog.logInfo("开始清除logcat缓存...");
		ADBUtil.execcmd(udid, CAndroidCMD.LOGCAT_CLEAR);
		oplog.logInfo("开始监控logcat日记");
		pid = "9999999999999999";// 应用pid
		isstop = false;
		// 定时获取pid
		getpid_timer = new Timer();
		getpid_timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				pid = AndroidInfo.getAppPID(udid, packagename);
			}
		}, 0, 3 * 1000);
		// 日志线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					applogStream = new FileOutputStream(logcatFolder.getAbsolutePath() + "/logcat_Applog.txt", true);
					syslogStream = new FileOutputStream(logcatFolder.getAbsolutePath() + "/logcat_Syslog.txt", true);
					if (MainRun.adbBridge.getDevice(udid) != null) {
						MainRun.adbBridge.getDevice(udid).executeShellCommand(CAndroidCMD.LOGCAT,
								new MultiLineReceiver() {
									String[] linestrs = null;// 07-24 09:54:40.639 4527 4527 E AndroidRuntime: Process:
																// com.main.thenhelper, PID: 4527
									StringBuffer crashbuf = new StringBuffer();
									boolean flag = false;
									String logTAG = "";
									String CatchTAG = "";

									@Override
									public boolean isCancelled() {
										// TODO Auto-generated method stub
										return isstop;
									}

									@Override
									public void processNewLines(String[] lines) {
										// TODO Auto-generated method stub
										try {
											for (String line : lines) { // 将输出的数据缓存起来
												linestrs = line.split("\\s+");
												syslogStream.write((line + "\n").getBytes("UTF-8"));
												if (linestrs.length > 4) {
													if (linestrs[2].equals(pid)) {
														applogStream.write((line + "\n").getBytes("UTF-8"));
													}

													if (CatchTAG.equals("")
															&& line.toLowerCase().contains("exception: ")) {// 识别异常
														logTAG = linestrs[5];
														flag = true;
														if (linestrs[2].equals(pid)) {
															// 判断是否为应用crash
															if (!appcrash) {
																flag = false;
															} else {
																CatchTAG = Cconfig.APPLOG;
															}
															// oplog.logInfo("logcat监控发现[应用]Exception异常:"+line);
														} else {
															if (!syscrash) {
																flag = false;
															} else {
																CatchTAG = Cconfig.SYSLOG;
																// oplog.logInfo("logcat监控发现[系统]Exception异常:"+line);
															}
														}
													} else if (CatchTAG.equals("") && userlogcatchMap.size() > 0) {
														for (Entry<String, String> entry : userlogcatchMap.entrySet()) {
															if (!entry.getValue().equals("")
																	&& line.contains(entry.getValue())) {
																logTAG = linestrs[5];
																flag = true;
																CatchTAG = Cconfig.CUSLOG;
																break;
															}
														}
													}
													if (flag && !line.contains(logTAG)) {// 下一次才写入的
														// logger.info("logTAG="+logTAG);
														// logger.info("logTAG+="+logTAG.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]",
														// ""));
														String time = TimeUtil.getTime4Log();
														Thread.sleep(500);
														switch (CatchTAG) {
														case Cconfig.APPLOG:
															oplog.logExcepitonLog(Cconfig.APPLOG, time,
																	crashbuf.toString(),
																	androidShot.drawText(
																			Cconfig.APPLOG + "-" + logTAG.replaceAll(
																					"[\\s\\\\/:\\*\\?\\\"<>\\|]", "")
																					+ "捕获",
																			"PC时间" + time + "," + Cconfig.APPLOG + "-"
																					+ logTAG + "捕获"));
															break;
														case Cconfig.SYSLOG:
															oplog.logExcepitonLog(Cconfig.SYSLOG, time,
																	crashbuf.toString(),
																	androidShot.drawText(
																			Cconfig.SYSLOG + "-" + logTAG.replaceAll(
																					"[\\s\\\\/:\\*\\?\\\"<>\\|]", "")
																					+ "捕获",
																			"PC时间" + time + "," + Cconfig.SYSLOG + "-"
																					+ logTAG + "捕获"));
															break;
														case Cconfig.CUSLOG:
															oplog.logExcepitonLog(Cconfig.CUSLOG, time,
																	crashbuf.toString(),
																	androidShot.drawText(
																			Cconfig.CUSLOG + "-" + logTAG.replaceAll(
																					"[\\s\\\\/:\\*\\?\\\"<>\\|]", "")
																					+ "捕获",
																			"PC时间" + time + "," + Cconfig.CUSLOG + "-"
																					+ logTAG + "捕获"));
															break;
														default:
															break;
														}
														flag = false;
														CatchTAG = "";
														crashbuf.setLength(0);
													}
													if (flag)
														crashbuf.append(line + "\n");
												}
											}
											applogStream.flush();
											syslogStream.flush();
										} catch (UnsupportedEncodingException e) {
											// TODO Auto-generated catch block
											logger.error("Exception", e);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											logger.error("Exception", e);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											logger.error("Exception", e);
										}
									}
								}, 999999999, TimeUnit.SECONDS);
					}
				} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
						| IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} finally {
					try {
						if (syslogStream != null) {
							syslogStream.close();
							syslogStream = null;
						}
						if (applogStream != null) {
							applogStream.close();
							applogStream = null;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception", e);
					}
					logger.info("Logcat监控已经停止");
					oplog.logInfo("Logcat监控已经停止");
				}
			}
		}).start();

	}

	/**
	 * 停止监控日记
	 */
	public void StopLogMonitor() {
		isstop = true;
		if (getpid_timer != null) {
			getpid_timer.cancel();
			getpid_timer = null;
		}
	}
	// /**
	// * 开始执行命令监控日记
	// * @param commands
	// * @return
	// */
	// public void StartLogMonitor(){
	// Process p = null;
	// ProcessBuilder pb;
	//
	// try {
	// if(MainRun.settingsBean.getSystem()==Cconfig.WINDOWS){
	// p = Runtime.getRuntime().exec(Ccmd.LOGCAT.replaceAll("#udid#",
	// udid).replaceAll("#adb#", MainRun.sysConfigBean.getAndroidSDK_adb()));
	// }else{
	// List<String> list = new ArrayList<String>();
	// list.add("/bin/sh");
	// list.add("-c");
	// list.add(Ccmd.LOGCAT.replaceAll("#udid#", udid).replaceAll("#adb#",
	// MainRun.sysConfigBean.getAndroidSDK_adb()));
	// pb=new ProcessBuilder(list);
	// p = pb.start();
	// }
	// outputStream = new
	// LogcatMonitorThread(p.getInputStream(),udid,syscrash,appcrash,oplog,logcatFolder,androidShot);
	// Thread thread=new Thread(outputStream);
	// thread.start();
	// getpid_timer=new Timer();
	// getpid_timer.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// outputStream.pid=androidAssistant.getAppPID();
	// }
	// }, 0,1*1000);
	// //p.waitFor();
	// } catch (IOException e) {
	// logger.error("Exception",e);
	// } finally{
	// if(p!=null){
	// p.destroy();
	// p=null;
	// pb=null;
	// }
	// }
	// }

	// /**
	// * 停止监控日记
	// */
	// public void StopLogMonitor(){
	// List<String> list=ADBUtil.returnlist(udid,Ccmd.LOGCAT_PID);
	// //List<String> list=CMDUtil.returnlist(Ccmd.LOGCAT_PID.replaceAll("#udid#",
	// udid), Ccmd.SYSCMD, true);
	// for( String str : list){
	// if (str.contains(" ")) {
	// String[] strArray = str.split("\\s+");
	// ADBUtil.execcmd(udid,Ccmd.LOGCAT_STOP.replaceAll("#pid#", strArray[1]));
	// //CMDUtil.execcmd(Ccmd.LOGCAT_STOP.replaceAll("#udid#",
	// udid).replaceAll("#pid#", strArray[1]), Ccmd.SYSCMD, true);
	// logger.info("摧毁adb logcat,pid="+strArray[1]);
	// }
	// }
	// outputStream.stop();//关闭logcat流
	// if(getpid_timer!=null){
	// getpid_timer.cancel();
	// }
	// }
}
/**
 * 捕获logcat流,用于确认异常发生
 *
 */
// class LogcatMonitorThread implements Runnable {
// Logger logger = LoggerFactory.getLogger(LogcatMonitorThread.class);
// InputStream stream;
// FileOutputStream applogStream;
// FileOutputStream syslogStream;
// BufferedReader br;
// String udid;
// SceneLogUtil oplog;
// File logcatFolder;
// AndroidShot androidShot;
// String pid;//应用pid
// boolean syscrash;
// boolean appcrash;
// boolean isstop=false;
// public LogcatMonitorThread(InputStream stream,String udid,boolean
// syscrash,boolean appcrash,SceneLogUtil oplog,File logcatFolder,AndroidShot
// androidShot) {
// this.stream = stream;
// this.udid=udid;
// this.oplog=oplog;
// this.logcatFolder=logcatFolder;
// this.androidShot=androidShot;
// this.syscrash=syscrash;
// this.appcrash=appcrash;
// }
//
// public void stop(){
// this.isstop=true;
// }
// public void run() {
// try {
// StringBuffer crashbuf=new StringBuffer();
// boolean flag=false;
// String logTAG="";
// boolean AppCrashTAG=false;
// br = new BufferedReader( new InputStreamReader(this.stream));
// String line = br.readLine();
// applogStream = new
// FileOutputStream(logcatFolder.getAbsolutePath()+"/Applog.txt",true);
// syslogStream = new
// FileOutputStream(logcatFolder.getAbsolutePath()+"/Syslog.txt",true);
// String[] linestrs;//07-24 09:54:40.639 4527 4527 E AndroidRuntime: Process:
// com.main.thenhelper, PID: 4527
// while (!isstop&&line != null) {
// linestrs=line.split("\\s+");
// syslogStream.write((line+"\n").getBytes("UTF-8"));
// syslogStream.flush();
// if (linestrs.length > 4) {
// if(linestrs[2].equals(pid)){
// applogStream.write((line+"\n").getBytes("UTF-8"));
// applogStream.flush();
// }
//
// if(line.toLowerCase().contains("exception: ")){//识别异常
// logTAG=linestrs[5];
// flag=true;
// if(linestrs[2].equals(pid)){
// //判断是否为应用crash
// if(!appcrash){
// flag=false;
// }else{
// AppCrashTAG=true;
// }
// //oplog.logInfo("logcat监控发现[应用]Exception异常:"+line);
// }else{
// if(!syscrash){
// flag=false;
// }else{
// AppCrashTAG=false;
// //oplog.logInfo("logcat监控发现[系统]Exception异常:"+line);
// }
// }
// }
// if(flag&&!line.contains(logTAG)){//下一次才写入的
// flag=false;
// //logger.info("logTAG="+logTAG);
// //logger.info("logTAG+="+logTAG.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]",
// ""));
// String time=TimeUtil.getTime4Log();
// if(AppCrashTAG){ //\ / : * ? " < > |
// oplog.logApplog(time,crashbuf.toString(),androidShot.drawText(
// "APP-"+logTAG.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]", "")+"异常",
// "PC时间"+time+",APP-"+logTAG+"异常"));
// }else{
// oplog.logSyslog(time,crashbuf.toString(),androidShot.drawText(
// "SYS-"+logTAG.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]", "")+"异常",
// "PC时间"+time+",SYS-"+logTAG+"异常"));
// }
// crashbuf.setLength(0);
// }
// if(flag)crashbuf.append(line+"\n");
//
// }
// line = br.readLine();
// // System.out.println(line);
// }
// } catch (Exception e) {
// logger.error("Exception",e);
// }finally {
// try {
// if (stream != null) {
// stream.close();
// }
// if(br!=null){
// br.close();
// }
// if(applogStream!=null){
// applogStream.flush();
// applogStream.close();
// }
// if(syslogStream!=null){
// syslogStream.flush();
// syslogStream.close();
// }
// } catch (IOException e) {
// // TODO Auto-generated catch block
// logger.error("Exception",e);
// }
// logger.info("Logcat监控已经停止");
// oplog.logInfo("Logcat监控已经停止");
// }
// }

// }
