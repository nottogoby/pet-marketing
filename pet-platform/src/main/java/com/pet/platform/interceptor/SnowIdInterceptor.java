package com.pet.platform.interceptor;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mybatis插件：使用雪花算法生成主键id
 */
public class SnowIdInterceptor implements Interceptor {

	private Map<Class, List<ParameterHandler>> handlerMap = new ConcurrentHashMap<>();

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		ParameterHandler target = (ParameterHandler) invocation.getTarget();

		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return null;
	}

	@Override
	public void setProperties(Properties properties) {

	}
}
