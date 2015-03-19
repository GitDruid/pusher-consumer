package com.galassiasoft.uhopper;

import java.util.HashMap;

import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

@Service @Named
public class LocalSequencesCache implements SequencesCache {

	HashMap<Integer, Sequence> sequences = new HashMap<>(); // <sensor_id, sequence>
	HashMap<Integer, Integer> lastValues = new HashMap<>(); // <sensor_id, lastValue>

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

}
