package com.appium;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Coperation;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidShot;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import sun.util.logging.resources.logging;

public class AndroidOpExtendSwipe extends BaseOpExtendSwipe {
	Logger logger = LoggerFactory.getLogger(logging.class);
	AndroidDriver<WebElement> driver;
	AndroidShot Shot;
	AndroidOp Op;

	public AndroidOpExtendSwipe(Object driver, Object Shot, SceneLogUtil oplog, AndroidOp androidOp,
			Translation translation) {
		super(oplog, translation);
		// TODO Auto-generated constructor stub
		this.driver = (AndroidDriver<WebElement>) driver;
		this.Shot = (AndroidShot) Shot;
		this.Op = androidOp;
		device_width = this.Shot.getDevice_width();
		device_hight = this.Shot.getDevice_hight();
		setSwipeTime(1500);
	}

	@Override
	public void scrolldown() {
		// TODO Auto-generated method stub
		oplog.logInfo("开始:向下翻页");
		swipe(device_width / 2, device_hight / scroll_hight_blocks, device_width / 2,
				device_hight / scroll_hight_blocks * (scroll_hight_blocks - 1), SWIPE_TIME);
		oplog.logInfo("结束:向下翻页");
		// driver.swipe(device_width/2, device_hight/4, device_width/2,
		// device_hight/4*3, 300);
	}

	@Override
	public void scrolldown(int times) {
		// TODO Auto-generated method stub
		if (times < 1)
			times = 1;
		for (int i = 0; i < times; i++) {
			scrolldown();
		}
	}

	@Override
	public void scrollup() {
		// TODO Auto-generated method stub
		oplog.logInfo("开始:向上翻页");
		swipe(device_width / 2, device_hight / scroll_hight_blocks * (scroll_hight_blocks - 1), device_width / 2,
				device_hight / scroll_hight_blocks, SWIPE_TIME);
		oplog.logInfo("结束:向上翻页");
	}

	@Override
	public void scrollup(int times) {
		// TODO Auto-generated method stub
		if (times < 1)
			times = 1;
		for (int i = 0; i < times; i++) {
			scrollup();
		}
	}

	@Override
	public void scrollleft() {
		// TODO Auto-generated method stub
		oplog.logInfo("开始:向左翻页");
		swipe(device_width / scroll_width_blocks * (scroll_width_blocks - 1), device_hight / 2,
				device_width / scroll_width_blocks, device_hight / 2, SWIPE_TIME);
		oplog.logInfo("结束:向左翻页");
	}

	@Override
	public void scrollleft(int times) {
		// TODO Auto-generated method stub
		if (times < 1)
			times = 1;
		for (int i = 0; i < times; i++) {
			scrollleft();
		}
	}

	@Override
	public void scrollright() {
		// TODO Auto-generated method stub
		oplog.logInfo("开始:向右翻页");
		swipe(device_width / scroll_width_blocks, device_hight / 2,
				device_width / scroll_width_blocks * (scroll_width_blocks - 1), device_hight / 2, SWIPE_TIME);
		oplog.logInfo("结束:向右翻页");
	}

	@Override
	public void scrollright(int times) {
		// TODO Auto-generated method stub
		if (times < 1)
			times = 1;
		for (int i = 0; i < times; i++) {
			scrollright();
		}
	}

	@Override
	public ElementAndroid scrolldownTo(By by, int scrollnum) {
		// TODO Auto-generated method stub
		return scrollTo(device_width / 2, device_hight / scroll_hight_blocks, device_width / 2,
				device_hight / scroll_hight_blocks * (scroll_hight_blocks - 1), by, scrollnum, DOWN);
	}

	@Override
	public ElementAndroid scrollupTo(By by, int scrollnum) {
		// TODO Auto-generated method stub
		return scrollTo(device_width / 2, device_hight / scroll_hight_blocks * (scroll_hight_blocks - 1),
				device_width / 2, device_hight / scroll_hight_blocks, by, scrollnum, UP);
	}

