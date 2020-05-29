package br.edu.utfpr.dv.siacoes.window;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipReportBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.components.CompanySupervisorComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipRequiredType;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipWindow extends EditWindow {

	private final Internship internship;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final StudentComboBox comboStudent;
	private final SupervisorComboBox comboSupervisor;
	private final CompanyComboBox comboCompany;
	private final CompanySupervisorComboBox comboCompanySupervisor;
	private final NativeSelect comboType;
	private final NativeSelect comboRequiredType;
	private final TextArea textComments;
	private final DateField startDate;
	private final DateField endDate;
	private final TextField textTotalHours;
	private final TextField textWeekHours;
	private final TextField textWeekDays;
	private final TextField textTerm;
	private final TextField textReportTitle;
	private final FileUploader uploadInternshipPlan;
	private final Button buttonDownloadInternshipPlan;
	private final FileUploader uploadFinalReport;
	private final Button buttonDownloadFinalReport;
	private final TabSheet tabContainer;
	private Grid gridStudentReport;
	private Grid gridSupervisorReport;
	private Grid gridCompanySupervisorReport;
	private final VerticalLayout layoutStudentReport;
	private final VerticalLayout layoutSupervisorReport;
	private final VerticalLayout layoutCompanySupervisorReport;
	private final Button buttonUploadStudentReport;
	private final Button buttonUploadSupervisorReport;
	private final Button buttonUploadCompanySupervisorReport;
	private final Button buttonDownloadStudentReport;
	private final Button buttonDownloadSupervisorReport;
	private final Button buttonDownloadCompanySupervisorReport;
	private final Button buttonDeleteStudentReport;
	private final Button buttonDeleteSupervisorReport;
	private final Button buttonDeleteCompanySupervisorReport;
	private final Button buttonValidateStudentReport;
	private final Button buttonValidateSupervisorReport;
	private final Button buttonValidateCompanySupervisorReport;
	
	private SigesConfig config;
	
	public EditInternshipWindow(Internship i, ListView parentView){
		super("Editar Estágio", parentView);
		
		if(i == null){
			this.internship = new Internship();
		}else{
			this.internship = i;
		}
		
		try {
			this.config = new SigesConfigBO().findByDepartment(this.internship.getDepartment().getIdDepartment());
		} catch (Exception e) {
			this.config = new SigesConfig();
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		this.comboStudent.setRequired(true);
		
		this.comboSupervisor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigesConfigBO().getSupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboSupervisor.setRequired(true);
		
		this.comboCompanySupervisor = new CompanySupervisorComboBox();
		this.comboCompanySupervisor.setRequired(true);
		
		this.comboCompany = new CompanyComboBox();
		this.comboCompany.setRequired(true);
		this.comboCompany.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(comboCompany.getCompany() == null){
					comboCompanySupervisor.setIdCompany(0);
				}else{
					comboCompanySupervisor.setIdCompany(comboCompany.getCompany().getIdCompany());	
				}
			}
		});
		
		this.comboType = new NativeSelect("Tipo de Estágio");
		this.comboType.setWidth("200px");
		this.comboType.addItem(InternshipType.NONREQUIRED);
		this.comboType.addItem(InternshipType.REQUIRED);
		this.comboType.select(InternshipType.NONREQUIRED);
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.setRequired(true);
		
		this.comboRequiredType = new NativeSelect("Categorização do Estágio");
		this.comboRequiredType.setWidth("200px");
		this.comboRequiredType.addItem(InternshipRequiredType.UNIVERSITY);
		this.comboRequiredType.addItem(InternshipRequiredType.EXTERNAL);
		this.comboRequiredType.addItem(InternshipRequiredType.SCHOLARSHIP);
		this.comboRequiredType.addItem(InternshipRequiredType.PROFESSIONAL);
		this.comboRequiredType.addItem(InternshipRequiredType.VALIDATION);
		this.comboRequiredType.select(InternshipRequiredType.UNIVERSITY);
		this.comboRequiredType.setNullSelectionAllowed(false);
		this.comboRequiredType.setRequired(true);
		
		this.startDate = new DateField("Data de Início");
		this.startDate.setDateFormat("dd/MM/yyyy");
		this.startDate.setRequired(true);
		
		this.endDate = new DateField("Data de Término");
		this.endDate.setDateFormat("dd/MM/yyyy");
		
		this.textTotalHours = new TextField("Horas");
		this.textTotalHours.setWidth("100px");
		this.textTotalHours.setRequired(true);
		
		this.textWeekHours = new TextField("C.H. Sem.");
		this.textWeekHours.setWidth("100px");
		this.textWeekHours.setRequired(true);
		
		this.textWeekDays = new TextField("Dias/Sem.");
		this.textWeekDays.setWidth("100px");
		this.textWeekDays.setRequired(true);
		
		this.textTerm = new TextField("Termo");
		this.textTerm.setWidth("200px");
		
		this.textReportTitle = new TextField("Título do Relatório Final");
		this.textReportTitle.setWidth("810px");
		
		this.uploadInternshipPlan = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadInternshipPlan.setButtonCaption("Enviar Plano de Estágio");
		this.uploadInternshipPlan.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadInternshipPlan.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadInternshipPlan.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadInternshipPlan.getUploadedFile() != null) {
					internship.setInternshipPlan(uploadInternshipPlan.getUploadedFile());
				}
				
				buttonDownloadInternshipPlan.setVisible(true);
			}
		});
		
		this.buttonDownloadInternshipPlan = new Button("Baixar Plano de Estágio", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadInternshipPlan();
            }
        });
		
		this.uploadFinalReport = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFinalReport.setButtonCaption("Enviar Relatório Final");
		this.uploadFinalReport.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFinalReport.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFinalReport.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFinalReport.getUploadedFile() != null) {
					internship.setFinalReport(uploadFinalReport.getUploadedFile());
				}
				
				buttonDownloadFinalReport.setVisible(true);
			}
		});
		
		this.buttonDownloadFinalReport = new Button("Baixar Relatório Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFinalReport();
            }
        });
		
		this.textComments = new TextArea();
		this.textComments.setSizeFull();
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboStudent, this.comboSupervisor);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboCompany, this.comboCompanySupervisor);
		h3.setSpacing(true);
		
		HorizontalLayout h4 = new HorizontalLayout(this.comboType, this.comboRequiredType, this.textTerm);
		h4.setSpacing(true);
		
		HorizontalLayout h9 = new HorizontalLayout(this.startDate, this.endDate, this.textWeekHours, this.textWeekDays, this.textTotalHours);
		h9.setSpacing(true);
		
		HorizontalLayout h5 = new HorizontalLayout(this.uploadInternshipPlan, this.uploadFinalReport);
		h5.setSpacing(true);
		
		VerticalLayout tab1 = new VerticalLayout(h1, h2, h3, h4, h9, this.textReportTitle);
		if(Session.isUserManager(SystemModule.SIGES)){
			tab1.addComponent(h5);
		}
		tab1.setSpacing(true);
		
		this.layoutStudentReport = new VerticalLayout();
		
		this.buttonUploadStudentReport = new Button("Enviar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addReport(ReportType.STUDENT);
            }
        });
		this.buttonUploadStudentReport.setIcon(FontAwesome.UPLOAD);
		this.buttonUploadStudentReport.setWidth("150px");
		
		this.buttonDownloadStudentReport = new Button("Download", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStudentReport();
            }
        });
		this.buttonDownloadStudentReport.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownloadStudentReport.setWidth("150px");
		
		this.buttonValidateStudentReport = new Button("Validar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	validateStudentReport();
            }
        });
		this.buttonValidateStudentReport.setIcon(FontAwesome.CHECK);
		this.buttonValidateStudentReport.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonValidateStudentReport.setWidth("150px");
		
		this.buttonDeleteStudentReport = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteStudentReport();
            }
        });
		this.buttonDeleteStudentReport.setIcon(FontAwesome.TRASH_O);
		this.buttonDeleteStudentReport.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonDeleteStudentReport.setWidth("150px");
		
		HorizontalLayout h6 = new HorizontalLayout(this.buttonUploadStudentReport, this.buttonDownloadStudentReport, this.buttonValidateStudentReport, this.buttonDeleteStudentReport);
		h6.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.layoutStudentReport, h6);
		tab2.setSpacing(true);
		
		this.layoutSupervisorReport = new VerticalLayout();
		
		this.buttonUploadSupervisorReport = new Button("Enviar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addReport(ReportType.SUPERVISOR);
            }
        });
		this.buttonUploadSupervisorReport.setIcon(FontAwesome.UPLOAD);
		this.buttonUploadSupervisorReport.setWidth("150px");
		
		this.buttonDownloadSupervisorReport = new Button("Download", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadSupervisorReport();
            }
        });
		this.buttonDownloadSupervisorReport.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownloadSupervisorReport.setWidth("150px");
		
		this.buttonValidateSupervisorReport = new Button("Validar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	validateSupervisorReport();
            }
        });
		this.buttonValidateSupervisorReport.setIcon(FontAwesome.CHECK);
		this.buttonValidateSupervisorReport.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonValidateSupervisorReport.setWidth("150px");
		
		this.buttonDeleteSupervisorReport = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteSupervisorReport();
            }
        });
		this.buttonDeleteSupervisorReport.setIcon(FontAwesome.TRASH_O);
		this.buttonDeleteSupervisorReport.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonDeleteSupervisorReport.setWidth("150px");
		
		HorizontalLayout h7 = new HorizontalLayout(this.buttonUploadSupervisorReport, this.buttonDownloadSupervisorReport, this.buttonValidateSupervisorReport, this.buttonDeleteSupervisorReport);
		h7.setSpacing(true);
		
		VerticalLayout tab3 = new VerticalLayout(this.layoutSupervisorReport, h7);
		tab3.setSpacing(true);
		
		this.layoutCompanySupervisorReport = new VerticalLayout();
		
		this.buttonUploadCompanySupervisorReport = new Button("Enviar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addReport(ReportType.COMPANY);
            }
        });
		this.buttonUploadCompanySupervisorReport.setIcon(FontAwesome.UPLOAD);
		this.buttonUploadCompanySupervisorReport.setWidth("150px");
		
		this.buttonValidateCompanySupervisorReport = new Button("Validar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	validateCompanySupervisorReport();
            }
        });
		this.buttonValidateCompanySupervisorReport.setIcon(FontAwesome.CHECK);
		this.buttonValidateCompanySupervisorReport.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonValidateCompanySupervisorReport.setWidth("150px");
		
		this.buttonDownloadCompanySupervisorReport = new Button("Download", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadCompanySupervisorReport();
            }
        });
		this.buttonDownloadCompanySupervisorReport.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownloadCompanySupervisorReport.setWidth("150px");
		
		this.buttonDeleteCompanySupervisorReport = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteCompanySupervisorReport();
            }
        });
		this.buttonDeleteCompanySupervisorReport.setIcon(FontAwesome.TRASH_O);
		this.buttonDeleteCompanySupervisorReport.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonDeleteCompanySupervisorReport.setWidth("150px");
		
		HorizontalLayout h8 = new HorizontalLayout(this.buttonUploadCompanySupervisorReport, this.buttonDownloadCompanySupervisorReport, this.buttonValidateCompanySupervisorReport, this.buttonDeleteCompanySupervisorReport);
		h8.setSpacing(true);
		
		VerticalLayout tab4 = new VerticalLayout(this.layoutCompanySupervisorReport, h8);
		tab4.setSpacing(true);
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("820px");
		this.tabContainer.setHeight("465px");
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		this.tabContainer.addTab(tab1, "Estágio");
		this.tabContainer.addTab(tab2, "Acadêmico");
		this.tabContainer.addTab(tab3, "Orientador");
		this.tabContainer.addTab(tab4, "Supervisor");
		this.tabContainer.addTab(this.textComments, "Observações");
		
		this.addField(this.tabContainer);
		
		this.addButton(this.buttonDownloadInternshipPlan);
		this.addButton(this.buttonDownloadFinalReport);
		
		this.buttonDownloadInternshipPlan.setWidth("250px");
		this.buttonDownloadFinalReport.setWidth("250px");
		
		if(!Session.isUserManager(SystemModule.SIGES)){
			this.setSaveButtonEnabled(false);
			this.buttonUploadStudentReport.setVisible(false);
			this.buttonUploadSupervisorReport.setVisible(false);
			this.buttonValidateStudentReport.setVisible(false);
			this.buttonValidateSupervisorReport.setVisible(false);
			this.buttonValidateCompanySupervisorReport.setVisible(false);
			this.buttonDeleteStudentReport.setVisible(false);
			this.buttonDeleteSupervisorReport.setVisible(false);
			this.buttonDeleteCompanySupervisorReport.setVisible(false);
			
			if(Session.getUser().getIdUser() == this.internship.getStudent().getIdUser()) {
				this.buttonUploadStudentReport.setVisible(true);
			}
			if(Session.getUser().getIdUser() == this.internship.getSupervisor().getIdUser()) {
				this.buttonUploadSupervisorReport.setVisible(true);
				this.buttonValidateStudentReport.setVisible(true);
			}
		}
		
		this.loadInternship();
		this.comboStudent.focus();
	}
	
	private void loadInternship(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.internship.getDepartment().getIdDepartment());
			
			if(campus != null){
				this.comboCampus.setCampus(campus);
				
				this.comboDepartment.setIdCampus(campus.getIdCampus());
				
				this.comboDepartment.setDepartment(this.internship.getDepartment());
			}else{
				this.comboCampus.setCampus(Session.getSelectedDepartment().getDepartment().getCampus());
				
				this.comboDepartment.setIdCampus(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus());
				
				this.comboDepartment.setDepartment(Session.getSelectedDepartment().getDepartment());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.comboStudent.setStudent(this.internship.getStudent());
		this.comboSupervisor.setProfessor(this.internship.getSupervisor());
		this.comboCompany.setCompany(this.internship.getCompany());
		this.comboCompanySupervisor.setSupervisor(this.internship.getCompanySupervisor());
		this.comboType.setValue(this.internship.getType());
		this.comboRequiredType.setValue(this.internship.getRequiredType());
		this.startDate.setValue(this.internship.getStartDate());
		this.endDate.setValue(this.internship.getEndDate());
		this.textTotalHours.setValue(String.valueOf(this.internship.getTotalHours()));
		this.textComments.setValue(this.internship.getComments());
		this.textReportTitle.setValue(this.internship.getReportTitle());
		this.textTerm.setValue(this.internship.getTerm());
		this.textWeekHours.setValue(String.valueOf(this.internship.getWeekHours()));
		this.textWeekDays.setValue(String.valueOf(this.internship.getWeekDays()));
		
		this.internship.setReports(null);
		
		this.loadReports();
		this.buttonDownloadInternshipPlan.setVisible(this.internship.getInternshipPlan() != null);
		this.buttonDownloadFinalReport.setVisible(this.internship.getFinalReport() != null);
		
		if(this.internship.getIdInternship() == 0) {
			this.internship.setFillOnlyTotalHours(this.config.isFillOnlyTotalHours());
		}
		
		if(this.internship.isFillOnlyTotalHours()) {
			this.textWeekHours.setVisible(false);
			this.textWeekDays.setVisible(false);
		} else {
			this.textTotalHours.setVisible(false);
		}
	}
	
	private void loadReports(){
		this.gridStudentReport = new Grid();
		this.gridStudentReport.addColumn("Relatório", String.class);
		this.gridStudentReport.addColumn("Data de Upload", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridStudentReport.addColumn("Feedback", String.class);
		this.gridStudentReport.setWidth("810px");
		this.gridStudentReport.setHeight("360px");
		this.gridStudentReport.getColumns().get(0).setWidth(150);
		this.gridStudentReport.getColumns().get(1).setWidth(150);
		
		this.gridSupervisorReport = new Grid();
		this.gridSupervisorReport.addColumn("Relatório", String.class);
		this.gridSupervisorReport.addColumn("Data de Upload", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridSupervisorReport.addColumn("Feedback", String.class);
		this.gridSupervisorReport.setWidth("810px");
		this.gridSupervisorReport.setHeight("360px");
		this.gridSupervisorReport.getColumns().get(0).setWidth(150);
		this.gridSupervisorReport.getColumns().get(1).setWidth(150);
		
		this.gridCompanySupervisorReport = new Grid();
		this.gridCompanySupervisorReport.addColumn("Relatório", String.class);
		this.gridCompanySupervisorReport.addColumn("Data de Upload", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridCompanySupervisorReport.addColumn("Feedback", String.class);
		this.gridCompanySupervisorReport.setWidth("810px");
		this.gridCompanySupervisorReport.setHeight("360px");
		this.gridCompanySupervisorReport.getColumns().get(0).setWidth(150);
		this.gridCompanySupervisorReport.getColumns().get(1).setWidth(150);
		
		if(this.internship.getReports() == null){
			try {
				InternshipReportBO bo = new InternshipReportBO();
				List<InternshipReport> list = bo.listByInternship(this.internship.getIdInternship());
				
				this.internship.setReports(list);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Relatórios", e.getMessage());
			}
		}
		
		if(this.internship.getReports() != null){
			int student = 1, supervisor = 1, company = 1;
			
			for(InternshipReport report : this.internship.getReports()){
				if(report.getType() == ReportType.STUDENT){
					this.gridStudentReport.addRow((report.isFinalReport() ? "Final" : "Parcial " + String.valueOf(student++)), report.getDate(), report.getFeedback().toString());
				}else if(report.getType() == ReportType.SUPERVISOR){
					this.gridSupervisorReport.addRow((report.isFinalReport() ? "Final" : "Parcial " + String.valueOf(supervisor++)), report.getDate(), report.getFeedback().toString());
				}else if(report.getType() == ReportType.COMPANY){
					this.gridCompanySupervisorReport.addRow((report.isFinalReport() ? "Final" : "Parcial " + String.valueOf(company++)), report.getDate(), report.getFeedback().toString());
				}
			}
		}
		
		this.layoutStudentReport.removeAllComponents();
		this.layoutStudentReport.addComponent(this.gridStudentReport);
		
		this.layoutSupervisorReport.removeAllComponents();
		this.layoutSupervisorReport.addComponent(this.gridSupervisorReport);
		
		this.layoutCompanySupervisorReport.removeAllComponents();
		this.layoutCompanySupervisorReport.addComponent(this.gridCompanySupervisorReport);
	}
	
	private void downloadInternshipPlan() {
		try {
        	this.showReport(this.internship.getInternshipPlan());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Plano de Estágio", e.getMessage());
		}
	}
	
	private void downloadFinalReport() {
		try {
        	this.showReport(this.internship.getFinalReport());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Relatório Final", e.getMessage());
		}
	}
	
	private void addReport(ReportType type) {
		UI.getCurrent().addWindow(new EditInternshipReportWindow(this, this.internship, type));
	}
	
	public void addReport(InternshipReport report) throws Exception {
		report.setDate(DateUtils.getToday().getTime());
		
		if(Session.isUserManager(SystemModule.SIGES)) {
			report.setFeedback(ReportFeedback.APPROVED);
			report.setFeedbackDate(DateUtils.getNow().getTime());
			report.setFeedbackUser(Session.getUser());
		}
		
        this.internship.getReports().add(report);
        
        this.loadReports();
	}
	
	public void editReport(InternshipReport report) throws Exception {
		for(InternshipReport r : this.internship.getReports()) {
			if(r.getIdInternshipReport() == report.getIdInternshipReport()) {
				r.setFeedback(report.getFeedback());
				r.setFeedbackDate(report.getFeedbackDate());
				r.setFeedbackUser(report.getFeedbackUser());
				
				this.loadReports();
			}
		}
	}
	
	private void validateStudentReport(){
		this.validateReport(this.getStudentReportSelectedIndex());
	}
	
	private void validateSupervisorReport(){
		this.validateReport(this.getSupervisorReportSelectedIndex());
	}
	
	private void validateCompanySupervisorReport(){
		this.validateReport(this.getCompanySupervisorReportSelectedIndex());
	}
	
	private void validateReport(int index) {
		if(index == -1) {
			this.showWarningNotification("Selecionar Relatório", "Selecione o relatório para validar.");
		} else if(this.internship.getReports().get(index).getIdInternshipReport() == 0) {
			this.showWarningNotification("Validar Relatório", "O relatório precisa ser salvo para ser validado.");
		} else {
			UI.getCurrent().addWindow(new ValidateInternshipReportWindow(this.internship.getReports().get(index), this));
		}
	}
	
	private int getStudentReportSelectedIndex(){
		return getReportSelectedIndex(this.gridStudentReport.getSelectedRow(), ReportType.STUDENT);
    }
	
	private int getSupervisorReportSelectedIndex(){
		return getReportSelectedIndex(this.gridSupervisorReport.getSelectedRow(), ReportType.SUPERVISOR);
    }
	
	private int getCompanySupervisorReportSelectedIndex(){
    	return getReportSelectedIndex(this.gridCompanySupervisorReport.getSelectedRow(), ReportType.COMPANY);
    }
	
	private int getReportSelectedIndex(Object itemId, ReportType type){
    	if(itemId == null){
    		return -1;
    	}else{
    		int selected = ((int)itemId) - 1, index = 0;
    		
    		for(int i = 0; i < this.internship.getReports().size(); i++){
    			if(this.internship.getReports().get(i).getType() == type){
    				if(index == selected){
    					return i;
    				}else{
    					index++;
    				}
    			}
    		}
    		
    		return -1;
    	}
    }
	
	private void deleteStudentReport(){
		this.deleteReport(this.getStudentReportSelectedIndex());
	}
	
	private void deleteSupervisorReport(){
		this.deleteReport(this.getSupervisorReportSelectedIndex());
	}
	
	private void deleteCompanySupervisorReport(){
		this.deleteReport(this.getCompanySupervisorReportSelectedIndex());
	}
	
	private void deleteReport(int index){
		if(index == -1){
			this.showWarningNotification("Selecionar Relatório", "Selecione o relatório para excluir.");
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a exclusão do relatório?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	internship.getReports().remove(index);
                    	loadReports();
                    }
                }
            });
		}
	}
	
	private void downloadStudentReport(){
		this.downloadReport(this.getStudentReportSelectedIndex());
	}
	
	private void downloadSupervisorReport(){
		this.downloadReport(this.getSupervisorReportSelectedIndex());
	}
	
	private void downloadCompanySupervisorReport(){
		this.downloadReport(this.getCompanySupervisorReportSelectedIndex());
	}
	
	private void downloadReport(int index) {
		if(index == -1) {
			this.showWarningNotification("Selecionar Relatório", "Selecione o relatório para baixar.");
		} else {
			try {
	        	this.showReport(this.internship.getReports().get(index).getReport());
	    	} catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	this.showErrorNotification("Download do Relatório de Estágio", e.getMessage());
			}
		}
	}
	
	@Override
	public void save() {
		try{
			InternshipBO bo = new InternshipBO();
			
			if(this.uploadInternshipPlan.getUploadedFile() != null) {
				this.internship.setInternshipPlan(this.uploadInternshipPlan.getUploadedFile());
			}
			
			if(this.uploadFinalReport.getUploadedFile() != null) {
				this.internship.setFinalReport(this.uploadFinalReport.getUploadedFile());
			}
			
			this.internship.setDepartment(this.comboDepartment.getDepartment());
			this.internship.setStudent(this.comboStudent.getStudent());
			this.internship.setSupervisor(this.comboSupervisor.getProfessor());
			this.internship.setCompany(this.comboCompany.getCompany());
			this.internship.setCompanySupervisor(this.comboCompanySupervisor.getSupervisor());
			this.internship.setType((InternshipType)this.comboType.getValue());
			this.internship.setRequiredType((InternshipRequiredType)this.comboRequiredType.getValue());
			this.internship.setStartDate(this.startDate.getValue());
			this.internship.setEndDate(this.endDate.getValue());
			this.internship.setTerm(this.textTerm.getValue());
			this.internship.setTotalHours(Integer.parseInt(this.textTotalHours.getValue()));
			this.internship.setWeekHours(Double.parseDouble(this.textWeekHours.getValue().replace(",", ".")));
			this.internship.setWeekDays(Integer.parseInt(this.textWeekDays.getValue()));
			this.internship.setComments(this.textComments.getValue());
			this.internship.setReportTitle(this.textReportTitle.getValue());
			
			bo.save(Session.getIdUserLog(), this.internship);
			
			this.showSuccessNotification("Salvar Estágio", "Estágio salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Estágio", e.getMessage());
		}
	}

}
