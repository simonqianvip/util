package com.simon.configuration;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 读取src目录下xx.properties文件的工具类
 * @author simon
 *
 */
public class ConfigurationTools {
		private static Log log = LogFactory.getLog(ConfigurationTools.class);
		private static final String PROP = "data.properties";
	    private static Properties p = new Properties();  
	    /** 
	     * 读取properties配置文件信息 
	     */  
	    static{  
	        try {  
	            p.load(ConfigurationTools.class.getClassLoader().getResourceAsStream(PROP));
	            log.info("加载 "+PROP+" 成功！！！");
	        } catch (IOException e) {  
	            e.printStackTrace();   
	        }  
	    }  
	    /** 
	     * 根据key得到value的值 
	     */  
	    public static String getValue(String key)  
	    {  
	        return p.getProperty(key);  
	    }
	    
	    /**
	     * 获取所有属性
	     * @return
	     */
	    public static Properties getProperties(){
	    	return p;
	    }
}
