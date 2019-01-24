package com.GetScreen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JCheckBox;

public class EditToolsUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 49126756462504642L;
	Logger logger = LoggerFactory.getLogger(EditToolsUI.class);
	ScreenShotUI screenshotui;
	private JToolBar toolBarDraw1;//定义按钮面板
	//定义工具栏图标的名称
	private String names[] = {"newfile","openfile","savefile","pen","line"
			   ,"rect","frect","oval","foval","circle","fcircle"
			   ,"roundrect","froundrect","rubber","color"
			   ,"stroke","word","revoke"};//定义工具栏图标的名称
	private Icon icons[];//定义图象数组
	
	private String tiptext[] = "新建一个图片,打开图片,保存图片,画笔,直线,矩形,实心矩形,椭圆,实心椭圆,圆,实心圆,圆角矩形,实心圆角矩形,橡皮擦,颜色,选择线条的粗细,文字,撤销".split(",");
	 JButton buttonDraw[];//定义工具条中的按钮组
	 JCheckBox chckbxBold;//bold
   	String[] fontName; 
	private JComboBox<?> stytles ;//工具条中的字体的样式（下拉列表）
	JLabel lblCoordinate;//鼠标位置
	JLabel lblDrawinfo;//Draw info
	int tiptextnum=3;
	/**
	 * Create the panel.
	 */
	public EditToolsUI() {
	      setSize(230,200);
	      setLocation(510,0);//location ui
	      setLayout(null);
	      //lbl edit tools
	      JLabel lblEditTools = new JLabel("涂鸦工具盒");
	      lblEditTools.setVerticalAlignment(SwingConstants.TOP);
	      lblEditTools.setBounds(0, 0, 110, 23);
	      add(lblEditTools);
	
	      //Tools bar draw1
	      toolBarDraw1 = new JToolBar(JToolBar.HORIZONTAL);
	      toolBarDraw1.setFloatable(false);
	      toolBarDraw1.setBounds(10, 20, 210, 28);
	      add(toolBarDraw1);
		   icons = new ImageIcon[names.length];
		   buttonDraw = new JButton[names.length];
		    for(int i = 3 ;i<8;i++)
		    {
		    	final int tempint=i;
		        icons[i] = new ImageIcon(getClass().getResource("/icon/"+names[i]+".jpg"));//获得图片（以类路径为基准）
		        buttonDraw[i] = new JButton("",icons[i]);//创建工具条中的按钮
		        buttonDraw[i].setToolTipText(tiptext[i]);//这里是鼠标移到相应的按钮上给出相应的提示
		        buttonDraw[i].setSize(35, 25);
		        toolBarDraw1.add(buttonDraw[i]);
		    //	buttonDraw[i].setBackground(Color.red);
		    	buttonDraw[i].addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						screenshotui.setCurrentChoice(tempint);
						screenshotui.createNewitem();
						screenshotui.repaint();
						tiptextnum=tempint;
						setlblDrawinfo(tiptext[tempint]);
					}
		    		
		    	});
		    }
		      //Tools bar draw2
		    JToolBar toolBarDraw2 = new JToolBar(SwingConstants.HORIZONTAL);
		    toolBarDraw2.setFloatable(false);
		    toolBarDraw2.setBounds(10, 53, 210, 28);
		    add(toolBarDraw2);
		    for(int i = 8 ;i<13;i++){
		    		final int tempint=i;
			        icons[i] = new ImageIcon(getClass().getResource("/icon/"+names[i]+".jpg"));//获得图片（以类路径为基准）
			        buttonDraw[i] = new JButton("",icons[i]);//创建工具条中的按钮
			        buttonDraw[i].setToolTipText(tiptext[i]);//这里是鼠标移到相应的按钮上给出相应的提示
			        buttonDraw[i].setSize(35, 25);
			        toolBarDraw2.add(buttonDraw[i]);
			    //	buttonDraw[i].setBackground(Color.red);
			    	buttonDraw[i].addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							screenshotui.setCurrentChoice(tempint);
							screenshotui.createNewitem();
							screenshotui.repaint();
							tiptextnum=tempint;
							setlblDrawinfo(tiptext[tempint]);
						}
			    		
			    	});
			}
		     //button draw13 Eraser
		      JButton btnDraw13 = new JButton("",new ImageIcon(getClass().getResource("/icon/"+names[13]+".jpg")));
		      btnDraw13.setToolTipText(tiptext[13]);
		      btnDraw13.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						screenshotui.setCurrentChoice(13);
						screenshotui.createNewitem();
						screenshotui.repaint();
						tiptextnum=13;
						setlblDrawinfo(tiptext[tiptextnum]);
					    logger.info("tap draw13 Eraser button");
					}
				});
		      btnDraw13.setBounds(45, 81, 35, 25);
		      add(btnDraw13);
		      //button draw14 color
		      JButton btnDraw14Color = new JButton("",new ImageIcon(getClass().getResource("/icon/"+names[14]+".jpg")));
		      btnDraw14Color.setToolTipText(tiptext[14]);
		      btnDraw14Color.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						screenshotui.chooseColor();
						setlblDrawinfo(tiptext[tiptextnum]);
						logger.info("tap draw14 color button");
					}
				});
		      btnDraw14Color.setBounds(150, 81, 35, 25);
		      add(btnDraw14Color);
		      //button draw15 Thickness
		      JButton btnDraw15Thickness = new JButton("",new ImageIcon(getClass().getResource("/icon/"+names[15]+".jpg")));
		      btnDraw15Thickness.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						screenshotui.setStroke();
						setlblDrawinfo(tiptext[tiptextnum]);
						logger.info("tap draw15 Thickness button");
					}
				});
		      btnDraw15Thickness.setToolTipText(tiptext[15]);
		      btnDraw15Thickness.setBounds(115, 81, 35, 25);
		      add(btnDraw15Thickness);
		      //button draw16 Text
		      JButton btnDraw16Text = new JButton("",new ImageIcon(getClass().getResource("/icon/"+names[16]+".jpg")));
		      btnDraw16Text.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
