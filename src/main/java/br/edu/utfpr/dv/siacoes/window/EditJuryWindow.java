package br.edu.utfpr.dv.siacoes.window;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.thomas.timefield.TimeField;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditJuryWindow extends EditWindow {

	private final Jury jury;
	
	private final DateField textDate;
	private final TextField textLocal;
	private final HorizontalLayout layoutAppraisers;
	private final HorizontalLayout layoutParticipants;
	private Grid gridAppraisers;
	private Grid gridParticipants;
	private final Button buttonAddAppraiser;
	private final Button buttonEditAppraiser;
	private final Button buttonRemoveAppraiser;
	private final Button buttonAppraiserScore;
	private final Button buttonAppraiserStatement;
	private final Button buttonAddParticipant;
	private final Button buttonRemoveParticipant;
	private final Button buttonParticipantStatement;
	private final Button buttonParticipants;
	private final Button buttonParticipantsReport;
	private final TimeField textStartTime;
	private final TimeField textEndTime;
	private final TextArea textComments;
	private final TextArea textSupervisorAbsenceReason;
	private final TabSheet tabContainer;
	
	public EditJuryWindow(Jury jury, ListView parentView){
		super("Banca", parentView);
		
		if(jury == null){
			this.jury = new Jury();
		}else{
			this.jury = jury;
		}
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("810px");
		this.tabContainer.setHeight("450px");
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("800px");
		this.textLocal.setMaxLength(100);
		this.textLocal.setRequired(true);
		
		this.textDate = new DateField("Data");
		this.textDate.setDateFormat("dd/MM/yyyy HH:mm");
		this.textDate.setResolution(Resolution.MINUTE);
		this.textDate.setRequired(true);
		
		this.textStartTime = new TimeField("Horário Inicial");
		this.textStartTime.set24HourClock(true);
		
		this.textEndTime = new TimeField("Horário Final");
		this.textEndTime.set24HourClock(true);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("150px");
		this.textComments.addStyleName("textscroll");
		
		this.textSupervisorAbsenceReason = new TextArea("Motivo da ausência do Professor Orientador na banca");
		this.textSupervisorAbsenceReason.setWidth("800px");
		this.textSupervisorAbsenceReason.setHeight("75px");
		this.textSupervisorAbsenceReason.addStyleName("textscroll");
		
		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSpacing(true);
		tab1.addComponent(this.textLocal);
		HorizontalLayout h1 = new HorizontalLayout(this.textDate, this.textStartTime, this.textEndTime);
		h1.setSpacing(true);
		tab1.addComponent(h1);
		tab1.addComponent(this.textComments);
		tab1.addComponent(this.textSupervisorAbsenceReason);
		
		this.layoutAppraisers = new HorizontalLayout();
		
		this.buttonAddAppraiser = new Button("Adicionar Membro", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addAppraiser();
            }
        });
		this.buttonAddAppraiser.setIcon(FontAwesome.PLUS);
		this.buttonAddAppraiser.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		this.buttonEditAppraiser = new Button("Editar Membro", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editAppraiser();
            }
        });
		this.buttonEditAppraiser.setIcon(FontAwesome.EDIT);
		this.buttonEditAppraiser.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		this.buttonRemoveAppraiser = new Button("Remover Membro", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeAppraiser();
            }
        });
		this.buttonRemoveAppraiser.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveAppraiser.addStyleName(ValoTheme.BUTTON_DANGER);
		
		this.buttonAppraiserScore = new Button("Lançar Notas", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addScore();
            }
        });
		this.buttonAppraiserScore.setIcon(FontAwesome.CALCULATOR);
		
		this.buttonAppraiserStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement();
            }
        });
		this.buttonAppraiserStatement.setIcon(FontAwesome.FILE_PDF_O);
		
		this.buttonParticipants = new Button("Lista de Presença", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadParticipants();
            }
        });
		
		this.buttonParticipantsReport = new Button("Lista de Ouvintes", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadParticipantsReport();
            }
        });
		
		HorizontalLayout layoutGridButtons = new HorizontalLayout(this.buttonAddAppraiser, this.buttonEditAppraiser, this.buttonRemoveAppraiser, this.buttonAppraiserScore, this.buttonAppraiserStatement);
		layoutGridButtons.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.layoutAppraisers, layoutGridButtons);
		tab2.setSpacing(true);
		
		this.layoutParticipants = new HorizontalLayout();
		
		this.buttonAddParticipant = new Button("Adicionar Acadêmico", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addParticipant();            	
            }
        });
		this.buttonAddParticipant.setIcon(FontAwesome.PLUS);
		this.buttonAddParticipant.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		this.buttonRemoveParticipant = new Button("Remover Acadêmico", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeParticipant();
            }
        });
		this.buttonRemoveParticipant.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveParticipant.addStyleName(ValoTheme.BUTTON_DANGER);
		
		this.buttonParticipantStatement = new Button("Gerar Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStudentStatement();
            }
        });
		this.buttonParticipantStatement.setIcon(FontAwesome.FILE_PDF_O);
		
		HorizontalLayout layoutGridButtons2 = new HorizontalLayout(this.buttonAddParticipant, this.buttonRemoveParticipant, this.buttonParticipantStatement, this.buttonParticipants, this.buttonParticipantsReport);
		layoutGridButtons2.setSpacing(true);
		
		VerticalLayout tab3 = new VerticalLayout(this.layoutParticipants, layoutGridButtons2);
		tab3.setSpacing(true);
		
		this.tabContainer.addTab(tab1, "Dados da Banca");
		this.tabContainer.addTab(tab2, "Membros");
		this.tabContainer.addTab(tab3, "Ouvintes");
		
		this.addField(this.tabContainer);
		
		this.loadJury();
		this.textLocal.focus();
	}
	
	private void loadJury(){
		this.textDate.setValue(this.jury.getDate());
		this.textLocal.setValue(this.jury.getLocal());
		this.textStartTime.setValue(this.jury.getStartTime());
		this.textEndTime.setValue(this.jury.getEndTime());
		this.textComments.setValue(this.jury.getComments());
		this.textSupervisorAbsenceReason.setValue(this.jury.getSupervisorAbsenceReason());
		
		if(this.jury.getIdJury() == 0) {
			this.textStartTime.setVisible(false);
			this.textEndTime.setVisible(false);
		}
		
		if(this.jury.getAppraisers() == null){
			try {
				JuryAppraiserBO bo = new JuryAppraiserBO();
				
				this.jury.setAppraisers(bo.listAppraisers(this.jury.getIdJury()));
			} catch (Exception e) {
				this.jury.setAppraisers(null);
				
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Banca", e.getMessage());
			}
		}
		
		if(this.jury.getParticipants() == null){
			try{
				JuryStudentBO bo = new JuryStudentBO();
				
				this.jury.setParticipants(bo.listByJury(this.jury.getIdJury()));
			} catch (Exception e) {
				this.jury.setParticipants(null);
				
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Participantes", e.getMessage());
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridParticipants();
		
		if(this.jury.getIdJury() == 0) {
			try{
				int idDepartment = 0;
				
				if((this.jury.getThesis() != null) && (this.jury.getThesis().getIdThesis() != 0)) {
					idDepartment = new ThesisBO().findIdDepartment(this.jury.getThesis().getIdThesis());
				} else {
					idDepartment = new ProjectBO().findIdDepartment(this.jury.getProject().getIdProject());
				}
				
				SigetConfig config = new SigetConfigBO().findByDepartment(idDepartment);
				
				if(config.isSupervisorJuryRequest()) {
					if((this.jury.getJuryRequest() == null) || (this.jury.getJuryRequest().getIdJuryRequest() == 0)) {
						this.showWarningNotification("Agendamento de Banca", "O Professor Orientador não efetuou a solicitação de agendamento de banca.");
					}
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Agendamento de Banca", e.getMessage());
			}
		} else {
			try {
				if(Document.hasSignature(DocumentType.JURY, this.jury.getIdJury())) {
					this.disableButtons();
				}
			} catch (SQLException e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.disableButtons();
			}
		}
	}
	
	private void loadGridAppraisers(){
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Membro", String.class);
		this.gridAppraisers.addColumn("Nome", String.class);
		this.gridAppraisers.setWidth("800px");
		this.gridAppraisers.setHeight("370px");
		this.gridAppraisers.getColumns().get(0).setWidth(100);
		
		if(this.jury.getAppraisers() != null){
			int member = 1, substitute = 1;
			
			for(JuryAppraiser appraiser : this.jury.getAppraisers()){
				if(appraiser.isChair()) {
					this.gridAppraisers.addRow("Presidente", appraiser.getAppraiser().getName());
				} else if (appraiser.isSubstitute()) {
					this.gridAppraisers.addRow("Suplente " + String.valueOf(substitute), appraiser.getAppraiser().getName());
					substitute = substitute + 1;
				}else {
					this.gridAppraisers.addRow("Membro " + String.valueOf(member), appraiser.getAppraiser().getName());
					member = member + 1;
				}
			}
		}
		
		this.layoutAppraisers.removeAllComponents();
		this.layoutAppraisers.addComponent(this.gridAppraisers);
	}
	
	private void loadGridParticipants(){
		this.gridParticipants = new Grid();
		this.gridParticipants.addColumn("RA", String.class);
		this.gridParticipants.addColumn("Nome", String.class);
		this.gridParticipants.setWidth("800px");
		this.gridParticipants.setHeight("370px");
		this.gridParticipants.getColumns().get(0).setWidth(100);
		
		if(this.jury.getAppraisers() != null){
			for(JuryStudent student : this.jury.getParticipants()){
				this.gridParticipants.addRow(student.getStudent().getStudentCode(), student.getStudent().getName());
			}
		}
		
		this.layoutParticipants.removeAllComponents();
		this.layoutParticipants.addComponent(this.gridParticipants);
	}
	
	@Override
	public void disableButtons() {
		super.disableButtons();
		this.buttonAddAppraiser.setEnabled(false);
		this.buttonEditAppraiser.setEnabled(false);
		this.buttonRemoveAppraiser.setEnabled(false);
		this.buttonAppraiserScore.setEnabled(false);
	}
	
	@Override
	public void save() {
		try {
			JuryBO bo = new JuryBO();
			
			this.jury.setLocal(this.textLocal.getValue());
			this.jury.setComments(this.textComments.getValue());
			this.jury.setStartTime(this.textStartTime.getValue());
			this.jury.setEndTime(this.textEndTime.getValue());
			this.jury.setDate(this.textDate.getValue());
			this.jury.setSupervisorAbsenceReason(this.textSupervisorAbsenceReason.getValue());
			
			bo.save(Session.getIdUserLog(), this.jury);
			
			this.showSuccessNotification("Salvar Banca", "Banca salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Banca", e.getMessage());
		}
	}
	
	private void addAppraiser(){
		UI.getCurrent().addWindow(new EditJuryAppraiserWindow(this));
	}
	
	public void addAppraiser(JuryAppraiser appraiser) throws Exception{
		JuryBO bo = new JuryBO();
		
		if(bo.canAddAppraiser(this.jury, appraiser.getAppraiser())){
			this.jury.getAppraisers().add(appraiser);
			
			this.loadGridAppraisers();
		}
	}
	
	private void addParticipant(){
		UI.getCurrent().addWindow(new EditJuryParticipantWindow(this));
	}
	
	public void addParticipant(User student) throws Exception{
		JuryStudent js = new JuryStudent();
		
		js.setStudent(student);
		
		this.jury.getParticipants().add(js);
		
		this.loadGridParticipants();
	}
	
	private void editAppraiser() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1) {
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para editar.");
		} else {
			try {
				if(new JuryBO().canRemoveAppraiser(this.jury, this.jury.getAppraisers().get(index).getAppraiser())) {
					UI.getCurrent().addWindow(new EditJuryAppraiserWindow(this.jury.getAppraisers().get(index), this));
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Editar Membro", e.getMessage().replace("removido", "editado"));
			}
		}
	}
	
	public void editAppraiser(JuryAppraiser appraiser) {
		for(JuryAppraiser a : this.jury.getAppraisers()) {
			if(a.getAppraiser().getIdUser() == appraiser.getAppraiser().getIdUser()) {
				a.setChair(appraiser.isChair());
				a.setSubstitute(appraiser.isSubstitute());
				
				this.loadGridAppraisers();
			}
		}
	}
	
	private void removeAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para remover.");
		}else{
			try {
				JuryBO bo = new JuryBO();
				JuryAppraiser appraiser = this.jury.getAppraisers().get(index);
				
				if(bo.canRemoveAppraiser(this.jury, appraiser.getAppraiser())){
					ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do membro?", new ConfirmDialog.Listener() {
		                public void onClose(ConfirmDialog dialog) {
		                    if (dialog.isConfirmed()) {
		                    	jury.getAppraisers().remove(index);
		                    	loadGridAppraisers();
		                    }
		                }
		            });
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Membro", e.getMessage());
			}
		}
	}
	
	private void removeParticipant(){
		int index = this.getParticipantSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Participante", "Selecione o participante para remover.");
		}else{
			try{
				ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do participante?", new ConfirmDialog.Listener() {
	                public void onClose(ConfirmDialog dialog) {
	                    if (dialog.isConfirmed()) {
	                    	jury.getParticipants().remove(index);
	                    	loadGridParticipants();
	                    }
	                }
	            });
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Participante", e.getMessage());
			}
		}
	}
	
	private void addScore() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1) {
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para lançar as notas.");
		} else {
			JuryAppraiser appraiser = this.jury.getAppraisers().get(index);
			
			if((appraiser == null) || (appraiser.getIdJuryAppraiser() == 0)) {
				this.showWarningNotification("Lançar Notas", "É necessário salvar a banca antes de lançar as notas.");
			} else if(appraiser.isSubstitute()) {
				this.showWarningNotification("Lançar Notas", "A nota somente pode ser atribuída por membros titulares da banca.");
			} else if(appraiser.isChair() && !this.jury.isSupervisorAssignsGrades()) {
				this.showWarningNotification("Lançar Notas", "O presidente da banca não atribui nota ao acadêmico.");
			} else {
				UI.getCurrent().addWindow(new EditJuryAppraiserScoreWindow(appraiser));
			}
		}
	}
	
	private void downloadProfessorStatement(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Gerar Declaração", "Selecione o membro para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report = bo.getJuryProfessorStatement(this.jury.getAppraisers().get(index).getIdJuryAppraiser());
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadStudentStatement(){
		int index = this.getParticipantSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Gerar Declaração", "Selecione o acadêmico para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report = bo.getJuryStudentStatement(this.jury.getParticipants().get(index).getIdJuryStudent());
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadParticipants(){
		try{
			JuryBO bo = new JuryBO();
			
			this.showReport(bo.getJuryParticipantsSignature(this.jury.getIdJury()));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Imprimir Lista de Presença", e.getMessage());
		}
	}
	
	private void downloadParticipantsReport(){
		try{
			JuryBO bo = new JuryBO();
			
			this.showReport(bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.jury.getIdJury(), 0, 0, true));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Imprimir Lista de Acadêmicos Ouvintes", e.getMessage());
		}
	}
	
	private int getAppraiserSelectedIndex(){
    	Object itemId = this.gridAppraisers.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }
	
	private int getParticipantSelectedIndex(){
    	Object itemId = this.gridParticipants.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }

}
