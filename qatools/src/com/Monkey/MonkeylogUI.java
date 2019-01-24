package com.Monkey;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Viewer.MainRun;

public class MonkeylogUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959899612064813689L;
	Logger logger = LoggerFactory.getLogger(MonkeylogUI.class);
	MonkeyGet monkeyget;
	private JPanel contentPane;
	JCheckBox chckbxRoot;
	JCheckBox chckbxCompression;
	boolean isroot = false;
	boolean iscompression = true;
	String udid;

	/**
	 * Create the frame.
	 */
	public MonkeylogUI(String udid) {
		this.udid = udid;
		setResizable(false);
		setTitle("请设置:");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 303, 214);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setIconImage(MainRun.imagelogo);

		monkeyget = new MonkeyGet(udid);

		chckbxCompression = new JCheckBox("7-Zip压缩");
		chckbxCompression.setSelected(true);
		chckbxCompression.setBounds(6, 16, 103, 23);
		chckbxCompression.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxCompression.isSelected()) {
					iscompression = true;
				} else {
					iscompression = false;
				}
			}
		});
		contentPane.add(chckbxCompression);

		chckbxRoot = new JCheckBox("Root");
		chckbxRoot.setBounds(118, 16, 103, 23);
		chckbxRoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxRoot.isSelected()) {
					isroot = true;
				} else {
					isroot = false;
				}
			}
		});
		contentPane.add(chckbxRoot);

		JLabel lbltheLogDirectory = new JLabel("<html>Log存储目录 :<br>桌面/ThenLog/Monkey/PCtime</html>");
		lbltheLogDirectory.setVerticalAlignment(SwingConstants.TOP);
		lbltheLogDirectory.setBounds(6, 105, 236, 36);
		contentPane.add(lbltheLogDirectory);

		// OK
		JButton buttonOK = new JButton("提取");
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// device null
				if (!CheckUE.checkDevice(udid)) {
					JOptionPane.showMessageDialog(contentPane, "未检测到设备!", "Message", JOptionPane.ERROR_MESSAGE);
					logger.info("monkey stop button: no devices");
					return;
				}

				// check monkey run
				if (CheckUE.checkMonkeyrun(udid)) {
					// stop first then get log
					int confirm = JOptionPane.showConfirmDialog(contentPane, "是否先停止Monkey后再提取日记?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logger.info("stop monkey first");
						monkeyget.Stop();
					} else {
						logger.info("not stop monkey first");
					}
				}

				// get log
				if (monkeyget.filepathexist()) {
					int confirm = JOptionPane.showConfirmDialog(contentPane,
							monkeyget.getMainlog() + "已存在\n" + "是否删除该文件夹后提取日记?", "确认", JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logger.info("del exist monkey floder and get log");
					} else {
						logger.info("not del exist monkey floder");
						return;
					}
				}
				;
				if (monkeyget.getGetlogthreadrun()) {
					JOptionPane.showMessageDialog(contentPane, "QATools正在从设备" + udid + "提取日记.", "Messge",
							JOptionPane.ERROR_MESSAGE);
				} else {
					// 开启抓取log
					monkeyget.run(iscompression);
					dispose();
				}
				logger.info("monkeylogUI OK dispose");
			}
		});
		buttonOK.setBounds(77, 151, 100, 25);
		contentPane.add(buttonOK);

		// Cancel
		JButton buttonCancel = new JButton("取消");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				logger.info("monkeylogUI Cancel dispose");
			}
		});
		buttonCancel.setBounds(187, 151, 100, 25);
		contentPane.add(buttonCancel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(MainRun.mainFrame);

	}

}
