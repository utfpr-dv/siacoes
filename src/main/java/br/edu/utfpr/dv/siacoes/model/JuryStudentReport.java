package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

public class JuryStudentReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idJury;
	private int idStudent;
	private int idJuryStudent;
	private int stage;
	private String studentName;
	private String studentCode;
	private String juryStudentName;
	private Date date;
	private Date startTime;
	private Date endTime;
	
	public JuryStudentReport() {
		this.setIdJury(0);
		this.setIdStudent(0);
		this.setIdJuryStudent(0);
		this.setStage(0);
		this.setStudentName("");
		this.setStudentCode("");
		this.setJuryStudentName("");
		this.setDate(new Date());
		this.setStartTime(new Date());
		this.setEndTime(new Date());
	}
	
	public int getIdJury() {
		return idJury;
	}
	public void setIdJury(int idJury) {
		this.idJury = idJury;
	}
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
	}
	public int getIdJuryStudent() {
		return idJuryStudent;
	}
	public void setIdJuryStudent(int idJuryStudent) {
		this.idJuryStudent = idJuryStudent;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
	public String getJuryStudentName() {
		return juryStudentName;
	}
	public void setJuryStudentName(String juryStudentName) {
		this.juryStudentName = juryStudentName;
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

}
