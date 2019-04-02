package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class JuryFormAppraiserDetailReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String evaluationItemType;
	private int order;
	private String evaluationItem;
	private double ponderosity;
	private double score;
	private double ponderositySum;
	private double scoreSum;
	
	public JuryFormAppraiserDetailReport(){
		this.setEvaluationItemType("");
		this.setOrder(0);
		this.setEvaluationItemType("");
		this.setPonderosity(0);
		this.setScore(0);
		this.setPonderositySum(0);
		this.setScoreSum(0);
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
	public double getPonderosity() {
		return ponderosity;
	}
	public void setPonderosity(double ponderosity) {
		this.ponderosity = ponderosity;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getPonderositySum() {
		return ponderositySum;
	}
	public void setPonderositySum(double ponderositySum) {
		this.ponderositySum = ponderositySum;
	}
	public double getScoreSum() {
		return scoreSum;
	}
	public void setScoreSum(double scoreSum) {
		this.scoreSum = scoreSum;
	}

}
