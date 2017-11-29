package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.ProfessorComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditProjectWindow extends EditWindow {

	private final Project project;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final TextField textSubarea;
	private final TextField textStudent;
	private final ProfessorComboBox comboSupervisor;
	private final ProfessorComboBox comboCosupervisor;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final Upload uploadFile;
	private final Image imageFileUploaded;
	private final Button buttonDownloadFile;
	private final TextArea textAbstract;
	private final TabSheet tabData;
	
	public EditProjectWindow(Project project, ListView parentView){
		super("Editar Projeto", parentView);
		
		if(project == null){
			this.project = new Project();
		}else{
			this.project = project;
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
		
		this.comboSupervisor = new ProfessorComboBox("Orientador");
		this.comboSupervisor.setEnabled(false);
		
		this.comboCosupervisor = new ProfessorComboBox("Co-orientador");
		this.comboCosupervisor.setNullSelectionAllowed(true);
		this.comboCosupervisor.setEnabled(false);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadFile = new Upload("(Formato PDF, Tam. Máx. 5 MB)", listener);
		this.uploadFile.addSucceededListener(listener);
		this.uploadFile.setButtonCaption("Enviar Arquivo");
		this.uploadFile.setImmediate(true);
		
		this.imageFileUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageFileUploaded.setVisible(false);
		
		this.buttonDownloadFile = new Button("Download do Projeto");
		this.buttonDownloadFile.setVisible(false);
		
		VerticalLayout v1 = new VerticalLayout();
		v1.setSpacing(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textTitle, this.textSubarea);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboSupervisor, this.comboCosupervisor);
		h3.setSpacing(true);
		
		HorizontalLayout h4 = new HorizontalLayout(this.uploadFile, this.imageFileUploaded, this.comboSemester, this.textYear, this.textSubmissionDate);
		h4.setSpacing(true);
		
		v1.addComponent(h1);
		v1.addComponent(this.textStudent);
		v1.addComponent(h2);
		v1.addComponent(h3);
		v1.addComponent(h4);
		
		this.tabData.addTab(v1, "Dados do Projeto");
		
		this.textAbstract = new TextArea();
		this.textAbstract.setWidth("800px");
		this.textAbstract.setHeight("300px");
		
		this.tabData.addTab(this.textAbstract, "Resumo");
		
		this.addField(this.tabData);
		
		if(Session.isUserStudent()){
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProjectDeadline())){
					this.setSaveButtonEnabled(false);	
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.setSaveButtonEnabled(false);
				Notification.show("Submeter Projeto", "Não foi possível determinar a data limite para entrega dos projetos.", Notification.Type.ERROR_MESSAGE);
			}
		}
		
		this.loadProject();
		this.textTitle.focus();
		
		this.addButton(this.buttonDownloadFile);
	}
	
	private void loadProject(){
		try{
			ProposalBO pbo = new ProposalBO();
			Proposal proposal = pbo.findById(this.project.getProposal().getIdProposal());
			
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(proposal.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(proposal.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textStudent.setValue(this.project.getStudent().getName());
		this.textTitle.setValue(this.project.getTitle());
		this.textSubarea.setValue(this.project.getSubarea());
		this.comboSemester.setSemester(this.project.getSemester());
		this.textYear.setYear(this.project.getYear());
		this.textSubmissionDate.setValue(this.project.getSubmissionDate());
		this.comboSupervisor.setProfessor(this.project.getSupervisor());
		this.comboCosupervisor.setProfessor(this.project.getCosupervisor());
		this.textAbstract.setValue(this.project.getAbstract());
		
		if(this.project.getIdProject() == 0){
			try{
				SupervisorChangeBO bo = new SupervisorChangeBO();
				
				this.comboSupervisor.setProfessor(bo.findCurrentSupervisor(this.project.getProposal().getIdProposal()));
				this.comboCosupervisor.setProfessor(bo.findCurrentCosupervisor(this.project.getProposal().getIdProposal()));
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}else if(this.project.getFile() != null){
			this.buttonDownloadFile.setVisible(true);
			this.prepareDownloadProject();
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
					Notification.show("Submeter Projeto", "O prazo para a submissão de projetos já foi encerrado.", Notification.Type.ERROR_MESSAGE);
					return;
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				Notification.show("Submeter Projeto", "Não foi possível determinar a data limite para entrega dos projetos.", Notification.Type.ERROR_MESSAGE);
				return;
			}
		}
		
		try{
			ProjectBO bo = new ProjectBO();
			
			if(Session.isUserStudent()){
				this.project.setSubmissionDate(DateUtils.getToday().getTime());
			}
			
			this.project.setTitle(this.textTitle.getValue());
			this.project.setSubarea(this.textSubarea.getValue());
			this.project.setAbstract(this.textAbstract.getValue());
			//this.project.setSupervisor(this.comboSupervisor.getProfessor());
			//this.project.setCosupervisor(this.comboCosupervisor.getProfessor());
			
			bo.save(this.project);
			
			Notification.show("Salvar Projeto", "Projeto salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Projeto", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownloadProject(){
		new ExtensionUtils().removeAllExtensions(this.buttonDownloadFile);
		
		if(this.project.getFile() != null){
			this.buttonDownloadFile.setVisible(true);
			
			new ExtensionUtils().extendToDownload("Projeto_TCC_" + this.project.getIdProject() + Document.DocumentType.PDF.getExtension(), this.project.getFile(), this.buttonDownloadFile);
		}else{
			this.buttonDownloadFile.setVisible(false);
		}
	}
	
	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageFileUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
				project.setFileType(DocumentType.fromMimeType(mimeType));
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
	            
	            project.setFile(buffer);
	            
	            imageFileUploaded.setVisible(true);
	            buttonDownloadFile.setVisible(true);
	            prepareDownloadProject();
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}
	
}
