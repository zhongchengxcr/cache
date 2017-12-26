package com.zc.cache.service;

import com.zc.cache.dao.db.entity.ProductInventory;

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
     * 将商品信息保存到本地缓存中
     *
     * @param productInfo
     * @return
     */
    ProductInventory saveLocalCache(ProductInventory productInfo);

    /**
     * 从本地缓存中获取商品信息
     *
     * @param id
     * @return
     */
    ProductInventory getLocalCache(Long id);

}
