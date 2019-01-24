package com.DataHandel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.HelperUtil;
import com.Util.TimeUtil;
import com.Viewer.MainRun;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;
import com.general.TextBoxFXUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DataHandelMainController implements Initializable {
	Logger logger = LoggerFactory.getLogger(DataHandelMainFXUI.class);
	@FXML
	SplitPane splitPaneMain;
	@FXML
	AnchorPane anchorPane_settings;
	@FXML
	VBox vbox_chart;
	// 辅助界面
	@FXML
	Button btn_statistics_show, btn_save_log, btn_expand_charts, btn_search;
	@FXML
	Label lbl_settings_valid;
	@FXML
	Button btn_settings;
	@FXML
	TextField textField_search;
	// 监控界面
	@FXML
	Button btn_monitor_start, btn_monitor_ios_settings;
	// 绘图界面
	@FXML
	Button btn_drawing_select_file, btn_drawing_draw;
	@FXML
	TextField textField_drawing_file_path;

	String udid = "";
	String deviceOS = "";
	LogMonitor logMonitor;
	FileHandel fileHandel;
	boolean isstartmonitor = false;
	DataHandelXmlParse dataHandelXmlParse;

	boolean isstatistics = false;
	boolean issavelog = false;
	StatisticsBoxFXUI statisticsBoxFXUI;// 数据显示窗体
	SettingsBoxFXUI settingsBoxFXUI = new SettingsBoxFXUI();
	boolean isexpanded = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initAssist();
		initMonitor();
		initDrawing();
	}

	/**
	 * 初始化绘图界面
	 */
	private void initDrawing() {
		textField_drawing_file_path.setText(MainRun.paramsBean.getDataHandel_filepath());
		btn_drawing_select_file.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_drawing_select_file button");
				File file = selectDrawingFile();
				if (file != null) {
					textField_drawing_file_path.setText(file.getAbsolutePath());
				}
			}
		});
		btn_drawing_draw.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_drawing_draw button");
				File file = new File(textField_drawing_file_path.getText());
				if (file.isFile() && file.exists()) {
					MainRun.paramsBean.setDataHandel_filepath(file.getAbsolutePath());
					MainRun.xmlOperationUtil.XMLChanger("DataHandel_filepath", file.getAbsolutePath());
					statisticsBoxFXUI.setType(Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING);
					fileHandel.start(settingsBoxFXUI.copySettingsMapList(), file);
					lbl_settings_valid.setText("绘图\n" + settingsBoxFXUI.getValidList());
					isstartmonitor = false;
					logMonitor.getDataFilter().clear();
				} else {
					AlertBoxFXUI.showMessageDialogError("日志文件不存在", "请输入正确的日志文件路径");
				}
			}
		});
	}

	/**
	 * 初始化统计界面
	 */
	private void initAssist() {
		btn_statistics_show.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_statistics_show button");
				statisticsBoxFXUI.setAlwaysOnTop(true);
				statisticsBoxFXUI.show();
				statisticsBoxFXUI.setAlwaysOnTop(false);
			}
		});
		btn_save_log.setTooltip(new Tooltip("保存符合配置列表的数据"));
		btn_save_log.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_save_log button");
				if (!issavelog) {
					File file = selectFileByTXT();
					if (file != null) {
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								logger.error("EXCEPTION", e);
							}
						}
						logMonitor.setSaveLog(file);
						fileHandel.setSaveLog(file);
					} else {
						return;
					}
					issavelog = true;
					btn_save_log.setText("停止保存");
				} else {
					logMonitor.setSaveLog(null);
					fileHandel.setSaveLog(null);
					issavelog = false;
					btn_save_log.setText("保存日志");
				}
			}
		});
		btn_settings.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_settings button");
				settingsBoxFXUI.setAlwaysOnTop(true);
				settingsBoxFXUI.show();
				settingsBoxFXUI.setAlwaysOnTop(false);
			}
		});
		btn_expand_charts.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_expand_charts button");
				if (!isexpanded) {
					isexpanded = true;
					btn_expand_charts.setText("收起图表");
				} else {
					isexpanded = false;
					btn_expand_charts.setText("展开图表");
				}
				fileHandel.getDataFilter().setExpanded(isexpanded);
				logMonitor.getDataFilter().setExpanded(isexpanded);
			}
		});
		btn_search.setTooltip(new Tooltip("搜索指定图表标题并展开,然后收起其它图表"));
		btn_search.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_search button");
				if (vbox_chart.getChildren().size() > 0 && fileHandel.getDataFilter().titledPanesList.size() > 0) {
					fileHandel.getDataFilter().searchTitledPane(textField_search.getText());
				} else if (vbox_chart.getChildren().size() > 0
						&& logMonitor.getDataFilter().titledPanesList.size() > 0) {
					logMonitor.getDataFilter().searchTitledPane(textField_search.getText());
				} else {
					AlertBoxFXUI.showMessageDialog("请先创建图表", "无图表搜索");
				}
			}
		});
	}

	/**
	 * 初始化监控界面
	 */
	private void initMonitor() {
		btn_monitor_start.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_monitor_start button");
				if (!isstartmonitor) {
					if (!CheckUE.checkDevice(udid)) {
						AlertBoxFXUI.showMessageDialogError("请插入设备!", "未检测到设备" + udid);
						return;
					}
					isstartmonitor = true;
					btn_monitor_start.setText("停止监控");
					statisticsBoxFXUI.setType(Cconfig.DATAHANDEL_SETTINGS_TYPE_MONITOR);
					logMonitor.start(settingsBoxFXUI.copySettingsMapList());
					lbl_settings_valid.setText("监控\n" + settingsBoxFXUI.getValidList());
					btn_drawing_draw.setDisable(true);
					fileHandel.getDataFilter().clear();
				} else {
					isstartmonitor = false;
					btn_monitor_start.setText("开始监控");
					logMonitor.stop();
					btn_drawing_draw.setDisable(false);
				}
			}
		});
		btn_monitor_ios_settings.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_monitor_ios_settings button");
				TextBoxFXUI textBoxFXUI = new TextBoxFXUI("iOS包名与日志路径设置", "格式:包名,沙盒日志路径,例如:com.xxx.xxx,/",
						MainRun.paramsBean.getIOS_Logs_App_path().replace(";", ";\n"), 300, 300) {

					@Override
					protected boolean confirmButton() {
						// TODO Auto-generated method stub
						// 判断格式是否正确
						if (!HelperUtil.check_format(getText())) {
							AlertBoxFXUI.showMessageDialogError("错误", "请按照指定格式输入文本!");
							return false;
						}
						// 存储
						MainRun.paramsBean.setIOS_Logs_App_path(getText().replaceAll("\n", ""));
						MainRun.xmlOperationUtil.XMLChanger("IOS_Logs_App_path", getText().replaceAll("\n", ""));
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
	}

	/**
	 * 设置设备udid
	 * 
	 * @param udid
	 */
	public void init(String udid, String deviceOS) {
		this.udid = udid;
		this.deviceOS = deviceOS;
		logMonitor = new LogMonitor(udid, deviceOS, vbox_chart);
		fileHandel = new FileHandel(vbox_chart);
		statisticsBoxFXUI = new StatisticsBoxFXUI(logMonitor, fileHandel);
		Stage stage = (Stage) splitPaneMain.getScene().getWindow();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {// 关闭UI时自动关闭监控

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				logger.info("close stage datahandel ui");
				logMonitor.stop();
			}
		});
	}

	/**
	 * 选择日志文件
	 * 
	 * @return
	 */
	private File selectDrawingFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择日志文件");
		File initfile = new File(MainRun.QALogfile + "/DataRecord");
		if (!initfile.exists()) {
			initfile.mkdirs();
		}
		fileChooser.setInitialDirectory(initfile);
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"),
				new FileChooser.ExtensionFilter("LOG", "*.log"));
		File file = fileChooser.showOpenDialog(null);
		return file;
	}

	/**
	 * 选择文本保存位置
	 * 
	 * @return
	 */
	private File selectFileByTXT() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择数据文本保存位置");
		File initfile = new File(MainRun.QALogfile + "/DataRecord");
		if (!initfile.exists()) {
			initfile.mkdirs();
		}
		fileChooser.setInitialDirectory(initfile);
		fileChooser.setInitialFileName("datahandel_" + TimeUtil.getTime4File() + ".txt");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
		File file = fileChooser.showSaveDialog(null);
		return file;
	}
}
