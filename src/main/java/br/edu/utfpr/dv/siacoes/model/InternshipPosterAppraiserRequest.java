package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class InternshipPosterAppraiserRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idInternshipPosterAppraiserRequest;
	private InternshipPosterRequest request;
	private User appraiser;
	private boolean substitute;
	
	public InternshipPosterAppraiserRequest() {
		this.setIdInternshipPosterAppraiserRequest(0);
		this.setRequest(new InternshipPosterRequest());
		this.setAppraiser(new User());
		this.setSubstitute(false);
	}
	
	public int getIdInternshipPosterAppraiserRequest() {
		return idInternshipPosterAppraiserRequest;
	}
	public void setIdInternshipPosterAppraiserRequest(int idInternshipPosterAppraiserRequest) {
		this.idInternshipPosterAppraiserRequest = idInternshipPosterAppraiserRequest;
	}
	public InternshipPosterRequest getRequest() {
		return request;
	}
	public void setRequest(InternshipPosterRequest request) {
		this.request = request;
	}
	public User getAppraiser() {
		return appraiser;
	}
	public void setAppraiser(User appraiser) {
		this.appraiser = appraiser;
	}
	public boolean isSubstitute() {
		return substitute;
	}
	public void setSubstitute(boolean substitute) {
		this.substitute = substitute;
	}

}
