package com.zc.cache.strom.preheat;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/03 下午12:43
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ProductKafkaSpout extends BaseRichSpout {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SpoutOutputCollector spoutOutputCollector;

    private LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    private ExecutorService kafkaExecutor;


    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector = spoutOutputCollector;
        kafkaExecutor = Executors.newFixedThreadPool(3);
        startKafkaConsumer();
    }

    @Override
    public void nextTuple() {

        try {
            String message = blockingQueue.take();
            spoutOutputCollector.emit(new Values(message));
            logger.info("【AccessLogKafkaSpout发射出去一条日志】message={}", message);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        outputFieldsDeclarer.declare(new Fields("message"));

    }


    @SuppressWarnings("rawtypes")
    private void startKafkaConsumer() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.0.105:2181,192.168.0.106:2181,192.168.0.107:2181");
        props.put("group.id", "eshop-cache-group");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        ConsumerConfig consumerConfig = new ConsumerConfig(props);

        ConsumerConnector consumerConnector = Consumer.
                createJavaConsumerConnector(consumerConfig);
        String topic = "access-log";

        Map<String, Integer> topicCountMap = new HashMap<>(3);
        topicCountMap.put(topic, 1);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        for (KafkaStream stream : streams) {
            kafkaExecutor.submit(new KafkaMessageProcessor(stream));
        }
    }

    private class KafkaMessageProcessor implements Callable<Object>, Serializable {

        private KafkaStream kafkaStream;

        public KafkaMessageProcessor(KafkaStream kafkaStream) {
            this.kafkaStream = kafkaStream;
        }

        @SuppressWarnings("all")
        @Override
        public Object call() throws Exception {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                String message = new String(it.next().message());
                logger.info("AccessLogKafkaSpout中的Kafka消费者接收到一条日志】message={}", message);
                blockingQueue.put(message);
            }
            return null;
        }
    }
}
