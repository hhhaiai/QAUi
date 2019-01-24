package com.GetScreen;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ScreenRecorderOptions;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;

public class ScreenRecord {
	Logger logger = LoggerFactory.getLogger(ScreenRecord.class);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	int bitrate = 8;
	int width = 480;
	int height = 800;
	boolean iscancel = false;
	boolean VideofromUEthreadrun = false;
	String videotime;
	String udid;

	public void run(String udid, String WidthandHeight) {
		this.udid = udid;
		int[] size = getWidthandHeight(WidthandHeight);
		width = size[0];
		height = size[1];
		setiscancel(false);
		videotime = sDateFormat.format(new Date());
		File screenfolder = new File(MainRun.QALogfile + "/ScreenRecord");
		if (!screenfolder.exists()) {
			screenfolder.mkdirs();
		}
		// 线程启动
		StartRecordThread startrecordthread = new StartRecordThread();
		new Thread(startrecordthread).start();
		logger.info("start startrecordthread");
	}

	public void setiscancel(boolean iscancel) {
		this.iscancel = iscancel;
	}

	public void startrecord() {
		try {
			ScreenRecorderOptions options = new ScreenRecorderOptions.Builder().setBitRate(bitrate)
					.setSize(width, height).build();
			MainRun.adbBridge.getDevice(udid).executeShellCommand(
					"if [ ! -d \"/sdcard/Movies\" ];then mkdir /sdcard/Movies;fi", new IShellOutputReceiver() {

						@Override
						public void addOutput(byte[] arg0, int arg1, int arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void flush() {
							// TODO Auto-generated method stub

						}

						@Override
						public boolean isCancelled() {
							// TODO Auto-generated method stub
							return false;
						}
					});

			MainRun.adbBridge.getDevice(udid).startScreenRecorder("/sdcard/Movies/Record_PCtime" + videotime + ".mp4",
					options, new IShellOutputReceiver() {
						@Override
						public boolean isCancelled() {
							return iscancel;
						}

						@Override
						public void flush() {
							logger.info("screen record flush");
						}

						@Override
						public void addOutput(byte[] data, int offset, int length) {
							String Message;
							if (data != null) {
								Message = new String(data);
							} else {
								Message = "";
							}
							logger.info(Message);
						}
					});

		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (ShellCommandUnresponsiveException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
	}

	// get video from ue
	public void getVideofromUE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					MainRun.mainFrame.progressBarmain.setValue(10);// ******************
					VideofromUEthreadrun = true;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						logger.error("Exception", e);
					}
					if (videotime != null) {
						MainRun.mainFrame.progressBarmain.setValue(30);// ******************
						MainRun.adbBridge.getDevice(udid).pullFile("/sdcard/Movies/Record_PCtime" + videotime + ".mp4",
								MainRun.QALogfile + "/ScreenRecord/Record_PCtime" + videotime + ".mp4");
						logger.info("get record finished");
						MainRun.mainFrame.progressBarmain.setValue(100);// ******************
						JOptionPane.showMessageDialog(null, "屏幕录制完成!\n视频保存在" + MainRun.QALogfile + "/ScreenRecord"
								+ "/Record_PCtime" + videotime + ".mp4", "消息", JOptionPane.INFORMATION_MESSAGE);
					} else {
						MainRun.mainFrame.progressBarmain.setValue(0);// ******************
						JOptionPane.showMessageDialog(null, "请先开始录制!", "消息", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SyncException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (AdbCommandRejectedException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
				VideofromUEthreadrun = false;
			}
		}).start();
	}

	public int[] getWidthandHeight(String str) {
		switch (str) {
		case "WVGA 480X800":
			return new int[] { 480, 800 };
		case "FHD 1080X1920":
			return new int[] { 1080, 1920 };
		case "HD 720x1280":
			return new int[] { 720, 1280 };
		case "QHD 540X960":
			return new int[] { 540, 960 };
		case "FWVGA 480X854":
			return new int[] { 480, 854 };
		case "HVGA 320X480":
			return new int[] { 320, 480 };
		default:
			return new int[] { 480, 800 };
		}
	}

	//
	public boolean getVideofromUEthreadrun() {
		return VideofromUEthreadrun;
	}

	class StartRecordThread implements Runnable {
		public StartRecordThread() {
		}

		public void run() {
			startrecord();
			logger.info("screen record finished");
		}
	}

}
