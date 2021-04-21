package br.edu.utfpr.dv.siacoes.ui.views;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.windows.EventCalendarWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Calendário de Eventos")
@Route(value = "eventcalendar", layout = MainLayout.class)
public class EventCalendarView extends LoggedView {

	private static final String ONLYTHESIS = "Listar apenas bancas de TCC";
	private static final String ONLYINTERNSHIP = "Listar apenas bancas de Estágio";
	private static final String LISTALL = "Listar todas as bancas";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Checkbox checkListOnlyMy;
	private final Checkbox checkListPreScheculing;
	private final RadioButtonGroup<String> optionFilterType;
	private final Button buttonFilter;
	private final Details panelFilter;
	private final Button buttonPrevious;
	private final Button buttonNext;
	private final Button buttonToday;
	
	private final FullCalendar calendar;
	
	public EventCalendarView() {
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.optionFilterType = new RadioButtonGroup<String>();
		this.optionFilterType.setItems(ONLYTHESIS, ONLYINTERNSHIP, LISTALL);
		this.optionFilterType.setValue(LISTALL);
		this.optionFilterType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setValue(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.checkListOnlyMy = new Checkbox("Listar apenas minhas bancas");
		
		this.checkListPreScheculing = new Checkbox("Incluir minhas bancas de TCC pré-agendadas");
		
		this.buttonFilter = new Button("Filtrar", new Icon(VaadinIcon.FILTER), event -> {
            loadEvents();
        });
		this.buttonFilter.setWidth("150px");
		
		VerticalLayout vl2 = new VerticalLayout(this.checkListOnlyMy, this.checkListPreScheculing);
		vl2.setSpacing(false);
		vl2.setMargin(false);
		vl2.setPadding(false);
		
		VerticalLayout vl1 = new VerticalLayout(this.optionFilterType);
		vl1.setSpacing(false);
		vl1.setMargin(false);
		vl1.setPadding(false);
		vl1.setMinWidth("300px");
		vl1.setMaxWidth("300px");
		
		HorizontalLayout layoutFields = new HorizontalLayout(vl1, this.comboSemester, this.textYear, vl2);
		layoutFields.setSpacing(true);
		layoutFields.setMargin(false);
		layoutFields.setPadding(false);
		layoutFields.setSizeFull();
		
		VerticalLayout layoutFilter = new VerticalLayout(layoutFields, this.buttonFilter);
		layoutFilter.setSpacing(false);
		layoutFilter.setMargin(false);
		layoutFilter.setPadding(false);
		
		this.panelFilter = new Details();
		this.panelFilter.setSummaryText("Filtros");
		this.panelFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelFilter.setOpened(true);
		this.panelFilter.getElement().getStyle().set("width", "100%");
		this.panelFilter.setContent(layoutFilter);
		
		this.calendar = FullCalendarBuilder.create().build();
		this.calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
		this.calendar.today();
		this.calendar.setBusinessHours(new BusinessHours(LocalTime.of(7, 0), LocalTime.of(23, 0)));
		this.calendar.addEntryClickedListener(event -> {
			EventCalendarWindow window = new EventCalendarWindow(event.getEntry());
			window.open();
		});
		
		this.buttonPrevious = new Button("Semana Anterior", new Icon(VaadinIcon.ARROW_LEFT), event -> {
			this.calendar.previous();
		});
		this.buttonPrevious.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		this.buttonToday = new Button("Semana Atual", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
			this.calendar.today();
		});
		this.buttonToday.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		this.buttonNext = new Button("Próxima Semana", new Icon(VaadinIcon.ARROW_RIGHT), event -> {
			this.calendar.next();
		});
		this.buttonNext.addThemeVariants(ButtonVariant.LUMO_SMALL);
		this.buttonNext.setIconAfterText(true);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.addAndExpand(this.buttonPrevious, this.buttonToday, this.buttonNext);
		hl.setMargin(false);
		hl.setPadding(false);
		hl.setSpacing(false);
		
		VerticalLayout vl = new VerticalLayout(this.panelFilter, hl, this.calendar);
		vl.setSizeFull();
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		vl.expand(this.calendar);
		this.setViewContent(vl);
		
		if(Session.isUserStudent()){
			this.checkListOnlyMy.setVisible(false);
		}
		if(!AppConfig.getInstance().isSigetEnabled() || !AppConfig.getInstance().isSigesEnabled()) {
			this.optionFilterType.setVisible(false);
			
			if(AppConfig.getInstance().isSigetEnabled()) {
				this.optionFilterType.setValue(ONLYTHESIS);
			} else {
				this.optionFilterType.setValue(ONLYINTERNSHIP);
			}
		}
		
		this.loadEvents();
	}
	
