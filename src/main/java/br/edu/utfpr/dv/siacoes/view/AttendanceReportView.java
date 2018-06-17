package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class AttendanceReportView extends ReportView {
	
	public static final String NAME = "attendancereport";
	
	private final SemesterComboBox comboSemester;
	private final StageComboBox comboStage;
	private final YearField textYear;
	private final CheckBox checkIncludeCosupervisor;
	private final CheckBox checkDetail;

	public AttendanceReportView(){
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Reunião de Orientação de TCC");
		
		this.comboSemester = new SemesterComboBox();
		
		this.comboStage = new StageComboBox();
		this.comboStage.setShowBoth(true);
		
		this.textYear = new YearField();
		
		this.checkIncludeCosupervisor = new CheckBox("Mostrar reuniões com o coorientador");
		
		this.checkDetail = new CheckBox("Mostrar relatório detalhado");
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
		this.addFilterField(this.checkDetail);
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		AttendanceBO bo = new AttendanceBO();
		return bo.getAttendanceReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), (this.comboStage.isBothSelected() ? 0 : this.comboStage.getStage()), this.checkIncludeCosupervisor.getValue(), this.checkDetail.getValue());
	}

}
