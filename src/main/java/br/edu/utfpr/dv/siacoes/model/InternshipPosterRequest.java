package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class InternshipPosterRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idInternshipPosterRequest;
	private Internship internship;
	private InternshipJury jury;
	private Date requestDate;
	private List<InternshipPosterAppraiserRequest> appraisers;
	
	public InternshipPosterRequest() {
		this.setIdInternshipPosterRequest(0);
		this.setInternship(new Internship());
		this.setJury(null);
		this.setRequestDate(null);
		this.setAppraisers(null);
	}
	
	public int getIdInternshipPosterRequest() {
		return idInternshipPosterRequest;
	}
	public void setIdInternshipPosterRequest(int idInternshipPosterRequest) {
		this.idInternshipPosterRequest = idInternshipPosterRequest;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
	}
	public InternshipJury getJury() {
		return jury;
	}
	public void setJury(InternshipJury jury) {
		this.jury = jury;
	}
	public List<InternshipPosterAppraiserRequest> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<InternshipPosterAppraiserRequest> appraisers) {
		this.appraisers = appraisers;
	}
	public boolean isConfirmed() {
		return ((this.getJury() != null) && (this.getJury().getIdInternshipJury() != 0));
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

}
