package com.viewer.scenetask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viewer.main.MainRun;

public abstract class TextShowBoxUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(TextShowBoxUI.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -6868855790619958851L;
	private JPanel contentPane;
	JTextArea textArea;

	/**
	 * 文本输入框
	 * 
	 * @param title 标题
	 * @param note  文本框说明
	 * @param text  初始文本
	 * @param width 窗体宽
	 * @param hight 窗体高
	 */
	public TextShowBoxUI(String title, String note, String text, int width, int hight) {
		setSize(width, hight);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle(title);
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());

		TextShowUI textShowUI = new TextShowUI(width - 30, hight - 90) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1151055290392543476L;

		};
		textShowUI.setEdit(true);
		textShowUI.setLocation(15, 30);
		textArea = textShowUI.getJTextArea();
		textArea.setText(text);
		contentPane.add(textShowUI);

		JLabel lbl_editbox = new JLabel(note);
		lbl_editbox.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_editbox.setBounds(5, 5, width, 15);
		contentPane.add(lbl_editbox);

		JButton btn_ok = new JButton("确定");
		btn_ok.addActionListener(e -> {
			logger.info("press btn_ok button");
			confirmButton();
			dispose();
		});
		btn_ok.setBounds(width - 240, hight - 60, 100, 30);
		contentPane.add(btn_ok);

		JButton btn_cancel = new JButton("取消");
		btn_cancel.addActionListener(e -> {
			logger.info("press btn_cancel button");
			cancelButton();
			dispose();
		});
		btn_cancel.setBounds(width - 120, hight - 60, 100, 30);
		contentPane.add(btn_cancel);
	}

	/**
	 * 确定按钮内容
	 * 
	 * @return true则关闭窗口
	 */
	protected abstract boolean confirmButton();

	/**
	 * 取消按钮内容
	 * 
	 * @return true则关闭窗口
	 */
	protected abstract boolean cancelButton();

	/**
	 * 获取文本内容
	 * 
	 * @return
	 */
	public String getText() {
		return textArea.getText();
	}
}
