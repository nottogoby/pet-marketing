package com.pet.platform;

import com.pet.common.service.test.TestService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@MapperScan(value = "com.pet.platform.mapper",sqlSessionTemplateRef = "sqlSessionTemplate")
@ServletComponentScan
public class PetPlatformApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PetPlatformApplication.class, args);

		TestService testService = (TestService) context.getBean("testServiceImpl");

		testService.test();
	}

}
