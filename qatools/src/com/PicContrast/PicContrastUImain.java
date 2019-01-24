package com.PicContrast;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

public class PicContrastUImain extends JPanel {
	Logger logger = LoggerFactory.getLogger(PicContrastUImain.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -3510039751443904728L;
	
	PicContrast picContrast=new PicContrast();
	JTextArea txtrPathA;
	JTextArea txtrPathB;
	File[] filesA;//路径A文件
	File[] filesB;//路径B文件
	//日记栏
	JTextArea textAreaLogs_Pass;
	private JTextArea textAreaLogs_Fail;
	JScrollPane scrollPaneLogs_Pass;
	private JScrollPane scrollPaneLogs_Fail;
	private JButton btn_SelectPathA;
	private JButton btn_SelectPathB;
	private JLabel lbl_TotalContrast;
	private JLabel lbl_Pass;
	private JLabel lbl_Fail;
	JLabel lblPathA;
	JLabel lblPathB;
	JButton btn_Contrast;
	private JLabel lblStatistics;
	private JLabel lblPassbox;
	private JLabel lblFailbox;
	
	JCheckBox checkBox_namesimple;
	/**
	 * Create the panel.
	 */
	public PicContrastUImain() {
		setSize(740, 500);
		setLocation(0, 100);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		//日记栏Pass
		textAreaLogs_Pass = new JTextArea();
		textAreaLogs_Pass.setWrapStyleWord(true);
		textAreaLogs_Pass.setLineWrap(true);
		scrollPaneLogs_Pass=new JScrollPane(textAreaLogs_Pass);
		scrollPaneLogs_Pass.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneLogs_Pass.setBounds(10, 33, 720, 128);
		add(scrollPaneLogs_Pass);
		//日记栏Fail
		textAreaLogs_Fail = new JTextArea();
		textAreaLogs_Fail.setWrapStyleWord(true);
		textAreaLogs_Fail.setLineWrap(true);
		scrollPaneLogs_Fail=new JScrollPane(textAreaLogs_Fail);
		scrollPaneLogs_Fail.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneLogs_Fail.setBounds(10, 188, 720, 128);
		add(scrollPaneLogs_Fail);
		//init
		picContrast.init(textAreaLogs_Pass,textAreaLogs_Fail);
		//路径A
		txtrPathA = new JTextArea();
		txtrPathA.setWrapStyleWord(true);
		txtrPathA.setLineWrap(true);
		txtrPathA.setEditable(false);
		txtrPathA.setBorder(new LineBorder(Color.BLACK));
		txtrPathA.setBounds(121, 351, 174, 63);
		add(txtrPathA);
		//路径B
		txtrPathB = new JTextArea();
		txtrPathB.setWrapStyleWord(true);
		txtrPathB.setLineWrap(true);
		txtrPathB.setEditable(false);
		txtrPathB.setBorder(new LineBorder(Color.BLACK));
		txtrPathB.setBounds(446, 351, 174, 63);
		add(txtrPathB);
		
		//选择路径A按钮
		JButton btn_SelectFileA = new JButton("选择文件");
		btn_SelectFileA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filesA =picContrast.selectfiles();
				if(filesA==null){
					txtrPathA.setText("");
					return;
				}else if(filesA.length==1){
					txtrPathA.setText(filesA[0].getAbsolutePath());
				}else if(filesA.length>1){
					txtrPathA.setText("选中 "+filesA.length+"个文件...");
				}
				logger.info("press btn_SelectPathA button");
			}
		});
		btn_SelectFileA.setBounds(305, 349, 100, 25);
		add(btn_SelectFileA);
		
		//选择路径B按钮
		JButton btn_SelectFileB = new JButton("选择文件");
		btn_SelectFileB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filesB =picContrast.selectfiles();
				if(filesB==null){
					txtrPathB.setText("");
					return;
				}else if(filesB.length==1){
					txtrPathB.setText(filesB[0].getAbsolutePath());
				}else if(filesB.length>1){
					txtrPathB.setText("选中 "+filesB.length+"个文件...");
				}
				logger.info("press btn_SelectPathB button");
			}
		});
		btn_SelectFileB.setBounds(630, 349, 100, 25);
		add(btn_SelectFileB);
		
		//选择文件夹A
		btn_SelectPathA = new JButton("选择文件夹");
		btn_SelectPathA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filesA =picContrast.selectfloder();
				if(filesA==null){
					txtrPathA.setText("");
					return;
				}else {
					txtrPathA.setText(filesA[0].getParent());
				}
				logger.info("press btn_SelectPathA button");
			}
		});
		btn_SelectPathA.setBounds(305, 389, 100, 25);
		add(btn_SelectPathA);
		
		//选择文件夹B
		btn_SelectPathB = new JButton("选择文件夹");
		btn_SelectPathB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filesB =picContrast.selectfloder();
				if(filesB==null){
					txtrPathB.setText("");
					return;
				}else {
					txtrPathB.setText(filesB[0].getParent());
				}
				logger.info("press btn_SelectPathB button");
			}
		});
		btn_SelectPathB.setBounds(630, 389, 100, 25);
		add(btn_SelectPathB);
		
		//对比按钮
		btn_Contrast = new JButton("对比");
		btn_Contrast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(filesA!=null&&filesB!=null){
					picContrast.RunThread(filesA, filesB,checkBox_namesimple.isSelected());	
					btn_Contrast.setEnabled(false);
				}
				logger.info("press btn_Contrast button");
			}
		});
		btn_Contrast.setBounds(630, 443, 100, 25);
		add(btn_Contrast);
		
		lblPathA = new JLabel("路径A:");
		lblPathA.setBounds(121, 326, 100, 15);
		add(lblPathA);
		
		lblPathB = new JLabel("路径B:");
		lblPathB.setBounds(446, 326, 94, 15);
		add(lblPathB);
		
		lbl_TotalContrast = new JLabel("对比组数:");
		lbl_TotalContrast.setBounds(10, 351, 101, 15);
		add(lbl_TotalContrast);
		
		lbl_Pass = new JLabel("通过:");
		lbl_Pass.setBounds(10, 374, 101, 15);
		add(lbl_Pass);
		
		lbl_Fail = new JLabel("未通过:");
		lbl_Fail.setBounds(10, 399, 101, 15);
		add(lbl_Fail);
		
		lblStatistics = new JLabel("统计:");
		lblStatistics.setBounds(10, 326, 101, 15);
		add(lblStatistics);
		
		lblPassbox = new JLabel("通过项:");
		lblPassbox.setBounds(10, 10, 101, 15);
		add(lblPassbox);
		
		lblFailbox = new JLabel("失败项:");
		lblFailbox.setBounds(10, 163, 101, 15);
		add(lblFailbox);
		
		checkBox_namesimple = new JCheckBox("名称简化");
		checkBox_namesimple.setBounds(496, 444, 103, 23);
		add(checkBox_namesimple);
		//开始更新
		Result_Monitor();
	}
	//更新统计数据
	public void Result_Monitor(){
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SwingUtilities.invokeLater(new Runnable() {   
				      @Override  
				      public void run() {   
						lbl_TotalContrast.setText("对比组数: "+picContrast.getTotal());
						lbl_Pass.setText("通过: "+picContrast.getPass());
						lbl_Fail.setText("<html><font color=\"#FF0000\">未通过: "+picContrast.getFail()+"</font></html>");
						if(picContrast.getTotal()==(picContrast.getFail()+picContrast.getPass())){//当完成后才恢复
							btn_Contrast.setEnabled(true);
						}
				      }
				});
			}
		}, 800,800);
	}
}
