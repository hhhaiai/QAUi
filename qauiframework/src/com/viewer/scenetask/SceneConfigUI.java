package com.viewer.scenetask;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.TestCaseBean;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.AndroidInfo;
import com.helper.IOSInfo;
import com.task.FactoryScene;
import com.viewer.main.CheckPC;
import com.viewer.main.MainRun;
import com.viewer.wechat.MemberListUI;

import javafx.application.Platform;

public class SceneConfigUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2941505332308272535L;
	Logger logger = LoggerFactory.getLogger(SceneConfigUI.class);
	Map<String, Object> config;
	Map<String, String> capabilityMap;
	Map<TestCaseBean, Boolean> caserunMap;// 执行case信息
	String udid;
	String version = "";
	String deviceName = "";
	JComboBox<String> comboBox_initdriver;
	JComboBox<String> comboBox_scene;
	JComboBox<String> comboBox_screenshot;
	JComboBox<String> comboBox_syscrash;
	JComboBox<String> comboBox_appcrash;
	JComboBox<String> comboBox_email_send;
	JComboBox<String> comboBox_appid;
	JComboBox<String> comboBox_setdevice;
	JComboBox<String> comboBox_wechat_send;

	JFormattedTextField TF_appiumServerUrl;
	JFormattedTextField TF_params;
	JFormattedTextField TF_note;
	JFormattedTextField TF_appspath;
	JFormattedTextField TF_idevicesyslogtag;
	JFormattedTextField TF_email_to;
	JFormattedTextField TF_email_cc;
	JFormattedTextField TF_userlogcatch;

	// JFormattedTextField TF_caseruninfo;
	JLabel lbl_udid_value;
	JLabel lbl_version_value;
	JLabel lbl_desc_value;
	// 布局
	GridBox gridBox = new GridBox();

	String deviceOS;
	String wechat_people_list;

	/**
	 * Create the panel.
	 */
	public SceneConfigUI(String udid, String deviceOS) {
		setSize(450, 450);
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		this.udid = udid;
		this.deviceOS = deviceOS;
		config = new HashMap<>();// 指向不同内存地址
		caserunMap = new LinkedHashMap<>();// 执行case信息
		/**
		 * 系统区分点
		 */
		if (deviceOS.equals(Cconfig.ANDROID)) {
			this.version = AndroidInfo.getVersion(udid);
			this.deviceName = udid;
			MainRun.androidConfigBean.getScene().entrySet().forEach(i -> config.put(i.getKey(), i.getValue()));
		} else {
			this.deviceName = IOSInfo.getProduct(udid);
			this.version = IOSInfo.getVersion(udid);
			MainRun.iosConfigBean.getScene().entrySet().forEach(i -> config.put(i.getKey(), i.getValue()));
		}
		gridBox.setInsets(0, 0, 2, 2);

		JLabel lbl_scene = new JLabel("场景");
		add(lbl_scene, gridBox.resetGridX().autoGridY());

		comboBox_scene = new JComboBox<>();
		comboBox_scene.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) { // 选中
					setValueByScene((String) e.getItem());
				} else {

				}
			}
		});
		add(comboBox_scene, gridBox.autoGridX());

		JLabel lbl_desc = new JLabel("描述");
		add(lbl_desc, gridBox.resetGridX().autoGridY());

		lbl_desc_value = new JLabel("");
		add(lbl_desc_value, gridBox.autoGridX());

		// ************************
		JLabel lbl_config_title = new JLabel("测试配置");
		lbl_config_title.setForeground(Color.BLUE);
		add(lbl_config_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_caseruninfo = new JLabel("执行用例配置");
		add(lbl_caseruninfo, gridBox.resetGridX().autoGridY());

		JButton btn_caseruninfo = new JButton("设置");
		btn_caseruninfo.addActionListener(e -> {
			logger.info("press btn_caseruninfo button");
			// CaseListBoxUI caseListBoxUI=new
			// CaseListBoxUI(caserunMap,(String)comboBox_scene.getSelectedItem());
			// caseListBoxUI.setVisible(true);
			CaseListBoxUI_Jtable caseListBoxUI_Jtable = new CaseListBoxUI_Jtable(caserunMap,
					(String) comboBox_scene.getSelectedItem());
			caseListBoxUI_Jtable.setVisible(true);

		});
		add(btn_caseruninfo, gridBox.autoGridX());
		// TF_caseruninfo = new JFormattedTextField();
		// TF_caseruninfo.setToolTipText("需要执行的用例,空则全部执行.PS:1,3,6");
		// add(TF_caseruninfo,gridBox.autoGridX());

		JLabel lbl_packages = new JLabel("安装包");
		add(lbl_packages, gridBox.resetGridX().autoGridY());

		JButton btn_apps = new JButton("选择");
		btn_apps.addActionListener(e -> {
			logger.info("press btn_apps button");
			File file = selectFloderAndFiles();
			if (file != null)
				TF_appspath.setText(file.getAbsolutePath());
		});
		add(btn_apps, gridBox.autoGridX());

		TF_appspath = new JFormattedTextField();
		TF_appspath.setToolTipText("安装包地址文件或目录,如果是目录则将会把该目录下所有.apk/ipa执行一次本场景;如果在路径前加上#则反转安装包执行顺序");
		add(TF_appspath, gridBox.resetGridX().autoGridY().setGridWH(2, 1));

		JLabel lbl_syscrash = new JLabel("捕获系统异常");
		add(lbl_syscrash, gridBox.resetGridX().autoGridY());

		comboBox_syscrash = new JComboBox<>();
		add(comboBox_syscrash, gridBox.autoGridX());

		JLabel lbl_appcrash = new JLabel("捕获应用异常");
		add(lbl_appcrash, gridBox.resetGridX().autoGridY());

		comboBox_appcrash = new JComboBox<>();
		add(comboBox_appcrash, gridBox.autoGridX());

		JLabel lbl_userlogcatch = new JLabel("日志捕获");
		add(lbl_userlogcatch, gridBox.resetGridX().autoGridY());

		TF_userlogcatch = new JFormattedTextField();
		TF_userlogcatch.setToolTipText("自定义logcat日志内容捕获,格式:xxx;xxx;xxx;");
		add(TF_userlogcatch, gridBox.autoGridX());

		JLabel lbl_screenshot = new JLabel("截图方式");
		add(lbl_screenshot, gridBox.resetGridX().autoGridY());

		comboBox_screenshot = new JComboBox<>();
		add(comboBox_screenshot, gridBox.autoGridX());

		JLabel lbl_params = new JLabel("参数");
		add(lbl_params, gridBox.resetGridX().autoGridY());

		TF_params = new JFormattedTextField();
		TF_params.setToolTipText("参数设置,格式:parma1=a;parma2=b;");
		add(TF_params, gridBox.autoGridX());

		JLabel lbl_note = new JLabel("备注");
		add(lbl_note, gridBox.resetGridX().autoGridY());

		TF_note = new JFormattedTextField();
		TF_note.setToolTipText("本任务的备注信息,用于区分某些任务");
		add(TF_note, gridBox.autoGridX());

		/**
		 * 系统区分点
		 */
		if (this.deviceOS.equals(Cconfig.ANDROID)) {
			JLabel lbl_setdevice = new JLabel("自动设置设备");
			add(lbl_setdevice, gridBox.resetGridX().autoGridY());

			comboBox_setdevice = new JComboBox<>();
			comboBox_setdevice.setToolTipText("打开wifi,自动时间时区,自动亮度,屏幕休眠5分钟,输入法切换为非appium输入法");
			add(comboBox_setdevice, gridBox.autoGridX());
		} else {
			JLabel lbl_idevicesyslogtag = new JLabel("日志标记");
			add(lbl_idevicesyslogtag, gridBox.resetGridX().autoGridY().setGridWH(1, 1));
			TF_idevicesyslogtag = new JFormattedTextField();
			TF_idevicesyslogtag.setToolTipText("idevicesyslog捕获日志时,区分是否为本应用日志");
			add(TF_idevicesyslogtag, gridBox.autoGridX());
		}
		// ************************
		JLabel lbl_appium_title = new JLabel("Appium配置");
		lbl_appium_title.setForeground(Color.BLUE);
		add(lbl_appium_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_appiumServer = new JLabel("服务器地址");
		add(lbl_appiumServer, gridBox.resetGridX().autoGridY());

		JButton btn_appiumServer = new JButton("检测");
		btn_appiumServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_appiumServer button");
				String result = CheckPC.checkAppiumServerUrl(TF_appiumServerUrl.getText()) ? "连接正常" : "无法连接";
				JOptionPane.showMessageDialog(null, result, "消息", JOptionPane.INFORMATION_MESSAGE,
						MainRun.settingsBean.getLogo());
			}
		});
		add(btn_appiumServer, gridBox.autoGridX());

		TF_appiumServerUrl = new JFormattedTextField();
		TF_appiumServerUrl.setToolTipText("Appium服务器的完整地址,如http://0.0.0.0:4723/wd/hub");
		add(TF_appiumServerUrl, gridBox.resetGridX().autoGridY().setGridWH(2, 1));

		JLabel lbl_initdriver = new JLabel("重新初始化");
		add(lbl_initdriver, gridBox.resetGridX().autoGridY());

		comboBox_initdriver = new JComboBox<>();
		comboBox_initdriver.setToolTipText("true则会先卸载appium的apk,然后重新安装当前appium版本的apk;多个任务时只会执行一次");
		add(comboBox_initdriver, gridBox.autoGridX());

		JLabel lbl_appid = new JLabel("选择应用ID");
		add(lbl_appid, gridBox.resetGridX().autoGridY());

		comboBox_appid = new JComboBox<>();
		add(comboBox_appid, gridBox.autoGridX());

		JLabel lbl_capability = new JLabel("capability设置");
		add(lbl_capability, gridBox.resetGridX().autoGridY());
		/**
		 * 设置capability按钮
		 */
		JButton btn_capability = new JButton("设置");
		btn_capability.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_capability button");
				if (deviceOS.equals(Cconfig.ANDROID)) {
					capabilityMap.put("appPackage", (String) comboBox_appid.getSelectedItem());
				} else {
					capabilityMap.put("bundleId", (String) comboBox_appid.getSelectedItem());
				}
				CapacilityBoxUI capacilityUI = new CapacilityBoxUI(capabilityMap, lbl_udid_value, lbl_version_value);
				capacilityUI.setVisible(true);
			}
		});
		add(btn_capability, gridBox.autoGridX());

		JLabel lbl_udid = new JLabel(Cparams.udid);
		add(lbl_udid, gridBox.resetGridX().autoGridY());

		lbl_udid_value = new JLabel("");
		add(lbl_udid_value, gridBox.autoGridX());

		JLabel lbl_version = new JLabel("设备版本");
		add(lbl_version, gridBox.resetGridX().autoGridY());

		lbl_version_value = new JLabel("");
		add(lbl_version_value, gridBox.autoGridX());
		// ************************
		JLabel lbl_email_title = new JLabel("邮件配置");
		lbl_email_title.setForeground(Color.BLUE);
		add(lbl_email_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_email_send = new JLabel("发送邮件");
		add(lbl_email_send, gridBox.resetGridX().autoGridY());

		comboBox_email_send = new JComboBox<>();
		add(comboBox_email_send, gridBox.autoGridX());

		JLabel lbl_email_to = new JLabel("发送到");
		add(lbl_email_to, gridBox.resetGridX().autoGridY());

		TF_email_to = new JFormattedTextField();
		TF_email_to.setToolTipText("发送给");
		add(TF_email_to, gridBox.autoGridX());

		JLabel lbl_email_cc = new JLabel("抄送到");
		add(lbl_email_cc, gridBox.resetGridX().autoGridY());

		TF_email_cc = new JFormattedTextField();
		TF_email_cc.setToolTipText("抄送给");
		add(TF_email_cc, gridBox.autoGridX());
		// *********************
		// android wechat
		JLabel lbl_wechat_title = new JLabel("微信通知");
		lbl_wechat_title.setForeground(Color.BLUE);
		add(lbl_wechat_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_wechat_send = new JLabel("发送微信");
		add(lbl_wechat_send, gridBox.resetGridX().autoGridY());

		comboBox_wechat_send = new JComboBox<>();
		add(comboBox_wechat_send, gridBox.autoGridX());

		JLabel lbl_wechat_people_list = new JLabel("人员设置");
		add(lbl_wechat_people_list, gridBox.resetGridX().autoGridY());

		JButton btn_wechat_people_list = new JButton("设置");
		btn_wechat_people_list.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				logger.info("press btn_wechat_people_list button");
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						MemberListUI memberListUI = new MemberListUI(wechat_people_list) {

							@Override
							public boolean confirm() {
								// TODO Auto-generated method stub
								wechat_people_list = getContorller().getPeopleList();
								return true;
							}

							@Override
							public boolean cancel() {
								// TODO Auto-generated method stub
								return true;
							}

						};
						memberListUI.show();
					}
				});

			}
		});
		add(btn_wechat_people_list, gridBox.autoGridX());

		InitValue();
	}

	/**
	 * 初始化控件值
	 */
	protected void InitValue() {
		// appium服务器地址
		TF_appiumServerUrl.setText(MainRun.sysConfigBean.getAppiumServerUrl());
		// 场景名
		config.entrySet().forEach(i -> comboBox_scene.addItem(i.getKey()));
		// UDID
		lbl_udid_value.setText(udid);
		// version
		lbl_version_value.setText(version);
		// initdriver
		comboBox_initdriver.addItem("true");
		comboBox_initdriver.addItem("false");
		// syscrash
		comboBox_syscrash.addItem("true");
		comboBox_syscrash.addItem("false");
		// appcrash
		comboBox_appcrash.addItem("true");
		comboBox_appcrash.addItem("false");
		// email
		comboBox_email_send.addItem("true");
		comboBox_email_send.addItem("false");
		// wechat
		comboBox_wechat_send.addItem("true");
		comboBox_wechat_send.addItem("false");
		/**
		 * 系统区分点
		 */
		if (this.deviceOS.equals(Cconfig.ANDROID)) {
			// 截图方式
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_APPIUM);
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_ADB);
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_DDMLIB);
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_NONE);

			comboBox_setdevice.addItem("true");
			comboBox_setdevice.addItem("false");
		} else {
			// 截图方式
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_APPIUM);
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_IDEVICESREENSHOT);
			comboBox_screenshot.addItem(Cconfig.SCREENSHOT_NONE);
		}

		setValueByScene((String) comboBox_scene.getSelectedItem());

	}

	/**
	 * 选择场景后自动设置控件值
	 * 
	 * @param name
	 */
	protected void setValueByScene(String name) {
		logger.info("select scene item=" + name);
		Map<String, Object> OriginalsceneMap = null;
		for (Entry<String, Object> entry : config.entrySet()) {
			if (name.equals(entry.getKey())) {
				OriginalsceneMap = (Map<String, Object>) entry.getValue();
				break;
			}
		}
		// desc
		lbl_desc_value.setText((String) OriginalsceneMap.get(Cparams.desc));
		// screenshot
		comboBox_screenshot.setSelectedItem((String) OriginalsceneMap.get(Cparams.screenshot));
		// capability
		capabilityMap = (Map<String, String>) OriginalsceneMap.get("capability");
		capabilityMap.put(Cparams.udid, udid);
		capabilityMap.put("deviceName", deviceName);
		capabilityMap.put("platformVersion", version);
		// UDID
		lbl_udid_value.setText((String) capabilityMap.get(Cparams.udid));
		// version
		lbl_version_value.setText((String) capabilityMap.get("platformVersion"));
		// caseruninfo
		caserunMap.clear();
		for (TestCaseBean testCaseBean : FactoryScene.getCase(name)) {
			caserunMap.put(testCaseBean, testCaseBean.getNo() > 0 ? true : false);
		}
		// userlogcatch
		TF_userlogcatch.setText((String) OriginalsceneMap.get(Cparams.userlogcatch));
		// params
		TF_params.setText((String) OriginalsceneMap.get(Cparams.params));
		// apps
		TF_appspath.setText((String) OriginalsceneMap.get(Cparams.apps));
		// syscrash
		comboBox_syscrash.setSelectedItem((String) OriginalsceneMap.get(Cparams.syscrash));
		// appcrash
		comboBox_appcrash.setSelectedItem((String) OriginalsceneMap.get(Cparams.appcrash));
		// initdriver
		comboBox_initdriver.setSelectedItem((String) OriginalsceneMap.get(Cparams.initdriver));
		// email
		comboBox_email_send.setSelectedItem((String) OriginalsceneMap.get(Cparams.email_send));
		TF_email_to.setText((String) OriginalsceneMap.get(Cparams.email_to));
		TF_email_cc.setText((String) OriginalsceneMap.get(Cparams.email_cc));
		// wechat
		comboBox_wechat_send.setSelectedItem((String) OriginalsceneMap.get(Cparams.wechat_send));
		wechat_people_list = (String) OriginalsceneMap.get(Cparams.wechat_people_list);
		/**
		 * 系统区分点
		 */
		if (this.deviceOS.equals(Cconfig.ANDROID)) {
			// setdevice
			comboBox_setdevice.setSelectedItem((String) OriginalsceneMap.get(Cparams.setdevice));
		} else {
			TF_idevicesyslogtag.setText((String) OriginalsceneMap.get(Cparams.idevicesyslogtag));
		}
		// 系统区分点
		if (this.deviceOS.equals(Cconfig.ANDROID)) {
			// appid
			comboBox_appid.removeAllItems();
			comboBox_appid.addItem(capabilityMap.get("appPackage"));
			for (String appid : ((String) OriginalsceneMap.get(Cparams.appid)).split(";")) {
				if (!appid.equals(""))
					comboBox_appid.addItem(appid);
			}
			comboBox_appid.setSelectedItem(capabilityMap.get("appPackage"));
		} else {
			// appid
			comboBox_appid.removeAllItems();
			comboBox_appid.addItem(capabilityMap.get("bundleId"));
			for (String appid : ((String) OriginalsceneMap.get(Cparams.appid)).split(";")) {
				if (!appid.equals(""))
					comboBox_appid.addItem(appid);
			}
			comboBox_appid.setSelectedItem(capabilityMap.get("bundleId"));
		}
	}

	/**
	 * 根据sceneMap设置控件显示值
	 * 
	 * @param sceneMap
	 */
	public void setValueByScene(Map<String, Object> sceneMap) {
		// name
		comboBox_scene.setSelectedItem((String) sceneMap.get(Cparams.name));
		// appiumServerUrl
		TF_appiumServerUrl.setText((String) sceneMap.get(Cparams.appiumserverurl));
		// desc
		lbl_desc_value.setText((String) sceneMap.get(Cparams.desc));
		// screenshot
		comboBox_screenshot.setSelectedItem((String) sceneMap.get(Cparams.screenshot));
		// capability
		capabilityMap = (Map<String, String>) sceneMap.get(Cparams.capability);
		// note
		TF_note.setText((String) sceneMap.get(Cparams.note));
		// caseruninfo
		caserunMap = (Map<TestCaseBean, Boolean>) sceneMap.get(Cparams.caseruninfo);
		// UDID
		lbl_udid_value.setText((String) capabilityMap.get(Cparams.udid));
		// version
		lbl_version_value.setText((String) capabilityMap.get("platformVersion"));
		// syscrash
		comboBox_syscrash.setSelectedItem((String) sceneMap.get(Cparams.syscrash));
		// appcrash
		comboBox_appcrash.setSelectedItem((String) sceneMap.get(Cparams.appcrash));
		// initdriver
		comboBox_initdriver.setSelectedItem((String) sceneMap.get(Cparams.initdriver));
		// email
		comboBox_email_send.setSelectedItem((String) sceneMap.get(Cparams.email_send));
		TF_email_to.setText((String) sceneMap.get(Cparams.email_to));
		TF_email_cc.setText((String) sceneMap.get(Cparams.email_cc));
		// wechat
		comboBox_wechat_send.setSelectedItem((String) sceneMap.get(Cparams.wechat_send));
		wechat_people_list = (String) sceneMap.get(Cparams.wechat_people_list);
		// apps
		TF_appspath.setText((String) sceneMap.get(Cparams.apps));
		// params
		TF_params.setText((String) sceneMap.get(Cparams.params));
		// userlogcatch
		TF_userlogcatch.setText((String) sceneMap.get(Cparams.userlogcatch));
		/**
		 * 系统区分点
		 */
		if (this.deviceOS.equals(Cconfig.ANDROID)) {
			// appid
			comboBox_appid.setSelectedItem((String) capabilityMap.get("appPackage"));
			// setdevice
			comboBox_setdevice.setSelectedItem((String) sceneMap.get(Cparams.setdevice));
		} else {
			TF_idevicesyslogtag.setText((String) sceneMap.get(Cparams.idevicesyslogtag));
			// appid
			comboBox_appid.setSelectedItem((String) capabilityMap.get("bundleId"));
		}
	}

	/**
	 * 返回当前任务参数设置
	 * 
	 * @return
	 */
	public Map<String, Object> getSceneMap() {
		Map<String, Object> sceneMap = new HashMap<>();
		for (Entry<String, Object> entry : config.entrySet()) {
			if (((String) comboBox_scene.getSelectedItem()).equals(entry.getKey())) {
				((Map<String, Object>) entry.getValue()).entrySet()
						.forEach(i -> sceneMap.put(i.getKey(), i.getValue()));
				break;
			}
		}
		sceneMap.put(Cparams.type, Cconfig.TASK_TYPE_SCENE);
		sceneMap.put(Cparams.appiumserverurl, TF_appiumServerUrl.getText());
		sceneMap.put(Cparams.screenshot, (String) comboBox_screenshot.getSelectedItem());
		sceneMap.put(Cparams.syscrash, (String) comboBox_syscrash.getSelectedItem());
		sceneMap.put(Cparams.appcrash, (String) comboBox_appcrash.getSelectedItem());
		sceneMap.put(Cparams.initdriver, (String) comboBox_initdriver.getSelectedItem());
		// email
		sceneMap.put(Cparams.email_send, (String) comboBox_email_send.getSelectedItem());
		sceneMap.put(Cparams.email_to, TF_email_to.getText());
		sceneMap.put(Cparams.email_cc, TF_email_cc.getText());
		// wechat
		sceneMap.put(Cparams.wechat_people_list, wechat_people_list);
		sceneMap.put(Cparams.wechat_send, (String) comboBox_wechat_send.getSelectedItem());

		Map<String, String> map_capability = new LinkedHashMap<>();// 指向不同内存地址
		for (Entry<String, String> entry : capabilityMap.entrySet()) {
			map_capability.put(entry.getKey(), entry.getValue());
		}
		sceneMap.put(Cparams.capability, map_capability);
		sceneMap.put(Cparams.name, (String) comboBox_scene.getSelectedItem());
		sceneMap.put(Cparams.apps, TF_appspath.getText());

		Map<TestCaseBean, Boolean> map_caseruninfo = new LinkedHashMap<>();// 指向不同内存地址
		for (Entry<TestCaseBean, Boolean> entry : caserunMap.entrySet()) {
			map_caseruninfo.put(entry.getKey(), entry.getValue());
		}
		sceneMap.put(Cparams.caseruninfo, map_caseruninfo);

		sceneMap.put(Cparams.note, TF_note.getText());
		sceneMap.put(Cparams.desc, lbl_desc_value.getText());
		sceneMap.put("os", this.deviceOS);
		sceneMap.put(Cparams.params, TF_params.getText());
		sceneMap.put(Cparams.userlogcatch, TF_userlogcatch.getText());
		/**
		 * 系统区分点
		 */
		if (this.deviceOS.equals(Cconfig.ANDROID)) {
			sceneMap.put(Cparams.email_smtp, MainRun.androidConfigBean.getEmail().get(Cparams.smtp));
			sceneMap.put(Cparams.email_account, MainRun.androidConfigBean.getEmail().get(Cparams.account));
			sceneMap.put(Cparams.email_password, MainRun.androidConfigBean.getEmail().get(Cparams.password));
			// appid
			map_capability.put("appPackage", (String) comboBox_appid.getSelectedItem());
			// setdevice
			sceneMap.put(Cparams.setdevice, (String) comboBox_setdevice.getSelectedItem());
		} else {
			sceneMap.put(Cparams.idevicesyslogtag, TF_idevicesyslogtag.getText());
			sceneMap.put(Cparams.email_smtp, MainRun.iosConfigBean.getEmail().get(Cparams.smtp));
			sceneMap.put(Cparams.email_account, MainRun.iosConfigBean.getEmail().get(Cparams.account));
			sceneMap.put(Cparams.email_password, MainRun.iosConfigBean.getEmail().get(Cparams.password));
			// appid
			map_capability.put("bundleId", (String) comboBox_appid.getSelectedItem());
		}

		return sceneMap;
	}

	/**
	 * 安装包文件夹选择
	 * 
	 * @return
	 */
	private File selectFloderAndFiles() {
		JFileChooser fileChooser = new JFileChooser("");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// 只能选择目录
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.apk|*.ipa";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".apk") | ext.endsWith(".ipa") | f.isDirectory();
			}
		});
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			logger.info("No floder selected.");
			return null;
		}
		return fileChooser.getSelectedFile();
	}
}
