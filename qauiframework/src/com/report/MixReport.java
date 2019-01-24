package com.report;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.HelperUtil;
import com.helper.TimeUtil;

public class MixReport extends BaseTaskReport {
	Logger logger = LoggerFactory.getLogger(MixReport.class);
	// scene
	boolean write_deviceinfo_flag = false;
	int warncount = 0;
	int stepcount = 0;
	int errorcount = 0;
	int PassCaseCount = 0;
	int FailCaseCount = 0;
	// monkey
	int crashcount = 0;
	// total
	long starttime;
	long endtime;
	boolean hasScene = false;
	boolean hasAndroidSYSMonkey = false;

	MixNote mixNote;
	File mixNoteFile;

	public MixReport() {

	}

	/**
	 * 插入自定义窗口
	 * 
	 * @return
	 */
	public String WriteDiyBox() {
		StringBuffer Buf = new StringBuffer();
		Buf.append("<div class='diyBox'>");
		Buf.append("<!--自定义信息1-->");//
		Buf.append("<!--自定义信息2-->");
		Buf.append("<!--自定义信息3-->");
		Buf.append("<!--自定义信息4-->");
		Buf.append("<!--自定义信息5-->");
		Buf.append("</div>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		return Buf.toString();
	}

	/**
	 * 添加设备信息,仅写入一次
	 */
	public String WriteDeviceInfo(Map<String, String> deviceinfoMap) {
		if (write_deviceinfo_flag)
			return "";
		write_deviceinfo_flag = true;
		return WriteHorizontalInfo("设备信息", "deviceTable", deviceinfoMap, true);
	}

	/**
	 * 添加测试用例汇总
	 */
	public String WriteSceneResult(String taskcount, File reportFolder, String taskname, int passcount, int failcount,
			long starttime, long endtime, int stepcount, int warncount, int errorcount) {
		hasScene = true;
		StringBuffer Buf = new StringBuffer();
		System.out.println(reportFolder.getName());
		String href = "./" + reportFolder.getName() + "/测试结果-Report.html";
		Buf.append("<h2>(" + taskcount + ") " + taskname + " <a href='" + href + "' style='font-size: 18pt;'>详情</a>"
				+ "</h2>");
		// Buf.append("<a href='"+href+"' style='font-size: 18pt;'>详情</a>");
		Buf.append("<table border='1' class='SceneResultTable'>");
		Buf.append("<tr>");
		Buf.append("<th>用例数量</th>");
		Buf.append("<th>通过</th>");
		Buf.append("<th>失败</th>");
		Buf.append("<th>步骤</th>");
		Buf.append("<th>警告</th>");
		Buf.append("<th>错误</th>");
		Buf.append("<th>开始时间</th>");
		Buf.append("<th>结束时间</th>");
		Buf.append("<th>用时</th>");
		Buf.append("</tr>");
		Buf.append("<tr>");
		Buf.append("<td>" + (passcount + failcount) + "</td>");
		Buf.append("<td>" + passcount + "</td>");
		Buf.append("<td>" + failcount + "</td>");
		Buf.append("<td>" + stepcount + "</td>");
		Buf.append("<td>" + warncount + "</td>");
		Buf.append("<td>" + errorcount + "</td>");
		Buf.append("<td>" + TimeUtil.getTime(starttime) + "</td>");
		Buf.append("<td>" + TimeUtil.getTime(endtime) + "</td>");
		Buf.append("<td>" + TimeUtil.getUseTime(starttime, endtime) + "</td>");
		Buf.append("</tr>");
		Buf.append("</table>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		emailBuf.append(Buf.toString());
		// 处理数据
		this.PassCaseCount += passcount;
		this.FailCaseCount += failcount;
		this.stepcount += stepcount;
		this.warncount += warncount;
		this.errorcount += errorcount;
		// Note
		mixNote.addOutlineItems(mixNote.ITEMS_SCENE, failcount > 0 ? false : true, reportFolder.getName(),
				"总用例=" + (passcount + failcount) + ",通过=" + passcount + ",失败=" + failcount);

		return Buf.toString();
	}

	/**
	 * 向NOTE写入失败用例信息
	 * 
	 * @param configMap
	 * @param capabilityMap
	 * @param failcaseList
	 */
	public void WriteSceneFailNote(Map<String, String> configMap, Map<String, String> capabilityMap,
			List<String> failcaseList) {
		mixNote.addFailcaseItems(configMap, capabilityMap, failcaseList);
	}

	/**
	 * 添加结果汇总
	 */
	public String WriteMonkeyAndroidSYSResult(String taskcount, File reportFolder, String taskname, int crashcount,
			long starttime, long endtime) {
		hasAndroidSYSMonkey = true;
		StringBuffer Buf = new StringBuffer();
		System.out.println(reportFolder.getName());
		String href = "./" + reportFolder.getName() + "/测试结果-Report.html";
		Buf.append("<h2>(" + taskcount + ") " + taskname + " <a href='" + href + "' style='font-size: 18pt;'>详情</a>"
				+ "</h2>");
		Buf.append("<table border='1' class='MonkeyResultTable'>");
		Buf.append("<tr>");
		Buf.append("<th>" + Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "出现次数</th>");
		Buf.append("<th>开始时间</th>");
		Buf.append("<th>结束时间</th>");
		Buf.append("<th>用时</th>");
		Buf.append("</tr>");
		Buf.append("<tr>");
		Buf.append("<td>" + crashcount + "</td>");
		Buf.append("<td>" + TimeUtil.getTime(starttime) + "</td>");
		Buf.append("<td>" + TimeUtil.getTime(endtime) + "</td>");
		Buf.append("<td>" + TimeUtil.getUseTime(starttime, endtime) + "</td>");
		Buf.append("</tr>");
		Buf.append("</table>");
		this.crashcount = crashcount;
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		emailBuf.append(Buf.toString());
		// Note
		mixNote.addOutlineItems(mixNote.ITEMS_MONKEY_ANDROID_SYS, crashcount > 0 ? false : true, reportFolder.getName(),
				Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "出现" + crashcount + "次");
		return Buf.toString();
	}

	/**
	 * 添加所有场景用例结果汇总
	 */
	public String WriteTaskTotalResult() {
		endtime = TimeUtil.getTime();
		Map<String, String> taskresultMap = new LinkedHashMap<>();
		if (hasScene) {
			taskresultMap.put("场景汇总", "用例数量=" + (PassCaseCount + FailCaseCount) + ",通过=" + PassCaseCount + ",失败="
					+ FailCaseCount + ",步骤=" + stepcount + ",警告=" + warncount + ",错误=" + errorcount);
		}
		if (hasAndroidSYSMonkey) {
			taskresultMap.put(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS + "汇总",
					Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "出现" + crashcount + "次");
		}
		taskresultMap.put("开始时间", TimeUtil.getTime(starttime));
		taskresultMap.put("结束时间", TimeUtil.getTime(endtime));
		taskresultMap.put("总耗时", TimeUtil.getUseTime(starttime, endtime));
		String taskresult = BuildVerticalTable("所有任务测试结果汇总", "totalResultTable", taskresultMap);
		String taskresultflag = "<!--汇总测试结果信息-->";
		HelperUtil.file_replace_content(reportFile.getAbsolutePath(), taskresultflag, taskresult);// header处替换
		if (emailBuf.toString().indexOf(taskresultflag) > -1) {
			emailBuf.replace(emailBuf.indexOf(taskresultflag),
					emailBuf.indexOf(taskresultflag) + taskresultflag.length(), taskresult);
			// emailBuf.toString().replace("<!--汇总测试结果信息-->", taskresult);
		} else {
			logger.info("can't find " + taskresultflag + " in emailBuf");
		}
		// Note
		StringBuffer Buf = new StringBuffer();
		if (hasScene)
			Buf.append("场景: 总用例=" + (PassCaseCount + FailCaseCount) + ",通过=" + PassCaseCount + ",失败=" + FailCaseCount
					+ ";");
		if (hasAndroidSYSMonkey)
			Buf.append(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS + ": " + Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "出现"
					+ crashcount + "次;");
		mixNote.changeStringByXPath("/root/outline/summary", 0, Buf.toString());
		return taskresult;
	}

	/**
	 * 生成结果总结
	 * 
	 * @return
	 */
	public String getUsetime() {
		return TimeUtil.getUseTime(starttime, endtime);
	}

	/**
	 * 统计结果
	 * 
	 * @return
	 */
	public String getResult() {
		StringBuffer Buf = new StringBuffer();
		if (hasScene) {
			Buf.append("场景汇总:" + "用例数量=" + (PassCaseCount + FailCaseCount) + ",通过=" + PassCaseCount + ",失败="
					+ FailCaseCount + ",步骤=" + stepcount + ",警告=" + warncount + ",错误=" + errorcount + ";");
		}
		if (hasAndroidSYSMonkey) {
			Buf.append(Cconfig.TASK_TYPE_MONKEY_ANDROID_SYS + "汇总:" + Cconfig.MONKEY_ANDROID_SYS_CRASH_FLAG + "出现"
					+ crashcount + "次;");
		}
		return Buf.toString();
	}

	/**
	 * 修改标题
	 * 
	 * @param title
	 */
	public void changeTile(String title) {
		mixNote.changeStringByXPath("/root/outline/title", 0, title);
	}

	/**
	 * 写入失败测试用例结果
	 * 
	 * @param reportFile
	 */
	public String WriteInfoBuf(StringBuffer infoBuf) {
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), infoBuf.toString(), true);
		emailBuf.append(infoBuf.toString());
		return infoBuf.toString();
	}

	@Override
	public File start(File reportFolder, String title) {
		// TODO Auto-generated method stub
		mixNote = new MixNote();
		mixNoteFile = mixNote.create(reportFolder);
		mixNote.initXML(title);
		File file = create(new File(reportFolder.getAbsolutePath() + "/测试结果汇总-Report.html"), title);
		starttime = TimeUtil.getTime();
		WriteHeader();
		WriteVideoWindow();
		WriteDiyBox();
		return file;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		WriteShowallButton();
		WriteTaskTotalResult();
		WriteTail();
		addEmailHeaderNote();
		mixNote.changeStringByXPath("/root/outline/done", 0, "true");
	}

	@Override
	String TestNoteInfo() {
		// TODO Auto-generated method stub
		StringBuffer Buf = new StringBuffer();
		return Buf.toString();
	}

	@Override
	String getHeaderCSS() {
		// TODO Auto-generated method stub
		return "";
	}
}
