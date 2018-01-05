package com.zc.cache.queue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/01 13:43
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class BooleanMutex {

    private static volatile BooleanMutex booleanMutex = new BooleanMutex(true);

    private Sync sync;

    public BooleanMutex() {
        sync = new Sync();
        set(true);
    }

    public BooleanMutex(Boolean mutex) {
        sync = new Sync();
        set(mutex);
    }

    public static BooleanMutex getBooleanMutex() {
        return booleanMutex;
    }

    /**
     * 阻塞等待Boolean为true
     *
     * @throws InterruptedException InterruptedException
     */
    public void get(boolean mutex) throws InterruptedException {
        sync.innerGet(mutex);
    }

    /**
     * 阻塞等待Boolean为true,允许设置超时时间
     *
     * @param timeout timeout
     * @param unit    unit
     * @throws InterruptedException InterruptedException
     * @throws TimeoutException     TimeoutException
     */
    public void get(boolean mutex, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        sync.innerGet(mutex, unit.toNanos(timeout));
    }

    /**
     * 重新设置对应的Boolean mutex
     *
     * @param mutex mutex
     */
    public void set(Boolean mutex) {
        if (mutex) {
            sync.innerSetTrue();
        } else {
            sync.innerSetFalse();
        }
    }

    public boolean state() {
        return sync.innerState();
    }

    /**
     * Synchronization control for BooleanMutex. Uses AQS sync state to
     * represent run status
     */
    private final class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 2559471934544126329L;

        /**
         * State value representing that TRUE
         */
        private static final int TRUE = 1;
        /**
         * State value representing that FALSE
         */
        private static final int FALSE = 2;


        void innerGet(boolean mutex) throws InterruptedException {
            int state = boolean2Int(mutex);
            acquireSharedInterruptibly(state);
        }

        void innerGet(boolean mutex, long nanosTimeout) throws InterruptedException, TimeoutException {
            int state = boolean2Int(mutex);
            if (!tryAcquireSharedNanos(state, nanosTimeout)) {
                throw new TimeoutException();
            }
        }

        /**
         * 实现AQS的接口，获取共享锁的判断
         */
        protected int tryAcquireShared(int state) {
            // 如果为true，直接允许获取锁对象
            // 如果为false，进入阻塞队列，等待被唤醒
            return getState() == state ? 1 : -1;
        }

        /**
         * 实现AQS的接口，释放共享锁的判断
         */
        protected boolean tryReleaseShared(int ignore) {
            // 始终返回true，代表可以release
            return true;
        }

        boolean innerState() {
            return int2boolean(getState());
        }

        void innerSetTrue() {
            for (; ; ) {
                int s = getState();
                if (s == TRUE) {
                    return; // 直接退出
                }
                if (compareAndSetState(s, TRUE)) {// cas更新状态，避免并发更新true操作
                    releaseShared(0);// 释放一下锁对象，唤醒一下阻塞的Thread
                    return;
                }
            }
        }

        void innerSetFalse() {
            for (; ; ) {
                int s = getState();
                if (s == FALSE) {
                    return; // 直接退出
                }
                if (compareAndSetState(s, FALSE)) {// cas更新状态，避免并发更新false操作
                    releaseShared(0);// 释放一下锁对象，唤醒一下阻塞的Thread
                    return;
                }
            }
        }

        private int boolean2Int(boolean state) {
            return state ? TRUE : FALSE;
        }


        private boolean int2boolean(int state) {

            if (state == TRUE) {
                return true;
            } else if (state == FALSE) {
                return false;
            }

            throw new RuntimeException();
        }
    }
}
