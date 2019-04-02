package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Attendance implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idAttendance;
	private Proposal proposal;
	private User supervisor;
	private User student;
	private Date date;
	private Date startTime;
	private Date endTime;
	private String comments;
	private String nextMeeting;
	private int stage;
	
	public Attendance(){
		this.setIdAttendance(0);
		this.setProposal(new Proposal());
		this.setSupervisor(new User());
		this.setStudent(new User());
		this.setDate(DateUtils.getToday().getTime());
		this.setStartTime(new Date());
		this.setEndTime(new Date());
		this.setComments("");
		this.setNextMeeting("");
		this.setStage(1);
	}
	
	public int getIdAttendance() {
		return idAttendance;
	}
	public void setIdAttendance(int idAttendance) {
		this.idAttendance = idAttendance;
	}
	public Proposal getProposal() {
		return proposal;
	}
	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}
	public User getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
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
	public String getComments(){
		return comments;
	}
	public void setComments(String comments){
		this.comments = comments;
	}
	public String getNextMeeting(){
		return nextMeeting;
	}
	public void setNextMeeting(String nextMeeting){
		this.nextMeeting = nextMeeting;
	}
	public int getStage(){
		return stage;
	}
	public void setStage(int stage){
		this.stage = stage;
	}

}
