package com.task;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.config.SceneXmlParse;
import com.viewer.main.MainRun;

public class ReadScene {
	Logger logger = LoggerFactory.getLogger(ReadScene.class);
	Map<String, Object> androidSceneMap = new LinkedHashMap<>();
	Map<String, Object> iosSceneMap = new LinkedHashMap<>();

	public ReadScene() {
		// TODO Auto-generated constructor stub
		setScene();
	}

	/**
	 * 得到android 场景信息
	 * 
	 * @return
	 */
	public Map<String, Object> getAndroidSceneMap() {
		return androidSceneMap;
	}

	/**
	 * 得到ios 场景信息
	 * 
	 * @return
	 */
	public Map<String, Object> getIOSSceneMap() {
		return iosSceneMap;
	}

	/**
	 * 刷新场景信息
	 */
	public void refreshScene() {
		logger.info("refresh scene xml info");
		setScene();
	}

	/**
	 * 设置场景信息
	 */
	private void setScene() {
		logger.info("start to read scene xml");
		// 从scene.xml中获取场景信息
		for (File file : getAndroidFiles()) {
			SceneXmlParse sceneXmlParse = new SceneXmlParse(file.getAbsolutePath());
			Map<String, Object> map = sceneXmlParse.getSceneMap();
			for (Entry<String, Object> entry : map.entrySet()) {
				androidSceneMap.put(entry.getKey(), entry.getValue());
			}
		}
		for (File file : getIOSFiles()) {
			SceneXmlParse sceneXmlParse = new SceneXmlParse(file.getAbsolutePath());
			Map<String, Object> map = sceneXmlParse.getSceneMap();
			for (Entry<String, Object> entry : map.entrySet()) {
				iosSceneMap.put(entry.getKey(), entry.getValue());
			}
		}
		// 从config.xml中获取场景信息
		MainRun.androidXmlParse.getSceneMap().entrySet().forEach(i -> androidSceneMap.put(i.getKey(), i.getValue()));
		MainRun.iosXmlParse.getSceneMap().entrySet().forEach(i -> iosSceneMap.put(i.getKey(), i.getValue()));
	}

	/**
	 * 得到android场景xml列表
	 * 
	 * @return
	 */
	private File[] getAndroidFiles() {
		File folder = new File(MainRun.settingsBean.getDatalocation());
		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				return file.isFile() && file.getName().startsWith("androidscene_") && file.getName().endsWith(".xml");
			}
		});
		return files;
	}

	/**
	 * 得到ios场景xml列表
	 * 
	 * @return
	 */
	private File[] getIOSFiles() {
		File folder = new File(MainRun.settingsBean.getDatalocation());
		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				return file.isFile() && file.getName().startsWith("iosscene_") && file.getName().endsWith(".xml");
			}
		});
		return files;
	}
}
