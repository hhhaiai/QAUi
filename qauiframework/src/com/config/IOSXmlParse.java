package com.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.IOSConfigBean;
import com.viewer.main.MainRun;

public class IOSXmlParse extends ConfigXmlParse {
	Logger logger = LoggerFactory.getLogger(IOSXmlParse.class);
	IOSConfigBean config = new IOSConfigBean();

	public IOSXmlParse() {
		// TODO Auto-generated constructor stub
		setDoc(MainRun.settingsBean.getDatalocation() + "/iosconfig.xml");
		readConfig();
	}

	@Override
	public IOSConfigBean getConfigBean() {
		// TODO Auto-generated method stub
		return config;
	}

	@Override
	public void readConfig() {
		config.setEmail(getMapByXpath("//email", 0));
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
}
