package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;

public class DownloadProposalFeedbackWindow extends BasicWindow {

	private final Proposal proposal;
	
	private final Grid grid;
	private final Button buttonComments;
	
	private List<ProposalAppraiser> appraisers;
	
	public DownloadProposalFeedbackWindow(Proposal proposal) {
		super("Feedback dos Avaliadores");
		
		if(proposal == null) {
			this.proposal = new Proposal();
		} else {
			this.proposal = proposal;
		}
		
		this.grid = new Grid();
		this.grid.addColumn("Avaliador", String.class);
		this.grid.addColumn("Parecer", String.class);
		this.grid.setWidth("500px");
		this.grid.setHeight("200px");
		
		this.buttonComments = new Button("Observações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	viewComments();
            }
        });
		this.buttonComments.setWidth("150px");
		
		VerticalLayout vl = new VerticalLayout(this.grid, this.buttonComments);
		vl.setSpacing(true);
		vl.setMargin(true);
		
		this.setContent(vl);
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
        
        this.loadFeedback();
	}
	
	private void loadFeedback() {
		try {
			ProposalAppraiserBO abo = new ProposalAppraiserBO();
			this.appraisers = abo.listAppraisers(this.proposal.getIdProposal());
    		
    		for(ProposalAppraiser a : appraisers){
    			this.grid.addRow(a.getAppraiser().getName(), a.getFeedback().toString());
    		}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Feedback", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void viewComments() {
		Object value = this.grid.getSelectedRow();
		
		if(value != null){
			int id = this.appraisers.get((int)value - 1).getIdProposalAppraiser();
			
			try {
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				ProposalAppraiser appraiser = bo.findById(id);
				
				UI.getCurrent().addWindow(new CommentWindow("Observações", appraiser.getComments()));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Carregar Observações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		} else {
			Notification.show("Carregar Observações", "Selecione o avaliador para visualizar as observações.", Notification.Type.WARNING_MESSAGE);
		}
	}
	
}
