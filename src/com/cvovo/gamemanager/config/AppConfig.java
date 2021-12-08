package com.cvovo.gamemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;

@Configuration
@PropertySource(value = { "/WEB-INF/conf/app.properties", "/WEB-INF/conf/callback.properties" }, ignoreResourceNotFound = true)
@ComponentScan(basePackages = "com.cvovo.gamemanager", excludeFilters = @Filter(Controller.class))
public class AppConfig {

	@Value("${callBack.pay}")
	public int CALLBACK_PAY;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		return configurer;
	}

}
