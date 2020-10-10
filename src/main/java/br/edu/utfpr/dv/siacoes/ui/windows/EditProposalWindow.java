package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.ProposalAppraiserDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

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
	private final DatePicker textSubmissionDate;
	private final FileUploader uploadFile;
	private final Grid<ProposalAppraiserDataSource> gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonViewAppraiser;
	private final Button buttonDeleteAppraiser;
	private final Button buttonDownloadProposal;
	private final Tabs tab;
	
	private SigetConfig config;
	
	public EditProposalWindow(Proposal p, ListView parentView, boolean submitProposal){
		super("Editar Proposta", parentView);
		
		this.submitProposal = submitProposal;
		
		if(!this.submitProposal){
			this.setTitle("Registrar Orientação");
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
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
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
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		
		this.textSubmissionDate = new DatePicker("Data de Registro");
		this.textSubmissionDate.setEnabled(false);
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
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textTitle, this.textSubarea);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboSupervisor, this.comboCosupervisor);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		
		HorizontalLayout h4 = new HorizontalLayout();
		if(this.submitProposal && Session.isUserStudent()){
			h4.add(this.uploadFile, this.comboSemester, this.textYear, this.textSubmissionDate);
		}else{
			this.uploadFile.setVisible(false);
			h4.add(this.comboSemester, this.textYear, this.textSubmissionDate);
		}
		h4.setSpacing(true);
		h4.setMargin(false);
		h4.setPadding(false);
		
		tab1.add(h1, this.textStudent, h2, h3, h4);
		
		this.gridAppraisers = new Grid<ProposalAppraiserDataSource>();
		this.gridAppraisers.setSelectionMode(SelectionMode.SINGLE);
		this.gridAppraisers.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridAppraisers.addColumn(ProposalAppraiserDataSource::getName).setHeader("Avaliador");
		this.gridAppraisers.addColumn(ProposalAppraiserDataSource::getIndication).setHeader("Indicação").setFlexGrow(0).setWidth("150px");
		this.gridAppraisers.addColumn(ProposalAppraiserDataSource::getFeedback).setHeader("Parecer").setFlexGrow(0).setWidth("150px");
		this.gridAppraisers.setWidth("800px");
		this.gridAppraisers.setHeight("300px");
		
		this.buttonAddAppraiser = new Button("Adicionar Avaliador", new Icon(VaadinIcon.PLUS), event -> {
            addAppraiser();
        });
		this.buttonAddAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		this.buttonViewAppraiser = new Button("Visualizar Observações", new Icon(VaadinIcon.SEARCH), event -> {
            editAppraiser();
        });
		this.buttonViewAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.buttonDeleteAppraiser = new Button("Remover Avaliador", new Icon(VaadinIcon.TRASH), event -> {
            deleteAppraiser();
        });
		this.buttonDeleteAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		
		HorizontalLayout layoutButtons = new HorizontalLayout(this.buttonAddAppraiser, this.buttonViewAppraiser, this.buttonDeleteAppraiser);
		layoutButtons.setSpacing(true);
		layoutButtons.setMargin(false);
		layoutButtons.setPadding(false);
		
		VerticalLayout tab2 = new VerticalLayout(this.gridAppraisers, layoutButtons);
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		
		Tab t1 = new Tab("Proposta");
		Tab t2 = new Tab("Avaliadores");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(t1, tab1);
		tabsToPages.put(t2, tab2);
		Div pages = new Div(tab1, tab2);
		
		this.tab = new Tabs(t1);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(t1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("820px");
		layout.setHeight("370px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		if(Session.isUserManager(SystemModule.SIGET)) {
			if(submitProposal) {
				this.tab.add(t2);
				
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
				Logger.log(Level.SEVERE, e.getMessage(), e);
				this.uploadFile.setEnabled(false);
				this.setSaveButtonEnabled(false);
				this.showErrorNotification("Submeter Proposta", "Não foi possível determinar a data limite para entrega das propostas.");
			}
		}
		
		this.buttonDownloadProposal = new Button("Download da Proposta", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFile();
        });
		
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textStudent.setValue(this.proposal.getStudent().getName());
		this.textTitle.setValue(this.proposal.getTitle());
		this.textSubarea.setValue(this.proposal.getSubarea());
		this.comboSemester.setSemester(this.proposal.getSemester());
		this.textYear.setYear(this.proposal.getYear());
		this.textSubmissionDate.setValue(DateUtils.convertToLocalDate(this.proposal.getSubmissionDate()));
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
		try {
			if(this.proposal.getAppraisers() == null){
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				this.proposal.setAppraisers(bo.listAppraisers(this.proposal.getIdProposal()));
			}
			
			this.gridAppraisers.setItems(ProposalAppraiserDataSource.load(this.proposal.getAppraisers()));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Proposta", e.getMessage());
		}
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
				Logger.log(Level.SEVERE, e.getMessage(), e);
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Proposta", e.getMessage());
		}
	}
	
	private int getAppraiserSelectedIndex(){
    	ProposalAppraiserDataSource pa = this.gridAppraisers.asSingleSelect().getValue();
    	
    	if(pa == null) {
    		return -1;
    	} else {
    		for(int i = 0; i < this.proposal.getAppraisers().size(); i++) {
    			if(pa.getIdUser() == this.proposal.getAppraisers().get(i).getAppraiser().getIdUser()) {
    				return i;
    			}
    		}
    		
    		return -1;
    	}
    }
	
	private void addAppraiser(){
		ProposalAppraiser p = new ProposalAppraiser();
    	p.setProposal(proposal);
    	
    	EditProposalAppraiserWindow window = new EditProposalAppraiserWindow(p, this);
    	window.open();
	}
	
	private void editAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Avaliador", "Selecione o avaliador para visualizar.");
		}else{
			EditProposalAppraiserWindow window = new EditProposalAppraiserWindow(this.proposal.getAppraisers().get(index), this);
			window.open();
		}
	}
	
	private void deleteAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Avaliador", "Selecione o avaliador para remover.");
		}else if(this.proposal.getAppraisers().get(index).getFeedback() != ProposalFeedback.NONE){
			this.showWarningNotification("Remover Avaliador", "O avaliador selecionado não pode ser removido pois o mesmo já informou seu parecer.");
		}else{
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Confirma a Exclusão?")
		    	.withMessage("Confirma a remoção do avaliador?")
		    	.withOkButton(() -> {
		    		this.proposal.getAppraisers().remove(index);
                	loadGridAppraisers();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
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
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
}
