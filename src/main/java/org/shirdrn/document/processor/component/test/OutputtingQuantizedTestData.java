package org.shirdrn.document.processor.component.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.AbstractOutputtingQuantizedData;

public class OutputtingQuantizedTestData extends AbstractOutputtingQuantizedData {

	private static final Log LOG = LogFactory.getLog(OutputtingQuantizedTestData.class);
	
	public OutputtingQuantizedTestData(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		super.fire();
	}

	@Override
	protected Integer getLabelNumber(String label) {
		Integer labelId = context.getMetadata().getlabelNumber(label);
		if(labelId == null) {
			LOG.warn("Label id can not be found: label=" + label + ", labelId=null");
		}
		return labelId;
	}
	
	@Override
	protected Integer getWordNumber(String word) {
		Integer wordId = context.getMetadata().getTermNumber(word);
		if(wordId == null) {
			LOG.warn("Word id can not be found: word=" + word + ", wordId=null");
		}
		return wordId;
	}
	
}
