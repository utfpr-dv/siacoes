package br.edu.utfpr.dv.siacoes.sign;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.JuryRequest;
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
	
	public static JuryRequest build(JuryFormReport jury) {
		JuryRequest dataset = new JuryRequest();
		
		dataset.setStage(jury.getStage());
		dataset.setTitle(jury.getTitle());
		dataset.setDate(jury.getDate());
		dataset.setLocal(jury.getLocal());
		dataset.setIdStudent(jury.getIdStudent());
		dataset.setIdSupervisor(jury.getIdSupervisor());
		dataset.setComments(jury.getComments());
		
		boolean findSupervisor = false;
		
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			dataset.addAppraiser(appraiser.getIdUser(), appraiser.getDescription());
			dataset.addSignature(appraiser.getIdUser(), appraiser.getName());
			
			if(appraiser.getIdUser() == jury.getIdSupervisor()) {
				findSupervisor = true;
			}
		}
		
		dataset.addSignature(jury.getIdStudent(), jury.getStudent());
		
		if(!findSupervisor) {
			dataset.addSignature(jury.getIdSupervisor(), jury.getSupervisor());
		}
		
		return dataset;
	}
	
	public static List<User> getSignaturesList(JuryFormReport jury) {
		List<User> users = new ArrayList<User>();
		boolean findSupervisor = false;
		
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			User u = new User();
			
			u.setIdUser(appraiser.getIdUser());
			u.setName(appraiser.getName());
			
			users.add(u);
			
			if(appraiser.getIdUser() == jury.getIdSupervisor()) {
				findSupervisor = true;
			}
		}
		
		if(!findSupervisor) {
			User supervisor = new User();
			
			supervisor.setIdUser(jury.getIdSupervisor());
			supervisor.setName(jury.getSupervisor());
			
			users.add(supervisor);
		}
		
		User student = new User();
		
		student.setIdUser(jury.getIdStudent());
		student.setName(jury.getStudent());
		
		users.add(student);
		
		return users;
	}

}
