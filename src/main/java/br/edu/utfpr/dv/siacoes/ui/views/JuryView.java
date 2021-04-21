package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

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
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryAppraiserFeedbackWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryAppraiserScoreWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.JuryAppraiserChangeWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.JuryGradesWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Agenda de Bancas de TCC")
@Route(value = "jury", layout = MainLayout.class)
public class JuryView extends ListView<JuryDataSource> implements HasUrlParameter<String> {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final StageComboBox comboStage;
	private final Button buttonFile;
	private final Button buttonForm;
	private final Button buttonTerm;
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
	private SigetConfig config;

	public JuryView(){
		super(SystemModule.SIGET);
		
		this.getGrid().addColumn(new LocalDateTimeRenderer<>(JuryDataSource::getDate, "dd/MM/yyyy HH:mm"), "Date").setHeader("Data e Hora").setFlexGrow(0).setWidth("175px");
		this.getGrid().addColumn(JuryDataSource::getLocal).setHeader("Local").setFlexGrow(0).setWidth("200px");
		this.getGrid().addColumn(JuryDataSource::getStage, "Stage").setHeader("TCC").setFlexGrow(0).setWidth("50px");
		this.getGrid().addColumn(JuryDataSource::getMember).setHeader("Membro").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(JuryDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(JuryDataSource::getTitle, "Title").setHeader("Título");
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			semester = new Semester();
		}
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setValue(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.comboStage = new StageComboBox(true);
		this.comboStage.selectBoth();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonFile = new Button("Down. do Trabalho", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
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
		
		this.buttonTerm = new Button("Termo de Aprovação", event -> {
            downloadTerm();
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
		this.addActionButton(this.buttonSendFeedback);
		this.addActionButton(this.buttonParticipants);
		this.addActionButton(this.buttonParticipantsReport);
		this.addActionButton(this.buttonTerm);
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
			this.buttonTerm.setVisible(Session.isUserManager(this.getModule()));
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
			this.buttonTerm.setVisible(false);
			this.buttonFile.setVisible(Session.isUserSupervisor());
			this.buttonForm.setVisible(Session.isUserProfessor());
			this.buttonParticipants.setVisible(Session.isUserProfessor());
			this.buttonParticipantsReport.setVisible(Session.isUserProfessor());
			this.buttonSendFeedback.setVisible(Session.isUserSupervisor());
			this.buttonStatements.setVisible(false);
			this.buttonSchedule.setVisible(Session.isUserSupervisor());
			this.buttonGrades.setVisible(false);
			this.comboStage.setVisible(false);
			this.buttonFillGrades.setVisible(Session.isUserSupervisor() && ((this.config.getDepartment().getIdDepartment() == 0) || this.config.isAppraiserFillsGrades()));
			this.buttonSign.setVisible(Session.isUserProfessor() && this.config.isUseDigitalSignature());
		}
	}
	
	@Override
	protected void loadGrid() {
		if(Session.isUserStudent() || this.listAll) {
			this.getGrid().getColumns().get(3).setVisible(false);
		}
		
		try {
			JuryBO bo = new JuryBO();
			List<Jury> list;
			
			if(this.listAll){
				list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage());
			}else{
				if(Session.isUserProfessor() || Session.isUserSupervisor()){
					list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}else{
					list = bo.listByStudent(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				}
			}
			
			List<JuryDataSource> data = JuryDataSource.load(list);
			
			if((Session.isUserProfessor() || Session.isUserSupervisor()) && !this.listAll) {
				for(JuryDataSource item : data) {
					JuryAppraiser appraiser = new JuryAppraiserBO().findByAppraiser(item.getId(), Session.getUser().getIdUser());
	    			item.setMember(appraiser.isSubstitute() ? "Suplente" : (appraiser.isChair() ? "Presidente" : "Titular"));
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
			try{
				JuryBO bo = new JuryBO();
				Jury jury = bo.findById((int)value);
				
				byte[] file = null;
				
				if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)){
	    			ThesisBO tbo = new ThesisBO();
	    			Thesis thesis = tbo.findById(jury.getThesis().getIdThesis());
	    			
	    			file = thesis.getFile();
	    		}else{
	    			ProjectBO pbo = new ProjectBO();
	    			Project project = pbo.findById(jury.getProject().getIdProject());
	    			
	    			file = project.getFile();
	    		}
				
				this.showReport(file);
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Download do Trabalho", e.getMessage());
			}
		}
	}
	
	private void downloadTerm(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Termo de Aprovação", "Selecione uma banca para gerar o termo de aprovação.");
		}else{
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.PRINT))
		    	.withCaption("Imprimir Termo de Aprovação")
		    	.withMessage("Selecione o modo de geração para o Termo de Aprovação.\n\n" +
						"Versão Impressa: documento que deverá ser assinado e armazenado na coordenação.\n" + 
						"Versão Eletrônica: documento sem assinaturas que deverá ser incluído na versão final entregue pelo acadêmico.")
		    	.withOkButton(() -> {
		    		try{
        				showReport(new JuryBO().getTermOfApproval((int)value, false, Session.isUserManager(SystemModule.SIGET)));
                	}catch(Exception e){
        				Logger.log(Level.SEVERE, e.getMessage(), e);
        	        	
        				showErrorNotification("Imprimir Termo de Aprovação", e.getMessage());
        			}
		    	}, ButtonOption.caption("Versão Impressa"), ButtonOption.icon(VaadinIcon.PRINT))
		    	.withCancelButton(() -> {
		    		try{
        				showReport(new JuryBO().getTermOfApproval((int)value, true, Session.isUserManager(SystemModule.SIGET)));
                	}catch(Exception e){
        				Logger.log(Level.SEVERE, e.getMessage(), e);
        	        	
        				showErrorNotification("Imprimir Termo de Aprovação", e.getMessage());
        			}
		    	},ButtonOption.focus(), ButtonOption.caption("Versão Eletrônica"), ButtonOption.icon(VaadinIcon.FILE_TEXT_O))
		    	.open();
		}
	}
	
