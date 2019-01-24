package com.DataHandel;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;
import com.Util.HelperUtil;
import com.Util.Tailer;
import com.Util.TailerListener;
import com.Util.TailerListenerAdapter;
import com.Util.TimeUtil;
import com.Viewer.MainRun;

public abstract class IOSIfuseContainer {
	Logger logger = LoggerFactory.getLogger(IOSIfuseContainer.class);
	String packagename = "";
	String logpath = "";

	boolean isstop = false;
	File folder = null;
	File file_log = null;
	String udid;

	public IOSIfuseContainer(String udid) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
	}

	/**
	 * 监控日志
	 * 
	 * @return -1,-2, 1
	 */
	public int monitorLog(boolean istail) {
		initFolder(MainRun.QALogfile + "/temp/ifuse/");
		if (mountFile()) {
			file_log = statFile();
			if (file_log != null) {
				// JOptionPane.showMessageDialog(null, "挂载成功,开始监控日志...", "消息",
				// JOptionPane.INFORMATION_MESSAGE);FXUI不能使用
				TailerListener listener = new TailerListenerAdapter() {
					@Override
					public void handle(String line) {
						// if (++count % 100000 == 0) {
						// logger.info("{} lines sent since the program up.", count);
						// }
						if (line == null) {
							logger.warn("should not read empty line.");
							return;
						} else {
							// do something ...
							// System.out.println(line);
							readline(line);
						}
					}

					@Override
					public boolean isCancelled() {
						return isstop;
					}
				};
				Tailer tailer = new Tailer(file_log, listener, 500, istail);
				tailer.run();
			} else {
				logger.error("未找到日志文件,请先启动应用后重试!");
				return -1;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			umountFile(folder);
		} else {
			logger.error("ios mount file failed:" + folder.getAbsolutePath());
			HelperUtil.delAllFile(folder.getAbsolutePath());
			return -2;
		}
		return 1;
	}

	public abstract void readline(String line);

	/**
	 * 得到日志文件
	 * 
	 * @return
	 */
	public File getFile_log() {
		return file_log;
	}

	/**
	 * 设置需要挂载的应用包名
	 * 
	 * @param packagename
	 */
	public void setIOSpackagename(String packagename) {
		this.packagename = packagename;
	}

	/**
	 * 设置应用日志路径
	 * 
	 * @param logpath
	 */
	public void setLogpath(String logpath) {
		this.logpath = logpath;
	}

	/**
	 * 停止监控
	 */
	public void stop() {
		isstop = true;
	}

	/**
	 * 初始化设置挂载路径
	 * 
	 * @param path
	 * @return
	 */
	public File initFolder(String path) {
		File temp_ifuse_folder = new File(path);
		if (temp_ifuse_folder.exists() && temp_ifuse_folder.isDirectory()) {
			for (File file : temp_ifuse_folder.listFiles()) {
				umountFile(file);
			}
		}
		folder = new File(path + "/" + TimeUtil.getTime4File());
		folder.mkdirs();
		return folder;
	}

	/**
	 * ios挂载文件
	 * 
	 * @return
	 */
	public boolean mountFile() {
		logger.info("mount file:" + folder.getAbsolutePath());
		logger.info("iOS packagename=" + packagename);
		String[] strings = Excute.execcmd(udid, MainRun.paramsBean.getMACcmd() + "/ifuse -u " + udid + " --container "
				+ packagename + " " + folder.getAbsolutePath(), 1, true);
		if (strings[0].equals("")) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("ifuse --container:" + strings[0]);
			return true;
		} else {
			logger.info("ifuse --container: error:" + strings[0] + "~~" + strings[1]);
			return false;
		}
	}

	/**
	 * ios卸载文件
	 * 
	 * @param file
	 * @return
	 */
	public boolean umountFile(File folder) {
		String[] strings = Excute.execcmd(udid, "/sbin/umount " + folder.getAbsolutePath(), 1, true);
		if (strings[0].equals("")) {
			logger.info("umount file:" + strings[0]);
			folder.delete();
			return true;
		} else {
			logger.info("umount file error:" + strings[0] + "~~" + strings[1]);
			return false;
		}
	}

	/**
	 * 更新文件
	 * 
	 * @param file
	 */
	public File statFile() {
		File folder_library = new File(folder.getAbsoluteFile() + logpath);
		logger.info("log folder: " + folder_library.getAbsolutePath());
		File[] files = folder_library.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return !f.getName().toLowerCase().startsWith(".")
						&& (f.getName().toLowerCase().endsWith(".log") || f.getName().toLowerCase().endsWith(".txt"));
			}
		});

		if (files != null && files.length > 0) {
			Arrays.sort(files, new Comparator<File>() {// 从大到小排列

				@Override
				public int compare(File a, File b) {
					// TODO Auto-generated method stub
					long a_data = a.lastModified();
					long b_data = b.lastModified();
					long diff = a_data - b_data;
					if (diff < 0) {
						return 1;
					} else if (diff == 0) {
						return 0;
					} else {
						return -1;
					}
				}

			});
			File file = files[0];
			if (file != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						logger.info("start stat file:" + file.getAbsolutePath());
						while (!isstop && file != null) {
							String[] strings = Excute.execcmd(udid,
									"/usr/bin/stat " + file.getAbsolutePath().replaceAll("\\s+", "\" \""), 1, true);
							if (strings[0].contains("stat: Input/output error")
									|| strings[1].contains("stat: Input/output error")) {
								logger.info("stat error:" + strings[0] + "~~" + strings[1]);
								isstop = true;
							}
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
							}
						}
						logger.info("stop stat file");
					}
				}).start();
			}
			return file;
		}
		return null;
	}
}
