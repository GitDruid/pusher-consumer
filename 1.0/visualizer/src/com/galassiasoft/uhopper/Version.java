package com.galassiasoft.uhopper;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Version {

	private String name;
	private int release;
	private String cache;
	private String storage;
	
	public Version() {
	}
	
	public Version(String name, int release, String cache, String storage) {
		this.name = name;
		this.release = release;
		this.cache = cache;
		this.storage = storage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRelease() {
		return release;
	}

	public void setRelease(int release) {
		this.release = release;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}
}