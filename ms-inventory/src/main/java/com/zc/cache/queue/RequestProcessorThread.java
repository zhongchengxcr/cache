package com.zc.cache.queue;

import com.zc.cache.request.ProductInventoryCacheRefreshRequest;
import com.zc.cache.request.ProductInventoryDBUpdateRequest;
import com.zc.cache.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 18:18
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class RequestProcessorThread implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(getClass());


    private LinkedBlockingQueue<Request> queue;

    public RequestProcessorThread(LinkedBlockingQueue<Request> queue) {
        this.queue = queue;
    }


    @Override
    public Boolean call() throws Exception {
        try {
            for (; ; ) {
                // ArrayBlockingQueue
                // Blocking就是说明，如果队列满了，或者是空的，那么都会在执行操作的时候，阻塞住
                Request request = queue.take();
                boolean forceRfresh = request.isForceRefresh();

                // 先做读请求的去重
                if (!forceRfresh) {
                    RequestQueue requestQueue = RequestQueue.getInstance();
                    Map<Long, Boolean> flagMap = requestQueue.getFlagMap();

                    if (request instanceof ProductInventoryDBUpdateRequest) {
                        // 如果是一个更新数据库的请求，那么就将那个productId对应的标识设置为true
                        flagMap.put(request.getProductId(), true);
                    } else if (request instanceof ProductInventoryCacheRefreshRequest) {
                        Boolean flag = flagMap.get(request.getProductId());

                        // 如果flag是null
                        if (flag == null) {
                            flagMap.put(request.getProductId(), false);
                        }

                        // 如果是缓存刷新的请求，那么就判断，如果标识不为空，而且是true，就说明之前有一个这个商品的数据库更新请求
                        if (flag != null && flag) {
                            flagMap.put(request.getProductId(), false);
                        }

                        // 如果是缓存刷新的请求，而且发现标识不为空，但是标识是false
                        // 说明前面已经有一个数据库更新请求+一个缓存刷新请求了，大家想一想
                        if (flag != null && !flag) {
                            // 对于这种读请求，直接就过滤掉，不要放到后面的内存队列里面去了
                            logger.info("===========日志===========: 忽略 重复读");
                            continue;
                        }
                    }
                }

                logger.info("===========日志===========: 工作线程处理请求，商品id=" + request.getProductId());
                // 执行这个request操作
                request.process();

                // 假如说，执行完了一个读请求之后，假设数据已经刷新到redis中了
                // 但是后面可能redis中的数据会因为内存满了，被自动清理掉
                // 如果说数据从redis中被自动清理掉了以后
                // 然后后面又来一个读请求，此时如果进来，发现标志位是false，就不会去执行这个刷新的操作了
                // 所以在执行完这个读请求之后，实际上这个标志位是停留在false的
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
