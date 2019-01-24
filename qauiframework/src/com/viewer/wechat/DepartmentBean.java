package com.viewer.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class DepartmentBean {
	Logger logger = LoggerFactory.getLogger(DepartmentBean.class);

	int id;
	String name;
	int parentid;
	int order;
	TitledPane titledPane;
	VBox vBox;

	public DepartmentBean() {
		// TODO Auto-generated constructor stub
		titledPane = new TitledPane();
		vBox = new VBox();
		vBox.setSpacing(5);
		titledPane.setContent(vBox);
		titledPane.setExpanded(false);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		titledPane.setText(name);
	}

	public int getParentid() {
		return parentid;
	}

	public void setParentid(int parentid) {
		this.parentid = parentid;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public TitledPane getTitledPane() {
		return titledPane;
	}

	public void setTitledPane(TitledPane titledPane) {
		this.titledPane = titledPane;
	}

	public VBox getvBox() {
		return vBox;
	}

	public void setvBox(VBox vBox) {
		this.vBox = vBox;
	}

}
