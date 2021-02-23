package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipJuryDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryAppraiserScoreWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryAppraiserSingleScoreWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJurySupervisorScoreWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.InternshipJuryAppraiserChangeWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.InternshipJuryGradesWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Agenda de Bancas de Estágio")
@Route(value = "internshipjury", layout = MainLayout.class)
public class InternshipJuryView extends ListView<InternshipJuryDataSource> implements HasUrlParameter<String> {

	public static final String NAME = "internshipjury";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonFile;
	private final Button buttonForm;
	private final Button buttonSchedule;
	private final Button buttonStatements;
	private final Button buttonSingleStatement;
	private final Button buttonSendFeedback;
	private final Button buttonParticipants;
	private final Button buttonParticipantsReport;
	private final Button buttonGrades;
	private final Button buttonChangeAppraiser;
	private final Button buttonFillGrades;
	private final Button buttonSign;
	
	private boolean listAll = false;
	private SigesConfig config;
	
	public InternshipJuryView(){
		super(SystemModule.SIGES);
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		try {
			this.config = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigesConfig();
		}
		
		this.getGrid().addColumn(new LocalDateTimeRenderer<>(InternshipJuryDataSource::getDate, "dd/MM/yyyy HH:mm"), "Date").setHeader("Data e Hora").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(InternshipJuryDataSource::getLocal).setHeader("Local").setFlexGrow(0).setWidth("200px");
		this.getGrid().addColumn(InternshipJuryDataSource::getMember).setHeader("Membro").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(InternshipJuryDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(InternshipJuryDataSource::getCompany, "Company").setHeader("Empresa");
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setValue(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonFile = new Button("Down. do Relatório", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFile();
        });
		
		this.buttonForm = new Button("Ficha de Avaliação", new Icon(VaadinIcon.CLIPBOARD), event -> {
            downloadForm();
        });
		
		this.buttonParticipants = new Button("Lista de Presença", new Icon(VaadinIcon.LINES_LIST), event -> {
            downloadParticipants();
        });
		
		this.buttonParticipantsReport = new Button("Lista de Ouvintes", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadParticipantsReport();
        });
		
		this.buttonSendFeedback = new Button("Feedback", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            sendFeedbackClick();
        });
		
