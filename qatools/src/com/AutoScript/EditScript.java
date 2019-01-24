package com.AutoScript;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.Viewer.MainRun;

public class EditScript {
	Logger logger = LoggerFactory.getLogger(EditScript.class);
	private SimpleDateFormat sDateFormatget = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
	JTextArea textAreaShowScript;
	String saveScriptpath;

	// insert Sleep
	public void Sleep(String time) {
		textAreaShowScript.append("Sleep:(" + time + "ms)\n");
	}

	// insert button
	public void Pressbutton(String button) {
		textAreaShowScript.append("Press:(" + button + "),1000ms\n");
	}

	// start loop
	public void Startloop(String count) {
		textAreaShowScript.append("==Start Loop:(" + count + ")==\n");
	}

	// end loop
	public void Endloop() {
		textAreaShowScript.append("==End Loop==\n");
	}

	// Screen cap
	public void Screencap() {
		textAreaShowScript.append("Screen Cap,1000ms\n");
	}

	// Notes
	public void Notes(String notes) {
		textAreaShowScript.append("**" + notes + "**\n");
	}

	// reboot
	public void Reboot(String time) {
		textAreaShowScript.append("Reboot:(" + time + "ms)\n");
	}

	// log
	public void Startlog() {
		textAreaShowScript.append("Active Log\n");
	}

	// wake
	public void Wake() {
		textAreaShowScript.append("Wake,2000ms\n");
	}

	// type
	public void Type(String str) {
		textAreaShowScript.append("Type:(" + str + "),3000ms\n");
	}

	// startactivity
	public void StartActivity(String str) {
		textAreaShowScript.append("StartActivity:(" + str + "),5000ms\n");
	}

	// save script
	public boolean SaveScript() {
		File folder = new File(MainRun.QALogfile + "/Script");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile + "/Script");
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.script";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".script");
			}
		});
		fileChooser.setSelectedFile(new File("Script_PCtime" + sDateFormatget.format(new Date())));
		if (fileChooser.showSaveDialog(MainRun.mainFrame) != 0)
			return false;
		try {
			File file = fileChooser.getSelectedFile();
			saveScriptpath = file.getAbsolutePath();
			if (!saveScriptpath.endsWith(".script")) {
				file = new File(saveScriptpath + "." + "script");
				saveScriptpath = saveScriptpath + ".script";
			}
			if (file.exists()) {
				int confirm = JOptionPane.showConfirmDialog(null, "文件已存在,是否删除后继续?", "确认", JOptionPane.YES_NO_OPTION);
				if (confirm == 0) {
					file.delete();
				} else {
					logger.info("script exist and do not save");
					return false;
				}
			} else {
				file.createNewFile();
			}
			HelperUtil.file_write_all(saveScriptpath, textAreaShowScript.getText(), true, true);
			return true;
		} catch (Exception e) {
			logger.error("Excepiton", e);
			JOptionPane.showMessageDialog(null, "图片保存失败!", "消息", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	// load script
	public boolean LoadScript() {
		String filepath = SelectScript(saveScriptpath);
		if (!filepath.equals("")) {
			textAreaShowScript.setText(HelperUtil.file_read_all(filepath).toString() + "");
		}
		return true;
	}

	public String SelectScript(String saveScriptpath) {
		if (saveScriptpath == null) {
			saveScriptpath = "";
		}
		String path = "";
		JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile + "/Script");
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.script";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return ext.endsWith(".script");
			}
		});
		fileChooser.setSelectedFile(new File(saveScriptpath));
		if (fileChooser.showOpenDialog(null) != 0) {
			logger.info("No file select");
			return path;
		}
		File file = fileChooser.getSelectedFile();
		if (!file.exists()) {
			logger.info("Invalid file, Pls select correct file!");
			JOptionPane.showMessageDialog(null, "无效的文件,请重新选择.", "消息", JOptionPane.ERROR_MESSAGE);
			return path;
		} else {
			path = file.getAbsolutePath();
		}
		logger.info("select file: " + path);
		return path;
	}

	// set textAreaShowScript
	public void settextAreaShowScript(JTextArea textAreaShowScript) {
		this.textAreaShowScript = textAreaShowScript;
	}
}
