package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.ThesisDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditThesisWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Monografias de TCC 2")
@Route(value = "thesis", layout = MainLayout.class)
public class ThesisView extends ListView<ThesisDataSource> {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonJury;
	private final Button buttonDownloadThesis;
	private final Button buttonStatements;
	private final Button buttonSendFeedback;
	private final Button buttonSupervisorStatement;
	private final Button buttonCosupervisorStatement;
	private final Button buttonGrades;
	
	public ThesisView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(ThesisDataSource::getSemester).setHeader("Semestre").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ThesisDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ThesisDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(ThesisDataSource::getSupervisor, "Supervisor").setHeader("Orientador");
		this.getGrid().addColumn(ThesisDataSource::getTitle, "Title").setHeader("Título");
		this.getGrid().addColumn(new LocalDateRenderer<>(ThesisDataSource::getSubmisstion, "dd/MM/yyyy"), "Submission").setHeader("Submissão").setFlexGrow(0).setWidth("125px");
		
		this.buttonDownloadThesis = new Button("Monografia", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFile();
        });
		this.addActionButton(this.buttonDownloadThesis);
		
		this.buttonJury = new Button("Banca", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
            juryClick();
        });
		
		this.buttonSendFeedback = new Button("Feedback", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            sendFeedbackClick();
        });
		
		this.buttonStatements = new Button("Declarações", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadStatements();
        });
		
		this.buttonSupervisorStatement = new Button("Dec. Orientação", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadSupervisorStatement();
        });
		
		this.buttonCosupervisorStatement = new Button("Dec. Coorientação", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadCosupervisorStatement();
        });
		
		this.buttonGrades = new Button("Relat. de Notas", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            juryGradesReport();
        });
		
		if(Session.isUserProfessor()){
			this.addActionButton(this.buttonSendFeedback);
		}
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonJury);	
			this.addActionButton(this.buttonStatements);
			this.addActionButton(this.buttonSupervisorStatement);
			this.addActionButton(this.buttonCosupervisorStatement);
			this.addActionButton(this.buttonGrades);
		}
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(new Icon(VaadinIcon.SEARCH));
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setValue(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}
	
	@Override
	protected void loadGrid() {
		try {
			ThesisBO bo = new ThesisBO();
	    	List<Thesis> list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	this.getGrid().setItems(ThesisDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Monografias", e.getMessage());
		}
	}
	
	private void downloadSupervisorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				ThesisBO tbo = new ThesisBO();
				CertificateBO bo = new CertificateBO();
				
				Thesis thesis = tbo.findById((int)value);

				byte[] report = bo.getThesisProfessorStatement(thesis.getSupervisor(), thesis);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadCosupervisorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				ThesisBO tbo = new ThesisBO();
				CertificateBO bo = new CertificateBO();
				
				Thesis thesis = tbo.findById((int)value);

				if(thesis.getCosupervisor() == null){
					this.showWarningNotification("Gerar Declaração", "Não foi indicado um coorientador para a monografia.");
				}else{
					byte[] report = bo.getThesisProfessorStatement(thesis.getCosupervisor(), thesis);
					
					this.showReport(report);
				}
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadStatements(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declarações", "Selecione uma banca para gerar as declarações.");
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
					this.showWarningNotification("Gerar Declarações", "Não há declarações para serem geradas.");
				}
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declarações", e.getMessage());
			}
		}
	}
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Download da Monografia", "Selecione um registro para baixar a monografia.");
		}else{
			try{
				ThesisBO bo = new ThesisBO();
            	Thesis p = bo.findById((int)value);
				
				this.showReport(p.getFile());
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Download da Monografia", e.getMessage());
			}
		}
	}
	
	private void juryClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para marcar a banca.");
    	}else{
    		try {
    			JuryBO bo = new JuryBO();
				Jury jury = bo.findByThesis((int)id);
				
				EditJuryWindow window = new EditJuryWindow(jury, this);
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Marcar Banca", e.getMessage());
			}
    	}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Enviar Feedback", "Selecione o registro para enviar o feedback.");
    	}else{
    		try {
    			JuryBO bo = new JuryBO();
				Jury jury = bo.findByThesis((int)id);
				
				JuryAppraiserBO bo2 = new JuryAppraiserBO();
    			
    			JuryAppraiser appraiser = bo2.findByAppraiser(jury.getIdJury(), Session.getUser().getIdUser());
    			
    			if(appraiser != null){
    				EditJuryAppraiserFeedbackWindow window = new EditJuryAppraiserFeedbackWindow(appraiser);
    				window.open();
    			}else{
    				this.showWarningNotification("Enviar Feedback", "É necessário ser membro da banca para enviar o feedback.");
    			}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Enviar Feedback", e.getMessage());
			}
    	}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), 2, true));
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Relatório de Notas", e.getMessage());
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			ThesisBO bo = new ThesisBO();
			Thesis p = bo.findById((int)id);
			
			EditThesisWindow window = new EditThesisWindow(p, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Monografia", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
