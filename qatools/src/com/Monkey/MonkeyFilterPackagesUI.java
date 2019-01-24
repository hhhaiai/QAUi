package com.Monkey;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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

public class MonkeyFilterPackagesUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3424438367661406447L;
	Logger logger = LoggerFactory.getLogger(MonkeyFilterPackagesUI.class);
	private JPanel contentPane;
	JTextArea textAreaFilterPackages;
	private JButton btnOK;
	private JButton btnCancel;
	MonkeyUImain monkeyUImain;

	/**
	 * Create the frame.
	 */
	public MonkeyFilterPackagesUI(MonkeyUImain monkeyUImain) {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("Monkey过滤包名设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);
		this.monkeyUImain = monkeyUImain;
		// 本文栏
		textAreaFilterPackages = new JTextArea(
				com.Viewer.MainRun.paramsBean.getMonkey_filterPackages().replaceAll(";", ";\n"));
		textAreaFilterPackages.setWrapStyleWord(true);
		textAreaFilterPackages.setLineWrap(true);
		JScrollPane scrollPaneFilterPackages = new JScrollPane(textAreaFilterPackages);
		scrollPaneFilterPackages.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneFilterPackages.setBounds(20, 10, 400, 150);
		contentPane.add(scrollPaneFilterPackages);

		// 确定按钮
		btnOK = new JButton("确定");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// 判断格式是否正确
				if (!HelperUtil.check_format(textAreaFilterPackages.getText())) {
					JOptionPane.showMessageDialog(contentPane, "请按照指定格式输入文本!", "消息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 更新
				monkeyUImain.setlistpackageAPP();
				// 存储
				com.Viewer.MainRun.paramsBean
						.setMonkey_filterPackages(textAreaFilterPackages.getText().replaceAll("\n", ""));
				com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_filterPackages",
						textAreaFilterPackages.getText().replaceAll("\n", ""));
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
		JLabel lblNewLabel = new JLabel("仅显示以上包名,格式: \"名称,包名;名称,包名;\"");
		lblNewLabel.setBounds(20, 170, 317, 38);
		contentPane.add(lblNewLabel);

	}

}
