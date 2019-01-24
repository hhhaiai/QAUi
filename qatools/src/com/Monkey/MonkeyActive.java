package com.Monkey;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.Excute;
import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class MonkeyActive {
	Logger logger = LoggerFactory.getLogger(MonkeyActive.class);
	boolean activemonkeythreadrun = false;
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("ssSSS");
	// http://www.cnblogs.com/keke-xiaoxiami/p/3918165.html monkey命令介绍
	MonkeyUImain monkeyUImain;

	public MonkeyActive(MonkeyUImain monkeyUImain) {
		// TODO Auto-generated constructor stub
		this.monkeyUImain = monkeyUImain;
	}

	public void run(String seed, String intervals, String monkeyradio, String packages) {
		// 线程启动
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		ActiveMonkeyThread activemonkeythread = new ActiveMonkeyThread(monkeyUImain.udid, seed, intervals, monkeyradio,
				packages);
		new Thread(activemonkeythread).start();
		logger.info("active monkey");

	}

	// runSystem
	public String runSystem(String seed, String intervals) {
		// echo "monkey -s %seed% --ignore-crashes --ignore-timeouts
		// --ignore-security-exceptions
		// -v -v -v --throttle %t% 1200000000 > /storage/sdcard0/monkeylog.txt 2>&1 &
		// ">tempmonkey.txt
		logger.info("System monkey seed=" + seed + "  intervals=" + intervals);
//		String cmd="echo monkey -s "+seed+" --ignore-crashes --ignore-timeouts --ignore-security-exceptions "
//				+ "-v -v -v --throttle "+intervals+" 1200000000 ^> /sdcard/monkeylog.txt 2^>^&1 ^& >"
//				+QAToolsRun.datalocation+"/tempdata";
//		Excute.execcmd(cmd,1,true);
		String cmd = "monkey -s " + seed
				+ " --ignore-crashes --ignore-timeouts --ignore-security-exceptions --ignore-native-crashes --monitor-native-crashes "
				+ "-v -v -v --throttle " + intervals + " 1200000000 > /sdcard/monkeylog.txt 2>&1 &";
		HelperUtil.file_write_all(MainRun.datalocation + "/tempdata", cmd, false, true);
		return cmd;
	}

	// runPackages
	public String runPackages(String seed, String intervals, String packages) {
		logger.info("Packages monkey seed=" + seed + "  intervals=" + intervals + " packages=" + packages);
//		String cmd="echo monkey -s "+seed+" "+packages+" --ignore-crashes --ignore-timeouts --ignore-security-exceptions "
//				+ "-v -v -v --throttle "+intervals+" 1200000000 ^> /sdcard/monkeylog.txt 2^>^&1 ^& >"
//				+QAToolsRun.datalocation+"/tempdata";
//		Excute.execcmd(cmd,1,true);
		String cmd = "monkey -s " + seed + " " + packages
				+ " --ignore-crashes --ignore-timeouts --ignore-security-exceptions --ignore-native-crashes --monitor-native-crashes "
				+ "-v -v -v --throttle " + intervals + " 1200000000 > /sdcard/monkeylog.txt 2>&1 &";
		HelperUtil.file_write_all(MainRun.datalocation + "/tempdata", cmd, false, true);
		return cmd;
	}

	// runCustomize
	public String runCustomize(String seed, String intervals, String packages) {
		packages = packages.replace("\n", "");
		logger.info("Customize monkey seed=" + seed + "  intervals=" + intervals + " packages=" + packages);
//		String cmd="echo monkey -s "+seed+" "+packages
//				+ " -v -v -v --throttle "+intervals+" 1200000000 ^> /sdcard/monkeylog.txt 2^>^&1 ^& >"
//				+QAToolsRun.datalocation+"/tempdata";
//		Excute.execcmd(cmd,1,true);
		String cmd = "monkey -s " + seed + " " + packages + " -v -v -v --throttle " + intervals
				+ " 1200000000 > /sdcard/monkeylog.txt 2>&1 &";
		HelperUtil.file_write_all(MainRun.datalocation + "/tempdata", cmd, false, true);
		return cmd;
	}

	public boolean getActiveMonkeythreadrun() {
		return activemonkeythreadrun;
	}

	class ActiveMonkeyThread implements Runnable {
		String udid, seed, intervals, monkeyradio, packages;

		public ActiveMonkeyThread(String udid, String seed, String intervals, String monkeyradio, String packages) {
			this.seed = seed;
			this.intervals = intervals;
			this.monkeyradio = monkeyradio;
			this.packages = packages;
			this.udid = udid;
		}

		public void run() {
			// check=null;
			activemonkeythreadrun = true;
			String cmd_monkey = "";
			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
			if (monkeyradio.equals("System")) {
				cmd_monkey = runSystem(seed, intervals);
			} else if (monkeyradio.equals("Packages")) {
				cmd_monkey = runPackages(seed, intervals, packages);
			} else if (monkeyradio.equals("Customize")) {
				cmd_monkey = runCustomize(seed, intervals, packages);
			}
//			try {
//				if(QAToolsRun.getdevices.getDevice()!=null){
//					QAToolsRun.getdevices.getDevice().executeShellCommand(cmd_monkey, new MultiLineReceiver(){
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
			Excute.execcmd(udid, "shell <" + MainRun.datalocation + "/tempdata", 3, false);// run
			try {
				Thread.sleep(1800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MainRun.mainFrame.progressBarmain.setValue(70);// ******************
			Excute.execcmd(udid, "echo \"\">" + MainRun.datalocation + "/tempdata", 1, true);
			if (!CheckUE.checkMonkeyrun(udid)) {
				logger.info("Monkey active failed");
				MainRun.mainFrame.progressBarmain.setValue(0);// ******************
				JOptionPane.showMessageDialog(monkeyUImain, "激活Monkey失败,请再次尝试!\n部分机型无法激活,请使用Monkey监控激活.", "消息",
						JOptionPane.ERROR_MESSAGE);
			} else {
				logger.info("Monkey active successful");
				MainRun.mainFrame.progressBarmain.setValue(100);// ******************
				JOptionPane.showMessageDialog(monkeyUImain,
						"激活 " + monkeyradio + " Monkey成功,种子=" + seed + ",时间间隔=" + intervals + "ms", "消息",
						JOptionPane.INFORMATION_MESSAGE);
				monkeyUImain.getformattedTextFieldSeed().setText(sDateFormat.format(new Date()));
			}
			activemonkeythreadrun = false;
		}
	}

}
