package com.viewer.scenetask;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

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

import com.constant.Cparams;
import com.helper.HelperUtil;
import com.viewer.main.MainRun;

public class CapacilityBoxUI extends JFrame {

	private static final long serialVersionUID = 3424438367661406447L;
	Logger logger = LoggerFactory.getLogger(CapacilityBoxUI.class);
	private JPanel contentPane;
	JTextArea textArea;
	private JButton btnOK;
	private JButton btnCancel;
	Map<String, String> capabilityMap;

	/**
	 * Create the frame.
	 */
	public CapacilityBoxUI(Map<String, String> map, JLabel lbl_udid_value, JLabel lbl_version_value) {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("Appium capability设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());
		/**
		 * 初始化
		 */
		this.capabilityMap = map;
		// 本文栏
		textArea = new JTextArea(Map2String(capabilityMap));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setBounds(20, 10, 400, 150);
		contentPane.add(scrollPane);

		// 确定按钮
		btnOK = new JButton("确定");
		btnOK.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				// 判断格式是否正确
				if (!HelperUtil.check_format(textArea.getText())) {
					JOptionPane.showMessageDialog(contentPane, "请按照指定格式输入文本!", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				String2Map(textArea.getText());
				// UDID
				lbl_udid_value.setText(capabilityMap.get(Cparams.udid));
				// version
				lbl_version_value.setText(capabilityMap.get("platformVersion"));
				dispose();

			}
		});
		btnOK.setBounds(208, 232, 100, 25);
		contentPane.add(btnOK);

		// 取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press cancel button");

				dispose();
			}
		});
		btnCancel.setBounds(320, 232, 100, 25);
		contentPane.add(btnCancel);
		// 解释名字
		JLabel lblNewLabel = new JLabel("<html>1.参数格式: \"参数名称,参数值;参数名称,参数值;\"<br>" + "2.空格表示不填</html>");
		lblNewLabel.setBounds(20, 170, 317, 50);
		contentPane.add(lblNewLabel);

	}

	/**
	 * 将Map转为换字符串,ps:key,value;
	 * 
	 * @param map
	 * @return
	 */
	protected String Map2String(Map<String, String> map) {
		StringBuffer strbuf = new StringBuffer();
		for (Entry<String, String> entry : map.entrySet()) {
			strbuf.append(entry.getKey() + "," + (entry.getValue().equals("") ? " " : entry.getValue()) + ";\n");
		}
		return strbuf.toString();
	}

	/**
	 * 将字符串转为Map
	 * 
	 * @param text
	 * @return
	 */
	protected void String2Map(String text) {
		capabilityMap.clear();
		for (String str : text.replaceAll("\n", "").split(";")) {
			if (str.contains(",")) {
				capabilityMap.put(str.split(",")[0].trim(), str.split(",")[1].trim());//
			}
		}
	}

}