	private void downloadForm(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Imprimir Ficha de Avaliação", "Selecione uma banca para gerar a ficha de avaliação.");
		}else{
			try{
				this.showReport(new JuryBO().getJuryForm((int)value));
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
				JuryBO bo = new JuryBO();
				
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
				JuryBO bo = new JuryBO();
				
				this.showReport(bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (int)value, 0, 0, true));
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
    			JuryAppraiserBO bo = new JuryAppraiserBO();
    			
    			JuryAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
    			
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
	
	private void downloadSingleStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione uma banca para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report;
				
				if(Session.isUserSupervisor()){
					JuryAppraiserBO jbo = new JuryAppraiserBO();
					JuryAppraiser appraiser = jbo.findByAppraiser((int)value, Session.getUser().getIdUser());
					
					report = bo.getJuryProfessorStatement(appraiser);
				}else{
					JuryStudentBO jbo = new JuryStudentBO();
					JuryStudent student = jbo.findByStudent((int)value, Session.getUser().getIdUser());
					
					report = bo.getJuryStudentStatement(student);
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

				byte[] reportProfessor = bo.getJuryProfessorStatementReportList((int)value);
				
				if(reportProfessor != null){
					pdfMerge.addSource(new ByteArrayInputStream(reportProfessor));
				}
				
				byte[] reportStudent = bo.getJuryStudentStatementReportList((int)value);
				
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
			JuryBO bo = new JuryBO();
			byte[] report = null;
			
			if(this.listAll) {
				report = bo.getScheduleReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 0, this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage());
			} else if(Session.isUserProfessor() || Session.isUserSupervisor()){
				report = bo.getScheduleReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear(), 0);
			}
			
			this.showReport(report);
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Imprimir Agenda de Bancas", e.getMessage());
		}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.comboStage.getStage(), false));
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
				if(Session.getUser().getIdUser() == new JuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser()) {
					JuryAppraiserChangeWindow window = new JuryAppraiserChangeWindow(new JuryBO().findById((int)value));
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
				if(Session.getUser().getIdUser() == new JuryAppraiserBO().findChair((int)value).getAppraiser().getIdUser()) {
					JuryGradesWindow window = new JuryGradesWindow(new JuryBO().findById((int)value));
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
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			JuryBO bo = new JuryBO();
			Jury jury = bo.findById((int)id);
			
			EditJuryWindow window = new EditJuryWindow(jury, this);
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
	
	private void addScore() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Lançar Notas", "Selecione uma banca para lançar as notas.");
		} else {
			try {
				if(this.config.getDepartment().getIdDepartment() == 0) {
					SigetConfig c = new SigetConfigBO().findByDepartment(new JuryBO().findIdDepartment((int)value));

					if(!c.isAppraiserFillsGrades()) {
						this.showWarningNotification("Lançar Notas", "As notas devem ser encaminhadas ao Professor Responsável pelo TCC.");
						return;
					}
				}
				
				JuryAppraiser appraiser = new JuryAppraiserBO().findByAppraiser((int)value, Session.getUser().getIdUser());
				
				if((appraiser == null) || (appraiser.getIdJuryAppraiser() == 0)) {
					this.showWarningNotification("Lançar Notas", "Não é possível lançar as notas pois você não faz parte dessa banca.");
				} else if(appraiser.isSubstitute()) {
					this.showWarningNotification("Lançar Notas", "Apenas membros titulares da banca podem lançar notas.");
				} else {
					EditJuryAppraiserScoreWindow window = new EditJuryAppraiserScoreWindow(appraiser);
					window.open();
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Lançar Notas", e.getMessage());
			}
		}
	}
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if((parameter != null) && (!parameter.trim().isEmpty())){
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
