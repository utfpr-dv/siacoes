package br.edu.utfpr.dv.siacoes.sign;

import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorAgreement;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SignDatasetBuilder {
	
	public static SupervisorAgreement build(Proposal proposal) {
		SupervisorAgreement dataset = new SupervisorAgreement();
		
		dataset.setDate(DateUtils.getNow().getTime());
		dataset.setTitle(proposal.getTitle());
		dataset.setStudent(proposal.getStudent().getName());
		dataset.setSupervisor(proposal.getSupervisor().getName());
		dataset.setFeedback(proposal.getSupervisorFeedback());
		dataset.setComments(proposal.getSupervisorComments());
		dataset.addSignature(proposal.getSupervisor().getIdUser(), proposal.getSupervisor().getName());
		
		return dataset;
	}
	
	public static ProposalFeedback build(ProposalAppraiser appraiser) {
		ProposalFeedback dataset = new ProposalFeedback();
		
		dataset.setDate(DateUtils.getNow().getTime());
		dataset.setTitle(appraiser.getProposal().getTitle());
		dataset.setStudent(appraiser.getProposal().getStudent().getName());
		dataset.setSupervisor(appraiser.getProposal().getSupervisor().getName());
		dataset.setAppraiser(appraiser.getAppraiser().getName());
		dataset.setInstitution(appraiser.getAppraiser().getInstitution());
		dataset.setArea(appraiser.getAppraiser().getArea());
		dataset.setFeedback(appraiser.getFeedback());
		dataset.setComments(appraiser.getComments());
		dataset.addSignature(appraiser.getAppraiser().getIdUser(), appraiser.getAppraiser().getName());
		
		return dataset;
	}

}
