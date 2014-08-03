package org.shirdrn.document.processor.common;


public class Term {

	private int id;
	private String word;
	private String lexicalCategory = "unknown";
	private int freq = 0;
	private double tf;
	private double idf;
	private double tfidf = 0;
	private double chi = 0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLexicalCategory() {
		return lexicalCategory;
	}

	public void setLexicalCategory(String lexicalCategory) {
		this.lexicalCategory = lexicalCategory;
	}

	public int getFreq() {
		return freq;
	}

	public void incrFreq() {
		freq++;
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
		StringBuffer buffer = new StringBuffer();
		buffer.append("[")
			.append("word=").append(word).append(", ")
			.append("freq=").append(freq).append(", ")
			.append("tf=").append(tf).append(", ")
			.append("idf=").append(idf).append(", ")
			.append("tf-idf=").append(tfidf).append(", ")
			.append("chi=").append(chi)
			.append("]");
		return buffer.toString();
	}

	public double getChi() {
		return chi;
	}

	public void setChi(double chi) {
		this.chi = chi;
	}

}
