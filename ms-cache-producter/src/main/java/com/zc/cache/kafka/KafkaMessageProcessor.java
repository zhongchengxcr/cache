package com.zc.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.dao.db.entity.ShopInfo;
import com.zc.cache.service.CacheService;
import com.zc.cache.spring.SpringContext;
import com.zc.cache.zk.ZooKeeperSession;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka消息处理线程
 *
 * @author Administrator
 */
@SuppressWarnings("rawtypes")
public class KafkaMessageProcessor implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private KafkaStream kafkaStream;
    private CacheService cacheService;
    private ZooKeeperSession zooKeeperSession;

    private static final String ZK_LOCK_PRODUCT_PATH = "/PRODUCT_KEY_%s";

    private static final String ZK_LOCK_SHOP_PATH = "/SHOP_KEY_%s";


    public KafkaMessageProcessor(KafkaStream kafkaStream) {
        this.kafkaStream = kafkaStream;
        this.cacheService = SpringContext.getApplicationContext()
                .getBean(CacheService.class);
        this.zooKeeperSession = SpringContext.getApplicationContext()
                .getBean(ZooKeeperSession.class);
    }

    @SuppressWarnings("all")
    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
        while (it.hasNext()) {

            try {
                String message = new String(it.next().message());

                logger.info("收到消息:{}", message);
                // 首先将message转换成json对象
                JSONObject messageJSONObject = JSONObject.parseObject(message);

                // 从这里提取出消息对应的服务的标识
                String serviceId = messageJSONObject.getString("serviceId");

                // 如果是商品信息服务
                if ("productInfoService".equals(serviceId)) {
                    processProductInfoChangeMessage(messageJSONObject);
                } else if ("shopInfoService".equals(serviceId)) {
                    processShopInfoChangeMessage(messageJSONObject);
                }

            } catch (Exception ignone) {
                ignone.printStackTrace();
            }

        }
    }

    /**
     * 处理商品信息变更的消息
     *
     * @param messageJSONObject messageJSONObject
     */

    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景

        String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1}";


        logger.info("===================获取刚保存到本地缓存的商品信息：" + cacheService.getProductInfoFromLocalCache(productId));

        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        String lockKey = String.format(ZK_LOCK_PRODUCT_PATH, productId);
        try {
            zooKeeperSession.acquireDistributedLock(lockKey);
            cacheService.saveProductInfoLocalCache(productInfo);
            cacheService.saveProductInfoRedisCache(productInfo);
        } finally {
            zooKeeperSession.releaseDistributedLock(lockKey);
        }

    }

    /**
     * 处理店铺信息变更的消息
     *
     * @param messageJSONObject messageJSONObject
     */
    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");
        Long shopId = messageJSONObject.getLong("shopId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景

        String shopInfoJSON = "{\"id\": " + shopId + ", \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";
        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);

        String lockKey = String.format(ZK_LOCK_SHOP_PATH, shopId);

        try {
            zooKeeperSession.acquireDistributedLock(lockKey);
            cacheService.saveShopInfoLocalCache(shopInfo);
            System.out.println("===================获取刚保存到本地缓存的店铺信息：" + cacheService.getShopInfoFromLocalCache(shopId));
            cacheService.saveShopInfoRedisCache(shopInfo);
        } finally {
            zooKeeperSession.releaseDistributedLock(lockKey);
        }
    }

}
