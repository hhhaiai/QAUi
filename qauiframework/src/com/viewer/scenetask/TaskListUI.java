package com.viewer.scenetask;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.helper.TimeUtil;

public class TaskListUI extends JScrollPane {

	private static final long serialVersionUID = 6220598111971331611L;
	Logger logger = LoggerFactory.getLogger(TaskListUI.class);
	int taskcount = 0;
	int gridy = 0;
	GridBox gridBox;
	Map<Long, List<JButton>> btnMap; // 按钮Map
	Map<Long, Map<String, Object>> tasksMap;// 任务Map
	String deviceOS;
	JPanel jPanel = new JPanel();

	/**
	 * Create the panel.
	 */
	public TaskListUI(String deviceOS) {
		setViewportView(jPanel);
		jPanel.setBounds(0, 0, 450, 800);
		jPanel.setLayout(new GridBagLayout());
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		/**
		 * 初始化
		 */
		this.deviceOS = deviceOS;
		btnMap = new LinkedHashMap<>();
		tasksMap = new LinkedHashMap<>();
		gridBox = new GridBox();
		gridBox.setInsets(0, 0, 2, 2);
	}

	/**
	 * 增加一个任务
	 * 
	 * @param taskMap
	 * @return
	 */
	public boolean addTask(Map<String, Object> taskMap) {
		if (taskcount >= 99)
			return false;// 最多只能添加多少个任务
		taskcount++;
		JButton btn_scene = new JButton();
		if (taskMap.get(Cparams.type).toString().equals(Cconfig.TASK_TYPE_SCENE)) {
			btn_scene.setText((String) taskMap.get(Cparams.name));
		} else if (taskMap.get(Cparams.type).toString().equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS)) {
			btn_scene.setText(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS);
		} else if (taskMap.get(Cparams.type).toString().equals(Cconfig.TASK_TYPE_MONKEY_ANDROID_APPIUM)) {
			btn_scene.setText(Cconfig.TASK_TYPE_MONKEY_ANDROID_APPIUM);
		} else if (taskMap.get(Cparams.type).toString().equals(Cconfig.TASK_TYPE_MONKEY_IOS_APPIUM)) {
			btn_scene.setText(Cconfig.TASK_TYPE_MONKEY_IOS_APPIUM);
		}
		btn_scene.setToolTipText("修改任务");
		jPanel.add(btn_scene, gridBox.setGridXY(0, gridy));
		JButton btn_up = new JButton("↑");
		btn_up.setToolTipText("上移任务");
		jPanel.add(btn_up, gridBox.setGridXY(1, gridy));
		JButton btn_down = new JButton("↓");
		btn_down.setToolTipText("下移任务");
		jPanel.add(btn_down, gridBox.setGridXY(2, gridy));
		JButton btn_cancel = new JButton("取消");
		btn_cancel.setToolTipText("删除任务");
		jPanel.add(btn_cancel, gridBox.setGridXY(3, gridy));

		List<JButton> btnList = new ArrayList<>();
		btnList.add(btn_scene);
		btnList.add(btn_up);
		btnList.add(btn_down);
		btnList.add(btn_cancel);
		// 增加
		Long key = TimeUtil.getTime();
		logger.info("add task,key=" + key);
		tasksMap.put(key, taskMap);
		btnMap.put(key, btnList);
		// 详情按钮
		btn_scene.addActionListener(e -> {
			// TODO Auto-generated method stub
			logger.info("press button " + btn_scene.getText() + ",key=" + key);
			// StringBuffer strbuf=new StringBuffer();
			// strbuf.append("1. 执行场景: "+(String)taskMap.get(Cparams.name)+"\n");
			// strbuf.append("2.
			// Appium服务器地址:"+(String)taskMap.get(Cparams.appiumserverurl)+"\n");
			// strbuf.append("3. 截图方式:"+(String)taskMap.get(Cparams.screenshot)+"\n");
			// strbuf.append("4. Appium Capability设置:\n");
			// ((Map<String,
			// String>)taskMap.get("capability")).entrySet().forEach(i->strbuf.append("
			// "+i.getKey()+":"+i.getValue()+"\n"));
			// JOptionPane.showMessageDialog(null, strbuf.toString(),
			// "任务设置详情", JOptionPane.INFORMATION_MESSAGE,null);
			if (tasksMap.get(key) == null)
				return;
			TaskDetailBoxUI taskDetailBoxUI = new TaskDetailBoxUI(key, tasksMap, deviceOS);
			taskDetailBoxUI.setVisible(true);

		});

