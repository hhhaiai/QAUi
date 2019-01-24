package com.Performance;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.AndroidInfo;
import com.Util.HelperUtil;
import com.Util.TimeUtil;
import com.Viewer.MainRun;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;
import com.general.TextBoxFXUI;
import com.general.ToastBoxFXUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class PerformanceMainController implements Initializable {
	Logger logger = LoggerFactory.getLogger(PerformanceMainController.class);

	@FXML
	private Button btn_start_monitor;
	@FXML
	AnchorPane anchorPane;

	@FXML
	ChoiceBox<String> choice_packagelist;
	@FXML
	Label lbl_statistics_data;
	@FXML
	Label lbl_statistics_time;
	@FXML
	Button btn_statistics;
	@FXML
	Button btn_statistics_clear;
	@FXML
	Button btn_set_xAxis_num;
	@FXML
	Button btn_set_packagenames;
	@FXML
	Button btn_save_data;
	@FXML
	Label lbl_device_info;

	LineChartsPaneFXUI lineChartFX;
	boolean isstart = false;
	boolean isstatistics = false;
	String udid;

	boolean issavedata = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		String packagenames = MainRun.paramsBean.getPerformance_packagename();
		for (String packagename : packagenames.split(";")) {
			choice_packagelist.getItems().add(packagename);
		}
		lbl_statistics_data.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub

				if (event.getButton() == MouseButton.SECONDARY) {
					ToastBoxFXUI.showToast("统计信息已复制到剪贴板", ToastBoxFXUI.INFO, 2000);
					HelperUtil.setSysClipboardText(lbl_statistics_data.getText());
				}
			}
		});
		lineChartFX = new LineChartsPaneFXUI(lbl_statistics_data);
		anchorPane.getChildren().addAll(lineChartFX.createLineChartBox());
		// 保存数据按钮
		btn_save_data.setTooltip(new Tooltip("保存各项数据到文本"));
		btn_save_data.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_save_data button");
				if (!issavedata) {
					if (lineChartFX.startSaveData() != null) {
						issavedata = true;
						btn_save_data.setText("停止保存");
					}
				} else {
					issavedata = false;
					lineChartFX.stopSaveData();
					btn_save_data.setText("保存数据");
				}
			}
		});
		// 统计按钮
		btn_statistics.setTooltip(new Tooltip("开始统计各项数据,求出最大最小平均累计等"));
		btn_statistics.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics button");
				if (!isstatistics) {
					isstatistics = true;
					btn_statistics.setText("停止统计");
					btn_statistics_clear.setDisable(true);
					lineChartFX.setIsStatistics(true);
					lbl_statistics_time.setText("开始时间:" + TimeUtil.getTime("HH:mm:ss"));
				} else {
					isstatistics = false;
					btn_statistics.setText("开始统计");
					btn_statistics_clear.setDisable(false);
					lineChartFX.setIsStatistics(false);
					lbl_statistics_time
							.setText(lbl_statistics_time.getText() + ",结束时间:" + TimeUtil.getTime("HH:mm:ss"));
				}
			}
		});
		// 统计清空数据按钮
		btn_statistics_clear.setTooltip(new Tooltip("重置统计数据"));
		btn_statistics_clear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics_clear button");
				lineChartFX.clearStatisticsData();
				lbl_statistics_time.setText("");
			}
		});
		// 包名选择框
		choice_packagelist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				logger.info("change packagename,from " + oldValue + " to " + newValue);
				if (newValue != null) {
					lineChartFX.setPackagename(newValue.split("=")[1]);
				}
			}
		});
		if (choice_packagelist.getItems().size() > 0) {
			choice_packagelist.getSelectionModel().select(0);
		}
		// 设置
		// X轴数量设置
		btn_set_xAxis_num.setTooltip(new Tooltip("设置折线图X轴显示的数据数量"));
		btn_set_xAxis_num.setManaged(false);
		btn_set_xAxis_num.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_set_xAxis_num button");
				String num = AlertBoxFXUI.showInputDialog("请输入大于零整数", "X轴显示数量",
						AlertBoxFXUI.showInputDialog_Integer_Positive);
				if (num == null) {
					return;
				}
				if (HelperUtil.isInteger(num) && Integer.parseInt(num) > 0) {
					MainRun.paramsBean.setPerformance_xAxis_num(num);
					MainRun.xmlOperationUtil.XMLChanger("Performance_xAxis_num", num);
					AlertBoxFXUI.showMessageDialog("设置成功", "设置X轴显示数量为" + num + ",下次开始监控后生效.");
				} else {
					AlertBoxFXUI.showMessageDialogError("错误", "请输入大于0整数");
				}
			}
		});
		// 包名设置
		btn_set_packagenames.setTooltip(new Tooltip("设置应用注释名和包名,需要按照一定格式填写."));
		btn_set_packagenames.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_set_packagenames button");
				String packagenames = MainRun.paramsBean.getPerformance_packagename().replace(";", ";\n");
				TextBoxFXUI textBoxFXUI = new TextBoxFXUI("应用包名设置", "输入应用包名,格式:XXX=com.xxx.xxx;XXX=com.xxx.xxx;",
						packagenames, 400, 400) {

					@Override
					protected boolean confirmButton() {
						// TODO Auto-generated method stub
						String text = getText();
						text = text.replace("\n", "");
						if (!text.matches(Cconfig.REGEX_FORMAT)) {
							AlertBoxFXUI.showMessageDialogError("格式错误", "输入应用包名,格式:XXX=com.xxx.xxx;XXX=com.xxx.xxx;");
							return false;
						}
						MainRun.paramsBean.setPerformance_packagename(text);
						MainRun.xmlOperationUtil.XMLChanger("Performance_packagename", text);
						choice_packagelist.getItems().removeAll(choice_packagelist.getItems());
						for (String packagename : text.split(";")) {
							choice_packagelist.getItems().add(packagename);
						}
						choice_packagelist.getSelectionModel().select(0);
						return true;
					}

					@Override
					protected boolean cancelButton() {
						// TODO Auto-generated method stub
						return true;
					}
				};
				textBoxFXUI.show();
			}
		});
		// 设备信息识别
		getDeviceInfo();
	}

	@FXML
	private void btn_start_monitor_click() {// 监控按钮
		logger.info("press btn_start_monitor button");
		if (udid == null) {
			AlertBoxFXUI.showMessageDialogError("错误", "设备id不能为null");
			return;
		}

		if (!isstart) {
			isstart = true;
			btn_start_monitor.setText("停止监控");
			lineChartFX.startMonitor();
			choice_packagelist.setDisable(true);
		} else {
			isstart = false;
			btn_start_monitor.setText("开始监控");
			lineChartFX.stopMonitor();
			choice_packagelist.setDisable(false);
			if (isstatistics) {
				btn_statistics.fire();// 停止统计按钮
			}
		}

	}

	/**
	 * 设置设备udid
	 * 
	 * @param udid
	 */
	public void setUdid(String udid) {
		this.udid = udid;
		lineChartFX.setUdid(udid);
	}

	/**
	 * 关闭监控
	 */
	public void close() {
		lineChartFX.stopMonitor();
	}

	/**
	 * 设置设备信息
	 * 
	 * @return
	 */
	private String getDeviceInfo() {
		StringBuffer infoBuf = new StringBuffer();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (udid != null) {
					infoBuf.append("厂商:" + AndroidInfo.getManufacturer(udid));
					infoBuf.append(",");
					infoBuf.append("机型:" + AndroidInfo.getModel(udid));
					infoBuf.append("\n");
					infoBuf.append("平台:" + AndroidInfo.getProduct(udid));
					infoBuf.append(",");
					infoBuf.append("API:" + AndroidInfo.getSDKVersion(udid));
					infoBuf.append("\n");
					infoBuf.append("版本:" + AndroidInfo.getVersion(udid));
					infoBuf.append(",");
					infoBuf.append("语言:" + AndroidInfo.getLanguage(udid));
					infoBuf.append("\n");
					int[] i = AndroidInfo.getDeviceResolution(udid);
					infoBuf.append("分辨率:" + i[0] + "x" + i[1]);
					infoBuf.append(",");
					infoBuf.append("内存:" + AndroidInfo.getDeviceMemroy(udid) + "MB");
					infoBuf.append("\n");
				} else {
					infoBuf.append("请插入Andorid设备!");
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						lbl_device_info.setText(infoBuf.toString());
					}
				});
			}
		}).start();
		return infoBuf.toString();
	}
}