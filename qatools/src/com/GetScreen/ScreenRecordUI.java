package com.GetScreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Viewer.MainRun;

public class ScreenRecordUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5125547172702914275L;
	Logger logger = LoggerFactory.getLogger(ScreenRecordUI.class);
	private JPanel contentPane;
	JComboBox<String> Resolutionlist;
	String WidthandHeight = "WVGA 480X800";
	ScreenRecord screenrecord = new ScreenRecord();
	JButton btnStart;
	JButton btnGet;
	JLabel lblCountdown;
	boolean isstart = false;
	int timecount = 0;
	Timer timer;
	String udid;

	/**
	 * Create the frame.
	 */
	public ScreenRecordUI(String udid) {
		this.udid = udid;
		setResizable(false);
		setTitle("屏幕录像");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 433, 295);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setIconImage(MainRun.imagelogo);

		JLabel labelvideopathPC = new JLabel("<html>PC存储路径:<br>桌面/ThenLog/ScreenRecord</html>");
		labelvideopathPC.setVerticalAlignment(SwingConstants.TOP);
		labelvideopathPC.setBounds(10, 221, 236, 36);
		contentPane.add(labelvideopathPC);

		// Start
		btnStart = new JButton("开始");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isstart) {
					btnGet.setEnabled(false);
					timecount = 180;
					screenrecord.run(udid, WidthandHeight);
					isstart = true;
					btnStart.setText("停止");
					timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									if (timecount >= 0) {
										lblCountdown.setText(
												"<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + timecount + "秒</html>");
										timecount--;
									} else {
										screenrecord.setiscancel(true);
										isstart = false;
										btnStart.setText("开始");
										logger.info("timecout<0 stop auto");
										if (timer != null) {
											timer.cancel();
										}
									}
								}
							});
						}
					}, 0, 1000);
					logger.info("start screen record button");
				} else {
					btnGet.setEnabled(true);
					if (timer != null) {
						timer.cancel();
					}
					screenrecord.setiscancel(true);
					isstart = false;
					btnStart.setText("开始");
					logger.info("stop screen record button");
				}

			}
		});
		btnStart.setBounds(306, 126, 100, 25);
		contentPane.add(btnStart);

		// Cancel
		JButton btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnGet.setEnabled(true);
				if (timer != null) {
					timer.cancel();
				}
				screenrecord.setiscancel(true);
				isstart = false;
				btnStart.setText("Start");
				logger.info("cancel screen record button");
				dispose();
			}
		});
		btnCancel.setBounds(306, 196, 100, 25);
		contentPane.add(btnCancel);

		// resolution list
		Resolutionlist = new JComboBox<String>();
		Resolutionlist.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getStateChange() == 1) {
					WidthandHeight = (String) Resolutionlist.getSelectedItem();
					logger.info("choose " + WidthandHeight);
				}
			}
		});
		Resolutionlist.setBounds(20, 24, 160, 23);
		Resolutionlist.setModel(new DefaultComboBoxModel<String>(new String[] { "WVGA 480X800", "FHD 1080X1920",
				"HD 720x1280", "QHD 540X960", "FWVGA 480X854", "HVGA 320X480" }));
		Resolutionlist.setSelectedItem("WVGA 480X800");
		contentPane.add(Resolutionlist);

		JLabel lblResolution = new JLabel("分辨率");
		lblResolution.setBounds(10, 7, 92, 15);
		contentPane.add(lblResolution);

		// countdown time
		lblCountdown = new JLabel("最长: 180秒");
		lblCountdown.setForeground(Color.CYAN);
		lblCountdown.setFont(new Font("微软雅黑", Font.BOLD, 35));
		lblCountdown.setBounds(114, 57, 236, 60);
		contentPane.add(lblCountdown);

		// get video
		btnGet = new JButton("获取录像");
		btnGet.setToolTipText("提取设备中获取刚才录制的视频到PC");
		// btnGet.setEnabled(false);
		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// getvideofromuethreadrun true
				if (screenrecord.getVideofromUEthreadrun()) {
					logger.info("activelogthreadrun =true");
					JOptionPane.showMessageDialog(contentPane, "QATools正在努力工作中,请稍后再试...", "消息",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// device null
				if (!CheckUE.checkDevice(udid)) {
					JOptionPane.showMessageDialog(contentPane, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
					logger.info("get screen record: no devices");
					return;
				}

				screenrecord.getVideofromUE();
				logger.info("get screen record button");
			}
		});
		btnGet.setBounds(306, 161, 100, 25);
		contentPane.add(btnGet);

		JLabel labelvideopathUE = new JLabel("<html>设备存储路径 :<br>/sdcard/Movies</html>");
		labelvideopathUE.setVerticalAlignment(SwingConstants.TOP);
		labelvideopathUE.setBounds(10, 175, 236, 36);
		contentPane.add(labelvideopathUE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(MainRun.mainFrame);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				btnGet.setEnabled(true);
				if (timer != null) {
					timer.cancel();
				}
				screenrecord.setiscancel(true);
				isstart = false;
				btnStart.setText("开始");
				logger.info("close screen record");
				dispose();
			}

		});
	}

	public JButton getbtnStart() {
		return btnStart;
	}

}
