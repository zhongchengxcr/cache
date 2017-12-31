package com.zc.cache.kafka;

import com.zc.cache.spring.SpringContext;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/30 下午8:44
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class KafkaConsumer implements Callable<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public KafkaConsumer(String topic, ExecutorService executorService) {
        this.consumerConnector = Consumer.createJavaConsumerConnector(
                createConsumerConfig());
        this.topic = topic;
        this.executorService = executorService;
    }

    private ConsumerConnector consumerConnector;
    private String topic;
    private ExecutorService executorService;


    private volatile boolean running = true;

    @Override
    public String call() throws Exception {


        Map<String, Integer> topicCountMap = new HashMap<>(3);
        topicCountMap.put(topic, 1);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumerConnector.createMessageStreams(topicCountMap);


        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        for (KafkaStream stream : streams) {
            executorService.submit(new KafkaMessageProcessor(stream));
        }

        return null;
    }


    /**
     * 创建kafka consumer config
     *
     * @return ConsumerConfig
     */
    private ConsumerConfig createConsumerConfig() {

        KafkaConfProperties conf = SpringContext.getApplicationContext().getBean(KafkaConfProperties.class);
        Properties props = new Properties();
        props.put("zookeeper.connect", conf.getZk());
        props.put("group.id", conf.getGroup());
        props.put("zookeeper.session.timeout.ms", conf.getZkSessionTimeout());
        props.put("zookeeper.sync.time.ms", conf.getZkSyncTime());
        props.put("auto.commit.interval.ms", conf.getAutoCommitInterval());
        return new ConsumerConfig(props);
    }
}
