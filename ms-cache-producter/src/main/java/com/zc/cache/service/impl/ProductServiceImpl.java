package com.zc.cache.service.impl;

import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.dao.db.mapper.ProductInfoMapper;
import com.zc.cache.dao.db.mapper.ShopInfoMapper;
import com.zc.cache.service.CacheService;
import com.zc.cache.service.ProductService;
import com.zc.cache.zk.ZooKeeperSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/31 上午1:49
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@Service
public class ProductServiceImpl implements ProductService {


    @Resource
    private ZooKeeperSession zooKeeperSession;

    @SuppressWarnings("all")
    @Resource
    private ProductInfoMapper productInfoMapper;

    @Resource
    private CacheService cacheService;

    private static final String ZK_LOCK_PATH = "/PRODUCT_KEY_%s";

    @Override
    public ProductInfo getProductInfoFromDb(Long productId) {
        return productInfoMapper.selectById(productId);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ProductInfo getProductInfoAndSetIntoRedis(Long productId) throws InterruptedException {



        logger.info("getProductInfoFromDb ");
        ProductInfo productInfo = getProductInfoFromDb(productId);


        if (productInfo != null) {

            logger.info("getProductInfoFromDb:{}", productInfo.toString());
            String key = String.format(ZK_LOCK_PATH, productId);
            try {
                zooKeeperSession.acquireDistributedLock(key);

                ProductInfo productInfoCache = cacheService.getProductInfoRedisCache(productId);

                if (productInfoCache != null) {
                    logger.info("判断时间========");
                }


                //Thread.sleep(80000);
                cacheService.saveProductInfoLocalCache(productInfo);
                cacheService.saveProductInfoRedisCache(productInfo);
            }finally {
                zooKeeperSession.releaseDistributedLock(key);

            }


        }

        return productInfo;
    }

}
