package br.edu.utfpr.dv.siacoes.sign;

import br.edu.utfpr.dv.siacoes.model.Proposal;
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

}
