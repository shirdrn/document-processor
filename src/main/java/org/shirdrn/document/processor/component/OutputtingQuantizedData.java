package org.shirdrn.document.processor.component;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;

public class OutputtingQuantizedData extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(OutputtingQuantizedData.class);
	private final Map<String, Integer> labelNumberMap = new HashMap<String, Integer>();
	private final Map<String, Integer> wordNumberMap = new HashMap<String, Integer>();
	private int labelNumber = 0;
	private int wordNumber = 0;
	private BufferedWriter writer;
	private static final String CHARSET = "UTF-8"; 
	
	public OutputtingQuantizedData(Context context) {
		super(context);
		String output = context.getConfiguration().get("processor.dataset.train.svm.verctor.file");
		LOG.info("Output path: output=" + output);
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fire() {
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			int labelId = getLabelNumber(label);
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
			Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
			while(docsIter.hasNext()) {
				StringBuffer line = new StringBuffer();
				line.append(labelId).append(" ");
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				Map<String, Term> terms = docsEntry.getValue();
				for(Entry<String, Term> termEntry : terms.entrySet()) {
					String word = termEntry.getKey();
					int wordId = getWordNumber(word);
					Term term = termEntry.getValue();
					line.append(wordId).append(":").append(term.getTfidf()).append(" ");
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
		}
		if(writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int getLabelNumber(String label) {
		if(!labelNumberMap.containsKey(label)) {
			labelNumber++;
			labelNumberMap.put(label, labelNumber);
			return labelNumber;
		} else {
			return labelNumberMap.get(label);
		}
	}
	
	private int getWordNumber(String word) {
		if(!wordNumberMap.containsKey(word)) {
			wordNumber++;
			wordNumberMap.put(word, wordNumber);
			return wordNumber;
		} else {
			return wordNumberMap.get(word);
		}
	}

}
