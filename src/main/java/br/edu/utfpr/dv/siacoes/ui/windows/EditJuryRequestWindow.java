package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
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

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryAppraiserDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;

public class EditJuryRequestWindow extends EditWindow {
	
	private final JuryRequest jury;
	private final List<JuryAppraiserRequest> members;
	private final List<JuryAppraiserRequest> substitutes;
	private SigetConfig config;
	
	private final Tabs tabContainer;
	private final TextField textStudent;
	private final TextField textTitle;
	private final StageComboBox comboStage;
	private final DateTimePicker textDate;
	private final TextField textLocal;
	private final TextArea textComments;
	private final TextArea textSupervisorAbsenceReason;
	private final SupervisorComboBox comboChair;
	private final Grid<JuryAppraiserDataSource> gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonRemoveAppraiser;
	private final Button buttonAppraiserSchedule;
	private final Grid<JuryAppraiserDataSource> gridSubstitutes;
	private final Button buttonAddSubstitute;
	private final Button buttonRemoveSubstitute;
	private final Button buttonSubstituteSchedule;
	
	public EditJuryRequestWindow(JuryRequest jury,  ListView parentView) {
		this(jury, true, parentView);
	}
	
	public EditJuryRequestWindow(JuryRequest jury, boolean allowEdit, ListView parentView) {
		super("Requisição de Banca", parentView);
		
		if(jury == null){
			this.jury = new JuryRequest();
		}else{
			this.jury = jury;
		}
		this.members = new ArrayList<JuryAppraiserRequest>();
		this.substitutes = new ArrayList<JuryAppraiserRequest>();
		try {
			if(this.jury.getIdJuryRequest() == 0) {
				this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} else {
				this.config = new SigetConfigBO().findByDepartment(new JuryRequestBO().findIdDepartment(this.jury.getIdJuryRequest()));
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
		}
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		this.textStudent.setRequired(true);
		
		this.textTitle = new TextField("Título do Trabalho");
		this.textTitle.setWidth("800px");
		this.textTitle.setEnabled(false);
		this.textTitle.setRequired(true);
		
		this.comboStage = new StageComboBox();
		this.comboStage.setEnabled(false);
		
		this.textDate = new DateTimePicker("Data");
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("800px");
		this.textLocal.setMaxLength(100);
		this.textLocal.setRequired(true);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("150px");
		
		this.textSupervisorAbsenceReason = new TextArea("Motivo da ausência do Professor Orientador na banca");
		this.textSupervisorAbsenceReason.setWidth("800px");
		this.textSupervisorAbsenceReason.setHeight("75px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboStage, this.textDate);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		VerticalLayout tab1 = new VerticalLayout(this.textStudent, this.textTitle, h1, this.textLocal, this.textComments);
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		this.comboChair = new SupervisorComboBox("Presidente da Banca", Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.config.getSupervisorFilter());
		this.comboChair.setWidth("800px");
		this.comboChair.setRequired(true);
		
		this.gridAppraisers = new Grid<JuryAppraiserDataSource>();
    	this.gridAppraisers.setSelectionMode(SelectionMode.SINGLE);
		this.gridAppraisers.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridAppraisers.addColumn(JuryAppraiserDataSource::getMember).setHeader("Membro");
		this.gridAppraisers.addColumn(JuryAppraiserDataSource::getAppraiser).setHeader("Nome");
		this.gridAppraisers.setSizeFull();
		
		this.buttonAddAppraiser = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addAppraiser();
        });
		this.buttonAddAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddAppraiser.setWidth("150px");
		
		this.buttonAppraiserSchedule = new Button("Agenda", new Icon(VaadinIcon.CALENDAR_O), event -> {
            appraiserSchedule();
        });
		this.buttonAppraiserSchedule.setWidth("150px");
		
		this.buttonRemoveAppraiser = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeAppraiser();
        });
		this.buttonRemoveAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveAppraiser.setWidth("150px");
		
		VerticalLayout v1 = new VerticalLayout(this.buttonAddAppraiser, this.buttonAppraiserSchedule, this.buttonRemoveAppraiser);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		v1.setWidth("150px");
		v1.setVisible(allowEdit);
		HorizontalLayout h2 = new HorizontalLayout(this.gridAppraisers, v1);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		h2.expand(this.gridAppraisers);
		h2.setHeight("135px");
		h2.setWidth("100%");
		Details panelAppraisers = new Details();
		panelAppraisers.setSummaryText("Membros Titulares");
		panelAppraisers.setOpened(true);
		panelAppraisers.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelAppraisers.getElement().getStyle().set("width", "800px");
		panelAppraisers.setContent(h2);
		
		this.gridSubstitutes = new Grid<JuryAppraiserDataSource>();
    	this.gridSubstitutes.setSelectionMode(SelectionMode.SINGLE);
		this.gridSubstitutes.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSubstitutes.addColumn(JuryAppraiserDataSource::getMember).setHeader("Suplente");
		this.gridSubstitutes.addColumn(JuryAppraiserDataSource::getAppraiser).setHeader("Nome");
		this.gridSubstitutes.setSizeFull();
		
		this.buttonAddSubstitute = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addSubstitute();
        });
		this.buttonAddSubstitute.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddSubstitute.setWidth("150px");
		
		this.buttonSubstituteSchedule = new Button("Agenda", new Icon(VaadinIcon.CALENDAR_O), event -> {
            substituteSchedule();
        });
		this.buttonSubstituteSchedule.setWidth("150px");
		
		this.buttonRemoveSubstitute = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeSubstitute();
        });
		this.buttonRemoveSubstitute.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveSubstitute.setWidth("150px");
		
		VerticalLayout v2 = new VerticalLayout(this.buttonAddSubstitute, this.buttonSubstituteSchedule, this.buttonRemoveSubstitute);
		v2.setSpacing(false);
		v2.setMargin(false);
		v2.setPadding(false);
		v2.setWidth("150px");
		v2.setVisible(allowEdit);
		HorizontalLayout h3 = new HorizontalLayout(this.gridSubstitutes, v2);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		h3.expand(this.gridSubstitutes);
		h3.setHeight("135px");
		h3.setWidth("100%");
		Details panelSubstitutes = new Details();
		panelSubstitutes.setSummaryText("Suplentes");
		panelSubstitutes.setOpened(true);
		panelSubstitutes.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelSubstitutes.getElement().getStyle().set("width", "800px");
		panelSubstitutes.setContent(h3);
		
		VerticalLayout tab2 = new VerticalLayout(this.comboChair, panelAppraisers, panelSubstitutes, this.textSupervisorAbsenceReason);
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		
		Tab t1 = new Tab("Informações da Banca");
		Tab t2 = new Tab("Membros");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(t1, tab1);
		tabsToPages.put(t2, tab2);
		Div pages = new Div(tab1, tab2);
		
		this.tabContainer = new Tabs(t1, t2);
		this.tabContainer.setWidthFull();
		this.tabContainer.setFlexGrowForEnclosedTabs(1);
		
		this.tabContainer.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tabContainer.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tabContainer.setSelectedTab(t1);
		
		VerticalLayout layout = new VerticalLayout(this.tabContainer, pages);
		layout.setWidth("820px");
		layout.setHeight("600px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		if(allowEdit && config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
		}
		if(!allowEdit) {
			this.disableButtons();
		}
		
		this.loadJury();
	}
	
	private void loadJury() {
		this.textStudent.setValue(this.jury.getStudent());
		this.textTitle.setValue(this.jury.getTitle());
		this.textDate.setValue(DateUtils.convertToLocalDateTime(this.jury.getDate()));
		this.textLocal.setValue(this.jury.getLocal());
		this.textComments.setValue(this.jury.getComments());
		this.comboStage.setStage(this.jury.getStage());
		this.textSupervisorAbsenceReason.setValue(this.jury.getSupervisorAbsenceReason());
		
		if(this.jury.getAppraisers() == null){
			try {
				JuryAppraiserRequestBO bo = new JuryAppraiserRequestBO();
				
				this.jury.setAppraisers(bo.listAppraisers(this.jury.getIdJuryRequest()));
			} catch (Exception e) {
				this.jury.setAppraisers(null);
				
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Banca", e.getMessage());
			}
		}
		
		for(JuryAppraiserRequest appraiser : this.jury.getAppraisers()) {
			if(appraiser.isChair()) {
				this.comboChair.setProfessor(appraiser.getAppraiser());
			} else if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridSubstitutes();
		
		try {
			if(Document.hasSignature(DocumentType.JURYREQUEST, this.jury.getIdJuryRequest())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
		
		if(this.jury.isConfirmed()) {
			this.disableButtons();
		}
	}
	
	@Override
	public void disableButtons() {
		super.disableButtons();
		this.buttonAddAppraiser.setEnabled(false);
		this.buttonRemoveAppraiser.setEnabled(false);
		this.buttonAddSubstitute.setEnabled(false);
		this.buttonRemoveSubstitute.setEnabled(false);
	}
	
	@Override
	public void sign() {
		if(this.jury.getIdJuryRequest() == 0) {
			this.showWarningNotification("Assinar Requisição", "É necessário salvar a requisição antes de assinar.");
		} else {
			try {
				JuryFormReport report = new JuryRequestBO().getJuryRequestFormReport(this.jury.getIdJuryRequest());
				
				SignatureWindow window = new SignatureWindow(DocumentType.JURYREQUEST, this.jury.getIdJuryRequest(), SignDatasetBuilder.build(report), SignDatasetBuilder.getSignaturesList(report), this, null);
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Requisição", e.getMessage());
			}
		}
	}

	@Override
	public void save() {
		try{
			JuryRequestBO bo = new JuryRequestBO();
			boolean foundChair = false;
			
			this.jury.setLocal(this.textLocal.getValue());
			this.jury.setComments(this.textComments.getValue());
			this.jury.setDate(DateUtils.convertToDate(this.textDate.getValue()));
			this.jury.setSupervisorAbsenceReason(this.textSupervisorAbsenceReason.getValue());
			
			for(int i = 0; i < this.jury.getAppraisers().size(); i++) {
				if(this.jury.getAppraisers().get(i).isChair()) {
					this.jury.getAppraisers().get(i).setAppraiser(this.comboChair.getProfessor());
					foundChair = true;
				}
			}
			
			if(!foundChair) {
				JuryAppraiserRequest chair = new JuryAppraiserRequest();
				
				chair.setAppraiser(this.comboChair.getProfessor());
				chair.setChair(true);
				chair.setSubstitute(false);
				
				this.jury.getAppraisers().add(chair);
			}
			
			bo.save(Session.getIdUserLog(), this.jury);
			
			this.showSuccessNotification("Salvar Agendamento de Banca", "Agendamento de banca salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if(this.config.isUseDigitalSignature()) {
				this.sign();
			} else {
				this.showReport(bo.getJuryRequestForm(this.jury.getIdJuryRequest()));
				
				this.close();	
			}
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Agendamento de Banca", e.getMessage());
		}
	}
	
	private void loadGridAppraisers() {
		this.gridAppraisers.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.jury.getAppraisers() != null) {
			this.gridAppraisers.setItems(JuryAppraiserDataSource.loadJuryRequest(this.jury.getAppraisers(), false, true, false));
		}
	}
	
	private void loadGridSubstitutes() {
		this.gridSubstitutes.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.jury.getAppraisers() != null) {
			this.gridSubstitutes.setItems(JuryAppraiserDataSource.loadJuryRequest(this.jury.getAppraisers(), false, false, true));
		}
	}
	
	private void addAppraiser() {
		EditJuryAppraiserWindow window = new EditJuryAppraiserWindow(this, false);
		window.open();
	}
	
	public void addAppraiser(JuryAppraiserRequest appraiser) throws Exception {
		JuryRequestBO bo = new JuryRequestBO();
		
		if(bo.canAddAppraiser(this.jury, appraiser.getAppraiser())) {
			this.jury.getAppraisers().add(appraiser);
			
			if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
			
			this.loadGridAppraisers();
			this.loadGridSubstitutes();
		}
	}
	
	private void removeAppraiser() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para remover.");
		}else{
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Confirma a Exclusão?")
		    	.withMessage("Confirma a exclusão do membro da banca?")
		    	.withOkButton(() -> {
		    		for(int i = 0; i < jury.getAppraisers().size(); i++) {
                		if(jury.getAppraisers().get(i).getAppraiser().getIdUser() == members.get(index).getAppraiser().getIdUser()) {
                			jury.getAppraisers().remove(i);
                			break;
                		}
                	}
                	
                	members.remove(index);
                	loadGridAppraisers();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
		}
	}
	
	private void appraiserSchedule() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para visualizar a agenda.");
		}else{
			ProfessorScheculeWindow window = new ProfessorScheculeWindow(members.get(index).getAppraiser());
			window.open();
		}
	}
	
	private int getAppraiserSelectedIndex() {
		JuryAppraiserDataSource appraiser = this.gridAppraisers.asSingleSelect().getValue();

    	if(appraiser == null){
    		return -1;
    	}else{
    		for(int i = 0; i < this.members.size(); i++) {
    			if(this.members.get(i).getAppraiser().getIdUser() == appraiser.getIdUser()) {
    				return i;
    			}
    		}
    		
    		return -1;
    	}
    }
	
	private void addSubstitute() {
		EditJuryAppraiserWindow window = new EditJuryAppraiserWindow(this, true);
		window.open();
	}
	
	private void removeSubstitute() {
		int index = this.getSubstituteSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Suplente", "Selecione o suplente para remover.");
		}else{
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Confirma a Exclusão?")
		    	.withMessage("CConfirma a remoção do suplente?")
		    	.withOkButton(() -> {
		    		for(int i = 0; i < jury.getAppraisers().size(); i++) {
                		if(jury.getAppraisers().get(i).getAppraiser().getIdUser() == substitutes.get(index).getAppraiser().getIdUser()) {
                			jury.getAppraisers().remove(i);
                			break;
                		}
                	}
                	
                	substitutes.remove(index);
                	loadGridSubstitutes();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
		}
	}
	
	private void substituteSchedule() {
		int index = this.getSubstituteSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Suplente", "Selecione o suplente para visualizar a agenda.");
		}else{
			ProfessorScheculeWindow window = new ProfessorScheculeWindow(substitutes.get(index).getAppraiser());
			window.open();
		}
	}
	
	private int getSubstituteSelectedIndex() {
		JuryAppraiserDataSource appraiser = this.gridSubstitutes.asSingleSelect().getValue();

    	if(appraiser == null){
    		return -1;
    	}else{
    		for(int i = 0; i < this.substitutes.size(); i++) {
    			if(this.substitutes.get(i).getAppraiser().getIdUser() == appraiser.getIdUser()) {
    				return i;
    			}
    		}
    		
    		return -1;
    	}
    }

}
