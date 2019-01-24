package com.IOSLogs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.HelperUtil;
import com.Util.TimeUtil;
import com.Viewer.MainRun;

public class LogsCheckAppLogsUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1826089092834096113L;
	Logger logger = LoggerFactory.getLogger(LogsCheckAppLogsUI.class);
	private JPanel contentPane;
	// 开始
	private JButton btnStart;
	boolean isstart = false;
	// 输入
	JTextArea textArea_Inputcmd;

	private JLabel lbl_inputcmd;
	private JButton btn_Highlight;
	// TextAreaUI textAreaUI=new TextAreaUI();
	LogTextAreaUI textAreaUI = new LogTextAreaUI(720, 650);
	LogsCheckAppLogs logsCheckLogs;
	private JButton btn_copylog;
	String udid;

	/**
	 * Create the frame.
	 */
	public LogsCheckAppLogsUI(String udid) {
		this.udid = udid;
		setBounds(100, 100, 750, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("日记高亮查看器");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		/**
		 * 初始化
		 */
		logsCheckLogs = new LogsCheckAppLogs(udid, textAreaUI.getJTextArea());
		textAreaUI.setLocation(10, 60);
		contentPane.add(textAreaUI);

		// 开始按钮
		btnStart = new JButton("开始");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isstart == false) {
					// device null
					if (!CheckUE.checkDevice(udid)) {
						JOptionPane.showMessageDialog(contentPane, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("check logs active button: no devices");
						return;
					}

					String[] path_strings = MainRun.paramsBean.getIOS_Logs_App_path().split(";");
					List<Object> list = new ArrayList<>();
					for (String path : path_strings) {
						list.add(path);
					}
					Object[] options = new Object[list.size()];
					list.toArray(options);
					String select_path = (String) JOptionPane.showInputDialog(contentPane, "请选择应用:", "请选择",
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (select_path == null) {
						return;
					}
					if (select_path.contains(",")) {
						logsCheckLogs.setPackagename(select_path.split(",")[0]);
						logsCheckLogs.setLogPath(select_path.split(",")[1]);
					} else {
						logger.error("select app path error");
						return;
					}
					// 开始检查
					saveParams();// 保存数据
					textAreaUI.getJTextArea().setText("");
					logsCheckLogs.run(textArea_Inputcmd.getText());
					btnStart.setText("停止");
					btnStart.setForeground(Color.RED);
					isstart = true;
					textArea_Inputcmd.setEnabled(false);
				} else {
					// 停止检查
					btnStart.setText("开始");
					btnStart.setForeground(Color.BLACK);
					isstart = false;
					logsCheckLogs.StopLogMonitor();
					textArea_Inputcmd.setEnabled(true);
				}

				logger.info("press btnStart button");
			}
		});
		btnStart.setBounds(628, 21, 100, 25);
		contentPane.add(btnStart);

		// 命令输入框
		textArea_Inputcmd = new JTextArea(MainRun.paramsBean.getIOS_Logs_App_inputcmd());
		textArea_Inputcmd.setWrapStyleWord(true);
		textArea_Inputcmd.setLineWrap(true);
		JScrollPane scrollPaneTxt_Inputcmd = new JScrollPane(textArea_Inputcmd);
		scrollPaneTxt_Inputcmd.setBounds(83, 6, 292, 40);
		scrollPaneTxt_Inputcmd.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(scrollPaneTxt_Inputcmd);

		lbl_inputcmd = new JLabel("过滤参数:");
		lbl_inputcmd.setBounds(10, 18, 61, 16);
		contentPane.add(lbl_inputcmd);

		// 高亮设置按钮
		btn_Highlight = new JButton("高亮设置");
		btn_Highlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_highlight button");
				LogsHighlightAppUI logsHighlightUI = new LogsHighlightAppUI(logsCheckLogs);
				logsHighlightUI.setVisible(true);
			}
		});
		btn_Highlight.setBounds(387, 35, 100, 25);
		contentPane.add(btn_Highlight);

		JButton btn_path = new JButton("应用设置");
		btn_path.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_path button");
				LogsPathSettingBoxUI logsPathSettingBoxUI = new LogsPathSettingBoxUI();
				logsPathSettingBoxUI.setVisible(true);
			}
		});
		btn_path.setBounds(387, 4, 100, 25);
		contentPane.add(btn_path);

		btn_copylog = new JButton("保存日志");
		btn_copylog.setToolTipText("保存完整日志文件,需要在挂载后进行...");
		btn_copylog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_copylog button");
				if (btnStart.getText().equals("停止")) {
					saveLogFile();
				} else {
					JOptionPane.showMessageDialog(contentPane, "保存日志文件只能在开始挂载后!", "消息", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		btn_copylog.setBounds(499, 19, 100, 25);
		contentPane.add(btn_copylog);

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				logger.info("window closing");
				logsCheckLogs.StopLogMonitor();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	// 保存数据
	public void saveParams() {
		MainRun.paramsBean.setIOS_Logs_App_inputcmd(textArea_Inputcmd.getText());
		MainRun.xmlOperationUtil.XMLChanger("IOS_Logs_App_inputcmd", textArea_Inputcmd.getText());
	}

	/**
	 * 保存日志文件
	 * 
	 * @return
	 */
	private File saveLogFile() {
		JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile);
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.txt";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".txt");
			}
		});
		fileChooser.setSelectedFile(new File("ioslog-" + TimeUtil.getTime4File()));
		if (fileChooser.showSaveDialog(this) != 0)
			return null;
		try {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".txt"))
				file = new File(file.getAbsolutePath() + ".txt");// 没有.txt后缀则加上
			if (logsCheckLogs.getFile_log() != null) {
				HelperUtil.file_write_all(file.getAbsolutePath(),
						HelperUtil.file_read_all(logsCheckLogs.getFile_log().getAbsolutePath()).toString(), false,
						false);
			}
			return file;
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return null;
	}
}
