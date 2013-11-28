package org.shirdrn.document.processor.analyzer;

import java.io.File;
import java.util.Set;

import org.junit.Test;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.config.Configuration;

public class TestIctclasAnalyzer {

	@Test
	public void analyze() {
		Configuration configuration = new Configuration();
		IctclasAnalyzer a = new IctclasAnalyzer(configuration);
		String f = "F:\\SogouC-UTF8\\UTF8\\test\\ClassFile\\C000013\\1.txt";
		Set<Term> terms = a.analyze(new File(f));
		for(Term t : terms) {
			System.out.println(t);
		}
	}
}
