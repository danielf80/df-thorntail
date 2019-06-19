package com.df.thorntail.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifecycleListener implements ServletContextListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		logger.info("Servlet context initialized: {}", contextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		logger.info("Servlet context destroyed: {}", contextEvent);
	}
}