	private void loadEvents() {
		try {
			List<Jury> listThesis = new ArrayList<Jury>();
			List<JuryRequest> listRequest = new ArrayList<JuryRequest>();
			List<InternshipJury> listInternship = new ArrayList<InternshipJury>();
			List<InternshipJuryRequest> listInternshipRequest = new ArrayList<InternshipJuryRequest>();
			LocalDateTime firstDate = null;
			
			this.calendar.removeAllEntries();
			
			if(this.optionFilterType.getValue().equals(ONLYTHESIS) || this.optionFilterType.getValue().equals(LISTALL)) {
				if(this.checkListOnlyMy.getValue()) {
					listThesis = new JuryBO().listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
					
					if(this.checkListPreScheculing.getValue()) {
						List<JuryRequest> list = new JuryRequestBO().listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
						
						for(JuryRequest request : list) {
							if((request.getJury() == null) || (request.getJury().getIdJury() == 0)) {
								listRequest.add(request);
							}
						}
					}
				} else {
					listThesis = new JuryBO().listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), 0);
				}
			}
			
			if(this.optionFilterType.getValue().equals(ONLYINTERNSHIP) || this.optionFilterType.getValue().equals(LISTALL)) {
				if(this.checkListOnlyMy.getValue()) {
					listInternship = new InternshipJuryBO().listByAppraiser(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
					
					if(this.checkListPreScheculing.getValue()) {
						List<InternshipJuryRequest> list = new InternshipJuryRequestBO().listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
						
						for(InternshipJuryRequest request : list) {
							if((request.getInternshipJury() == null) || (request.getInternshipJury().getIdInternshipJury() == 0)) {
								listInternshipRequest.add(request);
							}
						}
					}
				} else {
					listInternship = new InternshipJuryBO().listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
			this.calendar.today();
			
			if((listThesis.size() == 0) && (listRequest.size() == 0) && (listInternship.size() == 0) && (listInternshipRequest.size() == 0)) {
				this.showWarningNotification("Listar Eventos", "Não há bancas agendadas para este semestre.");
				this.calendar.today();
			} else {
				if(listThesis.size() > 0) {
					firstDate = DateUtils.convertToLocalDateTime(listThesis.get(0).getDate());
				}
				
				for(Jury jury : listThesis){
					String title = "Banca de TCC " + String.valueOf(jury.getStage());
					String student = "Acadêmico(a): " + jury.getStudent().getName();
					String local = "Local: " + jury.getLocal();
					String appraisers = "Membros da banca: ";
					Date endTime;
					
					jury.setAppraisers(new JuryAppraiserBO().listAppraisers(jury.getIdJury()));
					
					for(JuryAppraiser appraiser : jury.getAppraisers()) {
						appraisers += appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "; ";
					}
					
					try {
						SigetConfig config = new SigetConfigBO().findByDepartment(new JuryBO().findIdDepartment(jury.getIdJury()));
						
						if(jury.getStage() == 2) {
							endTime = DateUtils.addMinute(jury.getDate(), config.getJuryTimeStage2());
						} else {
							endTime = DateUtils.addMinute(jury.getDate(), config.getJuryTimeStage1());
						}
					} catch (Exception e) {
						endTime = DateUtils.addMinute(jury.getDate(), 60);
					}
					
					Entry entry = new Entry();
					entry.setTitle(title);
					entry.setStart(DateUtils.convertToLocalDateTime(jury.getDate()));
					entry.setEnd(DateUtils.convertToLocalDateTime(endTime));
					entry.setDescription(student + "\n" + local + "\n" + appraisers);
					entry.setColor("green");
					entry.setEditable(false);
					
					this.calendar.addEntry(entry);
				}
				
				if(listRequest.size() > 0) {
					if((firstDate == null) || (firstDate.isAfter(DateUtils.convertToLocalDateTime(listRequest.get(0).getDate())))) {
						firstDate = DateUtils.convertToLocalDateTime(listRequest.get(0).getDate());
					}
				}
				
				for(JuryRequest request : listRequest) {
					String title = "Solicitação de Banca de TCC " + String.valueOf(request.getStage());
					String student = "Acadêmico(a): " + request.getStudent();
					String local = "Local: " + request.getLocal();
					String appraisers = "Membros da banca: ";
					Date endTime;
					
					request.setAppraisers(new JuryAppraiserRequestBO().listAppraisers(request.getIdJuryRequest()));
					
					for(JuryAppraiserRequest appraiser : request.getAppraisers()) {
						appraisers += appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "; ";
					}
					
					try {
						SigetConfig config = new SigetConfigBO().findByDepartment(new ProposalBO().findIdDepartment(request.getProposal().getIdProposal()));
						
						if(request.getStage() == 2) {
							endTime = DateUtils.addMinute(request.getDate(), config.getJuryTimeStage2());
						} else {
							endTime = DateUtils.addMinute(request.getDate(), config.getJuryTimeStage1());
						}
					} catch (Exception e) {
						endTime = DateUtils.addMinute(request.getDate(), 60);
					}
					
					Entry entry = new Entry();
					entry.setTitle(title);
					entry.setStart(DateUtils.convertToLocalDateTime(request.getDate()));
					entry.setEnd(DateUtils.convertToLocalDateTime(endTime));
					entry.setDescription(student + "\n" + local + "\n" + appraisers);
					entry.setColor("red");
					entry.setEditable(false);
					
					this.calendar.addEntry(entry);
				}
				
				if(listInternship.size() > 0) {
					if((firstDate == null) || (firstDate.isAfter(DateUtils.convertToLocalDateTime(listInternship.get(0).getDate())))) {
						firstDate = DateUtils.convertToLocalDateTime(listInternship.get(0).getDate());
					}
				}
				
				for(InternshipJury jury : listInternship) {
					String title = "Banca de Estágio";
					String student = "Acadêmico(a): " + jury.getStudent();
					String local = "Local: " + jury.getLocal();
					String appraisers = "Membros da banca: ";
					Date endTime;
					
					jury.setAppraisers(new InternshipJuryAppraiserBO().listAppraisers(jury.getIdInternshipJury()));
					
					for(InternshipJuryAppraiser appraiser : jury.getAppraisers()) {
						appraisers += appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "; ";
					}
					
					try {
						SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(jury.getInternship().getIdInternship()));
						
						endTime = DateUtils.addMinute(jury.getDate(), config.getJuryTime());
					} catch (Exception e) {
						endTime = DateUtils.addMinute(jury.getDate(), 60);
					}
					
					Entry entry = new Entry();
					entry.setTitle(title);
					entry.setStart(DateUtils.convertToLocalDateTime(jury.getDate()));
					entry.setEnd(DateUtils.convertToLocalDateTime(endTime));
					entry.setDescription(student + "\n" + local + "\n" + appraisers);
					entry.setColor("blue");
					entry.setEditable(false);
					
					this.calendar.addEntry(entry);
				}
				
				if(listInternshipRequest.size() > 0) {
					if((firstDate == null) || (firstDate.isAfter(DateUtils.convertToLocalDateTime(listInternshipRequest.get(0).getDate())))) {
						firstDate = DateUtils.convertToLocalDateTime(listInternshipRequest.get(0).getDate());
					}
				}
				
				for(InternshipJuryRequest request : listInternshipRequest) {
					String title = "Solicitação de Banca de Estágio";
					String student = "Acadêmico(a): " + request.getInternship().getStudent();
					String local = "Local: " + request.getLocal();
					String appraisers = "Membros da banca: ";
					Date endTime;
					
					request.setAppraisers(new InternshipJuryAppraiserRequestBO().listAppraisers(request.getIdInternshipJuryRequest()));
					
					for(InternshipJuryAppraiserRequest appraiser : request.getAppraisers()) {
						appraisers += appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "; ";
					}
					
					try {
						SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(request.getInternship().getIdInternship()));
						
						endTime = DateUtils.addMinute(request.getDate(), config.getJuryTime());
					} catch (Exception e) {
						endTime = DateUtils.addMinute(request.getDate(), 60);
					}
					
					Entry entry = new Entry();
					entry.setTitle(title);
					entry.setStart(DateUtils.convertToLocalDateTime(request.getDate()));
					entry.setEnd(DateUtils.convertToLocalDateTime(endTime));
					entry.setDescription(student + "\n" + local + "\n" + appraisers);
					entry.setColor("yellow");
					entry.setEditable(false);
					
					this.calendar.addEntry(entry);
				}
				
				if((firstDate != null) && (firstDate.isAfter(LocalDateTime.now()))) {
					this.calendar.gotoDate(firstDate.toLocalDate());
				} else {
					this.calendar.today();
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Eventos", e.getMessage());
		}
	}
	
}
