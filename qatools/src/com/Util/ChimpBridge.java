package com.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.AutoScript.SilentLog;
import com.Viewer.MainRun;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.PhysicalButton;
import com.android.chimpchat.core.TouchPressType;

public class ChimpBridge {
	Logger logger = LoggerFactory.getLogger(ChimpBridge.class);
	private IChimpDevice ichimpDevice;
	// private AdbChimpDevice adbchimpdevice;
	boolean isok = true;

	private SimpleDateFormat sDateFormatget = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	// button
	PhysicalButton physicalbutton;
	String udid;

	public void connect(String udid) {
		// adbBack = new AdbBackend();
//		TreeMap<String, String> options = new TreeMap<String, String>();  
//        options.put("backend", "adb");  
//        options.put("adbLocation", QAToolsRun.extraBinlocation+"/adb.exe");  
//        mChimpchat = ChimpChat.getInstance(options);  
//        mChimpDevice = mChimpchat.waitForConnection(); 
		// adbchimpdevice=new AdbChimpDevice(QAToolsRun.getdevices.getDevice());
		this.udid = udid;
		if (CheckUE.checkDevice(udid)) {
			cancel();
			try {
				try {
					ichimpDevice = new AdbChimpDevice(MainRun.adbBridge.getDevice(udid));
				} catch (Exception e) {
					logger.info("connect fail");
					JOptionPane.showMessageDialog(MainRun.mainFrame, "Connect failed, pls press ok to try again!",
							"Message", JOptionPane.ERROR_MESSAGE);
					Thread.sleep(3000);
					try {
						ichimpDevice = new AdbChimpDevice(MainRun.adbBridge.getDevice(udid));
					} catch (Exception ee) {
						logger.info("connect chimpbridge device fail");
						JOptionPane.showMessageDialog(MainRun.mainFrame, "Connect device failed, pls check!", "Message",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
			// ichimpDevice=adbchimpdevice;
		} else {
			logger.info("no devices checked!");
			JOptionPane.showMessageDialog(MainRun.mainFrame, "no devices checked!", "Message",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setOK(boolean isok) {
		this.isok = isok;
	}

	// wake
	public void Wake(String wait) {
		if (isok) {
			ichimpDevice.wake();

			try {
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Touch
	public void Tap(String x, String y, String wait) {
		if (isok) {
			try {
				ichimpDevice.getManager().touch(Integer.parseInt(x), Integer.parseInt(y));
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Long Touch
	public void LongTap(String x, String y, String time, String wait) {
		if (isok) {
			try {
				ichimpDevice.getManager().touchDown(Integer.parseInt(x), Integer.parseInt(y));
				Thread.sleep(Integer.parseInt(time));
				ichimpDevice.getManager().touchUp(Integer.parseInt(x), Integer.parseInt(y));
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (IOException | NumberFormatException | InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Drag
	int stepx, stepy, step;

	public void Drag(String x1, String y1, String x2, String y2, String wait) {
		if (isok) {
			stepx = Math.abs(Integer.parseInt(x1) - Integer.parseInt(x2));
			stepy = Math.abs(Integer.parseInt(y1) - Integer.parseInt(y2));
			if (stepx > stepy) {
				step = stepx;
			} else {
				step = stepy;
			}
			ichimpDevice.drag(Integer.parseInt(x1), Integer.parseInt(y1), Integer.parseInt(x2), Integer.parseInt(y2),
					step / 25, step / 2);
			try {
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Press button
	public void Pressbutton(String button, String wait) {
		if (isok) {
			try {
				if (button.equals("HOME")) {
					ichimpDevice.getManager().press(PhysicalButton.HOME);
				} else if (button.equals("BACK")) {
					ichimpDevice.getManager().press(PhysicalButton.BACK);
				} else if (button.equals("MENU")) {
					ichimpDevice.getManager().press(PhysicalButton.MENU);
				} else if (button.equals("VOLUME_UP")) {
					ichimpDevice.press("KEYCODE_VOLUME_UP", TouchPressType.DOWN_AND_UP);
				} else if (button.equals("VOLUME_DOWN")) {
					ichimpDevice.press("KEYCODE_VOLUME_DOWN", TouchPressType.DOWN_AND_UP);
				} else if (button.equals("POWER")) {
					ichimpDevice.press("KEYCODE_POWER", TouchPressType.DOWN_AND_UP);
				}
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// sleep
	public void Sleep(String time) {
		if (isok) {
			try {
				Thread.sleep(Integer.parseInt(time));
			} catch (NumberFormatException | InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Screen cap
	public void Screencap(String wait) {
		if (isok) {
			BufferedImage image = ichimpDevice.takeSnapshot().createBufferedImage();
			File file = new File(MainRun.QALogfile + "/Script/ScreenCap/Script_PCtime_"
					+ sDateFormatget.format(new Date()) + ".png");
			try {
				ImageIO.write(image, "png", file);
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Type str
	public void Type(String str, String wait) {
		if (isok) {
			try {
				ichimpDevice.getManager().type(str);
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// start activity
	public void StartActivity(String activity, String wait) {
		if (isok) {
			/**
			 * Send a broadcast intent to the device.
			 * 
			 * @param uri        the URI for the Intent
			 * @param action     the action for the Intent
			 * @param data       the data URI for the Intent
			 * @param mimeType   the mime type for the Intent
			 * @param categories the category names for the Intent
			 * @param extras     the extras to add to the Intent
			 * @param component  the component of the Intent
			 * @param flags      the flags for the Intent
			 */
			try {
				// 添加启动权限
				Collection<String> categories = new ArrayList<String>();
				categories.add("android.intent.category.LAUNCHER");
				// 启动要测试的主界面
				ichimpDevice.startActivity(null, "android.intent.action.MAIN", null, null, categories,
						new HashMap<String, Object>(), activity, 0);
				int ms = Integer.parseInt(wait);
				if (ms > 0) {
					Thread.sleep(ms);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Start Log
	public void Startlog() {
		if (isok) {
			SilentLog silentlog = new SilentLog();
			silentlog.start(udid);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// Reboot
	public void Reboot(String time) {
		if (isok) {
			try {
				ichimpDevice.reboot("Script needs reboot");
				Thread.sleep(Integer.parseInt(time));
				while (MainRun.adbBridge.getDevice(udid) == null || MainRun.selectedID == null) {
					Thread.sleep(1000);
				}
				String[] result;
				do {
					result = Excute.execcmd("", MainRun.extraBinlocation + "/adb.exe -s " + udid + " shell cd /sdcard",
							1, true);
					Thread.sleep(1000);
				} while (!result[0].equals(""));
				try {
					ichimpDevice = new AdbChimpDevice(MainRun.adbBridge.getDevice(udid));
				} catch (Exception e) {
					logger.info("connect fail");
					Thread.sleep(15000);
					ichimpDevice = new AdbChimpDevice(MainRun.adbBridge.getDevice(udid));
				}
				Wake(0 + "");
			} catch (NumberFormatException | InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
	}

	// end
	public void cancel() {
		if (ichimpDevice != null) {
			logger.info("ichimpDevice dispose");
			ichimpDevice.dispose();
			ichimpDevice = null;
		}
//		if(adbchimpdevice!=null){
//			adbchimpdevice=null;
//		}
	}

	// get ichimpDevice
	public IChimpDevice getichimpDevice() {
		return ichimpDevice;
	}

	// Test
	public void Test(String str) {
//			IChimpImage a=ichimpDevice.takeSnapshot();
//			IChimpImage b=ichimpDevice.takeSnapshot();
//			a.sameAs(b, 1.0);
//		void startActivity(@Nullable String uri, @Nullable String action, 
//	            @Nullable String data, @Nullable String mimeType, 
//	            Collection<String> categories, Map<String, Object> extras, @Nullable String component, 
//	            int flags);  

	}
}
