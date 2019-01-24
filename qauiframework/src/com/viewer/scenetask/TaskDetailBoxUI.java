package com.viewer.scenetask;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.constant.Cparams;
import com.viewer.android.monkeytask.SYSAndroidMonkeyConfigUI;
import com.viewer.main.MainRun;

public class TaskDetailBoxUI extends JFrame {

	private static final long serialVersionUID = 3424438367661406447L;
	Logger logger = LoggerFactory.getLogger(TaskDetailBoxUI.class);
	private JPanel contentPane;
	JTextArea textArea;
	private JButton btnOK;
	private JButton btnCancel;
	SceneConfigUI sceneconfigUI = null;
	SYSAndroidMonkeyConfigUI sysAndroidMonkeyConfigUI=null;
	Map<String, Object> taskMap;
	Map<Long, Map<String, Object>> tasksMap;
	String deviceOS;
	/**
	 * Create the frame.
	 */
	public TaskDetailBoxUI(long key,Map<Long, Map<String, Object>> tasksMap,String deviceOS) {
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("任务详情");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());
		/**
		 * 初始化
		 */
		this.tasksMap=tasksMap;
		this.deviceOS=deviceOS;
		for(Entry<Long, Map<String, Object>> entry:tasksMap.entrySet()){
			if(entry.getKey()==key){
				this.taskMap=entry.getValue();
				break;
			}
		}
		if(taskMap.get(Cparams.type).equals(Cconfig.TASK_TYPE_SCENE)) {
			sceneconfigUI=new SceneConfigUI((String)((Map<String, String>)taskMap.get(Cparams.capability)).get(Cparams.udid),this.deviceOS);
			sceneconfigUI.setValueByScene(taskMap);
			JScrollPane configScroll = new JScrollPane(sceneconfigUI);
			configScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
			configScroll.setBounds(25, 10, 450, 300);
			contentPane.add(configScroll);	
		}else {
			sysAndroidMonkeyConfigUI=new SYSAndroidMonkeyConfigUI(taskMap.get(Cparams.udid).toString());
			sysAndroidMonkeyConfigUI.setValueByScene(taskMap);
			JScrollPane configScroll = new JScrollPane(sysAndroidMonkeyConfigUI);
			configScroll.setBorder(new LineBorder(new Color(0, 0, 0)));
			configScroll.setBounds(25, 10, 450, 300);
			contentPane.add(configScroll);	
		}
		
		//确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");
				for(Entry<Long, Map<String, Object>> entry:tasksMap.entrySet()){
					if(entry.getKey()==key){
						tasksMap.put(key, sceneconfigUI.getSceneMap());
						break;
					}
				}
				dispose();
				
			}
		});
		btnOK.setBounds(271, 347, 100, 25);
		contentPane.add(btnOK);
		//取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press cancel button");
				
				dispose();
			}
		});
		btnCancel.setBounds(383, 347, 100, 25);
		contentPane.add(btnCancel);

	}


}
