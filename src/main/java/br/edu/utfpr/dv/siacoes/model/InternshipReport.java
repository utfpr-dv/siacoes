package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum ReportType{
		STUDENT(0), SUPERVISOR(1), COMPANY(2);
		
		private final int value; 
		ReportType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static ReportType valueOf(int value){
			for(ReportType d : ReportType.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case STUDENT:
					return "Acadêmico";
				case SUPERVISOR:
					return "Orientador";
				case COMPANY:
					return "Supervisor";
				default:
					return "Acadêmico";
			}
		}
	}
	
	public enum ReportFeedback{
		NONE(0), APPROVED(1), DISAPPROVED(2);
		
		private final int value; 
		ReportFeedback(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static ReportFeedback valueOf(int value){
			for(ReportFeedback d : ReportFeedback.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case NONE:
					return "Nenhum";
				case APPROVED:
					return "Aprovado";
				case DISAPPROVED:
					return "Reprovado";
				default:
					return "Nenhum";
			}
		}
	}
	
	private int idInternshipReport;
	private Internship internship;
	private ReportType type;
	private transient byte[] report;
	private Date date;
	private ReportFeedback feedback;
	private Date feedbackDate;
	private User feedbackUser;
	private boolean finalReport;
	
	public InternshipReport(){
		this.setIdInternshipReport(0);
		this.setInternship(new Internship());
		this.setType(ReportType.STUDENT);
		this.setReport(null);
		this.setDate(DateUtils.getToday().getTime());
		this.setFinalReport(false);
		this.setFeedback(ReportFeedback.NONE);
		this.setFeedbackDate(null);
		this.setFeedbackUser(new User());
	}
	
	public int getIdInternshipReport() {
		return idInternshipReport;
	}
	public void setIdInternshipReport(int idInternshipReport) {
		this.idInternshipReport = idInternshipReport;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
	}
	public ReportType getType() {
		return type;
	}
	public void setType(ReportType type) {
		this.type = type;
	}
	public byte[] getReport() {
		return report;
	}
	public void setReport(byte[] report) {
		this.report = report;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ReportFeedback getFeedback() {
		return feedback;
	}
	public void setFeedback(ReportFeedback feedback) {
		this.feedback = feedback;
	}
	public Date getFeedbackDate() {
		return feedbackDate;
	}
	public void setFeedbackDate(Date feedbackDate) {
		this.feedbackDate = feedbackDate;
	}
	public User getFeedbackUser() {
		return feedbackUser;
	}
	public void setFeedbackUser(User feedbackUser) {
		this.feedbackUser = feedbackUser;
	}
	public boolean isFinalReport() {
		return finalReport;
	}
	public void setFinalReport(boolean finalReport) {
		this.finalReport = finalReport;
	}

}
