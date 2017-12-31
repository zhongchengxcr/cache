package com.zc.cache.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/30 下午8:47
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@PropertySource({"classpath:kafka.properties"})
@Configuration
public class KafkaConfProperties {

    @Value("${kafka.zk}")
    private String zk;

    @Value("${kafka.group}")
    private String group;

    @Value("${kafka.zk.session.timeout}")
    private String zkSessionTimeout;

    @Value("${kafka.zk.sync.time}")
    private String zkSyncTime;

    @Value("${kafka.auto.commit.interval}")
    private String autoCommitInterval;

    public String getZk() {
        return zk;
    }

    public KafkaConfProperties setZk(String zk) {
        this.zk = zk;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public KafkaConfProperties setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public KafkaConfProperties setZkSessionTimeout(String zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
        return this;
    }

    public String getZkSyncTime() {
        return zkSyncTime;
    }

    public KafkaConfProperties setZkSyncTime(String zkSyncTime) {
        this.zkSyncTime = zkSyncTime;
        return this;
    }

    public String getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public KafkaConfProperties setAutoCommitInterval(String autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
        return this;
    }

}
