package com.zc.ha.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.servlet.*;
import java.io.IOException;

/**
 * hystrix请求上下文过滤器
 * @author Administrator
 *
 */
public class HystrixRequestContextFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {
		//当前线程有效,内部使用threadlocal维护
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
		try {
			chain.doFilter(request, response); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.shutdown();
		}
	}

	@Override
	public void destroy() {
		
	}

}
