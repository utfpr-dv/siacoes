package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class JuryParticipantsReportView extends ReportView {
	
	public static final String NAME = "juryparticipantsreport";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final CheckBox checkGroup;
	
	public JuryParticipantsReportView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Participação em Bancas de TCC");
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.checkGroup = new CheckBox("Agrupar relatório por banca");
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		this.addFilterField(this.checkGroup);
	}

	@Override
	public byte[] generateReport() throws Exception {
		JuryBO bo = new JuryBO();
		
		return bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear(), this.checkGroup.getValue());
	}

}
