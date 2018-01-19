package com.zc.ha.web;

import com.zc.ha.hystrix.GetProductInfoCommand;
import com.zc.ha.hystrix.GetProductInfoObservableCommand;
import com.zc.ha.model.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/10 下午8:24
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@RestController
public class ProductController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @GetMapping("/product/{id}")
    public ProductInfo getProductInfo(@PathVariable Long id) {
        GetProductInfoCommand getProductInfoCommand = new GetProductInfoCommand(id.toString());
        logger.info("Call getProductInfoCommand start .....");


        ProductInfo productInfo = getProductInfoCommand.execute();
        //final List<ProductInfo> productInfos = new ArrayList<ProductInfo>();
        //普通的HystrixCommand也可以被转换成Observable异步执行,但是  线程还是 使用group绑定的线程池去执行的
//        getProductInfoCommand.toObservable().subscribe(new Observer<ProductInfo>() {
//            @Override
//            public void onCompleted() {
//                logger.info("On completed .....");
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                logger.info("On error .....");
//            }
//
//            @Override
//            public void onNext(ProductInfo productInfo) {
//                productInfos.add(productInfo);
//                logger.info("On next :{} ", productInfo.toString());
//            }
//        });
        logger.info("Call getProductInfoCommand end .....");
//        if (productInfos.size() > 0) {
//            return productInfos.get(0);
//        }
        return productInfo;
    }


    @GetMapping("/products")
    public List<ProductInfo> getProductInfos(@RequestParam String ids) throws InterruptedException, ExecutionException {
        GetProductInfoObservableCommand getProductInfoObservableCommand = new GetProductInfoObservableCommand(ids);

        final List<ProductInfo> productInfos = new ArrayList<ProductInfo>();
        logger.info("Call getProductInfoObservableCommand start .....");

        //当转换成 Observable 时 ,只能返回一个productInfo,当在call方法调用多次onNext()方法会报错
        // ProductInfo productInfo= getProductInfoObservableCommand.toObservable().toBlocking().toFuture().get();

        getProductInfoObservableCommand.observe().subscribe(new Observer<ProductInfo>() {
            @Override
            public void onCompleted() {
                logger.info("On completed .....");
            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("On error .....");
            }

            @Override
            public void onNext(ProductInfo productInfo) {
                productInfos.add(productInfo);
                logger.info("On next :{} ", productInfo.toString());
            }
        });
        //Observer 内为异步执行,如果在这里不sellp,可能导致请求已经返回了,Observer还没有执行完
        Thread.sleep(100L);
        logger.info("Call getProductInfoObservableCommand end .....");
        return productInfos;
    }

}
