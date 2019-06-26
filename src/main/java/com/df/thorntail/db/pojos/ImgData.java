package com.df.thorntail.db.pojos;

import org.bson.types.ObjectId;

import com.df.thorntail.db.DbCollection;

@DbCollection("ImgData")
public final class ImgData {
	private ObjectId id;
	private ObjectId infoId;
	private byte[] data;
	
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
}
