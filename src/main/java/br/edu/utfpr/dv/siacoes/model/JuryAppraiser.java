package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.List;

public class JuryAppraiser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idJuryAppraiser;
	private Jury jury;
	private User appraiser;
	private transient byte[] file;
	private transient byte[] additionalFile;
	private List<JuryAppraiserScore> scores;
	private String comments;
	private boolean chair;
	private boolean substitute;
	
	public JuryAppraiser(){
		this.setIdJuryAppraiser(0);
		this.setJury(new Jury());
		this.setAppraiser(new User());
		this.setFile(null);
		this.setAdditionalFile(null);
		this.setScores(null);
		this.setComments("");
		this.setChair(false);
		this.setSubstitute(false);
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
	public byte[] getAdditionalFile() {
		return additionalFile;
	}
	public void setAdditionalFile(byte[] additionalFile) {
		this.additionalFile = additionalFile;
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
