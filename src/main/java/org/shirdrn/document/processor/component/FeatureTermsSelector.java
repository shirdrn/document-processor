package org.shirdrn.document.processor.component;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;

public class FeatureTermsSelector extends AbstractComponent {

	private final int keptTermCountEachLabel;
	
	public FeatureTermsSelector(Context context) {
		super(context);
		keptTermCountEachLabel = context.getConfiguration().getInt("processor.each.label.kept.term.count", 3000);
	}

	@Override
	public void fire() {
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
			
		}
	}

}
