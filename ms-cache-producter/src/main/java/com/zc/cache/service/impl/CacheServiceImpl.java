package com.zc.cache.service.impl;

import com.zc.cache.dao.db.entity.ProductInventory;
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

    /**
     * 将商品信息保存到本地缓存中
     *
     * @param productInfo
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
    @Override
    public ProductInventory saveLocalCache(ProductInventory productInfo) {
        return productInfo;
    }

    /**
     * 从本地缓存中获取商品信息
     *
     * @param id
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
    @Override
    public ProductInventory getLocalCache(Long id) {
        return null;
    }
}
