package br.edu.utfpr.dv.siacoes.model;

import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class JuryAppraiser {
	
	private int idJuryAppraiser;
	private Jury jury;
	private User appraiser;
	private byte[] file;
	private DocumentType fileType;
	private List<JuryAppraiserScore> scores;
	private String comments;
	
	public JuryAppraiser(){
		this.setIdJuryAppraiser(0);
		this.setJury(new Jury());
		this.setAppraiser(new User());
		this.setFile(null);
		this.setFileType(DocumentType.UNDEFINED);
		this.setScores(null);
		this.setComments("");
	}
	
	public int getIdJuryAppraiser() {
		return idJuryAppraiser;
	}
	public void setIdJuryAppraiser(int idJuryAppraiser) {
		this.idJuryAppraiser = idJuryAppraiser;
	}
	public Jury getJury() {
		return jury;
	}
	public void setJury(Jury jury) {
		this.jury = jury;
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
	public List<JuryAppraiserScore> getScores(){
		return scores;
	}
	public void setScores(List<JuryAppraiserScore> scores){
		this.scores = scores;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

}
