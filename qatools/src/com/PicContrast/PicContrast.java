package com.PicContrast;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;

public class PicContrast {
	Logger logger = LoggerFactory.getLogger(PicContrast.class);

	JTextArea textAreaLogs_Pass;
	JTextArea textAreaLogs_Fail;
	int pass = 0;
	int fail = 0;
	int total = 0;
	Map<File, File> filesMap = new LinkedHashMap<>();
	Iterator<Entry<File, File>> files_Iterator;

	// select files
	public File[] selectfiles() {
		JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile);
		// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.jpg;*.jpeg;*.png;*.bmp;";
			}

			public boolean accept(File f) {
				String ext = f.getName().toLowerCase();
				return f.isDirectory() || ext.endsWith(".jpg") || ext.endsWith(".jpeg") || ext.endsWith(".png")
						|| ext.endsWith(".bmp");
			}
		});
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(null) != 0) {
			logger.info("No file selected.");
			return null;
		}
		File[] selectfiles = fileChooser.getSelectedFiles();
		logger.info("select " + selectfiles.length + " files.");
		return selectfiles;
	}

	// select floder
	public File[] selectfloder() {
		JFileChooser fileChooser = new JFileChooser(MainRun.QALogfile);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
		if (fileChooser.showOpenDialog(null) != 0) {
			logger.info("No floder selected.");
			return null;
		}
		File selectfloder = fileChooser.getSelectedFile();
		// 将文件夹中的符合要求图片转换成FILE数组
		if (selectfloder != null) {
			ArrayList<File> arrayList = new ArrayList<File>();
			for (File file : selectfloder.listFiles()) {
				if (IsPic(file)) {
					arrayList.add(file);
				}
			}
			File[] files = (File[]) arrayList.toArray(new File[arrayList.size()]);
			return files;
		} else {
			return null;
		}
	}

	// 初始化
	public void init(JTextArea textAreaLogs_Pass, JTextArea textAreaLogs_Fail) {
		this.textAreaLogs_Pass = textAreaLogs_Pass;
		this.textAreaLogs_Fail = textAreaLogs_Fail;
	}

	public void RunThread(File[] filesA, File[] filesB, boolean name2simple) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				filesMap.clear();
				Run(filesA, filesB, name2simple);
			}
		}).start();
	}

	// 开始比较
	public void Run(File[] filesA, File[] filesB, boolean name2simple) {
		textAreaLogs_Pass.setText("");
		textAreaLogs_Fail.setText("");
		pass = 0;
		fail = 0;
		total = 0;
		for (File fileA : filesA) {
			if (!IsPic(fileA)) {
				textAreaLogs_Fail.append("来自路径A的【" + fileA.getName() + "】不是图片文件...\n");
				continue;
			}
		}
		for (File fileB : filesB) {
			if (!IsPic(fileB)) {
				textAreaLogs_Fail.append("来自路径B的【" + fileB.getName() + "】不是图片文件...\n");
				continue;
			}
		}
		// 当一边为单个文件时,逐个比较
		if (filesA.length == 1 || filesB.length == 1) {
			for (File fileA : filesA) {
				for (File fileB : filesB) {
					if (!IsPic(fileA) || !IsPic(fileB)) {
						continue;
					}
					total++;
					if (filesA.length == 1) {
						filesMap.put(fileB, fileA);
					} else {
						filesMap.put(fileA, fileB);
					}
				}
			}
		} else { // 相同名称比较
			for (File fileA : filesA) {
				for (File fileB : filesB) {
					if (name2simple) {
						if (fileA.getName().length() > 4 && fileB.getName().length() > 4) {
							if (fileA.getName().substring(4, fileA.getName().length())
									.equals(fileB.getName().substring(4, fileB.getName().length()))) {// 需要对比的图片,去掉前面4位
								if (!IsPic(fileA) || !IsPic(fileB)) {
									continue;
								}
								total++;
								// StatisticsResult(fileA,fileB,Contrast(fileA,fileB));
								filesMap.put(fileA, fileB);
							}
						} else {
							if (fileA.getName().equals(fileB.getName())) {// 需要对比的图片
								if (!IsPic(fileA) || !IsPic(fileB)) {
									continue;
								}
								total++;
								// StatisticsResult(fileA,fileB,Contrast(fileA,fileB));
								filesMap.put(fileA, fileB);
							}
						}
					} else {
						if (fileA.getName().equals(fileB.getName())) {// 需要对比的图片
							if (!IsPic(fileA) || !IsPic(fileB)) {
								continue;
							}
							total++;
							// StatisticsResult(fileA,fileB,Contrast(fileA,fileB));
							filesMap.put(fileA, fileB);
						}
					}
				}
			}
		}
		logger.info("filesMap size=" + filesMap.size());
		files_Iterator = filesMap.entrySet().iterator();
		for (int i = 0; i < 10; i++) {
			PicContrastThread picContrastThread = new PicContrastThread();
			new Thread(picContrastThread).start();
		}
	}

	// 判断文件是否为图片
	public boolean IsPic(File file) {
		if (file.isFile()) {
			if ((file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")
					|| file.getName().toLowerCase().endsWith(".png")
					|| file.getName().toLowerCase().endsWith(".bmp"))) {
				return true;
			}
		}
		return false;
	}

	// 比较图片
	public double Contrast(File fileA, File fileB) {
		try {
			BufferedImage bImageA = ImageIO.read(fileA);
			BufferedImage bImageB = ImageIO.read(fileB);
			ImageComparer imageCom = new ImageComparer(bImageA, bImageB);// 比较算法
																			// //http://blog.csdn.net/jia20003/article/details/7771651
																			// 来源
			return imageCom.modelMatch();// 返回相似度
		} catch (IOException e) {
			logger.error("Exception", e);
		}
		return 0;
	}

	// 结果数据处理
	public void StatisticsResult(final File fileA, final File fileB, final double simility) {
		if (simility >= 0.999999) {// [0.9999999753632214,1]大约在此范围相同图片
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					pass++;
					textAreaLogs_Pass
							.append("路径A【" + fileA.getName() + "】对比路径B【" + fileB.getName() + "】: 相似度=1, Pass.\n");
					textAreaLogs_Pass.setCaretPosition(textAreaLogs_Pass.getText().length());
					logger.info("路径A【" + fileA.getName() + "】对比路径B【" + fileB.getName() + "】: 相似度=1, Pass.");
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fail++;
					textAreaLogs_Fail.append("路径A【" + fileA.getName() + "】对比路径B【" + fileB.getName() + "】: 相似度="
							+ simility + ", Fail.\n");
					textAreaLogs_Fail.setCaretPosition(textAreaLogs_Fail.getText().length());
					logger.info(
							"路径A【" + fileA.getName() + "】对比路径B【" + fileB.getName() + "】: 相似度=" + simility + ", Fail.");
				}
			});
		}
	}

	// 返回数据
	public int getPass() {
		return pass;
	}

	public int getFail() {
		return fail;
	}

	public int getTotal() {
		return total;
	}

	public synchronized Entry<File, File> getFiles() {
		while (files_Iterator.hasNext()) {
			return files_Iterator.next();
		}
		return null;
	}

	// 比较图片线程
	class PicContrastThread implements Runnable {
		File fileA;
		File fileB;

		public PicContrastThread() {

		}

		public void run() {
			Entry<File, File> entry = null;
			do {
				entry = getFiles();
				if (entry != null) {
					fileA = entry.getKey();
					fileB = entry.getValue();
					StatisticsResult(fileA, fileB, Contrast(fileA, fileB));
				}
			} while (entry != null);
			logger.info("PicContrastThread end");
		}
	}
}
