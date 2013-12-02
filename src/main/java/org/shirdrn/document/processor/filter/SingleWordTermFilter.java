package org.shirdrn.document.processor.filter;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;

public class SingleWordTermFilter implements TermFilter {

	public SingleWordTermFilter(Context context) {
		super();
	}
	
	@Override
	public void filter(Map<String, Term> terms) {
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Term> entry = iter.next();
			if(entry.getValue().getWord() == null 
					|| entry.getValue().getWord().length() <= 1) {
				iter.remove();
			}
		}
	}

}
