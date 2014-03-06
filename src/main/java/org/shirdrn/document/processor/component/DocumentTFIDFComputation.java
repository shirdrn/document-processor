package org.shirdrn.document.processor.component;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.utils.MetricUtils;

public class DocumentTFIDFComputation extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(DocumentTFIDFComputation.class);
	
	public DocumentTFIDFComputation(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		// for each document, compute TF, IDF, TF-UDF
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
			Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
			while(docsIter.hasNext()) {
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				String doc = docsEntry.getKey();
				Map<String, Term> terms = docsEntry.getValue();
				Iterator<Entry<String, Term>> termsIter = terms.entrySet().iterator();
				while(termsIter.hasNext()) {
					Entry<String, Term> termEntry = termsIter.next();
					String word = termEntry.getKey();
					// check whether word is contained in CHI vector
					if(context.getVectorMetadata().containsChiWord(word)) {
						Term term = termEntry.getValue();
						int freq = term.getFreq();
						int termCount = context.getVectorMetadata().getTermCount(label, doc);
						
						double tf = MetricUtils.tf(freq, termCount);
						int totalDocCount = context.getVectorMetadata().getTotalDocCount();
						int docCountContainingTerm = context.getVectorMetadata().getDocCount(term);
						
						double idf = MetricUtils.idf(totalDocCount, docCountContainingTerm);
						termEntry.getValue().setIdf(idf);
						termEntry.getValue().setTf(tf);
						termEntry.getValue().setTfidf(MetricUtils.tfidf(tf, idf));
						LOG.debug("Term detail: label=" + label + ", doc=" + doc + ", term=" + term);
					} else {
						// remove term not contained in CHI vector
						termsIter.remove();
						LOG.debug("Not in CHI vector: word=" + word);
					}
				}
			}
		}		
	}

}
