package com.galassiasoft.uhopper;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Sample {

	private int value;
	private long timestamp;

	public Sample() {
		this(0, 0L);
	}

	public Sample(int value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
