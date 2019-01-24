package com.report;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.HelperUtil;
import com.viewer.main.MainRun;

public abstract class BaseTaskReport {
	static Logger logger = LoggerFactory.getLogger(BaseTaskReport.class);
	File reportFile;
	String title;
	StringBuffer emailBuf = new StringBuffer();
	String class_button = "button blue button_medium";

	public BaseTaskReport() {

	}

	/**
	 * 开始
	 * 
	 * @param reportFolder
	 * @param title
	 * @return
	 */
	public abstract File start(File reportFolder, String title);

	/**
	 * 结束
	 */
	public abstract void end();

	/**
	 * 创建一个html报告文件
	 * 
	 * @param reportFolder
	 * @param simplename
	 * @return
	 */
	protected File create(File reportFile, String title) {
		this.title = title;
		this.reportFile = reportFile;
		try {
			reportFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		return reportFile;
	}

	/**
	 * 添加场景报告头部
	 */
	public String WriteHeader() {
		StringBuffer Buf = new StringBuffer();
		Buf.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
		Buf.append("<title>" + title + "-测试报告" + "</title>");
		// css
		String title_background_color = "background-color:#00FF7F;";
		Buf.append("<style>");
		// Buf.append(".SceneResultTable th{"+title_background_color+"}");
		// Buf.append(".allcaseTable {word-break:break-all;word-wrap:break-all;}");
		// Buf.append(".allcaseTable th{"+title_background_color+"}");
		// Buf.append(".failcaseTable {word-break:break-all; word-wrap:break-all;}");
		// Buf.append(".failcaseTable th{"+title_background_color+"}");
		// Buf.append(".appiumTable th{"+title_background_color+"}");
		// Buf.append(".apkinfoTable th{"+title_background_color+"}");
		// Buf.append(".permissionTable th{"+title_background_color+"}");
		// Buf.append(".deviceTable th{"+title_background_color+"}");
		// Buf.append(".sceneTable th{"+title_background_color+"}");
		Buf.append("table{word-break:break-all;word-wrap:break-all;}");
		Buf.append("th{" + title_background_color + ";}");
		Buf.append(
				".showall {position: fixed;width:40px;height:40px;-webkit-background-size: 100% 100%;-moz-background-size: 100% 100%;background-size:100% 100%;"
						+ "right:5%;bottom:12%;opacity:0.6;}");
		Buf.append(getHeaderCSS());// 自定义CSS
		// Buf.append(".showApplog {color:black;font-weight:bold;}");
		// Buf.append(".showSyslog {color:black;font-weight:bold;}");
		// button样式
		Buf.append(".button {" + "	display: inline-block;" + "	outline: none;" + "	cursor: pointer;"
				+ "	text-align: center;" + "	text-decoration: none;"
				+ "	font: 16px/100% 'Microsoft yahei',Arial, Helvetica, sans-serif;" + "	padding: .5em 2em .55em;"
				+ "	text-shadow: 0 1px 1px rgba(0,0,0,.3);" + "	-webkit-border-radius: .5em;"
				+ "	-moz-border-radius: .5em;" + "	border-radius: .5em;"
				+ "	-webkit-box-shadow: 0 1px 2px rgba(0,0,0,.2);" + "	-moz-box-shadow: 0 1px 2px rgba(0,0,0,.2);"
				+ "	box-shadow: 0 1px 2px rgba(0,0,0,.2);" + "}");
		Buf.append(".button:hover {text-decoration: none;}");
		Buf.append(".button:active {position: relative;top: 1px;}");
		Buf.append(".button_medium {font-size: 11px;padding: .2em 1em .275em;}");
		Buf.append(".blue {" + "	color: #d9eef7;" + "	border: solid 1px #0076a3;" + "	background: #0095cd;"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#00adee), to(#0078a5));"
				+ "	background: -moz-linear-gradient(top,  #00adee,  #0078a5);"
				+ "	filter:  progid:DXImageTransform.Microsoft.gradient(startColorstr='#00adee', endColorstr='#0078a5');"
				+ "}");
		Buf.append(".blue:hover {" + "	background: #007ead;"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#0095cc), to(#00678e));"
				+ "	background: -moz-linear-gradient(top,  #0095cc,  #00678e);"
				+ "	filter:  progid:DXImageTransform.Microsoft.gradient(startColorstr='#0095cc', endColorstr='#00678e');"
				+ "}");
		Buf.append(".blue:active {" + "	color: #80bed6;"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#0078a5), to(#00adee));"
				+ "	background: -moz-linear-gradient(top,  #0078a5,  #00adee);"
				+ "	filter:  progid:DXImageTransform.Microsoft.gradient(startColorstr='#0078a5', endColorstr='#00adee');"
				+ "}");
		Buf.append("</style>");
		// javascript
		Buf.append(
				"<script type='text/javascript' src='http://ajax.aspnetcdn.com/ajax/jQuery/jquery-2.1.0.min.js'></script>");// JQuery
																															// cdn
		Buf.append("<script type='text/javascript'>");
		// 信息展开
		Buf.append(
				"function show(id){if($('#'+id).next('div').is(':hidden')){$('#'+id).next('div').show();$('#'+id).text('点击隐藏')}else{$('#'+id).next('div').hide();$('#'+id).text('点击展开')} }");
		// 隐藏全部
		Buf.append("var isshowall=true;");
		// Buf.append("function showall(showid){"
		// + "if(!isshowall){"
		// + "for(var
		// id=0;id<=showid;id++){$('#'+id+'_text').show();$('#'+id).text('点击隐藏');};$('.showall').text('隐藏全部');isshowall=true;"
		// + "}else{"
		// + "for(var
		// id=0;id<=showid;id++){$('#'+id+'_text').hide();$('#'+id).text('点击展开');};$('.showall').text('显示全部');isshowall=false;"
		// + "}}");
		Buf.append("function showall(){" + "if(!isshowall){"
				+ "$('.hideText').show();$(\"button[id$='_text']\").text('点击隐藏');$('.showall').text('隐藏全部');isshowall=true;"
				+ "}else{"
				+ "$('.hideText').hide();$(\"button[id$='_text']\").text('点击展开');$('.showall').text('显示全部');isshowall=false;"
				+ "}}");
		// 播放视频
		Buf.append("function playvideo(id,src,sumsrc){console.log('video path='+src);$('.videoPoplayer').show();"
				+ "if($('#videobox video').attr('src')!=src){$('#videobox video').prop('src',src);};"
				+ "if($('#videobox video')[0].paused){$('#videobox video')[0].play();$('#'+id).text('播放/暂停');}"
				+ "else{$('#videobox video')[0].pause();$('#'+id).text('播放/暂停');}; }");
		Buf.append("function playvideo(id,src,sumsrc){" + "	$('.videoPoplayer').show();"
				+ "	console.log(window.location.pathname,decodeURI(window.location.pathname));"
				+ "	if(decodeURI(window.location.pathname).indexOf('测试结果汇总')>-1){"
				+ "		if($('#videobox video').attr('src')!=sumsrc){"
				+ "			$('#videobox video').prop('src',sumsrc);" + "		};	"
				+ "	console.log('video sumpath='+sumsrc);" + "	}else{"
				+ "		if($('#videobox video').attr('src')!=src){" + "			$('#videobox video').prop('src',src);"
				+ "		};" + "	console.log('video path='+src);" + "	}" + "	if($('#videobox video')[0].paused){"
				+ "		$('#videobox video')[0].play();" + "	}else{" + "		$('#videobox video')[0].pause();"
				+ "	};" + "}");
		Buf.append("function closevideoPoplayer(){$('.videoPoplayer').hide();}"); // 关闭视频浮层
		// 显示日志
		// Buf.append("function showApplog(){$.ajax({url:
		// './Logcat/Applog.txt',dataType: 'text',success: function(data)
		// {$('.logcatTextarea').val(data)}});}");
		Buf.append("function showApplog(){$('.logPoplayer').show();}");
		Buf.append("function showSyslog(){$('.logPoplayer').show();}");
		Buf.append("function closelogPoplayer(){$('.logPoplayer').hide();}"); // 关闭logcat浮层
		// 拖动视频浮层
		Buf.append("$(document).ready(function (e) {");
		Buf.append(
				"$('.videoPoplayer').mousedown(function (e) { iDiffX = e.pageX - $(this).offset().left; iDiffY = e.pageY - $(this).offset().top;");
		Buf.append("$(document).mousemove(function (e) { $('.videoPoplayer').css({ "
				+ "'left': (e.pageX - iDiffX-$(document).scrollLeft()), "
				+ "'top': (e.pageY - iDiffY-$(document).scrollTop()) " + "}); }); });");
		Buf.append("$(document).mouseup(function () { $(document).unbind('mousemove'); }); ");
		// Buf.append("$('.videoPoplayer').draggable();"); JQ UI
		Buf.append("});");

		Buf.append("</script>");
		Buf.append("</head><body>");
		Buf.append("<h1 style='text-align:center'>" + title + "-测试报告" + "</h1>");
		Buf.append("<!--汇总测试结果信息-->");
		emailBuf.append(Buf.toString());
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		return Buf.toString();
	}

	/**
	 * 添加隐藏全部按钮
	 * 
	 * @param showid
	 * @return
	 */
	public String WriteShowallButton() {
		StringBuffer Buf = new StringBuffer();
		// Buf.append("<h2>辅助功能:</h2>");
		// Buf.append("<button id='showall'
		// onclick=showall('"+showid+"')>隐藏全部</button>");
		Buf.append("<div class='showall' onclick=showall()>隐藏全部</div>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		return Buf.toString();
	}

	/**
	 * 垂直列表
	 * 
	 * @param title
	 * @param cssname
	 * @param map
	 * @return
	 */
	protected String BuildVerticalTable(String title, String cssname, Map<String, String> map) {
		StringBuffer Buf = new StringBuffer();
		if (title != null)
			Buf.append("<h2>" + title + "</h2>");
		Buf.append("<table border='1' class='" + cssname + "'>");
		Buf.append("<tr>");
		Buf.append("<th>参数名</th>");
		Buf.append("<th>值</th>");
		Buf.append("</tr>");
		for (Entry<String, String> entry : map.entrySet()) {
			Buf.append("<tr>");
			Buf.append("<td>" + entry.getKey() + "</td>");
			Buf.append("<td>" + entry.getValue() + "</td>");
			Buf.append("</tr>");
		}
		Buf.append("</table>");
		return Buf.toString();
	}

	/**
	 * 添加垂直列表
	 * 
	 * @param title
	 *            标题
	 * @param cssname
	 *            css名称
	 * @param infoMap
	 *            信息
	 * @param appendemail
	 *            是否添加到邮件buffer
	 * @return
	 */
	public String WriteVerticalInfo(String title, String cssname, Map<String, String> infoMap, boolean appendemail) {
		String html = BuildVerticalTable(title, cssname, infoMap);
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), html, true);
		if (appendemail)
			emailBuf.append(html);
		return html;
	}