		this.buttonStatements = new Button("Declarações", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadStatements();
        });
		
		this.buttonSingleStatement = new Button("Declaração", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadSingleStatement();
        });
		
		this.buttonSchedule = new Button("Imprimir", new Icon(VaadinIcon.PRINT), event -> {
            printSchedule();
        });
		
		this.buttonGrades = new Button("Relat. de Notas", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            juryGradesReport();
        });
		
		this.buttonChangeAppraiser = new Button("Alterar Membros", new Icon(VaadinIcon.USERS), event -> {
            changeAppraiser();
        });
		
		this.buttonFillGrades = new Button("Lançar Notas", new Icon(VaadinIcon.CALC_BOOK), event -> {
            addScore();
        });
		this.buttonFillGrades.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.buttonSign = new Button("Assinar", new Icon(VaadinIcon.PENCIL), event -> {
            sign();
        });
		this.buttonSign.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		this.addActionButton(this.buttonSchedule);
		this.addActionButton(this.buttonChangeAppraiser);
		this.addActionButton(this.buttonFile);
		this.addActionButton(this.buttonForm);
		this.addActionButton(this.buttonFillGrades);
		this.addActionButton(this.buttonSign);
		this.addActionButton(this.buttonParticipants);
		this.addActionButton(this.buttonParticipantsReport);
		this.addActionButton(this.buttonSendFeedback);
		this.addActionButton(this.buttonSingleStatement);
		
		if(Session.isUserManager(this.getModule())){
			this.addActionButton(this.buttonStatements);
			this.addActionButton(this.buttonGrades);
			this.setEditVisible(true);
			this.setEditCaption("Banca");
			this.setEditIcon(new Icon(VaadinIcon.CALENDAR_CLOCK));
		}
	}
	
	private void configureButtons(){
		if(this.listAll){
			this.buttonSendFeedback.setVisible(false);
			this.buttonChangeAppraiser.setVisible(false);
			this.buttonForm.setVisible(Session.isUserManager(this.getModule()));
			this.buttonParticipants.setVisible(Session.isUserManager(this.getModule()));
			this.buttonParticipantsReport.setVisible(Session.isUserManager(this.getModule()));
			this.buttonSingleStatement.setVisible(false);
			this.buttonFile.setVisible(Session.isUserManager(this.getModule()));
			this.buttonFillGrades.setVisible(false);
			this.buttonSign.setVisible(false);
		}else{
			this.buttonSendFeedback.setVisible(true);
			this.buttonChangeAppraiser.setVisible(Session.isUserProfessor());
			this.buttonFile.setVisible(Session.isUserProfessor());
			this.buttonForm.setVisible(Session.isUserProfessor());
			this.buttonParticipants.setVisible(Session.isUserProfessor());
			this.buttonParticipantsReport.setVisible(Session.isUserProfessor());
			this.buttonSendFeedback.setVisible(Session.isUserProfessor());
			this.buttonStatements.setVisible(false);
			this.buttonSchedule.setVisible(Session.isUserProfessor() || Session.isUserSupervisor());
			this.buttonFillGrades.setVisible(Session.isUserSupervisor() && this.config.isAppraiserFillsGrades());
			this.buttonSign.setVisible(Session.isUserProfessor() && this.config.isUseDigitalSignature());
		}
	}
	
	@Override
	protected void loadGrid() {
		if(Session.isUserStudent() || this.listAll) {
			this.getGrid().getColumns().get(2).setVisible(false);
		}
		
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			List<InternshipJury> list;
			
			if(this.listAll){
				list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
			}else{
				if(Session.isUserProfessor() || Session.isUserSupervisor()){
					list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}else{
					list = bo.listByStudent(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
			List<InternshipJuryDataSource> data = InternshipJuryDataSource.load(list);
			
			if((Session.isUserProfessor() || Session.isUserSupervisor()) && !this.listAll) {
				for(InternshipJuryDataSource item : data) {
					InternshipJuryAppraiser appraiser = new InternshipJuryAppraiserBO().findByAppraiser(item.getId(), Session.getUser().getIdUser());
					item.setMember((appraiser.isSubstitute() ? "Suplente" : (appraiser.isChair() ? "Presidente" : "Titular")));
				}
			}
	    	
	    	this.getGrid().setItems(data);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Bancas", e.getMessage());
		}
	}
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Download do Trabalho", "Selecione uma banca para fazer o download do trabalho.");
		}else{
			try {
				InternshipJuryBO bo = new InternshipJuryBO();
				InternshipJury jury = bo.findById((int)value);
				
				InternshipBO ibo = new InternshipBO();
				Internship internship = ibo.findById(jury.getInternship().getIdInternship());
				
				if(internship.getFinalReport() == null){
					this.showWarningNotification("Download do Trabalho", "O relatório final do estágio não foi anexado ao cadastro.");
				}else{
					this.showReport(internship.getFinalReport());
				}
        	} catch (Exception e) {
        		this.showErrorNotification("Download do Trabalho", e.getMessage());
			}
		}
	}
	
	private void downloadForm(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Ficha de Avaliação", "Selecione uma banca para gerar a ficha de avaliação.");
		}else{
			try{
				this.showReport(new InternshipJuryBO().getJuryForm((int)value));
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Imprimir Ficha de Avaliação", e.getMessage());
			}
		}
	}
	
	private void downloadParticipants(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Lista de Presença", "Selecione uma banca para gerar a lista de presença.");
		}else{
			try{
				InternshipJuryBO bo = new InternshipJuryBO();
				
				this.showReport(bo.getJuryParticipantsSignature((int)value));
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Imprimir Lista de Presença", e.getMessage());
			}
		}
	}
	
	private void downloadParticipantsReport(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Lista de Acadêmicos Ouvintes", "Selecione uma banca para gerar a lista de acadêmicos ouvintes.");
		}else{
			try{
				InternshipJuryBO bo = new InternshipJuryBO();
				
				this.showReport(bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (int)value, null, null, true));
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Imprimir Lista de Acadêmicos Ouvintes", e.getMessage());
			}
		}
	}
	
	private void sendFeedbackClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Enviar Feedback", "Selecione o registro para enviar o feedback.");
    	}else{
    		try {
    			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
    			
    			InternshipJuryAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
    			
    			if(appraiser != null){
    				EditInternshipJuryAppraiserFeedbackWindow window = new EditInternshipJuryAppraiserFeedbackWindow(appraiser);
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
	
	private void downloadSingleStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione uma banca para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report;
				
				if(Session.isUserProfessor()){
					InternshipJuryAppraiserBO jbo = new InternshipJuryAppraiserBO();
					InternshipJuryAppraiser appraiser = jbo.findByAppraiser((int)value, Session.getUser().getIdUser());
					
					report = bo.getInternshipJuryProfessorStatement(appraiser);
				}else{
					InternshipJuryStudentBO jbo = new InternshipJuryStudentBO();
					InternshipJuryStudent student = jbo.findByStudent((int)value, Session.getUser().getIdUser());
					
					report = bo.getInternshipJuryStudentStatement(student);
				}
				
				if(report != null){
					this.showReport(report);
				}else{
					this.showWarningNotification("Gerar Declaração", "Não foi encontrada a declaração para imprimir.");
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

				byte[] reportProfessor = bo.getInternshipJuryProfessorStatementReportList((int)value);
				
				if(reportProfessor != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportProfessor));
				}
				
				byte[] reportStudent = bo.getInternshipJuryStudentStatementReportList((int)value);
				
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
	
	private void printSchedule() {
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			byte[] report = null;
			
			if(this.listAll) {
				report = bo.getScheduleReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear());
			} else if(Session.isUserProfessor() || Session.isUserSupervisor()){
				report = bo.getScheduleReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
			}
			
			this.showReport(report);
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Imprimir Agenda de Bancas", e.getMessage());
		}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new InternshipJuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), false));
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Relatório de Notas", e.getMessage());
		}
	}
	
	private void changeAppraiser() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Alterar Membros", "Selecione uma banca para alterar a sua composição.");
		} else {
			try {
				if(Session.getUser().getIdUser() == new InternshipJuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser()) {
					InternshipJuryAppraiserChangeWindow window = new InternshipJuryAppraiserChangeWindow(new InternshipJuryBO().findById((int)value));
					window.open();
				} else {
					this.showWarningNotification("Alterar Membros", "Apenas o presidente da banca pode mudar sua composição.");
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Alterar Membros", e.getMessage());
			}
		}
	}
	
	private void sign() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Assinar Ficha", "Selecione uma banca para assinar a ficha de avaliação.");
		} else {
			try {
				if(Session.getUser().getIdUser() == new InternshipJuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser()) {
					InternshipJuryGradesWindow window = new InternshipJuryGradesWindow(new InternshipJuryBO().findById((int)value));
					window.open();
				} else {
					this.showWarningNotification("Assinar Ficha", "Apenas o presidente da banca pode efetuar a assinatura da ficha de avaliação.");
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Assinar Ficha", e.getMessage());
			}
		}
	}
	
	private void addScore() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Lançar Notas", "Selecione uma banca para lançar as notas.");
		} else {
			try {
				InternshipJury jury = new InternshipJuryBO().findById((int)value);
				
				if(!jury.isSupervisorFillJuryForm() && (Session.getUser().getIdUser() == new InternshipJuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser())) {
					EditInternshipJurySupervisorScoreWindow window = new EditInternshipJurySupervisorScoreWindow(jury);
					window.open();
				} else {
					InternshipJuryAppraiser appraiser = new InternshipJuryAppraiserBO().findByAppraiser((int)value, Session.getUser().getIdUser());
					
					if((appraiser == null) || (appraiser.getIdInternshipJuryAppraiser() == 0)) {
						this.showWarningNotification("Lançar Notas", "Não é possível lançar as notas pois você não faz parte dessa banca.");
					} else if(appraiser.isSubstitute()) {
						this.showWarningNotification("Lançar Notas", "Apenas membros titulares da banca podem lançar notas.");
					} else {
						if(jury.isUseEvaluationItems()) {
							EditInternshipJuryAppraiserScoreWindow window = new EditInternshipJuryAppraiserScoreWindow(appraiser);
							window.open();
						} else {
							EditInternshipJuryAppraiserSingleScoreWindow window = new EditInternshipJuryAppraiserSingleScoreWindow(appraiser);
							window.open();
						}
					}
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Lançar Notas", e.getMessage());
			}
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			InternshipJury jury = bo.findById((int)id);
			
			EditInternshipJuryWindow window = new EditInternshipJuryWindow(jury, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Marcar Banca", e.getMessage());
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
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(this.listAll)
			return;
		
		if((parameter != null) && !parameter.isEmpty()) {
			try{
				int i = Integer.parseInt(parameter.trim());
				
				this.listAll = (i == 1);
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.listAll = false;
			}
		}
		
		this.configureButtons();
	}

}
