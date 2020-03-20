package br.edu.utfpr.dv.siacoes;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;

import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.view.ActivitySubmissionView;
import br.edu.utfpr.dv.siacoes.view.ActivityGroupStatusChartView;
import br.edu.utfpr.dv.siacoes.view.ActivityGroupView;
import br.edu.utfpr.dv.siacoes.view.ActivityHighScoreChartView;
import br.edu.utfpr.dv.siacoes.view.ActivityUnitView;
import br.edu.utfpr.dv.siacoes.view.ActivityValidationReportView;
import br.edu.utfpr.dv.siacoes.view.ActivityView;
import br.edu.utfpr.dv.siacoes.view.AttendanceReportView;
import br.edu.utfpr.dv.siacoes.view.AttendanceView;
import br.edu.utfpr.dv.siacoes.view.AuthDocumentView;
import br.edu.utfpr.dv.siacoes.view.AuthenticateView;
import br.edu.utfpr.dv.siacoes.view.BasicView;
import br.edu.utfpr.dv.siacoes.view.BugReportView;
import br.edu.utfpr.dv.siacoes.view.JuryView;
import br.edu.utfpr.dv.siacoes.view.LoginLogView;
import br.edu.utfpr.dv.siacoes.view.CampusView;
import br.edu.utfpr.dv.siacoes.view.CertificateView;
import br.edu.utfpr.dv.siacoes.view.CityView;
import br.edu.utfpr.dv.siacoes.view.CompanySupervisorView;
import br.edu.utfpr.dv.siacoes.view.CompanyView;
import br.edu.utfpr.dv.siacoes.view.CountryView;
import br.edu.utfpr.dv.siacoes.view.DeadlineView;
import br.edu.utfpr.dv.siacoes.view.DepartmentView;
import br.edu.utfpr.dv.siacoes.view.DocumentView;
import br.edu.utfpr.dv.siacoes.view.EmailMessageView;
import br.edu.utfpr.dv.siacoes.view.Error403View;
import br.edu.utfpr.dv.siacoes.view.Error404View;
import br.edu.utfpr.dv.siacoes.view.EvaluationItemView;
import br.edu.utfpr.dv.siacoes.view.EventCalendarView;
import br.edu.utfpr.dv.siacoes.view.EventLogView;
import br.edu.utfpr.dv.siacoes.view.ExternalSupervisorView;
import br.edu.utfpr.dv.siacoes.view.FinalDocumentView;
import br.edu.utfpr.dv.siacoes.view.FinalSubmissionView;
import br.edu.utfpr.dv.siacoes.view.InternshipJuryView;
import br.edu.utfpr.dv.siacoes.view.InternshipCompanyChartView;
import br.edu.utfpr.dv.siacoes.view.InternshipEvaluationItemView;
import br.edu.utfpr.dv.siacoes.view.InternshipFinalDocumentView;
import br.edu.utfpr.dv.siacoes.view.InternshipJuryGradesReportView;
import br.edu.utfpr.dv.siacoes.view.InternshipJuryParticipantsReportView;
import br.edu.utfpr.dv.siacoes.view.InternshipLibraryView;
import br.edu.utfpr.dv.siacoes.view.InternshipMissingDocumentsReportView;
import br.edu.utfpr.dv.siacoes.view.InternshipPosterRequestView;
import br.edu.utfpr.dv.siacoes.view.InternshipView;
import br.edu.utfpr.dv.siacoes.view.JuryGradesReportView;
import br.edu.utfpr.dv.siacoes.view.JuryParticipantsReportView;
import br.edu.utfpr.dv.siacoes.view.JuryRequestView;
import br.edu.utfpr.dv.siacoes.view.JurySemesterChartView;
import br.edu.utfpr.dv.siacoes.view.LoginView;
import br.edu.utfpr.dv.siacoes.view.MainView;
import br.edu.utfpr.dv.siacoes.view.MessageView;
import br.edu.utfpr.dv.siacoes.view.MobileView;
import br.edu.utfpr.dv.siacoes.view.PDFView;
import br.edu.utfpr.dv.siacoes.view.PasswordView;
import br.edu.utfpr.dv.siacoes.view.ProjectView;
import br.edu.utfpr.dv.siacoes.view.ProposalFeedbackReportView;
import br.edu.utfpr.dv.siacoes.view.ProposalFeedbackStudentView;
import br.edu.utfpr.dv.siacoes.view.ProposalFeedbackView;
import br.edu.utfpr.dv.siacoes.view.ProposalView;
import br.edu.utfpr.dv.siacoes.view.SemesterView;
import br.edu.utfpr.dv.siacoes.view.SignatureView;
import br.edu.utfpr.dv.siacoes.view.SignedDocumentView;
import br.edu.utfpr.dv.siacoes.view.StateView;
import br.edu.utfpr.dv.siacoes.view.StudentActivityStatusReportView;
import br.edu.utfpr.dv.siacoes.view.StudentHistoryView;
import br.edu.utfpr.dv.siacoes.view.StudentView;
import br.edu.utfpr.dv.siacoes.view.SupervisorChangeView;
import br.edu.utfpr.dv.siacoes.view.SupervisorView;
import br.edu.utfpr.dv.siacoes.view.SystemInfoView;
import br.edu.utfpr.dv.siacoes.view.ThemeSuggestionView;
import br.edu.utfpr.dv.siacoes.view.ThesisView;
import br.edu.utfpr.dv.siacoes.view.TutoredSupervisorChartView;
import br.edu.utfpr.dv.siacoes.view.TutoredView;
import br.edu.utfpr.dv.siacoes.view.UserView;

