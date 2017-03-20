package org.shirdrn.document.processor.component;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;

public class BasicInformationCollector extends AbstractComponent {
	
	private static final Log LOG = LogFactory.getLog(BasicInformationCollector.class);//每个模块都有日志记录功能
	
	public BasicInformationCollector(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		// get total document count for computing TF-IDF
		int totalDocCount = 0;
		for(String label : context.getFDMetadata().getInputRootDir().list()) {//语料按类别存放
			context.getVectorMetadata().addLabel(label);//增加新类别
			LOG.info("Add label: label=" + label);
			File labelDir = new File(context.getFDMetadata().getInputRootDir(), label);
			File[] files = labelDir.listFiles(new FileFilter() {//只有指定后缀名的文件才算数
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(context.getFDMetadata().getFileExtensionName());
				}
			});
			context.getVectorMetadata().putLabelledTotalDocCount(label, files.length);//某一类文档的数量
			LOG.info("Put document count: label= " + label + ", docCount=" + files.length);
			totalDocCount += files.length;
		}
		LOG.info("Total documents: totalCount= " + totalDocCount);
		context.getVectorMetadata().setTotalDocCount(totalDocCount);
	}

}
