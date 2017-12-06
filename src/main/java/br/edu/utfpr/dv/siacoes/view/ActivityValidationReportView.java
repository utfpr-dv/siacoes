package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.User;

public class ActivityValidationReportView extends ReportView {
	
	public static final String NAME = "activityvalidationreport";
	
	private final NativeSelect comboFeedbackUser;

	public ActivityValidationReportView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Validação de Atividades Complementares");
		
		this.comboFeedbackUser = new NativeSelect("Atividades validadas por");
		this.comboFeedbackUser.setWidth("400px");
		this.comboFeedbackUser.setNullSelectionAllowed(false);
		this.loadComboFeedbackUser();
		
		this.addFilterField(this.comboFeedbackUser);
	}
	
	private void loadComboFeedbackUser(){
		try {
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<User> list = bo.listFeedbackUsers(Session.getUser().getDepartment().getIdDepartment());
			
			User all = new User();
			all.setIdUser(0);
			all.setName("(TODOS)");
			
			this.comboFeedbackUser.addItem(all);
			this.comboFeedbackUser.addItems(list);
			
			this.comboFeedbackUser.setValue(all);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Carregar Usuários", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public byte[] generateReport() throws Exception {
		ActivitySubmissionBO bo = new ActivitySubmissionBO();
		return bo.getActivityValidationReport(Session.getUser().getDepartment().getIdDepartment(), ((User)this.comboFeedbackUser.getValue()).getIdUser());
	}

}
