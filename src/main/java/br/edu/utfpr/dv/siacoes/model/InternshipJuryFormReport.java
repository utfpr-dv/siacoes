package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;

public class InternshipJuryFormReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String title;
	private Date date;
	private String local;
	private int idStudent;
	private String student;
	private String comments;
	private String company;
	private String supervisor;
	private int idSupervisor;
	private double appraisersPonderosity;
	private double supervisorPonderosity;
	private double companySupervisorPonderosity;
	private double appraiser1Score;
	private double appraiser2Score;
	private double supervisorScore;
	private double companySupervisorScore;
	private double finalScore;
	private JuryResult result;
	private List<JuryFormAppraiserReport> appraisers;
	private List<JuryFormAppraiserReport> appraisersSignatures;
	
	public InternshipJuryFormReport(){
		this.setTitle("");
		this.setDate(new Date());
		this.setLocal("");
		this.setStudent("");
		this.setComments("");
		this.setCompany("");
		this.setAppraisersPonderosity(0);
		this.setSupervisorPonderosity(0);
		this.setCompanySupervisorPonderosity(0);
		this.setAppraiser1Score(0);
		this.setAppraiser2Score(0);
		this.setSupervisorScore(0);
		this.setCompanySupervisorScore(0);
		this.setAppraisers(new ArrayList<JuryFormAppraiserReport>());
		this.setAppraisersSignatures(new ArrayList<JuryFormAppraiserReport>());
		this.setResult(JuryResult.NONE);
		this.setIdStudent(0);
		this.setIdSupervisor(0);
		this.setSupervisor("");
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
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public double getAppraisersPonderosity() {
		return appraisersPonderosity;
	}
	public void setAppraisersPonderosity(double appraisersPonderosity) {
		this.appraisersPonderosity = appraisersPonderosity;
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
	public double getAppraiser1Score() {
		return appraiser1Score;
	}
	public void setAppraiser1Score(double appraiser1Score) {
		this.appraiser1Score = appraiser1Score;
	}
	public double getAppraiser2Score() {
		return appraiser2Score;
	}
	public void setAppraiser2Score(double appraiser2Score) {
		this.appraiser2Score = appraiser2Score;
	}
	public double getSupervisorScore() {
		return supervisorScore;
	}
	public void setSupervisorScore(double supervisorScore) {
		this.supervisorScore = supervisorScore;
	}
	public double getCompanySupervisorScore() {
		return companySupervisorScore;
	}
	public void setCompanySupervisorScore(double companySupervisorScore) {
		this.companySupervisorScore = companySupervisorScore;
	}
	public double getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}
	public List<JuryFormAppraiserReport> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<JuryFormAppraiserReport> appraisers) {
		this.appraisers = appraisers;
	}
	public List<JuryFormAppraiserReport> getAppraisersSignatures() {
		return appraisersSignatures;
	}
	public void setAppraisersSignatures(List<JuryFormAppraiserReport> appraisersSignatures) {
		this.appraisersSignatures = appraisersSignatures;
	}
	public JuryResult getResult() {
		return result;
	}
	public void setResult(JuryResult result) {
		this.result = result;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
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
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

}
