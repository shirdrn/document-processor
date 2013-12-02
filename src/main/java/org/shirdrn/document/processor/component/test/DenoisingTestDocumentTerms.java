package org.shirdrn.document.processor.component.test;

import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.AbstractDenoisingDocumentTerms;

public class DenoisingTestDocumentTerms extends AbstractDenoisingDocumentTerms {

	public DenoisingTestDocumentTerms(Context context) {
		super(context);
	}

	@Override
	protected int getKeptTermCount(int totalTermCount) {
		double percent = 
				context.getConfiguration().getDouble("processor.dataset.test.denoise.tfidf.maxpercent", 0.5);
		int keptTermCount = (int) Math.round((double) totalTermCount * percent);
		int maxCount = keptTermCount;
		if(keptTermCount == 0) {
			maxCount = context.getConfiguration().getInt("processor.dataset.test.denoise.tfidf.maxCount", 5);
		}
		return maxCount;
	}

}
