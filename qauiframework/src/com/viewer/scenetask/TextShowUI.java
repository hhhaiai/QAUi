package com.viewer.scenetask;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TextShowUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9210529129380004326L;

	Logger logger = LoggerFactory.getLogger(TextShowUI.class);

	JTextArea textAreaShow = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textAreaShow);
	TextShowSeekBoxUI seekBoxUI = new TextShowSeekBoxUI(textAreaShow, scrollPane);
	JLabel lbl_linenum;
	// 设置是否可编辑
	boolean editable = true;
	int SelectStart = -1, SelectEnd = -1;
	// 对外API设置

	/**
	 * Create the panel.
	 */
	public TextShowUI(int width, int hight) {
		// setSize(720,650);
		setSize(width, hight);
		setLayout(null);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		// highLighter=textAreaShow.getHighlighter();

		textAreaShow.setWrapStyleWord(true);
		textAreaShow.setLineWrap(true);
		textAreaShow.setToolTipText("按下CTRL+F键进行查找...");
		textAreaShow.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				seekBoxUI.setIssearchNext(true);
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
		textAreaShow.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				seekBoxUI.rewriteHighlighter(SelectStart, SelectEnd);
				SelectStart = textAreaShow.getSelectionStart();
				SelectEnd = textAreaShow.getSelectionEnd();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				seekBoxUI.removeHighlighter();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		textAreaShow.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if (!editable)
					e.consume();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == 'F') {
					logger.info("press ctrl+f key to pop out found box");
					seekBoxUI.setSearchText(textAreaShow.getSelectedText());
					seekBoxUI.setVisible(true);
				}
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
				// 无选中内容才会自动向下滚动
				if (textAreaShow.getSelectedText() == null)
					textAreaShow.setCaretPosition(textAreaShow.getText().length());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
		});
		// textAreaShow.setEditable(false);//高亮原因
		scrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setBounds(10, 5, this.getWidth() - 20, this.getHeight() - 50);
		add(scrollPane);

		lbl_linenum = new JLabel("");
		lbl_linenum.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_linenum.setBounds(10, this.getHeight() - 35, this.getWidth(), 15);
		add(lbl_linenum);
	}

	/**
	 * 设置图标
	 * 
	 * @param icon
	 */
	public void setIcon(ImageIcon icon) {
		seekBoxUI.setIcon(icon);
	}

	/**
	 * 设置默认保存路径初始值
	 * 
	 * @param save_log_path
	 */
	public void setSaveLogFilePath(String save_log_path) {
		seekBoxUI.setSaveLogFilePath(save_log_path);
	}

	/**
	 * 显示总行数,光标所在行数
	 */
	public void setLineInfo() {
		try {
			lbl_linenum
					.setText(
							textAreaShow.hasFocus()
									? (textAreaShow.getText().length() == 0 ? "共0行"
											: "共" + textAreaShow.getLineCount() + "行" + ",第"
													+ (textAreaShow.getLineOfOffset(textAreaShow.getCaretPosition())
															+ 1)
													+ "行")
									: "共" + (textAreaShow.getText().length() == 0 ? "0" : textAreaShow.getLineCount())
											+ "行");
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			logger.info("EXCEPTION", e);
		}
	}

	/**
	 * 得到JTextArea
	 * 
	 * @return
	 */
	public JTextArea getJTextArea() {
		return textAreaShow;
	}

	/**
	 * 得到JScrollPane
	 * 
	 * @return
	 */
	public JScrollPane getJScrollPane() {
		return scrollPane;
	}

	// 对外API
	/**
	 * 是否可编辑
	 * 
	 * @param editable
	 */
	public void setEdit(boolean editable) {
		this.editable = editable;
	}
}