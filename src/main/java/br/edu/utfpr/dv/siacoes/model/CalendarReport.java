package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

public class CalendarReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String local;
	private String title;
	private String student;
	private String appraisers;
	private int stage;
	private String company;
	
	public CalendarReport(){
		this.setDate(new Date());
		this.setLocal("");
		this.setTitle("");
		this.setStudent("");
		this.setAppraisers("");
		this.setStage(1);
		this.setCompany("");
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
	public String getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(String appraisers) {
		this.appraisers = appraisers;
	}
	public int getStage(){
		return stage;
	}
	public void setStage(int stage){
		this.stage = stage;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

}
