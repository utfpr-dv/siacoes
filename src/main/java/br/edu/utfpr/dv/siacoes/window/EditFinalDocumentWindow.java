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
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditFinalDocumentWindow extends EditWindow {

	private final FinalDocument thesis;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final Upload uploadFile;
	private final Image imageFileUploaded;
	private final CheckBox checkPrivate;
	private final CheckBox checkCompanyInfo;
	private final CheckBox checkPatent;
	private final NativeSelect comboFeedback;
	private final TextArea textComments;
	private final DateField textFeedbackDate;
	private final Button buttonDownloadFile;
	
	public EditFinalDocumentWindow(FinalDocument thesis, ListView parentView){
		super("Versão Final do Projeto/Monografia", parentView);
		
		if(thesis == null){
			this.thesis = new FinalDocument();
		}else{
			this.thesis = thesis;
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
		
		this.checkPrivate = new CheckBox("Documento em sigilo (somente estará disponível para consulta na biblioteca)");
		
		this.checkCompanyInfo = new CheckBox("Este trabalho possui informações de empresas");
		
		this.checkPatent = new CheckBox("Este trabalho é base para geração de patente");
		
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
		this.addField(this.checkCompanyInfo);
		this.addField(this.checkPatent);
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
		this.comboSemester.setSemester(this.thesis.getThesis().getSemester());
		this.textYear.setYear(this.thesis.getThesis().getYear());
		this.textSubmissionDate.setValue(this.thesis.getSubmissionDate());
		this.checkPrivate.setValue(this.thesis.isPrivate());
		this.checkCompanyInfo.setValue(this.thesis.isCompanyInfo());
		this.checkPatent.setValue(this.thesis.isPatent());
		this.comboFeedback.setValue(this.thesis.getSupervisorFeedback());
		this.textFeedbackDate.setValue(this.thesis.getSupervisorFeedbackDate());
		this.textComments.setValue(this.thesis.getComments());
		
		this.prepareDownload();
	}
	
	private void prepareDownload(){
		try {
        	new ExtensionUtils().extendToDownload(this.thesis.getIdFinalDocument() + ".pdf", this.thesis.getFile(), this.buttonDownloadFile);
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
			FinalDocumentBO bo = new FinalDocumentBO();
			
			if(Session.isUserStudent()){
				this.thesis.setSubmissionDate(DateUtils.getToday().getTime());
			}
			
			this.thesis.setTitle(this.textTitle.getValue());
			this.thesis.setPrivate(this.checkPrivate.getValue());
			this.thesis.setCompanyInfo(this.checkCompanyInfo.getValue());
			this.thesis.setPatent(this.checkPatent.getValue());
			
			if(Session.isUserProfessor()){
				if((this.thesis.getSupervisorFeedback() == DocumentFeedback.NONE) && (this.comboFeedback.getValue() != DocumentFeedback.NONE)){
					this.thesis.setSupervisorFeedbackDate(DateUtils.getToday().getTime());
				}
				this.thesis.setSupervisorFeedback((DocumentFeedback)this.comboFeedback.getValue());
				this.thesis.setComments(this.textComments.getValue());
			}
			
			bo.save(this.thesis);
			
			Notification.show("Salvar Projeto/Monografia", "Projeto/Monografia salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Projeto/Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
	            
	            thesis.setFile(buffer);
	            
	            imageFileUploaded.setVisible(true);
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
