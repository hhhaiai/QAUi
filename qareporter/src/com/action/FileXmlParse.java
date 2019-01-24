package com.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.NoteBean;

public class FileXmlParse extends BaseXmlParse {
	Logger logger = LoggerFactory.getLogger(FileXmlParse.class);
	NoteBean noteBean = new NoteBean();

	public FileXmlParse(File file) {
		// TODO Auto-generated constructor stub
		setDoc(file.getAbsolutePath());
		noteBean.setDone(getStringByXpath("//outline/done", 0));
		noteBean.setFolder(getStringByXpath("//outline/folder", 0));
		noteBean.setTitle(getStringByXpath("//outline/title", 0));
		noteBean.setCreatetime(getStringByXpath("//outline/createtime", 0));
		noteBean.setSummary(getStringByXpath("//outline/summary", 0));
		noteBean.setItems_scene(getListMapByXpath("//outline/items_scene", -1, "item"));
		noteBean.setItems_monkey_android_sys(getListMapByXpath("//outline/items_monkey_android_sys", -1, "item"));
	}

	public NoteBean getNoteBean() {
		return noteBean;
	}
}
