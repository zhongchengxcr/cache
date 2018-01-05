package com.zc.cache.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
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


    public void acquireDistributedLock(Long productId) throws InterruptedException {

        String path = "/product-lock-" + productId;

        try {
            zookeeper.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for product[id=" + productId + "]");
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
                    System.out.println("the " + count + " times try to acquire lock for product[id=" + productId + "]......");
                    continue;
                }
                System.out.println("success to acquire lock for product[id=" + productId + "] after " + count + " times try......");
                break;
            }
        }
    }


    public void acquireDistributedLock(String path) {
        try {
            zookeeper.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for " + path);
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
                    System.out.println("the " + count + " times try to acquire lock for " + path + "......");
                    continue;
                }
                System.out.println("success to acquire lock for " + path + " after " + count + " times try......");
                break;
            }
        }
    }


    public boolean acquireFastFailedDistributedLock(String path) {
        try {
            zookeeper.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for " + path);
            return true;
        } catch (Exception e) {
            System.out.println("fail to acquire lock for " + path);
        }
        return false;
    }


    /**
     * 释放掉一个分布式锁
     *
     * @param productId
     */
    public void releaseDistributedLock(Long productId) {
        String path = "/product-lock-" + productId;
        try {
            zookeeper.delete(path, -1);
            System.out.println("release the lock for product[id=" + productId + "]......");
        } catch (Exception e) {
            e.printStackTrace();

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


    public String getNodeData(String path) {
        try {
            return new String(zookeeper.getData(path, false, new Stat()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
