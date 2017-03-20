package org.shirdrn.document.processor.common;

import java.io.File;
import java.util.Map;

public interface DocumentAnalyzer {

	Map<String, Term> analyze(File file,String label,Context context);//文件分析函数，返回文件的关键词表（特征提取之前）
}
