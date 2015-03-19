package com.galassiasoft.uhopper;

import org.glassfish.hk2.api.Factory;

public class SequencesCacheFactory implements Factory<SequencesCache> {

	private static SequencesCache sc;

	public SequencesCacheFactory() {
		
		//Here we'll have some sort of logic, based on properties file or whatever, to decide 
		//which implementation (Local memory, Redis, etc.) to use and which parameters to pass:
		//...tbd...
		
		//Hard-coded local memory implementation
		
		if(sc == null){
			try {
				sc = new LocalSequencesCache();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(SequencesCache scToDispose) {
		scToDispose.dispose();
		scToDispose = null;
	}

	@Override
	public SequencesCache provide() {
		return sc;
	}

}