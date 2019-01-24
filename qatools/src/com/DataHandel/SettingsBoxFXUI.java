package com.DataHandel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;
import com.general.ToastBoxFXUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SettingsBoxFXUI extends Stage {
	Logger logger = LoggerFactory.getLogger(SettingsBoxFXUI.class);

	ChoiceBox<String> choice_settingslist;
	TextArea textArea_simple, textArea_timeperiod;
	TextField textField_flag;
	TextField textField_regex;
	TextField textField_name;
	TextField textField_group_num;
	TextField textField_group_interval;
	TextField textField_retain_decimal;
	ToggleGroup toggleGroup_group;
	CheckBox checkBox_effective;
	Map<String, String> settingsMap;
	List<Map<String, String>> settingsMapList;
	int settingsno = 0;
	DataHandelXmlParse dataHandelXmlParse;

	public SettingsBoxFXUI() {
		// TODO Auto-generated constructor stub
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		setTitle("数据提取设置");
		setScene(new Scene(vBox));
		setWidth(400);
		setHeight(800);
		// setResizable(false);// 不允许变化窗口大小
		// vBox.setFillWidth(true);
		dataHandelXmlParse = new DataHandelXmlParse();
		settingsMapList = dataHandelXmlParse.getItemMapList();
		// 条目栏
		Label lbl_items = new Label("选择配置表");
		lbl_items.setTextFill(Color.BLUE);
		lbl_items.setFont(new Font(15));
		vBox.getChildren().add(lbl_items);
		choice_settingslist = new ChoiceBox<>();
		choice_settingslist.setPrefWidth(getWidth());
		for (int i = 0; i < settingsMapList.size(); i++) {
			Map<String, String> map = settingsMapList.get(i);
			choice_settingslist.getItems().add((i + 1) + ":" + map.get(Cconfig.DATAHANDEL_SETTINGS_FLAG)
					+ (map.get(Cconfig.DATAHANDEL_SETTINGS_EFFECTIVE).equals("true") ? "[生效]" : ""));
		}
		// 设置标记选择框
		choice_settingslist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				logger.info("change settings flag,from " + oldValue + " to " + newValue);
				if (newValue != null) {
					settingsno = Integer.parseInt(newValue.split(":")[0]) - 1;
					settingsMap = settingsMapList.get(settingsno);
					changeItem();
				}
			}
		});
		vBox.getChildren().addAll(choice_settingslist);

		Label lbl_general = new Label("通用设置");
		lbl_general.setTextFill(Color.BLUE);
		lbl_general.setFont(new Font(15));
		vBox.getChildren().add(lbl_general);
		Label lbl_flag = new Label("设置标记");
		textField_flag = new TextField();
		Label lbl_simple = new Label("测试样例");
		textArea_simple = new TextArea();
		textArea_simple.setPrefHeight(100);
		textArea_simple.setTooltip(new Tooltip("输入测试待匹配的字符串样例,用于测试解析是否符合预期"));
		Label lbl_regex = new Label("正则提取式");
		textField_regex = new TextField();
		textField_regex.setTooltip(new Tooltip("输入正则表达式,小括号中的数据将根据设置形成折线.\n"
				+ "字符串\"fps:22.6,aveTime:44ms\",则正则\"fps:(-?[0-9]+([.][0-9]+){0,1}),aveTime:(-?\\d+)ms\"\n"
				+ "推荐正则写法:数字(-?[0-9]+([.][0-9]+){0,1}),整数(-?\\d+),小数(-?\\d+\\.[0-9]+)"));
		Label lbl_name = new Label("名称设置(标识" + Cconfig.DATAHANDEL_SETTINGS_NAME_FLAG + ",无效"
				+ Cconfig.DATAHANDEL_SETTINGS_NAME_INVALID + ",时间" + Cconfig.DATAHANDEL_SETTINGS_NAME_TIME + ")");
		textField_name = new TextField();
		textField_name.setTooltip(new Tooltip("此处采用java-Matcher的group原则,根据正则表达式中的括号顺序,依次填写折线名称,不可重名,以分号分隔,\n"
				+ "无效括号则都用" + Cconfig.DATAHANDEL_SETTINGS_NAME_INVALID + "代替," + "标识用"
				+ Cconfig.DATAHANDEL_SETTINGS_NAME_FLAG + "代替(不同标识组合将创建不同的折线图)," + "时间用"
				+ Cconfig.DATAHANDEL_SETTINGS_NAME_TIME + "代替,\n" + "例如:帧率;" + Cconfig.DATAHANDEL_SETTINGS_NAME_INVALID
				+ ";耗时"));
