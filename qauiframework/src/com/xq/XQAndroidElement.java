package com.xq;

import java.awt.Color;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appium.Translation;
import com.bean.XQAndroidElementBean;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.constant.Coperation;
import com.helper.ADBUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidShot;

public class XQAndroidElement {
	Logger logger = LoggerFactory.getLogger(XQAndroidElement.class);
	XQAndroidElementBean bean;
	String udid;
	AndroidShot Shot;
	SceneLogUtil oplog;
	String Text;
	Translation translation;
	XQAndroidDriver driver;

	public XQAndroidElement(XQAndroidDriver driver, XQAndroidElementBean bean) {
		// TODO Auto-generated constructor stub
		this.bean = bean;
		this.udid = driver.getUdid();
		this.oplog = driver.getOplog();
		this.Shot = driver.getShot();
		this.translation = driver.getTranslation();
		this.driver = driver;
		if (bean != null) {
			this.Text = bean.getNickname();
		}
	}

	/**
	 * 设置元素Bean
	 * 
	 * @param bean
	 */
	public void setBean(XQAndroidElementBean bean) {
		this.bean = bean;
	}

	/**
	 * 查找元素 自动区分text/id/class及content-desc需要加上#
	 * 
	 * @param text
	 * @return
	 */
	public XQAndroidElement findElement(String text) {
		List<XQAndroidElement> list = driver.findElementBy(bean.getElement(), text, null);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return new XQAndroidElement(driver, null);
		}
	}

	/**
	 * 查找元素组 自动区分text/id/class及content-desc需要加上#
	 * 
	 * @param text
	 * @return
	 */
	public List<XQAndroidElement> findElements(String text) {
		return driver.findElementBy(bean.getElement(), text, null);
	}

	/**
	 * 长按
	 * 
	 * @param duration 毫秒
	 * @return
	 */
	public boolean longtap(int duration) {
		if (bean != null) {
			if (Shot != null) {
				Shot.drawRect(Coperation.LONGTAP, bean.getBounds_point_x(), bean.getBounds_point_y(),
						bean.getBounds_width(), bean.getBounds_hight(), Color.decode(Cconfig.GREEN_DEEP));// 操作后界面变化
			}
			oplog.logStep(Coperation.LONGTAP + "(" + Text + ")(" + bean.getBounds_x() + "," + bean.getBounds_y() + "),"
					+ duration + "毫秒");
			ADBUtil.execcmd(udid,
					CAndroidCMD.INPUT_SWIPE.replace("#x1#", bean.getBounds_x() + "")
							.replace("#y1#", bean.getBounds_y() + "").replace("#x2#", (bean.getBounds_x() + 1) + "")
							.replace("#y2#", (bean.getBounds_y() + 1) + "").replace("#duration#", duration + ""));
			return true;
		} else {
			oplog.logInfo(Coperation.LONGTAP + "失败(" + Text + ")");
			return false;
		}
	}

	/**
	 * 点击
	 * 
	 * @return
	 */
	public boolean click() {
		if (bean != null) {
			if (Shot != null) {
				Shot.drawRect(Coperation.CLICK, bean.getBounds_point_x(), bean.getBounds_point_y(),
						bean.getBounds_width(), bean.getBounds_hight());
			}
			oplog.logStep(Coperation.CLICK + "(" + Text + ")(" + bean.getBounds_x() + "," + bean.getBounds_y() + ")");
			ADBUtil.execcmd(udid, CAndroidCMD.INPUT_TAP.replace("#x#", bean.getBounds_x() + "").replace("#y#",
					bean.getBounds_y() + ""));
			return true;
		} else {
			oplog.logInfo(Coperation.CLICK + "失败(" + Text + ")");
			return false;
		}
	}

	/**
	 * 多次点击
	 * 
	 * @param times       点击次数
	 * @param millisecond 点击间隔
	 * @return
	 */
	public boolean click(int times, int millisecond) {
		if (millisecond > 8000)
			millisecond = 8000;
		if (millisecond < 0)
			millisecond = 0;
		if (bean != null) {
			if (Shot != null) {
				Shot.drawRect(Coperation.CLICK, bean.getBounds_point_x(), bean.getBounds_point_y(),
						bean.getBounds_width(), bean.getBounds_hight());
			}
			oplog.logStep(Coperation.CLICK + "(" + Text + ")(" + bean.getBounds_x() + "," + bean.getBounds_y() + "),"
					+ times + "次,间隔" + millisecond + "ms");
			if (times < 1)
				times = 1;
			for (int i = 0; i < times; i++) {
				ADBUtil.execcmd(udid, CAndroidCMD.INPUT_TAP.replace("#x#", bean.getBounds_x() + "").replace("#y#",
						bean.getBounds_y() + ""));
				try {
					Thread.sleep(millisecond);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}
			return true;
		} else {
			oplog.logInfo(Coperation.CLICK + "失败(" + Text + ")," + times + "次");
			return false;
		}
	}

	/**
	 * 拖到元素到指定增量位置<br>
	 * Ps:addx=100,addy=50.意味着将元素从当前位置向X轴移动100单位,向Y轴移动50单位.
	 * 
	 * @param addx
	 * @param addy
	 * @return
	 */
	public boolean dragTo(int addx, int addy) {
		if (bean != null) {
			int startx = bean.getBounds_x();
			int starty = bean.getBounds_y();
			int endx = startx + addx;
			int endy = starty + addy;
			if (Shot != null) {
				Shot.drawArrow(Coperation.SWIPE, startx, starty, endx, endy);
			}
			oplog.logStep(
					Coperation.DRAGTO + ":" + Text + ",从(" + startx + "," + starty + ")到(" + endx + "," + endy + ")");
			ADBUtil.execcmd(udid, CAndroidCMD.INPUT_SWIPE.replace("#x1#", startx + "").replace("#y1#", starty + "")
					.replace("#x2#", endx + "").replace("#y2#", endy + "").replace("#duration#", 500 + ""));
			return true;
		} else {
			oplog.logInfo(Coperation.DRAGTO + "失败(" + Text + ")");
			return false;
		}
	}

	/**
	 * 输入文字,不支持中文
	 * 
	 * @param msm
	 * @return
	 */
	public boolean sendText(String msm) {
		if (bean != null) {
			if (Shot != null) {
				Shot.drawRect(Coperation.CLICK, bean.getBounds_point_x(), bean.getBounds_point_y(),
						bean.getBounds_width(), bean.getBounds_hight());
			}
			oplog.logStep(Coperation.INPUT + ":" + msm + ",(" + Text + ")");
			ADBUtil.execcmd(udid, CAndroidCMD.INPUT_TAP.replace("#x#", bean.getBounds_x() + "").replace("#y#",
					bean.getBounds_y() + ""));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			String[] msms = msm.split("\\s+");
			for (String str : msms) {
				ADBUtil.execcmd(udid, CAndroidCMD.INPUT_TEXT.replace("#msm#", str));
				ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "62"));
			}
			if (Shot != null) {
				Shot.drawRect(Coperation.CLICK, bean.getBounds_point_x(), bean.getBounds_point_y(),
						bean.getBounds_width(), bean.getBounds_hight());
			}
			return true;
		} else {
			oplog.logInfo(Coperation.INPUT + ":" + msm + ",失败(" + Text + ")");
			return false;
		}
	}

	/**
	 * 元素是否存在
	 * 
	 * @return
	 */
	public boolean exist() {
		if (bean != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到元素Text信息
	 * 
	 * @return
	 */
	public String getText() {
		if (bean != null) {
			return bean.getText();
		}
		oplog.logWarn("未获取元素属性Text,返回\"null\"");
		return "null";
	}

	/**
	 * 得到元素bean信息
	 * 
	 * @return
	 */
	public XQAndroidElementBean getBean() {
		return bean;
	}
}
