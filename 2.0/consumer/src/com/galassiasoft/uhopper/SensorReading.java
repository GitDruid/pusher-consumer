package com.galassiasoft.uhopper;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SensorReading {

	private int sensor_id;
	private int value;
	private long timestamp;
	
	public int getSensor_id() {
		return sensor_id;
	}
	
	public void setSensor_id(int sensor_id) {
		this.sensor_id = sensor_id;
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