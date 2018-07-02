package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class JuryGradesReportView extends ReportView {
	
	public static final String NAME = "jurygradesreport";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final StageComboBox comboStage;
	
	public JuryGradesReportView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Notas da Banca");
		
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
		
		this.comboStage = new StageComboBox(true);
		this.comboStage.selectBoth();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
	}

	@Override
	public byte[] generateReport() throws Exception {
		return new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage(), true);
	}

}
