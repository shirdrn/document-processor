package org.shirdrn.document.processor.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import kevin.zhang.NLPIR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.common.DocInfo;
import org.shirdrn.document.processor.common.DocumentAnalyzer;
import org.shirdrn.document.processor.common.Term;
import org.shirdrn.document.processor.config.Configuration;

public class IctclasAnalyzer extends AbstractDocumentAnalyzer/*停用词读取*/ implements DocumentAnalyzer/*实现分析函数*/ {

	private static final Log LOG = LogFactory.getLog(IctclasAnalyzer.class);
	private final NLPIR analyzer;
	private static final AtomicBoolean isLoaded = new AtomicBoolean(false);
	private static final Set<String> POS_WORDS = new HashSet<String>();
	private static final Set<String> NEG_WORDS = new HashSet<String>();
	
	public IctclasAnalyzer(Configuration configuration) {
		super(configuration);
		analyzer = new NLPIR();
		try {
			boolean initialized = NLPIR.NLPIR_Init(".".getBytes(charSet), 1);
			if(!initialized) {
				throw new RuntimeException("Fail to initialize!");
			}
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
		if(isLoaded.compareAndSet(false, true)) {
			// stop words
			String posWordDir = configuration.get("processor.document.analyzer.moodwords.pos.path");
			String negWordDir = configuration.get("processor.document.analyzer.moodwords.neg.path");
			if(posWordDir != null) {
				File dir = new File(posWordDir);
				File[] files = dir.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						if(file.isFile()) {//过滤掉文件夹文件
							return true;
						}
						return false;
					}
					
				});
				for(File file : files) {
					try {
						load(file,"Pos");
					} catch (Exception e) {
						LOG.warn("Fail to load pos words: file=" + file, e);
					}
				}
			}
			if(negWordDir != null) {
				File dir = new File(negWordDir);
				File[] files = dir.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						if(file.isFile()) {//过滤掉文件夹文件
							return true;
						}
						return false;
					}					
				});
				for(File file : files) {
					try {
						load(file,"Neg");
					} catch (Exception e) {
						LOG.warn("Fail to load neg words: file=" + file, e);
					}
				}
			}
		}
		
		
	}
	
	private void load(File file,String mood) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			String word = null;
			while((word = reader.readLine()) != null) {
				word = word.trim();
				if(!word.isEmpty()) {
					if(mood=="Pos"){
						if(!POS_WORDS.contains(word)) {
							POS_WORDS.add(word);
						}
					}
					else{
						if(!NEG_WORDS.contains(word)) {
							NEG_WORDS.add(word);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

	@Override
	public Map<String, Term> analyze(File file,String label,Context context) {
		String doc = file.getAbsolutePath();
		LOG.info("Process document: file=" + doc);
		Map<String, Term> terms = new HashMap<String, Term>(0);
		DocInfo info = new DocInfo();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			String line = null;
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					byte nativeBytes[] = analyzer.NLPIR_ParagraphProcess(line.getBytes(charSet), 1);//词已经分好
					String content = new String(nativeBytes, 0, nativeBytes.length, charSet);
					String[] rawWords = line.split("\\s+");//一个或以上空格为分隔符 可用content或line
					for(String rawWord : rawWords) {
						String[] words = rawWord.split("/");
						if(words.length == 2) {
							String word = words[0];
							String lexicalCategory = words[1];
							Term term = terms.get(word);//关键词表中是否已经有记录							
							if(lexicalCategory.startsWith("n"))
								info.setN(info.getN()+1);
							if(lexicalCategory.startsWith("a"))
								info.setAdj(info.getAdj()+1);
							if(lexicalCategory.startsWith("p"))
								info.setPrep(info.getPrep()+1);
							if(lexicalCategory.startsWith("r"))
								info.setPron(info.getPron()+1);
							if(lexicalCategory.startsWith("v"))
								info.setVerb(info.getVerb()+1);
							if(lexicalCategory.startsWith("d"))
								info.setAdv(info.getAdv()+1);
							if(lexicalCategory.startsWith("e"))
								info.setInt(info.getInt()+1);
							if(term == null) {
								term = new Term(word);
								// TODO set lexical category
								term.setLexicalCategory(lexicalCategory);
								terms.put(word, term);//加入关键词表
							}
							if(POS_WORDS.contains(word))
								info.setPos(info.getPos()+1);
							if(NEG_WORDS.contains(word))
								info.setNeg(info.getNeg()+1);
							term.incrFreq();
							info.setTotal(info.getTotal()+1);
							LOG.debug("Got word: word=" + rawWord);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				LOG.warn(e);
			}
			context.getVectorMetadata().addDocInfoMap(label,doc,info);
		}
		return terms;
	}

}
