package com.zc.cache.request;

import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.service.ProductInventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 17:52
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ProductInventoryDBUpdateRequest implements Request {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 商品库存
     */
    private ProductInventory productInventory;
    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory productInventory, ProductInventoryService productInventoryService) {
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void process() {
        logger.info("===========日志===========: 数据库更新请求开始执行，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());
        productInventoryService.removeProductInventoryCache(productInventory);
        productInventoryService.updateProductInventory(productInventory);
    }

    @Override
    public Long getProductId() {
        return productInventory.getProductId();
    }

    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
