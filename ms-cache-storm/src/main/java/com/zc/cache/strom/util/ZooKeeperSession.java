package com.zc.cache.strom.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/03 下午8:30
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ZooKeeperSession {

    private static volatile ZooKeeperSession zooKeeperSession;

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zookeeper;

    public ZooKeeperSession() throws InterruptedException, IOException {
        this.zookeeper = new ZooKeeper("192.168.0.105:2181,192.168.0.106:2181,192.168.0.107:2181",
                50000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                connectedSemaphore.countDown();
            }
        });
        connectedSemaphore.await();
    }

    public static ZooKeeperSession getInstance() throws IOException, InterruptedException {

        if (zooKeeperSession == null) {
            synchronized (ZooKeeperSession.class) {
                if (zooKeeperSession == null) {
                    zooKeeperSession = new ZooKeeperSession();
                }
            }
        }
        return zooKeeperSession;
    }

    public void setNodeData(String path, String data) {
        try {
            zookeeper.setData(path, data.getBytes(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNode(String path) {
        try {
            zookeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNodeData(String path) {
        try {
            return new String(zookeeper.getData(path, false, new Stat()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取分布式锁
     */
    public void acquireDistributedLock() {
        String path = "/task-id-list-lock";

        try {
            zookeeper.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for taskid-list-lock");
        } catch (Exception e) {
            // 如果那个商品对应的锁的node，已经存在了，就是已经被别人加锁了，那么就这里就会报错
            // NodeExistsException
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    zookeeper.create(path, "".getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e2) {
                    count++;
                    System.out.println("the " + count + " times try to acquire lock for taskid-list-lock......");
                    continue;
                }
                System.out.println("success to acquire lock for taskid-list-lock after " + count + " times try......");
                break;
            }
        }
    }

    /**
     * 释放掉一个分布式锁
     */
    public void releaseDistributedLock() {
        String path = "/task-id-list-lock";
        try {
            zookeeper.delete(path, -1);
            System.out.println("release the lock for taskid-list-lock......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
