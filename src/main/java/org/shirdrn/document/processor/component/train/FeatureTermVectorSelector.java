package org.shirdrn.document.processor.component.train;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.utils.SortUtils;
import org.shirdrn.document.processor.utils.SortUtils.Result;

/**
 * Select term vector from the procedure of processing train data. The selection can
 * be based on any effective metric, here we use the Chi-square distance metric to
 * choose suitable terms. Certainly you can use another one to replace the
 * default one.</br>
 * If you have a huge data set which can fit in memory, the better practices
 * are plus a cache for caching computation result, because there are too many
 * identical computation logic.
 * 
 * @author Shirdrn
 */
public class FeatureTermVectorSelector extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(FeatureTermVectorSelector.class);
	private final int keptTermCountEachLabel;//每个类别可保留的特征词数量
	
	public FeatureTermVectorSelector(Context context) {
		super(context);
		keptTermCountEachLabel = context.getConfiguration().getInt("processor.each.label.kept.term.count", 3000);
	}

	@Override
	public void fire() {
		// compute CHI value for selecting feature terms 
		// after sorting by CHI value
		for(String label : context.getVectorMetadata().getLabels()) {
			// for each label, compute CHI vector  CHI是相对于某一类别而言的，chiLabelToWordsVectorsMap存储着每个类别所有词的CHI信息
			LOG.info("Compute CHI for: label=" + label);
			processOneLabel(label);
		}
		
		// sort and select CHI vectors
		Iterator<Entry<String, Map<String, Term>>> chiIter = 
				context.getVectorMetadata().chiLabelToWordsVectorsIterator();//每个类别对应的关键词
		while(chiIter.hasNext()) {
			Entry<String, Map<String, Term>> entry = chiIter.next();
			String label = entry.getKey();//得到当前类别
			//因为是遍历倒排表来得到每个类别的所有词的CHI，所以每个类别的关键词的数量相同
			LOG.info("Sort CHI terms for: label=" + label + ", termCount=" + entry.getValue().size());
			Result result = sort(entry.getValue());//对当前类别的特征词进行排序，按照chi值大小
			for (int i = result.getEndIndex(),l=0; i >= result.getStartIndex(); i--,l++) {
				Entry<String, Term> termEntry = result.get(i);
				// merge CHI terms for all labels
				context.getVectorMetadata().addChiMergedTerm(termEntry.getKey(), termEntry.getValue());
				
				if(l==0)
				{
					LOG.info("Label:"+label);
					LOG.info(result.get(i));
				}
				else if(l<=5)
					LOG.info(result.get(i));
			}
		}
	}
	
	private Result sort(Map<String, Term> terms) {
		SortUtils sorter = new SortUtils(terms, true, keptTermCountEachLabel);//降序
		Result result = sorter.heapSort();
		return result;
	}

	private void processOneLabel(String label) {
		Iterator<Entry<String, Map<String, Set<String>>>> iter = 
				context.getVectorMetadata().invertedTableIterator();//倒排表
		while(iter.hasNext()) {//针对倒排表中的每个词
			Entry<String, Map<String, Set<String>>> entry = iter.next();
			String word = entry.getKey();
			Map<String, Set<String>> labelledDocs = entry.getValue();//含有该词的某类别下的所有文档
			
			// A: doc count containing the word in this label
			int docCountContainingWordInLabel = 0;
			if(labelledDocs.get(label) != null) {
				docCountContainingWordInLabel = labelledDocs.get(label).size();
			}
			
			// B: doc count containing the word not in this label
			int docCountContainingWordNotInLabel = 0;
			Iterator<Entry<String, Set<String>>> labelledIter = 
					labelledDocs.entrySet().iterator();
			while(labelledIter.hasNext()) {
				Entry<String, Set<String>> labelledEntry = labelledIter.next();
				String tmpLabel = labelledEntry.getKey();
				if(!label.equals(tmpLabel)) {
					docCountContainingWordNotInLabel += labelledEntry.getValue().size();
				}
			}
			
			// C: doc count not containing the word in this label
			int docCountNotContainingWordInLabel = 
					getDocCountNotContainingWordInLabel(word, label);
			
			// D: doc count not containing the word not in this label
			int docCountNotContainingWordNotInLabel = 
					getDocCountNotContainingWordNotInLabel(word, label);
			
			// compute CHI value
			int N = context.getVectorMetadata().getTotalDocCount();
			int A = docCountContainingWordInLabel;
			int B = docCountContainingWordNotInLabel;
			int C = docCountNotContainingWordInLabel;
			int D = docCountNotContainingWordNotInLabel;
			int temp = (A*D-B*C);
			// double chi = (double) N*temp*temp / (A+C)*(A+B)*(B+D)*(C+D); // incorrect!!!
			double chi = (double) N*temp*temp / ((A+C)*(A+B)*(B+D)*(C+D)); // correct formula computation
			Term term = new Term(word);
			term.setChi(chi);
			context.getVectorMetadata().addChiTerm(label, word, term);//将计算好的CHI值存入chiLabelToWordsVectorsMap
		}
	}

	private int getDocCountNotContainingWordInLabel(String word, String label) {
		int count = 0;
		Iterator<Entry<String,Map<String,Map<String,Term>>>> iter = 
				context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {//针对每一个类别
			Entry<String,Map<String,Map<String,Term>>> entry = iter.next();
			String tmpLabel = entry.getKey();
			// in this label
			if(tmpLabel.equals(label)) {
				Map<String, Map<String, Term>> labelledDocs = entry.getValue();
				for(Entry<String, Map<String, Term>> docEntry : labelledDocs.entrySet()) {
					// not containing this word
					if(!docEntry.getValue().containsKey(word)) {
						++count;
					}
				}
				break;
			}
		}
		return count;
	}
	
	private int getDocCountNotContainingWordNotInLabel(String word, String label) {
		int count = 0;
		Iterator<Entry<String,Map<String,Map<String,Term>>>> iter = 
				context.getVectorMetadata().termTableIterator();
		while(iter.hasNext()) {
			Entry<String,Map<String,Map<String,Term>>> entry = iter.next();
			String tmpLabel = entry.getKey();
			// not in this label
			if(!tmpLabel.equals(label)) {
				Map<String, Map<String, Term>> labelledDocs = entry.getValue();
				for(Entry<String, Map<String, Term>> docEntry : labelledDocs.entrySet()) {
					// not containing this word
					if(!docEntry.getValue().containsKey(word)) {
						++count;
					}
				}
			}
		}
		return count;
	}

}
