package org.shirdrn.document.processor;

import org.shirdrn.document.processor.common.Component;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.ProcessorType;
import org.shirdrn.document.processor.component.BasicInformationCollector;
import org.shirdrn.document.processor.component.DocumentTFIDFComputation;
import org.shirdrn.document.processor.component.DocumentWordsCollector;
import org.shirdrn.document.processor.component.train.FeatureTermVectorSelector;
import org.shirdrn.document.processor.component.train.OutputtingQuantizedTrainData;

/**
 * Driver for starting components to process TRAIN data set.
 * It includes the following 5 components:
 * <ol>
 * <li>{@link BasicInformationCollector}</li>
 * <li>{@link DocumentWordsCollector}</li>
 * <li>{@link FeatureTermVectorSelector}</li>
 * <li>{@link DocumentTFIDFComputation}</li>
 * <li>{@link OutputtingQuantizedTrainData}</li>
 * </ol>
 * Executing above components in order can output the normalized
 * data for feeding libSVM classifier developed by <code>Lin Chih-Jen</code>
 * (<a href="www.csie.ntu.edu.tw/~cjlin/libsvm/‎">www.csie.ntu.edu.tw/~cjlin/libsvm/‎</a>)</br>
 * It can produce 2 files represented by the specified properties:
 * <ol>
 * <li>a term vector file property: <code>processor.dataset.chi.term.vector.file</code></li>
 * <li>a label vector file property: <code>processor.dataset.label.vector.file</code></li>
 * </ol>
 * which are used by {@link TestDocumentProcessorDriver} to produce TEST vector data.
 * 
 * @author Shirdrn
 */
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
