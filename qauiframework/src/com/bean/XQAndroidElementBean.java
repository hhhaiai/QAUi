package com.bean;

import org.dom4j.Element;

public class XQAndroidElementBean {
	String text;
	String resource_id;
	String classname;
	boolean enabled;
	String content_desc;
	int bounds_point_x = 0;
	int bounds_point_y = 0;
	int bounds_x = 0;
	int bounds_y = 0;
	int bounds_width = 0;
	int bounds_hight = 0;
	boolean NAF;
	int index;
	String packagename;
	boolean checkable;
	boolean checked;
	boolean clickable;
	boolean focusable;
	boolean focused;
	boolean scrollable;
	boolean password;
	boolean selected;
	int instance;
	boolean long_clickable;
	Element element;
	String nickname;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getContent_desc() {
		return content_desc;
	}

	public void setContent_desc(String content_desc) {
		this.content_desc = content_desc;
	}

	public boolean isNAF() {
		return NAF;
	}

	public void setNAF(boolean nAF) {
		NAF = nAF;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public boolean isCheckable() {
		return checkable;
	}

	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public boolean isFocusable() {
		return focusable;
	}

	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

	public boolean isLong_clickable() {
		return long_clickable;
	}

	public void setLong_clickable(boolean long_clickable) {
		this.long_clickable = long_clickable;
	}

	public int getBounds_x() {
		return bounds_x;
	}

	public void setBounds_x(int bounds_x) {
		this.bounds_x = bounds_x;
	}

	public int getBounds_y() {
		return bounds_y;
	}

	public void setBounds_y(int bounds_y) {
		this.bounds_y = bounds_y;
	}

	public int getBounds_width() {
		return bounds_width;
	}

	public void setBounds_width(int bounds_width) {
		this.bounds_width = bounds_width;
	}

	public int getBounds_hight() {
		return bounds_hight;
	}

	public void setBounds_hight(int bounds_hight) {
		this.bounds_hight = bounds_hight;
	}

	public int getBounds_point_x() {
		return bounds_point_x;
	}

	public void setBounds_point_x(int bounds_point_x) {
		this.bounds_point_x = bounds_point_x;
	}

	public int getBounds_point_y() {
		return bounds_point_y;
	}

	public void setBounds_point_y(int bounds_point_y) {
		this.bounds_point_y = bounds_point_y;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
