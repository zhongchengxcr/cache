package com.zc.cache.strom;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/31 下午3:52
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class WordCountTopology {


    public static void main(String[] args) {
        // 在main方法中，会去将spout和bolts组合起来，构建成一个拓扑
        TopologyBuilder builder = new TopologyBuilder();
        // 这里的第一个参数的意思，就是给这个spout设置一个名字
        // 第二个参数的意思，就是创建一个spout的对象
        // 第三个参数的意思，就是设置spout的executor有几个
        builder.setSpout("RandomSentence", new RandomSentenceSpout(), 3);


        builder.setBolt("SplitSentence", new SplitSentence(), 3)
                .setNumTasks(2)
                .shuffleGrouping("RandomSentence");

        builder.setBolt("WordCount", new WordCount(), 10)
                .setNumTasks(2)
                .fieldsGrouping("SplitSentence", new Fields("word"));


        Config conf = new Config();

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            try {
                StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            conf.setMaxTaskParallelism(20);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("WordCountTopology", conf, builder.createTopology());
            Utils.sleep(60000);
            cluster.shutdown();
        }


    }

    public static class RandomSentenceSpout extends BaseRichSpout {


        private Logger spoutLogger = LoggerFactory.getLogger(getClass());

        private SpoutOutputCollector collector;

        private Random random;


        /**
         * 做一些数据源初始化操作,例如初始化连接池
         *
         * @param conf                 conf
         * @param topologyContext      topologyContext
         * @param spoutOutputCollector 这个SpoutOutputCollector就是用来发射数据出去的
         */
        @Override
        public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.collector = spoutOutputCollector;
            this.random = new Random();

            spoutLogger.info(" ================  Call open method complete ! ");

        }

        /**
         * nextTuple方法
         * <p>
         * 这个spout类，之前说过，最终会运行在task中，某个worker进程的某个executor线程内部的某个task中
         * 那个task会负责去不断的无限循环调用nextTuple()方法
         * 只要的话呢，无限循环调用，可以不断发射最新的数据出去，形成一个数据流
         */
        @Override
        public void nextTuple() {
            String[] sentences = new String[]{"the cow jumped over the moon", "an apple a day keeps the doctor away",
                    "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature"};
            String sentence = sentences[random.nextInt(sentences.length)];

            spoutLogger.info(" ================  Call  nextTuple complete ! sentence :{}", sentence);
            collector.emit(new Values(sentence));
        }


        /**
         * declareOutputFields
         * 很重要，这个方法是定义一个你发射出去的每个tuple中的每个field的名称是什么
         *
         * @param outputFieldsDeclarer declarer
         */
        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("sentence"));
            spoutLogger.info(" ================  Call declareOutputFields method complete ! ");

        }
    }


    /**
     * 每个bolt代码，同样是发送到worker某个executor的task里面去运行
     */
    public static class SplitSentence extends BaseRichBolt {

        private Logger splitLogger = LoggerFactory.getLogger(getClass());

        private OutputCollector outputCollector;


        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
            splitLogger.info(" ================  Call prepare method complete ! ");
        }

        @Override
        public void execute(Tuple tuple) {
            String line = tuple.getStringByField("sentence");
            String[] words = line.split(" ");
            for (String word : words) {
                outputCollector.emit(new Values(word));
            }

            splitLogger.info(" ================  Call execute method complete ! ");

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
            splitLogger.info(" ================  Call declareOutputFields method complete ! ");
        }
    }


    public static class WordCount extends BaseRichBolt {

        private Logger countLogger = LoggerFactory.getLogger(getClass());

        private OutputCollector outputCollector;

        private Map<String, Integer> wordCount = new HashMap<>();

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
            countLogger.info(" ================  Call prepare method complete ! ");
        }

        @Override
        public void execute(Tuple tuple) {

            String word = tuple.getStringByField("word");
            Integer count = wordCount.get(word);
            if (count != null) {
                count += 1;
                wordCount.put(word, count);
            } else {
                count = 0;
                wordCount.put(word, count);
            }
            countLogger.info(" ================  Call execute method complete ! ");
            countLogger.info("【单词计数】" + word + "出现的次数是" + count);
            outputCollector.emit(new Values(word, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

            outputFieldsDeclarer.declare(new Fields("word", "count"));
            countLogger.info(" ================  Call declare method complete ! ");
        }
    }

}
