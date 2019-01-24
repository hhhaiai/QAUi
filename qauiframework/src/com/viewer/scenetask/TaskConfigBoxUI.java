package com.viewer.scenetask;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.helper.HelperUtil;
import com.viewer.main.MainRun;
import com.viewer.wechat.MemberListUI;

import javafx.application.Platform;

public class TaskConfigBoxUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(TaskConfigBoxUI.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 538080273248810256L;
	private JPanel contentPane;

	JComboBox<String> comboBox_email_send;
	JComboBox<String> comboBox_wechat_send;

	JFormattedTextField TF_email_to;
	JFormattedTextField TF_email_cc;
	JFormattedTextField TF_email_subject;
	// JFrame
	JButton btnOK;
	JButton btnCancel;

	String deviceOS;
	Map<String, String> taskConfigMap;
	String wechat_people_list = "";

	/**
	 * Create the frame.
	 */
	public TaskConfigBoxUI(String deviceOS, Map<String, String> taskConfigMap) {
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("场景任务配置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());

		/**
		 * init
		 */
		this.deviceOS = deviceOS;
		this.taskConfigMap = taskConfigMap;
		JScrollPane configScroll = new JScrollPane(ConfigJPanel());
		configScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		configScroll.setBounds(25, 10, 450, 300);
		contentPane.add(configScroll);
		initConfig();
		// 确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				if (!HelperUtil.checkEmail(TF_email_to.getText()) || !HelperUtil.checkEmail(TF_email_cc.getText())) {
					JOptionPane.showMessageDialog(contentPane, "邮箱格式错误,请检查!", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				savaConfig();
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

	}

	/**
	 * 保存数据
	 */
	private void savaConfig() {
		taskConfigMap.put(Cparams.email_send, (String) comboBox_email_send.getSelectedItem());
		taskConfigMap.put(Cparams.email_to, TF_email_to.getText());
		taskConfigMap.put(Cparams.email_cc, TF_email_cc.getText());
		taskConfigMap.put(Cparams.email_subject, TF_email_subject.getText());
		taskConfigMap.put(Cparams.wechat_send, (String) comboBox_wechat_send.getSelectedItem());
		taskConfigMap.put(Cparams.wechat_people_list, wechat_people_list);
	}

	/**
	 * 初始化数据
	 */
	private void initConfig() {
		comboBox_email_send.addItem("true");
		comboBox_email_send.addItem("false");
		comboBox_email_send.setSelectedItem(taskConfigMap.get(Cparams.email_send));
		TF_email_to.setText(taskConfigMap.get(Cparams.email_to));
		TF_email_cc.setText(taskConfigMap.get(Cparams.email_cc));
		TF_email_subject.setText(taskConfigMap.get(Cparams.email_subject));

		comboBox_wechat_send.addItem("true");
		comboBox_wechat_send.addItem("false");
		comboBox_wechat_send.setSelectedItem(taskConfigMap.get(Cparams.wechat_send));
		wechat_people_list = taskConfigMap.get(Cparams.wechat_people_list);
	}

	/**
	 * 设置界面
	 * 
	 * @return
	 */
	private JPanel ConfigJPanel() {
		JPanel contentPane = new JPanel();
		contentPane.setSize(600, 800);
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		// 布局
		GridBox gridBox = new GridBox();
		gridBox.setInsets(0, 0, 2, 2);

		// ************************
		JLabel lbl_email_title = new JLabel("汇总邮件配置");
		lbl_email_title.setForeground(Color.BLUE);
		contentPane.add(lbl_email_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_email_subject = new JLabel("邮件主标题");
		contentPane.add(lbl_email_subject, gridBox.resetGridX().autoGridY());

		TF_email_subject = new JFormattedTextField();
		TF_email_subject.setToolTipText("邮件主标题,及测试报告备注");
		contentPane.add(TF_email_subject, gridBox.autoGridX());

		JLabel lbl_email_send = new JLabel("发送邮件");
		contentPane.add(lbl_email_send, gridBox.resetGridX().autoGridY());

		comboBox_email_send = new JComboBox<>();
		contentPane.add(comboBox_email_send, gridBox.autoGridX());

		JLabel lbl_email_to = new JLabel("发送到");
		contentPane.add(lbl_email_to, gridBox.resetGridX().autoGridY());

		TF_email_to = new JFormattedTextField();
		TF_email_to.setToolTipText("发送给");
		contentPane.add(TF_email_to, gridBox.autoGridX());

		JLabel lbl_email_cc = new JLabel("抄送到");
		contentPane.add(lbl_email_cc, gridBox.resetGridX().autoGridY());

		TF_email_cc = new JFormattedTextField();
		TF_email_cc.setToolTipText("抄送给");
		contentPane.add(TF_email_cc, gridBox.autoGridX());

		JLabel lbl_wechat_title = new JLabel("微信通知");
		lbl_wechat_title.setForeground(Color.BLUE);
		contentPane.add(lbl_wechat_title, gridBox.resetGridX().autoGridY());

		JLabel lbl_wechat_send = new JLabel("发送微信");
		contentPane.add(lbl_wechat_send, gridBox.resetGridX().autoGridY());

		comboBox_wechat_send = new JComboBox<>();
		contentPane.add(comboBox_wechat_send, gridBox.autoGridX());

		JLabel lbl_wechat_people_list = new JLabel("人员设置");
		contentPane.add(lbl_wechat_people_list, gridBox.resetGridX().autoGridY());

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
		contentPane.add(btn_wechat_people_list, gridBox.autoGridX());
		return contentPane;
	}
}
