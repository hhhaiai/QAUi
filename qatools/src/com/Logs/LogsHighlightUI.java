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
import javax.swing.JColorChooser;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class LogsHighlightUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3424438367661406447L;
	Logger logger = LoggerFactory.getLogger(LogsHighlightUI.class);
	private JPanel contentPane;
	JTextArea textArea_hightlight;
	private JButton btnOK;
	private JButton btnCancel;
	JComboBox<String> Crashpath_list;
	private JButton btn_ColorBox;
	private Color color = Color.RED;//默认颜色
	/**
	 * Create the frame.
	 */
	public LogsHighlightUI(final LogsCheckLogs logsCheckLogs) {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("高亮设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);
		
		//本文栏
		textArea_hightlight = new JTextArea(com.Viewer.MainRun.paramsBean.getLogs_highlight().replaceAll(";", ";\n"));
		textArea_hightlight.setWrapStyleWord(true);
		textArea_hightlight.setLineWrap(true);
		JScrollPane scrollPane_highlight = new JScrollPane(textArea_hightlight);
		scrollPane_highlight.setBounds(20, 10, 400, 150);
		scrollPane_highlight.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(scrollPane_highlight);
		
		//确定按钮
		btnOK = new JButton("确定");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//判断格式是否正确
				if(!HelperUtil.check_format(textArea_hightlight.getText())){
					 JOptionPane.showMessageDialog(contentPane, "请按照指定格式输入文本!", 
						"消息", JOptionPane.ERROR_MESSAGE); 
					 return;
				}
				//存储
				com.Viewer.MainRun.paramsBean.setLogs_highlight( textArea_hightlight.getText().replaceAll("\n", ""));
				com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Logs_highlight", textArea_hightlight.getText().replaceAll("\n", ""));
				//更新
				logsCheckLogs.setMap_highlight(MainRun.paramsBean.getLogs_highlight());
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
		
		//颜色选择按钮
		btn_ColorBox = new JButton("颜色选择");
		btn_ColorBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				color = JColorChooser.showDialog(contentPane, "请选择高亮颜色:", color);
				if(color!=null){
					textArea_hightlight.append(color.getRGB()+";");	
				}
				logger.info("press btn_ColorBox button");
			}
		});
		btn_ColorBox.setBounds(88, 216, 100, 25);
		contentPane.add(btn_ColorBox);
		
		//解释名字
		JLabel lblNotes = new JLabel("高亮设置格式: \"字符串,颜色;字符串,颜色;\"");
		lblNotes.setBounds(20, 170, 317, 38);
		contentPane.add(lblNotes);
		

	}
	//应用当前高亮设置到现有文本
	public void update_CheckLogsTXT(){

	}

}
