package com.servlet;

import java.awt.Font;
import java.io.File;
import java.io.FileFilter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.action.FileAction;

public class ServerInit implements ServletContextListener {
    Logger logger = LoggerFactory.getLogger(ServerInit.class);
    
    public static FileAction fileAction=new FileAction();
    public static String Version="V1.0329.1";
    
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("QAreport contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("QAreport contextInitialized");
		//判断系统类型
		String OSname=System.getProperty("os.name");
		if(OSname.toLowerCase().indexOf("windows")>-1){
			fileAction.setReportPath(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"/QAUiReport");
		}else if(OSname.toLowerCase().indexOf("mac")>-1){
			fileAction.setReportPath(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"/Desktop/QAUiReport");
		}else{
			fileAction.setReportPath(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"/QAUiReport");
		}
		logger.info("System type="+OSname);
		logger.info("desktop path="+fileAction.getReportPath());
	}

}
