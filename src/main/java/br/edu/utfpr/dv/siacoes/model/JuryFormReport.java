package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JuryFormReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int stage;
	private String title;
	private Date date;
	private int idStudent;
	private String student;
	private String comments;
	private double score;
	private String evaluationText;
	private List<JuryFormAppraiserReport> appraisers;
	private List<JuryFormAppraiserReport> appraisersName;
	private List<JuryFormAppraiserScoreReport> scores;
	private String local;
	private int idSupervisor;
	private String supervisor;
	private boolean requestFinalDocumentStage1;
	
	public JuryFormReport(){
		this.setStage(0);
		this.setTitle("");
		this.setDate(new Date());
		this.setStudent("");
		this.setComments("");
		this.setScore(0);
		this.setEvaluationText("");
		this.setAppraisers(new ArrayList<JuryFormAppraiserReport>());
		this.setAppraisersName(new ArrayList<JuryFormAppraiserReport>());
		this.setScores(new ArrayList<JuryFormAppraiserScoreReport>());
		this.setLocal("");
		this.setSupervisor("");
		this.setRequestFinalDocumentStage1(false);
		this.setIdStudent(0);
		this.setIdSupervisor(0);
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
	public List<JuryFormAppraiserReport> getAppraisersName() {
		return appraisersName;
	}
	public void setAppraisersName(List<JuryFormAppraiserReport> appraisersName) {
		this.appraisersName = appraisersName;
	}
	public List<JuryFormAppraiserScoreReport> getScores() {
		return scores;
	}
	public void setScores(List<JuryFormAppraiserScoreReport> scores) {
		this.scores = scores;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public boolean isRequestFinalDocumentStage1() {
		return requestFinalDocumentStage1;
	}
	public void setRequestFinalDocumentStage1(boolean requestFinalDocumentStage1) {
		this.requestFinalDocumentStage1 = requestFinalDocumentStage1;
	}
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
	}
	public int getIdSupervisor() {
		return idSupervisor;
	}
	public void setIdSupervisor(int idSupervisor) {
		this.idSupervisor = idSupervisor;
	}
	
}
