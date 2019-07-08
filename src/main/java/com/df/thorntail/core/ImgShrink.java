package com.df.thorntail.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class ImgShrink {

	public static BufferedImage shrink(BufferedImage img, int targetWidth, int targetHeight, boolean crop) {
		BufferedImage result = img;
		
    	float imgScaleXY = (float)img.getWidth() / (float)img.getHeight();
    	float imgScaleYX = (float)img.getHeight() / (float)img.getWidth();
    	
    	int prevWidth = (int) (targetHeight * imgScaleXY);
    	int prevHeight = (int) (targetWidth * imgScaleYX);
    	
    	int targetSize;
    	int x = 0, y = 0;
    	Scalr.Mode mode = Scalr.Mode.AUTOMATIC;
    	if (prevWidth >= targetWidth) {
    		mode = Scalr.Mode.FIT_TO_HEIGHT;
    		targetSize = targetHeight;
    		x = (prevWidth - targetWidth) / 2;
    	} else {
    		mode = Scalr.Mode.FIT_TO_WIDTH;
    		targetSize = targetWidth;
    		y = (prevHeight - targetHeight) / 2;
    	}
    	
    	result = Scalr.resize(img, mode, targetSize);
    	
    	if (!crop)
    		return result;
    	
		return Scalr.crop(result, x, y, targetWidth, targetHeight);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Path file = Paths.get("C:\\Temp\\WALLPAPERC.JPG");
		
		try (InputStream inputStream = new FileInputStream(file.toFile())){
            BufferedImage buffImage = ImageIO.read(inputStream);
            
            BufferedImage result = shrink(buffImage, 800, 600, false);
            ImageIO.write(result, "jpg", new File("C:\\Temp\\WALLPAPER-D.JPG"));
		}
		
	}
}
