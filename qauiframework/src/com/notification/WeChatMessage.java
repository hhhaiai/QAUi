package com.notification;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.HelperUtil;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;
import com.viewer.main.MainRun;

public class WeChatMessage {
	Logger logger = LoggerFactory.getLogger(WeChatMessage.class);
	Map<String, String> peopleMap = new HashMap<>();;
	SceneLogUtil oplog;
	boolean sendsceneresult = false;// 是否发送单个场景的消息

	public WeChatMessage(SceneLogUtil oplog, String people_list) {
		// TODO Auto-generated constructor stub
		this.oplog = oplog;
		setPeopleMap(people_list);
	}

	/**
	 * 设置发送人员列表
	 * 
	 * @param people_list
	 */
	public void setPeopleMap(String people_list) {
		peopleMap.clear();
		for (String people : people_list.split(";")) {
			if (people.contains("=")) {
				peopleMap.put(people.split("=")[1], people.split("=")[0]);// userid,name
			}
		}
	}

	/**
	 * monkey结果消息
	 * 
	 * @param model
	 * @param type
	 * @param result
	 * @param usetime
	 * @param reportFile
	 */
	public void msmMonkeyResult(String model, String type, String result, String usetime, File reportFile) {
		// [机型][场景结果][功能遍历]总用例6条,成功5条,失败1条,用时13分钟23秒,点击查看报告.
		if (!sendsceneresult) {
			return;
		}
		StringBuffer msmBuf = new StringBuffer();
		msmBuf.append("[" + model + "]");
		msmBuf.append("[Monkey结果]");
		msmBuf.append("[" + type + "]");
		msmBuf.append(result);
		msmBuf.append(",用时" + usetime + ".");
		if (reportFile.exists() && reportFile.isFile() && !MainRun.sysConfigBean.getQAreporter_url().equals("")) {
			String url = MainRun.sysConfigBean.getQAreporter_url()
					+ reportFile.getAbsolutePath().substring(reportFile.getAbsolutePath().indexOf("QAUiReport"));
			logger.info("msmTotalCase report url=" + url);
			msmBuf.append("点击查看<a href=\"" + url + "\">测试报告</a>.");
		}
		sendText(msmBuf.toString());
	}

	/**
	 * 汇总结果消息
	 * 
	 * @param model
	 * @param subject
	 * @param sceneinfo
	 * @param usetime
	 * @param reportFile
	 */
	public void msmMixSceneResult(String model, String subject, String sceneinfo, String usetime, File reportFile) {
		// [机型][汇总结果]场景数量5个,总用例6条,成功5条,失败1条,用时13分钟23秒,点击查看报告.
//		StringBuffer msmBuf = new StringBuffer();
//		msmBuf.append("[" + model + "]");
//		msmBuf.append("[" + subject + "]");
//		msmBuf.append(sceneinfo);
//		msmBuf.append("用时" + usetime + ".");
//		if (reportFile.exists() && reportFile.isFile() && !MainRun.sysConfigBean.getQAreporter_url().equals("")) {
//			String url = MainRun.sysConfigBean.getQAreporter_url()
//					+ reportFile.getAbsolutePath().substring(reportFile.getAbsolutePath().indexOf("QAUiReport"));
//			logger.info("msmMixScene report url=" + url);
//			msmBuf.append("点击查看<a href=\"" + url + "\">汇总测试报告</a>.");
//		}
//		sendText(msmBuf.toString());
		String title = "[" + model + "] " + subject;
		String description = "<div class=\"gray\">" + TimeUtil.getTime("MM-dd HH:mm")
				+ "</div><br><div class=\"normal\">" + sceneinfo + "用时" + usetime + "</div>"
				+ (sceneinfo.matches(".*错误=0;.*") ? "" : "<br><br><div class=\"highlight\">!!!发现错误,请查看测试报告!!!</div>");
		String url = "";
		String btntxt = "点击查看";
		if (reportFile.exists() && reportFile.isFile() && !MainRun.sysConfigBean.getQAreporter_url().equals("")) {
			url = MainRun.sysConfigBean.getQAreporter_url()
					+ reportFile.getAbsolutePath().substring(reportFile.getAbsolutePath().indexOf("QAUiReport"));
			logger.info("msmMixScene report url=" + url);
		} else {
			btntxt = "未配置";
			description += "<br><div class=\"normal\">未配置测试报告服务器</div>";
		}
		sendTextcard(title, description, url, btntxt);
	}

