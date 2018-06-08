package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport.StudentStage;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class StudentActivityStatusReportView extends ReportView {
	
	public static final String NAME = "studentactivitystatusreport";
	
	private final NativeSelect comboStage;
	private final CheckBox checkStudentsWithoutPoints;

	public StudentActivityStatusReportView(){
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Situação do Acadêmico em Atividades Complementares");
		
		this.comboStage = new NativeSelect("Situação do Acadêmico");
		this.comboStage.setWidth("400px");
		this.comboStage.setNullSelectionAllowed(false);
		this.comboStage.addItem(StudentStage.REGULAR);
		this.comboStage.addItem(StudentStage.FINISHINGCOURSE);
		this.comboStage.addItem(StudentStage.ALMOSTGRADUATED);
		//this.comboStage.addItem(StudentStage.GRADUATED);
		this.comboStage.setValue(StudentStage.REGULAR);
		
		this.checkStudentsWithoutPoints = new CheckBox("Filtrar apenas acadêmicos que ainda não atingiram a pontuação necessária");
		
		this.addFilterField(this.comboStage);
		this.addFilterField(this.checkStudentsWithoutPoints);
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		ActivitySubmissionBO bo = new ActivitySubmissionBO();
		return bo.getStudentActivityStatusReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (StudentStage)this.comboStage.getValue(), this.checkStudentsWithoutPoints.getValue());
	}

}
