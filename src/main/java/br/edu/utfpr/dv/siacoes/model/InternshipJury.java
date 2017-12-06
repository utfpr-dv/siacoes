package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipJury {
	
	private int idInternshipJury;
	private Date date;
	private String local;
	private Internship internship;
	private List<InternshipJuryAppraiser> appraisers;
	private List<InternshipJuryStudent> participants;
	private String comments;
	private Date startTime;
	private Date endTime;
	private double minimumScore;
	private double supervisorPonderosity;
	private double companySupervisorPonderosity;
	private double companySupervisorScore;

	public InternshipJury(){
		this.setIdInternshipJury(0);
		this.setDate(DateUtils.getNow().getTime());
		this.setLocal("");
		this.setInternship(null);
		this.setAppraisers(null);
		this.setComments("");
		this.setStartTime(new Date());
		this.setEndTime(new Date());
		this.setMinimumScore(0);
		this.setSupervisorPonderosity(0);
		this.setCompanySupervisorPonderosity(0);
		this.setCompanySupervisorScore(0);
	}
	
	public int getIdInternshipJury() {
		return idInternshipJury;
	}
	public void setIdInternshipJury(int idJury) {
		this.idInternshipJury = idJury;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
	}
	public List<InternshipJuryAppraiser> getAppraisers(){
		return appraisers;
	}
	public void setAppraisers(List<InternshipJuryAppraiser> appraisers){
		this.appraisers = appraisers;
	}
	public List<InternshipJuryStudent> getParticipants(){
		return participants;
	}
	public void setParticipants(List<InternshipJuryStudent> participants){
		this.participants = participants;
	}
	public String getComments(){
		return comments;
	}
	public void setComments(String comments){
		this.comments = comments;
	}
	public Date getStartTime(){
		return startTime;
	}
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}
	public Date getEndTime(){
		return endTime;
	}
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	public double getMinimumScore(){
		return minimumScore;
	}
	public void setMinimumScore(double minimumScore){
		this.minimumScore = minimumScore;
	}
	public double getSupervisorPonderosity() {
		return supervisorPonderosity;
	}
	public void setSupervisorPonderosity(double supervisorPonderosity) {
		this.supervisorPonderosity = supervisorPonderosity;
	}
	public double getCompanySupervisorPonderosity() {
		return companySupervisorPonderosity;
	}
	public void setCompanySupervisorPonderosity(double companySupervisorPonderosity) {
		this.companySupervisorPonderosity = companySupervisorPonderosity;
	}
	public double getCompanySupervisorScore() {
		return companySupervisorScore;
	}
	public void setCompanySupervisorScore(double companySupervisorScore) {
		this.companySupervisorScore = companySupervisorScore;
	}
	public User getSupervisor(){
		if((this.getInternship().getSupervisor() == null) || (this.getInternship().getSupervisor().getIdUser() == 0)){
			this.loadInternship();
		}
		
		return this.getInternship().getSupervisor();
	}
	public User getCompanySupervisor(){
		if((this.getInternship().getCompanySupervisor() == null) || (this.getInternship().getCompanySupervisor().getIdUser() == 0)){
			this.loadInternship();
		}
		
		return this.getInternship().getCompanySupervisor();
	}
	public User getStudent(){
		if((this.getInternship().getStudent() == null) || (this.getInternship().getStudent().getIdUser() == 0)){
			this.loadInternship();
		}
		
		return this.getInternship().getStudent();
	}
	
	private void loadInternship(){
		try {
			InternshipBO bo = new InternshipBO();
			
			this.setInternship(bo.findById(this.getInternship().getIdInternship()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
