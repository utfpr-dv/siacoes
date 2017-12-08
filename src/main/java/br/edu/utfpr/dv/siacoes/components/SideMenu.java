package br.edu.utfpr.dv.siacoes.components;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.view.ActivityGroupView;
import br.edu.utfpr.dv.siacoes.view.ActivitySubmissionView;
import br.edu.utfpr.dv.siacoes.view.ActivityUnitView;
import br.edu.utfpr.dv.siacoes.view.ActivityValidationReportView;
import br.edu.utfpr.dv.siacoes.view.ActivityView;
import br.edu.utfpr.dv.siacoes.view.AttendanceReportView;
import br.edu.utfpr.dv.siacoes.view.AttendanceView;
import br.edu.utfpr.dv.siacoes.view.BugReportView;
import br.edu.utfpr.dv.siacoes.view.CalendarView;
import br.edu.utfpr.dv.siacoes.view.CampusView;
import br.edu.utfpr.dv.siacoes.view.CityView;
import br.edu.utfpr.dv.siacoes.view.CompanySupervisorView;
import br.edu.utfpr.dv.siacoes.view.CompanyView;
import br.edu.utfpr.dv.siacoes.view.CountryView;
import br.edu.utfpr.dv.siacoes.view.DeadlineView;
import br.edu.utfpr.dv.siacoes.view.DepartmentView;
import br.edu.utfpr.dv.siacoes.view.DocumentView;
import br.edu.utfpr.dv.siacoes.view.EmailMessageView;
import br.edu.utfpr.dv.siacoes.view.EvaluationItemView;
import br.edu.utfpr.dv.siacoes.view.EventCalendarView;
import br.edu.utfpr.dv.siacoes.view.FinalDocumentView;
import br.edu.utfpr.dv.siacoes.view.InternshipCalendarView;
import br.edu.utfpr.dv.siacoes.view.InternshipCompanyChartView;
import br.edu.utfpr.dv.siacoes.view.InternshipEvaluationItemView;
import br.edu.utfpr.dv.siacoes.view.InternshipLibraryView;
import br.edu.utfpr.dv.siacoes.view.InternshipMissingDocumentsReportView;
import br.edu.utfpr.dv.siacoes.view.InternshipView;
import br.edu.utfpr.dv.siacoes.view.LibraryView;
import br.edu.utfpr.dv.siacoes.view.LoginView;
import br.edu.utfpr.dv.siacoes.view.MainView;
import br.edu.utfpr.dv.siacoes.view.PDFView;
import br.edu.utfpr.dv.siacoes.view.ProjectView;
import br.edu.utfpr.dv.siacoes.view.ProposalFeedbackStudentView;
import br.edu.utfpr.dv.siacoes.view.ProposalFeedbackView;
import br.edu.utfpr.dv.siacoes.view.ProposalView;
import br.edu.utfpr.dv.siacoes.view.SemesterView;
import br.edu.utfpr.dv.siacoes.view.StateView;
import br.edu.utfpr.dv.siacoes.view.StudentActivityStatusReportView;
import br.edu.utfpr.dv.siacoes.view.StudentView;
import br.edu.utfpr.dv.siacoes.view.SupervisorChangeView;
import br.edu.utfpr.dv.siacoes.view.SupervisorView;
import br.edu.utfpr.dv.siacoes.view.ThemeSuggestionView;
import br.edu.utfpr.dv.siacoes.view.ThesisView;
import br.edu.utfpr.dv.siacoes.view.TutoredView;
import br.edu.utfpr.dv.siacoes.view.UserView;
import br.edu.utfpr.dv.siacoes.window.AboutWindow;
import br.edu.utfpr.dv.siacoes.window.DownloadFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditAppConfigWindow;
import br.edu.utfpr.dv.siacoes.window.EditFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.window.EditPasswordWindow;
import br.edu.utfpr.dv.siacoes.window.EditProjectWindow;
import br.edu.utfpr.dv.siacoes.window.EditProposalWindow;
import br.edu.utfpr.dv.siacoes.window.EditSigacWindow;
import br.edu.utfpr.dv.siacoes.window.EditSigesWindow;
import br.edu.utfpr.dv.siacoes.window.EditSigetWindow;
import br.edu.utfpr.dv.siacoes.window.EditSupervisorWindow;
import br.edu.utfpr.dv.siacoes.window.EditThesisWindow;
import br.edu.utfpr.dv.siacoes.window.EditUserWindow;

public class SideMenu extends CustomComponent {
	
	public static final String ID = "dashboard-menu";
    public static final String REPORTS_BADGE_ID = "dashboard-menu-reports-badge";
    public static final String NOTIFICATIONS_BADGE_ID = "dashboard-menu-notifications-badge";
    private static final String STYLE_VISIBLE = "valo-menu-visible";
    
    public enum SideMenuState{
    	EXPANDED(0), COLLAPSED(1);
    	
