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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
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
	private final Button buttonSupervisorStatement;
	private final Button buttonCosupervisorStatement;
	
	private Button.ClickListener listenerClickDownload;
	
	public ThesisView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Monografias de TCC 2");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.buttonDownloadThesis = new Button("Monografia", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownloadThesis.setIcon(FontAwesome.DOWNLOAD);
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
		
		this.buttonCosupervisorStatement = new Button("Dec. Coorientação", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadCosupervisorStatement();
            }
        });
		
		if(Session.isUserProfessor()){
			this.addActionButton(this.buttonSendFeedback);
		}
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonJury);	
			this.addActionButton(this.buttonStatements);
			this.addActionButton(this.buttonSupervisorStatement);
			this.addActionButton(this.buttonCosupervisorStatement);
		}
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Visualizar");
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
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
			ThesisBO bo = new ThesisBO();
	    	List<Thesis> list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Thesis p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getStudent().getName(), p.getSupervisor().getName(), p.getTitle(), p.getSubmissionDate());
				this.addRowId(itemId, p.getIdThesis());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Monografias", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void downloadSupervisorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione um registro para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				ThesisBO tbo = new ThesisBO();
				CertificateBO bo = new CertificateBO();
				
				Thesis thesis = tbo.findById((int)value);

				byte[] report = bo.getThesisProfessorStatement(thesis.getSupervisor(), thesis);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadCosupervisorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione um registro para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				ThesisBO tbo = new ThesisBO();
				CertificateBO bo = new CertificateBO();
				
				Thesis thesis = tbo.findById((int)value);

				if(thesis.getCosupervisor() == null){
					Notification.show("Gerar Declaração", "Não foi indicado um coorientador para a monografia.", Notification.Type.WARNING_MESSAGE);
				}else{
					byte[] report = bo.getThesisProfessorStatement(thesis.getCosupervisor(), thesis);
					
					this.showReport(report);
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

				byte[] reportProfessor = bo.getJuryProfessorStatementReportListByThesis((int)value);
				
				if(reportProfessor != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportProfessor));
				}
				
				byte[] reportStudent = bo.getJuryStudentStatementReportListByThesis((int)value);
				
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
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Download da Monografia", "Selecione um registro para baixar a monografia.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				ThesisBO bo = new ThesisBO();
            	Thesis p = bo.findById((int)value);
				
				this.showReport(p.getFile());
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Download da Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
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
