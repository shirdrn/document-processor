package org.shirdrn.document.processor.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import kevin.zhang.NLPIR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractDocumentAnalyzer;
import org.shirdrn.document.processor.common.DocumentAnalyzer;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.config.Configuration;

public class IctclasAnalyzer extends AbstractDocumentAnalyzer implements DocumentAnalyzer {

	private static final Log LOG = LogFactory.getLog(IctclasAnalyzer.class);
	private final NLPIR analyzer;
	private final Set<String> keptLexicalCategories = new HashSet<String>();
	
	public IctclasAnalyzer(Configuration configuration) {
		super(configuration);
		analyzer = new NLPIR();
		try {
			boolean initialized = NLPIR.NLPIR_Init(".".getBytes(charSet), 1);
			if(!initialized) {
				throw new RuntimeException("Fail to initialize!");
			}
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
		// read configured lexical categories
		String lexicalCategories = 
				configuration.get("processor.document.analyzer.lexical.categories", "n");
		for(String category : lexicalCategories.split("\\s*,\\s*")) {
			keptLexicalCategories.add(category);
		}
	}

	@Override
	public Set<Term> analyze(File file) {
		String doc = file.getAbsolutePath();
		LOG.info("Process document: file=" + doc);
		Set<Term> terms = new HashSet<Term>(0);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					byte nativeBytes[] = analyzer.NLPIR_ParagraphProcess(line.getBytes(charSet), 1);
					String content = new String(nativeBytes, 0, nativeBytes.length, charSet);
					String[] words = content.split("\\s+");
					for(String word : words) {
						String[] ws = word.split("/");
						if(ws.length == 2) {
							String w = ws[0];
							String lexicalCategory = ws[1];
							if(!super.isStopword(w) 
									&& keptLexicalCategories.contains(lexicalCategory)) {
								LOG.debug("Kept word: word" + w);
								Term term = new Term(w);
								terms.add(term);
							} else {
								LOG.debug("Discard: word=" + word);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				LOG.warn(e);
			}
			LOG.info("Done: file=" + file + ", termCount=" + terms.size());
		}
		return terms;
	}

}
