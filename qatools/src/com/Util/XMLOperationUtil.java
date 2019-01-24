package com.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XMLOperationUtil {
	Logger logger = LoggerFactory.getLogger(XMLOperationUtil.class);
	
	public void XMLChanger(String key,String value){
		SAXReader reader = new SAXReader();
	//	InputStream inputStream = null;
		Document doc = null;
		try {
			 doc = reader.read(System.getProperty("user.dir")+"/Data/Config.xml");
			Element root=doc.getRootElement();
			List<Element> childElements = root.elements();
			  for (Element child : childElements) {
				 if( child.getName().equals(key)){
					 child.setText(value);
				 }
			  }
		//		inputStream= new ByteArrayInputStream(doc.asXML().getBytes("UTF-8"))  ;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
		Write2XML(doc);
	}
	
	public void WritetoXML(InputStream inputStream){
        FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(System.getProperty("user.dir")+"/Data/Config.xml",false);
          //  content=content+"\r\n";
            byte[] b = new byte[1024];
            int len = 0;
            while((len = inputStream.read(b)) != -1)
            {
            	fileOutputStream.write(b,0,len);
            }
            fileOutputStream.close();
            fileOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e); 
		}finally {
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e); 
				}
			}
		}
	}
	
	public void Write2XML(Document doc){
		 try{ 
             /** 将document中的内容写入文件中 */ 
			 OutputFormat   format   =   OutputFormat.createPrettyPrint(); 
             format.setEncoding( "UTF-8"); 
             XMLWriter writer = new XMLWriter(new FileOutputStream(new File(System.getProperty("user.dir")+"/Data/Config.xml")),format); 
             writer.write(doc); 
             writer.flush();
             writer.close(); 
         }catch(Exception ex){ 
             ex.printStackTrace(); 
         } 
	}
	
}
