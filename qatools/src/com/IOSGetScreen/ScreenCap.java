package com.IOSGetScreen;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
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

import com.Util.Excute;
import com.Viewer.MainRun;
import com.constant.CIOSCMD;
import com.constant.Cconfig;

public class ScreenCap {
	Logger logger = LoggerFactory.getLogger(ScreenCap.class);

	EditToolsUI edittoolsui = new EditToolsUI();
	ScreenShotUI screenshotui = new ScreenShotUI(edittoolsui);
	boolean screencapthreadrun = false;
	private BufferedImage bufferedImage;
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
		if (bufferedImage != null) {
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
				int width = bufferedImage.getWidth();
				int height = bufferedImage.getHeight();
				if (saveNarrow && bufferedImage.getHeight() > 854) {
					width = (int) (((double) width / (double) height) * 854);
					Image zoomimage = bufferedImage.getScaledInstance(width, 854, Image.SCALE_SMOOTH);
					ImageIO.write(toBufferedImage(zoomimage), "png", file);
					logger.info("screen cap with 480P auto to save");
				} else {
					ImageIO.write(bufferedImage, "png", file);
				}
				return true;
			} catch (Exception e) {
				logger.error("Exception", e);
				JOptionPane.showMessageDialog(null, "存储图片失败!", "消息", JOptionPane.ERROR_MESSAGE);
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

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	// screencapthread
	class ScreenCapThread implements Runnable {

		public ScreenCapThread() {

		}

		public void run() {
			screencapthreadrun = true;
			MainRun.mainFrame.progressBarmain.setValue(10);// ******************
			bufferedImage = getDeviceImage();
			MainRun.mainFrame.progressBarmain.setValue(50);// ******************
			if (bufferedImage != null) {
				screenshotui.setIndex(0);
				screenshotui.createNewitem();
				screenshotui.setBufferedImage(bufferedImage);

				if (andsave) {
					File file = new File(
							MainRun.QALogfile + "/ScreenCap/Cap_PCtime_" + sDateFormatget.format(new Date()) + ".png");
					try {
//						      BufferedImage outImage = new BufferedImage((int)(mFBImage.getWidth() * 1), 
//						    		  (int)(mFBImage.getHeight() * 1), mFBImage.getType());
						// 是否保存480P截图
						int width = bufferedImage.getWidth();
						int height = bufferedImage.getHeight();
						if (saveNarrow && bufferedImage.getHeight() > 854) {
							width = (int) (((double) width / (double) height) * 854);
							Image zoomimage = bufferedImage.getScaledInstance(width, 854, Image.SCALE_SMOOTH);
							ImageIO.write(toBufferedImage(zoomimage), "png", file);
							logger.info("screen cap with 480P auto to save");
						} else {
							ImageIO.write(bufferedImage, "png", file);
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

	/**
	 * 得到截图
	 * 
	 * @return
	 */
	private BufferedImage getDeviceImage() {
		BufferedImage bimg = null;
		String picpath = MainRun.datalocation + "/temp.png";
		String[] strings_Pull;
		if (MainRun.OStype == Cconfig.WINDOWS) {
			strings_Pull = Excute.execcmd(udid, CIOSCMD.SCREEN_CAP_IOS.replaceAll("#udid#", udid)
					.replaceAll("#savepath#", picpath.replaceAll("\\\\", "/")), CIOSCMD.SYSCMD, true);

		} else {
			strings_Pull = Excute.execcmd(udid, MainRun.paramsBean.getMACcmd() + "/" + CIOSCMD.SCREEN_CAP_IOS
					.replaceAll("#udid#", udid).replaceAll("#savepath#", picpath.replaceAll("\\\\", "/")),
					CIOSCMD.SYSCMD, true);

		}
		if (strings_Pull[0].contains("Screenshot saved") && (new File(picpath).exists())) {
			File picfile = new File(picpath);
			try {
				bimg = ImageIO.read(picfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}
		} else {
			logger.info("screen shot failed:" + strings_Pull[0]);
		}
		return bimg;
	}

}
