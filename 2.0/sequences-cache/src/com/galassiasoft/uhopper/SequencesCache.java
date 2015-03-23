package com.galassiasoft.uhopper;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SequencesCache {

	// V 1.0
	void startSequence(Integer sensor_id, Sample sample);
	void appendToSequence(Integer sensor_id, Sample sample);
	Sequence getSequence(Integer sensor_id);
	Integer getLastValue(Integer sensor_id);
	String getInfo();
	void dispose();
	
	// V 2.0
	Sequence appendToMonotonicSequence(Integer sensor_id, Sample sample);
	String acquireLock(String variableName);
	boolean releaseLock(String variableName, String lockValue);
	String getVariable(String variableName);
	void setVariable(String variableName, String variableValue);

}
