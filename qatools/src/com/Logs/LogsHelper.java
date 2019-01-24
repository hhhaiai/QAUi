package com.Logs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.Excute;

public class LogsHelper {
	Logger logger = LoggerFactory.getLogger(LogsHelper.class);

	/**
	 * 清除应用数据
	 */
	public String clearApp(String udid) {
		String str = JOptionPane.showInputDialog(null, "请输入应用包名(输入#则为当前界面应用):", "输入", JOptionPane.INFORMATION_MESSAGE);
		if (str != null) {
			if (str.equals("#")) {
				String surface = Excute.returnlist2(udid, "dumpsys window w |grep -i 'name=.*\\/.*'").get(0);
				Matcher m = Pattern.compile(".*name=(.*)/.*").matcher(surface);
				if (m.find())
					str = m.group(1);
				Excute.execcmd2(udid, "pm clear " + str);
			} else if (str.equals("")) {
				// Excute.execcmd2(udid, "pm clear " + str);
			} else {
				Excute.execcmd2(udid, "pm clear " + str);
			}
			logger.info("clear app data: " + str);
		}
		return str;
	}

}
