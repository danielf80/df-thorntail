package com.df.thorntail.entity;

import javax.enterprise.inject.Model;

@Deprecated
@Model
public class ImgInfo {
	
	private long id;
	private String ref;
	private String name;
	private String source;
	private long size;
	private byte[] sample;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
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
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public byte[] getSample() {
		return sample;
	}
	public void setSample(byte[] sample) {
		this.sample = sample;
	}
	
}
