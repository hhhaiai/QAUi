package com.viewer.scenetask;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.bcel.generic.AALOAD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.TestCaseBean;
import com.task.FactoryScene;
import com.task.TestCase;
import com.viewer.main.MainRun;

public class CaseListBoxUI extends JFrame {
	Logger logger=LoggerFactory.getLogger(CaseListBoxUI.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5689338645043186746L;
	private JPanel contentPane;
	String scenename;
	
	Map<TestCaseBean, Boolean> tempcaserunMap=new LinkedHashMap<>();
	
	JButton btnOK;
	JButton btnCancel;
	private JButton btnSelectAll;
	ArrayList<JCheckBox> checkBoxslist=new ArrayList<>();
	boolean isselectall=true;
	/**
	 * Create the frame.
	 */
	public CaseListBoxUI(Map<TestCaseBean, Boolean> caserunMap,String scenename) {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("用例列表");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());
		
		/**
		 * 初始化
		 */
		this.scenename=scenename;
		caserunMap.entrySet().forEach(e->{tempcaserunMap.put(e.getKey(), e.getValue());});
		logger.info("init runcase:"+showinfo(caserunMap));
		JScrollPane scroll = new JScrollPane(CaseJPanel(this.scenename));
		scroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		scroll.setBounds(20, 10, 400, 225);
		contentPane.add(scroll);
		
		//确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				setCaseruninfo(caserunMap);
				dispose();
				
			}
		});
		btnOK.setBounds(241, 247, 100, 25);
		contentPane.add(btnOK);
		//取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press cancel button");
				dispose();
			}
		});
		btnCancel.setBounds(344, 247, 100, 25);
		contentPane.add(btnCancel);
		//全选按钮
		btnSelectAll = new JButton("反选");
		btnSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btnSelectAll button");
				if(isselectall) {
					btnSelectAll.setText("全选");
					isselectall=false;
					checkBoxslist.forEach(i->{if(i.isEnabled())i.setSelected(false);});
				}else {
					btnSelectAll.setText("反选");
					isselectall=true;
					checkBoxslist.forEach(i->{if(i.isEnabled())i.setSelected(true);});
				}
			}
		});
		btnSelectAll.setBounds(30, 247, 100, 25);
		contentPane.add(btnSelectAll);
	}
	/**
	 * 设置需要执行的用例序号   1,2,3
	 */
	private void setCaseruninfo(Map<TestCaseBean, Boolean> caserunMap){
		caserunMap.clear();
		for(Entry<TestCaseBean, Boolean>  entry:tempcaserunMap.entrySet()){
			caserunMap.put(entry.getKey(), entry.getValue());
		}
		logger.info("change runcase:"+showinfo(caserunMap));
	}
	/**
	 * 返回需要执行的case信息
	 * @param caserunMap
	 * @return
	 */
	private String showinfo(Map<TestCaseBean, Boolean> caserunMap) {
		StringBuffer stringBuffer=new StringBuffer();
		caserunMap.entrySet().forEach(e->{if(e.getValue()) {stringBuffer.append(e.getKey().getNo()+",");}});
		return stringBuffer.toString().substring(0, stringBuffer.toString().length());
	}
	/**
	 * 用例界面
	 * @return
	 */
	private JPanel CaseJPanel(String name) {
		JPanel jPanel=new JPanel();
		jPanel.setLayout(new GridBagLayout());
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jPanel.setSize(400, 1000);
		int gridy=0;
		GridBox gridBox;
		gridBox=new GridBox();
		gridBox.setInsets(0, 0, 2, 2);
		for(TestCaseBean testCaseBean:FactoryScene.getCase(name)){
			JCheckBox checkBox=new JCheckBox();
			checkBoxslist.add(checkBox);
			//初始值判断
			for(Entry<TestCaseBean, Boolean> entry:tempcaserunMap.entrySet()) {
				if(entry.getKey().getNo()==testCaseBean.getNo()&&entry.getValue()) {
					checkBox.setSelected(true);
					break;
				}
			}	
			if(testCaseBean.getNo()==0){
				//checkBox.setSelected(false);
				checkBox.setEnabled(false);
			}
			jPanel.add(checkBox, gridBox.setGridWH(1, 1).setGridXY(0, gridy));
			JButton button=new JButton(testCaseBean.getName());
			jPanel.add(button, gridBox.setGridWH(2, 1).setGridXY(1, gridy));
			JLabel desc=new JLabel(testCaseBean.getDesc());
			jPanel.add(desc, gridBox.setGridWH(2, 1).setGridXY(3, gridy));
			gridy++;
			//控件事件
			checkBox.addItemListener(e->{
				for(Entry<TestCaseBean, Boolean> entry:tempcaserunMap.entrySet()) {
					if(entry.getKey().getNo()==testCaseBean.getNo()) {
						entry.setValue(e.getStateChange()==ItemEvent.SELECTED?true:false);
						break;
					}
				}
			});
			button.addActionListener(e->{
				for(Entry<TestCaseBean, Boolean> entry:tempcaserunMap.entrySet()) {
					if(entry.getKey().getNo()==testCaseBean.getNo()) {
						JOptionPane.showMessageDialog(contentPane, 
								"执行序号: "+entry.getKey().getNo()+"\n"
										+ "用例名称: "+entry.getKey().getName()+"\n"
										+ "方法名称: "+entry.getKey().getMethodName()+"\n"
										+ "描述: "+entry.getKey().getDesc()+"\n"
										+ "执行次数: "+entry.getKey().getRuntime()+"\n", 
								"<<"+entry.getKey().getName()+">>用例详情", JOptionPane.INFORMATION_MESSAGE, MainRun.settingsBean.getLogo());
						break;
					}
				}
			
			});
			jPanel.validate();//重新布局
			jPanel.repaint(); //重绘界面 
		}
		return jPanel;
	}
	
}
