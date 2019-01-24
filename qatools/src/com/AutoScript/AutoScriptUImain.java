package com.AutoScript;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.HelperUtil;

public class AutoScriptUImain extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9141702563198615454L;
	Logger logger = LoggerFactory.getLogger(AutoScriptUImain.class);
	AutoScriptShotUI autoscriptshotui;
	RecordScript recordscript = new RecordScript(this);
	PlaybackScript playbackscript = new PlaybackScript(this);
	JTextArea textAreaShowScript;
	EditScript editscript = new EditScript();
	JLabel lblMouse;
	JLabel lblResolution;
	JLabel lblLandspcaceMode;
	JCheckBox chckbxlandscapeMode;
	JScrollPane scrollPaneShowXY;

	boolean isstartrecord = false;
	JButton btnStartRecord;
	boolean isstartplayback = false;
	JButton btnPlayback;
	AutoScriptUImain autoScriptUImain;
	String udid;

	/**
	 * Create the panel.
	 */
	public AutoScriptUImain(String udid) {
		this.udid = udid;
		setSize(740, 500);
		setLocation(0, 100);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);

		autoscriptshotui = recordscript.getAutoscriptshotui();
		add(autoscriptshotui);
		// textAreaShowScript
		textAreaShowScript = new JTextArea("**Script interpreter version=1.1**\n");
		textAreaShowScript.setWrapStyleWord(true);
		textAreaShowScript.setLineWrap(true);
		scrollPaneShowXY = new JScrollPane(textAreaShowScript);
		scrollPaneShowXY.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPaneShowXY.setBounds(250, 0, 225, 400);
		add(scrollPaneShowXY);
		// 行号
