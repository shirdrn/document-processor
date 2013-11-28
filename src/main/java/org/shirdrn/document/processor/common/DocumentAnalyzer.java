package org.shirdrn.document.processor.common;

import java.io.File;
import java.util.Set;

public interface DocumentAnalyzer {

	Set<Term> analyze(File file);
}
