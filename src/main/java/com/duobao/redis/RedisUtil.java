package com.duobao.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisUtil {

    static  Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private static Jedis jedis = null;
    static {
        try {
            jedis = new Jedis("localhost");
        } catch (Exception e) {
            logger.info("连接失败",e);
        }
        logger.info("连接成功");
        jedis.set("runoobkey", "www.runoob.com");
        // 获取存储的数据并输出
        logger.info("redis 存储的字符串为: "+ jedis.get("runoobkey"));
    }

    public void set(String key,String value) {
        try {
            jedis.set(key,value);
        } catch (Exception e) {
            logger.info("redis添加缓存失败");
        }
    }

    public String get(String key) {
        String value=null;
        try {
             value = (String) jedis.get(key);
        } catch (Exception e) {
            logger.info("redis获取缓存失败",e);
        }
        return value;
    }
    public Long setKeyExpire(String key,int hour) {
        int seconds=hour*60*60;
        Long expire = jedis.expire(key, seconds);
        logger.info("redis设置过期时间成功,key={},expire={}",key,expire);
        return expire;
    }
}
