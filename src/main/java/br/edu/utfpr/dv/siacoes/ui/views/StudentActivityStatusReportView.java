package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport.StudentStage;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;

@PageTitle("Relatório de Situação do Acadêmico em Atividades Complementares")
@Route(value = "studentactivitystatusreport", layout = MainLayout.class)
public class StudentActivityStatusReportView extends ReportView {
	
	private final Select<StudentStage> comboStage;
	private final Checkbox checkStudentsWithoutPoints;
	private final Checkbox checkStudentsWithPoints;
	private final Checkbox checkIncludeWithFinalSubmission;

	public StudentActivityStatusReportView(){
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.comboStage = new Select<StudentStage>();
		this.comboStage.setLabel("Situação do Acadêmico");
		this.comboStage.setWidth("400px");
		this.comboStage.setItems(StudentStage.REGULAR, StudentStage.FINISHINGCOURSE, StudentStage.ALMOSTGRADUATED);
		//this.comboStage.addItem(StudentStage.GRADUATED);
		this.comboStage.setValue(StudentStage.REGULAR);
		
		this.checkStudentsWithoutPoints = new Checkbox("Filtrar apenas acadêmicos que ainda não atingiram a pontuação necessária");
		
		this.checkStudentsWithPoints = new Checkbox("Filtrar apenas acadêmicos que já atingiram a pontuação necessária");
		
		this.checkStudentsWithoutPoints.addValueChangeListener(event -> {
			if(this.checkStudentsWithoutPoints.getValue()) {
				this.checkStudentsWithPoints.setValue(false);
			}
		});
		this.checkStudentsWithPoints.addValueChangeListener(event -> {
			if(this.checkStudentsWithPoints.getValue()) {
				this.checkStudentsWithoutPoints.setValue(false);
			}
		});
		
		this.checkIncludeWithFinalSubmission = new Checkbox("Incluir acadêmicos que já foram aprovados");
		
		this.addFilterField(this.comboStage);
		this.addFilterField(this.checkStudentsWithoutPoints);
		this.addFilterField(this.checkStudentsWithPoints);
		this.addFilterField(this.checkIncludeWithFinalSubmission);
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		ActivitySubmissionBO bo = new ActivitySubmissionBO();
		return bo.getStudentActivityStatusReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (StudentStage)this.comboStage.getValue(), this.checkStudentsWithoutPoints.getValue(), this.checkStudentsWithPoints.getValue(), this.checkIncludeWithFinalSubmission.getValue());
	}

}
