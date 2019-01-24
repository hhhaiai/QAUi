package com.appium;

import java.util.List;

import org.openqa.selenium.By;

public interface Operation {
	// ****************************************************
	// ***************************监听器******************
	// ****************************************************
	/**
	 * 注册Ui异常监听器
	 */
	void registerUiWatcher(String name, UiWatcher uiWatcher);

	/**
	 * 主动执行Ui异常监听器
	 * 
	 * @return
	 */
	boolean runUiWatcher();

	/**
	 * 删除Ui异常监听器
	 */
	void delUiWatcher();

	/**
	 * 取消下一次查找的异常监听器及警告日志
	 * 当第一次查找xxx元素没有找到时,将运行registerUiWatcher()中的步骤进行处理异常,处理完后会再进行一次查找xxx
	 * 
	 * @return
	 */
	<T> T cancelUiWathcher();

	/**
	 * 获取页面元素资源
	 * 
	 * @return
	 */
	String getPageSource();

	/**
	 * 取消下一次的自适应坐标
	 * 
	 * @return
	 */
	<T> T cancelAutoMultiple();

	// ****************************************************
	// ***************************常用操作******************
	// ****************************************************
	/**
	 * 休眠 单位秒(不精确,近似时间)
	 * 
	 * @param second
	 */
	void sleep(int second);

	/**
	 * 设置当前脚本运行的设备高与宽;<br>
	 * 在不同尺寸的设备运行时,会自动调整脚本中的坐标点;<br>
	 * iOS为point,Android为像素点;<br>
	 * 
	 * @param width 设备宽
	 * @param hight 设备高
	 */
	void setScreenSize(int width, int hight);

	/**
	 * 点击屏幕中心
	 * 
	 * @param time 点击次数
	 */
	void tapCenter(int time);

	/**
	 * 坐标点击
	 */
	void tap(int x, int y);

	/**
	 * 坐标点击,PS: x,y
	 * 
	 * @param text (x,y)
	 * @return
	 */
	void tap(String text);

	/**
	 * 点击坐标点times次
	 * 
	 * @param x
	 * @param y
	 * @param times       次数
	 * @param millisecond 每次点击间隔时间
	 * @return
	 */
	void tap(int x, int y, int times, long millisecond);

	/**
	 * 点击坐标点times次,PS: x,y
	 * 
	 * @param text        (x,y)
	 * @param times       次数
	 * @param millisecond 每次点击间隔时间
	 * @return
	 */
	void tap(String text, int times, long millisecond);

	/**
	 * 轻触坐标点
	 * 
	 * @param x
	 * @param y
	 * @param duration 触摸时间(毫秒)
	 * @return
	 */
	void longtap(int x, int y, int duration);

	/**
	 * 轻触坐标点,PS: x,y
	 * 
	 * @param text     (x,y)
	 * @param duration
	 * @return
	 */
	void longtap(String text, int duration);

	/**
	 * 放大,从(x,y)斜滑长度length
	 * 
	 * @param x
	 * @param y
	 * @param length   放大滑动长度,斜滑
	 * @param duration
	 */
	@Deprecated
	void zoom(int x, int y, int length, int duration);

	/**
	 * 缩小,从(x1,y1)和(x2,y2)向中点滑动
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param duration
	 */
	@Deprecated
	void pinch(int x1, int y1, int x2, int y2, int duration);

	/**
	 * 滑动
	 * 
	 * @param startx   初始坐标
	 * @param starty
	 * @param endx     结束坐标
	 * @param endy
	 * @param duration 滑动时间(毫秒)
	 * @return
	 */
	void swipe(int startx, int starty, int endx, int endy, int duration);

	/**
	 * 启动应用
	 */
	boolean launchApp();

	/**
	 * 关闭应用
	 */
	boolean closeApp();

	/**
	 * 处理单个或连续权限提示(当前按钮含有关键字时点击) iOS当含有{ "以后", "稍后" }时进行点击,如果不含则默认点击弹出框右边按钮
	 * 
	 * @return 是否处理过权限提示
	 */
	boolean HandlePermission();

