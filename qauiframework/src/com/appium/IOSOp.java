package com.appium;

import java.awt.Color;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Coperation;
import com.helper.IOSInfo;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.IOSShot;
import com.viewer.main.MainRun;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class IOSOp extends BaseOp {
	Logger logger = LoggerFactory.getLogger(IOSOp.class);
	protected IOSDriver<WebElement> driver;
	protected IOSShot Shot;
	protected IOSOpExtendSwipe iosOpExtendSwipe;

	public IOSOp(IOSDriver<WebElement> driver, Map<String, String> capabilityMap, File reportFolder, SceneLogUtil oplog,
			Object baseShot, Translation translation) {
		super(capabilityMap, reportFolder, oplog, baseShot, translation);
		// TODO Auto-generated constructor stub
		this.driver = driver;
		Shot = (IOSShot) baseShot;
		iosOpExtendSwipe = new IOSOpExtendSwipe(this.driver, this.Shot, oplog, this, translation);
		elementMap.put("Driver", driver);
	}

	// /**
	// * 根据名称查找元素
	// * @param text
	// * @param time 等待时间,单位秒
	// * @return
	// */
	// public ElementIOS findElementByName(String name, int time) {
	// // TODO Auto-generated method stub
	// return findElementBy(MobileBy.name(name),time);
	// }
	// /**
	// * 根据名称查找元素
	// * @param text
	// * @return
	// */
	// public ElementIOS findElementByName(String name){
	// return findElementBy(MobileBy.name(name));
	// }

	@Override
	public By MobileBy(String text) {
		text = text.trim();
		if (text != null && text.replaceAll("\\(", "").startsWith("/"))
			return MobileBy.xpath(text);
		// http://blog.csdn.net/jianglianye21/article/details/78326174
		if (text.matches(
				".+?(>|<|(>=)|(<=)|(!=)|(==)|(IN)|(BETWEEN)|(CONTAINS)|(BEGINSWITH)|(ENDSWITH)|(LIKE)|(MATCHES)|(AND)|(OR)).+"))
			return MobileBy.iOSNsPredicateString(text);
		return MobileBy.AccessibilityId(text);
	}

	// @Override
	// public ElementIOS findElementByClassName(String className, int time) {
	// // TODO Auto-generated method stub
	// return findElementBy(MobileBy.className(className),time);
	// }
	//
	// @Override
	// public ElementIOS findElementByXpath(String xpathExpression, int time) {
	// // TODO Auto-generated method stub
	// return findElementBy(MobileBy.xpath(xpathExpression),time);
	// }
	//
	// @Override
	// public ElementIOS findElementByClassName(String className){
	// return findElementBy(MobileBy.className(className));
	// }
	// @Override
	// public ElementIOS findElementByXpath(String xpathExpression){
	// return findElementBy(MobileBy.xpath(xpathExpression));
	// }
	// @Override
	// public ElementIOS findElementByAccessibilityId(String AccessibilityId){
	// return findElementBy(MobileBy.AccessibilityId(AccessibilityId));
	// }
	// @Override
	// public ElementIOS findElementByAccessibilityId(String AccessibilityId,int
	// time){
	// return findElementBy(MobileBy.AccessibilityId(AccessibilityId),time);
	// }
	@Override
	public ElementIOS findOneOfElements(int second, By... bys) {
		boolean showwarn = true;
		if (!tempUiWatcherFlag)
			showwarn = false;
		sysSleep(1000);
		ElementIOS elementIOS = null;
		if (second < 1)
			second = 1;
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("寻找元素之一:  ");
		for (By by : bys) {
			String text = translation.getName(by);
			strBuf.append(text + ",");
		}
		oplog.logInfo(strBuf.toString().substring(0, strBuf.length() - 1));
		for (By by : bys) {
			cancelUiWathcher();
			elementIOS = findElement(by, second);
			if (elementIOS.exist()) {
				oplog.logInfo("找到元素:" + elementIOS.Text);
				return elementIOS;
			}
		}
		if (UiWatcherFlag && showwarn) {
			Shot.drawText(Coperation.NOT_FIND_ELEMENT,
					"未找到元素之一:" + strBuf.toString().substring(7, strBuf.length() - 1));
			oplog.logWarn("未找到元素之一:" + strBuf.toString().substring(7, strBuf.length() - 1));
		} else {
			oplog.logInfo("未找到元素之一:" + strBuf.toString().substring(7, strBuf.length() - 1));
		}
		tempUiWatcherFlag = true;
		return elementIOS;
	}

	@Override
	public ElementIOS findElement(String text) {
		// TODO Auto-generated method stub
		return findElementBy(MobileBy(text));
	}

	@Override
	public ElementIOS findElement(String text, int time) {
		// TODO Auto-generated method stub
		return findElementBy(MobileBy(text), time);
	}

	@Override
	public ElementIOS findElement(By by) {
		return findElementBy(by);
	}

	@Override
	public ElementIOS findElement(By by, int time) {
		return findElementBy(by, time);
	}

	/**
	 * 查找元素方法
	 * 
	 * @param by
	 * @param args
	 * @return
	 */
	protected ElementIOS findElementBy(By by, Object... args) {
		String Text = translation.getName(by);
		WebElement webElement = null;
		int timeOutInSeconds = MainRun.sysConfigBean.getWaitforElement();
		try {
			if (args.length > 0 && args[0] instanceof Integer) {
				timeOutInSeconds = (Integer) args[0];
			}
			webElement = new IOSDriverWait(driver, timeOutInSeconds).until(new IOSExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(IOSDriver<WebElement> driver) {
					// TODO Auto-generated method stub
					return driver.findElement(by);
				}
			});
		} catch (org.openqa.selenium.NoSuchSessionException e) {
			// TODO: handle exception
			throw new org.openqa.selenium.NoSuchSessionException();
		} catch (RuntimeException e) {
			// TODO: handle exception
			if (UiWatcherFlag && tempUiWatcherFlag) {
				logger.warn("Exception", e);
				oplog.logWarn("未找到元素:" + Text);
				Shot.drawText(Coperation.NOT_FIND_ELEMENT, "未找到元素:" + Text);
			} else {
				oplog.logInfo("未找到元素:" + Text);
			}
			// UiWatcher,当异常出现后的处理
			if (UiWatcherFlag && uiWatcher != null && tempUiWatcherFlag) {
				if (runUiWatcher()) {
					oplog.logInfo("UiWatcher:成功处理异常.");
				} else {
					oplog.logWarn("UiWatcher:未成功处理异常.");
				}
				try {
					webElement = new IOSDriverWait(driver, 2).until(new IOSExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(IOSDriver driver) {
							// TODO Auto-generated method stub
							return driver.findElement(by);
						}
					});
				} catch (RuntimeException ex) {
					oplog.logWarn("仍然未找到元素:" + Text);
				}
			}
		}
		tempUiWatcherFlag = true;
		return new ElementIOS(webElement, by, Text, elementMap);
	}

	@Override
	public List<ElementIOS> findElements(By by) {
		// TODO Auto-generated method stub
		return findElementsBy(by);
	}

	@Override
	public List<String> findNamesbyElements(By by) {
		// TODO Auto-generated method stub
		List<ElementIOS> list = findElementsBy(by);
		List<String> nameList = new ArrayList<>();
		for (ElementIOS elementIOS : list) {
			nameList.add(elementIOS.getText());
		}
		return nameList;
	}

	@Override
	public ElementIOS findElementByElements(By by, int index) {
		// TODO Auto-generated method stub
		String Text = translation.getName(by) + "," + index;
		ElementIOS elementIOS = new ElementIOS(null, by, Text, elementMap);
		List<ElementIOS> elementIOSs = findElements(by);
		if (index <= elementIOSs.size() - 1) {
			elementIOS = elementIOSs.get(index);
		} else {
			oplog.logWarn("(" + Text + ")只有" + elementIOSs.size() + "个元素");
		}
		return elementIOS;
	}

	@Override
	public ElementIOS findElementByElements(String textandindex) {
		// TODO Auto-generated method stub
		String info = textandindex.split(",")[0];
		int index = Integer.parseInt(textandindex.split(",")[1]);
		By by = MobileBy(info);
		String Text = translation.getName(textandindex) + "," + index;
		ElementIOS elementIOS = new ElementIOS(null, by, Text, elementMap);
		List<ElementIOS> elementIOSs = findElements(by);
		if (index <= elementIOSs.size() - 1) {
			elementIOS = elementIOSs.get(index);
		} else {
			oplog.logWarn("(" + Text + ")只有" + elementIOSs.size() + "个元素");
		}
		return elementIOS;
	}

	/**
	 * 查找元素组方法
	 * 
	 * @param by
	 * @param args
	 * @return
	 */
	protected List<ElementIOS> findElementsBy(By by, Object... args) {
		// TODO Auto-generated method stub
		String Text = translation.getName(by);
		List<ElementIOS> elementIOSs = new ArrayList<>();
		List<WebElement> webElements = driver.findElements(by);
		if (webElements.size() == 0) {
			oplog.logWarn("未找到元素组:" + Text);
			Shot.drawText(Coperation.NOT_FIND_ELEMENT, "未找到元素组:" + Text);
		} else {
			oplog.logInfo("找到" + webElements.size() + "个元素:" + Text);
		}
		for (WebElement webElement : webElements) {
			elementIOSs.add(new ElementIOS(webElement, by, Text, elementMap));
		}
		return elementIOSs;
	}

	@Override
	public void swipe(int startx, int starty, int endx, int endy, int duration) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		if (AutoMultipleFlag) {
			startx = X_multiple(startx);
			starty = Y_multiple(starty);
			endx = X_multiple(endx);
			endy = Y_multiple(endy);
		}
		AutoMultipleFlag = true;
		waittime(stime);
		Shot.drawArrow(Coperation.SWIPE, startx, starty, endx, endy);
		oplog.logStep("滑动(" + startx + "," + starty + ")到(" + endx + "," + endy + "),滑动时间=" + duration + "毫秒");
		// driver.swipe(startx, starty, endx-startx, endy-starty, duration);
		// new TouchAction(driver).press(startx,
		// starty).waitAction(Duration.ofMillis(duration)).moveTo(endx-startx,
		// endy-starty).release().perform();
		// new TouchAction(driver).press(startx, starty).moveTo(endx-startx,
		// endy-starty).release().perform();
		new TouchAction(driver).press(PointOption.point(startx, starty))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).moveTo(PointOption.point(endx, endy))
				.release().perform();
	}

	@Override
	public void tapCenter(int time) {
		if (time < 1)
			time = 1;
		int x = Shot.getDevice_width() / 2;
		int y = Shot.getDevice_hight() / 2;
		for (int i = 0; i < time; i++) {
			long stime = TimeUtil.getTime();
			waittime(stime);
			Shot.drawOval(Coperation.TAP, x, y);
			oplog.logStep("点击屏幕中心(" + x + "," + y + ")");
			try {
				new TouchAction(driver).tap(PointOption.point(x, y)).perform();
			} catch (org.openqa.selenium.WebDriverException e) {
				// TODO: handle exception
				oplog.logWarn("点击坐标异常");
			}
		}
	}

	@Override
	public void tap(int x, int y) {
		long stime = TimeUtil.getTime();
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
		}
		AutoMultipleFlag = true;
		waittime(stime);
		Shot.drawOval(Coperation.TAP, x, y);
		oplog.logStep("点击坐标(" + x + "," + y + ")");
		// driver.tap(1, x, y, 0);
		try {
			new TouchAction(driver).tap(PointOption.point(x, y)).perform();
		} catch (org.openqa.selenium.WebDriverException e) {
			// TODO: handle exception
			oplog.logWarn("点击坐标异常");
		}
	}

	@Override
	public void tap(int x, int y, int times, long millisecond) {
		long stime = TimeUtil.getTime();
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
		}
		AutoMultipleFlag = true;
		Shot.drawOval(Coperation.TAP, x, y);
		waittime(stime);
		if (times < 1)
			times = 1;
		oplog.logStep("点击坐标(" + x + "," + y + ")" + times + "次,每次间隔" + millisecond + "毫秒");
		for (int i = 0; i < times; i++) {
			new TouchAction(driver).tap(PointOption.point(x, y)).perform();
			sysSleep(millisecond);
		}
	}

	@Override
	public void tap(String text) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		int x = Integer.parseInt(text.split(",")[0]);
		int y = Integer.parseInt(text.split(",")[1]);
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
		}
		AutoMultipleFlag = true;
		waittime(stime);
		Shot.drawOval(Coperation.TAP, x, y);
		oplog.logStep("点击坐标(" + translation.getName(text) + ")");
		// driver.tap(1, x, y, 0);
		try {
			new TouchAction(driver).tap(PointOption.point(x, y)).perform();
		} catch (org.openqa.selenium.WebDriverException e) {
			// TODO: handle exception
			oplog.logWarn("点击坐标异常");
		}
	}

	@Override
	public void tap(String text, int times, long millisecond) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		int x = Integer.parseInt(text.split(",")[0]);
		int y = Integer.parseInt(text.split(",")[1]);
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
		}
		AutoMultipleFlag = true;
		waittime(stime);
		Shot.drawOval(Coperation.TAP, x, y);
		if (times < 1)
			times = 1;
		oplog.logStep("点击坐标(" + translation.getName(text) + ")" + times + "次,每次间隔" + millisecond + "毫秒");
		for (int i = 0; i < times; i++) {
			new TouchAction(driver).tap(PointOption.point(x, y)).perform();
			sysSleep(millisecond);
		}
	}

	@Override
	public void longtap(int x, int y, int duration) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
		}
		AutoMultipleFlag = true;
		waittime(stime);
		Shot.drawOval(Coperation.LONGTAP, x, y, Color.decode(Cconfig.GREEN_DEEP));
		oplog.logStep("轻触坐标(" + x + "," + y + ")," + duration + "毫秒");
		// driver.tap(fingers, x, y, duration);
		try {
			new TouchAction(driver).press(PointOption.point(x, y))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).release().perform();
		} catch (org.openqa.selenium.WebDriverException e) {
			// TODO: handle exception
			oplog.logWarn("点击坐标异常");
		}
	}

	@Override
	public void longtap(String text, int duration) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		int x = Integer.parseInt(text.split(",")[0]);
		int y = Integer.parseInt(text.split(",")[1]);
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
		}
		AutoMultipleFlag = true;
		waittime(stime);
		Shot.drawOval(Coperation.LONGTAP, x, y, Color.decode(Cconfig.GREEN_DEEP));
		oplog.logStep("轻触坐标(" + translation.getName(text) + ")," + duration + "毫秒");
		// driver.tap(fingers, x, y, duration);
		// new TouchAction(driver).press(x,
		// y).waitAction(Duration.ofMillis(duration)).release().perform();
		try {
			new TouchAction(driver).press(PointOption.point(x, y))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).release().perform();
		} catch (org.openqa.selenium.WebDriverException e) {
			// TODO: handle exception
			oplog.logWarn("点击坐标异常");
		}
	}

	@Override
	@Deprecated
	public void zoom(int x, int y, int length, int duration) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		int x_length = length;
		int y_length = length;
		if (AutoMultipleFlag) {
			x = X_multiple(x);
			y = Y_multiple(y);
			x_length = X_multiple(length);
			y_length = Y_multiple(length);
		}
		AutoMultipleFlag = true;
		TouchAction actionA = new TouchAction(driver).press(PointOption.point(x, y))
				// .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
				.moveTo(PointOption.point(x + x_length, y + y_length)).release();
		TouchAction actionB = new TouchAction(driver).press(PointOption.point(x + 5, y + 5))
				// .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
				.moveTo(PointOption.point(x - x_length, y - y_length)).release();
		oplog.logStep("放大坐标(" + x + "," + y + "),滑动长度=" + length + ",滑动时间=" + duration);
		String picpath = Shot.drawArrow(Coperation.ZOOM, x, y, x + x_length, y + y_length, false);
		if (picpath != null)
			Shot.drawArrowPic(picpath, x, y, x - x_length, y - y_length, true);
		waittime(stime);
		new MultiTouchAction(driver).add(actionA).add(actionB).perform();

	}

	@Override
	@Deprecated
	public void pinch(int x1, int y1, int x2, int y2, int duration) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		if (AutoMultipleFlag) {
			x1 = X_multiple(x1);
			y1 = Y_multiple(y1);
			x2 = X_multiple(x2);
			y2 = Y_multiple(y2);
		}
		AutoMultipleFlag = true;
		int center_x = (x1 + x2) / 2;
		int center_y = (y1 + y2) / 2;
		TouchAction actionA = new TouchAction(driver).press(PointOption.point(x1, y1))
				// .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
				.moveTo(PointOption.point(center_x, center_y)).release();
		TouchAction actionB = new TouchAction(driver).press(PointOption.point(x2, y2))
				// .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
				.moveTo(PointOption.point(center_x - 10, center_y - 10)).release();
		oplog.logStep("缩小坐标(" + x1 + "," + y1 + ")-(" + x2 + "," + y2 + "),滑动时间=" + duration);
		String picpath = Shot.drawArrow(Coperation.ZOOM, x1, y1, center_x, center_y, false);
		if (picpath != null)
			Shot.drawArrowPic(picpath, x2, y2, center_x, center_y, true);
		waittime(stime);
		new MultiTouchAction(driver).add(actionA).add(actionB).perform();
	}

	@Override
	public void setScreenSize(int width, int hight) {
		// TODO Auto-generated method stub
		resolution_width_multiple = (double) Shot.getDevice_width() / width;
		resolution_hight_multiple = (double) Shot.getDevice_hight() / hight;
		oplog.logInfo("当前设备宽=" + Shot.getDevice_width() + ",高=" + Shot.getDevice_hight() + ";" + "原始设备宽=" + width
				+ ",高=" + hight + ";" + "得到宽倍数=" + resolution_width_multiple + ",高倍数=" + resolution_hight_multiple);
	}

	@Override
	public void sleep(int second) {
		try {
			oplog.logInfo("休眠" + second + "秒");
			new IOSDriverWait(driver, second, 1000).until(new IOSExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(IOSDriver driver) {
					// TODO Auto-generated method stub
					return driver.findElement(MobileBy.className("休眠专用名称"));
				}
			});
		} catch (RuntimeException e) {
		}
	}

	/**
	 * 扩展操作
	 * 
	 * @return
	 */
	public IOSOpExtendSwipe EXTEND_SWIPE() {
		// TODO Auto-generated method stub
		return iosOpExtendSwipe;
	}

	@Override
	public IOSOp waitForNewWindow() {
		IOSInfo.waitForNewWindow(driver, oplog);
		return this;
	}

	@Override
	public String getPageSource() {
		return IOSInfo.getPageSource(driver)[0];
	}

	@Override
	public IOSOp cancelUiWathcher() {
		// TODO Auto-generated method stub
		tempUiWatcherFlag = false;
		return this;
	}

	@Override
	public IOSOp cancelAutoMultiple() {
		AutoMultipleFlag = false;
		return this;
	}

	@Override
	public boolean launchApp() {
		oplog.logInfo("启动应用...");
		try {
			driver.launchApp();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			oplog.logWarn("启动应用出现异常");
		}
		return false;
	}

	@Override
	public boolean closeApp() {
		// TODO Auto-generated method stub
		oplog.logStep("关闭应用");
		try {
			driver.closeApp();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			oplog.logWarn("关闭应用出现异常");
		}
		return false;
	}

	/**
	 * 返回桌面
	 * 
	 * @param millis 毫秒
	 */
	public void backHome(int millis) {
		oplog.logStep("返回桌面:" + millis + "毫秒");
		driver.runAppInBackground(Duration.ofMillis(millis));
		oplog.logInfo("返回应用");
	}

	@Override
	protected int X_multiple(int x) {
		int value = (int) (resolution_width_multiple * x);
		if (value >= Shot.getDevice_width()) {
			if (value != Shot.getDevice_width())
				oplog.logWarn("X坐标" + x + ",自适应后坐标" + value + ",超出X坐标范围[1," + Shot.getDevice_width() + "],置为"
						+ (Shot.getDevice_width() - 1));
			value = Shot.getDevice_width() - 1;
		} else if (value <= 0) {
			if (value != 0)
				oplog.logWarn("X坐标" + x + ",自适应后坐标" + value + ",超出X坐标范围[1," + Shot.getDevice_width() + "],置为1");
			value = 1;
		}
		return value;
	}

	@Override
	protected int Y_multiple(int y) {
		int value = (int) (resolution_hight_multiple * y);
		if (value >= Shot.getDevice_hight()) {
			if (value != Shot.getDevice_hight())
				oplog.logWarn("Y坐标" + y + ",自适应后坐标" + value + ",超出Y坐标范围[1," + Shot.getDevice_hight() + "],置为"
						+ (Shot.getDevice_hight() - 1));
			value = Shot.getDevice_hight() - 1;
		} else if (value <= 0) {
			if (value != 0)
				oplog.logWarn("Y坐标" + y + ",自适应后坐标" + value + ",超出Y坐标范围[1," + Shot.getDevice_hight() + "],置为1");
			value = 1;
		}
		return value;
	}

	public boolean HandlePermission2() {
		oplog.logInfo("开始处理权限管理...");
		int count = 0;
		boolean isok;
		try {
			do {
				count++;
				Thread.sleep(2000);
				if (cancelUiWathcher().findElement("label=='以后' OR label LIKE '稍后*'").click()) {
					continue;
				}
				Shot.drawText("权限", "处理权限");
				driver.switchTo().alert().accept();// 1.9.0后不抛出异常了....
			} while (true && count < 20);
		} catch (Exception e) {
			// TODO: handle exception
			// logger.info("无弹出框提示");
			count--;
		}
		oplog.logInfo("处理权限管理完成.共处理" + count + "个权限提示");
		return count != 0;
	}

	/**
	 * 点击弹出框按钮
	 * 
	 * @param isleft true为左边,false为右边
	 * @return 点击成功返回true
	 */
	public boolean TapAlertBox(boolean isleft) {
		String picpath = null;
		try {
			if (isleft) {
				oplog.logStep("点击弹出框左边按钮");
				picpath = Shot.drawText("弹出框", "点击弹出框左边按钮");
				driver.switchTo().alert().dismiss();
			} else {
				oplog.logStep("点击弹出框右边按钮");
				picpath = Shot.drawText("弹出框", "点击弹出框右边按钮");
				driver.switchTo().alert().accept();
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			if (picpath != null)
				Shot.drawTextPicture(picpath, "点击弹出框失败", false);
		}
		return false;
	}

	/**
	 * 点击弹出框按钮 (Tap点击坐标错乱)
	 * 
	 * @param which 第几个按钮
	 * @return 点击成功返回true
	 */
	@Deprecated
	public boolean ClickAlertBox(int which) {
		try {
			String pagesource = driver.getPageSource();
			int start = pagesource.indexOf("<XCUIElementTypeAlert");
			int end = pagesource.indexOf("</XCUIElementTypeAlert>");
			if (start < 0 || end < 0) {
				Shot.drawText("弹出框", "无弹出框");
				oplog.logWarn("当前页面未发现弹出框");
				return false;
			}
			pagesource = pagesource.substring(start, end) + "</XCUIElementTypeAlert>";
			// <XCUIElementTypeButton type="XCUIElementTypeButton" name="允许" label="允许"
			// enabled="true" visible="true" x="188" y="363" width="135" height="44"/>
			Matcher m = Pattern.compile(
					"<XCUIElementTypeButton type=\"XCUIElementTypeButton\" name=\"(.*?)\" label=.*x=\"(.*?)\" y=\"(.*?)\" width=\"(.*?)\" height=\"(.*?)\"")
					.matcher(pagesource);
			int x = 0, y = 0, width = 0, height = 0;
			String name;
			if (m.groupCount() < which || m.groupCount() == 0) {
				Shot.drawText("弹出框", "未找到响应按钮");
				oplog.logWarn("处理弹出框,总共找到" + m.groupCount() + "按钮,但which=" + which + ".");
				return false;
			} else {
				for (int i = 0; i < which; i++) {
					m.find();
				}
				name = m.group(1);
				x = Integer.parseInt(m.group(2));
				y = Integer.parseInt(m.group(3));
				width = Integer.parseInt(m.group(4));
				height = Integer.parseInt(m.group(5));
				logger.info(name + "," + x + "," + y + "," + width + "," + height);
				int clickx = x + width / 2;
				int clicky = y + height / 2;
				oplog.logStep("处理弹出框,点击" + name + ",坐标信息(" + clickx + "," + clicky + ")");
				tap(clickx, clicky);
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Excepiton", e);
		}
		return false;
	}

	@Override
	public boolean HandlePermission() {
		String[] keywords = { "以后", "稍后" };
		int count = 0;
		boolean isok = false;
		try {
			do {
				Thread.sleep(2000);
				isok = false;
				String pagesource = driver.getPageSource();
				int start = pagesource.indexOf("<XCUIElementTypeAlert");
				int end = pagesource.indexOf("</XCUIElementTypeAlert>");
				if (start < 0 || end < 0)
					break;// 跳出
				pagesource = pagesource.substring(start, end) + "</XCUIElementTypeAlert>";
				// <XCUIElementTypeAlert[\s\S]*?(<XCUIElementTypeStaticText.*?/>)[\s\S]*?(<XCUIElementTypeButton.*?/>)[\s\S]*?(<XCUIElementTypeButton.*?/>)[\s\S]*?</XCUIElementTypeAlert>
				// <XCUIElementTypeButton type="XCUIElementTypeButton" name="允许" label="允许"
				// enabled="true" visible="true" x="188" y="363" width="135" height="44"/>
				Matcher m = Pattern.compile(
						"<XCUIElementTypeButton type=\"XCUIElementTypeButton\" name=\"(.*)\" label=.*x=\"(.*)\" y=\"(.*)\" width=\"(.*)\" height=\"(.*)\"")
						.matcher(pagesource);
				int x = 0, y = 0, width = 0, height = 0;
				String name;
				Map<String, String> buttonMap = new LinkedHashMap<>();
				while (m.find()) {
					name = m.group(1);
					x = Integer.parseInt(m.group(2));
					y = Integer.parseInt(m.group(3));
					width = Integer.parseInt(m.group(4));
					height = Integer.parseInt(m.group(5));
					logger.info(name + "," + x + "," + y + "," + width + "," + height);
					buttonMap.put(name, x + "," + y + "," + width + "," + height);
				}
				Iterator<Entry<String, String>> iterator = buttonMap.entrySet().iterator();
				while (iterator.hasNext() && !isok) {
					Entry<String, String> entry = iterator.next();
					for (String str : keywords) {
						if (entry.getKey().contains(str) || !iterator.hasNext()) {
							String[] infos = entry.getValue().split(",");
							x = Integer.parseInt(infos[0]);
							y = Integer.parseInt(infos[1]);
							width = Integer.parseInt(infos[2]);
							width = Integer.parseInt(infos[3]);
							Shot.drawRect(Coperation.CLICK, x, y, width, height);
							x = x + width / 2;
							y = y + height / 2;
							oplog.logStep("点击元素:" + entry.getKey() + ",坐标(" + x + "," + y + ")");
							new TouchAction(driver).tap(PointOption.point(x, y)).perform();// appium异常,点击位置错误
							count++;
							isok = true;
							break;
						}
					}
				}
			} while (isok && count <= 10);
		} catch (Exception e) {
			// TODO: handle exception
			oplog.logWarn("权限处理异常");
			logger.error("EXCEPITON", e);
			count--;
		}
		oplog.logInfo("处理权限管理完成.共处理" + count + "个权限提示");
		return count != 0;
	}

}
