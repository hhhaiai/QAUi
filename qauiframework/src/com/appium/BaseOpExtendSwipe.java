package com.appium;

import java.util.List;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;

/**
 * 滑动操作扩展封装
 * 
 * @author Then
 *
 */
public abstract class BaseOpExtendSwipe {
	Logger logger = LoggerFactory.getLogger(BaseOpExtendSwipe.class);
	SceneLogUtil oplog;
	Translation translation;
	int device_width;
	int device_hight;
	int scroll_width_blocks = 8;
	int scroll_hight_blocks = 6;
	// 常量
	public final String UP = "上";
	public final String DOWN = "下";
	public final String LEFT = "左";
	public final String RIGHT = "右";
	int SWIPE_TIME;// 默认滑动时间

	public BaseOpExtendSwipe(SceneLogUtil oplog, Translation translation) {
		// TODO Auto-generated constructor stub
		this.oplog = oplog;
		this.translation = translation;
	}

	/**
	 * 设置翻页参数
	 * 
	 * @param scroll_width_blocks>=3 将屏幕宽分割块数,默认8.
	 * @param scroll_hight_blocks>=3 将屏幕高分割块数,默认6.
	 */
	public void setScroll(int scroll_width_blocks, int scroll_hight_blocks) {
		if (scroll_width_blocks >= 3 && scroll_hight_blocks >= 3) {
			oplog.logInfo("设置翻页参数:宽块数=" + scroll_width_blocks + ",高块数=" + scroll_hight_blocks);
			this.scroll_width_blocks = scroll_width_blocks;
			this.scroll_hight_blocks = scroll_hight_blocks;
		} else {
			oplog.logWarn("设置翻页参数失败!块数必须大于等于3");
		}
	}

	/**
	 * 设置默认滑动时长
	 * 
	 * @param time 毫秒
	 */
	public void setSwipeTime(int time) {
		this.SWIPE_TIME = time;
	}

	/**
	 * 向下翻页
	 */
	public abstract void scrolldown();

	/**
	 * 向下翻页
	 * 
	 * @param times 执行次数
	 */
	public abstract void scrolldown(int times);

	/**
	 * 向上翻页
	 */
	public abstract void scrollup();

	/**
	 * 向上翻页
	 * 
	 * @param times 执行次数
	 */
	public abstract void scrollup(int times);

	/**
	 * 向左翻页
	 */
	public abstract void scrollleft();

	/**
	 * 向左翻页
	 * 
	 * @param times 执行次数
	 */
	public abstract void scrollleft(int times);

	/**
	 * 向右翻页
	 */
	public abstract void scrollright();

	/**
	 * 向右翻页
	 * 
	 * @param times 执行次数
	 */
	public abstract void scrollright(int times);

	/**
	 * 向下滚动到某个指定控件<br>
	 * 当目标控件可能被遮挡时慎用<br>
	 * 
	 * @param by        目标控件
	 * @param scrollnum 滑动多少次
	 * @return
	 */
	public abstract <T> T scrolldownTo(By by, int scrollnum);

	/**
	 * 向上滚动到某个指定控件<br>
	 * 当目标控件可能被遮挡时慎用<br>
	 * 
	 * @param by        目标控件
	 * @param scrollnum 滑动多少次
	 * @return
	 */
	public abstract <T> T scrollupTo(By by, int scrollnum);

	/**
	 * 向左滚动到某个指定控件<br>
	 * 当目标控件可能被遮挡时慎用<br>
	 * 
	 * @param by        目标控件
	 * @param scrollnum 滑动多少次
	 * @return
	 */
	public abstract <T> T scrollleftTo(By by, int scrollnum);

	/**
	 * 向右滚动到某个指定控件<br>
	 * 当目标控件可能被遮挡时慎用<br>
	 * 
	 * @param by        目标控件
	 * @param scrollnum 滑动多少次
	 * @return
	 */
	public abstract <T> T scrollrightTo(By by, int scrollnum);

	/**
	 * 滑动滑动条,适用于小距离移动
	 * 
	 * @param by          拖动控件
	 * @param direction   常量UP/DOWN/LEFT/RIGHT
	 * @param startadjust 开始端调整值,除去控件开始端的一段距离
	 * @param addx        移动的距离增量
	 * @return
	 */
	public abstract boolean swipeSeekbar(By by, String direction, int startadjust, int addx);

