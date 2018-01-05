package com.zc.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.service.CacheService;
import com.zc.cache.spring.SpringContext;
import com.zc.cache.zk.ZooKeeperSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/04 上午10:33
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class CachePrewarmThread implements Callable<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private CacheService cacheService = SpringContext.getApplicationContext().getBean(CacheService.class);


    /**
     * 修改预热状态需要获取的锁
     */
    private static final String ZK_TASK_STATUS_LOCK_PATH_TEMPLATE = "/task-id-status-lock-%s";

    /**
     * 保存每个 节点 预热 缓存 的 状态
     */
    private static final String ZK_TASK_STATUS_PATH_TEMPLATE = "/task-id-status-%s";

    /**
     * storm实时计算的热数据的zk路径,根据storm 中的task分段存储,每个task计算yipi
     */
    private static final String ZK_TASK_HOT_PRODUCT_LIST_PATH_TEMPLATE = "/task-hot-product-list-%s";

    /**
     * task 处理锁,在分布式环境下,保证同一个task的预热缓存只被同一个 节点处理
     */
    private static final String ZK_TASK_LOCK_PATH_TEMPLATE = "/task-id-lock-%s";

    @Override
    public String call() throws Exception {
        ZooKeeperSession zkSession = SpringContext.getApplicationContext().getBean(ZooKeeperSession.class);

        String taskIdListStr = zkSession.getNodeData("/task-id-list");

        if (StringUtils.isEmpty(taskIdListStr)) {
            return "zk task id list is empty!";
        }

        String[] taskIdArr = taskIdListStr.split(",");

        for (String taskId : taskIdArr) {

            String taskIdLockPath = String.format(ZK_TASK_LOCK_PATH_TEMPLATE, taskId);
            boolean isLock = zkSession.acquireFastFailedDistributedLock(taskIdLockPath);

            if (!isLock) {
                continue;
            }

            String zkTaskStatusLockPath = String.format(ZK_TASK_STATUS_LOCK_PATH_TEMPLATE, taskId);

            //TODO 为什么还要获取  task-id-status-lock-task-id 分布式锁 ,感觉没必要
            zkSession.acquireDistributedLock(zkTaskStatusLockPath);


            String zkTaskStatusPath = String.format(ZK_TASK_STATUS_PATH_TEMPLATE, taskId);

            String taskIdStatus = zkSession.getNodeData(zkTaskStatusPath);
            logger.info("【CachePrewarmThread获取task的预热状态】taskId=" + taskId + ", status=" + taskIdStatus);

            if (!"success".equals(taskIdStatus)) {
                String productIdList = zkSession.getNodeData(String.format(ZK_TASK_HOT_PRODUCT_LIST_PATH_TEMPLATE, taskId));
                logger.info("【CachePrewarmThread获取到task的热门商品列表】productIdList=" + productIdList);
                JSONArray productIdJSONArray = JSONArray.parseArray(productIdList);

                for (int i = 0; i < productIdJSONArray.size(); i++) {
                    Long productId = productIdJSONArray.getLong(i);
                    String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                    ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                    cacheService.saveProductInfoLocalCache(productInfo);
                    System.out.println("【CachePrwarmThread将商品数据设置到本地缓存中】productInfo=" + productInfo);
                    cacheService.saveProductInfoRedisCache(productInfo);
                    System.out.println("【CachePrwarmThread将商品数据设置到redis缓存中】productInfo=" + productInfo);
                }

                zkSession.createNode(zkTaskStatusPath);
                zkSession.setNodeData(zkTaskStatusPath, "success");
            }

            zkSession.releaseDistributedLock(zkTaskStatusLockPath);
            zkSession.releaseDistributedLock(taskIdLockPath);

        }


        return null;
    }
}