//		LineNumberHeaderView lineNumberHeader = new LineNumberHeaderView();
//		scrollPaneShowXY.setRowHeaderView(lineNumberHeader);

		recordscript.settextAreaShowScript(textAreaShowScript);// recordscript add textArea
		editscript.settextAreaShowScript(textAreaShowScript);
		// 鼠标信息
		lblMouse = new JLabel("坐标: 0,0");
		lblMouse.setBounds(500, 0, 100, 26);
		add(lblMouse);
		autoscriptshotui.setlblMouse(lblMouse);
		// 分辨率
		lblResolution = new JLabel("");
		lblResolution.setBounds(500, 30, 153, 26);
		add(lblResolution);
		recordscript.setlblResolution(lblResolution);
		// 向左横屏提示
		lblLandspcaceMode = new JLabel("<html><font color=\"#FF0000\">支持向左横屏</font></html>");
		lblLandspcaceMode.setBounds(600, 0, 93, 26);
		lblLandspcaceMode.setVisible(false);
		add(lblLandspcaceMode);
		// start record button
		btnStartRecord = new JButton("开始录制");
		btnStartRecord.setForeground(Color.BLACK);
		btnStartRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isstartrecord) {
					// acivelogthreadrun true
					if (recordscript.getAutoscriptthreadrun()) {
						logger.info("getAutoscriptthreadrun =true");
						JOptionPane.showMessageDialog(autoScriptUImain, "QATools正在努力工作中,请稍后再试...", "消息",
								JOptionPane.ERROR_MESSAGE);
						logger.info("start ScriptRecord button");
						return;
					}
					// acivelogthreadrun true
					if (playbackscript.getPlaybackscriptthreadrun()) {
						logger.info("getPlaybackscriptthreadrun =true");
						JOptionPane.showMessageDialog(autoScriptUImain, "请等待回放脚本完成!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("start ScriptRecord button");
						return;
					}
					// device null
					if (!CheckUE.checkDevice(udid)) {
						JOptionPane.showMessageDialog(autoScriptUImain, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("start ScriptRecord button");
						return;
					}
					recordscript.setiscancelled(true);
					recordscript.run();
					// lblisrecord.setText(getString("lblisrecord"));
					// change
					lblLandspcaceMode.setVisible(true);
					isstartrecord = true;
					btnStartRecord.setText("停止录制");
					btnStartRecord.setForeground(Color.RED);
					logger.info("start Script Record button");
				} else {
					recordscript.setiscancelled(true);
					// change
					lblLandspcaceMode.setVisible(false);
					isstartrecord = false;
					btnStartRecord.setText("开始录制");
					btnStartRecord.setForeground(Color.BLACK);
					// lblisrecord.setText(getString("lblisrecord1"));
					JOptionPane.showMessageDialog(autoScriptUImain, "已停止录制脚本.", "消息", JOptionPane.INFORMATION_MESSAGE);
					logger.info("stop record script button");
				}

			}
		});
		btnStartRecord.setBounds(500, 65, 100, 25);
		add(btnStartRecord);

		// Playback button
		btnPlayback = new JButton("回放脚本");
		btnPlayback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isstartplayback) {
					// acivelogthreadrun true
					if (recordscript.getAutoscriptthreadrun()) {
						logger.info("getAutoscriptthreadrun =true");
						JOptionPane.showMessageDialog(autoScriptUImain, "请先停止录制脚本!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("Playback button");
						return;
					}
					// acivelogthreadrun true
					if (playbackscript.getPlaybackscriptthreadrun()) {
						logger.info("getPlaybackscriptthreadrun =true");
						JOptionPane.showMessageDialog(autoScriptUImain, "QATools正在努力工作中,请稍后再试...", "消息",
								JOptionPane.ERROR_MESSAGE);
						logger.info("Playback button");
						return;
					}
					// device null
					if (!CheckUE.checkDevice(udid)) {
						JOptionPane.showMessageDialog(autoScriptUImain, "未检测到设备!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("Playback button");
						return;
					}
					if (textAreaShowScript.getText().equals("")) {
						JOptionPane.showMessageDialog(autoScriptUImain, "脚本为空,无法回放!", "消息", JOptionPane.ERROR_MESSAGE);
						logger.info("Playback button");
						return;
					}
//				playbackscript.setxD(autoscriptshotui.getxD());
//				playbackscript.setyD(autoscriptshotui.getyD());
					playbackscript.settextAreaShowScript(textAreaShowScript);
					playbackscript.run();
					// change
					isstartplayback = true;
					btnPlayback.setForeground(Color.RED);
					btnPlayback.setText("停止回放");
				} else {
					playbackscript.Cancelplayback();
					// change
					isstartplayback = false;
					btnPlayback.setForeground(Color.BLACK);
					btnPlayback.setText("开始回放");
				}
				logger.info("Playback button");
			}

		});
		btnPlayback.setBounds(610, 66, 100, 25);
		add(btnPlayback);

		// clear button
		JButton btnClear = new JButton("清除");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (recordscript.mFBImage != null) {
					recordscript.ClearScreen();
				} else {
					JOptionPane.showMessageDialog(autoScriptUImain, "请先录制脚本.", "消息", JOptionPane.ERROR_MESSAGE);
				}
				logger.info("clear record screen button");
			}
		});
		btnClear.setBounds(500, 100, 100, 25);
		add(btnClear);
		// sleep button
		JButton btnESleep = new JButton("睡眠(毫秒)");
		btnESleep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String time = JOptionPane.showInputDialog(autoScriptUImain, "请输入睡眠时间(毫秒):", "输入",
						JOptionPane.INFORMATION_MESSAGE);
				if (time != null) {
					if (!time.equals("") && HelperUtil.isInteger(time)) {
						editscript.Sleep(time);
					} else {
						JOptionPane.showMessageDialog(autoScriptUImain, "请输入数字!", "消息", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnESleep.setBounds(500, 230, 100, 25);
		add(btnESleep);
		// pressbutton button
		JButton btnEPressbutton = new JButton("物理按键");
		btnEPressbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] buttonOption = { "BACK", "HOME", "MENU", "POWER", "VOLUME_DOWN", "VOLUME_UP" };
				int response = JOptionPane.showOptionDialog(autoScriptUImain, "请选择插入的物理按键名:", "消息",
						JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, buttonOption,
						buttonOption[0]);
				if (response == 0) {
					editscript.Pressbutton("BACK");
				} else if (response == 1) {
					editscript.Pressbutton("HOME");
				} else if (response == 2) {
					editscript.Pressbutton("MENU");
				} else if (response == 3) {
					editscript.Pressbutton("POWER");
				} else if (response == 4) {
					editscript.Pressbutton("VOLUME_DOWN");
				} else if (response == 5) {
					editscript.Pressbutton("VOLUME_UP");
				}
			}
		});
		btnEPressbutton.setBounds(610, 265, 100, 25);
		add(btnEPressbutton);
		// start loop button
		JButton btnEStartLoop = new JButton("=开始循环=");
		btnEStartLoop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String time = JOptionPane.showInputDialog(autoScriptUImain, "请输入循环次数:", "输入",
						JOptionPane.INFORMATION_MESSAGE);
				if (time != null) {
					if (!time.equals("") && HelperUtil.isInteger(time) && !time.equals("0")) {
						editscript.Startloop(time);
					} else {
						JOptionPane.showMessageDialog(autoScriptUImain, "请输入数字!", "消息", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnEStartLoop.setBounds(500, 195, 100, 25);
		add(btnEStartLoop);
		// end loop button
		JButton btnEEndLoop = new JButton("=结束循环=");
		btnEEndLoop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editscript.Endloop();
			}
		});
		btnEEndLoop.setBounds(610, 195, 100, 25);
		add(btnEEndLoop);
		// screen cap button
		JButton btnScreencap = new JButton("截图");
		btnScreencap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editscript.Screencap();
			}
		});
		btnScreencap.setBounds(610, 230, 100, 25);
		add(btnScreencap);
		// lab edit
		JLabel lblEdit = new JLabel("插入命令");
		lblEdit.setBounds(500, 170, 124, 15);
		add(lblEdit);
		// Notes button
		JButton btnNotes = new JButton("**备注**");
		btnNotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String notes = JOptionPane.showInputDialog(autoScriptUImain, "请输入备注:", "输入",
						JOptionPane.INFORMATION_MESSAGE);
				if (notes != null) {
					editscript.Notes(notes);
				}
			}
		});
		btnNotes.setBounds(500, 335, 100, 25);
		add(btnNotes);
		// Save script button
		JButton btnSaveScript = new JButton("保存脚本");
		btnSaveScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editscript.SaveScript();
				logger.info("save script button");
			}
		});
		btnSaveScript.setBounds(610, 101, 100, 25);
		add(btnSaveScript);
		// load script button
		JButton btnLoadScript = new JButton("载入脚本");
		btnLoadScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editscript.LoadScript();
				logger.info("load script button");
			}
		});
		btnLoadScript.setBounds(500, 135, 100, 25);
		add(btnLoadScript);
		// detail button
		JButton detail = new JButton("脚本说明");
		detail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(autoScriptUImain,
						"语法说明:\n" + "1.如Tap:(x,x),1000ms最后面的1000ms表示执行后等待1秒,0表示不等待\n"
								+ "2.屏幕截图保存在桌面/QALogs/Script/ScreenCap中\n" + "3.向左横屏模式表示手机屏幕向左旋转后的模式,如短信界面向左横屏手机.",
						"消息", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		detail.setBounds(610, 335, 100, 25);
		add(detail);
		// wake button
		JButton btnWake = new JButton("亮屏");
		btnWake.setToolTipText("唤醒屏幕");
		btnWake.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editscript.Wake();
			}
		});
		btnWake.setBounds(610, 300, 100, 25);
		add(btnWake);

		// type button
		JButton btnType = new JButton("输入字符串");
		btnType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = JOptionPane.showInputDialog(autoScriptUImain, "请输入需要在设备上输入的字符串:", "输入",
						JOptionPane.INFORMATION_MESSAGE);
				if (str != null && !str.equals("")) {
					editscript.Type(str);
				}
			}
		});
		btnType.setBounds(500, 300, 100, 25);
		add(btnType);
		// 启动某个 Activity
		JButton btnStartActivity = new JButton("启动应用");
		btnStartActivity.setToolTipText("通过Package/Activity启动应用");
		btnStartActivity.setBounds(500, 265, 100, 25);
		btnStartActivity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String str = JOptionPane.showInputDialog(autoScriptUImain, "请输入需要启动的应用路径\"package/activity\":", "输入",
						JOptionPane.INFORMATION_MESSAGE);
				if (str != null && !str.equals("")) {
					editscript.StartActivity(str);
				}
			}
		});
		add(btnStartActivity);

	}

	// setLandscapeMode
	public void setmPortraitMode(boolean mPortrait) {
		if (!mPortrait) {
			autoscriptshotui.setmPortrait(false);
			autoscriptshotui.setmPortraitSize(false);
			recordscript.setmPortrait(false);
			textAreaShowScript.setBounds(10, 240, 400, 225);
			scrollPaneShowXY.setBounds(10, 240, 400, 225);
			textAreaShowScript.setCaretPosition(textAreaShowScript.getText().length());
		} else {
			autoscriptshotui.setmPortrait(true);
			recordscript.setmPortrait(true);
			autoscriptshotui.setmPortraitSize(true);
			textAreaShowScript.setBounds(250, 0, 225, 400);
			scrollPaneShowXY.setBounds(250, 0, 225, 400);
			textAreaShowScript.setCaretPosition(textAreaShowScript.getText().length());
		}
	}

	// get isstartrecord
	public boolean getisstartrecord() {
		return isstartrecord;
	}

	// get Btn startrecord
	public JButton getbtnStartRecord() {
		return btnStartRecord;
	}

	// get isstartplayback
	public boolean getisstartplayback() {
		return isstartplayback;
	}

	// get Btn btnPlayback
	public JButton getbtnbtnPlayback() {
		return btnPlayback;
	}
}
