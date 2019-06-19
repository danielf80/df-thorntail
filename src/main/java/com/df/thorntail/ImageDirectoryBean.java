package com.df.thorntail;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.core.InMemoryDb;
import com.df.thorntail.entity.ImgInfo;

@Named
@ConversationScoped
public class ImageDirectoryBean implements Serializable {

	private static final long serialVersionUID = 216609789842575327L;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	private Conversation conversation;
	
	@Inject
	private InMemoryDb db;
//	@Inject
//	private FacesContext facesContext;
	
	private final int maxImagesPerPage = 15;
	private List<ImgInfo> images;
	private int pages;
	
	@PostConstruct
	public void init() {
		logger.info("Initializing");
		images = db.getImages();
	}
	
	public void initConversation() {
		if (!FacesContext.getCurrentInstance().isPostback() && conversation.isTransient()) {
			logger.info("CBegin");
			conversation.begin();
		}
	}
	public void endConversation() {
		if (!conversation.isTransient()) {
			logger.info("CEnd");
			conversation.end();
		}
	}
	
//	public List<String> loadImages() {
//
//		logger.info("Loading images");
//		try (Stream<Path> walk = Files.walk(Paths.get("C:\\Users\\m210752\\Pictures"))){
//			return walk
//					.filter(Files::isRegularFile)
//					.map(Path::toFile)
//					.map(File::getName)
//					.filter(ImageDirectoryBean::accept)
//					.sorted()
//					.collect(Collectors.toList());
//		} catch (IOException e) {
//			logger.error("Error getting images", e);
//		}
//		
//		return Collections.emptyList();
//	}
	
	public List<ImgInfo> images() {
		return images;
	}
	
	public List<ImgInfo> imagesInPage(int page) {
		return images.stream().skip(page * maxImagesPerPage).limit(maxImagesPerPage).collect(Collectors.toList());
	}
	
	public int count() {
		return images.size();
	}
	
	public int pages() {
		return pages;
	}
	
//	public static boolean accept(String fileName) {
//		String f = fileName.toLowerCase();
//		return f.endsWith(".jpg") || f.endsWith(".jpeg") || f.endsWith(".png");
//	}
}
