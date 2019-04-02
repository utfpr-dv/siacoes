package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class JuryAppraiserScore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idJuryAppraiserScore;
	private JuryAppraiser juryAppraiser;
	private EvaluationItem evaluationItem;
	private double score;
	
	public JuryAppraiserScore(){
		this.setIdJuryAppraiserScore(0);
		this.setJuryAppraiser(new JuryAppraiser());
		this.setEvaluationItem(new EvaluationItem());
		this.setScore(0);
	}
	
	public int getIdJuryAppraiserScore() {
		return idJuryAppraiserScore;
	}
	public void setIdJuryAppraiserScore(int idJuryAppraiserScore) {
		this.idJuryAppraiserScore = idJuryAppraiserScore;
	}
	public JuryAppraiser getJuryAppraiser() {
		return juryAppraiser;
	}
	public void setJuryAppraiser(JuryAppraiser juryAppraiser) {
		this.juryAppraiser = juryAppraiser;
	}
	public EvaluationItem getEvaluationItem() {
		return evaluationItem;
	}
	public void setEvaluationItem(EvaluationItem evaluationItem) {
		this.evaluationItem = evaluationItem;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

}
