package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class InternshipJuryAppraiserScore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idInternshipJuryAppraiserScore;
	private InternshipJuryAppraiser internshipJuryAppraiser;
	private InternshipEvaluationItem internshipEvaluationItem;
	private double score;
	
	public InternshipJuryAppraiserScore(){
		this.setIdInternshipJuryAppraiserScore(0);
		this.setInternshipJuryAppraiser(new InternshipJuryAppraiser());
		this.setInternshipEvaluationItem(new InternshipEvaluationItem());
		this.setScore(0);
	}
	
	public int getIdInternshipJuryAppraiserScore() {
		return idInternshipJuryAppraiserScore;
	}
	public void setIdInternshipJuryAppraiserScore(int idInternshipJuryAppraiserScore) {
		this.idInternshipJuryAppraiserScore = idInternshipJuryAppraiserScore;
	}
	public InternshipJuryAppraiser getInternshipJuryAppraiser() {
		return internshipJuryAppraiser;
	}
	public void setInternshipJuryAppraiser(InternshipJuryAppraiser internshipJuryAppraiser) {
		this.internshipJuryAppraiser = internshipJuryAppraiser;
	}
	public InternshipEvaluationItem getInternshipEvaluationItem() {
		return internshipEvaluationItem;
	}
	public void setInternshipEvaluationItem(InternshipEvaluationItem internshipEvaluationItem) {
		this.internshipEvaluationItem = internshipEvaluationItem;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

}
