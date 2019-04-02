package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

public class ThemeSuggestion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idThemeSuggestion;
	private Department department;
	private User user;
	private String title;
	private String proponent;
	private String objectives;
	private String proposal;
	private Date submissionDate;
	private boolean active;
	
	public ThemeSuggestion(){
		this.setIdThemeSuggestion(0);
		this.setDepartment(new Department());
		this.setUser(new User());
		this.setTitle("");
		this.setProponent("");
		this.setObjectives("");
		this.setProposal("");
		this.setSubmissionDate(new Date());
		this.setActive(true);
	}
	public int getIdThemeSuggestion() {
		return idThemeSuggestion;
	}
	public void setIdThemeSuggestion(int idThemeSuggestion) {
		this.idThemeSuggestion = idThemeSuggestion;
	}
	public Department getDepartment(){
		return department;
	}
	public void setDepartment(Department department){
		this.department = department;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProponent() {
		return proponent;
	}
	public void setProponent(String proponent) {
		this.proponent = proponent;
	}
	public String getObjectives() {
		return objectives;
	}
	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}
	public String getProposal() {
		return proposal;
	}
	public void setProposal(String proposal) {
		this.proposal = proposal;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

}
