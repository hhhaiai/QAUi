package com.PicInspect;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class PicInspectMainController implements Initializable {
	Logger logger = LoggerFactory.getLogger(PicInspectMainController.class);
	@FXML
	AnchorPane anchorPane_Main;
	@FXML
	TextField tField_select_folder;
	@FXML
	Button btn_add, btn_select_floder, btn_pic_set, btn_set_ontop, btn_close_all;
	@FXML
	TextField tField_controller_serach, tField_pic_zoom, tField_pic_name_adjust;
	@FXML
	Button btn_controller_serach, btn_controller_next, btn_controller_previous, btn_controller_slide,
			btn_controller_scroll;
	@FXML
	VBox vbox_pic_list;
	@FXML
	ScrollPane scrollPane_pic_list;
	@FXML
	TitledPane titledPane_pic_list;// 图片展示面板
	@FXML
	Label lbl_inspect_info;// 查看器数量
	int numcount = 0;// 打开多少个窗口计数

	Map<Integer, InspectFXUI> stageMap = new HashMap<>();
	boolean isontop = false;
	boolean isslide = false;
	boolean isscroll = false;
	Timer slide_timer;// 幻灯片定时器
	PicInspectMainController picInspectMainController = this;
	ToggleGroup group_pic_list = new ToggleGroup();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initSettings();
		initController();
		initPicList();
		// 增加按钮
		btn_add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_add button");
				if (tField_pic_zoom.getText().equals("")) {
					AlertBoxFXUI.showMessageDialogError("错误", "请设置缩放百分比");
					return;
				}
				int zoom = Integer.parseInt(tField_pic_zoom.getText());
				if (zoom > 100 || zoom < 0) {
					AlertBoxFXUI.showMessageDialogError("缩放百分比不正确", "缩放百分比必须处于范围[1,100]");
					return;
				}
				File folder = new File(tField_select_folder.getText());
				if (folder.exists()) {
					numcount++;
					Map<String, Object> map = new HashMap<>();
					map.put(Cconfig.PICINSPECT_STAGE_NUM, numcount);
					map.put(Cconfig.PICINSPECT_STAGE_FOLDER, folder);
					map.put(Cconfig.PICINSPECT_STAGE_ZOOM, (double) zoom / 100);
					InspectFXUI inspectFXUI = new InspectFXUI(map, picInspectMainController);
					stageMap.put(numcount, inspectFXUI);// 先增加后初始化
					inspectFXUI.init();
					inspectFXUI.show();
					MainRun.xmlOperationUtil.XMLChanger("PicInspect_folder", folder.getAbsolutePath());
					MainRun.paramsBean.setPicInspect_folder(folder.getAbsolutePath());
					MainRun.xmlOperationUtil.XMLChanger("PicInspect_zoom", zoom + "");
					MainRun.paramsBean.setPicInspect_zoom(zoom + "");
				} else {
					AlertBoxFXUI.showMessageDialogError("请重新选择文件夹", "无此文件夹");
				}

			}
		});
	}

	/**
	 * 初始化图片展示列表
	 */
	private void initPicList() {
		titledPane_pic_list.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				logger.info("expanded titledPane_pic_list:" + newValue);
				Stage stage = (Stage) anchorPane_Main.getScene().getWindow();
				if (newValue) {// 展开
					stage.setHeight(stage.getHeight() + 300);
					titledPane_pic_list.setPrefHeight(300);
				} else {// 收起
					stage.setHeight(stage.getHeight() - 300);
					titledPane_pic_list.setPrefHeight(Region.USE_COMPUTED_SIZE);
				}
			}
		});
		group_pic_list.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				// TODO Auto-generated method stub
				logger.info("group_pic_list select from " + oldValue + " to " + newValue);
				if (newValue != null) {
					String[] userdata = ((String) newValue.getUserData()).split(",");
					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
						entry.getValue().showPic(userdata[0], true);
					}
