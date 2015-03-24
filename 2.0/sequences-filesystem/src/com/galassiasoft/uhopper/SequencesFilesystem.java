package com.galassiasoft.uhopper;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SequencesFilesystem {

	void saveToFile(Sequence sequence, String filename);
	String getInfo();
	void dispose();

}
