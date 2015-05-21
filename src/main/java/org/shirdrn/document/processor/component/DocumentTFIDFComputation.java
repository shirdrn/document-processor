package org.shirdrn.document.processor.component;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.utils.MetricUtils;

public class DocumentTFIDFComputation extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(DocumentTFIDFComputation.class);
	
	public DocumentTFIDFComputation(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		// for each document, compute TF, IDF, TF-IDF
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {//每一个特定类别
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();//文档-关键词表
			Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
			while(docsIter.hasNext()) {//每一篇特定文档
				Entry<String, Map<String, Term>> docsEntry = docsIter.next();
				String doc = docsEntry.getKey();//文档名
				Map<String, Term> terms = docsEntry.getValue();//对应的关键词表
				Iterator<Entry<String, Term>> termsIter = terms.entrySet().iterator();
				while(termsIter.hasNext()) {//每一个词
					Entry<String, Term> termEntry = termsIter.next();
					String word = termEntry.getKey();
					// check whether word is contained in CHI vector
					if(context.getVectorMetadata().containsChiWord(word)) {
						Term term = termEntry.getValue();
						int freq = term.getFreq();//词在该篇文档中的出现次数
						int termCount = context.getVectorMetadata().getTermCount(label, doc);//该篇文档的总词数
						
						double tf = MetricUtils.tf(freq, termCount);
						int totalDocCount = context.getVectorMetadata().getTotalDocCount();
						int docCountContainingTerm = context.getVectorMetadata().getDocCount(term);
						
						double idf = MetricUtils.idf(totalDocCount, docCountContainingTerm);
						termEntry.getValue().setIdf(idf);
						termEntry.getValue().setTf(tf);
						termEntry.getValue().setTfidf(MetricUtils.tfidf(tf, idf));
						LOG.debug("Term detail: label=" + label + ", doc=" + doc + ", term=" + term);
					} else {
						// remove term not contained in CHI vector
						// 仅从关键词表中移除了未被选择的特征，并未从倒排表中移出
						termsIter.remove();
						LOG.debug("Not in CHI vector: word=" + word);
					}
				}
			}
		}		
	}

}
