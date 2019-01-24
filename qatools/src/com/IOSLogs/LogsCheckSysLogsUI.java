package com.IOSLogs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;

public class LogsCheckSysLogsUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1826089092834096113L;
	Logger logger = LoggerFactory.getLogger(LogsCheckSysLogsUI.class);
	private JPanel contentPane;
	// 开始
	private JButton btnStart;
	boolean isstart = false;
	// 输入
	JTextArea textArea_Inputcmd;
	JScrollPane scrollPaneTxt_Inputcmd;

	private JLabel lbl_inputcmd;
	private JButton btn_Highlight;
	String udid;
	// TextAreaUI textAreaUI=new TextAreaUI();
	LogTextAreaUI textAreaUI = new LogTextAreaUI(720, 650);
	LogsCheckSysLogs logsCheckLogs;

	/**
	 * Create the frame.
	 */
	public LogsCheckSysLogsUI(String udid) {
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
		logsCheckLogs = new LogsCheckSysLogs(textAreaUI.getJTextArea(), udid);
		textAreaUI.setLocation(10, 60);
		contentPane.add(textAreaUI);

		// 开始按钮
		btnStart = new JButton("开始");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isstart == false) {
					// device null
					if (MainRun.selectedID == null) {
						JOptionPane.showMessageDialog(contentPane, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("check logs active button: no devices");
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
		textArea_Inputcmd = new JTextArea(MainRun.paramsBean.getIOS_Logs_inputcmd());
		textArea_Inputcmd.setWrapStyleWord(true);
		textArea_Inputcmd.setLineWrap(true);
		scrollPaneTxt_Inputcmd = new JScrollPane(textArea_Inputcmd);
		scrollPaneTxt_Inputcmd.setBounds(83, 6, 400, 40);
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
				LogsHighlightSysUI logsHighlightUI = new LogsHighlightSysUI(logsCheckLogs);
				logsHighlightUI.setVisible(true);
			}
		});
		btn_Highlight.setBounds(501, 21, 100, 25);
		contentPane.add(btn_Highlight);

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
		MainRun.paramsBean.setIOS_Logs_inputcmd(textArea_Inputcmd.getText());
		MainRun.xmlOperationUtil.XMLChanger("IOS_Logs_inputcmd", textArea_Inputcmd.getText());
	}
}
