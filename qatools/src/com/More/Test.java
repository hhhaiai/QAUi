package com.More;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.HelperUtil;

public class Test {
	static Logger logger = LoggerFactory.getLogger(Test.class);

	public static void run() {
		fpstest("2018-05-31 16:12:09.844", "2018-05-31 16:13:48.778", "/Users/auto/Desktop/FPS/小米6.txt");
	}

	public static void fpstest(String start, String end, String path) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		File file = new File(path);
		BufferedReader reader = null;
		try {
			Date startdate = df.parse(start);
			Date enddate = df.parse(end);
			Date tempdate;
			float minfps = 9999;
			float maxfps = 0;
			float tempfps;
			float totalfps = 0;
			int totaltime = 0;
			int temptime;
			int maxtime = 0;
			int mintime = 9999;
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int count = 0;
			// 一次读入一行，直到读入null为文件结束
			while ((line = reader.readLine()) != null) {
				if (line.contains("I GPUNormalLiveEffectFrame:")) {
					// 05-29 15:52:02.136 31450 1302 I GPUNormalLiveEffectFrame:
					// [FrameTest.logFps():37]fps:23.1,aveTime:43ms
					Pattern pattern = Pattern
							.compile("(\\d+-\\d+\\s\\d+:\\d+:\\d+.\\d+).*?fps:(\\d+.\\d+),aveTime:(\\d+)ms");
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						tempdate = df.parse("2018-" + matcher.group(1));
						tempfps = Float.parseFloat(matcher.group(2));
						temptime = Integer.parseInt(matcher.group(3));
						if (tempdate.getTime() > startdate.getTime() && tempdate.getTime() < enddate.getTime()
								&& tempfps > 1) {
							count++;
							if (tempfps < minfps) {
								minfps = tempfps;
							}
							if (tempfps > maxfps) {
								maxfps = tempfps;
							}
							if (temptime < mintime) {
								mintime = temptime;
							}
							if (temptime > maxtime) {
								maxtime = temptime;
							}
							totalfps += tempfps;
							totaltime += temptime;
							HelperUtil.file_write_line(file.getParentFile() + "/data.txt",
									df.format(tempdate) + "," + tempfps + "," + temptime + "\n", true);
						}
					} else {
						logger.error("异常:" + line);
					}
				}
			}
			logger.info(minfps + "-" + maxfps + ",avg=" + totalfps / count + ",line=" + count);
			logger.info(mintime + "-" + maxtime + ",avg=" + totaltime / count + ",line=" + count);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Exception", e);
				}
			}
		}
	}

	public static void test(String start, String end, String path) {
		// StringBuffer
		// resultbuf=HelperUtil.readAllfromfile("C:/Users/Administrator/Desktop/J7.txt");
		// Line 218812: 09-25 16:15:42.821 22490 22490 I VideoCameraFragmentPeanut:
		// [run():300]fps:18
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		File file = new File(path);
		BufferedReader reader = null;
		try {
			Date startdate = df.parse(start);
			Date enddate = df.parse(end);
			Date tempdate;
			int minfps = 9999;
			int maxfps = 0;
			int tempfps;
			String[] strings;
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int count = 0;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 09-25 16:03:32.271 22490 22490 I VideoCameraFragmentPeanut: [run():300]fps:30
				if (tempString.contains("I VideoCameraFragmentPeanut:")) {
					tempdate = df.parse("2017-" + tempString.substring(0, 18));
					if (tempdate.getTime() > startdate.getTime() && tempdate.getTime() < enddate.getTime()) {
						strings = tempString.split("\\:");
						tempfps = Integer.parseInt(strings[strings.length - 1]);
						if (tempfps < minfps) {
							minfps = tempfps;
						}
						if (tempfps > maxfps) {
							maxfps = tempfps;
						}
						count++;
					}
				}
				// if(tempString.contains("D PerformaceStatic:")){
				// tempdate=df.parse("2017-"+tempString.substring(0, 18));
				// if(tempdate.getTime()>startdate.getTime()&&tempdate.getTime()<enddate.getTime()){
				// strings=tempString.split("\\:|,");
				// tempfps=Integer.parseInt(strings[5]);
				// if(tempfps<minfps){
				// minfps=tempfps;
				// }
				// if(tempfps>maxfps){
				// maxfps=tempfps;
				// }
				// count++;
				// }
				// }
			}
			logger.info(minfps + "-" + minfps + ",max=" + maxfps + ",min=" + minfps + ",count=" + count);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Exception", e);
				}
			}
		}
	}
}
