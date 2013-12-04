package org.shirdrn.document.processor;

import org.shirdrn.document.processor.common.Component;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.ProcessorType;
import org.shirdrn.document.processor.component.BasicContextInitializer;
import org.shirdrn.document.processor.component.DocumentTFIDFComputation;
import org.shirdrn.document.processor.component.test.CollectingTestDocumentWords;
import org.shirdrn.document.processor.component.test.LoadFeatureTermVector;
import org.shirdrn.document.processor.component.test.OutputtingQuantizedTestData;

public class TestDocumentProcessorDriver extends AbstractDocumentProcessorDriver {

	@Override
	public void process() {
		Context context = new Context(ProcessorType.TEST, "config-test.properties");
		// for test data
		Component[]	chain = new Component[] {
				new BasicContextInitializer(context),
				new CollectingTestDocumentWords(context),
				new LoadFeatureTermVector(context),
				new DocumentTFIDFComputation(context),
				new OutputtingQuantizedTestData(context)
			};
		run(chain);
	}
	
	public static void main(String[] args) {
		AbstractDocumentProcessorDriver.start(
				TestDocumentProcessorDriver.class);		
	}

}
