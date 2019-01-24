package com.Util;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.constant.Cconfig;

public class ParamsUtil {
	Logger logger = LoggerFactory.getLogger(ParamsUtil.class);

	public boolean InitParams(ParamsBean paramsBean) {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(System.getProperty("user.dir") + "/Data/Config.xml");
			Element root = doc.getRootElement();
			List<Element> childElements = root.elements();
//			if(childElements.size()<0){
//				logger.warn("the number of params is "+childElements.size()+", pls check Config.xml!");
//				return false;
//			}
			for (Element child : childElements) {
				switch (child.getName()) {
				// Base config
				case "Base_firstInit":
					paramsBean.setBase_firstInit(child.getText());
					break;
				// Logs config
				case "Logs_crashPath":
					paramsBean.setLogs_crashPath(child.getText());
					break;
				case "Logs_inputcmd":
					paramsBean.setLogs_inputcmd(child.getText());
					break;
				case "Logs_highlight":
					paramsBean.setLogs_highlight(child.getText());
					break;
				// Monkey Setting
				case "Monkey_filterPackages":
					paramsBean.setMonkey_filterPackages(child.getText());
					break;
				case "Monkey_Customize":
					paramsBean.setMonkey_Customize(child.getText());
					break;
				case "Monkey_arow":
					paramsBean.setMonkey_arow(child.getText());
					break;
				case "Monkey_arowword":
					paramsBean.setMonkey_arowword(child.getText());
					break;
				case "Monkey_showduplicate":
					paramsBean.setMonkey_showduplicate(child.getText());
					break;
				case "Monkey_isreconnect":
					paramsBean.setMonkey_isreconnect(child.getText());
					break;
				case "androidSDK":
					paramsBean.setAndroidSDK(child.getText());
					if (MainRun.OStype == Cconfig.WINDOWS) {
						File file = new File(paramsBean.getAndroidSDK() + "/adb.exe");
						if (file.exists()) {
							MainRun.paramsBean.setAndroidSDK_adb(file.getAbsolutePath());
						} else {
							MainRun.paramsBean
									.setAndroidSDK_adb(MainRun.paramsBean.getAndroidSDK() + "/platform-tools/adb.exe");
						}
					} else {
						File file = new File(paramsBean.getAndroidSDK() + "/adb");
						if (file.exists()) {
							MainRun.paramsBean.setAndroidSDK_adb(file.getAbsolutePath());
						} else {
							MainRun.paramsBean
									.setAndroidSDK_adb(MainRun.paramsBean.getAndroidSDK() + "/platform-tools/adb");
						}
					}
					break;
				case "MACcmd":
					paramsBean.setMACcmd(child.getText());
					break;
				case "reportPath":
					File file = new File(child.getText());
					if (file.exists() && file.isDirectory()) {
						paramsBean.setReportPath(child.getText());
					} else {
						paramsBean.setReportPath("");
					}
					break;
				case "IOS_Logs_highlight":
					paramsBean.setIOS_Logs_highlight(child.getText());
					break;
				case "IOS_Logs_inputcmd":
					paramsBean.setIOS_Logs_inputcmd(child.getText());
					break;
				case "IOS_Logs_App_highlight":
					paramsBean.setIOS_Logs_App_highlight(child.getText());
					break;
				case "IOS_Logs_App_inputcmd":
					paramsBean.setIOS_Logs_App_inputcmd(child.getText());
					break;
				case "IOS_Logs_App_path":
					paramsBean.setIOS_Logs_App_path(child.getText());
					break;
				case "Live_username":
					paramsBean.setLive_username(child.getText());
					break;
				case "Live_password":
					paramsBean.setLive_password(child.getText());
					break;
				case "Live_Server":
					paramsBean.setLive_Server(child.getText());
					break;
				case "Live_videopath":
					paramsBean.setLive_videopath(child.getText());
					break;
				case "Live_loop":
					paramsBean.setLive_loop(child.getText().equals("true") ? true : false);
					break;
				case "Live_config_title":
					paramsBean.setLive_config_title(child.getText());
					break;
				case "Live_config_coverUrl":
					paramsBean.setLive_config_coverUrl(child.getText());
					break;
				case "Live_config_platform":
					paramsBean.setLive_config_platform(child.getText());
					break;
				case "Live_config_showlocation":
					paramsBean.setLive_config_showlocation(child.getText().equals("true") ? true : false);
					break;
				case "Live_config_tags":
					paramsBean.setLive_config_tags(child.getText());
					break;
				// bigdata
				case "Bigdata_filter_event":
					paramsBean.setBigdata_filter_event(child.getText());
					break;
				case "Bigdata_note_path":
					paramsBean.setBigdata_note_path(child.getText());
					break;
				// performance
				case "Performance_packagename":
					paramsBean.setPerformance_packagename(child.getText());
					break;
				case "Performance_xAxis_num":
					paramsBean.setPerformance_xAxis_num(child.getText());
					break;
				// PicInspect
				case "PicInspect_folder":
					paramsBean.setPicInspect_folder(child.getText());
					break;
				case "PicInspect_zoom":
					paramsBean.setPicInspect_zoom(child.getText());
					break;
				// datahandel
				case "DataHandel_filepath":
					paramsBean.setDataHandel_filepath(child.getText());
					break;
				default:
					logger.warn(child.getName() + "=" + child.getText()
							+ " is not the correct parmas, pls check Config.xml");
					return false;
				}
			}

			logger.info("Config.xml params: " + paramsBean.ReadParams());
			return true;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} finally {

		}
		return false;
	}
}
