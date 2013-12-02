package org.shirdrn.document.processor.component.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;
import org.shirdrn.document.processor.component.AbstractDocumentWordsCollector;
import org.shirdrn.document.processor.filter.LabelVectorFilter;
import org.shirdrn.document.processor.filter.LexicalCategoryFilter;
import org.shirdrn.document.processor.filter.SingleWordTermFilter;
import org.shirdrn.document.processor.filter.StopwordsTermFilter;
import org.shirdrn.document.processor.filter.TermVectorFilter;
import org.shirdrn.document.processor.utils.ReflectionUtils;

public class CollectingTestDocumentWords extends AbstractDocumentWordsCollector {

	private static final Log LOG = LogFactory.getLog(CollectingTestDocumentWords.class);
	private final List<TermFilter> filters = new ArrayList<TermFilter>(0);
	@SuppressWarnings("unchecked")
	private final Class<TermFilter>[] filterClasses = new Class[] {
		LexicalCategoryFilter.class,
		SingleWordTermFilter.class, 
		StopwordsTermFilter.class,
		LabelVectorFilter.class,
		TermVectorFilter.class
	};
	
	public CollectingTestDocumentWords(Context context) {
		super(context);
		for(Class<TermFilter> filterClass : filterClasses) {
			TermFilter instance = ReflectionUtils.newInstance(filterClass, TermFilter.class, new Object[] { context });
			if(instance == null) {
				throw new RuntimeException("Fail to reflect: class=" + filterClass.getName());
			}
			filters.add(instance);
			LOG.info("Load filter class: class=" + filterClass.getName());
		}
	}

	@Override
	public void fire() {
		// analyze and collect words
		super.fire();
	}
	
	@Override
	protected void filterTerms(Map<String, Term> terms) {
		for(TermFilter filter : filters) {
			filter.filter(terms);
		}
	}
	
}
