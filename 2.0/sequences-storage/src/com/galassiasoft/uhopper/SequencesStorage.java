package com.galassiasoft.uhopper;

import java.util.ArrayList;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SequencesStorage {

	void saveSequence(Sequence sequence);
	ArrayList<Sequence> searchSequence(Long from, Long to, Integer sensor_id);
	String getInfo();
	void dispose();

}
