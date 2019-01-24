package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneXmlParse extends ConfigXmlParse {
	Logger logger = LoggerFactory.getLogger(SceneXmlParse.class);

	public SceneXmlParse(String path) {
		// TODO Auto-generated constructor stub
		setDoc(path);
		readConfig();
	}

	@Override
	public void readConfig() {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T getConfigBean() {
		// TODO Auto-generated method stub
		return null;
	}

}