	// ****************************************************
	// ***************************查找元素******************
	// ****************************************************
	/**
	 * 查找元素By<br>
	 * Android自动区分xpath/text/id/classname(以#开头则以accessibilityid查找);<br>
	 * iOS自动区分name/xpath/NsPredicate;<br>
	 * NsPredicate:BETWEEN,CONTAINS,BEGINSWITH,ENDSWITH,LIKE,MATCHES,AND,OR<br>
	 * 
	 * @param text
	 * @return
	 */
	By MobileBy(String text);

	/**
	 * 查找元素<br>
	 * Android自动区分xpath/text/id/classname(以#开头则以accessibilityid查找);<br>
	 * iOS自动区分name/xpath/NsPredicate;<br>
	 * NsPredicate:BETWEEN,CONTAINS,BEGINSWITH,ENDSWITH,LIKE,MATCHES,AND,OR<br>
	 * 
	 * @param text
	 * @return
	 */
	BaseElement findElement(String text);

	/**
	 * 查找元素,Android自动区分xpath/text/id/classname(以#开头则以accessibilityid查找);<br>
	 * iOS自动区分name/xpath/NsPredicate;<br>
	 * NsPredicate:BETWEEN,CONTAINS,BEGINSWITH,ENDSWITH,LIKE,MATCHES,AND,OR<br>
	 * 
	 * @param text
	 * @param time 等待时间,单位秒
	 * @return
	 */
	BaseElement findElement(String text, int time);

	/**
	 * 查找元素
	 * 
	 * @param by
	 * @return
	 */
	BaseElement findElement(By by);

	/**
	 * 查找元素
	 * 
	 * @param by
	 * @param time 等待时间,单位秒
	 * @return
	 */
	BaseElement findElement(By by, int time);

	/**
	 * 查找元素组
	 * 
	 * @param by
	 * @return
	 */
	List<?> findElements(By by);

	/**
	 * 根据元素组中的name组成新的list
	 * 
	 * @param by
	 * @return
	 */
	List<String> findNamesbyElements(By by);

	/**
	 * 查找元素组中的下标为index的元素
	 * 
	 * @param by
	 * @param index
	 * @return
	 */
	BaseElement findElementByElements(By by, int index);

	/**
	 * 查找?元素组中的下标为index的元素;<br>
	 * android自动区分xpath/text/id/classname(以#开头则以accessibilityid查找);<br>
	 * iOS自动区分name/xpath/NsPredicate;<br>
	 * NsPredicate:BETWEEN,CONTAINS,BEGINSWITH,ENDSWITH,LIKE,MATCHES,AND<br>
	 * 
	 * @param Text PS:"id,index"
	 * @return
	 */
	BaseElement findElementByElements(String textandindex);

	/**
	 * 按照先后顺序查找元素,找到一个则返回
	 * 
	 * @param second 每个元素寻找时间,单位秒
	 * @param bys
	 * @return
	 */
	BaseElement findOneOfElements(int second, By... bys);
	// /**
	// * 根据AccessibilityId查找元素<br>
	// * android:desc; <br>
	// * ios:name;<br>
	// * @param text
	// * @return
	// */
	// <T>T findElementByAccessibilityId(String accessibilityId);
	// /**
	// * 根据AccessibilityId查找元素<br>
	// * android:desc; <br>
	// * ios:name;<br>
	// * @param text
	// * @param time 等待时间,单位秒
	// * @return
	// */
	// <T>T findElementByAccessibilityId(String accessibilityId,int time);
	// /**
	// * 根据类名查找元素
	// * @param text
	// * @return
	// */
	// <T>T findElementByClassName(String className);
	// /**
	// * 根据Xpath查找元素
	// * @param text
	// * @return
	// */
	// <T>T findElementByXpath(String xpathExpression);
	// /**
	// * 根据类名查找元素
	// * @param text
	// * @param time 等待时间,单位秒
	// * @return
	// */
	// <T>T findElementByClassName(String className,int time);
	// /**
	// * 根据Xpath查找元素
	// * @param text
	// * @param time 等待时间,单位秒
	// * @return
	// */
	// <T>T findElementByXpath(String xpathExpression,int time);
}
