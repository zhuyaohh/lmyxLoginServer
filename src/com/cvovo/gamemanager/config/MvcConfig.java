package com.cvovo.gamemanager.config;

import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan(basePackages = "com.cvovo.gamemanager", includeFilters = @Filter(Controller.class), useDefaultFilters = false)
public class MvcConfig extends WebMvcConfigurationSupport {

	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new ByteArrayHttpMessageConverter());

		MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
		jackson.setSupportedMediaTypes(MediaType.parseMediaTypes("text/json;charset=utf-8,application/json;charset=utf-8"));
		converters.add(jackson);
	}

	@Override
	protected void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/view/");
		viewResolver.setSuffix(".htm");
		registry.viewResolver(viewResolver);
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
		registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/view/js/");
	}

	@Override
	protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	// @Bean
	// public HandlerExceptionResolver handlerExceptionResolver() {
	// SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
	// resolver.setDefaultErrorView("error");
	// resolver.setDefaultStatusCode(500);
	// resolver.setWarnLogCategory("WARN");
	//
	// resolver.addStatusCode("error", 404);
	//
	// Properties mappings = new Properties();
	// mappings.setProperty("java.lang.Exception", "error");
	// mappings.setProperty("java.lang.Throwable", "error");
	// resolver.setExceptionMappings(mappings);
	//
	// return resolver;
	// }

}