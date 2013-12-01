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
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
			Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
			while(docsIter.hasNext()) {
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				String doc = docsEntry.getKey();
				Map<String, Term> terms = docsEntry.getValue();
				for(Entry<String, Term> termEntry : terms.entrySet()) {
					Term term = termEntry.getValue();
					int freq = term.getFreq();
					int termCount = context.getMetadata().getTermCount(label, doc);
					double tf = MetricUtils.tf(freq, termCount);
					int totalDocCount = context.getMetadata().getTotalDocCount();
					int docCountContainingTerm = context.getMetadata().getDocCount(term);
					int docCountContainingTermInThisLabel = context.getMetadata().getDocCountInThisLabel(term);
					double idf = MetricUtils.idf(totalDocCount, docCountContainingTermInThisLabel, docCountContainingTerm);
					termEntry.getValue().setIdf(idf);
					termEntry.getValue().setTf(tf);
					termEntry.getValue().setTfidf(MetricUtils.tfidf(tf, idf));
					LOG.info("Term detail: label=" + label + ", doc=" + doc + ", term=" + term);
				}
			}
		}		
	}

}
