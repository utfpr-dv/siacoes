package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.thomas.timefield.TimeField;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryStudentBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipJuryWindow extends EditWindow {
	
	private final InternshipJury jury;
	
	private final DateField textDate;
	private final TextField textLocal;
	private final HorizontalLayout layoutAppraisers;
	private final HorizontalLayout layoutParticipants;
	private Grid gridAppraisers;
	private Grid gridParticipants;
	private final Button buttonAddAppraiser;
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
	private final TextField textCompanySupervisorScore;
	private final TabSheet tabContainer;
	private final NativeSelect comboResult;
	
	public EditInternshipJuryWindow(InternshipJury jury, ListView parentView){
		super("Banca", parentView);
		
		if(jury == null){
			this.jury = new InternshipJury();
		}else{
			this.jury = jury;
		}
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("700px");
		this.tabContainer.setHeight("400px");
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("690px");
		this.textLocal.setMaxLength(100);
		
		this.textDate = new DateField("Data");
		this.textDate.setDateFormat("dd/MM/yyyy HH:mm");
		this.textDate.setResolution(Resolution.MINUTE);
		
		this.textCompanySupervisorScore = new TextField("Nota do Supervisor na Empresa");
		this.textCompanySupervisorScore.setWidth("100px");
		
		this.textStartTime = new TimeField("Horário Inicial");
		this.textStartTime.set24HourClock(true);
		
		this.textEndTime = new TimeField("Horário Final");
		this.textEndTime.set24HourClock(true);
		
		this.comboResult = new NativeSelect("Resultado Final");
		this.comboResult.setWidth("200px");
		this.comboResult.setNullSelectionAllowed(false);
		this.comboResult.addItem(JuryResult.NONE);
		this.comboResult.addItem(JuryResult.APPROVED);
		this.comboResult.addItem(JuryResult.APPROVEDWITHRESERVATIONS);
		this.comboResult.addItem(JuryResult.DISAPPROVED);
		this.comboResult.select(JuryResult.NONE);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("690px");
		this.textComments.setHeight("150px");
		this.textComments.addStyleName("textscroll");
		
		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSpacing(true);
		tab1.addComponent(this.textLocal);
		HorizontalLayout h1 = new HorizontalLayout(this.textDate, this.textStartTime, this.textEndTime);
		h1.setSpacing(true);
		tab1.addComponent(h1);
		HorizontalLayout h2 = new HorizontalLayout(this.textCompanySupervisorScore, this.comboResult);
		h2.setSpacing(true);
		tab1.addComponent(h2);
		tab1.addComponent(this.textComments);
		
		this.layoutAppraisers = new HorizontalLayout();
		
		this.buttonAddAppraiser = new Button("Adicionar Membro", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addAppraiser();
            }
        });
		
		this.buttonRemoveAppraiser = new Button("Remover Membro", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeAppraiser();
            }
        });
		
		this.buttonAppraiserScore = new Button("Lançar Notas", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addScore();
            }
        });
		
		this.buttonAppraiserStatement = new Button("Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement();
            }
        });
		
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
		
		HorizontalLayout layoutGridButtons = new HorizontalLayout(this.buttonAddAppraiser, this.buttonRemoveAppraiser, this.buttonAppraiserScore, this.buttonAppraiserStatement);
		layoutGridButtons.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.layoutAppraisers, layoutGridButtons);
		
		this.layoutParticipants = new HorizontalLayout();
		
		this.buttonAddParticipant = new Button("Adicionar Acadêmico", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addParticipant();            	
            }
        });
		
		this.buttonRemoveParticipant = new Button("Remover Acadêmico", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeParticipant();
            }
        });
		
		this.buttonParticipantStatement = new Button("Gerar Declaração", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadStudentStatement();
            }
        });
		
		HorizontalLayout layoutGridButtons2 = new HorizontalLayout(this.buttonAddParticipant, this.buttonRemoveParticipant, this.buttonParticipantStatement, this.buttonParticipants, this.buttonParticipantsReport);
		layoutGridButtons2.setSpacing(true);
		
		VerticalLayout tab3 = new VerticalLayout(this.layoutParticipants, layoutGridButtons2);
		
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
		this.textCompanySupervisorScore.setValue(String.format("%.2f", this.jury.getCompanySupervisorScore()));
		this.comboResult.setValue(this.jury.getResult());
		
		if(this.jury.getIdInternshipJury() == 0){
			InternshipJuryAppraiser appraiser = new InternshipJuryAppraiser();
			appraiser.setAppraiser(this.jury.getSupervisor());
			
			this.jury.setAppraisers(new ArrayList<InternshipJuryAppraiser>());
			this.jury.getAppraisers().add(appraiser);
			
			this.jury.setParticipants(new ArrayList<InternshipJuryStudent>());
		}else {
			if(this.jury.getAppraisers() == null){
				try {
					InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
					
					this.jury.setAppraisers(bo.listAppraisers(this.jury.getIdInternshipJury()));
				} catch (Exception e) {
					this.jury.setAppraisers(null);
					
					Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
					
					Notification.show("Carregar Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
			
			if(this.jury.getParticipants() == null){
				try{
					InternshipJuryStudentBO bo = new InternshipJuryStudentBO();
					
					this.jury.setParticipants(bo.listByJury(this.jury.getIdInternshipJury()));
				} catch (Exception e) {
					this.jury.setParticipants(null);
					
					Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
					
					Notification.show("Carregar Participantes", e.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridParticipants();
	}
	
	private void loadGridAppraisers(){
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Membro", String.class);
		this.gridAppraisers.addColumn("Nome", String.class);
		this.gridAppraisers.setWidth("690px");
		this.gridAppraisers.setHeight("300px");
		
		if(this.jury.getAppraisers() != null){
			User supervisor = this.jury.getSupervisor();
			int member = 1;
			
			for(InternshipJuryAppraiser appraiser : this.jury.getAppraisers()){
				if(appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()){
					this.gridAppraisers.addRow("Orientador", appraiser.getAppraiser().getName());
				}else{
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
		this.gridParticipants.setWidth("690px");
		this.gridParticipants.setHeight("300px");
		
		if(this.jury.getAppraisers() != null){
			for(InternshipJuryStudent student : this.jury.getParticipants()){
				this.gridParticipants.addRow(student.getStudent().getStudentCode(), student.getStudent().getName());
			}
		}
		
		this.layoutParticipants.removeAllComponents();
		this.layoutParticipants.addComponent(this.gridParticipants);
	}
	
	@Override
	public void save() {
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			
			this.jury.setLocal(this.textLocal.getValue());
			this.jury.setComments(this.textComments.getValue());
			this.jury.setStartTime(this.textStartTime.getValue());
			this.jury.setEndTime(this.textEndTime.getValue());
			this.jury.setDate(this.textDate.getValue());
			this.jury.setCompanySupervisorScore(Double.parseDouble(this.textCompanySupervisorScore.getValue().replace(",", ".")));
			this.jury.setResult((JuryResult)this.comboResult.getValue());
			
			bo.save(this.jury);
			
			Notification.show("Salvar Banca", "Banca salva com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void addAppraiser(){
		UI.getCurrent().addWindow(new EditInternshipJuryAppraiserWindow(this));
	}
	
	public void addAppraiser(User appraiser) throws Exception{
		InternshipJuryBO bo = new InternshipJuryBO();
		
		if(bo.canAddAppraiser(this.jury, appraiser)){
			InternshipJuryAppraiser ja = new InternshipJuryAppraiser();
			
			ja.setAppraiser(appraiser);
			
			this.jury.getAppraisers().add(ja);
			
			this.loadGridAppraisers();
		}
	}
	
	private void addParticipant(){
		UI.getCurrent().addWindow(new EditInternshipJuryParticipantWindow(this));
	}
	
	public void addParticipant(User student) throws Exception{
		InternshipJuryStudent js = new InternshipJuryStudent();
		
		js.setStudent(student);
		
		this.jury.getParticipants().add(js);
		
		this.loadGridParticipants();
	}
	
	private void removeAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Membro", "Selecione o membro para remover.", Notification.Type.WARNING_MESSAGE);
		}else{
			try {
				InternshipJuryBO bo = new InternshipJuryBO();
				InternshipJuryAppraiser appraiser = this.jury.getAppraisers().get(index);
				
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
				
				Notification.show("Remover Membro", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void removeParticipant(){
		int index = this.getParticipantSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Participante", "Selecione o participante para remover.", Notification.Type.WARNING_MESSAGE);
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
				
				Notification.show("Remover Participante", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void addScore(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Membro", "Selecione o membro para lançar as notas.", Notification.Type.WARNING_MESSAGE);
		}else{
			InternshipJuryAppraiser appraiser = this.jury.getAppraisers().get(index);
			
			if((appraiser == null) || (appraiser.getIdInternshipJuryAppraiser() == 0)){
				Notification.show("Lançar Notas", "É necessário salvar a banca antes de lançar as notas.", Notification.Type.WARNING_MESSAGE);
			}else{
				UI.getCurrent().addWindow(new EditInternshipJuryAppraiserScoreWindow(appraiser));	
			}
		}
	}
	
	private void downloadProfessorStatement(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			Notification.show("Gerar Declaração", "Selecione o membro para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report = bo.getInternshipJuryProfessorStatement(this.jury.getAppraisers().get(index).getIdInternshipJuryAppraiser());
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadStudentStatement(){
		int index = this.getParticipantSelectedIndex();
		
		if(index == -1){
			Notification.show("Gerar Declaração", "Selecione o acadêmico para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();
				byte[] report = bo.getInternshipJuryStudentStatement(this.jury.getParticipants().get(index).getIdInternshipJuryStudent());
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void downloadParticipants(){
		try{
			InternshipJuryBO bo = new InternshipJuryBO();
			
			this.showReport(bo.getJuryParticipantsSignature(this.jury.getIdInternshipJury()));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	Notification.show("Imprimir Lista de Presença", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void downloadParticipantsReport(){
		try{
			InternshipJuryBO bo = new InternshipJuryBO();
			
			this.showReport(bo.getJuryStudentReport(Session.getUser().getDepartment().getIdDepartment(), this.jury.getIdInternshipJury(), null, null, true));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	Notification.show("Imprimir Lista de Acadêmicos Ouvintes", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
