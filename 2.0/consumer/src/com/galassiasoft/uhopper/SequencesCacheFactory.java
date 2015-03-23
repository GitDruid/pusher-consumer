package com.galassiasoft.uhopper;

import org.glassfish.hk2.api.Factory;

public class SequencesCacheFactory implements Factory<SequencesCache> {

	private static SequencesCache sc;

	public SequencesCacheFactory() {
		
		//Here we'll have some sort of logic, based on properties file or whatever, to decide 
		//which implementation (Local memory, Redis, etc.) to use and which parameters to pass:
		//...tbd...
		
		/*
		 * Version 1.0
		 * Hard-coded local memory implementation
		 * 		
		if(sc == null){
			try {
				sc = new LocalSequencesCache();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		*/
		
		/*
		 * Version 2.0
		 * Hard-coded redis implementation
		 * Hard-coded references to environment variables from redis container 
		 */
		final String redisHost = System.getenv("REDIS_PORT_6379_TCP_ADDR");
		final String redisPort = System.getenv("REDIS_PORT_6379_TCP_PORT");
		
		if(sc == null){
			try {
				sc = new RedisSequencesCache(redisHost, Integer.parseInt(redisPort));
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