package org.shirdrn.document.processor.common;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDatasetManager extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(AbstractDatasetManager.class);
	protected final File inputRootDir;
	protected final File outputDir;
	protected final String outputVectorFile;
	protected final String fileExtensionName;
	protected final File labelVectorFile;
	
	public AbstractDatasetManager(Context context) {
		super(context);
		fileExtensionName = context.getConfiguration().get("processor.dataset.train.file.extension", "");
		LOG.info("Train dataset file extension: name=" + fileExtensionName);
		ProcessorType type = context.getProcessorType();
		if(type == ProcessorType.TRAIN) {
			String trainInputRootDir = context.getConfiguration().get("processor.dataset.train.input.root.dir");
			String train = context.getConfiguration().get("processor.dataset.train.svm.vector.file");
			String trainOutputDir = context.getConfiguration().get("processor.dataset.train.svm.vector.output.dir");
			inputRootDir = new File(trainInputRootDir);
			outputVectorFile = train;
			outputDir = new File(trainOutputDir);
			
		} else if(type == ProcessorType.TEST) {
			String testInputRootDir = context.getConfiguration().get("processor.dataset.test.input.root.dir");
			String test = context.getConfiguration().get("processor.dataset.test.svm.vector.file");
			String testOutputDir = context.getConfiguration().get("processor.dataset.test.svm.vector.output.dir");
			inputRootDir = new File(testInputRootDir);
			outputVectorFile = test;
			outputDir = new File(testOutputDir);
		} else {
			throw new RuntimeException("Undefined processor type!");
		}
		
		String labels = context.getConfiguration().get("processor.dataset.label.vector.file");
		labelVectorFile = new File(labels);
		
		LOG.info("Vector input root directory: outputDir=" + inputRootDir);
		LOG.info("Vector output directory: outputDir=" + outputDir);
		LOG.info("Vector output file: outputFile=" + outputVectorFile);
	}

	@Override
	public void fire() {
	}

}
