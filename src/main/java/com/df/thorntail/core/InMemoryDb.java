package com.df.thorntail.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.entity.ImgInfo;
import com.df.thorntail.util.SysProperties;

@Named
@ApplicationScoped
public class InMemoryDb {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<ImgInfo> images = new ArrayList<ImgInfo>();
	
	@Inject
	private SysProperties props;  
	
	@Inject
	private ImgScanner scanner;
	
	@PostConstruct
	public void init() {
		logger.info("Inicializing DB");
		images.addAll(scanner.load(props.getRootFolder()));
	}
	
	public List<ImgInfo> getImages() {
		return images;
	}
}