	/**
	 * 场景结果消息
	 * 
	 * @param model
	 * @param scenename
	 * @param result
	 * @param usetime
	 * @param reportFile
	 */
	public void msmSceneResult(String model, String scenename, String result, String usetime, File reportFile) {
		// [机型][场景结果][功能遍历]总用例6条,成功5条,失败1条,用时13分钟23秒,点击查看报告.
		if (!sendsceneresult) {
			return;
		}
		StringBuffer msmBuf = new StringBuffer();
		msmBuf.append("[" + model + "]");
		msmBuf.append("[场景结果]");
		msmBuf.append("[" + scenename + "]");
		msmBuf.append(result);
		msmBuf.append(",用时" + usetime + ".");
		if (reportFile.exists() && reportFile.isFile() && !MainRun.sysConfigBean.getQAreporter_url().equals("")) {
			String url = MainRun.sysConfigBean.getQAreporter_url()
					+ reportFile.getAbsolutePath().substring(reportFile.getAbsolutePath().indexOf("QAUiReport"));
			logger.info("msmTotalCase report url=" + url);
			msmBuf.append("点击查看<a href=\"" + url + "\">测试报告</a>.");
		}
		sendText(msmBuf.toString());
	}

	/**
	 * 失败用例消息
	 * 
	 * @param model
	 * @param scenename
	 * @param casename
	 * @param stepsstr
	 * @param usetime
	 * @param videopath
	 */
	public void msmFailCase(String model, String scenename, String casename, String stepsstr, String usetime,
			String videopath) {
		// [机型][失败用例][功能遍历][相机]共执行95步,发现警告0处,发现错误1处,用时13分钟23秒,点击查看视频.
		StringBuffer msmBuf = new StringBuffer();
		msmBuf.append("[" + model + "]");
		msmBuf.append("[失败用例]");
		msmBuf.append("[" + scenename + "]");
		msmBuf.append("[" + casename + "]");
		int step = HelperUtil.getStringShowCount(stepsstr, "[STEP]");
		int warn = HelperUtil.getStringShowCount(stepsstr, "[WARN]");
		int error = HelperUtil.getStringShowCount(stepsstr, "[ERROR]");
		msmBuf.append("共执行" + step + "步," + "发现警告" + warn + "处," + "发现错误" + error + "处,");
		msmBuf.append("用时" + usetime + ".");
		File file = new File(videopath);
		if (file.exists() && file.isFile() && !MainRun.sysConfigBean.getQAreporter_url().equals("")) {
			String url = MainRun.sysConfigBean.getQAreporter_url()
					+ file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("QAUiReport"));
			logger.info("msmFailCase video url=" + url);
			msmBuf.append("点击查看<a href=\"" + url + "\">视频</a>.");
		}
		sendText(msmBuf.toString());
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 * @return 发送失败人员
	 */
	private void sendText(String content) {
		StringBuffer touserBuf = new StringBuffer();
		for (Entry<String, String> entry : peopleMap.entrySet()) {
			touserBuf.append(entry.getKey() + "|");
		}
		JSONObject jsonObject = WeChatUtil.getInstance().sendText(touserBuf.toString(), null, null, content);
		try {
			if (jsonObject.has("errcode")) {// 请求成功
				if (jsonObject.getInt("errcode") == 0) {
					String invaliduser = jsonObject.getString("invaliduser");
					if (!invaliduser.equals("")) {
						oplog.logWarn("微信通知消息text部分发送失败:" + invaliduser);
					} else {
						oplog.logTask("微信通知消息text发送成功.");
					}
					return;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		oplog.logError("微信通知消息text发送失败!");
	}

	private void sendTextcard(String title, String description, String url, String btntxt) {
		StringBuffer touserBuf = new StringBuffer();
		for (Entry<String, String> entry : peopleMap.entrySet()) {
			touserBuf.append(entry.getKey() + "|");
		}
		JSONObject jsonObject = WeChatUtil.getInstance().sendTextcard(touserBuf.toString(), null, null, title,
				description, url, btntxt);
		try {
			if (jsonObject.has("errcode")) {// 请求成功
				if (jsonObject.getInt("errcode") == 0) {
					String invaliduser = jsonObject.getString("invaliduser");
					if (!invaliduser.equals("")) {
						oplog.logWarn("微信通知消息textcard部分发送失败:" + invaliduser);
					} else {
						oplog.logTask("微信通知消息textcard发送成功.");
					}
					return;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		oplog.logError("微信通知消息textcard发送失败!");
	}
}
