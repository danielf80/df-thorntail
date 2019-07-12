package com.df.thorntail.web.srvlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.db.ImageCollectionDb;


@WebServlet({"/app/list/*","/app/list"})
public class ImageListServlet extends HttpServlet {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DEFAULT_CHARSET = "UTF-8"; 
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ImageCollectionDb db;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		logger.info("Getting list");
		
		request.getParameterMap().forEach((k, v) -> logger.info("Param {} = {}", k, v));
		
        response.setContentType("application/json");
        response.setCharacterEncoding(DEFAULT_CHARSET);
        
        
        BsonArray jArray = new BsonArray();
        for (String val : new String[]{"1234", "3455", "5673", "0987", "23454"}) {
        	jArray.add(new BsonString(val));
        }
        
        IOUtils.write(jArray.toString(), response.getOutputStream(), Charset.forName(DEFAULT_CHARSET));
	}
}
