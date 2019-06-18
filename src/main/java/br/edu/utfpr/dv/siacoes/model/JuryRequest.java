package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class JuryRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idJuryRequest;
	private Date date;
	private String local;
	private Proposal proposal;
	private int stage;
	private List<JuryAppraiserRequest> appraisers;
	private String comments;
	private String supervisorAbsenceReason;
	private Jury jury;
	private String student;
	private String title;
	
	public JuryRequest(){
		this.setIdJuryRequest(0);
		this.setDate(DateUtils.getNow().getTime());
		this.setLocal("");
		this.setProposal(new Proposal());
		this.setAppraisers(null);
		this.setComments("");
		this.setSupervisorAbsenceReason("");
		this.setJury(null);
		this.student = "";
		this.title = "";
	}
	
	public int getIdJuryRequest() {
		return idJuryRequest;
	}
	public void setIdJuryRequest(int idJuryRequest) {
		this.idJuryRequest = idJuryRequest;
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
	public Proposal getProposal() {
		return proposal;
	}
	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public List<JuryAppraiserRequest> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<JuryAppraiserRequest> appraisers) {
		this.appraisers = appraisers;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getSupervisorAbsenceReason() {
		return supervisorAbsenceReason;
	}
	public void setSupervisorAbsenceReason(String supervisorAbsenceReason) {
		this.supervisorAbsenceReason = supervisorAbsenceReason;
	}
	public Jury getJury() {
		return jury;
	}
	public void setJury(Jury jury) {
		this.jury = jury;
	}
	public boolean isConfirmed() {
		return ((this.getJury() != null) && (this.getJury().getIdJury() != 0));
	}
	public User getSupervisor() {
		if((this.getProposal() == null) || (this.getProposal().getIdProposal() == 0)) {
			return new User();
		} else {
			try {
				return new SupervisorChangeBO().findCurrentSupervisor(this.getProposal().getIdProposal());
			} catch (Exception e) {
				return new User();
			}
		}
	}
	public User getCosupervisor() {
		if((this.getProposal() == null) || (this.getProposal().getIdProposal() == 0)) {
			return null;
		} else {
			try {
				return new SupervisorChangeBO().findCurrentCosupervisor(this.getProposal().getIdProposal());
			} catch (Exception e) {
				return new User();
			}
		}
	}
	public String getStudent() {
		if(this.student.isEmpty() && (this.getProposal() != null) && (this.getProposal().getIdProposal() != 0)) {
			if((this.getProposal().getStudent() != null) && !this.getProposal().getStudent().getName().isEmpty()) {
				this.student = this.getProposal().getStudent().getName();
			} else {
				try {
					this.student = new ProposalBO().getStudentName(this.getProposal().getIdProposal());
				} catch (Exception e) {
					this.student = "";
				}
			}
		}
		
		return this.student;
	}
	public String getTitle() {
		if(this.title.isEmpty() && (this.getProposal() != null) && (this.getProposal().getIdProposal() != 0)) {
			try {
				Thesis thesis = new ThesisBO().findByProposal(this.getProposal().getIdProposal());
				
				if((thesis != null) && (thesis.getIdThesis() != 0)) {
					this.title = thesis.getTitle();
				} else {
					Project project = new ProjectBO().findByProposal(this.getProposal().getIdProposal());
					
					if((project != null) && (project.getIdProject() != 0)) {
						this.title = project.getTitle();
					} else {
						this.setProposal(new ProposalBO().findById(this.getProposal().getIdProposal()));
						
						this.title = this.getProposal().getTitle();
					}
				}
			} catch (Exception e) {
				this.title = "";
			}
		} 

		return this.title;
	}

}
