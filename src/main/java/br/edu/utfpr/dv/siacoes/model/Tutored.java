package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class Tutored implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int stage;
	private Proposal proposal;
	private Project project;
	private Thesis thesis;
	private String title;
	private int semester;
	private int year;
	private User student;
	private User supervisor;
	private User cosupervisor;
	private FinalDocument projectFinalDocument;
	private FinalDocument thesisFinalDocument;
	
	public Tutored(){
		this.setStage(0);
		this.setProposal(new Proposal());
		this.setProject(new Project());
		this.setThesis(new Thesis());
		this.setTitle("");
		this.setStudent(new User());
		this.setSupervisor(new User());
		this.setCosupervisor(new User());
		this.setProjectFinalDocument(new FinalDocument());
		this.setThesisFinalDocument(new FinalDocument());
	}
	
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public Proposal getProposal() {
		return proposal;
	}
	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public User getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}
	public User getCosupervisor() {
		return cosupervisor;
	}
	public void setCosupervisor(User cosupervisor) {
		this.cosupervisor = cosupervisor;
	}
	public FinalDocument getProjectFinalDocument() {
		return projectFinalDocument;
	}
	public void setProjectFinalDocument(FinalDocument projectFinalDocument) {
		this.projectFinalDocument = projectFinalDocument;
	}
	public FinalDocument getThesisFinalDocument() {
		return thesisFinalDocument;
	}
	public void setThesisFinalDocument(FinalDocument thesisFinalDocument) {
		this.thesisFinalDocument = thesisFinalDocument;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

}
