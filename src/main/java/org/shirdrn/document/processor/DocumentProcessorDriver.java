package org.shirdrn.document.processor;

import org.shirdrn.document.processor.common.Component;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.BasicContextInitializer;
import org.shirdrn.document.processor.component.DenoisingDocumentTerms;
import org.shirdrn.document.processor.component.DocumentTFIDFComputation;
import org.shirdrn.document.processor.component.DocumentWordsCollector;
import org.shirdrn.document.processor.component.OutputtingQuantizedData;

public class DocumentProcessorDriver {

	public static void main(String[] args) {
		Context context = new Context();
		Component[] chain = new Component[] {
			new BasicContextInitializer(context),
			new DocumentWordsCollector(context),
			new DocumentTFIDFComputation(context),
			new DenoisingDocumentTerms(context),
			new OutputtingQuantizedData(context)
		};
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
