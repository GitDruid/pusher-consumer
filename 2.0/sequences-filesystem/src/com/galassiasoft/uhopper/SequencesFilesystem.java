package com.galassiasoft.uhopper;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SequencesFilesystem {

	void saveFile();
	String getInfo();
	void dispose();

}
