package org.shirdrn.document.processor;

import org.shirdrn.document.processor.common.Component;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.ProcessorType;
import org.shirdrn.document.processor.component.BasicInformationCollector;
import org.shirdrn.document.processor.component.DocumentTFIDFComputation;
import org.shirdrn.document.processor.component.DocumentWordsCollector;
import org.shirdrn.document.processor.component.train.FeatureTermVectorSelector;
import org.shirdrn.document.processor.component.train.OutputtingQuantizedTrainData;

public class TrainDocumentProcessorDriver extends AbstractDocumentProcessorDriver {

	@Override
	public void process() {
		Context context = new Context(ProcessorType.TRAIN, "config-train.properties");
		// for train data
		Component[]	chain = new Component[] {
				new BasicInformationCollector(context),
				new DocumentWordsCollector(context),
				new FeatureTermVectorSelector(context), 
				new DocumentTFIDFComputation(context),
				new OutputtingQuantizedTrainData(context)
			};
		run(chain);
	}
	
	public static void main(String[] args) {
		AbstractDocumentProcessorDriver.start(
				TrainDocumentProcessorDriver.class);	
		
	}

}
