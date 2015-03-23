package com.galassiasoft.uhopper;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;

@Path("/")
public class Visualizer{
 	
	static Version serviceVersion;

	@Inject
	private SequencesStorage storage;
	
	@GET
	@Path("/data")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("sensor_id") final Integer sensor_id, @QueryParam("from") final Long from, @QueryParam("to") final Long to) {
		ArrayList<Sequence> sequences = storage.searchSequence( from, to, sensor_id );
		return Response.status(200).entity(sequences).build();
	}

	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersion() {
		if(serviceVersion == null) {
			serviceVersion = new Version("visualizer", 2, "None", "None");
			
			if (storage != null) {
				serviceVersion.setStorage(storage.getInfo());
			}
		}
		
		return Response.status(200).entity(serviceVersion).build();
	}

}
