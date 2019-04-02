package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSupervisorIndicationWindow extends EditWindow {

	private final Proposal proposal;
	private final List<ProposalAppraiser> appraisers;
	private SigetConfig config;
	
	public EditSupervisorIndicationWindow(Proposal proposal, ListView parentView) {
		super("Indicar Avaliadores", parentView);
		
		if(proposal == null){
			this.proposal = new Proposal();
		}else{
			this.proposal = proposal;
		}
		
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			this.config = new SigetConfig();
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.appraisers = new ArrayList<ProposalAppraiser>();
		
		this.loadAppraisers();
	}
	
	private void loadAppraisers() {
		try {
			if(this.proposal.getAppraisers() == null) {
				this.proposal.setAppraisers(new ProposalAppraiserBO().listAppraisers(this.proposal.getIdProposal()));
			}
			
			for(ProposalAppraiser a : this.proposal.getAppraisers()) {
				if(a.isSupervisorIndication()) {
					this.appraisers.add(a);
				}
			}
			
			for(int i = 0; i < this.config.getSupervisorIndication(); i++) {
				SupervisorComboBox combo = new SupervisorComboBox("Avaliador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
				combo.setRequired(true);
				
				if(this.appraisers.size() > i) {
					combo.setProfessor(this.appraisers.get(i).getAppraiser());
					
					if(this.appraisers.get(i).getFeedback() != ProposalFeedback.NONE) {
						combo.setEnabled(false);
					}
				}
				
				this.addField(combo);
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Avaliadores", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		try {
			ProposalAppraiserBO bo = new ProposalAppraiserBO();
			
			for(int i = 0; i < this.config.getSupervisorIndication(); i++) {
				SupervisorComboBox combo = (SupervisorComboBox) this.getField(i);
				
				if(this.appraisers.size() > i) {
					this.appraisers.get(i).setAppraiser(combo.getProfessor());
				} else {
					ProposalAppraiser a = new ProposalAppraiser();
					
					a.setProposal(this.proposal);
					a.setAppraiser(combo.getProfessor());
					a.setSupervisorIndication(true);
					
					this.appraisers.add(a);
				}
			}
			
			for(ProposalAppraiser a : this.appraisers) {
				bo.save(Session.getIdUserLog(), a);
			}
			
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Avaliadores", e.getMessage());
		}
	}

}
