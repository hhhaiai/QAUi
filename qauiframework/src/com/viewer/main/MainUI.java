package com.viewer.main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.AndroidInfo;
import com.helper.HelperUtil;
import com.helper.IOSInfo;
import com.viewer.scenetask.MainTaskUI;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class MainUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3458655753456501131L;
	Logger logger = LoggerFactory.getLogger(MainUI.class);
	private JPanel contentPane;

	JComboBox<String> JComboBox_deviceslist; // 设备列表
	String selectedUDID;

	/**
	 * QAUiFramework主界面
	 */
	public MainUI() {
		// 图标logo
		MainRun.settingsBean.setLogo(new ImageIcon(getClass().getResource("/Resources/logo.png")));
		setResizable(false);
		setTitle("QAUiFramework " + MainRun.Version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 650);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setLocation(0, 0);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setIconImage(MainRun.settingsBean.getLogo().getImage());

		// start buuton
		JButton btnCreateScene = new JButton("创建任务");
		btnCreateScene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btnCreateScene button");
				if (selectedUDID == null || selectedUDID.equals("")) {
					JOptionPane.showMessageDialog(contentPane, "请插入设备!", "消息", JOptionPane.INFORMATION_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				if (selectedUDID.contains(Cconfig.ANDROID)) {
					MainTaskUI mainTaskUI = new MainTaskUI(selectedUDID.split("\\s+")[0], Cconfig.ANDROID);
					mainTaskUI.setVisible(true);
				} else if (selectedUDID.contains(Cconfig.IOS)) {
					MainTaskUI mainTaskUI = new MainTaskUI(selectedUDID.split("\\s+")[0], Cconfig.IOS);
					mainTaskUI.setVisible(true);
				}
			}
		});
		btnCreateScene.setBounds(698, 117, 117, 29);
		contentPane.add(btnCreateScene);

		JComboBox_deviceslist = new JComboBox<>();
		JComboBox_deviceslist.setFont(new Font("微软雅黑", Font.PLAIN, 19));
		JComboBox_deviceslist.setBounds(64, 62, 751, 50);
		JComboBox_deviceslist.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) { // 选中
					selectedUDID = (String) JComboBox_deviceslist.getSelectedItem();
					logger.info("select device UDID=" + selectedUDID);
				} else {// 取消
					logger.info("cancel one device,selectedUDID=" + selectedUDID);
				}
			}

		});
		contentPane.add(JComboBox_deviceslist);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 850, 22);
		contentPane.add(menuBar);

		JMenu menu_main = new JMenu("菜单");
		menuBar.add(menu_main);

		JMenuItem menuItem_checkpc = new JMenuItem("环境检测");
		menuItem_checkpc.addActionListener(e -> {
			logger.info("press menuItem_checkpc button");
			JOptionPane.showMessageDialog(contentPane, CheckPC.checkAll(), "消息", JOptionPane.INFORMATION_MESSAGE,
					MainRun.settingsBean.getLogo());
		});
		menu_main.add(menuItem_checkpc);

		JMenuItem menuItem_sysconfig = new JMenuItem("系统设置");
		menuItem_sysconfig.addActionListener(e -> {
			logger.info("press menuItem_sysconfig button");
			SysConfigBoxUI sysConfigUI = new SysConfigBoxUI();
			sysConfigUI.setVisible(true);
		});
		menu_main.add(menuItem_sysconfig);

		JMenu menu_about = new JMenu("关于");
		menuBar.add(menu_about);

		JMenuItem menuItem_aboutproduct = new JMenuItem("产品");
		menuItem_aboutproduct.addActionListener(e -> {
			JOptionPane.showMessageDialog(contentPane, "QAUiFramework " + MainRun.Version + ",一套基于Appium-java的测试框架.",
					"关于产品", JOptionPane.INFORMATION_MESSAGE, MainRun.settingsBean.getLogo());
		});
		menu_about.add(menuItem_aboutproduct);

		JMenuItem menuItem_aboutus = new JMenuItem("我们");
		menuItem_aboutus.addActionListener(e -> {
			JOptionPane.showMessageDialog(contentPane, "QA团队荣誉出品.", "关于我们", JOptionPane.INFORMATION_MESSAGE,
					MainRun.settingsBean.getLogo());
		});
		menu_about.add(menuItem_aboutus);

		JLabel lbl_deviceslist = new JLabel("设备列表");
		lbl_deviceslist.setBounds(10, 34, 61, 16);
		contentPane.add(lbl_deviceslist);

		JLabel lbl_biglogo = new JLabel("biglogo");
		lbl_biglogo.setIcon(new ImageIcon(getClass().getResource("/Resources/biglogo.png")));
		lbl_biglogo.setBounds(120, 158, 600, 375);
		contentPane.add(lbl_biglogo);

		JLabel lbl_version = new JLabel(
				"基于AppiumServer=" + MainRun.AppiumServerVersion + ", JavaClient=" + MainRun.JavaClientVersion);
		lbl_version.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_version.setBounds(340, 558, 475, 16);
		contentPane.add(lbl_version);

		// addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent e) {
		// super.windowClosing(e);
		// logger.info("close QAUiFramework");
		// MainRun.adbBridge.terminate();
		// }
		// });
		setJComboBox_deviceslist();
		// 初始化jfx
		JFXPanel jfxPanel = new JFXPanel();
		jfxPanel.setBounds(0, 0, 5, 5);
		getContentPane().add(jfxPanel);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				jfxPanel.setScene(new Scene(new AnchorPane()));
			}
		});
		// 检查环境
		String check = CheckPC.checkAll();
		if (check.contains("异常"))
			JOptionPane.showMessageDialog(contentPane, check, "消息", JOptionPane.ERROR_MESSAGE,
					MainRun.settingsBean.getLogo());
	}

	/**
	 * 得到插入PC的设备
	 * 
	 * @return
	 */
	protected List<String> getAllDevices() {
		// List<String> list=new ArrayList<>();
		// AndroidInfo.getDevices().forEach(str->list.add(str+"
		// -----"+AndroidInfo.getModel(str)+","+AndroidInfo.getVersion(str)+","+Cconfig.ANDROID));
		List<String> list = AndroidInfo.getDevices();
		if (MainRun.settingsBean.getSystem() == Cconfig.MAC)
			IOSInfo.getDevices().forEach(str -> list.add(
					str + "  -----" + IOSInfo.getProduct(str) + "," + IOSInfo.getVersion(str) + "," + Cconfig.IOS));
		return list;
	}

	/**
	 * 更新JComboBox<String> JComboBox_deviceslist设备列表
	 */
	protected void setJComboBox_deviceslist() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean nodevice = true;
				List<String> newlist = null;
				List<String> oldlist = null;
				while (true) {
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						logger.error("Exception", e);
					}
					newlist = null;
					newlist = getAllDevices();
					nodevice = true;
					if (!HelperUtil.equals(newlist, oldlist)) {
						oldlist = null;
						oldlist = newlist;
						JComboBox_deviceslist.removeAllItems();
						for (String str : newlist) {
							JComboBox_deviceslist.addItem(str);
							logger.info("update devices ID=" + str);
							nodevice = false;
						}
						if (nodevice) {
							selectedUDID = null;
							logger.info("select devices ID=null");
						}
					}
				}
			}
		}).start();
	}
}
