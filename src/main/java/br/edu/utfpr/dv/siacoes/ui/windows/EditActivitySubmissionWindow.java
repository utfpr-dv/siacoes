package br.edu.utfpr.dv.siacoes.ui.windows;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.FinalSubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader.AcceptedDocumentType;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditActivitySubmissionWindow extends EditWindow {
	
	private final ActivitySubmission submission;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textStudent;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DatePicker textSubmissionDate;
	private final TextField textAmount;
	private final Select<ActivityGroup> comboGroup;
	private final Select<Activity> comboActivity;
	private final FileUploader uploadFile;
	private final Select<ActivityFeedback> comboFeedback;
	private final DatePicker textFeedbackDate;
	private final TextField textValidatedAmount;
	private final TextArea textComments;
	private final TextField textFeedbackUser;
	private final TextField textDescription;
	private final TextField textRegisterSemester;
	private final TextArea textFeedbackReason;
	private final Tabs tabContainer;
	private final VerticalLayout layoutTab3;
	private final Tab tab2;
	private final Tab tab4;
	
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
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("400px");
		this.textStudent.setEnabled(false);
		this.textStudent.setRequired(true);
		
		this.textRegisterSemester = new TextField("Semestre de Ingresso");
		this.textRegisterSemester.setWidth("150px");
		this.textRegisterSemester.setEnabled(false);
		
		this.textDescription = new TextField("Descrição da Atividade");
		this.textDescription.setWidth("400px");
		this.textDescription.setMaxLength(100);
		this.textDescription.setRequired(true);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.textSubmissionDate = new DatePicker("Data de Submissão");
		//this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setRequired(true);
		
		this.textAmount = new TextField("Quantidade");
		this.textAmount.setWidth("100px");
		this.textAmount.setVisible(false);
		this.textAmount.setRequired(true);
		
		this.comboGroup = new Select<ActivityGroup>();
		this.comboGroup.setLabel("Grupo");
		this.comboGroup.setWidth("810px");
		this.loadGroups();
		this.comboGroup.addValueChangeListener(event -> {
			loadActivities();
		});
		
		this.comboActivity = new Select<Activity>();
		this.comboActivity.setLabel("Atividade");
		this.comboActivity.setWidth("810px");
		this.comboActivity.addValueChangeListener(event -> {			
			showAmount();
		});
		
		this.uploadFile = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.setAcceptedType(AcceptedDocumentType.PDF);
		if(this.config.getMaxFileSize() > 0) {
			this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		} else {
			this.uploadFile.setMaxBytesLength(250 * 1024);
		}
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					submission.setFile(uploadFile.getUploadedFile());
					
					loadCertificate();
				}
			}
		});
		
		this.comboFeedback = new Select<ActivityFeedback>();
		this.comboFeedback.setLabel("Parecer");
		this.comboFeedback.setWidth("200px");
		this.comboFeedback.setItems(ActivityFeedback.NONE, ActivityFeedback.APPROVED, ActivityFeedback.DISAPPROVED);
		this.comboFeedback.setValue(ActivityFeedback.NONE);
		
		this.textFeedbackDate = new DatePicker("Data do Parecer");
		//this.textFeedbackDate.setDateFormat("dd/MM/yyyy");
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
		this.textComments.setHeight("400px");
		this.textComments.setWidth("800px");
		this.textComments.setVisible(false);
		
		if(Session.isUserDepartmentManager() && !Session.isUserManager(SystemModule.SIGAC)){
			this.setSaveButtonEnabled(false);
			this.uploadFile.setVisible(false);
			this.comboFeedback.setEnabled(false);
			this.textValidatedAmount.setEnabled(false);
			this.textAmount.setEnabled(false);
			this.comboSemester.setEnabled(false);
			this.textYear.setEnabled(false);
			this.textComments.setEnabled(false);
			this.textDescription.setEnabled(false);
		}else if(Session.isUserManager(SystemModule.SIGAC)){
			this.uploadFile.setVisible(false);
			this.textAmount.setEnabled(false);
			//this.comboSemester.setEnabled(false);
			//this.textYear.setEnabled(false);
			this.textComments.setEnabled(false);
			this.textDescription.setEnabled(false);
		}else{
			this.comboFeedback.setEnabled(false);
			this.textValidatedAmount.setEnabled(false);
			this.textFeedbackReason.setEnabled(false);
			this.textRegisterSemester.setVisible(false);
			
			if(this.submission.getFeedback() != ActivityFeedback.NONE){
				this.setSaveButtonEnabled(false);
				this.uploadFile.setVisible(false);
			}
		}
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h4 = new HorizontalLayout(this.textStudent, this.textDescription);
		h4.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboSemester, this.textYear, this.textAmount, this.textSubmissionDate, this.uploadFile, this.textRegisterSemester);
		h2.setSpacing(true);
		
		VerticalLayout layoutTab1 = new VerticalLayout(h1, h4, this.comboGroup, this.comboActivity, h2);
		layoutTab1.setSpacing(false);
		layoutTab1.setMargin(false);
		layoutTab1.setPadding(false);
		
		this.layoutTab3 = new VerticalLayout();
		this.layoutTab3.setHeight("400px");
		this.layoutTab3.setWidthFull();
		this.layoutTab3.setVisible(false);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboFeedback, this.textValidatedAmount, this.textFeedbackDate);
		h3.setSpacing(true);
		
		VerticalLayout tabFeedback = new VerticalLayout(h3, this.textFeedbackUser, this.textFeedbackReason);
		tabFeedback.setHeight("300px");
		tabFeedback.setSpacing(false);
		tabFeedback.setMargin(false);
		tabFeedback.setPadding(false);
		tabFeedback.setVisible(false);
		
		Tab tab1 = new Tab("Atividade");
		this.tab2 = new Tab("Comprovante");
		Tab tab3 = new Tab("Observações");
		this.tab4 = new Tab("Parecer");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, layoutTab1);
		tabsToPages.put(this.tab2, this.layoutTab3);
		tabsToPages.put(tab3, this.textComments);
		tabsToPages.put(this.tab4, tabFeedback);
		Div pages = new Div(layoutTab1, this.layoutTab3, this.textComments, tabFeedback);
		
		this.tabContainer = new Tabs(tab1, this.tab2, tab3, this.tab4);
		this.tabContainer.setWidthFull();
		this.tabContainer.setFlexGrowForEnclosedTabs(1);
		
		this.tabContainer.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tabContainer.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tabContainer.setSelectedTab(tab1);
		
		VerticalLayout layout = new VerticalLayout(this.tabContainer, pages);
		layout.setWidth("820px");
		layout.setHeight("450px");
		
		this.addField(layout);
		
		this.loadSubmission();
		this.loadCertificate();
		this.textDescription.focus();
	}
	
	private void loadGroups(){
		try{
			ActivityGroupBO bo = new ActivityGroupBO();
			List<ActivityGroup> list = bo.listAll();
			
			this.comboGroup.setItems(list);
			
			if(list.size() > 0){
				this.comboGroup.setValue(list.get(0));
			}
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Grupos", e.getMessage());
		}
	}
	
	private void loadActivities(){
		try{
			ActivityBO bo = new ActivityBO();
			List<Activity> list = bo.listByGroup(Session.getSelectedDepartment().getDepartment().getIdDepartment(), ((ActivityGroup)this.comboGroup.getValue()).getIdActivityGroup());
			
			this.comboActivity.setItems(list);
			
			if(list.size() > 0){
				this.comboActivity.setValue(list.get(0));
			}
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
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
					this.textAmount.setLabel(unit.getAmountDescription());
					
					this.textValidatedAmount.setVisible(unit.isFillAmount());
					this.textValidatedAmount.setLabel(unit.getAmountDescription() + " Validado(a)");
				}
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		if((this.submission.getActivity() == null) || (this.submission.getActivity().getIdActivity() == 0)){
			this.loadActivities();	
		}else{
			try {
				ActivityGroupBO bo = new ActivityGroupBO();
				ActivityGroup group = bo.findByActivity(this.submission.getActivity().getIdActivity());
				
				this.comboGroup.setValue(group);
				this.loadActivities();
				this.comboActivity.setValue(this.submission.getActivity());
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		this.textStudent.setValue(this.submission.getStudent().getName());
		this.textDescription.setValue(this.submission.getDescription());
		this.comboSemester.setSemester(this.submission.getSemester());
		this.textYear.setYear(this.submission.getYear());
		this.textSubmissionDate.setValue(DateUtils.convertToLocalDate(this.submission.getSubmissionDate()));
		this.textAmount.setValue(String.format("%.2f", this.submission.getAmount()));
		this.comboFeedback.setValue(this.submission.getFeedback());
		this.textValidatedAmount.setValue(String.format("%.2f", this.submission.getValidatedAmount()));
		this.textFeedbackDate.setValue(DateUtils.convertToLocalDate(this.submission.getFeedbackDate()));
		this.textFeedbackUser.setValue(this.submission.getFeedbackUser().getName());
		this.textComments.setValue(this.submission.getComments());
		this.textFeedbackReason.setValue(this.submission.getFeedbackReason());
		
		if(Session.isUserManager(SystemModule.SIGAC) && (this.submission.getValidatedAmount() <= 0)) {
			this.textValidatedAmount.setValue(String.format("%.2f", this.submission.getAmount()));
		}
		
		try {
			Semester semester = new UserBO().getRegisterSemester(submission.getStudent().getIdUser(), submission.getDepartment().getIdDepartment());
			
			if(semester != null) {
				this.textRegisterSemester.setValue(String.valueOf(semester.getSemester()) + "/" + String.valueOf(semester.getYear()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean allowEdit = false;
		
		if(this.submission.getIdActivitySubmission() == 0) {
			this.tab4.setVisible(false);
		}
		
		try {
			allowEdit = !new FinalSubmissionBO().studentHasSubmission(this.submission.getStudent().getIdUser(), this.submission.getDepartment().getIdDepartment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.setSaveButtonEnabled(this.isSaveButtonEnabled() && allowEdit);
	}
	
	private void loadCertificate(){
		this.tab2.setVisible(false);
		
		if(this.uploadFile.getUploadedFile() != null) {
			this.submission.setFile(this.uploadFile.getUploadedFile());
		}
		
		if(submission.getFile() != null){
			this.layoutTab3.removeAll();
			
			StreamResource s = new StreamResource("report.pdf", () -> new ByteArrayInputStream(submission.getFile()));
			
			PdfBrowserViewer viewer = new PdfBrowserViewer(s);
			viewer.setWidth("800px");
			viewer.setHeight("400px");
			
			this.layoutTab3.add(viewer);
			this.tab2.setVisible(true);
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
				this.submission.setFeedbackDate(DateUtils.convertToDate(this.textFeedbackDate.getValue()));
				this.submission.setValidatedAmount(Double.parseDouble(this.textValidatedAmount.getValue().replace(",", ".")));
				this.submission.setFeedbackReason(this.textFeedbackReason.getValue());
				
				if(this.submission.getFeedbackDate() == null){
					this.submission.setFeedbackDate(new Date());
				}
			}
			
			if((this.submission.getFeedback() == ActivityFeedback.NONE) || (Session.isUserManager(SystemModule.SIGAC))){
				this.submission.setSemester(this.comboSemester.getSemester());
				this.submission.setYear(this.textYear.getYear());
			}
			
			if(this.submission.getFeedback() == ActivityFeedback.NONE){
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Submissão", e.getMessage());
		}
	}

}
