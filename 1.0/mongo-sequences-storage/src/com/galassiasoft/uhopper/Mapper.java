package com.galassiasoft.uhopper;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

/*
 * Document format:
 * 
{
	"_id" : ObjectId("507f191e810c19729de860ea"),
	"sensor_id" : 3,
	"length" : 3,
	"minTimestamp" : 1406810583123,
	"maxTimestamp" : 1406810583723,
	"samples" : [
				{"value" : 100, "timestamp" : 1406810583123},
				{"value" : 130, "timestamp" : 1406810583223},
				{"value" : 200, "timestamp" : 1406810583723}
			]
} 
 */
public class Mapper {

	public static BasicDBObject toBasicDBObject(Sequence sequence) {
		BasicDBList sequenceSamples = new BasicDBList();
		Long minTimestamp = Long.MAX_VALUE;
		Long maxTimestamp = Long.MIN_VALUE;
		
		for(Sample s:sequence.getSamples()) {
			if(s.getTimestamp() < minTimestamp) minTimestamp = s.getTimestamp();
			if(s.getTimestamp() > maxTimestamp) maxTimestamp = s.getTimestamp();
			sequenceSamples.add(new BasicDBObject("value", s.getValue()).append("timestamp", s.getTimestamp()));
		}
		
		BasicDBObject sequenceDoc = new BasicDBObject("_id", new ObjectId())
								      .append("sensor_id", sequence.getSensor_id())
						              .append("length", sequenceSamples.size())
						              .append("minTimestamp", minTimestamp)
						              .append("maxTimestamp", maxTimestamp)
						              .append("samples", sequenceSamples);
		return sequenceDoc;
	}
	
	public static Sequence toSequence(BasicDBObject sequenceDoc) {
		BasicDBList sequenceSamples = (BasicDBList)sequenceDoc.get("samples");
		Sequence sequence = new Sequence(sequenceDoc.getInt("sensor_id"));

		BasicDBObject sequenceSample;
		Sample sample;

		for(int i=0; i<sequenceSamples.size(); i++) {
    		sequenceSample = (BasicDBObject)sequenceSamples.get(i);
    		sample = new Sample(sequenceSample.getInt("value"), sequenceSample.getLong("timestamp"));
    		sequence.addSample(sample);
    	}

    	return sequence;
	}
}
