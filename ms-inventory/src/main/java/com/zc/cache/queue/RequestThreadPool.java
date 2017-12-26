package com.zc.cache.queue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zc.cache.request.Request;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 下午7:40
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@Component
public class RequestThreadPool {

    private static int queueSize = 10;

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("queue-thread-pool-%s").build();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(queueSize, threadFactory);


    @PostConstruct
    public void init() {
        RequestQueue requestQueue = RequestQueue.getInstance();
        for (int i = 0; i < queueSize; i++) {
            LinkedBlockingQueue<Request> queue = new LinkedBlockingQueue<>(100);
            requestQueue.addQueue(queue);
            threadPool.submit(new RequestProcessorThread(queue));
        }
    }

}
