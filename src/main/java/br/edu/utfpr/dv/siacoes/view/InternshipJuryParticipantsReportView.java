package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipJuryParticipantsReportView extends ReportView {

	public static final String NAME = "internshipjuryparticipantsreport";
	
	private final DateField dateInitial;
	private final DateField dateFinal;
	private final CheckBox checkGroup;
	
	public InternshipJuryParticipantsReportView() {
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Participação em Bancas de Estágio Obrigatório");
		
		this.dateInitial = new DateField("Data Inicial");
		this.dateInitial.setDateFormat("dd/MM/yyyy");
		this.dateInitial.setValue(DateUtils.getSunday(DateUtils.getToday().getTime()));
		
		this.dateFinal = new DateField("Data Final");
		this.dateFinal.setDateFormat("dd/MM/yyyy");
		this.dateFinal.setValue(DateUtils.addDay(this.dateInitial.getValue(), 7));
		
		this.checkGroup = new CheckBox("Agrupar relatório por banca");
		
		this.addFilterField(new HorizontalLayout(this.dateInitial, this.dateFinal));
		this.addFilterField(this.checkGroup);
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		InternshipJuryBO bo = new InternshipJuryBO();
		
		return bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, this.dateInitial.getValue(), this.dateFinal.getValue(), this.checkGroup.getValue());
	}

}
