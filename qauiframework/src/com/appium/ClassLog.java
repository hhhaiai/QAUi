package com.appium;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.log.CusLogCapture;
import com.log.SceneLogUtil;
import com.notification.WeChatUtil;

/**
 * 日志类
 *
 */
public class ClassLog {
	Logger logger = LoggerFactory.getLogger(ClassLog.class);

	SceneLogUtil oplog;
	CusLogCapture cusLogCapture;

	public ClassLog(SceneLogUtil oplog, CusLogCapture cusLogCapture) {
		// TODO Auto-generated constructor stub
		this.oplog = oplog;
		this.cusLogCapture = cusLogCapture;
	}

	/**
	 * 自定义log信息,标志:[CUSTOMER] 日志将会显示在测试报告的测试详情的步骤中
	 * 
	 * @param msm
	 */
	public void Customer(String msm) {
		oplog.logCustomer(msm);
	}

	/**
	 * 自定义测试报告结果信息,标志:[Result] 日志将会显示在测试报告的测试详情信息中
	 * 
	 * @param msm
	 */
	public void Result(String msm) {
		oplog.logResult(msm);
	}

	/**
	 * 通过正则捕获日志并输出到文件
	 * 
	 * @param regex
	 * @param filename
	 */
	public void captureLogByRegex(String regex, String filename) {
		cusLogCapture.captureByRegex(regex, filename);
	}

	/**
	 * 通过包含key捕获日志并输出到文件
	 * 
	 * @param key
	 * @param filename
	 */
	public void captureLogByContain(String key, String filename) {
		cusLogCapture.captureByContain(key, filename);
	}

	/**
	 * 开始捕获日志
	 */
	public void captureLogStart() {
		cusLogCapture.captureStart();
	}

	/**
	 * 停止捕获日志
	 */
	public void captureLogStop() {
		cusLogCapture.captureStop();
	}

	/**
	 * 发送微信消息
	 * 
	 * @param userid  userid1|userid2
	 * @param content 消息内容
	 * @return { "errcode" : 0, "errmsg" : "ok", "invaliduser" : "userid1|userid2",
	 *         // 不区分大小写，返回的列表都统一转为小写 "invalidparty" : "partyid1|partyid2",
	 *         "invalidtag":"tagid1|tagid2" }
	 */
	@Deprecated
	public JSONObject sendWechatMessage(String userid, String content) {
		return WeChatUtil.getInstance().sendText(userid, null, null, content);
	}
}
