package com.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.helper.HttpsUtil;
import com.viewer.main.MainRun;

public class WeChatUtil {
	Logger logger = LoggerFactory.getLogger(WeChatUtil.class);
	// https://work.weixin.qq.com/api/doc API文档
	String corpid = "";
	String corpsecret = "";
	String agentid = "";

	Timer access_token_timer;
	String TAG = "[微信企业号]";// 日志标记
	int access_token_wrongful = 40014;// 不合法
	int access_token_overdue = 42001;// 过期
	int access_token_lack = 41001;// 缺少
	int errcode_ok = 0;// 成功

	// 缓存数据
	boolean init = false;
	String access_token = "";
	JSONArray departmentinfo;
	JSONArray peopleinfo;

	/**
	 * 获取历史部门信息
	 * 
	 * @return
	 */
	public JSONArray getDepartmentInfo() {
		if (departmentinfo == null) {
			refreshDepartmentInfo();
		}
		return departmentinfo;
	}

	/**
	 * 获取历史人员信息
	 * 
	 * @return
	 */
	public JSONArray getPeopleInfo() {
		if (peopleinfo == null) {
			refreshPeopleInfo();
		}
		return peopleinfo;
	}

	/**
	 * 定时刷新access_token,2小时一次,失败重试三次
	 */
	public boolean timingAccess_token() {
		corpid = MainRun.sysConfigBean.getWechat().get(Cparams.corpid);
		corpsecret = MainRun.sysConfigBean.getWechat().get(Cparams.corpsecret);
		agentid = MainRun.sysConfigBean.getWechat().get(Cparams.agentid);
		if (corpid.equals("") || corpsecret.equals("") || agentid.equals("")) {
			logger.info("timingAccess_token does not set, corpid=" + corpid + ",corpsecret=" + corpsecret + ",agentid="
					+ agentid);
		} else {
			if (!init) {
				logger.info("timingAccess_token start work!...");
				refreshAccess_token();
				//
				access_token_timer = new Timer();
				access_token_timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshAccess_token();
						try2getAccess_token(10 * 1000, 3);
					}
				}, 1000 * 60 * 60 * 2, 1000 * 60 * 60 * 2);
				init = true;
			} else {
				logger.info("timingAccess_token has been run");
			}
		}
		return !access_token.equals("");
	}

	/**
	 * 摧毁
	 */
	public void destory() {
		if (access_token_timer != null) {
			access_token_timer.cancel();
			access_token_timer = null;
		}
		init = false;
	}

	/**
	 * 获取access_token失败后尝试
	 * 
	 * @param time 每隔多少时间,秒
	 * @param max  尝试多少次
	 * @return
	 */
	private boolean try2getAccess_token(int time, int max) {
		int failcount = 0;
		while (access_token.equals("") && failcount < max) {// 失败尝试
			try {
				Thread.sleep(1000 * time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}
			failcount++;
			refreshAccess_token();
		}
		return !access_token.equals("");
	}

	/**
	 * 获取access_token,2小时过期,不可频繁调用!
	 * 
	 * @return
	 */
	public String refreshAccess_token() {
		// https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT
		String access_token = "";
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("corpid", corpid);
		paramsMap.put("corpsecret", corpsecret);
		JSONObject jsonObject = (JSONObject) HttpsUtil
				.sendGetData("https://qyapi.weixin.qq.com", "/cgi-bin/gettoken", paramsMap, null)
				.get(HttpsUtil.JSONOBJECT);// 2小时过期,不能频繁调用
		try {
			if (jsonObject.has("errcode") && jsonObject.getInt("errcode") == errcode_ok) {// 请求成功
//				{
//				   "errcode":0，
//				   "errmsg":""，
//				   "access_token": "accesstoken000001",
//				   "expires_in": 7200
//				}
				access_token = jsonObject.getString("access_token");
			} else {
				access_token = "";
				logger.error(TAG + "get access_token failed");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		logger.info("access_token info :" + access_token);
		this.access_token = access_token;
		return access_token;
	}

	/**
	 * 获取部门信息
	 * 
	 * @return "department": [ { "id": 2, "name": "广州研发中心", "parentid": 1, "order":
	 *         10 }, { "id": 3 "name": "邮箱产品部", "parentid": 2, "order": 40 } ]
	 */
	public JSONArray refreshDepartmentInfo() {
		// https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID
		JSONArray jsonArray = null;
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("access_token", access_token);
		// paramsMap.put("id", 1);
		JSONObject jsonObject = (JSONObject) HttpsUtil
				.sendGetData("https://qyapi.weixin.qq.com", "/cgi-bin/department/list", paramsMap, null)
				.get(HttpsUtil.JSONOBJECT);
		try {
			if (jsonObject.has("errcode")) {// 请求成功
				if (jsonObject.getInt("errcode") == errcode_ok) {
					jsonArray = jsonObject.getJSONArray("department");
				} else if (jsonObject.getInt("errcode") == access_token_lack
						|| jsonObject.getInt("errcode") == access_token_overdue
						|| jsonObject.getInt("errcode") == access_token_wrongful) {
					refreshAccess_token();
				}
			} else {
				logger.error(TAG + "get department info failed!");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		logger.info("department info:" + (jsonArray == null ? "null" : jsonArray.toString()));
		departmentinfo = jsonArray;
		return jsonArray;
	}

	/**
	 * 获取成员信息
	 * 
	 * @return "userlist": [ { "userid": "zhangsan", "name": "李四", "department": [1,
	 *         2] } ]
	 */
	public JSONArray refreshPeopleInfo() {
		JSONArray jsonArray = null;
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("access_token", access_token);
		paramsMap.put("department_id", 1);
		paramsMap.put("fetch_child", 1);
		JSONObject jsonObject = (JSONObject) HttpsUtil
				.sendGetData("https://qyapi.weixin.qq.com", "/cgi-bin/user/simplelist", paramsMap, null)
				.get(HttpsUtil.JSONOBJECT);
		try {
			if (jsonObject.has("errcode")) {// 请求成功
				if (jsonObject.getInt("errcode") == errcode_ok) {
					jsonArray = jsonObject.getJSONArray("userlist");
				} else if (jsonObject.getInt("errcode") == access_token_lack
						|| jsonObject.getInt("errcode") == access_token_overdue
						|| jsonObject.getInt("errcode") == access_token_wrongful) {
					refreshAccess_token();
				}
			} else {
				logger.error(TAG + "get people info failed");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		logger.info("people info :" + (jsonArray == null ? "null" : jsonArray.toString()));
		peopleinfo = jsonArray;
		return jsonArray;
	}

	/**
	 * 发送消息
	 * 
	 * @param userid  成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向该企业应用的全部成员发送
	 * @param toparty 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 * @param totag   标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 * @param text
	 * @return { "errcode" : 0, "errmsg" : "ok", "invaliduser" : "userid1|userid2",
	 *         // 不区分大小写，返回的列表都统一转为小写 "invalidparty" : "partyid1|partyid2",
	 *         "invalidtag":"tagid1|tagid2" }
	 */
	public JSONObject sendText(String touser, String toparty, String totag, String text) {
//		参数	是否必须	说明
//		touser	否	成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向该企业应用的全部成员发送
//		toparty	否	部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
//		totag	否	标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
//		msgtype	是	消息类型，此时固定为：text
//		agentid	是	企业应用的id，整型。可在应用的设置页面查看
//		content	是	消息内容，最长不超过2048个字节
//		safe	否	表示是否是保密消息，0表示否，1表示是，默认0
		JSONObject jsonObject = null;
		try {
			JSONObject params = new JSONObject();
			JSONObject content = new JSONObject();
			params.put("text", content.put("content", text));
			params.put("msgtype", "text");
			params.put("agentid", agentid);
			params.put("safe", 0);
			if (touser != null) {
				params.put("touser", touser);
			}
			if (toparty != null) {
				params.put("toparty", toparty);
			}
			if (totag != null) {
				params.put("totag", totag);
			}
			jsonObject = (JSONObject) HttpsUtil.sendPostData("https://qyapi.weixin.qq.com",
					"/cgi-bin/message/send?access_token=" + access_token, params.toString(), null)
					.get(HttpsUtil.JSONOBJECT);
			if (jsonObject.has("errcode")) {// 请求成功
				if (jsonObject.getInt("errcode") == errcode_ok) {
					if (jsonObject.has("invaliduser")) {
						String invaliduser = jsonObject.getString("invaliduser");
						logger.info("send message invaliduser fail:" + invaliduser);
					}
					if (jsonObject.has("invalidparty")) {
						String invalidparty = jsonObject.getString("invalidparty");
						logger.info("send message invalidparty fail:" + invalidparty);
					}
					if (jsonObject.has("invalidtag")) {
						String invalidtag = jsonObject.getString("invalidtag");
						logger.info("send message invalidtag fail:" + invalidtag);
					}
				} else if (jsonObject.getInt("errcode") == access_token_lack
						|| jsonObject.getInt("errcode") == access_token_overdue
						|| jsonObject.getInt("errcode") == access_token_wrongful) {
					refreshAccess_token();
				}
			} else {
				logger.error(TAG + "send message to " + touser + "/" + toparty + "/" + totag + " failed");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		return jsonObject;
	}

	/**
	 * 发送卡片消息,格式只有在企业微信中显示.微信不支持颜色格式
	 * 
	 * @param userid      成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向该企业应用的全部成员发送
	 * @param toparty     部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 * @param totag       标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 * @param title       是 标题，不超过128个字节，超过会自动截断
	 * @param description 是 描述，不超过512个字节，超过会自动截断
	 * @param url         是 点击后跳转的链接。
	 * @param btntxt      否 按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。
	 * @return { "errcode" : 0, "errmsg" : "ok", "invaliduser" : "userid1|userid2",
	 *         // 不区分大小写，返回的列表都统一转为小写 "invalidparty" : "partyid1|partyid2",
	 *         "invalidtag":"tagid1|tagid2" }
	 */
	public JSONObject sendTextcard(String touser, String toparty, String totag, String title, String description,
			String url, String btntxt) {
		JSONObject jsonObject = null;
		try {
			JSONObject params = new JSONObject();
			JSONObject content = new JSONObject();
			content.put("title", title);
			content.put("description", description);
			content.put("url", url.equals("") ? "www.baidu.com" : url);// 为空则跳转百度
			content.put("btntxt", btntxt);

			params.put("textcard", content);
			params.put("msgtype", "textcard");
			params.put("agentid", agentid);
			if (touser != null) {
				params.put("touser", touser);
			}
			if (toparty != null) {
				params.put("toparty", toparty);
			}
			if (totag != null) {
				params.put("totag", totag);
			}
			jsonObject = (JSONObject) HttpsUtil.sendPostData("https://qyapi.weixin.qq.com",
					"/cgi-bin/message/send?access_token=" + access_token, params.toString(), null)
					.get(HttpsUtil.JSONOBJECT);
			if (jsonObject.has("errcode")) {// 请求成功
				if (jsonObject.getInt("errcode") == errcode_ok) {
					if (jsonObject.has("invaliduser")) {
						String invaliduser = jsonObject.getString("invaliduser");
						logger.info("send message invaliduser fail:" + invaliduser);
					}
					if (jsonObject.has("invalidparty")) {
						String invalidparty = jsonObject.getString("invalidparty");
						logger.info("send message invalidparty fail:" + invalidparty);
					}
					if (jsonObject.has("invalidtag")) {
						String invalidtag = jsonObject.getString("invalidtag");
						logger.info("send message invalidtag fail:" + invalidtag);
					}
				} else if (jsonObject.getInt("errcode") == access_token_lack
						|| jsonObject.getInt("errcode") == access_token_overdue
						|| jsonObject.getInt("errcode") == access_token_wrongful) {
					refreshAccess_token();
				}
			} else {
				logger.error(TAG + "send message to " + touser + "/" + toparty + "/" + totag + " failed");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		return jsonObject;
	}

	// 单例
	private static class SingletonHolder {
		private static final WeChatUtil INSTANCE = new WeChatUtil();
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static final WeChatUtil getInstance() {
		return SingletonHolder.INSTANCE;
	}

}
