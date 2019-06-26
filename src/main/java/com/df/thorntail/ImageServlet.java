package com.df.thorntail;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.core.InMemoryDb;
import com.df.thorntail.entity.ImgInfo;


@WebServlet("/app/image/*")
public class ImageServlet extends HttpServlet {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private InMemoryDb db;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// Get requested image by path info.
        String requestedImage = request.getPathInfo();
        
        // Check if file name is actually supplied to the request URI.
        if (requestedImage == null) {
            // Do your thing if the image is not supplied to the request URI.
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        
        String decodedUrl = URLDecoder.decode(requestedImage, "UTF-8").substring(1);
        
        logger.info("Loading image: {} ({})", requestedImage, decodedUrl);
        ImgInfo imgInfo = null;
        try {
	        final long rqstId = Long.parseLong(decodedUrl);
	        Optional<ImgInfo> optImage = db.getImages().stream().filter(i -> i.getId() == rqstId).findFirst();
	        imgInfo = optImage.orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e) {
        	logger.warn("Invalid image request link: {}", decodedUrl, e);
        	response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
        	return;
        }
        
        String filename = imgInfo.getRef();
        
        // Get content type by filename.
        String contentType = getServletContext().getMimeType(filename);
        
        // Check if file is actually an image (avoid download of other files by hackers!).
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        if (contentType == null || !contentType.startsWith("image")) {
        	logger.warn("Content type not acceptable: {}", contentType);
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        
        if (imgInfo.getSample() == null || imgInfo.getSample().length == 0) {
        	response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
        	return;
        }
        IOUtils.write(imgInfo.getSample(), response.getOutputStream());
        
//        Path imgPath = Paths.get(filename);
//        if (imgPath == null) {
//        	logger.warn("Null InputStream");
//            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
//            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
//            return;
//        }
//		Files.copy(imgPath, response.getOutputStream());
	}
}
