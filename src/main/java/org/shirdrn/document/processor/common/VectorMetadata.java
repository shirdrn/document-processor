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
	private final List<String> labels = new ArrayList<String>();//labels表示类别
	// Map<类别, 文档数量>
	private final Map<String, Integer> labelledTotalDocCountMap = new HashMap<String, Integer>();
	//  Map<类别, Map<文档 ,Map<词, 词信息>>> 分析结果 通过文档词语收集器更新
	private final Map<String, Map<String, Map<String, Term>>> termTable = 
			new HashMap<String, Map<String, Map<String, Term>>>();
	//  Map<词 ,Map<类别, Set<文档>>> 倒排表 通过文档词语收集器更新
	private final Map<String, Map<String, Set<String>>> invertedTable = 
			new HashMap<String, Map<String, Set<String>>>();
	
	// <labelId, label>
	private final Map<Integer, String> globalIdToLabelMap = new HashMap<Integer, String>(0);
	// <label, labelId>
	private final Map<String, Integer> globalLabelToIdMap = new HashMap<String, Integer>(0);
	
	// Map<label, Map<word, term>> 每个类别包含的特征词表，包含了chi值信息（跟具体文档无关，通过特征选择模块更新）
	private final Map<String, Map<String, Term>> chiLabelToWordsVectorsMap = new HashMap<String, Map<String, Term>>(0);
	// Map<word, term>, finally merged vector 合并后的全类别特征向量表
	private final Map<String, Term> chiMergedTermVectorMap = new HashMap<String, Term>(0);
	//存储每个文档对应的F-Measure的值
	private final Map<String,Map<String,DocInfo>> docInfoMap=new HashMap<String,Map<String,DocInfo>>(0);
	
	public void addDocInfoMap(String label,String doc,DocInfo docInfo){
		Map<String, DocInfo> docs = docInfoMap.get(label);
		if(docs == null) {
			docs = new HashMap<String, DocInfo>();
			docInfoMap.put(label, docs);
		}
		docs.put(doc, docInfo);
	}
	
	public DocInfo getDocInfo(String label,String doc){
		return docInfoMap.get(label).get(doc);
	}
		
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
		Map<String/*类别*/, Set<String>/*文档*/> labelledDocs = invertedTable.get(word);//读取倒排表
		if(labelledDocs == null) {//说明这个词是第一次出现
			labelledDocs = new HashMap<String, Set<String>>(0);
			invertedTable.put(word, labelledDocs);
		}
		Set<String> docs = labelledDocs.get(label);//得到包含term的特定类别label的文档的列表
		if(docs == null) {//该类之前没有包含term的文档
			docs = new HashSet<String>();
			labelledDocs.put(label, docs);
		}
		docs.add(doc);
	}
	
	public void addTermsToInvertedTable(String label, String doc, Map<String, Term> terms) {
		Iterator<Entry<String, Term>> iter = terms.entrySet().iterator();
		while(iter.hasNext()) {//遍历terms中的每一个term
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
		docs.put(doc, terms);//将某类别label下的特定文档doc的关键词表terms记录下来
	}
	
	public int getLabelCount() {
		return termTable.keySet().size();
	}
	
	public Iterator<Entry<String, Map<String, Map<String, Term>>>> termTableIterator() {
		return termTable.entrySet().iterator();
	}
	
	//////// label vector map ////////
	
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
	
	public Set<String> getWordSet() {
		return invertedTable.keySet();
	}
	
	//////// CHI vectors ////////
	
	public void addChiTerm(String label, String word, Term term) {
		Map<String,Term> words = chiLabelToWordsVectorsMap.get(label);
		if(words == null) {
			words = new HashMap<String,Term>(1);
			chiLabelToWordsVectorsMap.put(label, words);
		}
		words.put(word, term);
	}
	
	public Iterator<Entry<String, Map<String, Term>>> chiLabelToWordsVectorsIterator() {
		return chiLabelToWordsVectorsMap.entrySet().iterator();
	}
	
	public void addChiMergedTerm(String word, Term term) {
		if(!chiMergedTermVectorMap.containsKey(word)) {
			chiMergedTermVectorMap.put(word, term);
		}
	}
	
	public boolean containsChiWord(String word) {
		return chiMergedTermVectorMap.containsKey(word);
	}
	
	public Iterator<Entry<String, Term>> chiMergedTermVectorIterator() {
		return chiMergedTermVectorMap.entrySet().iterator();
	}
	
	public Integer getLabelId(String label) {
		return globalLabelToIdMap.get(label);
	}
	
	public Integer getWordId(String word) {
		Term term = chiMergedTermVectorMap.get(word);
		if(term != null) {
			return term.getId();
		}
		return null;
	}
	
}
