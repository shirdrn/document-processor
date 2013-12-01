package org.shirdrn.document.processor.component.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.component.AbstractDocumentWordsCollector;
import org.shirdrn.document.processor.utils.CheckUtils;

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
	
	@Override
	protected void loadVectors() {
		// check files
		CheckUtils.checkFileExists(labelVectorFile);
		CheckUtils.checkFileExists(termVectorFile);
		// load term vector
		loadTermVector();
		// load label vector
		loadLabelVector();
	}
	
	private void loadLabelVector() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(labelVectorFile), charSet));
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					try {
						String[] a = line.split("\\s+");
						if(a.length == 2) {
							String id = a[0];
							String label = a[1];
							context.getMetadata().putLabelNumber(Integer.parseInt(id), label);
						}
					} catch (NumberFormatException e) {
						LOG.warn(e);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadTermVector() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(termVectorFile), charSet));
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					try {
						String[] a = line.split("\\s+");
						if(a.length == 2) {
							String id = a[0];
							String word = a[1];
							context.getMetadata().putTermNumber(Integer.parseInt(id), word);
						}
					} catch (NumberFormatException e) {
						LOG.warn(e);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void analyze(String label, File file) {
		String doc = file.getAbsolutePath();
		LOG.info("Process document: label=" + label + ", file=" + doc);
		Map<String, Term> terms = analyzer.analyze(file);
		// filter terms
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Term> entry = iter.next();
			if(context.getMetadata().getTermNumber(entry.getValue()) == null) {
				iter.remove();
			}
		}
		context.getMetadata().addTerms(label, doc, terms);
		// add inverted table as needed
		context.getMetadata().addTermsToInvertedTable(label, doc, terms);
		LOG.info("Done: file=" + file + ", termCount=" + terms.size());
		LOG.debug("Terms in a doc: terms=" + terms);
	}

}
