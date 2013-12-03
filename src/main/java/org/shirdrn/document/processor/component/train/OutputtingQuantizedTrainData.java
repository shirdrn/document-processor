package org.shirdrn.document.processor.component.train;

import java.io.BufferedWriter;
import java.io.File;
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
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.component.AbstractOutputtingQuantizedData;

public class OutputtingQuantizedTrainData extends AbstractOutputtingQuantizedData {

	private static final Log LOG = LogFactory.getLog(OutputtingQuantizedTrainData.class);
	private int labelNumber = 0;
	private int wordNumber = 0;
	
	public OutputtingQuantizedTrainData(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		super.fire();
		LOG.info("Output label mapping file: labelFile=" + labelVectorFile);
		output(labelVectorFile, context.getVectorMetadata().labelVectorMapIterator());
	}
	
	private void output(File file, Iterator<Entry<String, Integer>> iter) {
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charSet));
			while(iter.hasNext()) {
				Entry<String, Integer> entry = iter.next();
				w.write(entry.getValue().toString() + " " + entry.getKey());
				w.newLine();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(w != null) {
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	@Override
	protected void quantizeTermVectors() {
		// store all <label, labelId> pairs
		Map<String, Integer> globalLabelToIdMap = new HashMap<String, Integer>(0);
		// store all <labelId, label> pairs
		Map<Integer, String> globalIdToLabelMap = new HashMap<Integer, String>(0);
		// store all <word, wordId> pairs
		Map<String, Integer> globalTermsMap = new HashMap<String, Integer>(0);
		// iterate collected document words
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			// generate label id
			Integer labelId = globalLabelToIdMap.get(label);
			if(labelId == null) {
				++labelNumber;
				labelId = labelNumber;
				globalLabelToIdMap.put(label, labelId);
				globalIdToLabelMap.put(labelId, label);
			}
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
			Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
			while(docsIter.hasNext()) {
				StringBuffer line = new StringBuffer();
				line.append(labelId).append(" ");
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				Map<String, Term> terms = docsEntry.getValue();
				for(Entry<String, Term> termEntry : terms.entrySet()) {
					String word = termEntry.getKey();
					// generate word id
					Integer wordId = globalTermsMap.get(word);
					if(wordId == null) {
						++wordNumber;
						wordId = wordNumber;
						globalTermsMap.put(word, wordId);
					}
					// store relationship: labelId->word->wordId
					context.getVectorMetadata().putWordWithId(labelId, word, wordId);
				}
			}
		}
		
		// store metadata
		context.getVectorMetadata().putLabelToIdPairs(globalLabelToIdMap);
		context.getVectorMetadata().putIdToLabelPairs(globalIdToLabelMap);
		
		// output term vectors
		outputTermVectors();
	}

	private void outputTermVectors() {
		Iterator<Entry<Integer, Map<String, Integer>>> iter = context.getVectorMetadata().termVectorsMapIterator();
		while(iter.hasNext()) {
			Entry<Integer, Map<String, Integer>> entry = iter.next();
			int labelId = entry.getKey();
			String label = context.getVectorMetadata().getlabel(labelId);
			BufferedWriter w = null;
			try {
				File file = getTermVectorFileName(labelId);
				LOG.info("Term vector file: labelId=" + labelId + ", label=" + label + ", file=" + file);
				w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charSet));
				Iterator<Entry<String, Integer>> wordIter = entry.getValue().entrySet().iterator();
				while(wordIter.hasNext()) {
					Entry<String, Integer> wordEntry = wordIter.next();
					String word = wordEntry.getKey();
					Integer wordId = wordEntry.getValue();
					StringBuffer buf = new StringBuffer();
					buf
						.append(labelId).append("\t")
						.append(word).append("\t")
						.append(wordId);
					LOG.debug("Write term vector: label=" + label + 
							",datum=" + buf.toString());
					w.write(buf.toString());
					w.newLine();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(w != null) {
					try {
						w.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	private File getTermVectorFileName(int labelId) {
		String label = context.getVectorMetadata().getlabel(labelId);
		String fileName = label + "-" + labelId + "-terms.txt";
		File termVectorFile = new File(outputDir, fileName);
		return termVectorFile;
	}

}
