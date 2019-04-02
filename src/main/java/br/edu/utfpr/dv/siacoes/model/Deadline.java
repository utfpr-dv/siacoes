package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Deadline implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idDeadline;
	private int semester;
	private int year;
	private Date proposalDeadline;
	private Date projectDeadline;
	private Date thesisDeadline;
	private Date projectFinalDocumentDeadline;
	private Date thesisFinalDocumentDeadline;
	private Department department;
	
	public Deadline(){
		this.setIdDeadline(0);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setProposalDeadline(new Date());
		this.setProjectDeadline(new Date());
		this.setThesisDeadline(new Date());
		this.setProjectFinalDocumentDeadline(new Date());
		this.setThesisFinalDocumentDeadline(new Date());
		this.setDepartment(new Department());
	}

	public int getIdDeadline() {
		return idDeadline;
	}

	public void setIdDeadline(int idDeadline) {
		this.idDeadline = idDeadline;
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

	public Date getProposalDeadline() {
		return proposalDeadline;
	}

	public void setProposalDeadline(Date proposalDeadline) {
		this.proposalDeadline = proposalDeadline;
	}

	public Date getProjectDeadline() {
		return projectDeadline;
	}

	public void setProjectDeadline(Date projectDeadline) {
		this.projectDeadline = projectDeadline;
	}

	public Date getThesisDeadline() {
		return thesisDeadline;
	}

	public void setThesisDeadline(Date thesisDeadline) {
		this.thesisDeadline = thesisDeadline;
	}
	public void setDepartment(Department department){
		this.department = department;
	}
	public Department getDepartment(){
		return department;
	}
	public Date getProjectFinalDocumentDeadline() {
		return projectFinalDocumentDeadline;
	}
	public void setProjectFinalDocumentDeadline(Date projectFinalDocumentDeadline) {
		this.projectFinalDocumentDeadline = projectFinalDocumentDeadline;
	}
	public Date getThesisFinalDocumentDeadline() {
		return thesisFinalDocumentDeadline;
	}
	public void setThesisFinalDocumentDeadline(Date thesisFinalDocumentDeadline) {
		this.thesisFinalDocumentDeadline = thesisFinalDocumentDeadline;
	}
	
}