//		Label lbl_retain_decimal = new Label("结果保留小数位数");
//		textField_retain_decimal = new TextField();
//		textField_retain_decimal.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_RETAIN_DECIMAL));
//		textField_retain_decimal.setTooltip(new Tooltip("采样完成后输出数值保留几位小数"));
//		textField_retain_decimal.textProperty().addListener((observable, oldValue, newValue) -> {
//			if (!newValue.matches("[0-9]+")) {
//				textField_retain_decimal.setText(oldValue);
//			}
//		});
		vBox.getChildren().addAll(lbl_flag, textField_flag, lbl_simple, textArea_simple, lbl_regex, textField_regex,
				lbl_name, textField_name);

		Label lbl_group_num = new Label("数据组-采样数量");
		textField_group_num = new TextField();
		textField_group_num.setTooltip(new Tooltip("每多少组数据的生成一个折线点"));
		textField_group_num.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals("") && !newValue.matches("^[1-9]+\\d*")) {// 大于等于1
				textField_group_num.setText(oldValue);
			}
		});
		Label lbl_group_type = new Label("数据组-取值类型(不影响统计数据)");
		toggleGroup_group = new ToggleGroup();
		HBox hBox_group = new HBox();
		RadioButton radioButton_group_avg = new RadioButton("平均值");
		radioButton_group_avg.setToggleGroup(toggleGroup_group);
		radioButton_group_avg.setUserData("avg");
		RadioButton radioButton_group_max = new RadioButton("最大值");
		radioButton_group_max.setToggleGroup(toggleGroup_group);
		radioButton_group_max.setUserData("max");
		RadioButton radioButton_group_min = new RadioButton("最小值");
		radioButton_group_min.setToggleGroup(toggleGroup_group);
		radioButton_group_min.setUserData("min");
		hBox_group.getChildren().addAll(radioButton_group_avg, radioButton_group_max, radioButton_group_min);
		vBox.getChildren().addAll(lbl_group_num, textField_group_num, lbl_group_type, hBox_group);

		Label lbl_monitor = new Label("监控设置");
		lbl_monitor.setTextFill(Color.BLUE);
		lbl_monitor.setFont(new Font(15));
		vBox.getChildren().add(lbl_monitor);
		Label lbl_group_interval = new Label("数据组-间隔时间(毫秒,PC时间)");
		textField_group_interval = new TextField();
		textField_group_interval.setTooltip(new Tooltip("以PC时间为基准,每间隔多少毫秒后取一次样,直到取样结束绘制折线点后才重新开始计时"));
		textField_group_interval.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals("") && !newValue.matches("[0-9]+")) {
				textField_group_interval.setText(oldValue);
			}
		});
		vBox.getChildren().addAll(lbl_group_interval, textField_group_interval);

		Label lbl_drawing = new Label("绘图设置");
		lbl_drawing.setTextFill(Color.BLUE);
		lbl_drawing.setFont(new Font(15));
		vBox.getChildren().add(lbl_drawing);
		Label lbl_timeperiod = new Label("时间段配置(时间格式;开始时间/结束时间;)");
		textArea_timeperiod = new TextArea();
		textArea_timeperiod.setPrefHeight(100);
		textArea_timeperiod.setTooltip(new Tooltip("时间格式及时间段选择设置,时间格式采用java-SimpleDateFormat格式,\n"
				+ "时间段选择需以设置的时间格式书写,以/分隔开始时间和结束时间(当结束时间小于开始时间,则视为第二天),以冒号分隔时间段,\n" + "如果在时间格式中最前面加上感叹号!,则表示不以时间段绘图,\n"
				+ "例如:HH:mm:ss;05:14:31/09:15:21;14:02:01/12:01:22;"));
		vBox.getChildren().addAll(lbl_timeperiod, textArea_timeperiod);

		HBox hBox_update = new HBox();
		checkBox_effective = new CheckBox("生效");
		Button btn_del = new Button("删除");
		Button btn_add = new Button("增加");
		Button btn_change = new Button("修改");
		Button btn_test = new Button("测试");
		btn_test.setTextFill(Color.BLUE);
		hBox_update.setSpacing(20);
		hBox_update.setAlignment(Pos.CENTER_RIGHT);
		hBox_update.getChildren().addAll(checkBox_effective, btn_del, btn_add, btn_change, btn_test);
		vBox.getChildren().addAll(hBox_update);

		HBox hBox_confirm = new HBox();
		Button btn_ok = new Button();
		btn_ok.setText("确定");
		btn_ok.setPrefWidth(100);
		btn_ok.setVisible(false);
		Button btn_cancel = new Button();
		btn_cancel.setText("关闭");
		btn_cancel.setPrefWidth(100);
		hBox_confirm.getChildren().addAll(btn_ok, btn_cancel);
		hBox_confirm.setSpacing(20);
		hBox_confirm.setAlignment(Pos.CENTER_RIGHT);
		vBox.getChildren().addAll(hBox_confirm);

		btn_test.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_test button");
				AlertBoxFXUI.showMessageDialog("验证测试", testSimple());
			}
		});
		btn_ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_ok button");
				close();
			}
		});
		btn_cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_cancel button");
				close();
			}
		});
		btn_del.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_del button");
				if (AlertBoxFXUI.showConfirmDialog("是否删除", "请确认是否删除,不可恢复")) {
					if (settingsMapList.size() > 1) {
						dataHandelXmlParse.delElementByXpath("/root/settings/item", settingsno);
						updateItems();
						choice_settingslist.getSelectionModel().select(0);// 设置为增加的那一个
					} else {
						ToastBoxFXUI.showToast("只剩最后一项,无法删除", ToastBoxFXUI.ERROR, 3000);
					}
				}
			}
		});
		btn_add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_add button");
				if (!checkItem()) {
					return;
				}
				for (int i = 0; i < settingsMapList.size(); i++) {
					Map<String, String> map = settingsMapList.get(i);
					if (map.get(Cconfig.DATAHANDEL_SETTINGS_FLAG).equals(textField_flag.getText())) {
						ToastBoxFXUI.showToast("不能添加相同标记条目", ToastBoxFXUI.ERROR, 3000);
						return;
					}
				}
				setSettingsMap();
				dataHandelXmlParse.addElementByString("/root/settings", 0, "item", "");
				dataHandelXmlParse.addElementByMap("/root/settings/item", settingsMapList.size(), settingsMap);
				updateItems();
				choice_settingslist.getSelectionModel().select(settingsMapList.size() - 1);// 设置为增加的那一个
			}
		});
		btn_change.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_change button");
				if (!checkItem()) {
					return;
				}
				setSettingsMap();
				dataHandelXmlParse.changeMapByXPath("/root/settings/item", settingsno, settingsMap);
				updateItems();
				choice_settingslist.getSelectionModel().select(settingsno);// 保持当前序号
			}
		});
		// 初始化条目
		if (choice_settingslist.getItems().size() > 0) {
			choice_settingslist.getSelectionModel().select(0);
		}
	}

	private boolean checkItem() {
		if (textField_group_num.getText().equals("")) {
			AlertBoxFXUI.showMessageDialogError("错误", "请填写数据组-采样数量");
			return false;
		}
		if (textField_group_interval.getText().equals("")) {
			AlertBoxFXUI.showMessageDialogError("错误", "请填写数据组-间隔时间");
			return false;
		}
		return true;
	}

	/**
	 * 改变条目值
	 */
	private void changeItem() {
		textField_flag.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_FLAG));
		textArea_simple.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_SIMPLE));
		textField_regex.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_REGEX));
		textField_name.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_NAME));
		textField_group_num.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_NUM));
		for (Toggle toggle : toggleGroup_group.getToggles()) {
			if (toggle.getUserData().equals(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_TYPE))) {
				toggle.setSelected(true);
			}
		}
		textField_group_interval.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_GROUP_INTERVAL));
		textArea_timeperiod
				.setText(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_DRAWING_TIMEPERIOD).replace(";", ";\n"));
		checkBox_effective
				.setSelected(settingsMap.get(Cconfig.DATAHANDEL_SETTINGS_EFFECTIVE).equals("true") ? true : false);
	}

	/**
	 * 设置settingmap
	 */
	private void setSettingsMap() {
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_FLAG, textField_flag.getText());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_EFFECTIVE, checkBox_effective.isSelected() ? "true" : "false");
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_SIMPLE, textArea_simple.getText());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_REGEX, textField_regex.getText());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_NAME, textField_name.getText());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_GROUP_NUM, textField_group_num.getText());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_GROUP_TYPE,
				toggleGroup_group.getSelectedToggle().getUserData().toString());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_GROUP_INTERVAL, textField_group_interval.getText());
		settingsMap.put(Cconfig.DATAHANDEL_SETTINGS_DRAWING_TIMEPERIOD,
				textArea_timeperiod.getText().replace("\n", ""));
	}

	/**
	 * 验证测试样例
	 * 
	 * @return
	 */
	private String testSimple() {
		String[] seriesname = textField_name.getText().replace(";\n", ";").split(";");
		Matcher matcher = Pattern.compile(textField_regex.getText()).matcher(textArea_simple.getText());
		StringBuffer resultBuf = new StringBuffer();
		String time_value = null;
		if (matcher.find()) {
			resultBuf.append("请确认名称与值符合预期:\n");
			for (int i = 0; i < matcher.groupCount(); i++) {
				String value = matcher.group(i + 1);
				if (seriesname[i].equals(Cconfig.DATAHANDEL_SETTINGS_NAME_FLAG)) {
					resultBuf.append("名称=" + seriesname[i] + ",值=" + value + ",标识\n");
				} else if (seriesname[i].equals(Cconfig.DATAHANDEL_SETTINGS_NAME_INVALID)) {
					resultBuf.append("名称=" + seriesname[i] + ",值=" + value + ",无效值\n");
				} else if (seriesname[i].equals(Cconfig.DATAHANDEL_SETTINGS_NAME_TIME)) {
					resultBuf.append("名称=" + seriesname[i] + ",值=" + value + ",时间轴\n");
					time_value = value;
				} else {
					if (HelperUtil.isNumber(value) || value.matches("\\d+s\\d+")) {
						resultBuf.append("名称=" + seriesname[i] + ",值=" + value + ",采样值\n");
					} else {
						resultBuf.append("名称=" + seriesname[i] + ",值=" + value + ",采样异常值\n");
					}
				}
			}
		} else {
			resultBuf.append("ERROR=正则表达式无法匹配样例文本!");
		}
		resultBuf.append("\n");
		resultBuf.append(testTimeperoid(time_value));
		return resultBuf.toString();
	}

	/**
	 * 验证时间段
	 * 
	 * @return
	 */
	private String testTimeperoid(String timevalue) {
		StringBuffer resultBuf = new StringBuffer();
		String[] timeperoids = textArea_timeperiod.getText().replace("\n", "").split(";");
		String timeformat = timeperoids[0];
		if (!timeformat.equals("")) {// 填写有内容时检查
			if (timeformat.startsWith("!")) {
				resultBuf.append("!禁止以时间段绘制图表\n");
			}
			SimpleDateFormat df = null;
			try {
				df = new SimpleDateFormat(timeformat);
				resultBuf.append("时间格式=" + timeformat + "\n");
			} catch (Exception e) {
				// TODO: handle exception
				resultBuf.append("ERROR=时间格式错误:" + timeformat + "\n");
			}
			if (df != null && timevalue != null) {
				try {
					df.parse(timevalue);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					resultBuf.append("ERROR=时间格式与正则提取式提取的样例时间不符合:" + timevalue + "\n");
				}
			}
			for (int i = 1; i < timeperoids.length; i++) {
				String timeperoid = timeperoids[i];
				String[] times = timeperoid.split("/");
				if (times.length == 2) {
					String start = times[0];
					String end = times[1];
					try {
						df.parse(start);
						df.parse(end);
						resultBuf.append("时间段" + i + "=" + timeperoid + "\n");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						resultBuf.append("ERROR=时间段" + i + "与时间格式不符合:" + timeperoid + "\n");
					}
				} else {
					resultBuf.append("ERROR=时间段" + i + "格式错误:" + timeperoid + "\n");
				}
			}
		}
		return resultBuf.toString();
	}

	/**
	 * 确定存储
	 */
	private void updateItems() {
		settingsMapList = dataHandelXmlParse.refreshItemMapList();// 刷新
		choice_settingslist.getItems().clear();
		for (int i = 0; i < settingsMapList.size(); i++) {
			Map<String, String> map = settingsMapList.get(i);
			choice_settingslist.getItems().add((i + 1) + ":" + map.get(Cconfig.DATAHANDEL_SETTINGS_FLAG)
					+ (map.get(Cconfig.DATAHANDEL_SETTINGS_EFFECTIVE).equals("true") ? "[生效]" : ""));
		}
	}

	/**
	 * 获取配置表
	 * 
	 * @return
	 */
	public List<Map<String, String>> copySettingsMapList() {
		return dataHandelXmlParse.copyItemMapList();
	}

	/**
	 * 得到有效配置列表
	 * 
	 * @return
	 */
	public String getValidList() {
		StringBuffer validBuf = new StringBuffer();
		int count = 0;
		for (Map<String, String> map : settingsMapList) {
			if (map.get(Cconfig.DATAHANDEL_SETTINGS_EFFECTIVE).equals("true")) {
				count++;
				validBuf.append(count + "." + map.get(Cconfig.DATAHANDEL_SETTINGS_FLAG) + "\n");
			}
		}
		return validBuf.toString();
	}
}
