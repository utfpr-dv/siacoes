package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ActivitySubmission implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum ActivityFeedback{
		NONE(0), APPROVED(1), DISAPPROVED(2);
		
		private final int value; 
		ActivityFeedback(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static ActivityFeedback valueOf(int value){
			for(ActivityFeedback p : ActivityFeedback.values()){
				if(p.getValue() == value){
					return p;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
			case NONE:
				return "Nenhum";
			case APPROVED:
				return "Aceita";
			case DISAPPROVED:
				return "Não aceita";
			default:
				return "";
			}
		}
	}
	
	private int idActivitySubmission;
	private User student;
	private User feedbackUser;
	private Department department;
	private Activity activity;
	private int semester;
	private int year;
	private Date submissionDate;
	private transient byte[] file;
	private double amount;
	private ActivityFeedback feedback;
	private Date feedbackDate;
	private double validatedAmount;
	private String comments;
	private String description;
	private int stage;
	private String feedbackReason;
	
	public ActivitySubmission(){
		this.setIdActivitySubmission(0);
		this.setStudent(new User());
		this.setFeedbackUser(new User());
		this.setDepartment(new Department());
		this.setActivity(new Activity());
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setSubmissionDate(new Date());
		this.setFile(null);
		this.setAmount(0);
		this.setFeedback(ActivityFeedback.NONE);
		this.setFeedbackDate(null);
		this.setValidatedAmount(0);
		this.setComments("");
		this.setDescription("");
		this.setStage(0);
		this.setFeedbackReason("");
	}
	
	public int getIdActivitySubmission() {
		return idActivitySubmission;
	}
	public void setIdActivitySubmission(int idActivitySubmission) {
		this.idActivitySubmission = idActivitySubmission;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public User getFeedbackUser() {
		return feedbackUser;
	}
	public void setFeedbackUser(User feedbackUser) {
		this.feedbackUser = feedbackUser;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Activity getActivity(){
		return activity;
	}
	public void setActivity(Activity activity){
		this.activity = activity;
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
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public ActivityFeedback getFeedback(){
		return feedback;
	}
	public void setFeedback(ActivityFeedback feedback){
		this.feedback = feedback;
	}
	public Date getFeedbackDate(){
		return feedbackDate;
	}
	public void setFeedbackDate(Date feedbackDate){
		this.feedbackDate = feedbackDate;
	}
	public double getValidatedAmount(){
		return validatedAmount;
	}
	public void setValidatedAmount(double validatedAmount){
		this.validatedAmount = validatedAmount;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getScore() {
		double score = 0;
		
		if(this.getFeedback() == ActivityFeedback.APPROVED) {
			if(this.getActivity().getUnit().isFillAmount()) {
				score = this.getActivity().getScore() * this.getValidatedAmount();
			} else {
				score = this.getActivity().getScore();
			}
			
			if((this.getActivity().getMaximumInSemester() > 0) && (score > this.getActivity().getMaximumInSemester())) {
				score = this.getActivity().getMaximumInSemester();
			}
		}
		
		return score;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getFeedbackReason() {
		return feedbackReason;
	}
	public void setFeedbackReason(String feedbackReason) {
		this.feedbackReason = feedbackReason;
	}

}
