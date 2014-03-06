package org.shirdrn.document.processor.component;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.DocumentAnalyzer;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;
import org.shirdrn.document.processor.utils.ReflectionUtils;

public class DocumentWordsCollector extends AbstractComponent {
	
	private static final Log LOG = LogFactory.getLog(DocumentWordsCollector.class);
	private final DocumentAnalyzer analyzer;
	private final Set<TermFilter> filters = new HashSet<TermFilter>();
	
	public DocumentWordsCollector(Context context) {
		super(context);
		String analyzerClass = context.getConfiguration().get("processor.document.analyzer.class");
		LOG.info("Analyzer class name: class=" + analyzerClass);
		analyzer = ReflectionUtils.getInstance(
				analyzerClass, DocumentAnalyzer.class, new Object[] { context.getConfiguration() });
		// load term filter classes
		String filterClassNames = context.getConfiguration().get("processor.document.filter.classes");
		if(filterClassNames != null) {
			LOG.info("Load filter classes: classNames=" + filterClassNames);
			String[] aClazz = filterClassNames.split("\\s*,\\s*");
			for(String clazz : aClazz) {
				TermFilter filter = ReflectionUtils.getInstance(
						clazz, TermFilter.class,  new Object[] { context });
				if(filter == null) {
					throw new RuntimeException("Fail to reflect: class=" + clazz);
				}
				filters.add(filter);
				LOG.info("Added filter instance: filter=" + filter);
			}
		}
	}
	
	@Override
	public void fire() {
		for(String label : context.getFDMetadata().getInputRootDir().list()) {
			LOG.info("Collect words for: label=" + label);
			File labelDir = new File(context.getFDMetadata().getInputRootDir(), label);
			File[] files = labelDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(context.getFDMetadata().getFileExtensionName());
				}
			});
			LOG.info("Prepare to analyze " + files.length + " files.");
			int n = 0;
			for(File file : files) {
				analyze(label, file);
				++n;
			}
			LOG.info("Analyzed files: count=" + n);
		}
		// output statistics
		stat();
	}
	
	protected void analyze(String label, File file) {
		String doc = file.getAbsolutePath();
		LOG.debug("Process document: label=" + label + ", file=" + doc);
		Map<String, Term> terms = analyzer.analyze(file);
		// filter terms
		filterTerms(terms);
		// construct memory structure
		context.getVectorMetadata().addTerms(label, doc, terms);
		// add inverted table as needed
		context.getVectorMetadata().addTermsToInvertedTable(label, doc, terms);
		LOG.debug("Done: file=" + file + ", termCount=" + terms.size());
		LOG.debug("Terms in a doc: terms=" + terms);
	}

	protected void filterTerms(Map<String, Term> terms) {
		for(TermFilter filter : filters) {
			filter.filter(terms);
		}
	}

	private void stat() {
		LOG.info("STAT: totalDocCount=" + context.getVectorMetadata().getTotalDocCount());
		LOG.info("STAT: labelCount=" + context.getVectorMetadata().getLabelCount());
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> entry = iter.next();
			LOG.info("STAT: label=" + entry.getKey() + ", docCount=" + entry.getValue().size());
		}
	}

}
