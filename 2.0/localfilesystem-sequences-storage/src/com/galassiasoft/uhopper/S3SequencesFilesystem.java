package com.galassiasoft.uhopper;

import java.util.ArrayList;

public class S3SequencesFilesystem implements SequencesStorage {

	@Override
	public void saveSequence(Sequence sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Sequence> searchSequence(Long from, Long to, Integer sensor_id) {
		//Not implemented
		return null;
	}

	@Override
	public String getInfo() {
		return "Local filesystem";
	}

	@Override
	public void dispose() {
		// Disposing objects
	}

}
