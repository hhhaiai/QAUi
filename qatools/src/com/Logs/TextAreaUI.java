package com.Logs;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Util.TimeUtil;
import com.Viewer.MainRun;

import javax.swing.SwingConstants;

public class TextAreaUI extends JPanel {
	Logger logger=LoggerFactory.getLogger(TextAreaUI.class);
	
	JTextArea textAreaShow = new JTextArea();;
	JScrollPane scrollPane = new JScrollPane(textAreaShow);
	private JButton btn_clear;
	private JFormattedTextField frmtdtxtfld_Search;
	private JButton btn_searchNext;
	private JButton btn_searchPre;
	
	Highlighter highLighter;
	boolean iscancel=false;
	HashMap<Integer,Integer> searchMap=new LinkedHashMap<Integer,Integer>();//保存关键字符串搜索索引   序号,索引
	Map<Integer, Object> highlighterlist=new LinkedHashMap<Integer,Object>();//搜索高亮对象列表
	Object highlighterObj;//搜索指定位置的高亮对象
	//查找
	int searchNum=-1;
	int searchCount=0;
	boolean issearchNext=false;
	JLabel lbl_find;
	JLabel lbl_linenum;
	private JButton btn_save;
	/**
	 * Create the panel.
	 */
	public TextAreaUI() {
		setSize(720,650);
		setLayout(null);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		highLighter=textAreaShow.getHighlighter();
		
		textAreaShow.setWrapStyleWord(true);
		textAreaShow.setLineWrap(true);
		textAreaShow.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				issearchNext=true;
				setLineInfo();
			}
		});
		textAreaShow.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				setLineInfo();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		textAreaShow.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//无选中内容才会自动向下滚动
				if(textAreaShow.getSelectedText()==null)textAreaShow.setCaretPosition(textAreaShow.getText().length());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
		});
		//textAreaShow.setEditable(false);//高亮原因
		scrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setBounds(10, 6, 700, 560);
		add(scrollPane);
		
		//清空文本按钮
		btn_clear = new JButton("清空");
		//btn_clear.setEnabled(false);
		btn_clear.addActionListener(e->{
			logger.info("press btn_clear button");
			highLighter.removeAllHighlights();
			textAreaShow.setText("");
		});
		btn_clear.setBounds(488, 581, 100, 25);
		add(btn_clear);
		
		//查找字符串
		lbl_find = new JLabel("");
		lbl_find.setBounds(10, 609, 151, 16);
		add(lbl_find);
		
		//查找文本框
		frmtdtxtfld_Search = new JFormattedTextField();
		//frmtdtxtfld_Search.setEnabled(false);
		frmtdtxtfld_Search.setBounds(10, 578, 375, 28);
		frmtdtxtfld_Search.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				searchNum=-1;
				issearchNext=true;
				searchCount=searchTxt(frmtdtxtfld_Search.getText());
				lbl_find.setText("共"+searchCount+"条");
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(frmtdtxtfld_Search);
		
		//查找下一个按钮
		btn_searchNext = new JButton("查找下一个");
		btn_searchNext.setFocusable(false);
		btn_searchNext.addActionListener(e->{
			logger.info("press btn_searchNext button");
			searchNum++;
			if(searchNum>(searchCount-1))searchNum=0;
			searchTxtNext(frmtdtxtfld_Search.getText(),true);
			lbl_find.setText(searchCount!=0?"共"+searchCount+"条,第"+(searchNum+1)+"条":"共0条");
		});
		//btn_searchNext.setEnabled(false);
		btn_searchNext.setBounds(173, 609, 100, 25);
		add(btn_searchNext);
		
		//查找上一个按钮
		btn_searchPre = new JButton("查找上一个");
		btn_searchPre.setFocusable(false);
		btn_searchPre.addActionListener(e->{
			logger.info("press btn_searchPre button");
			searchNum--;
			if(searchNum<0)searchNum=searchCount-1;
			searchTxtNext(frmtdtxtfld_Search.getText(),false);
			lbl_find.setText(searchCount!=0?"共"+searchCount+"条,第"+(searchNum+1)+"条":"共0条");
		});
		//btn_searchPre.setEnabled(false);
		btn_searchPre.setBounds(285, 609, 100, 25);
		add(btn_searchPre);
		
		lbl_linenum = new JLabel("");
		lbl_linenum.setBounds(452, 609, 129, 16);
		add(lbl_linenum);
		
		btn_save = new JButton("保存");
		btn_save.addActionListener(e->{
			logger.info("press btn_save button");
			saveLogFile();
		});
		btn_save.setBounds(610, 581, 100, 25);
		add(btn_save);
		
		JButton btn_statistics = new JButton("统计");
		btn_statistics.addActionListener(e->{
			logger.info("press btn_statistics button");
			JOptionPane.showMessageDialog(this, Statistics(), "统计信息", JOptionPane.INFORMATION_MESSAGE);
		});
		btn_statistics.setBounds(610, 609, 100, 25);
		add(btn_statistics);
		
	}
	
	/**
	 * 得到JTextArea
	 * @return
	 */
	public JTextArea getJTextArea(){
		return textAreaShow;
	}
	/**
	 * 得到JScrollPane
	 * @return
	 */
	public JScrollPane getJScrollPane(){
		return scrollPane;
	}
	/**
	 * 信息统计
	 * @return
	 */
	private String Statistics(){
		StringBuffer Buf=new StringBuffer();

		return Buf.toString();
	}
	/**
	 * 保存日志文件
	 * @return
	 */
	private File saveLogFile() {
	   	JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile);
	    fileChooser.setFileFilter(new FileFilter(){
	      public String getDescription() {
	        return "*.txt";
	      }
	      public boolean accept(File f){
	        String ext = f.getName().toLowerCase();
	        return ext.endsWith(".txt");
	      }
	    });
	    fileChooser.setSelectedFile(new File("Runlog-"+TimeUtil.getTime4File()));
	    if (fileChooser.showSaveDialog(this) != 0) return null;
	    try {
	      File file = fileChooser.getSelectedFile();
	      if(!file.getName().toLowerCase().endsWith(".txt"))file=new File(file.getAbsolutePath()+".txt");//没有.txt后缀则加上
	      HelperUtil.file_write_all(file.getAbsolutePath(), textAreaShow.getText(), false, false);
	      return file;
	    } catch (Exception e) {
	    	logger.error("Exception",e);
	     
	    }
	    return null;
	 }
	/**
	 * 显示总行数,光标所在行数
	 */
	private void setLineInfo(){
		try {
			lbl_linenum.setText(textAreaShow.hasFocus()?(textAreaShow.getText().length()==0?"共0行":"共"+textAreaShow.getLineCount()+"行"
					+ ",第"+(textAreaShow.getLineOfOffset(textAreaShow.getCaretPosition())+1)+"行"):"共"+(textAreaShow.getText().length()==0?"0":textAreaShow.getLineCount())+"行");
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			logger.info("EXCEPTION",e);
		}
	}

	/**
	 * 搜索字符串
	 * @param keyword
	 * @return
	 */
	private int searchTxt(String keyword){
        int count=0;
        int pos = 0;
        searchMap.clear();
		if(highlighterObj!=null)highLighter.removeHighlight(highlighterObj);
        highlighterlist.entrySet().forEach(h->highLighter.removeHighlight(h.getValue()));
        highlighterlist.clear();
		if(keyword.equals(""))return count;
        while ((pos = textAreaShow.getText().indexOf(keyword, pos)) >= 0){
            try {
            	searchMap.put(count, pos);
                Object obj=highLighter.addHighlight(pos, pos + keyword.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
                highlighterlist.put(count,obj);
                pos += keyword.length();
                count++;
            }catch (BadLocationException e) {
            	logger.error("Exception",e);
            }
        }
        return count;
	}
	/**
	 * 搜索字符串
	 * @param keyword
	 * @param isback true为后一个,false为前一个
	 */
	private void searchTxtNext(String keyword,boolean isback){
		if(issearchNext){
			int caretpos=textAreaShow.getCaretPosition();
			Map<Integer, Integer> frontmap=new HashMap<>();
			Map<Integer, Integer> backmap=new HashMap<>();
			for(Entry<Integer, Integer> entry: searchMap.entrySet()){//<num,index>
				if(entry.getValue()>=caretpos){
					backmap.put(entry.getKey(), entry.getValue());
				}else{
					frontmap.put(entry.getKey(), entry.getValue());
				}
			}
			if(isback){
				int diff=Integer.MAX_VALUE;
				for(Entry<Integer, Integer> entry: backmap.entrySet()){//<num,index>
					int tempdiff=entry.getValue()-caretpos;
					if(tempdiff<diff){
						diff=tempdiff;
						searchNum=entry.getKey();
					}
				}
				if(diff==Integer.MAX_VALUE)searchNum=0;	
			}else{
				int diff=Integer.MIN_VALUE;
				for(Entry<Integer, Integer> entry: frontmap.entrySet()){//<num,index>
					int tempdiff=entry.getValue()-caretpos;
					if(tempdiff>diff){
						diff=tempdiff;
						searchNum=entry.getKey();
					}
				}
				if(diff==Integer.MIN_VALUE)searchNum=searchCount-1;	
			}
			issearchNext=false;
		}

		
		for(Entry<Integer, Integer> entry: searchMap.entrySet()){//<num,index>
			if(entry.getKey()==searchNum){
				try {
					int pos=entry.getValue();
					int line = textAreaShow.getLineOfOffset(pos);
					int hight = (int)(line * ((float)textAreaShow.getHeight() / textAreaShow.getLineCount()) - (float)(scrollPane.getHeight()/2));//滚动条位置
					scrollPane.getVerticalScrollBar().setValue(hight);
					if(highlighterObj!=null)highLighter.removeHighlight(highlighterObj);
					highlighterlist.entrySet().forEach(h->highLighter.removeHighlight(h.getValue()));
			        highlighterlist.clear();
					highlighterObj=highLighter.addHighlight(pos, pos+keyword.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.decode("#FFA500")));
					
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				}
				return;
			}
		}
	}
}
