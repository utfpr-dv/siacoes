package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.ui.grid.ProfessorScheduleDataSource;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ProfessorScheculeWindow extends BasicWindow {
	
	private final Grid<ProfessorScheduleDataSource> gridSchedule;

	public ProfessorScheculeWindow(User professor) {
		super("Agenda do Professor");
		
		this.gridSchedule = new Grid<ProfessorScheduleDataSource>();
		this.gridSchedule.setSelectionMode(SelectionMode.SINGLE);
		this.gridSchedule.setSizeFull();
		this.gridSchedule.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSchedule.addColumn(new LocalDateRenderer<>(ProfessorScheduleDataSource::getDate, "dd/MM/yyyy")).setHeader("Data").setFlexGrow(0).setWidth("150px");
		this.gridSchedule.addColumn(new LocalDateTimeRenderer<>(ProfessorScheduleDataSource::getStart, "HH:mm")).setHeader("Início").setFlexGrow(0).setWidth("100px");
		this.gridSchedule.addColumn(new LocalDateTimeRenderer<>(ProfessorScheduleDataSource::getEnd, "HH:mm")).setHeader("Término").setFlexGrow(0).setWidth("100px");
		this.gridSchedule.addColumn(ProfessorScheduleDataSource::getDescription).setHeader("Compromisso");
		
		VerticalLayout layout = new VerticalLayout(this.gridSchedule, new Label("* Bancas pré-agendadas."));
		layout.expand(this.gridSchedule);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		this.add(layout);
		
		this.setWidth("900px");
		this.setHeight("400px");
		
		this.loadSchedule(professor);
	}
	
	private void loadSchedule(User professor) {
		try {
			Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
			List<Jury> list = new JuryBO().listByAppraiser(professor.getIdUser(), semester.getSemester(), semester.getYear());
			List<JuryRequest> list3 = new JuryRequestBO().listByAppraiser(professor.getIdUser(), semester.getSemester(), semester.getYear());
			List<InternshipJury> list2 = new InternshipJuryBO().listByAppraiser(professor.getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
			List<InternshipJuryRequest> list4 = new InternshipJuryRequestBO().listByAppraiser(professor.getIdUser(), semester.getSemester(), semester.getYear());
			
			List<Jury> listJury = new ArrayList<Jury>();
			for(Jury jury : list) {
				if(jury.getFormat() != JuryFormat.ASYNC) {
					listJury.add(jury);
				}
			}
			
			List<JuryRequest> listJuryRequest = new ArrayList<JuryRequest>();
			for(JuryRequest jury : list3) {
				if(jury.getFormat() != JuryFormat.ASYNC) {
					listJuryRequest.add(jury);
				}
			}
			
			this.gridSchedule.setItems(ProfessorScheduleDataSource.load(listJury, listJuryRequest, list2, list4));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Compromissos", e.getMessage());
		}
	}
	
}
