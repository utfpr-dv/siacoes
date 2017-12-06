package br.edu.utfpr.dv.siacoes.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JuryFormReport {
	
	private int stage;
	private String title;
	private Date date;
	private String student;
	private String comments;
	private double score;
	private String evaluationText;
	private List<JuryFormAppraiserReport> appraisers;
	private List<JuryFormAppraiserScoreReport> scores;
	
	public JuryFormReport(){
		this.setStage(0);
		this.setTitle("");
		this.setDate(new Date());
		this.setStudent("");
		this.setComments("");
		this.setScore(0);
		this.setEvaluationText("");
		this.setAppraisers(new ArrayList<JuryFormAppraiserReport>());
		this.setScores(new ArrayList<JuryFormAppraiserScoreReport>());
	}
	
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getEvaluationText() {
		return evaluationText;
	}
	public void setEvaluationText(String evaluationText) {
		this.evaluationText = evaluationText;
	}
	public List<JuryFormAppraiserReport> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<JuryFormAppraiserReport> appraisers) {
		this.appraisers = appraisers;
	}
	public List<JuryFormAppraiserScoreReport> getScores() {
		return scores;
	}
	public void setScores(List<JuryFormAppraiserScoreReport> scores) {
		this.scores = scores;
	}

}
