package br.edu.utfpr.dv.siacoes.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InternshipJuryFormReport {
	
	private String title;
	private Date date;
	private String student;
	private String comments;
	private String company;
	private double appraisersPonderosity;
	private double supervisorPonderosity;
	private double companySupervisorPonderosity;
	private double appraiser1Score;
	private double appraiser2Score;
	private double supervisorScore;
	private double companySupervisorScore;
	private double finalScore;
	private List<JuryFormAppraiserReport> appraisers;
	
	public InternshipJuryFormReport(){
		this.setTitle("");
		this.setDate(new Date());
		this.setStudent("");
		this.setComments("");
		this.setCompany("");
		this.setAppraisersPonderosity(0);
		this.setSupervisorPonderosity(0);
		this.setCompanySupervisorPonderosity(0);
		this.setAppraiser1Score(0);
		this.setAppraiser2Score(0);
		this.setSupervisorScore(0);
		this.setCompanySupervisorScore(0);
		this.setAppraisers(new ArrayList<JuryFormAppraiserReport>());
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
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
	public double getAppraisersPonderosity() {
		return appraisersPonderosity;
	}
	public void setAppraisersPonderosity(double appraisersPonderosity) {
		this.appraisersPonderosity = appraisersPonderosity;
	}
	public double getSupervisorPonderosity() {
		return supervisorPonderosity;
	}
	public void setSupervisorPonderosity(double supervisorPonderosity) {
		this.supervisorPonderosity = supervisorPonderosity;
	}
	public double getCompanySupervisorPonderosity() {
		return companySupervisorPonderosity;
	}
	public void setCompanySupervisorPonderosity(double companySupervisorPonderosity) {
		this.companySupervisorPonderosity = companySupervisorPonderosity;
	}
	public double getAppraiser1Score() {
		return appraiser1Score;
	}
	public void setAppraiser1Score(double appraiser1Score) {
		this.appraiser1Score = appraiser1Score;
	}
	public double getAppraiser2Score() {
		return appraiser2Score;
	}
	public void setAppraiser2Score(double appraiser2Score) {
		this.appraiser2Score = appraiser2Score;
	}
	public double getSupervisorScore() {
		return supervisorScore;
	}
	public void setSupervisorScore(double supervisorScore) {
		this.supervisorScore = supervisorScore;
	}
	public double getCompanySupervisorScore() {
		return companySupervisorScore;
	}
	public void setCompanySupervisorScore(double companySupervisorScore) {
		this.companySupervisorScore = companySupervisorScore;
	}
	public double getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}
	public List<JuryFormAppraiserReport> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<JuryFormAppraiserReport> appraisers) {
		this.appraisers = appraisers;
	}

}
