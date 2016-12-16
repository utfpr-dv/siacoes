package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.EventRouter;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.StatementReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditThesisWindow;

public class ThesisView extends ListView {
	
	public static final String NAME = "thesis";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonJury;
	private final Button buttonDownloadThesis;
	private final Button buttonStatements;
	private final Button buttonSendFeedback;
	
	private Button.ClickListener listenerClickDownload;
	private Button.ClickListener listenerClickStatement;
	
	public ThesisView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.buttonDownloadThesis = new Button("Monografia");
		this.addActionButton(this.buttonDownloadThesis);
		
		this.buttonJury = new Button("Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryClick();
            }
        });
		
		this.buttonSendFeedback = new Button("Feedback", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sendFeedbackClick();
            }
        });
		
		this.buttonStatements = new Button("Declarações");
		
		if(Session.isUserProfessor()){
			this.addActionButton(this.buttonSendFeedback);
		}
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonJury);	
			this.addActionButton(this.buttonStatements);
		}
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(4).setWidth(125);
		
		this.prepareDownload();
		
		try {
			ThesisBO bo = new ThesisBO();
	    	List<Thesis> list = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Thesis p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getTitle(), p.getStudent().getName(), p.getSubmissionDate());
				this.addRowId(itemId, p.getIdThesis());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Monografias", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownload(){
		Object value = getIdSelected();
		
		this.buttonDownloadThesis.removeClickListener(this.listenerClickDownload);
		this.buttonStatements.removeClickListener(this.listenerClickStatement);
    	
    	if(value != null){
			try {
            	ThesisBO bo = new ThesisBO();
            	Thesis p = bo.findById((int)value);
            	
            	new ExtensionUtils().extendToDownload(p.getTitle() + p.getFileType().getExtension(), p.getFile(), this.buttonDownloadThesis);
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download da Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownloadThesis.addClickListener(this.listenerClickDownload);
			}
			
			try {
				JuryStudentBO sbo = new JuryStudentBO();
				List<StatementReport> listStudent = sbo.getStatementReportListByThesis((int)value);
				
				JuryAppraiserBO abo = new JuryAppraiserBO();
				List<StatementReport> listAppraiser = abo.getStatementReportListByThesis((int)value);
				
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
    		new ExtensionUtils().removeAllExtensions(this.buttonDownloadThesis);
    		
    		this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download da Monografia", "Selecione um registro para baixar a monografia.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonDownloadThesis.addClickListener(this.listenerClickDownload);
    		
    		new ExtensionUtils().removeAllExtensions(this.buttonStatements);
    		
    		this.listenerClickStatement = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Gerar Declarações", "Selecione um registro para gerar as declarações.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonStatements.addClickListener(this.listenerClickStatement);
    	}
	}

	private void juryClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para marcar a banca.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try {
    			JuryBO bo = new JuryBO();
				Jury jury = bo.findByThesis((int)id);
				
				UI.getCurrent().addWindow(new EditJuryWindow(jury, this));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Marcar Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
    	}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para marcar a banca.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try {
    			JuryBO bo = new JuryBO();
				Jury jury = bo.findByThesis((int)id);
				
				JuryAppraiserBO bo2 = new JuryAppraiserBO();
    			
    			JuryAppraiser appraiser = bo2.findByAppraiser(jury.getIdJury(), Session.getUser().getIdUser());
    			
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
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			ThesisBO bo = new ThesisBO();
			Thesis p = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditThesisWindow(p, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
