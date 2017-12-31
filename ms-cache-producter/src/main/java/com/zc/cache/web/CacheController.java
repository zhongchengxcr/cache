package com.zc.cache.web;

import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.dao.db.entity.ShopInfo;
import com.zc.cache.dao.db.mapper.ShopInfoMapper;
import com.zc.cache.service.CacheService;
import com.zc.cache.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/26 21:40
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@RestController
public class CacheController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CacheService cacheService;

    @Resource
    private ProductService productService;

    @GetMapping("/product/load/{productId}")
    public ProductInfo getProduct(@PathVariable Long productId) throws InterruptedException {
        return productService.getProductInfoAndSetIntoRedis(productId);
    }


    @GetMapping("/product/{productId}")
    public ProductInfo getProductInfo(@PathVariable Long productId) {

        logger.info("获取 product,productId : {}", productId);
        ProductInfo productInfo;
        productInfo = cacheService.getProductInfoFromLocalCache(productId);

        logger.info("堆缓存获取 product 结果, product : {}", productInfo);
        if (productInfo == null) {
            productInfo = cacheService.getProductInfoRedisCache(productId);

            logger.info("Redis 缓存获取 product 结果, product : {}", productInfo);
        }

        return productInfo;
    }


    @GetMapping("/shop/{shopId}")
    public ShopInfo getShopInfo(@PathVariable Long shopId) {

        logger.info("获取 product,shopId : {}", shopId);
        ShopInfo shopInfo;
        shopInfo = cacheService.getShopInfoFromLocalCache(shopId);

        logger.info("堆缓存获取 shop 结果, shop : {}", shopInfo);
        if (shopInfo == null) {
            shopInfo = cacheService.getShopInfoRedisCache(shopId);

            logger.info("Redis 缓存获取 shop 结果, shop : {}", shopInfo);
        }

        return shopInfo;
    }

    @GetMapping("/product/cache/local/{productId}")
    public ProductInfo getProductInfoFromLocalCache(@PathVariable Long productId) {
        return cacheService.getProductInfoFromLocalCache(productId);
    }

    @GetMapping("/product/cache/redis/{productId}")
    public ProductInfo getProductInfoRedisCache(@PathVariable Long productId) {
        return cacheService.getProductInfoRedisCache(productId);
    }


    @GetMapping("/shop/cache/local/{shopId}")
    public ShopInfo getShopInfoFromLocalCache(@PathVariable Long shopId) {
        return cacheService.getShopInfoFromLocalCache(shopId);
    }


    @GetMapping("/shop/cache/redis/{shopId}")
    public ShopInfo getShopInfoRedisCache(@PathVariable Long shopId) {
        return cacheService.getShopInfoRedisCache(shopId);
    }


}
