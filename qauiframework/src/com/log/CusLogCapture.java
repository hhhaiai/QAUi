package com.log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.constant.CAndroidCMD;
import com.helper.ADBUtil;
import com.helper.HelperUtil;
import com.helper.TimeUtil;
import com.viewer.main.MainRun;

public class CusLogCapture {
	Logger logger = LoggerFactory.getLogger(CusLogCapture.class);
	String udid;
	SceneLogUtil oplog;
	File logcatFolder;
	Map<String, File> filterMap = new HashMap<>();
	boolean isstop = false;
	String devicesOS;

	public CusLogCapture(String udid, String devicesOS, SceneLogUtil oplog, File reportFolder) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
		this.oplog = oplog;
		this.devicesOS = devicesOS;
		logcatFolder = new File(reportFolder.getAbsolutePath() + "/Logs");
		if (!logcatFolder.exists()) {
			logcatFolder.mkdirs();
		}
	}

	/**
	 * 通过正则打印日志
	 * 
	 * @param regex
	 * @param filename
	 */
	public void captureByRegex(String regex, String filename) {
		File file = createFile(filename);
		if (file != null) {
			filterMap.put(regex, file);
			oplog.logInfo("增加自定义捕获日志:regex=" + regex + ",filename=" + filename);
		}
	}

	/**
	 * 通过包含key打印日志
	 * 
	 * @param key
	 * @param filename
	 */
	public void captureByContain(String key, String filename) {
		File file = createFile(filename);
		if (file != null) {
			String regex = ".*?" + key + ".*";
			filterMap.put(regex, file);
			oplog.logInfo("增加自定义捕获日志:contain=" + key + ",filename=" + filename);
		}
	}

	/**
	 * 开始打印
	 */
	public void captureStart() {
		isstop = false;
		oplog.logInfo("开始自定义捕获日志");
		ADBUtil.execcmd(udid, CAndroidCMD.LOGCAT_CLEAR);
		new Thread(new captureLogRunnable()).start();
	}

	/**
	 * 停止打印
	 */
	public void captureStop() {
		oplog.logInfo("停止自定义捕获日志");
		isstop = true;
	}

	/**
	 * 创建日志文件
	 * 
	 * @param filename
	 * @return
	 */
	private File createFile(String filename) {
		File file;
		if (filename == null) {
			file = new File(logcatFolder.getAbsolutePath() + "/" + TimeUtil.getTime4File() + ".txt");
		} else {
			file = new File(logcatFolder.getAbsolutePath() + "/" + filename);
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
				oplog.logError("无法创建日志文件:" + file.getAbsolutePath());
			}
		}
		if (file.exists() && file.isFile()) {
			return file;
		}
		return null;
	}

	/**
	 * 捕获日志
	 * 
	 * @author auto
	 *
	 */
	class captureLogRunnable implements Runnable {

		public captureLogRunnable() {
			// TODO Auto-generated constructor stub

		}

		@Override
		public void run() {
			if (MainRun.adbBridge.getDevice(udid) != null) {
				do {
					try {
						MainRun.adbBridge.getDevice(udid).executeShellCommand(CAndroidCMD.LOGCAT,
								new MultiLineReceiver() {

									@Override
									public boolean isCancelled() {
										// TODO Auto-generated method stub
										return isstop;
									}

									@Override
									public void processNewLines(String[] lines) {
										// TODO Auto-generated method stub
										for (String line : lines) {
											for (Entry<String, File> entry : filterMap.entrySet()) {
												if (line.matches(entry.getKey())) {// 符合本正则
													HelperUtil.file_write_line(entry.getValue().getAbsolutePath(),
															line + "\r\n", true);
												}
											}
										}
									}

								}, 999999999, TimeUnit.SECONDS);
					} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
							| IOException e) {
						// TODO Auto-generated catch block
						logger.error("EXCEPTION", e);
						oplog.logError("用户自定义捕获日志出现异常!");
					}
				} while (!isstop && MainRun.adbBridge.getDevice(udid) != null);

			}
		}
	}
}
