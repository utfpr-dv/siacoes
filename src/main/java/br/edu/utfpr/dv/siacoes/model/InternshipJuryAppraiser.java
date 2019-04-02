package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.List;

public class InternshipJuryAppraiser implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idInternshipJuryAppraiser;
	private InternshipJury internshipJury;
	private User appraiser;
	private transient byte[] file;
	private transient byte[] additionalFile;
	private List<InternshipJuryAppraiserScore> scores;
	private String comments;
	private boolean chair;
	private boolean substitute;
	
	public InternshipJuryAppraiser(){
		this.setIdInternshipJuryAppraiser(0);
		this.setInternshipJury(new InternshipJury());
		this.setAppraiser(new User());
		this.setFile(null);
		this.setAdditionalFile(null);
		this.setScores(null);
		this.setComments("");
		this.setChair(false);
		this.setSubstitute(false);
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
	public byte[] getAdditionalFile() {
		return additionalFile;
	}
	public void setAdditionalFile(byte[] additionalFile) {
		this.additionalFile = additionalFile;
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
	public boolean isChair() {
		return chair;
	}
	public void setChair(boolean chair) {
		this.chair = chair;
	}
	public boolean isSubstitute() {
		return substitute;
	}
	public void setSubstitute(boolean substitute) {
		this.substitute = substitute;
	}
	
}
