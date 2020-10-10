package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.model.User;

@PageTitle("Relatório de Validação de Atividades Complementares")
@Route(value = "activityvalidationreport", layout = MainLayout.class)
public class ActivityValidationReportView extends ReportView {
	
	private final Select<User> comboFeedbackUser;

	public ActivityValidationReportView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.comboFeedbackUser = new Select<User>();
		this.comboFeedbackUser.setLabel("Atividades validadas por");
		this.comboFeedbackUser.setWidth("400px");
		this.loadComboFeedbackUser();
		
		this.addFilterField(this.comboFeedbackUser);
	}
	
	private void loadComboFeedbackUser(){
		try {
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<User> list = bo.listFeedbackUsers(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			User all = new User();
			all.setIdUser(0);
			all.setName("(TODOS)");
			list.add(0, all);
			
			this.comboFeedbackUser.setItems(list);
			
			this.comboFeedbackUser.setValue(all);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Usuários", e.getMessage());
		}
	}

	@Override
	public byte[] generateReport() throws Exception {
		ActivitySubmissionBO bo = new ActivitySubmissionBO();
		return bo.getActivityValidationReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboFeedbackUser.getValue().getIdUser());
	}

}
