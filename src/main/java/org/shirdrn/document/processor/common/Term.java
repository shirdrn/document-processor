package org.shirdrn.document.processor.common;


public class Term {

	private String word;
	private int freq = 0;
	private double tf;
	private double idf;
	private double tfidf = 0;
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public double getTf() {
		return tf;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

	public double getTfidf() {
		return tfidf;
	}

	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}

	public Term(String word) {
		super();
		this.word = word;
	}
	
	@Override
	public int hashCode() {
		return word.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Term other = (Term) obj;
		return word.equals(other.word);
	}
	
	@Override
	public String toString() {
		return "[word=" + word + ", freq=" + freq + ", tf=" + tf + ", tfidf=" + tfidf + "]";
	}

}
