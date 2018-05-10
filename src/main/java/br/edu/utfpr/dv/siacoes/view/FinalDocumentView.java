package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.window.EditFinalDocumentWindow;

public class FinalDocumentView extends ListView {
	
	public static final String NAME = "finaldocument";
	
	private final CheckBox checkListAll;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonLibraryReport;
	
	private Button.ClickListener listenerClickLibraryReport;
	
	public FinalDocumentView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Versão Final do TCC");
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
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
		
		this.checkListAll = new CheckBox("Listar Todos");
		
		this.buttonLibraryReport = new Button("Relat. Biblioteca");
		this.buttonLibraryReport.setIcon(FontAwesome.FILE_PDF_O);
		
		if(!Session.isUserManager(this.getModule())){
			this.checkListAll.setVisible(false);
			
			this.buttonLibraryReport.setVisible(false);
		}
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.checkListAll));
		
		this.addActionButton(this.buttonLibraryReport);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Validar");
		this.setEditIcon(FontAwesome.CHECK);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Sigilo", String.class);
		this.getGrid().addColumn("Feedback", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(100);
		this.getGrid().getColumns().get(5).setWidth(125);
		this.getGrid().getColumns().get(6).setWidth(100);
		this.getGrid().getColumns().get(7).setWidth(125);
		
		try{
			FinalDocumentBO bo = new FinalDocumentBO();
			List<FinalDocument> list;
			
			if(this.checkListAll.getValue() == true){
				list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), true);
			}else{
				list = bo.listBySupervisor(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
			}
			
			if(Session.isUserManager(this.getModule())){
				this.prepareLibraryReport();
			}
			
			for(FinalDocument doc : list){
				Object itemId = null;

				if((doc.getProject() != null) && (doc.getProject().getIdProject() > 0)){
					itemId = this.getGrid().addRow(1, doc.getProject().getSemester(), doc.getProject().getYear(), doc.getProject().getStudent().getName(), doc.getTitle(), doc.getSubmissionDate(), (doc.isPrivate() ? "Sim" : "Não"), doc.getSupervisorFeedback().toString());
				}else{
					itemId = this.getGrid().addRow(2, doc.getThesis().getSemester(), doc.getThesis().getYear(), doc.getThesis().getStudent().getName(), doc.getTitle(), doc.getSubmissionDate(), (doc.isPrivate() ? "Sim" : "Não"), doc.getSupervisorFeedback().toString());
				}
				
				this.addRowId(itemId, doc.getIdFinalDocument());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Projetos/Monografias", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			FinalDocumentBO bo = new FinalDocumentBO();
			FinalDocument doc = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditFinalDocumentWindow(doc, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Validar Projeto/Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
	
	private void prepareLibraryReport(){
		this.buttonLibraryReport.removeClickListener(this.listenerClickLibraryReport);
		new ExtensionUtils().removeAllExtensions(this.buttonLibraryReport);
    	
		try {
			FinalDocumentBO bo = new FinalDocumentBO();
        	byte[] report = bo.getLibraryReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textYear.getYear(), this.comboSemester.getSemester());
        	
        	new ExtensionUtils().extendToDownload("RelatorioBiblioteca.zip", report, this.buttonLibraryReport);	
    	} catch (Exception e) {
    		this.listenerClickLibraryReport = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            	
	            	Notification.show("Relatório para Biblioteca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	            }
	        };
	        
    		this.buttonLibraryReport.addClickListener(this.listenerClickLibraryReport);
		}
	}

}
