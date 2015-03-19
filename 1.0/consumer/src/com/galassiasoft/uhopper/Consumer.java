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
	
	@POST
	@Path("/data")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveBatch(SensorReading[] batch) {

		Sample currentSample;
		
		for(SensorReading sr:batch) {
			
			currentSample = new Sample(sr.getValue(), sr.getTimestamp());

			/*
			 * Logic behind sequence construction
			 */
			
			if(cache.getLastValue(sr.getSensor_id()) == null) {
				
				//Create new sequence for current sensor with current sample
				cache.startSequence(sr.getSensor_id(), currentSample);
				
			} else if(sr.getValue() > cache.getLastValue(sr.getSensor_id())) {
				
				//Add to sequence for current sensor
				cache.appendToSequence(sr.getSensor_id(), currentSample);
				
			} else {
				
				//Save sequence for current sensor
				storage.saveSequence( cache.getSequence(sr.getSensor_id()) );

				//TODO
				//if(length of sequence for current sensor > length of longest sequence)
					//clone the sequence and start an asynchronous thread to save the file

				//Create new sequence for current sensor with current sample
				cache.startSequence(sr.getSensor_id(), currentSample);
				
			}
			
		}
		return Response.status(200).build();
	}
	
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersion() {
		if(serviceVersion == null) {
			serviceVersion = new Version("consumer", 1, "None", "None");
			
			if (cache != null) {
				serviceVersion.setCache(cache.getInfo());
			}
			
			if (storage != null) {
				serviceVersion.setStorage(storage.getInfo());
			}
		}
		
		return Response.status(200).entity(serviceVersion).build();
	}

}
