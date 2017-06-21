package br.edu.utfpr.dv.siacoes.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Internship {
	
	public enum InternshipStatus{
		CURRENT(0), FINISHED(1);
		
		private final int value; 
		InternshipStatus(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static InternshipStatus valueOf(int value){
			for(InternshipStatus d : InternshipStatus.values()){
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
				case CURRENT:
					return "Em Andamento";
				case FINISHED:
					return "Finalizado";
				default:
					return "";
			}
		}
	}
	
	public enum InternshipType{
		NONREQUIRED(0), REQUIRED(1);
		
		private final int value; 
		InternshipType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static InternshipType valueOf(int value){
			for(InternshipType d : InternshipType.values()){
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
				case NONREQUIRED:
					return "Não Obrigatório";
				case REQUIRED:
					return "Obrigatório";
				default:
					return "Não Obrigatório";
			}
		}
	}
	
	private int idInternship;
	private Department department;
	private Company company;
	private User companySupervisor;
	private User supervisor;
	private User student;
	private InternshipType type;
	private String comments;
	private Date startDate;
	private Date endDate;
	private String reportTitle;
	private int totalHours;
	private byte[] internshipPlan;
	private byte[] finalReport;
	private List<InternshipReport> reports;
	
	public Internship(){
		this.setIdInternship(0);
		this.setDepartment(new Department());
		this.setCompany(new Company());
		this.setCompanySupervisor(new User());
		this.setSupervisor(new User());
		this.setStudent(new User());
		this.setType(InternshipType.NONREQUIRED);
		this.setComments("");
		this.setReportTitle("");
		this.setStartDate(DateUtils.getToday().getTime());
		this.setEndDate(null);
		this.setTotalHours(0);
		this.setInternshipPlan(null);
		this.setFinalReport(null);
		this.setReports(new ArrayList<InternshipReport>());
	}
	
	public int getIdInternship() {
		return idInternship;
	}
	public void setIdInternship(int idInternship) {
		this.idInternship = idInternship;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public User getCompanySupervisor() {
		return companySupervisor;
	}
	public void setCompanySupervisor(User companySupervisor) {
		this.companySupervisor = companySupervisor;
	}
	public User getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public InternshipType getType() {
		return type;
	}
	public void setType(InternshipType type) {
		this.type = type;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(int totalHours) {
		this.totalHours = totalHours;
	}
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	public byte[] getInternshipPlan() {
		return internshipPlan;
	}
	public void setInternshipPlan(byte[] internshipPlan) {
		this.internshipPlan = internshipPlan;
	}
	public byte[] getFinalReport() {
		return finalReport;
	}
	public void setFinalReport(byte[] finalReport) {
		this.finalReport = finalReport;
	}
	public List<InternshipReport> getReports() {
		return reports;
	}
	public void setReports(List<InternshipReport> reports) {
		this.reports = reports;
	}
	public InternshipStatus getStatus(){
		if((this.getEndDate() == null) || (this.getEndDate().after(DateUtils.getToday().getTime()))){
			return InternshipStatus.CURRENT;
		}else{
			return InternshipStatus.FINISHED;
		}
	}

}
