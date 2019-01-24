package com.GetScreen;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;

public class ScreenCap {
	Logger logger = LoggerFactory.getLogger(ScreenCap.class);

	EditToolsUI edittoolsui = new EditToolsUI();
	ScreenShotUI screenshotui = new ScreenShotUI(edittoolsui);
	boolean screencapthreadrun = false;
	private FBImage mFBImage;
	private SimpleDateFormat sDateFormatget = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	String saveimagepath = "";
	boolean andsave = false;
	boolean saveNarrow = true;
	String udid;

	public void run(String udid) {
		this.udid = udid;
		File screenfolder = new File(MainRun.QALogfile + "/ScreenCap");
		if (!screenfolder.exists()) {
			screenfolder.mkdirs();
		}
		ScreenCapThread screencapthread = new ScreenCapThread();
		new Thread(screencapthread).start();
		logger.info("screen cap");
	}

	// sava image
	public boolean saveImage() {
		FBImage inImage = this.mFBImage;
		if (inImage != null) {
			BufferedImage outImage = new BufferedImage((int) (inImage.getWidth() * 1), (int) (inImage.getHeight() * 1),
					inImage.getType());
			if (outImage != null) {
				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(1, 1), 2);
				op.filter(inImage, outImage);
				JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile + "/ScreenCap");
				fileChooser.setFileFilter(new FileFilter() {
					public String getDescription() {
						return "*.png";
					}

					public boolean accept(File f) {
						String ext = f.getName().toLowerCase();
						return ext.endsWith(".png");
					}
				});
				fileChooser.setSelectedFile(new File("Cap_PCtime_" + sDateFormatget.format(new Date())));
				if (fileChooser.showSaveDialog(MainRun.mainFrame) != 0)
					return false;
				try {
					File file = fileChooser.getSelectedFile();
					saveimagepath = file.getAbsolutePath();
					if (!saveimagepath.endsWith(".png")) {
						file = new File(saveimagepath + "." + "png");
						saveimagepath = saveimagepath + ".png";
					}
					// 是否保存720P截图
					int width = outImage.getWidth();
					int height = outImage.getHeight();
					if (saveNarrow && outImage.getHeight() > 854) {
						width = (int) (((double) width / (double) height) * 854);
						Image zoomimage = outImage.getScaledInstance(width, 854, Image.SCALE_SMOOTH);
						ImageIO.write(toBufferedImage(zoomimage), "png", file);
						logger.info("screen cap with 480P auto to save");
					} else {
						ImageIO.write(outImage, "png", file);
					}
					return true;
				} catch (Exception e) {
					logger.error("Exception", e);
					JOptionPane.showMessageDialog(null, "存储图片失败!", "消息", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "图片为空!", "消息", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	// image 2 bufferedImage
	public BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent Pixels
		// boolean hasAlpha = hasAlpha(image);
		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			/*
			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
			 */
			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}
		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			/*
			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
			 */
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	public String getimagepath() {
		return saveimagepath;
	}

	public boolean getScreenCapThreadrun() {
		return screencapthreadrun;
	}

	public ScreenShotUI getScreenShotUI() {
		return screenshotui;
	}

	public EditToolsUI getEditTools() {
		return edittoolsui;
	}

	public void setAndsave(boolean andsave) {
		this.andsave = andsave;
	}

	public void setsaveNarrow(boolean saveNarrow) {
		this.saveNarrow = saveNarrow;
	}

	public FBImage getmFBImage() {
		return mFBImage;
	}

	// screencapthread
	class ScreenCapThread implements Runnable {

		public ScreenCapThread() {

		}

		public void run() {
			screencapthreadrun = true;
			MainRun.mainFrame.progressBarmain.setValue(10);// ******************
			mFBImage = getDeviceImage();
			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
			if (mFBImage != null) {
				screenshotui.setIndex(0);
				screenshotui.createNewitem();
				screenshotui.setFBImage(mFBImage);
				if (andsave) {
					File file = new File(
							MainRun.QALogfile + "/ScreenCap/Cap_PCtime_" + sDateFormatget.format(new Date()) + ".png");
					try {
//						      BufferedImage outImage = new BufferedImage((int)(mFBImage.getWidth() * 1), 
//						    		  (int)(mFBImage.getHeight() * 1), mFBImage.getType());
						// 是否保存720P截图
						int width = mFBImage.getWidth();
						int height = mFBImage.getHeight();
						if (saveNarrow && mFBImage.getHeight() > 854) {
							width = (int) (((double) width / (double) height) * 854);
							Image zoomimage = mFBImage.getScaledInstance(width, 854, Image.SCALE_SMOOTH);
							ImageIO.write(toBufferedImage(zoomimage), "png", file);
							logger.info("screen cap with 480P auto to save");
						} else {
							ImageIO.write(mFBImage, "png", file);
						}
						saveimagepath = file.getAbsolutePath();
						MainRun.mainFrame.progressBarmain.setValue(100);// ******************
						JOptionPane.showMessageDialog(null, "截图完成!\n图片保存在" + file.getAbsolutePath(), "消息",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						MainRun.mainFrame.progressBarmain.setValue(0);// ******************
						logger.error("Exception", e);
					}
				} else {
					MainRun.mainFrame.progressBarmain.setValue(100);// ******************
					JOptionPane.showMessageDialog(null, "截图完成!", "消息", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				MainRun.mainFrame.progressBarmain.setValue(0);// ******************
			}
			screencapthreadrun = false;
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
				tmpRawImage = MainRun.adbBridge.getDevice(udid).getScreenshot();

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

			if (!udid.equals("test")) {
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
				if (!udid.equals("test")) {
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

				if (!udid.equals("test")) {
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

//	/**
//	 * copy from http://bbs.csdn.net/topics/390502035. modify by Geek_Soledad
//	 *http://javadox.com/com.android.tools.ddms/ddmlib/23.0.1/com/android/ddmlib/IDevice.html
//	 */
//		public static IDevice connect() {
//			// init the lib
//			// [try to] ensure ADB is running
//			String adbLocation = System.getProperty("com.android.screenshot.bindir"); //$NON-NLS-1$
//			if (adbLocation != null && adbLocation.length() != 0) {
//				adbLocation += File.separator + "adb"; //$NON-NLS-1$
//			} else {
//				adbLocation = "adb"; //$NON-NLS-1$
//			}
//
//			AndroidDebugBridge.init(false /* debugger support */);
//
//			AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adbLocation, true /* forceNewBridge */);
//
//			// we can't just ask for the device list right away, as the internal
//			// thread getting
//			// them from ADB may not be done getting the first list.
//			// Since we don't really want getDevices() to be blocking, we wait
//			// here manually.
//			int count = 0;
//			while (bridge.hasInitialDeviceList() == false) {
//				try {
//					Thread.sleep(100);
//					count++;
//				} catch (InterruptedException e) {
//					// pass
//				}
//
//				// let's not wait > 10 sec.
//				if (count > 100) {
//					System.err.println("Timeout getting device list!");
//					return null;
//				}
//			}
//
//			// now get the devices
//			IDevice[] devices = bridge.getDevices();
//
//			if (devices.length == 0) {
//				com.Main.ThenToolsRun.logger.log(Level.INFO,"No devices found!");
//				return null;
//			}
//			return devices[0];
//		}
//
//		public static BufferedImage screenShot(IDevice device) {
//			RawImage rawImage;
//			try {
//				rawImage = device.getScreenshot();
//			} catch (Exception ioe) {
//				com.Main.ThenToolsRun.logger.log(Level.INFO,"Unable to get frame buffer: " + ioe.getMessage());
//				return null;
//			}
//
//			// device/adb not available?
//			if (rawImage == null)
//				return null;
//
//			// convert raw data to an Image
//			BufferedImage image = new BufferedImage(rawImage.width, rawImage.height,
//					BufferedImage.TYPE_INT_ARGB);
//
//			int index = 0;
//			int IndexInc = rawImage.bpp >> 3;
//			for (int y = 0; y < rawImage.height; y++) {
//				for (int x = 0; x < rawImage.width; x++) {
//					int value = rawImage.getARGB(index);
//					index += IndexInc;
//					image.setRGB(x, y, value);
//				}
//			}
//			return image;
//		}
//
//		/**
//		 * Grab an image from an ADB-connected device.
//		 */
//		public static boolean screenShotAndSave(IDevice device, String filepath)  {
//
//			boolean result=false;
//			try {
//				result = ImageIO.write(screenShot(device), "png", new File(filepath));
//				if (result) {
//					com.Main.ThenToolsRun.logger.log(Level.INFO,"file is saved in:" + filepath);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				com.Main.ThenToolsRun.logger.log(Level.WARNING,e.toString());LoggerUtil.printException(e);
//			}
//			return result;
//		}
//
//		public static void terminate() {
//			AndroidDebugBridge.terminate();
//		}
// =====================================================================
//		private IChimpDevice mChimpDevice;
//		private AdbBackend adbBack;
//
//		public Robot() {
//			mImgHash = new ImageHash();
//			adbBack = new AdbBackend();
//			mChimpDevice = adbBack.waitForConnection();
//		}
//
//		/**
//		 * ��ͼ
//		 */
//		public BufferedImage snapshot() {
//			IChimpImage img;
//			// ������һ��whileѭ������ʱ��ͼʱ���׳���ʱ�쳣�����·��ص���null����
//			do {
//				img = mChimpDevice.takeSnapshot();
//			} while (img == null);
//			return img.getBufferedImage();
//		}