	/**
	 * 滑动滑动条,适用于小距离移动
	 * 
	 * @param by          拖动控件
	 * @param direction   常量this.UP/DOWN/LEFT/RIGHT
	 * @param startadjust 开始端调整值,除去控件开始端(如方法为UP,则是向上滑动的开始端)的一段距离
	 * @param addx        移动的距离增量
	 * @param times       滑动次数
	 * @return
	 */
	public abstract boolean swipeSeekbar(By by, String direction, int startadjust, int addx, int times);

	/**
	 * 拖动滑动条
	 * 
	 * @param by          拖动控件
	 * @param direction   常量UP/DOWN/LEFT/RIGHT
	 * @param startadjust 开始端调整值,除去控件开始端的一段距离
	 * @param endadjust   结束端调整值,除去控件结束端的一段距离
	 * @return 是否找到控件
	 */
	public abstract boolean dragSeekbar(By by, String direction, int startadjust, int endadjust);

	/**
	 * 拖动滑动条
	 * 
	 * @param by          拖动控件
	 * @param direction   常量UP/DOWN/LEFT/RIGHT
	 * @param startadjust 开始端调整值,除去控件开始端的一段距离
	 * @param endadjust   结束端调整值,除去控件结束端的一段距离
	 * @param times       滑动几次
	 * @return 是否找到控件
	 */
	public abstract boolean dragSeekbar(By by, String direction, int startadjust, int endadjust, int times);

	/**
	 * 拖动控件查找控件滑动范围内的目标元素 如果元素位于非滑动范围(调整值内),则无效,将继续滑动查找符合滑动范围内的元素
	 * 
	 * @param by          拖动控件
	 * @param targetby    目标
	 * @param 滑动多少次
	 * @param direction   常量UP/DOWN/LEFT/RIGHT
	 * @param startadjust 开始端调整值,除去控件开始端的一段距离
	 * @param endadjust   结束端调整值,除去控件结束端的一段距离
	 */
	public abstract <T> T dragSeekbarTo(By by, String direction, int startadjust, int endadjust, By targetby,
			int scrollnum);

	/**
	 * 滑动
	 */
	protected abstract void swipe(int startx, int starty, int endx, int endy, int duration);

	/**
	 * 拖动进度条,获取item的text,直到没有新的item,查找完后会反向拖动回到滑动框最初位置
	 * 如果元素位于非滑动范围(调整值内),则无效,将继续滑动查找符合滑动范围内的元素
	 * 
	 * @param by          滑动框
	 * @param item        子项
	 * @param startadjust 开始端调整值,除去控件开始端的一段距离
	 * @param endadjust   结束端调整值,除去控件结束端的一段距离
	 * @param direction   方向UP/DOWN/LEFT/RIGHT
	 * @return
	 */
	public abstract List<String> dragSeekbarTogetItems(By by, String direction, By item, int startadjust,
			int endadjust);

	/**
	 * 翻页查找指定元素
	 * 
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @param by
	 * @param scrollnum
	 * @param direction
	 * @return
	 */
	protected abstract <T> T scrollTo(int startx, int starty, int endx, int endy, By by, int scrollnum,
			String direction);

	/**
	 * 滑动搜索
	 * 
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @param by
	 * @param scrollnum
	 * @param direction
	 * @param Text
	 * @return
	 */
	protected abstract <T> T scroll2Target(String direction, int startx, int starty, int endx, int endy, By by,
			int scrollnum, String Text, int minlimit, int maxlimit);

	/**
	 * 滑动搜索核心
	 * 
	 * @param direction
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @param by
	 * @param scrollnum
	 * @param Text
	 * @param minlimit
	 * @param maxlimit
	 * @return
	 */
	protected abstract <T> T scroll2TargetCore(String direction, int startx, int starty, int endx, int endy, By by,
			int scrollnum, String Text, int minlimit, int maxlimit);

	/**
	 * 少于UNTILTIME毫秒,则等待直到等到UNTILTIME毫秒
	 * 
	 * @param stime
	 * @param etime
	 */
	protected void waittime(long stime) {
		long dif = TimeUtil.getTime() - stime;
		if (dif < Cconfig.UNTILTIME) {
			try {
				Thread.sleep(Cconfig.UNTILTIME - dif);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}
		}
	}
}
