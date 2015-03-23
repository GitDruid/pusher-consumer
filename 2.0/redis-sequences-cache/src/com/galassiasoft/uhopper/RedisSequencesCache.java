package com.galassiasoft.uhopper;

import java.util.List;
import java.util.UUID;

import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import com.owlike.genson.Genson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

@Service @Named
public class RedisSequencesCache implements SequencesCache {

	private static final String SEQUENCE_KEY_PREFIX = "sequence_";
	private static final String LASTSAMPLE_KEY_PREFIX = "lastSample_";
	private static final String LOCK_KEY_PREFIX = "lock_";
    private static final int retryLimit = 10;
	
	private static JedisPool pool;

	public RedisSequencesCache() {
		this("localhost", 6379);
	}
	
	public RedisSequencesCache(String host, int port) {
		pool = new JedisPool(new JedisPoolConfig(), host, port);
	}
	
	private String LASTSAMPLE_KEY(int sensor_id) {
		return LASTSAMPLE_KEY_PREFIX + sensor_id;
	}
	
	private String SEQUENCE_KEY(int sensor_id){
		return SEQUENCE_KEY_PREFIX + sensor_id;
	}

	private String LOCK_KEY(String lockName){
		return LOCK_KEY_PREFIX + lockName;
	}

	@Override
	public void startSequence(Integer sensor_id, Sample sample) {
		try (Jedis jedis = pool.getResource()) {
			Transaction t = jedis.multi();

			t.del(SEQUENCE_KEY(sensor_id));

			String jsonSample = new Genson().serialize(sample);
			t.rpush(SEQUENCE_KEY(sensor_id), jsonSample);
			t.set(LASTSAMPLE_KEY(sensor_id), jsonSample);

			t.exec();
		}
	}

	@Override
	public void appendToSequence(Integer sensor_id, Sample sample) {
		try (Jedis jedis = pool.getResource()) {
			Transaction t = jedis.multi();

			String jsonSample = new Genson().serialize(sample);
			t.rpush(SEQUENCE_KEY(sensor_id), jsonSample);
			t.set(LASTSAMPLE_KEY(sensor_id), jsonSample);

			t.exec();
		}
	}

	@Override
	public Sequence getSequence(Integer sensor_id) {
		Sequence sensorSequence = new Sequence(sensor_id);
		
		try (Jedis jedis = pool.getResource()) {
			List<String> jsonSequence = jedis.lrange(SEQUENCE_KEY(sensor_id), 0, -1);
			
			for(String jsonSample:jsonSequence){
				Sample sample = new Genson().deserialize(jsonSample, Sample.class);
				sensorSequence.addSample(sample);
			}
		}

		return sensorSequence;
	}
	
	@Override
	public Integer getLastValue(Integer sensor_id) {
		Integer sensorLastValue = null;
		
		try (Jedis jedis = pool.getResource()) {
			String jsonSample = jedis.get(LASTSAMPLE_KEY(sensor_id));
			if(jsonSample != null) {
				Sample sample = new Genson().deserialize(jsonSample, Sample.class);
				sensorLastValue = sample.getValue();
			}
		}

		return sensorLastValue;
	}

	@Override
	public String getInfo() {
		return "Redis";
	}

	@Override
	public void dispose() {
		pool.destroy();
	}

	@Override
	public Sequence appendToMonotonicSequence(Integer sensor_id, Sample currentSample) {
		Sequence closedSequence = appendToMonotonicSequenceOptimisticLock(sensor_id, currentSample);
		
		//TODO: implement retry in case of optimistic lock failure.
		//Note: it is not needed since if OL is failing, this means that a newer sample was already saved.
		
		return closedSequence;
	}

