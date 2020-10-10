package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Relatório de Participação em Bancas de TCC")
@Route(value = "juryparticipantsreport", layout = MainLayout.class)
public class JuryParticipantsReportView extends ReportView {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Checkbox checkGroup;
	
	public JuryParticipantsReportView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
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
		
		this.checkGroup = new Checkbox("Agrupar relatório por banca");
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		this.addFilterField(this.checkGroup);
	}

	@Override
	public byte[] generateReport() throws Exception {
		JuryBO bo = new JuryBO();
		
		return bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear(), this.checkGroup.getValue());
	}

}
