package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
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
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipFinalDocumentWindow extends EditWindow {
	
	private final InternshipFinalDocument doc;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final Upload uploadFile;
	private final Image imageFileUploaded;
	private final CheckBox checkPrivate;
	private final NativeSelect comboFeedback;
	private final TextArea textComments;
	private final DateField textFeedbackDate;
	private final Button buttonDownloadFile;
	
	public EditInternshipFinalDocumentWindow(InternshipFinalDocument doc, ListView parentView){
		super("Versão Final do Relatório de Estágio", parentView);
		
		if(doc == null){
			this.doc = new InternshipFinalDocument();
		}else{
			this.doc = doc;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("800px");
		this.textTitle.setMaxLength(255);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadFile = new Upload("(Formato PDF, Tam. Máx 5 MB)", listener);
		this.uploadFile.addSucceededListener(listener);
		this.uploadFile.setButtonCaption("Enviar Arquivo");
		this.uploadFile.setImmediate(true);
		
		this.imageFileUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageFileUploaded.setVisible(false);
		
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
		
		this.buttonDownloadFile = new Button("Baixar Arquivo");
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		this.addField(this.textTitle);
		this.addField(new HorizontalLayout(this.uploadFile, this.imageFileUploaded, this.comboSemester, this.textYear, this.textSubmissionDate));
		this.addField(this.checkPrivate);
		this.addField(new HorizontalLayout(this.comboFeedback, this.textFeedbackDate));
		this.addField(this.textComments);
		
		if(Session.isUserProfessor()){
			this.uploadFile.setVisible(false);
			this.imageFileUploaded.setVisible(false);
			this.addButton(this.buttonDownloadFile);
		}else{
			this.comboFeedback.setEnabled(false);
			this.textComments.setReadOnly(true);
		}
		
		this.loadThesis();
		this.textTitle.focus();
	}
	
	private void loadThesis(){
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
		
		this.prepareDownload();
	}
	
	private void prepareDownload(){
		try {
        	new ExtensionUtils().extendToDownload(this.doc.getIdInternshipFinalDocument() + ".pdf", this.doc.getFile(), this.buttonDownloadFile);
    	} catch (Exception e) {
    		this.buttonDownloadFile.addClickListener(new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            	
	            	Notification.show("Download do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	            }
	        });
		}
    }
	
	@Override
	public void save() {
		try{
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
			
			if(Session.isUserStudent()){
				this.doc.setSubmissionDate(DateUtils.getToday().getTime());
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
			
			bo.save(this.doc);
			
			Notification.show("Salvar Relatório de Estágio", "Relatório de estágio salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Relatório de Estágio", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageFileUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            doc.setFile(buffer);
	            
	            imageFileUploaded.setVisible(true);
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
