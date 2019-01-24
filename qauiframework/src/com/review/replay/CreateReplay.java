package com.review.replay;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.helper.CMDUtil;
import com.video.core.DefaultMovieInfoProvider;
import com.video.core.ImageProvider;
import com.video.core.Jim2Mov;
import com.video.core.MovieInfoProvider;
import com.video.core.MovieSaveException;
import com.video.utils.MovieUtils;
import com.viewer.main.MainRun;

public class CreateReplay {
	Logger logger=LoggerFactory.getLogger(CreateReplay.class);
	File reportFolder;
	
	public CreateReplay(File reportFolder) {
		// TODO Auto-generated constructor stub
		this.reportFolder=reportFolder;
		File movieFolder=new File(reportFolder.getAbsolutePath()+"/Movie/");
		if(!movieFolder.exists())movieFolder.mkdirs();
	}
	/**
	 * 生成视频,返回MP4视频地址
	 * @param startpic
	 * @param endpic
	 * @return
	 */
	public String createVideo(int startpic,int endpic){
		File[] pics=new File(reportFolder.getAbsolutePath()+"/ScreenShot").listFiles((File dir,String name)-> name.toLowerCase().endsWith(".png"));
		ArrayList<File> sortfiles=new ArrayList<>();
		int piccount=endpic-startpic;
		int currentpic=startpic;
		for(int i=0;i<piccount+1;i++){
			for(File file:pics){
				if(Integer.parseInt(file.getName().split("-")[0])==currentpic){
					sortfiles.add(file);
					break;	
				}
			}
			currentpic++;
		}
		String avisavepath=reportFolder.getAbsolutePath()+"/Movie/"+startpic+"-"+endpic+"-video.avi";
		String mp4savepath=reportFolder.getAbsolutePath()+"/Movie/"+startpic+"-"+endpic+"-video.mp4";
		convertPicToAvi( (File[])sortfiles.toArray(new File[sortfiles.size()]), avisavepath, 1, 0, 0);
		File avifile=new File(avisavepath);
		if(avifile.exists()){
			String ffmpeg;
			if(MainRun.settingsBean.getSystem()==Cconfig.WINDOWS){
				ffmpeg="ffmpeg.exe";
			}else if(MainRun.settingsBean.getSystem()==Cconfig.LINUX){
				ffmpeg="ffmpeg_linux";
			}else{
				ffmpeg="ffmpeg_mac";
			}
			if(CMDUtil.execcmd(MainRun.settingsBean.getExtraBinlocation()+"/ffmpeg/"+ffmpeg+" -i \""+avisavepath+"\" -vcodec libx264 -acodec aac \""+mp4savepath+"\"", 1, true)[1].contains("Conversion failed!")){
				logger.error("AVI转化MP4失败:"+avisavepath);
				return mp4savepath;
			};			
			avifile.delete();
		}else{
			logger.error("生成AVI视频失败:"+avisavepath);
		}
		return mp4savepath;
	}
	
	/**
     * 将图片转换成视频
     *
     * @param pics      
     * @param savefile 生成的avi视频文件名
     * @param fps         每秒帧数
     * @param mWidth      视频的宽度
     * @param mHeight     视频的高度
     * @throws Exception
     */
    private void convertPicToAvi(File[] pics, String savefile, int fps, int mWidth, int mHeight){
        if (pics == null || pics.length == 0) return;
        try{
	        DefaultMovieInfoProvider dmip = new DefaultMovieInfoProvider("file:///"+savefile);      //视频名称(生成路径为本工程目录)
	        dmip.setFPS(fps > 0 ? fps : 1); 												//每秒帧数
	        dmip.setNumberOfFrames(pics.length);                                            //总帧数
	        dmip.setMWidth(mWidth > 0 ? mWidth : 480);          							//视频宽(图片一致)
	        dmip.setMHeight(mHeight > 0 ? mHeight : 854);								    //视频高(图片一致)
	    	new Jim2Mov(new ImageProvider() {
	    		public byte[] getImage(int frame) {
	    			try {
	    				BufferedImage bufferedImage = ImageIO.read(pics[frame]);
	    				if (null != bufferedImage)		
	    					return MovieUtils.convertImageToJPEG(bufferedImage, 1.0f);      // 设置压缩比
	    			} catch (IOException e) {
	    				System.out.println(e);
	    			}
	    			return null;
	    		}
	    	}, dmip, null).saveMovie(MovieInfoProvider.TYPE_AVI_MJPEG);                     //将图片转换视频
	        logger.info("create avi video successful,pics="+pics.length);
        } catch (MovieSaveException e) {  
        	logger.error("Exception",e); 
        } catch (Exception e) {
        	logger.error("Exception",e); 
		}
    }
    
    /**
	 * 依赖JMF环境，运行前请先进行相关配置
	 * 
     * 将图片转换成视频
     *
     * @param picDir      图片文件夹绝对路径
     * @param suffix      图片后缀
     * @param aviFileName 生成的avi视频文件名
     * @param fps         每秒帧数
     * @param mWidth      视频的宽度
     * @param mHeight     视频的高度
     * @throws Exception
     */
    public static void convertPicToAvi(String picDir, String suffix, String aviFileName, int fps, int mWidth, int mHeight) throws MovieSaveException, Exception {
        final File[] pics = new File(picDir).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("." + suffix);
            }
        });

        if (pics == null || pics.length == 0) {
            return;
        }
        Arrays.sort(pics, new Comparator<File>() {
            public int compare(File file1, File file2) {          	
//              按照创建时间排序
                String ct1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file1.lastModified()));
    	        String ct2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file2.lastModified()));
    	        return ct1.compareTo(ct2);	
            }
        });

        DefaultMovieInfoProvider dmip = new DefaultMovieInfoProvider(aviFileName);      //视频名称(生成路径为本工程目录)
        dmip.setFPS(fps > 0 ? fps : 1); 												//每秒帧数
        dmip.setNumberOfFrames(pics.length);                                            //总帧数
        dmip.setMWidth(mWidth > 0 ? mWidth : 1080);          							//视频宽(图片一致)
        dmip.setMHeight(mHeight > 0 ? mHeight : 1920);								    //视频高(图片一致)
        
        try{
        	new Jim2Mov(new ImageProvider() {
        		public byte[] getImage(int frame) {
        			try {
        				BufferedImage bufferedImage = ImageIO.read(pics[frame]);
        				if (null != bufferedImage)		
        					return MovieUtils.convertImageToJPEG(bufferedImage, 1.0f);      // 设置压缩比
        			} catch (IOException e) {
        				System.out.println(e);
        			}
        			return null;
        		}
        	}, dmip, null).saveMovie(MovieInfoProvider.TYPE_AVI_MJPEG);                     //将图片转换视频
        } catch (MovieSaveException e) {  
        	System.err.println(e);  
        } 
        System.out.println("Create Video Success.");
    }
}
