package org.shirdrn.document.processor.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;

public class StopwordsTermFilter implements TermFilter {

	private static final Log LOG = LogFactory.getLog(StopwordsTermFilter.class);
	private String charSet = "UTF-8";
	private static final Set<String> STOP_WORDS = new HashSet<String>();
	private static final AtomicBoolean isLoaded = new AtomicBoolean(false);
	
	public StopwordsTermFilter(Context context) {
		// set charset
		String charSet = context.getConfiguration().get("processor.common.charset");
		if(charSet != null) {
			this.charSet = charSet;
		}
		// try to load stop words
		if(isLoaded.compareAndSet(false, true)) {
			// stop words
			String stopWordsDir = context.getConfiguration().get("processor.analyzer.stopwords.path");
			if(stopWordsDir != null) {
				File dir = new File(stopWordsDir);
				File[] files = dir.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						if(file.isFile()) {
							return true;
						}
						return false;
					}
					
				});
				for(File file : files) {
					try {
						load(file);
					} catch (Exception e) {
						LOG.warn("Fail to load stop words: file=" + file, e);
					}
				}
			}
		}
	}
	
	@Override
	public void filter(Map<String, Term> terms) {
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Term> entry = iter.next();
			if(STOP_WORDS.contains(entry.getValue().getWord())) {
				iter.remove();
			}
		}
	}

	private void load(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			String word = null;
			while((word = reader.readLine()) != null) {
				word = word.trim();
				if(!word.isEmpty()) {
					if(!STOP_WORDS.contains(word)) {
						STOP_WORDS.add(word);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

}
