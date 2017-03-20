package org.shirdrn.document.processor.analyzer;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.config.Configuration;

public class TestIctclasAnalyzer {

	@Test
	public void analyze() {
		Configuration configuration = new Configuration();
		IctclasAnalyzer a = new IctclasAnalyzer(configuration);
		String f = "F:\\SogouC-UTF8\\UTF8\\test\\ClassFile\\C000013\\1.txt";
		Map<String, Term> terms = a.analyze(new File(f),"a",new Context());
		for(Entry<String, Term> entry : terms.entrySet()) {
			System.out.println(entry.getValue());
		}
	}
}