	/**
	 * 添加水平列表
	 * 
	 * @param title
	 *            标题
	 * @param cssname
	 *            css名称
	 * @param infoMap
	 *            信息
	 * @param appendemail
	 *            是否添加到邮件buffer
	 */
	public String WriteHorizontalInfo(String title, String cssname, Map<String, String> infoMap, boolean appendemail) {
		String html = BulidHorizontalTable(title, cssname, infoMap);
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), html, true);
		if (appendemail)
			emailBuf.append(html);
		return html;
	}

	/**
	 * 横向列表
	 * 
	 * @param title
	 * @param cssname
	 * @param map
	 * @return
	 */
	protected String BulidHorizontalTable(String title, String cssname, Map<String, String> map) {
		StringBuffer Buf = new StringBuffer();
		if (title != null)
			Buf.append("<h2>" + title + "</h2>");
		Buf.append("<table border='1' class='" + cssname + "'>");
		Buf.append("<tr>");
		for (Entry<String, String> entry : map.entrySet()) {
			Buf.append("<th>" + entry.getKey() + "</th>");
		}
		Buf.append("</tr>");
		Buf.append("<tr>");
		for (Entry<String, String> entry : map.entrySet()) {
			Buf.append("<td>" + entry.getValue() + "</td>");
		}
		Buf.append("</tr>");
		Buf.append("</table>");
		return Buf.toString();
	}

	/**
	 * 添加视频播放窗口
	 */
	public String WriteVideoWindow() {
		StringBuffer Buf = new StringBuffer();
		Buf.append(
				"<div class='videoPoplayer' style='z-index:9002;position:fixed;display:none;width:30%;height:520px;top:0;left:0;cursor:move;'>");
		Buf.append(
				"<div style='width:100%;height:100%;background-color: white;border-radius: 10px;margin: auto;border: 2px solid black;'>");
		Buf.append("<span>视频播放:</span>");
		Buf.append("<button style='position: absolute;right:15px;' class='" + class_button
				+ "' onclick=closevideoPoplayer()>关闭</button>");
		Buf.append("<div id='videobox'>");
		Buf.append("<video style='position: absolute;top: 25px;bottom:20px;width: 100%;height: 480px;' controls>");
		Buf.append("<source src='' type='video/mp4'>");
		Buf.append("您的浏览器不支持 HTML5 video 标签。");
		Buf.append("</video>");
		Buf.append("</div>");
		Buf.append("</div>");
		Buf.append("</div>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		return Buf.toString();
	}

	/**
	 * 添加返回测试结果汇总的超链接
	 * 
	 * @return
	 */
	protected String WriteLink2Mixreport() {
		StringBuffer Buf = new StringBuffer();
		Buf.append("<br>");
		Buf.append("<a href='../测试结果汇总-Report.html' style='font-size: 18pt;color: blue;'>返回结果汇总</a>");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		return Buf.toString();
	}

	/**
	 * 添加场景报告尾部
	 */
	public String WriteTail() {
		StringBuffer Buf = new StringBuffer();
		Buf.append("<br>");
		if (MainRun.sysConfigBean.getQAreporter_url().equals("")) {
			Buf.append("<div style='text-align:center'><span>视频及日志等信息需要额外资源显示,请联系QA!</span></div>");
		} else {// 有服务器则显示返回服务器列表
			Buf.append("<br>");
			Buf.append("<a href='" + MainRun.sysConfigBean.getQAreporter_url()
					+ "' style='font-size: 18pt;color: blue;'>进入测试报告展示系统</a>");
		}
		Buf.append("<div style='text-align:center'><span style='color:black;'>QAUiFramework测试框架 " + MainRun.Version
				+ "</span></div>");
		Buf.append("</body></html>");
		// 测试备注信息
		Buf.append("<!--备注信息{");
		Buf.append(TestNoteInfo());
		Buf.append("}-->");
		HelperUtil.file_write_line(reportFile.getAbsolutePath(), Buf.toString(), true);
		emailBuf.append(Buf.toString());
		return Buf.toString();
	}

	/**
	 * 在邮件开头增加一些内容.
	 */
	public void addEmailHeaderNote() {
		StringBuffer Buf = new StringBuffer();
		if (!MainRun.sysConfigBean.getQAreporter_url().equals("")) {
			Buf.append("<div>");
			Buf.append("<span>查看详细内容,请进入在线系统:</span>");
			String url = MainRun.sysConfigBean.getQAreporter_url() + reportFile.getParentFile().getAbsolutePath()
					.substring(reportFile.getAbsolutePath().indexOf("QAUiReport"));
			try {
				url += "/" + URLEncoder.encode(reportFile.getName(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}
			Buf.append("<a href='" + url + "' style='font-size: 18pt;color: blue;'>点击进入</a>");
			Buf.append("</div>");
		}
		emailBuf.insert(0, Buf.toString());
	}

	/**
	 * 返回邮件信息Buf
	 * 
	 * @return
	 */
	public StringBuffer getEmailBuf() {
		return emailBuf;
	}

	/**
	 * 得到报告文件
	 * 
	 * @return
	 */
	public File getReportFile() {
		return reportFile;
	}

	/**
	 * 报告结尾测试备注信息,用于QAreporter读取
	 * 
	 * @return
	 */
	abstract String TestNoteInfo();

	/**
	 * css信息
	 * 
	 * @return
	 */
	abstract String getHeaderCSS();

}
