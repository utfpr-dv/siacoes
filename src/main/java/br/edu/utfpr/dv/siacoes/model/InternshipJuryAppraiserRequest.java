package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class InternshipJuryAppraiserRequest implements Serializable {

private static final long serialVersionUID = 1L;
	
	private int idInternshipJuryAppraiserRequest;
	private InternshipJuryRequest juryRequest;
	private User appraiser;
	private boolean chair;
	private boolean substitute;
	
	public InternshipJuryAppraiserRequest() {
		this.setIdInternshipJuryAppraiserRequest(0);
		this.setInternshipJuryRequest(new InternshipJuryRequest());
		this.setAppraiser(new User());
		this.setChair(false);
		this.setSubstitute(false);
	}
	
	public int getIdInternshipJuryAppraiserRequest() {
		return idInternshipJuryAppraiserRequest;
	}
	public void setIdInternshipJuryAppraiserRequest(int idInternshipJuryAppraiserRequest) {
		this.idInternshipJuryAppraiserRequest = idInternshipJuryAppraiserRequest;
	}
	public InternshipJuryRequest getInternshipJuryRequest() {
		return juryRequest;
	}
	public void setInternshipJuryRequest(InternshipJuryRequest juryRequest) {
		this.juryRequest = juryRequest;
	}
	public User getAppraiser() {
		return appraiser;
	}
	public void setAppraiser(User appraiser) {
		this.appraiser = appraiser;
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
