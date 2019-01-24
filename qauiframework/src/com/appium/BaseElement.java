package com.appium;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Coperation;
import com.log.SceneLogUtil;
import com.review.getscreen.BaseShot;

/**
 * 自定义元素
 *
 */
public abstract class BaseElement {
	Logger logger = LoggerFactory.getLogger(BaseElement.class);
	WebElement webElement;
	BaseShot Shot;
	SceneLogUtil oplog;
	String Text;
	Translation translation;
	By thisby;
	String udid;

	boolean isinit = false;// 第一次读取后就不需要再读取了
	Point point;
	Dimension dimension;
	protected double resolution_width_multiple = 1D;
	protected double resolution_hight_multiple = 1D;
	Map<String, Object> elementMap;

	public BaseElement(WebElement webElement, By by, String Text, Map<String, Object> elementMap) {
		// TODO Auto-generated constructor stub
		this.elementMap = elementMap;
		this.Text = Text;
		oplog = (SceneLogUtil) elementMap.get("SceneLogUtil");
		translation = (Translation) elementMap.get("Translation");
		this.udid = (String) elementMap.get("udid");
		resolution_width_multiple = (Double) elementMap.get("resolution_width_multiple");
		resolution_hight_multiple = (Double) elementMap.get("resolution_hight_multiple");
		setWebElement(webElement, by);
	}

	/**
	 * 设置WebElement元素
	 * 
	 * @param webElement
	 */
	public void setWebElement(WebElement webElement, By by) {
		this.webElement = webElement;
		this.thisby = by;
		isinit = false;
	}