	private Sequence appendToMonotonicSequenceOptimisticLock(Integer sensor_id, Sample currentSample) {
		
		Sequence closedSequence = null;
		Response<List<String>> jsonClosedSequenceResponse = null;
		
		String jsonCurrentSample = new Genson().serialize(currentSample);
		
		try (Jedis jedis = pool.getResource()) {
			
			jedis.watch(LASTSAMPLE_KEY(sensor_id), SEQUENCE_KEY(sensor_id));
			
			String jsonPreviousSample = jedis.get(LASTSAMPLE_KEY(sensor_id));
			
			Transaction t = jedis.multi();
			
			if(jsonPreviousSample == null) { 

				//Start a new sequence for current sensor with current sample
				transactionalAppendAndStoreSample(sensor_id, jsonCurrentSample, t);

			} else { 
				
				Sample previousSample = new Genson().deserialize(jsonPreviousSample, Sample.class);
				
				/* 
				 * Sequence structure logic.
				 * TODO: it can be redesigned to have a more general solution.
				 */
				if( currentSample.getValue() > previousSample.getValue() &&
						currentSample.getTimestamp() >= previousSample.getTimestamp() ) {
					
					//Add to sequence for current sensor
					transactionalAppendAndStoreSample(sensor_id, jsonCurrentSample, t);
					
				} else if( currentSample.getValue() <= previousSample.getValue() &&
						currentSample.getTimestamp() >= previousSample.getTimestamp() ) {
					
					//Prepare to return closed sequence for current sensor 
					jsonClosedSequenceResponse = t.lrange(SEQUENCE_KEY(sensor_id), 0, -1);

					//Re-Start (delete and create) a new sequence for current sensor with current sample
					transactionalDeleteSequence(sensor_id, t);
					transactionalAppendAndStoreSample(sensor_id, jsonCurrentSample, t);
					
				} else {
					
					// currentSample.getTimestamp() < previousSample.getTimestamp()
					// Too late: Sample is discarded ! 
					
				}
			}
			
			t.exec();
		}

		//TODO Manage the error notification in case of optimistic lock failure
		
		if(jsonClosedSequenceResponse != null) {
			closedSequence = new Sequence(sensor_id);
			for(String jsonSample:jsonClosedSequenceResponse.get()){
				Sample sample = new Genson().deserialize(jsonSample, Sample.class);
				closedSequence.addSample(sample);
			}
		}

		return closedSequence;
	}

	private void transactionalAppendAndStoreSample(Integer sensor_id, String jsonSample, Transaction transaction) {
		//Add to sequence for sensor_id. Create sequence if it doesn't exists.
		transaction.rpush(SEQUENCE_KEY(sensor_id), jsonSample);
		
		//Store last sample for sensor_id.
		transaction.set(LASTSAMPLE_KEY(sensor_id), jsonSample);
		
		//TODO ?
		//transaction.incr(SEQUENCELENGTH_KEY(sensor_id));
	}

	private void transactionalDeleteSequence(Integer sensor_id, Transaction transaction) {
		transaction.del(SEQUENCE_KEY(sensor_id));
		
		//TODO ?
		//transaction.del(SEQUENCELENGTH_KEY(sensor_id));
	}

	@Override
	public String acquireLock(String variableName) {
		String lockValue = UUID.randomUUID().toString();
		
		int i = 0;
		long delay = 10;
        String result = null;
        
        try (Jedis jedis = pool.getResource()) {

            while (result == null && i < retryLimit) {
            	
                try {
                	//Exponential backoff
                	//10, 20, 40, 80, 160, 320, 640, 1280, 2560, 5120
                    Thread.sleep(delay << i);
                } catch (InterruptedException e) {
                    new RuntimeException(e);
                }

			    result = jedis.set(LOCK_KEY(variableName), lockValue, "nx", "px", 30000L);
			    i++;
			    
            }
		}
        
		if(result != null) {
			return lockValue;
		} else {
			return null;
		}
        
	}
	
	@Override
	public boolean releaseLock(String variableName, String lockValue) {
		try (Jedis jedis = pool.getResource()) {
			String storedLockValue = jedis.get(LOCK_KEY(variableName));
			
			if(lockValue.equals(storedLockValue)) {
				jedis.del(LOCK_KEY(variableName));
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String getVariable(String variableName){
		try (Jedis jedis = pool.getResource()) {
			return jedis.get(variableName);
		}
	}
	
	@Override
	public void setVariable(String variableName, String variableValue){
		try (Jedis jedis = pool.getResource()) {
			jedis.set(variableName, variableValue);
		}		
	}

}
