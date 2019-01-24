package com.report;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.HelperUtil;
import com.helper.TimeUtil;

public abstract class BaseCaseReport {
	Logger logger = LoggerFactory.getLogger(BaseCaseReport.class);

	StringBuffer failBuf = new StringBuffer();
	StringBuffer allBuf = new StringBuffer();
	int showid = 0;
	int warncount = 0;
	int stepcount = 0;
	int errorcount = 0;
	boolean crash_flag = false;
	boolean result_flag = false;
	boolean desc_flag = false;

	public BaseCaseReport() {
		// TODO Auto-generated constructor stub
		addHeader();
	}

	/**
	 * 添加用例表格标题
	 */
	public void addHeader() {
		allBuf.append("<h2>用例测试详情:</h2>");
		allBuf.append("<table border='1' class='allcaseTable'>");
		allBuf.append("<tr>");
		allBuf.append("<th style='width:5%;'>测试用例</th>");
		allBuf.append("<th style='width:3%;'>结果</th>");
		allBuf.append("<th style='width:7%;'>用例描述</th>");
		allBuf.append("<th style='width:15%;'>信息</th>");
		allBuf.append("<th style='width:40%;'>步骤</th>");
		allBuf.append("<th style='width:20%;'>捕获</th>");
		allBuf.append("<th style='width:5%;'>用时</th>");
		allBuf.append("<th style='width:5%;'>视频</th>");
		allBuf.append("</tr>");

		failBuf.append("<h2>失败用例测试详情:</h2>");
		failBuf.append("<table border='1' class='failcaseTable'>");
		failBuf.append("<tr>");
		failBuf.append("<th style='width:5%;'>测试用例</th>");
		failBuf.append("<th style='width:3%;'>结果</th>");
		failBuf.append("<th style='width:7%;'>用例描述</th>");
		failBuf.append("<th style='width:15%;'>信息</th>");
		failBuf.append("<th style='width:40%;'>步骤</th>");
		failBuf.append("<th style='width:20%;'>捕获</th>");
		failBuf.append("<th style='width:5%;'>用时</th>");
		failBuf.append("<th style='width:5%;'>视频</th>");
		failBuf.append("</tr>");
	}

	/**
	 * 得到showid
	 * 
	 * @return
	 */
	public int getShowid() {
		return showid;
	}

	/**
	 * 得到步骤计数
	 * 
	 * @return
	 */
	public int getStepcount() {
		return stepcount;
	}

	/**
	 * 得到警告计数
	 * 
	 * @return
	 */
	public int getWarncount() {
		return warncount;
	}

	/**
	 * 得到错误计数
	 * 
	 * @return
	 */
	public int getErrorcount() {
		return errorcount;
	}

