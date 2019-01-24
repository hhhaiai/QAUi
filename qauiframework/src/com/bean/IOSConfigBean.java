package com.bean;

import java.util.Map;

public class IOSConfigBean {
	private Map<String, String> email;
	private Map<String, String> wechat;
	private Map<String, Object> scene;

	public Map<String, String> getEmail() {
		return email;
	}

	public void setEmail(Map<String, String> email) {
		this.email = email;
	}

	public Map<String, String> getWechat() {
		return wechat;
	}

	public void setWechat(Map<String, String> wechat) {
		this.wechat = wechat;
	}

	public Map<String, Object> getScene() {
		return scene;
	}

	public void setScene(Map<String, Object> scene) {
		this.scene = scene;
	}

}
