package com.xq;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appium.Translation;
import com.constant.CAndroidCMD;
import com.constant.Coperation;
import com.helper.ADBUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidShot;

import io.appium.java_client.android.nativekey.AndroidKey;

public class XQAndroidOp {
	Logger logger = LoggerFactory.getLogger(XQAndroidOp.class);
	String page = "";
	String udid;
	AndroidShot Shot;
	SceneLogUtil oplog;
	Translation translation;
	XQAndroidDriver driver;

	public XQAndroidOp(XQAndroidDriver driver) {
		// TODO Auto-generated constructor stub
		this.driver = driver;
		this.udid = driver.getUdid();
		this.oplog = driver.getOplog();
		this.Shot = driver.getShot();
		this.translation = driver.getTranslation();
	}

	/**
	 * 设置xml页面资源
	 * 
	 * @param page
	 */
	public XQAndroidOp setPage(String page) {
		this.page = page;
		return this;
	}

	/**
	 * 通过uiautomator dump获取页面资源,(与Appium无法同时使用)
	 * 
	 * @return
	 */
	public boolean getPage() {
		page = driver.getPageByADB();
		return !page.equals("");
	}

	/**
	 * 系统休眠,不能超过10秒.
	 * 
	 * @param millisecond 毫秒
	 */
	public XQAndroidOp sleep(long millisecond) {
		try {
			if (millisecond > 10 * 1000)
				millisecond = 10 * 60 * 1000;
			oplog.logInfo("线程休眠" + millisecond + "毫秒");
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
		return this;
	}

	/**
	 * 查找元素
	 * 
	 * @param text  自动区分xpath/text/id/class及content-desc需要加上#
	 * @param regex 正则表达式匹配的text
	 * @return
	 */
	public XQAndroidElement findElement(String text, String regex) {
		List<XQAndroidElement> list = driver.findElementBy(driver.getRootByPage(page), text, regex);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return new XQAndroidElement(driver, null);
		}
	}

