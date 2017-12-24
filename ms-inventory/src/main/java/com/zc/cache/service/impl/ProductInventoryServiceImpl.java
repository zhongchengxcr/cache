package com.zc.cache.service.impl;

import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.dao.db.mapper.ProductInventoryMapper;
import com.zc.cache.dao.redis.mapper.RedisDAO;
import com.zc.cache.service.ProductInventoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 18:03
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private static final String productInventoryKeyTemplate = "product:inventory:%s";


    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisDAO redisDAO;

    @SuppressWarnings("all")
    @Autowired
    private ProductInventoryMapper productInventoryMapper;


    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        productInventoryMapper.updateById(productInventory);
        logger.info("===========日志===========: 已修改数据库中的库存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());


    }

    @Override
    public void removeProductInventoryCache(ProductInventory productInventory) {
        String productInventoryId = productInventory.getProductId().toString();
        String productInventoryKey = String.format(productInventoryKeyTemplate, productInventoryId);
        redisDAO.delete(productInventoryKey);
        logger.info("===========日志===========: 已删除redis中的缓存，key=" + productInventoryKey);


    }

    @Override
    public ProductInventory findProductInventory(Long productId) {
        return productInventoryMapper.selectById(productId);

    }

    @Override
    public void setProductInventoryCache(ProductInventory productInventory) {
        String productInventoryId = productInventory.getProductId().toString();
        String productInventoryKey = String.format(productInventoryKeyTemplate, productInventoryId);
        redisDAO.set(productInventoryKey, productInventory.getInventoryCnt().toString());
        logger.info("===========日志===========: 已更新商品库存的缓存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt() + ", key=" + productInventoryKey);

    }

    @Override
    public ProductInventory getProductInventoryCache(Long productId) {
        String productInventoryId = productId.toString();
        String productInventoryKey = String.format(productInventoryKeyTemplate, productInventoryId);
        String productInventCntStr = redisDAO.get(productInventoryKey);

        if (!StringUtils.isEmpty(productInventCntStr)) {
            Long inventoryCnt = Long.valueOf(productInventCntStr);
            ProductInventory productInventory = new ProductInventory();
            productInventory.setProductId(productId);
            productInventory.setInventoryCnt(inventoryCnt);
            return productInventory;
        }
        return null;
    }
}
