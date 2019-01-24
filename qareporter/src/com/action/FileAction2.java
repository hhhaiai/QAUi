package com.action;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.filechooser.FileSystemView;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.HelperUtil;
import com.servlet.ServerInit;

public class FileAction2 {
	Logger logger=LoggerFactory.getLogger(FileAction2.class);
	
	String reportpath=FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"/QAUiReport";
	
	public void setReportPath(String path) {
		this.reportpath=path;
	}
	public String getReportPath() {
		return reportpath;
	}
	/**
	 * 删除目录
	 * @param jsonstr
	 * @return
	 */
	public JSONObject delCata(String jsonstr) {
		JSONObject jsonresult=null;
		try {
			jsonresult=new JSONObject("{\"success\":\"false\",\"value\":\"删除失败\"}");//定义返回初始值
			JSONObject jsonObject=new JSONObject(jsonstr);//转化请求为json
			if(jsonObject.getString("type").equals("QAUiReport")){
				File file =new File(reportpath+"/"+jsonObject.getString("subfoldername"));
				if(file.exists()&&file.isDirectory()) {
					logger.info("del:"+file.getAbsolutePath());
					HelperUtil.delFolder(file.getAbsolutePath());
				}
				if(!file.exists()) {
					jsonresult=new JSONObject("{\"success\":\"true\",\"value\":\"删除成功\"}");
				}
			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
		return jsonresult;
	}
	
	/**
	 * 强制刷新一次TXT信息
	 * @param jsonstr
	 * @return
	 */
	public JSONObject refreshCataInfo(String jsonstr) {
		JSONObject jsonresult=null;
		try {
			jsonresult=new JSONObject("{\"success\":\"false\",\"value\":\"查询失败\"}");//定义返回初始值
			JSONObject jsonObject=new JSONObject(jsonstr);//转化请求为json
			if(jsonObject.getString("type").equals("QAUiReport")){
				//初始化一次
				File androidfile=new File(reportpath+"/Android");
				File iosfile=new File(reportpath+"/iOS");
				if(androidfile.exists()&&androidfile.isDirectory()) {
					File[] files=androidfile.listFiles(new FileFilter() {
						
						@Override
						public boolean accept(File f) {
							// TODO Auto-generated method stub
							return f.isDirectory()&&f.getName().matches("^[\\d_]+-\\w+$");
						}
					});
					for(File file:files){
						if(HelperUtil.file_read_all(file.getAbsolutePath()+"/测试结果汇总-Report.html").indexOf("<!--备注信息")>-1) {//标明已经运行完毕
							getReportInfo(file,true);	
						}
					}
					logger.info("init android report over");
				}else {
					logger.info("init android report over:no folder");
				}
				if(iosfile.exists()&&iosfile.isDirectory()) {
					File[] files=iosfile.listFiles(new FileFilter() {
						
						@Override
						public boolean accept(File f) {
							// TODO Auto-generated method stub
							return f.isDirectory()&&f.getName().matches("^[\\d_]+-\\w+$");
						}
					});
					for(File file:files){
						if(HelperUtil.file_read_all(file.getAbsolutePath()+"/测试结果汇总-Report.html").indexOf("<!--备注信息")>-1) {//标明已经运行完毕
							getReportInfo(file,true);	
						}
					}
					logger.info("init ios report over");
				}else {
					logger.info("init ios report over:no folder");
				}
				jsonresult=new JSONObject("{\"success\":\"true\",\"value\":\"更新完毕\"}");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
		return jsonresult;
	}
	/**
	 * 获取目录信息
	 * @param jsonstr
	 * @return
	 */
	public JSONObject getCataInfo(String jsonstr){
		JSONObject jsonresult=null;
		try {
			jsonresult=new JSONObject("{\"success\":\"false\",\"value\":\"查询失败\"}");//定义返回初始值
			JSONObject jsonObject=new JSONObject(jsonstr);//转化请求为json
			if(jsonObject.getString("type").equals("QAUiReport")){
				File catalogfile;
				if(jsonObject.getString("foldername").toLowerCase().equals("android")){
					catalogfile=new File(reportpath+"/Android");
				}else{
					catalogfile=new File(reportpath+"/iOS");
				}
				if(catalogfile.exists()&&catalogfile.isDirectory()){
					File[] files=catalogfile.listFiles(new FileFilter() {
						
						@Override
						public boolean accept(File f) {
							// TODO Auto-generated method stub
							return f.isDirectory()&&f.getName().matches("^[\\d_]+-\\w+$");
						}
					});
					//排序
					files=sortFileByCreateTime(files);
					StringBuffer Buf=new StringBuffer();
					for(File file:files){
						if(HelperUtil.file_read_all(file.getAbsolutePath()+"/测试结果汇总-Report.html").indexOf("<!--备注信息")>-1) {//标明已经运行完毕
							Buf.append(getReportInfo(file,false)+",");	
						}
					}
					//logger.info("JSON:"+"{\"success\":\"true\",\"value\":["+Buf.toString()+"]}");
					jsonresult=new JSONObject("{\"success\":\"true\",\"value\":["+Buf.toString()+"]}");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
		
		return jsonresult;
	}
	/**
	 * 获取报告信息
	 * @param file
	 * @param force 强制更新一次txt
	 * @return
	 */
	public String getReportInfo(File file,boolean force) {
		StringBuffer jsonBuf=new StringBuffer();
		File txt=new File(file.getAbsolutePath()+"/QAreporterNote.txt");
		if(!force&&txt.exists()&&txt.isFile()) {
			jsonBuf.append(HelperUtil.file_read_all(txt.getAbsolutePath()));
		}else {
			File[] subfiles=file.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File f) {
					// TODO Auto-generated method stub
					return f.isDirectory()&&f.getName().matches("^\\d+-.*");
				}
			});
			subfiles=sortFileByNo(subfiles);//排序
			//组装json
			jsonBuf.append("{\"name\":\""+file.getName()+"\",");
			String total_report_str=HelperUtil.file_read_content(file.getAbsolutePath()+"/测试结果汇总-Report.html", "<!--备注信息", "-->", -1);
			jsonBuf.append("\"reportnote\":"+total_report_str+",");
			jsonBuf.append("\"info\":[");
			for(File subfile:subfiles) {
				jsonBuf.append("{\"subname\":\""+subfile.getName()+"\",\"subreportnote\":");
				String sub_report_str=HelperUtil.file_read_content(subfile.getAbsolutePath()+"/测试结果-Report.html", "<!--备注信息", "-->", -1);
				jsonBuf.append((sub_report_str==null?"\"\"":sub_report_str)+"},");
			}
			jsonBuf.append("]}");
			HelperUtil.file_write_line(txt.getAbsolutePath(), jsonBuf.toString(), false);
		}
		return jsonBuf.toString();
	}
	
	
	/**
	 * 按照创建时间排序,从大到小
	 * @param files
	 * @return
	 */
	private File[] sortFileByCreateTime(File[] files){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
		Arrays.sort(files, new Comparator<File>(){

			@Override
			public int compare(File a, File b) {
				// TODO Auto-generated method stub
				try {
					long timea=simpleDateFormat.parse(a.getName().substring(0,a.getName().lastIndexOf("-"))).getTime();
					long timeb=simpleDateFormat.parse(b.getName().substring(0,b.getName().lastIndexOf("-"))).getTime();
					long diff=timea-timeb;
				    if(diff>0){    
				    	return -1;
				    }else if(diff==0){     
				    	return 0;     
				    }else{   
				    	return 1;  
				    }
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				}
				return 0;
			}
			
		}); 
		return files;
	}
	/**
	 * 按照序号排序,从小到大
	 * @param files
	 * @return
	 */
	private File[] sortFileByNo(File[] files){
		Arrays.sort(files, new Comparator<File>(){

			@Override
			public int compare(File a, File b) {
				// TODO Auto-generated method stub
				int noa=Integer.parseInt(a.getName().substring(0,a.getName().indexOf("-")));
				int nob=Integer.parseInt(b.getName().substring(0,b.getName().indexOf("-")));
				long diff=noa-nob;
			    if(diff>0){    
			    	return 1;
			    }else if(diff==0){     
			    	return 0;     
			    }else{   
			    	return -1;  
			    }
			}
			
		}); 
		return files;
	}
}  

