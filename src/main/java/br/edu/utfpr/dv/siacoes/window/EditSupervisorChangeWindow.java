package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange.ChangeFeedback;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSupervisorChangeWindow extends EditWindow {

	private SupervisorChange supervisorChange;
	
	private TextField textStudent;
	private TextField textTitle;
	private TextField textCurrentSupervisor;
	private TextField textCurrentCosupervisor;
	private SupervisorComboBox comboNewSupervisor;
	private SupervisorComboBox comboNewCosupervisor;
	private TextArea textComments;
	private NativeSelect comboApproved;
	private Label labelDateApproved;
	private TextArea textApprovalComments;
	private TabSheet tabData;
	private Button buttonPrintStatement;
	
	private SigetConfig config;
	
	public EditSupervisorChangeWindow(SupervisorChange change, ListView parentView){
		super("Alterar Orientador", parentView);
		
		this.supervisorChange = change;
		
		this.buildWindow();
		
		this.loadChange();
	}
	
	public EditSupervisorChangeWindow(Proposal proposal, ListView parentView, boolean supervisorRequest){
		super("Alterar Orientador", parentView);
		
		try {
			SupervisorChangeBO bo = new SupervisorChangeBO();
			
			this.supervisorChange = bo.findPendingChange(proposal.getIdProposal());
			
			if(this.supervisorChange == null){
				this.supervisorChange = new SupervisorChange();
				
				this.supervisorChange.setProposal(proposal);
				this.supervisorChange.setOldSupervisor(bo.findCurrentSupervisor(proposal.getIdProposal()));
				this.supervisorChange.setNewSupervisor(this.supervisorChange.getOldSupervisor());
				this.supervisorChange.setOldCosupervisor(bo.findCurrentCosupervisor(proposal.getIdProposal()));
				this.supervisorChange.setNewCosupervisor(this.supervisorChange.getOldCosupervisor());
				this.supervisorChange.setSupervisorRequest(supervisorRequest);
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Alterar Orientador", "Não foi possível carregar os dados de orientação.");
		}
		
		this.buildWindow();
		
		this.loadChange();
	}
	
	private void buildWindow(){
		try {
			this.config = new SigetConfigBO().findByDepartment(this.supervisorChange.getProposal().getDepartment().getIdDepartment());
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
		}
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("810px");
		this.textStudent.setEnabled(false);
		this.textStudent.setRequired(true);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("800px");
		this.textTitle.setEnabled(false);
		this.textTitle.setRequired(true);
		
		this.tabData = new TabSheet();
		this.tabData.setWidth("800px");
		this.tabData.setHeight("275px");
		this.tabData.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabData.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabData.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		this.textCurrentSupervisor = new TextField("Orientador Atual");
		this.textCurrentSupervisor.setWidth("390px");
		this.textCurrentSupervisor.setEnabled(false);
		this.textCurrentSupervisor.setRequired(true);
		
		this.textCurrentCosupervisor = new TextField("Coorientador Atual");
		this.textCurrentCosupervisor.setWidth("390px");
		this.textCurrentCosupervisor.setEnabled(false);
		
		this.comboNewSupervisor = new SupervisorComboBox("Novo Orientador", this.config.getDepartment().getIdDepartment(), this.config.getSupervisorFilter());
		
		this.comboNewCosupervisor = new SupervisorComboBox("Novo Coorientador", this.config.getDepartment().getIdDepartment(), this.config.getCosupervisorFilter());
		this.comboNewCosupervisor.setNullSelectionAllowed(true);
		
		this.textComments = new TextArea("Motivo/Observações");
		this.textComments.setWidth("800px");
		this.textComments.setMaxLength(255);
		this.textComments.setRequired(true);
		
		VerticalLayout v1 = new VerticalLayout();
		v1.setSpacing(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textCurrentSupervisor, this.comboNewSupervisor);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textCurrentCosupervisor, this.comboNewCosupervisor);
		h2.setSpacing(true);
		
		v1.addComponent(h1);
		v1.addComponent(h2);
		v1.addComponent(this.textComments);
		
		this.tabData.addTab(v1, "Substituição");
		
		this.comboApproved = new NativeSelect("Situação");
		this.comboApproved.addItem(ChangeFeedback.NONE);
		this.comboApproved.addItem(ChangeFeedback.APPROVED);
		this.comboApproved.addItem(ChangeFeedback.DISAPPROVED);
		this.comboApproved.setNullSelectionAllowed(false);
		this.comboApproved.setWidth("200px");
		
		this.labelDateApproved = new Label();
		
		this.textApprovalComments = new TextArea("Comentários");
		this.textApprovalComments.setWidth("810px");
		this.textApprovalComments.setMaxLength(255);
		
		VerticalLayout v2 = new VerticalLayout();
		v2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboApproved, this.labelDateApproved);
		h3.setSpacing(true);
		
		v2.addComponent(h3);
		v2.addComponent(this.textApprovalComments);
		
		this.tabData.addTab(v2, "Aprovação");
		
		this.addField(this.textStudent);
		this.addField(this.textTitle);
		this.addField(this.tabData);
		
		this.buttonPrintStatement = new Button("Imprimir Requisição", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try {
					printStatement();
				} catch (Exception e) {
					e.printStackTrace();
					
					showErrorNotification("Imprimir Requisição", e.getMessage());
				}
            }
        });
		this.buttonPrintStatement.setIcon(FontAwesome.PRINT);
		this.addButton(this.buttonPrintStatement);
		this.buttonPrintStatement.setWidth("200px");
		
		if(config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
		}
		
		if(this.supervisorChange.getIdSupervisorChange() == 0) {
			this.buttonPrintStatement.setVisible(false);
		}
	}
	
	private void loadChange() {
		this.textStudent.setValue(this.supervisorChange.getProposal().getStudent().getName());
		this.textTitle.setValue(this.supervisorChange.getProposal().getTitle());
		this.textCurrentSupervisor.setValue(this.supervisorChange.getOldSupervisor().getName());
		this.textCurrentCosupervisor.setValue((this.supervisorChange.getOldCosupervisor() == null) ? "" : this.supervisorChange.getOldCosupervisor().getName());
		this.comboNewSupervisor.setProfessor(this.supervisorChange.getNewSupervisor());
		this.comboNewCosupervisor.setProfessor(this.supervisorChange.getNewCosupervisor());
		this.textComments.setValue(this.supervisorChange.getComments());
		this.comboApproved.setValue(this.supervisorChange.getApproved());
		this.textApprovalComments.setValue(this.supervisorChange.getApprovalComments());
		this.labelDateApproved.setValue(DateUtils.format(this.supervisorChange.getApprovalDate(), "dd/MM/yyyy"));
		
		if(!Session.isUserManager(SystemModule.SIGET) || (this.supervisorChange.getApproved() != ChangeFeedback.NONE)){
			this.comboApproved.setEnabled(false);
			this.textApprovalComments.setEnabled(false);
		}
		
		if(this.supervisorChange.getIdSupervisorChange() != 0){
			this.comboNewSupervisor.setEnabled(false);
			this.comboNewCosupervisor.setEnabled(false);
			this.textComments.setEnabled(false);
			
			if(!Session.isUserManager(SystemModule.SIGET) || (this.supervisorChange.getApproved() != ChangeFeedback.NONE)){
				this.setSaveButtonEnabled(false);
			}
		}
		
		try {
			if(Document.hasSignature(DocumentType.SUPERVISORCHANGE, this.supervisorChange.getIdSupervisorChange())) {
				this.disableButtons();
			} else if(this.supervisorChange.isSupervisorRequest()) {
				this.setSignButtonEnabled(Session.getUser().getIdUser() == this.supervisorChange.getOldSupervisor().getIdUser());
			} else {
				this.setSignButtonEnabled(Session.getUser().getIdUser() == this.supervisorChange.getProposal().getStudent().getIdUser());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
	}
	
	@Override
	public void sign() {
		if(this.supervisorChange.getIdSupervisorChange() == 0) {
			this.showWarningNotification("Assinar Requisição", "É necessário salvar a requisição antes de assinar.");
		} else if(this.supervisorChange.getOldSupervisor().getIdUser() == this.supervisorChange.getNewSupervisor().getIdUser()) {
			this.showWarningNotification("Assinar Requisição", "Não é necessária a assinatura da requisição pois não houve troca de orientador.");
		} else {
			try {
				List<User> users = new ArrayList<User>();
				
				if(this.supervisorChange.isSupervisorRequest()) {
					users.add(this.supervisorChange.getOldSupervisor());
				} else {
					users.add(this.supervisorChange.getProposal().getStudent());
				}
				
				UI.getCurrent().addWindow(new SignatureWindow(DocumentType.SUPERVISORCHANGE, this.supervisorChange.getIdSupervisorChange(), SignDatasetBuilder.build(this.supervisorChange), users, this, null));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Requisição", e.getMessage());
			}
		}
	}
	
	@Override
	public void save() {
		try {
			SupervisorChangeBO bo = new SupervisorChangeBO();
			
			if(this.supervisorChange.getIdSupervisorChange() == 0){
				this.supervisorChange.setNewSupervisor(this.comboNewSupervisor.getProfessor());
				this.supervisorChange.setNewCosupervisor(this.comboNewCosupervisor.getProfessor());
				this.supervisorChange.setComments(this.textComments.getValue());
			}
			
			if(Session.isUserManager(SystemModule.SIGET) && (this.supervisorChange.getApproved() == ChangeFeedback.NONE)){
				this.supervisorChange.setApproved((ChangeFeedback)this.comboApproved.getValue());
				this.supervisorChange.setApprovalComments(this.textApprovalComments.getValue());
				this.supervisorChange.setApprovalDate(DateUtils.getNow().getTime());
			}
			
			bo.save(Session.getIdUserLog(), this.supervisorChange);
			
			this.comboNewSupervisor.setEnabled(false);
			this.comboNewCosupervisor.setEnabled(false);
			this.textComments.setEnabled(false);
			if(!Session.isUserManager(SystemModule.SIGET) || (this.supervisorChange.getApproved() != ChangeFeedback.NONE)){
				this.setSaveButtonEnabled(false);
			}
			
			if(this.supervisorChange.getOldSupervisor().getIdUser() != this.supervisorChange.getNewSupervisor().getIdUser()) {
				this.buttonPrintStatement.setVisible(true);
			}
			
			this.showSuccessNotification("Salvar Alteração", "Orientação alterada com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if(Session.isUserManager(SystemModule.SIGET) || (this.supervisorChange.getOldSupervisor().getIdUser() == this.supervisorChange.getNewSupervisor().getIdUser())) {
				this.close();
			} else if((this.supervisorChange.getOldSupervisor().getIdUser() != this.supervisorChange.getNewSupervisor().getIdUser()) && this.config.isUseDigitalSignature()) {
				this.sign();
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Alteração", e.getMessage());
		}
	}
	
	private void printStatement() throws Exception {
		if(this.supervisorChange.getIdSupervisorChange() == 0) {
			this.showWarningNotification("Imprimir Requisição", "É necessário salvar a requisição antes de imprimir.");
		} else {
			this.showReport(new SupervisorChangeBO().getSupervisorChangeReport(this.supervisorChange.getIdSupervisorChange()));	
		}
	}

}
