package org.shirdrn.document.processor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class VectorMetadata {

	private int totalDocCount;
	private final List<String> labels = new ArrayList<String>();
	// Map<类别, 文档数量>
	private final Map<String, Integer> labelledTotalDocCountMap = new HashMap<String, Integer>();
	//  Map<类别, Map<文档 ,Map<词, 词信息>>>
	private final Map<String, Map<String, Map<String, Term>>> termTable = 
			new HashMap<String, Map<String, Map<String, Term>>>();
	//  Map<词 ,Map<类别, Set<文档>>>
	private final Map<String, Map<String, Set<String>>> invertedTable = 
			new HashMap<String, Map<String, Set<String>>>();
	// Map<类别编号, Map<词, 词编号>>
	private final Map<Integer, Map<String, Integer>> termVectorsMap = 
			new HashMap<Integer, Map<String, Integer>>(0); 
	
	// <labelId, label>
	private final Map<Integer, String> globalIdToLabelMap = new HashMap<Integer, String>(0);
	// <label, labelId>
	private final Map<String, Integer> globalLabelToIdMap = new HashMap<String, Integer>(0);
	
	public void addLabel(String label) {
		if(!labels.contains(label)) {
			labels.add(label);
		}
	}
	
	public List<String> getLabels() {
		return labels;
	}
	
	public int getTotalDocCount() {
		return totalDocCount;
	}
	
	public void setTotalDocCount(int totalDocCount) {
		this.totalDocCount = totalDocCount;
	}
	
	public int getLabelledTotalDocCount(String label) {
		return labelledTotalDocCountMap.get(label);
	}
	
	public void putLabelledTotalDocCount(String label, int labelledDocCount) {
		labelledTotalDocCountMap.put(label, labelledDocCount);
	}
	
	
	//////// inverted table ////////
	
	public int getDocCount(Term term, String label) {
		String word = term.getWord();
		return invertedTable.get(word).get(label).size();
	}
	
	public void addTermToInvertedTable(String label, String doc, Term term) {
		String word = term.getWord();
		Map<String, Set<String>> labelledDocs = invertedTable.get(word);
		if(labelledDocs == null) {
			labelledDocs = new HashMap<String, Set<String>>(0);
			invertedTable.put(word, labelledDocs);
		}
		Set<String> docs = labelledDocs.get(label);
		if(docs == null) {
			docs = new HashSet<String>();
			labelledDocs.put(label, docs);
		}
		docs.add(doc);
	}
	
	public void addTermsToInvertedTable(String label, String doc, Map<String, Term> terms) {
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Term> entry = iter.next();
			addTermToInvertedTable(label, doc, entry.getValue());
		}
	}
	
	public int getDocCount(Term term) {
		String word = term.getWord();
		int count = 0;
		Map<String, Set<String>> labelledDocs = invertedTable.get(word);
		Iterator<Entry<String, Set<String>>> iter = labelledDocs.entrySet().iterator();
		while(iter.hasNext()) {
			count += iter.next().getValue().size();
		}
		return count;
	}
	
	public Iterator<Entry<String, Map<String, Set<String>>>> invertedTableIterator() {
		return invertedTable.entrySet().iterator();
	}
	
	public int getDocCountInThisLabel(Term term) {
		return invertedTable.get(term.getWord()).size();
	}
	
	//////// term table ////////
	
	public int getDocCount(String label) {
		return termTable.get(label).size();
	}
	
	public int getTermCount(String label, String doc) {
		int size = 0;
		// avoid empty file
		if(termTable.get(label) != null 
				&& termTable.get(label).get(doc) != null) {
			size = termTable.get(label).get(doc).size();
		}
		return size;
	}
	
	public void addTerms(String label, String doc, Map<String, Term> terms) {
		Map<String, Map<String, Term>> docs = termTable.get(label);
		if(docs == null) {
			docs = new HashMap<String, Map<String, Term>>();
			termTable.put(label, docs);
		}
		docs.put(doc, terms);
	}
	
	public int getLabelCount() {
		return termTable.keySet().size();
	}
	
	public Iterator<Entry<String, Map<String, Map<String, Term>>>> termTableIterator() {
		return termTable.entrySet().iterator();
	}
	
	//////// term vectors map ////////
	
	public Iterator<Map.Entry<Integer,Map<String,Integer>>> termVectorsMapIterator() {
		return termVectorsMap.entrySet().iterator();
	}
	
	public Integer getLabeledWordId(int labelId, String word) {
		return termVectorsMap.get(labelId).get(word);
	}
	
	public void putWordWithId(int labelId, String word, Integer wordId) {
		Map<String, Integer> terms = termVectorsMap.get(labelId);
		if(terms == null) {
			terms = new HashMap<String, Integer>(1);
			termVectorsMap.put(labelId, terms);
		}
		terms.put(word, wordId);
	}
	
	public void putLabeledWords(int labelId, Map<String, Integer> words) {
		Map<String, Integer> terms = termVectorsMap.get(labelId);
		if(terms == null) {
			terms = new HashMap<String, Integer>(1);
			termVectorsMap.put(labelId, terms);
		}
		terms.putAll(words);
	}
	
	public boolean containsTerm(String word) {
		return termVectorsMap.get(word) == null ? false : true;
	}
	
	//////// label vector map
	
	// label->id
	
	public Iterator<Entry<String, Integer>> labelVectorMapIterator() {
		return globalLabelToIdMap.entrySet().iterator();
	}
	
	public Integer getlabelId(String label) {
		return globalLabelToIdMap.get(label);
	}
	
	public void putLabelToIdPairs(Map<String, Integer> globalLabelToIdMap) {
		this.globalLabelToIdMap.putAll(globalLabelToIdMap);
	}
	
	// id->label
	
	public void putIdToLabelPairs(Map<Integer, String> globalIdToLabelMap) {
		this.globalIdToLabelMap.putAll(globalIdToLabelMap);
	}
	
	public String getlabel(Integer labelId) {
		return globalIdToLabelMap.get(labelId);
	}
	
}
