package br.edu.utfpr.dv.siacoes.model;

import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class InternshipJuryAppraiser {

	private int idInternshipJuryAppraiser;
	private InternshipJury internshipJury;
	private User appraiser;
	private byte[] file;
	private DocumentType fileType;
	private List<InternshipJuryAppraiserScore> scores;
	private String comments;
	
	public InternshipJuryAppraiser(){
		this.setIdInternshipJuryAppraiser(0);
		this.setInternshipJury(new InternshipJury());
		this.setAppraiser(new User());
		this.setFile(null);
		this.setFileType(DocumentType.UNDEFINED);
		this.setScores(null);
		this.setComments("");
	}
	
	public int getIdInternshipJuryAppraiser() {
		return idInternshipJuryAppraiser;
	}
	public void setIdInternshipJuryAppraiser(int idInternshipJuryAppraiser) {
		this.idInternshipJuryAppraiser = idInternshipJuryAppraiser;
	}
	public InternshipJury getInternshipJury() {
		return internshipJury;
	}
	public void setInternshipJury(InternshipJury internshipJury) {
		this.internshipJury = internshipJury;
	}
	public User getAppraiser() {
		return appraiser;
	}
	public void setAppraiser(User appraiser) {
		this.appraiser = appraiser;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public DocumentType getFileType() {
		return fileType;
	}
	public void setFileType(DocumentType fileType) {
		this.fileType = fileType;
	}
	public List<InternshipJuryAppraiserScore> getScores(){
		return scores;
	}
	public void setScores(List<InternshipJuryAppraiserScore> scores){
		this.scores = scores;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
