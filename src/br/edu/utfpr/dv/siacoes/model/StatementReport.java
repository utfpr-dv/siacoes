package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;

public class StatementReport {
	
	private String studentCode;
	private String name;
	private String event;
	private String student;
	private String title;
	private Date date;
	private Date startTime;
	private Date endTime;
	private String managerName;
	
	public StatementReport(){
		this.setStudentCode("");
		this.setName("");
		this.setEvent("");
		this.setStudent("");
		this.setTitle("");
		this.setDate(new Date());
		this.setStartTime(new Date());
		this.setEndTime(new Date());
		this.setManagerName("");
	}
	
	public String getStudentCode(){
		return studentCode;
	}
	
	public void setStudentCode(String studentCode){
		this.studentCode = studentCode;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

}
