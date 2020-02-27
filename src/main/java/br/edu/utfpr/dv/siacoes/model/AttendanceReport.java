package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttendanceReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idGroup;
	private int stage;
	private String title;
	private int idStudent;
	private String student;
	private int idSupervisor;
	private String supervisor;
	private List<Attendance> attendances;
	
	public AttendanceReport(){
		this.setIdGroup(0);
		this.setStage(1);
		this.setTitle("");
		this.setStudent("");
		this.setSupervisor("");
		this.setAttendances(new ArrayList<Attendance>());
		this.setIdStudent(0);
		this.setIdSupervisor(0);
	}
	
	public int getIdGroup() {
		return idGroup;
	}
	public void setIdGroup(int idGroup) {
		this.idGroup = idGroup;
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
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
	}
	public int getIdSupervisor() {
		return idSupervisor;
	}
	public void setIdSupervisor(int idSupervisor) {
		this.idSupervisor = idSupervisor;
	}

}
