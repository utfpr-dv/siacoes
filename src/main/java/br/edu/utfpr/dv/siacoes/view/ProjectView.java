package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditProjectWindow;

public class ProjectView extends ListView {

	public static final String NAME = "project";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonJury;
	private final Button buttonDownloadProject;
	private final Button buttonStatements;
	private final Button buttonSendFeedback;
	private final Button buttonSupervisorStatement;
	private final Button buttonCosupervisorStatement;
	private final Button buttonGrades;
	
	public ProjectView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Projetos de TCC 1");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.buttonDownloadProject = new Button("Projeto", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownloadProject.setIcon(FontAwesome.DOWNLOAD);
		this.addActionButton(this.buttonDownloadProject);
		
		this.buttonJury = new Button("Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryClick();
            }
        });
		this.buttonJury.setIcon(FontAwesome.CALENDAR_CHECK_O);
		
		this.buttonSendFeedback = new Button("Feedback", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sendFeedbackClick();
            }
        });
		this.buttonSendFeedback.setIcon(FontAwesome.UPLOAD);
		
		this.buttonStatements = new Button("Declarações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStatements();
            }
        });
		this.buttonStatements.setIcon(FontAwesome.FILE_PDF_O);
		
		this.buttonSupervisorStatement = new Button("Dec. Orientação", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadSupervisorStatement();
            }
        });
		this.buttonSupervisorStatement.setIcon(FontAwesome.FILE_PDF_O);
		
		this.buttonCosupervisorStatement = new Button("Dec. Coorientação", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadCosupervisorStatement();
            }
        });
		this.buttonCosupervisorStatement.setIcon(FontAwesome.FILE_PDF_O);
		
		this.buttonGrades = new Button("Relat. de Notas", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryGradesReport();
            }
        });
		this.buttonGrades.setIcon(FontAwesome.FILE_PDF_O);
		
		if(Session.isUserProfessor()){
			this.addActionButton(this.buttonSendFeedback);
		}
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonJury);
			this.addActionButton(this.buttonStatements);
			this.addActionButton(this.buttonSupervisorStatement);
			this.addActionButton(this.buttonCosupervisorStatement);
			this.addActionButton(this.buttonGrades);
		}
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(FontAwesome.SEARCH);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Orientador", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(5).setWidth(125);
		
		try {
			ProjectBO bo = new ProjectBO();
	    	List<Project> list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Project p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getStudent().getName(), p.getSupervisor().getName(), p.getTitle(), p.getSubmissionDate());
				this.addRowId(itemId, p.getIdProject());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Projetos", e.getMessage());
		}
	}
	
	private void downloadSupervisorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				ProjectBO pbo = new ProjectBO();
				CertificateBO bo = new CertificateBO();
				
				Project project = pbo.findById((int)value);

				byte[] report = bo.getThesisProfessorStatement(project.getSupervisor(), project);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadCosupervisorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				ProjectBO pbo = new ProjectBO();
				CertificateBO bo = new CertificateBO();
				
				Project project = pbo.findById((int)value);

				if(project.getCosupervisor() == null){
					this.showWarningNotification("Gerar Declaração", "Não foi indicado um coorientador para o projeto.");
				}else{
					byte[] report = bo.getThesisProfessorStatement(project.getCosupervisor(), project);
					
					this.showReport(report);
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadStatements(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declarações", "Selecione uma banca para gerar as declarações.");
		}else{
			try{
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				PDFMergerUtility pdfMerge = new PDFMergerUtility();
				pdfMerge.setDestinationStream(output);
				
				CertificateBO bo = new CertificateBO();

				byte[] reportProfessor = bo.getJuryProfessorStatementReportListByProject((int)value);
				
				if(reportProfessor != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportProfessor));
				}
				
				byte[] reportStudent = bo.getJuryStudentStatementReportListByProject((int)value);
				
				if(reportStudent != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportStudent));
				}
				
				if((reportProfessor != null) || (reportStudent != null)){
					pdfMerge.mergeDocuments(null);
					
					byte[] report = output.toByteArray();
					
					this.showReport(report);
				}else{
					this.showWarningNotification("Gerar Declarações", "Não há declarações para serem geradas.");
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declarações", e.getMessage());
			}
		}
	}
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Download do Projeto", "Selecione um registro para baixar o projeto.");
		}else{
			try{
				ProjectBO bo = new ProjectBO();
            	Project p = bo.findById((int)value);
				
				this.showReport(p.getFile());
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Download do Projeto", e.getMessage());
			}
		}
	}
	
	private void juryClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para marcar a banca.");
    	}else{
    		try {
    			JuryBO bo = new JuryBO();
				Jury jury = bo.findByProject((int)id);
				
				UI.getCurrent().addWindow(new EditJuryWindow(jury, this));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Marcar Banca", e.getMessage());
			}
    	}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Enviar Feedback", "Selecione o registro para enviar o feedback.");
    	}else{
    		try {
    			JuryBO bo = new JuryBO();
				Jury jury = bo.findByProject((int)id);
				
				JuryAppraiserBO bo2 = new JuryAppraiserBO();
    			
    			JuryAppraiser appraiser = bo2.findByAppraiser(jury.getIdJury(), Session.getUser().getIdUser());
    			
    			if(appraiser != null){
    				UI.getCurrent().addWindow(new EditJuryAppraiserFeedbackWindow(appraiser));
    			}else{
    				this.showWarningNotification("Enviar Feedback", "É necessário ser membro da banca para enviar o feedback.");
    			}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Enviar Feedback", e.getMessage());
			}
    	}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), 1, true));
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Relatório de Notas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			ProjectBO bo = new ProjectBO();
			Project p = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditProjectWindow(p, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Projeto", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
