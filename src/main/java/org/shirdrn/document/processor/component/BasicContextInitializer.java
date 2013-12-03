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
		// get total document count for computing TF-IDF
		int totalDocCount = 0;
		for(String label : inputRootDir.list()) {
			context.getVectorMetadata().addLabel(label);
			LOG.info("Add label: label=" + label);
			File labelDir = new File(inputRootDir, label);
			File[] files = labelDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(fileExtensionName);
				}
			});
			context.getVectorMetadata().putLabelledTotalDocCount(label, files.length);
			LOG.info("Put document count: label= " + label + ", docCount=" + files.length);
			totalDocCount += files.length;
		}
		LOG.info("Total documents: totalCount= " + totalDocCount);
		context.getVectorMetadata().setTotalDocCount(totalDocCount);
	}

}
