package com.appium;

import java.awt.Color;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.constant.Coperation;
import com.helper.ADBUtil;
import com.helper.AndroidInfo;
import com.helper.CMDUtil;
import com.helper.HelperUtil;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidShot;
import com.viewer.main.MainRun;
import com.xq.XQAndroidDriver;
import com.xq.XQAndroidOp;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

@SuppressWarnings("unchecked")
public class AndroidOp extends BaseOp {
	Logger logger = LoggerFactory.getLogger(AndroidOp.class);
	protected AndroidDriver<WebElement> driver;
	protected AndroidShot Shot;
	AndroidOpExtendSwipe androidOpExtendSwipe;
	XQAndroidOp xqAndroidOp;

	public AndroidOp(AndroidDriver<WebElement> driver, Map<String, String> capabilityMap, File reportFolder,
			SceneLogUtil oplog, Object baseShot, Translation translation) {
		super(capabilityMap, reportFolder, oplog, baseShot, translation);
		// TODO Auto-generated constructor stub
		this.driver = (AndroidDriver<WebElement>) driver;
		Shot = (AndroidShot) baseShot;
		androidOpExtendSwipe = new AndroidOpExtendSwipe(this.driver, this.Shot, oplog, this, translation);
		setWaitForNewWindow(true);
		xqAndroidOp = new XQAndroidOp(new XQAndroidDriver(udid, reportFolder, oplog, Shot, translation));
		elementMap.put("Driver", driver);
	}

	@Override
	public AndroidOp waitForNewWindow() {
		AndroidInfo.waitForNewWindow(driver, oplog);
		return this;
	}

	@Override
	public String getPageSource() {
		return AndroidInfo.getPageSource(driver)[0];
	}

