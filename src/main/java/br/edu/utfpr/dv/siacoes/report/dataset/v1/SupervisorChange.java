package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class SupervisorChange extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private boolean supervisorRequest;
	private String oldSupervisor;
	private String student;
	private String studentCode;
	private int stage;
	private String title;
	private String newSupervisor;
	private String comments;
	private Date date;
	
	public boolean isSupervisorRequest() {
		return supervisorRequest;
	}
	public void setSupervisorRequest(boolean supervisorRequest) {
		this.supervisorRequest = supervisorRequest;
	}
	public String getOldSupervisor() {
		return oldSupervisor;
	}
	public void setOldSupervisor(String oldSupervisor) {
		this.oldSupervisor = oldSupervisor;
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
	public String getNewSupervisor() {
		return newSupervisor;
	}
	public void setNewSupervisor(String newSupervisor) {
		this.newSupervisor = newSupervisor;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSignatureName() {
		if(this.getSignatures().size() > 0) {
			return this.getSignatures().get(0).getName();
		} else {
			return "";
		}
	}
	public InputStream getSignature() {
		if(this.getSignatures().size() > 0) {
			return this.getSignatures().get(0).getSignature();
		} else {
			return null;
		}
	}
	
	public SupervisorChange() {
		this.setSupervisorRequest(false);
		this.setOldSupervisor("");
		this.setStudent("");
		this.setStudentCode("");
		this.setStage(1);
		this.setTitle("");
		this.setNewSupervisor("");
		this.setComments("");
		this.setDate(null);
	}

}
