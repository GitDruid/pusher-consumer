package com.galassiasoft.uhopper;

import org.glassfish.hk2.api.Factory;

public class SequencesStorageFactory implements Factory<SequencesStorage> {

	private static SequencesStorage ss;

	public SequencesStorageFactory() {
		
		//Here we'll have some sort of logic, based on properties file or whatever, to decide 
		//which implementation (MongoDB, MySQL, etc.) to use and which parameters to pass:
		//...tbd...

		//Hard-coded MongoDB implementation
		//Hard-coded references to environment variables from mongo container
		
		final String mongoHost = System.getenv("MONGO_PORT_27017_TCP_ADDR");
		final String mongoPort = System.getenv("MONGO_PORT_27017_TCP_PORT");
		
		if(ss == null){
			try {
				ss = new MongoSequencesStorage(mongoHost, Integer.parseInt(mongoPort));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(SequencesStorage ssToDispose) {
		ssToDispose.dispose();
		ssToDispose = null;
	}

	@Override
	public SequencesStorage provide() {
		return ss;
	}

}