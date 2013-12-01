package org.shirdrn.document.processor.component.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
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
		output(labelVectorFile, context.getMetadata().labelVectorMapIterator());
		LOG.info("Output term mapping file: termsFile=" + termVectorFile);
		output(labelVectorFile, context.getMetadata().termVectorMapIterator());
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
	protected Integer getLabelNumber(String label) {
		if(context.getMetadata().getlabelNumber(label) == null) {
			labelNumber++;
			context.getMetadata().putLabelNumber(labelNumber, label);
			return labelNumber;
		} else {
			return context.getMetadata().getlabelNumber(label);
		}
	}
	
	@Override
	protected Integer getWordNumber(String word) {
		if(context.getMetadata().getTermNumber(word) == null) {
			wordNumber++;
			context.getMetadata().putTermNumber(wordNumber, word);
			return wordNumber;
		} else {
			return context.getMetadata().getTermNumber(word);
		}
	}

}
