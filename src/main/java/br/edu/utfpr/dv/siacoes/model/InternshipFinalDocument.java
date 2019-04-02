package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;

public class InternshipFinalDocument implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idInternshipFinalDocument;
	private Internship internship;
	private String title;
	private Date submissionDate;
	private transient byte[] file;
	private boolean _private;
	private Date supervisorFeedbackDate;
	private DocumentFeedback supervisorFeedback;
	private String comments;
	
	public InternshipFinalDocument(){
		this.setIdInternshipFinalDocument(0);
		this.setInternship(new Internship());
		this.setTitle("");
		this.setSubmissionDate(new Date());
		this.setFile(null);
		this.setPrivate(false);
		this.setSupervisorFeedbackDate(null);
		this.setSupervisorFeedback(DocumentFeedback.NONE);
		this.setComments("");
	}
	
	public int getIdInternshipFinalDocument() {
		return idInternshipFinalDocument;
	}
	public void setIdInternshipFinalDocument(int idInternshipFinalDocument) {
		this.idInternshipFinalDocument = idInternshipFinalDocument;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
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
	public boolean isPrivate() {
		return _private;
	}
	public void setPrivate(boolean _private) {
		this._private = _private;
	}
	public Date getSupervisorFeedbackDate() {
		return supervisorFeedbackDate;
	}
	public void setSupervisorFeedbackDate(Date supervisorFeedbackDate) {
		this.supervisorFeedbackDate = supervisorFeedbackDate;
	}
	public DocumentFeedback getSupervisorFeedback() {
		return supervisorFeedback;
	}
	public void setSupervisorFeedback(DocumentFeedback supervisorFeedback) {
		this.supervisorFeedback = supervisorFeedback;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

}
