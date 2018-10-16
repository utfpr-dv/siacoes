package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSupervisorAgreementWindow extends EditWindow {

	private final Proposal proposal;
	
	private final TextField textStudent;
	private final TextField textTitle;
	private final NativeSelect comboFeedback;
	private final DateField textFeedbackDate;
	private final TextArea textComments;
	
	private static final String APPROVED = "Favorável";
	private static final String DISAPPROVED = "Desfavorável";
	
	public EditSupervisorAgreementWindow(Proposal proposal, ListView parentView) {
		super("Concordância de Orientação", parentView);
		
		if(proposal == null) {
			this.proposal = new Proposal();
		} else {
			this.proposal = proposal;
		}
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		this.textStudent.setRequired(true);
		
		this.textTitle = new TextField("Título da Proposta");
		this.textTitle.setWidth("800px");
		this.textTitle.setEnabled(false);
		this.textTitle.setRequired(true);
		
		this.comboFeedback = new NativeSelect("Parecer");
		this.comboFeedback.setNullSelectionAllowed(false);
		this.comboFeedback.setWidth("300px");
		this.comboFeedback.addItem(ProposalFeedback.NONE);
		this.comboFeedback.addItem(APPROVED);
		this.comboFeedback.addItem(DISAPPROVED);
		this.comboFeedback.setRequired(true);
		
		this.textFeedbackDate = new DateField("Data do Parecer");
		this.textFeedbackDate.setDateFormat("dd/MM/yyyy");
		this.textFeedbackDate.setEnabled(false);
		this.textFeedbackDate.setRequired(true);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("200px");
		this.textComments.addStyleName("textscroll");
		
		this.addField(this.textStudent);
		this.addField(this.textTitle);
		this.addField(new HorizontalLayout(this.comboFeedback, this.textFeedbackDate));
		this.addField(this.textComments);
		
		this.loadProposal();
	}
	
	private void loadProposal() {
		this.textStudent.setValue(this.proposal.getStudent().getName());
		this.textTitle.setValue(this.proposal.getTitle());
		this.textFeedbackDate.setValue(this.proposal.getSupervisorFeedbackDate());
		this.textComments.setValue(this.proposal.getSupervisorComments());
		
		if(this.proposal.getSupervisorFeedback() == ProposalFeedback.APPROVED) {
			this.comboFeedback.setValue(APPROVED);
		} else if(this.proposal.getSupervisorFeedback() == ProposalFeedback.DISAPPROVED) {
			this.comboFeedback.setValue(DISAPPROVED);
		} else {
			this.comboFeedback.setValue(ProposalFeedback.NONE);
		}
	}
	
	@Override
	public void save() {
		try{
			ProposalBO bo = new ProposalBO();
			
			this.proposal.setSupervisorFeedbackDate(DateUtils.getNow().getTime());
			this.proposal.setSupervisorComments(this.textComments.getValue());
			
			if(this.comboFeedback.getValue() == APPROVED) {
				this.proposal.setSupervisorFeedback(ProposalFeedback.APPROVED);
			} else if(this.comboFeedback.getValue() == DISAPPROVED) {
				this.proposal.setSupervisorFeedback(ProposalFeedback.DISAPPROVED);
			} else {
				this.proposal.setSupervisorFeedback(ProposalFeedback.NONE);
			}
			
			bo.saveSupervisorFeedback(this.proposal);
			
			this.showReport(bo.getSupervisorFeedbackReport(this.proposal.getIdProposal()));
			
			Notification.show("Salvar Parecer", "Parecer salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Parecer", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
