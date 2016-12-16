package br.edu.utfpr.dv.siacoes.components;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.view.ActivityGroupView;
import br.edu.utfpr.dv.siacoes.view.ActivitySubmissionView;
import br.edu.utfpr.dv.siacoes.view.ActivityUnitView;
import br.edu.utfpr.dv.siacoes.view.ActivityView;
import br.edu.utfpr.dv.siacoes.view.AttendanceView;
import br.edu.utfpr.dv.siacoes.view.BugReportView;
import br.edu.utfpr.dv.siacoes.view.CalendarView;
import br.edu.utfpr.dv.siacoes.view.CampusView;
import br.edu.utfpr.dv.siacoes.view.DeadlineView;
import br.edu.utfpr.dv.siacoes.view.DepartmentView;
import br.edu.utfpr.dv.siacoes.view.DocumentView;
import br.edu.utfpr.dv.siacoes.view.EmailMessageView;
import br.edu.utfpr.dv.siacoes.view.EvaluationItemView;
import br.edu.utfpr.dv.siacoes.view.LibraryView;
import br.edu.utfpr.dv.siacoes.view.LoginView;
import br.edu.utfpr.dv.siacoes.view.MainView;
import br.edu.utfpr.dv.siacoes.view.ProjectView;
import br.edu.utfpr.dv.siacoes.view.ProposalFeedbackView;
import br.edu.utfpr.dv.siacoes.view.ProposalView;
import br.edu.utfpr.dv.siacoes.view.SigacView;
import br.edu.utfpr.dv.siacoes.view.SigesView;
import br.edu.utfpr.dv.siacoes.view.SigetView;
import br.edu.utfpr.dv.siacoes.view.SupervisorChangeView;
import br.edu.utfpr.dv.siacoes.view.SupervisorView;
import br.edu.utfpr.dv.siacoes.view.ThemeSuggestionView;
import br.edu.utfpr.dv.siacoes.view.ThesisView;
import br.edu.utfpr.dv.siacoes.view.UserView;
import br.edu.utfpr.dv.siacoes.window.AboutWindow;
import br.edu.utfpr.dv.siacoes.window.DownloadFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.window.EditPasswordWindow;
import br.edu.utfpr.dv.siacoes.window.EditProjectWindow;
import br.edu.utfpr.dv.siacoes.window.EditProposalWindow;
import br.edu.utfpr.dv.siacoes.window.EditSigacWindow;
import br.edu.utfpr.dv.siacoes.window.EditSigetWindow;
import br.edu.utfpr.dv.siacoes.window.EditSupervisorWindow;
import br.edu.utfpr.dv.siacoes.window.EditThesisWindow;
import br.edu.utfpr.dv.siacoes.window.EditUserWindow;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class MenuBar extends CustomComponent {
	
	private final com.vaadin.ui.MenuBar menu;
	
	public MenuBar(SystemModule module){
		this.menu = new com.vaadin.ui.MenuBar();
		
		HorizontalLayout layout = new HorizontalLayout(this.menu);
		
		MenuItem modules = this.menu.addItem("Módulos", null);
		if(module != SystemModule.GENERAL){
			modules.addItem("Módulo Inicial", new Command(){
        	    @Override
        	    public void menuSelected(MenuItem selectedItem){
        	        UI.getCurrent().getNavigator().navigateTo(MainView.NAME);
        	    }
        	});
		}
		if(module != SystemModule.SIGAC){
			modules.addItem("Atividades Complementares", new Command(){
        	    @Override
        	    public void menuSelected(MenuItem selectedItem){
        	        UI.getCurrent().getNavigator().navigateTo(SigacView.NAME);
        	    }
        	});
		}
		if(module != SystemModule.SIGES){
			modules.addItem("Estágios", new Command(){
        	    @Override
        	    public void menuSelected(MenuItem selectedItem){
        	        UI.getCurrent().getNavigator().navigateTo(SigesView.NAME);
        	    }
        	});
		}
		if(module != SystemModule.SIGET){
			modules.addItem("Trabalho de Conclusão de Curso", new Command(){
        	    @Override
        	    public void menuSelected(MenuItem selectedItem){
        	        UI.getCurrent().getNavigator().navigateTo(SigetView.NAME);
        	    }
        	});
		}
		
		if(module == SystemModule.SIGET){
			if(Session.getUser().getDepartment().isSigetRegisterProposal()){
				MenuItem proposal = this.menu.addItem("Proposta de TCC 1", null);
				if(Session.isUserManager(SystemModule.SIGET)){
					proposal.addItem("Listar Propostas", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(ProposalView.NAME);
		        	    }
		        	});
					proposal.addSeparator();
				}
				if(Session.isUserStudent()){
					proposal.addItem("Submeter Proposta", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
							try {
								ProposalBO bo = new ProposalBO();
								Proposal p = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(p == null){
									DeadlineBO dbo = new DeadlineBO();
									Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									
									if((d == null) || DateUtils.getToday().getTime().after(d.getProposalDeadline())){
										throw new Exception("A submissão de propostas já foi encerrada.");
									}
									
									p = new Proposal(Session.getUser());
								}
								
								UI.getCurrent().addWindow(new EditProposalWindow(p, null));
							} catch (Exception e) {
								Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
								
								Notification.show("Submeter Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
							}
		        	    }
		        	});
				}
				if(Session.isUserProfessor()){
					proposal.addItem("Cadastrar Parecer", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(ProposalFeedbackView.NAME);
		        	    }
		        	});
				}
			}
	    	
			if(Session.isUserStudent() || Session.isUserManager(SystemModule.SIGET)){
		    	MenuItem project = this.menu.addItem("Projeto de TCC 1", null);
		    	if(Session.isUserStudent()){
		    		if(!Session.getUser().getDepartment().isSigetRegisterProposal()){
		    			project.addItem("Registrar Orientação", new Command(){
			        	    @Override
			        	    public void menuSelected(MenuItem selectedItem){
			        	    	try {
									ProposalBO bo = new ProposalBO();
									Proposal p = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									
									if(p == null){
										DeadlineBO dbo = new DeadlineBO();
										Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
										
										if((d == null) || DateUtils.getToday().getTime().after(d.getProposalDeadline())){
											throw new Exception("O registro de orientações já foi encerrado.");
										}
										
										p = new Proposal(Session.getUser());
									}
									
									UI.getCurrent().addWindow(new EditProposalWindow(p, null));
								} catch (Exception e) {
									Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
									
									Notification.show("Registrar Orientação", e.getMessage(), Notification.Type.ERROR_MESSAGE);
								}
			        	    }
			        	});
		    			project.addSeparator();
		    		}
		    		project.addItem("Submeter Projeto", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try {
								ProjectBO bo = new ProjectBO();
								Project p = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(p == null){
									DeadlineBO dbo = new DeadlineBO();
									Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									
									if((d == null) || DateUtils.getToday().getTime().after(d.getProjectDeadline())){
										throw new Exception("A submissão de projetos já foi encerrada.");
									}
									
									ProposalBO pbo = new ProposalBO();
									Proposal proposal = pbo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									
									if(proposal == null){
										if(Session.getUser().getDepartment().isSigetRegisterProposal()){
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
		        	});
		    		project.addSeparator();
		    		project.addItem("Imprimir Documentos", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try {
								ProjectBO bo = new ProjectBO();
								Project p = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(p == null){
									p = bo.findLastProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								}
								
								if(p == null){
									Notification.show("Imprimir Documentos", "É necessário submeter o projeto para imprimir os documentos.", Notification.Type.ERROR_MESSAGE);
								}else{
									Page.getCurrent().getJavaScript().execute("document.getElementById('projectFilesDownload').click();");	
								}
		        	    	} catch (Exception e) {
		        	    		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		        	    		
								Notification.show("Imprimir Documentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
							}
		        	    }
		        	});
		    		project.addSeparator();
		    		project.addItem("Feedback da Banca", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try {
								ProjectBO bo = new ProjectBO();
								Project p = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(p == null){
									p = bo.findLastProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
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
		        	});
		    		project.addSeparator();
		    		project.addItem("Submeter Versão Final", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
							try {
								FinalDocumentBO fbo = new FinalDocumentBO();
								FinalDocument ft = fbo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(ft == null){
									ProjectBO bo = new ProjectBO();
									Project project = bo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
									/*if(project == null){
										project = bo.findApprovedProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									}*/
									
									if(project == null){
										throw new Exception("É necessário submeter o projeto para avaliação da banca antes.");
									}else{
										ft = new FinalDocument();
										ft.setTitle(project.getTitle());
										ft.setProject(project);
									}
								}
								
								UI.getCurrent().addWindow(new EditFinalDocumentWindow(ft, null));
							} catch (Exception e) {
								Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
								
								Notification.show("Submeter Versão Final", e.getMessage(), Notification.Type.ERROR_MESSAGE);
							}
		        	    }
		        	});
		    		
		        	Button button = new Button();
		        	button.setId("projectFilesDownload");
		        	button.addStyleName("InvisibleButton");
		        	layout.addComponent(button);
		        	this.prepareDownloadStage1(button);
		    	}
		    	if(Session.isUserManager(SystemModule.SIGET)){
		    		project.addItem("Listar Projetos", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(ProjectView.NAME);
		        	    }
		        	});
		    	}
		    	
		    	MenuItem thesis = this.menu.addItem("Monografia de TCC 2", null);
		    	if(Session.isUserStudent()){
		    		thesis.addItem("Submeter Monografia", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try {
								ThesisBO bo = new ThesisBO();
								Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(thesis == null){
									DeadlineBO dbo = new DeadlineBO();
									Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									
									if((d == null) || DateUtils.getToday().getTime().after(d.getThesisDeadline())){
										throw new Exception("A submissão de monografias já foi encerrada.");
									}
									
									ProjectBO pbo = new ProjectBO();
									Project project = pbo.findApprovedProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									
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
		        	});
		    		thesis.addSeparator();
		    		thesis.addItem("Imprimir Documentos", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
							try {
								ThesisBO bo = new ThesisBO();
								Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(thesis == null){
									thesis = bo.findLastThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								}
								
								if(thesis == null){
									Notification.show("Imprimir Documentos", "É necessário submeter a monografia para imprimir os documentos.", Notification.Type.ERROR_MESSAGE);
								}else{
									Page.getCurrent().getJavaScript().execute("document.getElementById('thesisFilesDownload').click();");	
								}
							} catch (Exception e) {
								Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
								
								Notification.show("Imprimir Documentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
							}
		        	    }
		        	});
		    		thesis.addSeparator();
		    		thesis.addItem("Feedback da Banca", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try {
		        	    		ThesisBO bo = new ThesisBO();
								Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(thesis == null){
									thesis = bo.findLastThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
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
		        	});
		    		thesis.addSeparator();
		    		thesis.addItem("Submeter Versão Final", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
							try {
								FinalDocumentBO fbo = new FinalDocumentBO();
								FinalDocument ft = fbo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
								if(ft == null){
									ThesisBO bo = new ThesisBO();
									Thesis thesis = bo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
								
									/*if(thesis == null){
										thesis = bo.findApprovedThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
									}*/
									
									if(thesis == null){
										throw new Exception("É necessário submeter a monografia para avaliação da banca antes.");
									}else{
										ft = new FinalDocument();
										ft.setTitle(thesis.getTitle());
										ft.setThesis(thesis);
									}
								}
								
								UI.getCurrent().addWindow(new EditFinalDocumentWindow(ft, null));
							} catch (Exception e) {
								Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
								
								Notification.show("Submeter Versão Final", e.getMessage(), Notification.Type.ERROR_MESSAGE);
							}
		        	    }
		        	});
		    		
		        	Button button = new Button();
		        	button.setId("thesisFilesDownload");
		        	button.addStyleName("InvisibleButton");
		        	layout.addComponent(button);
			    	this.prepareDownloadStage2(button);
		    	}
		    	if(Session.isUserManager(SystemModule.SIGET)){
		    		thesis.addItem("Listar Monografias", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(ThesisView.NAME);
		        	    }
		        	});
		    	}
			}
	    	
	    	if(Session.isUserStudent()){
	    		MenuItem supervision = this.menu.addItem("Orientação", null);
	    		supervision.addItem("Lista de Orientadores", new Command(){
	        	    @Override
	        	    public void menuSelected(MenuItem selectedItem){
	        	        UI.getCurrent().getNavigator().navigateTo(SupervisorView.NAME);
	        	    }
	        	});
	    		supervision.addSeparator();
	    		supervision.addItem("Registro de Reuniões", new Command(){
	        	    @Override
	        	    public void menuSelected(MenuItem selectedItem){
	        	        UI.getCurrent().getNavigator().navigateTo(AttendanceView.NAME);
	        	    }
	        	});
	    		supervision.addSeparator();
	    		supervision.addItem("Alterar Orientador", new Command(){
	        	    @Override
	        	    public void menuSelected(MenuItem selectedItem){
	        	        try {
	        	        	ProposalBO bo = new ProposalBO();
							Proposal proposal = bo.findCurrentProposal(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
							
							DeadlineBO dbo = new DeadlineBO();
							Deadline d;
							
							if(proposal == null){
								d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
							}else{
								d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), proposal.getSemester(), proposal.getYear());
							}
							
							if(proposal == null){
								Notification.show("Alterar Orientador", "É necessário efetuar a submissão da proposta.", Notification.Type.ERROR_MESSAGE);
							}else if((d == null) || (!DateUtils.getToday().getTime().after(d.getProposalDeadline()))){
								Notification.show("Alterar Orientador", "A submissão de propostas ainda não foi encerrada. Você pode fazer a alteração do orientador no menu Submeter Proposta.", Notification.Type.ERROR_MESSAGE);
							}else{
								UI.getCurrent().addWindow(new EditSupervisorWindow(proposal, null));
							}
						} catch (Exception e) {
							Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
							
							Notification.show("Alterar Orientador", e.getMessage(), Notification.Type.ERROR_MESSAGE);
						}
	        	    }
	        	});
	    	}
	    	
	    	if(Session.isUserProfessor()){
	    		MenuItem professor = this.menu.addItem("Professor", null);
	    		professor.addItem("Registro de Reuniões", new Command(){
	        	    @Override
	        	    public void menuSelected(MenuItem selectedItem){
	        	    	UI.getCurrent().getNavigator().navigateTo(AttendanceView.NAME);
	        	    }
	        	});
	    		professor.addSeparator();
	    		professor.addItem("Agenda de Bancas", new Command(){
	        	    @Override
	        	    public void menuSelected(MenuItem selectedItem){
	        	    	UI.getCurrent().getNavigator().navigateTo(CalendarView.NAME);
	        	    }
	        	});
	    	}
	    	
	    	if(Session.isUserAdministrator() || Session.isUserManager(SystemModule.SIGET)){
	    		MenuItem administration = this.menu.addItem("Administração", null);
	    		if(Session.isUserManager(SystemModule.SIGET)){
	    			administration.addItem("Alterações de Orientador", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(SupervisorChangeView.NAME);
		        	    }
		        	});
	    			administration.addSeparator();
		    		administration.addItem("Definir Datas", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(DeadlineView.NAME);
		        	    }
		        	});
		    		administration.addSeparator();
		    		administration.addItem("Quesitos de Avaliação", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	        UI.getCurrent().getNavigator().navigateTo(EvaluationItemView.NAME);
		        	    }
		        	});
		    		administration.addSeparator();
		    		administration.addItem("Configurações", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try{
		        	    		DepartmentBO bo = new DepartmentBO();
		        	    		Department department = bo.findById(Session.getUser().getDepartment().getIdDepartment());
		        	    		
		        	    		UI.getCurrent().addWindow(new EditSigetWindow(department, null));
		        	    	}catch(Exception e){
		        	    		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		        	    		
		        	    		Notification.show("Carregar Configurações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		        	    	}
		        	    }
		        	});
	    		}
	    	}
	    	
	    	MenuItem repository = this.menu.addItem("Repositório", null);
	    	repository.addItem("Regulamentos de TCC e Anexos", new Command(){
	    	    @Override
	    	    public void menuSelected(MenuItem selectedItem){
	    	        UI.getCurrent().getNavigator().navigateTo(DocumentView.NAME + "/" + String.valueOf(SystemModule.SIGET.getValue()));
	    	    }
	    	});
	    	repository.addSeparator();
	    	repository.addItem("Biblioteca", new Command(){
	    	    @Override
	    	    public void menuSelected(MenuItem selectedItem){
	    	        UI.getCurrent().getNavigator().navigateTo(LibraryView.NAME);
	    	    }
	    	});
	    	repository.addSeparator();
	    	repository.addItem("Sugestões de Projetos", new Command(){
	    	    @Override
	    	    public void menuSelected(MenuItem selectedItem){
	    	        UI.getCurrent().getNavigator().navigateTo(ThemeSuggestionView.NAME);
	    	    }
	    	});
		}else if(module == SystemModule.SIGAC){
			if(Session.isUserManager(SystemModule.SIGAC) || Session.isUserStudent()){
				MenuItem activities = this.menu.addItem("Registro de Atividades", null);
				if(Session.isUserManager(SystemModule.SIGAC)){
					activities.addItem("Validar Atividades", new Command(){
	    	    	    @Override
	    	    	    public void menuSelected(MenuItem selectedItem){
	    	    	        UI.getCurrent().getNavigator().navigateTo(ActivitySubmissionView.NAME);
	    	    	    }
	    	    	});
				}
				if(Session.isUserStudent()){
					activities.addItem("Atividades Registradas", new Command(){
	    	    	    @Override
	    	    	    public void menuSelected(MenuItem selectedItem){
	    	    	    	UI.getCurrent().getNavigator().navigateTo(ActivitySubmissionView.NAME);
	    	    	    }
	    	    	});
				}
			}
			
			if(Session.isUserAdministrator() || Session.isUserManager(SystemModule.SIGAC)){
	    		MenuItem administration = this.menu.addItem("Administração", null);
	    		if(Session.isUserAdministrator()){
	    			administration.addItem("Grupos de Atividades", new Command(){
	    	    	    @Override
	    	    	    public void menuSelected(MenuItem selectedItem){
	    	    	        UI.getCurrent().getNavigator().navigateTo(ActivityGroupView.NAME);
	    	    	    }
	    	    	});
	    			administration.addSeparator();
	    			administration.addItem("Unidades de Atividades", new Command(){
	    	    	    @Override
	    	    	    public void menuSelected(MenuItem selectedItem){
	    	    	        UI.getCurrent().getNavigator().navigateTo(ActivityUnitView.NAME);
	    	    	    }
	    	    	});
	    		}
	    		if(Session.isUserManager(SystemModule.SIGAC)){
	    			if(Session.isUserAdministrator()){
	    				administration.addSeparator();
	    			}
	    			
	    			administration.addItem("Atividades", new Command(){
	    	    	    @Override
	    	    	    public void menuSelected(MenuItem selectedItem){
	    	    	        UI.getCurrent().getNavigator().navigateTo(ActivityView.NAME);
	    	    	    }
	    	    	});
	    			administration.addSeparator();
		    		administration.addItem("Configurações", new Command(){
		        	    @Override
		        	    public void menuSelected(MenuItem selectedItem){
		        	    	try{
		        	    		DepartmentBO bo = new DepartmentBO();
		        	    		Department department = bo.findById(Session.getUser().getDepartment().getIdDepartment());
		        	    		
		        	    		UI.getCurrent().addWindow(new EditSigacWindow(department, null));
		        	    	}catch(Exception e){
		        	    		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		        	    		
		        	    		Notification.show("Carregar Configurações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		        	    	}
		        	    }
		        	});
	    		}
			}
		}else if(module == SystemModule.SIGES){
			
		}else{
			if(Session.isUserAdministrator()){
				MenuItem administration = this.menu.addItem("Administração", null);
				administration.addItem("Câmpus", new Command(){
    	    	    @Override
    	    	    public void menuSelected(MenuItem selectedItem){
    	    	        UI.getCurrent().getNavigator().navigateTo(CampusView.NAME);
    	    	    }
    	    	});
				administration.addSeparator();
				administration.addItem("Departamentos", new Command(){
    	    	    @Override
    	    	    public void menuSelected(MenuItem selectedItem){
    	    	        UI.getCurrent().getNavigator().navigateTo(DepartmentView.NAME);
    	    	    }
    	    	});
				administration.addSeparator();
				administration.addItem("Envio de E-mails", new Command(){
    	    	    @Override
    	    	    public void menuSelected(MenuItem selectedItem){
    	    	        UI.getCurrent().getNavigator().navigateTo(EmailMessageView.NAME);
    	    	    }
    	    	});
				administration.addSeparator();
	    		administration.addItem("Usuários", new Command(){
	        	    @Override
	        	    public void menuSelected(MenuItem selectedItem){
	        	        UI.getCurrent().getNavigator().navigateTo(UserView.NAME);
	        	    }
	        	});
			}
		}
		
		MenuItem account = this.menu.addItem("Minha Conta", null);
    	account.addItem("Meus Dados", new Command(){
    	    @Override
    	    public void menuSelected(MenuItem selectedItem){
    	    	UI.getCurrent().addWindow(new EditUserWindow(Session.getUser(), null));
    	    }
    	});
		account.addSeparator();
    	if((Session.getUser() != null) && Session.getUser().isExternal()){
    		account.addItem("Alterar Senha", new Command(){
        	    @Override
        	    public void menuSelected(MenuItem selectedItem){
        	    	UI.getCurrent().addWindow(new EditPasswordWindow());
        	    }
        	});
    		account.addSeparator();
    	}
    	account.addItem("Logoff", new Command(){
    	    @Override
    	    public void menuSelected(MenuItem selectedItem){
    	        logoff();
    	    }
    	});
    	
    	MenuItem help = this.menu.addItem("", new ThemeResource("images/help.png"), null);
    	help.addItem("Reportar Erro", new Command(){
    	    @Override
    	    public void menuSelected(MenuItem selectedItem){
    	    	UI.getCurrent().getNavigator().navigateTo(BugReportView.NAME);
    	    }
    	});
    	help.addSeparator();
    	help.addItem("Sobre o Sistema", new Command(){
    	    @Override
    	    public void menuSelected(MenuItem selectedItem){
    	    	UI.getCurrent().addWindow(new AboutWindow());
    	    }
    	});
    	
    	this.setCompositionRoot(layout);
	}
	
	private void logoff(){
    	// "Logout" the user
        getSession().setAttribute("user", null);

        // Refresh this view, should redirect to login view
        getUI().getNavigator().navigateTo(LoginView.NAME);
    }
	
	private void prepareDownloadStage1(Button buttonToExtend){
		try {
			ProjectBO pbo = new ProjectBO();
			Project project = pbo.findCurrentProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
			
			if(project == null){
				project = pbo.findLastProject(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
			}
			
			if(project != null){
				AttendanceBO abo = new AttendanceBO();
				AttendanceReport attendance = abo.getReport(Session.getUser().getIdUser(), project.getProposal().getIdProposal(), project.getSupervisor().getIdUser(), 1);
				
				List<AttendanceReport> list = new ArrayList<AttendanceReport>();
				list.add(attendance);
				
				new ReportUtils().prepareForPdfReport("Attendances", "Acompanhamento", list, buttonToExtend);
				
				List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
				list2.add(pbo.getSupervisorFeedbackReport(project));
				
				new ReportUtils().prepareForPdfReport("SupervisorFeedback", "Parecer do Orientador", list2, buttonToExtend, false);
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Download de Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownloadStage2(Button buttonToExtend){
		try {
			ThesisBO tbo = new ThesisBO();
			Thesis thesis = tbo.findCurrentThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
			
			if(thesis == null){
				thesis = tbo.findLastThesis(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
			}
			
			if(thesis != null){
				ProjectBO pbo = new ProjectBO();
				Project project = pbo.findById(thesis.getProject().getIdProject());
				
				AttendanceBO abo = new AttendanceBO();
				AttendanceReport attendance = abo.getReport(Session.getUser().getIdUser(), project.getProposal().getIdProposal(), thesis.getSupervisor().getIdUser(), 2);
				
				List<AttendanceReport> list = new ArrayList<AttendanceReport>();
				list.add(attendance);
				
				new ReportUtils().prepareForPdfReport("Attendances", "Acompanhamento", list, buttonToExtend);
				
				List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
				list2.add(tbo.getSupervisorFeedbackReport(thesis));
				
				new ReportUtils().prepareForPdfReport("SupervisorFeedback", "Parecer do Orientador", list2, buttonToExtend, false);
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Download de Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
