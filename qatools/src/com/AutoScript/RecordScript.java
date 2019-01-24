package com.AutoScript;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

public class RecordScript {
	Logger logger = LoggerFactory.getLogger(RecordScript.class);
	AutoScriptShotUI autoscriptshotui;
	JTextArea textAreaShowScript;
	boolean autoscriptthreadrun;
	public FBImage mFBImage;
	JLabel lblResolution;

	String x1, y1, x2, y2, temp1, temp2;
	long currenttime = System.currentTimeMillis();
	String tempshow;
	long temptime;
	boolean tapx1 = true, tapy1 = true;
	boolean iscancelled = false;
	// 横竖屏模式
	private boolean mPortrait = true;
	AutoScriptUImain autoScriptUImain;

	public RecordScript(AutoScriptUImain autoScriptUImain) {
		// TODO Auto-generated constructor stub
		this.autoScriptUImain = autoScriptUImain;
		autoscriptshotui = new AutoScriptShotUI(autoScriptUImain);
	}

	public void run() {
		// 线程启动
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		autoscriptthreadrun = true;
		MonitorThread monitorthread = new MonitorThread();
		new Thread(monitorthread).start();
		logger.info("auto change image thread start");
		RecordScriptThread recordscriptthread = new RecordScriptThread();
		new Thread(recordscriptthread).start();
		logger.info("record script thread start");

	}

