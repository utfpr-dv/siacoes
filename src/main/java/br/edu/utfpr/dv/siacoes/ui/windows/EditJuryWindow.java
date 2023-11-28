package br.edu.utfpr.dv.siacoes.ui.windows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryStudentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryFormat;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.ui.components.JuryFormatComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryAppraiserDataSource;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryParticipantDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditJuryWindow extends EditWindow {

	private final Jury jury;
	
	private final JuryFormatComboBox comboJuryFormat;
	private final DateTimePicker textDate;
	private final TextField textLocal;
	private final Grid<JuryAppraiserDataSource> gridAppraisers;
	private final Grid<JuryParticipantDataSource> gridParticipants;
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
	private final TimePicker textStartTime;
	private final TimePicker textEndTime;
	private final TextField textSei;
	private final TextArea textComments;
	private final TextArea textSupervisorAbsenceReason;
	private final Tabs tabContainer;
	
	public EditJuryWindow(Jury jury, ListView parentView){
		super("Banca", parentView);
		
		if(jury == null){
			this.jury = new Jury();
		}else{
			this.jury = jury;
		}
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("640px");
		this.textLocal.setMaxLength(100);
		this.textLocal.setRequired(true);
		
		this.comboJuryFormat = new JuryFormatComboBox("Formato da Banca");
		this.comboJuryFormat.addValueChangeListener(event -> {
			changeFormat(event.getValue());
		});
		this.comboJuryFormat.setEnabled(false);
		
		this.textDate = new DateTimePicker("Data");
		this.textDate.setWidth("300px");
		
		this.textStartTime = new TimePicker("Horário Inicial");
		this.textStartTime.setWidth("150px");
		
		this.textEndTime = new TimePicker("Horário Final");
		this.textEndTime.setWidth("150px");
		
		this.textSei = new TextField("Processo no SEI");
		this.textSei.setWidth("200px");
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("900px");
		this.textComments.setHeight("150px");
		
		this.textSupervisorAbsenceReason = new TextArea("Motivo da ausência do Professor Orientador na banca");
		this.textSupervisorAbsenceReason.setWidth("900px");
		this.textSupervisorAbsenceReason.setHeight("75px");
		
		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		HorizontalLayout h2 = new HorizontalLayout(this.comboJuryFormat, this.textLocal);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		tab1.add(h2);
		HorizontalLayout h1 = new HorizontalLayout(this.textDate, this.textStartTime, this.textEndTime, this.textSei);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		tab1.add(h1);
		tab1.add(this.textComments);
		tab1.add(this.textSupervisorAbsenceReason);
		
		this.gridAppraisers = new Grid<JuryAppraiserDataSource>();
    	this.gridAppraisers.setSelectionMode(SelectionMode.SINGLE);
		this.gridAppraisers.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridAppraisers.addColumn(JuryAppraiserDataSource::getMember).setHeader("Membro");
		this.gridAppraisers.addColumn(JuryAppraiserDataSource::getAppraiser).setHeader("Nome");
		this.gridAppraisers.setWidth("900px");
		this.gridAppraisers.setHeight("370px");
		
		this.buttonAddAppraiser = new Button("Adicionar Membro", new Icon(VaadinIcon.PLUS), event -> {
            addAppraiser();
        });
		this.buttonAddAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		this.buttonEditAppraiser = new Button("Editar Membro", new Icon(VaadinIcon.EDIT), event -> {
            editAppraiser();
        });
		this.buttonEditAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.buttonRemoveAppraiser = new Button("Remover Membro", new Icon(VaadinIcon.TRASH), event -> {
            removeAppraiser();
        });
		this.buttonRemoveAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		
		this.buttonAppraiserScore = new Button("Lançar Notas", new Icon(VaadinIcon.CALC_BOOK), event -> {
            addScore();
        });
		
		this.buttonAppraiserStatement = new Button("Declaração", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadProfessorStatement();
        });
		
		this.buttonParticipants = new Button("Lista de Presença", event -> {
            downloadParticipants();
        });
		
		this.buttonParticipantsReport = new Button("Lista de Ouvintes", event -> {
            downloadParticipantsReport();
        });
		
		HorizontalLayout layoutGridButtons = new HorizontalLayout(this.buttonAddAppraiser, this.buttonEditAppraiser, this.buttonRemoveAppraiser, this.buttonAppraiserScore, this.buttonAppraiserStatement);
		layoutGridButtons.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.gridAppraisers, layoutGridButtons);
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		
		this.gridParticipants = new Grid<JuryParticipantDataSource>();
    	this.gridParticipants.setSelectionMode(SelectionMode.SINGLE);
		this.gridParticipants.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridParticipants.addColumn(JuryParticipantDataSource::getStudentCode).setHeader("R.A.");
		this.gridParticipants.addColumn(JuryParticipantDataSource::getName).setHeader("Nome");
		this.gridParticipants.setWidth("900px");
		this.gridParticipants.setHeight("370px");
		
		this.buttonAddParticipant = new Button("Adicionar Acadêmico", new Icon(VaadinIcon.PLUS), event -> {
            addParticipant();
        });
		this.buttonAddParticipant.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		this.buttonRemoveParticipant = new Button("Remover Acadêmico", new Icon(VaadinIcon.TRASH), event -> {
            removeParticipant();
        });
		this.buttonRemoveParticipant.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		
		this.buttonParticipantStatement = new Button("Gerar Declaração", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            downloadStudentStatement();
        });
		
		HorizontalLayout layoutGridButtons2 = new HorizontalLayout(this.buttonAddParticipant, this.buttonRemoveParticipant, this.buttonParticipantStatement, this.buttonParticipants, this.buttonParticipantsReport);
		layoutGridButtons2.setSpacing(true);
		layoutGridButtons2.setMargin(false);
		layoutGridButtons2.setPadding(false);
		
		VerticalLayout tab3 = new VerticalLayout(this.gridParticipants, layoutGridButtons2);
		tab3.setSpacing(false);
		tab3.setMargin(false);
		tab3.setPadding(false);
		tab3.setVisible(false);
		
		Tab t1 = new Tab("Dados da Banca");
		Tab t2 = new Tab("Membros");
		Tab t3 = new Tab("Ouvintes");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(t1, tab1);
		tabsToPages.put(t2, tab2);
		tabsToPages.put(t3, tab3);
		Div pages = new Div(tab1, tab2, tab3);
		
		this.tabContainer = new Tabs(t1, t2, t3);
		this.tabContainer.setWidthFull();
		this.tabContainer.setFlexGrowForEnclosedTabs(1);
		
		this.tabContainer.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tabContainer.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tabContainer.setSelectedTab(t1);
		
		VerticalLayout layout = new VerticalLayout(this.tabContainer, pages);
		layout.setWidth("920px");
		layout.setHeight("480px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		this.loadJury();
		this.textLocal.focus();
	}
	
	private void loadJury(){
		this.textDate.setValue(DateUtils.convertToLocalDateTime(this.jury.getDate()));
		this.textLocal.setValue(this.jury.getLocal());
		this.textStartTime.setValue(DateUtils.convertToLocalTime(this.jury.getStartTime()));
		this.textEndTime.setValue(DateUtils.convertToLocalTime(this.jury.getEndTime()));
		this.textComments.setValue(this.jury.getComments());
		this.textSupervisorAbsenceReason.setValue(this.jury.getSupervisorAbsenceReason());
		this.textSei.setValue(this.jury.getSei());
		this.comboJuryFormat.setFormat(this.jury.getFormat());
		
		this.changeFormat(this.jury.getFormat());
		
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
				
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Banca", e.getMessage());
			}
		}
		
		if(this.jury.getParticipants() == null){
			try{
				JuryStudentBO bo = new JuryStudentBO();
				
				this.jury.setParticipants(bo.listByJury(this.jury.getIdJury()));
			} catch (Exception e) {
				this.jury.setParticipants(null);
				
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
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
				
				if(!config.isUseSei() && this.jury.getSei().trim().isEmpty()) {
					this.textSei.setVisible(false);
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Agendamento de Banca", e.getMessage());
			}
		} else {
			try {
				if(Document.hasSignature(DocumentType.JURY, this.jury.getIdJury())) {
					this.disableButtons();
				}
			} catch (SQLException e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.disableButtons();
			}
		}
	}
	
	private void changeFormat(JuryFormat format) {
		if(format == JuryFormat.SYNC) {
			this.textLocal.setLabel("Local");
			this.textDate.setLabel("Data");
		} else {
			this.textLocal.setLabel("Link para o vídeo");
			this.textDate.setLabel("Disponível em");
		}
	}
	
	private void loadGridAppraisers(){
		this.gridAppraisers.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.jury.getAppraisers() != null) {
			this.gridAppraisers.setItems(JuryAppraiserDataSource.loadJury(this.jury.getAppraisers()));
		}
	}
	
	private void loadGridParticipants(){
		this.gridParticipants.setItems(new ArrayList<JuryParticipantDataSource>());
		
		if(this.jury.getParticipants() != null){
			this.gridParticipants.setItems(JuryParticipantDataSource.load(this.jury.getParticipants()));
		}
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
			this.jury.setStartTime(DateUtils.convertToDate(this.textStartTime.getValue()));
			this.jury.setEndTime(DateUtils.convertToDate(this.textEndTime.getValue()));
			this.jury.setDate(DateUtils.convertToDate(this.textDate.getValue()));
			this.jury.setSupervisorAbsenceReason(this.textSupervisorAbsenceReason.getValue());
			this.jury.setSei(this.textSei.getValue());
			this.jury.setFormat(this.comboJuryFormat.getFormat());
			
			bo.save(Session.getIdUserLog(), this.jury);
			
			this.showSuccessNotification("Salvar Banca", "Banca salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Banca", e.getMessage());
		}
	}
	
	private void addAppraiser(){
		EditJuryAppraiserWindow window = new EditJuryAppraiserWindow(this);
		window.open();
	}
	
	public void addAppraiser(JuryAppraiser appraiser) throws Exception{
		JuryBO bo = new JuryBO();
		
		if(bo.canAddAppraiser(this.jury, appraiser.getAppraiser())){
			this.jury.getAppraisers().add(appraiser);
			
			this.loadGridAppraisers();
		}
	}
	
	private void addParticipant(){
		EditJuryParticipantWindow window = new EditJuryParticipantWindow(this);
		window.open();
	}
	
	public void addParticipant(User student) throws Exception{
		JuryStudent js = new JuryStudent();
		
		js.setStudent(student);
		
		if(this.jury.getIdJury() > 0) {
			js.setJury(this.jury);
			
			try {
				int id = new JuryStudentBO().save(Session.getIdUserLog(), js);
				
				js.setIdJuryStudent(id);
				
				this.showSuccessNotification("Salvar Participante", "Participante salvo com sucesso.");
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Salvar Participante", e.getMessage());
			}
		}
		
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
					EditJuryAppraiserWindow window = new EditJuryAppraiserWindow(this.jury.getAppraisers().get(index), this);
					window.open();
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
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
					ConfirmDialog.createQuestion()
		    			.withIcon(new Icon(VaadinIcon.TRASH))
		    	    	.withCaption("Confirma a Remoção?")
		    	    	.withMessage("Confirma a remoção do membro da banca?")
		    	    	.withOkButton(() -> {
		    	    		this.jury.getAppraisers().remove(index);
	                    	loadGridAppraisers();
		    	    	}, ButtonOption.caption("Remover"), ButtonOption.icon(VaadinIcon.TRASH))
		    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	    	.open();
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
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
				ConfirmDialog.createQuestion()
	    			.withIcon(new Icon(VaadinIcon.TRASH))
	    	    	.withCaption("Confirma a Remoção?")
	    	    	.withMessage("Confirma a remoção do participante?")
	    	    	.withOkButton(() -> {
                    	if((this.jury.getIdJury() > 0) && (this.jury.getParticipants().get(index).getIdJuryStudent() > 0)) {
                    		try {
                    			new JuryStudentBO().delete(Session.getIdUserLog(), this.jury.getParticipants().get(index).getIdJuryStudent());
                				
                				this.showSuccessNotification("Remover Participante", "Participante removido com sucesso.");
                    		} catch (Exception e) {
                				Logger.log(Level.SEVERE, e.getMessage(), e);
                				
                				this.showErrorNotification("Remover Participante", e.getMessage());
                			}
                    	}
                    	
                    	this.jury.getParticipants().remove(index);
                    	loadGridParticipants();
	    	    	}, ButtonOption.caption("Remover"), ButtonOption.icon(VaadinIcon.TRASH))
	    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
	    	    	.open();
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
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
				EditJuryAppraiserScoreWindow window = new EditJuryAppraiserScoreWindow(appraiser);
				window.open();
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
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
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
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void downloadParticipants(){
		try{
			JuryBO bo = new JuryBO();
			
			this.showReport(bo.getJuryParticipantsSignature(this.jury.getIdJury()));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Imprimir Lista de Presença", e.getMessage());
		}
	}
	
	private void downloadParticipantsReport(){
		try{
			JuryBO bo = new JuryBO();
			
			this.showReport(bo.getJuryStudentReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.jury.getIdJury(), 0, 0, true));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Imprimir Lista de Acadêmicos Ouvintes", e.getMessage());
		}
	}
	
	private int getAppraiserSelectedIndex(){
		JuryAppraiserDataSource appraiser = this.gridAppraisers.asSingleSelect().getValue();
		
		if(appraiser == null) {
			return -1;
		} else {
			for(int i = 0; i < this.jury.getAppraisers().size(); i++) {
				if(this.jury.getAppraisers().get(i).getAppraiser().getIdUser() == appraiser.getIdUser()) {
					return i;
				}
			}
			
			return -1;
		}
    }
	
	private int getParticipantSelectedIndex(){
		JuryParticipantDataSource participant = this.gridParticipants.asSingleSelect().getValue();
		
		if(participant == null) {
			return -1;
		} else {
			for(int i = 0; i < this.jury.getParticipants().size(); i++) {
				if(this.jury.getParticipants().get(i).getStudent().getIdUser() == participant.getIdUser()) {
					return i;
				}
			}
			
			return -1;
		}
    }

}
