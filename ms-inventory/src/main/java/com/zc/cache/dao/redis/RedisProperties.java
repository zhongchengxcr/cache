package com.zc.cache.dao.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 上午11:30
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "redis.cache")
@Component
public class RedisProperties {

    private int timeout;
    private String clusterNodes;

    public int getTimeout() {
        return timeout;
    }

    public RedisProperties setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public RedisProperties setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
        return this;
    }
}
