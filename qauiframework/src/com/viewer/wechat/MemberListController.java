package com.viewer.wechat;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.WeChatUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemberListController implements Initializable {
	Logger logger = LoggerFactory.getLogger(MemberListController.class);

	@FXML
	AnchorPane anchorPaneMain;
	@FXML
	VBox VboxMain, vBox_Member;
	@FXML
	Button btn_cancel, btn_ok, btn_refresh, btn_expanded, btn_select_all;
	@FXML
	Label lbl_title;

	List<PeopleBean> peopleBeanList = new ArrayList<>();;// 成员列表信息
	List<DepartmentBean> departmentBeanList = new ArrayList<>();;// 部门列表
	Stage stage;
	String people_list;// xml记录
	MemberListUI memberListUI;// stage

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initUI();
	}

	/**
	 * 初始化部门列表
	 */
	private void initMemberList() {
		lbl_title.setText("加载中...");
		vBox_Member.getChildren().removeAll(vBox_Member.getChildren());// 清除旧的
		peopleBeanList.clear();
		departmentBeanList.clear();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (WeChatUtil.getInstance().timingAccess_token()) {
					JSONArray departmentinfo = WeChatUtil.getInstance().getDepartmentInfo();
					JSONArray peopleinfo = WeChatUtil.getInstance().getPeopleInfo();
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							createMemberList(departmentinfo, peopleinfo);
							if (peopleBeanList.isEmpty()) {
								lbl_title.setText("获取部门列表失败,请检查微信配置!");
							} else {
								lbl_title.setText("部门列表");
								setCheckedPeople(people_list);
							}
						}
					});
				} else {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							lbl_title.setText("获取部门列表失败,请检查微信配置!");
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 创建成员列表
	 * 
	 * @param departmentinfo
	 * @param peopleinfo
	 */
	private void createMemberList(JSONArray departmentinfo, JSONArray peopleinfo) {
		if (departmentinfo == null || peopleinfo == null) {
			logger.info("createMemberList failed");
			return;
		}
		logger.info("start to create department list");
		for (int i = 0; i < departmentinfo.length(); i++) {
			try {
				JSONObject subinfo = departmentinfo.getJSONObject(i);
				int id = subinfo.getInt("id");
				String name = subinfo.getString("name");
				int parentid = subinfo.getInt("parentid");
				int order = subinfo.getInt("order");
				if (id != 1) {
					DepartmentBean departmentBean = new DepartmentBean();
					departmentBean.setId(id);
					departmentBean.setName(name);
					departmentBean.setParentid(parentid);
					departmentBean.setOrder(order);
					departmentBeanList.add(departmentBean);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}
		}
		logger.info("start to create people list");
		for (int i = 0; i < peopleinfo.length(); i++) {
			try {
				JSONObject subinfo = peopleinfo.getJSONObject(i);
				String userid = subinfo.getString("userid");
				String name = subinfo.getString("name");
				int department = subinfo.getJSONArray("department")
						.getInt(subinfo.getJSONArray("department").length() - 1);// 取最后一个部门ID
				PeopleBean peopleBean = new PeopleBean();
				peopleBean.setDepartment(department);
				peopleBean.setName(name);
				peopleBean.setUserid(userid);
				peopleBeanList.add(peopleBean);
				for (int j = 0; j < departmentBeanList.size(); j++) {
					DepartmentBean departmentBean = departmentBeanList.get(j);
					if (departmentBean.getId() == department) {
						departmentBean.getvBox().getChildren().add(peopleBean.gethBox());
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}
		}
		for (int i = 0; i < departmentBeanList.size(); i++) {
			DepartmentBean departmentBean = departmentBeanList.get(i);
			if (departmentBean.getParentid() == 1) {// 一级部门,公司下的子部门
				vBox_Member.getChildren().add(departmentBean.getTitledPane());
			} else {
				for (int j = 0; j < departmentBeanList.size(); j++) {// 每个部门加上父部门
					DepartmentBean pardepartmentBean = departmentBeanList.get(j);
					if (departmentBean.getParentid() == pardepartmentBean.getId()) {
						pardepartmentBean.getvBox().getChildren().add(departmentBean.getTitledPane());
					}
				}
			}
		}
	}

	/**
	 * 初始化
	 */
	public void init(MemberListUI memberListUI, String people_list) {
		this.memberListUI = memberListUI;
		this.people_list = people_list;
		stage = (Stage) anchorPaneMain.getScene().getWindow();
		initMemberList();
		// 按照窗体改变
		stage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				VboxMain.setPrefWidth(stage.getWidth());
			}
		});
		stage.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				VboxMain.setPrefHeight(stage.getHeight());
			}
		});
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		btn_ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_ok button");
				if (memberListUI.confirm()) {
					stage.close();
				}
			}
		});
		btn_cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_cancel button");
				if (memberListUI.cancel()) {
					stage.close();
				}
			}
		});
		btn_refresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_refresh button");
				WeChatUtil.getInstance().refreshAccess_token();
				WeChatUtil.getInstance().refreshDepartmentInfo();
				WeChatUtil.getInstance().refreshPeopleInfo();
				initMemberList();
			}
		});
		btn_expanded.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_expanded button");
				if (btn_expanded.getText().equals("展开")) {
					for (DepartmentBean departmentBean : departmentBeanList) {
						departmentBean.getTitledPane().setExpanded(true);
					}
					btn_expanded.setText("收起");
				} else {
					for (DepartmentBean departmentBean : departmentBeanList) {
						departmentBean.getTitledPane().setExpanded(false);
					}
					btn_expanded.setText("展开");
				}
			}
		});

		btn_select_all.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_select_all button");
				if (btn_select_all.getText().equals("全选")) {
					for (PeopleBean peopleBean : peopleBeanList) {
						peopleBean.getCheckBox().setSelected(true);
					}
					btn_select_all.setText("取消全选");
				} else {
					for (PeopleBean peopleBean : peopleBeanList) {
						peopleBean.getCheckBox().setSelected(false);
					}
					btn_select_all.setText("全选");
				}
			}
		});
	}

	/**
	 * 设置已选中人员
	 * 
	 * @param people_list
	 */
	public void setCheckedPeople(String people_list) {
		String[] people = people_list.split(";");
		logger.info("set checkbox people list=" + people_list);
		for (PeopleBean peopleBean : peopleBeanList) {// 勾选默认人员
			for (String str : people) {
				// String name=str.split("=")[0];
				// String userid=str.split("=")[1];
				if (str.contains("=") && str.split("=")[1].equals(peopleBean.getUserid())) {
					peopleBean.getCheckBox().setSelected(true);
				}
			}
		}
	}

	/**
	 * 得到发送人员的id|id|id字符串
	 * 
	 * @return
	 */
	public String getPeopleSendStr() {
		StringBuffer peopleBuf = new StringBuffer();
		Iterator<PeopleBean> iterator = peopleBeanList.iterator();
		while (iterator.hasNext()) {
			PeopleBean peopleBean = iterator.next();
			if (peopleBean.getCheckBox().isSelected()) {
				peopleBuf.append(peopleBean.getUserid() + "|");
			}
		}
		if (peopleBuf.length() > 0) {
			String temp = peopleBuf.substring(0, peopleBuf.length() - 1);
			peopleBuf.setLength(0);
			peopleBuf.append(temp);
		}
		logger.info("people list =" + peopleBuf.toString());
		return peopleBuf.toString();
	}

	/**
	 * 得到people_list
	 * 
	 * @return
	 */
	public String getPeopleList() {
		StringBuffer peopleBuf = new StringBuffer();
		Iterator<PeopleBean> iterator = peopleBeanList.iterator();
		while (iterator.hasNext()) {
			PeopleBean peopleBean = iterator.next();
			if (peopleBean.getCheckBox().isSelected()) {
				peopleBuf.append(peopleBean.getName() + "=" + peopleBean.getUserid() + ";");
			}
		}
		logger.info("people list=" + peopleBuf.toString());
		return peopleBuf.toString();
	}
}