	@Override
	public ElementAndroid scrollleftTo(By by, int scrollnum) {
		// TODO Auto-generated method stub
		return scrollTo(device_width / scroll_width_blocks * (scroll_width_blocks - 1), device_hight / 2,
				device_width / scroll_width_blocks, device_hight / 2, by, scrollnum, LEFT);
	}

	@Override
	public ElementAndroid scrollrightTo(By by, int scrollnum) {
		// TODO Auto-generated method stub
		return scrollTo(device_width / scroll_width_blocks, device_hight / 2,
				device_width / scroll_width_blocks * (scroll_width_blocks - 1), device_hight / 2, by, scrollnum, RIGHT);
	}

	@Override
	protected ElementAndroid scrollTo(int startx, int starty, int endx, int endy, By by, int scrollnum,
			String direction) {
		String Text = translation.getName(by);
		oplog.logInfo("开始:向" + direction + "翻页寻找:" + Text);
		ElementAndroid element = null;
		if (direction.equals(RIGHT) || direction.equals(LEFT)) {
			element = scroll2Target(direction, startx, starty, endx, endy, by, scrollnum, Text, 0,
					Shot.getDevice_width());
		} else if (direction.equals(DOWN) || direction.equals(UP)) {
			element = scroll2Target(direction, startx, starty, endx, endy, by, scrollnum, Text, 0,
					Shot.getDevice_hight());
		}
		if (element.exist()) {
			oplog.logInfo("结束:向" + direction + "翻页寻找:" + Text + "," + "找到");
		} else {
			Shot.drawText(Coperation.NOT_FIND_ELEMENT, "未找到元素:" + Text);
			oplog.logWarn("结束:向" + direction + "翻页寻找:" + Text + "," + "未找到");
		}
		return element;
	}

	@Override
	protected ElementAndroid scroll2Target(String direction, int startx, int starty, int endx, int endy, By by,
			int scrollnum, String Text, int minlimit, int maxlimit) {
		ElementAndroid element = scroll2TargetCore(direction, startx, starty, endx, endy, by, scrollnum, Text, minlimit,
				maxlimit);
		// 为了解决有时候元素被滑过了而造成的无法找到元素,相反方向半滑动
		if (!element.exist()) {
			oplog.logWarn("仍然未找到元素,反向折半搜索:" + Text);
			scrollnum = scrollnum * 2;
			if (direction.equals(LEFT)) {
				direction = RIGHT;
				int tempx = startx;
				startx = Math.abs(startx - endx) / 2 + endx;
				endx = tempx;
				endy = starty;
			} else if (direction.equals(UP)) {
				direction = DOWN;
				int tempy = starty;
				starty = Math.abs(starty - endy) / 2 + endy;
				endx = startx;
				endy = tempy;
			} else if (direction.equals(RIGHT)) {
				direction = LEFT;
				int tempx = startx;
				startx = Math.abs(startx - endx) / 2 + startx;
				endx = tempx;
				endy = starty;
			} else if (direction.equals(DOWN)) {
				direction = UP;
				int tempy = starty;
				starty = Math.abs(starty - endy) / 2 + starty;
				endx = startx;
				endy = tempy;
			}
			element = scroll2TargetCore(direction, startx, starty, endx, endy, by, scrollnum, Text, minlimit, maxlimit);
		}
		return element;
	}

