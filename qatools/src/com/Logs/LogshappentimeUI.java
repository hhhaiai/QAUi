package com.Logs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;
import com.constant.Cconfig;

public class LogshappentimeUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2626014748391876669L;
	Logger logger = LoggerFactory.getLogger(LogshappentimeUI.class);
	private JPanel contentPane;
	String timeornone = "None";
	String Mon, Day, Hour, Min, happentime;
	boolean isroot = false;
	boolean iscompression = true;
	LogsGet logsGet;
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("MMddHHmm");
	File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
	String desktopPath = desktopDir.getAbsolutePath();

	JRadioButton rdbtnNone;
	JRadioButton rdbtnTime;

	JComboBox<String> comboBoxMon;
	JComboBox<String> comboBoxDay;
	JComboBox<String> comboBoxHour;
	JComboBox<String> comboBoxMin;
	JLabel lblFolderPath;
	JCheckBox chckbxCompression;
	JCheckBox chckbxRoot;
	// 过滤复选框
	JCheckBox chckbx_delUseless;
	boolean isdeluseless = true;
	String udid;

	/**
	 * Create the frame.
	 */
	public LogshappentimeUI(String udid) {
		this.udid = udid;
		setResizable(false);
		setTitle("请选择发生时间:");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 433, 295);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		logsGet = new LogsGet(udid);
		// Cancel
		JButton btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				logger.info("happentimeUI Cancel dispose");
			}
		});
		btnCancel.setBounds(312, 222, 100, 25);
		contentPane.add(btnCancel);

		// OK
		JButton btnOK = new JButton("提取");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// None time
				if (timeornone.equals("None")) {
					int confirm = JOptionPane.showConfirmDialog(contentPane, "无发生时间,是否继续提取日记?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logger.info("continue to get log with nonetime");
					} else {
						logger.info("not continue to get log with nonetime");
						return;
					}
				}

				happentime = Mon + Day + "_" + Hour + "H" + Min + "M";
				if (logsGet.filepathexist(timeornone, happentime)) {
					int confirm = JOptionPane.showConfirmDialog(contentPane,
							logsGet.getMainlog() + "已存在!\n" + "是否删除该文件夹后提取日记?", "确认", JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						logger.info("del exist hapentime floder and get log");
					} else {
						logger.info("not del exist hapentime floder");
						return;
					}
				}
				;
				if (logsGet.getGetlogthreadrun()) {
					JOptionPane.showMessageDialog(contentPane, "QATools正在从设备" + udid + "提取日记...", "消息",
							JOptionPane.ERROR_MESSAGE);
				} else {
					// ����ץȡlog
					logsGet.run(timeornone, happentime, iscompression, isdeluseless);
					dispose();
				}
				logger.info("happentimeUI OK dispose");
			}
		});
		btnOK.setBounds(204, 222, 100, 25);
		contentPane.add(btnOK);

		// None radiobutton
		rdbtnNone = new JRadioButton("无发生时间");
		rdbtnNone.setForeground(Color.RED);
		rdbtnNone.setSelected(true);
		rdbtnNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timeornone = "None";
				rdbtnNone.setForeground(Color.RED);
				rdbtnTime.setForeground(Color.BLACK);
				logger.info("select None option in happentimeUI");
			}

		});
		rdbtnNone.setBounds(6, 16, 103, 23);
		contentPane.add(rdbtnNone);

		// Time radiobutton
		rdbtnTime = new JRadioButton("有发生时间(24小时制,设备时间)");
		rdbtnTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timeornone = "Time";
				rdbtnTime.setForeground(Color.RED);
				rdbtnNone.setForeground(Color.BLACK);
				logger.info("select Time option in happentimeUI");
			}

		});
		rdbtnTime.setBounds(6, 41, 278, 23);
		contentPane.add(rdbtnTime);

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNone);
		group.add(rdbtnTime);

		// happentime set
		String MMddHHmm = sDateFormat.format(new Date());
		Mon = MMddHHmm.substring(0, 2);
		Day = MMddHHmm.substring(2, 4);
		Hour = MMddHHmm.substring(4, 6);
		Min = MMddHHmm.substring(6, 8);
		comboBoxMon = new JComboBox<String>();
		comboBoxMon.setModel(new DefaultComboBoxModel<String>(
				new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
		comboBoxMon.setSelectedItem(Mon);
		comboBoxMon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mon = (String) comboBoxMon.getSelectedItem();

			}
		});
		comboBoxMon.setBounds(32, 64, 75, 25);
		contentPane.add(comboBoxMon);

		comboBoxDay = new JComboBox<String>();
		comboBoxDay.setModel(new DefaultComboBoxModel<String>(new String[] { "01", "02", "03", "04", "05", "06", "07",
				"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
				"25", "26", "27", "28", "29", "30", "31" }));
		comboBoxDay.setSelectedItem(Day);
		comboBoxDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Day = (String) comboBoxDay.getSelectedItem();

			}
		});
		comboBoxDay.setBounds(122, 64, 75, 25);
		contentPane.add(comboBoxDay);

		comboBoxHour = new JComboBox<String>();
		comboBoxHour.setModel(
				new DefaultComboBoxModel<String>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08",
						"09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
		comboBoxHour.setSelectedItem(Hour);
		comboBoxHour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Hour = (String) comboBoxHour.getSelectedItem();

			}
		});
		comboBoxHour.setBounds(32, 93, 75, 25);
		contentPane.add(comboBoxHour);

		comboBoxMin = new JComboBox<String>();
		comboBoxMin.setModel(new DefaultComboBoxModel<String>(
				new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
						"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
						"31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
						"47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
		comboBoxMin.setSelectedItem(Min);
		comboBoxMin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Min = (String) comboBoxMin.getSelectedItem();

			}
		});
		comboBoxMin.setBounds(122, 93, 75, 25);
		contentPane.add(comboBoxMin);

		JLabel lblM = new JLabel("月");
		lblM.setBounds(106, 68, 20, 15);
		contentPane.add(lblM);

		lblFolderPath = new JLabel("<html>Log存储目录 :<br>桌面/QALogs/AndroidLogs/PCtime日期</html>:");
		lblFolderPath.setVerticalAlignment(SwingConstants.TOP);
		lblFolderPath.setBounds(10, 176, 294, 36);
		contentPane.add(lblFolderPath);

		// compression
		chckbxCompression = new JCheckBox("压缩");
		if (MainRun.OStype == Cconfig.WINDOWS) {
			chckbxCompression.setText("7-Zip压缩");
		} else {
			chckbxCompression.setText("Zip压缩");
		}
		chckbxCompression.setSelected(true);
		chckbxCompression.setBounds(309, 152, 103, 23);
		chckbxCompression.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxCompression.isSelected()) {
					iscompression = true;
				} else {
					iscompression = false;
				}
			}
		});
		contentPane.add(chckbxCompression);

		// Syn ue time
		JButton btnSynUe = new JButton("同步时间");
		btnSynUe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] time = logsGet.getUEtime();
				if (time[1] != null) {
					Mon = time[1];
					Day = time[2];
					Hour = time[3];
					Min = time[4];
					comboBoxMon.setSelectedItem(Mon);
					comboBoxDay.setSelectedItem(Day);
					comboBoxHour.setSelectedItem(Hour);
					comboBoxMin.setSelectedItem(Min);
				} else {
					JOptionPane.showMessageDialog(contentPane, "获取设备时间失败,请重试.", "消息", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSynUe.setBounds(224, 93, 100, 25);
		contentPane.add(btnSynUe);

		JLabel lblD = new JLabel("日");
		lblD.setBounds(195, 68, 20, 15);
		contentPane.add(lblD);

		JLabel lblH = new JLabel("时");
		lblH.setBounds(106, 97, 20, 15);
		contentPane.add(lblH);

		JLabel lblMin = new JLabel("分");
		lblMin.setBounds(195, 97, 20, 15);
		contentPane.add(lblMin);
		// 过滤多余的日记按钮
		chckbx_delUseless = new JCheckBox("过滤日记");
		chckbx_delUseless.setSelected(true);
		chckbx_delUseless.setBounds(224, 63, 103, 23);
		chckbx_delUseless.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbx_delUseless.isSelected()) {
					isdeluseless = true;
				} else {
					isdeluseless = false;
				}
			}
		});
		contentPane.add(chckbx_delUseless);

	}

	public void updatetime() {
		String MMddHHmm = sDateFormat.format(new Date());
		Mon = MMddHHmm.substring(0, 2);
		Day = MMddHHmm.substring(2, 4);
		Hour = MMddHHmm.substring(4, 6);
		Min = MMddHHmm.substring(6, 8);

		comboBoxMon.setSelectedItem(Mon);
		comboBoxDay.setSelectedItem(Day);
		comboBoxHour.setSelectedItem(Hour);
		comboBoxMin.setSelectedItem(Min);
	}
}
