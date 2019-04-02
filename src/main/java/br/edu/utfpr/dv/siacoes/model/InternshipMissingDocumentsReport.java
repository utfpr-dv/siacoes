package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;

public class InternshipMissingDocumentsReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String student;
	private String supervisor;
	private String companySupervisor;
	private String company;
	private InternshipStatus status;
	private InternshipType type;
	private Date startDate;
	private Date endDate;
	private int studentReport;
	private int supervisorReport;
	private int companySupervisorReport;
	private boolean finalReport;
	
	public InternshipMissingDocumentsReport(){
		this.setStudent("");
		this.setSupervisor("");
		this.setCompanySupervisor("");
		this.setCompany("");
		this.setStartDate(new Date());
		this.setEndDate(new Date());
		this.setStudentReport(0);
		this.setSupervisorReport(0);
		this.setCompanySupervisorReport(0);
		this.setFinalReport(false);
		this.setStatus(InternshipStatus.CURRENT);
		this.setType(InternshipType.NONREQUIRED);
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getCompanySupervisor() {
		return companySupervisor;
	}
	public void setCompanySupervisor(String companySupervisor) {
		this.companySupervisor = companySupervisor;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
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
	public boolean isFinalReport() {
		return finalReport;
	}
	public void setFinalReport(boolean finalReport) {
		this.finalReport = finalReport;
	}
	public InternshipStatus getStatus() {
		return status;
	}
	public void setStatus(InternshipStatus status) {
		this.status = status;
	}
	public InternshipType getType() {
		return type;
	}
	public void setType(InternshipType type) {
		this.type = type;
	}

}
