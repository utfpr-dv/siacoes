package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Relatório de Reunião de Orientação de TCC")
@Route(value = "attendancereport", layout = MainLayout.class)
public class AttendanceReportView extends ReportView {
	
	private final SemesterComboBox comboSemester;
	private final StageComboBox comboStage;
	private final YearField textYear;
	private final Checkbox checkIncludeCosupervisor;
	private final Checkbox checkDetail;

	public AttendanceReportView(){
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.comboStage = new StageComboBox();
		this.comboStage.setShowBoth(true);
		this.comboStage.selectBoth();
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setValue(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.checkIncludeCosupervisor = new Checkbox("Mostrar reuniões com o coorientador");
		
		this.checkDetail = new Checkbox("Mostrar relatório detalhado");
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
		this.addFilterField(this.checkDetail);
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		AttendanceBO bo = new AttendanceBO();
		return bo.getAttendanceReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), (this.comboStage.isBothSelected() ? 0 : this.comboStage.getStage()), this.checkIncludeCosupervisor.getValue(), this.checkDetail.getValue());
	}

}
