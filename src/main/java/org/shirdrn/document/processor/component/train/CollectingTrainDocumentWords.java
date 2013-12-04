package org.shirdrn.document.processor.component.train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;
import org.shirdrn.document.processor.component.AbstractDocumentWordsCollector;
import org.shirdrn.document.processor.filter.LexicalCategoryFilter;
import org.shirdrn.document.processor.filter.SingleWordTermFilter;
import org.shirdrn.document.processor.filter.StopwordsTermFilter;
import org.shirdrn.document.processor.utils.ReflectionUtils;

public class CollectingTrainDocumentWords extends AbstractDocumentWordsCollector {

	private static final Log LOG = LogFactory.getLog(CollectingTrainDocumentWords.class);
	
	private final List<TermFilter> filters = new ArrayList<TermFilter>(0);
	@SuppressWarnings("unchecked")
	private final Class<TermFilter>[] filterClasses = new Class[] {
		LexicalCategoryFilter.class,
		SingleWordTermFilter.class, 
		StopwordsTermFilter.class
	};
	
	private int labelNumber = 0;
	private int wordNumber = 0;
	
	public CollectingTrainDocumentWords(Context context) {
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
		super.fire();
		// collect all terms
		// store all <label, labelId> pairs
		Map<String, Integer> globalLabelToIdMap = new HashMap<String, Integer>(0);
		// store all <labelId, label> pairs
		Map<Integer, String> globalIdToLabelMap = new HashMap<Integer, String>(0);
		// store all <word, wordId> pairs
		Map<String, Integer> globalTermsMap = new HashMap<String, Integer>(0);
		// iterate collected document words
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			// generate label id
			Integer labelId = globalLabelToIdMap.get(label);
			if(labelId == null) {
				++labelNumber;
				labelId = labelNumber;
				globalLabelToIdMap.put(label, labelId);
				globalIdToLabelMap.put(labelId, label);
			}
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
			Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
			while(docsIter.hasNext()) {
				StringBuffer line = new StringBuffer();
				line.append(labelId).append(" ");
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				Map<String, Term> terms = docsEntry.getValue();
				for(Entry<String, Term> termEntry : terms.entrySet()) {
					String word = termEntry.getKey();
					// generate word id
					Integer wordId = globalTermsMap.get(word);
					if(wordId == null) {
						++wordNumber;
						wordId = wordNumber;
						globalTermsMap.put(word, wordId);
					}
				}
			}
		}
		
		// store metadata
		context.getVectorMetadata().putLabelToIdPairs(globalLabelToIdMap);
		context.getVectorMetadata().putIdToLabelPairs(globalIdToLabelMap);
		
	}

	@Override
	protected void filterTerms(Map<String, Term> terms) {
		for(TermFilter filter : filters) {
			filter.filter(terms);
		}		
	}

}
