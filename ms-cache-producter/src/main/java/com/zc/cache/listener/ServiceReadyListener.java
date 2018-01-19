package com.zc.cache.listener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zc.cache.dao.db.entity.ShopInfo;
import com.zc.cache.dao.db.mapper.ShopInfoMapper;
import com.zc.cache.kafka.KafkaConfProperties;
import com.zc.cache.kafka.KafkaConsumer;
import com.zc.cache.rebuild.RebuildCacheTask;
import com.zc.cache.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.*;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/26 下午10:48
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class ServiceReadyListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("ext-thread-pool-%d")
            .build();

    private ThreadFactory kafkaMessageProcessthreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("kafka-msg-thread-pool-%d")
            .build();


    private ExecutorService executorService = Executors.newFixedThreadPool(3, threadFactory);

    private ExecutorService kafkaMessageProcessExecutor = new ThreadPoolExecutor(20, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            kafkaMessageProcessthreadFactory);


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        //executorService.submit(new KafkaConsumer("shop-cache", kafkaMessageProcessExecutor));
        //executorService.submit(new RebuildCacheTask());

        logger.info("init complete ======================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
