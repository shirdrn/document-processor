package org.shirdrn.document.processor.common;

import org.shirdrn.document.processor.config.Configuration;

public class Context {

	private final Configuration configuration;
	private final VectorMetadata vectorMetadata;
	private final FDMetadata fDMetadata;
	private final ProcessorType processorType;
	
	public Context(ProcessorType processorType, String config) {
		this.processorType = processorType;
		this.configuration = new Configuration(config);
		this.vectorMetadata = new VectorMetadata();
		this.fDMetadata = new FDMetadata(processorType, configuration);
	}
	
	public Context() {
		this.processorType = ProcessorType.TRAIN;
		this.configuration = new Configuration();
		this.vectorMetadata = new VectorMetadata();
		this.fDMetadata = new FDMetadata(processorType, configuration);
	}
	
	public FDMetadata getFDMetadata() {
		return fDMetadata;
	}

	public Configuration getConfiguration() {
		return configuration;
	}
	
	public VectorMetadata getVectorMetadata() {
		return vectorMetadata;
	}

	public ProcessorType getProcessorType() {
		return processorType;
	}

}
