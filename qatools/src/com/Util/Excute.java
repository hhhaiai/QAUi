package com.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.constant.Cconfig;

public class Excute {
	static Logger logger = LoggerFactory.getLogger(Excute.class);

	public static void pullfile(String udid, String source, String target) {
		if (MainRun.adbBridge.getDevice(udid) != null) {
			try {
				MainRun.adbBridge.getDevice(udid).pullFile(source, target);
			} catch (SyncException | IOException | AdbCommandRejectedException | TimeoutException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// *********************************************************************************************************************
	// cmd2 get info
	public static StringBuffer execcmd2(String udid, String command) {
		final StringBuffer output = new StringBuffer("No device checked!");
		try {
			if (MainRun.adbBridge.getDevice(udid) != null) {
				output.setLength(0);
				MainRun.adbBridge.getDevice(udid).executeShellCommand(command, new MultiLineReceiver() {

					@Override
					public boolean isCancelled() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void processNewLines(String[] arg0) {
						// TODO Auto-generated method stub
						for (String line : arg0) { // 将输出的数据缓存起来
							if (!line.startsWith("* daemon") || !line.startsWith("adb server is out of")) {
								output.append(line).append("\n");
							}
						}
					}

				});
			}
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		// com.Main.ThenToolsRun.logger.log(Level.INFO, output.toString());
		return output;
	}

	public static ArrayList<String> returnlist2(String udid, String command) {
		ArrayList<String> list = new ArrayList<String>();
		String str = execcmd2(udid, command).toString();
		String str1[] = str.split("\n");
		for (int i = 0; i < str1.length; i++) {
			if (!str1[i].equals("")) {
				list.add(str1[i]);
			}
		}
		return list;
	}

	// **************************************************************************************************************

	public static List<String> returnlist(String udid, String command, int which, boolean wait) {
		List<String> list = new ArrayList<String>();
		String str[] = execcmd(udid, command, which, wait);
		String str1[] = str[0].toString().split("\n|\r");
		for (String str3 : str1) {
			// com.Main.ThenToolsRun.logger.log(Level.INFO,str3);
			list.add(str3);
		}
		return list;
	}

	public static String[] execcmd(String udid, String commands, int which, boolean wait) {
		String output[] = { "", "" };
		try {
			Process p;
			ProcessBuilder pb = null;
			if (MainRun.OStype == Cconfig.WINDOWS) {
				if (which == 1) {
					p = Runtime.getRuntime().exec("cmd /c " + commands);
				} else if (which == 2) {
					p = Runtime.getRuntime().exec("cmd /c adb -s \"" + udid + "\" shell \"" + commands + "\"");
				} else if (which == 3) {
					p = Runtime.getRuntime().exec("cmd /c " + "adb -s \"" + udid + "\" " + commands);
				} else {
					logger.info("execcmd error which");
					return output;
				}
			} else {
				List<String> list = new ArrayList<String>();
				list.add("/bin/sh");
				list.add("-c");
				if (which == 1) {
					list.add(commands);
				} else if (which == 2) {
					list.add(
							MainRun.paramsBean.getAndroidSDK_adb() + " -s \"" + udid + "\" shell \"" + commands + "\"");
				} else if (which == 3) {
					list.add(MainRun.paramsBean.getAndroidSDK_adb() + " -s \"" + udid + "\" " + commands);
				} else {
					logger.info("execcmd error which");
					return output;
				}
				pb = new ProcessBuilder(list);
				p = pb.start();
				// if(which==1){
				// List<String> list = new ArrayList<String>();
				// list.add("/bin/sh");
				// list.add("-c");
				// list.add(commands);
				// pb=new ProcessBuilder(list);
				// p = pb.start();
				// }else if(which==2){
				// p=Runtime.getRuntime().exec(QAToolsRun.extraBinlocation+"/adb -s
				// "+QAToolsRun.selectedID+" shell "+commands);
				// }else if(which==3) {
				// p=Runtime.getRuntime().exec(QAToolsRun.extraBinlocation+"/adb -s
				// "+QAToolsRun.selectedID+" "+commands);
				// }else {
				// logger.info("execcmd error which");
				// return output;
				// }

			}
			ExcuteStreamCaptureThread errorStream = new ExcuteStreamCaptureThread(p.getErrorStream());
			ExcuteStreamCaptureThread outputStream = new ExcuteStreamCaptureThread(p.getInputStream());
			new Thread(errorStream).start();
			new Thread(outputStream).start();
			if (wait) {
				p.waitFor();
			}
			String outputString = outputStream.output.toString();
			String errorString = errorStream.output.toString();
			output[0] = outputString;
			output[1] = errorString;
			// logger.info("~~~~~"+outputString);
			// logger.info("@@@@@"+errorString);
		} catch (InterruptedException | IOException e) {
			logger.error("Exception", e);
		}
		return output;
	}

}

class ExcuteStreamCaptureThread implements Runnable {
	Logger logger = LoggerFactory.getLogger(ExcuteStreamCaptureThread.class);
	InputStream stream;
	StringBuffer output;
	BufferedReader br;

	public ExcuteStreamCaptureThread(InputStream stream) {
		this.stream = stream;
		this.output = new StringBuffer();
	}

	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(this.stream));
			String line = br.readLine();
			while (line != null) {
				if (line.trim().length() > 0) {
					output.append(line).append("\n");
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

}
