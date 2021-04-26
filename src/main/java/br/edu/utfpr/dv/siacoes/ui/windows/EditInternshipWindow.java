package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipReportBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipRequiredType;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.CompanySupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.TokenField;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader.AcceptedDocumentType;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipReportDataSource;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryGradeDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditInternshipWindow extends EditWindow {

	private final Internship internship;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final StudentComboBox comboStudent;
	private final SupervisorComboBox comboSupervisor;
	private final CompanyComboBox comboCompany;
	private final CompanySupervisorComboBox comboCompanySupervisor;
	private final Select<InternshipType> comboType;
	private final Select<InternshipRequiredType> comboRequiredType;
	private final TextArea textComments;
	private final DatePicker startDate;
	private final DatePicker endDate;
	private final DatePicker terminationDate;
	private final IntegerField textTotalHours;
	private final NumberField textWeekHours;
	private final TextField textWeekDays;
	private final TextField textTerm;
	private final TextField textSei;
	private final TextField textReportTitle;
	private final FileUploader uploadInternshipPlan;
	private final Button buttonDownloadInternshipPlan;
	private final FileUploader uploadFinalReport;
	private final Button buttonDownloadFinalReport;
	private final Tabs tabContainer;
	private final Tabs tabReport;
	private Grid<InternshipReportDataSource> gridStudentReport;
	private Grid<InternshipReportDataSource> gridSupervisorReport;
	private Grid<InternshipReportDataSource> gridCompanySupervisorReport;
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
	private final Button buttonJury;
	private final TokenField tags;

	private final VerticalLayout layoutJury;
	private final VerticalLayout layoutGrades;
	private final DateTimePicker textJuryDate;
	private final TextField textJuryLocal;
	private final TextField textJurySei;
	private final Tab tabJury;
	
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
		this.comboCampus.setWidth("425px");
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setWidth("425px");
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		this.comboStudent.setRequired(true);
		this.comboStudent.setWidth("425px");
		
		this.comboSupervisor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigesConfigBO().getSupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboSupervisor.setRequired(true);
		this.comboSupervisor.setWidth("425px");
		
		this.comboCompanySupervisor = new CompanySupervisorComboBox();
		this.comboCompanySupervisor.setRequired(true);
		this.comboCompanySupervisor.setWidth("425px");
		
		this.comboCompany = new CompanyComboBox();
		this.comboCompany.setRequired(true);
		this.comboCompany.addValueChangeListener(event -> {
			if(comboCompany.getCompany() == null){
				comboCompanySupervisor.setIdCompany(0);
			}else{
				comboCompanySupervisor.setIdCompany(comboCompany.getCompany().getIdCompany());	
			}
		});
		this.comboCompany.setWidth("425px");
		
		this.comboType = new Select<InternshipType>();
		this.comboType.setLabel("Tipo de Estágio");
		this.comboType.setWidth("175px");
		this.comboType.setItems(InternshipType.NONREQUIRED, InternshipType.REQUIRED);
		this.comboType.setValue(InternshipType.NONREQUIRED);
		
		this.comboRequiredType = new Select<InternshipRequiredType>();
		this.comboRequiredType.setLabel("Categorização do Estágio");
		this.comboRequiredType.setWidth("200px");
		this.comboRequiredType.setItems(InternshipRequiredType.UNIVERSITY, InternshipRequiredType.EXTERNAL, InternshipRequiredType.SCHOLARSHIP, InternshipRequiredType.PROFESSIONAL, InternshipRequiredType.VALIDATION);
		this.comboRequiredType.setValue(InternshipRequiredType.UNIVERSITY);
		
		this.startDate = new DatePicker("Data de Início");
		//this.startDate.setDateFormat("dd/MM/yyyy");
		this.startDate.setRequired(true);
		
		this.endDate = new DatePicker("Data de Término");
		//this.endDate.setDateFormat("dd/MM/yyyy");
		
		this.terminationDate = new DatePicker("Data de Rescisão");
		
		this.textTotalHours = new IntegerField("Horas");
		this.textTotalHours.setWidth("100px");
		//this.textTotalHours.setRequired(true);
		
		this.textWeekHours = new NumberField("C.H. Sem.");
		this.textWeekHours.setWidth("100px");
		//this.textWeekHours.setRequired(true);
		
		this.textWeekDays = new TextField("Dias/Sem.");
		this.textWeekDays.setWidth("100px");
		this.textWeekDays.setRequired(true);
		
		this.textTerm = new TextField("Termo");
		this.textTerm.setWidth("150px");
		
		this.textSei = new TextField("Processo no SEI");
		this.textSei.setWidth("200px");
		
		this.textReportTitle = new TextField("Título do Relatório Final");
		this.textReportTitle.setWidth("860px");
		
		this.uploadInternshipPlan = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadInternshipPlan.setAcceptedType(AcceptedDocumentType.PDF);
		this.uploadInternshipPlan.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadInternshipPlan.setDropLabel("Enviar Plano de Estágio");
		this.uploadInternshipPlan.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadInternshipPlan.getUploadedFile() != null) {
					internship.setInternshipPlan(uploadInternshipPlan.getUploadedFile());
				}
				
				buttonDownloadInternshipPlan.setVisible(true);
			}
		});
		
		this.buttonDownloadInternshipPlan = new Button("Baixar Plano de Estágio", event -> {
            downloadInternshipPlan();
        });
		this.buttonDownloadInternshipPlan.setIcon(new Icon(VaadinIcon.FILE_O));
		
		this.uploadFinalReport = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFinalReport.setAcceptedType(AcceptedDocumentType.PDF);
		this.uploadFinalReport.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFinalReport.setDropLabel("Enviar Relatório Final");
		this.uploadFinalReport.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFinalReport.getUploadedFile() != null) {
					internship.setFinalReport(uploadFinalReport.getUploadedFile());
				}
				
				buttonDownloadFinalReport.setVisible(true);
			}
		});
		
		this.buttonDownloadFinalReport = new Button("Baixar Relatório Final", event -> {
            downloadFinalReport();
        });
		this.buttonDownloadFinalReport.setIcon(new Icon(VaadinIcon.FILE_O));
		
		this.textComments = new TextArea();
		this.textComments.setWidth("870px");
		this.textComments.setHeight("370px");
		this.textComments.setVisible(false);
		
		this.textJuryDate = new DateTimePicker("Data e Hora");
		this.textJuryDate.setWidth("250px");

		this.textJuryLocal = new TextField("Local");
		this.textJuryLocal.setWidth("300px");

		this.textJurySei = new TextField("Processo no SEI");
		this.textJurySei.setWidth("200px");
		
		this.tags = new TokenField("Tags");

		HorizontalLayout hJury = new HorizontalLayout(this.textJuryDate, this.textJuryLocal, this.textJurySei);
		hJury.setSpacing(true);

		this.layoutGrades = new VerticalLayout();
		this.layoutGrades.setSpacing(false);
		this.layoutGrades.setWidth("100%");

		this.layoutJury = new VerticalLayout(hJury, this.layoutGrades);
		this.layoutJury.setSpacing(true);
		this.layoutJury.setVisible(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboStudent, this.comboSupervisor);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboCompany, this.comboCompanySupervisor);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		
		VerticalLayout tab1 = new VerticalLayout(h1, h2, h3, this.tags);
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		HorizontalLayout h4 = new HorizontalLayout(this.comboType, this.comboRequiredType, this.textTerm, this.textSei);
		h4.setSpacing(true);
		h4.setMargin(false);
		h4.setPadding(false);
		
		HorizontalLayout h9 = new HorizontalLayout(this.startDate, this.endDate, this.textWeekHours, this.textWeekDays, this.textTotalHours, this.terminationDate);
		h9.setSpacing(true);
		h9.setMargin(false);
		h9.setPadding(false);
		
		HorizontalLayout h5 = new HorizontalLayout(this.uploadInternshipPlan, this.uploadFinalReport);
		h5.setSpacing(true);
		h5.setMargin(false);
		h5.setPadding(false);
		
		VerticalLayout tab2 = new VerticalLayout(h4, h9, this.textReportTitle);
		if(Session.isUserManager(SystemModule.SIGES)){
			tab2.add(h5);
		}
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		
		this.gridStudentReport = new Grid<InternshipReportDataSource>();
		this.gridStudentReport.setSelectionMode(SelectionMode.SINGLE);
		this.gridStudentReport.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridStudentReport.addColumn(InternshipReportDataSource::getTitle).setHeader("Relatório").setFlexGrow(0).setWidth("150px");
		this.gridStudentReport.addColumn(new LocalDateRenderer<>(InternshipReportDataSource::getDate, "dd/MM/yyyy")).setHeader("Data de Upload").setFlexGrow(0).setWidth("150px");
		this.gridStudentReport.addColumn(InternshipReportDataSource::getFeedback).setHeader("Feedback");
		this.gridStudentReport.setWidth("860px");
		this.gridStudentReport.setHeight("290px");
		
		this.buttonUploadStudentReport = new Button("Enviar", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            addReport(ReportType.STUDENT);
        });
		this.buttonUploadStudentReport.setWidth("150px");
		
		this.buttonDownloadStudentReport = new Button("Download", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadStudentReport();
        });
		this.buttonDownloadStudentReport.setWidth("150px");
		
		this.buttonValidateStudentReport = new Button("Validar", new Icon(VaadinIcon.CHECK), event -> {
            validateStudentReport();
        });
		this.buttonValidateStudentReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonValidateStudentReport.setWidth("150px");
		
		this.buttonDeleteStudentReport = new Button("Excluir", new Icon(VaadinIcon.TRASH), event -> {
            deleteStudentReport();
        });
		this.buttonDeleteStudentReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonDeleteStudentReport.setWidth("150px");
		
		HorizontalLayout h6 = new HorizontalLayout(this.buttonUploadStudentReport, this.buttonDownloadStudentReport, this.buttonValidateStudentReport, this.buttonDeleteStudentReport);
		h6.setSpacing(true);
		h6.setMargin(false);
		h6.setPadding(false);
		
		VerticalLayout tabStudentReport = new VerticalLayout(this.gridStudentReport, h6);
		tabStudentReport.setSpacing(false);
		tabStudentReport.setMargin(false);
		tabStudentReport.setPadding(false);
		
		this.gridSupervisorReport = new Grid<InternshipReportDataSource>();
		this.gridSupervisorReport.setSelectionMode(SelectionMode.SINGLE);
		this.gridSupervisorReport.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSupervisorReport.addColumn(InternshipReportDataSource::getTitle).setHeader("Relatório").setFlexGrow(0).setWidth("150px");
		this.gridSupervisorReport.addColumn(new LocalDateRenderer<>(InternshipReportDataSource::getDate, "dd/MM/yyyy")).setHeader("Data de Upload").setFlexGrow(0).setWidth("150px");
		this.gridSupervisorReport.addColumn(InternshipReportDataSource::getFeedback).setHeader("Feedback");
		this.gridSupervisorReport.setWidth("860px");
		this.gridSupervisorReport.setHeight("290px");
		
		this.buttonUploadSupervisorReport = new Button("Enviar", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            addReport(ReportType.SUPERVISOR);
        });
		this.buttonUploadSupervisorReport.setWidth("150px");
		
		this.buttonDownloadSupervisorReport = new Button("Download", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadSupervisorReport();
        });
		this.buttonDownloadSupervisorReport.setWidth("150px");
		
		this.buttonValidateSupervisorReport = new Button("Validar", new Icon(VaadinIcon.CHECK), event -> {
            validateSupervisorReport();
        });
		this.buttonValidateSupervisorReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonValidateSupervisorReport.setWidth("150px");
		
		this.buttonDeleteSupervisorReport = new Button("Excluir", new Icon(VaadinIcon.TRASH), event -> {
            deleteSupervisorReport();
        });
		this.buttonDeleteSupervisorReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonDeleteSupervisorReport.setWidth("150px");
		
		HorizontalLayout h7 = new HorizontalLayout(this.buttonUploadSupervisorReport, this.buttonDownloadSupervisorReport, this.buttonValidateSupervisorReport, this.buttonDeleteSupervisorReport);
		h7.setSpacing(true);
		h7.setMargin(false);
		h7.setPadding(false);
		
		VerticalLayout tabSupervisorReport = new VerticalLayout(this.gridSupervisorReport, h7);
		tabSupervisorReport.setSpacing(false);
		tabSupervisorReport.setMargin(false);
		tabSupervisorReport.setPadding(false);
		tabSupervisorReport.setVisible(false);
		
		this.gridCompanySupervisorReport = new Grid<InternshipReportDataSource>();
		this.gridCompanySupervisorReport.setSelectionMode(SelectionMode.SINGLE);
		this.gridCompanySupervisorReport.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridCompanySupervisorReport.addColumn(InternshipReportDataSource::getTitle).setHeader("Relatório").setFlexGrow(0).setWidth("150px");
		this.gridCompanySupervisorReport.addColumn(new LocalDateRenderer<>(InternshipReportDataSource::getDate, "dd/MM/yyyy")).setHeader("Data de Upload").setFlexGrow(0).setWidth("150px");
		this.gridCompanySupervisorReport.addColumn(InternshipReportDataSource::getFeedback).setHeader("Feedback");
		this.gridCompanySupervisorReport.setWidth("860px");
		this.gridCompanySupervisorReport.setHeight("290px");
		
		this.buttonUploadCompanySupervisorReport = new Button("Enviar", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            addReport(ReportType.COMPANY);
        });

		this.buttonUploadCompanySupervisorReport.setWidth("150px");
		
		this.buttonValidateCompanySupervisorReport = new Button("Validar", new Icon(VaadinIcon.CHECK), event -> {
            validateCompanySupervisorReport();
        });
		this.buttonValidateCompanySupervisorReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonValidateCompanySupervisorReport.setWidth("150px");
		
		this.buttonDownloadCompanySupervisorReport = new Button("Download", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadCompanySupervisorReport();
        });
		this.buttonDownloadCompanySupervisorReport.setWidth("150px");
		
		this.buttonDeleteCompanySupervisorReport = new Button("Excluir", new Icon(VaadinIcon.TRASH), event -> {
            deleteCompanySupervisorReport();
        });
		this.buttonDeleteCompanySupervisorReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonDeleteCompanySupervisorReport.setWidth("150px");
		
		HorizontalLayout h8 = new HorizontalLayout(this.buttonUploadCompanySupervisorReport, this.buttonDownloadCompanySupervisorReport, this.buttonValidateCompanySupervisorReport, this.buttonDeleteCompanySupervisorReport);
		h8.setSpacing(true);
		h8.setMargin(false);
		h8.setPadding(false);
		
		VerticalLayout tabCompanySupervisorReport = new VerticalLayout(this.gridCompanySupervisorReport, h8);
		tabCompanySupervisorReport.setSpacing(false);
		tabCompanySupervisorReport.setMargin(false);
		tabCompanySupervisorReport.setPadding(false);
		tabCompanySupervisorReport.setVisible(false);
		
		Tab tStudentReport = new Tab("Acadêmico");
		Tab tSupervisorReport = new Tab("Orientador");
		Tab tCompanySupervisorReport = new Tab("Supervisor");
		
		Map<Tab, Component> tabsToPages2 = new HashMap<>();
		tabsToPages2.put(tStudentReport, tabStudentReport);
		tabsToPages2.put(tSupervisorReport, tabSupervisorReport);
		tabsToPages2.put(tCompanySupervisorReport, tabCompanySupervisorReport);
		Div pages2 = new Div(tabStudentReport, tabSupervisorReport, tabCompanySupervisorReport);
		
		this.tabReport = new Tabs(tStudentReport, tSupervisorReport, tCompanySupervisorReport);
		this.tabReport.setWidthFull();
		this.tabReport.setFlexGrowForEnclosedTabs(1);
		
		this.tabReport.addSelectedChangeListener(event -> {
			tabsToPages2.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages2.get(this.tabReport.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tabReport.setSelectedTab(tStudentReport);
		
		VerticalLayout tab3 = new VerticalLayout(this.tabReport, pages2);
		tab3.setWidth("860px");
		tab3.setHeight("400px");
		tab3.setSpacing(false);
		tab3.setMargin(false);
		tab3.setPadding(false);
		tab3.setVisible(false);
		
		Tab t1 = new Tab("Informações Gerais");
		Tab t2 = new Tab("Plano de Estágio");
		Tab t3 = new Tab("Relatórios");
		Tab t4 = new Tab("Observações");
		this.tabJury = new Tab("Banca");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(t1, tab1);
		tabsToPages.put(t2, tab2);
		tabsToPages.put(t3, tab3);
		tabsToPages.put(t4, this.textComments);
		tabsToPages.put(this.tabJury, this.layoutJury);
		Div pages = new Div(tab1, tab2, tab3, this.textComments, this.layoutJury);
		
		this.tabContainer = new Tabs(t1, t2, t3, t4, this.tabJury);
		this.tabContainer.setWidthFull();
		this.tabContainer.setFlexGrowForEnclosedTabs(1);
		
		this.tabContainer.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tabContainer.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tabContainer.setSelectedTab(t1);
		
		VerticalLayout layout = new VerticalLayout(this.tabContainer, pages);
		layout.setWidth("870px");
		layout.setHeight("430px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		this.buttonJury = new Button("Banca", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
			viewJury();
        });
		
		this.addButton(this.buttonDownloadInternshipPlan);
		this.addButton(this.buttonDownloadFinalReport);
		this.addButton(this.buttonJury);
		
		this.buttonDownloadInternshipPlan.setWidth("250px");
		this.buttonDownloadFinalReport.setWidth("250px");
		
		if(!Session.isUserManager(SystemModule.SIGES)){
			this.setSaveButtonEnabled(false);
			this.buttonJury.setVisible(false);
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.tags.setPredefinedTokens(new InternshipBO().getTagsList(this.internship.getDepartment().getIdDepartment(), false));
		
		this.comboStudent.setStudent(this.internship.getStudent());
		this.comboSupervisor.setProfessor(this.internship.getSupervisor());
		this.comboCompany.setCompany(this.internship.getCompany());
		this.comboCompanySupervisor.setSupervisor(this.internship.getCompanySupervisor());
		this.comboType.setValue(this.internship.getType());
		this.comboRequiredType.setValue(this.internship.getRequiredType());
		this.startDate.setValue(DateUtils.convertToLocalDate(this.internship.getStartDate()));
		this.endDate.setValue(DateUtils.convertToLocalDate(this.internship.getEndDate()));
		this.terminationDate.setValue(DateUtils.convertToLocalDate(this.internship.getTerminationDate()));
		this.textTotalHours.setValue(this.internship.getTotalHours());
		this.textComments.setValue(this.internship.getComments());
		this.textReportTitle.setValue(this.internship.getReportTitle());
		this.textTerm.setValue(this.internship.getTerm());
		this.textWeekHours.setValue(this.internship.getWeekHours());
		this.textWeekDays.setValue(String.valueOf(this.internship.getWeekDays()));
		this.textSei.setValue(this.internship.getSei());
		this.tags.setValue(this.internship.getTags());
		
		this.internship.setReports(null);
		
		this.loadReports();
		this.loadJuryInfo();
		
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
		
		if(!this.config.isUseSei() && this.internship.getSei().trim().isEmpty()) {
			this.textSei.setVisible(false);
		}
	}
	
	private void loadReports(){
		if(this.internship.getReports() == null){
			try {
				InternshipReportBO bo = new InternshipReportBO();
				List<InternshipReport> list = bo.listByInternship(this.internship.getIdInternship());
				
				this.internship.setReports(list);
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Relatórios", e.getMessage());
			}
		}
		
		if(this.internship.getReports() != null){
			this.gridStudentReport.setItems(InternshipReportDataSource.load(this.internship.getReports(), ReportType.STUDENT));
			this.gridSupervisorReport.setItems(InternshipReportDataSource.load(this.internship.getReports(), ReportType.SUPERVISOR));
			this.gridCompanySupervisorReport.setItems(InternshipReportDataSource.load(this.internship.getReports(), ReportType.COMPANY));
		}
	}
	
	private void loadJuryInfo() {
		InternshipJury jury = new InternshipJury();

		if((this.internship.getIdInternship() != 0) && (this.internship.getType() == InternshipType.REQUIRED)) {
			try {
				jury = new InternshipJuryBO().findByInternship(this.internship.getIdInternship());
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		if(jury.getIdInternshipJury() != 0) {
			try {
				this.textJuryDate.setValue(DateUtils.convertToLocalDateTime(jury.getDate()));
				this.textJuryLocal.setValue(jury.getLocal());
				this.textJurySei.setValue(jury.getSei());

				if(!this.config.isUseSei() && jury.getSei().trim().isEmpty()) {
					this.textJurySei.setVisible(false);
				}
				
				this.textJuryDate.setReadOnly(true);
				this.textJuryLocal.setReadOnly(true);
				this.textJurySei.setReadOnly(true);
				
				InternshipJuryFormReport report = new InternshipJuryBO().getJuryFormReport(jury.getIdInternshipJury());
				
				Grid<JuryGradeDataSource> gridGeneral = new Grid<JuryGradeDataSource>();
				gridGeneral.setWidth("100%");
				gridGeneral.setHeight("245px");
				gridGeneral.addColumn(JuryGradeDataSource::getDescription).setHeader("").setFlexGrow(0).setWidth("150px");
				gridGeneral.addColumn(JuryGradeDataSource::getAppraiser).setHeader("Avaliador");
				gridGeneral.addColumn(JuryGradeDataSource::getPonderosity).setHeader("Peso").setFlexGrow(0).setWidth("100px");
				gridGeneral.addColumn(JuryGradeDataSource::getTotal).setHeader("Nota").setFlexGrow(0).setWidth("100px");
				
				gridGeneral.setItems(JuryGradeDataSource.load(report));
				
				this.layoutGrades.removeAll();
				this.layoutGrades.add(gridGeneral);
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.tabJury.setVisible(false);
				this.buttonJury.setVisible(false);
			}
		} else {
			this.tabJury.setVisible(false);
			this.buttonJury.setVisible(false);
		}
	}
	
	private void viewJury() {
		try {
			InternshipJury jury = new InternshipJuryBO().findByInternship(this.internship.getIdInternship());
			
			EditInternshipJuryWindow window = new EditInternshipJuryWindow(jury, null);
			window.open();
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);

			this.showErrorNotification("Banca de Estágio", e.getMessage());
		}
	}
	
	private void downloadInternshipPlan() {
		try {
        	this.showReport(this.internship.getInternshipPlan());
    	} catch (Exception e) {
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Plano de Estágio", e.getMessage());
		}
	}
	
	private void downloadFinalReport() {
		try {
        	this.showReport(this.internship.getFinalReport());
    	} catch (Exception e) {
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Relatório Final", e.getMessage());
		}
	}
	
	private void addReport(ReportType type) {
		EditInternshipReportWindow window = new EditInternshipReportWindow(this, this.internship, type);
		window.open();
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
			ValidateInternshipReportWindow window = new ValidateInternshipReportWindow(this.internship.getReports().get(index), this);
			window.open();
		}
	}
	
	private int getStudentReportSelectedIndex(){
		return getReportSelectedIndex(this.gridStudentReport.asSingleSelect().getValue(), ReportType.STUDENT);
    }
	
	private int getSupervisorReportSelectedIndex(){
		return getReportSelectedIndex(this.gridSupervisorReport.asSingleSelect().getValue(), ReportType.SUPERVISOR);
    }
	
	private int getCompanySupervisorReportSelectedIndex(){
    	return getReportSelectedIndex(this.gridCompanySupervisorReport.asSingleSelect().getValue(), ReportType.COMPANY);
    }
	
	private int getReportSelectedIndex(InternshipReportDataSource rep, ReportType type){
    	if(rep == null){
    		return -1;
    	}else{
    		for(int i = 0; i < this.internship.getReports().size(); i++){
    			if((this.internship.getReports().get(i).getType() == rep.getType()) && (this.internship.getReports().get(i).getIdInternshipReport() == rep.getId()) && (Arrays.equals(this.internship.getReports().get(i).getReport(), rep.getFile()))) {
    				return i;
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
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Confirma a Exclusão?")
		    	.withMessage("Confirma a exclusão do relatório?")
		    	.withOkButton(() -> {
		    		this.internship.getReports().remove(index);
                	loadReports();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
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
	        	Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
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
			this.internship.setStartDate(DateUtils.convertToDate(this.startDate.getValue()));
			this.internship.setEndDate(DateUtils.convertToDate(this.endDate.getValue()));
			this.internship.setTerminationDate(DateUtils.convertToDate(this.terminationDate.getValue()));
			this.internship.setTerm(this.textTerm.getValue());
			this.internship.setTotalHours(this.textTotalHours.getValue());
			this.internship.setWeekHours(this.textWeekHours.getValue());
			this.internship.setWeekDays(Integer.parseInt(this.textWeekDays.getValue()));
			this.internship.setComments(this.textComments.getValue());
			this.internship.setReportTitle(this.textReportTitle.getValue());
			this.internship.setSei(this.textSei.getValue());
			this.internship.setTags(this.tags.getValue());
			
			bo.save(Session.getIdUserLog(), this.internship);
			
			this.showSuccessNotification("Salvar Estágio", "Estágio salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Estágio", e.getMessage());
		}
	}

}