    	private final int value; 
    	SideMenuState(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static SideMenuState valueOf(int value){
			for(SideMenuState u : SideMenuState.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
    }
    
    private SigetConfig sigetConfig;
	private SigacConfig sigacConfig;
	private SigesConfig sigesConfig;
	private Semester semester;

	private MenuItem settingsItem;
	private Accordion accordionMenu;
	private SideMenuState state;
	
	private VerticalLayout layoutCollapsed;
	private VerticalLayout layoutExpanded;
	private CssLayout menuContent;
	
	private SystemModule openMenu = SystemModule.GENERAL;
	
	public SideMenu() {
        setPrimaryStyleName("valo-menu");
        setId(ID);
        setSizeUndefined();
        
        this.semester = new Semester();
		try {
			this.semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e1) {
			Notification.show("Semestre", e1.getMessage(), Notification.Type.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
		}
        
		SigetConfigBO bo = new SigetConfigBO();
		this.sigetConfig = new SigetConfig();
		try {
			this.sigetConfig = bo.findByDepartment(Session.getUser().getDepartment().getIdDepartment());
		} catch (Exception e1) {
			Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
		}
		
        SigacConfigBO bo2 = new SigacConfigBO();
		this.sigacConfig = new SigacConfig();
		try {
			this.sigacConfig = bo2.findByDepartment(Session.getUser().getDepartment().getIdDepartment());
		} catch (Exception e1) {
			Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		SigesConfigBO bo3 = new SigesConfigBO();
		this.sigesConfig = new SigesConfig();
		try {
			this.sigesConfig = bo3.findByDepartment(Session.getUser().getDepartment().getIdDepartment());
		} catch (Exception e1) {
			Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
		}

        setCompositionRoot(buildContent());
        
        
        this.setMenuState(this.getMenuStateFromCookie());
    }
	
	public void setOpenMenu(SystemModule module){
		this.openMenu = module;
		
		switch(module){
			case SIGAC:
				this.accordionMenu.setSelectedTab(0);
				break;
			case SIGES:
				this.accordionMenu.setSelectedTab(1);
				break;
			case SIGET:
				this.accordionMenu.setSelectedTab(2);
				break;
			default:
				this.accordionMenu.setSelectedTab(3);
		}
	}
	
	public SystemModule getOpenMenu(){
		return this.openMenu;
	}
	
	public SideMenuState getMenuState(){
		return this.state;
	}
	
	public void setMenuState(SideMenuState state){
		this.state = state;
		this.setMenuStateToCookie(this.state);
		
		this.layoutExpanded.setVisible(this.getMenuState() == SideMenuState.EXPANDED);
		this.layoutCollapsed.setVisible(this.getMenuState() == SideMenuState.COLLAPSED);
		
		if(this.getMenuState() == SideMenuState.EXPANDED){
			this.menuContent.setWidth("220px");
		}else{
			this.menuContent.setWidth("50px");
		}
	}
	
	public void toggleMenu(){
		if(this.getMenuState() == SideMenuState.COLLAPSED){
			this.setMenuState(SideMenuState.EXPANDED);
		}else{
			this.setMenuState(SideMenuState.COLLAPSED);
		}
	}
	
	private SideMenuState getMenuStateFromCookie(){
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		
		for (Cookie cookie : cookies) {
			if ("menuState".equals(cookie.getName())) {
				return SideMenuState.valueOf(Integer.valueOf(cookie.getValue()));
		    }
		}

		return SideMenuState.EXPANDED;
	}
	
	private void setMenuStateToCookie(SideMenuState state){
		Cookie cookie = new Cookie("menuState", String.valueOf(state.getValue()));
		
		cookie.setMaxAge(60 * 24 * 3600);
		cookie.setPath("/");
		
		VaadinService.getCurrentResponse().addCookie(cookie);
	}
	
	private Component buildContent() {
		this.menuContent = new CssLayout();
		this.menuContent.addStyleName("sidebar");
		this.menuContent.addStyleName(ValoTheme.MENU_PART);
		this.menuContent.addStyleName("no-vertical-drag-hints");
		this.menuContent.addStyleName("no-horizontal-drag-hints");
		this.menuContent.setWidth("220px");
		this.menuContent.setHeight("100%");
        
        this.accordionMenu = new Accordion();
        
        this.accordionMenu.addTab(this.buildMenuActivities(), "Atividades Complementares");
        this.accordionMenu.getTab(0).setVisible(AppConfig.getInstance().isSigacEnabled());
        
        this.accordionMenu.addTab(this.buildMenuInternship(), "Estágios");
        this.accordionMenu.getTab(1).setVisible(AppConfig.getInstance().isSigesEnabled());
        
        this.accordionMenu.addTab(this.buildMenuThesis(), "TCC");
        this.accordionMenu.getTab(2).setVisible(AppConfig.getInstance().isSigetEnabled());
        
        this.accordionMenu.addTab(this.buildMenuManagement(), "Recursos Gerais");
        
        this.menuContent.addComponent(this.accordionMenu);
        
        this.layoutExpanded = new VerticalLayout();
        this.layoutExpanded.addComponent(this.buildUserMenu());
        this.layoutExpanded.addComponent(this.buildLinkMenu());
        this.layoutExpanded.addComponent(this.accordionMenu);
        
        this.menuContent.addComponent(this.layoutExpanded);
        
        this.layoutCollapsed = new VerticalLayout();
        this.layoutCollapsed.addComponent(this.buildLinkMenuCollapsed());
        
        this.menuContent.addComponent(this.layoutCollapsed);
        
        return this.menuContent;
    }
	
	private MenuBar buildUserMenu() {
        final MenuBar settings = new MenuBar();
        final User user = Session.getUser();
        
        settings.addStyleName("user-menu");
        
        settingsItem = settings.addItem("", new ThemeResource("images/profile-pic-300px.jpg"), null);
        settingsItem.setText(user.getName());
        
        if(user.getPhoto() != null){
        	StreamResource resource = new StreamResource(
	            new StreamResource.StreamSource() {
	                @Override
	                public InputStream getStream() {
	                    return new ByteArrayInputStream(user.getPhoto());
	                }
	            }, "userphoto" + String.valueOf(user.getPhoto()) + ".jpg");
	
    		settingsItem.setIcon(resource);
        }
        
        settingsItem.addItem("Meus Dados", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
            	UI.getCurrent().addWindow(new EditUserWindow(Session.getUser(), null));
            }
        });
        if(Session.getUser().isExternal() && !Session.isLoggedAs()){
	        settingsItem.addItem("Alterar Senha", new Command() {
	            @Override
	            public void menuSelected(final MenuItem selectedItem) {
	            	UI.getCurrent().addWindow(new EditPasswordWindow());
	            }
	        });
        }
        if(Session.isLoggedAs()){
        	settingsItem.addSeparator();
            settingsItem.addItem("Logoff de " + Session.getUser().getName(), new Command() {
                @Override
                public void menuSelected(final MenuItem selectedItem) {
                    Session.logoffAs();
                    
                    getUI().getNavigator().navigateTo(MainView.NAME);
                }
            });
        }
        settingsItem.addSeparator();
        settingsItem.addItem("Logoff", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Session.logoff();
                
                getUI().getNavigator().navigateTo(LoginView.NAME);
            }
        });
        
        return settings;
    }
	
