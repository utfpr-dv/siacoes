package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.FinalSubmission;

public class FinalSubmissionDataSource extends BasicDataSource {

	private String student;
	private double finalScore;
	private Date date;
	private String feedbackUser;
	
	public FinalSubmissionDataSource(FinalSubmission submission) {
		this.setId(submission.getIdFinalSubmission());
		this.setStudent(submission.getStudent().getName());
		this.setFinalScore(submission.getFinalScore());
		this.setDate(submission.getDate());
		this.setFeedbackUser(submission.getFeedbackUser().getName());
	}
	
	public static List<FinalSubmissionDataSource> load(List<FinalSubmission> list) {
		List<FinalSubmissionDataSource> ret = new ArrayList<FinalSubmissionDataSource>();
		
		for(FinalSubmission submission : list) {
			ret.add(new FinalSubmissionDataSource(submission));
		}
		
		return ret;
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public double getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getFeedbackUser() {
		return feedbackUser;
	}
	public void setFeedbackUser(String feedbackUser) {
		this.feedbackUser = feedbackUser;
	}
	
}