import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("facebook")
public class SiacoesUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SiacoesUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		//
        // Create a new instance of the navigator. The navigator will attach
        // itself automatically to this view.
        //
        new Navigator(this, this);
        getNavigator().addView("", MainView.class);
        getNavigator().addView(MainView.NAME, MainView.class);
        getNavigator().addView(LoginView.NAME, LoginView.class);
        getNavigator().addView(DocumentView.NAME, DocumentView.class);
        getNavigator().addView(UserView.NAME, UserView.class);
        getNavigator().addView(DeadlineView.NAME, DeadlineView.class);
        getNavigator().addView(ProposalView.NAME, ProposalView.class);
        getNavigator().addView(ProposalFeedbackView.NAME, ProposalFeedbackView.class);
        getNavigator().addView(AttendanceView.NAME, AttendanceView.class);
        getNavigator().addView(ProjectView.NAME, ProjectView.class);
        getNavigator().addView(ThesisView.NAME, ThesisView.class);
        getNavigator().addView(SupervisorView.NAME, SupervisorView.class);
        getNavigator().addView(EvaluationItemView.NAME, EvaluationItemView.class);
        getNavigator().addView(JuryView.NAME, JuryView.class);
        getNavigator().addView(ActivityGroupView.NAME, ActivityGroupView.class);
        getNavigator().addView(ActivityUnitView.NAME, ActivityUnitView.class);
        getNavigator().addView(ActivityView.NAME, ActivityView.class);
        getNavigator().addView(ActivitySubmissionView.NAME, ActivitySubmissionView.class);
        getNavigator().addView(CampusView.NAME, CampusView.class);
        getNavigator().addView(DepartmentView.NAME, DepartmentView.class);
        getNavigator().addView(ThemeSuggestionView.NAME, ThemeSuggestionView.class);
        getNavigator().addView(SupervisorChangeView.NAME, SupervisorChangeView.class);
        getNavigator().addView(EmailMessageView.NAME, EmailMessageView.class);
        getNavigator().addView(BugReportView.NAME, BugReportView.class);
        getNavigator().addView(CountryView.NAME, CountryView.class);
        getNavigator().addView(StateView.NAME, StateView.class);
        getNavigator().addView(CityView.NAME, CityView.class);
        getNavigator().addView(CompanyView.NAME, CompanyView.class);
        getNavigator().addView(CompanySupervisorView.NAME, CompanySupervisorView.class);
        getNavigator().addView(InternshipView.NAME, InternshipView.class);
        getNavigator().addView(StudentView.NAME, StudentView.class);
        getNavigator().addView(AuthenticateView.NAME, AuthenticateView.class);
        getNavigator().addView(CertificateView.NAME, CertificateView.class);
        getNavigator().addView(InternshipJuryView.NAME, InternshipJuryView.class);
        getNavigator().addView(InternshipEvaluationItemView.NAME, InternshipEvaluationItemView.class);
        getNavigator().addView(FinalDocumentView.NAME, FinalDocumentView.class);
        getNavigator().addView(InternshipLibraryView.NAME, InternshipLibraryView.class);
        getNavigator().addView(TutoredView.NAME, TutoredView.class);
        getNavigator().addView(SemesterView.NAME, SemesterView.class);
        getNavigator().addView(ProposalFeedbackStudentView.NAME, ProposalFeedbackStudentView.class);
        getNavigator().addView(ActivityValidationReportView.NAME, ActivityValidationReportView.class);
        getNavigator().addView(StudentActivityStatusReportView.NAME, StudentActivityStatusReportView.class);
        getNavigator().addView(AttendanceReportView.NAME, AttendanceReportView.class);
        getNavigator().addView(InternshipMissingDocumentsReportView.NAME, InternshipMissingDocumentsReportView.class);
        getNavigator().addView(InternshipCompanyChartView.NAME, InternshipCompanyChartView.class);
        getNavigator().addView(EventCalendarView.NAME, EventCalendarView.class);
        getNavigator().addView(PDFView.NAME, PDFView.class);
        getNavigator().addView(InternshipFinalDocumentView.NAME, InternshipFinalDocumentView.class);
        getNavigator().addView(TutoredSupervisorChartView.NAME, TutoredSupervisorChartView.class);
        getNavigator().addView(JurySemesterChartView.NAME, JurySemesterChartView.class);
        getNavigator().addView(ActivityGroupStatusChartView.NAME, ActivityGroupStatusChartView.class);
        getNavigator().addView(ActivityHighScoreChartView.NAME, ActivityHighScoreChartView.class);
        getNavigator().addView(FinalSubmissionView.NAME, FinalSubmissionView.class);
        getNavigator().addView(Error403View.NAME, Error403View.class);
        getNavigator().addView(Error404View.NAME, Error404View.class);
        getNavigator().addView(JuryParticipantsReportView.NAME, JuryParticipantsReportView.class);
        getNavigator().addView(InternshipJuryParticipantsReportView.NAME, InternshipJuryParticipantsReportView.class);
        getNavigator().addView(PasswordView.NAME, PasswordView.class);
        getNavigator().addView(MobileView.NAME, MobileView.class);
        getNavigator().addView(ExternalSupervisorView.NAME, ExternalSupervisorView.class);
        getNavigator().addView(JuryGradesReportView.NAME, JuryGradesReportView.class);
        getNavigator().addView(InternshipJuryGradesReportView.NAME, InternshipJuryGradesReportView.class);
        getNavigator().addView(JuryRequestView.NAME, JuryRequestView.class);
        getNavigator().addView(ProposalFeedbackReportView.NAME, ProposalFeedbackReportView.class);
        getNavigator().addView(MessageView.NAME, MessageView.class);
        getNavigator().addView(LoginLogView.NAME, LoginLogView.class);
        getNavigator().addView(EventLogView.NAME, EventLogView.class);
        getNavigator().addView(SystemInfoView.NAME, SystemInfoView.class);
        getNavigator().addView(AuthDocumentView.NAME, AuthDocumentView.class);
        getNavigator().addView(SignatureView.NAME, SignatureView.class);
        getNavigator().addView(InternshipPosterRequestView.NAME, InternshipPosterRequestView.class);
        getNavigator().addView(StudentHistoryView.NAME, StudentHistoryView.class);
        getNavigator().addView(SignedDocumentView.NAME, SignedDocumentView.class);
        
        getNavigator().setErrorView(Error404View.class);
        
        //
        // We use a view change handler to ensure the user is always redirected
        // to the login view if the user is not logged in.
        //
        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                // Check if a user has logged in
                boolean isLoggedIn = Session.isAuthenticated();
                boolean isLoginView = event.getNewView() instanceof LoginView;
                boolean isMainView = event.getNewView() instanceof MainView;
                
                if ((event.getNewView() instanceof AuthenticateView) || (event.getNewView() instanceof AuthDocumentView) || (event.getNewView() instanceof CertificateView) || (event.getNewView() instanceof PasswordView) || (event.getNewView() instanceof MobileView)){
                	return true;
                } else if (!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet
                    // logged in
                    getNavigator().navigateTo(LoginView.NAME + "/" + event.getViewName());
                    return false;
                } else if (isLoggedIn && isLoginView) {
                    // If someone tries to access to login view while logged in,
                    // then cancel
                    return true;
                } else if(!isMainView && !isLoginView) {
                	if(event.getNewView() instanceof BasicView){
                		BasicView view = (BasicView)event.getNewView();
                    	
                    	if((view.getProfilePermissions() == UserProfile.ADMINISTRATOR) && (!Session.isUserAdministrator())){
                    		getNavigator().navigateTo(Error403View.NAME);
                    		return false;
                    	} else if((view.getProfilePermissions() == UserProfile.MANAGER) && !Session.isUserManager(view.getModule()) && !Session.isUserDepartmentManager()){
                    		getNavigator().navigateTo(Error403View.NAME);
                    		return false;
                    	} else if((view.getProfilePermissions() == UserProfile.PROFESSOR) && (!Session.isUserProfessor())) {
                    		getNavigator().navigateTo(Error403View.NAME);
                    		return false;
                    	} else if((view.getProfilePermissions() == UserProfile.SUPERVISOR) && (!Session.isUserProfessor() && !Session.isUserSupervisor())) {
                    		getNavigator().navigateTo(Error403View.NAME);
                    		return false;
                    	} else if((view.getProfilePermissions() == UserProfile.COMPANYSUPERVISOR) && (!Session.isUserCompanySupervisor())) {
                    		getNavigator().navigateTo(Error403View.NAME);
                    		return false;
                    	} else if((view.getProfilePermissions() == UserProfile.ADMINISTRATIVE) && (!Session.isUserAdministrative())) {
                    		getNavigator().navigateTo(Error403View.NAME);
                    		return false;
                    	}
                	}
                }

                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
            	Page.getCurrent().setTitle("SIACOES");
            }
        });
        
        UI.getCurrent().setTheme(AppConfig.getInstance().getTheme().name().toLowerCase());
	}

}