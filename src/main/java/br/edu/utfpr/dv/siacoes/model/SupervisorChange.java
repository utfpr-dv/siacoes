package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SupervisorChange implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum ChangeFeedback{
		NONE(0), APPROVED(1), DISAPPROVED(2);
		
		private final int value; 
		ChangeFeedback(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static ChangeFeedback valueOf(int value){
			for(ChangeFeedback p : ChangeFeedback.values()){
				if(p.getValue() == value){
					return p;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
			case NONE:
				return "Pendente";
			case APPROVED:
				return "Aprovada";
			case DISAPPROVED:
				return "Reprovada";
			default:
				return "";
			}
		}
	}
	
	private int idSupervisorChange;
	private Proposal proposal;
	private User oldSupervisor;
	private User newSupervisor;
	private User oldCosupervisor;
	private User newCosupervisor;
	private Date date;
	private String comments;
	private ChangeFeedback approved;
	private Date approvalDate;
	private String approvalComments;
	private int stage;
	private boolean supervisorRequest;
	
	public SupervisorChange(){
		this.setIdSupervisorChange(0);
		this.setOldSupervisor(new User());
		this.setNewSupervisor(new User());
		this.setOldCosupervisor(new User());
		this.setNewCosupervisor(new User());
		this.setDate(DateUtils.getNow().getTime());
		this.setComments("");
		this.setApproved(ChangeFeedback.NONE);
		this.setApprovalDate(new Date());
		this.setApprovalComments("");
		this.stage = 0;
		this.setSupervisorRequest(false);
	}
	
	public int getIdSupervisorChange() {
		return idSupervisorChange;
	}
	public void setIdSupervisorChange(int idSupervisorChange) {
		this.idSupervisorChange = idSupervisorChange;
	}
	public Proposal getProposal(){
		return proposal;
	}
	public void setProposal(Proposal proposal){
		this.proposal = proposal;
	}
	public User getOldSupervisor() {
		return oldSupervisor;
	}
	public void setOldSupervisor(User oldSupervisor) {
		this.oldSupervisor = oldSupervisor;
	}
	public User getNewSupervisor() {
		return newSupervisor;
	}
	public void setNewSupervisor(User newSupervisor) {
		this.newSupervisor = newSupervisor;
	}
	public User getOldCosupervisor() {
		return oldCosupervisor;
	}
	public void setOldCosupervisor(User oldCosupervisor) {
		this.oldCosupervisor = oldCosupervisor;
	}
	public User getNewCosupervisor() {
		return newCosupervisor;
	}
	public void setNewCosupervisor(User newCosupervisor) {
		this.newCosupervisor = newCosupervisor;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getComments(){
		return comments;
	}
	public void setComments(String comments){
		this.comments = comments;
	}
	public ChangeFeedback getApproved(){
		return approved;
	}
	public void setApproved(ChangeFeedback approved){
		this.approved = approved;
	}
	public Date getApprovalDate(){
		return approvalDate;
	}
	public void setApprovalDate(Date approvalDate){
		this.approvalDate = approvalDate;
	}
	public String getApprovalComments(){
		return approvalComments;
	}
	public void setApprovalComments(String approvalComments){
		this.approvalComments = approvalComments;
	}
	public int getStage(){
		if((stage == 0) && (this.getProposal().getIdProposal() != 0)){
			try {
				ProposalBO bo = new ProposalBO();
				
				stage = bo.getProposalStage(this.getProposal().getIdProposal());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return stage;
	}
	public boolean isSupervisorRequest() {
		return supervisorRequest;
	}
	public void setSupervisorRequest(boolean supervisorRequest) {
		this.supervisorRequest = supervisorRequest;
	}

}
