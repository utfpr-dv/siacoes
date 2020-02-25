package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class ProposalFeedback extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String title;
	private String student;
	private String supervisor;
	private String appraiser;
	private String institution;
	private String area;
	private br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback feedback;
	private String comments;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getAppraiser() {
		return appraiser;
	}
	public void setAppraiser(String appraiser) {
		this.appraiser = appraiser;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback getFeedback() {
		return feedback;
	}
	public void setFeedback(br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback feedback) {
		this.feedback = feedback;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public InputStream getSupervisorSignature() {
		if(this.getSignatures().size() > 0) {
			return this.getSignatures().get(0).getSignature();
		} else {
			return null;
		}
	}
	
	public ProposalFeedback() {
		this.setDate(null);
		this.setTitle("");
		this.setStudent("");
		this.setSupervisor("");
		this.setAppraiser("");
		this.setInstitution("");
		this.setArea("");
		this.setFeedback(br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback.NONE);
		this.setComments("");
	}

}
