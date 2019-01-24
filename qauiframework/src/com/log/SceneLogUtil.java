package com.log;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.HelperUtil;
import com.helper.TimeUtil;

public class SceneLogUtil {
	Logger logger=LoggerFactory.getLogger(SceneLogUtil.class);
	StringBuffer stepsBuf=new StringBuffer();
	StringBuffer customerBuf=new StringBuffer();
	StringBuffer logBuf=new StringBuffer();
	StringBuffer resultBuf=new StringBuffer();//自定义结果信息
	String udid;
	JTextArea textAreaShow;
	Highlighter highLighter;
	boolean print=true;
	Map<String, DefaultHighlighter.DefaultHighlightPainter> highlightMap;
	//log
	File logfile;
	public SceneLogUtil(String udid) {
		// TODO Auto-generated constructor stub
		this.udid=udid;
		logfile=new File(System.getProperty("user.dir")+"/Logs/Runlog-"+TimeUtil.getTime4File()+"-"+udid+".txt");
		logger.info("create runlog file="+logfile.getAbsolutePath());
	}
	public SceneLogUtil(String udid,JTextArea textAreaShow) {
		// TODO Auto-generated constructor stub
		this.udid=udid;
		logfile=new File(System.getProperty("user.dir")+"/Logs/Runlog-"+TimeUtil.getTime4File()+"-"+udid+".txt");
		logger.info("create runlog file="+logfile.getAbsolutePath());
		this.textAreaShow=textAreaShow;
		//高亮设置
		if(textAreaShow!=null){
			highLighter=textAreaShow.getHighlighter();
			highlightMap=new HashMap<>();
			highlightMap.put("[CUSTOMER]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.BLUE)));//浅蓝
			highlightMap.put("[RESULT]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.BLUE)));
			highlightMap.put("[ASSERT]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.BLUE)));
			highlightMap.put("[CHECK]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.BLUE)));
			highlightMap.put("[TASK]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.GREEN)));//浅绿
//			highlightMap.put("[STEP]", new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
//			highlightMap.put("[INFO]", new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
			highlightMap.put("[WARN]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.YELLOW)));//浅黄
			highlightMap.put("[ERROR]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.RED)));//红色
			highlightMap.put("[SYSLOG]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.RED)));
			highlightMap.put("[APPLOG]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.RED)));	
			highlightMap.put("[CUSLOG]", new DefaultHighlighter.DefaultHighlightPainter(Color.decode(Cconfig.BLUE)));	
		}
	}
	/**
	 * 添加消息到界面
	 * @param text
	 */
	private synchronized void sendmsm(String text){
		logger.info("["+udid+"] "+text);
		if(textAreaShow!=null&&print){
			textAreaShow.append(text+"\n");
			addcolor(text+"\n");
		}else{
			if(print)System.out.println(text);
		}
		HelperUtil.file_write_line(logfile.getAbsolutePath(), text+"\n", true);
	}
	/**
	 * 是否显示日志
	 * @param print
	 */
	public void setPrint(boolean print){
		this.print=print;
	}
	/**
	 * 给日志添加颜色
	 */
	private void addcolor(String text){
		int lastpos=textAreaShow.getText().length()-text.length();//得到上次标记最后的位置index
		for(Entry<String, DefaultHighlighter.DefaultHighlightPainter> entry:highlightMap.entrySet()){
	        int pos = 0;
	        while ((pos = text.indexOf(entry.getKey(), pos)) >= 0){
	            try {
	                highLighter.addHighlight(lastpos+pos, lastpos+pos + entry.getKey().length(), entry.getValue());//
	                pos += entry.getKey().length();
	            }catch (BadLocationException e) {
	            	logger.error("Exception",e);
	            }
	        }
		}
	}
	
	
	/**
	 * 记录SceneRunner运行信息
	 * @param msm
	 */
	public void logTask(String msm){
		String text=TimeUtil.getTime4Log()+" [TASK]:"+msm+"";
		sendmsm(text);
	}
	
	/**
	 * 记录步骤信息
	 */
	public void logStep(String msm){
		String text=TimeUtil.getTime4Log()+" [STEP]:"+msm+"";
		sendmsm(text);
		stepsBuf(text);
	}
	/**
	 * 记录运行信息
	 */
	public void logInfo(String msm) {
		String text=TimeUtil.getTime4Log()+" [INFO]:"+msm;
		sendmsm(text);
		stepsBuf(text);
	}
	/**
	 * 记录警告信息
	 */
	public void logWarn(String msm) {
		String text=TimeUtil.getTime4Log()+" [WARN]:"+msm;
		//System.err.println(text);
		sendmsm(text);
		stepsBuf(text);
	}
	/**
	 * 记录错误信息
	 */
	public void logError(String msm) {
		String text=TimeUtil.getTime4Log()+" [ERROR]:"+msm;
		sendmsm(text);
		stepsBuf(text);
	}
	/**
	 * 记录断言信息
	 */
	public void logAssert(String msm) {
		String text=TimeUtil.getTime4Log()+" [ASSERT]:"+msm;
		sendmsm(text);
		stepsBuf(text);
	}
	/**
	 * 记录检查信息
	 */
	public void logCheck(String msm) {
		String text=TimeUtil.getTime4Log()+" [CHECK]:"+msm;
		sendmsm(text);
		stepsBuf(text);
	}
	/**
	 * 用户自定义信息
	 * @param msm
	 */
	public void logCustomer(String msm){
		String text=TimeUtil.getTime4Log()+" [CUSTOMER]:"+msm;
		sendmsm(text);
		stepsBuf(text);
		customerBuf(text+"\n");
	}
	/**
	 * log异常信息
	 * @param msm
	 */
	public void logExcepitonLog(String flag,String time,String msm,String picpath){
		String text=TimeUtil.getTime4Log()+" ["+flag+"]:"+"[PCtime"+time+"]"+msm;
		//System.err.println(text);
		sendmsm(text.split("\n")[0]);
		logBuf(text,picpath);
	}

	/**
	 * 自定义结果信息
	 * @param msm
	 */
	public void logResult(String msm) {
		String text=TimeUtil.getTime4Log()+" [RESULT]:"+msm; 
		sendmsm(text);
		stepsBuf(text);
		resultBuf(msm);
	}
	/**
	 * 加入resultBuf
	 * @param text
	 */
	private synchronized void resultBuf(String text){
		resultBuf.append(StringEscapeUtils.escapeHtml4(text)+"<br>");
	}
	/**
	 * 加入customerBuf
	 * @param text
	 */
	private synchronized void customerBuf(String text){
		customerBuf.append(StringEscapeUtils.escapeHtml4(text)+"<br>");
	}
	/**
	 * 加入setpsbuf
	 * @param text
	 */
	private synchronized void stepsBuf(String text){
		stepsBuf.append(StringEscapeUtils.escapeHtml4(text)+"<br>");
	}
	/**
	 * 加入logbuf
	 * @param text
	 * @param picpath
	 */
	private synchronized void logBuf(String text,String picpath){
		logBuf.append(StringEscapeUtils.escapeHtml4(text)+"<br>");
		if(picpath!=null){
			//String[] strings=picpath.split("\\/");
			File file=new File(picpath);
			logBuf.append("<font color='blue'>异常截图:"+file.getName()+"</font><br>");
			logBuf.append("<img src='./"+file.getParentFile().getName()+"/"+file.getName()+"' onerror=\""
					+ "this.src='"+"./"+file.getParentFile().getParentFile().getName()+"/"+file.getParentFile().getName()+"/"+file.getName()+"'\" style='width:50%'/>");
//			System.out.println("<img src='./"+file.getParentFile().getName()+"/"+file.getName()+"' onerror=\""
//					+ "this.src='"+"./"+file.getParentFile().getParentFile().getName()+"/"+file.getParentFile().getName()+"/"+file.getName()+"'\" style='width:50%'/>");
			//logBuf.append("<img src='./"+file.getParentFile().getParent()+"/"+file.getParent()+"/"+file.getName()+"' style='width:50%'/>");
		}else{
			logBuf.append("<font color='red'>异常截图失败</font><br>");
		}
		logBuf.append("[EXCEPTION]");
	}
	/**
	 * 返回用例步骤数据
	 * @return
	 */
	public StringBuffer getStepsbuf(){
		return stepsBuf;
	}
	/**
	 * 返回logcat日志信息
	 * @return
	 */
	public StringBuffer getLogcatBuf(){
		return logBuf;
	}
	/**
	 * 返回用户自定义信息
	 * @return
	 */
	public StringBuffer getCustomerBuf(){
		return customerBuf;
	}
	/**
	 * 返回用户自定义结果信息
	 * @return
	 */
	public StringBuffer getResultBuf(){
		return resultBuf;
	}
	/**
	 * 清空用例步骤数据
	 */
	public void clearCaseBuf(){
		stepsBuf.setLength(0);
		logBuf.setLength(0);
		customerBuf.setLength(0);
		resultBuf.setLength(0);
	}
	
}
