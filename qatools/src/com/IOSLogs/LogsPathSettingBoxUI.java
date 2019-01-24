package com.IOSLogs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class LogsPathSettingBoxUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3424438367661406447L;
	Logger logger = LoggerFactory.getLogger(LogsPathSettingBoxUI.class);
	private JPanel contentPane;
	JTextArea textArea_path;
	private JButton btnOK;
	private JButton btnCancel;
	JComboBox<String> Crashpath_list;

	/**
	 * Create the frame.
	 */
	public LogsPathSettingBoxUI() {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("应用日志路径设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		// 本文栏
		textArea_path = new JTextArea(com.Viewer.MainRun.paramsBean.getIOS_Logs_App_path().replaceAll(";", ";\n"));
		textArea_path.setWrapStyleWord(true);
		textArea_path.setLineWrap(true);
		JScrollPane scrollPane_highlight = new JScrollPane(textArea_path);
		scrollPane_highlight.setBounds(20, 10, 400, 150);
		scrollPane_highlight.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(scrollPane_highlight);

		// 确定按钮
		btnOK = new JButton("确定");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// 判断格式是否正确
				if (!HelperUtil.check_format(textArea_path.getText())) {
					JOptionPane.showMessageDialog(contentPane, "请按照指定格式输入文本!", "消息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 存储
				com.Viewer.MainRun.paramsBean.setIOS_Logs_App_path(textArea_path.getText().replaceAll("\n", ""));
				com.Viewer.MainRun.xmlOperationUtil.XMLChanger("IOS_Logs_App_path",
						textArea_path.getText().replaceAll("\n", ""));
				dispose();
				logger.info("press ok button");
			}
		});
		btnOK.setBounds(210, 218, 100, 25);
		contentPane.add(btnOK);

		// 取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				logger.info("press cancel button");
			}
		});
		btnCancel.setBounds(320, 218, 100, 25);
		contentPane.add(btnCancel);

		// 解释名字
		JLabel lblNotes = new JLabel("应用设置格式: \"包名,日志路径;包名,日志路径;\"");
		lblNotes.setBounds(20, 170, 317, 38);
		contentPane.add(lblNotes);

	}

	// 应用当前高亮设置到现有文本
	public void update_CheckLogsTXT() {

	}

}
