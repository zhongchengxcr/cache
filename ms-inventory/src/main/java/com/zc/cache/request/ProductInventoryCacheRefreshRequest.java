package com.zc.cache.request;

import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.service.ProductInventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 17:51
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ProductInventoryCacheRefreshRequest implements Request {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private Long productId;

    private Boolean force;

    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;


    public ProductInventoryCacheRefreshRequest(Long productId, Boolean force, ProductInventoryService productInventoryService) {
        this.productId = productId;
        this.force = force;
        this.productInventoryService = productInventoryService;
    }

    /**
     * 刷新缓存
     */
    @Override
    public void process() {


        ProductInventory productInventory = productInventoryService.findProductInventory(productId);


        if (productInventory != null) {

            logger.info("===========日志===========: 已查询到商品最新的库存数量，商品id=" + productId + ", 商品库存数量=" + productInventory.getInventoryCnt());

            // 将最新的商品库存数量，刷新到redis缓存中去
              productInventoryService.setProductInventoryCache(productInventory);
        }


    }

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public boolean isForceRefresh() {
        return force;
    }
}
