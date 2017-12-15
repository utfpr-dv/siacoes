package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.components.ProfessorComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.DownloadInternshipFeedbackWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipWindow;
import br.edu.utfpr.dv.siacoes.window.InternshipUploadFinalReportWindow;

public class InternshipView extends ListView {
	
	public static final String NAME = "internship";
	private UserProfile profile = UserProfile.STUDENT;
	
	private final YearField textYear;
	private final StudentComboBox comboStudent;
	private final ProfessorComboBox comboProfessor;
	private final CompanyComboBox comboCompany;
	private final NativeSelect comboStatus;
	private final NativeSelect comboType;
	private final Button buttonFinalReport;
	private final Button buttonFinalDocument;
	private final Button buttonJury;
	private final Button buttonProfessorStatement;
	private final Button buttonStudentStatement;
	private final Button buttonJuryFeedback;
	
	public InternshipView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Estágios");
		
		this.textYear = new YearField();
		this.textYear.setYear(0);
		
		this.comboStudent = new StudentComboBox("Aluno");
		
		this.comboProfessor = new ProfessorComboBox("Orientador");
		
		this.comboCompany = new CompanyComboBox();
		
		this.comboStatus = new NativeSelect("Situação");
		this.comboStatus.setWidth("195px");
		this.comboStatus.setNullSelectionAllowed(false);
		this.comboStatus.addItem(InternshipStatus.CURRENT);
		this.comboStatus.addItem(InternshipStatus.FINISHED);
		this.comboStatus.addItem("Todos");
		this.comboStatus.select(InternshipStatus.CURRENT);
		
		this.comboType = new NativeSelect("Tipo");
		this.comboType.setWidth("195px");
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.addItem(InternshipType.NONREQUIRED);
		this.comboType.addItem(InternshipType.REQUIRED);
		this.comboType.addItem("Todos");
		this.comboType.select("Todos");
		
		VerticalLayout v1 = new VerticalLayout(this.comboStudent, this.comboCompany);
		v1.setSpacing(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboType, this.comboStatus);
		h1.setSpacing(true);
		
		VerticalLayout v2 = new VerticalLayout(this.comboProfessor, h1);
		v2.setSpacing(true);
		
		this.addFilterField(new HorizontalLayout(v1, v2, this.textYear));
		
