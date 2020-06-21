package com.pet.platform.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource druid(){
		return new DruidDataSource();
	}

	/**
	 * 配置druid监控中心
	 * @return
	 */
	@Bean
	public ServletRegistrationBean statViewServlet(){
		ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		Map<String,String> initParams = new HashMap<>();

		initParams.put("loginUsername","admin");//账号
		initParams.put("loginPassword","12345");//密码
		initParams.put("allow","");//默认允许所有
		initParams.put("deny","192.168.123.22");//不允许的黑名单ip
		initParams.put("resetEnable","false");//禁用HTML页面上的“Reset All”功能

		bean.setInitParameters(initParams);
		return bean;
	}

	/**
	 * 配置一个监控的filter
	 * @return
	 */
	@Bean
	public FilterRegistrationBean webStatFilter(){
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new WebStatFilter());

		Map<String,String> initParams = new HashMap<>();
		initParams.put("exclusions","*.js,*.css,/druid/*");

		bean.setInitParameters(initParams);
		bean.setUrlPatterns(Arrays.asList("/*"));

		return bean;
	}

}
