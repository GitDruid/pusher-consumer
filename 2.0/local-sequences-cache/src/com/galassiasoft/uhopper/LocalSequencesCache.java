package com.galassiasoft.uhopper;

import java.util.HashMap;

import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

@Service @Named
public class LocalSequencesCache implements SequencesCache {

	HashMap<Integer, Sequence> sequences = new HashMap<>(); // <sensor_id, sequence>
	HashMap<Integer, Integer> lastValues = new HashMap<>(); // <sensor_id, lastValue>
	HashMap<String, String> userValues = new HashMap<>(); // <key, value>

	@Override
	public synchronized void startSequence(Integer sensor_id, Sample sample) {
		Sequence s = new Sequence(sensor_id, sample);
		sequences.put(sensor_id, s);
		
		setLastValue(sensor_id, sample.getValue());
	}

	@Override
	public synchronized void appendToSequence(Integer sensor_id, Sample sample) {
		Sequence s = sequences.get(sensor_id);
		
		if(s == null) {
			
			startSequence(sensor_id, sample);
			
		} else {
			
			s.addSample(sample);
			sequences.put(sensor_id, s);
			
			setLastValue(sensor_id, sample.getValue());
		}
	}

	@Override
	public synchronized Sequence getSequence(Integer sensor_id) {
		return sequences.get(sensor_id);
	}
	
	@Override
	public synchronized Integer getLastValue(Integer sensor_id) {
		return lastValues.get(sensor_id);
	}

	private synchronized void setLastValue(Integer sensor_id, Integer value) {
		lastValues.put(sensor_id, value);
	}

	@Override
	public String getInfo() {
		return "Local memory";
	}

	@Override
	public void dispose() {
		// Disposing objects
	}

	@Override
	public synchronized Sequence appendToMonotonicSequence(Integer sensor_id, Sample sample) {
		Sequence closedSequence = null;
		
		if(getLastValue(sensor_id) == null) {
			
			//Create new sequence for current sensor with current sample
			startSequence(sensor_id, sample);
			
		} else if(sample.getValue() > getLastValue(sensor_id)) {
			
			//Add to sequence for current sensor
			appendToSequence(sensor_id, sample);
			
		} else {
			
			//Clone sequence for current sensor
			closedSequence = getSequence(sensor_id).clone();

			//Create new sequence for current sensor with current sample
			startSequence(sensor_id, sample);
			
		}
		
		return closedSequence;
	}

	@Override
	public synchronized String acquireLock(String variableName) {
		return "";
	}

	@Override
	public synchronized boolean releaseLock(String variableName, String lockValue) {
		return true;
	}

	@Override
	public synchronized String getVariable(String variableName) {
		return userValues.get(variableName);
	}

	@Override
	public synchronized void setVariable(String variableName, String variableValue) {
		userValues.put(variableName, variableValue);
	}
}