//						JOptionPane.showMessageDialog(null, "请单击画板以确定输入文字的位置！","提示"
//								,JOptionPane.INFORMATION_MESSAGE); 
						screenshotui.setCurrentChoice(14);
						screenshotui.createNewitem();
						screenshotui.repaint();
						tiptextnum=16;
						setlblDrawinfo(tiptext[tiptextnum]);
						logger.info("tap draw16 Text button");
					}
				});
		      btnDraw16Text.setToolTipText(tiptext[16]);
		      btnDraw16Text.setBounds(80, 81, 35, 25);
		      add(btnDraw16Text);
			   //Draw revoke17
			   JButton btnDrawBack = new JButton("", new ImageIcon(getClass().getResource("/icon/"+names[17]+".jpg")));
			   btnDrawBack.addActionListener(new ActionListener() {
			   	public void actionPerformed(ActionEvent arg0) {
			   		if(screenshotui.getIndex()>0){
			   		screenshotui.setIndex(screenshotui.getIndex()-1);
			   		screenshotui.createNewitem();
			   		screenshotui.repaint();
			   		}
			   		logger.info("tap draw back doodle button: "+screenshotui.getIndex());
			   	}
			   });
			   btnDrawBack.setToolTipText(tiptext[17]);
			   btnDrawBack.setBounds(10, 81, 35, 25);
			   add(btnDrawBack);
		      //text bold 
		      chckbxBold = new JCheckBox("粗体");
		      chckbxBold.setFont(new Font(Font.DIALOG,Font.BOLD,10));//设置字体
		      chckbxBold.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
							if(chckbxBold.isSelected()){
								screenshotui.setFont(1, Font.BOLD);//2=斜体
							}else{
								screenshotui.setFont(1, Font.PLAIN);
							}
							logger.info("tap bold or plain checkbox");
					}
				}); 
		      chckbxBold.setSelected(true);
		      chckbxBold.setBounds(10, 107, 61, 23);
		      add(chckbxBold);
		      //字体选择
			   GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();//计算机上字体可用的名称
		       fontName = ge.getAvailableFontFamilyNames();
		       stytles = new JComboBox<Object>(fontName);//下拉列表的初始化
			   stytles.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					screenshotui.stytle = fontName[stytles.getSelectedIndex()];
				}
				   
			   });//stytles注册监听
			   stytles.setMaximumSize(new Dimension(400,50));//设置下拉列表的最大尺寸
			   stytles.setMinimumSize(new  Dimension(250,40));
			   stytles.setBounds(80, 108, 120, 23);
			   add(stytles);
			   
			   //coordinate
			   lblCoordinate = new JLabel("坐标: 0,0");
			   lblCoordinate.setBounds(10, 155, 210, 15);
			   add(lblCoordinate);
			   
			   //Save doodle
			   JButton btnSavedoodle = new JButton("保存涂鸦");
			   btnSavedoodle.addActionListener(new ActionListener() {
			   	public void actionPerformed(ActionEvent arg0) {
			   		screenshotui.saveImage();
			   		logger.info("tap save doodle button");
			   	}
			   });
			   btnSavedoodle.setBounds(0, 175, 100, 25);
			   add(btnSavedoodle);
			   //Draw info
			   lblDrawinfo = new JLabel("画笔,粗细=4,颜色=255,0,0");
			   lblDrawinfo.setBounds(10, 133, 280, 15);
			   add(lblDrawinfo);
			   //clear button
			   JButton btnClear = new JButton("清除涂鸦");
			   btnClear.addActionListener(new ActionListener() {
			   	public void actionPerformed(ActionEvent e) {
			   		screenshotui.ClearNoodle();
			   		logger.info("tap clear doodle button");
			   	}
			   });
			   btnClear.setBounds(109, 175, 100, 25);
			   add(btnClear);

	}
	public void setscreenshotui(ScreenShotUI screenshotui){
		this.screenshotui=screenshotui;
	}
	//set lblCoordinate
	public void setlblCoordinate(String text){
		lblCoordinate.setText(text);
	}
	//set draw info
	public void setlblDrawinfo(String tiptext){
		lblDrawinfo.setText(tiptext+",粗细="+(int)(Float.parseFloat(screenshotui.getStroke()))+
				",颜色="+screenshotui.R+","+screenshotui.G+","+screenshotui.B);
	}
	
}
