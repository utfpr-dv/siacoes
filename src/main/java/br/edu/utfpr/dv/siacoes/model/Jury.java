package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Jury implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum JuryResult{
		NONE(0), APPROVED(1), APPROVEDWITHRESERVATIONS(2), DISAPPROVED(3);
		
		private final int value; 
		JuryResult(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static JuryResult valueOf(int value){
			for(JuryResult d : JuryResult.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case NONE:
					return "Nenhum";
				case APPROVED:
					return "Aprovado";
				case APPROVEDWITHRESERVATIONS:
					return "Aprovado com Ressalvas";
				case DISAPPROVED:
					return "Reprovado";
				default:
					return "";
			}
		}
	}
	
	public enum JuryFormat {
		SYNC(0), ASYNC(1);
		
		private final int value;
		JuryFormat(int value) {
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static JuryFormat valueOf(int value){
			for(JuryFormat d : JuryFormat.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case SYNC:
					return "Síncrona";
				case ASYNC:
					return "Assíncrona";
				default:
					return "";
			}
		}
	}
	
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
	private JuryRequest juryRequest;
	private String supervisorAbsenceReason;
	private boolean supervisorAssignsGrades;
	private String sei;
	private JuryFormat format;
	
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
		this.setJuryRequest(null);
		this.setSupervisorAbsenceReason("");
		this.setSupervisorAssignsGrades(false);
		this.setSei("");
		this.setFormat(JuryFormat.SYNC);
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
	public JuryRequest getJuryRequest() {
		return juryRequest;
	}
	public void setJuryRequest(JuryRequest juryRequest) {
		this.juryRequest = juryRequest;
	}
	public String getSupervisorAbsenceReason() {
		return supervisorAbsenceReason;
	}
	public void setSupervisorAbsenceReason(String supervisorAbsenceReason) {
		this.supervisorAbsenceReason = supervisorAbsenceReason;
	}
	public boolean isSupervisorAssignsGrades() {
		return supervisorAssignsGrades;
	}
	public void setSupervisorAssignsGrades(boolean supervisorAssignsGrades) {
		this.supervisorAssignsGrades = supervisorAssignsGrades;
	}
	public String getSei() {
		return sei;
	}
	public void setSei(String sei) {
		this.sei = sei;
	}
	public JuryFormat getFormat() {
		return format;
	}
	public void setFormat(JuryFormat format) {
		this.format = format;
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
			if((this.getThesis().getSupervisor() == null) || (this.getThesis().getSupervisor().getIdUser() == 0)){
				this.loadThesis();
			}
			
			return this.getThesis().getSupervisor();
		}else{
			if((this.getProject().getSupervisor() == null) || (this.getProject().getSupervisor().getIdUser() == 0)){
				this.loadProject();
			}
			
			return this.getProject().getSupervisor();
		}
	}
	public User getCosupervisor(){
		if((this.getThesis() != null) && (this.getThesis().getIdThesis() != 0)){
			if((this.getThesis().getSupervisor() == null) || (this.getThesis().getSupervisor().getIdUser() == 0)){
				this.loadThesis();
			}
			
			return this.getThesis().getCosupervisor();
		}else{
			if((this.getProject().getSupervisor() == null) || (this.getProject().getSupervisor().getIdUser() == 0)){
				this.loadProject();
			}
			
			return this.getProject().getCosupervisor();
		}
	}
	public User getStudent(){
		if((this.getThesis() != null) && (this.getThesis().getIdThesis() != 0)){
			if((this.getThesis().getStudent() == null) || (this.getThesis().getStudent().getIdUser() == 0)){
				this.loadThesis();
			}
			
			return this.getThesis().getStudent();
		}else{
			if((this.getProject().getStudent() == null) || (this.getProject().getStudent().getIdUser() == 0)){
				this.loadProject();
			}
			
			return this.getProject().getStudent();
		}
	}
	public String getTitle(){
		if((this.getThesis() != null) && (this.getThesis().getIdThesis() != 0)){
			if((this.getThesis().getStudent() == null) || (this.getThesis().getStudent().getIdUser() == 0)){
				this.loadThesis();
			}
			
			return this.getThesis().getTitle();
		}else{
			if((this.getProject().getStudent() == null) || (this.getProject().getStudent().getIdUser() == 0)){
				this.loadProject();
			}
			
			return this.getProject().getTitle();
		}
	}
	
	private void loadThesis(){
		try {
			ThesisBO bo = new ThesisBO();
			
			this.setThesis(bo.findById(this.getThesis().getIdThesis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadProject(){
		try {
			ProjectBO bo = new ProjectBO();
			
			this.setProject(bo.findById(this.getProject().getIdProject()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
