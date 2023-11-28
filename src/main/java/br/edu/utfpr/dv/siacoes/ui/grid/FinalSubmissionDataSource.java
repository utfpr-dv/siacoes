package br.edu.utfpr.dv.siacoes.ui.grid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.model.FinalSubmission;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class FinalSubmissionDataSource extends BasicDataSource {

	private String student;
	private double finalScore;
	private LocalDate date;
	private String feedbackUser;
	private int semester;
	private int year;
	private String signed;
	
	public FinalSubmissionDataSource(FinalSubmission submission) {
		this.setId(submission.getIdFinalSubmission());
		this.setStudent(submission.getStudent().getName());
		this.setFinalScore(submission.getFinalScore());
		this.setDate(DateUtils.convertToLocalDate(submission.getDate()));
		this.setFeedbackUser(submission.getFeedbackUser().getName());
		this.setSemester(0);
		this.setYear(0);
		this.setSigned("Não");
		
		try {
			Semester s = new SemesterBO().findByDate(new CampusBO().findByDepartment(submission.getDepartment().getIdDepartment()).getIdCampus(), submission.getDate());
			
			this.setSemester(s.getSemester());
			this.setYear(s.getYear());
			
			this.setSigned(Document.hasSignature(DocumentType.ACTIVITYFINALSUBMISSION, submission.getIdFinalSubmission()) ? "Sim" : "Não");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getSemesterYear() {
		return String.valueOf(this.getSemester()) + "/" + String.valueOf(this.getYear());
	}
	public String getSigned() {
		return signed;
	}
	public void setSigned(String signed) {
		this.signed = signed;
	}

	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
