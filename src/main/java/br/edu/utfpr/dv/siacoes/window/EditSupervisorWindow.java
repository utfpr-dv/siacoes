package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange.ChangeFeedback;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSupervisorWindow extends EditWindow {

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
	
	public EditSupervisorWindow(SupervisorChange change, ListView parentView){
		super("Alterar Orientador", parentView);
		
		this.supervisorChange = change;
		
		this.buildWindow();
		
		this.loadChange();
	}
	
	public EditSupervisorWindow(Proposal proposal, ListView parentView){
		super("Alterar Orientador", parentView);
		
		this.buildWindow();
		
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
				
				this.buttonPrintStatement.setVisible(false);
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Alterar Orientador", "Não foi possível carregar os dados de orientação.", Notification.Type.ERROR_MESSAGE);
		}
		
		this.loadChange();
	}
	
	private void buildWindow(){
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("810px");
		this.textStudent.setEnabled(false);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("800px");
		this.textTitle.setEnabled(false);
		
		this.tabData = new TabSheet();
		
		this.textCurrentSupervisor = new TextField("Orientador Atual");
		this.textCurrentSupervisor.setWidth("390px");
		this.textCurrentSupervisor.setEnabled(false);
		
		this.textCurrentCosupervisor = new TextField("Co-orientador Atual");
		this.textCurrentCosupervisor.setWidth("390px");
		this.textCurrentCosupervisor.setEnabled(false);
		
		this.comboNewSupervisor = new SupervisorComboBox("Novo Orientador", Session.getUser().getDepartment().getIdDepartment(), new SigetConfigBO().getSupervisorFilter(Session.getUser().getDepartment().getIdDepartment()));
		
		this.comboNewCosupervisor = new SupervisorComboBox("Novo Co-orientador", Session.getUser().getDepartment().getIdDepartment(), new SigetConfigBO().getCosupervisorFilter(Session.getUser().getDepartment().getIdDepartment()));
		this.comboNewCosupervisor.setNullSelectionAllowed(true);
		
		this.textComments = new TextArea("Motivo/Observações");
		this.textComments.setWidth("800px");
		this.textComments.setMaxLength(255);
		
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
		
		this.buttonPrintStatement = new Button("Imprimir Requisição");
		this.buttonPrintStatement.setId("buttonPrintStatement");
		this.addButton(this.buttonPrintStatement);
		this.buttonPrintStatement.setWidth("200px");
	}
	
	private void loadChange() {
		if(this.supervisorChange.getIdSupervisorChange() != 0){
			this.prepareDownload();
		}else{
			this.buttonPrintStatement.setEnabled(false);
		}
		
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
			
			bo.save(this.supervisorChange);
			
			this.prepareDownload();
			this.buttonPrintStatement.setEnabled(true);
			
			Notification.show("Salvar Alteração", "Orientação alterada com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.comboNewSupervisor.setEnabled(false);
			this.comboNewCosupervisor.setEnabled(false);
			this.textComments.setEnabled(false);
			if(!Session.isUserManager(SystemModule.SIGET) || (this.supervisorChange.getApproved() != ChangeFeedback.NONE)){
				this.setSaveButtonEnabled(false);
			}
			
			this.parentViewRefreshGrid();
			
			if(Session.isUserManager(SystemModule.SIGET)){
				this.close();	
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Alteração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownload(){
		List<SupervisorChange> list = new ArrayList<SupervisorChange>();
		
		list.add(this.supervisorChange);
		
		new ReportUtils().prepareForPdfReport("SupervisorChangeStatement", "Requerimento", list, this.buttonPrintStatement);
	}

}
