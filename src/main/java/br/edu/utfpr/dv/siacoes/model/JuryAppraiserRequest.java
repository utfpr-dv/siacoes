package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class JuryAppraiserRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idJuryAppraiserRequest;
	private JuryRequest juryRequest;
	private User appraiser;
	private boolean chair;
	private boolean substitute;
	
	public JuryAppraiserRequest() {
		this.setIdJuryAppraiserRequest(0);
		this.setJuryRequest(new JuryRequest());
		this.setAppraiser(new User());
		this.setChair(false);
		this.setSubstitute(false);
	}
	
	public int getIdJuryAppraiserRequest() {
		return idJuryAppraiserRequest;
	}
	public void setIdJuryAppraiserRequest(int idJuryAppraiserRequest) {
		this.idJuryAppraiserRequest = idJuryAppraiserRequest;
	}
	public JuryRequest getJuryRequest() {
		return juryRequest;
	}
	public void setJuryRequest(JuryRequest juryRequest) {
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
