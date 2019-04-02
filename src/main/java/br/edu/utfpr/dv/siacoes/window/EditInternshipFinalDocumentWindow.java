package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipFinalDocumentWindow extends EditWindow {
	
	private final InternshipFinalDocument doc;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final FileUploader uploadFile;
	private final CheckBox checkPrivate;
	private final NativeSelect comboFeedback;
	private final TextArea textComments;
	private final DateField textFeedbackDate;
	private final Button buttonDownloadFile;
	
	public EditInternshipFinalDocumentWindow(InternshipFinalDocument d, ListView parentView){
		super("Versão Final do Relatório de Estágio", parentView);
		
		if(d == null){
			this.doc = new InternshipFinalDocument();
		}else{
			this.doc = d;
		}
		
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
		
		this.uploadFile = new FileUploader("(Formato PDF, Tam. Máx. 5 MB)");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDFA);
		this.uploadFile.setMaxBytesLength(6 * 1024 * 1024);
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					doc.setFile(uploadFile.getUploadedFile());
				}
				
				buttonDownloadFile.setVisible(true);
			}
		});
		
		this.checkPrivate = new CheckBox("Documento em sigilo (não será visível nas pesquisas)");
		
		this.textComments = new TextArea("Comentários");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("100px");
		
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
		
		this.buttonDownloadFile = new Button("Baixar Arquivo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		this.addField(this.textTitle);
		this.addField(new HorizontalLayout(this.uploadFile, this.comboSemester, this.textYear, this.textSubmissionDate));
		this.addField(this.checkPrivate);
		this.addField(new HorizontalLayout(this.comboFeedback, this.textFeedbackDate));
		this.addField(this.textComments);
		
		this.loadInternship();
		
		if(Session.isUserProfessor()) {
			this.uploadFile.setVisible(false);
			this.addButton(this.buttonDownloadFile);
		} else {
			this.comboFeedback.setEnabled(false);
			this.textComments.setReadOnly(true);
			
			if(this.doc.getSupervisorFeedback() == DocumentFeedback.APPROVED) {
				this.uploadFile.setEnabled(false);
				this.setSaveButtonEnabled(false);
			}
		}
		
		this.textTitle.focus();
	}
	
	private void loadInternship(){
		try{
			InternshipBO ibo = new InternshipBO();
			Internship internship = ibo.findById(this.doc.getInternship().getIdInternship());
			
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(internship.getDepartment().getIdDepartment());
			
			InternshipJuryBO jbo = new InternshipJuryBO();
			InternshipJury jury = jbo.findByInternship(internship.getIdInternship());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(internship.getDepartment());
			
			Semester semester = new SemesterBO().findByDate(campus.getIdCampus(), jury.getDate());
			
			this.comboSemester.setSemester(semester.getSemester());
			this.textYear.setYear(semester.getYear());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textTitle.setValue(this.doc.getTitle());
		this.textSubmissionDate.setValue(this.doc.getSubmissionDate());
		this.checkPrivate.setValue(this.doc.isPrivate());
		this.comboFeedback.setValue(this.doc.getSupervisorFeedback());
		this.textFeedbackDate.setValue(this.doc.getSupervisorFeedbackDate());
		this.textComments.setValue(this.doc.getComments());
	}
	
	private void downloadFile() {
		try {
        	this.showReport(this.doc.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		try{
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
			
			if(Session.isUserStudent()){
				this.doc.setSubmissionDate(DateUtils.getToday().getTime());
				
				if(this.uploadFile.getUploadedFile() != null) {
					this.doc.setFile(this.uploadFile.getUploadedFile());
				}
			}
			
			this.doc.setTitle(this.textTitle.getValue());
			this.doc.setPrivate(this.checkPrivate.getValue());
			
			if(Session.isUserProfessor()){
				if((this.doc.getSupervisorFeedback() == DocumentFeedback.NONE) && (this.comboFeedback.getValue() != DocumentFeedback.NONE)){
					this.doc.setSupervisorFeedbackDate(DateUtils.getToday().getTime());
				}
				this.doc.setSupervisorFeedback((DocumentFeedback)this.comboFeedback.getValue());
				this.doc.setComments(this.textComments.getValue());
			}
			
			bo.save(Session.getIdUserLog(), this.doc);
			
			this.showSuccessNotification("Salvar Relatório de Estágio", "Relatório de Estágio salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Relatório de Estágio", e.getMessage());
		}
	}
	
}