	/**
	 * 添加用例行
	 * 
	 * @param casename
	 * @param result
	 * @param stepsstr
	 * @param logcatstr
	 * @param customerstr
	 */
	public void addLine(String casename, String desc, boolean result, String resultstr, String stepsstr,
			String logcatstr, String customerstr, String videopath, String usetime) {
		if (!result) {
			failBuf.append("<tr>");
			// 用例名称
			failBuf.append("<td style='background-color:red'>" + casename + "</td>");
			// 测试结果
			failBuf.append("<td>" + (result ? "通过" : "失败") + "</td>");
			// 用例描述
			failBuf.append("<td>" + desc + "</td>");
			// 自定义结果信息
			failBuf.append("<td>" + resultstr + "</td>");
			// 步骤
			failBuf.append("<td>");
			failBuf.append("<div>" + analysisSteps(stepsstr, false) + "</div>");
			if (!stepsstr.equals("")) {
				failBuf.append("<button id='" + showid + "' onclick=show(id)>点击展开</button>");
				failBuf.append("<div id='" + showid + "_text' style='display:none'>" + colorSteps(stepsstr) + "</div>");
				showid++;
			}
			failBuf.append("</td>");

			// 日记
			failBuf.append("<td>");
			for (String str : logcatstr.split("\\[EXCEPTION\\]")) {
				if (!str.equals("")) {
					int index = str.indexOf("[PC时间") > 0 ? str.indexOf("[PC时间") + ("[PC时间").length() : 0;
					if (str.contains(Cconfig.APPLOG)) {
						failBuf.append("<div>PC时间" + str.substring(index, index + TimeUtil.getTime4Log().length())
								+ "应用异常" + "</div>");
					} else if (str.contains(Cconfig.SYSLOG)) {
						failBuf.append("<div>PC时间" + str.substring(index, index + TimeUtil.getTime4Log().length())
								+ "系统异常" + "</div>");
					} else {
						failBuf.append("<div>PC时间" + str.substring(index, index + TimeUtil.getTime4Log().length())
								+ "自定义捕获" + "</div>");
					}
					failBuf.append("<button id='" + showid + "' onclick=show(id)>点击展开</button>");
					failBuf.append("<div id='" + showid + "_text' style='display:none'>" + str + "</div>");
					showid++;
				}
			}
			failBuf.append("</td>");
			// 用时
			failBuf.append("<td>" + usetime + "</td>");
			// 视频按钮
			failBuf.append("<td>");
			if (videopath == null) {
				failBuf.append("<div>无视频</div>");
			} else {
				failBuf.append("<button id='" + showid + "_video' value='" + "./Movie"
						+ videopath.substring(videopath.lastIndexOf("/"))
						+ "' onclick=playvideo(id,value)>播放/暂停</button>");
			}
			failBuf.append("</td>");
			showid++;

			failBuf.append("</tr>");
		}

		// 全部用例结果
		allBuf.append("<tr>");
		// 用例名称
		if (result) {
			allBuf.append("<td style='background-color:yellow'>" + casename + "</td>");
		} else {
			allBuf.append("<td style='background-color:red'>" + casename + "</td>");
		}
		// 用例结果
		allBuf.append("<td>" + (result ? "通过" : "失败") + "</td>");
		// 用例描述
		allBuf.append("<td>" + desc + "</td>");
		// 自定义结果信息
		allBuf.append("<td>" + resultstr + "</td>");
		// 步骤
		allBuf.append("<td>");
		allBuf.append("<div>" + analysisSteps(stepsstr, true) + "</div>");
		if (!stepsstr.equals("")) {
			allBuf.append("<button id='" + showid + "' onclick=show(id)>点击展开</button>");
			allBuf.append("<div id='" + showid + "_text' style='display:none'>" + colorSteps(stepsstr) + "</div>");
			showid++;
		}
		allBuf.append("</td>");

		// logcat日记
		allBuf.append("<td>");
		for (String str : logcatstr.split("\\[EXCEPTION\\]")) {
			if (!str.equals("")) {
				int index = str.indexOf("[PC时间") > 0 ? str.indexOf("[PC时间") + ("[PC时间").length() : 0;
				if (str.contains(Cconfig.APPLOG)) {
					allBuf.append("<div>PC时间" + str.substring(index, index + TimeUtil.getTime4Log().length()) + "应用异常"
							+ "</div>");
				} else if (str.contains(Cconfig.SYSLOG)) {
					allBuf.append("<div>PC时间" + str.substring(index, index + TimeUtil.getTime4Log().length()) + "系统异常"
							+ "</div>");
				} else {
					allBuf.append("<div>PC时间" + str.substring(index, index + TimeUtil.getTime4Log().length()) + "自定义捕获"
							+ "</div>");
				}

				allBuf.append("<button id='" + showid + "' onclick=show(id)>点击展开</button>");
				allBuf.append("<div id='" + showid + "_text' style='display:none'>" + str + "</div>");
				showid++;
			}
		}
		allBuf.append("</td>");
		// 用时
		allBuf.append("<td>" + usetime + "</td>");
		// 视频按钮
		allBuf.append("<td>");
		if (videopath == null) {
			allBuf.append("<div>无视频</div>");
		} else {
			allBuf.append("<button id='" + showid + "_video' value='" + "./Movie"
					+ videopath.substring(videopath.lastIndexOf("/"), videopath.length())
					+ "' onclick=playvideo(id,value)>播放/暂停</button>");
		}
		allBuf.append("</td>");
		showid++;

		allBuf.append("</tr>");

	}

	/**
	 * 写入全部测试用例结果
	 * 
	 * @param reportFile
	 */
	public String WriteAllBuf(File reportFile) {
		allBuf.append("</table>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), allBuf.toString(), true);
		return allBuf.toString();
	}

	/**
	 * 写入失败测试用例结果
	 * 
	 * @param reportFile
	 */
	public String WriteFailBuf(File reportFile) {
		failBuf.append("</table>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), failBuf.toString(), true);
		return failBuf.toString();
	}

	/**
	 * 分析步骤信息,得到统计信息
	 * 
	 * @param stepsstr
	 * @return
	 */
	private String analysisSteps(String stepsstr, boolean addresult) {
		StringBuffer result = new StringBuffer();
		int step = HelperUtil.getStringShowCount(stepsstr, "[STEP]");
		int warn = HelperUtil.getStringShowCount(stepsstr, "[WARN]");
		int error = HelperUtil.getStringShowCount(stepsstr, "[ERROR]");
		if (addresult) {
			stepcount += step;
			warncount += warn;
			errorcount += error;
		}
		result.append("共执行" + step + "步," + "发现警告" + warn + "处," + "发现错误" + error + "处");

		return result.toString();
	}

	/**
	 * 处理步骤信息,标记行号及颜色
	 * 
	 * @param stepsstr
	 * @return
	 */
	private String colorSteps(String stepsstr) {
		StringBuffer stringBuffer = new StringBuffer();
		int count = 0;
		for (String str : stepsstr.split("<br>")) {
			count++;
			if (str.contains("[ERROR]")) {
				stringBuffer.append("<font color='" + Cconfig.RED + "'>" + count + ". " + str + "</font><br>");
			} else if (str.contains("[CUSTOMER]")) {
				stringBuffer.append("<font color='" + Cconfig.BLUE + "'>" + count + ". " + str + "</font><br>");
			} else if (str.contains("[WARN]")) {
				stringBuffer.append("<font color='" + Cconfig.YELLOW + "'>" + count + ". " + str + "</font><br>");
			} else if (str.contains("[RESULT]")) {
				stringBuffer.append("<font color='" + Cconfig.BLUE + "'>" + count + ". " + str + "</font><br>");
			} else {
				stringBuffer.append(count + ". " + str + "<br>");
			}
		}
		return stringBuffer.toString();
	}

	protected String analysisLogcatlog() {

		return "";
	}

}
