package com.zxz.redis;

import java.util.Map;

import org.apache.log4j.Logger;

import com.zxz.controller.StartServer;
import com.zxz.utils.Constant;

import redis.clients.jedis.Jedis;

public class RedisUtil {

	
	private static Logger logger = Logger.getLogger(StartServer.class);
	
	
	public static void main(String[] args) {
		Map<String, String> hashMap = getHashMap("uid"+10021, Constant.REDIS_DB_FENGTIAN);
		System.out.println(hashMap);
	}
	
	/*
	 * @param key
	 * @param db 分区
	 * @return
	 */
	public static String getKey(String key,int db){
		String result = null;
		Jedis jedis = MyJedisPool.getJedis();
		try {
			jedis.select(db);
			result = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e);
		} finally {
			MyJedisPool.returnResource(jedis);
		}
		return result;
	}
	
	

	/**从key得到value
	 * @param key
	 * @return
	 */
	public static String setKey(String key,String value,int db){
		String result = null;
		Jedis jedis = MyJedisPool.getJedis();
		try {
			jedis.select(db);
			result = jedis.set(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e);
		}finally {
			MyJedisPool.returnResource(jedis);
		}
		return result;
	}
	
	
	
	
	
	/**从key得到value
	 * @param key
	 * @return
	 */
	public static Long delKey(String key,int db){
		Long del = null;
		Jedis jedis = MyJedisPool.getJedis();
		try {
			jedis.select(db);
			del = jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e);
		}finally{
			MyJedisPool.returnResource(jedis);
		}
		return del;
	}
	
	
	
	/**得到一个jedis
	 * @param key
	 * @return
	 */
	public static Jedis getOneRedis(){
		try {
			Jedis jedis = MyJedisPool.getJedis();
			return jedis;
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e);
		}
		return null;
	}
	
	
	

	/**选择分区
	 * @param key
	 * @param db
	 * @return
	 */
	public static Map<String,String> getHashMap(String key,int db){
		Map<String,String> map;
		Jedis oneRedis = getOneRedis();
		try {
			oneRedis.select(db);
			map = oneRedis.hgetAll(key);
		} finally {
			MyJedisPool.returnResource(oneRedis);
		}
		return map;
	}
	
	
	
	public static String setHashMap(String key,Map<String , String> hashMap,int db,int second){
		Jedis oneRedis = getOneRedis();
		String hmset;
		try {
			oneRedis.select(db);
			hmset = oneRedis.hmset(key, hashMap);
			oneRedis.expire(key, second);
		} finally {
			MyJedisPool.returnResource(oneRedis);
		}
		return hmset;
	}
}