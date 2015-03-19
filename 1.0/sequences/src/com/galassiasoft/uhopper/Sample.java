package com.galassiasoft.uhopper;

public class Sample {

	private Integer value;
	private Long timestamp;
	
	public Sample(Integer value, Long timestamp) {
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
