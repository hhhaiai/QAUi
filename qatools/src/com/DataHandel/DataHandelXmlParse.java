package com.DataHandel;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.XmlParse;
import com.Viewer.MainRun;

public class DataHandelXmlParse extends XmlParse {
	Logger logger = LoggerFactory.getLogger(DataHandelXmlParse.class);
	List<Map<String, String>> itemMapList;

	public DataHandelXmlParse() {
		// TODO Auto-generated constructor stub
		setDoc(MainRun.datalocation + "/Datahandel.xml");
		itemMapList = getListMapByXpath("/root/settings", -1, "item");
	}

	public List<Map<String, String>> refreshItemMapList() {
		itemMapList = getListMapByXpath("/root/settings", -1, "item");
		return itemMapList;
	}

	public List<Map<String, String>> getItemMapList() {
		return itemMapList;
	}

	public List<Map<String, String>> copyItemMapList() {
		return getListMapByXpath("/root/settings", -1, "item");
	}

}
