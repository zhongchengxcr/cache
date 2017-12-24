package com.zc.cache.service;


import com.zc.cache.request.Request;

/**
 * 请求异步执行的service
 * @author Administrator
 *
 */
public interface RequestAsyncProcessService {

	void process(Request request);
	
}
