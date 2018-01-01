package com.zc.cache.queue;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/01 13:59
 * <p>
 * Company: 百趣
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        BooleanMutex booleanMutex = BooleanMutex.getBooleanMutex();


        LinkedBlockingQueue<Integer> stringQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Integer> rollbackQueue = new LinkedBlockingQueue<>();
        stringQueue.put(1);
        stringQueue.put(2);
        stringQueue.put(3);
        stringQueue.put(4);
        stringQueue.put(5);
        stringQueue.put(6);
        stringQueue.put(7);
        stringQueue.put(8);
        stringQueue.put(9);
        stringQueue.put(10);
        stringQueue.put(11);
        stringQueue.put(12);
        stringQueue.put(13);
        stringQueue.put(14);
        stringQueue.put(15);
        stringQueue.put(16);
        stringQueue.put(17);
        stringQueue.put(18);


        new Thread(() -> {


            try {
                while (true) {
                    booleanMutex.get(true);

                    int num = stringQueue.take();

                    if (num % 3 == 0) {
                        System.out.println(Thread.currentThread().getName() + "===========rollback : " + num);
                        rollbackQueue.put(num);
                        booleanMutex.set(false);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "===========process num : " + num);
                    }
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {


            try {
                while (true) {
                    booleanMutex.get(true);

                    int num = stringQueue.take();

                    if (num % 3 == 0) {
                        System.out.println(Thread.currentThread().getName() + "===========rollback : " + num);
                        rollbackQueue.put(num);
                        booleanMutex.set(false);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "===========process num : " + num);
                    }
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {


            try {
                while (true) {
                    booleanMutex.get(true);

                    int num = stringQueue.take();

                    if (num % 3 == 0) {
                        System.out.println(Thread.currentThread().getName() + "===========rollbackum : " + num);
                        rollbackQueue.put(num);
                        booleanMutex.set(false);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "===========process num : " + num);
                    }
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {


            try {
                while (true) {
                    booleanMutex.get(true);

                    int num = stringQueue.take();

                    if (num % 3 == 0) {
                        System.out.println(Thread.currentThread().getName() + "===========rollback : " + num);
                        rollbackQueue.put(num);
                        booleanMutex.set(false);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "===========process num : " + num);
                    }
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();


        new Thread(() -> {

            while (true) {
                try {
                    booleanMutex.get(false);
                    Thread.sleep(2000);

                    Integer rollbackNum = null;
                    while (true) {
                        rollbackNum = rollbackQueue.poll();

                        if (rollbackNum != null) {
                            System.out.println(Thread.currentThread().getName() + "*********************=======process rollbackNum :" + rollbackNum);
                        } else {
                            booleanMutex.set(true);
                        }
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();


    }


}
