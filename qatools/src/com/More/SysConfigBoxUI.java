package com.More;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.GridBox;
import com.Viewer.MainRun;
import com.constant.Cconfig;

public class SysConfigBoxUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(SysConfigBoxUI.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 8517102059308833966L;
	private JPanel contentPane;
	// android
	JFormattedTextField TF_androidsdk;
	// ios
	JFormattedTextField TF_maccmd;

	JFormattedTextField TF_reportPath;
	// JFrame
	JButton btnOK;
	JButton btnCancel;

	/**
	 * Create the frame.
	 */
	public SysConfigBoxUI() {
		setBounds(100, 100, 500, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("系统设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		JScrollPane configScroll = new JScrollPane(ConfigJPanel());
		configScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		configScroll.setBounds(25, 10, 450, 300);
		contentPane.add(configScroll);
		// 确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				setSysconfig();
				// 检查环境
				String check = CheckPC.checkAll();
				if (check.contains("异常"))
					JOptionPane.showMessageDialog(contentPane, check, "消息", JOptionPane.ERROR_MESSAGE);
				dispose();

			}
		});
		btnOK.setBounds(271, 347, 100, 25);
		contentPane.add(btnOK);
		// 取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press cancel button");

				dispose();
			}
		});
		btnCancel.setBounds(383, 347, 100, 25);
		contentPane.add(btnCancel);

		JButton btnDefault = new JButton("恢复默认");
		btnDefault.addActionListener(e -> {

		});
		btnDefault.setBounds(25, 347, 100, 25);
		contentPane.add(btnDefault);
	}

	/**
	 * 文件夹选择
	 * 
	 * @return
	 */
	private File selectFloder() {
		JFileChooser fileChooser = new JFileChooser("");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
		if (fileChooser.showOpenDialog(null) != 0) {
			logger.info("No floder selected.");
			return null;
		}
		return fileChooser.getSelectedFile();
	}

	/**
	 * 修改系统设置
	 */
	private void setSysconfig() {
		MainRun.paramsBean.setAndroidSDK(TF_androidsdk.getText());
		MainRun.paramsBean.setMACcmd(TF_maccmd.getText());
		File reportFile = new File(TF_reportPath.getText());
		if (reportFile.exists() && reportFile.isDirectory()) {
			MainRun.paramsBean.setReportPath(TF_reportPath.getText());
		} else {
			if (MainRun.OStype == Cconfig.MAC) {
				MainRun.paramsBean.setReportPath(
						FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/Desktop");
			} else {
				MainRun.paramsBean
						.setReportPath(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
			}
		}
		if (MainRun.OStype == Cconfig.WINDOWS) {
			File file = new File(TF_androidsdk.getText() + "/adb.exe");
			if (file.exists()) {
				MainRun.paramsBean.setAndroidSDK_adb(file.getAbsolutePath());
			} else {
				MainRun.paramsBean.setAndroidSDK_adb(MainRun.paramsBean.getAndroidSDK() + "/platform-tools/adb.exe");
			}
		} else {
			File file = new File(TF_androidsdk.getText() + "/adb");
			if (file.exists()) {
				MainRun.paramsBean.setAndroidSDK_adb(file.getAbsolutePath());
			} else {
				MainRun.paramsBean.setAndroidSDK_adb(MainRun.paramsBean.getAndroidSDK() + "/platform-tools/adb");
			}
		}
		MainRun.xmlOperationUtil.XMLChanger("androidSDK", TF_androidsdk.getText());
		MainRun.xmlOperationUtil.XMLChanger("MACcmd", TF_maccmd.getText());
		MainRun.xmlOperationUtil.XMLChanger("reportPath", MainRun.paramsBean.getReportPath());
		MainRun.QALogfile = MainRun.paramsBean.getReportPath() + "/QAToolsLogs";
		logger.info("QALOGFILE=" + MainRun.QALogfile);
	}

	/**
	 * 设置界面
	 * 
	 * @return
	 */
	private JPanel ConfigJPanel() {
		JPanel contentPane = new JPanel();
		contentPane.setSize(450, 450);
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		// 布局
		GridBox gridBox = new GridBox();
		gridBox.setInsets(0, 0, 2, 2);

		JLabel lbl_config = new JLabel("通用设置");
		lbl_config.setForeground(Color.BLUE);
		contentPane.add(lbl_config, gridBox.resetGridX().autoGridY());

		JLabel lbl_reportPath = new JLabel("保存路径");
		contentPane.add(lbl_reportPath, gridBox.resetGridX().autoGridY());

		JButton btn_reportPath = new JButton("选择");
		btn_reportPath.addActionListener(e -> {
			logger.info("press btn_apks button");
			File file = selectFloder();
			if (file != null)
				TF_reportPath.setText(file.getAbsolutePath());
		});
		contentPane.add(btn_reportPath, gridBox.autoGridX());

		TF_reportPath = new JFormattedTextField();
		TF_reportPath.setText(MainRun.paramsBean.getReportPath());
		TF_reportPath.setToolTipText("日志保存地址,不填默认在桌面/QAToolsLogs");
		contentPane.add(TF_reportPath, gridBox.resetGridX().autoGridY().setGridWH(2, 1));

		JLabel lbl_android = new JLabel("Android设置");
		lbl_android.setForeground(Color.BLUE);
		contentPane.add(lbl_android, gridBox.resetGridX().autoGridY());

		JLabel lbl_androidsdk = new JLabel("SDK路径");
		contentPane.add(lbl_androidsdk, gridBox.resetGridX().autoGridY());

		JButton btn_androidsdk = new JButton("选择");
		btn_androidsdk.addActionListener(e -> {
			logger.info("press btn_androidsdk button");
			File file = selectFloder();
			if (file != null)
				TF_androidsdk.setText(file.getAbsolutePath());
		});
		contentPane.add(btn_androidsdk, gridBox.autoGridX());

		TF_androidsdk = new JFormattedTextField();
		TF_androidsdk.setText(MainRun.paramsBean.getAndroidSDK());
		TF_androidsdk.setToolTipText("设置android SDK 路径根目录");
		contentPane.add(TF_androidsdk, gridBox.resetGridX().autoGridY().setGridWH(2, 1));

		JLabel lbl_IOS = new JLabel("iOS设置");
		lbl_IOS.setForeground(Color.BLUE);
		contentPane.add(lbl_IOS, gridBox.resetGridX().autoGridY());

		JLabel lbl_maccmd = new JLabel("idevice路径");
		contentPane.add(lbl_maccmd, gridBox.resetGridX().autoGridY());

		JButton btn_maccmd = new JButton("选择");
		btn_maccmd.addActionListener(e -> {
			logger.info("press btn_maccmd button");
			File file = selectFloder();
			if (file != null)
				TF_maccmd.setText(file.getAbsolutePath());
		});
		contentPane.add(btn_maccmd, gridBox.autoGridX());

		TF_maccmd = new JFormattedTextField();
		TF_maccmd.setText(MainRun.paramsBean.getMACcmd());
		TF_maccmd.setToolTipText("设置MAC cmd目录,如/usr/local/bin");
		contentPane.add(TF_maccmd, gridBox.resetGridX().autoGridY().setGridWH(2, 1));

		return contentPane;
	}

	/**
	 * 只能输入数字
	 * 
	 * @return
	 */
	private KeyListener inputNumbers() {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if ((e.getKeyChar() >= KeyEvent.VK_0 && e.getKeyChar() <= KeyEvent.VK_9)
						|| e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB
						|| e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE
						|| e.getKeyChar() == KeyEvent.VK_LEFT || e.getKeyChar() == KeyEvent.VK_RIGHT
						|| e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					return;
				} else {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		};
	}
}
