package com.df.thorntail.db.pojos;

import org.bson.types.ObjectId;

import com.df.thorntail.db.DbCollection;

@DbCollection("ImgSample")
public final class ImgSample {
	
	public static enum Scale {
		ICON,
		SMALL,
		REDUCED
	}
	
	private ObjectId id;
	private ObjectId infoId;
	private byte[] data;
	private String type;
	private Scale scale; 
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ObjectId getInfoId() {
		return infoId;
	}
	public void setInfoId(ObjectId infoId) {
		this.infoId = infoId;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Scale getScale() {
		return scale;
	}
	public void setScale(Scale scale) {
		this.scale = scale;
	}
}
