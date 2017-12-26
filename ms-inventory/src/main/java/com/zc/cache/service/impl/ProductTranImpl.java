package com.zc.cache.service.impl;

import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.dao.db.mapper.ProductInventoryMapper;
import com.zc.cache.service.ProductTran;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 下午10:20
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@SuppressWarnings("all")
@Service
public class ProductTranImpl implements ProductTran {

    private TransactionTemplate transactionTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ProductInventoryMapper productInventoryMapper;


    /**
     * mybatis plus 并没有默认加事物
     *
     * @param productId 商品id
     * @return
     */
    @Override
    public ProductInventory findProductInventory(Long productId) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(8);
        ProductInventory productInventory=   transactionTemplate.execute(new TransactionCallback<ProductInventory>() {

            @Override
            public ProductInventory doInTransaction(TransactionStatus status) {
                System.out.println("开始执行 findProductInventory");
                ProductInventory productInventory = productInventoryMapper.selectById(productId);
                System.out.println("开始执行 findProductInventory===================");
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("结束执行 findProductInventory");
                return productInventory;
            }
        });


        return productInventory;

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        System.out.println("事物开始");

        //ProductInventory productInventory1 = productInventoryMapper.selectById(productInventory.getProductId());


        productInventoryMapper.updateById(productInventory);
        System.out.println("事物开始========");

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("事物结束");
    }
}
