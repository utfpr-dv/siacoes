package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditFinalDocumentWindow extends EditWindow {

	private final FinalDocument thesis;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final FileUploader uploadFile;
	private final CheckBox checkPrivate;
	private final CheckBox checkCompanyInfo;
	private final CheckBox checkPatent;
	private final NativeSelect comboFeedback;
	private final TextArea textComments;
	private final DateField textFeedbackDate;
	private final TextArea textNativeAbstract;
	private final TextArea textEnglishAbstract;
	private final Button buttonDownloadFile;
	private final TabSheet tab;
	
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
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.setHeight("385px");
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("800px");
		this.textTitle.setMaxLength(255);
		this.textTitle.setRequired(true);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		this.comboSemester.setRequired(true);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		this.textYear.setRequired(true);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setRequired(true);
		
		this.uploadFile = new FileUploader("(Formato PDF/A, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDFA);
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
		
		this.checkPrivate = new CheckBox("Documento em sigilo (somente estará disponível para consulta na biblioteca)");
		
		this.checkCompanyInfo = new CheckBox("Este trabalho possui informações de empresas");
		
		this.checkPatent = new CheckBox("Este trabalho é base para geração de patente");
		
		this.textComments = new TextArea();
		this.textComments.setWidth("800px");
		this.textComments.setHeight("350px");
		
		this.textFeedbackDate = new DateField("Data do Feedback");
		this.textFeedbackDate.setEnabled(false);
		this.textFeedbackDate.setDateFormat("dd/MM/yyyy");
		
		this.comboFeedback = new NativeSelect("Feedback do Orientador");
		this.comboFeedback.setWidth("300px");
		this.comboFeedback.addItem(DocumentFeedback.NONE);
		this.comboFeedback.addItem(DocumentFeedback.APPROVED);
		this.comboFeedback.addItem(DocumentFeedback.DISAPPROVED);
		this.comboFeedback.select(DocumentFeedback.NONE);
		this.comboFeedback.setNullSelectionAllowed(false);
		
		this.textNativeAbstract = new TextArea();
		this.textNativeAbstract.setWidth("800px");
		this.textNativeAbstract.setHeight("350px");
		
		this.textEnglishAbstract = new TextArea();
		this.textEnglishAbstract.setWidth("800px");
		this.textEnglishAbstract.setHeight("350px");
		
		this.buttonDownloadFile = new Button("Baixar Arquivo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		HorizontalLayout h2 = new HorizontalLayout(this.uploadFile, this.comboSemester, this.textYear, this.textSubmissionDate);
		h2.setSpacing(true);
		HorizontalLayout h3 = new HorizontalLayout(this.comboFeedback, this.textFeedbackDate);
		h3.setSpacing(true);
		VerticalLayout vl = new VerticalLayout(h1, this.textTitle, h2, this.checkPrivate, this.checkCompanyInfo, this.checkPatent, h3);
		vl.setSpacing(true);
		this.tab.addTab(vl, "Dados do Trabalho");
		
		this.tab.addTab(this.textNativeAbstract, "Resumo");
		
		this.tab.addTab(this.textEnglishAbstract, "Abstract");
		
		this.tab.addTab(this.textComments, "Comentários Adicionais");
		
		this.addField(this.tab);
		
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
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textTitle.setValue(this.thesis.getTitle());
		this.comboSemester.setSemester(this.thesis.getSemester());
		this.textYear.setYear(this.thesis.getYear());	
		this.textSubmissionDate.setValue(this.thesis.getSubmissionDate());
		this.checkPrivate.setValue(this.thesis.isPrivate());
		this.checkCompanyInfo.setValue(this.thesis.isCompanyInfo());
		this.checkPatent.setValue(this.thesis.isPatent());
		this.comboFeedback.setValue(this.thesis.getSupervisorFeedback());
		this.textFeedbackDate.setValue(this.thesis.getSupervisorFeedbackDate());
		this.textComments.setValue(this.thesis.getComments());
		this.textNativeAbstract.setValue(this.thesis.getNativeAbstract());
		this.textEnglishAbstract.setValue(this.thesis.getEnglishAbstract());
	}
	
	private void downloadFile() {
		try {
        	this.showReport(this.thesis.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Projeto/Monografia", e.getMessage());
		}
	}
	
}
