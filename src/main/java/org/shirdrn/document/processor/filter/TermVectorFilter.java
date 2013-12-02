package org.shirdrn.document.processor.filter;

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
import org.shirdrn.document.processor.common.TermFilter;
import org.shirdrn.document.processor.utils.CheckUtils;

public class TermVectorFilter implements TermFilter {

	private static final Log LOG = LogFactory.getLog(TermVectorFilter.class);
	private String charSet = "UTF-8";
	protected File labelVectorFile;
	protected File termVectorFile;
	private Context context;
	
	public TermVectorFilter(Context context) {
		this.context = context;
		String terms = context.getConfiguration().get("processor.dataset.term.vector.file");
		termVectorFile = new File(terms);
		CheckUtils.checkFile(termVectorFile, false);
		
		
		String labels = context.getConfiguration().get("processor.dataset.label.vector.file");
		labelVectorFile = new File(labels);
		CheckUtils.checkFile(labelVectorFile, false);
		
		// set charset
		String charSet = context.getConfiguration().get("processor.common.charset");
		if(charSet != null) {
			this.charSet = charSet;
		}
		loadTermVector();
	}
	
	@Override
	public void filter(Map<String, Term> terms) {
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Term> entry = iter.next();
			if(!context.getMetadata().containsTerm(entry.getValue().getWord())) {
				iter.remove();
			}
		}
	}
	
	private void loadTermVector() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(termVectorFile), this.charSet));
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

}
