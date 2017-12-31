package com.zc.cache.service;

import com.zc.cache.dao.db.entity.ProductInfo;

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
public interface ProductService {

    ProductInfo getProductInfoFromDb(Long productId);


    ProductInfo getProductInfoAndSetIntoRedis(Long productId) throws InterruptedException;

}
