package br.edu.utfpr.dv.siacoes.window;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ProfessorScheculeWindow extends BasicWindow {
	
	private final Grid gridSchedule;

	public ProfessorScheculeWindow(User professor) {
		super("Agenda do Professor");
		
		this.gridSchedule = new Grid();
		this.gridSchedule.addColumn("Data", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridSchedule.addColumn("Início", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("HH:mm")));
		this.gridSchedule.addColumn("Término", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("HH:mm")));
		this.gridSchedule.addColumn("Compromisso");
		this.gridSchedule.getColumns().get(0).setWidth(125);
		this.gridSchedule.getColumns().get(1).setWidth(100);
		this.gridSchedule.getColumns().get(2).setWidth(100);
		this.gridSchedule.setSizeFull();
		
		VerticalLayout layout = new VerticalLayout(this.gridSchedule, new Label("* Bancas pré-agendadas."));
		layout.setExpandRatio(this.gridSchedule, 1.0f);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		this.setContent(layout);
		
		this.setWidth("900px");
		this.setHeight("400px");
		
		this.loadSchedule(professor);
	}
	
	private void loadSchedule(User professor) {
		try {
			Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
			List<Event> events = new ArrayList<Event>();
			
			List<Jury> list = new JuryBO().listByAppraiser(professor.getIdUser(), semester.getSemester(), semester.getYear());
			
			for(Jury jury : list) {
				Event event = new Event();
				
				event.setDate(jury.getDate());
				event.setStartTime(jury.getDate());
				event.setEndTime(DateUtils.addHour(jury.getDate(), 1));
				if(jury.getStage() == 2) {
					event.setEndTime(DateUtils.addMinute(event.getEndTime(), 30));
					event.setDescription("Banca de TCC 2 - " + jury.getStudent());
				} else {
					event.setDescription("Banca de TCC 1 - " + jury.getStudent());
				}
						
				events.add(event);
			}
			
			List<JuryRequest> list3 = new JuryRequestBO().listByAppraiser(professor.getIdUser(), semester.getSemester(), semester.getYear());
			
			for(JuryRequest jury : list3) {
				if((jury.getJury() == null) || (jury.getJury().getIdJury() == 0)) {
					Event event = new Event();
					
					event.setDate(jury.getDate());
					event.setStartTime(jury.getDate());
					event.setEndTime(DateUtils.addHour(jury.getDate(), 1));
					if(jury.getStage() == 2) {
						event.setEndTime(DateUtils.addMinute(event.getEndTime(), 30));
						event.setDescription("* Banca de TCC 2 - " + jury.getStudent());
					} else {
						event.setDescription("* Banca de TCC 1 - " + jury.getStudent());
					}
							
					events.add(event);
				}
			}
			
			List<InternshipJury> list2 = new InternshipJuryBO().listByAppraiser(professor.getIdUser(), semester.getSemester(), semester.getYear());
			
			for(InternshipJury jury : list2) {
				Event event = new Event();
				
				event.setDate(jury.getDate());
				event.setStartTime(jury.getDate());
				event.setEndTime(DateUtils.addHour(jury.getDate(), 1));
				event.setDescription("Banca de Estágio - " + jury.getStudent());
						
				events.add(event);
			}

			Collections.sort(events, new Comparator<Event>() {
				@Override
				public int compare(Event e1, Event e2) {
					if (e1.getDate().before(e2.getDate())) {
			            return -1;
			        } else if (e1.getDate().after(e2.getDate())) {
			            return 1;
			        } else {
			            return 0;
			        }       
				}
			});
			
			for(Event e : events) {
				this.gridSchedule.addRow(e.getDate(), e.getStartTime(), e.getEndTime(), e.getDescription());	
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Compromissos", e.getMessage());
		}
	}
	
	private class Event {
		
		private Date date;
		private Date startTime;
		private Date endTime;
		private String description;
		
		public Event() {
			this.setDate(DateUtils.getToday().getTime());
			this.setStartTime(DateUtils.getNow().getTime());
			this.setEndTime(DateUtils.getNow().getTime());
			this.setDescription("");
		}
		
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Date getStartTime() {
			return startTime;
		}
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
	}
	
}
