package org.shirdrn.document.processor.common;

import java.util.Map;

public interface TermFilter {

	void filter(Map<String, Term> terms);
}
