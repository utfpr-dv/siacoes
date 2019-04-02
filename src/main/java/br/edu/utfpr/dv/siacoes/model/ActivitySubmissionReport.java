package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ActivitySubmissionReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String student;
	private String studentCode;
	private int registerSemester;
	private int registerYear;
	private double totalScore;
	private String situation;
	private List<ActivitySubmissionDetailReport> details;
	private List<ActivitySubmissionFooterReport> footer;
	
	public ActivitySubmissionReport(){
		this.setStudent("");
		this.setStudentCode("");
		this.setRegisterSemester(DateUtils.getSemester());
		this.setRegisterYear(DateUtils.getYear());
		this.setTotalScore(0);
		this.setSituation("");
		this.setDetails(new ArrayList<ActivitySubmissionDetailReport>());
		this.setFooter(new ArrayList<ActivitySubmissionFooterReport>());
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
	public int getRegisterSemester() {
		return registerSemester;
	}
	public void setRegisterSemester(int registerSemester) {
		this.registerSemester = registerSemester;
	}
	public int getRegisterYear() {
		return registerYear;
	}
	public void setRegisterYear(int registerYear) {
		this.registerYear = registerYear;
	}
	public double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}
	public String getSituation(){
		return situation;
	}
	public void setSituation(String situation){
		this.situation = situation;
	}
	public List<ActivitySubmissionDetailReport> getDetails() {
		return details;
	}
	public void setDetails(List<ActivitySubmissionDetailReport> details) {
		this.details = details;
	}
	public List<ActivitySubmissionFooterReport> getFooter() {
		return footer;
	}
	public void setFooter(List<ActivitySubmissionFooterReport> footer) {
		this.footer = footer;
	}

}
