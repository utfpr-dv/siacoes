package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipJuryRequest implements Serializable {

private static final long serialVersionUID = 1L;

	private int idInternshipJuryRequest;
	private Date date;
	private String local;
	private Internship internship;
	private List<InternshipJuryAppraiserRequest> appraisers;
	private String comments;
	private InternshipJury jury;

	public InternshipJuryRequest(){
		this.setIdInternshipJuryRequest(0);
		this.setDate(DateUtils.getNow().getTime());
		this.setLocal("");
		this.setInternship(new Internship());
		this.setAppraisers(null);
		this.setComments("");
		this.setInternshipJury(null);
	}

	public int getIdInternshipJuryRequest() {
		return idInternshipJuryRequest;
	}
	public void setIdInternshipJuryRequest(int idInternshipJuryRequest) {
		this.idInternshipJuryRequest = idInternshipJuryRequest;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
	}
	public List<InternshipJuryAppraiserRequest> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<InternshipJuryAppraiserRequest> appraisers) {
		this.appraisers = appraisers;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public InternshipJury getInternshipJury() {
		return jury;
	}
	public void setInternshipJury(InternshipJury jury) {
		this.jury = jury;
	}
	public boolean isConfirmed() {
		return ((this.getInternshipJury() != null) && (this.getInternshipJury().getIdInternshipJury() != 0));
	}
	public String getStudent() {
		if(this.getInternship() != null) {
			return this.getInternship().getStudent().getName();
		} else {
			return "";
		}
	}
	public String getSupervisor() {
		if(this.getInternship() != null) {
			return this.getInternship().getSupervisor().getName();
		} else {
			return "";
		}
	}

}
