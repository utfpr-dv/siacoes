package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

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
	
	public EditThesisWindow(Thesis t, ListView parentView){
		super("Editar Monografia", parentView);
		
		if(t == null){
			this.thesis = new Thesis();
		}else{
			this.thesis = t;
		}
		
		this.tabData = new TabSheet();
		this.tabData.setWidth("810px");
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("400px");
		this.textTitle.setMaxLength(255);
		
		this.textSubarea = new TextField("Subárea");
		this.textSubarea.setWidth("400px");
		this.textSubarea.setMaxLength(255);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setEnabled(false);
		this.textStudent.setWidth("800px");
		
		this.comboSupervisor = new SupervisorComboBox("Orientador", Session.getUser().getDepartment().getIdDepartment(), new SigetConfigBO().getSupervisorFilter(Session.getUser().getDepartment().getIdDepartment()));
		this.comboSupervisor.setEnabled(false);
		
		this.comboCosupervisor = new SupervisorComboBox("Coorientador", Session.getUser().getDepartment().getIdDepartment(), new SigetConfigBO().getCosupervisorFilter(Session.getUser().getDepartment().getIdDepartment()));
		this.comboCosupervisor.setNullSelectionAllowed(true);
		this.comboCosupervisor.setEnabled(false);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		this.uploadFile = new FileUploader("(Formato PDF, Tam. Máx. 5 MB)");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(6 * 1024 * 1024);
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					thesis.setFile(uploadFile.getUploadedFile());
					thesis.setFileType(uploadFile.getFileType());
				}
				
				buttonDownloadFile.setVisible(true);
			}
		});
		
		this.buttonDownloadFile = new Button("Download da Monografia", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
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
				Semester semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getThesisDeadline())){
					this.uploadFile.setEnabled(false);
					this.setSaveButtonEnabled(false);
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.uploadFile.setEnabled(false);
				this.setSaveButtonEnabled(false);
				Notification.show("Submeter Monografia", "Não foi possível determinar a data limite para entrega das monografias.", Notification.Type.ERROR_MESSAGE);
			}
		}
		
		this.loadThesis();
		this.textTitle.focus();
		
		this.setSaveButtonEnabled(this.thesis.getStudent().getIdUser() == Session.getUser().getIdUser());
		this.uploadFile.setEnabled(this.thesis.getStudent().getIdUser() == Session.getUser().getIdUser());
		
		this.addButton(this.buttonDownloadFile);
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
				Semester semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProjectDeadline())){
					Notification.show("Submeter Monografia", "O prazo para a submissão de monografias já foi encerrado.", Notification.Type.ERROR_MESSAGE);
					return;
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				Notification.show("Submeter Monografia", "Não foi possível determinar a data limite para entrega das monografias.", Notification.Type.ERROR_MESSAGE);
				return;
			}
		}
		
		try{
			ThesisBO bo = new ThesisBO();
			
			if(Session.isUserStudent()){
				this.thesis.setSubmissionDate(DateUtils.getToday().getTime());
				
				if(this.uploadFile.getUploadedFile() != null) {
					this.thesis.setFile(this.uploadFile.getUploadedFile());
					this.thesis.setFileType(this.uploadFile.getFileType());
				}
			}
			
			this.thesis.setTitle(this.textTitle.getValue());
			this.thesis.setSubarea(this.textSubarea.getValue());
			this.thesis.setAbstract(this.textAbstract.getValue());
			
			bo.save(this.thesis);
			
			Notification.show("Salvar Monografia", "Monografia salva com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Monografia", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void downloadFile() {
		try {
        	this.showReport(this.thesis.getFile());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	Notification.show("Download do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
}
