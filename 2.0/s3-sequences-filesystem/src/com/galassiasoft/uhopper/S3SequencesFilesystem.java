package com.galassiasoft.uhopper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.owlike.genson.Genson;

@Service @Named
public class S3SequencesFilesystem implements SequencesFilesystem {

	private AmazonS3Client s3Client;
	
	public S3SequencesFilesystem() {
		this("localhost", 4569, "foo", "bar");
	}
	
	public S3SequencesFilesystem(String host, int port, String accessKey, String secretKey) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		s3Client = new AmazonS3Client(credentials);
		s3Client.setEndpoint("http://" + host + ":" + port);
		s3Client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
		try {
			s3Client.createBucket("com.galassiasoft.bucket");
		} catch (AmazonServiceException e) {
			//already created
		}
	}
	
	@Override
	public void saveToFile(Sequence sequence, String filename) {
		String jsonSequence = new Genson().serialize(sequence);
		
		ObjectMetadata om = new ObjectMetadata();
		om.setContentType("application/json");
		om.setContentLength(jsonSequence.length());
		om.setContentEncoding("UTF-8");
		
		s3Client.putObject("com.galassiasoft.bucket", "longest-sequence", getStream(jsonSequence), om);
	}

	@Override
	public String getInfo() {
		return "AWS S3 (" + s3Client.getServiceName() + ")";
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	private static InputStream getStream(String s) {
		return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
	}
	
}
