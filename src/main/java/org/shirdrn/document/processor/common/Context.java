package org.shirdrn.document.processor.common;

import org.shirdrn.document.processor.config.Configuration;

public class Context {

	private final Configuration configuration;
	private final VectorMetadata vectorMetadata;
	private final FileMetadata fileMetadata;
	private ProcessorType processorType = ProcessorType.TRAIN;
	
	public Context(String config) {
		this.configuration = new Configuration(config);
		this.vectorMetadata = new VectorMetadata();
		this.fileMetadata = new FileMetadata();
	}
	
	public Context() {
		this.configuration = new Configuration();
		this.vectorMetadata = new VectorMetadata();
		this.fileMetadata = new FileMetadata();
	}
	
	public FileMetadata getFileMetadata() {
		return fileMetadata;
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

	public void setProcessorType(ProcessorType processorType) {
		this.processorType = processorType;
	}
	
}
