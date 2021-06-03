package br.edu.utfpr.dv.siacoes.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.Lumo;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.MessageBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.ui.components.FlexBoxLayout;
import br.edu.utfpr.dv.siacoes.ui.components.Notification;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.bar.AppBar;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.bar.TabBar;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.drawer.NaviDrawer;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.drawer.NaviItem;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.drawer.NaviMenu;
import br.edu.utfpr.dv.siacoes.ui.util.UIUtils;
import br.edu.utfpr.dv.siacoes.ui.util.css.Overflow;
import br.edu.utfpr.dv.siacoes.ui.views.Home;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipCompanyChartView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipEvaluationItemView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipFinalDocumentView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipJuryGradesReportView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipJuryParticipantsReportView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipJuryRequestView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipJuryView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipLibraryView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipMissingDocumentsReportView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipPosterRequestView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipReportView;
import br.edu.utfpr.dv.siacoes.ui.views.InternshipView;
import br.edu.utfpr.dv.siacoes.ui.views.JuryGradesReportView;
import br.edu.utfpr.dv.siacoes.ui.views.JuryParticipantsReportView;
import br.edu.utfpr.dv.siacoes.ui.views.JuryRequestView;
import br.edu.utfpr.dv.siacoes.ui.views.JurySemesterChartView;
import br.edu.utfpr.dv.siacoes.ui.views.JuryView;
import br.edu.utfpr.dv.siacoes.ui.views.LoginLogView;
import br.edu.utfpr.dv.siacoes.ui.views.MessageView;
import br.edu.utfpr.dv.siacoes.ui.views.ProjectView;
import br.edu.utfpr.dv.siacoes.ui.views.ProposalFeedbackReportView;
import br.edu.utfpr.dv.siacoes.ui.views.ProposalFeedbackView;
import br.edu.utfpr.dv.siacoes.ui.views.ProposalView;
import br.edu.utfpr.dv.siacoes.ui.views.ReminderMessageView;
import br.edu.utfpr.dv.siacoes.ui.views.SemesterView;
import br.edu.utfpr.dv.siacoes.ui.views.SignatureView;
import br.edu.utfpr.dv.siacoes.ui.views.SignedDocumentView;
import br.edu.utfpr.dv.siacoes.ui.views.StateView;
import br.edu.utfpr.dv.siacoes.ui.views.StudentActivityStatusReportView;
import br.edu.utfpr.dv.siacoes.ui.views.StudentHistoryView;
import br.edu.utfpr.dv.siacoes.ui.views.StudentView;
import br.edu.utfpr.dv.siacoes.ui.views.SupervisorChangeView;
import br.edu.utfpr.dv.siacoes.ui.views.SystemInfoView;
import br.edu.utfpr.dv.siacoes.ui.views.ThemeSuggestionView;
import br.edu.utfpr.dv.siacoes.ui.views.ThesisView;
import br.edu.utfpr.dv.siacoes.ui.views.TutoredSupervisorChartView;
import br.edu.utfpr.dv.siacoes.ui.views.TutoredView;
import br.edu.utfpr.dv.siacoes.ui.views.UserView;
import br.edu.utfpr.dv.siacoes.ui.windows.DownloadFeedbackWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.DownloadProposalFeedbackWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditAppConfigWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProjectWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProposalWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditSigacWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditSigesWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditSigetWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditSupervisorChangeWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditThesisWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.JuryGradesWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.ui.views.ActivitySubmissionView;
import br.edu.utfpr.dv.siacoes.ui.views.ActivityUnitView;
import br.edu.utfpr.dv.siacoes.ui.views.ActivityValidationReportView;
import br.edu.utfpr.dv.siacoes.ui.views.ActivityView;
import br.edu.utfpr.dv.siacoes.ui.views.AttendanceReportView;
import br.edu.utfpr.dv.siacoes.ui.views.AttendanceView;
import br.edu.utfpr.dv.siacoes.ui.views.BugReportView;
import br.edu.utfpr.dv.siacoes.ui.views.CampusView;
import br.edu.utfpr.dv.siacoes.ui.views.CityView;
import br.edu.utfpr.dv.siacoes.ui.views.CompanySupervisorView;
import br.edu.utfpr.dv.siacoes.ui.views.CompanyView;
import br.edu.utfpr.dv.siacoes.ui.views.CountryView;
import br.edu.utfpr.dv.siacoes.ui.views.DeadlineView;
import br.edu.utfpr.dv.siacoes.ui.views.DepartmentView;
import br.edu.utfpr.dv.siacoes.ui.views.DocumentView;
import br.edu.utfpr.dv.siacoes.ui.views.EmailMessageView;
import br.edu.utfpr.dv.siacoes.ui.views.EvaluationItemView;
import br.edu.utfpr.dv.siacoes.ui.views.EventCalendarView;
import br.edu.utfpr.dv.siacoes.ui.views.EventLogView;
import br.edu.utfpr.dv.siacoes.ui.views.ExternalSupervisorView;
import br.edu.utfpr.dv.siacoes.ui.views.FinalDocumentView;
import br.edu.utfpr.dv.siacoes.ui.views.FinalSubmissionView;
import br.edu.utfpr.dv.siacoes.ui.views.AboutView;
import br.edu.utfpr.dv.siacoes.ui.views.ActivityGroupStatusChartView;
import br.edu.utfpr.dv.siacoes.ui.views.ActivityGroupView;
import br.edu.utfpr.dv.siacoes.ui.views.ActivityHighScoreChartView;

