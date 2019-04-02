package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class BugReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum BugStatus{
		REPORTED(0), DEVELOPMENT(1), SOLVED(2), REFUSED(3);
		
		private final int value; 
		BugStatus(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static BugStatus valueOf(int value){
			for(BugStatus d : BugStatus.values()){
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
				case DEVELOPMENT:
					return "Em análise/desenvolvimento";
				case SOLVED:
					return "Solucionado";
				case REFUSED:
					return "Recusado";
				default:
					return "Reportado";
			}
		}
	}
	
	public enum BugType{
		ERROR(0), SUGESTION(1);
		
		private final int value; 
		BugType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static BugType valueOf(int value){
			for(BugType d : BugType.values()){
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
				case SUGESTION:
					return "Sugestão";
				default:
					return "Erro";
			}
		}
	}
	
	private int idBugReport;
	private User user;
	private Module.SystemModule module;
	private String title;
	private String description;
	private Date reportDate;
	private BugType type;
	private BugStatus status;
	private Date statusDate;
	private String statusDescription;
	
	public BugReport(){
		this.setIdBugReport(0);
		this.setUser(new User());
		this.setModule(Module.SystemModule.GENERAL);
		this.setTitle("");
		this.setDescription("");
		this.setReportDate(DateUtils.getToday().getTime());
		this.setType(BugType.ERROR);
		this.setStatus(BugStatus.REPORTED);
		this.setStatusDate(null);
		this.setStatusDescription("");
	}
	
	public int getIdBugReport() {
		return idBugReport;
	}
	public void setIdBugReport(int idBugReport) {
		this.idBugReport = idBugReport;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Module.SystemModule getModule() {
		return module;
	}
	public void setModule(Module.SystemModule module) {
		this.module = module;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	public BugType getType(){
		return type;
	}
	public void setType(BugType type){
		this.type = type;
	}
	public BugStatus getStatus() {
		return status;
	}
	public void setStatus(BugStatus status) {
		this.status = status;
	}
	public Date getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	
}