	/**
	 * 查找元素
	 * 
	 * @param text 自动区分xpath/text/id/class及content-desc需要加上#
	 * @return
	 */
	public XQAndroidElement findElement(String text) {
		List<XQAndroidElement> list = driver.findElementBy(driver.getRootByPage(page), text, null);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return new XQAndroidElement(driver, null);
		}
	}

	/**
	 * 查找元素组
	 * 
	 * @param text 自动区分xpath/text/id/class及content-desc需要加上#
	 * @return
	 */
	public List<XQAndroidElement> findElements(String text) {
		return driver.findElementBy(driver.getRootByPage(page), text, null);
	}

	/**
	 * 查找元素组
	 * 
	 * @param text  自动区分xpath/text/id/class及content-desc需要加上#
	 * @param regex 正则表达式匹配的text
	 * @return
	 */
	public List<XQAndroidElement> findElements(String text, String regex) {
		return driver.findElementBy(driver.getRootByPage(page), text, regex);
	}

	/**
	 * 按下返回按钮
	 */
	public void pressBACK() {
		Shot.drawText("返回", "按下BACK按钮");
		oplog.logStep("按下BACK按钮");
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "4"));
	}

	/**
	 * 连续按下times次返回按钮
	 * 
	 * @param times
	 * @param millisecond 每次按下间隔时间
	 */
	public void pressBACK(int times, int millisecond) {
		Shot.drawText("返回", "连续按下" + times + "次BACK按钮");
		oplog.logStep("连续按下" + times + "次BACK按钮");
		if (millisecond > 8000)
			millisecond = 8000;
		if (millisecond < 0)
			millisecond = 0;
		if (times < 1)
			times = 1;
		for (int i = 0; i < times; i++) {
			ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "4"));
			try {
				Thread.sleep(millisecond);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	/**
	 * 按下HOME按钮
	 */
	public void pressHOME() {
		Shot.drawText("Home", "按下HOME按钮");
		oplog.logStep("按下HOME按钮");
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "3"));
	}

	/**
	 * 按下音量上按钮
	 */
	public void pressVOLUME_UP() {
		Shot.drawText("VOLUME_UP", "按下VOLUME_UP按钮");
		oplog.logStep("按下VOLUME_UP按钮");
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "24"));
	}

	/**
	 * 按下音量下按钮
	 */
	public void pressVOLUME_DOWN() {
		Shot.drawText("VOLUME_DOWN", "按下VOLUME_DOWN按钮");
		oplog.logStep("按下VOLUME_DOWN按钮");
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "25"));
	}

	/**
	 * 按下电源键按钮
	 */
	public void pressPOWER() {
		Shot.drawText("POWER", "按下POWER按钮");
		oplog.logStep("按下POWER按钮");
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", "26"));
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
		Shot.drawText(Coperation.KEYCODE, "按下键值=" + KeyCode.getCode() + "按钮");
		oplog.logStep("按下键值=" + KeyCode.getCode() + "按钮");
		ADBUtil.execcmd(udid, CAndroidCMD.INPUT_KEYEVENT.replace("#value#", KeyCode.getCode() + ""));
	}
	// /**
	// * 在Element中根据属性名和值查找元素
	// *
	// * @param node
	// * @param type
	// * 属性名
	// * @param value
	// * 属性值
	// * @return
	// */
	// private Element findElement(Element node, String type, String value) {
	// Element element = null;
	// for (Iterator<Element> iter = node.elementIterator(); iter.hasNext();) {
	// element = iter.next();
	// Attribute name = element.attribute(type);
	// if (name != null) {
	// if (name.getValue() != null && value.equals(name.getValue())) {
	// return element;
	// } else {
	// element = findElement(element, type, value);
	// }
	// }
	// }
	// return element;
	// }
	//
	//
	// /**
	// * 查找元素,原理:根据Xpath查找
	// *
	// * @param classname
	// * @param resource_id
	// * @return
	// */
	// @Deprecated
	// private List<XQAndroidElement> analysisPageByXpath(String nodename, String
	// resource_id) {
	// List<XQAndroidElement> list = new ArrayList<>();
	// InputStream in = new ByteArrayInputStream(page.getBytes());
	// try {
	// Document doc = new SAXReader().read(in);
	// List<Node> listnode = doc.selectNodes("//" + nodename + "[@resource-id='" +
	// resource_id + "']");
	// if (listnode != null) {
	// for (Node node : listnode) {
	// if (node instanceof Element) {
	// XQAndroidElement androidDynamicElement = new XQAndroidElement(udid,
	// parseXQBean((Element) node),
	// oplog, Shot, translation != null ? translation.getName(resource_id) :
	// resource_id,
	// translation);
	// list.add(androidDynamicElement);
	// }
	// }
	// }
	// } catch (DocumentException e) {
	// // TODO Auto-generated catch block
	// logger.error("Exception", e);
	// } finally {
	// if (in != null) {
	// try {
	// in.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// logger.error("Exception", e);
	// }
	// }
	// }
	// return list;
	// }
	//
	// /**
	// * 根据resource id 搜索元素组
	// *
	// * @param resource_id
	// * @return
	// */
	// @Deprecated
	// private List<XQAndroidElement> analysisPageByRegex(String resource_id) {
	// // <android.widget.ImageView NAF="true" index="0" text=""
	// // class="android.widget.ImageView" package="vStudio.Android.Camera"
	// // content-desc="" checkable="false" checked="false" clickable="true"
	// // enabled="true" focusable="false"
	// // focused="false" scrollable="false" long-clickable="false" password="false"
	// // selected="false"
	// // bounds="[48,1749][153,1854]"
	// // resource-id="vStudio.Android.Camera:id/info_bottom_comment_img"
	// // instance="4"/>
	// List<XQAndroidElement> list = new ArrayList<>();
	// Matcher matcher =
	// Pattern.compile("<android.*?text=\"(.*?)\".*?enabled=\"(.*?)\""
	// + ".*?bounds=\"\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]\"" +
	// ".*?resource-id=\"" + resource_id + "\".*?>")
	// .matcher(page);
	// while (matcher.find()) {
	// Map<String, String> infoMap = new HashMap<>();
	// infoMap.put("text", matcher.group(1));
	// infoMap.put("enabled", matcher.group(2));
	// infoMap.put("x", matcher.group(3));
	// infoMap.put("y", matcher.group(4));
	// infoMap.put("width", matcher.group(5));
	// infoMap.put("hight", matcher.group(6));
	// XQAndroidElement androidDynamicElement = new XQAndroidElement(udid,
	// parseXQBean(null), oplog, Shot,
	// translation != null ? translation.getName(resource_id) : resource_id,
	// translation);// !!!!
	// list.add(androidDynamicElement);
	// }
	// return list;
	// }

}
