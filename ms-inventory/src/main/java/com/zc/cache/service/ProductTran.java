package com.zc.cache.service;

import com.zc.cache.dao.db.entity.ProductInventory;

public interface ProductTran {


    /**
     * 根据商品id查询商品库存
     *
     * @param productId 商品id
     * @return 商品库存
     */
    ProductInventory findProductInventory(Long productId);

    /**
     * 更新商品库存
     *
     * @param productInventory 商品库存
     */
    void updateProductInventory(ProductInventory productInventory);

}
