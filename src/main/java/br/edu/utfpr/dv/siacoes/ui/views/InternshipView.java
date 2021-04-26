package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipCompanyStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.DownloadInternshipFeedbackWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryRequestWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipPosterRequestWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipReportWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.InternshipJuryGradesWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.InternshipUploadFinalReportWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Estágios")
@Route(value = "internship", layout = MainLayout.class)
public class InternshipView extends ListView<InternshipDataSource> implements HasUrlParameter<String> {
	
	private UserProfile profile = UserProfile.STUDENT;
	
	private static final String ALL = "(TODOS)";
	
	private final YearField textYear;
	private final StudentComboBox comboStudent;
	private final SupervisorComboBox comboProfessor;
	private final CompanyComboBox comboCompany;
	private final Select<String> comboStatus;
	private final Select<String> comboCompanyStatus;
	private final Select<String> comboType;
	private final Select<String> comboTag;
	private final DatePicker textStartDate1;
	private final DatePicker textStartDate2;
	private final DatePicker textEndDate1;
	private final DatePicker textEndDate2;
	private final Button buttonParcialReport;
	private final Button buttonCompanySupervisorReport;
	private final Button buttonFinalReport;
	private final Button buttonFinalDocument;
	private final Button buttonJury;
	private final Button buttonProfessorStatement;
	private final Button buttonStudentStatement;
	private final Button buttonJuryFeedback;
	private final Button buttonPosterRequest;
	private final Button buttonPrintPosterRequest;
	private final Button buttonJuryGrades;
	private final Button buttonReport;
	
	private SigesConfig config;
	
	public InternshipView(){
		super(SystemModule.SIGES);
		
		try {
			this.config = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch(Exception e) {
			this.config = new SigesConfig();
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.getGrid().addColumn(InternshipDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(InternshipDataSource::getCompany, "Company").setHeader("Empresa");
		this.getGrid().addColumn(InternshipDataSource::getSupervisor, "Supervisor").setHeader("Orientador");
		this.getGrid().addColumn(new LocalDateRenderer<>(InternshipDataSource::getStartDate, "dd/MM/yyyy"), "Date").setHeader("Data Início").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(InternshipDataSource::getType, "Type").setHeader("Tipo").setFlexGrow(0).setWidth("160px");
		this.getGrid().addColumn(InternshipDataSource::getStatus, "Status").setHeader("Situação").setFlexGrow(0).setWidth("150px");
		
		this.textYear = new YearField();
		this.textYear.setYear(0);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.comboProfessor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.comboCompany = new CompanyComboBox();
		
		this.comboStatus = new Select<String>();
		this.comboStatus.setLabel("Situação do Processo");
		this.comboStatus.setWidth("170px");
		this.comboStatus.setItems(InternshipStatus.CURRENT.toString(), InternshipStatus.FINISHED.toString(), ALL);
		this.comboStatus.setValue(InternshipStatus.CURRENT.toString());
		
		this.comboCompanyStatus = new Select<String>();
		this.comboCompanyStatus.setLabel("Situação na Empresa");
		this.comboCompanyStatus.setWidth("170px");
		this.comboCompanyStatus.setItems(InternshipCompanyStatus.CURRENT.toString(), InternshipCompanyStatus.FINISHED.toString(), InternshipCompanyStatus.TERMINATED.toString(), ALL);
		this.comboCompanyStatus.setValue(InternshipCompanyStatus.CURRENT.toString());
		
		this.comboType = new Select<String>();
		this.comboType.setLabel("Tipo");
		this.comboType.setWidth("150px");
		this.comboType.setItems(InternshipType.NONREQUIRED.toString(), InternshipType.REQUIRED.toString(), ALL);
		this.comboType.setValue(ALL);
		
		this.textStartDate1 = new DatePicker("Início Entre");
		//this.textStartDate1.setDateFormat("dd/MM/yyyy");
		this.textStartDate2 = new DatePicker("E");
		//this.textStartDate2.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate1 = new DatePicker("Término Entre");
		//this.textEndDate1.setDateFormat("dd/MM/yyyy");
		this.textEndDate2 = new DatePicker("E");
		//this.textEndDate2.setDateFormat("dd/MM/yyyy");
		
		this.comboTag = new Select<String>();
		this.comboTag.setLabel("Apenas com a tag");
		this.comboTag.setWidth("250px");
		this.comboTag.setItems(new InternshipBO().getTagsList(Session.getSelectedDepartment().getDepartment().getIdDepartment(), true));
		this.comboTag.setValue(ALL);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboStudent, this.comboProfessor, this.comboType, this.comboCompanyStatus, this.comboStatus);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboCompany, this.textYear, this.textStartDate1, this.textStartDate2, this.textEndDate1, this.textEndDate2);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		VerticalLayout v1 = new VerticalLayout(h1, h2, this.comboTag);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		
		this.addFilterField(new HorizontalLayout(v1));
		
		this.buttonJury = new Button("Banca", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
            juryClick();
        });
		this.addActionButton(this.buttonJury);	
		
		this.buttonProfessorStatement = new Button("Declaração", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadProfessorStatement();
        });
		this.addActionButton(this.buttonProfessorStatement);
		
