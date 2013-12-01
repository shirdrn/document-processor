package org.shirdrn.document.processor.component.train;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.component.AbstractDocumentWordsCollector;

public class CollectingTrainDocumentWords extends AbstractDocumentWordsCollector {

	private static final Log LOG = LogFactory.getLog(CollectingTrainDocumentWords.class);
	
	public CollectingTrainDocumentWords(Context context) {
		super(context);
	}

	@Override
	protected void analyze(String label, File file) {
		String doc = file.getAbsolutePath();
		LOG.info("Process document: label=" + label + ", file=" + doc);
		Map<String, Term> terms = analyzer.analyze(file);
		context.getMetadata().addTerms(label, doc, terms);
		// add inverted table as needed
		context.getMetadata().addTermsToInvertedTable(label, doc, terms);
		LOG.info("Done: file=" + file + ", termCount=" + terms.size());
		LOG.debug("Terms in a doc: terms=" + terms);
	}

	@Override
	protected void loadVectors() {
				
	}

}
