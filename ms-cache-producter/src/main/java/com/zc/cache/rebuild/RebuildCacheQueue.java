package com.zc.cache.rebuild;

import com.zc.cache.dao.db.entity.ProductInfo;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/04 上午9:46
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class RebuildCacheQueue {

    private static LinkedBlockingQueue<ProductInfo> queue = new LinkedBlockingQueue<>();


    public static void put(ProductInfo productInfo) throws InterruptedException {
        queue.put(productInfo);
    }


    public static ProductInfo take() throws InterruptedException {
        return queue.take();
    }


    public static ProductInfo poll() throws InterruptedException {
        return queue.poll();
    }

}
