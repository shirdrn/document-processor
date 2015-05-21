package org.shirdrn.document.processor.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.AbstractComponent;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.DocInfo;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.component.test.OutputtingQuantizedTestData;

public abstract class AbstractOutputtingQuantizedData extends AbstractComponent {

	private static final Log LOG = LogFactory.getLog(OutputtingQuantizedTestData.class);
	protected BufferedWriter writer;
	
	public AbstractOutputtingQuantizedData(Context context) {
		super(context);
	}
	
	@Override
	public void fire() {
		// create term vectors for outputting/inputting
		quantizeTermVectors();
		// output train/test vectors
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(context.getFDMetadata().getOutputDir(), 
							context.getFDMetadata().getOutputVectorFile())), charSet));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Iterator<Entry<String, Map<String, Map<String, Term>>>> iter = context.getVectorMetadata().termTableIterator();
		DocInfo docInfo;
		while(iter.hasNext()) {//对每一个类别而言
			Entry<String, Map<String, Map<String, Term>>> labelledDocsEntry = iter.next();
			String label = labelledDocsEntry.getKey();
			Integer labelId = getLabelId(label);
			if(labelId != null) {
				Map<String, Map<String, Term>>  docs = labelledDocsEntry.getValue();
				Iterator<Entry<String, Map<String, Term>>> docsIter = docs.entrySet().iterator();
				while(docsIter.hasNext()) {//对每一篇文档而言					
					StringBuffer line = new StringBuffer();
					line.append(labelId).append(" ");
					Entry<String, Map<String, Term>> docsEntry = docsIter.next();
					String doc=docsEntry.getKey();
					docInfo=context.getVectorMetadata().getDocInfo(label, doc);
					Map<String, Term> terms = docsEntry.getValue();
					//将每篇文章的关键词按序号排序，存储在List中
					List<Map.Entry<Integer, Double>> termIntList = new ArrayList<Map.Entry<Integer, Double>>();
					//Map<String,Integer> termIntMap = new HashMap<String, Integer>(0);
					for(Entry<String, Term> termEntry : terms.entrySet()) {//对每一个关键词而言		
						String word = termEntry.getKey();
						Integer wordId = getWordId(word);
						if(wordId != null) {
							Term term = termEntry.getValue();
							//line.append(wordId).append(":").append(term.getTfidf()).append(" ");						
							termIntList.add(new AbstractMap.SimpleEntry<Integer, Double>(wordId,term.getTfidf()));
							//termIntMap.put(word, wordId);	
						}
					}
					Collections.sort(termIntList, new Comparator<Map.Entry<Integer, Double>>() {
						@Override
						public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
							if(o1.getKey()>o2.getKey())
								return 1;
							else if(o1.getKey()<o2.getKey())
								return -1;
							else
								return 0;
						}
					});//对termIntList中的关键词排序
					Iterator listIter=termIntList.iterator();
					while(listIter.hasNext()){
						Entry<Integer, Double> entryList=(Entry<Integer, Double>) listIter.next();
						line.append(String.valueOf(entryList.getKey())).append(":").append(String.valueOf(entryList.getValue())).append(" ");			
					}
					//line.append("6000:").append(docInfo.getFM()).append(" ");
					//line.append("6001:").append(docInfo.getMood()).append(" ");
					/*for(Entry<String,Integer> termIntEntry : termIntMap.entrySet())
					{
						termIntList.add(termIntEntry);
					}*/
					try {
						String element = line.toString().trim();
						LOG.debug("Write line: " + element);
						//element=element+"\n";
						writer.write(element);
						writer.newLine();			//windows环境下是/r/n，但libsvm需要的格式是/n
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				LOG.warn("Label ID can not be found: label=" + label + ", labelId=null");
			}
		}
		if(writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOG.info("Finished: outputVectorFile=" + context.getFDMetadata().getOutputVectorFile());
			
	}
	
	private Integer getWordId(String word) {
		return context.getVectorMetadata().getWordId(word);
	}

	private Integer getLabelId(String label) {
		return context.getVectorMetadata().getlabelId(label);
	}

	protected abstract void quantizeTermVectors();

}
