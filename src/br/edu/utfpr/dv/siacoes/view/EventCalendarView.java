package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.CalendarEvent;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.CalendarEventWindow;

public class EventCalendarView extends BasicView {
	
	public static final String NAME = "eventcalendar";
	
	private static final String ONLYTHESIS = "Listar apenas bancas de TCC";
	private static final String ONLYINTERNSHIP = "Listar apenas bancas de Estágio";
	private static final String LISTALL = "Listar todas as bancas";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final CheckBox checkListOnlyMy;
	private final OptionGroup optionFilterType;
	private final Button buttonFilter;
	private final Panel panelFilter;
	
	private SystemModule module;
	
	private final Calendar calendar;
	
	public EventCalendarView(){
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.calendar = new Calendar();
		this.calendar.setFirstVisibleHourOfDay(7);
		this.calendar.setLastVisibleHourOfDay(23);
		this.calendar.setSizeFull();
		this.calendar.setReadOnly(true);
		this.calendar.setHandler(new EventClickHandler() {
			@Override
			public void eventClick(EventClick e) {
				CalendarEvent event = (CalendarEvent) e.getCalendarEvent();

				UI.getCurrent().addWindow(new CalendarEventWindow(event));
			}
		});
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem(EventCalendarView.ONLYTHESIS);
		this.optionFilterType.addItem(EventCalendarView.ONLYINTERNSHIP);
		this.optionFilterType.addItem(EventCalendarView.LISTALL);
		this.optionFilterType.select(EventCalendarView.LISTALL);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.checkListOnlyMy = new CheckBox("Listar apenas minhas bancas");
		
		this.buttonFilter = new Button("Filtrar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	loadEvents();
            }
        });
		this.buttonFilter.setWidth("150px");
		
		HorizontalLayout layoutFields = new HorizontalLayout(this.optionFilterType, this.comboSemester, this.textYear, this.checkListOnlyMy);
		layoutFields.setSpacing(true);
		
		VerticalLayout layoutFilter = new VerticalLayout(layoutFields, this.buttonFilter);
		layoutFilter.setSpacing(true);
		layoutFilter.setMargin(true);
		
		this.panelFilter = new Panel("Filtros");
		this.panelFilter.setContent(layoutFilter);
		
		this.setSizeFull();
    	
    	VerticalLayout vl = new VerticalLayout(this.panelFilter, this.calendar);
		vl.setSizeFull();
		vl.setExpandRatio(this.calendar, 1);
		this.setContent(vl);
		
		if(Session.isUserStudent()){
			this.checkListOnlyMy.setVisible(false);
		}
		
		this.loadEvents();
	}
	
	public void setModule(SystemModule module){
    	if((this.getCaption() == null) || this.getCaption().trim().isEmpty()){
    		this.setCaption(module.getDescription());
    	}
    	
    	this.module = module;
    	this.setOpenMenu(module);
    }
    
    public SystemModule getModule(){
    	return this.module;
    }
    
    private void loadEvents(){
    	try {
			List<Jury> listThesis = new ArrayList<Jury>();
			List<InternshipJury> listInternship = new ArrayList<InternshipJury>();
			BasicEventProvider provider = new BasicEventProvider();
			int startHour = 23, endHour = 0;
			
			List<com.vaadin.ui.components.calendar.event.CalendarEvent> listEvents = this.calendar.getEvents(this.calendar.getStartDate(), this.calendar.getEndDate());
			
			for(int i = listEvents.size() - 1; i >= 0; i--){
				this.calendar.removeEvent(listEvents.get(i));
			}
			
			if(this.optionFilterType.isSelected(EventCalendarView.ONLYTHESIS) || this.optionFilterType.isSelected(EventCalendarView.LISTALL)){
				JuryBO bo = new JuryBO();
				
				if(this.checkListOnlyMy.getValue()){
					listThesis = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}else{
					listThesis = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
			if(this.optionFilterType.isSelected(EventCalendarView.ONLYINTERNSHIP) || this.optionFilterType.isSelected(EventCalendarView.LISTALL)){
				InternshipJuryBO bo = new InternshipJuryBO();
				
				if(this.checkListOnlyMy.getValue()){
					listInternship = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}else{
					listInternship = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
			if((listThesis.size() == 0) && (listInternship.size() == 0)){
				this.calendar.setStartDate(DateUtils.getSunday(DateUtils.getToday().getTime()));
				this.calendar.setEndDate(DateUtils.addDay(this.calendar.getStartDate(), 6));
				
				Notification.show("Listar Eventos", "Não há bancas agendadas para este semestre.", Notification.Type.WARNING_MESSAGE);
			}else{
				if((listThesis.size() > 0) && (listInternship.size() == 0)){
					this.calendar.setStartDate(listThesis.get(0).getDate());
				}else if((listThesis.size() == 0) && (listInternship.size() > 0)){
					this.calendar.setStartDate(listInternship.get(0).getDate());
				}else{
					if(listThesis.get(0).getDate().before(listInternship.get(0).getDate())){
						this.calendar.setStartDate(listThesis.get(0).getDate());
					}else{
						this.calendar.setStartDate(listInternship.get(0).getDate());
					}
				}
				this.calendar.setStartDate(DateUtils.getSunday(this.calendar.getStartDate()));
				this.calendar.setEndDate(DateUtils.addDay(this.calendar.getStartDate(), 6));
				
				for(Jury jury : listThesis){
					String title = "Banca de TCC " + String.valueOf(jury.getStage());
					String student = "Acadêmico(a): " + jury.getStudent().getName();
					String local = "Local: " + jury.getLocal();
					String appraisers = "Membros da banca: ";
					
					jury.setAppraisers(new JuryAppraiserBO().listAppraisers(jury.getIdJury()));
					
					for(JuryAppraiser appraiser : jury.getAppraisers()){
						appraisers += appraiser.getAppraiser().getName() + "; ";
					}
					
					CalendarEvent event = new CalendarEvent(title + " - " + student + " - " + local, title + " - " + student + " - " + local + " - " + appraisers, jury.getDate(), DateUtils.addHour(jury.getDate(), 1));
					
					event.setJury(jury);
					
					provider.addEvent(event);
					
					if(DateUtils.getHour(jury.getDate()) > endHour){
						endHour = DateUtils.getHour(jury.getDate()) + 2;
					}
					if(DateUtils.getHour(jury.getDate()) < startHour){
						startHour = DateUtils.getHour(jury.getDate()) - 1;
					}
				}
				
				for(InternshipJury jury : listInternship){
					String title = "Banca de Estágio";
					String student = "Acadêmico(a): " + jury.getStudent();
					String local = "Local: " + jury.getLocal();
					String appraisers = "Membros da banca: ";
					
					jury.setAppraisers(new InternshipJuryAppraiserBO().listAppraisers(jury.getIdInternshipJury()));
					
					for(InternshipJuryAppraiser appraiser : jury.getAppraisers()){
						appraisers += appraiser.getAppraiser().getName() + "; ";
					}
					
					CalendarEvent event = new CalendarEvent(title + " - " + student + " - " + local, title + " - " + student + " - " + local + " - " + appraisers, jury.getDate(), DateUtils.addHour(jury.getDate(), 1));
					
					event.setInternshipJury(jury);
					
					provider.addEvent(event);
					
					if(DateUtils.getHour(jury.getDate()) > endHour){
						endHour = DateUtils.getHour(jury.getDate()) + 2;
					}
					if(DateUtils.getHour(jury.getDate()) < startHour){
						startHour = DateUtils.getHour(jury.getDate()) - 1;
					}
				}
				
				this.calendar.setEventProvider(provider);
			}
			
			if(startHour < 0){
				startHour = 0;
			}
			if(endHour > 23){
				endHour = 23;
			}
			this.calendar.setFirstVisibleHourOfDay(startHour);
			this.calendar.setLastVisibleHourOfDay(endHour);
    	} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Eventos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
    }

	@Override
	public void enter(ViewChangeEvent event) {
		this.setModule(SystemModule.GENERAL);
		
		if((event.getParameters() != null) && (!event.getParameters().isEmpty())){
			try{
				SystemModule module = SystemModule.valueOf(Integer.parseInt(event.getParameters()));
				
				this.setModule(module);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

}
