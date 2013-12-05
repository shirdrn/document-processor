package org.shirdrn.document.processor.component.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.AbstractDocumentWordsCollector;

public class CollectingTestDocumentWords extends AbstractDocumentWordsCollector {

	private static final Log LOG = LogFactory.getLog(CollectingTestDocumentWords.class);
	
	public CollectingTestDocumentWords(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		// analyze and collect words
		super.fire();
	}
	
}
