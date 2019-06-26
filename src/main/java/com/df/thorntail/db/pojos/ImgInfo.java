package com.df.thorntail.db.pojos;

import org.bson.types.ObjectId;

import com.df.thorntail.db.DbCollection;

@DbCollection("ImgInfo")
public final class ImgInfo {
	
	private ObjectId id;
	private String name;
	private String source;
	private String extref;
	private long size;
	private long creationTime;
	private long modifiedTime;
	private ImgMetadata metadata;
	private int height;
	private int width;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getExtref() {
		return extref;
	}
	public void setExtref(String extref) {
		this.extref = extref;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(long createdDttm) {
		this.creationTime = createdDttm;
	}
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public ImgMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(ImgMetadata metadata) {
		this.metadata = metadata;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
}
