package com.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoteBean {
	// <?xml version="1.0" encoding="UTF-8"?>
	// <root>
	// <folder>/Users/auto/Desktop/QAUiReport/Android/2018_0509_1752_47-52107038f4494427</folder>
	// <title>测试结果汇总</title>
	// <summary>场景:总用例=1,通过=1,失败=0;Monkey(Android-SYS):CRASH:出现0次;</summary>
	// <createtime>2018-05-09 17:52:47.072</createtime>
	// <items_scene>
	// <item><success>true</success><no>1</no><folder>1-单项测试</folder><summary>总用例=1,通过=1,失败=0</summary></item>
	// </items_scene>
	// <items_monkey_android_sys>
	// <item><success>true</success><no>2</no><folder>2-Monkey(Android-SYS)</folder><summary>CRASH:出现0次</summary></item>
	// </items_monkey_android_sys>
	// </root>
	String done;
	String folder;
	String title;
	String summary;
	String createtime;
	List<Map<String, String>> items_scene = new ArrayList<>();
	List<Map<String, String>> items_monkey_android_sys = new ArrayList<>();

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public List<Map<String, String>> getItems_scene() {
		return items_scene;
	}

	public void setItems_scene(List<Map<String, String>> items_scene) {
		this.items_scene = items_scene;
	}

	public List<Map<String, String>> getItems_monkey_android_sys() {
		return items_monkey_android_sys;
	}

	public void setItems_monkey_android_sys(List<Map<String, String>> items_monkey_android_sys) {
		this.items_monkey_android_sys = items_monkey_android_sys;
	}

	public String getDone() {
		return done;
	}

	public void setDone(String done) {
		this.done = done;
	}

}
