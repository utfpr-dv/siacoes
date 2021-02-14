package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;

public class InternshipListReport {

	private String student;
	private String studentCode;
	private String supervisor;
	private String company;
	private String companySupervisor;
	private String type;
	private String sei;
	private String jurySei;
	private String status;
	private Date startDate;
	private Date endDate;
	private Date juryDate;
	private boolean internshipPlan;
	private boolean finalReport;
	private int studentReport;
	private int supervisorReport;
	private int companySupervisorReport;
	private double juryScore;
	
	public InternshipListReport() {
		this.setStudent("");
		this.setStudentCode("");
		this.setSupervisor("");
		this.setCompany("");
		this.setCompanySupervisor("");
		this.setType("");
		this.setSei("");
		this.setJurySei("");
		this.setStatus("");
		this.setStartDate(null);
		this.setEndDate(null);
		this.setJuryDate(null);
		this.setInternshipPlan(false);
		this.setFinalReport(false);
		this.setStudentReport(0);
		this.setSupervisorReport(0);
		this.setCompanySupervisorReport(0);
		this.setJuryScore(0);
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
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCompanySupervisor() {
		return companySupervisor;
	}
	public void setCompanySupervisor(String companySupervisor) {
		this.companySupervisor = companySupervisor;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSei() {
		return sei;
	}
	public void setSei(String sei) {
		this.sei = sei;
	}
	public String getJurySei() {
		return jurySei;
	}
	public void setJurySei(String jurySei) {
		this.jurySei = jurySei;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public Date getJuryDate() {
		return juryDate;
	}
	public void setJuryDate(Date juryDate) {
		this.juryDate = juryDate;
	}
	public boolean isInternshipPlan() {
		return internshipPlan;
	}
	public void setInternshipPlan(boolean internshipPlan) {
		this.internshipPlan = internshipPlan;
	}
	public boolean isFinalReport() {
		return finalReport;
	}
	public void setFinalReport(boolean finalReport) {
		this.finalReport = finalReport;
	}
	public int getStudentReport() {
		return studentReport;
	}
	public void setStudentReport(int studentReport) {
		this.studentReport = studentReport;
	}
	public int getSupervisorReport() {
		return supervisorReport;
	}
	public void setSupervisorReport(int supervisorReport) {
		this.supervisorReport = supervisorReport;
	}
	public int getCompanySupervisorReport() {
		return companySupervisorReport;
	}
	public void setCompanySupervisorReport(int companySupervisorReport) {
		this.companySupervisorReport = companySupervisorReport;
	}
	public double getJuryScore() {
		return juryScore;
	}
	public void setJuryScore(double juryScore) {
		this.juryScore = juryScore;
	}
	
}
