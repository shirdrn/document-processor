package org.shirdrn.document.processor.utils;

public class MetricUtils {

	public static double tf(int freq, int termCount) {
		return (double) freq / termCount;
	}
	
	public static double idf(int totalDocCount, int docCountContainingTerm) {
		return log2((double) totalDocCount / (1 + docCountContainingTerm));
	}
	
	private static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}
	
	public static double tfidf(double tf, double idf) {
		return tf * idf;
	}
}
