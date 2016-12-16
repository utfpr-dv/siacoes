package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Jury {
	
	private int idJury;
	private Date date;
	private String local;
	private Project project;
	private Thesis thesis;
	private List<JuryAppraiser> appraisers;
	private List<JuryStudent> participants;
	private String comments;
	private Date startTime;
	private Date endTime;
	private double minimumScore;
	
	public Jury(){
		this.setIdJury(0);
		this.setDate(DateUtils.getNow().getTime());
		this.setLocal("");
		this.setProject(null);
		this.setThesis(null);
		this.setAppraisers(null);
		this.setComments("");
		this.setStartTime(new Date());
		this.setEndTime(new Date());
		this.setMinimumScore(0);
	}
	
	public int getIdJury() {
		return idJury;
	}
	public void setIdJury(int idJury) {
		this.idJury = idJury;
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
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Thesis getThesis() {
		return thesis;
	}
	public void setThesis(Thesis thesis) {
		this.thesis = thesis;
	}
	public List<JuryAppraiser> getAppraisers(){
		return appraisers;
	}
	public void setAppraisers(List<JuryAppraiser> appraisers){
		this.appraisers = appraisers;
	}
	public List<JuryStudent> getParticipants(){
		return participants;
	}
	public void setParticipants(List<JuryStudent> participants){
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
	public int getStage(){
		if((this.getThesis() != null) && (this.getThesis().getIdThesis() != 0)){
			return 2;
		}else{
			return 1;
		}
	}
	public User getSupervisor(){
		if((this.getThesis() != null) && (this.getThesis().getIdThesis() != 0)){
			Thesis thesis = new Thesis();
			
			try {
				ThesisBO bo = new ThesisBO();
				
				thesis = bo.findById(this.getThesis().getIdThesis());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return thesis.getSupervisor();
		}else{
			Project project = new Project();
			
			try {
				ProjectBO bo = new ProjectBO();
				
				project = bo.findById(this.getProject().getIdProject());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return project.getSupervisor();
		}
	}

}
