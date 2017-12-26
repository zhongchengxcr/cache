package com.zc.cache.web;

import com.zc.cache.dao.db.entity.ProductInventory;
import com.zc.cache.service.CacheService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/26 21:40
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@RestController
public class CacheController {

    @Resource
    private CacheService cacheService;

    @PutMapping("/testPutCache")
    @ResponseBody
    public String testPutCache(@RequestBody ProductInventory productInventory) {
        cacheService.saveLocalCache(productInventory);
        return "success";
    }

    @GetMapping("/testGetCache/{id}")
    @ResponseBody
    public ProductInventory testGetCache(@PathVariable Long id) {
        return cacheService.getLocalCache(id);
    }
}
