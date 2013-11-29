package org.shirdrn.document.processor.component;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractDatasetManager;
import org.shirdrn.document.processor.common.Context;

public class BasicContextInitializer extends AbstractDatasetManager {
	
	private static final Log LOG = LogFactory.getLog(BasicContextInitializer.class);
	
	public BasicContextInitializer(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		super.fire();
		String labelDirs = context.getConfiguration().get("processor.dataset.train.label.dirs");
		for(String label : labelDirs.split("\\s*,\\s*")) {
			context.getMetadata().addLabel(label.trim());
		}
		
		// get total document count for computing TF-IDF
		int totalDocCount = 0;
		for(String label : inputRootDir.list()) {
			context.getMetadata().addLabel(label);
			LOG.info("Add label: label=" + label);
			File labelDir = new File(inputRootDir, label);
			File[] files = labelDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(fileExtensionName);
				}
			});
			context.getMetadata().putLabelledTotalDocCount(label, files.length);
			LOG.info("Put document count: label= " + label + ", docCount=" + files.length);
			totalDocCount += files.length;
		}
		LOG.info("Total documents: totalCount= " + totalDocCount);
		context.getMetadata().setTotalDocCount(totalDocCount);
	}

}
