package com.galassiasoft.uhopper;

import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

@Service @Named
public class MongoSequencesStorage implements SequencesStorage {
	
	private static final String DB_NAME = "sequence-storage";
	private static final String COLLECTION_NAME = "sequences";
	
	private MongoClient mongoClient;
	private DB db;
	private DBCollection coll;
	
	public MongoSequencesStorage() throws UnknownHostException {
		this("localhost", 27017);
	}

	public MongoSequencesStorage(String host, int port) throws UnknownHostException {
		mongoClient = new MongoClient( host, port );
		db = mongoClient.getDB(DB_NAME);

	    if (!db.collectionExists(COLLECTION_NAME)) {
	        db.createCollection(COLLECTION_NAME, new BasicDBObject("capped", false));
	    }
	    
	    coll = db.getCollection(COLLECTION_NAME);
	}

	public void saveSequence(Sequence sequence) {
		BasicDBObject sequenceDoc = Mapper.toBasicDBObject(sequence);
		coll.insert(sequenceDoc);		
	}
	
	@Override
	public ArrayList<Sequence> searchSequence(Long from, Long to, Integer sensor_id) {
		DBCursor cursor = coll.find( QueryBuilder.search(from, to, sensor_id) );

		ArrayList<Sequence> sequences = new ArrayList<>(cursor.count());
		BasicDBObject sequenceDoc;
		Sequence sequence;
		
        while (cursor.hasNext()) {
        	sequenceDoc = (BasicDBObject)cursor.next();
        	sequence = Mapper.toSequence(sequenceDoc);

        	sequences.add(sequence);
        }
        
        return sequences;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public String getInfo() {
		return "MongoDB (Client " + mongoClient.getVersion() + ", Server " + db.command("buildInfo").getString("version") + ")";
	}

	@Override
	public void dispose() {
		// Disposing objects
	}
}
