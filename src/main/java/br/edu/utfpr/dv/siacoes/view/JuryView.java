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
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryAppraiserScoreWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.window.JuryAppraiserChangeWindow;
import br.edu.utfpr.dv.siacoes.window.JuryGradesWindow;

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
	private final Button buttonChangeAppraiser;
	private final Button buttonFillGrades;
	private final Button buttonSign;
	
	private boolean listAll = false;
	private SigetConfig config;

	public JuryView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Agenda de Bancas de TCC");
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			semester = new Semester();
		}
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
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
		
		this.buttonChangeAppraiser = new Button("Alterar Membros", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	changeAppraiser();
            }
        });
		this.buttonChangeAppraiser.setIcon(FontAwesome.USERS);
		
		this.buttonFillGrades = new Button("Lançar Notas", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addScore();
            }
        });
		this.buttonFillGrades.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.buttonFillGrades.setIcon(FontAwesome.CALCULATOR);
		
		this.buttonSign = new Button("Assinar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sign();
            }
        });
		this.buttonSign.setIcon(FontAwesome.PENCIL);
		this.buttonSign.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		this.addActionButton(this.buttonSchedule);
		this.addActionButton(this.buttonChangeAppraiser);
		this.addActionButton(this.buttonFile);
		this.addActionButton(this.buttonForm);
		this.addActionButton(this.buttonFillGrades);
		this.addActionButton(this.buttonSign);
		this.addActionButton(this.buttonSendFeedback);
		this.addActionButton(this.buttonParticipants);
		this.addActionButton(this.buttonParticipantsReport);
		this.addActionButton(this.buttonTerm);
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
			this.buttonChangeAppraiser.setVisible(false);
			this.buttonTerm.setVisible(Session.isUserManager(this.getModule()));
			this.buttonForm.setVisible(Session.isUserManager(this.getModule()));
			this.buttonParticipants.setVisible(Session.isUserManager(this.getModule()));
			this.buttonParticipantsReport.setVisible(Session.isUserManager(this.getModule()));
			this.buttonSingleStatement.setVisible(false);
			this.buttonFile.setVisible(Session.isUserManager(this.getModule()));
			this.buttonFillGrades.setVisible(false);
			this.buttonSign.setVisible(false);
		}else{
			this.buttonSendFeedback.setVisible(true);
			this.buttonChangeAppraiser.setVisible(true);
			this.buttonTerm.setVisible(false);
			this.buttonFile.setVisible(Session.isUserSupervisor());
			this.buttonForm.setVisible(Session.isUserProfessor());
			this.buttonParticipants.setVisible(Session.isUserProfessor());
			this.buttonParticipantsReport.setVisible(Session.isUserProfessor());
			this.buttonSendFeedback.setVisible(Session.isUserSupervisor());
			this.buttonStatements.setVisible(false);
			this.buttonSchedule.setVisible(Session.isUserSupervisor());
			this.buttonGrades.setVisible(false);
			this.comboStage.setVisible(false);
			this.buttonFillGrades.setVisible(Session.isUserSupervisor() && this.config.isAppraiserFillsGrades());
			this.buttonSign.setVisible(Session.isUserProfessor() && this.config.isUseDigitalSignature());
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
			
			this.showErrorNotification("Listar Bancas", e.getMessage());
		}
	}
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Download do Trabalho", "Selecione uma banca para fazer o download do trabalho.");
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
	        	
				this.showErrorNotification("Download do Trabalho", e.getMessage());
			}
		}
	}
	
	private void downloadTerm(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Termo de Aprovação", "Selecione uma banca para gerar o termo de aprovação.");
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Imprimir Termo de Aprovação", "Selecione o modo de geração para o Termo de Aprovação.\n\n" +
					"Versão Impressa: documento que deverá ser assinado e armazenado na coordenação.\n" + 
					"Versão Eletrônica: documento sem assinaturas que deverá ser incluído na versão final entregue pelo acadêmico.", "Versão Impressa", "Versão Eletrônica", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog d) {
                	try{
        				showReport(new JuryBO().getTermOfApproval((int)value, !d.isConfirmed(), Session.isUserManager(SystemModule.SIGET)));
                	}catch(Exception e){
        				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	        	
        				showErrorNotification("Imprimir Termo de Aprovação", e.getMessage());
        			}
                }
            });
		}
	}
	
	private void downloadForm(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Ficha de Avaliação", "Selecione uma banca para gerar a ficha de avaliação.");
		}else{
			try{
				this.showReport(new JuryBO().getJuryForm((int)value));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Imprimir Ficha de Avaliação", e.getMessage());
			}
		}
	}
	
	private void downloadParticipants(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Lista de Presença", "Selecione uma banca para gerar a lista de presença.");
		}else{
			try{
				JuryBO bo = new JuryBO();
				
				this.showReport(bo.getJuryParticipantsSignature((int)value));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Imprimir Lista de Presença", e.getMessage());
			}
		}
	}
	
	private void downloadParticipantsReport(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Lista de Acadêmicos Ouvintes", "Selecione uma banca para gerar a lista de acadêmicos ouvintes.");
		}else{
			try{
				JuryBO bo = new JuryBO();
				
				this.showReport(bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (int)value, 0, 0, true));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Imprimir Lista de Acadêmicos Ouvintes", e.getMessage());
			}
		}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Enviar Feedback", "Selecione o registro para enviar o feedback.");
    	}else{
    		try {
    			JuryAppraiserBO bo = new JuryAppraiserBO();
    			
    			JuryAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
    			
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
	
	private void downloadSingleStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione uma banca para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report;
				
				if(Session.isUserSupervisor()){
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
					this.showWarningNotification("Gerar Declaração", "Não foi encontrada a declaração para imprimir.");
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
					this.showWarningNotification("Gerar Declarações", "Não há declarações para serem geradas.");
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declarações", e.getMessage());
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
        	
			this.showErrorNotification("Imprimir Agenda de Bancas", e.getMessage());
		}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage(), false));
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Relatório de Notas", e.getMessage());
		}
	}
	
	private void changeAppraiser() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Alterar Membros", "Selecione uma banca para alterar a sua composição.");
		} else {
			try {
				if(Session.getUser().getIdUser() == new JuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser()) {
					UI.getCurrent().addWindow(new JuryAppraiserChangeWindow(new JuryBO().findById((int)value)));
				} else {
					this.showWarningNotification("Alterar Membros", "Apenas o presidente da banca pode mudar sua composição.");
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Alterar Membros", e.getMessage());
			}
		}
	}
	
	private void sign() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Assinar Ficha", "Selecione uma banca para assinar a ficha de avaliação.");
		} else {
			try {
				if(Session.getUser().getIdUser() == new JuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser()) {
					UI.getCurrent().addWindow(new JuryGradesWindow(new JuryBO().findById((int)value)));
				} else {
					this.showWarningNotification("Assinar Ficha", "Apenas o presidente da banca pode efetuar a assinatura da ficha de avaliação.");
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Assinar Ficha", e.getMessage());
			}
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
			
			this.showErrorNotification("Marcar Banca", e.getMessage());
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
	
	private void addScore() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Lançar Notas", "Selecione uma banca para lançar as notas.");
		} else {
			try {
				JuryAppraiser appraiser = new JuryAppraiserBO().findByAppraiser((int)value, Session.getUser().getIdUser());
				
				if((appraiser == null) || (appraiser.getIdJuryAppraiser() == 0)) {
					this.showWarningNotification("Lançar Notas", "Não é possível lançar as notas pois você não faz parte dessa banca.");
				} else if(appraiser.isSubstitute()) {
					this.showWarningNotification("Lançar Notas", "Apenas membros titulares da banca podem lançar notas.");
				} else {
					UI.getCurrent().addWindow(new EditJuryAppraiserScoreWindow(appraiser));	
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Lançar Notas", e.getMessage());
			}
		}
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
