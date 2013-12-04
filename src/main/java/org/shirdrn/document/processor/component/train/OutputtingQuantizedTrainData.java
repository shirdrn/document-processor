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
		LOG.info("Output label mapping file: labelFile=" + context.getFDMetadata().getLabelVectorFile());
		output(context.getFDMetadata().getLabelVectorFile(), context.getVectorMetadata().labelVectorMapIterator());
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
		
		// generate label id
		for(String label : context.getVectorMetadata().getLabels()) {
			Integer labelId = globalLabelToIdMap.get(label);
			if(labelId == null) {
				++labelNumber;
				labelId = labelNumber;
				globalLabelToIdMap.put(label, labelId);
				globalIdToLabelMap.put(labelId, label);
			}
		}
		// generate word id
		 Iterator<Entry<String,Term>> iter = 
					context.getVectorMetadata().chiMergedTermVectorIterator();
		while(iter.hasNext()) {
			Entry<String,Term> entry = iter.next();
			++wordNumber;
			entry.getValue().setId(wordNumber);
		}
		// store metadata
		context.getVectorMetadata().putLabelToIdPairs(globalLabelToIdMap);
		context.getVectorMetadata().putIdToLabelPairs(globalIdToLabelMap);
		
		// output term vectors
		outputChiTermVector();
	}

	private void outputChiTermVector() {
		BufferedWriter w = null;
		Iterator<Entry<String,Term>> iter = 
				context.getVectorMetadata().chiMergedTermVectorIterator();
		try {
			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					context.getFDMetadata().getChiTermVectorFile()), charSet));
			while(iter.hasNext()) {
				Entry<String,Term> entry = iter.next();
				String word = entry.getKey();
				Integer wordId = entry.getValue().getId();
				StringBuffer buf = new StringBuffer();
				buf
					.append(word).append("\t")
					.append(wordId);
				LOG.debug("Write CHI term vector: word=" + word + 
						", datum=" + buf.toString());
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
