package org.shirdrn.document.processor.component.train;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.component.AbstractDocumentWordsCollector;

public class CollectingTrainDocumentWords extends AbstractDocumentWordsCollector {

	private static final Log LOG = LogFactory.getLog(CollectingTrainDocumentWords.class);
	
	private int labelNumber = 0;
	private int wordNumber = 0;
	
	public CollectingTrainDocumentWords(Context context) {
		super(context);
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

}
