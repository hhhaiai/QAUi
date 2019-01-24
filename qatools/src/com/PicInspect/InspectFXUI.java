package com.PicInspect;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CMDUtil;
import com.Util.HelperUtil;
import com.Viewer.MainRun;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.general.AlertBoxFXUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class InspectFXUI extends Stage {
	Logger logger = LoggerFactory.getLogger(InspectFXUI.class);
	int num;
	File folder;
	File[] files_pic;
	File file_show = null;
	int picNo = 0;
	ImageView imageView = new ImageView();
	Map<String, Object> configMap;
	Map<Integer, InspectFXUI> stageMap;
	Label lbl_file_name;// 图片名称
	Label lbl_pic_info, lbl_win_size;
	double zoom = 1;
	int adjust_name_begin = -1;
	int adjust_name_end = -1;

	int info_height = 200;
	boolean isontop = false;// 是否置顶
	boolean isreference = false;// 是否主窗体
	Button btn_set_reference;
	Button btn_ontop;
	PicInspectMainController picInspectMainController;

	public InspectFXUI(Map<String, Object> configMap, PicInspectMainController picInspectMainController) {
		// TODO Auto-generated constructor stub
		this.picInspectMainController = picInspectMainController;
		this.configMap = configMap;
		this.stageMap = picInspectMainController.getStageMap();
		num = (int) configMap.get(Cconfig.PICINSPECT_STAGE_NUM);
		folder = (File) configMap.get(Cconfig.PICINSPECT_STAGE_FOLDER);
		zoom = (double) configMap.get(Cconfig.PICINSPECT_STAGE_ZOOM);
//		int width = (int) configMap.get(Cconfig.PICINSPECT_STAGE_WIDTH);
//		int height = (int) configMap.get(Cconfig.PICINSPECT_STAGE_HEIGHT);
		// setResizable(false);
		// UI
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		lbl_file_name = new Label();
		lbl_file_name.setFont(new Font(18));

		HBox hBox_serach = new HBox();
		hBox_serach.setSpacing(5);
		TextField tField_serach = new TextField();
		tField_serach.setTooltip(new Tooltip("如果想直接搜索第XX张图片,请输入#XX"));
		Button btn_serach = new Button("搜索");
		btn_serach.setTooltip(new Tooltip("如果想直接搜索第XX张图片,请输入#XX"));
		btn_serach.setEllipsisString("搜");
		Button btn_previous = new Button("上一张");
		btn_previous.setEllipsisString("↑");
		Button btn_next = new Button("下一张");
		btn_next.setEllipsisString("↓");
		hBox_serach.getChildren().addAll(tField_serach, btn_serach, btn_previous, btn_next);
		lbl_win_size = new Label("图片显示分辨率");
		lbl_pic_info = new Label("图片分辨率,大小");

		HBox hBox_settings = new HBox();
		hBox_settings.setSpacing(5);
		btn_ontop = new Button("置顶");
		btn_ontop.setTooltip(new Tooltip("将本窗体放在最前面"));
		btn_set_reference = new Button("设置为主窗体");
		btn_set_reference.setTooltip(new Tooltip("将本窗体设置为参考,其它窗体图片跟随主窗体改变"));
		Label lbl_note_image = new Label("在图片上左键复制路径,右键打开文件夹");
		hBox_settings.getChildren().addAll(lbl_note_image, btn_set_reference, btn_ontop);
		vBox.getChildren().addAll(imageView, lbl_file_name, lbl_pic_info, lbl_win_size, hBox_serach, hBox_settings);
		setScene(new Scene(vBox));
		// 滚动
		vBox.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				// TODO Auto-generated method stub
				int y = (int) event.getDeltaY();
				if (MainRun.OStype == Cconfig.WINDOWS) {// windows 40一级
					y = y / 40;
				} else if (MainRun.OStype == Cconfig.MAC) {

				}
				if (y > 2) {
					y = 2;
				} else if (y < -2) {
					y = -2;
				}
				if (y < 0) {
					showPicNext(Math.abs(y), false);
				} else if (y > 0) {
					showPicPrevious(Math.abs(y), false);
				} else {
					showPicNext(1, false);
				}
				logger.info("mouse scroll event:" + (int) event.getDeltaY());
			}
		});
		// 鼠标监听
		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if (event.getButton() == MouseButton.SECONDARY) {// 右键
					logger.info("click imageView right button");
					if (MainRun.OStype == Cconfig.WINDOWS) {
						CMDUtil.execcmd("explorer " + folder.getAbsolutePath(), CAndroidCMD.SYSCMD, true);
					} else if (MainRun.OStype == Cconfig.MAC) {
						CMDUtil.execcmd("open " + folder.getAbsolutePath(), CAndroidCMD.SYSCMD, true);
					} else {

					}
				} else if (event.getButton() == MouseButton.PRIMARY) {
					logger.info("click imageView left button");
					if (file_show != null) {
						HelperUtil.setSysClipboardText(file_show.getAbsolutePath());
					}
				}
			}
		});
		// 按照窗体改变
		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				imageView.setFitWidth(newValue.doubleValue());
				lbl_win_size.setText("共" + files_pic.length + "张图片,当前显示分辨率:" + newValue + "X" + getHeight());
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				imageView.setFitHeight(newValue.doubleValue() - info_height);
				lbl_win_size.setText("共" + files_pic.length + "张图片,当前显示分辨率:" + getWidth() + "X"
						+ (newValue.doubleValue() - info_height));
			}
		});

		// 窗体关闭
		setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				logger.info("inspectui num=" + num + ",closed");
				stageMap.remove(num);
				if (stageMap.size() > 0 && isReferenceStage()) {// 自动选择序号最小的为主窗体
					int min = Integer.MAX_VALUE;
					int temp = -1;
					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
						temp = entry.getKey();
						if (temp < min) {
							min = temp;
						}
					}
					if (min != Integer.MAX_VALUE && min >= 0) {
						stageMap.get(min).setReference(true);
					}
				}
				setInspectInfo();
				logger.info("left windows=" + stageMap.size());
			}
		});
		btn_serach.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_serach button");
				showPicBySerach(tField_serach.getText());
			}
		});
		btn_previous.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_previous button");
				showPicPrevious();
			}
		});
		btn_next.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_next button");
				showPicNext();
			}
		});
		// 设置
		btn_set_reference.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_set_reference button");
				if (!isreference) {
					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {// 先设置一个窗体为true
						if (entry.getKey() == num) {
							entry.getValue().setReference(true);
						}
					}
					for (Entry<Integer, InspectFXUI> entry : stageMap.entrySet()) {
						if (entry.getKey() != num) {
							entry.getValue().setReference(false);
						}
					}
				}
			}
		});
		btn_ontop.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				logger.info("press btn_ontop button");
				if (!isontop) {
					setOnTop(true);
				} else {
					setOnTop(false);
				}
			}
		});
	}

	/**
	 * 初始化
	 */
	public void init() { // init
		files_pic = getFiles();
		if (isReferenceStage()) {
			logger.info("this stage is the reference stage");
			picNo = 0;
			showPicCore(picNo, null, false);// 默认第一张
			btn_set_reference.setText("主窗体");
			setReference(true);
		} else {
			showPic(getReferenceStage().getShowName(), false);
			btn_set_reference.setText("设置为主窗体");
			setReference(false);
		}
		setTitle("序号=" + num + ",文件夹名=" + folder.getName());
		setZoomSize(zoom);
	}

	/**
	 * 设置是否为主窗体
	 * 
	 * @param isreference
	 */
	public void setReference(boolean isreference) {
		this.isreference = isreference;
		if (isreference) {
			btn_set_reference.setText("主窗体");
			picInspectMainController.setPicList(getFileList());
		} else {
			btn_set_reference.setText("设置为主窗体");
		}
		setInspectInfo();
	}

	/**
	 * 设置是否置顶
	 * 
	 * @param isontop
	 */
	public void setOnTop(boolean isontop) {
		this.isontop = isontop;
		if (isontop) {
			setAlwaysOnTop(true);
			btn_ontop.setText("取消置顶");
		} else {
			setAlwaysOnTop(false);
			btn_ontop.setText("置顶");
		}
	}

	/**
	 * 关闭窗体
	 */
	public void closeWindows() {
		this.close();// 不会触发setoncloserequest
	}

	/**
	 * 设置缩放大小
	 * 
	 * @param zoom
	 */
	public void setZoomSize(double zoom) {
		double width = 480;
		double height = 854;
		if (file_show != null) {
			width = imageView.getImage().getWidth();
			height = imageView.getImage().getHeight();
			lbl_pic_info.setText("图片分辨率:" + width + "X" + height + ",大小:" + file_show.length() / 1024 + "KB");
		}
		setWidth(width * zoom);
		setHeight(height * zoom + info_height);
		imageView.setFitWidth(width * zoom);
		imageView.setFitHeight(height * zoom);
	}

	public void setPicNameAdjust(String substring) {
		// 1,5
		if (!substring.equals("")) {
			if (substring.contains(",")) {
				adjust_name_begin = Integer.parseInt(substring.split(",")[0]);
				adjust_name_end = Integer.parseInt(substring.split(",")[1]);
			} else {
				adjust_name_begin = Integer.parseInt(substring.split(",")[0]);
				adjust_name_end = -1;
			}
		} else {
			adjust_name_begin = -1;
			adjust_name_end = -1;
		}

	}

	/**
	 * 截取字符串
	 * 
	 * @param name
	 * @param begin
	 * @param end
	 * @return
	 */
	private String adjustName(String name) {
		try {
			if (adjust_name_begin <= -1) {
				return name;
			}
			if (adjust_name_end <= -1) {
				return name.substring(adjust_name_begin);
			} else {
				return name.substring(adjust_name_begin, adjust_name_end);
			}
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			logger.error("Exception", e);
		}
		return name;
	}

	/**
	 * 下一张
	 * 
	 * @return
	 */
	public int showPicNext() {
		picNo++;
		if (picNo >= files_pic.length) {
			picNo = 0;
		}
		return showPicCore(picNo, null, true);
	}

	/**
	 * 下add张
	 * 
	 * @param add
	 * @return
	 */
	public int showPicNext(int add, boolean restart) {
		picNo += add;
		if (picNo >= files_pic.length) {
			if (restart) {
				picNo = picNo % files_pic.length;
			} else {
				picNo = files_pic.length - 1;
			}
		}
		return showPicCore(picNo, null, true);
	}

	/**
	 * 上一张
	 * 
	 * @return
	 */
	public int showPicPrevious() {
		picNo--;
		if (picNo < 0) {
			picNo = files_pic.length - 1;
		}
		return showPicCore(picNo, null, true);
	}

	/**
	 * 上reduce涨
	 * 
	 * @param reduce
	 * @return
	 */
	public int showPicPrevious(int reduce, boolean restart) {
		picNo -= reduce;
		if (picNo < 0) {
			if (restart) {
				picNo = picNo % files_pic.length + files_pic.length;
			} else {
				picNo = 0;
			}
		}
		return showPicCore(picNo, null, true);
	}

	/**
	 * 显示下标为no的图片,核心
	 * 
	 * @param No
	 * @return
	 */
	private int showPicCore(int No, String name, boolean newthread) {
		String imagepath;
		if (files_pic.length > No && No >= 0) {
			file_show = files_pic[No];
			logger.info("picNo=" + No + ",show pic:" + file_show.getAbsolutePath());
			imagepath = "file:" + file_show.getAbsolutePath();
		} else {
			file_show = null;
			logger.info("show pic error, no file found!folder=" + folder.getAbsolutePath());
			imagepath = "Resources/logo.jpg";
		}
		if (newthread) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Image image = new Image(imagepath);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (file_show != null) {
								lbl_file_name.setTextFill(Color.BLUE);
								lbl_file_name.setText("第" + (No + 1) + "张:" + file_show.getName());
								lbl_pic_info.setText("图片分辨率:" + image.getWidth() + "X" + image.getHeight() + ",大小:"
										+ file_show.length() / 1024 + "KB");
							} else {
								lbl_file_name.setTextFill(Color.RED);
								if (name != null && !name.equals("")) {
									lbl_file_name.setText("未找到图片:" + name);
								} else {
									lbl_file_name.setText("未找到该图片!");
								}
								lbl_pic_info.setText("");
							}
							imageView.setImage(image);
						}
					});
				}
			}).start();
		} else {
			Image image = new Image(imagepath);
			if (file_show != null) {
				lbl_file_name.setTextFill(Color.BLUE);
				lbl_file_name.setText("第" + (No + 1) + "张:" + file_show.getName());
				lbl_pic_info.setText("图片分辨率:" + image.getWidth() + "X" + image.getHeight() + ",大小:"
						+ file_show.length() / 1024 + "KB");
			} else {
				lbl_file_name.setTextFill(Color.RED);
				if (name != null && !name.equals("")) {
					lbl_file_name.setText("未找到图片:" + name);
				} else {
					lbl_file_name.setText("未找到该图片!");
				}
				lbl_pic_info.setText("");
			}
			imageView.setImage(image);
		}
		return No;
	}

	/**
	 * 显示名称为name的图片
	 * 
	 * @param name
	 * @return
	 */
	public int showPic(String name, boolean newthread) {
		int count = 0;
		for (File file : files_pic) {
			if (adjustName(file.getName()).equals(adjustName(name))) {
				break;
			}
			count++;
		}
		picNo = count;
		return showPicCore(picNo, name, newthread);
	}

	/**
	 * 搜索图片并显示
	 * 
	 * @param serachkey
	 * @return
	 */
	public int showPicBySerach(String serachkey) {
		logger.info("show pic by serach key=" + serachkey);
		if (serachkey.startsWith("#") && serachkey.length() > 1
				&& HelperUtil.isInteger(serachkey.substring(1, serachkey.length()))) {
			return showPicCore(Integer.parseInt(serachkey.substring(1, serachkey.length())) - 1, null, true);
		} else {
			List<String> filesList = new ArrayList<>();
			int count = 0;
			for (File file : files_pic) {
				count++;
				if (file.getName().contains(serachkey)) {
					filesList.add(file.getName() + "," + count);
				}
			}
			if (filesList.size() != 0) {
				String name = AlertBoxFXUI.showOptionDialog("请选择要显示的图片名称", "共搜索到" + filesList.size() + "张图片",
						filesList);
				if (name != null) {
					return showPic(name.split(",")[0], true);
				}
			} else {
				AlertBoxFXUI.showMessageDialog("未搜索到",
						"在" + folder.getAbsolutePath() + "下未搜索到包含\"" + serachkey + "\"的图片");
			}
		}
		return picNo;
	}

	/**
	 * 根据文件夹得到图片组
	 * 
	 * @return
	 */
	private File[] getFiles() {
		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				String name = file.getName().toLowerCase();
				return name.endsWith(".png") || name.endsWith(".jpg");
			}
		});

		return files;
	}

	/**
	 * 判断是否是num最小的窗体
	 * 
	 * @return
	 */
	public boolean isReferenceStage() {
		if (stageMap.size() == 1) {
			isreference = true;
		}
		return isreference;
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
	 * 设置图片查看器信息显示
	 */
	public void setInspectInfo() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("已打开查看器数量:" + stageMap.size() + "\n");
		InspectFXUI inspectFXUI = getReferenceStage();
		if (inspectFXUI != null) {
			stringBuffer.append("主窗体序号:" + inspectFXUI.num);
		} else {
			stringBuffer.append("主窗体序号:");
		}
		picInspectMainController.getLblInspectInfo().setText(stringBuffer.toString());
	}

	/**
	 * 获取configmap
	 * 
	 * @return
	 */
	public Map<String, Object> getConfigMap() {
		return configMap;
	}

	/**
	 * 获取当前显示图片名称
	 * 
	 * @return ""则无图像显示
	 */
	public String getShowName() {
		return file_show != null ? file_show.getName() : "";
	}

	/**
	 * 获取文件列表
	 * 
	 * @return
	 */
	public List<File> getFileList() {
		return Arrays.asList(files_pic);
	}
}
