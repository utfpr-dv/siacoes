package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class FinalSubmission implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idFinalSubmission;
	private Department department;
	private User student;
	private double finalScore;
	private User feedbackUser;
	private Date date;
	private transient byte[] report;
	
	public FinalSubmission() {
		this.setIdFinalSubmission(0);
		this.setDepartment(new Department());
		this.setStudent(new User());
		this.setFinalScore(0);
		this.setFeedbackUser(new User());
		this.setDate(DateUtils.getToday().getTime());
		this.setReport(null);
	}
	
	public int getIdFinalSubmission() {
		return idFinalSubmission;
	}
	public void setIdFinalSubmission(int idFinalSubmission) {
		this.idFinalSubmission = idFinalSubmission;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public double getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}
	public User getFeedbackUser() {
		return feedbackUser;
	}
	public void setFeedbackUser(User feedbackUser) {
		this.feedbackUser = feedbackUser;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public byte[] getReport() {
		return report;
	}
	public void setReport(byte[] report) {
		this.report = report;
	}

}
