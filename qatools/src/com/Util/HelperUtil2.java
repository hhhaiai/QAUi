package com.Util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class HelperUtil2 {
	  static Logger logger = LoggerFactory.getLogger(HelperUtil2.class);
	  //转化时间戳
	  public static String translateTime(String txt){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Pattern time_format=Pattern.compile("[0-9]{13}|[0-9]{10}");
		Matcher matcher=time_format.matcher(txt);
		if(matcher.find()){
			String timestamp=matcher.group(0);
			long temp=0;
			if(timestamp.length()==10){
				temp=Long.parseLong(timestamp)*1000;
			}else{
				temp=Long.parseLong(timestamp);
			}
			return sdf.format(temp); 	
		}
		return "";
	  }
	  
	  
	  //判断格式是否符合
	  public static boolean check_format(String txt){
		  Pattern TXT_format = Pattern.compile("^.+,.+$"); 
		  String[] strings=txt.replaceAll("\n", "").split(";");
		  for(String str:strings){
			  if(!TXT_format.matcher(str).matches()){
				  return false;
			  }
		  }
		  return true;
	  }
	  
	//判断是否数字
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	//判断是否数字
	public static boolean isDecimal(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*.[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	//是否含有中文
	 public static final boolean isChinese(String strName) {  
	        char[] ch = strName.toCharArray();  
	        for (int i = 0; i < ch.length; i++) {  
	            char c = ch[i];  
	            if (isChinese(c)) {  
	                return true;  
	            }  
	        }  
	        return false;  
	    } 
	  private static final boolean isChinese(char c) {  
	        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
	        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
	                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
	                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
	                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
	                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
	            return true;  
	        }  
	        return false;  
	  }
	/** 
	   * 功能：检测当前URL是否可连接或是否有效, 
	   * 描述：最多连接网络 5 次, 如果 5 次都不成功，视为该地址不可用 
	   * @param urlStr 指定URL网络地址 
	   * @return URL 
	   */  
	public static boolean URLisConnect(String urlStr) {  
		URL url = null;  
	   HttpURLConnection con;  
	   int state = -1;  
	   int counts = 0;  
	   if (urlStr == null || urlStr.length() <= 0) {                         
		   return false;                   
	   }  
	   while (counts < 5) {  
	    try {  
	     url = new URL(urlStr);  
	     con = (HttpURLConnection) url.openConnection();  
	     state = con.getResponseCode();   
	     if (state == 200) {  
	    	 logger.info("URL="+urlStr+" is available!");  
	    	 return true;
	     } 
	     logger.info(counts +"= "+state); 
	    }catch (Exception e) {  
	     counts++;   
	     logger.error("URL is unavailable,counts="+counts,e);   
	     continue;  
	    }  
	   }  
	   return false;  
	}  


	
	//MD5加密
	  public final static String MD5(String s) {
	        char hexDigits[] = { '0', '1', '2', '3', '4',
	                             '5', '6', '7', '8', '9',
	                             'A', 'B', 'C', 'D', 'E', 'F' };
	        try {
	            byte[] btInput = s.getBytes();
	     //获得MD5摘要算法的 MessageDigest 对象
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	     //使用指定的字节更新摘要
	            mdInst.update(btInput);
	     //获得密文
	            byte[] md = mdInst.digest();
	     //把密文转换成十六进制的字符串形式
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str).toString().toLowerCase();
	        }
	        catch (Exception e) {
	        	logger.error("Exception",e);
	            return null;
	        }
	    }
 //随即
	 public static int[] randomCommon(int min, int max, int n){  
			if (n > (max - min + 1) || max < min) {  
			       return null;  
			   }  
			int[] result = new int[n];  
			int count = 0;  
			while(count < n) {  
			    int num = (int) (Math.random() * (max - min)) + min;  
			    boolean flag = true;  
			    for (int j = 0; j < n; j++) {  
				if(num == result[j]){  
				    flag = false;  
				    break;  
				}  
			    }  
			    if(flag){  
				result[count] = num;  
				count++;  
			    }  
			}  
			return result;  
	}
	 
//获取时间戳
	 public static String getTime(String timestr){
		 if(timestr==null){
			 return null;
		 }
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try{
			Date date=sdf.parse(timestr);
			return date.getTime()/1000+"";
		}catch(ParseException e){
			logger.error("Exception",e);
		}
	    return null;
	}
	 
// AES加密
    public static String AESEncrypt(String sSrc, String sKey) {
        if (sKey == null) {
            System.out.println("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.println("Key长度不是16位");
            return null;
        }
        String result = null;
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		    byte[] raw = md.digest(sKey.getBytes("utf-8"));
		      //  byte[] raw = sKey.getBytes("utf-8");
		        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
		        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
			  result=Base64.getEncoder().encodeToString(encrypted);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //为了和服务器nodejs加密结果一直 必须将密钥先进行md5校验
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	//String result=bytesToHexString(encrypted);
		logger.info("Str="+sSrc+",key="+sKey+",AESEncrypt="+result);
        return result;//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }
    
	//write all string to a file
	public static void writeAlltoFile(String filepath,String content,boolean isappend,boolean split){
        FileOutputStream fileOutputStream;
		try {
			byte[] initline;
			if(split){
				String[] spiltstr=content.split("\n");
				fileOutputStream = new FileOutputStream(filepath,isappend);
	            for(String line:spiltstr){
	            	line=line+"\r\n";
	                initline = line.getBytes("UTF-8");
	                fileOutputStream.write(initline);
	            }
			}else{
				fileOutputStream = new FileOutputStream(filepath,isappend);
				initline = content.getBytes("UTF-8");
	            fileOutputStream.write(initline);
			}
            fileOutputStream.close();
            fileOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
	}
		
	//write to file
	public static void writetoFile(String filepath,String content,boolean isappend){
	        FileOutputStream fileOutputStream;
			try {
				fileOutputStream = new FileOutputStream(filepath,isappend);
	            content=content+"\r\n";
	            byte[] initContent = content.getBytes("UTF-8");
	            fileOutputStream.write(initContent);
	            fileOutputStream.close();
	            fileOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e); 
			}
		}
	
	//string to json
	public static String string2Json(String s) {      
	    StringBuffer sb = new StringBuffer ();      
	    for (int i=0; i<s.length();i++){
	        char c = s.charAt(i);      
	        switch (c) {      
	        case '\"':      
	            sb.append("\\\"");      
	            break;      
	        case '\\':      
	            sb.append("\\\\");      
	            break;      
	        case '/':      
	            sb.append("\\/");      
	            break;      
	        case '\b':      
	            sb.append("\\b");      
	            break;      
	        case '\f':      
	            sb.append("\\f");      
	            break;      
	        case '\n':      
	            sb.append("\\n");      
	            break;      
	        case '\r':      
	            sb.append("\\r");      
	            break;      
	        case '\t':      
	            sb.append("\\t");      
	            break;      
	        default:      
	            sb.append(c);      
	        }   
	 }
	    return sb.toString();  
	}
	///n to <br>
	public static String string2html(String s){
		s=s.replace("\r\n", "<br>");
	    return s;  
	}
	//interBR 10
	public static String interBR(String str,int i){
		i++;
		char[] charArray=str.toCharArray();
		StringBuffer stringBuffer=new StringBuffer();
		int count=0;
		for(char c:charArray){
			count++;
			if(count%i==0){
				stringBuffer.append(String.valueOf(c)+"<br>");
			}else{
	    		stringBuffer.append(String.valueOf(c));
			}
		}
		return stringBuffer.toString();
	}
	//open a file
	public static boolean OpenFile(String path){
		File file=new File(path);
		if(file.exists()){
		Desktop desk=Desktop.getDesktop(); 
			try {
				desk.open(file);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
			}
		}
		return false;
	}
	
	//second to time
	public static String SStoTime(int timecount){//格式化时间
		int hh=0,mm=0,ss=0;
		String h,m,s;
		for(int i=0;i<=timecount;i++){
			if(ss>=59){
				ss=0;
				mm++;
			}
			if(mm>=59){
				mm=0;
				hh++;
			}
			ss++;
		}
		if(hh>9){
			h=String.valueOf(hh);
		}else{
			h="0"+String.valueOf(hh);
		}
		if(mm>9){
			m=String.valueOf(mm);
		}else{
			m="0"+String.valueOf(mm);
		}
		if(ss>9){
			s=String.valueOf(ss);
		}else{
			s="0"+String.valueOf(ss);
		}
		return h+":"+m+":"+s;
	}
	
	//time to second
	public static int TimetoSS(String time){
		String[] temp=time.split(":");//00:00:26.51
		temp[2]=temp[2].substring(0,2);
		if(temp[0].trim().startsWith("0")){
			temp[0]=temp[0].substring(1,temp[0].length());
		}
		if(temp[1].startsWith("0")){
			temp[1]=temp[1].substring(1,temp[1].length());
		}
		if(temp[2].startsWith("0")){
			temp[2]=temp[2].substring(1,temp[2].length());
		}
		return Integer.parseInt(temp[0])*3600+Integer.parseInt(temp[1])*60+Integer.parseInt(temp[2]);
	}
	//get string show count
		public static int getStringShowCount(String str,String sub)  
	    {  
	        int index = 0;  
	        int count = 0;  
	  
	        while((index=str.indexOf(sub))!=-1)  
	        {  
	            str = str.substring(index+sub.length());  
	            count++;  
	        }  
	        return count;  
	    } 
		
		
		//read all string 
	public static StringBuffer readAllfromfile(String filepath) {  
	    File file = new File(filepath);  
	    BufferedReader reader = null;  
	    StringBuffer strbuf=new StringBuffer();
	    try {  
	        reader = new BufferedReader(new FileReader(file));  
	        String tempString = null;  
	        while ((tempString = reader.readLine()) != null) {
	        	strbuf.append(tempString+"\n");
	        }  
	        reader.close();  
	    } catch (IOException e) {  
	    	logger.error("Exception",e);
	    } finally {  
	        if (reader != null) {  
	                try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e); 
					}
	        } 
	        
	    }  
	    return strbuf;
	} 
	 /** 
	 * 以行为单位读取文件，常用于读面向行的格式化文件 
	 */  
	public static void readFileByLines(String fileName) {  
	    File file = new File(fileName);  
	    BufferedReader reader = null;  
	    try {  
	       // System.out.println("以行为单位读取文件内容，一次读一整行：");  
	        reader = new BufferedReader(new FileReader(file));  
	        String tempString = null;  
	        int line = 1;  
	        // 一次读入一行，直到读入null为文件结束  
	        while ((tempString = reader.readLine()) != null) {  
	            // 显示行号  
	            System.out.println("line " + line + ": " + tempString);  
	            line++;  
	        }  
	        reader.close();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (reader != null) {  
	            try {  
	                reader.close();  
	            } catch (IOException e) {  
	            	logger.error("Exception",e);
	            }  
	        }  
	    }  
	} 
	
	/**
	 * 格式化json
	 * @param content
	 * @return
	 */
	public static String formatJson(String content) {
		if(content!=null){
	    StringBuffer sb = new StringBuffer();
	    int index = 0;
	    int count = 0;
	    while(index < content.length()){
	        char ch = content.charAt(index);
	        if(ch == '{' || ch == '['){
	            sb.append(ch);
	            sb.append('\n');
	            count++;
	            for (int i = 0; i < count; i++) {                   
	             //   sb.append('\t');
	            }
	        }
	        else if(ch == '}' || ch == ']'){
	            sb.append('\n');
	            count--;
	            for (int i = 0; i < count; i++) {                   
	             //   sb.append('\t');
	            }
	            sb.append(ch);
	        } 
	        else if(ch == ','){
	            sb.append(ch);
	            sb.append('\n');
	            for (int i = 0; i < count; i++) {                   
	               // sb.append('\t');
	            }
	        } 
	        else {
	            sb.append(ch);              
	        }
	        index ++;
	    }
	    return sb.toString();
		}
		return "formatJson content is null...";
	}
	/**
	 * 把格式化的json紧凑
	 * @param content
	 * @return
	 */
	public static String compactJson(String content) {
	    String regEx="[\t\n]"; 
	    Pattern p = Pattern.compile(regEx); 
	    Matcher m = p.matcher(content);
	    return m.replaceAll("").trim();
	    }
	    
	 // 判断一个字符串是否都为数字  
	public static boolean isDigit(String strNum) { 
		if(strNum==null){
			return false;
		}
	    return strNum.matches("[0-9]{1,}");  
	}  
	
	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
	   try {
	    delAllFile(folderPath); // 删除完里面所有内容
	    String filePath = folderPath;
	    filePath = filePath.toString();
	    java.io.File myFilePath = new java.io.File(filePath);
	    myFilePath.delete(); // 删除空文件夹
	   } catch (Exception e) {
		   logger.error("Excepiton",e);
	   }
	}
	
	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
	   boolean flag = false;
	   File file = new File(path);
	   if (!file.exists()) {
	    return flag;
	   }
	   if (!file.isDirectory()) {
	    return flag;
	   }
	   String[] tempList = file.list();
	   File temp = null;
	   for (int i = 0; i < tempList.length; i++) {
	    if (path.endsWith(File.separator)) {
	     temp = new File(path + tempList[i]);
	    } else {
	     temp = new File(path + File.separator + tempList[i]);
	    }
	    if (temp.isFile()) {
	     temp.delete();
	    }
	    if (temp.isDirectory()) {
	     delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
	     delFolder(path + "/" + tempList[i]);// 再删除空文件夹
	     flag = true;
	    }
	   }
	   return flag;
	}
    /**
     * 字符串相似度
     * @param str1
     * @param str2
     * @return
     */
	 public static float StringSimilarity(String str1,String str2) {  
	        //计算两个字符串的长度。  
	        int len1 = str1.length();  
	        int len2 = str2.length();  
	        //建立上面说的数组，比字符长度大一个空间  
	        int[][] dif = new int[len1 + 1][len2 + 1];  
	        //赋初值，步骤B。  
	        for (int a = 0; a <= len1; a++) {  
	            dif[a][0] = a;  
	        }  
	        for (int a = 0; a <= len2; a++) {  
	            dif[0][a] = a;  
	        }  
	        //计算两个字符是否一样，计算左上的值  
	        int temp;  
	        for (int i = 1; i <= len1; i++) {  
	            for (int j = 1; j <= len2; j++) {  
	                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {  
	                    temp = 0;  
	                } else {  
	                    temp = 1;  
	                }  
	                //取三个值中最小的  
	                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
	                        dif[i - 1][j] + 1);  
	            }  
	        }  
	        //System.out.println("字符串\""+str1+"\"与\""+str2+"\"的比较");  
	        //取数组右下角的值，同样不同位置代表不同字符串的比较  
	        //System.out.println("差异步骤："+dif[len1][len2]);  
	        //计算相似度  
	        float similarity =1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());  
	        //System.out.println("相似度："+similarity);  
	        return similarity;
	}  
	  
	 //得到最小值  
	 private static int min(int... is) {  
	     int min = Integer.MAX_VALUE;  
	     for (int i : is) {  
	         if (min > i) {  
	             min = i;  
	         }  
	     }  
	     return min;  
	 } 
}
