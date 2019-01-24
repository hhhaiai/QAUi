package com.review.getscreen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.constant.CIOSCMD;
import com.constant.Cconfig;
import com.helper.CMDUtil;
import com.helper.TimeUtil;
import com.viewer.main.MainRun;

import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;

public class IOSShot extends BaseShot {
	IOSDriver<WebElement> driver;

	public IOSShot(String shottype, IOSDriver<WebElement> driver, String udid, File reportFolder) {
		super(shottype, udid, reportFolder);
		this.driver = driver;
		setZoom();
		drawsize();
	}

	@Override
	public String ScreenShotByCustomer(String name, String picpath) {
		// TODO Auto-generated method stub
		// cuspiccount++;
		name = name.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]", "");
		if (picpath == null || !new File(picpath).exists() || new File(picpath).isFile()) {
			File cusscreenfolder = new File(
					screenfolder.getParent().replaceAll("\\\\", "/") + "/" + Cconfig.CUSTOMER_FOLDER);
			if (!cusscreenfolder.exists())
				cusscreenfolder.mkdirs();
			picpath = cusscreenfolder.getAbsolutePath() + "/" + name + ".png";// 图片保存路径
			System.out.println(picpath);
		} else {
			picpath = picpath + "/" + name + ".png";
		}
		return Shot(name, picpath, true);
	}

	@Override
	public synchronized String Shot(String name, String picpath, boolean iscutomer) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(MainRun.sysConfigBean.getWaitAfterOperation());
		} catch (InterruptedException e) {
		}
		if (shottype.equals(Cconfig.SCREENSHOT_IDEVICESREENSHOT)) {
			String[] strings_Pull = CMDUtil.execcmd(
					MainRun.sysConfigBean.getMACcmd() + "/"
							+ CIOSCMD.SCREEN_CAP_IOS.replaceAll("#udid#", udid).replaceAll("#savepath#", picpath),
					CIOSCMD.SYSCMD, true);
			if (strings_Pull[0].contains("Screenshot saved") && (new File(picpath).exists())) {
				return picpath;
			} else {
				logger.info("Screenshot saved failed=" + picpath + ":" + strings_Pull[0] + "~~~" + strings_Pull[1]);
			}
		} else if (shottype.equals(Cconfig.SCREENSHOT_APPIUM)) {
			try {
				FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE), new File(picpath));
				return picpath;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		} else if (shottype.equals(Cconfig.SCREENSHOT_NONE)) {
			if (iscutomer) {
				String[] strings_Pull = CMDUtil.execcmd(
						MainRun.sysConfigBean.getMACcmd() + "/"
								+ CIOSCMD.SCREEN_CAP_IOS.replaceAll("#udid#", udid).replaceAll("#savepath#", picpath),
						CIOSCMD.SYSCMD, true);
				if (strings_Pull[0].contains("Screenshot saved") && (new File(picpath).exists())) {
					return picpath;
				} else {
					logger.info("Screenshot saved failed=" + picpath);
				}
			} else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				} // 避免点击过快,界面更新不及时
			}
			return null;
		}
		return null;
	}

	@Override
	public String ScreenShot(String name) {
		// TODO Auto-generated method stub
		piccount++;
		String picpath = screenfolder.getAbsolutePath().replaceAll("\\\\", "/") + "/" + piccount + "-" + name + "-"
				+ TimeUtil.getTime4File() + ".png";// 图片保存路径
		return Shot(name, picpath, false);
	}

	/**
	 * 设置Zoom倍数
	 * 
	 * @return
	 */
	public void setZoom() {
		logger.info("start get screen size to set zoom");
		try {
			// Dimension
			// dimension=driver.findElementByIosNsPredicate("type=='XCUIElementTypeApplication'").getSize();
			Dimension dimension = driver
					.findElement(MobileBy.iOSNsPredicateString("type=='XCUIElementTypeApplication'")).getSize();
			device_width = dimension.getWidth();
			device_hight = dimension.getHeight();
			logger.info("ios point frame by element:" + device_width + "," + device_hight);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception", e);
		}
		try {
			if (device_width == 1 || device_hight == 1) {
				Dimension dimension = driver.manage().window().getSize();
				device_width = dimension.getWidth();
				device_hight = dimension.getHeight();
				logger.info("ios point frame:" + device_width + "," + device_hight);
			}

			BufferedImage image = readMemoryImage(driver.getScreenshotAs(OutputType.BYTES));
			logger.info("ios image width=" + image.getWidth() + ",image height=" + image.getHeight());
			int xzoom = image.getWidth() / device_width;
			int yzoom = image.getHeight() / device_hight;
			image = null;
			logger.info("ios xzoom=" + xzoom + ",yzoom=" + yzoom);
			if (xzoom > 1)
				zoom = xzoom;

		} catch (org.openqa.selenium.NoSuchElementException e) {
			// TODO: handle exception
			logger.error("Exception", e);
		} catch (WebDriverException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
	}

}
