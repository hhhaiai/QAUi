package com.GetScreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Viewer.MainRun;

public class GetScreenUImain extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5333754059569929162L;
	Logger logger = LoggerFactory.getLogger(GetScreenUImain.class);
	ScreenCap screencap = new ScreenCap();
	ScreenRecord screenrecord = new ScreenRecord();
	private ScreenMirrorUI mMainFrame;
	ScreenShotUI screenshotui;
	EditToolsUI edittoolsui;
	ScreenRecordUI screenrecordui;
	JButton btnShow;
	ScreenEdit screenedit = new ScreenEdit();
	JButton btnEdit;
	JCheckBox chckbxAndSave;
	JCheckBox chckbx_SaveNarrow;
	GetScreenUImain getScreenUImain = this;
	String udid;

	public GetScreenUImain(String udid) {
		setSize(740, 500);
		setLocation(0, 100);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		this.udid = udid;
		screenshotui = screencap.getScreenShotUI();
		edittoolsui = screencap.getEditTools();
		screenrecordui = new ScreenRecordUI(udid);
		add(screenshotui);
		add(edittoolsui);
		edittoolsui.setscreenshotui(screenshotui);

		// Screen Cap
		JButton btnScreencap = new JButton("截图");
		btnScreencap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// acivelogthreadrun true
				if (screencap.getScreenCapThreadrun()) {
					logger.info("getScreenCapThreadrun =true");
					JOptionPane.showMessageDialog(getScreenUImain, "QATools正在努力工作中,请稍后再试...", "Message",
							JOptionPane.ERROR_MESSAGE);
					logger.info("screen cap button");
					return;
				}
				// device null
				if (!CheckUE.checkDevice(udid)) {
					JOptionPane.showMessageDialog(getScreenUImain, "未检测到设备!", "Message", JOptionPane.ERROR_MESSAGE);
					logger.info("screen cap button: no devices");
					return;
				}

				screencap.run(udid);
				logger.info("screen cap button");
			}
		});
		btnScreencap.setBounds(529, 249, 100, 25);
		add(btnScreencap);
		// Screen Monitor
		JButton btnMirror = new JButton("镜像");
		// btnMirror.setForeground(Color.RED);
		btnMirror.setToolTipText("屏幕映射,时时获取当前设备图像");
		btnMirror.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				if(!com.Main.ThenToolsRun.crypt.isvip()){
//					JOptionPane.showMessageDialog(com.Main.ThenToolsRun.mainFrame, "VIP feature!", 
//							"Message", JOptionPane.ERROR_MESSAGE);
//					com.Main.ThenToolsRun.logger.log(Level.INFO,"btnMirror needs VIP");
//					return;
//				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mMainFrame = new ScreenMirrorUI(udid, null);
						mMainFrame.setLocationRelativeTo(MainRun.mainFrame);
						mMainFrame.setVisible(true);
						mMainFrame.selectDevice();
					}
				});
				logger.info("button Mirror start");
			}
		});
		btnMirror.setBounds(529, 424, 100, 25);
		add(btnMirror);

		// Save image
		JButton btnSave = new JButton("保存截图");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (screencap.saveImage() && MainRun.OStype == 0) {
					int confirm = JOptionPane.showConfirmDialog(getScreenUImain, "是否编辑该图片?", "确认",
							JOptionPane.YES_NO_OPTION);
					if (confirm == 0) {
						btnEdit.doClick();
					} else {
						return;
					}
				}
				logger.info("tap save button");
			}
		});
		btnSave.setBounds(529, 284, 100, 25);
		add(btnSave);

		// screen record
		JButton btnRecord = new JButton("录像");
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				screenrecordui.setVisible(true);
				// edittoolsui.setVisible(true);
			}
		});
		btnRecord.setBounds(529, 389, 100, 25);
		add(btnRecord);

		JLabel lblScreenCap = new JLabel("屏幕截图");
		lblScreenCap.setBounds(514, 214, 105, 25);
		add(lblScreenCap);

		JLabel lblScreenRecord = new JLabel("屏幕录像");
		lblScreenRecord.setBounds(514, 354, 105, 25);
		add(lblScreenRecord);
		// Edit
		btnEdit = new JButton("编辑截图");
		if (MainRun.OStype != 0) {
			btnEdit.setVisible(false);
		}
		btnEdit.setToolTipText("运行自带编辑工具编辑截图");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				DrawPad drawpad = new DrawPad();
//				drawpad.setVisible(true);
				screenedit.EditByWin(udid, screencap.getimagepath());
				logger.info("tap Edit button");
			}
		});
		btnEdit.setBounds(529, 319, 100, 25);
		add(btnEdit);
		// and save checkbox
		chckbxAndSave = new JCheckBox("自动保存");
		chckbxAndSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxAndSave.isSelected()) {
					screencap.setAndsave(true);
				} else {
					screencap.setAndsave(false);
				}
			}
		});
		chckbxAndSave.setBounds(635, 250, 101, 23);
		add(chckbxAndSave);
		// 保存720P截屏
		chckbx_SaveNarrow = new JCheckBox("压缩截图");
		chckbx_SaveNarrow.setBounds(635, 283, 101, 23);
		chckbx_SaveNarrow.setSelected(true);
		chckbx_SaveNarrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbx_SaveNarrow.isSelected()) {
					screencap.setsaveNarrow(true);
				} else {
					screencap.setsaveNarrow(false);
				}
			}
		});
		add(chckbx_SaveNarrow);

	}

}