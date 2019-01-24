package com.appium;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.PicturesUtil;
import com.log.SceneLogUtil;
/**
 * 用于快速检查
 *
 */
public abstract class BaseCheck {
	Logger logger=LoggerFactory.getLogger(BaseCheck.class);
	SceneLogUtil oplog;
	Translation translation;
	public BaseCheck(SceneLogUtil oplog,Translation translation){
		this.oplog=oplog;
		this.translation=translation;
	}
	
	/**
	 * 获取页面控件XML信息
	 * @return
	 */
	public abstract String getPagesource();
	/**
	 * 是否存在字符串
	 * @param strs 字符串,PS:"关注","热门","相机",...
	 * @return 不存在返回true
	 */
	public abstract boolean notExist(String... strs);
	/**
	 * 是否存在字符串
	 * @param regexs 正则表达式
	 * @return 不存在返回true
	 */
	public abstract boolean notExistByRegex(String... regexs);
	
	/**
	 * 是否存在字符串
	 * @param strs 字符串,PS:"关注","热门","相机",...
	 * @return
	 */
	public abstract boolean exist(String... strs);
	/**
	 * 是否存在字符串
	 * @param regexs 正则表达式
	 * @return
	 */
	public abstract boolean existByRegex(String... regexs);
	/**
	 * 出现次数
	 * @param uniquestr 唯一标志字符串
	 * @return
	 */
	public abstract int getCount(String uniquestr);
	/**
	 * 出现次数
	 * @param regex 正则表达式
	 * @return
	 */
	public abstract int getCountByRegex(String regex);
	/**
	 * 将出现了字符串的所有的"&lt;xxx&gt;"增加到列表
	 * @param uniquestr
	 * @return
	 */
	public abstract ArrayList<String> getList(String uniquestr);
	
	/**
	 * 比较两张图片是否一致 jpg/jpeg/png/bmp
	 * @param actual 图片绝对路径A
	 * @param expected 图片绝对路径B
	 * @return boolean
	 */
	public boolean comparePictures(String actualPath,String expectedPath){
		File fileA=new File(actualPath);
		File fileB=new File(expectedPath);
		boolean issame=PicturesUtil.compare(fileA, fileB);
		if(!fileA.exists())oplog.logError("图片对比:"+fileA.getAbsolutePath()+"不存在");
		if(!fileB.exists())oplog.logError("图片对比:"+fileB.getAbsolutePath()+"不存在");
		if(issame){
			oplog.logInfo("图片对比:"+fileA.getName()+"与"+fileB.getName()+"对比相同");
		}else{
			oplog.logError("图片对比:"+fileA.getName()+"与"+fileB.getName()+"对比不相同");
		}
		return issame;
	}
	/**
	 * 比较两张图片是否一致 jpg/jpeg/png/bmp
	 * @param actual 图片绝对路径A
	 * @param expected 图片绝对路径B
	 * @param x 开始比较的x坐标
     * @param y 开始比较的y坐标
     * @param width 需要比较的宽
     * @param height 需要比较的高
	 * @return boolean
	 */
	public boolean comparePictures(String actualPath,String expectedPath,int x, int y, int width,int height){
		File fileA=new File(actualPath);
		File fileB=new File(expectedPath);
		boolean issame=PicturesUtil.compare(fileA, fileB,x,y,width,height);
		if(!fileA.exists())oplog.logError("图片对比:"+fileA.getAbsolutePath()+"不存在");
		if(!fileB.exists())oplog.logError("图片对比:"+fileB.getAbsolutePath()+"不存在");
		if(issame){
			oplog.logInfo("图片对比:"+fileA.getName()+"与"+fileB.getName()+"在区域("+x+","+y+","+width+","+height+")对比相同");
		}else{
			oplog.logError("图片对比:"+fileA.getName()+"与"+fileB.getName()+"在区域("+x+","+y+","+width+","+height+")对比不相同");
		}
		return issame;
	}
}
