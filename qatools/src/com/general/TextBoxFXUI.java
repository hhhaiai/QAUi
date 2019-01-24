package com.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class TextBoxFXUI extends Stage {
	Logger logger = LoggerFactory.getLogger(TextBoxFXUI.class);
	TextArea textArea;
	Button btn_ok;
	Button btn_cancel;

	/**
	 * 文本输入框
	 * 
	 * @param title 标题
	 * @param note  文本框说明
	 * @param text  初始文本
	 * @param width 窗体宽
	 * @param hight 窗体高
	 */
	public TextBoxFXUI(String title, String note, String text, int width, int hight) {
		// TODO Auto-generated constructor stub
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		setTitle(title);
		setScene(new Scene(vBox));
		setWidth(width);
		setHeight(hight);
		// setResizable(false);// 不允许变化窗口大小
		// setAlwaysOnTop(true);

		textArea = new TextArea();
		textArea.setText(text);
		textArea.setPrefHeight(hight - 100);

		Label lbl_note = new Label();
		lbl_note.setText(note);
		lbl_note.setPrefHeight(15);

		HBox hBox = new HBox();
		btn_ok = new Button();
		btn_ok.setText("确定");
		btn_ok.setPrefWidth(100);
		btn_cancel = new Button();
		btn_cancel.setText("取消");
		btn_cancel.setPrefWidth(100);
		hBox.setAlignment(Pos.CENTER_RIGHT);
		hBox.getChildren().addAll(btn_ok, btn_cancel);

		vBox.getChildren().addAll(textArea, lbl_note, hBox);

		btn_ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_ok button");
				if (confirmButton()) {
					close();
				}
			}
		});
		btn_cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_cancel button");
				if (cancelButton()) {
					close();
				}
			}
		});
	}

	/**
	 * 确定按钮内容
	 * 
	 * @return true则关闭窗口
	 */
	protected abstract boolean confirmButton();

	/**
	 * 取消按钮内容
	 * 
	 * @return true则关闭窗口
	 */
	protected abstract boolean cancelButton();

	/**
	 * 获取文本
	 * 
	 * @return
	 */
	public String getText() {
		return textArea.getText();
	}

	public TextArea getTextArea() {
		return textArea;
	}

	public Button getBtnOK() {
		return btn_ok;
	}

	public Button getBtnCancel() {
		return btn_cancel;
	}
}
