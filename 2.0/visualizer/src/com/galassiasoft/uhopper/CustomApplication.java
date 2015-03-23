package com.galassiasoft.uhopper;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
 
public class CustomApplication extends ResourceConfig {
 
    public CustomApplication() {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(SequencesStorageFactory.class).to(SequencesStorage.class);
            }
        });
        
    }
    
}