	public void getTPinfo() {
		try {
			// getevent |grep -E \": 0003 QAToolsRunff)\"
			MainRun.adbBridge.getDevice(autoScriptUImain.udid).executeShellCommand("getevent", new MultiLineReceiver() {
				@Override
				public boolean isCancelled() {
					// TODO Auto-generated method stub
					return iscancelled;
				}

				@Override
				public void processNewLines(String[] arg0) {
					// TODO Auto-generated method stub
					for (String line : arg0) { // 将输出的数据缓存起来
						// System.out.println(line);
						if (line.equals("")) {
							logger.info("plug out usb line for exception");
							iscancelled = true;
						} else {
							String[] splitstr = line.split(" ");
							if (splitstr[2].equals("0035")) {
								if (tapx1) {
									x1 = HEXtoDEC(splitstr[3]);
									tapx1 = false;
								}
								x2 = HEXtoDEC(splitstr[3]);
							} else if (splitstr[2].equals("0036")) {
								if (tapy1) {
									y1 = HEXtoDEC(splitstr[3]);
									tapy1 = false;
								}
								y2 = HEXtoDEC(splitstr[3]);
							}
							// 014a 00000000
							else if (splitstr[2].equals("014a") && splitstr[3].equals("00000000")) {
								ChangeXY();
								if (Math.abs(Integer.parseInt(x1) - Integer.parseInt(x2)) > 25
										|| Math.abs(Integer.parseInt(y1) - Integer.parseInt(y2)) > 25) {
									tempshow = "Drag:(" + x1 + "," + y1 + "),(" + x2 + "," + y2 + "),1000ms\n";
								} else {
									temptime = System.currentTimeMillis() - currenttime;
									if (temptime > 500) {
										tempshow = "Long tap:(" + x2 + "," + y2 + ")," + temptime + "ms,1000ms\n";
									} else {
										tempshow = "Tap:(" + x2 + "," + y2 + "),1000ms\n";
									}
								}
								// add word
								if (autoScriptUImain.udid.equals("test") && x2.equals("900")) {
									if (y2.equals("-160")) {
										textAreaShowScript.append("Press:(BACK),1000ms\n");
									} else if (y2.equals("-10")) {
										textAreaShowScript.append("Press:(MENU),1000ms\n");
									}
								} else {
									textAreaShowScript.append(tempshow);
									autoscriptshotui.drawpicture(tempshow);// draw pictures
								}
								textAreaShowScript.setCaretPosition(textAreaShowScript.getText().length());
								tapx1 = true;
								tapy1 = true;
							} else if (splitstr[2].equals("014a") && splitstr[3].equals("00000001")) {
								currenttime = System.currentTimeMillis();
							}
							// 0039 ffffffff
							else if (splitstr[2].equals("0039") && splitstr[3].equals("ffffffff")) {
								ChangeXY();
								if (Math.abs(Integer.parseInt(x1) - Integer.parseInt(x2)) > 25
										|| Math.abs(Integer.parseInt(y1) - Integer.parseInt(y2)) > 25) {
									tempshow = "Drag:(" + x1 + "," + y1 + "),(" + x2 + "," + y2 + "),1000ms\n";
								} else {
									temptime = System.currentTimeMillis() - currenttime;
									if (temptime > 500) {
										tempshow = "Long tap:(" + x2 + "," + y2 + ")," + temptime + "ms,1000ms\n";
									} else {
										tempshow = "Tap:(" + x2 + "," + y2 + "),1000ms\n";
									}
								}
								// add word
								if (autoScriptUImain.udid.equals("test") && x2.equals("900")) {
									if (y2.equals("-160")) {
										textAreaShowScript.append("Press:(BACK),1000ms\n");
									} else if (y2.equals("-10")) {
										textAreaShowScript.append("Press:(MENU),1000ms\n");
									}
								} else {
									textAreaShowScript.append(tempshow);
									autoscriptshotui.drawpicture(tempshow);// draw pictures
								}
								textAreaShowScript.setCaretPosition(textAreaShowScript.getText().length());
								tapx1 = true;
								tapy1 = true;
							} else if (splitstr[2].equals("0039") && !splitstr[3].equals("ffffffff")
									&& !splitstr[3].equals("00000000")) {
								currenttime = System.currentTimeMillis();// 重启后第一次点击长按无效
							}

						}
					}
					// count++;
					// System.out.println(x+" "+y+" count="+count);
				}

			}, 999999999, TimeUnit.SECONDS);
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
	}

	// landsapce mode x y
	public void ChangeXY() {
		if (!mPortrait) {
			temp1 = x1;
			x1 = y1;
			y1 = (mFBImage.getHeight() - Integer.parseInt(temp1)) + "";
			temp2 = x2;
			x2 = y2;
			y2 = (mFBImage.getHeight() - Integer.parseInt(temp2)) + "";

		}
	}

	// Clear Screen
	public void ClearScreen() {
		MainRun.mainFrame.progressBarmain.setValue(10);// ******************
		textAreaShowScript.setText("**Script interpreter version=1.1**\n");
		autoscriptshotui.setindex(0);
		autoscriptshotui.setFBImage(mFBImage);
		MainRun.mainFrame.progressBarmain.setValue(100);// ******************
	}

	// 16 to 10
	public String HEXtoDEC(String s) {
		return Integer.parseInt(s, 16) + "";
	}

	// get AutoScriptShotUI
	public AutoScriptShotUI getAutoscriptshotui() {
		return autoscriptshotui;
	}

	// set textAreaShowScript
	public void settextAreaShowScript(JTextArea textAreaShowScript) {
		this.textAreaShowScript = textAreaShowScript;
	}

	public boolean getAutoscriptthreadrun() {
		return autoscriptthreadrun;
	}

	// stop getevent
	public void setiscancelled(boolean iscancelled) {
		this.iscancelled = iscancelled;
	}

	// 设置分辨率信息字符串
	public void setlblResolution(JLabel lblResolution) {
		this.lblResolution = lblResolution;
	}

	// 设置横竖屏
	public void setmPortrait(boolean mPortrait) {
		this.mPortrait = mPortrait;
	}

	// change image thread
	class MonitorThread implements Runnable {
		public MonitorThread() {
		}

		public void run() {
			if (MainRun.adbBridge.getDevice(autoScriptUImain.udid) == null) {
				return;
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
			// long time;
			// long imagecurrenttime;
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<FBImage> future;
			while (autoscriptthreadrun) {
				// imagecurrenttime=System.currentTimeMillis();
				try {
					future = executor.submit(new Callable<FBImage>() {// 使用Callable接口作为构造参数
						public FBImage call() throws IOException {
							// 真正的任务在这里执行，这里的返回值类型为String，可以为任意类型
							return getDeviceImage();// 概率卡住
						}
					});
					mFBImage = future.get(3500, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				} catch (java.util.concurrent.TimeoutException e) {
					// TODO Auto-generated catch block
					// timeout
					logger.error("Exception", e);
					mFBImage = null;
				}
//					time=System.currentTimeMillis()-imagecurrenttime;
//					if(time<=500){
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							logger.error("Exception",e);
//						}
//					}
				if (mFBImage == null && MainRun.adbBridge.getDevice(autoScriptUImain.udid) != null) {
					logger.info("get mFBImage timeout, restart!");
					autoscriptthreadrun = true;
					MonitorThread monitorthread = new MonitorThread();
					new Thread(monitorthread).start();
					break;
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							autoscriptshotui.setFBImage(mFBImage);
						}
					});
				}
			}
		}
	}

