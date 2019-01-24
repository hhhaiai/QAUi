package com.DataHandel;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Util.TimeUtil;
import com.constant.Cconfig;
import com.general.TextBoxFXUI;
import com.general.ToastBoxFXUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StatisticsBoxFXUI extends Stage {
	Logger logger = LoggerFactory.getLogger(StatisticsBoxFXUI.class);
	Label lbl_statistics;
	Label lbl_statistics_result_monitor = new Label();
	Label lbl_statistics_result_drawing = new Label();
	boolean isstatistics = false;
	LogMonitor logMonitor;
	FileHandel fileHandel;
	Timer timer_statistics;
	boolean isontop = false;
	String type = Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING;

	public StatisticsBoxFXUI(LogMonitor logMonitor, FileHandel fileHandel) {
		// TODO Auto-generated constructor stub
		this.logMonitor = logMonitor;
		this.fileHandel = fileHandel;
		setTitle("统计数据");
		setWidth(500);
		setHeight(700);
		// setAlwaysOnTop(true);
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		lbl_statistics = new Label("统计数据");
		lbl_statistics.setTextFill(Color.BLUE);
		lbl_statistics.setFont(new Font(18));
		Label lbl_statistics_time = new Label();
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefHeight(500);
		VBox scroll_vbox = new VBox();
		HBox lbl_hBox = new HBox();
		lbl_hBox.getChildren().addAll(lbl_statistics_result_monitor, lbl_statistics_result_drawing);
		scroll_vbox.getChildren().add(lbl_hBox);
		scrollPane.setContent(scroll_vbox);
		Label lbl_note = new Label("图表绘制:先点击开始统计,然后绘制图表.\n" + "日志监控:需要统计时,点击开始统计,不需要时点击停止统计");
		HBox btn_hBox = new HBox();
		btn_hBox.setSpacing(20);
		btn_hBox.setAlignment(Pos.CENTER_RIGHT);
		Button btn_statistics_report = new Button("表格数据");
		Button btn_statistics_ontop = new Button("置顶");
		Button btn_statistics_start = new Button("开始统计");
		Button btn_statistics_clear = new Button("清除");
		btn_hBox.getChildren().addAll(btn_statistics_ontop, btn_statistics_report, btn_statistics_clear,
				btn_statistics_start);
		vBox.getChildren().addAll(lbl_statistics, lbl_statistics_time, scrollPane, lbl_note, btn_hBox);
		setScene(new Scene(vBox));

		lbl_statistics_result_monitor.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub

				if (event.getButton() == MouseButton.SECONDARY) {
					ToastBoxFXUI.showToast("统计信息已复制到剪贴板", ToastBoxFXUI.INFO, 2000);
					HelperUtil.setSysClipboardText(lbl_statistics_result_monitor.getText());
				}
			}
		});
		lbl_statistics_result_drawing.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub

				if (event.getButton() == MouseButton.SECONDARY) {
					ToastBoxFXUI.showToast("统计信息已复制到剪贴板", ToastBoxFXUI.INFO, 2000);
					HelperUtil.setSysClipboardText(lbl_statistics_result_drawing.getText());
				}
			}
		});
		btn_statistics_start.setTooltip(new Tooltip("图表绘制:先点击开始统计,然后绘制图表.\n" + "日志监控:需要统计时,点击开始统计,不需要时点击停止统计"));
		btn_statistics_start.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics_start button");
				if (!isstatistics) {
					isstatistics = true;
					btn_statistics_start.setText("停止统计");
					btn_statistics_clear.setDisable(true);
					lbl_statistics_time.setText("开始时间:" + TimeUtil.getTime("HH:mm:ss"));
				} else {
					isstatistics = false;
					btn_statistics_start.setText("开始统计");
					btn_statistics_clear.setDisable(false);
					lbl_statistics_time
							.setText(lbl_statistics_time.getText() + ",结束时间:" + TimeUtil.getTime("HH:mm:ss"));
				}
				logMonitor.getDataFilter().setIsstatistics(isstatistics);
				fileHandel.getDataFilter().setIsstatistics(isstatistics);
				updateInfo();
			}
		});

		btn_statistics_clear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics_clear button");
				lbl_statistics_time.setText("");
				lbl_statistics_result_monitor.setText("");
				lbl_statistics_result_drawing.setText("");
				logMonitor.getDataFilter().clearStatisticsData();
				fileHandel.getDataFilter().clearStatisticsData();
			}
		});
		btn_statistics_ontop.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics_ontop button");
				if (!isontop) {
					isontop = true;
					btn_statistics_ontop.setText("取消置顶");
					setAlwaysOnTop(true);
				} else {
					isontop = false;
					btn_statistics_ontop.setText("置顶");
					setAlwaysOnTop(false);
				}
			}
		});
		btn_statistics_report.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics_report button");
				ToastBoxFXUI.showToast("报告信息已复制到剪贴板", ToastBoxFXUI.INFO, 2000);
				if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR)) {
					TextBoxFXUI textBoxFXUI = new TextBoxFXUI("统计数据表", "主要用于添加报告",
							logMonitor.getDataFilter().getStatisticsForm(), 600, 600) {

						@Override
						protected boolean confirmButton() {
							// TODO Auto-generated method stub
							getTextArea().setText(logMonitor.getDataFilter().sortStatisticsForm());
							return false;
						}

						@Override
						protected boolean cancelButton() {
							// TODO Auto-generated method stub
							return true;
						}

					};
					textBoxFXUI.getBtnOK().setText("排序");
					textBoxFXUI.getBtnCancel().setText("关闭");
					textBoxFXUI.show();
				} else if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING)) {
					TextBoxFXUI textBoxFXUI = new TextBoxFXUI("统计数据表", "主要用于添加报告",
							fileHandel.getDataFilter().getStatisticsForm(), 600, 600) {

						@Override
						protected boolean confirmButton() {
							// TODO Auto-generated method stub
							getTextArea().setText(fileHandel.getDataFilter().sortStatisticsForm());
							return false;
						}

						@Override
						protected boolean cancelButton() {
							// TODO Auto-generated method stub
							return true;
						}

					};
					textBoxFXUI.getBtnOK().setText("排序");
					textBoxFXUI.getBtnCancel().setText("关闭");
					textBoxFXUI.show();
				}
			}
		});
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 */
	private void updateInfo() {
		if (isstatistics) {
			timer_statistics = new Timer();
			timer_statistics.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR)) {
								lbl_statistics.setText("监控统计数据");
								lbl_statistics_result_monitor.setText(logMonitor.getDataFilter().getStatisticsInfo());
								lbl_statistics_result_drawing.setText("");
							} else if (type.equals(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING)) {
								lbl_statistics.setText("图表统计数据");
								lbl_statistics_result_drawing.setText(fileHandel.getDataFilter().getStatisticsInfo());
								lbl_statistics_result_monitor.setText("");
							}
						}
					});
				}
			}, 100, 1000);
		} else {
			if (timer_statistics != null) {
				timer_statistics.cancel();
				timer_statistics = null;
			}
		}
	}
}
