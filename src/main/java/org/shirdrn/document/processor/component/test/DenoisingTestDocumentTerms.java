package org.shirdrn.document.processor.component.test;

import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.AbstractDenoisingDocumentTerms;

public class DenoisingTestDocumentTerms extends AbstractDenoisingDocumentTerms {

	public DenoisingTestDocumentTerms(Context context) {
		super(context);
	}

	@Override
	protected double getMaxTFIDFPercent() {
		double percent = 
				context.getConfiguration().getDouble("processor.dataset.test.denoise.tfidf.maxpercent", 1.0);
		return Math.min(percent, 1.0);
	}

}
