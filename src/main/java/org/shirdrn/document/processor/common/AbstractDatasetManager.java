package org.shirdrn.document.processor.common;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.utils.CheckUtils;

public abstract class AbstractDatasetManager extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(AbstractDatasetManager.class);
	protected File inputRootDir;
	protected File outputDir;
	protected String outputVectorFile;
	protected String fileExtensionName;
	protected boolean isTrainOpen;
	
	protected boolean isTestOpen;
	protected File termVectorFile;
	protected File labelVectorFile;
	
	public AbstractDatasetManager(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		fileExtensionName = context.getConfiguration().get("processor.dataset.file.extension", "");
		String termsFile = context.getConfiguration().get("processor.dataset.term.vector.file");
		CheckUtils.checkNotNull(termsFile, "processor.dataset.term.vector.file");
		termVectorFile = new File(termsFile);
		
		String labelsFile = context.getConfiguration().get("processor.dataset.label.vector.file");
		CheckUtils.checkNotNull(labelsFile, "processor.dataset.label.vector.file");
		labelVectorFile = new File(labelsFile);
		
		LOG.info("Train dataset file extension: name=" + fileExtensionName);
		isTrainOpen = context.getConfiguration().getBoolean("processor.dataset.train.isopen", false);
		isTestOpen = context.getConfiguration().getBoolean("processor.dataset.test.isopen", false);
		
		// at least one is opened
		if((isTrainOpen && isTestOpen) || (!isTrainOpen && !isTestOpen)) {
			throw new RuntimeException("Please set open flag for TRAINING, or TEST, not BOTH!");			
		}
		if(isTrainOpen) {
			String trainInputRootDir = context.getConfiguration().get("processor.dataset.train.input.root.dir");
			String train = context.getConfiguration().get("processor.dataset.train.svm.vector.file");
			String trainOutputDir = context.getConfiguration().get("processor.dataset.train.svm.vector.output.dir");
			
			CheckUtils.checkNotNull(trainInputRootDir, "processor.dataset.train.input.root.dir");
			CheckUtils.checkNotNull(train, "processor.dataset.train.svm.vector.file");
			CheckUtils.checkNotNull(trainOutputDir, "processor.dataset.train.svm.vector.output.dir");
			
			inputRootDir = new File(trainInputRootDir);
			outputVectorFile = train;
			outputDir = new File(trainOutputDir);
			
		} else {
			String testInputRootDir = context.getConfiguration().get("processor.dataset.test.input.root.dir");
			String test = context.getConfiguration().get("processor.dataset.test.svm.vector.file");
			String testOutputDir = context.getConfiguration().get("processor.dataset.test.svm.vector.output.dir");
			
			CheckUtils.checkNotNull(testInputRootDir, "processor.dataset.test.input.root.dir");
			CheckUtils.checkNotNull(test, "processor.dataset.test.svm.vector.file");
			CheckUtils.checkNotNull(testOutputDir, "processor.dataset.test.svm.vector.output.dir");
			
			inputRootDir = new File(testInputRootDir);
			outputVectorFile = test;
			outputDir = new File(testOutputDir);
			
			CheckUtils.checkFileExists(termVectorFile);
		}
		CheckUtils.checkFileExists(inputRootDir);
		CheckUtils.checkFileExists(outputDir);
		
		// log file or directory information
		LOG.info("Vector input root directory: outputDir=" + inputRootDir);
		LOG.info("Vector output directory: outputDir=" + outputDir);
		LOG.info("Vector output file: outputFile=" + outputVectorFile);
	}

}
