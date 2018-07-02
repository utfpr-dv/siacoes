package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;

public class JuryGrade {
	
	private String student;
	private int stage;
	private Date juryDate;
	private double score;
	private JuryResult result;
	
	public JuryGrade() {
		this.setStudent("");
		this.setStage(0);
		this.setJuryDate(new Date());
		this.setScore(0);
		this.setResult(JuryResult.NONE);
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public Date getJuryDate() {
		return juryDate;
	}
	public void setJuryDate(Date juryDate) {
		this.juryDate = juryDate;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public JuryResult getResult() {
		return result;
	}
	public void setResult(JuryResult result) {
		this.result = result;
	}

}