		// 取消按钮
		btn_cancel.addActionListener(e -> {
			// TODO Auto-generated method stub
			logger.info("remove task : " + btn_scene.getText() + ",key=" + key);
			taskcount--;
			removeBtn(key);
			removeTask(key);

		});
		// 上移按钮
		btn_up.addActionListener(e -> {
			logger.info("move up task : " + btn_scene.getText() + ",key=" + key);
			List<JButton> tempJButtons = null;
			Map<String, Object> tempMap = null;
			Long tempkey = null;

			Map<Long, List<JButton>> tempbtnMap = new LinkedHashMap<>();
			Map<Long, Map<String, Object>> temptasksMap = new LinkedHashMap<>();
			boolean istop = true;// 第一个禁止移动
			for (Entry<Long, List<JButton>> entry : btnMap.entrySet()) {
				if (istop && entry.getKey().equals(key)) {
					logger.info("this task is on top!");
					return;
				} else {
					istop = false;
				}
				if (entry.getKey().equals(key)) {
					tempbtnMap.remove(tempkey);
					tempbtnMap.put(entry.getKey(), entry.getValue());
					tempbtnMap.put(tempkey, tempJButtons);
				} else {
					tempkey = entry.getKey();
					tempJButtons = entry.getValue();
					tempbtnMap.put(entry.getKey(), entry.getValue());
				}
			}
			btnMap.clear();
			tempbtnMap.entrySet().forEach(i -> btnMap.put(i.getKey(), i.getValue()));

			for (Entry<Long, Map<String, Object>> entry : tasksMap.entrySet()) {
				if (entry.getKey().equals(key)) {
					temptasksMap.remove(tempkey);
					temptasksMap.put(entry.getKey(), entry.getValue());
					temptasksMap.put(tempkey, tempMap);
				} else {
					tempMap = entry.getValue();
					tempkey = entry.getKey();
					temptasksMap.put(entry.getKey(), entry.getValue());
				}
			}
			tasksMap.clear();
			temptasksMap.entrySet().forEach(i -> tasksMap.put(i.getKey(), i.getValue()));
			// tasksMap.entrySet().forEach(i->logger.info(tasksMap.size()+"!!!"+i.getKey()));
			resortButtons();
		});
		// 下移按钮
		btn_down.addActionListener(e -> {
			logger.info("move down task : " + btn_scene.getText() + ",key=" + key);
			List<JButton> tempJButtons = null;
			Map<String, Object> tempMap = null;
			Long tempkey = null;

			Map<Long, List<JButton>> tempbtnMap = new LinkedHashMap<>();
			Map<Long, Map<String, Object>> temptasksMap = new LinkedHashMap<>();
			int bottom = 0;// 最后一个禁止移动
			boolean next = false;
			for (Entry<Long, List<JButton>> entry : btnMap.entrySet()) {
				bottom++;
				if (btnMap.size() == bottom && entry.getKey().equals(key)) {
					logger.info("this task is in bottom");
					return;
				}
				if (entry.getKey().equals(key)) {
					tempkey = entry.getKey();
					tempJButtons = entry.getValue();
					next = true;
				} else {
					if (next) {
						tempbtnMap.put(entry.getKey(), entry.getValue());
						tempbtnMap.put(tempkey, tempJButtons);
						next = false;
					} else {
						tempbtnMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
			btnMap.clear();
			tempbtnMap.entrySet().forEach(i -> btnMap.put(i.getKey(), i.getValue()));
			next = false;
			for (Entry<Long, Map<String, Object>> entry : tasksMap.entrySet()) {
				if (entry.getKey().equals(key)) {
					tempMap = entry.getValue();
					tempkey = entry.getKey();
					next = true;
				} else {
					if (next) {
						temptasksMap.put(entry.getKey(), entry.getValue());
						temptasksMap.put(tempkey, tempMap);
						next = false;
					} else {
						temptasksMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
			tasksMap.clear();
			temptasksMap.entrySet().forEach(i -> tasksMap.put(i.getKey(), i.getValue()));
			// tasksMap.entrySet().forEach(i->logger.info(tasksMap.size()+"!!!"+i.getKey()));
			resortButtons();
		});
		// 布局修改
		gridy++;
		this.validate();// 重新布局
		this.repaint(); // 重绘界面
		this.getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());// 拉动在最下面
		// revalidate();//=validate+repaint
		return true;
	}

	/**
	 * 重新布局按钮
	 */
	public void resortButtons() {
		btnMap.entrySet().forEach(i -> i.getValue().forEach(b -> jPanel.remove(b)));
		int column = 0;
		for (Entry<Long, List<JButton>> entry : btnMap.entrySet()) {
			List<JButton> list = entry.getValue();
			int row = 0;
			GridBagConstraints style = new GridBagConstraints();
			style.insets = new Insets(0, 0, 5, 5);
			style.fill = GridBagConstraints.BOTH;
			for (JButton btn : list) {
				style.gridx = row;
				style.gridy = column;
				btn.setFocusable(false);// 去掉焦点
				jPanel.add(btn, style);
				row++;
			}
			column++;
		}
		this.validate();// 重新布局
		this.repaint(); // 重绘界面
	}

	/**
	 * 清除指定任务
	 * 
	 * @param key
	 */
	public void removeTask(Long key) {
		for (Entry<Long, Map<String, Object>> entry : tasksMap.entrySet()) {
			if (entry.getKey().equals(key)) {
				tasksMap.remove(key);
				break;
			}
		}
		logger.info("left scene tasksMap=" + tasksMap.size() + ",btnMap=" + btnMap.size());
	}

	/**
	 * 清除指定按钮行
	 * 
	 * @param key
	 */
	public void removeBtn(Long key) {
		for (Entry<Long, List<JButton>> entry : btnMap.entrySet()) {
			if (entry.getKey().equals(key)) {
				entry.getValue().forEach(i -> jPanel.remove(i));
				this.validate();// 重新布局
				this.repaint(); // 重绘界面
				btnMap.remove(key);
				break;
			}
		}
	}

	/**
	 * 停用当前运行任务的相关按钮
	 * 
	 * @param key
	 */
	public void disableBtns(Long key) {
		for (int i = 0; i < btnMap.get(key).size() - 1; i++) {
			btnMap.get(key).get(i).setEnabled(false);
		}
	}

	/**
	 * 得到任务Map
	 * 
	 * @return
	 */
	public Map<Long, Map<String, Object>> getTasksMap() {
		return tasksMap;
	}

	/**
	 * 得到按钮Map
	 * 
	 * @return
	 */
	public Map<Long, List<JButton>> getBtnMap() {
		return btnMap;
	}
}
