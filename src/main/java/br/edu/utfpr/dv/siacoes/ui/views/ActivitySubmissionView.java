package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.FinalSubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.ui.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.ActivitySubmissionDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditActivitySubmissionWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditStudentProfileWindow;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.FinalSubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;

@PageTitle("Submissão de Atividades")
@Route(value = "activitysubmission", layout = MainLayout.class)
public class ActivitySubmissionView extends ListView<ActivitySubmissionDataSource> {
	
	private final RadioButtonGroup<String> optionFilterType;
	private final StudentComboBox comboStudent;
	private final Select<String> comboFeedback;
	private final TextField textDescription;
	private final Button buttonFinalReport;
	private final Button buttonFinalSubmission;
	private final Button buttonStudentProfile;
	
	private final VerticalLayout layoutScore;
	private final VerticalLayout layoutLabel;
	
	private final Details panelScore;
	private final Details panelLabel;
	
	private static final String SUBMISSIONS_WITHOUT_FEEDBACK = "Listar submissões sem parecer";
	private static final String SUBMISSIONS_BY_STUDENT = "Filtro personalizado";
	
	public ActivitySubmissionView(){
		super(SystemModule.SIGAC);
		
		this.getGrid().addComponentColumn(item -> createStudentDescription(this.getGrid(), item)).setHeader("Acadêmico");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getSemester).setHeader("Sem.").setFlexGrow(0).setWidth("75px");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("80px");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getGroup, "Group").setHeader("Grupo").setFlexGrow(0).setWidth("80px");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getActivity, "Activity").setHeader("Atividade");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getDescription).setHeader("Descrição da Atividade").setFlexGrow(0).setWidth("350px");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getFeedback, "Feedback").setHeader("Parecer").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(ActivitySubmissionDataSource::getScore).setHeader("Pontuação").setFlexGrow(0).setWidth("125px");
		
		this.optionFilterType = new RadioButtonGroup<String>();
		this.optionFilterType.setItems(SUBMISSIONS_WITHOUT_FEEDBACK, SUBMISSIONS_BY_STUDENT);
		this.optionFilterType.setValue(SUBMISSIONS_WITHOUT_FEEDBACK);
		//this.optionFilterType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.comboFeedback = new Select<String>();
		this.comboFeedback.setLabel("Parecer");
		this.comboFeedback.setWidth("200px");
		this.comboFeedback.setItems(ActivityFeedback.NONE.toString(), ActivityFeedback.APPROVED.toString(), ActivityFeedback.DISAPPROVED.toString(), "(Todos)");
		this.comboFeedback.setValue("(Todos)");
		
		this.textDescription = new TextField("Descrição da Atividade");
		this.textDescription.setWidth("300px");
		
		this.buttonFinalReport = new Button("Relatório Final");
		this.buttonFinalReport.setDisableOnClick(true);
		this.buttonFinalReport.addClickListener(event -> {
			finalReport();
			this.buttonFinalReport.setEnabled(true);
        });
		this.buttonFinalReport.setIcon(new Icon(VaadinIcon.FILE_TEXT));
		
		this.buttonFinalSubmission = new Button("Finalizar Processo", event -> {
			finalSubmission();
        });
		this.buttonFinalSubmission.setIcon(new Icon(VaadinIcon.CLIPBOARD_CHECK));
		
		this.buttonStudentProfile = new Button("Perfil do Acadêmico", event -> {
			studentProfile();
        });
		this.buttonStudentProfile.setIcon(new Icon(VaadinIcon.USER));
		
		this.layoutScore = new VerticalLayout();
		this.layoutScore.setSpacing(false);
		this.layoutScore.setMargin(false);
		this.layoutScore.setPadding(false);
		
		this.layoutLabel = new VerticalLayout();
		this.layoutLabel.setSpacing(true);
		this.layoutLabel.setMargin(false);
		this.layoutLabel.setPadding(false);
		
		if(Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()){
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			
			if(Session.isUserManager(this.getModule())){
				this.setEditCaption("Validar");
				this.setEditIcon(new Icon(VaadinIcon.CHECK));
			}else{
				this.setEditCaption("Visualizar");
				this.setEditIcon(new Icon(VaadinIcon.SEARCH));
			}
			
			Image imageRedWarning = new Image();
			imageRedWarning.setSrc("images/redwarning.png");
			imageRedWarning.setHeight("16px");
			imageRedWarning.setWidth("16px");
			
			HorizontalLayout h1 = new HorizontalLayout(imageRedWarning, new Label("Provável formando"));
			h1.setMargin(false);
			h1.setPadding(false);
			h1.setSpacing(true);
			
			Image imageYellowWarning = new Image();
			imageYellowWarning.setSrc("images/yellowwarning.png");
			imageYellowWarning.setHeight("16px");
			imageYellowWarning.setWidth("16px");
			
			HorizontalLayout h2 = new HorizontalLayout(imageYellowWarning, new Label("Períodos finais"));
			h2.setMargin(false);
			h2.setPadding(false);
			h2.setSpacing(true);
			
			this.layoutLabel.add(h1, h2);
			
			this.panelLabel = new Details("Legenda", this.layoutLabel);
			this.addActionPanel(this.panelLabel);
			
			HorizontalLayout h3 = new HorizontalLayout(this.comboStudent, this.comboFeedback, this.textDescription);
			h3.setMargin(false);
			h3.setPadding(false);
			h3.setSpacing(true);
			
			this.addFilterField(new VerticalLayout(this.optionFilterType, h3));
		}else{
			boolean allowAdd = false;
			
			try {
				allowAdd = !new FinalSubmissionBO().studentHasSubmission(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.setAddVisible(allowAdd);
			this.setDeleteVisible(allowAdd);
			
			if(!allowAdd) {
				this.setEditCaption("Visualizar");
				this.setEditIcon(new Icon(VaadinIcon.SEARCH));
			}
			
			this.addFilterField(this.comboFeedback);
			
			this.panelLabel = new Details("Legenda", this.layoutLabel);
		}
		
		this.addActionButton(this.buttonFinalReport);
		
		if(Session.isUserManager(this.getModule())) {
			this.addActionButton(this.buttonFinalSubmission);
			this.addActionButton(this.buttonStudentProfile);
		}
		
		this.panelScore = new Details("Pontuação", this.layoutScore);
		this.addActionPanel(this.panelScore);
	}
	
	private Component createStudentDescription(Grid<ActivitySubmissionDataSource> grid, ActivitySubmissionDataSource item) {
		Image image = null;
		
		if((Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()) && (this.optionFilterType.getValue().equals(SUBMISSIONS_WITHOUT_FEEDBACK))) {
			if(item.getStage() > 0) {
				image = new Image();
				image.setHeight("16px");
				image.setWidth("16px");
				
				if(item.getStage() == 2) {
					image.setSrc("images/redwarning.png");
				} else if(item.getStage() == 1) {
					image.setSrc("images/yellowwarning.png");
				}
			}
		}
		
		Label label = new Label(item.getStudent());
		
		if(image != null) {
			HorizontalLayout layout = new HorizontalLayout(image, label);
			layout.setSpacing(true);
			layout.setPadding(false);
			layout.setMargin(false);
			
			return layout;
		} else {
			return label;
		}
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().getColumns().get(0).setVisible(true);
		this.getGrid().getColumns().get(4).setVisible(true);
		
		if((Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()) && this.optionFilterType.getValue().equals(SUBMISSIONS_WITHOUT_FEEDBACK)) {
			this.getGrid().getColumns().get(4).setVisible(false);
		}
		if(Session.isUserStudent() || (!this.optionFilterType.getValue().equals(SUBMISSIONS_WITHOUT_FEEDBACK) && (this.comboStudent.getStudent() != null) && (this.comboStudent.getStudent().getIdUser() != 0))) {
			this.getGrid().getColumns().get(0).setVisible(false);
		}
		
		this.buttonFinalReport.setEnabled(true);
		this.buttonFinalSubmission.setEnabled(true);
		this.buttonStudentProfile.setEnabled(true);
		this.panelScore.setVisible(true);
		this.panelLabel.setVisible(false);
		
		try {
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			List<ActivitySubmissionFooterReport> scores = new ArrayList<ActivitySubmissionFooterReport>();
			int feedback = -1;
			
			if(!this.comboFeedback.getValue().equals("(Todos)")) {
				feedback = ActivityFeedback.fromDescription(this.comboFeedback.getValue()).getValue();
			}
			
			if(Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()) {
				this.buttonFinalReport.setEnabled(false);
				this.buttonFinalSubmission.setEnabled(false);
				this.buttonStudentProfile.setEnabled(false);
				this.panelScore.setVisible(false);
				this.panelLabel.setVisible(true);
				
				if(this.optionFilterType.getValue().equals(SUBMISSIONS_WITHOUT_FEEDBACK)) {
					list = bo.listWithNoFeedback2(Session.getSelectedDepartment().getDepartment().getIdDepartment());
				} else if((this.comboStudent.getStudent() != null) && (this.comboStudent.getStudent().getIdUser() != 0) && this.textDescription.getValue().trim().isEmpty()) {
					list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), feedback, (feedback == ActivityFeedback.APPROVED.getValue()));
					List<ActivitySubmission> listReport;
					
					if(feedback == ActivityFeedback.APPROVED.getValue()) {
						listReport = list;
					} else {
						listReport = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), ActivityFeedback.APPROVED.getValue(), true);
					}
					
					scores = bo.getFooterReport(listReport);
					
					this.buttonFinalReport.setEnabled(true);
					this.buttonFinalSubmission.setEnabled(true);
					this.buttonStudentProfile.setEnabled(true);
					this.panelScore.setVisible(true);
					this.panelLabel.setVisible(false);
				} else {
					list = bo.list(Session.getSelectedDepartment().getDepartment().getIdDepartment(), ((this.comboStudent.getStudent() != null) ? this.comboStudent.getStudent().getIdUser() : 0), feedback, this.textDescription.getValue());
				}
			} else {
				list = bo.listByStudent(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), feedback, (feedback == ActivityFeedback.APPROVED.getValue()));
				List<ActivitySubmission> listReport;
				
				if(feedback == ActivityFeedback.APPROVED.getValue()) {
					listReport = list;
				} else {
					listReport = bo.listByStudent(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), ActivityFeedback.APPROVED.getValue(), true);
				}
				
				scores = bo.getFooterReport(listReport);
			}
			
			this.buildPanelScores(scores);
			
			this.getGrid().setItems(ActivitySubmissionDataSource.load(list));
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Submissões", e.getMessage());
		}
	}
	
	private void buildPanelScores(List<ActivitySubmissionFooterReport> scores) {
		this.layoutScore.removeAll();
		
		for(ActivitySubmissionFooterReport group : scores) {
			H5 labelTitle = new H5("Grupo " + String.valueOf(group.getSequence()));
			Label labelScore = new Label(String.valueOf(group.getTotal()) + " ponto" + (group.getTotal() >= 2 ? "s" : ""));
			
			this.layoutScore.add(labelTitle, labelScore);
		}
		
		H5 labelTitle = new H5("Situação");
		Label labelScore;
		try {
			labelScore = new Label(new ActivitySubmissionBO().getSituation(scores, Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		} catch (Exception e) {
			labelScore = new Label("-");
		}
		
		this.layoutScore.add(labelTitle, labelScore);
	}
	
	private void finalReport() {
		try {
			User student;
			
			if(Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()) {
				student = this.comboStudent.getStudent();
			} else {
				student = Session.getUser();
			}
			
			this.showReport(new ActivitySubmissionBO().getPdfReport(student.getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), true));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Gerar Relatório", e.getMessage());
		}
	}
	
	private void studentProfile() {
		try {
			User student = this.comboStudent.getStudent();
			UserDepartment profile = new UserDepartmentBO().find(student.getIdUser(), UserProfile.STUDENT, Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			if((profile != null) && (profile.getIdUserDepartment() != 0)) {
				EditStudentProfileWindow window = new EditStudentProfileWindow(student, profile);
				window.open();
			} else {
				this.showWarningNotification("Perfil do Acadêmico", "Não foi possível encontrar o perfil do acadêmico.");
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Perfil do Acadêmico", e.getMessage());
		}
	}
	
	private void finalSubmission() {
		ConfirmDialog.createQuestion()
			.withIcon(new Icon(VaadinIcon.QUESTION))
	    	.withCaption("Confirma a Ação?")
	    	.withMessage("Confirma a aprovação do acadêmico em Atividades Complementares?\n\nEsta operação não poderá ser desfeita.")
	    	.withOkButton(() -> {
	    		try {
	        		FinalSubmissionBO bo = new FinalSubmissionBO();
	        		
	        		FinalSubmission submission = bo.registerFinalSubmission(comboStudent.getStudent().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), Session.getUser().getIdUser());
	        		
	        		showSuccessNotification("Finalizar Processo", "O processo de aprovação do acadêmico foi realizado com sucesso.");
	    		} catch (Exception ex) {
	    			ex.printStackTrace();
	    			
	    			showErrorNotification("Finalizar Processo", ex.getMessage());
	    		}
	    	}, ButtonOption.caption("Sim"), ButtonOption.icon(VaadinIcon.CHECK))
	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Não"), ButtonOption.icon(VaadinIcon.CLOSE))
	    	.open();
	}

	@Override
	public void addClick() {
		EditActivitySubmissionWindow window = new EditActivitySubmissionWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			ActivitySubmission submission = bo.findById((int)id);
			
			EditActivitySubmissionWindow window = new EditActivitySubmissionWindow(submission, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Submissão", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Submissão", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		if(!this.optionFilterType.getValue().equals(SUBMISSIONS_WITHOUT_FEEDBACK)){
			if(((this.comboStudent.getStudent() == null) || (this.comboStudent.getStudent().getIdUser() == 0)) && this.comboFeedback.getValue().equals("(Todos)") && this.textDescription.getValue().trim().isEmpty()){
				throw new Exception("Selecione ao menos um filtro.");	
			}
		}
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		super.beforeEnter(event);
		
		if(Session.isAuthenticated()) {
			if(!Session.isUserManager(this.getModule()) && !Session.isUserDepartmentManager() && !Session.isUserStudent()) {
				event.rerouteTo("403");
			}
		}
	}

}
