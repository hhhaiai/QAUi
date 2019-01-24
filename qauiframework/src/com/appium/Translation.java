package com.appium;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translation {
	Logger logger=LoggerFactory.getLogger(Translation.class);

	Map<String, Object> map;
	public Translation(List<String> varsclass) {
		// TODO Auto-generated constructor stub
		setMap(varsclass);
	}
	/**
	 * 获取常量名及值,存入map
	 */
	private void setMap(List<String> varsclass){
		map=new HashMap<String,Object>();
		Class clazz;
		try {
				for(String str:varsclass){
					logger.info("load interface vars from "+str);
					clazz = Class.forName(str);
			        Field[] fields = clazz.getFields();
			        for( Field field : fields ){
			            //System.out.println( field.getName() + "=" +(String)field.get(clazz));
			        	field.setAccessible(true);
			        	if(field.get(clazz)!=null && (field.get(clazz) instanceof String||field.get(clazz) instanceof Integer)){
		        			map.put(field.getName().trim(), field.get(clazz));	
			        	}
			        }
				}
			logger.info("varsclass set map successful,total key="+map.size());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}

	}
	/**
	 * 根据常量值By查询名称
	 * @param value
	 * @return Name
	 */
	public String getName(By by){
		//By.name: 挑战
		String text=by.toString().substring(by.toString().indexOf(":")<0?0:by.toString().indexOf(":")+1, by.toString().length()).trim();
		for(Entry<String, Object> entry:map.entrySet()){
			if(entry.getValue() instanceof String)if(text.equals((String)entry.getValue()))return entry.getKey();	
		}
		return by.toString();
	}
	/**
	 * 根据常量值String查询名称
	 * @param text
	 * @return
	 */
	public String getName(String text){
		for(Entry<String, Object> entry:map.entrySet()){
			if(entry.getValue() instanceof String)if(text.equals((String)entry.getValue()))return entry.getKey();
		}
		return text;
	}
	/**
	 * 根据常量值int查询名称
	 * @param text
	 * @return
	 */
	public String getName(int index){
		for(Entry<String, Object> entry:map.entrySet()){
			if(entry.getValue() instanceof Integer)if(index==(Integer)entry.getValue())return entry.getKey();
		}
		return index+"";
	}
}
