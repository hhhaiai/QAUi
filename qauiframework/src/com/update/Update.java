package com.update;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Update {
	Logger logger = LoggerFactory.getLogger(Update.class);

	/**
	 * 简易客户端更新设计 JSON 方式:POST 接口名:get_updateinfo 请求参数: {
	 * "CurrentVersion":"V1.0717.1", //当前版本号 "system":"windows", //操作系统
	 * windows/mac/linux "ip":"xx.xx.xx.xx", //本机IP "PCname":"xxxx", //PC名称
	 * "update":"1", //更新方式 1=普通更新,升级到指定版本;2=直接下载最新版本V0x9999.zip到桌面 } 返回结果:
	 * 以分号为间隔,一个分号一组信息
	 * 如果本地无指定文件夹,则需先新建文件夹,如需要复制文件A.txt到/FloderA/FloderB下,本地没有/FloderA/FloderB,则需先创建
	 * 获取更新信息后,应该先执行删除操作,然后执行复制操作,最后执行替换操作 不需要更新: { "code":"200",
	 * //返回码100=请求信息错误,200=不需要更新,201=升级到目标版本,202=下载最新版本到桌面 "message":"不需要更新" }
	 * 升级到目标版本: { "code":"201", "message":"升级到目标版本", "domain":"", //下载域名
	 * "ip":"xx.xx.xx.xx", //下载局域网IP,自动判断是否能ping通该IP,如果能则从该IP下载,优先走局域网
	 * "TargetVersion":"V0x0810", //需要升级到的版本号 "LatestVersion":"V0x9999", //最新版本号
	 * "copy":"/D.txt;/FloderA/E.txt;", //复制文件,如:/FloderA/E.txt;
	 * 意思是从/V0x0810/FloderA/E.txt下载文件E.txt到本地目录/FloderA/
	 * "copyfloder":"/FloderA/FloderD/files.zip",//复制文件夹,如/FloderA/FloderD/files.zip;意思是下载files.zip,然后解压到本地目录/FloderA/FloderD下
	 * "del":"/FloderA/A.txt;", //删除文件,同上 "delfloder":"/FloderC", //删除文件夹,同上
	 * "replace":"/FloderB/B.txt;/B.txt;",
	 * //替换文件,如/FloderB/B.txt;意思是下载B.txt并重名名为B.txt.temp到本地/FloderB下,然后删除本地/FloderB/B.txt文件,最后重命名B.txt.temp为B.txt
	 * } 下载最新版本到桌面: { "code":"202", "message":"下载最新版本",
	 * "LatestVersionURL":"xxxxxxxx.zip" //最新版本下载地址 } 请求信息错误: { "code":"100",
	 * "message":"请求信息错误" }
	 */
	public void test() {
		try {
			JSONObject jsonObject = new JSONObject("");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
