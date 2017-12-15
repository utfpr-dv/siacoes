package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.ProfessorComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
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
	
	public EditThesisWindow(Thesis thesis, ListView parentView){
		super("Editar Monografia", parentView);
		
		if(thesis == null){
			this.thesis = new Thesis();
		}else{
			this.thesis = thesis;
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
		
		HorizontalLayout h4 = new HorizontalLayout(this.uploadFile, this.imageFileUploaded, this.comboSemester, this.textYear, this.textSubmissionDate);
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
		
		this.tabData.addTab(this.textAbstract, "Resumo");
		
		this.addField(this.tabData);
		
		if(Session.isUserStudent()){
			try {
				DeadlineBO dbo = new DeadlineBO();
				Semester semester = new SemesterBO().findByDate(Session.getUser().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
				Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getThesisDeadline())){
					this.setSaveButtonEnabled(false);	
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.setSaveButtonEnabled(false);
				Notification.show("Submeter Monografia", "Não foi possível determinar a data limite para entrega das monografias.", Notification.Type.ERROR_MESSAGE);
			}
		}
		
		this.loadThesis();
		this.textTitle.focus();
		
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
		}else{
			if(this.thesis.getFile() != null){
				this.buttonDownloadFile.setVisible(true);
			}
			
			try {
				this.loadGrades();
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				Notification.show("Carregar Notas", "Não foi possível carregar as notas atribuídas pela banca.", Notification.Type.ERROR_MESSAGE);
			}
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
	
	private void loadGrades() throws Exception {
		if(this.thesis.getIdThesis() != 0) {
			SigetConfig config = new SigetConfig();
			
			if(Session.isUserStudent()) {
				ProposalBO pbo = new ProposalBO();
				Proposal proposal = pbo.findByProject(this.thesis.getProject().getIdProject());
				
				SigetConfigBO sbo = new SigetConfigBO();
				config = sbo.findByDepartment(proposal.getDepartment().getIdDepartment());
			}
			
			if(config.isShowGradesToStudent() || Session.isUserProfessor()) {
				JuryBO bo = new JuryBO();
				Jury jury = bo.findByThesis(this.thesis.getIdThesis());
				
				if((jury.getIdJury() != 0) && (bo.hasScores(jury.getIdJury()))) {
					JuryFormReport report = bo.getFormReport(jury.getIdJury());
					
					TabSheet tab = new TabSheet();
					tab.setSizeFull();
					
					Grid gridGeneral = new Grid();
					gridGeneral.setWidth("100%");
					gridGeneral.setHeight("150px");
					gridGeneral.addColumn("", String.class);
					gridGeneral.addColumn("Avaliador", String.class);
					gridGeneral.addColumn("Escrita", Double.class);
					gridGeneral.addColumn("Apresentação", Double.class);
					gridGeneral.addColumn("Arguição", Double.class);
					gridGeneral.addColumn("Total", Double.class);
					
					for(JuryFormAppraiserScoreReport appraiser : report.getScores()) {
						gridGeneral.addRow(appraiser.getDescription(), appraiser.getName(), appraiser.getScoreWriting(), appraiser.getScoreOral(), appraiser.getScoreArgumentation(), (appraiser.getScoreWriting() + appraiser.getScoreOral() + appraiser.getScoreArgumentation()));
					}
					
					TextField textScore = new TextField();
					textScore.setCaption(null);
					textScore.setEnabled(false);
					textScore.setWidth("100px");
					textScore.setValue(String.format("%.2f", report.getScore()));
					
					Label labelScore = new Label("Média Final:");
					
					HorizontalLayout layoutScore = new HorizontalLayout(labelScore, textScore);
					layoutScore.setComponentAlignment(labelScore, Alignment.MIDDLE_RIGHT);
					layoutScore.setSpacing(true);
					
					TextArea textComments = new TextArea("Comentários");
					textComments.setWidth("100%");
					textComments.setHeight("75px");
					textComments.setEnabled(false);
					textComments.setValue(report.getComments());
					
					VerticalLayout tab1 = new VerticalLayout(gridGeneral, layoutScore, textComments);
					tab1.setComponentAlignment(layoutScore, Alignment.MIDDLE_RIGHT);
					tab1.setSpacing(true);
					
					tab.addTab(tab1, "Geral");
					
					for(JuryFormAppraiserReport appraiser : report.getAppraisers()) {
						TextField textAppraiser = new TextField("Avaliador:");
						textAppraiser.setWidth("100%");
						textAppraiser.setEnabled(false);
						textAppraiser.setValue(appraiser.getName());
						
						Grid gridScores = new Grid();
						gridScores.setWidth("100%");
						gridScores.setHeight("150px");
						gridScores.addColumn("Quesito", String.class);
						gridScores.addColumn("Peso", Double.class);
						gridScores.addColumn("Nota", Double.class);
						
						for(JuryFormAppraiserDetailReport scores : appraiser.getDetail()) {
							gridScores.addRow(scores.getEvaluationItem(), scores.getPonderosity(), scores.getScore());
						}
						
						TextArea textAppraiserComments = new TextArea("Comentários");
						textAppraiserComments.setWidth("100%");
						textAppraiserComments.setHeight("50px");
						textAppraiserComments.setEnabled(false);
						textAppraiserComments.setValue(appraiser.getComments());
						
						VerticalLayout tabAppraiser = new VerticalLayout(textAppraiser, gridScores, textAppraiserComments);
						tabAppraiser.setSpacing(true);
						
						tab.addTab(tabAppraiser, appraiser.getDescription());
					}
					
					this.tabData.addTab(tab, "Avaliação");
				}
			}
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
				
				thesis.setFileType(DocumentType.fromMimeType(mimeType));
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
	            
	            thesis.setFile(buffer);
	            
	            imageFileUploaded.setVisible(true);
	            buttonDownloadFile.setVisible(true);
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
