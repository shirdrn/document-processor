package org.shirdrn.document.processor.component;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.utils.SortUtils;

public abstract class AbstractDenoisingDocumentTerms extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(AbstractDenoisingDocumentTerms.class);
	
	public AbstractDenoisingDocumentTerms(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		double maxTFIDFPercent = getMaxTFIDFPercent();
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			Iterator<Entry<String, Map<String, Term>>> docsIter = labelledDocsEntry.getValue().entrySet().iterator();
			while(docsIter.hasNext()) {
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				String doc = docsEntry.getKey();
				// terms contained by a doc
				Map<String, Term> terms = docsEntry.getValue();
				int keptTermCount = (int) Math.round((double) terms.size() * maxTFIDFPercent);
				LOG.debug("termCount=" + terms.size() + ", keptTermCount=" + keptTermCount);
				// sort by TF-IDF
				LOG.info("Sort by TDIDF for document: doc=" + doc);
				Entry<String, Term>[] a = sort(terms, Math.max(keptTermCount, 1));
				if(LOG.isDebugEnabled()) {
					StringBuffer buf = new StringBuffer();
					for(int i=0; i<keptTermCount - 1; i++) {
						buf.append(a[i]).append(", ");
					}
					buf.append(a[keptTermCount - 1]);
					LOG.debug("Kept terms: " + buf.toString());
				}
				// remove noising terms
				for(int i=keptTermCount; i<a.length; i++) {
					terms.remove(a[i].getKey());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Entry<String, Term>[] sort(Map<String, Term> terms, int keptTermCount) {
		Entry<String, Term>[] a = new Entry[terms.size()];
		a = terms.entrySet().toArray(a);
		SortUtils.heapSort(a, true, keptTermCount);
		return a;
	}
	
	protected abstract double getMaxTFIDFPercent();

}
