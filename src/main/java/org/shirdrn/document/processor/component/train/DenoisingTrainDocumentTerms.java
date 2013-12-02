package org.shirdrn.document.processor.component.train;

import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.AbstractDenoisingDocumentTerms;

public class DenoisingTrainDocumentTerms extends AbstractDenoisingDocumentTerms {

	public DenoisingTrainDocumentTerms(Context context) {
		super(context);
	}

	@Override
	protected double getMaxTFIDFPercent() {
		double percent = 
				context.getConfiguration().getDouble("processor.dataset.train.denoise.tfidf.maxpercent", 0.5);
		return Math.min(percent, 0.5);
	}

}
