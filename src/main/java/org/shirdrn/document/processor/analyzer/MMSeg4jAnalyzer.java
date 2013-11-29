package org.shirdrn.document.processor.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.shirdrn.document.processor.common.AbstractDocumentAnalyzer;
import org.shirdrn.document.processor.common.DocumentAnalyzer;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.config.Configuration;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;

public class MMSeg4jAnalyzer extends AbstractDocumentAnalyzer implements DocumentAnalyzer {

	private static final Log LOG = LogFactory.getLog(MMSeg4jAnalyzer.class);
	private final Analyzer analyzer;
	
	public MMSeg4jAnalyzer(Configuration configuration) {
		super(configuration);
		analyzer = new ComplexAnalyzer();
	}
	
	@Override
	public Set<Term> analyze(File file) {
		String doc = file.getAbsolutePath();
		LOG.info("Process document: file=" + doc);
		Set<Term> terms = new HashSet<Term>(0);
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), charSet));
			String line = null;
			while((line = br.readLine()) != null) {
				StringReader reader = new StringReader(line);
				TokenStream ts = analyzer.tokenStream("", reader);
				ts.addAttribute(CharTermAttribute.class); 
				while (ts.incrementToken()) {  
					CharTermAttributeImpl attr = (CharTermAttributeImpl) ts.getAttribute(CharTermAttribute.class);  
					String word = attr.toString().trim();
					if(!word.isEmpty() && !super.isStopword(word)) {
						Term term = new Term(word);
						terms.add(term);
					} else {
						LOG.info("Filter out stop word: file=" + file + ", word=" + word);
					}
				}
				ts.close();
				reader.close();
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
