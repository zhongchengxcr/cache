package com.zc.cache.dao.redis.mapper.impl;

import com.zc.cache.dao.redis.mapper.RedisDAO;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;


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

    @Override
    public void delete(String key) {
        jedisCluster.del(key);
    }

}
