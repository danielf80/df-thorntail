package com.df.thorntail.db.pojos;

import com.df.thorntail.db.DbCollection;

@DbCollection("ImgMetadata")
public final class ImgMetadata {
	private String description;
	private String height;
	private String width;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
}
