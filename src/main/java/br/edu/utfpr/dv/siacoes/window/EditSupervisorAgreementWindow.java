package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSupervisorAgreementWindow extends EditWindow {

	private final Proposal proposal;
	
	private final TextField textStudent;
	private final TextField textTitle;
	private final NativeSelect comboFeedback;
	private final DateField textFeedbackDate;
	private final TextArea textComments;
	
	private SigetConfig config;
	
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
		
		try {
			this.config = new SigetConfigBO().findByDepartment(this.proposal.getDepartment().getIdDepartment());
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
		}
		
		this.addField(this.textStudent);
		this.addField(this.textTitle);
		this.addField(new HorizontalLayout(this.comboFeedback, this.textFeedbackDate));
		this.addField(this.textComments);
		
		if(config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
		}
		
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
		
		try {
			if(Document.hasSignature(DocumentType.SUPERVISORAGREEMENT, this.proposal.getIdProposal())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
	}
	
	@Override
	public void sign() {
		try {
			List<User> users = new ArrayList<User>();
			
			users.add(this.proposal.getSupervisor());
			
			UI.getCurrent().addWindow(new SignatureWindow(DocumentType.SUPERVISORAGREEMENT, this.proposal.getIdProposal(), SignDatasetBuilder.build(this.proposal), users, this, null));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Assinar Parecer", e.getMessage());
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
			
			this.showSuccessNotification("Salvar Parecer", "Parecer salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if(this.config.isUseDigitalSignature()) {
				this.sign();
			} else {
				this.close();	
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Parecer", e.getMessage());
		}
	}

}
