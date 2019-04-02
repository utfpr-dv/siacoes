package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

public class TermOfApprovalReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String student;
	private String supervisor;
	private String member1;
	private String member2;
	private String local;
	private Date date;
	private Date startTime;
	private Date endTime;
	private boolean hideSignatures;
	
	public TermOfApprovalReport(){
		this.setTitle("");
		this.setStudent("");
		this.setSupervisor("");
		this.setMember1("");
		this.setMember2("");
		this.setLocal("");
		this.setDate(new Date());
		this.setStartTime(new Date());
		this.setEndTime(new Date());
		this.setHideSignatures(false);
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
	public String getMember1() {
		return member1;
	}
	public void setMember1(String member1) {
		this.member1 = member1;
	}
	public String getMember2() {
		return member2;
	}
	public void setMember2(String member2) {
		this.member2 = member2;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public boolean isHideSignatures() {
		return hideSignatures;
	}
	public void setHideSignatures(boolean hideSignatures) {
		this.hideSignatures = hideSignatures;
	}

}
