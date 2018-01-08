package com.zc.cache.strom.preheat;

import com.alibaba.fastjson.JSON;
import com.zc.cache.strom.util.ZooKeeperSession;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.trident.util.LRUMap;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/03 下午12:45
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ProductCountBolt extends BaseRichBolt {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private LRUMap<Long, Long> productCountMap = new LRUMap<>(1000);

    private Long productCountTime = 5000L;

    private static final int TOP_N = 3;

    private ZooKeeperSession zkSession;

    private int taskId;

    private ScheduledExecutorService scheduledExecutorService;

    private static final String HOT_POINT_PRODUCY_NOTIC_URL = "http://192.168.0.107/hot_product?productId=%s&method=%s";

    private static OkHttpClient okHttpClient;


    private Comparator<Map.Entry<Long, Long>> PRODUCT_COUNT_COMPARATOR;

    public ProductCountBolt() throws IOException, InterruptedException {
    }


    private void initTaskId(int taskId) {
        // ProductCountBolt所有的task启动的时候， 都会将自己的taskid写到同一个node的值中
        // 格式就是逗号分隔，拼接成一个列表
        // 111,211,355
        zkSession.acquireDistributedLock();

        zkSession.createNode("/task-id-list");
        String taskIdList = zkSession.getNodeData("/task-id-list");
        logger.info("【ProductCountBolt获取到taskId list】taskIdList=" + taskIdList);
        if (!"".equals(taskIdList)) {
            taskIdList += "," + taskId;
        } else {
            taskIdList += taskId;
        }
        zkSession.setNodeData("/task-id-list", taskIdList);
        logger.info("【ProductCountBolt设置taskId list】taskIdList=" + taskIdList);

        zkSession.releaseDistributedLock();
    }


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.taskId = topologyContext.getThisTaskId();
        PRODUCT_COUNT_COMPARATOR = new Comparator<Map.Entry<Long, Long>>() {
            @Override
            public int compare(Map.Entry<Long, Long> o1, Map.Entry<Long, Long> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        };
        try {
            zkSession = ZooKeeperSession.getInstance();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new GzipRequestInterceptor())
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        scheduledExecutorService.scheduleWithFixedDelay(new ProductCountProcess(), 5, 5, TimeUnit.MINUTES);
        scheduledExecutorService.scheduleWithFixedDelay(new HotPointProductDiscover(), 5, 5, TimeUnit.SECONDS);

        initTaskId(taskId);

        logger.info("init compleete");
    }

    @Override
    public void execute(Tuple tuple) {
        Long productId = tuple.getLongByField("productId");
        logger.info("【ProductCountBolt接收到一个商品id】 productId={}", productId);
        Long count = productCountMap.get(productId);
        if (count == null) {
            count = 0L;
        }
        count += 1;
        productCountMap.put(productId, count);
        logger.info("【ProductCountBolt完成商品访问次数统计】productId={} , count={} ", productId, count);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }


    private class HotPointProductDiscover implements Runnable {
        @Override
        public void run() {

        }
    }


    private class ProductCountProcess implements Runnable {

        @Override
        public void run() {

            try {
                logger.info("N==================================");

                if (productCountMap.size() == 0) {
                    Utils.sleep(100);
                    return;
                }

                Set<Map.Entry<Long, Long>> productMapEntrySet = productCountMap.entrySet();

                if (productMapEntrySet.size() < TOP_N) {
                    logger.info("LRUMAP size :{}", productMapEntrySet.size());
                }

                List<Map.Entry<Long, Long>> topNProductMapEntryList = top(productMapEntrySet, TOP_N, PRODUCT_COUNT_COMPARATOR);


                List<Long> topNList = new ArrayList<>(TOP_N);

                for (Map.Entry<Long, Long> entry : topNProductMapEntryList) {
                    topNList.add(entry.getKey());
                }

                String jsonStr = JSON.toJSONString(topNList);

                zkSession.createNode("/task-hot-product-list-" + taskId);
                zkSession.setNodeData("/task-hot-product-list-" + taskId, jsonStr);
                logger.info("【ProductCountThread计算出一份top3热门商品列表】zk path=" + ("/task-hot-product-list-" + taskId) + ", topNProductListJSON=" + jsonStr);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void noticHotPointProduct(Long productId, String method) {
            String url = String.format(HOT_POINT_PRODUCY_NOTIC_URL, productId, method);
            Request request = new Request.Builder()
                    //尽可能服用底层TCP连接
                    .addHeader("Connection", "keep-alive")
                    .url(url)
                    .get()
                    .build();

            Call call = okHttpClient.newCall(request);
            try {
                call.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private <E> List<E> top(Collection<E> collection, int n, Comparator<E> comparator) {

            Object[] arr = new Object[n];
            Object[] src = collection.toArray();

            Object temp;
            for (int i = 0; i < n; i++) {
                for (int j = i; j < src.length; j++) {
                    if (comparator.compare((E) src[j], (E) src[i]) > 0) {
                        temp = src[i];
                        src[i] = src[j];
                        src[j] = temp;
                    }
                }
                arr[i] = src[i];
            }
            return (List<E>) Arrays.asList(arr);
        }

    }
}
