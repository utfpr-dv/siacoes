package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.DownloadInternshipFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipPosterRequestWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipReportWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipWindow;
import br.edu.utfpr.dv.siacoes.window.InternshipJuryGradesWindow;
import br.edu.utfpr.dv.siacoes.window.InternshipUploadFinalReportWindow;

public class InternshipView extends ListView {
	
	public static final String NAME = "internship";
	private UserProfile profile = UserProfile.STUDENT;
	
	private final YearField textYear;
	private final StudentComboBox comboStudent;
	private final SupervisorComboBox comboProfessor;
	private final CompanyComboBox comboCompany;
	private final NativeSelect comboStatus;
	private final NativeSelect comboType;
	private final DateField textStartDate1;
	private final DateField textStartDate2;
	private final DateField textEndDate1;
	private final DateField textEndDate2;
	private final Button buttonParcialReport;
	private final Button buttonCompanySupervisorReport;
	private final Button buttonFinalReport;
	private final Button buttonFinalDocument;
	private final Button buttonJury;
	private final Button buttonProfessorStatement;
	private final Button buttonStudentStatement;
	private final Button buttonJuryFeedback;
	private final Button buttonPosterRequest;
	private final Button buttonPrintPosterRequest;
	private final Button buttonJuryGrades;
	
	private SigesConfig config;
	
	public InternshipView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Estágios");
		
		try {
			this.config = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch(Exception e) {
			this.config = new SigesConfig();
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textYear = new YearField();
		this.textYear.setYear(0);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.comboProfessor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.comboCompany = new CompanyComboBox();
		
		this.comboStatus = new NativeSelect("Situação");
		this.comboStatus.setWidth("195px");
		this.comboStatus.setNullSelectionAllowed(false);
		this.comboStatus.addItem(InternshipStatus.CURRENT);
		this.comboStatus.addItem(InternshipStatus.FINISHED);
		this.comboStatus.addItem("Todos");
		this.comboStatus.select(InternshipStatus.CURRENT);
		
		this.comboType = new NativeSelect("Tipo");
		this.comboType.setWidth("195px");
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.addItem(InternshipType.NONREQUIRED);
		this.comboType.addItem(InternshipType.REQUIRED);
		this.comboType.addItem("Todos");
		this.comboType.select("Todos");
		
		this.textStartDate1 = new DateField("Início Entre");
		this.textStartDate1.setDateFormat("dd/MM/yyyy");
		this.textStartDate2 = new DateField("");
		this.textStartDate2.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate1 = new DateField("Término Entre");
		this.textEndDate1.setDateFormat("dd/MM/yyyy");
		this.textEndDate2 = new DateField("");
		this.textEndDate2.setDateFormat("dd/MM/yyyy");
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboStudent, this.comboProfessor, this.comboType, this.comboStatus);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboCompany, this.textYear, this.textStartDate1, this.textStartDate2, this.textEndDate1, this.textEndDate2);
		h2.setSpacing(true);
		
		VerticalLayout v1 = new VerticalLayout(h1, h2);
		v1.setSpacing(true);
		
		this.addFilterField(new HorizontalLayout(v1));
		
