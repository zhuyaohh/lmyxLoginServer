package com.cvovo.gamemanager.config;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;
import ch.qos.logback.ext.spring.web.WebLogbackConfigurer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		// log4j2
		// servletContext.removeAttribute(Log4jWebSupport.SUPPORT_ATTRIBUTE);
		// servletContext.setInitParameter(Log4jWebSupport.LOG4J_CONFIG_LOCATION, "/WEB-INF/conf/log4j2.xml");
		// servletContext.setInitParameter("log4jContextName", "GLS");

		// logback
		servletContext.setInitParameter(WebLogbackConfigurer.CONFIG_LOCATION_PARAM, "/WEB-INF/conf/logback.xml");
		servletContext.setInitParameter("webAppRootKey", "lmyxLoginServer.root");
		servletContext.addListener(LogbackConfigListener.class);

		super.onStartup(servletContext);
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { AppConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { MvcConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		OpenEntityManagerInViewFilter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();

		return new Filter[] { characterEncodingFilter, openEntityManagerInViewFilter };
	}

}