	@Override
	public By MobileBy(String text) {
		text = text.trim();
		if (text != null && text.startsWith("#"))
			return MobileBy.AccessibilityId(text.substring(1));
		if (text != null && text.replaceAll("\\(", "").startsWith("/"))
			return MobileBy.xpath(text);
		if (text != null && text.contains(":id/"))
			return MobileBy.id(text);
		if (text != null && text.startsWith("android.") && !HelperUtil.hasChinese(text))
			return MobileBy.className(text);
		return MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + text + "\")");
	}

	/**
	 * 根据名称查找,返回By
	 * 
	 * @param Text
	 * @return
	 */
	public By MobileByText(String Text) {
		return MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + Text + "\")");
	}

	/**
	 * 根据名称包含字符串查找,返回By
	 * 
	 * @param Text
	 * @return
	 */
	public By MobileByContainText(String Text) {
		return MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + Text + "\")");
	}

	/**
	 * 根据名称开头字符串查找,返回By
	 * 
	 * @param Text
	 * @return
	 */
	public By MobileByStartText(String Text) {
		return MobileBy.AndroidUIAutomator("new UiSelector().textStartsWith(\"" + Text + "\")");
	}

	/**
	 * 根据名称正则匹配字符串查找,返回By
	 * 
	 * @param Text
	 * @return
	 */
	public By MobileByMatchText(String regex) {
		return MobileBy.AndroidUIAutomator("new UiSelector().textMatches(\"" + regex + "\")");
	}

	/**
	 * 根据名称查找元素
	 * 
	 * @param name
	 * @return
	 */
	public ElementAndroid findElementByText(String Text) {
		return findElementBy(MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + Text + "\")"));
	}

	/**
	 * 根据名称查找元素
	 * 
	 * @param Text
	 * @param time 等待时间,单位秒
	 * @return
	 */
	public ElementAndroid findElementByText(String Text, int time) {
		// TODO Auto-generated method stub
		return findElementBy(MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + Text + "\")"), time);
	}

	// @Override
	// public ElementAndroid findElementByClassName(String className){
	// return findElementBy(MobileBy.className(className));
	// }
	// @Override
	// public ElementAndroid findElementByXpath(String xpathExpression){
	// return findElementBy(MobileBy.xpath(xpathExpression));
	// }
	// @Override
	// public ElementAndroid findElementByClassName(String className, int time) {
	// // TODO Auto-generated method stub
	// return findElement(MobileBy.className(className), time);
	// }
	//
	// @Override
	// public ElementAndroid findElementByXpath(String xpathExpression, int time) {
	// // TODO Auto-generated method stub
	// return findElement(MobileBy.xpath(xpathExpression), time);
	// }
	//
	// @Override
	// public ElementAndroid findElementByAccessibilityId(String AccessibilityId){
	// return findElementBy(MobileBy.AccessibilityId(AccessibilityId));
	// }
	// @Override
	// public ElementAndroid findElementByAccessibilityId(String desc,int time){
	// return findElementBy(MobileBy.AccessibilityId(desc),time);
	// }
	/**
	 * 根据id查找元素
	 * 
	 * @param text
	 * @return
	 */
	public ElementAndroid findElementById(String id) {
		return findElementBy(MobileBy.id(id));
	}

	/**
	 * 根据id查找元素
	 * 
	 * @param text
	 * @param time 等待时间,单位秒
	 * @return
	 */
	public ElementAndroid findElementById(String id, int time) {
		return findElementBy(MobileBy.id(id), time);
	}

	/**
	 * 根据Uiautomator查找元素
	 * 
	 * @param text
	 * @return
	 */
	public ElementAndroid findElementByAndroidUIAutomator(String uiautomatorText) {
		return findElementBy(MobileBy.AndroidUIAutomator(uiautomatorText));
	}

	/**
	 * 根据Uiautomator查找元素
	 * 
	 * @param text
	 * @param time 等待时间,单位秒
	 * @return
	 */
	public ElementAndroid findElementByAndroidUIAutomator(String uiautomatorText, int time) {
		return findElementBy(MobileBy.AndroidUIAutomator(uiautomatorText), time);
	}

	@Override
	public ElementAndroid findOneOfElements(int second, By... bys) {
		boolean showwarn = true;
		if (!tempUiWatcherFlag)
			showwarn = false;
		sysSleep(1000);
		ElementAndroid elementAndroid = null;
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
			elementAndroid = findElement(by, second);
			if (elementAndroid.exist()) {
				oplog.logInfo("找到元素:" + elementAndroid.Text);
				return elementAndroid;
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
		return elementAndroid;
	}

	@Override
	public ElementAndroid findElement(String text) {
		// TODO Auto-generated method stub
		return findElementBy(MobileBy(text));
	}

	@Override
	public ElementAndroid findElement(String text, int time) {
		// TODO Auto-generated method stub
		return findElementBy(MobileBy(text), time);
	}

	@Override
	public ElementAndroid findElement(By by) {
		return findElementBy(by);
	}

	@Override
	public ElementAndroid findElement(By by, int time) {
		return findElementBy(by, time);
	}

	/**
	 * 查找元素方法
	 * 
	 * @param by
	 * @param args
	 * @return
	 */
	protected ElementAndroid findElementBy(By by, Object... args) {
		AndroidInfo.waitForNewWindow(driver, oplog);
		String Text = translation.getName(by);
		WebElement webElement = null;
		int timeOutInSeconds = MainRun.sysConfigBean.getWaitforElement();
		try {
			if (args.length > 0 && args[0] instanceof Integer) {
				timeOutInSeconds = (Integer) args[0];
			}
			webElement = new AndroidDriverWait(driver, timeOutInSeconds)
					.until(new AndroidExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(AndroidDriver<WebElement> driver) {
							// TODO Auto-generated method stub
							return driver.findElement(by);
						}
					});
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
					webElement = new AndroidDriverWait(driver, 2).until(new AndroidExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(AndroidDriver driver) {
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
		return new ElementAndroid(webElement, by, Text, elementMap);
	}

	@Override
	public List<ElementAndroid> findElements(By by) {
		// TODO Auto-generated method stub
		return findElementsBy(by);
	}

	@Override
	public List<String> findNamesbyElements(By by) {
		// TODO Auto-generated method stub
		List<ElementAndroid> list = findElementsBy(by);
		List<String> nameList = new ArrayList<>();
		for (ElementAndroid elementAndroid : list) {
			nameList.add(elementAndroid.getText());
		}
		return nameList;
	}

	@Override
	public ElementAndroid findElementByElements(By by, int index) {
		// TODO Auto-generated method stub
		String Text = translation.getName(by) + "," + index;
		ElementAndroid elementAndroid = new ElementAndroid(null, by, Text, elementMap);
		List<ElementAndroid> elementAndroids = findElements(by);
		if (index <= elementAndroids.size() - 1) {
			elementAndroid = elementAndroids.get(index);
		} else {
			oplog.logWarn("(" + Text + ")只有" + elementAndroids.size() + "个元素");
		}
		return elementAndroid;
	}

	@Override
	public ElementAndroid findElementByElements(String textandindex) {
		// TODO Auto-generated method stub
		String info = textandindex.split(",")[0];
		int index = Integer.parseInt(textandindex.split(",")[1]);
		By by = MobileBy(info);
		String Text = translation.getName(textandindex) + "," + index;
		ElementAndroid elementAndroid = new ElementAndroid(null, by, Text, elementMap);
		List<ElementAndroid> elementAndroids = findElements(by);
		if (index <= elementAndroids.size() - 1) {
			elementAndroid = elementAndroids.get(index);
		} else {
			oplog.logWarn("(" + Text + ")只有" + elementAndroids.size() + "个元素");
		}
		return elementAndroid;
	}

	/**
	 * 查找元素组方法
	 * 
	 * @param by
	 * @param args
	 * @return
	 */
	protected List<ElementAndroid> findElementsBy(By by, Object... args) {
		// TODO Auto-generated method stub
		AndroidInfo.waitForNewWindow(driver, oplog);
		String Text = translation.getName(by);
		List<ElementAndroid> elementAndroids = new ArrayList<>();
		List<WebElement> webElements = driver.findElements(by);
		if (webElements.size() == 0) {
			oplog.logWarn("未找到元素组:" + Text);
			Shot.drawText(Coperation.NOT_FIND_ELEMENT, "未找到元素组:" + Text);
		} else {
			oplog.logInfo("找到" + webElements.size() + "个元素:" + Text);
		}
		for (WebElement webElement : webElements) {
			elementAndroids.add(new ElementAndroid(webElement, by, Text, elementMap));
		}
		return elementAndroids;
	}

	/**
	 * 按下返回按钮
	 */
	public void pressBACK() {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText("返回", "按下BACK按钮");
		oplog.logStep("按下BACK按钮");
		// driver.pressKeyCode(AndroidKeyCode.BACK);
		driver.pressKey(new KeyEvent().withKey(AndroidKey.BACK));
	}

	/**
	 * 连续按下times次返回按钮
	 * 
	 * @param times
	 * @param millisecond 每次按下间隔时间
	 */
	public void pressBACK(int times, int millisecond) {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText("返回", "连续按下" + times + "次BACK按钮");
		oplog.logStep("连续按下" + times + "次BACK按钮");
		if (millisecond > 8000)
			millisecond = 8000;
		if (millisecond < 0)
			millisecond = 0;
		if (times < 1)
			times = 1;
		for (int i = 0; i < times; i++) {
			driver.pressKey(new KeyEvent().withKey(AndroidKey.BACK));
			try {
				Thread.sleep(millisecond);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		// driver.pressKeyCode(AndroidKeyCode.BACK);
		// driver.pressKeyCode(AndroidKeyCode.BACK);
	}

	/**
	 * 按下HOME按钮
	 */
	public void pressHOME() {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText("Home", "按下HOME按钮");
		oplog.logStep("按下HOME按钮");
		// driver.pressKeyCode(AndroidKeyCode.HOME);
		driver.pressKey(new KeyEvent().withKey(AndroidKey.HOME));
	}

	/**
	 * 按下音量上按钮
	 */
	public void pressVOLUME_UP() {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText("VOLUME_UP", "按下VOLUME_UP按钮");
		oplog.logStep("按下VOLUME_UP按钮");
		// driver.pressKeyCode(AndroidKeyCode.KEYCODE_VOLUME_UP);
		driver.pressKey(new KeyEvent().withKey(AndroidKey.VOLUME_UP));
	}

	/**
	 * 按下音量下按钮
	 */
	public void pressVOLUME_DOWN() {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText("VOLUME_DOWN", "按下VOLUME_DOWN按钮");
		oplog.logStep("按下VOLUME_DOWN按钮");
		// driver.pressKeyCode(AndroidKeyCode.KEYCODE_VOLUME_DOWN);
		driver.pressKey(new KeyEvent().withKey(AndroidKey.VOLUME_DOWN));
	}

	/**
	 * 按下电源键按钮
	 */
	public void pressPOWER() {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText("POWER", "按下POWER按钮");
		oplog.logStep("按下POWER按钮");
		// driver.pressKeyCode(AndroidKeyCode.KEYCODE_POWER);
		driver.pressKey(new KeyEvent().withKey(AndroidKey.POWER));
		sleep(2);
	}

	/**
	 * 模拟键值发送
	 * 
	 * @param KeyCode
	 * @return
	 */
	public void pressKeyCode(AndroidKey KeyCode) {
		// TODO Auto-generated method stub
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText(Coperation.KEYCODE, "按下键值=" + KeyCode.getCode() + "按钮");
		oplog.logStep("按下键值=" + KeyCode.getCode() + "按钮");
		// driver.pressKeyCode(KeyCode);
		driver.pressKey(new KeyEvent().withKey(KeyCode));
	}

	/**
	 * 模拟长按键值发送
	 * 
	 * @param KeyCode
	 * @return
	 */
	public void longPressKeyCode(AndroidKey KeyCode) {
		AndroidInfo.waitForNewWindow(driver, oplog);
		Shot.drawText(Coperation.KEYCODE, "长按键值=" + KeyCode + "按钮");
		oplog.logStep("长按键值=" + KeyCode + "按钮");
		// driver.longPressKeyCode(KeyCode);
		driver.longPressKey(new KeyEvent().withKey(KeyCode));
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
		// new TouchAction(driver).press(startx,
		// starty).waitAction(Duration.ofMillis(duration)).moveTo(endx,
		// endy).release().perform();
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
			op_tap(x, y);
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
		Shot.drawOval(Coperation.TAP, x, y);
		waittime(stime);
		oplog.logStep("点击坐标(" + x + "," + y + ")");
		op_tap(x, y);
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
			op_tap(x, y);
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
		Shot.drawOval(Coperation.LONGTAP, x, y, Color.decode(Cconfig.GREEN_DEEP));
		waittime(stime);
		oplog.logStep("轻触坐标(" + x + "," + y + ")" + duration + "毫秒");
		new TouchAction(driver).press(PointOption.point(x, y))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).release().perform();
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
		op_tap(x, y);
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
			op_tap(x, y);
			sysSleep(millisecond);
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
		oplog.logStep("轻触坐标(" + translation.getName(text) + ")" + duration + "毫秒");
		new TouchAction(driver).press(PointOption.point(x, y))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).release().perform();
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
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).release();
		TouchAction actionB = new TouchAction(driver).press(PointOption.point(x + 5, y + 5))
				// .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
				.moveTo(PointOption.point(x_length, y_length)).release();
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
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).release();
		TouchAction actionB = new TouchAction(driver).press(PointOption.point(x2, y2))
				// .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
				.moveTo(PointOption.point(center_x - 10 - x2, center_y - 10 - y2)).release();
		oplog.logStep("缩小坐标(" + x1 + "," + y1 + ")-(" + x2 + "," + y2 + "),滑动时间=" + duration);
		String picpath = Shot.drawArrow(Coperation.ZOOM, x1, y1, center_x, center_y, false);
		if (picpath != null)
			Shot.drawArrowPic(picpath, x2, y2, center_x, center_y, true);
		waittime(stime);
		new MultiTouchAction(driver).add(actionA).add(actionB).perform();
	}

	// /**
	// * Timer:检查程序是否出现异常(1.是否在表面 2.是否存活)
	// */
	// protected void Timer_checkAPPstatus() {
	// Timer timer=new Timer();
	// timer.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// ForcestopFlag=!AndroidInfo.checkOnSurface(udid,capabilityMap.get("appPackage"));
	// ForcestopFlag=!AndroidInfo.checkIsAlive(udid,capabilityMap.get("appPackage"));
	// }
	// }, 0,MainRun.sysConfigBean.getCheckOnSurface()*1000);
	//
	// }
	/**
	 * 卸载应用
	 * 
	 * @param appPackage 应用包名
	 */
	public boolean uninstallApp(String packagename) {
		if (AndroidInfo.isAppInstall(udid, packagename)) {
			if (AndroidInfo.uninstallApp(udid, packagename)) {
				oplog.logInfo("卸载应用成功:" + packagename);
				return true;
			} else {
				oplog.logError("卸载应用失败:" + packagename);
			}
		} else {
			oplog.logWarn("应用不存在:" + packagename);
		}
		return false;
	}

	/**
	 * 安装应用
	 * 
	 * @param appPath  应用本地路劲
	 * @param isluanch 安装后是否启动应用
	 * @return
	 */
	public boolean installApp(String appPath, boolean isluanch) {
		oplog.logInfo("开始安装应用:" + appPath);
		InstallRunnable installRunnable = new InstallRunnable(appPath);
		new Thread(installRunnable).start();
		int count = 0;
		while (!installRunnable.isSend()) {
			if (count > 100)
				break;
			try {
				WebElement webElement = new AndroidDriverWait(driver, 5)
						.until(new AndroidExpectedCondition<WebElement>() {
							@Override
							public WebElement apply(AndroidDriver driver) {
								// TODO Auto-generated method stub
								return driver.findElement(MobileBy.AndroidUIAutomator(
										"new UiSelector().className(\"android.widget.Button\").textMatches(\".*[删除|安装|确定|允许|同意|确认].*\")"));
							}
						});
				webElement.click();
				oplog.logStep("<<安装应用>>点击一次确认框");
			} catch (Exception e) {
				// TODO: handle exception
				count++;
				logger.info("try to install app: " + count);
			}
			if (installRunnable.isSend())
				break;
		}
		if (isluanch)
			launchApp();
		return installRunnable.isSuccess();
	}

	/**
	 * 安装应用线程
	 * 
	 * @author Then
	 *
	 */
	class InstallRunnable implements Runnable {
		boolean send = false;
		boolean success = false;
		String appPath;

		public InstallRunnable(String appPath) {
			// TODO Auto-generated constructor stub
			this.appPath = appPath;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			String[] installstrs = CMDUtil.execcmd(CAndroidCMD.INSTALL_APP.replace("#appPath#", appPath),
					CAndroidCMD.SYSCMD, true);
			logger.info("安装返回0:" + installstrs[0]);
			logger.info("安装返回1:" + installstrs[1]);
			if (installstrs[0].contains("Success") || installstrs[1].contains("Success")
					|| (installstrs[0].equals("") && !installstrs[1].toLowerCase().contains("failed"))) {
				oplog.logInfo("安装应用成功:" + appPath);
				success = true;
			} else {
				oplog.logError("安装应用失败:" + appPath + "(" + installstrs[0] + ")(" + installstrs[1] + ")");
				success = false;
			}
			send = true;
		}

		public boolean isSend() {
			return send;
		}

		public boolean isSuccess() {
			return success;
		}
	}

	@Override
	public boolean launchApp() {
		oplog.logStep("启动应用入口(" + capabilityMap.get("appPackage") + "/" + capabilityMap.get("appActivity") + ")");
		// Activity activity = new Activity(capabilityMap.get("appPackage"),
		// capabilityMap.get("appActivity"));
		// activity.setAppWaitPackage(capabilityMap.get("appPackage"));
		// activity.setAppWaitActivity(capabilityMap.get("appActivity"));
		// driver.startActivity(activity);
		// driver.launchApp();//BUG会清掉APP数据
		// [warn] [AndroidDriver] No app sent in, not parsing package/activity
		// [debug] [AndroidDriver] No app capability. Assuming it is already on the
		// device
		// [debug] [AndroidDriver] Running fast reset (stop and clear)
		if (AndroidInfo.launchApp(udid, capabilityMap.get("appPackage"), capabilityMap.get("appActivity"))) {
			sleep(2);// 启动后立刻执行driver.getpagesrouce会出现socket包异常.
			oplog.logInfo("启动应用成功");
			return true;
		} else {
			oplog.logError("启动应用失败");
			return false;
		}
	}

	@Override
	public boolean closeApp() {
		// TODO Auto-generated method stub
		oplog.logStep("关闭应用");
		sysSleep(2500);
		if (AndroidInfo.stopApp(udid, capabilityMap.get("appPackage"))) {
			oplog.logInfo("关闭应用成功");
			return true;
		} else {
			// oplog.logError("关闭应用失败");
			return false;
		}
	}

	/**
	 * 清除应用数据
	 */
	public boolean clearApp() {
		oplog.logStep("清除应用数据");
		return AndroidInfo.clearApp(udid, capabilityMap.get("appPackage"));
	}

	@Override
	public void setScreenSize(int width, int hight) {
		// TODO Auto-generated method stub
		resolution_width_multiple = (double) Shot.getDevice_width() / width;
		resolution_hight_multiple = (double) Shot.getDevice_hight() / hight;
		oplog.logInfo("当前设备宽=" + Shot.getDevice_width() + ",高=" + Shot.getDevice_hight() + ";" + "原始设备宽=" + width
				+ ",高=" + hight + ";" + "得到宽倍数=" + resolution_width_multiple + ",高倍数=" + resolution_hight_multiple);
		elementMap.put("resolution_width_multiple", resolution_width_multiple);
		elementMap.put("resolution_hight_multiple", resolution_hight_multiple);
	}

	@Override
	public void sleep(int second) {
		try {
			if (second < 1)
				second = 1;
			oplog.logInfo("休眠" + second + "秒");
			new AndroidDriverWait(driver, second, 1000).until(new AndroidExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(AndroidDriver<WebElement> driver) {
					// TODO Auto-generated method stub
					return driver.findElement(MobileBy.className("休眠专用名称"));
				}
			});
		} catch (RuntimeException e) {
		}
	}

	/**
	 * 扩展操作_滑动
	 * 
	 * @return
	 */
	public AndroidOpExtendSwipe EXTEND_SWIPE() {
		// TODO Auto-generated method stub
		return androidOpExtendSwipe;
	}

	@Override
	public AndroidOp cancelUiWathcher() {
		// TODO Auto-generated method stub
		tempUiWatcherFlag = false;
		return this;
	}

	@Override
	public AndroidOp cancelAutoMultiple() {
		AutoMultipleFlag = false;
		return this;
	}

	/**
	 * 设置是否等待界面不再变化,主要用于动态窗口等待时间太久.
	 * 
	 * @param iswait 默认true
	 */
	public void setWaitForNewWindow(boolean iswait) {
		oplog.logInfo("设置等待界面变化完成:" + iswait);
		AndroidInfo.setWaitForNewWindow(iswait);
	}

	/**
	 * 手势操作<br>
	 * Android单位为像素,iOS单位为point<br>
	 * operation:"startx,starty->addx,addy->addx,addy->..."<br>
	 * ps:"300,300->100,-50->0,100"意思说从坐标点300,300开始,向X轴移动100,Y轴移动-50,然后X轴移动0,Y轴移动100.<br>
	 * 
	 * @param operation
	 * @return
	 */
	public boolean gesture(String operation) {
		String Text = translation.getName(operation);
		String picpath = null;
		try {
			String[] ops = operation.split("->");
			TouchAction touchAction = new TouchAction(driver);
			int startx = 0;
			int starty = 0;
			for (int j = 0; j < ops.length; j++) {
				String[] xy = ops[j].split(",");
				int x = Integer.parseInt(xy[0]);
				int y = Integer.parseInt(xy[1]);
				if (AutoMultipleFlag) {
					x = X_multiple(x);
					y = Y_multiple(y);
				}
				AutoMultipleFlag = true;
				if (j == 0) {
					touchAction.press(PointOption.point(x, y))
							.waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)));
				} else if (j == 1 && ops.length > 2) {
					touchAction.moveTo(PointOption.point(x, y))
							.waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)));
					picpath = Shot.drawArrow(Coperation.SWIPE, startx, starty, startx + x, starty + y, false);
				} else if (j == ops.length - 1) {
					touchAction.moveTo(PointOption.point(x, y))
							.waitAction(WaitOptions.waitOptions(Duration.ofMillis(500))).release();
					Shot.drawArrowPic(picpath, startx, starty, startx + x, starty + y, true);
				} else {
					touchAction.moveTo(PointOption.point(x, y))
							.waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)));
					Shot.drawArrowPic(picpath, startx, starty, startx + x, starty + y, false);
				}
				startx = x + startx;
				starty = y + starty;
			}
			oplog.logStep(Coperation.GESTURE + "(" + Text + ")");
			touchAction.perform();
			return true;
		} catch (Exception e) {
			oplog.logWarn(Coperation.OPEXCEPITON);
			if (picpath != null)
				Shot.drawTextPicture(picpath, Coperation.GESTURE + "失败" + "(" + Text + ")", false);
		}
		oplog.logWarn(Coperation.GESTURE + "失败(" + Text + ")");
		return false;
	}

	@Override
	protected int X_multiple(int x) {
		int value = (int) (resolution_width_multiple * x);
		if (Math.abs(value) >= Shot.getDevice_width()) {
			if (Math.abs(value) != Shot.getDevice_width())
				oplog.logWarn("X坐标" + x + ",自适应后坐标" + value + ",超出X坐标范围[1," + Shot.getDevice_width() + "],置为"
						+ (value >= 0 ? Shot.getDevice_width() - 1 : -Shot.getDevice_width() + 1));
			value = (value >= 0 ? Shot.getDevice_width() - 1 : -Shot.getDevice_width() + 1);
		} else if (value == 0) {
			value = 1;
		}
		return value;
	}

	@Override
	protected int Y_multiple(int y) {
		int value = (int) (resolution_hight_multiple * y);
		if (Math.abs(value) >= Shot.getDevice_hight()) {
			if (Math.abs(value) != Shot.getDevice_hight())
				oplog.logWarn("Y坐标" + y + ",自适应后坐标" + value + ",超出Y坐标范围[1," + Shot.getDevice_hight() + "],置为"
						+ (value >= 0 ? Shot.getDevice_hight() - 1 : -Shot.getDevice_hight() + 1));
			value = (value >= 0 ? Shot.getDevice_hight() - 1 : -Shot.getDevice_hight() + 1);
		} else if (value == 0) {
			value = 1;
		}
		return value;
	}

	@Override
	public boolean HandlePermission() {
		StringBuffer keywordsBuf = new StringBuffer("好的|同意|确定|允许|确认|知道|立即删除|关闭|重启应用");
		oplog.logInfo("开始处理权限管理...");
		int count = 0;
		boolean isok;
		do {
			isok = cancelUiWathcher().findElementBy(MobileBy
					.AndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").textMatches(\".*["
							+ keywordsBuf.toString() + "].*\")"))
					.click();
			if (isok)
				count++;
		} while (isok);
		oplog.logInfo("处理权限管理完成.共处理" + count + "个权限提示");
		return count != 0;
	}

	/**
	 * 转化到XQOP,目前主要用于动态页面操作
	 * 
	 * @return
	 */
	public XQAndroidOp toXQOP() {
		return xqAndroidOp;
	}

	/**
	 * 替换应用启动信息
	 * 
	 * @param appPackage
	 * @param appActivity
	 */
	public void setAppInfo(String appPackage, String appActivity) {
		oplog.logInfo("更新:将" + capabilityMap.get("appPackage") + "替换为" + appPackage);
		oplog.logInfo("更新:将" + capabilityMap.get("appActivity") + "替换为" + appActivity);
		capabilityMap.put("appPackage", appPackage);
		capabilityMap.put("appActivity", appActivity);
	}

	/**
	 * 点击
	 * 
	 * @param x
	 * @param y
	 */
	private void op_tap(int x, int y) {
		// new TouchAction(driver).tap(PointOption.point(x, y)).perform();
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_TAP.replace("#x#", x + "").replace("#y#", y + ""));
	}

}
