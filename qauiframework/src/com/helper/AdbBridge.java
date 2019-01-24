package com.helper;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.constant.CAndroidCMD;
import com.constant.Cconfig;
import com.viewer.main.MainRun;
import com.android.ddmlib.IDevice;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdbBridge{
	  Logger logger = LoggerFactory.getLogger(AdbBridge.class);
	  IDevice[] devices;
	  /**
	   * 初始化adb桥接
	   * @return
	   */
	  public boolean initialize(){
		  AndroidDebugBridge mAndroidDebugBridge;
		  boolean success = true;
		  // http://javadox.com/com.android.tools.ddms/ddmlib/23.0.1/com/android/ddmlib/IDevice.html
		  //https://android.googlesource.com/platform/tools/base/+/master/ddmlib/src/main/java/com/android/ddmlib/ScreenRecorderOptions.java
		  //https://www.programcreek.com/java-api-examples/index.php?source_dir=android-platform_sdk-master/ddms/libs/ddmuilib/src/com/android/ddmuilib/SysinfoPanel.java 源码
			  AndroidDebugBridge.init(false);
//			  if(QAToolsRun.OStype==0){
//				  mAndroidDebugBridge = AndroidDebugBridge.createBridge(QAToolsRun.extraBinlocation+"/adb.exe",true);
//			  }else{
//				  mAndroidDebugBridge = AndroidDebugBridge.createBridge(QAToolsRun.extraBinlocation+"/adb",true);
//			  }
			  
			  mAndroidDebugBridge=AndroidDebugBridge.createBridge();
		      if (mAndroidDebugBridge == null) {
		    	  logger.info("mAndroidDebugBridge success = false");
		    	  success = false;
		    	  return success;
		      }
		//  com.Main.ThenToolsRun.logger.log(Level.INFO,mAndroidDebugBridge.getAdbVersion(new File(com.Main.ThenToolsRun.extraBinlocation+"/adb.exe")).toString());
		  AndroidDebugBridge.addDeviceChangeListener(new IDeviceChangeListener(){
			@Override
			public void deviceChanged(IDevice arg0, int arg1) {
				// TODO Auto-generated method stub
				devices = mAndroidDebugBridge.getDevices();
			}
	
			@Override
			public void deviceConnected(IDevice arg0) {
				// TODO Auto-generated method stub
				devices = mAndroidDebugBridge.getDevices();
			}
	
			@Override
			public void deviceDisconnected(IDevice arg0) {
				// TODO Auto-generated method stub
				devices = mAndroidDebugBridge.getDevices();
			//	device=null;
				//com.Main.ThenToolsRun.selectedID=null;
			}
			  
		  });
	    if (!success) {
	      terminate();
	    }
	    logger.info("mAndroidDebugBridge success");
	    return success;
	  }
	  /**
	   * 关闭adb
	   */
	  public void terminate() {
		  logger.info("try to adb terminate");
		  //部分PC卡死 win7 64
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(
		        	   new Callable<String>() {//使用Callable接口作为构造参数
		        	       public String call() throws IOException {
		 	        	      //真正的任务在这里执行，这里的返回值类型为String，可以为任意类型
		        	    	   AndroidDebugBridge.terminate();
		        	    	   return "ok";
							}
		        	   });
	 		try {
				future.get(5, TimeUnit.SECONDS).equals("ok");
				logger.info("adb terminate ok");
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				// TODO Auto-generated catch block
				logger.info("adb terminate failed");
				logger.error("Exception",e);
			}
	  }
	  /**
	   * 获取设备列表
	   * @return
	   */
	  public IDevice[] getDevices() {
	//    if (mAndroidDebugBridge != null) {
	//      devices = this.mAndroidDebugBridge.getDevices();
	//    }
	    return devices;
	  }
	  /**
	   * 获取设备
	   * @param udid
	   * @return
	   */
	  public IDevice getDevice(String udid){
		if (devices!=null) {
	      for(IDevice iDevice:devices){
	    	  if(iDevice.toString().equals(udid)){
	    		  return iDevice;
	    	  }
	      }
		}
		return null;
	  }
  
}