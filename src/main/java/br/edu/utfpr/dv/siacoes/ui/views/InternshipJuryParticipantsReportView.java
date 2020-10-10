package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Relatório de Participação em Bancas de Estágio Obrigatório")
@Route(value = "internshipjuryparticipantsreport", layout = MainLayout.class)
public class InternshipJuryParticipantsReportView extends ReportView {
	
	private final DatePicker dateInitial;
	private final DatePicker dateFinal;
	private final Checkbox checkGroup;
	
	public InternshipJuryParticipantsReportView() {
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.dateInitial = new DatePicker("Data Inicial");
		//this.dateInitial.setDateFormat("dd/MM/yyyy");
		this.dateInitial.setValue(DateUtils.convertToLocalDate(DateUtils.getSunday(DateUtils.getToday().getTime())));
		
		this.dateFinal = new DatePicker("Data Final");
		//this.dateFinal.setDateFormat("dd/MM/yyyy");
		this.dateFinal.setValue(DateUtils.convertToLocalDate(DateUtils.addDay(DateUtils.convertToDate(this.dateInitial.getValue()), 7)));
		
		this.checkGroup = new Checkbox("Agrupar relatório por banca");
		
		this.addFilterField(new HorizontalLayout(this.dateInitial, this.dateFinal));
		this.addFilterField(this.checkGroup);
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		InternshipJuryBO bo = new InternshipJuryBO();
		
		return bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, DateUtils.convertToDate(this.dateInitial.getValue()), DateUtils.convertToDate(this.dateFinal.getValue()), this.checkGroup.getValue());
	}

}