	private Component buildLinkMenu(){
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		
		if(AppConfig.getInstance().isSigesEnabled() || AppConfig.getInstance().isSigetEnabled()){
			Link linkCalendar = new Link(null, null);
			linkCalendar.setIcon(new ThemeResource("images/calendar.png"));
			linkCalendar.setDescription("Calendário de eventos");
			
			VerticalLayout layoutCalendar = new VerticalLayout(linkCalendar);
			layoutCalendar.setHeight("50px");
			layoutCalendar.setWidth("50px");
			layoutCalendar.setComponentAlignment(linkCalendar, Alignment.MIDDLE_CENTER);
			layoutCalendar.addLayoutClickListener(new LayoutClickListener() {
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					UI.getCurrent().getNavigator().navigateTo(EventCalendarView.NAME + "/" + getOpenMenu().getValue());
	            }
			});
			
			layout.addComponent(layoutCalendar);
			layout.setComponentAlignment(layoutCalendar, Alignment.MIDDLE_CENTER);
		}
		
		if(AppConfig.getInstance().isSigacEnabled()){
			Link linkActivities = new Link(null, null);
			linkActivities.setIcon(new ThemeResource("images/activities.png"));
			linkActivities.setDescription("Atividades Complementares");
			
			VerticalLayout layoutActivities = new VerticalLayout(linkActivities);
			layoutActivities.setHeight("50px");
			layoutActivities.setWidth("50px");
			layoutActivities.setComponentAlignment(linkActivities, Alignment.MIDDLE_CENTER);
			layoutActivities.addLayoutClickListener(new LayoutClickListener() {
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					setOpenMenu(SystemModule.SIGAC);
	            }
			});
			
			layout.addComponent(layoutActivities);
			layout.setComponentAlignment(layoutActivities, Alignment.MIDDLE_CENTER);
		}
		
		if(AppConfig.getInstance().isSigesEnabled()){
			Link linkInternsip = new Link(null, null);
			linkInternsip.setIcon(new ThemeResource("images/internship.png"));
			linkInternsip.setDescription("Estágios");
			
			VerticalLayout layoutInternship = new VerticalLayout(linkInternsip);
			layoutInternship.setHeight("50px");
			layoutInternship.setWidth("50px");
			layoutInternship.setComponentAlignment(linkInternsip, Alignment.MIDDLE_CENTER);
			layoutInternship.addLayoutClickListener(new LayoutClickListener() {   
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					setOpenMenu(SystemModule.SIGES);
	            }
			});
			
