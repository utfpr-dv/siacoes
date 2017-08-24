package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.CalendarReport;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditInternshipJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipJuryWindow;

public class InternshipCalendarView extends ListView {

	public static final String NAME = "internshipcalendar";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonFile;
	private final Button buttonForm;
	private final Button buttonCalendar;
	private final Button buttonStatements;
	private final Button buttonSingleStatement;
	private final Button buttonSendFeedback;
	
	private Button.ClickListener listenerClickFile;
	private Button.ClickListener listenerClickForm;
	
	private boolean listAll = false;
	
	public InternshipCalendarView(){
		super(SystemModule.SIGES);
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonFile = new Button("Trabalho");
		
		this.buttonForm = new Button("Ficha");
		
		this.buttonSendFeedback = new Button("Feedback", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sendFeedbackClick();
            }
        });
		
		this.buttonStatements = new Button("Declarações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStatements();
            }
        });
		
		this.buttonSingleStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadSingleStagement();
            }
        });
		
		this.buttonCalendar = new Button("Imprimir");
		
		this.addActionButton(this.buttonCalendar);
		this.addActionButton(this.buttonFile);
		this.addActionButton(this.buttonForm);
		this.addActionButton(this.buttonSendFeedback);
		this.addActionButton(this.buttonSingleStatement);
		
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonStatements);
			this.setEditVisible(true);
			this.setEditCaption("Banca");
		}
	}
	
	private void configureButtons(){
		if(this.listAll){
			this.buttonSendFeedback.setVisible(false);
			this.buttonForm.setVisible(Session.isUserManager(this.getModule()));
			this.buttonSingleStatement.setVisible(false);
			this.buttonFile.setVisible(Session.isUserManager(this.getModule()));
		}else{
			this.buttonSendFeedback.setVisible(true);
			this.buttonFile.setVisible(Session.isUserProfessor());
			this.buttonForm.setVisible(Session.isUserProfessor());
			this.buttonSendFeedback.setVisible(Session.isUserProfessor());
			this.buttonStatements.setVisible(false);
			this.buttonCalendar.setVisible(Session.isUserProfessor());
		}
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
		this.getGrid().addColumn("Local", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(165);
		
		this.prepareDownload();
		
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			List<InternshipJury> list;
			List<CalendarReport> report = null;
			
			if(this.listAll){
				list = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
				report = bo.getCalendarReport(Session.getUser().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear());
			}else{
				if(Session.isUserProfessor()){
					list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
					report = bo.getCalendarReport(Session.getUser().getDepartment().getIdDepartment(), Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}else{
					list = bo.listByStudent(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
			if(this.listAll || Session.isUserProfessor()){
				new ReportUtils().prepareForPdfReport("InternshipCalendar", "Agenda de Defesas", report, this.buttonCalendar);
			}
			
	    	for(InternshipJury jury : list){
	    		InternshipBO ibo = new InternshipBO();
	    		Internship internship = ibo.findById(jury.getInternship().getIdInternship());
	    		
				Object itemId = this.getGrid().addRow(jury.getDate(), jury.getLocal(), internship.getStudent().getName(), internship.getCompany().getName());
				this.addRowId(itemId, jury.getIdInternshipJury());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Bancas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para enviar o feedback.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try {
    			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
    			
    			InternshipJuryAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
    			
    			if(appraiser != null){
    				UI.getCurrent().addWindow(new EditInternshipJuryAppraiserFeedbackWindow(appraiser));
    			}else{
    				Notification.show("Enviar Feedback", "É necessário ser membro da banca para enviar o feedback.", Notification.Type.WARNING_MESSAGE);
    			}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Enviar Feedback", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
    	}
	}
	
	private void downloadSingleStagement(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione uma banca para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report;
				
				if(Session.isUserProfessor()){
					InternshipJuryAppraiserBO jbo = new InternshipJuryAppraiserBO();
					InternshipJuryAppraiser appraiser = jbo.findByAppraiser((int)value, Session.getUser().getIdUser());
					
					report = bo.getInternshipJuryProfessorStatement(appraiser);
				}else{
					InternshipJuryStudentBO jbo = new InternshipJuryStudentBO();
					InternshipJuryStudent student = jbo.findByStudent((int)value, Session.getUser().getIdUser());
					
					report = bo.getInternshipJuryStudentStatement(student);
				}
				
				if(report != null){
					Session.putReport(report);
					
					getUI().getPage().open("#!" + CertificateView.NAME + "/session/" + UUID.randomUUID().toString(), "_blank");
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

				byte[] reportProfessor = bo.getInternshipJuryProfessorStatementReportList((int)value);
				
				if(reportProfessor != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportProfessor));
				}
				
				byte[] reportStudent = bo.getInternshipJuryStudentStatementReportList((int)value);
				
				if(reportStudent != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportStudent));
				}
				
				if((reportProfessor != null) || (reportStudent != null)){
					pdfMerge.mergeDocuments(null);
					
					byte[] report = output.toByteArray();
					
					Session.putReport(report);
					
					getUI().getPage().open("#!" + CertificateView.NAME + "/session/" + UUID.randomUUID().toString(), "_blank");
				}else{
					Notification.show("Gerar Declarações", "Não há declarações para serem geradas.", Notification.Type.WARNING_MESSAGE);
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declarações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void prepareDownload(){
		Object value = getIdSelected();
    	
		this.buttonForm.removeClickListener(this.listenerClickForm);
		this.buttonFile.removeClickListener(this.listenerClickFile);
		
    	if(value != null){
    		if(this.buttonForm.isVisible()){
				try {
					InternshipJuryBO bo = new InternshipJuryBO();
					InternshipJuryFormReport report = bo.getFormReport((int)value);
					
					List<InternshipJuryFormReport> list = new ArrayList<InternshipJuryFormReport>();
					list.add(report);
					
					new ReportUtils().prepareForPdfReport("InternshipJuryForm", "Ficha de Avaliação", list, this.buttonForm);
				} catch (Exception e) {
					this.listenerClickForm = new Button.ClickListener() {
			            @Override
			            public void buttonClick(ClickEvent event) {
			            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			            	
			            	Notification.show("Imprimir Ficha de Avaliação", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			            }
			        };
			        
					this.buttonForm.addClickListener(this.listenerClickForm);
				}
    		}
			
    		if(this.buttonFile.isVisible()){
				try {
					InternshipJuryBO bo = new InternshipJuryBO();
					InternshipJury jury = bo.findById((int)value);
					
					InternshipBO ibo = new InternshipBO();
					Internship internship = ibo.findById(jury.getInternship().getIdInternship());
					
					if(internship.getFinalReport() == null){
						this.listenerClickFile = new Button.ClickListener() {
				            @Override
				            public void buttonClick(ClickEvent event) {
				            	Notification.show("Download do Trabalho", "O relatório final do estágio não foi anexado ao cadastro.", Notification.Type.WARNING_MESSAGE);
				            }
				        };
				        
		        		this.buttonFile.addClickListener(this.listenerClickFile);
					}else{
						String fileName = internship.getStudent().getName() + ".pdf";
						
						new ExtensionUtils().extendToDownload(fileName, internship.getFinalReport(), this.buttonFile);
					}
	        	} catch (Exception e) {
	        		this.listenerClickFile = new Button.ClickListener() {
			            @Override
			            public void buttonClick(ClickEvent event) {
			            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			            	
			            	Notification.show("Download do Trabalho", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			            }
			        };
			        
	        		this.buttonFile.addClickListener(this.listenerClickFile);
				}
    		}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonFile);
    		new ExtensionUtils().removeAllExtensions(this.buttonForm);
    		
    		this.listenerClickForm = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Imprimir Ficha de Avaliação", "Selecione uma banca para imprimir a ficha de avaliação.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
	        
	        this.listenerClickFile = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download do Trabalho", "Selecione uma banca para baixar o trabalho.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonForm.addClickListener(this.listenerClickForm);
    		this.buttonFile.addClickListener(this.listenerClickFile);
    	}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			InternshipJury jury = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditInternshipJuryWindow(jury, this));
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
