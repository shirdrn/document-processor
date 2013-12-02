package org.shirdrn.document.processor.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;
import org.shirdrn.document.processor.utils.ReflectionUtils;

public class AggregatedTermFilter implements TermFilter {

	private final List<TermFilter> filters = new ArrayList<TermFilter>(0);
	
	public AggregatedTermFilter(Context context) {
		String classes = context.getConfiguration().get("processor.document.filter.classes");
		if(classes != null) {
			String[] aClass = classes.split("[,;\\s\\|:-]+");
			for(String className : aClass) {
				filters.add(ReflectionUtils.getInstance(className, TermFilter.class));
			}
		}
	}
	
	@Override
	public void filter(Map<String, Term> terms) {
		for(TermFilter filter : filters) {
			filter.filter(terms);
		}
	}

}
