package com.zc.ha.hystrix;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.zc.ha.model.ProductInfo;
import com.zc.ha.service.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 使用 productGroup  线程池 <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/10 下午9:05
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    private static Logger logger = LoggerFactory.getLogger(GetProductInfoCommand.class);
    private String productId;

    private final static String PRODUCT_CACHE = "http://localhost:8081/product/";


    public GetProductInfoCommand(String id) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("productGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetBrandNameCommand"))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("======GetBrandInfoPool"))
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                              /* 配置线程池大小,默认值10个. 建议值:请求高峰时99.5%的平均响应时间 + 向上预留一些即可 */
                                .withCoreSize(15)
                                /* 配置线程值等待队列长度,默认值:-1 建议值:-1表示不等待直接拒绝,测试表明线程池使用直接决绝策略+ 合适大小的非回缩线程池效率最高.所以不建议修改此值。 当使用非回缩线程池时，queueSizeRejectionThreshold,keepAliveTimeMinutes 参数无效 */
                                .withMaxQueueSize(12)//
                                .withQueueSizeRejectionThreshold(15))//
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(1))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                        //隔离策略
                                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                //.withExecutionIsolationSemaphoreMaxConcurrentRequests(15)
                        )
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                //熔断开始生效的10秒内 请求个数 statisticalWindowVolumeThreshold: 20 requests in 10 seconds must occur before statistics matter
                                .withCircuitBreakerRequestVolumeThreshold(30)
                                //错误比例>40%时开始熔断
                                .withCircuitBreakerErrorThresholdPercentage(40)
                                //sleepWindow: 5000 = 5 seconds that we will sleep before trying again after tripping the circuit
                                .withCircuitBreakerSleepWindowInMilliseconds(3000)
                                //超时时间
                                .withExecutionTimeoutInMilliseconds(500)
                                //fallback接受的最大请求数
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(30))
                //fallback 接受最大数量
        );
        this.productId = id;
    }

    @Override
    protected ProductInfo run() throws Exception {
        logger.info("Current thread name : {}", Thread.currentThread().getName());
        Thread.sleep(500);
        return Http.get(PRODUCT_CACHE + productId, ProductInfo.class);
    }

    @Override
    protected ProductInfo getFallback() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setName("getFallback");
        return productInfo;
    }

    @Override
    protected String getCacheKey() {
        return "productId-" + productId;
    }

    public static void main(String[] args) throws InterruptedException {


        /**
         * execute()、queue()、observe()、toObservable()这4个方法用来触发执行run()/construct()，一个实例只能执行一次这4个方法
         */
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        for (int i = 0; i < 30; i++) {
            GetProductInfoCommand getProductInfoCommand1 = new GetProductInfoCommand("1");
            executorService.submit(() -> {
                logger.info("_____________________");
                ProductInfo productInfo = getProductInfoCommand1.execute();
                System.out.println(getProductInfoCommand1.isResponseFromCache);
                logger.info("==============productInfo:{}", productInfo);
            });
        }

        //       GetProductInfoCommand getProductInfoCommand = new GetProductInfoCommand("1");
        //异步执行 run 返回 feature
        //getProductInfoCommand.queue();
        // observe() 事件注册(调用subscribe)前执行run()/construct() , 是异步非堵塞性执行，同queue
        //       Observable<ProductInfo> observable = getProductInfoCommand.observe();
        //toObservable()：事件注册后(调用subscribe)执行run()/construct()
        //       Observable<ProductInfo> observable1 = getProductInfoCommand.toObservable();
        //// single()是堵塞的

        //ProductInfo productInfo = observable.toBlocking().single();


        //      observable.subscribe(new Action1<ProductInfo>() {
        //          @Override
        //          public void call(ProductInfo productInfo) {
        //              logger.info("Call :{}", productInfo);
        //          }
        //     });

        //可以注册多个事件
        //     observable.subscribe(new Action1<ProductInfo>() {
        //         @Override
        //         public void call(ProductInfo productInfo) {
        //             logger.info("Call :{}", productInfo);
        //         }
        //     });

//        observable1.subscribe(new Action1<ProductInfo>() {
//            @Override
//            public void call(ProductInfo productInfo) {
//                logger.info("Call11 :{}", productInfo);
//            }
//        });

        //logger.info("productInfo :{}", productInfo);
        Thread.sleep(3000L);
    }

}
