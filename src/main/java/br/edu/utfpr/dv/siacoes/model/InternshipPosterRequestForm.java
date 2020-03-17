package br.edu.utfpr.dv.siacoes.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipPosterRequestForm {
	
	private Date date;
	private Department department;
	private User manager;
	private User student;
	private User supervisor;
	private List<InternshipPosterAppraiserRequest> appraisers;
	private String articles;
	
	public InternshipPosterRequestForm() {
		this.setDate(DateUtils.getNow().getTime());
		this.setDepartment(new Department());
		this.setManager(new User());
		this.setStudent(new User());
		this.setSupervisor(new User());
		this.setAppraisers(new ArrayList<InternshipPosterAppraiserRequest>());
		this.setArticles("");
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public User getManager() {
		return manager;
	}
	public void setManager(User manager) {
		this.manager = manager;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public User getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}
	public List<InternshipPosterAppraiserRequest> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<InternshipPosterAppraiserRequest> appraisers) {
		this.appraisers = appraisers;
	}
	public String getArticles() {
		return articles;
	}
	public void setArticles(String articles) {
		this.articles = articles;
	}

}
