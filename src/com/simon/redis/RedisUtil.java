package com.simon.redis;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	private static Log log = LogFactory.getLog(RedisUtil.class);
	private static String ADDR = "172.16.0.147";
	private static int PORT = 6379;
	private static int MAX_ACTIVE = 1024;
	private static int MAX_IDLE = 200;
	private static int MAX_WAIT = 10000;
	private static int TIMEOUT = 10000;
	private static boolean TEST_ON_BORROW = true;
	private static JedisPool jedisPool = null;
	/**
	 * redis init
	 */
	static {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
			log.info("【创建jedis连接池】");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * get redis connectionPool
	 * 
	 * @return
	 */
	public synchronized static Jedis getJedis() {
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				log.info("【获取jedis实例】");
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * close redis connection
	 * 
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
			log.info("【释放jedis实例】");
		}
	}
	public static void main(String[] args) {
		RedisUtil.getJedis();
	}

}