	@Override
	protected ElementAndroid scroll2TargetCore(String direction, int startx, int starty, int endx, int endy, By by,
			int scrollnum, String Text, int minlimit, int maxlimit) {
		if (scrollnum < 0)
			scrollnum = 10;
		int count = 0;
		ElementAndroid element = new ElementAndroid(null, by, Text, Op.getElementMap());
		do {
			count++;
			WebElement webElement = null;
			try {
				webElement = new AndroidDriverWait(driver, 3).until(new AndroidExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(AndroidDriver driver) {
						// TODO Auto-generated method stub
						return driver.findElement(by);
					}
				});
			} catch (RuntimeException e) {
			}
			if (webElement == null) {
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (webElement != null) {
				Point point = webElement.getLocation();
				Dimension dimension = webElement.getSize();
				if (direction.equals(LEFT) || direction.equals(RIGHT)) {
					if (point.getX() + dimension.getWidth() / 2 > maxlimit
							|| point.getX() + dimension.getWidth() / 2 < minlimit) {// 大于最大,小于最小
						element.setWebElement(null, by);
						swipe(startx, starty, endx, endy, SWIPE_TIME);
					} else {
						element.setWebElement(webElement, by);
					}
				} else if (direction.equals(UP) || direction.equals(DOWN)) {
					if (point.getY() + dimension.getHeight() / 2 > maxlimit
							|| point.getY() + dimension.getHeight() / 2 < minlimit) {
						element.setWebElement(null, by);
						swipe(startx, starty, endx, endy, SWIPE_TIME);
					} else {
						element.setWebElement(webElement, by);
					}
				}
			}
		} while (!element.exist() && count < scrollnum);
		return element;
	}

