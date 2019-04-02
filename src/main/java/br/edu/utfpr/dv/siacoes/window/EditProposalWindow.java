package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditProposalWindow extends EditWindow {

	private final Proposal proposal;
	private final boolean submitProposal;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final TextField textSubarea;
	private final TextField textStudent;
	private final SupervisorComboBox comboSupervisor;
	private final SupervisorComboBox comboCosupervisor;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final FileUploader uploadFile;
	private final HorizontalLayout layoutAppraisers;
	private Grid gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonViewAppraiser;
	private final Button buttonDeleteAppraiser;
	private final Button buttonDownloadProposal;
	private final TabSheet tab;
	
	private SigetConfig config;
	
	public EditProposalWindow(Proposal p, ListView parentView, boolean submitProposal){
		super("Editar Proposta", parentView);
		
		this.submitProposal = submitProposal;
		
		if(!this.submitProposal){
			this.setCaption("Registrar Orientação");
		}
		
		if(p == null){
			this.proposal = new Proposal();
			this.proposal.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.proposal = p;
		}
		
		try {
			this.config = new SigetConfigBO().findByDepartment(this.proposal.getDepartment().getIdDepartment());
		} catch (Exception e1) {
			this.config = new SigetConfig();
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("400px");
		this.textTitle.setMaxLength(255);
		this.textTitle.setRequired(true);
		
		this.textSubarea = new TextField("Subárea");
		this.textSubarea.setWidth("400px");
		this.textSubarea.setMaxLength(255);
		this.textSubarea.setRequired(true);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setEnabled(false);
		this.textStudent.setWidth("800px");
		this.textStudent.setRequired(true);
		
		this.comboSupervisor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigetConfigBO().getSupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboSupervisor.setRequired(true);
		
		this.comboCosupervisor = new SupervisorComboBox("Coorientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigetConfigBO().getCosupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboCosupervisor.setNullSelectionAllowed(true);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		this.comboSemester.setRequired(true);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		this.textYear.setRequired(true);
		
		this.textSubmissionDate = new DateField("Data de Registro");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setRequired(true);
		
		this.uploadFile = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					proposal.setFile(uploadFile.getUploadedFile());
					
					buttonDownloadProposal.setVisible(true);
				}
			}
		});
		
		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSpacing(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textTitle, this.textSubarea);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboSupervisor, this.comboCosupervisor);
		h3.setSpacing(true);
		
		HorizontalLayout h4;
		if(this.submitProposal && Session.isUserStudent()){
			h4 = new HorizontalLayout(this.uploadFile, this.comboSemester, this.textYear, this.textSubmissionDate);
		}else{
			this.uploadFile.setVisible(false);
			h4 = new HorizontalLayout(this.comboSemester, this.textYear, this.textSubmissionDate);
		}
		h4.setSpacing(true);
		
		tab1.addComponent(h1);
		tab1.addComponent(this.textStudent);
		tab1.addComponent(h2);
		tab1.addComponent(h3);
		tab1.addComponent(h4);
		
		this.layoutAppraisers = new HorizontalLayout();
		
		this.buttonAddAppraiser = new Button("Adicionar Avaliador", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addAppraiser();
            }
        });
		this.buttonAddAppraiser.setIcon(FontAwesome.PLUS);
		this.buttonAddAppraiser.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		this.buttonViewAppraiser = new Button("Visualizar Observações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editAppraiser();
            }
        });
		this.buttonViewAppraiser.setIcon(FontAwesome.SEARCH);
		this.buttonViewAppraiser.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		this.buttonDeleteAppraiser = new Button("Remover Avaliador", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteAppraiser();
            }
        });
		this.buttonDeleteAppraiser.setIcon(FontAwesome.TRASH_O);
		this.buttonDeleteAppraiser.addStyleName(ValoTheme.BUTTON_DANGER);
		
		HorizontalLayout layoutButtons = new HorizontalLayout(this.buttonAddAppraiser, this.buttonViewAppraiser, this.buttonDeleteAppraiser);
		layoutButtons.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.layoutAppraisers, layoutButtons);
		tab2.setSpacing(true);
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		this.tab.addTab(tab1, "Proposta");
		
		if(Session.isUserManager(SystemModule.SIGET)) {
			if(submitProposal) {
				this.tab.addTab(tab2, "Avaliadores");
				
				this.loadGridAppraisers();
			} else {
				this.setSaveButtonEnabled(false);
			}
		} else if(Session.isUserStudent()) {
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProposalDeadline())){
					this.uploadFile.setEnabled(false);
					this.setSaveButtonEnabled(false);
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.uploadFile.setEnabled(false);
				this.setSaveButtonEnabled(false);
				this.showErrorNotification("Submeter Proposta", "Não foi possível determinar a data limite para entrega das propostas.");
			}
		}
		
		this.addField(this.tab);
		
		this.buttonDownloadProposal = new Button("Download da Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownloadProposal.setIcon(FontAwesome.DOWNLOAD);
		this.addButton(this.buttonDownloadProposal);
		this.buttonDownloadProposal.setWidth("250px");
		
		this.loadProposal();
		this.textTitle.focus();
	}
	
	private void loadProposal(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.proposal.getDepartment().getIdDepartment());
			
			if(campus != null){
				this.comboCampus.setCampus(campus);
				
				this.comboDepartment.setIdCampus(campus.getIdCampus());
				
				this.comboDepartment.setDepartment(this.proposal.getDepartment());
			}else{
				this.comboCampus.setCampus(Session.getSelectedDepartment().getDepartment().getCampus());
				
				this.comboDepartment.setIdCampus(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus());
				
				this.comboDepartment.setDepartment(Session.getSelectedDepartment().getDepartment());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textStudent.setValue(this.proposal.getStudent().getName());
		this.textTitle.setValue(this.proposal.getTitle());
		this.textSubarea.setValue(this.proposal.getSubarea());
		this.comboSemester.setSemester(this.proposal.getSemester());
		this.textYear.setYear(this.proposal.getYear());
		this.textSubmissionDate.setValue(this.proposal.getSubmissionDate());
		this.comboSupervisor.setProfessor(this.proposal.getSupervisor());
		this.comboCosupervisor.setProfessor(this.proposal.getCosupervisor());
		
		if(this.proposal.getIdProposal() != 0){
			this.comboSupervisor.setEnabled(false);
			
			if((this.proposal.getCosupervisor() != null) && (this.proposal.getCosupervisor().getIdUser() != 0)){
				this.comboCosupervisor.setEnabled(false);
			}
		}
		
		this.buttonDownloadProposal.setVisible(this.uploadFile.isVisible() && (this.proposal.getFile() != null));
	}
	
	private void loadGridAppraisers(){
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Avaliador", String.class);
		this.gridAppraisers.addColumn("Indicação", String.class);
		this.gridAppraisers.addColumn("Parecer", String.class);
		this.gridAppraisers.setWidth("800px");
		this.gridAppraisers.setHeight("300px");
		this.gridAppraisers.getColumns().get(1).setWidth(150);
		this.gridAppraisers.getColumns().get(2).setWidth(150);
		
		try {
			if(this.proposal.getAppraisers() == null){
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				this.proposal.setAppraisers(bo.listAppraisers(this.proposal.getIdProposal()));
			}
			
	    	for(ProposalAppraiser p : this.proposal.getAppraisers()){
				this.gridAppraisers.addRow(p.getAppraiser().getName(), (p.isSupervisorIndication() ? "Orientador" : "Resp. TCC"), p.getFeedback().toString());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Proposta", e.getMessage());
		}
		
		this.layoutAppraisers.removeAllComponents();
		this.layoutAppraisers.addComponent(this.gridAppraisers);
	}
	
	@Override
	public void save() {
		if(Session.isUserStudent()) {
			if(this.uploadFile.getUploadedFile() != null) {
				this.proposal.setFile(this.uploadFile.getUploadedFile());
			}
			
			if(this.submitProposal && (this.proposal.getFile() == null)){
				if(this.proposal.getFile() == null){
					this.showErrorNotification("Submeter Proposta", "É necessário enviar o arquivo da proposta.");
					return;	
				}
			}
			
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProposalDeadline())){
					this.showErrorNotification("Submeter Proposta", "O prazo para a submissão de propostas já foi encerrado.");
					return;
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.showErrorNotification("Submeter Proposta", "Não foi possível determinar a data limite para entrega das propostas.");
				return;
			}
		}
		
		try{
			ProposalBO bo = new ProposalBO();
		
			if(Session.isUserStudent()){
				this.proposal.setSubmissionDate(DateUtils.getToday().getTime());
			}
			
			this.proposal.setTitle(this.textTitle.getValue());
			this.proposal.setSubarea(this.textSubarea.getValue());
			
			if(this.proposal.getIdProposal() == 0){
				this.proposal.setSupervisor(this.comboSupervisor.getProfessor());
				this.proposal.setCosupervisor(this.comboCosupervisor.getProfessor());
				this.proposal.setDepartment(this.comboDepartment.getDepartment());
			}
			
			if((this.proposal.getCosupervisor() == null) || (this.proposal.getCosupervisor().getIdUser() == 0)){
				this.proposal.setCosupervisor(this.comboCosupervisor.getProfessor());
			}
			
			bo.save(Session.getIdUserLog(), this.proposal);
			
			this.showSuccessNotification("Salvar Proposta", "Proposta salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Proposta", e.getMessage());
		}
	}
	
	private int getAppraiserSelectedIndex(){
    	Object itemId = this.gridAppraisers.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }
	
	private void addAppraiser(){
		ProposalAppraiser p = new ProposalAppraiser();
    	p.setProposal(proposal);
    	
    	UI.getCurrent().addWindow(new EditProposalAppraiserWindow(p, this));
	}
	
	private void editAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Avaliador", "Selecione o avaliador para visualizar.");
		}else{
			UI.getCurrent().addWindow(new EditProposalAppraiserWindow(this.proposal.getAppraisers().get(index), this));	
		}
	}
	
	private void deleteAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Avaliador", "Selecione o avaliador para remover.");
		}else if(this.proposal.getAppraisers().get(index).getFeedback() != ProposalFeedback.NONE){
			this.showWarningNotification("Remover Avaliador", "O avaliador selecionado não pode ser removido pois o mesmo já informou seu parecer.");
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do avaliador?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	proposal.getAppraisers().remove(index);
                    	loadGridAppraisers();
                    }
                }
            });
		}
	}
	
	public void setAppraiser(ProposalAppraiser appraiser){
		boolean add = true;
		
		for(int i = 0; i < this.proposal.getAppraisers().size(); i++){
			if((this.proposal.getAppraisers().get(i).getIdProposalAppraiser() == appraiser.getIdProposalAppraiser()) && (this.proposal.getAppraisers().get(i).getAppraiser().getIdUser() == appraiser.getAppraiser().getIdUser())){
				this.proposal.getAppraisers().set(i, appraiser);
				add = false;
				break;
			}
		}
		
		if(add){
			this.proposal.getAppraisers().add(appraiser);
		}
		
		this.loadGridAppraisers();
	}
	
	private void downloadFile() {
		try {
        	this.showReport(this.proposal.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
}