	// record thread
	class RecordScriptThread implements Runnable {

		public RecordScriptThread() {

		}

		public void run() {
			autoscriptthreadrun = true;
			mFBImage = getDeviceImage();
			iscancelled = false;
			// logger.info("image y="+mFBImage.getHeight()+",x="+mFBImage.getWidth());
			// textAreaShowScript.setText("**Script interpreter version=1.0**\n");
			autoscriptshotui.setindex(0);
			// autoscriptshotui.setFBImage(mFBImage);
			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
			JOptionPane.showMessageDialog(autoScriptUImain, "开始录制脚本,请操作你的设备...", "消息", JOptionPane.INFORMATION_MESSAGE);
			lblResolution.setText("分辨率: " + mFBImage.getHeight() + "X" + mFBImage.getWidth());
			MainRun.mainFrame.progressBarmain.setValue(100);// ******************
			getTPinfo();// start get tp info
			autoscriptthreadrun = false;
			iscancelled = true;
			logger.info("auto script and get image ok");
		}
	}

	// getFBimage
	private FBImage getDeviceImage() {
		boolean success = true;
		boolean debug = false;
		FBImage fbImage = null;
		RawImage tmpRawImage = null;
		RawImage rawImage = null;

		if (success) {
			try {
				tmpRawImage = MainRun.adbBridge.getDevice(autoScriptUImain.udid).getScreenshot();

				if (tmpRawImage == null) {
					success = false;
				} else if (!debug) {
					rawImage = tmpRawImage;
				} else {
					rawImage = new RawImage();
					rawImage.version = 1;
					rawImage.bpp = 32;
					rawImage.size = (tmpRawImage.width * tmpRawImage.height * 4);
					rawImage.width = tmpRawImage.width;
					rawImage.height = tmpRawImage.height;
					rawImage.red_offset = 0;
					rawImage.red_length = 8;
					rawImage.blue_offset = 16;
					rawImage.blue_length = 8;
					rawImage.green_offset = 8;
					rawImage.green_length = 8;
					rawImage.alpha_offset = 0;
					rawImage.alpha_length = 0;
					rawImage.data = new byte[rawImage.size];

					int index = 0;
					int dst = 0;
					for (int y = 0; y < rawImage.height; ++y) {
						for (int x = 0; x < rawImage.width; ++x) {
							int value = tmpRawImage.data[(index++)] & 0xFF;
							value |= tmpRawImage.data[(index++)] << 8 & 0xFF00;
							int r = (value >> 11 & 0x1F) << 3;
							int g = (value >> 5 & 0x3F) << 2;
							int b = (value >> 0 & 0x1F) << 3;

							rawImage.data[(dst++)] = (byte) r;
							rawImage.data[(dst++)] = (byte) g;
							rawImage.data[(dst++)] = (byte) b;
							rawImage.data[(dst++)] = -1;
						}
					}

				}

			} catch (IOException | TimeoutException | AdbCommandRejectedException e) {
				logger.error("Exception", e);
			} finally {
				if ((rawImage == null) || ((rawImage.bpp != 16) && (rawImage.bpp != 32))) {
					success = false;
				}
			}
		}
		if (success) {
			int imageHeight;
			int imageWidth;

			if (!autoScriptUImain.udid.equals("test")) {
				imageWidth = rawImage.width;
				imageHeight = rawImage.height;
			} else {
				imageWidth = rawImage.height;
				imageHeight = rawImage.width;
			}

			fbImage = new FBImage(imageWidth, imageHeight, 1, rawImage.width, rawImage.height);
			byte[] buffer = rawImage.data;
			int redOffset = rawImage.red_offset;
			int greenOffset = rawImage.green_offset;
			int blueOffset = rawImage.blue_offset;
			int alphaOffset = rawImage.alpha_offset;
			int redMask = getMask(rawImage.red_length);
			int greenMask = getMask(rawImage.green_length);
			int blueMask = getMask(rawImage.blue_length);
			int alphaMask = getMask(rawImage.alpha_length);
			int redShift = 8 - rawImage.red_length;
			int greenShift = 8 - rawImage.green_length;
			int blueShift = 8 - rawImage.blue_length;
			int alphaShift = 8 - rawImage.alpha_length;

			int index = 0;
			if (rawImage.bpp == 16) {
				int offset1;
				int offset0;
				offset0 = 0;
				offset1 = 1;
				if (!autoScriptUImain.udid.equals("test")) {
					for (int y = 0; y < rawImage.height; ++y)
						for (int x = 0; x < rawImage.width; ++x) {
							int value = buffer[(index + offset0)] & 0xFF;
							value |= buffer[(index + offset1)] << 8 & 0xFF00;
							int r = (value >>> redOffset & redMask) << redShift;
							int g = (value >>> greenOffset & greenMask) << greenShift;
							int b = (value >>> blueOffset & blueMask) << blueShift;
							value = 0xFF000000 | r << 16 | g << 8 | b;
							index += 2;
							fbImage.setRGB(x, y, value);
						}
				} else {
					for (int y = 0; y < rawImage.height; ++y)
						for (int x = 0; x < rawImage.width; ++x) {
							int value = buffer[(index + offset0)] & 0xFF;
							value |= buffer[(index + offset1)] << 8 & 0xFF00;
							int r = (value >>> redOffset & redMask) << redShift;
							int g = (value >>> greenOffset & greenMask) << greenShift;
							int b = (value >>> blueOffset & blueMask) << blueShift;
							value = 0xFF000000 | r << 16 | g << 8 | b;
							index += 2;
							fbImage.setRGB(y, rawImage.width - x - 1, value);
						}
				}
			} else if (rawImage.bpp == 32) {
				int offset3;
				int offset0;
				int offset1;
				int offset2;
				offset0 = 0;
				offset1 = 1;
				offset2 = 2;
				offset3 = 3;

				if (!autoScriptUImain.udid.equals("test")) {
					for (int y = 0; y < rawImage.height; ++y)
						for (int x = 0; x < rawImage.width; ++x) {
							int value = buffer[(index + offset0)] & 0xFF;
							value |= (buffer[(index + offset1)] & 0xFF) << 8;
							value |= (buffer[(index + offset2)] & 0xFF) << 16;
							value |= (buffer[(index + offset3)] & 0xFF) << 24;
							int r = (value >>> redOffset & redMask) << redShift;
							int g = (value >>> greenOffset & greenMask) << greenShift;
							int b = (value >>> blueOffset & blueMask) << blueShift;
							int a;
							if (rawImage.alpha_length == 0)
								a = 255;
							else {
								a = (value >>> alphaOffset & alphaMask) << alphaShift;
							}
							value = a << 24 | r << 16 | g << 8 | b;
							index += 4;
							fbImage.setRGB(x, y, value);
						}
				} else {
					for (int y = 0; y < rawImage.height; ++y) {
						for (int x = 0; x < rawImage.width; ++x) {
							int value = buffer[(index + offset0)] & 0xFF;
							value |= (buffer[(index + offset1)] & 0xFF) << 8;
							value |= (buffer[(index + offset2)] & 0xFF) << 16;
							value |= (buffer[(index + offset3)] & 0xFF) << 24;
							int r = (value >>> redOffset & redMask) << redShift;
							int g = (value >>> greenOffset & greenMask) << greenShift;
							int b = (value >>> blueOffset & blueMask) << blueShift;
							int a;
							if (rawImage.alpha_length == 0)
								a = 255;
							else {
								a = (value >>> alphaOffset & alphaMask) << alphaShift;
							}
							value = a << 24 | r << 16 | g << 8 | b;
							index += 4;
							fbImage.setRGB(y, rawImage.width - x - 1, value);
						}
					}
				}
			}
		}
		return fbImage;
	}

	public int getMask(int length) {
		int res = 0;
		for (int i = 0; i < length; ++i) {
			res = (res << 1) + 1;
		}

		return res;
	}

}
