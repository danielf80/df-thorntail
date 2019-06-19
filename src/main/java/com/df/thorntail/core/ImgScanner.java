package com.df.thorntail.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.entity.ImgInfo;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

@Named
@ApplicationScoped
public class ImgScanner {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final AtomicLong ID = new AtomicLong(new Random().nextInt(31) * 100000L);
	
	@PostConstruct
	public void init() {
		logger.info("Inicializing Scanner");
	}
	
	public List<ImgInfo> load(String source) {
		
		List<ImgInfo> images = new ArrayList<ImgInfo>();
		logger.info("Scanning {}", source);
		
		long totalSize = 0;
		Path dir = Paths.get(source);
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir, "*.{png,jpg,gif}")) {
			for (Path imgPath : dirStream) {
				logger.debug("Reading image '{}'", imgPath.getFileName());
				
				@SuppressWarnings("unused")
				Metadata metadata = ImageMetadataReader.readMetadata(imgPath.toFile());
				
				ImgInfo imgInfo = new ImgInfo();
				imgInfo.setId(calcId(imgPath, source));
				imgInfo.setName(imgPath.getFileName().toString());
				imgInfo.setRef(imgPath.toAbsolutePath().toString());
				imgInfo.setSource(source);
				
				logger.debug("Reading size");
				try (FileChannel fChannel = FileChannel.open(imgPath)) {
					imgInfo.setSize(fChannel.size());
				}
				imgInfo.setSample(getSample(imgPath));
				
				logger.debug("Adding image to the list");
				images.add(imgInfo);
				totalSize += imgInfo.getSize();
			}
		} catch (IOException | ImageProcessingException e) {
			logger.error("Error reading images", e);
		}
		
		logger.info("Scanning finished ({} files, {} bytes)", images.size(), totalSize);
		return images;
	}

	private byte[] getSample(Path imgPath) throws IOException {
		
		logger.debug("Generating sample for {}", imgPath);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
		
			BufferedImage image = ImageIO.read(imgPath.toFile());
			BufferedImage resizedImg = Scalr.resize(image, Method.QUALITY, 300, 300);
			
			ImageIO.write(resizedImg, FilenameUtils.getExtension(imgPath.getFileName().toString()), baos);
			baos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			logger.error("Error generating sample", e);
		}
		return new byte[0];
	}
	
	private long calcId(Path imgPath, String source) {
		return ID.incrementAndGet();
	}
}