			layout.addComponent(layoutInternship);
			layout.setComponentAlignment(layoutInternship, Alignment.MIDDLE_CENTER);
		}
		
		if(AppConfig.getInstance().isSigetEnabled()){
			Link linkThesis = new Link(null, null);
			linkThesis.setIcon(new ThemeResource("images/thesis.png"));
			linkThesis.setDescription("TCC");
			
			VerticalLayout layoutThesis = new VerticalLayout(linkThesis);
			layoutThesis.setHeight("50px");
			layoutThesis.setWidth("50px");
			layoutThesis.setComponentAlignment(linkThesis, Alignment.MIDDLE_CENTER);
			layoutThesis.addLayoutClickListener(new LayoutClickListener() {
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					setOpenMenu(SystemModule.SIGET);
	            }
			});
			
			layout.addComponent(layoutThesis);
			layout.setComponentAlignment(layoutThesis, Alignment.MIDDLE_CENTER);
		}
		
		return layout;
	}
	
	private Component buildLinkMenuCollapsed(){
		VerticalLayout layout = new VerticalLayout();
		
		MenuBar settings = this.buildUserMenu();
		settings.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
		settings.addStyleName(ValoTheme.MENUBAR_SMALL);
		settings.addStyleName("withouarrow");
		settings.setHeight("50px");
		settings.setWidth("50px");
		settings.getItems().get(0).setIcon(new ThemeResource("images/user.png"));
		settings.getItems().get(0).setText("");
		
		layout.addComponent(settings);
		layout.setComponentAlignment(settings, Alignment.MIDDLE_CENTER);
		
		if(AppConfig.getInstance().isSigesEnabled() || AppConfig.getInstance().isSigetEnabled()){
			Link linkCalendar = new Link(null, null);
			linkCalendar.setIcon(new ThemeResource("images/calendar.png"));
			linkCalendar.setDescription("Calendário de eventos");
			
			VerticalLayout layoutCalendar = new VerticalLayout(linkCalendar);
			layoutCalendar.setHeight("50px");
			layoutCalendar.setWidth("50px");
			layoutCalendar.setComponentAlignment(linkCalendar, Alignment.MIDDLE_CENTER);
			layoutCalendar.addLayoutClickListener(new LayoutClickListener() {
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					UI.getCurrent().getNavigator().navigateTo(EventCalendarView.NAME + "/" + getOpenMenu().getValue());
	            }
			});
			
			layout.addComponent(layoutCalendar);
			layout.setComponentAlignment(layoutCalendar, Alignment.MIDDLE_CENTER);
		}
		
		if(AppConfig.getInstance().isSigacEnabled()){
			Link linkActivities = new Link(null, null);
			linkActivities.setIcon(new ThemeResource("images/activities.png"));
			linkActivities.setDescription("Atividades Complementares");
			
			VerticalLayout layoutActivities = new VerticalLayout(linkActivities);
			layoutActivities.setHeight("50px");
			layoutActivities.setWidth("50px");
			layoutActivities.setComponentAlignment(linkActivities, Alignment.MIDDLE_CENTER);
			layoutActivities.addLayoutClickListener(new LayoutClickListener() {
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					setOpenMenu(SystemModule.SIGAC);
					setMenuState(SideMenuState.EXPANDED);
					setMenuStateToCookie(SideMenuState.COLLAPSED);
	            }
			});
			
			layout.addComponent(layoutActivities);
			layout.setComponentAlignment(layoutActivities, Alignment.MIDDLE_CENTER);
		}
		
		if(AppConfig.getInstance().isSigesEnabled()){
			Link linkInternsip = new Link(null, null);
			linkInternsip.setIcon(new ThemeResource("images/internship.png"));
			linkInternsip.setDescription("Estágios");
			
			VerticalLayout layoutInternship = new VerticalLayout(linkInternsip);
			layoutInternship.setHeight("50px");
			layoutInternship.setWidth("50px");
			layoutInternship.setComponentAlignment(linkInternsip, Alignment.MIDDLE_CENTER);
			layoutInternship.addLayoutClickListener(new LayoutClickListener() {   
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					setOpenMenu(SystemModule.SIGES);
					setMenuState(SideMenuState.EXPANDED);
					setMenuStateToCookie(SideMenuState.COLLAPSED);
	            }
			});
			
			layout.addComponent(layoutInternship);
			layout.setComponentAlignment(layoutInternship, Alignment.MIDDLE_CENTER);
		}
		
		if(AppConfig.getInstance().isSigetEnabled()){
			Link linkThesis = new Link(null, null);
			linkThesis.setIcon(new ThemeResource("images/thesis.png"));
			linkThesis.setDescription("TCC");
			
			VerticalLayout layoutThesis = new VerticalLayout(linkThesis);
			layoutThesis.setHeight("50px");
			layoutThesis.setWidth("50px");
			layoutThesis.setComponentAlignment(linkThesis, Alignment.MIDDLE_CENTER);
			layoutThesis.addLayoutClickListener(new LayoutClickListener() {
				@Override
	        	public void layoutClick(LayoutClickEvent event) {
					setOpenMenu(SystemModule.SIGET);
					setMenuState(SideMenuState.EXPANDED);
					setMenuStateToCookie(SideMenuState.COLLAPSED);
	            }
			});
			
			layout.addComponent(layoutThesis);
			layout.setComponentAlignment(layoutThesis, Alignment.MIDDLE_CENTER);
		}
		
		Link linkConfig = new Link(null, null);
		linkConfig.setIcon(new ThemeResource("images/config.png"));
		linkConfig.setDescription("Recursos Gerais");
		
		VerticalLayout layoutConfig = new VerticalLayout(linkConfig);
		layoutConfig.setHeight("50px");
		layoutConfig.setWidth("50px");
		layoutConfig.setComponentAlignment(linkConfig, Alignment.MIDDLE_CENTER);
		layoutConfig.addLayoutClickListener(new LayoutClickListener() {
			@Override
        	public void layoutClick(LayoutClickEvent event) {
				setOpenMenu(SystemModule.GENERAL);
				setMenuState(SideMenuState.EXPANDED);
				setMenuStateToCookie(SideMenuState.COLLAPSED);
            }
		});
		
		layout.addComponent(layoutConfig);
		layout.setComponentAlignment(layoutConfig, Alignment.MIDDLE_CENTER);
		
		return layout;
	}
	
	private Component buildMenuActivities(){
		VerticalLayout layout = new VerticalLayout();
		
		if(Session.isUserManager(SystemModule.SIGAC) || Session.isUserStudent() || Session.isUserDepartmentManager()){
			layout.addComponent(new MenuEntry("Registro de Atividades", 0));
			
			layout.addComponent(new MenuEntry((Session.isUserManager(SystemModule.SIGAC) ? "Validar Atividades" : (Session.isUserStudent() ? "Atividades Registradas" : "Atividades Submetidas")), 1, ActivitySubmissionView.NAME));
		}
		if(Session.isUserAdministrator() || Session.isUserManager(SystemModule.SIGAC)){
			layout.addComponent(new MenuEntry("Administração", 0));
			
			if(Session.isUserAdministrator()){
				layout.addComponent(new MenuEntry("Grupos de Atividades", 1, ActivityGroupView.NAME));
				layout.addComponent(new MenuEntry("Unidades de Atividades", 1, ActivityUnitView.NAME));
			}
			if(Session.isUserManager(SystemModule.SIGAC)){
				layout.addComponent(new MenuEntry("Atividades", 1, ActivityView.NAME));
				layout.addComponent(new MenuEntry("Configurações", 1, new EditSigacWindow(sigacConfig, null)));
			}
		}
		
		layout.addComponent(new MenuEntry("Repositório", 0));
		layout.addComponent(new MenuEntry("Regulamentos e Anexos", 1, DocumentView.NAME + "/" + String.valueOf(SystemModule.SIGAC.getValue())));
		
		if(Session.isUserManager(SystemModule.SIGAC) || Session.isUserDepartmentManager()){
			layout.addComponent(new MenuEntry("Relatórios", 0));
			layout.addComponent(new MenuEntry("Validação de Atividades", 1, ActivityValidationReportView.NAME));
			layout.addComponent(new MenuEntry("Situação do Acadêmico", 1, StudentActivityStatusReportView.NAME));
		}
		
		layout.setSizeFull();
		
		return layout;
	}
	
	private Component buildMenuInternship(){
		VerticalLayout layout = new VerticalLayout();
		
		if(Session.isUserManager(SystemModule.SIGES)){
			layout.addComponent(new MenuEntry("Cadastros", 0));
			
			layout.addComponent(new MenuEntry("Países", 1, CountryView.NAME));
			layout.addComponent(new MenuEntry("Estados", 1, StateView.NAME));
			layout.addComponent(new MenuEntry("Cidades", 1, CityView.NAME));
			layout.addComponent(new MenuEntry("Empresas", 1, CompanyView.NAME));
			layout.addComponent(new MenuEntry("Supervisores", 1, CompanySupervisorView.NAME));
			layout.addComponent(new MenuEntry("Alunos", 1, StudentView.NAME));
		}
		
		layout.addComponent(new MenuEntry("Estágio", 0));
		if(Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()){
			layout.addComponent(new MenuEntry("Registro de Estágio", 1, InternshipView.NAME + "/1"));
		}
		if(Session.isUserProfessor()){
			layout.addComponent(new MenuEntry("Meus Orientados", 1, InternshipView.NAME));
		}else if(Session.isUserStudent()){
			layout.addComponent(new MenuEntry("Meus Estágios", 1, InternshipView.NAME));
		}
		
		layout.addComponent(new MenuEntry("Bancas", 0));
		layout.addComponent(new MenuEntry("Agenda de Bancas", 1, InternshipCalendarView.NAME + "/1"));
		layout.addComponent(new MenuEntry((Session.isUserProfessor() ? "Minhas Bancas" : "Bancas que Assisti"), 1, InternshipCalendarView.NAME));
		
		if(Session.isUserManager(SystemModule.SIGES)){
			layout.addComponent(new MenuEntry("Administração", 0));
			
			layout.addComponent(new MenuEntry("Quesitos de Avaliação", 1, InternshipEvaluationItemView.NAME));
			layout.addComponent(new MenuEntry("Configurações", 1, new EditSigesWindow(sigesConfig, null)));
		}
		
		layout.addComponent(new MenuEntry("Repositório", 0));
		layout.addComponent(new MenuEntry("Regulamentos e Anexos", 1, DocumentView.NAME + "/" + String.valueOf(SystemModule.SIGES.getValue())));
		layout.addComponent(new MenuEntry("Biblioteca", 1, InternshipLibraryView.NAME));
		
		if(Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()){
			layout.addComponent(new MenuEntry("Relatórios", 0));
			layout.addComponent(new MenuEntry("Documentos Faltantes", 1, InternshipMissingDocumentsReportView.NAME));
			
			layout.addComponent(new MenuEntry("Gráficos", 0));
			layout.addComponent(new MenuEntry("Estagiários por Empresa", 1, InternshipCompanyChartView.NAME));
		}
		
		layout.setSizeFull();
		
		return layout;
	}
	
	private Component buildMenuThesis(){
		VerticalLayout layout = new VerticalLayout();
		
		if(this.sigetConfig.isRegisterProposal()){
			layout.addComponent(new MenuEntry("Proposta de TCC 1", 0));
			
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()){
				layout.addComponent(new MenuEntry("Listar Propostas", 1, ProposalView.NAME));
			}
			
			if(Session.isUserStudent()){
				layout.addComponent(new MenuEntry("Submeter Proposta", 1, new MenuEntryClickListener() {
					@Override
					public void menuClick() {
						try {
							ProposalBO bo = new ProposalBO();
							Proposal p = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if(p == null){
								DeadlineBO dbo = new DeadlineBO();
								Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
								
								if((d == null) || DateUtils.getToday().getTime().after(d.getProposalDeadline())){
									throw new Exception("A submissão de propostas já foi encerrada.");
								}
								
								p = new Proposal(Session.getUser());
							}
							
							UI.getCurrent().addWindow(new EditProposalWindow(p, null, true));
						} catch (Exception e) {
							Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
							
							Notification.show("Submeter Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
						}
					}
				}));
			}else if(Session.isUserProfessor()){
				layout.addComponent(new MenuEntry("Cadastrar Parecer", 1, ProposalFeedbackView.NAME));
			}
			
			if(Session.isUserProfessor() || Session.isUserStudent()){
				layout.addComponent(new MenuEntry("Parecer dos Avaliadores", 1, ProposalFeedbackStudentView.NAME));
			}
		}
		
		layout.addComponent(new MenuEntry("Projeto de TCC 1", 0));
		if(Session.isUserStudent()){
			layout.addComponent(new MenuEntry("Submeter Projeto", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						ProjectBO bo = new ProjectBO();
						Project p = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(p == null){
							DeadlineBO dbo = new DeadlineBO();
							Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if((d == null) || DateUtils.getToday().getTime().after(d.getProjectDeadline())){
								throw new Exception("A submissão de projetos já foi encerrada.");
							}
							
							ProposalBO pbo = new ProposalBO();
							Proposal proposal = pbo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if(proposal == null){
								if(sigetConfig.isRegisterProposal()){
									throw new Exception("Não foi encontrada a submissão da proposta. É necessário primeiramente submeter a proposta.");
								}else{
									throw new Exception("Não foi encontrada o registro de orientação. É necessário primeiramente registrar a orientação.");
								}
							}
							
							p = new Project(Session.getUser(), proposal);
						}
						
						UI.getCurrent().addWindow(new EditProjectWindow(p, null));
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Submeter Projeto", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
				
			layout.addComponent(new MenuEntry("Imprimir Documentos", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						ProjectBO bo = new ProjectBO();
						Project p = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(p == null){
							p = bo.findLastProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						}
						
						if(p == null){
							Notification.show("Imprimir Documentos", "É necessário submeter o projeto para imprimir os documentos.", Notification.Type.ERROR_MESSAGE);
						}else{
							AttendanceBO abo = new AttendanceBO();
							AttendanceReport attendance = abo.getReport(Session.getUser().getIdUser(), p.getProposal().getIdProposal(), p.getSupervisor().getIdUser(), 1);
							
							List<AttendanceReport> list = new ArrayList<AttendanceReport>();
							list.add(attendance);
							
							showReport(new ReportUtils().createPdfStream(list, "Attendances").toByteArray());
							
							List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
							list2.add(bo.getSupervisorFeedbackReport(p));
							
							showReport(new ReportUtils().createPdfStream(list2, "SupervisorFeedback").toByteArray());
						}
        	    	} catch (Exception e) {
        	    		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	    		
						Notification.show("Imprimir Documentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
			
			layout.addComponent(new MenuEntry("Feedback da Banca", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						ProjectBO bo = new ProjectBO();
						Project p = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(p == null){
							p = bo.findLastProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						}
						
						if(p == null){
							Notification.show("Feedback da Banca", "É necessário submeter o projeto para obter o feedback da banca examinadora.", Notification.Type.ERROR_MESSAGE);
						}else{
							JuryBO jbo = new JuryBO();
							Jury jury = jbo.findByProject(p.getIdProject());
							
							if(jury == null){
								Notification.show("Feedback da Banca", "A banca examinadora do projeto ainda não foi agendada.", Notification.Type.ERROR_MESSAGE);
							}else{
								UI.getCurrent().addWindow(new DownloadFeedbackWindow(jury));
							}
						}
        	    	} catch (Exception e) {
        	    		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	    		
						Notification.show("Feedback da Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
			
			layout.addComponent(new MenuEntry("Submeter Versão Final", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						FinalDocumentBO fbo = new FinalDocumentBO();
						FinalDocument ft = fbo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(ft == null){
							ProjectBO bo = new ProjectBO();
							Project project = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
							/*if(project == null){
								project = bo.findApprovedProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
							}*/
							
							if(project == null){
								throw new Exception("É necessário submeter o projeto para avaliação da banca antes.");
							}else{
								JuryBO jbo = new JuryBO();
								Jury jury = jbo.findByProject(project.getIdProject());
								
								if((jury == null) || (jury.getIdJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())){
									throw new Exception("A versão final do projeto só pode ser enviada após a realização da banca.");
								}else{
									ft = new FinalDocument();
									ft.setTitle(project.getTitle());
									ft.setProject(project);	
								}
							}
						}
						
						UI.getCurrent().addWindow(new EditFinalDocumentWindow(ft, null));
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Submeter Versão Final", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
		}else{
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()){
				layout.addComponent(new MenuEntry("Listar Projetos", 1, ProjectView.NAME));
			}
			
			layout.addComponent(new MenuEntry("Validar Versão Final", 1, FinalDocumentView.NAME));
    	}
		
		layout.addComponent(new MenuEntry("Monografia de TCC 2", 0));
		if(Session.isUserStudent()){
			layout.addComponent(new MenuEntry("Submeter Monografia", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						ThesisBO bo = new ThesisBO();
						Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(thesis == null){
							DeadlineBO dbo = new DeadlineBO();
							Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if((d == null) || DateUtils.getToday().getTime().after(d.getThesisDeadline())){
								throw new Exception("A submissão de monografias já foi encerrada.");
							}
							
							ProjectBO pbo = new ProjectBO();
							Project project = pbo.findApprovedProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if(project == null){
								throw new Exception("Não foi encontrada a submissão do projeto. É necessário submeter primeiramente o projeto.");
							}
							
							thesis = new Thesis(Session.getUser(), project);
						}
						
						UI.getCurrent().addWindow(new EditThesisWindow(thesis, null));
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Submeter Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
			
			layout.addComponent(new MenuEntry("Imprimir Documentos", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						ThesisBO bo = new ThesisBO();
						Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(thesis == null){
							thesis = bo.findLastThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						}
						
						if(thesis == null){
							Notification.show("Imprimir Documentos", "É necessário submeter a monografia para imprimir os documentos.", Notification.Type.ERROR_MESSAGE);
						}else{
							ProjectBO pbo = new ProjectBO();
							Project project = pbo.findById(thesis.getProject().getIdProject());
							
							AttendanceBO abo = new AttendanceBO();
							AttendanceReport attendance = abo.getReport(Session.getUser().getIdUser(), project.getProposal().getIdProposal(), thesis.getSupervisor().getIdUser(), 2);
							
							List<AttendanceReport> list = new ArrayList<AttendanceReport>();
							list.add(attendance);
							
							showReport(new ReportUtils().createPdfStream(list, "Attendances").toByteArray());
							
							List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
							list2.add(bo.getSupervisorFeedbackReport(thesis));
							
							showReport(new ReportUtils().createPdfStream(list2, "SupervisorFeedback").toByteArray());
						}
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Imprimir Documentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
			
			layout.addComponent(new MenuEntry("Feedback da Banca", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
        	    		ThesisBO bo = new ThesisBO();
						Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(thesis == null){
							thesis = bo.findLastThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						}
						
						if(thesis == null){
							Notification.show("Feedback da Banca", "É necessário submeter o projeto para obter o feedback da banca examinadora.", Notification.Type.ERROR_MESSAGE);
						}else{
							JuryBO jbo = new JuryBO();
							Jury jury = jbo.findByThesis(thesis.getIdThesis());
							
							if(jury == null){
								Notification.show("Feedback da Banca", "A banca examinadora do projeto ainda não foi agendada.", Notification.Type.ERROR_MESSAGE);
							}else{
								UI.getCurrent().addWindow(new DownloadFeedbackWindow(jury));
							}
						}
        	    	} catch (Exception e) {
        	    		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	    		
						Notification.show("Feedback da Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
			
			layout.addComponent(new MenuEntry("Submeter Versão Final", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						FinalDocumentBO fbo = new FinalDocumentBO();
						FinalDocument ft = fbo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(ft == null){
							ThesisBO bo = new ThesisBO();
							Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
							/*if(thesis == null){
								thesis = bo.findApprovedThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
							}*/
							
							if(thesis == null){
								throw new Exception("É necessário submeter a monografia para avaliação da banca antes.");
							}else{
								JuryBO jbo = new JuryBO();
								Jury jury = jbo.findByThesis(thesis.getIdThesis());
								
								if((jury == null) || (jury.getIdJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())){
									throw new Exception("A versão final da monografia só pode ser enviada após a realização da banca.");
								}else{
									ft = new FinalDocument();
									ft.setTitle(thesis.getTitle());
									ft.setThesis(thesis);
								}
							}
						}
						
						UI.getCurrent().addWindow(new EditFinalDocumentWindow(ft, null));
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Submeter Versão Final", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
		}else{
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()){
				layout.addComponent(new MenuEntry("Listar Monografias", 1, ThesisView.NAME));
			}
			
			layout.addComponent(new MenuEntry("Validar Versão Final", 1, FinalDocumentView.NAME));
    	}
		
		
		layout.addComponent(new MenuEntry("Orientação", 0));
		if(Session.isUserStudent()){
			layout.addComponent(new MenuEntry("Lista de Orientadores", 1, SupervisorView.NAME));
			layout.addComponent(new MenuEntry("Registrar Orientação", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
						ProposalBO bo = new ProposalBO();
						Proposal p = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						if(p == null){
							DeadlineBO dbo = new DeadlineBO();
							Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if((d == null) || DateUtils.getToday().getTime().after(d.getProposalDeadline())){
								throw new Exception("O registro de orientações já foi encerrado.");
							}
							
							p = new Proposal(Session.getUser());
						}
						
						UI.getCurrent().addWindow(new EditProposalWindow(p, null, false));
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Registrar Orientação", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
			layout.addComponent(new MenuEntry("Alterar Orientador", 1, new MenuEntryClickListener() {
				@Override
				public void menuClick() {
					try {
        	        	ProposalBO bo = new ProposalBO();
						Proposal proposal = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
						
						/*DeadlineBO dbo = new DeadlineBO();
						Deadline d;
						
						if(proposal == null){
							d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
						}else{
							d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), proposal.getSemester(), proposal.getYear());
						}*/
						
						if(proposal == null){
							Notification.show("Alterar Orientador", "É necessário efetuar a submissão da proposta.", Notification.Type.ERROR_MESSAGE);
						//}else if((d == null) || (!DateUtils.getToday().getTime().after(d.getProposalDeadline()))){
						//	Notification.show("Alterar Orientador", "A submissão de propostas ainda não foi encerrada. Você pode fazer a alteração do orientador no menu Submeter Proposta.", Notification.Type.ERROR_MESSAGE);
						}else{
							UI.getCurrent().addWindow(new EditSupervisorWindow(proposal, null));
						}
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						Notification.show("Alterar Orientador", e.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			}));
		}
		if(Session.isUserProfessor()){
			layout.addComponent(new MenuEntry("Meus Orientados", 1, TutoredView.NAME));
		}
		layout.addComponent(new MenuEntry("Registro de Reuniões", 1, AttendanceView.NAME));
		
		layout.addComponent(new MenuEntry("Bancas", 0));
		layout.addComponent(new MenuEntry("Agenda de Bancas", 1, CalendarView.NAME + "/1"));
		layout.addComponent(new MenuEntry((Session.isUserProfessor() ? "Minhas Bancas" : "Bancas que Assisti"), 1, CalendarView.NAME));
		
		if(Session.isUserManager(SystemModule.SIGET)){
			layout.addComponent(new MenuEntry("Administração", 0));
			layout.addComponent(new MenuEntry("Alterações de Orientador", 1, SupervisorChangeView.NAME));
			layout.addComponent(new MenuEntry("Definir Datas", 1, DeadlineView.NAME));
			layout.addComponent(new MenuEntry("Quesitos de Avaliação", 1, EvaluationItemView.NAME));
			layout.addComponent(new MenuEntry("Configurações", 1, new EditSigetWindow(sigetConfig, null)));
		}
		
		layout.addComponent(new MenuEntry("Repositório", 0));
		layout.addComponent(new MenuEntry("Regulamentos e Anexos", 1, DocumentView.NAME + "/" + String.valueOf(SystemModule.SIGET.getValue())));
		layout.addComponent(new MenuEntry("Biblioteca", 1, LibraryView.NAME));
		layout.addComponent(new MenuEntry("Sugestões de Projetos", 1, ThemeSuggestionView.NAME));
		
		if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()){
			layout.addComponent(new MenuEntry("Relatórios", 0));
			layout.addComponent(new MenuEntry("Reuniões de Orientação", 1, AttendanceReportView.NAME));
		}
		
		layout.setSizeFull();
		
		return layout;
	}
	
	private Component buildMenuManagement(){
		VerticalLayout layout = new VerticalLayout();
		
		if(Session.isUserAdministrator()){
			layout.addComponent(new MenuEntry("Administração", 0));
			layout.addComponent(new MenuEntry("Câmpus", 1, CampusView.NAME));
			layout.addComponent(new MenuEntry("Departamentos", 1, DepartmentView.NAME));
			layout.addComponent(new MenuEntry("Semestres", 1, SemesterView.NAME));
			layout.addComponent(new MenuEntry("Envio de E-mails", 1, EmailMessageView.NAME));
			layout.addComponent(new MenuEntry("Usuários", 1, UserView.NAME));
			layout.addComponent(new MenuEntry("Configurações", 1, new EditAppConfigWindow()));
		}
		
		layout.addComponent(new MenuEntry("Reportar Erro", 0, BugReportView.NAME));
		layout.addComponent(new MenuEntry("Sobre o Sistema", 0, new AboutWindow()));
		
		layout.setSizeFull();
		
		return layout;
	}
	
	private void showReport(byte[] pdfReport){
		String id = UUID.randomUUID().toString();
    	
		Session.putReport(pdfReport, id);
		
		getUI().getPage().open("#!" + PDFView.NAME + "/session/" + id, "_blank");
    }
	
}
