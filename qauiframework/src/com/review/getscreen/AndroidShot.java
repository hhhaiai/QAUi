package com.review.getscreen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.ADBUtil;
import com.helper.AndroidInfo;
import com.helper.CMDUtil;
import com.helper.TimeUtil;
import com.viewer.main.MainRun;

import io.appium.java_client.android.AndroidDriver;

public class AndroidShot extends BaseShot{
	Logger logger=LoggerFactory.getLogger(AndroidShot.class);
	AndroidDriver<WebElement> driver;
	/**
	 * 构造函数
	 * @param configMap
	 * @param driver 没有driver则传入null
	 * @param udid
	 * @param reportFolder
	 */
	public AndroidShot(String shottype,AndroidDriver<WebElement> driver,String udid,File reportFolder) {
		super(shottype,udid,reportFolder);
		this.driver=driver;
		getResolution();
		drawsize();
	}

	@Override
	public String ScreenShotByCustomer(String name,String picpath) {
		// TODO Auto-generated method stub
		//cuspiccount++;
		name=name.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]", "");
		if(picpath==null||!new File(picpath).exists()||new File(picpath).isFile()){
			File cusscreenfolder=new File(screenfolder.getParent().replaceAll("\\\\", "/")+"/"+Cconfig.CUSTOMER_FOLDER);
			if(!cusscreenfolder.exists())cusscreenfolder.mkdirs();
			picpath=cusscreenfolder.getAbsolutePath()+"/"+name+".png";//图片保存路径	
		}else{
			picpath=picpath+"/"+name+".png";
		}
		return Shot(name,picpath,true);
	}
	@Override
	public String ScreenShot(String name) {
		// TODO Auto-generated method stub
		piccount++;
		String picpath=screenfolder.getAbsolutePath()+"/"+piccount+"-"+name+"-"+TimeUtil.getTime4File()+".png";//图片保存路径
		return Shot(name,picpath,false);
	}
	/**
	 * 得到屏幕分辨率
	 */
	protected void getResolution() {
		try {
//			Dimension dimension=driver.manage().window().getSize();
//			device_width=dimension.getWidth();
//			device_hight=dimension.getHeight();
			logger.info("start get screen size to set zoom");
			int[] resolution=AndroidInfo.getDeviceResolution(udid);
			device_width=resolution[0];
			device_hight=resolution[1];
			if((device_width<1||device_hight<1)&&driver!=null) {
				BufferedImage image = readMemoryImage(driver.getScreenshotAs(OutputType.BYTES));
				device_width=image.getWidth();
				device_hight=image.getHeight();
				image=null;	
			}
			logger.info("android image resolution width="+device_width+",image height="+device_hight);
		} catch (WebDriverException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION",e);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION",e);
		}

	}

	@Override
	public synchronized String Shot(String name, String picpath,boolean iscutomer) {
		// TODO Auto-generated method stub
		try {Thread.sleep(MainRun.sysConfigBean.getWaitAfterOperation());} catch (InterruptedException e) {}
		picpath=picpath.replaceAll("\\\\", "/");
		if(shottype.equals(Cconfig.SCREENSHOT_ADB)){
			String string_cap=ADBUtil.execcmd(udid,CAndroidCMD.SCREEN_CAP_ANDROID).toString();
			if(string_cap.equals("")){
				ADBUtil.pullfile(udid, "/data/local/tmp/qascreenshot.png", picpath);
				if((new File(picpath).exists())){
					return picpath;
				}else{
					logger.info("pull failed="+picpath);
				}
			}else {
				logger.warn("screen shot failed="+string_cap);
			}
//			String[] strings_Cap=CMDUtil.execcmd(Ccmd.SCREEN_CAP_ANDROID.replaceAll("#udid#", udid), Ccmd.SYSCMD, true);
//			if(strings_Cap[1].equals("")){
//				String[] strings_Pull=CMDUtil.execcmd(Ccmd.PULL_SCREENSHOT_ANDROID.replaceAll("#udid#", udid).replaceAll("#savepath#", picpath), Ccmd.SYSCMD, true);
//				if(strings_Pull[0].contains("file pulled")&&(new File(picpath).exists())){
//					return picpath;
//				}else{
//					logger.info("pull failed="+picpath);
//				}
//			}
		}else if(shottype.equals(Cconfig.SCREENSHOT_APPIUM)){
			try {
				if(driver!=null) {
					FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE), new File(picpath));
					return picpath;	
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
			}
		}else if(shottype.equals(Cconfig.SCREENSHOT_DDMLIB)){
			IDevice iDevice=MainRun.adbBridge.getDevice(udid);
			try {
				if(iDevice!=null){
					BufferedImage image=RawImage2BufferImage(iDevice.getScreenshot(10, TimeUnit.SECONDS));
					if(image!=null){
						ImageIO.write(image, "PNG", new File(picpath));	
					}
					image=null;
					return picpath;	
				}
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
			} catch (AdbCommandRejectedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
			}
		}else if(shottype.equals(Cconfig.SCREENSHOT_NONE)){
			if(iscutomer){
				if(ADBUtil.execcmd(udid,CAndroidCMD.SCREEN_CAP_ANDROID).toString().equals("")){
					ADBUtil.pullfile(udid, "/data/local/tmp/qascreenshot.png", picpath);
					if((new File(picpath).exists())){
						return picpath;
					}else{
						logger.info("pull failed="+picpath);
					}
				}
			}else{
				try {Thread.sleep(500);} catch (InterruptedException e) {}//避免点击过快,界面更新不及时
			}
			return null;
		}
		return null;
	}
	/**
	 * RawImage转化为BufferImage
	 * @param rawScreen
	 * @return
	 */
	protected BufferedImage RawImage2BufferImage(RawImage rawScreen){
		BufferedImage image = null;
		if(rawScreen != null){
            image = new BufferedImage(rawScreen.width,rawScreen.height,BufferedImage.TYPE_INT_RGB);
            int index = 0;
            int indexInc = rawScreen.bpp >> 3;
            for (int y = 0; y < rawScreen.height; y++) {
                for (int x = 0; x < rawScreen.width; x++, index += indexInc) {
                    int value = rawScreen.getARGB(index);
                    image.setRGB(x, y, value);
                }
            }
		}
		return image;
	}

}
