package com.zc.cache.rebuild;

import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.service.CacheService;
import com.zc.cache.spring.SpringContext;
import com.zc.cache.zk.ZooKeeperSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/04 上午9:49
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class RebuildCacheTask implements Callable<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean running = true;


    @Override
    public String call() throws Exception {
        ZooKeeperSession zkSession = SpringContext.getApplicationContext().getBean(ZooKeeperSession.class);
        CacheService cacheService = SpringContext.getApplicationContext().getBean(CacheService.class);
        while (running) {
            Long productId = null;
            try {
                ProductInfo productInfo = RebuildCacheQueue.take();

                logger.info("当前重建的 ProductInfo = {}", productInfo.toString());

                productId = productInfo.getId();
                zkSession.acquireDistributedLock(productId);
                ProductInfo cacheProductInfo = cacheService.getProductInfoRedisCache(productId);


                if (cacheProductInfo != null) {
                    logger.info("缓存中的 ProductInfo = {}", cacheProductInfo.toString());
                    long productTime = productInfo.getModifiedTime().getTime();
                    long cacheProductTime = cacheProductInfo.getModifiedTime().getTime();

                    logger.info("比较时间版本 productTime={} , cacheProductTime={}", productTime, cacheProductTime);
                    if (productTime <= cacheProductTime) {
                        logger.info("当前过期");
                        continue;
                    }

                } else {
                    logger.info("缓存中的 ProductInfo = null , 直接重建缓存");
                }

                cacheService.saveProductInfoRedisCache(productInfo);
                cacheService.saveProductInfoLocalCache(productInfo);
            } finally {
                zkSession.releaseDistributedLock(productId);
            }

        }


        return null;
    }
}
