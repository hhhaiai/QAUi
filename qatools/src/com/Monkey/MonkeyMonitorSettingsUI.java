package com.Monkey;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class MonkeyMonitorSettingsUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -112570228327285969L;
	Logger logger = LoggerFactory.getLogger(MonkeyMonitorSettingsUI.class);
	private JPanel contentPane;
	JCheckBox chckbxFillter;//显示过滤重复
	boolean showduplicate;
	private JLabel lblViewRow;
	private JFormattedTextField formattedTextFieldViewrow;
	private JLabel lblAnalysisRow;
	private JFormattedTextField formattedTextFieldAnalysiswords;
	private JLabel lblShowDuplicateIssue;
	private JButton btnOk;
	private JButton btnCancel;
	int arow,arowword;
	private JButton btnReset;
	JCheckBox chckbxReconnect;
	boolean isreconnect;
	/**
	 * Create the frame.
	 */
	public MonkeyMonitorSettingsUI() {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("Monkey监控设置");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);
		
		JLabel lblAnalysisSettings = new JLabel("分析设置:");
		lblAnalysisSettings.setBounds(10, 10, 128, 15);
		contentPane.add(lblAnalysisSettings);
		//show duplicate checkbox
		chckbxFillter = new JCheckBox("");
		//get showduplicate value
		if(showduplicate){
			chckbxFillter.setSelected(true);
		}else{
			chckbxFillter.setSelected(false);
		}
		chckbxFillter.setBounds(208, 35, 50, 15);
		chckbxFillter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					if(chckbxFillter.isSelected()){
						showduplicate=true;
					}else{
						showduplicate=false;
					}
					logger.info("set showduplicate="+showduplicate);
			}
		}); 
		contentPane.add(chckbxFillter);
		
		lblViewRow = new JLabel("分析和显示行数:");
		lblViewRow.setBounds(25, 65, 173, 15);
		contentPane.add(lblViewRow);
		//view and analysis row value
		formattedTextFieldViewrow = new JFormattedTextField();
		//get arow value
		formattedTextFieldViewrow.setText(arow+"");
		formattedTextFieldViewrow.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e)
		    {
		     if ((e.getKeyChar() >= KeyEvent.VK_0 && e.getKeyChar() <= KeyEvent.VK_9) 
		      || e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB
		      || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE 
		      || e.getKeyChar() == KeyEvent.VK_LEFT || e.getKeyChar() == KeyEvent.VK_RIGHT 
		      || e.getKeyChar() == KeyEvent.VK_ESCAPE){  return;   }else{e.consume();}
		    }
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		formattedTextFieldViewrow.setBounds(208, 62, 50, 21);
		contentPane.add(formattedTextFieldViewrow);
		
		lblAnalysisRow = new JLabel("每行分析字数:");
		lblAnalysisRow.setBounds(25, 91, 173, 15);
		contentPane.add(lblAnalysisRow);
		//analysis words per line value
		formattedTextFieldAnalysiswords = new JFormattedTextField();
		formattedTextFieldAnalysiswords.setText(arowword+"");
		formattedTextFieldAnalysiswords.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e)
		    {
		     if ((e.getKeyChar() >= KeyEvent.VK_0 && e.getKeyChar() <= KeyEvent.VK_9) 
		      || e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB
		      || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE 
		      || e.getKeyChar() == KeyEvent.VK_LEFT || e.getKeyChar() == KeyEvent.VK_RIGHT 
		      || e.getKeyChar() == KeyEvent.VK_ESCAPE){  return;   }else{e.consume();}
		    }
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		formattedTextFieldAnalysiswords.setBounds(208, 88, 50, 21);
		contentPane.add(formattedTextFieldAnalysiswords);
		
		lblShowDuplicateIssue = new JLabel("显示重复的issue:");
		lblShowDuplicateIssue.setBounds(25, 35, 173, 15);
		contentPane.add(lblShowDuplicateIssue);
		//ok button
		btnOk = new JButton("确定");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int temparow=Integer.parseInt(formattedTextFieldViewrow.getText());
				int temparowword=Integer.parseInt(formattedTextFieldAnalysiswords.getText());
				if(temparow<50&&temparow>0){
					arow=temparow;
					com.Viewer.MainRun.paramsBean.setMonkey_arow(temparow+"");
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_arow", temparow+"");
					logger.info("set arow="+arow);
				}else{
					JOptionPane.showMessageDialog(contentPane, "分析和显示行数应该在1到50之间.", 
							"消息", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				if(temparowword>0&&temparowword<500){
					arowword=temparowword;
					com.Viewer.MainRun.paramsBean.setMonkey_arowword(temparowword+"");
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_arowword", temparowword+"");
					logger.info("set arowword="+arowword);
				}else{
					JOptionPane.showMessageDialog(contentPane, "每行分析字数应该在1到500之间.", 
							"消息", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				if(showduplicate){
					com.Viewer.MainRun.paramsBean.setMonkey_showduplicate("true");
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_showduplicate", "true");
				}else{
					com.Viewer.MainRun.paramsBean.setMonkey_showduplicate("false");
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_showduplicate", "false");
				}
				if(isreconnect){
					com.Viewer.MainRun.paramsBean.setMonkey_isreconnect("true");
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_isreconnect", "true");
				}else{
					com.Viewer.MainRun.paramsBean.setMonkey_isreconnect("false");
					com.Viewer.MainRun.xmlOperationUtil.XMLChanger("Monkey_isreconnect", "false");
				}
				dispose();
				logger.info("monkey monitor settring ok button");
			}
		});
		btnOk.setBounds(227, 218, 100, 25);
		contentPane.add(btnOk);
		//cancel button
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				logger.info("monkey monitor settring cancel button");
			}
		});
		btnCancel.setBounds(337, 218, 100, 25);
		contentPane.add(btnCancel);
		//reset button
		btnReset = new JButton("恢复默认");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				com.Viewer.MainRun.paramsBean.setMonkey_arow(15+"");
				com.Viewer.MainRun.paramsBean.setMonkey_arowword(""+80);
				com.Viewer.MainRun.paramsBean.setMonkey_showduplicate("false");
				com.Viewer.MainRun.paramsBean.setMonkey_isreconnect("true");
