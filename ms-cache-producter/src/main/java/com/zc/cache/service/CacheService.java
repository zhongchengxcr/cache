package com.zc.cache.service;

import com.zc.cache.dao.db.entity.ProductInfo;
import com.zc.cache.dao.db.entity.ShopInfo;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/26 21:33
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public interface CacheService {

    /**
     * 将商品信息保存到本地的ehcache缓存中
     *
     * @param productInfo productInfo
     */
    ProductInfo saveProductInfoLocalCache(ProductInfo productInfo);

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId productId
     * @return ProductInfo
     */
    ProductInfo getProductInfoFromLocalCache(Long productId);

    /**
     * 将店铺信息保存到本地的ehcache缓存中
     *
     * @param shopInfo shopInfo
     */
    ShopInfo saveShopInfoLocalCache(ShopInfo shopInfo);

    /**
     * 从本地ehcache缓存中获取店铺信息
     *
     * @param shopId
     * @return ShopInfo
     */
    ShopInfo getShopInfoFromLocalCache(Long shopId);

    /**
     * 将商品信息保存到redis中
     *
     * @param productInfo productInfo
     */
    void saveProductInfoRedisCache(ProductInfo productInfo);

    ProductInfo getProductInfoRedisCache(Long productInfo);

    /**
     * 将店铺信息保存到redis中
     *
     * @param shopInfo shopInfo
     */
    void saveShopInfoRedisCache(ShopInfo shopInfo);

    ShopInfo getShopInfoRedisCache(Long shopId);

}