	@Override
	public boolean swipeSeekbar(By by, String direction, int startadjust, int addx) {
		startadjust = Op.X_multiple(startadjust);
		addx = Op.X_multiple(addx);
		String Text = translation.getName(by);
		ElementAndroid element = Op.findElement(by);
		if (element.exist()) {
			oplog.logInfo("开始:向" + direction + "滑动" + Text + ",调整值=" + startadjust + ",移动值=" + addx);
			WebElement webElement = element.getWebElement();
			Point point = webElement.getLocation();
			Dimension dimension = webElement.getSize();
			if (direction.equals(RIGHT)) {
				int startx = point.getX() + startadjust;
				int starty = point.getY() + dimension.getHeight() / 2;
				int endx = startx + addx;
				int endy = starty;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (direction.equals(LEFT)) {
				int startx = point.getX() + dimension.getWidth() - startadjust;
				int starty = point.getY() + dimension.getHeight() / 2;
				int endx = startx - addx;
				int endy = starty;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (direction.equals(DOWN)) {
				int startx = point.getX() + dimension.getWidth() / 2;
				int starty = point.getY() + startadjust;
				int endx = startx;
				int endy = starty + addx;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (direction.equals(UP)) {
				int startx = point.getX() + dimension.getWidth() / 2;
				int starty = point.getY() + dimension.getHeight() - startadjust;
				int endx = startx;
				int endy = starty - addx;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			}
			oplog.logInfo("结束:向" + direction + "滑动" + Text + ",调整值=" + startadjust + ",移动值=" + addx);
			return true;
		} else {
			oplog.logWarn("未找到元素:" + Text + ",未进行滑动.");
			return false;
		}
	}

	@Override
	public boolean swipeSeekbar(By by, String direction, int startadjust, int addx, int times) {
		String Text = translation.getName(by);
		oplog.logInfo("开始滑动" + Text + ",共" + times + "次");
		boolean isok = true;
		for (int i = 0; i < times; i++) {
			if (!dragSeekbar(by, direction, startadjust, addx)) {
				isok = false;
			}
			// Op.sysSleep(800);
		}
		oplog.logInfo("结束滑动" + Text + ",共" + times + "次");
		return isok;
	}

	@Override
	public boolean dragSeekbar(By by, String direction, int startadjust, int endadjust) {
		// TODO Auto-generated method stub
		startadjust = Op.X_multiple(startadjust);
		endadjust = Op.X_multiple(endadjust);
		String Text = translation.getName(by);
		ElementAndroid element = Op.findElement(by);
		if (element.exist()) {
			oplog.logInfo("开始:向" + direction + "拖动" + Text + ",调整值=" + startadjust + "," + endadjust);
			WebElement webElement = element.getWebElement();
			Point point = webElement.getLocation();
			Dimension dimension = webElement.getSize();
			if (direction.equals(RIGHT)) {
				int startx = point.getX() + startadjust;
				int starty = point.getY() + dimension.getHeight() / 2;
				int endx = point.getX() + dimension.getWidth() - endadjust;
				int endy = starty;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (direction.equals(LEFT)) {
				int startx = point.getX() + dimension.getWidth() - startadjust;
				int starty = point.getY() + dimension.getHeight() / 2;
				int endx = point.getX() + endadjust;
				int endy = starty;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (direction.equals(DOWN)) {
				int startx = point.getX() + dimension.getWidth() / 2;
				int starty = point.getY() + startadjust;
				int endx = startx;
				int endy = point.getY() + dimension.getHeight() - endadjust;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			} else if (direction.equals(UP)) {
				int startx = point.getX() + dimension.getWidth() / 2;
				int starty = point.getY() + dimension.getHeight() - startadjust;
				int endx = startx;
				int endy = point.getY() + endadjust;
				swipe(startx, starty, endx, endy, SWIPE_TIME);
			}
			oplog.logInfo("结束:向" + direction + "拖动" + Text + ",调整值=" + startadjust + "," + endadjust);
			return true;
		} else {
			oplog.logWarn("未找到元素:" + Text + ",未进行拖动.");
			return false;
		}
	}

	@Override
	public boolean dragSeekbar(By by, String direction, int startadjust, int endadjust, int times) {
		String Text = translation.getName(by);
		oplog.logInfo("开始拖动" + Text + ",共" + times + "次");
		boolean isok = true;
		for (int i = 0; i < times; i++) {
			if (!dragSeekbar(by, direction, startadjust, endadjust)) {
				isok = false;
			}
			// Op.sysSleep(800);
		}
		oplog.logInfo("结束拖动" + Text + ",共" + times + "次");
		return isok;
	}

	@Override
	public ElementAndroid dragSeekbarTo(By by, String direction, int startadjust, int endadjust, By targetby,
			int scrollnum) {
		// TODO Auto-generated method stub
		startadjust = Op.X_multiple(startadjust);
		endadjust = Op.X_multiple(endadjust);
		String Text = translation.getName(by);
		String targetText = translation.getName(targetby);
		oplog.logInfo("开始:向" + direction + "拖动" + Text + ",调整值=" + startadjust + "," + endadjust + ",寻找" + targetText);
		ElementAndroid element = Op.findElement(by);
		ElementAndroid targetelement = new ElementAndroid(null, by, Text, Op.getElementMap());
		if (element.exist()) {
			WebElement webElement = element.getWebElement();
			Point point = webElement.getLocation();
			Dimension dimension = webElement.getSize();
			if (direction.equals(RIGHT)) {
				int startx = point.getX() + startadjust;
				int starty = point.getY() + dimension.getHeight() / 2;
				int endx = point.getX() + dimension.getWidth() - endadjust;
				int endy = starty;
				targetelement = scroll2Target(direction, startx, starty, endx, endy, targetby, scrollnum, targetText,
						startx, endx);
			} else if (direction.equals(LEFT)) {
				int startx = point.getX() + dimension.getWidth() - startadjust;
				int starty = point.getY() + dimension.getHeight() / 2;
				int endx = point.getX() + endadjust;
				int endy = starty;
				targetelement = scroll2Target(direction, startx, starty, endx, endy, targetby, scrollnum, targetText,
						endx, startx);
			} else if (direction.equals(DOWN)) {
				int startx = point.getX() + dimension.getWidth() / 2;
				int starty = point.getY() + startadjust;
				int endx = startx;
				int endy = point.getY() + dimension.getHeight() - endadjust;
				targetelement = scroll2Target(direction, startx, starty, endx, endy, targetby, scrollnum, targetText,
						starty, endy);
			} else if (direction.equals(UP)) {
				int startx = point.getX() + dimension.getWidth() / 2;
				int starty = point.getY() + dimension.getHeight() - startadjust;
				int endx = startx;
				int endy = point.getY() + endadjust;
				targetelement = scroll2Target(direction, startx, starty, endx, endy, targetby, scrollnum, targetText,
						endy, starty);
			}
		}
		if (targetelement.exist()) {
			oplog.logInfo("结束:向" + direction + "拖动" + Text + ",调整值=" + startadjust + "," + endadjust + ",寻找"
					+ targetText + "," + "找到");
		} else {
			Shot.drawText(Coperation.NOT_FIND_ELEMENT, "未找到元素:" + targetText);
			oplog.logWarn("结束:向" + direction + "拖动" + Text + ",调整值=" + startadjust + "," + endadjust + ",寻找"
					+ targetText + "," + "未找到");
		}
		return targetelement;
	}

	@Override
	public List<String> dragSeekbarTogetItems(By by, String direction, By item, int startadjust, int endadjust) {
		String Text = translation.getName(by);
		String itemText = translation.getName(item);
		List<String> list = new ArrayList<>();
		boolean nonew = true;
		int count = 0;
		do {
			nonew = true;
			for (ElementAndroid element : Op.findElement(by).findElements(item)) {
				String name = element.getText();
				if (name == null || name.equals("") || name.equals("null"))
					continue;
				if (list.contains(name)) {
					continue;
				} else {
					list.add(name);
					nonew = false;
				}
			}
			if (!nonew) {
				count++;
				dragSeekbar(by, direction, startadjust, endadjust);
			}
		} while (!nonew);
		for (int i = 0; i < count; i++) {
			if (direction.equals(LEFT)) {
				dragSeekbar(by, RIGHT, endadjust, startadjust);
			} else if (direction.equals(RIGHT)) {
				dragSeekbar(by, LEFT, endadjust, startadjust);
			} else if (direction.equals(DOWN)) {
				dragSeekbar(by, UP, endadjust, startadjust);
			} else if (direction.equals(UP)) {
				dragSeekbar(by, DOWN, endadjust, startadjust);
			}
		}
		StringBuffer stringBuffer = new StringBuffer();
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			stringBuffer.append(iterator.next());
			if (iterator.hasNext())
				stringBuffer.append(",");
		}
		oplog.logInfo(Text + "向" + direction + "共找到" + list.size() + "个子项:" + itemText + ",调整值:" + startadjust + ","
				+ endadjust);
		oplog.logInfo("子项:" + stringBuffer.toString());
		return list;
	}

	@Override
	protected void swipe(int startx, int starty, int endx, int endy, int duration) {
		// TODO Auto-generated method stub
		long stime = TimeUtil.getTime();
		if (startx >= device_width) {
			startx = device_width - 1;
		} else if (startx <= 0) {
			startx = 1;
		}
		if (endx >= device_width) {
			endx = device_width - 1;
		} else if (endx <= 0) {
			endx = 1;
		}
		if (starty >= device_hight) {
			starty = device_hight - 1;
		} else if (starty <= 0) {
			starty = 1;
		}
		if (endy >= device_hight) {
			endy = device_hight - 1;
		} else if (endy <= 0) {
			endy = 1;
		}
		Shot.drawArrow(Coperation.SWIPE, startx, starty, endx, endy);
		oplog.logStep("滑动(" + startx + "," + starty + ")到(" + endx + "," + endy + "),滑动时间=" + duration + "毫秒");
		// driver.swipe(startx, starty, endx, endy, duration);
		try {
			waittime(stime);
			new TouchAction(driver).press(PointOption.point(startx, starty))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
					.moveTo(PointOption.point(endx, endy)).release().perform();
		} catch (org.openqa.selenium.WebDriverException e) {// org.openqa.selenium.WebDriverException
			// TODO: handle exception
			logger.error("EXCEPITON", e);
		}

	}
}
