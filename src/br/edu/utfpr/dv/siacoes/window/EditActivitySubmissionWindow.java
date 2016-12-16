package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditActivitySubmissionWindow extends EditWindow {
	
	private final ActivitySubmission submission;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textStudent;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final TextField textAmount;
	private final NativeSelect comboGroup;
	private final NativeSelect comboActivity;
	private final Upload uploadFile;
	private final NativeSelect comboFeedback;
	private final DateField textFeedbackDate;
	private final TextField textValidatedAmount;
	private final Button buttonDownload;
	
	public EditActivitySubmissionWindow(ActivitySubmission submission, ListView parentView){
		super("Editar Submissão", parentView);
		
		if(submission == null){
			this.submission = new ActivitySubmission();
			this.submission.setStudent(Session.getUser());
			this.submission.setDepartment(Session.getUser().getDepartment());
		}else{
			this.submission = submission;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setEnabled(false);
		
		this.textAmount = new TextField("Quantidade");
		this.textAmount.setWidth("100px");
		
		this.comboGroup = new NativeSelect("Grupo");
		this.comboGroup.setWidth("800px");
		this.comboGroup.setNullSelectionAllowed(false);
		this.loadGroups();
		this.comboGroup.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				loadActivities();
			}
		});
		
		this.comboActivity = new NativeSelect("Atividade");
		this.comboActivity.setWidth("800px");
		this.comboActivity.setNullSelectionAllowed(false);
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadFile = new Upload("Enviar Arquivo (Formato PDF, Tam. Máx. 200 KB)", listener);
		this.uploadFile.addSucceededListener(listener);
		this.uploadFile.setButtonCaption("Enviar");
		
		this.comboFeedback = new NativeSelect("Parecer");
		this.comboFeedback.setWidth("200px");
		this.comboFeedback.setNullSelectionAllowed(false);
		this.comboFeedback.addItem(ActivityFeedback.NONE);
		this.comboFeedback.addItem(ActivityFeedback.APPROVED);
		this.comboFeedback.addItem(ActivityFeedback.DISAPPROVED);
		this.comboFeedback.select(ActivityFeedback.NONE);
		
		this.textFeedbackDate = new DateField("Data do Parecer");
		this.textFeedbackDate.setDateFormat("dd/MM/yyyy");
		this.textFeedbackDate.setEnabled(false);
		
		this.textValidatedAmount = new TextField("Quantidade Validada");
		
		if(Session.isUserManager(SystemModule.SIGAC)){
			this.uploadFile.setEnabled(false);
			this.textAmount.setEnabled(false);
			this.comboSemester.setEnabled(false);
			this.textYear.setEnabled(false);
		}else{
			this.comboFeedback.setEnabled(false);
			this.textValidatedAmount.setEnabled(false);
		}
		
		this.buttonDownload = new Button("Comprovante");
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		this.addField(this.textStudent);
		this.addField(this.comboGroup);
		this.addField(this.comboActivity);
		this.addField(new HorizontalLayout(this.comboSemester, this.textYear, this.textAmount, this.textSubmissionDate));
		if(Session.isUserManager(SystemModule.SIGAC)){
			this.addButton(this.buttonDownload);
			this.prepareDownload();
		}else{
			this.addField(this.uploadFile);
		}
		this.addField(new HorizontalLayout(this.comboFeedback, this.textValidatedAmount, this.textFeedbackDate));
		
		this.loadSubmission();
		this.comboSemester.focus();
	}
	
	private void loadGroups(){
		try{
			ActivityGroupBO bo = new ActivityGroupBO();
			List<ActivityGroup> list = bo.listAll();
			
			this.comboGroup.removeAllItems();
			this.comboGroup.addItems(list);
			
			if(list.size() > 0){
				this.comboGroup.setValue(list.get(0));
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Carregar Grupos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void loadActivities(){
		try{
			ActivityBO bo = new ActivityBO();
			List<Activity> list = bo.listByGroup(Session.getUser().getDepartment().getIdDepartment(), ((ActivityGroup)this.comboGroup.getValue()).getIdActivityGroup());
			
			this.comboActivity.removeAllItems();
			this.comboActivity.addItems(list);
			
			if(list.size() > 0){
				this.comboActivity.setValue(list.get(0));
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Carregar Atividades", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void loadSubmission(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.submission.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.submission.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		if((this.submission.getActivity() == null) || (this.submission.getActivity().getIdActivity() == 0)){
			this.loadActivities();	
		}else{
			try {
				ActivityGroupBO bo = new ActivityGroupBO();
				ActivityGroup group = bo.findByActivity(this.submission.getActivity().getIdActivity());
				
				this.comboGroup.select(group);
				this.loadActivities();
				this.comboActivity.select(this.submission.getActivity());
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		this.textStudent.setValue(this.submission.getStudent().getName());
		this.comboSemester.setSemester(this.submission.getSemester());
		this.textYear.setYear(this.submission.getYear());
		this.textSubmissionDate.setValue(this.submission.getSubmissionDate());
		this.textAmount.setValue(String.valueOf(this.submission.getAmount()));
		this.comboFeedback.setValue(this.submission.getFeedback());
		this.textValidatedAmount.setValue(String.valueOf(this.submission.getValidatedAmount()));
		this.textFeedbackDate.setValue(this.submission.getFeedbackDate());
	}

	@Override
	public void save() {
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			
			if(Session.isUserManager(SystemModule.SIGAC)){
				this.submission.setFeedback((ActivityFeedback)this.comboFeedback.getValue());
				this.submission.setFeedbackDate(this.textFeedbackDate.getValue());
				this.submission.setValidatedAmount(Double.parseDouble(this.textValidatedAmount.getValue()));
				
				if(this.submission.getFeedbackDate() == null){
					this.submission.setFeedbackDate(new Date());
				}
			}
			
			if(this.submission.getFeedback() == ActivityFeedback.NONE){
				this.submission.setSemester(this.comboSemester.getSemester());
				this.submission.setYear(this.textYear.getYear());
				this.submission.setAmount(Double.parseDouble(this.textAmount.getValue()));
				this.submission.setActivity((Activity)this.comboActivity.getValue());
			}
			
			bo.save(this.submission);
			
			Notification.show("Salvar Submissão", "Submissão salva com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Submissão", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownload(){
		try {
        	new ExtensionUtils().extendToDownload(this.submission.getIdActivitySubmission() + this.submission.getFileType().getExtension(), this.submission.getFile(), this.buttonDownload);
    	} catch (Exception e) {
    		this.buttonDownload.addClickListener(new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            	
	            	Notification.show("Download do Comprovante", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	            }
	        });
		}
    }

	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}

				submission.setFileType(DocumentType.fromMimeType(mimeType));
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
	            
	            if(input.available() > (200 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 200 KB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            submission.setFile(buffer);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
