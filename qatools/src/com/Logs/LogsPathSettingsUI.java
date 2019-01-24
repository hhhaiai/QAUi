package com.Logs;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Viewer.MainRun;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class LogsPathSettingsUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3424438367661406447L;
	Logger logger = LoggerFactory.getLogger(LogsPathSettingsUI.class);
	private JPanel contentPane;
	JTextArea textAreaFilterPackages;
	private JButton btnOK;
	private JButton btnCancel;
	JComboBox<String> Crashpath_list;
	/**
	 * Create the frame.
	 */
	public LogsPathSettingsUI(final JComboBox<String> Crashpath_list) {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("应用崩溃日记路径设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);
		
		//本文栏
		textAreaFilterPackages = new JTextArea(com.Viewer.MainRun.paramsBean.getLogs_crashPath().replaceAll(";", ";\n"));
		textAreaFilterPackages.setWrapStyleWord(true);
		textAreaFilterPackages.setLineWrap(true);
		JScrollPane scrollPaneFilterPackages = new JScrollPane(textAreaFilterPackages);
		scrollPaneFilterPackages.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneFilterPackages.setBounds(20, 10, 400, 150);
		contentPane.add(scrollPaneFilterPackages);
		
		//确定按钮
		btnOK = new JButton("确定");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//判断格式是否正确
				if(!HelperUtil.check_format(textAreaFilterPackages.getText())){
					 JOptionPane.showMessageDialog(contentPane, "请按照指定格式输入文本!", 
						"消息", JOptionPane.ERROR_MESSAGE); 
					 return;
				}
				//存储
				com.Viewer.MainRun.paramsBean.setLogs_crashPath( textAreaFilterPackages.getText().replaceAll("\n", ""));
				com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Logs_crashPath", textAreaFilterPackages.getText().replaceAll("\n", ""));
				//更新list
				setCrashpath_list(Crashpath_list);
				dispose();
				logger.info("press ok button");
			}
		});
		btnOK.setBounds(210, 218, 100, 25);
		contentPane.add(btnOK);
		
		//取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				logger.info("press cancel button");
			}
		});
		btnCancel.setBounds(320, 218, 100, 25);
		contentPane.add(btnCancel);
		//解释名字
		JLabel lblNewLabel = new JLabel("路径格式: \"名称,日记路径;名称,日记路径;\"");
		lblNewLabel.setBounds(20, 170, 317, 38);
		contentPane.add(lblNewLabel);

	}
	//set crash path list
	public void setCrashpath_list(JComboBox<String> Crashpath_list){
		ArrayList<String> arrayList=new ArrayList<>();
		String[] strings=com.Viewer.MainRun.paramsBean.getLogs_crashPath().split(";");
		for(String str: strings){
			if(str.contains(",")){
				arrayList.add("<html><font color=\"#FF0000\">"+str.split(",")[0]+"</font>="+str.split(",")[1]+"</html>");
			}else{
				continue;
			}
		}
	    Crashpath_list.removeAllItems();
  		for(String str:arrayList){
  			Crashpath_list.addItem(str);
  		} 
	}

}
