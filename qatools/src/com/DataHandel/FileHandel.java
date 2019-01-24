package com.DataHandel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;
import com.constant.Cconfig;

import javafx.scene.layout.VBox;

public class FileHandel {
	Logger logger = LoggerFactory.getLogger(FileHandel.class);
	VBox vbox_chart;
	DataFilter dataFilter;
	boolean isstop = true;
	File logfile;

	public FileHandel(VBox vbox_chart) {
		// TODO Auto-generated constructor stub
		this.vbox_chart = vbox_chart;
		dataFilter = new DataFilter(vbox_chart);
	}

	/**
	 * 开始分析文件
	 * 
	 * @param settingsMap
	 * @param file
	 */
	public void start(List<Map<String, String>> settingsMapList, File file) {
		settingsMapList.forEach(e -> e.put(Cconfig.DATAHANDEL_SETTINGS_TYPE, Cconfig.DATAHANDEL_SETTINGS_TYPE_DRAWING));
		dataFilter.setSettingsMapList(settingsMapList);
		isstop = false;
		if (file != null && file.exists() && file.isFile()) {
			new Thread(new FileHandelRunnable(file)).start();
		}
	}

	/**
	 * 日志文件分析线程
	 * 
	 * @author auto
	 *
	 */
	class FileHandelRunnable implements Runnable {
		File file;

		public FileHandelRunnable(File file) {
			// TODO Auto-generated constructor stub
			this.file = file;
		}

		@Override
		public void run() {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (dataFilter.filterLine(line) && logfile != null) {
						HelperUtil.file_write_line(logfile.getAbsolutePath(), line + "\r\n", true);
					}
				}
			} catch (IOException e) {
				logger.error("Exception", e);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
			}
			isstop = true;
		}
	}

	/**
	 * 获取数据过滤器
	 * 
	 * @return
	 */
	public DataFilter getDataFilter() {
		return dataFilter;
	}

	/**
	 * 设置保存日志文件
	 * 
	 * @param logfile
	 */
	public void setSaveLog(File logfile) {
		this.logfile = logfile;
	}
}
