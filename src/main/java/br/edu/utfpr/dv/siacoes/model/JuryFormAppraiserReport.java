package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JuryFormAppraiserReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idUser;
	private int stage;
	private String title;
	private String company;
	private Date date;
	private String local;
	private String student;
	private String description;
	private String name;
	private String comments;
	private double score;
	private List<JuryFormAppraiserDetailReport> detail;
	
	public JuryFormAppraiserReport(){
		this.setIdUser(0);
		this.setStage(0);
		this.setTitle("");
		this.setCompany("");
		this.setDate(new Date());
		this.setLocal("");
		this.setStudent("");
		this.setDescription("");
		this.setName("");
		this.setComments("");
		this.setScore(0);
		this.setDetail(new ArrayList<JuryFormAppraiserDetailReport>());
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public List<JuryFormAppraiserDetailReport> getDetail() {
		return detail;
	}
	public void setDetail(List<JuryFormAppraiserDetailReport> detail) {
		this.detail = detail;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}

}
