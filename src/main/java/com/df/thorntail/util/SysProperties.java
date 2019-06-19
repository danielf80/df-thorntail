package com.df.thorntail.util;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ApplicationScoped
public class SysProperties {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Properties prop = new Properties();
	private String rootFolder;
	
	@PostConstruct
	public void init() {
		try {
			logger.info("Loading properties...");
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/sys.properties"));
			logger.info("System properties loaded");
			
			rootFolder = prop.getProperty("ROOT_FOLDER", System.getProperty("java.io.tmpdir"));
			
			logger.info("Root folder: '{}'", rootFolder);
		} catch (IOException e) {
			logger.error("Fail to read property file", e);
		}
	}
	
	public String getRootFolder() {
		return rootFolder;
	}
}
