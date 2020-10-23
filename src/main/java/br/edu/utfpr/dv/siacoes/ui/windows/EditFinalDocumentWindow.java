package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader.AcceptedDocumentType;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditFinalDocumentWindow extends EditWindow {

	private final FinalDocument thesis;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DatePicker textSubmissionDate;
	private final FileUploader uploadFile;
	private final Checkbox checkPrivate;
	private final Checkbox checkCompanyInfo;
	private final Checkbox checkPatent;
	private final Select<DocumentFeedback> comboFeedback;
	private final TextArea textComments;
	private final DatePicker textFeedbackDate;
	private final TextArea textNativeAbstract;
	private final TextArea textEnglishAbstract;
	private final Button buttonDownloadFile;
	private final Tabs tab;
	
	private SigetConfig config;
	
	public EditFinalDocumentWindow(FinalDocument doc, ListView parentView){
		super("Versão Final do Projeto/Monografia", parentView);
		
		if(doc == null){
			this.thesis = new FinalDocument();
		}else{
			this.thesis = doc;
		}
		
		try {
			if((this.thesis.getThesis() != null) && (this.thesis.getThesis().getIdThesis() != 0)) {
				this.config = new SigetConfigBO().findByDepartment(new ThesisBO().findIdDepartment(this.thesis.getThesis().getIdThesis()));
			} else {
				this.config = new SigetConfigBO().findByDepartment(new ProjectBO().findIdDepartment(this.thesis.getProject().getIdProject()));
			}
		} catch (Exception e1) {
			this.config = new SigetConfig();
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("800px");
		this.textTitle.setMaxLength(255);
		this.textTitle.setRequired(true);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		
		this.textSubmissionDate = new DatePicker("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setRequired(true);
		
		this.uploadFile = new FileUploader("(Formato PDF/A, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.setAcceptedType(AcceptedDocumentType.PDFA);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					thesis.setFile(uploadFile.getUploadedFile());
				}
				
				buttonDownloadFile.setVisible(true);
			}
		});
		
		this.checkPrivate = new Checkbox("Documento em sigilo (somente estará disponível para consulta na biblioteca)");
		
		this.checkCompanyInfo = new Checkbox("Este trabalho possui informações de empresas");
		
		this.checkPatent = new Checkbox("Este trabalho é base para geração de patente");
		
		this.textComments = new TextArea();
		this.textComments.setWidth("800px");
		this.textComments.setHeight("430px");
		this.textComments.setVisible(false);
		
		this.textFeedbackDate = new DatePicker("Data do Feedback");
		this.textFeedbackDate.setEnabled(false);
		
		this.comboFeedback = new Select<DocumentFeedback>();
		this.comboFeedback.setLabel("Feedback do Orientador");
		this.comboFeedback.setWidth("300px");
		this.comboFeedback.setItems(DocumentFeedback.NONE, DocumentFeedback.APPROVED, DocumentFeedback.DISAPPROVED);
		this.comboFeedback.setValue(DocumentFeedback.NONE);
		
		this.textNativeAbstract = new TextArea();
		this.textNativeAbstract.setWidth("800px");
		this.textNativeAbstract.setHeight("430px");
		this.textNativeAbstract.setVisible(false);
		
		this.textEnglishAbstract = new TextArea();
		this.textEnglishAbstract.setWidth("800px");
		this.textEnglishAbstract.setHeight("430px");
		this.textEnglishAbstract.setVisible(false);
		
		this.buttonDownloadFile = new Button("Baixar Arquivo", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFile();
        });
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		HorizontalLayout h2 = new HorizontalLayout(this.uploadFile, this.comboSemester, this.textYear, this.textSubmissionDate);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		HorizontalLayout h3 = new HorizontalLayout(this.comboFeedback, this.textFeedbackDate);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		VerticalLayout vl = new VerticalLayout(h1, this.textTitle, h2, this.checkPrivate, this.checkCompanyInfo, this.checkPatent, h3);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		Tab tab1 = new Tab("Dados do Trabalho");
		Tab tab2 = new Tab("Resumo");
		Tab tab3 = new Tab("Abstract");
		Tab tab4 = new Tab("Comentários Adicionais");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, vl);
		tabsToPages.put(tab2, this.textNativeAbstract);
		tabsToPages.put(tab3, this.textEnglishAbstract);
		tabsToPages.put(tab4, this.textComments);
		Div pages = new Div(vl, this.textNativeAbstract, this.textEnglishAbstract, this.textComments);
		
		this.tab = new Tabs(tab1, tab2, tab3, tab4);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tab1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("820px");
		layout.setHeight("540px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		if(Session.isUserProfessor()){
			this.uploadFile.setVisible(false);
			this.addButton(this.buttonDownloadFile);
		}else{
			this.comboFeedback.setEnabled(false);
			this.textComments.setReadOnly(true);
		}
		
		if(Session.isUserStudent()){
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if((this.thesis.getThesis() != null) && (this.thesis.getThesis().getIdThesis() != 0)) {
					if(DateUtils.getToday().getTime().after(d.getThesisFinalDocumentDeadline())){
						this.uploadFile.setEnabled(false);
						this.setSaveButtonEnabled(false);
					}	
				} else {
					if(DateUtils.getToday().getTime().after(d.getProjectFinalDocumentDeadline())){
						this.uploadFile.setEnabled(false);
						this.setSaveButtonEnabled(false);
					}
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				this.uploadFile.setEnabled(false);
				this.setSaveButtonEnabled(false);
				this.showErrorNotification("Submeter Versão Final", "Não foi possível determinar a data limite para entrega da versão final do documento.");
			}
		}
		
		this.loadThesis();
		this.textTitle.focus();
	}
	
	private void loadThesis(){
		try{
			ProposalBO pbo = new ProposalBO();
			Proposal proposal;
			
			if((this.thesis.getThesis() != null) && (this.thesis.getThesis().getIdThesis() != 0)){
				proposal = pbo.findByProject(this.thesis.getThesis().getProject().getIdProject());
			}else{
				proposal = pbo.findByProject(this.thesis.getProject().getIdProject());
			}
			
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(proposal.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(proposal.getDepartment());
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textTitle.setValue(this.thesis.getTitle());
		this.comboSemester.setSemester(this.thesis.getSemester());
		this.textYear.setYear(this.thesis.getYear());	
		this.textSubmissionDate.setValue(DateUtils.convertToLocalDate(this.thesis.getSubmissionDate()));
		this.checkPrivate.setValue(this.thesis.isPrivate());
		this.checkCompanyInfo.setValue(this.thesis.isCompanyInfo());
		this.checkPatent.setValue(this.thesis.isPatent());
		this.comboFeedback.setValue(this.thesis.getSupervisorFeedback());
		this.textFeedbackDate.setValue(DateUtils.convertToLocalDate(this.thesis.getSupervisorFeedbackDate()));
		this.textComments.setValue(this.thesis.getComments());
		this.textNativeAbstract.setValue(this.thesis.getNativeAbstract());
		this.textEnglishAbstract.setValue(this.thesis.getEnglishAbstract());
	}
	
	private void downloadFile() {
		try {
        	this.showReport(this.thesis.getFile());
    	} catch (Exception e) {
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		try{
			FinalDocumentBO bo = new FinalDocumentBO();
			
			if(Session.isUserStudent()){
				this.thesis.setSubmissionDate(DateUtils.getToday().getTime());
				
				if(this.uploadFile.getUploadedFile() != null) {
					this.thesis.setFile(this.uploadFile.getUploadedFile());
				}
			}
			
			this.thesis.setTitle(this.textTitle.getValue());
			this.thesis.setPrivate(this.checkPrivate.getValue());
			this.thesis.setCompanyInfo(this.checkCompanyInfo.getValue());
			this.thesis.setPatent(this.checkPatent.getValue());
			this.thesis.setNativeAbstract(this.textNativeAbstract.getValue());
			this.thesis.setEnglishAbstract(this.textEnglishAbstract.getValue());
			
			if(Session.isUserProfessor()){
				if((this.thesis.getSupervisorFeedback() == DocumentFeedback.NONE) && (this.comboFeedback.getValue() != DocumentFeedback.NONE)){
					this.thesis.setSupervisorFeedbackDate(DateUtils.getToday().getTime());
				}
				this.thesis.setSupervisorFeedback((DocumentFeedback)this.comboFeedback.getValue());
				this.thesis.setComments(this.textComments.getValue());
			}
			
			bo.save(Session.getIdUserLog(), this.thesis);
			
			this.showSuccessNotification("Salvar Projeto/Monografia", "Projeto/Monografia salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Projeto/Monografia", e.getMessage());
		}
	}
	
}
