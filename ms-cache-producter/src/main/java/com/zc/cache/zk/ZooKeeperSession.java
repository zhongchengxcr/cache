package com.zc.cache.zk;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/31 上午1:37
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@Component
public class ZooKeeperSession {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zookeeper;


    public ZooKeeperSession() {
        try {
            this.zookeeper = new ZooKeeper(
                    "192.168.0.105:2181,192.168.0.106:2181,192.168.0.107:2181",
                    50000,
                    new ZooKeeperWatcher());
            // 给一个状态CONNECTING，连接中
            System.out.println(zookeeper.getState());

            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("ZooKeeper session established......");

    }


    public void acquireDistributedLock(String key) throws InterruptedException {

        try {
            zookeeper.create(key, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            logger.info("success to acquire lock for product[id= " + key + " ] ");

        } catch (KeeperException e) {
            e.printStackTrace();
            int count = 0;
            while (true) {
                //100ms
                Thread.sleep(1000);

                try {
                    zookeeper.create(key, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (KeeperException e1) {
                    count++;
                    logger.info("the " + count + " times try to acquire lock for product[key=" + key + "]......");
                    continue;
                }


                logger.info("success to acquire lock for product[id= " + key + " ] after " + count + " times try......");
                break;
            }
        }

    }


    /**
     * 释放掉一个分布式锁
     *
     * @param key key
     */
    public void releaseDistributedLock(String key) {
        try {
            zookeeper.delete(key, -1);
            System.out.println("release the lock for product[key=" + key + "]......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ZooKeeperWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            logger.info("Receive watched event: " + event.getState());
            if (Event.KeeperState.SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }
        }
    }
}
