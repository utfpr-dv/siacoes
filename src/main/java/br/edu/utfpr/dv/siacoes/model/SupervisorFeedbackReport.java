package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class SupervisorFeedbackReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int stage;
	private String title;
	private String student;
	private String supervisor;
	
	public SupervisorFeedbackReport(){
		this.setStage(1);
		this.setTitle("");
		this.setStudent("");
		this.setSupervisor("");
	}
	
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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

}
