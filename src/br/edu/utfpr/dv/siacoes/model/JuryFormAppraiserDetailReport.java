package br.edu.utfpr.dv.siacoes.model;

public class JuryFormAppraiserDetailReport {
	
	private String evaluationItemType;
	private int order;
	private String evaluationItem;
	private int ponderosity;
	private double score;
	
	public JuryFormAppraiserDetailReport(){
		this.setEvaluationItemType("");
		this.setOrder(0);
		this.setEvaluationItemType("");
		this.setPonderosity(0);
		this.setScore(0);
	}
	
	public String getEvaluationItemType() {
		return evaluationItemType;
	}
	public void setEvaluationItemType(String evaluationItemType) {
		this.evaluationItemType = evaluationItemType;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getEvaluationItem() {
		return evaluationItem;
	}
	public void setEvaluationItem(String evaluationItem) {
		this.evaluationItem = evaluationItem;
	}
	public int getPonderosity() {
		return ponderosity;
	}
	public void setPonderosity(int ponderosity) {
		this.ponderosity = ponderosity;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

}
