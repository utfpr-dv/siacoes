package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipReportBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.components.CompanySupervisorComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.ProfessorComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipWindow extends EditWindow {

	private final Internship internship;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final StudentComboBox comboStudent;
	private final ProfessorComboBox comboSupervisor;
	private final CompanyComboBox comboCompany;
	private final CompanySupervisorComboBox comboCompanySupervisor;
	private final NativeSelect comboType;
	private final TextArea textComments;
	private final DateField startDate;
	private final DateField endDate;
	private final TextField textTotalHours;
	private final TextField textReportTitle;
	private final Upload uploadInternshipPlan;
	private final Image imageInternshipPlanUploaded;
	private final Button buttonDownloadInternshipPlan;
	private final Upload uploadFinalReport;
	private final Image imageFinalReportUploaded;
	private final Button buttonDownloadFinalReport;
	private final TabSheet tabContainer;
	private Grid gridStudentReport;
	private Grid gridSupervisorReport;
	private Grid gridCompanySupervisorReport;
	private final VerticalLayout layoutStudentReport;
	private final VerticalLayout layoutSupervisorReport;
	private final VerticalLayout layoutCompanySupervisorReport;
	private final Upload uploadStudentReport;
	private final Upload uploadSupervisorReport;
	private final Upload uploadCompanySupervisorReport;
	private final Button buttonDownloadStudentReport;
	private final Button buttonDownloadSupervisorReport;
	private final Button buttonDownloadCompanySupervisorReport;
	private final Button buttonDeleteStudentReport;
	private final Button buttonDeleteSupervisorReport;
	private final Button buttonDeleteCompanySupervisorReport;
	
	private Button.ClickListener listenerStudentReportDownload;
	private Button.ClickListener listenerSupervisorReportDownload;
	private Button.ClickListener listenerCompanySupervisorReportDownload;
	
	public EditInternshipWindow(Internship internship, ListView parentView){
		super("Editar Estágio", parentView);
		
		if(internship == null){
			this.internship = new Internship();
		}else{
			this.internship = internship;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.comboStudent = new StudentComboBox("Aluno");
		
		this.comboSupervisor = new ProfessorComboBox("Orientador");
		
		this.comboCompanySupervisor = new CompanySupervisorComboBox();
		
		this.comboCompany = new CompanyComboBox();
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
		
		this.startDate = new DateField("Data de Início");
		this.startDate.setDateFormat("dd/MM/yyyy");
		
		this.endDate = new DateField("Data de Término");
		this.endDate.setDateFormat("dd/MM/yyyy");
		
		this.textTotalHours = new TextField("Horas");
		this.textTotalHours.setWidth("100px");
		
		this.textReportTitle = new TextField("Título do Relatório Final");
		this.textReportTitle.setWidth("810px");
		
		InternshipPlanUploader internshipPlanListener = new InternshipPlanUploader();
		this.uploadInternshipPlan = new Upload("(Formato PDF, Tam. Máx. 5 MB)", internshipPlanListener);
		this.uploadInternshipPlan.addSucceededListener(internshipPlanListener);
		this.uploadInternshipPlan.setButtonCaption("Plano de Estágio");
		this.uploadInternshipPlan.setImmediate(true);
		
		this.imageInternshipPlanUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageInternshipPlanUploaded.setVisible(false);
		
		this.buttonDownloadInternshipPlan = new Button("Baixar Plano de Estágio");
		
		FinalReportUploader finalReportUploader = new FinalReportUploader();
		this.uploadFinalReport = new Upload("(Formato PDF, Tam. Máx. 5 MB)", finalReportUploader);
		this.uploadFinalReport.addSucceededListener(finalReportUploader);
		this.uploadFinalReport.setButtonCaption("Relatório Final");
		this.uploadFinalReport.setImmediate(true);
		
		this.imageFinalReportUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageFinalReportUploaded.setVisible(false);
		
		this.buttonDownloadFinalReport = new Button("Baixar Relatório Final");
		
		this.textComments = new TextArea();
		this.textComments.setWidth("810px");
		this.textComments.setHeight("350px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboStudent, this.comboSupervisor);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboCompany, this.comboCompanySupervisor);
		h3.setSpacing(true);
		
		HorizontalLayout h4 = new HorizontalLayout(this.comboType, this.startDate, this.endDate, this.textTotalHours);
		h4.setSpacing(true);
		
		HorizontalLayout h5 = new HorizontalLayout(this.uploadInternshipPlan, this.imageInternshipPlanUploaded, this.uploadFinalReport, this.imageFinalReportUploaded);
		
		VerticalLayout tab1 = new VerticalLayout(h1, h2, h3, h4, this.textReportTitle);
		if(Session.isUserManager(SystemModule.SIGES)){
			tab1.addComponent(h5);
		}
		tab1.setSpacing(true);
		
		this.layoutStudentReport = new VerticalLayout();
		
		ReportUploader studentReportUploader = new ReportUploader(ReportType.STUDENT);
		this.uploadStudentReport = new Upload(null, studentReportUploader);
		this.uploadStudentReport.addSucceededListener(studentReportUploader);
		this.uploadStudentReport.setButtonCaption("Upload");
		this.uploadStudentReport.setWidth("150px");
		this.uploadStudentReport.setImmediate(true);
		
		this.buttonDownloadStudentReport = new Button("Download");
		this.buttonDownloadStudentReport.setWidth("150px");
		
		this.buttonDeleteStudentReport = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteStudentReport();
            }
        });
		this.buttonDeleteStudentReport.setWidth("150px");
		
		HorizontalLayout h6 = new HorizontalLayout(this.uploadStudentReport, this.buttonDownloadStudentReport, this.buttonDeleteStudentReport);
		h6.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.layoutStudentReport, h6);
		tab2.setSpacing(true);
		
		this.layoutSupervisorReport = new VerticalLayout();
		
		ReportUploader supervisorReportUploader = new ReportUploader(ReportType.SUPERVISOR);
		this.uploadSupervisorReport = new Upload(null, supervisorReportUploader);
		this.uploadSupervisorReport.addSucceededListener(supervisorReportUploader);
		this.uploadSupervisorReport.setButtonCaption("Upload");
		this.uploadSupervisorReport.setWidth("150px");
		this.uploadSupervisorReport.setImmediate(true);
		
		this.buttonDownloadSupervisorReport = new Button("Download");
		this.buttonDownloadSupervisorReport.setWidth("150px");
		
		this.buttonDeleteSupervisorReport = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteSupervisorReport();
            }
        });
		this.buttonDeleteSupervisorReport.setWidth("150px");
		
		HorizontalLayout h7 = new HorizontalLayout(this.uploadSupervisorReport, this.buttonDownloadSupervisorReport, this.buttonDeleteSupervisorReport);
		h7.setSpacing(true);
		
		VerticalLayout tab3 = new VerticalLayout(this.layoutSupervisorReport, h7);
		tab3.setSpacing(true);
		
		this.layoutCompanySupervisorReport = new VerticalLayout();
		
		ReportUploader companySupervisorReportUploader = new ReportUploader(ReportType.COMPANY);
		this.uploadCompanySupervisorReport = new Upload(null, companySupervisorReportUploader);
		this.uploadCompanySupervisorReport.addSucceededListener(companySupervisorReportUploader);
		this.uploadCompanySupervisorReport.setButtonCaption("Upload");
		this.uploadCompanySupervisorReport.setWidth("150px");
		this.uploadCompanySupervisorReport.setImmediate(true);
		
		this.buttonDownloadCompanySupervisorReport = new Button("Download");
		this.buttonDownloadCompanySupervisorReport.setWidth("150px");
		
		this.buttonDeleteCompanySupervisorReport = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteCompanySupervisorReport();
            }
        });
		this.buttonDeleteCompanySupervisorReport.setWidth("150px");
		
		HorizontalLayout h8 = new HorizontalLayout(this.uploadCompanySupervisorReport, this.buttonDownloadCompanySupervisorReport, this.buttonDeleteCompanySupervisorReport);
		h8.setSpacing(true);
		
		VerticalLayout tab4 = new VerticalLayout(this.layoutCompanySupervisorReport, h8);
		tab4.setSpacing(true);
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("820px");
		this.tabContainer.addTab(tab1, "Estágio");
		this.tabContainer.addTab(tab2, "Aluno");
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
			this.uploadStudentReport.setVisible(false);
			this.uploadSupervisorReport.setVisible(false);
			this.uploadCompanySupervisorReport.setVisible(false);
			this.buttonDeleteStudentReport.setVisible(false);
			this.buttonDeleteSupervisorReport.setVisible(false);
			this.buttonDeleteCompanySupervisorReport.setVisible(false);
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
				this.comboCampus.setCampus(Session.getUser().getDepartment().getCampus());
				
				this.comboDepartment.setIdCampus(Session.getUser().getDepartment().getCampus().getIdCampus());
				
				this.comboDepartment.setDepartment(Session.getUser().getDepartment());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.comboStudent.setStudent(this.internship.getStudent());
		this.comboSupervisor.setProfessor(this.internship.getSupervisor());
		this.comboCompany.setCompany(this.internship.getCompany());
		this.comboCompanySupervisor.setSupervisor(this.internship.getCompanySupervisor());
		this.comboType.setValue(this.internship.getType());
		this.startDate.setValue(this.internship.getStartDate());
		this.endDate.setValue(this.internship.getEndDate());
		this.textTotalHours.setValue(String.valueOf(this.internship.getTotalHours()));
		this.textComments.setValue(this.internship.getComments());
		this.textReportTitle.setValue(this.internship.getReportTitle());
		
		this.imageInternshipPlanUploaded.setVisible(this.internship.getInternshipPlan() != null);
		this.imageFinalReportUploaded.setVisible(this.internship.getFinalReport() != null);
		
		this.internship.setReports(null);
		
		this.prepareDownloadInternshipPlan();
		this.prepareDownloadFinalReport();
		
		this.loadReports();
	}
	
	private void loadReports(){
		this.gridStudentReport = new Grid();
		this.gridStudentReport.addColumn("Relatório", Integer.class);
		this.gridStudentReport.addColumn("Data de Upload", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridStudentReport.setWidth("810px");
		this.gridStudentReport.setHeight("300px");
		this.gridStudentReport.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareStudentReportDownload();
			}
		});
		
		this.gridSupervisorReport = new Grid();
		this.gridSupervisorReport.addColumn("Relatório", Integer.class);
		this.gridSupervisorReport.addColumn("Data de Upload", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridSupervisorReport.setWidth("810px");
		this.gridSupervisorReport.setHeight("300px");
		this.gridSupervisorReport.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareSupervisorReportDownload();
			}
		});
		
		this.gridCompanySupervisorReport = new Grid();
		this.gridCompanySupervisorReport.addColumn("Relatório", Integer.class);
		this.gridCompanySupervisorReport.addColumn("Data de Upload", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridCompanySupervisorReport.setWidth("810px");
		this.gridCompanySupervisorReport.setHeight("300px");
		this.gridCompanySupervisorReport.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareCompanySupervisorReportDownload();
			}
		});
		
		if(this.internship.getReports() == null){
			try {
				InternshipReportBO bo = new InternshipReportBO();
				List<InternshipReport> list = bo.listByInternship(this.internship.getIdInternship());
				
				this.internship.setReports(list);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Carregar Relatórios", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
		
		if(this.internship.getReports() != null){
			int student = 1, supervisor = 1, company = 1;
			
			for(InternshipReport report : this.internship.getReports()){
				if(report.getType() == ReportType.STUDENT){
					this.gridStudentReport.addRow(student, report.getDate());
					student++;
				}else if(report.getType() == ReportType.SUPERVISOR){
					this.gridSupervisorReport.addRow(supervisor, report.getDate());
					supervisor++;
				}else if(report.getType() == ReportType.COMPANY){
					this.gridCompanySupervisorReport.addRow(company, report.getDate());
					company++;
				}
			}
		}
		
		this.layoutStudentReport.removeAllComponents();
		this.layoutStudentReport.addComponent(this.gridStudentReport);
		
		this.layoutSupervisorReport.removeAllComponents();
		this.layoutSupervisorReport.addComponent(this.gridSupervisorReport);
		
		this.layoutCompanySupervisorReport.removeAllComponents();
		this.layoutCompanySupervisorReport.addComponent(this.gridCompanySupervisorReport);
		
		this.prepareStudentReportDownload();
		this.prepareSupervisorReportDownload();
		this.prepareCompanySupervisorReportDownload();
	}
	
	private void prepareDownloadInternshipPlan(){
		new ExtensionUtils().removeAllExtensions(this.buttonDownloadInternshipPlan);
		
		if(this.internship.getInternshipPlan() != null){
			this.buttonDownloadInternshipPlan.setVisible(true);
			
			new ExtensionUtils().extendToDownload("Plano_de_Estagio_" + this.internship.getIdInternship() + Document.DocumentType.PDF.getExtension(), this.internship.getInternshipPlan(), this.buttonDownloadInternshipPlan);
		}else{
			this.buttonDownloadInternshipPlan.setVisible(false);
		}
	}
	
	private void prepareDownloadFinalReport(){
		new ExtensionUtils().removeAllExtensions(this.buttonDownloadFinalReport);
		
		if(this.internship.getFinalReport() != null){
			this.buttonDownloadFinalReport.setVisible(true);
			
			new ExtensionUtils().extendToDownload("Relatorio_Final_" + this.internship.getIdInternship() + Document.DocumentType.PDF.getExtension(), this.internship.getFinalReport(), this.buttonDownloadFinalReport);
		}else{
			this.buttonDownloadFinalReport.setVisible(false);
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
    		int selected = ((int)itemId) - 1, index = 0;;
    		
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
			Notification.show("Selecionar Relatório", "Selecione o relatório para excluir.", Notification.Type.WARNING_MESSAGE);
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
	
	private void prepareStudentReportDownload(){
		int index = this.getStudentReportSelectedIndex();
		
		this.buttonDownloadStudentReport.removeClickListener(this.listenerStudentReportDownload);
    	
    	if(index >= 0){
			try {
				InternshipReport report = this.internship.getReports().get(index);
				
            	new ExtensionUtils().extendToDownload(this.internship.getIdInternship() + "_" + report.getIdInternshipReport() + Document.DocumentType.PDF.getExtension(), report.getReport(), this.buttonDownloadStudentReport);
        	} catch (Exception e) {
        		this.listenerStudentReportDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download do Relatório", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownloadStudentReport.addClickListener(this.listenerStudentReportDownload);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonDownloadStudentReport);
    		
    		this.listenerStudentReportDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download do Relatório", "Selecione um registro para baixar o relatório.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonDownloadStudentReport.addClickListener(this.listenerStudentReportDownload);
    	}
	}
	
	private void prepareSupervisorReportDownload(){
		int index = this.getSupervisorReportSelectedIndex();
		
		this.buttonDownloadSupervisorReport.removeClickListener(this.listenerSupervisorReportDownload);
    	
    	if(index >= 0){
			try {
				InternshipReport report = this.internship.getReports().get(index);
				
            	new ExtensionUtils().extendToDownload(this.internship.getIdInternship() + "_" + report.getIdInternshipReport() + Document.DocumentType.PDF.getExtension(), report.getReport(), this.buttonDownloadSupervisorReport);
        	} catch (Exception e) {
        		this.listenerSupervisorReportDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download do Relatório", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownloadSupervisorReport.addClickListener(this.listenerSupervisorReportDownload);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonDownloadStudentReport);
    		
    		this.listenerSupervisorReportDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download do Relatório", "Selecione um registro para baixar o relatório.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonDownloadSupervisorReport.addClickListener(this.listenerSupervisorReportDownload);
    	}
	}
	
	private void prepareCompanySupervisorReportDownload(){
		int index = this.getCompanySupervisorReportSelectedIndex();
		
		this.buttonDownloadCompanySupervisorReport.removeClickListener(this.listenerCompanySupervisorReportDownload);
    	
    	if(index >= 0){
			try {
				InternshipReport report = this.internship.getReports().get(index);
				
            	new ExtensionUtils().extendToDownload(this.internship.getIdInternship() + "_" + report.getIdInternshipReport() + Document.DocumentType.PDF.getExtension(), report.getReport(), this.buttonDownloadCompanySupervisorReport);
        	} catch (Exception e) {
        		this.listenerCompanySupervisorReportDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download do Relatório", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownloadCompanySupervisorReport.addClickListener(this.listenerCompanySupervisorReportDownload);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonDownloadStudentReport);
    		
    		this.listenerCompanySupervisorReportDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download do Relatório", "Selecione um registro para baixar o relatório.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonDownloadCompanySupervisorReport.addClickListener(this.listenerCompanySupervisorReportDownload);
    	}
	}
	
	@Override
	public void save() {
		try{
			InternshipBO bo = new InternshipBO();
			
			this.internship.setDepartment(this.comboDepartment.getDepartment());
			this.internship.setStudent(this.comboStudent.getStudent());
			this.internship.setSupervisor(this.comboSupervisor.getProfessor());
			this.internship.setCompany(this.comboCompany.getCompany());
			this.internship.setCompanySupervisor(this.comboCompanySupervisor.getSupervisor());
			this.internship.setType((InternshipType)this.comboType.getValue());
			this.internship.setStartDate(this.startDate.getValue());
			this.internship.setEndDate(this.endDate.getValue());
			this.internship.setTotalHours(Integer.parseInt(this.textTotalHours.getValue()));
			this.internship.setComments(this.textComments.getValue());
			this.internship.setReportTitle(this.textReportTitle.getValue());
			
			bo.save(this.internship);
			
			Notification.show("Salvar Estágio", "Estágio salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Estágio", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@SuppressWarnings("serial")
	class InternshipPlanUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageInternshipPlanUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            internship.setInternshipPlan(buffer);
	            
	            imageInternshipPlanUploaded.setVisible(true);
	            prepareDownloadInternshipPlan();
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}
	
	@SuppressWarnings("serial")
	class FinalReportUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageFinalReportUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            internship.setFinalReport(buffer);
	            
	            imageFinalReportUploaded.setVisible(true);
	            prepareDownloadFinalReport();
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}
	
	@SuppressWarnings("serial")
	class ReportUploader implements Receiver, SucceededListener {
		
		private File tempFile;
		private ReportType type;
		
		public ReportUploader(ReportType type){
			this.type = type;
		}
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            InternshipReport report = new InternshipReport();
	            report.setDate(DateUtils.getToday().getTime());
	            report.setType(this.type);
	            report.setReport(buffer);
	            
	            internship.getReports().add(report);
	            
	            loadReports();
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
