package com.zc.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.dao.db.entity.ShopInfo;
import com.zc.cache.dao.redis.mapper.RedisDAO;
import com.zc.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/26 21:35
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@Service
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";


    @CachePut(value = CACHE_NAME, key = "'product_info_'+#productInfo.getId()")
    @Override
    public ProductInfo saveProductInfoLocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
    @Override
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    @CachePut(value = CACHE_NAME, key = "'shop_info_'+#shopInfo.getId()")
    @Override
    public ShopInfo saveShopInfoLocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
    @Override
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }


    @Autowired
    private RedisDAO redisDAO;


    @Override
    public void saveProductInfoRedisCache(ProductInfo productInfo) {

        String key = "product_info_" + productInfo.getId();
        redisDAO.set(key, JSONObject.toJSONString(productInfo));

    }

    @Override
    public ProductInfo getProductInfoRedisCache(Long productInfo) {
        String jsonStr = redisDAO.get("product_info_" + productInfo);
        return JSONObject.parseObject(jsonStr, ProductInfo.class);
    }

    @Override
    public void saveShopInfoRedisCache(ShopInfo shopInfo) {
        String key = "shop_info_" + shopInfo.getId();
        redisDAO.set(key, JSONObject.toJSONString(shopInfo));
    }

    @Override
    public ShopInfo getShopInfoRedisCache(Long shopId) {
        String jsonStr = redisDAO.get("shop_info_" + shopId);
        return JSONObject.parseObject(jsonStr, ShopInfo.class);
    }
}
