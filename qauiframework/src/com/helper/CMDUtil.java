package com.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.viewer.main.MainRun;

public class CMDUtil {
	static Logger logger = LoggerFactory.getLogger(CMDUtil.class);
	final public static int SYSCMD = 1;
	final public static int CUSCMD = 2;

	/**
	 * 执行命令,返回list
	 * 
	 * @param command
	 * @param which
	 * @param wait
	 * @return
	 */
	public static List<String> returnlist(String command, int which, boolean wait) {
		List<String> list = new ArrayList<String>();
		String strs[] = execcmd(command, which, wait);
		String lines[] = strs[0].toString().split("\n|\r");
		for (String line : lines) {
			if (!line.equals(""))
				list.add(line);
		}
		return list;
	}

	/**
	 * 执行命令,返回正常String[0],异常String[1]数组 Ccmd.SYSCMD=1:系统命令,Ccmd.CUSCMD=2:自带命令
	 * 
	 * @param commands
	 * @param which
	 * @param wait
	 * @return
	 */
	public static String[] execcmd(String commands, int which, boolean wait) {
		String output[] = { "", "" };
		Process p = null;
		ProcessBuilder pb;
		try {
//			commands=commands.replaceAll("#adb#", MainRun.sysConfigBean.getAndroidSDK_adb())
//					.replaceAll("#aapt#", MainRun.settingsBean.getExtraBinlocation()+"/"+"aapt.exe");
			if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
				commands = commands.replaceAll("#adb#", "adb").replaceAll("#aapt#",
						MainRun.settingsBean.getExtraBinlocation() + "/" + "aapt.exe");
				if (which == CAndroidCMD.SYSCMD) {
					p = Runtime.getRuntime().exec("cmd /c " + commands);
				} else if (which == CAndroidCMD.CUSCMD) {
					p = Runtime.getRuntime().exec("cmd /c " + commands);
				} else {
					logger.info("execcmd error which=" + which);
					return output;
				}
			} else {
				List<String> list = new ArrayList<String>();
				list.add("/bin/sh");
				list.add("-c");
				commands = commands.replaceAll("#adb#", MainRun.sysConfigBean.getAndroidSDK_adb()).replaceAll("#aapt#",
						MainRun.settingsBean.getExtraBinlocation() + "/" + "aapt");
				if (which == CAndroidCMD.SYSCMD) {
					list.add(commands);
				} else if (which == CAndroidCMD.CUSCMD) {
					list.add(commands);
					// logger.info(MainRun.settingsBean.getExtraBinlocation()+"/"+commands);
				} else {
					logger.info("execcmd error which=" + which);
					return output;
				}
				pb = new ProcessBuilder(list);
				p = pb.start();
			}
			StreamCaptureThread errorStream = new StreamCaptureThread(p.getErrorStream(), wait);
			StreamCaptureThread outputStream = new StreamCaptureThread(p.getInputStream(), wait);
			new Thread(errorStream).start();
			new Thread(outputStream).start();
			if (wait)
				p.waitFor();
			output[0] = outputStream.output.toString();
			output[1] = errorStream.output.toString();
//			logger.info("@@@@"+commands);
//			logger.info("~~~~"+output[0]);
//			logger.info("****"+output[1]);
		} catch (InterruptedException | IOException e) {
			logger.error("Exception", e);
		}
//		finally{
//			if(p!=null){
//				//p.destroy();
//				p=null;
//				pb=null;
//			}
//		}
		return output;
	}

}

class StreamCaptureThread implements Runnable {
	Logger logger = LoggerFactory.getLogger(StreamCaptureThread.class);
	InputStream stream;
	StringBuffer output;
	BufferedReader br;
	boolean wait;

	public StreamCaptureThread(InputStream stream, boolean wait) {
		this.stream = stream;
		this.wait = wait;
		this.output = new StringBuffer();
	}

	public void run() {
		try {
			if (!wait)
				return;
			br = new BufferedReader(new InputStreamReader(this.stream));
			String line = br.readLine();
			while (line != null) {
				if (line.trim().length() > 0) {
					// System.out.println("%%%%%"+line);
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
