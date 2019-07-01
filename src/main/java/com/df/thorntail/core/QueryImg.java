package com.df.thorntail.core;

import java.util.function.Consumer;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.db.StandaloneDb;
import com.df.thorntail.db.pojos.ImgInfo;
import com.df.thorntail.db.pojos.ImgSample;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

public class QueryImg {

	private final static Logger logger = LoggerFactory.getLogger("main");
	
	private static StandaloneDb database;
	
	public static void main(String[] args) throws InterruptedException {
		
		logger.info("Connection to database");
		database = new StandaloneDb("enel", "admin", "test", "localhost", 27017);
		if (!database.isConnected())
			return;
		
		if (!database.init())
			return;
		
		ImageDatabase imageDatabase = new ImageDatabase(database);
		
//		{
//			FindIterable<ImgInfo> fi =  imageDatabase.getInfoCollection().find();
//			fi.forEach(new Consumer<ImgInfo>() {
//				@Override
//				public void accept(ImgInfo t) {
//					logger.info("Img {} id {}", t.getName(), t.getId());
//				}
//			});
//		}
//		{
//			FindIterable<ImgSample> fis = imageDatabase.getSampleCollection().find();
//			fis.forEach(new Consumer<ImgSample>() {
//				@Override
//				public void accept(ImgSample t) {
//					logger.info("Sample ID: {}", t.getInfoId());
//				}
//			});
//		}
		
		{
			ObjectId ref = new ObjectId("5d1654b48fb4226b06c409a6");
			FindIterable<ImgSample> fis = imageDatabase.getSampleCollection().find(Filters.eq("infoId", ref));
			fis.forEach(new Consumer<ImgSample>() {
				@Override
				public void accept(ImgSample t) {
					logger.info("Sample ID: {}", t.getInfoId());
				}
			});
		}
		
		
		logger.info("Done");
	}
}
