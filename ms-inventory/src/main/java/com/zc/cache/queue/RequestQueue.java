package com.zc.cache.queue;

import com.zc.cache.request.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 17:44
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class RequestQueue {


    private int queueSize = 10;

    private ExecutorService threadPool = Executors.newFixedThreadPool(queueSize);

    private RequestQueue() {
        RequestQueue requestQueue = RequestQueue.getInstance();
        for (int i = 0; i < queueSize; i++) {
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<>(100);
            requestQueue.addQueue(queue);
            threadPool.submit(new RequestProcessorThread(queue));
        }
    }

    private Map<Long, Boolean> flagMap = new ConcurrentHashMap<>();

    private static volatile RequestQueue requestQueue;

    public static RequestQueue getInstance() {
        if (requestQueue == null) {

            synchronized (RequestQueue.class) {
                if (requestQueue == null) {
                    return new RequestQueue();
                } else {
                    return requestQueue;
                }
            }
        } else {
            return requestQueue;
        }
    }


    private List<ArrayBlockingQueue> queueList = new ArrayList<>();


    /**
     * 添加一个内存队列
     *
     * @param queue
     */
    public void addQueue(ArrayBlockingQueue<Request> queue) {
        this.queueList.add(queue);
    }

    /**
     * 获取内存队列的数量
     *
     * @return
     */
    public int queueSize() {
        return queueList.size();
    }

    /**
     * 获取内存队列
     *
     * @param index
     * @return
     */
    public ArrayBlockingQueue<Request> getQueue(int index) {
        return queueList.get(index);
    }

    public Map<Long, Boolean> getFlagMap() {
        return flagMap;
    }


}