import java.util.UUID;
import java.util.logging.Level;

@CssImport(value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")
@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
@CssImport("./styles/misc/box-shadow-borders.css")
@CssImport(value = "./styles/styles.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge")
public class MainLayout extends FlexBoxLayout implements RouterLayout, AfterNavigationObserver {

	private static final String CLASS_NAME = "root";

	private Div appHeaderOuter;

	private FlexBoxLayout row;
	private NaviDrawer naviDrawer;
	private FlexBoxLayout column;

	private Div appHeaderInner;
	private FlexBoxLayout viewContainer;
	private Div appFooterInner;

	private Div appFooterOuter;

	private TabBar tabBar;
	private boolean navigationTabs = false;
	private AppBar appBar;
	
	private SigetConfig sigetConfig;
	private SigacConfig sigacConfig;
	private SigesConfig sigesConfig;
	private Semester semester;

	public MainLayout() {
		/*VaadinSession.getCurrent()
				.setErrorHandler((ErrorHandler) errorEvent -> {
					log.error("Uncaught UI exception",
							errorEvent.getThrowable());
					Notification.show(
							"We are sorry, but an internal error occurred");
				});*/

		addClassName(CLASS_NAME);
		setFlexDirection(FlexDirection.COLUMN);
		setSizeFull();
		
		this.semester = new Semester();
        if((Session.getSelectedDepartment() != null) && (Session.getSelectedDepartment().getDepartment() != null) && (Session.getSelectedDepartment().getDepartment().getCampus() != null)) {
        	try {
    			this.semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
    		} catch (Exception e1) {
    			Logger.log(Level.SEVERE, e1.getMessage(), e1);
    		}
        }
        
        this.sigetConfig = new SigetConfig();
        this.sigacConfig = new SigacConfig();
        this.sigesConfig = new SigesConfig();
        
        if((Session.getSelectedDepartment() != null) && (Session.getSelectedDepartment().getDepartment() != null)) {
			try {
				this.sigetConfig = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} catch (Exception e1) {
				Logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
			
	        try {
				this.sigacConfig = new SigacConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} catch (Exception e1) {
				Logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
			
			try {
				this.sigesConfig = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} catch (Exception e1) {
				Logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
        }

		// Initialise the UI building blocks
		initStructure();

		// Populate the navigation drawer
		initNaviItems();

		// Configure the headers and footers (optional)
		initHeadersAndFooters();
	}

	/**
	 * Initialise the required components and containers.
	 */
	private void initStructure() {
		naviDrawer = new NaviDrawer();

		viewContainer = new FlexBoxLayout();
		viewContainer.addClassName(CLASS_NAME + "__view-container");
		viewContainer.setOverflow(Overflow.HIDDEN);

		column = new FlexBoxLayout(viewContainer);
		column.addClassName(CLASS_NAME + "__column");
		column.setFlexDirection(FlexDirection.COLUMN);
		column.setFlexGrow(1, viewContainer);
		column.setOverflow(Overflow.HIDDEN);

		row = new FlexBoxLayout(naviDrawer, column);
		row.addClassName(CLASS_NAME + "__row");
		row.setFlexGrow(1, column);
		row.setOverflow(Overflow.HIDDEN);
		add(row);
		setFlexGrow(1, row);
	}

	/**
	 * Initialise the navigation items.
	 */
	private void initNaviItems() {
		NaviMenu menu = naviDrawer.getMenu();
		
		menu.removeAll();
		
		menu.addNaviItem(VaadinIcon.ENVELOPE, this.getUnreadMessages(), "Mensagens", MessageView.class);
		
		if((AppConfig.getInstance().isSigetEnabled() && sigetConfig.isUseDigitalSignature()) || (AppConfig.getInstance().isSigesEnabled() && sigesConfig.isUseDigitalSignature()) || (AppConfig.getInstance().isSigacEnabled() && sigacConfig.isUseDigitalSignature())) {
			menu.addNaviItem(VaadinIcon.EDIT, this.getUnsignedDocuments(), "Central de Assinaturas", SignatureView.class);
		}
		
		if(AppConfig.getInstance().isSigesEnabled() || AppConfig.getInstance().isSigetEnabled()) {
			menu.addNaviItem(VaadinIcon.CALENDAR, "Calendário", EventCalendarView.class);
		}
		
		if(AppConfig.getInstance().isSigacEnabled()) {
			NaviItem sigac = menu.addNaviItem(VaadinIcon.DIPLOMA, "Atividades Complementares", null);
			
			if(Session.isUserManager(SystemModule.SIGAC) || Session.isUserStudent() || Session.isUserDepartmentManager()) {
				NaviItem sigac1 = menu.addNaviItem(sigac, "Registro de Atividades", null);
				
				menu.addNaviItem(sigac1, (Session.isUserManager(SystemModule.SIGAC) ? "Validar Atividades" : (Session.isUserStudent() ? "Atividades Registradas" : "Atividades Submetidas")), ActivitySubmissionView.class);
				
				if(Session.isUserManager(SystemModule.SIGAC) || Session.isUserDepartmentManager()) {
					menu.addNaviItem(sigac1, "Acadêmicos Aprovados", FinalSubmissionView.class);
				}
			}
			NaviItem sigac3 = menu.addNaviItem(sigac, "Repositório", null);
			NaviItem doc1 = menu.addNaviItem(sigac3, "Regulamentos e Anexos", DocumentView.class);
			doc1.addClickListener(event -> {
				this.getUI().ifPresent(ui -> ui.navigate(DocumentView.class, String.valueOf(SystemModule.SIGAC.getValue())));
			});
			doc1.setId("DocumentView" + String.valueOf(SystemModule.SIGAC.getValue()));
			if(Session.isUserAdministrator() || Session.isUserManager(SystemModule.SIGAC)) {
				NaviItem sigac2 = menu.addNaviItem(sigac, "Administração", null);
				
				if(Session.isUserAdministrator()) {
					menu.addNaviItem(sigac2, "Grupos de Atividades", ActivityGroupView.class);
					menu.addNaviItem(sigac2, "Unidades de Atividades", ActivityUnitView.class);
				}
				
				if(Session.isUserManager(SystemModule.SIGAC)) {
					menu.addNaviItem(sigac2, "Atividades", ActivityView.class);
					NaviItem config = menu.addNaviItem(sigac2, "Configurações", null);
					config.addClickListener(event -> {
						try {
							SigacConfig sigacConfig = new SigacConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
							EditSigacWindow window = new EditSigacWindow(sigacConfig, null);
							window.open();
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Abrir Configurações", e.getMessage());
						}
						
					});
				}
			}
			if(Session.isUserManager(SystemModule.SIGAC) || Session.isUserDepartmentManager()) {
				NaviItem sigac4 = menu.addNaviItem(sigac, "Relatórios", null);
				menu.addNaviItem(sigac4, "Validação de Atividades", ActivityValidationReportView.class);
				menu.addNaviItem(sigac4, "Situação do Acadêmico", StudentActivityStatusReportView.class);
				
				NaviItem sigac5 = menu.addNaviItem(sigac, "Gráficos", null);
				menu.addNaviItem(sigac5, "Pontuação por Grupo", ActivityGroupStatusChartView.class);
				menu.addNaviItem(sigac5, "Atividades mais Pontuadas", ActivityHighScoreChartView.class);
			}
		}
		
		if(AppConfig.getInstance().isSigesEnabled()){
			NaviItem siges = menu.addNaviItem(VaadinIcon.CLIPBOARD_USER, "Estágios", null);
			
			if(Session.isUserManager(SystemModule.SIGES)) {
				NaviItem siges1 = menu.addNaviItem(siges, "Cadastros", null);
				menu.addNaviItem(siges1, "Países", CountryView.class);
				menu.addNaviItem(siges1, "Estados", StateView.class);
				menu.addNaviItem(siges1, "Cidades", CityView.class);
				menu.addNaviItem(siges1, "Empresas", CompanyView.class);
				menu.addNaviItem(siges1, "Supervisores", CompanySupervisorView.class);
				menu.addNaviItem(siges1, "Acadêmicos", StudentView.class);
			}
			if(Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager() || Session.isUserProfessor() || Session.isUserCompanySupervisor() || Session.isUserStudent()) {
				NaviItem siges2 = menu.addNaviItem(siges, "Estágios", null);
				if(Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()) {
					NaviItem internshp = menu.addNaviItem(siges2, "Registro de Estágio", InternshipView.class);
					internshp.addClickListener(event -> {
						this.getUI().ifPresent(ui -> ui.navigate(InternshipView.class, "1"));
					});
					menu.addNaviItem(siges2, "Versão Final do Relatório", InternshipFinalDocumentView.class);
				}
				if(Session.isUserProfessor()) {
					NaviItem internshp = menu.addNaviItem(siges2, "Meus Orientados", InternshipView.class);
					internshp.addClickListener(event -> {
						this.getUI().ifPresent(ui -> ui.navigate(InternshipView.class, "0"));
					});
				} else if(Session.isUserCompanySupervisor()) {
					NaviItem internshp = menu.addNaviItem(siges2, "Meus Estagiários", InternshipView.class);
					internshp.addClickListener(event -> {
						this.getUI().ifPresent(ui -> ui.navigate(InternshipView.class, "0"));
					});
				} else if(Session.isUserStudent()) {
					NaviItem internshp = menu.addNaviItem(siges2, "Meus Estágios", InternshipView.class);
					internshp.addClickListener(event -> {
						this.getUI().ifPresent(ui -> ui.navigate(InternshipView.class, "0"));
					});
				}
			}
			NaviItem siges3 = menu.addNaviItem(siges, "Bancas", null);
			if((Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager())) {
				if(this.sigesConfig.getJuryFormat() == JuryFormat.SESSION) {
					menu.addNaviItem(siges3, "Solicitações de Bancas", InternshipPosterRequestView.class);
				} else if(this.sigesConfig.getJuryFormat() == JuryFormat.INDIVIDUAL) {
					menu.addNaviItem(siges3, "Solicitações de Bancas", InternshipJuryRequestView.class);
				}
			}
			NaviItem ijv = menu.addNaviItem(siges3, "Agenda de Bancas", InternshipJuryView.class);
			ijv.addClickListener(event -> {
				this.getUI().ifPresent(ui -> ui.navigate(InternshipJuryView.class, "1"));
			});
			if(Session.isUserStudent()) {
				NaviItem ijv1 = menu.addNaviItem(siges3, "Bancas que Assisti", InternshipJuryView.class);
				ijv1.addClickListener(event -> {
					this.getUI().ifPresent(ui -> ui.navigate(InternshipJuryView.class, "0"));
				});
			} else if(Session.isUserSupervisor()) {
				NaviItem ijv2 = menu.addNaviItem(siges3, "Minhas Bancas", InternshipJuryView.class);
				ijv2.addClickListener(event -> {
					this.getUI().ifPresent(ui -> ui.navigate(InternshipJuryView.class, "0"));
				});
			}
			NaviItem siges5 = menu.addNaviItem(siges, "Repositório", null);
			NaviItem doc2 = menu.addNaviItem(siges5, "Regulamentos e Anexos", DocumentView.class);
			doc2.addClickListener(event -> {
				this.getUI().ifPresent(ui -> ui.navigate(DocumentView.class, String.valueOf(SystemModule.SIGES.getValue())));
			});
			doc2.setId("DocumentView" + String.valueOf(SystemModule.SIGES.getValue()));
			menu.addNaviItem(siges5, "Biblioteca", InternshipLibraryView.class);
			if(Session.isUserManager(SystemModule.SIGES)){
				NaviItem siges4 = menu.addNaviItem(siges, "Administração", null);
				NaviItem history = menu.addNaviItem(siges4, "Histórico do Acadêmico", StudentHistoryView.class);
				history.addClickListener(event -> {
					this.getUI().ifPresent(ui -> ui.navigate(StudentHistoryView.class, String.valueOf(SystemModule.SIGES.getValue())));
				});
				if(this.sigesConfig.isUseEvaluationItems()) {
					menu.addNaviItem(siges4, "Quesitos de Avaliação", InternshipEvaluationItemView.class);
				}
				NaviItem config = menu.addNaviItem(siges4, "Configurações", null);
				config.addClickListener(event -> {
					try {
						SigesConfig sigesConfig = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
						EditSigesWindow window = new EditSigesWindow(sigesConfig, null);
						window.open();
					} catch (Exception e) {
						Logger.log(Level.SEVERE, e.getMessage(), e);
						
						Notification.showErrorNotification("Abrir Configurações", e.getMessage());
					}
					
				});
			}
			if(Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()) {
				NaviItem siges6 = menu.addNaviItem(siges, "Relatórios", null);
				menu.addNaviItem(siges6, "Estágios", InternshipReportView.class);
				menu.addNaviItem(siges6, "Documentos Faltantes", InternshipMissingDocumentsReportView.class);
				menu.addNaviItem(siges6, "Participação em Bancas", InternshipJuryParticipantsReportView.class);
				menu.addNaviItem(siges6, "Notas da Banca", InternshipJuryGradesReportView.class);
				
				NaviItem siges7 = menu.addNaviItem(siges, "Gráficos", null);
				menu.addNaviItem(siges7, "Estagiários por Empresa", InternshipCompanyChartView.class);
			}
		}
		
		if(AppConfig.getInstance().isSigetEnabled()){
			NaviItem siget = menu.addNaviItem(VaadinIcon.ACADEMY_CAP, "TCC", null);
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager() || Session.isUserSupervisor() || Session.isUserStudent()) {
				NaviItem siget1 = menu.addNaviItem(siget, "Proposta de TCC 1", null);
				if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()) {
					menu.addNaviItem(siget1, "Listar Propostas", ProposalView.class);
				}
				if(this.sigetConfig.isRegisterProposal() && Session.isUserStudent()) {
					NaviItem proposalSubmit = menu.addNaviItem(siget1, "Submeter Proposta", null);
					proposalSubmit.addClickListener(event -> {
						try {
							Proposal p = new ProposalBO().prepareProposal(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear(), false);
							
							EditProposalWindow window = new EditProposalWindow(p, null, true);
							window.open();
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Submeter Proposta", e.getMessage());
						}
					});
					NaviItem proposalFeedback = menu.addNaviItem(siget1, "Parecer dos Avaliadores", null);
					proposalFeedback.addClickListener(event -> {
						try {
							ProposalBO bo = new ProposalBO();
							Proposal p = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if((p == null) || (p.getFile() == null)) {
								throw new Exception("A proposta ainda não foi enviada.");
							} else {
								DownloadProposalFeedbackWindow window = new DownloadProposalFeedbackWindow(p);
								window.open();
							}
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Feedback dos Avaliadores", e.getMessage());
						}
					});
				} else if(Session.isUserSupervisor()) {
					menu.addNaviItem(siget1, "Avalição de Propostas", ProposalFeedbackView.class);
				}
			}
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager() || Session.isUserProfessor() || Session.isUserStudent()) {
				NaviItem siget2 = menu.addNaviItem(siget, "Projeto de TCC 1", null);
				if(Session.isUserStudent()) {
					NaviItem projectSubmit = menu.addNaviItem(siget2, "Submeter Projeto", null);
					projectSubmit.addClickListener(event -> {
						try {
							Project p = new ProjectBO().prepareProject(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							EditProjectWindow window = new EditProjectWindow(p, null);
							window.open();
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Submeter Projeto", e.getMessage());
						}
					});
					NaviItem printDocuments = menu.addNaviItem(siget2, "Imprimir Documentos", null);
					printDocuments.addClickListener(event -> {
						try {
							for(byte[] data : new ProjectBO().prepareDocuments(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear())) {
								this.showReport(data);
							}
	        	    	} catch (Exception e) {
	        	    		Logger.log(Level.SEVERE, e.getMessage(), e);
	        	    		
	        	    		Notification.showErrorNotification("Imprimir Documentos", e.getMessage());
						}
					});
					NaviItem juryFeedback = menu.addNaviItem(siget2, "Feedback da Banca", null);
					juryFeedback.addClickListener(event -> {
						try {
							Jury jury = new JuryBO().findByProject(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							DownloadFeedbackWindow window = new DownloadFeedbackWindow(jury);
							window.open();
	        	    	} catch (Exception e) {
	        	    		Logger.log(Level.SEVERE, e.getMessage(), e);
	        	    		
	        	    		Notification.showErrorNotification("Feedback da Banca", e.getMessage());
						}
					});
					if(this.sigetConfig.isShowGradesToStudent()) {
						NaviItem juryGrades = menu.addNaviItem(siget2, "Notas da Banca", null);
						juryGrades.addClickListener(event -> {
							try {
								JuryBO bo = new JuryBO();
								Jury jury = bo.findByProject(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
								
								if(bo.hasScores(jury.getIdJury())) {
									JuryGradesWindow window = new JuryGradesWindow(jury);
									window.open();
								} else {
									Notification.showErrorNotification("Notas da Banca", "As notas ainda não foram lançacas.");
								}
		        	    	} catch (Exception e) {
		        	    		Logger.log(Level.SEVERE, e.getMessage(), e);
		        	    		
		        	    		Notification.showErrorNotification("Notas da Banca", e.getMessage());
							}
						});
					}
					if(this.sigetConfig.isRequestFinalDocumentStage1()) {
						NaviItem finalDocument = menu.addNaviItem(siget2, "Submeter Versão Final", null);
						finalDocument.addClickListener(event -> {
							try {
								FinalDocument ft = new FinalDocumentBO().prepareFinalProject(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
								
								EditFinalDocumentWindow window = new EditFinalDocumentWindow(ft, null);
								window.open();
							} catch (Exception e) {
								Logger.log(Level.SEVERE, e.getMessage(), e);
								
								Notification.showErrorNotification("Submeter Versão Final", e.getMessage());
							}
						});
					}
				} else {
					if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()) {
						menu.addNaviItem(siget2, "Listar Projetos", ProjectView.class);
					}
					if(Session.isUserProfessor() && this.sigetConfig.isRequestFinalDocumentStage1()) {
						menu.addNaviItem(siget2, "Validar Versão Final", FinalDocumentView.class);
					}
				}
			}
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager() || Session.isUserProfessor() || Session.isUserStudent()) {
				NaviItem siget3 = menu.addNaviItem(siget, "Monografia de TCC 2", null);
				if(Session.isUserStudent()) {
					NaviItem thesisSubmit = menu.addNaviItem(siget3, "Submeter Monografia", null);
					thesisSubmit.addClickListener(event -> {
						try {
							Thesis thesis = new ThesisBO().prepareThesis(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							EditThesisWindow window = new EditThesisWindow(thesis, null);
							window.open();
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Submeter Monografia", e.getMessage());
						}
					});
					NaviItem printDocuments = menu.addNaviItem(siget3, "Imprimir Documentos", null);
					printDocuments.addClickListener(event -> {
						try {
							for(byte[] data : new ThesisBO().prepareDocuments(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear())) {
								this.showReport(data);
							}
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Imprimir Documentos", e.getMessage());
						}
					});
					NaviItem juryFeedback = menu.addNaviItem(siget3, "Feedback da Banca", null);
					juryFeedback.addClickListener(event -> {
						try {
							Jury jury = new JuryBO().findByThesis(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							DownloadFeedbackWindow window = new DownloadFeedbackWindow(jury);
							window.open();
	        	    	} catch (Exception e) {
	        	    		Logger.log(Level.SEVERE, e.getMessage(), e);
	        	    		
	        	    		Notification.showErrorNotification("Feedback da Banca", e.getMessage());
						}
					});
					if(this.sigetConfig.isShowGradesToStudent()) {
						NaviItem juryGrades = menu.addNaviItem(siget3, "Notas da Banca", null);
						juryGrades.addClickListener(event -> {
							try {
								JuryBO bo = new JuryBO();
								Jury jury = bo.findByThesis(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
								
								if(bo.hasScores(jury.getIdJury())) {
									JuryGradesWindow window = new JuryGradesWindow(jury);
									window.open();
								} else {
									Notification.showErrorNotification("Notas da Banca", "As notas ainda não foram lançacas.");
								}
		        	    	} catch (Exception e) {
		        	    		Logger.log(Level.SEVERE, e.getMessage(), e);
		        	    		
		        	    		Notification.showErrorNotification("Notas da Banca", e.getMessage());
							}
						});
					}
					NaviItem approvalTerm = menu.addNaviItem(siget3, "Termo de Aprovação", null);
					approvalTerm.addClickListener(event -> {
						try {
							Jury jury = new JuryBO().findByThesis(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							if((jury == null) || (jury.getIdJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())) {
								throw new Exception("O Termo de Aprovação somente é gerado após o envio da Monografia e a composição da Banca.");
							}
							
							this.showReport(new JuryBO().getTermOfApproval(jury.getIdJury(), true, false));
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Termo de Aprovação", e.getMessage());
						}
					});
					NaviItem finalDocument = menu.addNaviItem(siget3, "Submeter Versão Final", null);
					finalDocument.addClickListener(event -> {
						try {
							FinalDocument ft = new FinalDocumentBO().prepareFinalThesis(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
							
							EditFinalDocumentWindow window = new EditFinalDocumentWindow(ft, null);
							window.open();
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Submeter Versão Final", e.getMessage());
						}
					});
				} else {
					if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()) {
						menu.addNaviItem(siget3, "Listar Monografias", ThesisView.class);
					}
					if(Session.isUserProfessor()) {
						menu.addNaviItem(siget3, "Validar Versão Final", FinalDocumentView.class);
					}
				}
			}
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager() || Session.isUserProfessor() || Session.isUserSupervisor() || Session.isUserStudent()) {
				NaviItem siget4 = menu.addNaviItem(siget, "Orientação", null);
				if(Session.isUserStudent()) {
					NaviItem supervisorRegister = menu.addNaviItem(siget4, "Registrar Orientação", null);
					supervisorRegister.addClickListener(event -> {
						try {
							Proposal p = new ProposalBO().prepareProposal(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear(), true);
							
							EditProposalWindow window = new EditProposalWindow(p, null, false);
							window.open();
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Registrar Orientação", e.getMessage());
						}
					});
				}
				if(Session.isUserStudent()) {
					NaviItem supervisorChange = menu.addNaviItem(siget4, "Alterar Orientador", null);
					supervisorChange.addClickListener(event -> {
						try {
		    	        	Proposal proposal = new ProposalBO().findLastProposal(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
							
							if((proposal == null) || (proposal.getIdProposal() == 0)) {
								Notification.showErrorNotification("Alterar Orientador", "É necessário efetuar a submissão da proposta.");
							} else {
								Thesis thesis = new ThesisBO().findByProposal(proposal.getIdProposal());
								Jury jury = null;
								
								if((thesis != null) && (thesis.getIdThesis() != 0)) {
									jury = new JuryBO().findByThesis(0);
								}
								
								if((jury != null) && (jury.getIdJury() != 0)) {
									Notification.showErrorNotification("Alterar Orientador", "Não é possível efetuar a alteração de orientador pois a banca de TCC 2 já foi agendada.");
								} else {
									EditSupervisorChangeWindow window = new EditSupervisorChangeWindow(proposal, null, false);
									window.open();
								}
							}
						} catch (Exception e) {
							Logger.log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Alterar Orientador", e.getMessage());
						}
					});
				}
				if(Session.isUserSupervisor()) {
					menu.addNaviItem(siget4, "Meus Orientados", TutoredView.class);
				}
				if(Session.isUserSupervisor() || Session.isUserStudent()) {
					menu.addNaviItem(siget4, "Registro de Reuniões", AttendanceView.class);
				}
				if((Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager())) {
					if(!this.sigetConfig.isRegisterProposal()) {
						menu.addNaviItem(siget4, "Registros de Orientação", ProposalView.class);
					}
					menu.addNaviItem(siget4, "Orientadores Externos", ExternalSupervisorView.class);
				}
			}
			NaviItem siget5 = menu.addNaviItem(siget, "Bancas", null);
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()) {
				menu.addNaviItem(siget5, "Solicitações de Bancas", JuryRequestView.class);
			}
			NaviItem jury2 = menu.addNaviItem(siget5, "Agenda de Bancas", JuryView.class);
			jury2.addClickListener(event -> {
				this.getUI().ifPresent(ui -> ui.navigate(JuryView.class, "1"));
			});
			if(Session.isUserStudent()) {
				NaviItem jury3 = menu.addNaviItem(siget5, "Bancas que Assisti", JuryView.class);
				jury3.addClickListener(event -> {
					this.getUI().ifPresent(ui -> ui.navigate(JuryView.class, "0"));
				});
			} else if(Session.isUserSupervisor()) {
				NaviItem jury3 = menu.addNaviItem(siget5, "Minhas Bancas", JuryView.class);
				jury3.addClickListener(event -> {
					this.getUI().ifPresent(ui -> ui.navigate(JuryView.class, "0"));
				});
			}
			NaviItem siget7 = menu.addNaviItem(siget, "Repositório", null);
			NaviItem doc3 = menu.addNaviItem(siget7, "Regulamentos e Anexos", DocumentView.class);
			doc3.addClickListener(event -> {
				this.getUI().ifPresent(ui -> ui.navigate(DocumentView.class, String.valueOf(SystemModule.SIGET.getValue())));
			});
			doc3.setId("DocumentView" + String.valueOf(SystemModule.SIGET.getValue()));
			NaviItem library = menu.addNaviItem(siget7, "Biblioteca", null);
			library.addClickListener(event -> {
				if(sigetConfig.getRepositoryLink().trim().isEmpty()) {
					Notification.showErrorNotification("Biblioteca", "O link do repositório de TCCs não foi configurado.");
				} else {
					UI.getCurrent().getPage().open(sigetConfig.getRepositoryLink(), "_blank");
				}
			});
			menu.addNaviItem(siget7, "Sugestões de Projetos", ThemeSuggestionView.class);
			if(Session.isUserManager(SystemModule.SIGET)) {
				NaviItem siget6 = menu.addNaviItem(siget, "Administração", null);
				NaviItem history = menu.addNaviItem(siget6, "Histórico do Acadêmico", StudentHistoryView.class);
				history.addClickListener(event -> {
					this.getUI().ifPresent(ui -> ui.navigate(StudentHistoryView.class, String.valueOf(SystemModule.SIGET.getValue())));
				});
				menu.addNaviItem(siget6, "Alterações de Orientador", SupervisorChangeView.class);
				menu.addNaviItem(siget6, "Definir Datas", DeadlineView.class);
				menu.addNaviItem(siget6, "Quesitos de Avaliação", EvaluationItemView.class);
				NaviItem config = menu.addNaviItem(siget6, "Configurações", null);
				config.addClickListener(event -> {
					try {
						SigetConfig sigetConfig = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
						EditSigetWindow window = new EditSigetWindow(sigetConfig, null);
						window.open();
					} catch (Exception e) {
						Logger.log(Level.SEVERE, e.getMessage(), e);
						
						Notification.showErrorNotification("Abrir Configurações", e.getMessage());
					}
					
				});
			}
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()) {
				NaviItem siget8 = menu.addNaviItem(siget, "Relatórios", null);
				if(this.sigetConfig.isRegisterProposal()) {
					menu.addNaviItem(siget8, "Avaliação de Propostas de TCC 1", ProposalFeedbackReportView.class);
				}
				menu.addNaviItem(siget8, "Reuniões de Orientação", AttendanceReportView.class);
				menu.addNaviItem(siget8, "Participação em Bancas", JuryParticipantsReportView.class);
				menu.addNaviItem(siget8, "Notas da Banca", JuryGradesReportView.class);
				
				NaviItem siget9 = menu.addNaviItem(siget, "Gráficos", null);
				menu.addNaviItem(siget9, "Orientados por Orientador", TutoredSupervisorChartView.class);
				menu.addNaviItem(siget9, "Bancas por Semestre", JurySemesterChartView.class);
			}
		}
		
		NaviItem admin = menu.addNaviItem(VaadinIcon.COG, "Recursos Gerais", null);
		if(Session.isUserAdministrator()) {
			NaviItem admin1 = menu.addNaviItem(admin, "Administração", null);
			menu.addNaviItem(admin1, "Câmpus", CampusView.class);
			menu.addNaviItem(admin1, "Departamentos", DepartmentView.class);
			menu.addNaviItem(admin1, "Semestres", SemesterView.class);
			menu.addNaviItem(admin1, "Envio de E-mails", EmailMessageView.class);
			menu.addNaviItem(admin1, "Lembretes", ReminderMessageView.class);
			menu.addNaviItem(admin1, "Usuários", UserView.class);
			if(this.sigesConfig.isUseDigitalSignature() || this.sigetConfig.isUseDigitalSignature()) {
				menu.addNaviItem(admin1, "Assinatura de Documentos", SignedDocumentView.class);	
			}
			NaviItem appConfig = menu.addNaviItem(admin1, "Configurações", null);
			appConfig.addClickListener(event -> {
				EditAppConfigWindow window = new EditAppConfigWindow();
				window.open();
			});
			
			NaviItem admin2 = menu.addNaviItem(admin, "Auditoria", null);
			menu.addNaviItem(admin2, "Registro de Acessos", LoginLogView.class);
			menu.addNaviItem(admin2, "Registro de Eventos", EventLogView.class);
			
			menu.addNaviItem(admin, "Informações do Sistema", SystemInfoView.class);
		}
		menu.addNaviItem(admin, "Sugestões e Problemas", BugReportView.class);
		menu.addNaviItem(admin, "Sobre o Sistema", AboutView.class);
		
		menu.collapseAll();
	}

	/**
	 * Configure the app's inner and outer headers and footers.
	 */
	private void initHeadersAndFooters() {
		// setAppHeaderOuter();
		// setAppFooterInner();
		// setAppFooterOuter();

		// Default inner header setup:
		// - When using tabbed navigation the view title, user avatar and main menu button will appear in the TabBar.
		// - When tabbed navigation is turned off they appear in the AppBar.

		appBar = new AppBar("");

		// Tabbed navigation
		if (navigationTabs) {
			tabBar = new TabBar();
			UIUtils.setTheme(Lumo.DARK, tabBar);

			// Shift-click to add a new tab
			for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
				item.addClickListener(e -> {
					if (e.getButton() == 0 && e.isShiftKey()) {
						tabBar.setSelectedTab(tabBar.addClosableTab(item.getText(), item.getNavigationTarget()));
					}
				});
			}
			//appBar.getAvatar().setVisible(false);
			setAppHeaderInner(tabBar, appBar);

			// Default navigation
		} else {
			UIUtils.setTheme(Lumo.DARK, appBar);
			setAppHeaderInner(appBar);
		}
	}

	private void setAppHeaderOuter(Component... components) {
		if (appHeaderOuter == null) {
			appHeaderOuter = new Div();
			appHeaderOuter.addClassName("app-header-outer");
			getElement().insertChild(0, appHeaderOuter.getElement());
		}
		appHeaderOuter.removeAll();
		appHeaderOuter.add(components);
	}

	private void setAppHeaderInner(Component... components) {
		if (appHeaderInner == null) {
			appHeaderInner = new Div();
			appHeaderInner.addClassName("app-header-inner");
			column.getElement().insertChild(0, appHeaderInner.getElement());
		}
		appHeaderInner.removeAll();
		appHeaderInner.add(components);
	}

	private void setAppFooterInner(Component... components) {
		if (appFooterInner == null) {
			appFooterInner = new Div();
			appFooterInner.addClassName("app-footer-inner");
			column.getElement().insertChild(column.getElement().getChildCount(),
					appFooterInner.getElement());
		}
		appFooterInner.removeAll();
		appFooterInner.add(components);
	}

	private void setAppFooterOuter(Component... components) {
		if (appFooterOuter == null) {
			appFooterOuter = new Div();
			appFooterOuter.addClassName("app-footer-outer");
			getElement().insertChild(getElement().getChildCount(),
					appFooterOuter.getElement());
		}
		appFooterOuter.removeAll();
		appFooterOuter.add(components);
	}

	@Override
	public void showRouterLayoutContent(HasElement content) {
		this.viewContainer.getElement().appendChild(content.getElement());
	}

	public NaviDrawer getNaviDrawer() {
		return naviDrawer;
	}

	public static MainLayout get() {
		if(UI.getCurrent().getChildren().filter(component -> component.getClass() == MainLayout.class).count() > 0) {
			return (MainLayout) UI.getCurrent().getChildren()
				.filter(component -> component.getClass() == MainLayout.class)
				.findFirst().get();
		} else {
			return null;	
		}
	}

	public AppBar getAppBar() {
		return appBar;
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		if (navigationTabs) {
			afterNavigationWithTabs(event);
		} else {
			afterNavigationWithoutTabs(event);
		}
	}

	private void afterNavigationWithTabs(AfterNavigationEvent e) {
		NaviItem active = getActiveItem(e);
		if (active == null) {
			if (tabBar.getTabCount() == 0) {
				tabBar.addClosableTab("", Home.class);
			}
		} else {
			if (tabBar.getTabCount() > 0) {
				tabBar.updateSelectedTab(active.getText(),
						active.getNavigationTarget());
			} else {
				tabBar.addClosableTab(active.getText(),
						active.getNavigationTarget());
			}
		}
		appBar.getMenuIcon().setVisible(false);
	}

	private NaviItem getActiveItem(AfterNavigationEvent e) {
		for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
			if (item.isHighlighted(e)) {
				return item;
			}
		}
		return null;
	}

	private void afterNavigationWithoutTabs(AfterNavigationEvent e) {
		NaviItem active = getActiveItem(e);
		if (active != null) {
			getAppBar().setTitle(active.getText());
		}
	}
	
	public static void setHighlightedNaviItem(String id) {
		MainLayout layout = MainLayout.get();
		
		if(layout != null) {
			for(NaviItem item : layout.naviDrawer.getMenu().getNaviItems()) {
				if(id.equals(item.getId().toString())) {
					
				}
			}
		}
	}
	
	public static void reloadNaviItems() {
		MainLayout layout = MainLayout.get();
		
		if(layout != null) {
			layout.initNaviItems();
			layout.initHeadersAndFooters();
		}
	}
	
	private void showReport(byte[] pdfReport){
    	if(pdfReport == null) {
    		Notification.showErrorNotification("Visualizar Arquivo", "O arquivo solicitado não foi encontrado.");
    	} else {
        	String id = UUID.randomUUID().toString();
        	
        	Session.putReport(pdfReport, id);
    		
        	UI.getCurrent().getPage().open("pdf/session-" + id, "_blank");
    	}
    }
	
	private int getUnreadMessages() {
		try {
			return new MessageBO().getUnreadMessages(Session.getUser().getIdUser());
		} catch (Exception e) {
			return 0;
		}
	}
	
	private int getUnsignedDocuments() {
		try {
			return Document.getPendingDocuments(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			return 0;
		}
	}

}
