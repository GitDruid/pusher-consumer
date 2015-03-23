package com.galassiasoft.uhopper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

/*
 * Query format:
 * 
{ "$and" : [
            { "sensor_id" : 3 },
            { "$or" : [
                       { "minTimestamp" : { "$gte": 1406810583123, "$lte": 1406810583523 } },
                       { "maxTimestamp" : { "$gte": 1406810583123, "$lte": 1406810583523 } }
                      ]
            }
           ]
}
 */
public class QueryBuilder {

	public static BasicDBObject search(Long from, Long to, Integer sensor_id) {
	
		BasicDBObject sensorIdQueryDoc = new BasicDBObject();
		if(sensor_id != null) sensorIdQueryDoc.put("sensor_id", sensor_id);

		BasicDBObject minQueryDoc = new BasicDBObject();
		minQueryDoc.put("minTimestamp", new BasicDBObject("$gte", from).append("$lte", to));

		BasicDBObject maxQueryDoc = new BasicDBObject();
		maxQueryDoc.put("maxTimestamp", new BasicDBObject("$gte", from).append("$lte", to));

		BasicDBList orQueriesList = new BasicDBList();
		orQueriesList.add(minQueryDoc);
		orQueriesList.add(maxQueryDoc);
		
		BasicDBObject orQueryDoc = new BasicDBObject();
		orQueryDoc.put("$or", orQueriesList);

		BasicDBList andQueriesList = new BasicDBList();
		andQueriesList.add(sensorIdQueryDoc);
		andQueriesList.add(orQueryDoc);

		BasicDBObject andQueryDoc = new BasicDBObject();
		andQueryDoc.put("$and", andQueriesList);

		return andQueryDoc;
	}
	
}