//				com.Viewer.QAToolsRun.xmlOperationUtil.XMLChanger("Monkey_arow", "15");
//				com.Viewer.QAToolsRun.xmlOperationUtil.XMLChanger("Monkey_arowword", "80");
//				com.Viewer.QAToolsRun.xmlOperationUtil.XMLChanger("Monkey_showduplicate", "false");
//				com.Viewer.QAToolsRun.xmlOperationUtil.XMLChanger("Monkey_isreconnect", "true");
				initvalue();
				logger.info("monkey monitor settring reset button");
			}
		});
		btnReset.setBounds(104, 219, 100, 25);
		contentPane.add(btnReset);
		//Global setting
		JLabel lblSettings = new JLabel("全局设置:");
		lblSettings.setBounds(10, 136, 144, 15);
		contentPane.add(lblSettings);
		//reconnect check box
		chckbxReconnect = new JCheckBox("断线重连");
		if(isreconnect){
			chckbxReconnect.setSelected(true);
		}else{
			chckbxReconnect.setSelected(false);
		}
		chckbxReconnect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					if(chckbxReconnect.isSelected()){
						isreconnect=true;
					}else{
						isreconnect=false;
					}
					logger.info("set isreconnect="+isreconnect);
			}
		}); 
		chckbxReconnect.setBounds(25, 157, 190, 23);
		contentPane.add(chckbxReconnect);
		
		//init value
		initvalue();
	}
	//init 
	public void initvalue(){
		arow=Integer.parseInt(com.Viewer.MainRun.paramsBean.getMonkey_arow());
		arowword=Integer.parseInt(com.Viewer.MainRun.paramsBean.getMonkey_arowword());
		if(com.Viewer.MainRun.paramsBean.getMonkey_showduplicate().equals("true")){
			showduplicate=true;	
		}else{
			showduplicate=false;
		}
		if(com.Viewer.MainRun.paramsBean.getMonkey_isreconnect().equals("true")){
			isreconnect=true;
		}else{
			isreconnect=false;
		}
		
		chckbxReconnect.setSelected(isreconnect);
		chckbxFillter.setSelected(showduplicate);
		formattedTextFieldViewrow.setText(arow+"");
		formattedTextFieldAnalysiswords.setText(arowword+"");
		logger.info("get arow="+arow+" arowword="+arowword+" showduplicate="+showduplicate+" Isreconnect="+isreconnect);
		
	}
	//get arow
	public int getArow(){
		return arow;
	}
	//get arowword
	public int getArowword(){
		return arowword;
	}
	//get showduplicate
	public boolean getShowduplicate(){
		return showduplicate;
	}
	//get isreconnect
	public boolean getIsreconnect(){
		return isreconnect;
	}

}
