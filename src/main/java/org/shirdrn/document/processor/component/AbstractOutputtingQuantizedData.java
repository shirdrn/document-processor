package org.shirdrn.document.processor.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractDatasetManager;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.component.test.OutputtingQuantizedTestData;

public abstract class AbstractOutputtingQuantizedData extends AbstractDatasetManager {

	private static final Log LOG = LogFactory.getLog(OutputtingQuantizedTestData.class);
	protected BufferedWriter writer;
	
	public AbstractOutputtingQuantizedData(Context context) {
		super(context);
	}
	
	@Override
	public void fire() {
		super.fire();
		// create term vectors for outputting/inputting
		quantizeTermVectors();
		// output train/test vectors
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(outputDir, outputVectorFile)), charSet));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			Integer labelId = getLabelId(label);
			if(labelId != null) {
				Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
				Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
				while(docsIter.hasNext()) {
					StringBuffer line = new StringBuffer();
					line.append(labelId).append(" ");
					Entry<String, Map<String, Term>> docsEntry = docsIter.next();
					Map<String, Term> terms = docsEntry.getValue();
					for(Entry<String, Term> termEntry : terms.entrySet()) {
						String word = termEntry.getKey();
						Integer wordId = getWordId(labelId, word);
						if(wordId != null) {
							Term term = termEntry.getValue();
							line.append(wordId).append(":").append(term.getTfidf()).append(" ");
						}
					}
					try {
						String element = line.toString().trim();
						LOG.debug("Write line: " + element);
						writer.write(element);
						writer.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				LOG.warn("Label ID can not be found: label=" + label + ", labelId=null");
			}
		}
		if(writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOG.info("Finished: outputVectorFile=" + outputVectorFile);
			
	}
	
	private Integer getWordId(Integer labelId, String word) {
		return context.getVectorMetadata().getLabeledWordId(labelId, word);
	}

	private Integer getLabelId(String label) {
		return context.getVectorMetadata().getlabelId(label);
	}

	protected abstract void quantizeTermVectors();

}
