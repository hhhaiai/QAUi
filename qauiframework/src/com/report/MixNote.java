package com.report;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.config.XmlParse;
import com.helper.HelperUtil;
import com.helper.TimeUtil;

public class MixNote extends XmlParse {
	Logger logger = LoggerFactory.getLogger(MixNote.class);
	File reportFile;
	public final String ITEMS_SCENE = "items_scene";
	public final String ITEMS_MONKEY_ANDROID_SYS = "items_monkey_android_sys";

	public MixNote() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 创建XML文件
	 * 
	 * @param reportFolder
	 * @return
	 */
	public File create(File reportFolder) {
		reportFile = new File(reportFolder.getAbsolutePath() + "/note.xml");
		try {
			reportFile.createNewFile();
			HelperUtil.file_write_line(reportFile.getAbsolutePath(),
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n<outline></outline>\n<failcase></failcase>\n</root>",
					true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		setDoc(reportFile.getAbsolutePath());
		return reportFile;
	}

	/**
	 * 初始化XML
	 * 
	 * @param title
	 *            报告标题
	 */
	public void initXML(String title) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("done", "false");
		map.put("folder", reportFile.getParentFile().getName());
		map.put("title", title);
		map.put("summary", "正在测试中...");
		map.put("createtime", TimeUtil.getTime4Log());
		map.put(ITEMS_SCENE, "");
		map.put(ITEMS_MONKEY_ANDROID_SYS, "");
		addElementByMap("/root/outline", 0, map);
	}

	/**
	 * 添加子项
	 * 
	 * @param type
	 *            任务类型
	 * @param success
	 *            是否通过
	 * @param folder
	 *            文件夹名称
	 * @param summary
	 *            结果概要
	 */
	public void addOutlineItems(String type, boolean success, String folder, String summary) {
		Node node = getDoc().selectSingleNode("/root/outline/" + type);
		if (node instanceof Element) {
			Element ele = (Element) node;
			Element ele_item = DocumentHelper.createElement("item");
			Element ele_item_success = ele_item.addElement("success");
			ele_item_success.addText(success ? "true" : "false");
			Element ele_item_no = ele_item.addElement("no");
			ele_item_no.addText(folder.indexOf("-") > -1 ? folder.substring(0, folder.indexOf("-")) : "0");
			Element ele_item_folder = ele_item.addElement("folder");
			ele_item_folder.addText(folder);
			Element ele_item_summary = ele_item.addElement("summary");
			ele_item_summary.addText(summary);
			ele.add(ele_item);
		}
		writeDoc();
	}

	/**
	 * 添加失败用例
	 * 
	 * @param configMap
	 * @param capabilityMap
	 * @param failcaseList
	 */
	public void addFailcaseItems(Map<String, String> configMap, Map<String, String> capabilityMap,
			List<String> failcaseList) {
		Node node = getDoc().selectSingleNode("/root/failcase");
		if (node instanceof Element) {
			Element ele = (Element) node;
			Element ele_item = DocumentHelper.createElement("item");
			Element ele_item_configMap = ele_item.addElement("configMap");
			for (Entry<String, String> entry : configMap.entrySet()) {
				ele_item_configMap.addElement(entry.getKey()).setText(entry.getValue());
			}
			Element ele_item_capabilityMap = ele_item.addElement("capabilityMap");
			for (Entry<String, String> entry : capabilityMap.entrySet()) {
				ele_item_capabilityMap.addElement(entry.getKey()).setText(entry.getValue());
			}
			Element ele_item_method = ele_item.addElement("method");
			for (String name : failcaseList) {
				ele_item_method.addElement("name").setText(name);
			}
			ele.add(ele_item);
		}
		writeDoc();
	}

}
