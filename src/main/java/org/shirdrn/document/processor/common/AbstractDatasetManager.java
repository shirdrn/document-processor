package org.shirdrn.document.processor.common;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.utils.CheckUtils;

public abstract class AbstractDatasetManager extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(AbstractDatasetManager.class);
	protected final File inputRootDir;
	protected final File outputDir;
	protected final String outputVectorFile;
	protected final String fileExtensionName;
	protected final boolean isTrainOpen;
	protected final boolean isTestOpen;
	
	public AbstractDatasetManager(Context context) {
		super(context);
		fileExtensionName = context.getConfiguration().get("processor.dataset.train.file.extension", "");
		LOG.info("Train dataset file extension: name=" + fileExtensionName);
		isTrainOpen = context.getConfiguration().getBoolean("processor.dataset.train.isopen", false);
		isTestOpen = context.getConfiguration().getBoolean("processor.dataset.test.isopen", false);
		if(isTrainOpen && isTestOpen) {
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
		}
		
		// log file or directory information
		LOG.info("Vector input root directory: outputDir=" + inputRootDir);
		LOG.info("Vector output directory: outputDir=" + outputDir);
		LOG.info("Vector output file: outputFile=" + outputVectorFile);
	}

	@Override
	public void fire() {
	}

}
