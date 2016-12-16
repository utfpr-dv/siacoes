package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;

public class FinalDocument {
	
	private int idFinalDocument;
	private Project project;
	private Thesis thesis;
	private String title;
	private Date submissionDate;
	private byte[] file;
	
	public FinalDocument(){
		this.setIdFinalDocument(0);
		this.setProject(new Project());
		this.setThesis(new Thesis());
		this.setTitle("");
		this.setSubmissionDate(new Date());
		this.setFile(null);
	}
	
	public int getIdFinalDocument() {
		return idFinalDocument;
	}
	public void setIdFinalDocument(int idFinalDocument) {
		this.idFinalDocument = idFinalDocument;
	}
	public Project getProject(){
		return project;
	}
	public void setProject(Project project){
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
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public int getStage(){
		if((this.getThesis() != null) && (this.getThesis().getIdThesis() != 0))
			return 2;
		else
			return 1;
	}

}
