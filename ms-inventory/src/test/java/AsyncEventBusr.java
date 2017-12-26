import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/25 下午5:05
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class AsyncEventBusr {

    ExecutorService executorService = Executors.newFixedThreadPool(3);
    AsyncEventBus eventBus = new AsyncEventBus(executorService);

    public AsyncEventBusr() {
        eventBus.register(this);
    }


    //@AllowConcurrentEvents
    @Subscribe
    public void add(String value) throws InterruptedException {

        System.out.println(value+"=================start==============" + Thread.currentThread().getName());
        Thread.sleep(500000);
        System.out.println(value+"=================end==============" + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        AsyncEventBusr asyncEventBus = new AsyncEventBusr();

        for (int i=0;i<10;i++){
            asyncEventBus.eventBus.post("zhong"+i);
        }


    }
}
