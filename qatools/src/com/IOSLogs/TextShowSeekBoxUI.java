package com.IOSLogs;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Util.TimeUtil;

public class TextShowSeekBoxUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(TextShowSeekBoxUI.class);

	private static final long serialVersionUID = 1590243793833571859L;
	private JPanel contentPane;
	private JButton btn_clear;
	private JFormattedTextField frmtdtxtfld_Search;
	private JButton btn_searchNext;
	private JButton btn_searchPre;
	private JButton btn_save;
	JTextArea textAreaShow;
	JScrollPane scrollPane;

	Highlighter highLighter;
	boolean iscancel = false;
	HashMap<Integer, Integer> searchMap = new LinkedHashMap<Integer, Integer>();// 保存关键字符串搜索索引 序号,索引
	Map<Integer, Object> highlighterlist = new LinkedHashMap<Integer, Object>();// 搜索高亮对象列表
	Object highlighterObj;// 搜索指定位置的高亮对象
	// 查找
	int searchNum = -1;
	int searchCount = 0;
	boolean issearchNext = false;
	JLabel lbl_find;
	Highlight[] highlights;
	ImageIcon icon = null;
	String save_log_path = "";

	/**
	 * Create the frame.
	 */
	public TextShowSeekBoxUI(JTextArea textAreaShow, JScrollPane scrollPane) {
		setBounds(100, 100, 425, 135);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("查找界面");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(textAreaShow);
		// setIconImage(icon.getImage());

		/**
		 * init
		 */
		this.textAreaShow = textAreaShow;
		this.scrollPane = scrollPane;
		highLighter = textAreaShow.getHighlighter();

		// 清空文本按钮
		btn_clear = new JButton("清空");
		// btn_clear.setEnabled(false);
		btn_clear.addActionListener(e -> {
			logger.info("press btn_clear button");
			if (JOptionPane.showConfirmDialog(contentPane, "是否清除内容?", "消息", JOptionPane.YES_NO_OPTION,
					JOptionPane.YES_NO_OPTION, icon) == 0) {
				highLighter.removeAllHighlights();
				textAreaShow.setText("");
			}
		});
		btn_clear.setBounds(88, 83, 100, 25);
		getContentPane().add(btn_clear);

		// 查找字符串
		lbl_find = new JLabel("");
		lbl_find.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_find.setBounds(20, 46, 160, 16);
		getContentPane().add(lbl_find);

		// 查找文本框
		frmtdtxtfld_Search = new JFormattedTextField();
		// frmtdtxtfld_Search.setEnabled(false);
		frmtdtxtfld_Search.setBounds(10, 6, 402, 28);
		frmtdtxtfld_Search.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				searchNum = -1;
				issearchNext = true;
				searchCount = searchTxt(frmtdtxtfld_Search.getText());
				lbl_find.setText("共" + searchCount + "条");
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		getContentPane().add(frmtdtxtfld_Search);

		// 查找下一个按钮
		btn_searchNext = new JButton("查找下一个");
		btn_searchNext.setFocusable(false);
		btn_searchNext.addActionListener(e -> {
			logger.info("press btn_searchNext button");
			searchNum++;
			if (searchNum > (searchCount - 1))
				searchNum = 0;
			searchTxtNext(frmtdtxtfld_Search.getText(), true);
			lbl_find.setText(searchCount != 0 ? "共" + searchCount + "条,第" + (searchNum + 1) + "条" : "共0条");
		});
		// btn_searchNext.setEnabled(false);
		btn_searchNext.setBounds(312, 46, 100, 25);
		getContentPane().add(btn_searchNext);

		// 查找上一个按钮
		btn_searchPre = new JButton("查找上一个");
		btn_searchPre.setFocusable(false);
		btn_searchPre.addActionListener(e -> {
			logger.info("press btn_searchPre button");
			searchNum--;
			if (searchNum < 0)
				searchNum = searchCount - 1;
			searchTxtNext(frmtdtxtfld_Search.getText(), false);
			lbl_find.setText(searchCount != 0 ? "共" + searchCount + "条,第" + (searchNum + 1) + "条" : "共0条");
		});
		// btn_searchPre.setEnabled(false);
		btn_searchPre.setBounds(200, 46, 100, 25);
		getContentPane().add(btn_searchPre);

		btn_save = new JButton("保存");
		btn_save.addActionListener(e -> {
			logger.info("press btn_save button");
			saveLogFile();
		});
		btn_save.setBounds(200, 83, 100, 25);
		getContentPane().add(btn_save);

		JLabel lbl_function = new JLabel("辅助功能");
		lbl_function.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_function.setBounds(20, 67, 100, 16);
		contentPane.add(lbl_function);
	}

	/**
	 * 设置图标
	 * 
	 * @param icon
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
		if (icon != null)
			setIconImage(icon.getImage());
	}

	/**
	 * 初始化搜索框
	 * 
	 * @param text
	 */
	public void setSearchText(String text) {
		frmtdtxtfld_Search.setText(text);
	}

	/**
	 * 设置issearchNext
	 * 
	 * @param issearchNext
	 */
	public void setIssearchNext(boolean issearchNext) {
		this.issearchNext = issearchNext;
	}

	/**
	 * 获取并移除
	 */
	public void removeHighlighter() {
		highlights = highLighter.getHighlights();
		highLighter.removeAllHighlights();
	}

	/**
	 * 重写高亮,后写的不能覆盖先写的
	 * 
	 * @param SelectStart
	 * @param SelectEnd
	 */
	public void rewriteHighlighter(int SelectStart, int SelectEnd) {
		boolean SelectText_flag = true;
		boolean tempSelectText_flag = true;
		for (Highlighter.Highlight highlight : highlights) {
			try {
				if (tempSelectText_flag && highlight.getStartOffset() == SelectStart
						&& highlight.getEndOffset() == SelectEnd) {
					tempSelectText_flag = false;
					continue;
				}
				if (SelectText_flag && highlight.getStartOffset() == textAreaShow.getSelectionStart()
						&& highlight.getEndOffset() == textAreaShow.getSelectionEnd()) {
					SelectText_flag = false;
					continue;
				}
				Object object = highLighter.addHighlight(highlight.getStartOffset(), highlight.getEndOffset(),
						highlight.getPainter());
				// 指定位置
				if (highlight.equals(highlighterObj)) {
					highlighterObj = object;
					continue;
				}
				// 搜索
				for (Entry<Integer, Object> entry : highlighterlist.entrySet()) {
					if (entry.getValue().equals(highlight)) {
						highlighterlist.put(entry.getKey(), object);
						break;
					}
				}

			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPITON", e);
			}
		}
	}

	/**
	 * 设置默认保存路径初始值
	 * 
	 * @param save_log_path
	 */
	public void setSaveLogFilePath(String save_log_path) {
		this.save_log_path = save_log_path;
	}

	/**
	 * 保存日志文件
	 * 
	 * @return
	 */
	private File saveLogFile() {
		JFileChooser fileChooser = new JFileChooser(save_log_path);
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.txt";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".txt");
			}
		});
		fileChooser.setSelectedFile(new File("Runlog-" + TimeUtil.getTime4File()));
		if (fileChooser.showSaveDialog(this) != 0)
			return null;
		try {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".txt"))
				file = new File(file.getAbsolutePath() + ".txt");// 没有.txt后缀则加上
			HelperUtil.file_write_all(file.getAbsolutePath(), textAreaShow.getText(), false, false);
			return file;
		} catch (Exception e) {
			logger.error("Exception", e);

		}
		return null;
	}

	/**
	 * 搜索字符串
	 * 
	 * @param keyword
	 * @return
	 */
	private int searchTxt(String keyword) {
		int count = 0;
		int pos = 0;
		searchMap.clear();
		if (highlighterObj != null)
			highLighter.removeHighlight(highlighterObj);
		highlighterlist.entrySet().forEach(h -> highLighter.removeHighlight(h.getValue()));
		highlighterlist.clear();
		if (keyword.equals(""))
			return count;
		removeHighlighter();
		try {
			while ((pos = textAreaShow.getText().indexOf(keyword, pos)) >= 0) {
				searchMap.put(count, pos);
				Object obj = highLighter.addHighlight(pos, pos + keyword.length(),
						new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
				highlighterlist.put(count, obj);
				pos += keyword.length();
				count++;
			}
		} catch (BadLocationException e) {
			logger.error("Exception", e);
		}
		rewriteHighlighter(-1, -1);
		return count;
	}

	/**
	 * 搜索字符串
	 * 
	 * @param keyword
	 * @param isback  true为后一个,false为前一个
	 */
	private void searchTxtNext(String keyword, boolean isback) {
		if (issearchNext) {
			int caretpos = textAreaShow.getCaretPosition();
			Map<Integer, Integer> frontmap = new HashMap<>();
			Map<Integer, Integer> backmap = new HashMap<>();
			for (Entry<Integer, Integer> entry : searchMap.entrySet()) {// <num,index>
				if (entry.getValue() >= caretpos) {
					backmap.put(entry.getKey(), entry.getValue());
				} else {
					frontmap.put(entry.getKey(), entry.getValue());
				}
			}
			if (isback) {
				int diff = Integer.MAX_VALUE;
				for (Entry<Integer, Integer> entry : backmap.entrySet()) {// <num,index>
					int tempdiff = entry.getValue() - caretpos;
					if (tempdiff < diff) {
						diff = tempdiff;
						searchNum = entry.getKey();
					}
				}
				if (diff == Integer.MAX_VALUE)
					searchNum = 0;
			} else {
				int diff = Integer.MIN_VALUE;
				for (Entry<Integer, Integer> entry : frontmap.entrySet()) {// <num,index>
					int tempdiff = entry.getValue() - caretpos;
					if (tempdiff > diff) {
						diff = tempdiff;
						searchNum = entry.getKey();
					}
				}
				if (diff == Integer.MIN_VALUE)
					searchNum = searchCount - 1;
			}
			issearchNext = false;
		}

		for (Entry<Integer, Integer> entry : searchMap.entrySet()) {// <num,index>
			if (entry.getKey() == searchNum) {
				try {
					int pos = entry.getValue();
					int line = textAreaShow.getLineOfOffset(pos);
					int hight = (int) (line * ((float) textAreaShow.getHeight() / textAreaShow.getLineCount())
							- (float) (scrollPane.getHeight() / 2));// 滚动条位置
					scrollPane.getVerticalScrollBar().setValue(hight);
					if (highlighterObj != null)
						highLighter.removeHighlight(highlighterObj);
					highlighterlist.entrySet().forEach(h -> highLighter.removeHighlight(h.getValue()));
					highlighterlist.clear();
					removeHighlighter();
					highlighterObj = highLighter.addHighlight(pos, pos + keyword.length(),
							new DefaultHighlighter.DefaultHighlightPainter(Color.decode("#FFA500")));
					rewriteHighlighter(-1, -1);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
				return;
			}
		}
	}
}
