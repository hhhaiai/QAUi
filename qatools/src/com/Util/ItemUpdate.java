package com.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemUpdate {
	Logger logger = LoggerFactory.getLogger(ItemUpdate.class);
	// MonkeyPackage monkeypackage=new MonkeyPackage();

//	public void run() {
//		// 当设备状态改变时,更新界面
//		ItemUpdateThread itemupdatethread = new ItemUpdateThread();
//		new Thread(itemupdatethread).start();
//		logger.info("Item Update");
//
//	}
//
//	public void updateMonkeyUI() {
//		if (MainRun.mainFrame.getMonkey_UImain() != null) {
//			MainRun.mainFrame.getMonkey_UImain().setlistpackageAPP();
//			logger.info("ItemUpdate MonkeyUI");
//		}
//	}
//
//	public void updateAutoscriptUImain() {
//		if (MainRun.mainFrame.getAutoScript_UImain() != null) {
//			if (MainRun.mainFrame.getAutoScript_UImain().getisstartrecord()) {
//				MainRun.mainFrame.getAutoScript_UImain().getbtnStartRecord().doClick();
//			}
//			logger.info("ItemUpdate AutoscriptUImain");
//		}
//	}
//
//	class ItemUpdateThread implements Runnable {
//
//		public ItemUpdateThread() {
//
//		}
//
//		public void run() {
//			if (MainRun.selectedID != null && MainRun.selectedID.length() > 35) {
//
//			} else {
//				updateMonkeyUI();
//				updateAutoscriptUImain();
//			}
//		}
//	}
}
