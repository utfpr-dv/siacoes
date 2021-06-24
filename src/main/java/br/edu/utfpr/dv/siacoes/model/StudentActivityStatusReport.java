package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentActivityStatusReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum StudentStage{
		REGULAR(0), FINISHINGCOURSE(1), ALMOSTGRADUATED(2), GRADUATED(3);
		
		private final int value; 
		StudentStage(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static StudentStage valueOf(int value){
			for(StudentStage u : StudentStage.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case GRADUATED:
					return "Formado";
				case ALMOSTGRADUATED:
					return "Provável Formando";
				case FINISHINGCOURSE:
					return "Períodos Finais";
				case REGULAR:
					return "Regular";
				default:
					return "";
			}
		}
	}
	
	private int idUser;
	private String studentName;
	private String studentCode;
	private int registerSemester;
	private int registerYear;
	private int stage;
	private boolean finalSubmission;
	private List<ActivitySubmissionFooterReport> scores;
	private double group1Score;
	private double group2Score;
	private double group3Score;
	private double totalScore;
	private String situation;
	
	public StudentActivityStatusReport(){
		this.setIdUser(0);
		this.setStudentName("");
		this.setStudentCode("");
		this.setRegisterSemester(0);
		this.setRegisterYear(0);
		this.setStage(0);
		this.setScores(new ArrayList<ActivitySubmissionFooterReport>());
		this.setGroup1Score(0);
		this.setGroup2Score(0);
		this.setGroup3Score(0);
		this.setTotalScore(0);
		this.setSituation("");
		this.setFinalSubmission(false);
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
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
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public List<ActivitySubmissionFooterReport> getScores() {
		return scores;
	}
	public void setScores(List<ActivitySubmissionFooterReport> scores) {
		this.scores = scores;
	}
	public double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}
	public String getSituation() {
		return situation;
	}
	public void setSituation(String situation) {
		this.situation = situation;
	}
	public boolean hasFinalSubmission() {
		return finalSubmission;
	}
	public void setFinalSubmission(boolean finalSubmission) {
		this.finalSubmission = finalSubmission;
	}
	public double getGroup1Score() {
		return group1Score;
	}
	public void setGroup1Score(double group1Score) {
		this.group1Score = group1Score;
	}
	public double getGroup2Score() {
		return group2Score;
	}
	public void setGroup2Score(double group2Score) {
		this.group2Score = group2Score;
	}
	public double getGroup3Score() {
		return group3Score;
	}
	public void setGroup3Score(double group3Score) {
		this.group3Score = group3Score;
	}
	
}
