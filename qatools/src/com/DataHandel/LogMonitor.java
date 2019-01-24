package com.DataHandel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Util.HelperUtil;
import com.Viewer.MainRun;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;

import javafx.application.Platform;
import javafx.scene.layout.VBox;

public class LogMonitor {
	Logger logger = LoggerFactory.getLogger(LogMonitor.class);
	String udid = "";
	String deviceOS;
	boolean isstop = true;
	VBox vbox_chart;
	DataFilter dataFilter;
	IOSIfuseContainer ifuseContainer;

	Timer timer_statistics;
	File logfile;

	public LogMonitor(String udid, String deviceOS, VBox vbox_chart) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
		this.deviceOS = deviceOS;
		this.vbox_chart = vbox_chart;
		dataFilter = new DataFilter(vbox_chart);
	}

	/**
	 * 开始监控
	 * 
	 * @param settingsMap
	 */
	public void start(List<Map<String, String>> settingsMapList) {
		settingsMapList.forEach(e -> e.put(Cconfig.DATAHANDEL_SETTINGS_TYPE, Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR));
		dataFilter.setSettingsMapList(settingsMapList);
		isstop = false;
		String select_path = null;
		if (deviceOS.equals(Cconfig.IOS)) {
			String[] path_strings = MainRun.paramsBean.getIOS_Logs_App_path().split(";");
			select_path = AlertBoxFXUI.showOptionDialog("请选择应用", "请选择需要监控的应用,及其日志路径", Arrays.asList(path_strings));
		} else {

		}
		new Thread(new DataMonitorRunnable(select_path)).start();
	}

	/**
	 * 停止监控
	 */
	public void stop() {
		isstop = true;
		if (ifuseContainer != null) {
			ifuseContainer.stop();
		}
	}

	/**
	 * 获取数据过滤器
	 * 
	 * @return
	 */
	public DataFilter getDataFilter() {
		return dataFilter;
	}

	/**
	 * 设置保存日志文件
	 * 
	 * @param logfile
	 */
	public void setSaveLog(File logfile) {
		this.logfile = logfile;
	}

	/**
	 * 数据监控线程
	 * 
	 * @author auto
	 *
	 */
	class DataMonitorRunnable implements Runnable {
		String select_path;

		public DataMonitorRunnable(String select_path) {
			// TODO Auto-generated constructor stub
			this.select_path = select_path;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (deviceOS.equals(Cconfig.ANDROID)) {
				AndroidLogMonitor();
			} else {
				int result = iOSLogMonitor(select_path);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (result == 0) {

						} else if (result == -1) {
							AlertBoxFXUI.showMessageDialogError("错误", "未找到日志文件,请先启动应用后重试!");
						} else if (result == -2) {
							AlertBoxFXUI.showMessageDialogError("错误", "挂载应用文件失败!");
						} else if (result == -3) {
							AlertBoxFXUI.showMessageDialogError("请先在iOS设置中设置", "应用名及沙盒日志路径格式不正确!");
						}
					}
				});

			}
			stop();// 强制结束
		}

	}

	/**
	 * ios 获取日志
	 */
	private int iOSLogMonitor(String select_path) {
		ifuseContainer = new IOSIfuseContainer(udid) {
			@Override
			public void readline(String line) {
				// TODO Auto-generated method stub
				if (dataFilter.filterLine(line) && logfile != null) {
					HelperUtil.file_write_line(logfile.getAbsolutePath(), line + "\r\n", true);
				}
			}
		};
		if (select_path == null) {
			logger.error("cancel select app path");
			return 0;
		} else if (select_path.contains(",")) {
			ifuseContainer.setIOSpackagename(select_path.split(",")[0]);
			ifuseContainer.setLogpath(select_path.split(",")[1]);
			int i = ifuseContainer.monitorLog(true);
			logger.info("ifuseContainer return value=" + i);
			return i;

		} else {
			logger.error("select app path error");
			return -3;
		}
	}

	/**
	 * android 获取日志
	 */
	private void AndroidLogMonitor() {
		try {
			do {
				if (MainRun.adbBridge.getDevice(udid) != null) {
					String command = "logcat -v threadtime";
					Excute.execcmd2(udid, "logcat -c");// 清除缓存
					MainRun.adbBridge.getDevice(udid).executeShellCommand(command, new MultiLineReceiver() {

						@Override
						public boolean isCancelled() {
							// TODO Auto-generated method stub
							return isstop;
						}

						@Override
						public void processNewLines(String[] lines) {
							// TODO Auto-generated method stub
							for (String line : lines) { // 将输出的数据缓存起来
								if (dataFilter.filterLine(line) && logfile != null) {
									HelperUtil.file_write_line(logfile.getAbsolutePath(), line + "\r\n", true);
								}
							}
						}

					}, 999999999, TimeUnit.SECONDS);
				}
				logger.info("data handel android monitor exit ");
			} while (!isstop && MainRun.adbBridge.getDevice(udid) != null);
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		} finally {

		}
	}

}
