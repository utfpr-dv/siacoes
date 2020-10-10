package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditSupervisorAgreementWindow extends EditWindow {

	private final Proposal proposal;
	
	private final TextField textStudent;
	private final TextField textTitle;
	private final Select<String> comboFeedback;
	private final DatePicker textFeedbackDate;
	private final TextArea textComments;
	
	private SigetConfig config;
	
	private static final String NONE = "Nenhum";
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
		
		this.comboFeedback = new Select<String>();
		this.comboFeedback.setLabel("Parecer");
		this.comboFeedback.setWidth("300px");
		this.comboFeedback.setItems(NONE, APPROVED, DISAPPROVED);
		
		this.textFeedbackDate = new DatePicker("Data do Parecer");
		this.textFeedbackDate.setEnabled(false);
		this.textFeedbackDate.setRequired(true);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("200px");
		
		try {
			this.config = new SigetConfigBO().findByDepartment(this.proposal.getDepartment().getIdDepartment());
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
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
		this.textFeedbackDate.setValue(DateUtils.convertToLocalDate(this.proposal.getSupervisorFeedbackDate()));
		this.textComments.setValue(this.proposal.getSupervisorComments());
		
		if(this.proposal.getSupervisorFeedback() == ProposalFeedback.APPROVED) {
			this.comboFeedback.setValue(APPROVED);
		} else if(this.proposal.getSupervisorFeedback() == ProposalFeedback.DISAPPROVED) {
			this.comboFeedback.setValue(DISAPPROVED);
		} else {
			this.comboFeedback.setValue(NONE);
			this.setSignButtonEnabled(false);
		}
		
		try {
			if(Document.hasSignature(DocumentType.SUPERVISORAGREEMENT, this.proposal.getIdProposal())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
	}
	
	@Override
	public void sign() {
		try {
			List<User> users = new ArrayList<User>();
			
			users.add(this.proposal.getSupervisor());
			
			SignatureWindow window = new SignatureWindow(DocumentType.SUPERVISORAGREEMENT, this.proposal.getIdProposal(), SignDatasetBuilder.build(this.proposal), users, this, null);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Assinar Parecer", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		try {
			ProposalBO bo = new ProposalBO();
			
			this.proposal.setSupervisorFeedbackDate(DateUtils.getNow().getTime());
			this.proposal.setSupervisorComments(this.textComments.getValue());
			
			if(this.comboFeedback.getValue().equals(APPROVED)) {
				this.proposal.setSupervisorFeedback(ProposalFeedback.APPROVED);
			} else if(this.comboFeedback.getValue().equals(DISAPPROVED)) {
				this.proposal.setSupervisorFeedback(ProposalFeedback.DISAPPROVED);
			} else {
				this.proposal.setSupervisorFeedback(ProposalFeedback.NONE);
			}
			
			bo.saveSupervisorFeedback(this.proposal);
			
			if(!this.config.isUseDigitalSignature()) {
				this.showReport(bo.getSupervisorFeedbackReport(this.proposal.getIdProposal()));
			}
			
			this.showSuccessNotification("Salvar Parecer", "Parecer salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if((this.proposal.getSupervisor().getIdUser() == Session.getUser().getIdUser()) && this.config.isUseDigitalSignature() && this.proposal.getSupervisorFeedback() != ProposalFeedback.NONE) {
				this.sign();
				this.setSignButtonEnabled(true);
			} else {
				this.close();
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Parecer", e.getMessage());
		}
	}

}
