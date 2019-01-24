package com.viewer.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

public class PeopleBean {
	Logger logger = LoggerFactory.getLogger(PeopleBean.class);

	String name;
	int department;
	String userid;
	HBox hBox;
	CheckBox checkBox;

	public PeopleBean() {
		// TODO Auto-generated constructor stub
		hBox = new HBox();
		checkBox = new CheckBox();
		hBox.getChildren().addAll(checkBox);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		checkBox.setText(name);
	}

	public int getDepartment() {
		return department;
	}

	public void setDepartment(int department) {
		this.department = department;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public HBox gethBox() {
		return hBox;
	}

	public void sethBox(HBox hBox) {
		this.hBox = hBox;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

}