		this.buttonStudentStatement = new Button("Declaração", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadStudentStatement();
        });
		this.addActionButton(this.buttonStudentStatement);
		
		this.buttonParcialReport = new Button("Relatório Parcial", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            parcialReportClick();
        });
		this.addActionButton(this.buttonParcialReport);
		
		this.buttonCompanySupervisorReport = new Button("Relat. Supervisor", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            companySupervisorReportClick();
        });
		this.addActionButton(this.buttonCompanySupervisorReport);
		
		this.buttonFinalReport = new Button("Relatório Final", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            finalReportClick();
        });
		this.addActionButton(this.buttonFinalReport);
		
		this.buttonPosterRequest = new Button("Solicitar Banca", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
			if(config.getJuryFormat() == JuryFormat.SESSION) {
        		posterRequestClick();
        	} else {
        		juryRequestClick();
        	}
        });
		this.addActionButton(this.buttonPosterRequest);
		
		this.buttonPrintPosterRequest = new Button("Imp. Solic. de Banca", event -> {
			if(config.getJuryFormat() == JuryFormat.SESSION) {
        		downloadPosterRequest();
        	} else {
        		downloadJuryRequest();
        	}
        });
		this.addActionButton(this.buttonPrintPosterRequest);
		
		this.buttonJuryFeedback = new Button("Feedback da Banca", new Icon(VaadinIcon.CHECK), event -> {
            juryFeedback();
        });
		this.addActionButton(this.buttonJuryFeedback);
		
		this.buttonJuryGrades = new Button("Notas da Banca", new Icon(VaadinIcon.CALC_BOOK), event -> {
            juryGrades();
        });
		this.addActionButton(this.buttonJuryGrades);
		
		this.buttonFinalDocument = new Button("Versão Final", new Icon(VaadinIcon.CLOUD_UPLOAD), event -> {
            finalDocumentClick();
        });
		this.addActionButton(this.buttonFinalDocument);
		
		this.buttonReport = new Button("Relatório", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            internshipReport();
        });
		this.addActionButton(this.buttonReport);
	}
	
	private void configureProfile(){
		if((this.profile != UserProfile.MANAGER) || !Session.isUserManager(this.getModule())) {
			this.setFiltersVisible(false);
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			this.setEditCaption("Visualizar");
			this.setEditIcon(new Icon(VaadinIcon.SEARCH));
			
			this.buttonJury.setVisible(false);
			this.buttonReport.setVisible(false);
			this.buttonProfessorStatement.setVisible(this.profile == UserProfile.PROFESSOR);
			this.buttonStudentStatement.setVisible(this.profile == UserProfile.STUDENT);
			this.buttonParcialReport.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			this.buttonFinalReport.setVisible(this.profile == UserProfile.STUDENT);
			this.buttonJuryFeedback.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			this.buttonFinalDocument.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			this.buttonPosterRequest.setVisible(((this.profile == UserProfile.STUDENT) && this.config.isStudentRequestJury()) || (this.profile == UserProfile.PROFESSOR));
			//this.buttonPrintPosterRequest.setVisible((this.profile == UserProfile.STUDENT) && (this.config.getJuryFormat() == JuryFormat.SESSION));
			this.buttonJuryGrades.setVisible(((this.profile == UserProfile.STUDENT) || (this.profile == UserProfile.PROFESSOR)) && (this.config.isShowGradesToStudent()));
			
			if(this.profile == UserProfile.PROFESSOR){
				this.buttonFinalDocument.setText("Val. Relat. Final");
				this.buttonFinalDocument.setIcon(new Icon(VaadinIcon.CHECK));
			}
		} else {
			this.buttonProfessorStatement.setText("Declaração Prof.");
			this.buttonStudentStatement.setText("Declaração Acad.");
			this.buttonParcialReport.setVisible(false);
			this.buttonFinalReport.setVisible(false);
			this.buttonPosterRequest.setVisible(false);
			this.buttonPrintPosterRequest.setVisible(false);
			this.buttonJuryFeedback.setVisible(false);
			this.buttonFinalDocument.setVisible(false);
			this.buttonJuryGrades.setVisible(false);
		}
	}

	@Override
	protected void loadGrid() {
		try{
			InternshipBO bo = new InternshipBO();
			List<Internship> list;
			
			if(this.profile == UserProfile.MANAGER){
				list = this.getInternshipList();
			}else if(this.profile == UserProfile.PROFESSOR){
				list = bo.listBySupervisor(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			}else if(this.profile == UserProfile.COMPANYSUPERVISOR){
				list = bo.listByCompanySupervisor(Session.getUser().getIdUser());
			}else{
				list = bo.listByStudent(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			}
			
			this.getGrid().setItems(InternshipDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Estágios", e.getMessage());
		}
	}
	
	private List<Internship> getInternshipList() throws Exception {
		int type = -1, status = -1, companyStatus = -1;
		String tag = "";
		
		if(!this.comboType.getValue().equals(ALL)){
			type = InternshipType.fromDescription(this.comboType.getValue()).getValue();
		}
		
		if(!this.comboStatus.getValue().equals(ALL)){
			status = InternshipStatus.fromDescription(this.comboStatus.getValue()).getValue();
		}
		
		if(!this.comboCompanyStatus.getValue().equals(ALL)){
			companyStatus = InternshipCompanyStatus.fromDescription(this.comboCompanyStatus.getValue()).getValue();
		}
		
		if(!this.comboTag.getValue().equals(ALL)) {
			tag = this.comboTag.getValue();
		}
		
		return new InternshipBO().list(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textYear.getYear(), (this.comboStudent.getStudent() == null ? 0 : this.comboStudent.getStudent().getIdUser()), (this.comboProfessor.getProfessor() == null ? 0 : this.comboProfessor.getProfessor().getIdUser()), (this.comboCompany.getCompany() == null ? 0 : this.comboCompany.getCompany().getIdCompany()), type, status, DateUtils.convertToDate(this.textStartDate1.getValue()), DateUtils.convertToDate(this.textStartDate2.getValue()), DateUtils.convertToDate(this.textEndDate1.getValue()), DateUtils.convertToDate(this.textEndDate2.getValue()), companyStatus, tag);
	}

	@Override
	public void addClick() {
		Internship internship = new Internship();
		internship.setDepartment(Session.getSelectedDepartment().getDepartment());
		
		EditInternshipWindow window = new EditInternshipWindow(internship, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			InternshipBO bo = new InternshipBO();
			Internship internship = bo.findById((int)id);
			
			EditInternshipWindow window = new EditInternshipWindow(internship, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Estágio", e.getMessage());
		}
	}
	
	private void parcialReportClick() {
		Object id = getIdSelected();
		
		if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para enviar o relatório parcial.");
    	} else {
    		try {
				Internship internship = new InternshipBO().findById((int)id);
				
				EditInternshipReportWindow window = new EditInternshipReportWindow(null, internship, ((this.profile == UserProfile.PROFESSOR) ? ReportType.SUPERVISOR : ReportType.STUDENT));
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Enviar Relatório", e.getMessage());
			}
    	}
	}
	
	private void companySupervisorReportClick() {
		Object id = getIdSelected();
		
		if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para enviar o relatório do supervisor.");
    	} else {
    		try {
				Internship internship = new InternshipBO().findById((int)id);
				
				EditInternshipReportWindow window = new EditInternshipReportWindow(null, internship, ReportType.COMPANY);
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Enviar Relatório", e.getMessage());
			}
    	}
	}
	
	private void finalReportClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para enviar o relatório final.");
    	}else{
    		try{
    			InternshipJuryBO jbo = new InternshipJuryBO();
    			InternshipJury jury = jbo.findByInternship((int)id);
    			
    			if((jury != null) && (jury.getIdInternshipJury() != 0) && (jury.getDate().before(DateUtils.getToday().getTime()))) {
    				ConfirmDialog.createQuestion()
	        			.withIcon(new Icon(VaadinIcon.QUESTION))
	        	    	.withCaption("Enviar Relatório")
	        	    	.withMessage("Não é possível enviar o relatório pois a banca de estágio já ocorreu.\n\nVocê deseja enviar a versão final do relatório?")
	        	    	.withOkButton(() -> {
	        	    		finalDocumentClick();
	        	    	}, ButtonOption.caption("Sim"), ButtonOption.icon(VaadinIcon.CHECK))
	        	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Não"), ButtonOption.icon(VaadinIcon.CLOSE))
	        	    	.open();
    			} else {
    				InternshipBO bo = new InternshipBO();
        			Internship internship = bo.findById((int)id);
        			
        			InternshipUploadFinalReportWindow window = new InternshipUploadFinalReportWindow(internship, this);
        			window.open();
    			}
    		}catch(Exception e){
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Enviar Relatório", e.getMessage());
    		}
    	}
	}
	
	private void juryClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para marcar a banca.");
    	}else{
    		try {
    			Internship internship = new InternshipBO().findById((int)id);
    			
    			if(internship.getType() == InternshipType.REQUIRED){
    				InternshipJury jury = new InternshipJuryBO().findByInternship((int)id);
    				
    				EditInternshipJuryWindow window = new EditInternshipJuryWindow(jury, this);
    				window.open();
    			}else{
    				this.showWarningNotification("Marcar Banca", "A banca só pode ser marcada para o estágio obrigatório.");
    			}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Marcar Banca", e.getMessage());
			}
    	}
	}
	
	private void finalDocumentClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para " + ((this.profile == UserProfile.PROFESSOR) ? "validar a versão final do relatório final" : "enviar a versão final do relatório") + ".");
    	}else{
    		try{
    			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
    			InternshipFinalDocument doc = bo.findByInternship((int)id);
    			
    			if(doc == null){
    				if(this.profile == UserProfile.PROFESSOR){
    					throw new Exception("O acadêmico ainda não enviou a versão final do relatório.");
    				}else{
    					doc = new InternshipFinalDocument();
    					
    					InternshipBO bo2 = new InternshipBO();
    					Internship internship = bo2.findById((int)id);
    					
    					if(internship.getType() != InternshipType.REQUIRED){
    						throw new Exception("A versão final do relatório só pode ser enviada para estágio obrigatório.");
    					}else{
    						InternshipJuryBO bo3 = new InternshipJuryBO();
    						InternshipJury jury = bo3.findByInternship((int)id);
    						
    						if((jury == null) || (jury.getIdInternshipJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())){
    							throw new Exception("A versão final do relatório só pode ser enviada após a realização da banca de estágio.");
    						}
    					}
    					
    					doc.setInternship(internship);
    					doc.setTitle(internship.getReportTitle());
    				}
    			}
    			
    			EditInternshipFinalDocumentWindow window = new EditInternshipFinalDocumentWindow(doc, this);
    			window.open();
    		} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification(((this.profile == UserProfile.PROFESSOR) ? "Validar" : "Enviar") + " Versão Final", e.getMessage());
			}
    	}
	}

	@Override
	public void deleteClick(int id) {
		try {
			InternshipBO bo = new InternshipBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Estágio", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private void juryFeedback(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para visualizar o feedback.");
    	}else{
    		try{
    			InternshipJuryBO bo = new InternshipJuryBO();
    			InternshipJury jury = bo.findByInternship((int)id);
    			
    			if((jury != null) && (jury.getIdInternshipJury() != 0)){
    				DownloadInternshipFeedbackWindow window = new DownloadInternshipFeedbackWindow(jury);
    				window.open();
    			}else{
    				this.showWarningNotification("Feedback da Banca", "Ainda não foi marcada a banca para este trabalho.");
    			}
    		} catch (Exception e) {
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Feedback da Banca", e.getMessage());
    		}
    	}
	}
	
	private void downloadProfessorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getInternshipProfessorStatement((int)value);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadStudentStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getInternshipStudentStatement((int)value);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void posterRequestClick() {
		Object id = getIdSelected();
    	
    	if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para efetuar a solicitação de banca.");
    	} else {
    		try {
				InternshipJury jury = new InternshipJuryBO().findByInternship((int)id);
				
				if((jury != null) && (jury.getIdInternshipJury() != 0)) {
					this.showWarningNotification("Solicitar Banca", "Não é possível efetuar a solicitação pois a banca já foi agendada.");
				} else {
					Internship internship = new InternshipBO().findById((int)id);
	    			
	    			if(internship.getType() == InternshipType.REQUIRED) {
	    				InternshipPosterRequest request = new InternshipPosterRequestBO().preparePosterRequest((int)id);
	    				
	    				EditInternshipPosterRequestWindow window = new EditInternshipPosterRequestWindow(request, this);
	    				window.open();
	    			} else {
	    				this.showWarningNotification("Solicitar Banca", "A solicitação de banca só pode ser efetuada para o estágio obrigatório");
	    			}
				}
    		} catch(Exception e) {
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Solicitar Banca", e.getMessage());
    		}
    	}
	}
	
	private void juryRequestClick() {
		Object id = getIdSelected();
    	
    	if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para efetuar a solicitação de banca.");
    	} else {
    		try {
				InternshipJury jury = new InternshipJuryBO().findByInternship((int)id);
				
				if((jury != null) && (jury.getIdInternshipJury() != 0)) {
					this.showWarningNotification("Solicitar Banca", "Não é possível efetuar a solicitação pois a banca já foi agendada.");
				} else {
					Internship internship = new InternshipBO().findById((int)id);
	    			
	    			if(internship.getType() == InternshipType.REQUIRED) {
	    				InternshipJuryRequest request = new InternshipJuryRequestBO().prepareInternshipJuryRequest((int)id);
	    				
	    				EditInternshipJuryRequestWindow window = new EditInternshipJuryRequestWindow(request, this);
	    				window.open();
	    			} else {
	    				this.showWarningNotification("Solicitar Banca", "A solicitação de banca só pode ser efetuada para o estágio obrigatório");
	    			}
				}
    		} catch(Exception e) {
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Solicitar Banca", e.getMessage());
    		}
    	}
	}
	
	private void downloadPosterRequest() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
				this.showReport(new InternshipPosterRequestBO().getPosterRequestForm(new InternshipPosterRequestBO().preparePosterRequest((int)value).getIdInternshipPosterRequest()));
			} catch (Exception e) {
            	Logger.log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Solicitação de Banca", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Solicitação de Banca", "Selecione um registro para imprimir a Solicitação de Banca.");
		}
	}
	
	private void downloadJuryRequest() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
				this.showReport(new InternshipJuryRequestBO().getInternshipJuryRequestForm(new InternshipJuryRequestBO().prepareInternshipJuryRequest((int)value).getIdInternshipJuryRequest()));
			} catch (Exception e) {
            	Logger.log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Solicitação de Banca", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Solicitação de Banca", "Selecione um registro para imprimir a Solicitação de Banca.");
		}
	}
	
	private void juryGrades() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
				Internship internship = new InternshipBO().findById((int)value);
    			
    			if(internship.getType() == InternshipType.REQUIRED) {
    				InternshipJuryGradesWindow window = new InternshipJuryGradesWindow(new InternshipJuryBO().findByInternship((int)value));
    				window.open();
    			} else {
    				this.showWarningNotification("Notas da Banca", "As notas somente são atribuídas a estágios obrigatórios.");
    			}
			} catch (Exception e) {
            	Logger.log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Notas da Banca", e.getMessage());
			}
		} else {
			this.showWarningNotification("Notas da Banca", "Selecione um registro para visualizar as notas atribuídas pela banca.");
		}
	}
	
	private void internshipReport() {
		try {
			List<Internship> list = this.getInternshipList();
			
			this.showReport(new InternshipBO().getInternshipReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), list));
		} catch (Exception e) {
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Relatório", e.getMessage());
		}
	}
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(this.profile != UserProfile.STUDENT)
			return;
		
		if(Session.isUserProfessor()) {
			this.profile = UserProfile.PROFESSOR;
		} else if(Session.isUserCompanySupervisor()) {
			this.profile = UserProfile.COMPANYSUPERVISOR;
		} else {
			this.profile = UserProfile.STUDENT;
		}
		
		if(parameter != null && !parameter.isEmpty()) {
			try{
				int p = Integer.parseInt(parameter);
				
				if((p == 1) && Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()){
					this.profile = UserProfile.MANAGER;
				}
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		this.refreshGrid();
		this.configureProfile();
	}

}
