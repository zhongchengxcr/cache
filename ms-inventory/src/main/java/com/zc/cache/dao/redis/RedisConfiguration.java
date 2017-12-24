package com.zc.cache.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 上午11:26
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@Configuration
public class RedisConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisCluster JedisClusterFactory() {

        String[] nodeArr = redisProperties.getClusterNodes().split(",");
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();

        for (String node : nodeArr) {
            String[] ipPortPair = node.split(":");
            jedisClusterNodes.add(new HostAndPort(ipPortPair[0], Integer.valueOf(ipPortPair[1])));
        }

        return new JedisCluster(jedisClusterNodes);
    }
}
