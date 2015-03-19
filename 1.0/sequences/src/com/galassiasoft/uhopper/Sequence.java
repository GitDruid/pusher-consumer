package com.galassiasoft.uhopper;

import java.util.ArrayList;

/*
 * Simple <value, timestamp> collection
 */
public class Sequence {
	
	private Integer sensor_id;
	private ArrayList<Sample> samples;
	
	public Sequence(Integer sensor_id) {
		this(sensor_id, null);
	}

	public Sequence(Integer sensor_id, Sample sample) {
		this.sensor_id = sensor_id;
		this.samples = new ArrayList<Sample>();
		if(sample != null) {
			this.samples.add(sample);
		}
	}
	
	public Integer getSensor_id() {
		return sensor_id;
	}

	public ArrayList<Sample> getSamples() {
		return samples;
	}

	public void addSample(Sample sample) {
		this.samples.add(sample);
	}

}