	/**
	 * 初始化信息,减少重复请求时间
	 */
	protected boolean initInfo() {
		if (webElement != null) {
			if (!isinit) {
				isinit = true;
				Text = translation.getName(thisby);
				point = webElement.getLocation();
				dimension = webElement.getSize();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取元素位置信息
	 * 
	 * @return 0中心点坐标X,1中心点坐标Y,2矩形左上角坐标点X,3矩形左上角坐标点Y,4矩形宽度W,5矩形长度H (元素为空,则返回0)
	 */
	public int[] getPosition() {
		if (initInfo()) {
			return new int[] { point.getX() + dimension.getWidth() / 2, point.getY() + dimension.getHeight() / 2,
					point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight() };
		}
		oplog.logWarn("获取" + Text + "位置信息,失败");
		return new int[] { 0, 0, 0, 0, 0, 0 };
	}

	/**
	 * 设置控件属性
	 * 
	 * @param name     PS:checked,focused,selected
	 * @param selected
	 */
	@Deprecated
	public boolean setSelected(String name, boolean selected) {
		if (webElement != null) {
			if (selected) {
				if (webElement.getAttribute(name).equals("false"))
					click();
			} else {
				if (webElement.getAttribute(name).equals("true"))
					click();
			}
			oplog.logInfo("设置" + Text + "属性:" + name + "=" + selected + ",成功");
			return true;
		}
		oplog.logWarn("设置" + Text + "属性:" + name + "=" + selected + ",失败");
		return false;
	}

	/**
	 * 获取控件属性
	 * 
	 * @param name PS:checked,focused,selected
	 * @return
	 */
	@Deprecated
	public boolean getAttribute(String name) {
		if (webElement != null) {
			if (webElement.getAttribute(name).equals("true")) {
				return true;
			} else {
				return false;
			}
		}
		oplog.logWarn("未找到元素:" + Text + ",返回false");
		return false;
	}

	/**
	 * 点击
	 * 
	 * @return
	 */
	public abstract boolean click();

	/**
	 * 双击
	 * 
	 * @return
	 */
	public abstract boolean doubleclick();

	/**
	 * 点击并等待新窗口打开完成(原理:界面元素不再变化)
	 * 
	 * @return
	 */
	@Deprecated
	public boolean clickAndWaitForNewWindow() {
		String picpath = null;
		try {
			if (initInfo()) {
				picpath = Shot.drawRect(Coperation.CLICK, point.getX(), point.getY(), dimension.getWidth(),
						dimension.getHeight());// 操作后界面变化
				oplog.logStep(Coperation.CLICK + "(" + Text + ")");
				webElement.click();
				// waitForNewWindow();
				return true;
			}
		} catch (Exception e) {
			oplog.logWarn(Coperation.OPEXCEPITON);
			if (picpath != null)
				Shot.drawTextPicture(picpath, Coperation.CLICK + "失败", false);
		}
		oplog.logInfo(Coperation.CLICK + "失败(" + Text + ")");
		return false;
	}

	/**
	 * 清除文本
	 * 
	 * @return
	 */
	public boolean clearText() {
		String picpath = null;
		try {
			if (initInfo()) {
				picpath = Shot.drawRect(Coperation.CLEAR, point.getX(), point.getY(), dimension.getWidth(),
						dimension.getHeight());// 操作后界面变化
				oplog.logStep(Coperation.CLEAR + "(" + Text + ")");
				webElement.clear();
				return true;
			}
		} catch (Exception e) {
			oplog.logWarn(Coperation.OPEXCEPITON);
			if (picpath != null)
				Shot.drawTextPicture(picpath, Coperation.CLEAR + "失败", false);
		}
		oplog.logInfo(Coperation.CLEAR + "失败(" + Text + ")");
		return false;
	}

	/**
	 * 输入文本(需要将默认键盘设置为系统键盘)
	 * 
	 * @return
	 */
	public boolean sendText(String msm) {
		String picpath = null;
		try {
			if (initInfo()) {
				picpath = Shot.drawRect(Coperation.INPUT, point.getX(), point.getY(), dimension.getWidth(),
						dimension.getHeight());// 操作后界面变化
				oplog.logStep(Coperation.INPUT + ":" + msm + ",(" + Text + ")");
				webElement.sendKeys(msm);
				Shot.drawRect(Coperation.INPUT, point.getX(), point.getY(), dimension.getWidth(),
						dimension.getHeight());
				return true;
			}
		} catch (Exception e) {
			oplog.logWarn(Coperation.OPEXCEPITON);
			if (picpath != null)
				Shot.drawTextPicture(picpath, Coperation.INPUT + "失败", false);
		}
		oplog.logInfo(Coperation.INPUT + ":" + msm + ",失败(" + Text + ")");
		return false;
	}

	/**
	 * 长按
	 * 
	 * @param duration 时间(ms)
	 * @return
	 */
	public abstract boolean longtap(int duration);
	/**
	 * 放大
	 * 
	 * @return
	 */
	// public abstract boolean zoom();
	/**
	 * 缩小
	 * 
	 * @return
	 */

	// public abstract boolean pinch();
	/**
	 * 获取WebElement元素
	 * 
	 * @return
	 */
	public WebElement getWebElement() {
		if (webElement == null)
			oplog.logWarn("元素为空:" + Text);
		return webElement;
	}

	/**
	 * 获取元素Text
	 * 
	 * @return 未找到元素则返回字符串null
	 */
	public abstract String getText();

	/**
	 * 查找子元素
	 * 
	 * @param by
	 * @return
	 */
	public abstract <T> T findElement(By by);

	/**
	 * 查找元素组中的下标为index的子元素
	 * 
	 * @param by
	 * @param index
	 * @return
	 */
	public abstract <T> T findElementByElements(By by, int index);

	/**
	 * 查找子元素组
	 * 
	 * @param by
	 * @param args
	 * @return
	 */
	public abstract List<?> findElements(By by);

	/**
	 * 元素是否存在
	 * 
	 * @return boolean
	 */
	public abstract boolean exist();

	/**
	 * 拖到元素到指定增量位置<br>
	 * Ps:addx=100,addy=50.意味着将元素从当前位置向X轴移动100单位,向Y轴移动50单位.<br>
	 * 
	 * @param addx
	 * @param addy
	 * @return
	 */
	public abstract boolean dragTo(int addx, int addy);

	/**
	 * 拖到元素到指定元素位置<br>
	 * 
	 * @param baseElement
	 * @return
	 */
	public abstract boolean dragTo(BaseElement baseElement);

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
}
