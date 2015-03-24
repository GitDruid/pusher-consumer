package com.galassiasoft.uhopper;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;

@Path("/")
public class Consumer{
 	
	static Version serviceVersion;

	@Inject
	private SequencesCache cache;

	@Inject
	private SequencesStorage storage;

	@Inject
	private SequencesFilesystem filesystem;

	@POST
	@Path("/data")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveBatch(SensorReading[] batch) {

		Sample currentSample;
		
		for(SensorReading sr:batch) {
			
			currentSample = new Sample(sr.getValue(), sr.getTimestamp());

			/*
			 * Version 2.0
			 * Logic behind sequence construction MOVED inside the cache to use atomic features of the cache
			 */ 
			Sequence newSequence = cache.appendToMonotonicSequence(sr.getSensor_id(), currentSample);
			if(newSequence != null) {
				
				//Store sequence for current sensor
				storage.saveSequence( newSequence );
				
				//Acquire a distributed lock to check (and eventually save) if it is the longest sequence
				String lock = cache.acquireLock("MAX_SEQUENCE_LENGTH");
				if(lock != null) {
					Integer maxLength;
					try {
						maxLength = Integer.parseInt( cache.getVariable("MAX_SEQUENCE_LENGTH") );
					} catch (Exception e) {
						maxLength = 0;
					}
					
					if(newSequence.getSamples().size() > maxLength) {
						maxLength = newSequence.getSamples().size();
						
						filesystem.saveToFile(newSequence, "LongestSequence.txt");
						
						cache.setVariable("MAX_SEQUENCE_LENGTH", maxLength.toString());
					}
				
					cache.releaseLock("MAX_SEQUENCE_LENGTH", lock);
				}
			}
			
		}
		return Response.status(200).build();
	}
	
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersion() {
		if(serviceVersion == null) {
			serviceVersion = new Version("consumer", 2, "None", "None", "None");
			
			if (cache != null) {
				serviceVersion.setCache(cache.getInfo());
			}
			
			if (storage != null) {
				serviceVersion.setStorage(storage.getInfo());
			}
			
			if (filesystem != null) {
				serviceVersion.setFilesystem(filesystem.getInfo());
			}
		}
		
		return Response.status(200).entity(serviceVersion).build();
	}

}
