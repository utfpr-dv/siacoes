package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditTutoredWindow extends EditWindow {
	
	private final Proposal proposal;
	private final Project project;
	private final Thesis thesis;
	private final FinalDocument projectFinalDocument;
	private final FinalDocument thesisFinalDocument;
	
	private final TextField textStudent;
	private final TextField textDepartment;
	
	private final TextField textProposalTitle;
	private final TextField textProposalSubarea;
	private final TextField textProposalSupervisor;
	private final TextField textProposalCosupervisor;
	private final TextField textProposalSemester;
	private final DateField textProposalSubmissionDate;
	private final Button buttonProposalDownloadFile;
	private final Button buttonProposalFeedback;
	
	private final TextField textProjectTitle;
	private final TextField textProjectSubarea;
	private final TextField textProjectSupervisor;
	private final TextField textProjectCosupervisor;
	private final TextField textProjectSemester;
	private final DateField textProjectSubmissionDate;
	private final Button buttonProjectDownloadFile;
	private final Button buttonProjectFinalDocument;
	private final Button buttonProjectFeedback;
	private final TextArea textProjectAbstract;
	
	private final TextField textThesisTitle;
	private final TextField textThesisSubarea;
	private final TextField textThesisSupervisor;
	private final TextField textThesisCosupervisor;
	private final TextField textThesisSemester;
	private final DateField textThesisSubmissionDate;
	private final Button buttonThesisDownloadFile;
	private final Button buttonThesisFinalDocument;
	private final Button buttonThesisFeedback;
	private final TextArea textThesisAbstract;
	
	private final TabSheet tab;
	
	public EditTutoredWindow(int idProposal, ListView parentView){
		super("Visualizar Orientado", parentView);
		
		this.setSaveButtonVisible(false);
		
		Proposal proposal = new Proposal();
		Project project = new Project();
		Thesis thesis = new Thesis();
		FinalDocument projectFinalDocument = new FinalDocument();
		FinalDocument thesisFinalDocument = new FinalDocument();
		Semester semester = new Semester();
		
		try {
			ProposalBO bo = new ProposalBO();
			proposal = bo.findById(idProposal);
		} catch (Exception e) {
			this.showErrorNotification("Carregar Orientado", e.getMessage());
		}
		
		try {
			ProjectBO bo = new ProjectBO();
			project = bo.findByProposal(idProposal);
		} catch (Exception e) {
			this.showErrorNotification("Carregar Orientado", e.getMessage());
		}
		
		try {
			ThesisBO bo = new ThesisBO();
			thesis = bo.findByProposal(idProposal);
		} catch (Exception e) {
			this.showErrorNotification("Carregar Orientado", e.getMessage());
		}
		
		if((project != null) && (project.getIdProject() != 0)){
			try {
				FinalDocumentBO bo = new FinalDocumentBO();
				projectFinalDocument = bo.findByProject(project.getIdProject());
			} catch (Exception e) {
				this.showErrorNotification("Carregar Orientado", e.getMessage());
			}
		}
		
		if((thesis != null) && (thesis.getIdThesis() != 0)){
			try {
				FinalDocumentBO bo = new FinalDocumentBO();
				thesisFinalDocument = bo.findByThesis(thesis.getIdThesis());
			} catch (Exception e) {
				this.showErrorNotification("Carregar Orientado", e.getMessage());
			}
		}
		
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			this.showErrorNotification("Carregar Orientado", e.getMessage());
		}
		
		this.proposal = proposal;
		this.project = project;
		this.thesis = thesis;
		this.projectFinalDocument = projectFinalDocument;
		this.thesisFinalDocument = thesisFinalDocument;
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("400px");
		this.textStudent.setEnabled(false);
		
		this.textDepartment = new TextField("Departamento");
		this.textDepartment.setWidth("400px");
		this.textDepartment.setEnabled(false);
		
		this.addField(new HorizontalLayout(this.textStudent, this.textDepartment));
		
		this.textProposalTitle = new TextField("Título");
		this.textProposalTitle.setWidth("400px");
		this.textProposalTitle.setEnabled(false);
		
		this.textProposalSubarea = new TextField("Subárea");
		this.textProposalSubarea.setWidth("400px");
		this.textProposalSubarea.setEnabled(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textProposalTitle, this.textProposalSubarea);
		h1.setSpacing(true);
		
		this.textProposalSupervisor = new TextField("Orientador");
		this.textProposalSupervisor.setWidth("400px");
		this.textProposalSupervisor.setEnabled(false);
		
		this.textProposalCosupervisor = new TextField("Coorientador");
		this.textProposalCosupervisor.setWidth("400px");
		this.textProposalCosupervisor.setEnabled(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textProposalSupervisor, this.textProposalCosupervisor);
		h2.setSpacing(true);
		
		this.textProposalSemester = new TextField("Sem./Ano");
		this.textProposalSemester.setWidth("100px");
		this.textProposalSemester.setEnabled(false);
		
		this.textProposalSubmissionDate = new DateField("Submissão");
		this.textProposalSubmissionDate.setEnabled(false);
		this.textProposalSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		this.buttonProposalDownloadFile = new Button("Download da Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProposal();
            }
        });
		this.buttonProposalDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		try {
			this.buttonProposalDownloadFile.setVisible(new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment()).isRegisterProposal());
		} catch (Exception e) {
			this.buttonProposalDownloadFile.setVisible(false);
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.buttonProposalFeedback = new Button("Feedback da Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	openProposalFeedback();
            }
        });
		this.buttonProposalFeedback.setVisible(this.buttonProposalDownloadFile.isVisible());
		this.buttonProposalFeedback.setIcon(FontAwesome.CHECK);
		
		HorizontalLayout h3 = new HorizontalLayout(this.textProposalSemester, this.textProposalSubmissionDate, this.buttonProposalDownloadFile, this.buttonProposalFeedback);
		h3.setSpacing(true);
		
		VerticalLayout tab1 = new VerticalLayout(h1, h2, h3);
		tab1.setSpacing(true);
		
		this.textProjectTitle = new TextField("Título");
		this.textProjectTitle.setWidth("400px");
		this.textProjectTitle.setEnabled(false);
		
		this.textProjectSubarea = new TextField("Subárea");
		this.textProjectSubarea.setWidth("400px");
		this.textProjectSubarea.setEnabled(false);
		
		HorizontalLayout h4 = new HorizontalLayout(this.textProjectTitle, this.textProjectSubarea);
		h4.setSpacing(true);
		
		this.textProjectSupervisor = new TextField("Orientador");
		this.textProjectSupervisor.setWidth("400px");
		this.textProjectSupervisor.setEnabled(false);
		
		this.textProjectCosupervisor = new TextField("Coorientador");
		this.textProjectCosupervisor.setWidth("400px");
		this.textProjectCosupervisor.setEnabled(false);
		
		HorizontalLayout h5 = new HorizontalLayout(this.textProjectSupervisor, this.textProjectCosupervisor);
		h5.setSpacing(true);
		
		this.textProjectSemester = new TextField("Sem./Ano");
		this.textProjectSemester.setWidth("100px");
		this.textProjectSemester.setEnabled(false);
		
		this.textProjectSubmissionDate = new DateField("Submissão");
		this.textProjectSubmissionDate.setEnabled(false);
		this.textProjectSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		this.buttonProjectDownloadFile = new Button("Download do Projeto", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProject();
            }
        });
		this.buttonProjectDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		
		this.buttonProjectFeedback = new Button("Feedback do Projeto", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	openProjectFeedback();
            }
        });
		this.buttonProjectFeedback.setIcon(FontAwesome.CHECK);
		
		this.buttonProjectFinalDocument = new Button("Validar Versão Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().addWindow(new EditFinalDocumentWindow(EditTutoredWindow.this.projectFinalDocument, null));
            }
        });
		this.buttonProjectFinalDocument.setIcon(FontAwesome.CHECK);
		this.buttonProjectFinalDocument.setVisible((this.projectFinalDocument != null) && (this.projectFinalDocument.getIdFinalDocument() != 0));
		
		HorizontalLayout h6 = new HorizontalLayout(this.textProjectSemester, this.textProjectSubmissionDate, this.buttonProjectDownloadFile, this.buttonProjectFeedback, this.buttonProjectFinalDocument);
		h6.setSpacing(true);
		
		this.textProjectAbstract = new TextArea("Resumo");
		this.textProjectAbstract.setWidth("810px");
		this.textProjectAbstract.setHeight("100px");
		this.textProjectAbstract.setEnabled(false);
		
		VerticalLayout tab2 = new VerticalLayout(h4, h5, h6, this.textProjectAbstract);
		tab2.setSpacing(true);
		
		this.textThesisTitle = new TextField("Título");
		this.textThesisTitle.setWidth("400px");
		this.textThesisTitle.setEnabled(false);
		
		this.textThesisSubarea = new TextField("Subárea");
		this.textThesisSubarea.setWidth("400px");
		this.textThesisSubarea.setEnabled(false);
		
		HorizontalLayout h7 = new HorizontalLayout(this.textThesisTitle, this.textThesisSubarea);
		h7.setSpacing(true);
		
		this.textThesisSupervisor = new TextField("Orientador");
		this.textThesisSupervisor.setWidth("400px");
		this.textThesisSupervisor.setEnabled(false);
		
		this.textThesisCosupervisor = new TextField("Coorientador");
		this.textThesisCosupervisor.setWidth("400px");
		this.textThesisCosupervisor.setEnabled(false);
		
		HorizontalLayout h8 = new HorizontalLayout(this.textThesisSupervisor, this.textThesisCosupervisor);
		h8.setSpacing(true);
		
		this.textThesisSemester = new TextField("Sem./Ano");
		this.textThesisSemester.setWidth("100px");
		this.textThesisSemester.setEnabled(false);
		
		this.textThesisSubmissionDate = new DateField("Submissão");
		this.textThesisSubmissionDate.setEnabled(false);
		this.textThesisSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		this.buttonThesisDownloadFile = new Button("Download da Monografia", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadThesis();
            }
        });
		this.buttonThesisDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		
		this.buttonThesisFeedback = new Button("Feedback da Monografia", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	openThesisFeedback();
            }
        });
		this.buttonThesisFeedback.setIcon(FontAwesome.CHECK);
		
		this.buttonThesisFinalDocument = new Button("Validar Versão Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().addWindow(new EditFinalDocumentWindow(EditTutoredWindow.this.thesisFinalDocument, null));
            }
        });
		this.buttonThesisFinalDocument.setIcon(FontAwesome.CHECK);
		this.buttonThesisFinalDocument.setVisible((this.thesisFinalDocument != null) && (this.thesisFinalDocument.getIdFinalDocument() != 0));
		
		HorizontalLayout h9 = new HorizontalLayout(this.textThesisSemester, this.textThesisSubmissionDate, this.buttonThesisDownloadFile, this.buttonThesisFeedback, this.buttonThesisFinalDocument);
		h9.setSpacing(true);
		
		this.textThesisAbstract = new TextArea("Resumo");
		this.textThesisAbstract.setWidth("810px");
		this.textThesisAbstract.setHeight("100px");
		this.textThesisAbstract.setEnabled(false);
		
		VerticalLayout tab3 = new VerticalLayout(h7, h8, h9, this.textThesisAbstract);
		tab3.setSpacing(true);
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		this.tab.addTab(tab1, "Proposta de TCC 1");
		if((this.project != null) && (this.project.getIdProject() != 0)){
			this.tab.addTab(tab2, "Projeto de TCC 1");
		}
		if((this.thesis != null) && (this.thesis.getIdThesis() != 0)){
			this.tab.addTab(tab3, "Monografia de TCC 2");
		}
		this.tab.setHeight("340px");
		
		this.addField(this.tab);
		
		this.loadTutored();
	}
	
	private void loadTutored(){
		this.textStudent.setValue(this.proposal.getStudent().getName());
		this.textDepartment.setValue(this.proposal.getDepartment().getName());
		
		this.textProposalTitle.setValue(this.proposal.getTitle());
		this.textProposalSubarea.setValue(this.proposal.getSubarea());
		this.textProposalSupervisor.setValue(this.proposal.getSupervisor().getName());
		this.textProposalCosupervisor.setValue(this.proposal.getCosupervisor().getName());
		this.textProposalSemester.setValue(String.valueOf(this.proposal.getSemester()) + "/" + String.valueOf(this.proposal.getYear()));
		this.textProposalSubmissionDate.setValue(this.proposal.getSubmissionDate());
		
		if(this.proposal.getFile() == null) {
			this.buttonProposalDownloadFile.setVisible(false);
			this.buttonProposalFeedback.setVisible(false);
		}
		
		if((this.project != null) && (this.project.getIdProject() != 0)){
			this.textProjectTitle.setValue(this.project.getTitle());
			this.textProjectSubarea.setValue(this.project.getSubarea());
			this.textProjectSupervisor.setValue(this.project.getSupervisor().getName());
			this.textProjectCosupervisor.setValue(this.project.getCosupervisor().getName());
			this.textProjectSemester.setValue(String.valueOf(this.project.getSemester()) + "/" + String.valueOf(this.project.getYear()));
			this.textProjectSubmissionDate.setValue(this.project.getSubmissionDate());
			this.textProjectAbstract.setValue(this.project.getAbstract());
		}
		
		if((this.thesis != null) && (this.thesis.getIdThesis() != 0)){
			this.textThesisTitle.setValue(this.thesis.getTitle());
			this.textThesisSubarea.setValue(this.thesis.getSubarea());
			this.textThesisSupervisor.setValue(this.thesis.getSupervisor().getName());
			this.textThesisCosupervisor.setValue(this.thesis.getCosupervisor().getName());
			this.textThesisSemester.setValue(String.valueOf(this.thesis.getSemester()) + "/" + String.valueOf(this.thesis.getYear()));
			this.textThesisSubmissionDate.setValue(this.thesis.getSubmissionDate());
			this.textThesisAbstract.setValue(this.thesis.getAbstract());
		}
	}
	
	private void downloadProposal() {
		try {
        	this.showReport(this.proposal.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
	private void downloadProject() {
		try {
        	this.showReport(this.project.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
	private void downloadThesis() {
		try {
        	this.showReport(this.thesis.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
	private void openProposalFeedback() {
		UI.getCurrent().addWindow(new DownloadProposalFeedbackWindow(this.proposal));
	}
	
	private void openProjectFeedback() {
		try {
			JuryBO jbo = new JuryBO();
			Jury jury = jbo.findByProject(this.project.getIdProject());
			
			if(jury == null){
				this.showErrorNotification("Feedback da Banca", "A banca examinadora da monografia ainda não foi agendada.");
			}else{
				UI.getCurrent().addWindow(new DownloadFeedbackWindow(jury));
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Feedback da Banca", e.getMessage());
		}
	}
	
	private void openThesisFeedback() {
		try {
			JuryBO jbo = new JuryBO();
			Jury jury = jbo.findByThesis(this.thesis.getIdThesis());
			
			if(jury == null){
				this.showErrorNotification("Feedback da Banca", "A banca examinadora da monografia ainda não foi agendada.");
			}else{
				UI.getCurrent().addWindow(new DownloadFeedbackWindow(jury));
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Feedback da Banca", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

}
