package com.review.getscreen;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ScreenRecorderOptions;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.constant.Cconfig;
import com.helper.ADBUtil;
import com.log.SceneLogUtil;
import com.viewer.main.MainRun;

public class AndroidRecord {
	Logger logger=LoggerFactory.getLogger(AndroidRecord.class);
	String qa_screenrecord_path="/data/local/tmp/qascreenrecord.mp4";///data/local/tmp
	int bitrate;
	int width;
	int height;
	String udid;
	File reportFolder;
	SceneLogUtil oplog;
	boolean iscancel=false;
	boolean recordThreadrun=false;
	
	public AndroidRecord(String udid,SceneLogUtil oplog,File reportFolder,int device_width,int device_height){
		this.udid=udid;
		this.reportFolder=reportFolder;
		this.oplog=oplog;
		bitrate=8;
		height=MainRun.sysConfigBean.getPicTargetHight();
		width=(int)(((double)device_width/(double)device_height)*height);
		logger.info("record height="+height+",width="+width);
	}
	/**
	 * 设置录制视频参数
	 * @param bitrate
	 * @param width
	 * @param height
	 */
	public void setParams(int bitrate,int width,int height){
		this.bitrate=bitrate;
		this.width=width;
		this.height=height;
	}
	/**
	 * 停止录制
	 */
	public void stop(){
		oplog.logInfo("停止录制视频...");
		iscancel=true;
		recordThreadrun=false;
	}
	/**
	 * 开始录制视频
	 * @param name 视频命名
	 * @param path 视频保存路径
	 */
	public void start(String name,String path){
		if(recordThreadrun){
			oplog.logWarn("目前视频正在录制,请检查代码逻辑...");
			return;
		}
		name=name.replaceAll("[\\s\\\\/:\\*\\?\\\"<>\\|]", "");
		if(path==null||!new File(path).exists()||new File(path).isFile()){
			File cusfolder=new File(reportFolder.getAbsolutePath().replaceAll("\\\\", "/")+"/"+Cconfig.CUSTOMER_FOLDER);
			if(!cusfolder.exists())cusfolder.mkdirs();
			path=cusfolder.getAbsolutePath()+"/"+name+".mp4";//保存路径	
		}else{
			path=path+"/"+name+".mp4";
		}
		RecordVideo(path);
	}
	/**
	 * 录制视频,最长时间3分钟
	 * @param path 视频保存路径
	 */
	private void RecordVideo(String path){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean failed=false;
				try {
					IDevice iDevice=MainRun.adbBridge.getDevice(udid);
					if(iDevice!=null){
						oplog.logInfo("开始录制视频,最长时间3分钟,视频保存路径: "+path);
						iscancel=false;
						recordThreadrun=true;
						logger.info("start to screen record...");
						ScreenRecorderOptions options=new ScreenRecorderOptions.Builder().setBitRate(bitrate).setSize(width, height).build();
						iDevice.startScreenRecorder(qa_screenrecord_path, options,  new IShellOutputReceiver() { 
						    @Override 
						    public boolean isCancelled() {
						        return iscancel; 
						    } 

						    @Override 
						    public void flush() { 
						    		logger.info("screen record flush");
						    } 

						    @Override 
						    public void addOutput(byte[] data, int offset, int length) { 
						        String Message; 
						        if (data != null) { 
						        	Message = new String(data); 
						        } else { 
						        	Message = ""; 
						        } 
						        logger.info(Message);
						        if(Message.contains("screenrecord: not found")) {
						        		oplog.logWarn("设备不支持adb录屏!");
						        }
						    } 
						});
					}
				} catch (TimeoutException | AdbCommandRejectedException
						| ShellCommandUnresponsiveException | IOException e) {
					// TODO Auto-generated catch block
					logger.warn("Exception",e);
					failed=true;
					oplog.logWarn("视频录制失败,请检查!");
				}
				logger.info("record video finished");
				getVideoFromUE(failed,path);
			}
		}).start();
	}
	/**
	 * 从设备提取录制的视频
	 * @param path
	 */
	private void getVideoFromUE(boolean failed,String path){
		try {
			if(!failed) {
				Thread.sleep(2000);//等待视频生成完成
				oplog.logInfo("开始提取视频...");
				if(!ADBUtil.pullfile(udid, qa_screenrecord_path, path)){
					oplog.logWarn("视频提取失败,请检查!");
				}else{
					oplog.logInfo("视频提取完成,保存路径: "+path);	
				}	
			}
		} catch (Exception e) {
			logger.error("Exception",e);
		}finally {
			recordThreadrun=false;
			iscancel=false;
			logger.info("get record finished");	
		}
	}
}
