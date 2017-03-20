package org.shirdrn.document.processor.common;

public class DocInfo {
	private int nNum;
	private int adjNum;
	private int prepNum;
	private int artNum;//article 中文中没有冠词
	private int pronNum;
	private int verbNum;
	private int advNum;
	private int intNum;
	private int totalNum;
	private int posNum;
	private int negNum;
	private double fMeasure;
	private double moodIndex;
	
	public double calc(){
		return ((double)(nNum+adjNum+prepNum+artNum-pronNum-verbNum-advNum-intNum+300)*0.5);
	}
	
	public double calcMood(){
		return (double)(posNum-negNum)/(double)totalNum;
	}
	
	public double getMood(){
		moodIndex=this.calcMood();
		return moodIndex;
	}
	
	public double getFM(){
		fMeasure=this.calc();
		return fMeasure;
	}
	
	public int getPos(){
		return posNum;
	}
	
	public void setPos(int pos){
		this.posNum=pos;
	}
	
	public int getNeg(){
		return negNum;
	}
	
	public void setNeg(int neg){
		this.negNum=neg;
	}
	
	public int getN(){
		return nNum;
	}
	
	public int getAdj(){
		return adjNum;
	}
	
	public int getPrep(){
		return prepNum;
	}
	
	public int getArt(){
		return artNum;
	}
	
	public int getPron(){
		return pronNum;
	}
	
	public int getVerb(){
		return verbNum;
	}
	
	public int getAdv(){
		return advNum;
	}
	
	public int getInt(){
		return intNum;
	}
	
	public int getTotal(){
		return totalNum;
	}
	
	public void setN(int n){
		this.nNum=n;
	}
	
	public void setAdj(int adj){
		this.adjNum=adj;
	}
	
	public void setPrep(int prep){
		this.prepNum=prep;
	}
	
	public void setArt(int art){
		this.artNum=art;
	}
	
	public void setPron(int pron){
		this.pronNum=pron;
	}
	
	public void setVerb(int verb){
		this.verbNum=verb;
	}
	
	public void setAdv(int adv){
		this.advNum=adv;
	}
	
	public void setInt(int i){
		this.intNum=i;
	}
	
	public void setTotal(int total){
		this.totalNum=total;
	}
}
