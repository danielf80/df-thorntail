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

	private static SysProperties instance = new SysProperties();
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Properties prop = new Properties();
	private boolean initialized;
	private String rootFolder;
	private int defSampleHeight;
	private int defSampleWidth;
	
	@PostConstruct
	public void init() {
		try {
			logger.info("Loading properties...");
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/sys.properties"));
			logger.info("System properties loaded");
			
			rootFolder = prop.getProperty("ROOT_FOLDER", System.getProperty("java.io.tmpdir"));
			defSampleHeight = Integer.parseInt(prop.getProperty("DEF_SAMPLE_HEIGHT", "255"));
			defSampleWidth = Integer.parseInt(prop.getProperty("DEF_SAMPLE_WIDTH", "255"));
			logger.info("Root folder: '{}'", rootFolder);
		} catch (IOException e) {
			logger.error("Fail to read property file", e);
		}
		initialized = true;
	}
	
	public static SysProperties getInstance() {
		synchronized (instance) {
			if (!instance.initialized) {
				instance.init();
			}
		}
		return instance;
	}
	
	
	public String getRootFolder() {
		return rootFolder;
	}
	
	public int getDefSampleHeight() {
		return defSampleHeight;
	}
	public int getDefSampleWidth() {
		return defSampleWidth;
	}
}
