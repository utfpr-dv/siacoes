package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditThesisWindow extends EditWindow {

	private final Thesis thesis;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final TextField textSubarea;
	private final TextField textStudent;
	private final SupervisorComboBox comboSupervisor;
	private final SupervisorComboBox comboCosupervisor;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final FileUploader uploadFile;
	private final Button buttonDownloadFile;
	private final TextArea textAbstract;
	private final TabSheet tabData;
	
	private SigetConfig config;
	
	public EditThesisWindow(Thesis t, ListView parentView){
		super("Editar Monografia", parentView);
		
		if(t == null){
			this.thesis = new Thesis();
		}else{
			this.thesis = t;
		}
		
		try {
			this.config = new SigetConfigBO().findByDepartment(new ThesisBO().findIdDepartment(this.thesis.getIdThesis()));
		} catch (Exception e1) {
			this.config = new SigetConfig();
		}
		
		this.tabData = new TabSheet();
		this.tabData.setWidth("810px");
		this.tabData.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabData.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabData.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("400px");
		this.textTitle.setMaxLength(255);
		this.textTitle.setRequired(true);
		
		this.textSubarea = new TextField("Subárea");
		this.textSubarea.setWidth("400px");
		this.textSubarea.setMaxLength(255);
		this.textSubarea.setRequired(true);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setEnabled(false);
		this.textStudent.setWidth("800px");
		this.textStudent.setRequired(true);
		
		this.comboSupervisor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigetConfigBO().getSupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboSupervisor.setEnabled(false);
		this.comboSupervisor.setRequired(true);
		
		this.comboCosupervisor = new SupervisorComboBox("Coorientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigetConfigBO().getCosupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboCosupervisor.setNullSelectionAllowed(true);
		this.comboCosupervisor.setEnabled(false);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		this.comboSemester.setRequired(true);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		this.textYear.setRequired(true);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setRequired(true);
		
		this.uploadFile = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					thesis.setFile(uploadFile.getUploadedFile());
					
					buttonDownloadFile.setVisible(true);
				}
			}
		});
		
		this.buttonDownloadFile = new Button("Download da Monografia", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownloadFile.setVisible(false);
		
		VerticalLayout v1 = new VerticalLayout();
		v1.setSpacing(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textTitle, this.textSubarea);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboSupervisor, this.comboCosupervisor);
		h3.setSpacing(true);
		
		HorizontalLayout h4 = new HorizontalLayout(this.uploadFile, this.comboSemester, this.textYear, this.textSubmissionDate);
		h4.setSpacing(true);
		
		v1.addComponent(h1);
		v1.addComponent(this.textStudent);
		v1.addComponent(h2);
		v1.addComponent(h3);
		v1.addComponent(h4);
		
		this.tabData.addTab(v1, "Dados da Monografia");
		
		this.textAbstract = new TextArea();
		this.textAbstract.setWidth("800px");
		this.textAbstract.setHeight("300px");
		this.textAbstract.addStyleName("textscroll");
		
		this.tabData.addTab(this.textAbstract, "Resumo");
		
		this.addField(this.tabData);
		
		if(Session.isUserStudent()){
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getThesisDeadline())){
					this.uploadFile.setEnabled(false);
					this.setSaveButtonEnabled(false);
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.uploadFile.setEnabled(false);
				this.setSaveButtonEnabled(false);
				this.showErrorNotification("Submeter Monografia", "Não foi possível determinar a data limite para entrega das monografias.");
			}
		}
		
		this.loadThesis();
		this.textTitle.focus();
		
		this.setSaveButtonEnabled(this.thesis.getStudent().getIdUser() == Session.getUser().getIdUser());
		this.uploadFile.setEnabled(this.thesis.getStudent().getIdUser() == Session.getUser().getIdUser());
		
		this.addButton(this.buttonDownloadFile);
		this.buttonDownloadFile.setWidth("250px");
	}
	
	private void loadThesis(){
		try{
			ProposalBO pbo = new ProposalBO();
			Proposal proposal = pbo.findByProject(this.thesis.getProject().getIdProject());
			
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(proposal.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(proposal.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textStudent.setValue(this.thesis.getStudent().getName());
		this.textTitle.setValue(this.thesis.getTitle());
		this.textSubarea.setValue(this.thesis.getSubarea());
		this.comboSemester.setSemester(this.thesis.getSemester());
		this.textYear.setYear(this.thesis.getYear());
		this.textSubmissionDate.setValue(this.thesis.getSubmissionDate());
		this.comboSupervisor.setProfessor(this.thesis.getSupervisor());
		this.comboCosupervisor.setProfessor(this.thesis.getCosupervisor());
		this.textAbstract.setValue(this.thesis.getAbstract());
		
		if(this.thesis.getIdThesis() == 0){
			try{
				ProjectBO pbo = new ProjectBO();
				Project project = pbo.findById(this.thesis.getProject().getIdProject());
				
				SupervisorChangeBO bo = new SupervisorChangeBO();
				
				this.comboSupervisor.setProfessor(bo.findCurrentSupervisor(project.getProposal().getIdProposal()));
				this.comboCosupervisor.setProfessor(bo.findCurrentCosupervisor(project.getProposal().getIdProposal()));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}else if(this.thesis.getFile() != null){
			this.buttonDownloadFile.setVisible(true);
		}
	}
	
	@Override
	public void save() {
		if(Session.isUserStudent()){
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProjectDeadline())){
					this.showErrorNotification("Submeter Monografia", "O prazo para a submissão de monografias já foi encerrado.");
					return;
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.showErrorNotification("Submeter Monografia", "Não foi possível determinar a data limite para entrega das monografias.");
				return;
			}
		}
		
		try{
			ThesisBO bo = new ThesisBO();
			
			if(Session.isUserStudent()){
				this.thesis.setSubmissionDate(DateUtils.getToday().getTime());
				
				if(this.uploadFile.getUploadedFile() != null) {
					this.thesis.setFile(this.uploadFile.getUploadedFile());
				}
			}
			
			this.thesis.setTitle(this.textTitle.getValue());
			this.thesis.setSubarea(this.textSubarea.getValue());
			this.thesis.setAbstract(this.textAbstract.getValue());
			
			bo.save(Session.getIdUserLog(), this.thesis);
			
			this.showSuccessNotification("Salvar Monografia", "Monografia salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Monografia", e.getMessage());
		}
	}
	
	private void downloadFile() {
		try {
        	this.showReport(this.thesis.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}
	
}
