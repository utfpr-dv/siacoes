package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.FinalSubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
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
	private final FileUploader uploadFile;
	private final NativeSelect comboFeedback;
	private final DateField textFeedbackDate;
	private final TextField textValidatedAmount;
	private final TextArea textComments;
	private final TextField textFeedbackUser;
	private final TextField textDescription;
	private final TextArea textFeedbackReason;
	private final TabSheet tabContainer;
	private final VerticalLayout tab3;
	
	private SigacConfig config;
	
	public EditActivitySubmissionWindow(ActivitySubmission s, ListView parentView){
		super("Editar Submissão", parentView);
		
		if(s == null){
			this.submission = new ActivitySubmission();
			this.submission.setStudent(Session.getUser());
			this.submission.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.submission = s;
		}
		
		try {
			this.config = new SigacConfigBO().findByDepartment(this.submission.getDepartment().getIdDepartment());
		} catch (Exception e) {
			this.config = new SigacConfig();
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("400px");
		this.textStudent.setEnabled(false);
		this.textStudent.setRequired(true);
		
		this.textDescription = new TextField("Descrição da Atividade");
		this.textDescription.setWidth("400px");
		this.textDescription.setMaxLength(100);
		this.textDescription.setRequired(true);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setRequired(true);
		
		this.textYear = new YearField();
		this.textYear.setRequired(true);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setRequired(true);
		
		this.textAmount = new TextField("Quantidade");
		this.textAmount.setWidth("100px");
		this.textAmount.setVisible(false);
		this.textAmount.setRequired(true);
		
		this.comboGroup = new NativeSelect("Grupo");
		this.comboGroup.setWidth("810px");
		this.comboGroup.setNullSelectionAllowed(false);
		this.comboGroup.setRequired(true);
		this.loadGroups();
		this.comboGroup.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				loadActivities();
			}
		});
		
		this.comboActivity = new NativeSelect("Atividade");
		this.comboActivity.setWidth("810px");
		this.comboActivity.setNullSelectionAllowed(false);
		this.comboActivity.setRequired(true);
		this.comboActivity.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				showAmount();
			}
		});
		
		this.uploadFile = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					submission.setFile(uploadFile.getUploadedFile());
					
					loadCertificate();
				}
			}
		});
		
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
		this.textFeedbackDate.setWidth("150px");
		
		this.textValidatedAmount = new TextField("Quantidade Validada");
		this.textValidatedAmount.setWidth("150px");
		this.textValidatedAmount.setVisible(false);
		
		this.textFeedbackUser = new TextField("Validado por");
		this.textFeedbackUser.setEnabled(false);
		this.textFeedbackUser.setWidth("810px");
		
		this.textFeedbackReason = new TextArea("Observações do Parecerista");
		this.textFeedbackReason.setWidth("810px");
		this.textFeedbackReason.setHeight("150px");

		this.textComments = new TextArea();
		this.textComments.setWidth("810px");
		this.textComments.setHeight("300px");
		this.textComments.addStyleName("textscroll");
		
		if(Session.isUserDepartmentManager() && !Session.isUserManager(SystemModule.SIGAC)){
			this.setSaveButtonEnabled(false);
			this.uploadFile.setVisible(false);
			this.comboFeedback.setEnabled(false);
			this.textValidatedAmount.setEnabled(false);
			this.textAmount.setEnabled(false);
			this.comboSemester.setEnabled(false);
			this.textYear.setEnabled(false);
			this.textComments.setEnabled(false);
		}else if(Session.isUserManager(SystemModule.SIGAC)){
			this.uploadFile.setVisible(false);
			this.textAmount.setEnabled(false);
			this.comboSemester.setEnabled(false);
			this.textYear.setEnabled(false);
			this.textComments.setEnabled(false);
		}else{
			this.comboFeedback.setEnabled(false);
			this.textValidatedAmount.setEnabled(false);
			this.textFeedbackReason.setEnabled(false);
			
			if(this.submission.getFeedback() != ActivityFeedback.NONE){
				this.setSaveButtonEnabled(false);
				this.uploadFile.setVisible(false);
			}
		}
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h4 = new HorizontalLayout(this.textStudent, this.textDescription);
		h4.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboSemester, this.textYear, this.textAmount, this.textSubmissionDate, this.uploadFile);
		h2.setSpacing(true);
		
		VerticalLayout tab1 = new VerticalLayout(h1, h4, this.comboGroup, this.comboActivity, h2);
		tab1.setSpacing(true);
		
		this.tab3 = new VerticalLayout();
		this.tab3.setHeight("300px");
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboFeedback, this.textValidatedAmount, this.textFeedbackDate);
		h3.setSpacing(true);
		
		VerticalLayout v1 = new VerticalLayout(h3, this.textFeedbackUser, this.textFeedbackReason);
		v1.setSpacing(true);
		
		VerticalLayout tabFeedback = new VerticalLayout(v1);
		tabFeedback.setHeight("300px");
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("820px");
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		this.tabContainer.addTab(tab1, "Atividade");
		this.tabContainer.addTab(this.tab3, "Comprovante");
		this.tabContainer.addTab(this.textComments, "Observações");
		this.tabContainer.addTab(tabFeedback, "Parecer");
		
		this.addField(this.tabContainer);
		
		this.loadSubmission();
		this.loadCertificate();
		this.textDescription.focus();
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
			
			this.showErrorNotification("Carregar Grupos", e.getMessage());
		}
	}
	
	private void loadActivities(){
		try{
			ActivityBO bo = new ActivityBO();
			List<Activity> list = bo.listByGroup(Session.getSelectedDepartment().getDepartment().getIdDepartment(), ((ActivityGroup)this.comboGroup.getValue()).getIdActivityGroup());
			
			this.comboActivity.removeAllItems();
			this.comboActivity.addItems(list);
			
			if(list.size() > 0){
				this.comboActivity.setValue(list.get(0));
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Atividades", e.getMessage());
		}
	}
	
	private void showAmount() {
		try {
			Activity activity = (Activity)this.comboActivity.getValue();
			
			if((activity == null) || (activity.getIdActivity() == 0)) {
				this.textAmount.setVisible(false);
				this.textValidatedAmount.setVisible(false);
			} else {
				ActivityUnit unit = new ActivityUnitBO().findById(activity.getUnit().getIdActivityUnit());
				
				if((unit == null) || (unit.getIdActivityUnit() == 0)) {
					this.textAmount.setVisible(false);
					this.textValidatedAmount.setVisible(false);
				} else {
					this.textAmount.setVisible(unit.isFillAmount());
					this.textAmount.setCaption(unit.getAmountDescription());
					
					this.textValidatedAmount.setVisible(unit.isFillAmount());
					this.textValidatedAmount.setCaption(unit.getAmountDescription() + " Validado(a)");
				}
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Selecionar Atividade", e.getMessage());
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
		this.textDescription.setValue(this.submission.getDescription());
		this.comboSemester.setSemester(this.submission.getSemester());
		this.textYear.setYear(this.submission.getYear());
		this.textSubmissionDate.setValue(this.submission.getSubmissionDate());
		this.textAmount.setValue(String.format("%.2f", this.submission.getAmount()));
		this.comboFeedback.setValue(this.submission.getFeedback());
		this.textValidatedAmount.setValue(String.format("%.2f", this.submission.getValidatedAmount()));
		this.textFeedbackDate.setValue(this.submission.getFeedbackDate());
		this.textFeedbackUser.setValue(this.submission.getFeedbackUser().getName());
		this.textComments.setValue(this.submission.getComments());
		this.textFeedbackReason.setValue(this.submission.getFeedbackReason());
		
		if(Session.isUserManager(SystemModule.SIGAC) && (this.submission.getValidatedAmount() <= 0)) {
			this.textValidatedAmount.setValue(String.format("%.2f", this.submission.getAmount()));
		}
		
		boolean allowEdit = false;
		
		if(this.submission.getIdActivitySubmission() == 0) {
			this.tabContainer.getTab(2).setVisible(false);
		}
		
		try {
			allowEdit = !new FinalSubmissionBO().studentHasSubmission(this.submission.getStudent().getIdUser(), this.submission.getDepartment().getIdDepartment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.setSaveButtonEnabled(allowEdit);
	}
	
	private void loadCertificate(){
		this.tabContainer.getTab(3).setVisible(false);
		
		if(this.uploadFile.getUploadedFile() != null) {
			this.submission.setFile(this.uploadFile.getUploadedFile());
		}
		
		if(submission.getFile() != null){
			this.tab3.removeAllComponents();
			
			StreamSource s = new StreamResource.StreamSource() {
				@Override
				public InputStream getStream() {
					return new ByteArrayInputStream(submission.getFile());
				}
			};
			
			StreamResource r = new StreamResource(s, "comprovante.pdf");
			r.setMIMEType("application/pdf");
			r.setCacheTime(0);
			
			BrowserFrame e = new BrowserFrame(null, r);
			e.setSizeFull();
			
			this.tab3.addComponent(e);
			this.tabContainer.getTab(3).setVisible(true);
		}
	}

	@Override
	public void save() {
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			
			if(Session.isUserManager(SystemModule.SIGAC)){
				if((this.submission.getFeedback() == ActivityFeedback.NONE) && ((ActivityFeedback)this.comboFeedback.getValue() != ActivityFeedback.NONE)){
					this.submission.setFeedbackUser(Session.getUser());
				}
				
				this.submission.setFeedback((ActivityFeedback)this.comboFeedback.getValue());
				this.submission.setFeedbackDate(this.textFeedbackDate.getValue());
				this.submission.setValidatedAmount(Double.parseDouble(this.textValidatedAmount.getValue().replace(",", ".")));
				this.submission.setFeedbackReason(this.textFeedbackReason.getValue());
				
				if(this.submission.getFeedbackDate() == null){
					this.submission.setFeedbackDate(new Date());
				}
			}
			
			if(this.submission.getFeedback() == ActivityFeedback.NONE){
				this.submission.setSemester(this.comboSemester.getSemester());
				this.submission.setYear(this.textYear.getYear());
				this.submission.setAmount(Double.parseDouble(this.textAmount.getValue().replace(",", ".")));
			}
			
			this.submission.setActivity((Activity)this.comboActivity.getValue());
			this.submission.setDescription(this.textDescription.getValue());
			this.submission.getActivity().setGroup((ActivityGroup)this.comboGroup.getValue());
			this.submission.setComments(this.textComments.getValue());
			
			if(this.uploadFile.getUploadedFile() != null) {
				this.submission.setFile(this.uploadFile.getUploadedFile());
			}
			
			bo.save(Session.getIdUserLog(), this.submission);
			
			this.showSuccessNotification("Salvar Submissão", "Submissão salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Submissão", e.getMessage());
		}
	}

}