//					double percent = 1D / newValue.getToggleGroup().getToggles().size() * Integer.parseInt(userdata[1]);
////						int no = Integer.parseInt(userdata[1]);
////						double percent = ((18 + 5) * no) / (vbox_pic_list.getHeight() - 5);
//					scrollPane_pic_list.setVvalue(percent);
//					System.out.println(percent + "," + vbox_pic_list.getHeight() + ","
//							+ scrollPane_pic_list.getViewportBounds().getHeight());

				}
			}
		});

	}

	/**
	 * 设置图片列表显示
	 * 
	 * @param optionList
	 */
	public void setPicList(List<File> optionList) {
		vbox_pic_list.getChildren().clear();
		group_pic_list.getToggles().clear();
		int count = 0;
		for (File option : optionList) {
			count++;
			RadioButton radioButton = new RadioButton(option.getName() + "," + count);
			radioButton.setToggleGroup(group_pic_list);
			radioButton.setUserData(option.getName() + "," + count);
//			radioButton.setOnAction(new EventHandler<ActionEvent>() {
//
//				@Override
//				public void handle(ActionEvent event) {
//					// TODO Auto-generated method stub
//					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
//						entry.getValue().showPic(option.getName(), true);
//					}
//					double percent = radioButton.getLayoutY() / vbox_pic_list.getHeight();
//					System.out.println("" + percent + "," + radioButton.getLayoutY() + "," + vbox_pic_list.getHeight());
//					scrollPane_pic_list.setVvalue(percent);
//				}
//			});
			vbox_pic_list.getChildren().add(radioButton);
		}
		if (group_pic_list.getToggles().size() > 0) {
			group_pic_list.getToggles().get(0).setSelected(true);// 默认第一个选中
		}
	}

	/**
	 * 设置非参照窗体显示及图片列表显示
	 * 
	 * @param name
	 */
	public void setNotReferenceStage(String name) {
		for (Toggle toggle : group_pic_list.getToggles()) {
			if (((String) toggle.getUserData()).split(",")[0].equals(name)) {
				toggle.setSelected(true);
				break;
			}
		}
		for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
			if (!entry.getValue().isReferenceStage()) {
				entry.getValue().showPic(name, true);
			}
		}
	}

	/**
	 * 初始化
	 */
	private void initSettings() {
		lbl_inspect_info.setText("已打开查看器数量:0\n主窗体序号:");
		// 文件夹选择
		tField_select_folder.setText(MainRun.paramsBean.getPicInspect_folder());
		btn_select_floder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_select_floder button");
				File file = selectFolder();
				if (file != null) {
					tField_select_folder.setText(file.getAbsolutePath());
				}
			}
		});
		// 缩放百分比
		tField_pic_zoom.setText(MainRun.paramsBean.getPicInspect_zoom());
		tField_pic_zoom.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals("") && !newValue.matches("[0-9]*")) {
				tField_pic_zoom.setText(oldValue);
			}
		});
		// 名称调整提示
		tField_pic_name_adjust.setTooltip(new Tooltip("格式为'beginIndex,endIndex',endIndex为负数则表示截取从beginIndex到字符串结束"));
		// 全部设置按钮
		btn_pic_set.setTooltip(new Tooltip("将所有查看器按照上方设置参数进行调整"));
		btn_pic_set.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_pic_set button");
				if (stageMap.size() == 0) {
					AlertBoxFXUI.showMessageDialogError("错误", "请先创建图片查看器");
					return;
				}
				if (tField_pic_zoom.getText().equals("")) {
					AlertBoxFXUI.showMessageDialogError("错误", "请设置缩放百分比");
					return;
				}
				int zoom = Integer.parseInt(tField_pic_zoom.getText());
				if (zoom > 100 || zoom < 0) {
					AlertBoxFXUI.showMessageDialogError("缩放百分比不正确", "缩放百分比必须处于范围[1,100]");
					return;
				}
				String adjustname = tField_pic_name_adjust.getText();
				if (!adjustname.equals("") && !adjustname.matches("\\d+(,\\d+)?")) {
					AlertBoxFXUI.showMessageDialogError("名称调整格式错误",
							"格式为'beginIndex,endIndex',endIndex为负数则表示截取从beginIndex到字符串结束");
					return;
				}
				for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
					entry.getValue().setZoomSize((double) zoom / 100);
				}
				for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
					entry.getValue().setPicNameAdjust(adjustname);
				}
			}
		});
		btn_set_ontop.setTooltip(new Tooltip("将所有查看器置顶,显示在最前面"));
		btn_set_ontop.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_set_ontop button");
				if (stageMap.size() == 0) {
					AlertBoxFXUI.showMessageDialogError("错误", "请先创建图片查看器");
					return;
				}
				if (!isontop) {
					isontop = true;
					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
						entry.getValue().setOnTop(true);
					}
					btn_set_ontop.setText("取消置顶");
				} else {
					isontop = false;
					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
						entry.getValue().setOnTop(false);
					}
					btn_set_ontop.setText("置顶");
				}
			}
		});
		btn_close_all.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_close_all button");
				if (stageMap.size() == 0) {
					AlertBoxFXUI.showMessageDialogError("错误", "请先创建图片查看器");
					return;
				}
				// 置顶还原
				isontop = true;
				btn_set_ontop.setText("置顶");
				// 关闭
				for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
					entry.getValue().closeWindows();
				}
				lbl_inspect_info.setText("已打开查看器数量:0\n主窗体序号:");
				stageMap.clear();
				vbox_pic_list.getChildren().clear();
				titledPane_pic_list.setExpanded(false);
			}
		});
	}

	/**
	 * 初始化窗体控制器
	 */
	private void initController() {
		tField_controller_serach.setTooltip(new Tooltip("如果想直接搜索第XX张图片,请输入#XX"));
		btn_controller_serach.setTooltip(new Tooltip("如果想直接搜索第XX张图片,请输入#XX"));
		btn_controller_serach.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_controller_serach button");
				String serachkey = tField_controller_serach.getText();
				InspectFXUI inspectFXUI = getReferenceStage();
				if (inspectFXUI == null) {
					AlertBoxFXUI.showMessageDialogError("错误", "请先创建图片查看器");
					return;
				}
				inspectFXUI.showPicBySerach(serachkey);
				setNotReferenceStage(inspectFXUI.getShowName());
			}
		});

		btn_controller_next.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_controller_next button");
				InspectFXUI inspectFXUI = getReferenceStage();
				if (inspectFXUI == null) {
					AlertBoxFXUI.showMessageDialogError("错误", "请先创建图片查看器");
					return;
				}
				inspectFXUI.showPicNext();
				setNotReferenceStage(inspectFXUI.getShowName());
			}
		});

		btn_controller_previous.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_controller_previous button");
				InspectFXUI inspectFXUI = getReferenceStage();
				if (inspectFXUI == null) {
					AlertBoxFXUI.showMessageDialogError("错误", "请先创建图片查看器");
					return;
				}
				inspectFXUI.showPicPrevious();
				setNotReferenceStage(inspectFXUI.getShowName());
			}
		});
		btn_controller_slide.setTooltip(new Tooltip("左键点击播放暂停幻灯片,默认间隔800ms,右键点击自定义间隔时间播放幻灯片"));
		btn_controller_slide.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if (!isslide) {
					int time = 800;
					if (event.getButton() == MouseButton.PRIMARY) {// 左键
						logger.info("click btn_controller_slide left button");
					} else if (event.getButton() == MouseButton.SECONDARY) {// 右键
						logger.info("click btn_controller_slide right button");
						String input = AlertBoxFXUI.showInputDialog("请输入,单位毫秒", "幻灯片播放间隔时间,默认800毫秒,范围[300,60000]",
								AlertBoxFXUI.showInputDialog_Integer_Positive);
						if (input == null) {
							return;
						} else {
							time = Integer.parseInt(input);
						}
						if (time < 300 || time > 60000) {
							AlertBoxFXUI.showMessageDialogError("请输入范围[300,60000]", "幻灯片播放间隔时间,默认800毫秒,范围[300,60000]");
							return;
						}
					}
					isslide = true;
					btn_controller_slide.setText("暂停播放");
					slide_timer = new Timer();
					slide_timer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							InspectFXUI inspectFXUI = getReferenceStage();
							if (inspectFXUI != null) {
								inspectFXUI.showPicNext();
								setNotReferenceStage(inspectFXUI.getShowName());
							}
						}
					}, 100, time);
				} else {
					isslide = false;
					btn_controller_slide.setText("幻灯片");
					cancelSlideTimer();
				}

			}
		});
		btn_controller_scroll.setTooltip(new Tooltip("打开后,可以使用滚动滚动翻动图片"));
		btn_controller_scroll.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press button");
				if (!isscroll) {
					isscroll = true;
					btn_controller_scroll.setText("取消滚轮");
				} else {
					isscroll = false;
					btn_controller_scroll.setText("滚轮查看");
				}
			}
		});
		anchorPane_Main.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				// TODO Auto-generated method stub
				if (isscroll) {
					int y = (int) event.getDeltaY();
					InspectFXUI inspectFXUI = getReferenceStage();
					if (inspectFXUI != null) {
						if (MainRun.OStype == Cconfig.WINDOWS) {// windows 40一级
							y = y / 40;
						} else if (MainRun.OStype == Cconfig.MAC) {

						}
						if (y > 3) {
							y = 3;
						} else if (y < -3) {
							y = -3;
						}
						if (y < 0) {
							inspectFXUI.showPicNext(Math.abs(y), false);
						} else if (y > 0) {
							inspectFXUI.showPicPrevious(Math.abs(y), false);
						} else {
							inspectFXUI.showPicNext(1, false);
						}
						logger.info("all mouse scroll event:" + (int) event.getDeltaY());
						setNotReferenceStage(inspectFXUI.getShowName());
					}
				}
			}
		});
	}

	/**
	 * 获取参照窗体
	 * 
	 * @return
	 */
	private InspectFXUI getReferenceStage() {
		for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
			if (entry.getValue().isReferenceStage()) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 选择图片文件夹
	 * 
	 * @return
	 */
	private File selectFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("选择文件夹");
		File initfile = new File(MainRun.QALogfile);
		if (!initfile.exists()) {
			initfile.mkdirs();
		}
		directoryChooser.setInitialDirectory(initfile);
		File file = directoryChooser.showDialog(null);
		return file;
	}

	/**
	 * 获取查看器窗体map
	 * 
	 * @return
	 */
	public Map<Integer, InspectFXUI> getStageMap() {
		return stageMap;
	}

	/**
	 * 获取查看器控件
	 */
	public Label getLblInspectInfo() {
		return lbl_inspect_info;
	}

	/**
	 * 取消幻灯片线程
	 */
	public void cancelSlideTimer() {
		if (slide_timer != null) {
			slide_timer.cancel();
		}
	}

}
