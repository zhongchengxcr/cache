package com.zc.cache.web.controller;

import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.request.ProductInventoryDBUpdateRequest;
import com.zc.cache.request.Request;
import com.zc.cache.service.ProductTran;
import com.zc.cache.web.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 下午10:26
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@RestController
public class ProductTranController {

    @Autowired
    private ProductTran productTran;


    /**
     * 获取商品库存
     */
    @GetMapping("/getProductInventory/{productId}")
    public ProductInventory getProductInventory(@PathVariable Long productId) {
        return productTran.findProductInventory(productId);
    }

    /**
     * 更新商品库存
     */
    @PostMapping("/updateProductInventory")
    public Response updateProductInventory(@RequestBody ProductInventory productInventory) {

        productTran.updateProductInventory(productInventory);

        return new Response(Response.SUCCESS);
    }


}

