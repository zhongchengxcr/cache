package com.zc.cache.strom.util;

import java.util.*;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/03 下午4:52
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class FixSizedPriorityQueue<E> {

    private PriorityQueue<E> queue;

    private Comparator<E> comparator;

    private int maxSize; //堆的最大容量

    public FixSizedPriorityQueue(int maxSize, Comparator<E> comparator) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxSize = maxSize;
        this.comparator = comparator;
        this.queue = new PriorityQueue<>(maxSize, comparator);
    }

    public void add(E e) {
        if (queue.size() < maxSize) { //未达到最大容量，直接添加
            queue.add(e);
        } else { //队列已满
            E peek = queue.peek();
            if (comparator.compare(peek, e) > 0) { //将新元素与当前堆顶元素比较，保留较小的元素
                queue.poll();
                queue.add(e);
            }
        }
    }

    public List<E> sortedList() {
        List<E> list = new ArrayList<>(queue);
        Collections.sort(list, comparator); //PriorityQueue本身的遍历是无序的，最终需要对队列中的元素进行排序
        return list;
    }
}
