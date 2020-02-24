package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class SupervisorAgreement extends SignDataset {

	private Date date;
	private String title;
	private String student;
	private String supervisor;
	private ProposalFeedback feedback;
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
	public ProposalFeedback getFeedback() {
		return feedback;
	}
	public void setFeedback(ProposalFeedback feedback) {
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
	
	public SupervisorAgreement() {
		this.setDate(null);
		this.setTitle("");
		this.setStudent("");
		this.setSupervisor("");
		this.setFeedback(ProposalFeedback.NONE);
		this.setComments("");
	}
	
}
