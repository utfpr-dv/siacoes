package br.edu.utfpr.dv.siacoes.ui.grid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.FinalSubmission;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class FinalSubmissionDataSource extends BasicDataSource {

	private String student;
	private double finalScore;
	private LocalDate date;
	private String feedbackUser;
	
	public FinalSubmissionDataSource(FinalSubmission submission) {
		this.setId(submission.getIdFinalSubmission());
		this.setStudent(submission.getStudent().getName());
		this.setFinalScore(submission.getFinalScore());
		this.setDate(DateUtils.convertToLocalDate(submission.getDate()));
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
		this.finalScore = this.round(finalScore);
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getFeedbackUser() {
		return feedbackUser;
	}
	public void setFeedbackUser(String feedbackUser) {
		this.feedbackUser = feedbackUser;
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
