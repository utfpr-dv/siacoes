package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;

public class JuryView extends ListView {
	
	public static final String NAME = "jury";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final StageComboBox comboStage;
	private final Button buttonFile;
	private final Button buttonForm;
	private final Button buttonTerm;
	private final Button buttonSchedule;
	private final Button buttonStatements;
	private final Button buttonSingleStatement;
	private final Button buttonSendFeedback;
	private final Button buttonParticipants;
	private final Button buttonParticipantsReport;
	private final Button buttonGrades;
	
	private boolean listAll = false;

	public JuryView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Agenda de Bancas de TCC");
		
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
		
		this.comboStage = new StageComboBox(true);
		this.comboStage.selectBoth();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonFile = new Button("Down. do Trabalho", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonFile.setIcon(FontAwesome.DOWNLOAD);
		
		this.buttonForm = new Button("Ficha de Avaliação", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadForm();
            }
        });
		this.buttonForm.setIcon(FontAwesome.CLIPBOARD);
		
		this.buttonParticipants = new Button("Lista de Presença", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadParticipants();
            }
        });
		this.buttonParticipants.setIcon(FontAwesome.LIST_ALT);
		
		this.buttonParticipantsReport = new Button("Lista de Ouvintes", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadParticipantsReport();
            }
        });
		this.buttonParticipantsReport.setIcon(FontAwesome.FILE_PDF_O);
		
		this.buttonTerm = new Button("Termo de Aprovação", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadTerm();
            }
        });
		
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
		
		this.buttonSingleStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadSingleStatement();
            }
        });
		this.buttonSingleStatement.setIcon(FontAwesome.FILE_PDF_O);
		
		this.buttonSchedule = new Button("Imprimir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	printSchedule();
            }
        });
		this.buttonSchedule.setIcon(FontAwesome.PRINT);
		
		this.buttonGrades = new Button("Relat. de Notas", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryGradesReport();
            }
        });
		this.buttonGrades.setIcon(FontAwesome.FILE_PDF_O);
		
		this.addActionButton(this.buttonSchedule);
		this.addActionButton(this.buttonFile);
		this.addActionButton(this.buttonForm);
		this.addActionButton(this.buttonParticipants);
		this.addActionButton(this.buttonParticipantsReport);
		this.addActionButton(this.buttonTerm);
		this.addActionButton(this.buttonSendFeedback);
		this.addActionButton(this.buttonSingleStatement);
		
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonStatements);
			this.addActionButton(this.buttonGrades);
			this.setEditVisible(true);
			this.setEditCaption("Banca");
			this.setEditIcon(FontAwesome.CALENDAR_CHECK_O);
		}
	}
	
	private void configureButtons(){
		if(this.listAll){
			this.buttonSendFeedback.setVisible(false);
			this.buttonTerm.setVisible(Session.isUserManager(this.getModule()));
			this.buttonForm.setVisible(Session.isUserManager(this.getModule()));
			this.buttonParticipants.setVisible(Session.isUserManager(this.getModule()));
			this.buttonParticipantsReport.setVisible(Session.isUserManager(this.getModule()));
			this.buttonSingleStatement.setVisible(false);
			this.buttonFile.setVisible(Session.isUserManager(this.getModule()));
		}else{
			this.buttonSendFeedback.setVisible(true);
			this.buttonTerm.setVisible(false);
			this.buttonFile.setVisible(Session.isUserProfessor());
			this.buttonForm.setVisible(Session.isUserProfessor());
			this.buttonParticipants.setVisible(Session.isUserProfessor());
			this.buttonParticipantsReport.setVisible(Session.isUserProfessor());
			this.buttonSendFeedback.setVisible(Session.isUserProfessor());
			this.buttonStatements.setVisible(false);
			this.buttonSchedule.setVisible(Session.isUserProfessor() || Session.isUserSupervisor());
			this.buttonGrades.setVisible(false);
			this.comboStage.setVisible(false);
		}
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
		this.getGrid().addColumn("Local", String.class);
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Membro", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().getColumns().get(0).setWidth(150);
		this.getGrid().getColumns().get(1).setWidth(200);
		this.getGrid().getColumns().get(2).setWidth(50);
		this.getGrid().getColumns().get(3).setWidth(100);
		
		if(Session.isUserStudent() || this.listAll) {
			this.getGrid().getColumns().get(3).setHidden(true);
		}
		
		try {
			JuryBO bo = new JuryBO();
			List<Jury> list;
			
			if(this.listAll){
				list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage());
			}else{
				if(Session.isUserProfessor() || Session.isUserSupervisor()){
					list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}else{
					list = bo.listByStudent(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
	    	for(Jury jury : list){
	    		String title = "";
	    		String student = "";
	    		String member = "";
	    		
	    		if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)){
	    			ThesisBO tbo = new ThesisBO();
	    			Thesis thesis = tbo.findById(jury.getThesis().getIdThesis());
	    			
	    			title = thesis.getTitle();
	    			student = thesis.getStudent().getName();
	    		}else{
	    			ProjectBO pbo = new ProjectBO();
	    			Project project = pbo.findById(jury.getProject().getIdProject());
	    			
	    			title = project.getTitle();
	    			student = project.getStudent().getName();
	    		}
	    		
	    		if((Session.isUserProfessor() || Session.isUserSupervisor()) && !this.listAll) {
	    			JuryAppraiser appraiser = new JuryAppraiserBO().findByAppraiser(jury.getIdJury(), Session.getUser().getIdUser());
	    			member = (appraiser.isSubstitute() ? "Suplente" : (appraiser.isChair() ? "Presidente" : "Titular"));
	    		}
	    		
				Object itemId = this.getGrid().addRow(jury.getDate(), jury.getLocal(), jury.getStage(), member, student, title);
				this.addRowId(itemId, jury.getIdJury());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Bancas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Download do Trabalho", "Selecione uma banca para fazer o download do trabalho.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				JuryBO bo = new JuryBO();
				Jury jury = bo.findById((int)value);
				
				byte[] file = null;
				
				if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)){
	    			ThesisBO tbo = new ThesisBO();
	    			Thesis thesis = tbo.findById(jury.getThesis().getIdThesis());
	    			
	    			file = thesis.getFile();
	    		}else{
	    			ProjectBO pbo = new ProjectBO();
	    			Project project = pbo.findById(jury.getProject().getIdProject());
	    			
	    			file = project.getFile();
	    		}
				
				this.showReport(file);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Download do Trabalho", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadTerm(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Imprimir Termo de Aprovação", "Selecione uma banca para gerar o termo de aprovação.", Notification.Type.WARNING_MESSAGE);
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Imprimir Termo de Aprovação", "Selecione o modo de geração para o Termo de Aprovação.\n\n" +
					"Versão Impressa: documento que deverá ser assinado e armazenado na coordenação.\n" + 
					"Versão Eletrônica: documento sem assinaturas que deverá ser incluído na versão final entregue pelo acadêmico.", "Versão Impressa", "Versão Eletrônica", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog d) {
                	try{
        				showReport(new JuryBO().getTermOfApproval((int)value, !d.isConfirmed()));
                	}catch(Exception e){
        				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	        	
        	        	Notification.show("Imprimir Termo de Aprovação", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        			}
                }
            });
		}
	}
	
	private void downloadForm(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Imprimir Ficha de Avaliação", "Selecione uma banca para gerar a ficha de avaliação.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				this.showReport(new JuryBO().getJuryForm((int)value));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Imprimir Ficha de Avaliação", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadParticipants(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Imprimir Lista de Presença", "Selecione uma banca para gerar a lista de presença.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				JuryBO bo = new JuryBO();
				
				this.showReport(bo.getJuryParticipantsSignature((int)value));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Imprimir Lista de Presença", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadParticipantsReport(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Imprimir Lista de Acadêmicos Ouvintes", "Selecione uma banca para gerar a lista de acadêmicos ouvintes.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				JuryBO bo = new JuryBO();
				
				this.showReport(bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (int)value, 0, 0, true));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Imprimir Lista de Acadêmicos Ouvintes", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para enviar o feedback.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try {
    			JuryAppraiserBO bo = new JuryAppraiserBO();
    			
    			JuryAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
    			
    			if(appraiser != null){
    				UI.getCurrent().addWindow(new EditJuryAppraiserFeedbackWindow(appraiser));
    			}else{
    				Notification.show("Enviar Feedback", "É necessário ser membro da banca para enviar o feedback.", Notification.Type.WARNING_MESSAGE);
    			}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Enviar Feedback", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
    	}
	}
	
	private void downloadSingleStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione uma banca para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report;
				
				if(Session.isUserProfessor()){
					JuryAppraiserBO jbo = new JuryAppraiserBO();
					JuryAppraiser appraiser = jbo.findByAppraiser((int)value, Session.getUser().getIdUser());
					
					report = bo.getJuryProfessorStatement(appraiser);
				}else{
					JuryStudentBO jbo = new JuryStudentBO();
					JuryStudent student = jbo.findByStudent((int)value, Session.getUser().getIdUser());
					
					report = bo.getJuryStudentStatement(student);
				}
				
				if(report != null){
					this.showReport(report);
				}else{
					Notification.show("Gerar Declaração", "Não foi encontrada a declaração para imprimir.", Notification.Type.WARNING_MESSAGE);
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadStatements(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declarações", "Selecione uma banca para gerar as declarações.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				PDFMergerUtility pdfMerge = new PDFMergerUtility();
				pdfMerge.setDestinationStream(output);
				
				CertificateBO bo = new CertificateBO();

				byte[] reportProfessor = bo.getJuryProfessorStatementReportList((int)value);
				
				if(reportProfessor != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportProfessor));
				}
				
				byte[] reportStudent = bo.getJuryStudentStatementReportList((int)value);
				
				if(reportStudent != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportStudent));
				}
				
				if((reportProfessor != null) || (reportStudent != null)){
					pdfMerge.mergeDocuments(null);
					
					byte[] report = output.toByteArray();
					
					this.showReport(report);
				}else{
					Notification.show("Gerar Declarações", "Não há declarações para serem geradas.", Notification.Type.WARNING_MESSAGE);
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declarações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void printSchedule() {
		try {
			JuryBO bo = new JuryBO();
			byte[] report = null;
			
			if(this.listAll) {
				report = bo.getScheduleReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage());
			} else if(Session.isUserProfessor() || Session.isUserSupervisor()){
				report = bo.getScheduleReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear(), 0);
			}
			
			this.showReport(report);
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	Notification.show("Imprimir Agenda de Bancas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage(), false));
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	Notification.show("Relatório de Notas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			JuryBO bo = new JuryBO();
			Jury jury = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditJuryWindow(jury, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Marcar Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
	
	@Override
	public void enter(ViewChangeEvent event){
		if((event.getParameters() != null) && (!event.getParameters().isEmpty())){
			try{
				int i = Integer.parseInt(event.getParameters().trim());
				
				this.listAll = (i == 1);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.listAll = false;
			}
		}
		
		this.configureButtons();
		
		super.enter(event);
	}

}
