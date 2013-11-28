package org.shirdrn.document.processor.common;

import org.shirdrn.document.processor.config.Configuration;


public class Context {

	private final Configuration configuration;
	private final Metadata metadata;
	
	public Context() {
		this.configuration = new Configuration();
		this.metadata = new Metadata();
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	
}