		this.buttonJury = new Button("Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryClick();
            }
        });
		
		
		this.addActionButton(this.buttonJury);	
		
		this.buttonProfessorStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement();
            }
        });
		
		this.addActionButton(this.buttonProfessorStatement);
		
		this.buttonStudentStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStudentStatement();
            }
        });
		
		this.addActionButton(this.buttonStudentStatement);
		
		this.buttonFinalReport = new Button("Enviar Relatório", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	finalReportClick();
            }
        });
		
		this.addActionButton(this.buttonFinalReport);
		
		this.buttonJuryFeedback = new Button("Feedback da Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryFeedback();
            }
        });
		
		this.addActionButton(this.buttonJuryFeedback);
		
		this.buttonFinalDocument = new Button("Versão Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	finalDocumentClick();
            }
        });
		
		this.addActionButton(this.buttonFinalDocument);
	}
	
	private void configureProfile(){
		if((this.profile != UserProfile.MANAGER) || !Session.isUserManager(this.getModule())){
			this.setFiltersVisible(this.profile == UserProfile.MANAGER);
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			this.setEditCaption("Visualizar");
			
			this.buttonJury.setVisible(false);
			this.buttonProfessorStatement.setVisible(Session.isUserProfessor());
			this.buttonStudentStatement.setVisible(Session.isUserStudent());
			this.buttonFinalReport.setVisible(this.profile == UserProfile.STUDENT);
			this.buttonJuryFeedback.setVisible(this.profile == UserProfile.STUDENT || this.profile == UserProfile.PROFESSOR);
			
			if(this.profile == UserProfile.PROFESSOR){
				this.buttonFinalDocument.setCaption("Val. Relat. Final");
			}
		}else{
			this.buttonProfessorStatement.setCaption("Declaração Prof.");
			this.buttonStudentStatement.setCaption("Declaração Aluno");
		}
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Aluno", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addColumn("Orientador", String.class);
		this.getGrid().addColumn("Data Início", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Tipo", String.class);
		this.getGrid().addColumn("Situação", String.class);
		
		this.getGrid().getColumns().get(3).setWidth(125);
		this.getGrid().getColumns().get(4).setWidth(160);
		this.getGrid().getColumns().get(5).setWidth(150);
		
		try{
			InternshipBO bo = new InternshipBO();
			List<Internship> list;
			
			if(this.profile == UserProfile.MANAGER){
				int type = -1, status = -1;
				
				if(!this.comboType.getValue().equals("Todos")){
					type = ((InternshipType)this.comboType.getValue()).getValue();
				}
				
				if(!this.comboStatus.getValue().equals("Todos")){
					status = ((InternshipStatus)this.comboStatus.getValue()).getValue();
				}
				
				list = bo.list(Session.getUser().getDepartment().getIdDepartment(), this.textYear.getYear(), (this.comboStudent.getStudent() == null ? 0 : this.comboStudent.getStudent().getIdUser()), (this.comboProfessor.getProfessor() == null ? 0 : this.comboProfessor.getProfessor().getIdUser()), (this.comboCompany.getCompany() == null ? 0 : this.comboCompany.getCompany().getIdCompany()), type, status);
			}else if(this.profile == UserProfile.PROFESSOR){
				list = bo.listBySupervisor(Session.getUser().getIdUser());
			}else{
				list = bo.listByStudent(Session.getUser().getIdUser());
			}
			
			for(Internship c : list){
				Object itemId = this.getGrid().addRow(c.getStudent().getName(), c.getCompany().getName(), c.getSupervisor().getName(), c.getStartDate(), c.getType().toString(), c.getStatus().toString());
				this.addRowId(itemId, c.getIdInternship());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Estágios", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		Internship internship = new Internship();
		internship.setDepartment(Session.getUser().getDepartment());
		
		UI.getCurrent().addWindow(new EditInternshipWindow(internship, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			InternshipBO bo = new InternshipBO();
			Internship internship = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditInternshipWindow(internship, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Estágio", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void finalReportClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para enviar o relatório final.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try{
    			InternshipJuryBO jbo = new InternshipJuryBO();
    			InternshipJury jury = jbo.findByInternship((int)id);
    			
    			if((jury != null) && (jury.getIdInternshipJury() != 0) && (jury.getDate().before(DateUtils.getToday().getTime()))) {
    				ConfirmDialog.show(UI.getCurrent(), "Não é possível enviar o relatório pois a banca de estágio já ocorreu.\n\nVocê deseja enviar a versão final do relatório?", new ConfirmDialog.Listener() {
    	                public void onClose(ConfirmDialog dialog) {
    	                    if (dialog.isConfirmed()) {
    	                    	finalDocumentClick();
    	                    }
    	                }
    	            });
    			} else {
    				InternshipBO bo = new InternshipBO();
        			Internship internship = bo.findById((int)id);
        			
        			UI.getCurrent().addWindow(new InternshipUploadFinalReportWindow(internship, this));
    			}
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			Notification.show("Enviar Relatório", e.getMessage(), Notification.Type.ERROR_MESSAGE);
    		}
    	}
	}
	
	private void juryClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para marcar a banca.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try {
    			InternshipBO bo = new InternshipBO();
    			Internship internship = bo.findById((int)id);
    			
    			if(internship.getType() == InternshipType.REQUIRED){
    				InternshipJuryBO bo2 = new InternshipJuryBO();
    				InternshipJury jury = bo2.findByInternship((int)id);
    				
    				UI.getCurrent().addWindow(new EditInternshipJuryWindow(jury, this));
    			}else{
    				Notification.show("Marcar Banca", "A banca só pode ser marcada para o estágio obrigatório.", Notification.Type.WARNING_MESSAGE);    				
    			}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Marcar Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
    	}
	}
	
	private void finalDocumentClick(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para " + ((this.profile == UserProfile.PROFESSOR) ? "validar a versão final do relatório final" : "enviar a versão final do relatório") + ".", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try{
    			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
    			InternshipFinalDocument doc = bo.findByInternship((int)id);
    			
    			if(doc == null){
    				if(this.profile == UserProfile.PROFESSOR){
    					throw new Exception("O aluno ainda não enviou a versão final do relatório.");
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
    			
    			UI.getCurrent().addWindow(new EditInternshipFinalDocumentWindow(doc, this));
    		} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show(((this.profile == UserProfile.PROFESSOR) ? "Validar" : "Enviar") + " Versão Final", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
    	}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			InternshipBO bo = new InternshipBO();
			
			bo.delete((int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Excluir Estágio", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private void juryFeedback(){
		Object id = getIdSelected();
    	
    	if(id == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para visualizar o feedback.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		try{
    			InternshipJuryBO bo = new InternshipJuryBO();
    			InternshipJury jury = bo.findByInternship((int)id);
    			
    			if((jury != null) && (jury.getIdInternshipJury() != 0)){
    				UI.getCurrent().addWindow(new DownloadInternshipFeedbackWindow(jury));
    			}else{
    				Notification.show("Feedback da Banca", "Ainda não foi marcada a banca para este trabalho.", Notification.Type.WARNING_MESSAGE);
    			}
    		} catch (Exception e) {
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			Notification.show("Feedback da Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
    		}
    	}
	}
	
	private void downloadProfessorStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione um registro para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getInternshipProfessorStatement((int)value);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadStudentStatement(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione um registro para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getInternshipStudentStatement((int)value);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event){
		super.enter(event);
		
		if(Session.isUserProfessor()){
			this.profile = UserProfile.PROFESSOR;
		}else{
			this.profile = UserProfile.STUDENT;
		}
		
		if((event.getParameters() != null) && (!event.getParameters().isEmpty())){
			try{
				int p = Integer.parseInt(event.getParameters());
				
				if((p == 1) && Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()){
					this.profile = UserProfile.MANAGER;
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		this.refreshGrid();
		this.configureProfile();
	}

}
