package com.df.thorntail.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.db.StandaloneDb;
import com.df.thorntail.db.pojos.ImgData;
import com.df.thorntail.db.pojos.ImgInfo;
import com.df.thorntail.db.pojos.ImgSample;
import com.df.thorntail.db.pojos.ImgSample.Scale;
import com.df.thorntail.util.SysProperties;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

public class ImportImgSource {

	private final static Logger logger = LoggerFactory.getLogger("main");
	
	private static StandaloneDb database;
	private static Set<String> ignoredTags;
	private static Set<String> ignoredDirs;
	
	public static void main(String[] args) throws InterruptedException {
		
		logger.info("Connection to database");
		database = new StandaloneDb("enel", "admin", "test", "localhost", 27017);
		if (!database.isConnected())
			return;
		
		if (!database.init())
			return;
		
		ignoredTags = loadTagCfgFile("META-INF/tags.ignore.txt");
		ignoredDirs = new HashSet<String>();
		ignoredTags.forEach(t -> { if(t.endsWith(".*")) ignoredDirs.add(t.substring(0, t.length()-2));});
		
		final int maxThreads = 10;
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(
				maxThreads , maxThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		
		ImageDatabase imageDatabase = new ImageDatabase(database);
		imageDatabase.deleteAll();
		
		Path dir = Paths.get("C:\\Temp\\imgs");
		int count = 0;
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir, "*.{png,jpg,gif}")) {

			for (Path imgPath : dirStream) {
				logger.info("File: {}", imgPath);
				count++;
				executor.submit(new ImageProcessor(imageDatabase, imgPath));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread.sleep(5000);
		while (executor.getActiveCount() > 0) {
			logger.info("Waiting. {} tasks in execution ({} completed)", executor.getActiveCount(), executor.getCompletedTaskCount());
			Thread.sleep(5000);	
		}
		
		List<?> pending = executor.shutdownNow();
		logger.info("Executor is shutdown. Pending: {}", pending.size());
		
		logger.info("Done ({} files processed)", count);
	}
	
	private static Set<String> loadTagCfgFile(String resourcePath) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
		
		Set<String> tags = new HashSet<String>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(url.toURI()));
			
			tags.addAll(lines);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return tags;
	}
}

class ImageData {
	public ImgInfo info;
	public ImgData data;
	public List<ImgSample> samples = new ArrayList<ImgSample>();
}

class ImageDatabase {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final StandaloneDb database;
	private final MongoCollection<ImgInfo> infoCollection;
	private final MongoCollection<ImgData> dataCollection;
	private final MongoCollection<ImgSample> sampleCollection;
	
	public ImageDatabase(StandaloneDb database) {
		this.database = database;
		
		this.infoCollection = database.getCollection(ImgInfo.class);
		logger.info("Info Collection: {}", infoCollection);
		
		this.dataCollection = database.getCollection(ImgData.class);
		logger.info("Data Collection: {}", dataCollection);
		
		this.sampleCollection = database.getCollection(ImgSample.class);
		logger.info("Sample Collection: {}", sampleCollection);
	}
	
	public void deleteAll() {
		
		logger.info("Deleting {} info", this.infoCollection.estimatedDocumentCount());
		this.infoCollection.deleteMany(new BasicDBObject());
		
		logger.info("Deleting {} data", this.dataCollection.estimatedDocumentCount());
		this.dataCollection.deleteMany(new BasicDBObject());
		
		logger.info("Deleting {} sample", this.sampleCollection.estimatedDocumentCount());
		this.sampleCollection.deleteMany(new BasicDBObject());
		
		logger.info("Database clear");
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

class ImageProcessor implements Callable<String> {

	private final ImageDatabase db;
	private final Path imgPath;
	
	public ImageProcessor(ImageDatabase database, Path imgPath) {
		this.db = database;
		this.imgPath = imgPath;
	}
	
	@Override
	public String call() throws Exception {
		ImageData data = loadImage(imgPath);
		
		return saveImage(data);
	}
	
	private ImageData loadImage(Path imgPath) throws IOException {
		ImgInfo img = new ImgInfo();
		ImgData imgData = new ImgData();
		
		ImageData ret = new ImageData();
		ret.info = img;
		ret.data = imgData;
		
		img.setSource("local");
		img.setName(imgPath.getFileName().toString());
		img.setExtref(imgPath.toAbsolutePath().toString());
		
		BasicFileAttributes attr = Files.readAttributes(imgPath, BasicFileAttributes.class);
		
		img.setCreationTime(attr.creationTime().toMillis());
		img.setModifiedTime(attr.lastModifiedTime().toMillis());
		img.setSize(attr.size());
		
		final String sampleType = "jpg";
		
		
		try (InputStream inputStream = new FileInputStream(imgPath.toFile())){
            BufferedImage buffImage = ImageIO.read(inputStream);
            
            img.setHeight(buffImage.getHeight());
            img.setWidth(buffImage.getWidth());
            
            imgData.setData(IOUtils.toByteArray(inputStream));
            
            final int width = SysProperties.getInstance().getDefSampleWidth();
            final int height = SysProperties.getInstance().getDefSampleHeight();
            
            {
            	float baseScaleWxH = (float)width / (float)height;
            	float baseScaleHxW = (float)height / (float)width;
            	
            	int startWidth = 0;
            	int startHeight = 0;
            	int cropWidth = img.getWidth();
            	int cropHeight = img.getHeight();
            	if (baseScaleWxH < 1) {
            		cropHeight = (int)(cropWidth * baseScaleWxH);
            		startHeight = (img.getHeight() - cropHeight) / 2;
            	} else {
            		cropWidth = (int)(cropHeight * baseScaleHxW);
            		startWidth = (img.getWidth() - cropWidth) / 2;
            	}
            	
            	BufferedImage sampleImg;
            	BufferedImage cropImg = Scalr.crop(buffImage, startWidth, startHeight, cropWidth, cropHeight);
            	if (cropImg.getWidth() > width || cropImg.getHeight() > height) {
            		sampleImg = Scalr.resize(cropImg, Method.QUALITY, width, height);
            	} else {
            		sampleImg = cropImg;
            	}
	            
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(sampleImg, sampleType, baos);
	            ImgSample imgSample = new ImgSample();
	            imgSample.setData(baos.toByteArray());
	            imgSample.setScale(Scale.SMALL);
	            imgSample.setType(sampleType);
	            ret.samples.add(imgSample);
            }
        }
		
		return ret;
	}
	
	private String saveImage(ImageData ret) {
		db.getInfoCollection().insertOne(ret.info);
		
		ret.data.setInfoId(ret.info.getId());
		
		db.getDataCollection().insertOne(ret.data);
		
		ret.samples.forEach(s -> s.setInfoId(ret.info.getId()));
		
		db.getSampleCollection().insertMany(ret.samples);
		
		return ret.info.getId().toString();
	}
}