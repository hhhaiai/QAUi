package com.viewer.wechat;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.viewer.main.MainRun;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class MemberListUI extends Stage {
	Logger logger = LoggerFactory.getLogger(MemberListUI.class);
	MemberListController controller;

	public MemberListUI(String people_list) {
		// TODO Auto-generated constructor stub
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/viewer/wechat/MemberListUI.fxml"));
		try {
			Parent root = fxmlLoader.load();
			setScene(new Scene(root));
			controller = (MemberListController) fxmlLoader.getController();
			controller.init(this, people_list);
			String title = MainRun.sysConfigBean.getWechat().get(Cparams.name);
			setTitle(title);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
	}

	/**
	 * 获得控制器
	 * 
	 * @return
	 */
	public MemberListController getContorller() {
		return controller;
	}

	/**
	 * 确定按钮
	 * 
	 * @return
	 */
	public abstract boolean confirm();

	/**
	 * 取消按钮
	 * 
	 * @return
	 */
	public abstract boolean cancel();
}
