package com.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.viewer.main.MainRun;

public abstract class ConfigXmlParse extends XmlParse {
	Logger logger = LoggerFactory.getLogger(ConfigXmlParse.class);

	/**
	 * 读取配置文件
	 */
	public abstract void readConfig();

	/**
	 * 得到configbean
	 * 
	 * @return
	 */
	public abstract <T> T getConfigBean();

	/**
	 * 得到场景列表<br>
	 * String name 场景名<br>
	 * String screenshot 截图方式<br>
	 * Map capability appium参数<br>
	 * 
	 * @return
	 */
	public Map<String, Object> getSceneMap() {
		Map<String, Object> itemMap = new LinkedHashMap<>();
		List<Node> listnode = getDoc().selectNodes("//scene");
		if (null != listnode) {
			for (Node node : listnode) {
				if (node instanceof Element) {
					for (Element item : ((Element) node).elements()) {// item
						String itemname = null;
						Map<String, Object> map = new HashMap<>();
						// if(item.getName().toLowerCase().contains("deprecated"))continue;//过滤过时的用例
						if (!item.getName().toLowerCase().equals("item"))
							continue;
						for (Element e : item.elements()) {
							if (e.getName().equals(Cparams.capability)) {
								Map<String, String> capabilityMap = new LinkedHashMap<>();
								e.elements().forEach(n -> capabilityMap.put(n.getName(), n.getTextTrim()));// capability
								// 默认值设置
								if (getConfigpath().toLowerCase().contains("android")) {
									if (capabilityMap.get("automationName") == null)
										capabilityMap.put("automationName", "uiautomator2");
									if (capabilityMap.get("platformName") == null)
										capabilityMap.put("platformName", "Android");
									if (capabilityMap.get("unicodeKeyboard") == null)
										capabilityMap.put("unicodeKeyboard", "true");// 设置默认键盘为appium的键盘
									if (capabilityMap.get("resetKeyboard") == null)
										capabilityMap.put("resetKeyboard", "true");// 退出后还原键盘
									if (capabilityMap.get("noSign") == null)
										capabilityMap.put("noSign", "true");// 安装时不对apk进行重签名，设置很有必要，否则有的apk在重签名之后无法正常使用
									if (capabilityMap.get("autoGrantPermissions") == null)
										capabilityMap.put("autoGrantPermissions", "true");
								} else {
									if (capabilityMap.get("automationName") == null)
										capabilityMap.put("automationName", "XCUITest");
									if (capabilityMap.get("platformName") == null)
										capabilityMap.put("platformName", "iOS");
									if (capabilityMap.get("wdaLocalPort") == null)
										capabilityMap.put("wdaLocalPort", "8100");
									// if (capabilityMap.get("shutdownOtherSimulators") == null)
									// capabilityMap.put("shutdownOtherSimulators", "true");
								}
								if (capabilityMap.get("newCommandTimeout") == null
										|| Integer.parseInt(capabilityMap.get("newCommandTimeout")) <= 60)// 没有新命令，appium退出时间,秒
									capabilityMap.put("newCommandTimeout", "600");
								if (capabilityMap.get(Cparams.udid) == null)
									capabilityMap.put(Cparams.udid, "");
								if (capabilityMap.get("platformVersion") == null)
									capabilityMap.put("platformVersion", "");
								if (capabilityMap.get("deviceName") == null)
									capabilityMap.put("deviceName", "");
								if (capabilityMap.get("app") == null)
									capabilityMap.put("app", "");
								if (capabilityMap.get("noReset") == null)
									capabilityMap.put("noReset", "false");// 不需要重置应用
								// if (capabilityMap.get("autoLaunch") == null)
								// capabilityMap.put("autoLaunch", "true");// 是否自动启动,非标准属性
								map.put(Cparams.capability, capabilityMap);
								continue;
							} else if (e.getName().equals("name")) {
								itemname = e.getTextTrim();
								map.put("name", itemname);
								continue;
							} else {
								map.put(e.getName(), e.getTextTrim());
								continue;
							}
						}
						// 默认值设置
						if (map.get(Cparams.app) == null)
							map.put(Cparams.app, "");
						if (map.get(Cparams.userlogcatch) == null)
							map.put(Cparams.userlogcatch, "");
						if (map.get(Cparams.params) == null)
							map.put(Cparams.params, "");
						if (map.get(Cparams.desc) == null)
							map.put(Cparams.desc, "");
						if (map.get(Cparams.appid) == null)
							map.put(Cparams.appid, "");
						if (map.get(Cparams.syscrash) == null)
							map.put(Cparams.syscrash, "false");
						if (map.get(Cparams.appcrash) == null)
							map.put(Cparams.appcrash, "false");
						// 任务标志
						map.put(Cparams.type, Cconfig.TASK_TYPE_SCENE);
						// 是否初始化过appium driver
						map.put(Cparams.initdriverdone, "false");

						Map<String, String> emailMap;
						Map<String, String> wechatMap;
						if (getConfigpath().toLowerCase().contains("android")) {
							if (map.get(Cparams.screenshot) == null)
								map.put(Cparams.screenshot, Cconfig.SCREENSHOT_ADB);
							if (map.get(Cparams.initdriver) == null)
								map.put(Cparams.initdriver, "true");
							if (map.get(Cparams.setdevice) == null)
								map.put(Cparams.setdevice, "true");
							emailMap = MainRun.androidConfigBean.getEmail();
							wechatMap = MainRun.androidConfigBean.getWechat();
						} else {
							if (map.get(Cparams.screenshot) == null)
								map.put(Cparams.screenshot, Cconfig.SCREENSHOT_IDEVICESREENSHOT);
							if (map.get(Cparams.idevicesyslogtag) == null)
								map.put(Cparams.idevicesyslogtag, "");
							if (map.get(Cparams.initdriver) == null)
								map.put(Cparams.initdriver, "true");
							emailMap = MainRun.iosConfigBean.getEmail();
							wechatMap = MainRun.iosConfigBean.getWechat();
						}
						// email
						map.put(Cparams.email_send, emailMap.get(Cparams.send));
						map.put(Cparams.email_to, emailMap.get(Cparams.to));
						map.put(Cparams.email_cc, emailMap.get(Cparams.cc));
						map.put(Cparams.email_smtp, emailMap.get(Cparams.smtp));
						map.put(Cparams.email_account, emailMap.get(Cparams.account));
						map.put(Cparams.email_password, emailMap.get(Cparams.password));
						// wechat
						map.put(Cparams.wechat_people_list, wechatMap.get(Cparams.people_list));
						map.put(Cparams.wechat_send, wechatMap.get(Cparams.send));
						// 正确性检查
						if (map.get(Cparams.name) != null && map.get(Cparams.capability) != null && itemMap != null)
							itemMap.put(itemname, map);
					}
				}
			}
		}
		return itemMap;
	}
}