		this.buttonJury = new Button("Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryClick();
            }
        });
		this.buttonJury.setIcon(FontAwesome.CALENDAR_CHECK_O);
		this.addActionButton(this.buttonJury);	
		
		this.buttonProfessorStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement();
            }
        });
		this.buttonProfessorStatement.setIcon(FontAwesome.FILE_PDF_O);
		this.addActionButton(this.buttonProfessorStatement);
		
		this.buttonStudentStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStudentStatement();
            }
        });
		this.buttonStudentStatement.setIcon(FontAwesome.FILE_PDF_O);
		this.addActionButton(this.buttonStudentStatement);
		
		this.buttonParcialReport = new Button("Relatório Parcial", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	parcialReportClick();
            }
        });
		this.buttonParcialReport.setIcon(FontAwesome.UPLOAD);
		this.addActionButton(this.buttonParcialReport);
		
		this.buttonCompanySupervisorReport = new Button("Relat. Supervisor", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	companySupervisorReportClick();
            }
        });
		this.buttonCompanySupervisorReport.setIcon(FontAwesome.UPLOAD);
		this.addActionButton(this.buttonCompanySupervisorReport);
		
		this.buttonFinalReport = new Button("Relatório Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	finalReportClick();
            }
        });
		this.buttonFinalReport.setIcon(FontAwesome.UPLOAD);
		this.addActionButton(this.buttonFinalReport);
		
		this.buttonPosterRequest = new Button("Solicitar Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	posterRequestClick();
            }
        });
		this.buttonPosterRequest.setIcon(FontAwesome.CALENDAR_PLUS_O);
		this.addActionButton(this.buttonPosterRequest);
		
		this.buttonPrintPosterRequest = new Button("Imp. Solic. de Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadPosterRequest();
            }
        });
		this.addActionButton(this.buttonPrintPosterRequest);
		
		this.buttonJuryFeedback = new Button("Feedback da Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryFeedback();
            }
        });
		this.buttonJuryFeedback.setIcon(FontAwesome.CHECK);
		this.addActionButton(this.buttonJuryFeedback);
		
		this.buttonJuryGrades = new Button("Notas da Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryGrades();
            }
        });
		this.buttonJuryGrades.setIcon(FontAwesome.CALCULATOR);
		this.addActionButton(this.buttonJuryGrades);
		
		this.buttonFinalDocument = new Button("Versão Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	finalDocumentClick();
            }
        });
		this.buttonFinalDocument.setIcon(FontAwesome.UPLOAD);
		this.addActionButton(this.buttonFinalDocument);
	}
	
	private void configureProfile(){
		if((this.profile != UserProfile.MANAGER) || !Session.isUserManager(this.getModule())) {
			this.setFiltersVisible(false);
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			this.setEditCaption("Visualizar");
			this.setEditIcon(FontAwesome.SEARCH);
			
			this.buttonJury.setVisible(false);
			this.buttonProfessorStatement.setVisible(this.profile == UserProfile.PROFESSOR);
			this.buttonStudentStatement.setVisible(this.profile == UserProfile.STUDENT);
			this.buttonParcialReport.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			this.buttonFinalReport.setVisible(this.profile == UserProfile.STUDENT);
			this.buttonJuryFeedback.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			this.buttonFinalDocument.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			this.buttonPosterRequest.setVisible((this.profile == UserProfile.STUDENT) && (this.config.getJuryFormat() == JuryFormat.SESSION));
			this.buttonPrintPosterRequest.setVisible((this.profile == UserProfile.STUDENT) && (this.config.getJuryFormat() == JuryFormat.SESSION));
			this.buttonJuryGrades.setVisible(((this.profile == UserProfile.STUDENT) || (this.profile == UserProfile.PROFESSOR)) && (this.config.isShowGradesToStudent()));
			
			if(this.profile == UserProfile.PROFESSOR){
				this.buttonFinalDocument.setCaption("Val. Relat. Final");
				this.buttonFinalDocument.setIcon(FontAwesome.CHECK);
			}
		} else {
			this.buttonProfessorStatement.setCaption("Declaração Prof.");
			this.buttonStudentStatement.setCaption("Declaração Acad.");
			this.buttonParcialReport.setVisible(false);
			this.buttonFinalReport.setVisible(false);
			this.buttonPosterRequest.setVisible(false);
			this.buttonPrintPosterRequest.setVisible(false);
			this.buttonJuryFeedback.setVisible(false);
			this.buttonFinalDocument.setVisible(false);
			this.buttonJuryGrades.setVisible(false);
		}
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addColumn("Orientador", String.class);
		this.getGrid().addColumn("Data Início", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Tipo", String.class);
		this.getGrid().addColumn("Situação", String.class);
		
		this.getGrid().getColumns().get(3).setWidth(125);
		this.getGrid().getColumns().get(4).setWidth(160);
		this.getGrid().getColumns().get(5).setWidth(150);
		
		try{
			InternshipBO bo = new InternshipBO();
			List<Internship> list;
			
			if(this.profile == UserProfile.MANAGER){
				int type = -1, status = -1;
				
				if(!this.comboType.getValue().equals("Todos")){
					type = ((InternshipType)this.comboType.getValue()).getValue();
				}
				
				if(!this.comboStatus.getValue().equals("Todos")){
					status = ((InternshipStatus)this.comboStatus.getValue()).getValue();
				}
				
				list = bo.list(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textYear.getYear(), (this.comboStudent.getStudent() == null ? 0 : this.comboStudent.getStudent().getIdUser()), (this.comboProfessor.getProfessor() == null ? 0 : this.comboProfessor.getProfessor().getIdUser()), (this.comboCompany.getCompany() == null ? 0 : this.comboCompany.getCompany().getIdCompany()), type, status, this.textStartDate1.getValue(), this.textStartDate2.getValue(), this.textEndDate1.getValue(), this.textEndDate2.getValue());
			}else if(this.profile == UserProfile.PROFESSOR){
				list = bo.listBySupervisor(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			}else if(this.profile == UserProfile.COMPANYSUPERVISOR){
				list = bo.listByCompanySupervisor(Session.getUser().getIdUser());
			}else{
				list = bo.listByStudent(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			}
			
			for(Internship c : list){
				Object itemId = this.getGrid().addRow(c.getStudent().getName(), c.getCompany().getName(), c.getSupervisor().getName(), c.getStartDate(), c.getType().toString(), c.getStatus().toString());
				this.addRowId(itemId, c.getIdInternship());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Estágios", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		Internship internship = new Internship();
		internship.setDepartment(Session.getSelectedDepartment().getDepartment());
		
		UI.getCurrent().addWindow(new EditInternshipWindow(internship, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			InternshipBO bo = new InternshipBO();
			Internship internship = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditInternshipWindow(internship, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Estágio", e.getMessage());
		}
	}
	
	private void parcialReportClick() {
		Object id = getIdSelected();
		
		if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para enviar o relatório parcial.");
    	} else {
    		try {
				Internship internship = new InternshipBO().findById((int)id);
				
				UI.getCurrent().addWindow(new EditInternshipReportWindow(null, internship, ((this.profile == UserProfile.PROFESSOR) ? ReportType.SUPERVISOR : ReportType.STUDENT)));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Enviar Relatório", e.getMessage());
			}
    	}
	}
	
	private void companySupervisorReportClick() {
		Object id = getIdSelected();
		
		if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para enviar o relatório do supervisor.");
    	} else {
    		try {
				Internship internship = new InternshipBO().findById((int)id);
				
				UI.getCurrent().addWindow(new EditInternshipReportWindow(null, internship, ReportType.COMPANY));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Enviar Relatório", e.getMessage());
			}
    	}
	}
	
	private void finalReportClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para enviar o relatório final.");
    	}else{
    		try{
    			InternshipJuryBO jbo = new InternshipJuryBO();
    			InternshipJury jury = jbo.findByInternship((int)id);
    			
    			if((jury != null) && (jury.getIdInternshipJury() != 0) && (jury.getDate().before(DateUtils.getToday().getTime()))) {
    				ConfirmDialog.show(UI.getCurrent(), "Não é possível enviar o relatório pois a banca de estágio já ocorreu.\n\nVocê deseja enviar a versão final do relatório?", new ConfirmDialog.Listener() {
    	                public void onClose(ConfirmDialog dialog) {
    	                    if (dialog.isConfirmed()) {
    	                    	finalDocumentClick();
    	                    }
    	                }
    	            });
    			} else {
    				InternshipBO bo = new InternshipBO();
        			Internship internship = bo.findById((int)id);
        			
        			UI.getCurrent().addWindow(new InternshipUploadFinalReportWindow(internship, this));
    			}
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Enviar Relatório", e.getMessage());
    		}
    	}
	}
	
	private void juryClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para marcar a banca.");
    	}else{
    		try {
    			Internship internship = new InternshipBO().findById((int)id);
    			
    			if(internship.getType() == InternshipType.REQUIRED){
    				InternshipJury jury = new InternshipJuryBO().findByInternship((int)id);
    				
    				UI.getCurrent().addWindow(new EditInternshipJuryWindow(jury, this));
    			}else{
    				this.showWarningNotification("Marcar Banca", "A banca só pode ser marcada para o estágio obrigatório.");
    			}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Marcar Banca", e.getMessage());
			}
    	}
	}
	
	private void finalDocumentClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para " + ((this.profile == UserProfile.PROFESSOR) ? "validar a versão final do relatório final" : "enviar a versão final do relatório") + ".");
    	}else{
    		try{
    			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
    			InternshipFinalDocument doc = bo.findByInternship((int)id);
    			
    			if(doc == null){
    				if(this.profile == UserProfile.PROFESSOR){
    					throw new Exception("O acadêmico ainda não enviou a versão final do relatório.");
    				}else{
    					doc = new InternshipFinalDocument();
    					
    					InternshipBO bo2 = new InternshipBO();
    					Internship internship = bo2.findById((int)id);
    					
    					if(internship.getType() != InternshipType.REQUIRED){
    						throw new Exception("A versão final do relatório só pode ser enviada para estágio obrigatório.");
    					}else{
    						InternshipJuryBO bo3 = new InternshipJuryBO();
    						InternshipJury jury = bo3.findByInternship((int)id);
    						
    						if((jury == null) || (jury.getIdInternshipJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())){
    							throw new Exception("A versão final do relatório só pode ser enviada após a realização da banca de estágio.");
    						}
    					}
    					
    					doc.setInternship(internship);
    					doc.setTitle(internship.getReportTitle());
    				}
    			}
    			
    			UI.getCurrent().addWindow(new EditInternshipFinalDocumentWindow(doc, this));
    		} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification(((this.profile == UserProfile.PROFESSOR) ? "Validar" : "Enviar") + " Versão Final", e.getMessage());
			}
    	}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			InternshipBO bo = new InternshipBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Estágio", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private void juryFeedback(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para visualizar o feedback.");
    	}else{
    		try{
    			InternshipJuryBO bo = new InternshipJuryBO();
    			InternshipJury jury = bo.findByInternship((int)id);
    			
    			if((jury != null) && (jury.getIdInternshipJury() != 0)){
    				UI.getCurrent().addWindow(new DownloadInternshipFeedbackWindow(jury));
    			}else{
    				this.showWarningNotification("Feedback da Banca", "Ainda não foi marcada a banca para este trabalho.");
    			}
    		} catch (Exception e) {
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Feedback da Banca", e.getMessage());
    		}
    	}
	}
	
	private void downloadProfessorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getInternshipProfessorStatement((int)value);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadStudentStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getInternshipStudentStatement((int)value);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void posterRequestClick() {
		Object id = getIdSelected();
    	
    	if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para efetuar a solicitação de banca.");
    	} else {
    		try {
				InternshipJury jury = new InternshipJuryBO().findByInternship((int)id);
				
				if((jury != null) && (jury.getIdInternshipJury() != 0)) {
					this.showWarningNotification("Solicitar Banca", "Não é possível efetuar a solicitação pois a banca já foi agendada.");
				} else {
					Internship internship = new InternshipBO().findById((int)id);
	    			
	    			if(internship.getType() == InternshipType.REQUIRED) {
	    				InternshipPosterRequest request = new InternshipPosterRequestBO().preparePosterRequest((int)id);
	    				
	    				UI.getCurrent().addWindow(new EditInternshipPosterRequestWindow(request, this));
	    			} else {
	    				this.showWarningNotification("Solicitar Banca", "A solicitação de banca só pode ser efetuada para o estágio obrigatório");
	    			}
				}
    		} catch(Exception e) {
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Solicitar Banca", e.getMessage());
    		}
    	}
	}
	
	private void downloadPosterRequest() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
				this.showReport(new InternshipPosterRequestBO().getPosterRequestForm(new InternshipPosterRequestBO().preparePosterRequest((int)value).getIdInternshipPosterRequest()));
			} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Solicitação de Banca", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Solicitação de Banca", "Selecione um registro para imprimir a Solicitação de Banca.");
		}
	}
	
	private void juryGrades() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
				Internship internship = new InternshipBO().findById((int)value);
    			
    			if(internship.getType() == InternshipType.REQUIRED) {
    				UI.getCurrent().addWindow(new InternshipJuryGradesWindow(new InternshipJuryBO().findByInternship((int)value)));
    			} else {
    				this.showWarningNotification("Notas da Banca", "As notas somente são atribuídas a estágios obrigatórios.");
    			}
			} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Notas da Banca", e.getMessage());
			}
		} else {
			this.showWarningNotification("Notas da Banca", "Selecione um registro para visualizar as notas atribuídas pela banca.");
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event){
		super.enter(event);
		
		if(Session.isUserProfessor()) {
			this.profile = UserProfile.PROFESSOR;
		} else if(Session.isUserCompanySupervisor()) {
			this.profile = UserProfile.COMPANYSUPERVISOR;
		} else {
			this.profile = UserProfile.STUDENT;
		}
		
		if((event.getParameters() != null) && (!event.getParameters().isEmpty())){
			try{
				int p = Integer.parseInt(event.getParameters());
				
				if((p == 1) && Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()){
					this.profile = UserProfile.MANAGER;
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		this.refreshGrid();
		this.configureProfile();
	}

}
