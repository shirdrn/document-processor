package org.shirdrn.document.processor;

import org.shirdrn.document.processor.common.Component;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.BasicContextInitializer;
import org.shirdrn.document.processor.component.DocumentTFIDFComputation;
import org.shirdrn.document.processor.component.test.CollectingTestDocumentWords;
import org.shirdrn.document.processor.component.test.DenoisingTestDocumentTerms;
import org.shirdrn.document.processor.component.test.OutputtingQuantizedTestData;
import org.shirdrn.document.processor.component.train.CollectingTrainDocumentWords;
import org.shirdrn.document.processor.component.train.DenoisingTrainDocumentTerms;
import org.shirdrn.document.processor.component.train.OutputtingQuantizedTrainData;

public class DocumentProcessorDriver {

	public static void main(String[] args) {
		Context context = new Context();
		boolean isTrainOpen = 
				context.getConfiguration().getBoolean("processor.dataset.train.isopen", false);
		Component[] chain = null;
		if(isTrainOpen) {
			// for train data
			chain = new Component[] {
					new BasicContextInitializer(context),
					new CollectingTrainDocumentWords(context),
					new DocumentTFIDFComputation(context),
					new DenoisingTrainDocumentTerms(context),
					new OutputtingQuantizedTrainData(context)
				};
		} else {
			// for test data
			chain = new Component[] {
					new BasicContextInitializer(context),
					new CollectingTestDocumentWords(context),
					new DocumentTFIDFComputation(context),
					new DenoisingTestDocumentTerms(context),
					new OutputtingQuantizedTestData(context)
				};
		}
		run(chain);
		
	}

	private static void run(Component[] chain) {
		for (int i = 0; i < chain.length - 1; i++) {
			Component current = chain[i];
			Component next = chain[i + 1];
			current.setNext(next);
		}
		
		for (Component componennt : chain) {
			componennt.fire();
		}
	}

}
