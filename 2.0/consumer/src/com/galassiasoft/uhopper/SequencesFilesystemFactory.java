package com.galassiasoft.uhopper;

import org.glassfish.hk2.api.Factory;

public class SequencesFilesystemFactory implements Factory<SequencesFilesystem> {

	private static SequencesFilesystem sf;

	public SequencesFilesystemFactory() {
		
		//Here we'll have some sort of logic, based on properties file or whatever, to decide 
		//which implementation (S3, Local filesystem, etc.) to use and which parameters to pass:
		//...tbd...

		//Hard-coded AWS S3 implementation
		//Hard-coded references to environment variables from s3filesystem container
		//Hard-coded credentials
		
		final String s3Host = System.getenv("S3FILESYSTEM_PORT_4569_TCP_ADDR");
		final String s3Port = System.getenv("S3FILESYSTEM_PORT_4569_TCP_PORT");
		
		if(sf == null){
			try {
				sf = new S3SequencesFilesystem(s3Host, Integer.parseInt(s3Port), "foo", "bar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(SequencesFilesystem sfToDispose) {
		sfToDispose.dispose();
		sfToDispose = null;
	}

	@Override
	public SequencesFilesystem provide() {
		return sf;
	}

}