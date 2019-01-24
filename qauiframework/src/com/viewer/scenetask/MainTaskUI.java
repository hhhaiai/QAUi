package com.viewer.scenetask;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.CMDUtil;
import com.helper.HelperUtil;
import com.log.SceneLogUtil;
import com.task.GetTaskFromNoteXML;
import com.task.TaskRunner;
import com.viewer.android.monkeytask.SYSAndroidMonkeyConfigUI;
import com.viewer.main.MainRun;

public class MainTaskUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4539642239200910147L;
	Logger logger = LoggerFactory.getLogger(MainTaskUI.class);
	private JPanel contentPane;
	TextAreaUI textAreaUI = new TextAreaUI(720, 650);
	JTextArea textAreaShow = textAreaUI.getJTextArea();
	JScrollPane scrollPaneShow = textAreaUI.getJScrollPane();
	JButton btn_addtask;
	JButton btn_starttask;
	JButton btn_config;
	JComboBox<String> comboBox_tasktype;
	boolean isstartScene = false;

	SceneConfigUI sceneConfigUI;
	SYSAndroidMonkeyConfigUI sysAndroidMonkeyConfigUI;
	TaskListUI tasklistUI;
	Map<Long, Map<String, Object>> tasksMap;

	String udid;
	String deviceOS;
	TaskRunner taskRunner;
	SceneLogUtil oplog;
	private JButton btn_report;

	/**
	 * Create the frame.
	 */
	public MainTaskUI(String udid, String deviceOS) {
		setResizable(false);
		setTitle(deviceOS + "设备: " + udid);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		setLocationRelativeTo(MainRun.mainUI);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setIconImage(MainRun.settingsBean.getLogo().getImage());
		/**
		 * 初始化
		 */
		this.udid = udid;
		this.deviceOS = deviceOS;
		oplog = new SceneLogUtil(udid, textAreaShow);
		taskRunner = new TaskRunner(oplog, udid, deviceOS);// 场景运行

		sceneConfigUI = new SceneConfigUI(udid, this.deviceOS);
		JScrollPane sceneconfigScroll = new JScrollPane(sceneConfigUI);
		// sceneconfigScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sceneconfigScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		sceneconfigScroll.setBounds(732, 36, 450, 320);
		contentPane.add(sceneconfigScroll);

		sysAndroidMonkeyConfigUI = new SYSAndroidMonkeyConfigUI(udid);
		JScrollPane monkeyconfigScroll = new JScrollPane(sysAndroidMonkeyConfigUI);
		monkeyconfigScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		monkeyconfigScroll.setBounds(732, 36, 450, 320);
		contentPane.add(monkeyconfigScroll);
		monkeyconfigScroll.setVisible(false);

		tasklistUI = new TaskListUI(this.deviceOS);
		tasklistUI.setBorder(null);
		// tasklistScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tasklistUI.setBounds(732, 422, 450, 200);
		contentPane.add(tasklistUI);

		textAreaUI.setLocation(10, 30);
		getContentPane().add(textAreaUI);

		/**
		 * 开始任务按钮
		 */
		btn_starttask = new JButton("开始");
		btn_starttask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isstartScene) {
					logger.info("press btn_starttask button stop");
					if (JOptionPane.showConfirmDialog(contentPane, "是否强制停止任务?(可能会造成未知异常)", "消息",
							JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION,
							MainRun.settingsBean.getLogo()) != JOptionPane.OK_OPTION)
						return;
					if (JOptionPane.showConfirmDialog(contentPane, "现在后悔还来得及!你确定不停止了吗?", "消息",
							JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION,
							MainRun.settingsBean.getLogo()) == JOptionPane.OK_OPTION)
						return;
					oplog.logTask("开始强制中断与Appium服务器连接...");
					btn_starttask.setEnabled(false);
					isstartScene = false;
					btn_starttask.setText("开始");
					taskRunner.quit(true);// 停止driver
					oplog.logTask("与Appium服务器的连接已被强制中断!");
				} else {
					logger.info("press btn_starttask button start");
					tasksMap = tasklistUI.getTasksMap();
					if (tasksMap.size() == 0) {
						JOptionPane.showMessageDialog(contentPane, "请先添加任务!", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
						return;
					}
					if (textAreaShow.getText().length() > 0) {
						if (JOptionPane.showConfirmDialog(contentPane, "是否先清除日志?", "消息", JOptionPane.YES_NO_OPTION,
								JOptionPane.YES_NO_OPTION, MainRun.settingsBean.getLogo()) == 0)
							textAreaShow.setText("");
					}
					if (JOptionPane.showConfirmDialog(contentPane, "是否开始执行计划任务?", "消息", JOptionPane.YES_NO_OPTION,
							JOptionPane.YES_NO_OPTION, MainRun.settingsBean.getLogo()) == 0) {
						StartTaskThread startTaskThread = new StartTaskThread(tasksMap);
						new Thread(startTaskThread).start();
					} else {
						return;
					}
					isstartScene = true;
					btn_starttask.setText("停止");
				}
			}
		});
		btn_starttask.setBounds(1065, 634, 117, 29);
		contentPane.add(btn_starttask);

		/**
		 * 添加任务按钮
		 */
		btn_addtask = new JButton("添加任务");
		btn_addtask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_addtask button");
				if (comboBox_tasktype.getSelectedItem().equals(Cconfig.TASK_TYPE_SCENE)) {
					if (!HelperUtil.checkEmail((String) sceneConfigUI.getSceneMap().get(Cparams.email_to))
							|| !HelperUtil.checkEmail((String) sceneConfigUI.getSceneMap().get(Cparams.email_cc))) {
						JOptionPane.showMessageDialog(contentPane, "邮箱格式错误,请检查!", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
						return;
					}
					if (!tasklistUI.addTask(sceneConfigUI.getSceneMap())) {
						JOptionPane.showMessageDialog(contentPane, "添加任务数量达到上限!", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
						return;
					}
				} else if (comboBox_tasktype.getSelectedItem().equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {
					if (!HelperUtil.checkEmail((String) sysAndroidMonkeyConfigUI.getMonkeyMap().get(Cparams.email_to))
							|| !HelperUtil.checkEmail(
									(String) sysAndroidMonkeyConfigUI.getMonkeyMap().get(Cparams.email_cc))) {
						JOptionPane.showMessageDialog(contentPane, "邮箱格式错误,请检查!", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
						return;
					}
					if (!tasklistUI.addTask(sysAndroidMonkeyConfigUI.getMonkeyMap())) {
						JOptionPane.showMessageDialog(contentPane, "添加任务数量达到上限!", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
						return;
					}
				}
			}
		});
		btn_addtask.setBounds(1065, 366, 117, 29);
		contentPane.add(btn_addtask);

		JLabel lbl_tasklist = new JLabel("任务列表");
		lbl_tasklist.setBounds(732, 400, 61, 16);
		contentPane.add(lbl_tasklist);

		JLabel lbl_log = new JLabel("运行日志");
		lbl_log.setBounds(10, 6, 61, 16);
		contentPane.add(lbl_log);

		JLabel lbl_scene = new JLabel("任务设置");
		lbl_scene.setBounds(732, 6, 61, 16);
		contentPane.add(lbl_scene);

		btn_config = new JButton("配置");
		btn_config.setToolTipText("场景任务配置");
		btn_config.setBounds(732, 634, 117, 29);
		btn_config.addActionListener(e -> {
			logger.info("press btn_config button");
			TaskConfigBoxUI taskConfigBoxUI = new TaskConfigBoxUI(deviceOS, taskRunner.getTaskConfigMap());
			taskConfigBoxUI.setVisible(true);
		});
		contentPane.add(btn_config);

		btn_report = new JButton("报告");
		btn_report.setToolTipText("打开当前测试报告文件夹");
		btn_report.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btn_report button");
				if (taskRunner.getCatalog() == null) {
					JOptionPane.showMessageDialog(contentPane, "无报告,请先开始任务...", "消息", JOptionPane.ERROR_MESSAGE,
							MainRun.settingsBean.getLogo());
					return;
				}
				if (MainRun.settingsBean.getSystem() == Cconfig.WINDOWS) {
					CMDUtil.execcmd("explorer " + taskRunner.getCatalog().getAbsolutePath(), CAndroidCMD.SYSCMD, true);
				} else if (MainRun.settingsBean.getSystem() == Cconfig.MAC) {
					CMDUtil.execcmd("open " + taskRunner.getCatalog().getAbsolutePath(), CAndroidCMD.SYSCMD, true);
				} else {

				}

			}
		});
		btn_report.setBounds(854, 634, 117, 29);
		contentPane.add(btn_report);
		/**
		 * 任务类型选择
		 */
		comboBox_tasktype = new JComboBox<String>();
		comboBox_tasktype.addItem(Cconfig.TASK_TYPE_SCENE);
		if (deviceOS.equals(Cconfig.ANDROID)) {
			comboBox_tasktype.addItem(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
		}
		comboBox_tasktype.setSelectedItem(Cconfig.TASK_TYPE_SCENE);
		comboBox_tasktype.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (comboBox_tasktype.getSelectedItem().equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {
						logger.info("select TASK_TYPE_MONKEY_ANDROID_SYS task");
						monkeyconfigScroll.setVisible(true);
						sceneconfigScroll.setVisible(false);
					} else if (comboBox_tasktype.getSelectedItem().equals(Cconfig.TASK_TYPE_SCENE)) {
						logger.info("select scene task");
						monkeyconfigScroll.setVisible(false);
						sceneconfigScroll.setVisible(true);
					}
				}
			}
		});
		comboBox_tasktype.setBounds(1022, 2, 160, 27);
		contentPane.add(comboBox_tasktype);

		JButton btn_importtask = new JButton("导入任务");
		btn_importtask.setToolTipText("选择测试报告中的note.xml文件,将会自动添加xml中的失败用例到任务列表");
		btn_importtask.addActionListener(e -> {
			logger.info("press btn_importtask button");
			File file = selectNoteXMLFile();
			if (file == null) {
				return;
			}
			GetTaskFromNoteXML getTaskFromNoteXML = new GetTaskFromNoteXML(udid, deviceOS, file.getAbsolutePath());
			List<Map<String, Object>> list = getTaskFromNoteXML.getSceneMapList();
			if (list.size() == 0) {
				JOptionPane.showMessageDialog(contentPane, "未发现场景任务信息!", "消息", JOptionPane.ERROR_MESSAGE,
						MainRun.settingsBean.getLogo());
				return;
			} else {
				for (Map<String, Object> map : list) {
					map.put(Cparams.run, "GUI");
					if (!tasklistUI.addTask(map)) {
						JOptionPane.showMessageDialog(contentPane, "添加任务数量达到上限!", "消息", JOptionPane.ERROR_MESSAGE,
								MainRun.settingsBean.getLogo());
						return;
					}
				}
			}
		});
		btn_importtask.setBounds(732, 366, 117, 29);
		contentPane.add(btn_importtask);

		// test
	}

	/**
	 * 选择note.xml文件
	 * 
	 * @return
	 */
	private File selectNoteXMLFile() {
		File selectfile = null;
		JFileChooser fileChooser = new JFileChooser(
				taskRunner.getCatalog() != null ? taskRunner.getCatalog().getAbsolutePath()
						: MainRun.settingsBean.getUiReportPath());
		// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.xml";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".xml");
			}
		});
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			logger.info("No file selected.");
			return selectfile;
		}
		selectfile = fileChooser.getSelectedFile();
		logger.info("select files=" + selectfile.getAbsolutePath());
		return selectfile;
	}

	/**
	 * 开始执行计划任务
	 * 
	 * @author Then
	 *
	 */
	class StartTaskThread implements Runnable {
		Map<Long, Map<String, Object>> tasksMap;

		public StartTaskThread(Map<Long, Map<String, Object>> tasksMap) {
			this.tasksMap = tasksMap;
		}

		public void run() {
			oplog.logTask("开始执行计划任务,共" + tasksMap.size() + "个...");
			taskRunner.initRunner();
			// 测试报告相关
			while (tasksMap.size() > 0) {// 末尾会修改tasksMap
				long key = 0;
				Map<String, Object> taskmap = null;
				for (Entry<Long, Map<String, Object>> entry : tasksMap.entrySet()) {
					taskmap = entry.getValue();
					key = entry.getKey();
					break;
				}
				// 禁用按钮
				tasklistUI.disableBtns(key);
				// 运行场景
				taskmap.put(Cparams.run, "GUI");
				taskRunner.runTask(taskmap);
				// 运行完成后删除
				tasklistUI.removeTask(key);
			}
			taskRunner.endRunner();

			isstartScene = false;
			btn_starttask.setText("开始");
			btn_starttask.setEnabled(true);
		}
	}
}
