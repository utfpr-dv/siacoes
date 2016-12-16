package br.edu.utfpr.dv.siacoes.model;

import java.util.ArrayList;
import java.util.List;

public class AttendanceReport {
	
	private int stage;
	private String title;
	private String student;
	private String supervisor;
	private List<Attendance> attendances;
	
	public AttendanceReport(){
		this.setStage(1);
		this.setTitle("");
		this.setStudent("");
		this.setSupervisor("");
		this.setAttendances(new ArrayList<Attendance>());
	}
	
	public int getStage(){
		return stage;
	}
	public void setStage(int stage){
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
	public List<Attendance> getAttendances() {
		return attendances;
	}
	public void setAttendances(List<Attendance> attendances) {
		this.attendances = attendances;
	}

}
