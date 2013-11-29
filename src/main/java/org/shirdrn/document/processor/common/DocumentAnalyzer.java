package org.shirdrn.document.processor.common;

import java.io.File;
import java.util.Map;

public interface DocumentAnalyzer {

	Map<String, Term> analyze(File file);
}
