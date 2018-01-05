package com.zc.cache.strom.preheat;

import com.zc.cache.strom.WordCountTopology;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import java.io.IOException;
import java.io.Serializable;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/03 下午12:42
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ProductPreheatTopology implements Serializable {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 在main方法中，会去将spout和bolts组合起来，构建成一个拓扑
        TopologyBuilder builder = new TopologyBuilder();
        // 这里的第一个参数的意思，就是给这个spout设置一个名字
        // 第二个参数的意思，就是创建一个spout的对象
        // 第三个参数的意思，就是设置spout的executor有几个
        builder.setSpout("ProductKafkaSpout", new ProductKafkaSpout(), 1);

        builder.setBolt("ProductLogParseBolt", new ProductLogParseBolt(), 3)
                .setNumTasks(2)
                .shuffleGrouping("ProductKafkaSpout");

        builder.setBolt("ProductCountBolt", new ProductCountBolt(), 3)
                .setNumTasks(2)
                .fieldsGrouping("ProductLogParseBolt", new Fields("productId"));


        Config conf = new Config();

        if (args != null && args.length > 0) {
            conf.setNumWorkers(1);
            try {
                StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            conf.setMaxTaskParallelism(20);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("ProductPreheatTopology", conf, builder.createTopology());
            Utils.sleep(60000);
            cluster.shutdown();
        }
    }
}
