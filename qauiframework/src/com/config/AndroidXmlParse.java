package com.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.AndroidConfigBean;
import com.viewer.main.MainRun;

public class AndroidXmlParse extends ConfigXmlParse {
	Logger logger = LoggerFactory.getLogger(AndroidXmlParse.class);
	AndroidConfigBean config = new AndroidConfigBean();

	public AndroidXmlParse() {
		// TODO Auto-generated constructor stub
		setDoc(MainRun.settingsBean.getDatalocation() + "/androidconfig.xml");
		readConfig();
	}

	@Override
	public AndroidConfigBean getConfigBean() {
		// TODO Auto-generated method stub
		return config;
	}

	@Override
	public void readConfig() {
		config.setEmail(getMapByXpath("//email", 0));
		config.setMonkey_sys(getMapByXpath("//monkey_sys", 0));
		config.setWechat(getMapByXpath("//wechat", 0));
	}

	/**
	 * 保存wechat设置
	 * 
	 * @param map
	 */
	public void writeWechatMap(Map<String, String> map) {
		config.setWechat(changeMapByXPath("//wechat", 0, map));
	}

	/**
	 * 保存email设置
	 * 
	 * @param map
	 */
	public void writeEmailMap(Map<String, String> map) {
		config.setEmail(changeMapByXPath("//email", 0, map));
	}

	/**
	 * 保存monkey设置
	 * 
	 * @param map
	 */
	public void writeMonkeyMap(Map<String, String> map) {
		config.setMonkey_sys(changeMapByXPath("//monkey_sys", 0, map));
	}
}
// if(null!=doc.selectNodes("//identify-default")){
// sidentify = doc.selectNodes("//identify-default").get(0).getText();
// if (null != sidentify && !sidentify.isEmpty())
// identifyDefault = Integer.parseInt(sidentify.trim());
// config.setidentifyDefault(identifyDefault);
// }
//
// if(null!=doc.selectNodes("//rever")){
// rever = doc.selectNodes("//rever").get(0).getText();
// if (null != rever && !rever.isEmpty())
// config.setRever(rever);
// }

/* List存入方法 */
// if(null!=doc.selectNodes("//clcik")){
// List<Node> t =doc.selectNodes("//click");
// clicklist = getList(t);
// config.setClickList(clicklist);
//
// }