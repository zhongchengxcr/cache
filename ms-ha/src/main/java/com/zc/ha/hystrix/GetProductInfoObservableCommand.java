package com.zc.ha.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.zc.ha.model.ProductInfo;
import com.zc.ha.service.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 使用 RxIoScheduler-3 线程池
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/10 下午9:21
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class GetProductInfoObservableCommand extends HystrixObservableCommand<ProductInfo> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String productIds;

    private final static String PRODUCT_CACHE = "http://localhost:8081/product/";

    private final static String DEC = ",";

    public GetProductInfoObservableCommand(String ids) {
        super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("productObservableGroup")));
        this.productIds = ids;
    }

    @Override
    protected Observable<ProductInfo> resumeWithFallback() {
        return Observable.create(new Observable.OnSubscribe<ProductInfo>() {
            @Override
            public void call(Subscriber<? super ProductInfo> subscriber) {
                //Fallback
            }
        });
    }

    @Override
    protected Observable<ProductInfo> construct() {
        return Observable.create(new Observable.OnSubscribe<ProductInfo>() {
            @Override
            public void call(Subscriber<? super ProductInfo> subscriber) {
                try {
                    for (String id : productIds.split(DEC)) {
                        logger.info("Current thread name : {}", Thread.currentThread().getName());
                        ProductInfo info = Http.get(PRODUCT_CACHE + id, ProductInfo.class);
                        logger.info("ProductInfo : {}", info);
                        subscriber.onNext(info);
                    }
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
