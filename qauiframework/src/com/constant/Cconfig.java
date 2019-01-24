package com.constant;

import java.awt.Color;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public interface Cconfig {
	String ANDROID = "Android";
	String IOS = "iOS";

	int WINDOWS = 0;
	int MAC = 1;
	int LINUX = 2;

	String APPIUM_INPUT_METHOD_NAME = "io.appium.android.ime/.UnicodeIME";
	/**
	 * 正则格式:xxx=xxx;xxx=xxx;
	 */
	String REGEX_FORMAT = "^([^;=]+=[^;=]+;?){1,}$";// 格式:xxx=xxx;xxx=xxx;
	// 截图选项
	String SCREENSHOT_APPIUM = "appium";
	String SCREENSHOT_ADB = "adb";
	String SCREENSHOT_IDEVICESREENSHOT = "idevicescreenshot";
	String SCREENSHOT_DDMLIB = "ddmlib";
	String SCREENSHOT_NONE = "none";
	// 场景选项
	String TASK_TYPE_SCENE = "场景";
	String TASK_TYPE_MONKEY_ANDROID_SYS = "Monkey(Android-SYS)";
	String TASK_TYPE_MONKEY_ANDROID_APPIUM = "Monkey(Andoird-Appium)";
	String TASK_TYPE_MONKEY_IOS_APPIUM = "Monkey(iOS-Appium)";
	String MONKEY_ANDROID_SYS_CRASH_FLAG = "CRASH:";
	// String MONKEY_CRASH_FLAG="";
	String MONKEY_ANDROID_SYS_PACKAGE_NAME = "com.android.commands.monkey";
	String MONKEY_ANDROID_SYS_pct_touch = "60";
	String MONKEY_ANDROID_SYS_pct_motion = "10";
	String MONKEY_ANDROID_SYS_pct_trackball = "10";
	String MONKEY_ANDROID_SYS_pct_nav = "1";
	String MONKEY_ANDROID_SYS_pct_majornav = "8";
	String MONKEY_ANDROID_SYS_pct_syskeys = "1";
	String MONKEY_ANDROID_SYS_pct_appswitch = "5";
	String MONKEY_ANDROID_SYS_pct_anyevent = "5";
	String MONKEY_ANDROID_SYS_analysis_show = "35";
	String MONKEY_ANDROID_SYS_analysis_arow = "40";
	String MONKEY_ANDROID_SYS_analysis_arowword = "80";
	// io.appium.settings.Settings配置
	String APPIUM_IO_SETTINGS_PACKAGENAME = "io.appium.settings";
	String APPIUM_IO_SETTINGS_ACTIVITY = "io.appium.settings.Settings";
	String APPIUM_IO_UIAUTOMATOR2_SERVEER_PACKAGENAME = "io.appium.uiautomator2.server";
	String APPIUM_IO_UIAUTOMATOR2_SERVEER_TEST_PACKAGENAME = "io.appium.uiautomator2.server.test";
	String APPIUM_IO_UNLOCK_PACKAGENAME = "io.appium.unlock";
	String APPIUM_IO_UNLOCK_ACTIVITY = "io.appium.unlock.Unlock";
	String APPIUM_IO_ANDROID_IME_PACKAGENAME = "io.appium.android.ime";
	String APPIUM_IO_ANDROID_IME_ACTIVITY = "io.appium.android.ime.UnicodeIME";
	boolean APPIUM_REPACLE_DRIVER = true;
	// 图片编辑字体大小
	int SMALLFONT = 1;
	int MEDIUMFONT = 2;
	int LARGEFONT = 3;

	int UNTILTIME = 3000;// 滑动后等待不少于X毫秒
	String CUSTOMER_FOLDER = "自定义捕获";

	String APPLOG = "APPLOG";
	String SYSLOG = "SYSLOG";
	String CUSLOG = "CUSLOG";

	String BLUE = "#00EEEE";
	String GREEN = "#00EE76";
	String GREEN_DEEP = "#228B22";
	String RED = "#FF3030";
	String YELLOW = "#FFC125";
	Highlighter.HighlightPainter highlightPainter_RED = new DefaultHighlighter.DefaultHighlightPainter(
			Color.decode(Cconfig.RED));
	Highlighter.HighlightPainter highlightPainter_GREEN = new DefaultHighlighter.DefaultHighlightPainter(
			Color.decode(Cconfig.GREEN));
	Highlighter.HighlightPainter highlightPainter_BLUE = new DefaultHighlighter.DefaultHighlightPainter(
			Color.decode(Cconfig.BLUE));
	Highlighter.HighlightPainter highlightPainter_YELLO = new DefaultHighlighter.DefaultHighlightPainter(
			Color.decode(Cconfig.YELLOW));
}
