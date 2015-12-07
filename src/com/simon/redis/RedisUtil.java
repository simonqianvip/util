package com.simon.redis;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

public class RedisUtil {
	private static Log log = LogFactory.getLog(RedisUtil.class);
	private static Jedis jedis;
	/*
	 * 正则规则
	 */
	private static final String REGEX ="[0-9]*";
	/**
	 * 显示库里总数
	 * @param jedis
	 */
	public static void showResult(Jedis jedis) {
		Long size = jedis.dbSize();
		log.info("【key的总数】：" + size);
		if(size>=1){
			log.info("【显示redis库里的所有key】");
			Set<String> keys = jedis.keys("*");
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				matcher(key,REGEX);
			}
		}
	}
	/**
	 * 返回匹配的字符串，否则返回空
	 * [0-9]*
	 * @param key
	 */
	public static String matcher(String key,String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher isNum = pattern.matcher(key);
		if(isNum.matches()) {
			log.info(key);
			return key;
		}
		return "";
	}
	/**
	 * 根据键查找对应的map
	 * @param key
	 */
	private static void showMap(String key) {
		Map<String, String> map = jedis.hgetAll(key);
		for (Entry<String, String> entry : map.entrySet()) {
			log.info("{" + entry.getKey() + " = "
					+ entry.getValue()+" }");
		}
	}

}
