package com.zc.ha.service;

import com.alibaba.fastjson.JSON;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2018/01/10 下午8:49
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class Http {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            //.addInterceptor(new GzipRequestInterceptor())
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();


    public static <T> T get(String url, Class<T> clazz) {

        Request request = new Request.Builder()
                //尽可能服用底层TCP连接
                .addHeader("Connection", "keep-alive")
                .url(url)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String bodyStr = response.body().string();
            return JSON.parseObject(bodyStr, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
