package com.zc.cache.dao.redis.mapper.impl;

import javax.annotation.Resource;

import com.zc.cache.dao.redis.mapper.RedisDAO;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisCluster;


@Repository("redisDAO")
public class RedisDAOImpl implements RedisDAO {

    @Resource
    private JedisCluster jedisCluster;

    @Override
    public void set(String key, String value) {
        jedisCluster.set(key, value);
    }

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

}
