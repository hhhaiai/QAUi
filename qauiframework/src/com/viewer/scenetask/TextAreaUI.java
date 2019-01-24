package com.viewer.scenetask;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.helper.HelperUtil;
import com.viewer.main.MainRun;

public class TextAreaUI extends TextShowUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4235877892202534673L;

	public TextAreaUI(int width, int hight) {
		super(width, hight);
		JButton btn_statistics = new JButton("统计");
		btn_statistics.addActionListener(e -> {
			logger.info("press btn_statistics button");
			JOptionPane.showMessageDialog(this, Statistics(), "统计信息", JOptionPane.INFORMATION_MESSAGE,
					MainRun.settingsBean.getLogo());
		});
		btn_statistics.setBounds(610, 609, 100, 25);
		add(btn_statistics);

		setEdit(false);
		setIcon(MainRun.settingsBean.getLogo());
		setSaveLogFilePath(MainRun.settingsBean.getUiReportPath());
	}

	/**
	 * 信息统计
	 * 
	 * @return
	 */
	private String Statistics() {
		StringBuffer Buf = new StringBuffer();
		String text = textAreaShow.getText();
		Buf.append("常规统计:\n");
		Buf.append("[WARN]出现" + HelperUtil.getStringShowCount(text, "[WARN]") + "次\n");
		Buf.append("[ERROR]出现" + HelperUtil.getStringShowCount(text, "[ERROR]") + "次\n");
		Buf.append("[STEP]出现" + HelperUtil.getStringShowCount(text, "[STEP]") + "次\n");
		Buf.append("[CHECK]出现" + HelperUtil.getStringShowCount(text, "[CHECK]") + "次\n");
		Buf.append("[ASSERT]出现" + HelperUtil.getStringShowCount(text, "[ASSERT]") + "次\n");
		Buf.append("[CUSTOMER]出现" + HelperUtil.getStringShowCount(text, "[CUSTOMER]") + "次\n");
		Buf.append("[CUSLOG]出现" + HelperUtil.getStringShowCount(text, "[CUSLOG]") + "次\n");
		Buf.append("[CHECK]出现" + HelperUtil.getStringShowCount(text, "[CHECK]") + "次\n");
		Buf.append("[APPLOG]出现" + HelperUtil.getStringShowCount(text, "[APPLOG]") + "次\n");
		Buf.append("[SYSLOG]出现" + HelperUtil.getStringShowCount(text, "[SYSLOG]") + "次\n");
		Buf.append("[RESULT]出现" + HelperUtil.getStringShowCount(text, "[RESULT]") + "次\n");
		Buf.append("结果查询:\n");
		String[] strings = text.split("\n");
		int count = 0;
		for (String str : strings) {
			if (str.contains("[TASKDONE]")) {
				count++;
				Buf.append(count + ": " + str + "\n");
			}
		}
		return Buf.toString();
	}
}
