package org.shirdrn.document.processor.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.common.TermFilter;

public class LexicalCategoryFilter implements TermFilter {

	private final Set<String> keptLexicalCategories = new HashSet<String>();
	
	public LexicalCategoryFilter(Context context) {
		// read configured lexical categories
		String lexicalCategories = 
				context.getConfiguration().get("processor.document.filter.kept.lexical.categories", "n");
		for(String category : lexicalCategories.split("\\s*,\\s*")) {
			keptLexicalCategories.add(category);
		}
	}
	
	@Override
	public void filter(Map<String, Term> terms) {
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Term> entry = iter.next();
			if(!keptLexicalCategories.contains(entry.getValue().getLexicalCategory())) {
				iter.remove();
			}
		}
	}

}
