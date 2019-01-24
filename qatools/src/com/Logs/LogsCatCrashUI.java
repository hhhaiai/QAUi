package com.Logs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;

public class LogsCatCrashUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8548394641213823123L;
	Logger logger = LoggerFactory.getLogger(LogsCatCrashUI.class);
	private JPanel contentPane;
	JTextArea textAreaShowlog;
	JScrollPane scrollPaneTxt;
	private JComboBox<String> Crashpath_list;
	JComboBox<String> File_list;
	String Crashpath_Selected;// 选中路径
	String file_Selected;// 选中文件
	private JButton btn_PathSettings;
	LogsCatCrash logsCatCrash;
	private JLabel lblTime;// 获取发生时间
	String udid;

	/**
	 * Create the frame.
	 */
	public LogsCatCrashUI(String udid) {
		this.udid = udid;
		setBounds(100, 100, 750, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("崩溃日记查看器");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);
		logsCatCrash = new LogsCatCrash(udid);
		// textAreaShowlog
		textAreaShowlog = new JTextArea("");
		textAreaShowlog.setWrapStyleWord(true);
		textAreaShowlog.setLineWrap(true);
		scrollPaneTxt = new JScrollPane(textAreaShowlog);
		scrollPaneTxt.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneTxt.setBounds(10, 69, 724, 339);
		contentPane.add(scrollPaneTxt);

		// Crashpath_list
		Crashpath_list = new JComboBox<String>();
		Crashpath_list.setBounds(86, 10, 628, 25);
		Crashpath_list.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == 1) { // 改变
					String[] tempsplit = Crashpath_list.getSelectedItem().toString().split("=");
					Crashpath_Selected = tempsplit[2].substring(0, tempsplit[2].length() - 7);
					setFile_list(Crashpath_Selected);// 更新文件列表
					logger.info("select package crash path=" + Crashpath_Selected);
				} else {// 取消
					Crashpath_Selected = null;
					logger.info("select package crash path=null");
				}
			}
		});
		contentPane.add(Crashpath_list);
		// file——list
		File_list = new JComboBox<String>();
		File_list.setBounds(86, 38, 314, 25);
		File_list.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == 1) { // 改变
					file_Selected = File_list.getSelectedItem().toString();
					lblTime.setText("时间戳=" + translateTime(file_Selected));// 获取时间戳
					logger.info("select package crash file=" + file_Selected);
				} else {// 取消
					file_Selected = null;
					logger.info("select package crash file=null");
				}
			}
		});
		contentPane.add(File_list);
		// 路径配置按钮
		btn_PathSettings = new JButton("路径配置");
		btn_PathSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LogsPathSettingsUI logsPathSettingsUI = new LogsPathSettingsUI(Crashpath_list);
				logsPathSettingsUI.setVisible(true);

				logger.info("press btn_PathSettings button");
			}
		});
		btn_PathSettings.setBounds(514, 433, 100, 25);
		contentPane.add(btn_PathSettings);

		// 查看err日记按钮
		JButton btn_CatFile = new JButton("查看");
		btn_CatFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textAreaShowlog.setText("");
				logsCatCrash.getFile_txt(Crashpath_Selected, file_Selected, textAreaShowlog);
				// textAreaShowlog.setText(logsCatCrash.getFile_txt(Crashpath_Selected,file_Selected));
				logger.info("press btn_CatErr button");
			}
		});
		btn_CatFile.setBounds(634, 433, 100, 25);
		contentPane.add(btn_CatFile);

		JLabel lbl_path = new JLabel("选择路径：");
		lbl_path.setBounds(10, 13, 73, 15);
		contentPane.add(lbl_path);

		JLabel lbl_file = new JLabel("文件列表：");
		lbl_file.setBounds(10, 42, 73, 15);
		contentPane.add(lbl_file);

		lblTime = new JLabel("");
		lblTime.setBounds(423, 41, 311, 16);
		contentPane.add(lblTime);

		JLabel lblNote = new JLabel("当文件大于4MB时,读取会非常缓慢,请耐心等待几分钟...");
		lblNote.setBounds(10, 436, 311, 16);
		contentPane.add(lblNote);

		JButton btn_refresh = new JButton("刷新目录");
		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press button btn_refresh");
				setFile_list(Crashpath_Selected);
			}
		});
		btn_refresh.setBounds(390, 431, 100, 25);
		contentPane.add(btn_refresh);
		// 填充路径列表
		setCrashpath_list();
	}

	// set crash path list
	public void setCrashpath_list() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Crashpath_list.removeAllItems();
				ArrayList<String> arrayPath = logsCatCrash.getCrashPath();
				for (String str : arrayPath) {
					Crashpath_list.addItem(str);
				}
			}
		});
	}

	// set file list
	public void setFile_list(final String path) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				File_list.removeAllItems();
				ArrayList<String> arrayPath = logsCatCrash.getFilelist(path);
				for (String str : arrayPath) {
					File_list.addItem(str);
				}
			}
		});
	}

	public String translateTime(String txt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Pattern time_format = Pattern.compile("[0-9]{13}|[0-9]{10}");
		Matcher matcher = time_format.matcher(txt);
		if (matcher.find()) {
			String timestamp = matcher.group(0);
			long temp = 0;
			if (timestamp.length() == 10) {
				temp = Long.parseLong(timestamp) * 1000;
			} else {
				temp = Long.parseLong(timestamp);
			}
			return sdf.format(temp);
		}
		return "";
	}
}
