package com.df.thorntail.db;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.db.pojos.ImgData;
import com.df.thorntail.db.pojos.ImgInfo;
import com.df.thorntail.db.pojos.ImgSample;
import com.mongodb.client.MongoCollection;

@Stateless
public class ImageCollectionDb {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Inject
	private MongoClientProvider mongoClientProvider;
	
	private MongoCollection<ImgInfo> infoCollection;
	private MongoCollection<ImgData> dataCollection;
	private MongoCollection<ImgSample> sampleCollection;
	
	@PostConstruct
	public void init() {
		infoCollection = mongoClientProvider.getCollection(ImgInfo.class);
		logger.info("Info Collection: {}", infoCollection);
		
		dataCollection = mongoClientProvider.getCollection(ImgData.class);
		logger.info("Data Collection: {}", dataCollection);
		
		sampleCollection = mongoClientProvider.getCollection(ImgSample.class);
		logger.info("Sample Collection: {}", sampleCollection);
	}
	
	public MongoCollection<ImgInfo> getInfoCollection() {
		return infoCollection;
	}
	
	public MongoCollection<ImgData> getDataCollection() {
		return dataCollection;
	}
	
	public MongoCollection<ImgSample> getSampleCollection() {
		return sampleCollection;
	}
}
