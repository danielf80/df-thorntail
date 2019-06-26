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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ImportImgSource {

	private final static Logger logger = LoggerFactory.getLogger("main");
	
	private static StandaloneDb database;
	private static Set<String> ignoredTags;
	private static Set<String> ignoredDirs;
	
	public static void main(String[] args) {
		
		database = new StandaloneDb("enel", "admin", "test", "localhost", 27017);
		if (!database.isConnected())
			return;
		
		if (!database.init())
			return;
		
		ignoredTags = loadTagCfgFile("META-INF/tags.ignore.txt");
		ignoredDirs = new HashSet<String>();
		ignoredTags.forEach(t -> { if(t.endsWith(".*")) ignoredDirs.add(t.substring(0, t.length()-2));});
		
		final int maxThreads = 10;
		final ExecutorService executor = new ThreadPoolExecutor(
				maxThreads , maxThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		
		
		Path dir = Paths.get("C:\\Temp\\imgs");
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir, "*.{png,jpg,gif}")) {

			for (Path imgPath : dirStream) {
				logger.info("File: {}", imgPath);
				executor.submit(new Runnable() {
					@Override
					public void run() {
						try {
							ImageData ret = loadImage(imgPath);
							saveImage(ret);
						} catch (IOException e) {}
					}
				});
			}
			
			Thread.sleep(5000);
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static ImageData loadImage(Path imgPath) throws IOException {
		ImgInfo img = new ImgInfo();
		ImgData imgData = new ImgData();
		
		ImageData ret = new ImageData();
		ret.info = img;
		ret.data = imgData;
		ret.samples = new ImgSample[1];
		
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
	            BufferedImage sampleImg = Scalr.resize(buffImage, Method.QUALITY, width, height);
	            if (width < sampleImg.getWidth() || height < sampleImg.getHeight()) {
	            	sampleImg = Scalr.crop(sampleImg, width, height);
	            }
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(sampleImg, sampleType, baos);
	            ImgSample imgSample = new ImgSample();
	            imgSample.setData(baos.toByteArray());
	            imgSample.setScale(Scale.SMALL);
	            imgSample.setType(sampleType);
	            ret.samples[0] = imgSample;
            }
        }
		
		return ret;
	}
	
	private static void saveImage(ImageData ret) {
		database.getCollection(ImgInfo.class).insertOne(ret.info);
		System.out.println(ret.info.getId());
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
	public ImgSample[] samples;
}

class ImageDatabase {
	
	private final StandaloneDb database;
	private final MongoCollection<ImgInfo> infoCollection;
	
	public ImageDatabase(StandaloneDb database) {
		this.database = database;
		this.infoCollection = database.getCollection(ImgInfo.class);
	}
	
	public MongoCollection<ImgInfo> getInfoCollection() {
		return infoCollection;
	}
}

class ImageProcessor implements Callable<String> {

	private final ImageDatabase db;
	
	public ImageProcessor(ImageDatabase database) {
		this.db = database;
	}
	
	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ImageData loadImage(Path imgPath) throws IOException {
		ImgInfo img = new ImgInfo();
		ImgData imgData = new ImgData();
		
		ImageData ret = new ImageData();
		ret.info = img;
		ret.data = imgData;
		ret.samples = new ImgSample[1];
		
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
	            BufferedImage sampleImg = Scalr.resize(buffImage, Method.QUALITY, width, height);
	            if (width < sampleImg.getWidth() || height < sampleImg.getHeight()) {
	            	sampleImg = Scalr.crop(sampleImg, width, height);
	            }
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(sampleImg, sampleType, baos);
	            ImgSample imgSample = new ImgSample();
	            imgSample.setData(baos.toByteArray());
	            imgSample.setScale(Scale.SMALL);
	            imgSample.setType(sampleType);
	            ret.samples[0] = imgSample;
            }
        }
		
		return ret;
	}
	
	private void saveImage(ImageData ret) {
		database.getCollection(ImgInfo.class).insertOne(ret.info);
		System.out.println(ret.info.getId());
	}
	
}