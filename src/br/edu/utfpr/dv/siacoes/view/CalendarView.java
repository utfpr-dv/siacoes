package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.CalendarReport;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.StatementReport;
import br.edu.utfpr.dv.siacoes.model.TermOfApprovalReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;

public class CalendarView extends ListView {
	
	public static final String NAME = "calendar";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final CheckBox checkAll;
	private final Button buttonFile;
	private final Button buttonForm;
	private final Button buttonTerm;
	private final Button buttonCalendar;
	private final Button buttonStatements;
	private final Button buttonSendFeedback;
	
	private Button.ClickListener listenerClickFile;
	private Button.ClickListener listenerClickForm;
	private Button.ClickListener listenerClickTerm;
	private Button.ClickListener listenerClickStatement;

	public CalendarView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(DateUtils.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setValue(String.valueOf(DateUtils.getYear()));
		
		this.checkAll = new CheckBox("Todas as bancas do departamento/coordenação");
		this.checkAll.setValue(false);
		
		if(Session.isUserManager(this.getModule())){
			HorizontalLayout h = new HorizontalLayout(this.comboSemester, this.textYear, this.checkAll);
			h.setComponentAlignment(this.checkAll, Alignment.MIDDLE_LEFT);
			this.addFilterField(h);
		}else{
			this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		}
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonFile = new Button("Trabalho");
		
		this.buttonForm = new Button("Ficha");
		
		this.buttonTerm = new Button("Termo");
		
		this.buttonSendFeedback = new Button("Feedback", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sendFeedbackClick();
            }
        });
		
		this.buttonStatements = new Button("Declarações");
		
		this.buttonCalendar = new Button("Imprimir");
		
		this.addActionButton(this.buttonCalendar);
		this.addActionButton(this.buttonFile);
		this.addActionButton(this.buttonForm);
		this.addActionButton(this.buttonTerm);
		this.addActionButton(this.buttonSendFeedback);
		
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonStatements);
			this.setEditVisible(true);
			this.setEditCaption("Banca");
		}
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
		this.getGrid().addColumn("Local", String.class);
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(165);
		this.getGrid().getColumns().get(2).setWidth(65);
		
		if(this.checkAll.getValue() == true){
			this.buttonSendFeedback.setVisible(false);
		}else{
			this.buttonSendFeedback.setVisible(true);
		}
		
		this.prepareDownload();
		
		try {
			JuryBO bo = new JuryBO();
			List<Jury> list;
			List<CalendarReport> report;
			
			if(this.checkAll.getValue()){
				list = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
				report = bo.getCalendarReport(Session.getUser().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear());
			}else{
				list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				report = bo.getCalendarReport(Session.getUser().getDepartment().getIdDepartment(), Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
			}
			
			new ReportUtils().prepareForPdfReport("Calendar", "Agenda de Defesas", report, this.buttonCalendar);
			
	    	for(Jury jury : list){
	    		String title = "";
	    		String student = "";
	    		
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
	    		
				Object itemId = this.getGrid().addRow(jury.getDate(), jury.getLocal(), jury.getStage(), title, student);
				this.addRowId(itemId, jury.getIdJury());
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
	
	private void prepareDownload(){
		Object value = getIdSelected();
    	
		this.buttonForm.removeClickListener(this.listenerClickForm);
		this.buttonFile.removeClickListener(this.listenerClickFile);
		this.buttonTerm.removeClickListener(this.listenerClickTerm);
		this.buttonStatements.removeClickListener(this.listenerClickStatement);
		
    	if(value != null){
			try {
				JuryBO bo = new JuryBO();
				JuryFormReport report = bo.getFormReport((int)value);
				
				List<JuryFormReport> list = new ArrayList<JuryFormReport>();
				list.add(report);
				
				new ReportUtils().prepareForPdfReport("JuryForm", "Ficha de Avaliação", list, this.buttonForm);
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
			
			try {
				JuryBO bo = new JuryBO();
				TermOfApprovalReport report = bo.getTermOfApprovalReport((int)value);
				
				List<TermOfApprovalReport> list = new ArrayList<TermOfApprovalReport>();
				list.add(report);
				
				new ReportUtils().prepareForPdfReport("TermOfApproval", "Termo de Aprovação", list, this.buttonTerm);
			} catch (Exception e) {
				this.listenerClickTerm = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Imprimir Termo de Aprovação", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
				this.buttonTerm.addClickListener(this.listenerClickTerm);
			}
			
			try {
				JuryBO bo = new JuryBO();
				Jury jury = bo.findById((int)value);
				
				String fileName = "";
				byte[] file = null;
				
				if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)){
	    			ThesisBO tbo = new ThesisBO();
	    			Thesis thesis = tbo.findById(jury.getThesis().getIdThesis());
	    			
	    			fileName = thesis.getTitle() + thesis.getFileType().getExtension();
	    			file = thesis.getFile();
	    		}else{
	    			ProjectBO pbo = new ProjectBO();
	    			Project project = pbo.findById(jury.getProject().getIdProject());
	    			
	    			fileName = project.getTitle() + project.getFileType().getExtension();
	    			file = project.getFile();
	    		}
				
				new ExtensionUtils().extendToDownload(fileName, file, this.buttonFile);
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
			
			try {
				JuryStudentBO sbo = new JuryStudentBO();
				List<StatementReport> listStudent = sbo.getStatementReportList((int)value);
				
				JuryAppraiserBO abo = new JuryAppraiserBO();
				List<StatementReport> listAppraiser = abo.getStatementReportList((int)value);
				
				if(listStudent.size() > 0) {
					new ReportUtils().prepareForPdfReport("StudentStatement", "Declaração Alunos", listStudent, this.buttonStatements);
				}
				
				if(listAppraiser.size() > 0) {
					new ReportUtils().prepareForPdfReport("ProfessorStatement", "Declaração Professores", listAppraiser, this.buttonStatements, false);
				}
			} catch (Exception e) {
				this.listenerClickStatement = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Gerar Declarações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonStatements.addClickListener(this.listenerClickStatement);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonFile);
    		new ExtensionUtils().removeAllExtensions(this.buttonForm);
    		new ExtensionUtils().removeAllExtensions(this.buttonTerm);
    		new ExtensionUtils().removeAllExtensions(this.buttonStatements);
    		
    		this.listenerClickForm = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Imprimir Ficha de Avaliação", "Selecione uma banca para imprimir a ficha de avaliação.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
	        
	        this.listenerClickTerm = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Imprimir Termo de Aprovação", "Selecione uma banca para imprimir o termo de aprovação.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
	        
	        this.listenerClickFile = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download do Trabalho", "Selecione uma banca para baixar o trabalho.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.listenerClickStatement = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Gerar Declarações", "Selecione uma banca para gerar as declarações.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonForm.addClickListener(this.listenerClickForm);
    		this.buttonTerm.addClickListener(this.listenerClickTerm);
    		this.buttonFile.addClickListener(this.listenerClickFile);
    		this.buttonStatements.addClickListener(this.listenerClickStatement);
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

}
