package com.galassiasoft.uhopper;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SequencesCache {

	void startSequence(Integer sensor_id, Sample sample);
	void appendToSequence(Integer sensor_id, Sample sample);
	Sequence getSequence(Integer sensor_id);
	Integer getLastValue(Integer sensor_id);
	String getInfo();
	void dispose();

}
