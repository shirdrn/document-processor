package org.shirdrn.document.processor.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kevin.zhang.NLPIR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	public Map<String, Term> analyze(File file) {
		String doc = file.getAbsolutePath();
		LOG.info("Process document: file=" + doc);
		Map<String, Term> terms = new HashMap<String, Term>(0);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					byte nativeBytes[] = analyzer.NLPIR_ParagraphProcess(line.getBytes(charSet), 1);
					String content = new String(nativeBytes, 0, nativeBytes.length, charSet);
					String[] rawWords = content.split("\\s+");
					for(String rawWord : rawWords) {
						String[] words = rawWord.split("/");
						if(words.length == 2) {
							String word = words[0];
							String lexicalCategory = words[1];
							if(!super.isStopword(word) 
									&& keptLexicalCategories.contains(lexicalCategory)) {
								LOG.debug("Kept word: word" + word);
								Term term = terms.get(word);
								if(term == null) {
									term = new Term(word);
									terms.put(word, term);
								}
								term.incrFreq();
							} else {
								LOG.debug("Discard: word=" + rawWord);
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
		}
		return terms;
	}

}
