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
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryAppraiserDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditInternshipJuryRequestWindow extends EditWindow {
	
	private final InternshipJuryRequest request;
	private final List<InternshipJuryAppraiserRequest> members;
	private final List<InternshipJuryAppraiserRequest> substitutes;
	private SigesConfig config;
	
	private final Tabs tabContainer;
	private final DateTimePicker textDate;
	private final TextField textLocal;
	private final TextField textStudent;
	private final TextField textCompany;
	private final TextField textSupervisor;
	private Grid<JuryAppraiserDataSource> gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonRemoveAppraiser;
	private final Button buttonAppraiserSchedule;
	private Grid<JuryAppraiserDataSource> gridSubstitutes;
	private final Button buttonAddSubstitute;
	private final Button buttonRemoveSubstitute;
	private final Button buttonSubstituteSchedule;
	
	public EditInternshipJuryRequestWindow(InternshipJuryRequest request, ListView parentView) {
		this(request, true, parentView);
	}
	
	public EditInternshipJuryRequestWindow(InternshipJuryRequest request, boolean allowEdit, ListView parentView) {
		super("Requisição de Banca", parentView);
		
		if(request == null) {
			this.request = new InternshipJuryRequest();
		} else {
			this.request = request;
		}
		this.members = new ArrayList<InternshipJuryAppraiserRequest>();
		this.substitutes = new ArrayList<InternshipJuryAppraiserRequest>();
		try {
			if(this.request.getIdInternshipJuryRequest() == 0) {
				this.config = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} else {
				this.config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(this.request.getInternship().getIdInternship()));
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigesConfig();
		}
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("400px");
		this.textLocal.setMaxLength(100);
		this.textLocal.setRequired(true);
		
		this.textDate = new DateTimePicker("Data");
		
		this.textStudent = new TextField("Acadêmico(a)");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("800px");
		this.textCompany.setEnabled(false);
		
		this.textSupervisor = new TextField("Professor(a) Orientador(a)");
		this.textSupervisor.setWidth("800px");
		this.textSupervisor.setEnabled(false);
		
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
		h2.setHeight("150px");
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
		h3.setHeight("150px");
		h3.setWidth("100%");
		Details panelSubstitutes = new Details();
		panelSubstitutes.setSummaryText("Suplentes");
		panelSubstitutes.setOpened(true);
		panelSubstitutes.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelSubstitutes.getElement().getStyle().set("width", "800px");
		panelSubstitutes.setContent(h3);
		
		HorizontalLayout h4 = new HorizontalLayout(this.textDate, this.textLocal);
		h4.setSpacing(true);
		h4.setMargin(false);
		h4.setPadding(false);
		
		VerticalLayout tab1 = new VerticalLayout(this.textStudent, this.textCompany, this.textSupervisor, h4);
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		VerticalLayout tab2 = new VerticalLayout(panelAppraisers, panelSubstitutes);
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
		layout.setHeight("470px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		if(allowEdit && this.config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
		}
		if(!allowEdit) {
			this.disableButtons();
		}
		
		this.loadRequest();
	}
	
	private void loadRequest() {
		this.textDate.setValue(DateUtils.convertToLocalDateTime(this.request.getDate()));
		this.textLocal.setValue(this.request.getLocal());
		this.textStudent.setValue(this.request.getInternship().getStudent().getName());
		this.textCompany.setValue(this.request.getInternship().getCompany().getName());
		this.textSupervisor.setValue(this.request.getInternship().getSupervisor().getName());
		
		if(this.request.getAppraisers() == null) {
			try {
				this.request.setAppraisers(new InternshipJuryAppraiserRequestBO().listAppraisers(this.request.getIdInternshipJuryRequest()));
			} catch (Exception e) {
				this.request.setAppraisers(null);
				
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Banca", e.getMessage());
			}
		}
		
		for(InternshipJuryAppraiserRequest appraiser : this.request.getAppraisers()) {
			if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridSubstitutes();
		
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPJURYREQUEST, this.request.getIdInternshipJuryRequest())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
		
		if(this.request.isConfirmed()) {
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
		if(this.request.getIdInternshipJuryRequest() == 0) {
			this.showWarningNotification("Assinar Requisição", "É necessário salvar a requisição antes de assinar.");
		} else {
			try {
				InternshipJuryFormReport report = new InternshipJuryRequestBO().getInternshipJuryRequestFormReport(this.request.getIdInternshipJuryRequest());
				
				SignatureWindow window = new SignatureWindow(DocumentType.INTERNSHIPJURYREQUEST, this.request.getIdInternshipJuryRequest(), SignDatasetBuilder.buildRequest(report), SignDatasetBuilder.getSignaturesList(report), this, null);
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Requisição", e.getMessage());
			}
		}
	}
	
	@Override
	public void save() {
		try {
			this.request.setLocal(this.textLocal.getValue());
			this.request.setDate(DateUtils.convertToDate(this.textDate.getValue()));
			
			new InternshipJuryRequestBO().save(Session.getIdUserLog(), this.request);
			
			this.showSuccessNotification("Salvar Solicitação de Banca", "Solicitação de banca salva com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if(((Session.getUser().getIdUser() == this.request.getInternship().getStudent().getIdUser()) || (Session.getUser().getIdUser() == this.request.getInternship().getSupervisor().getIdUser())) && this.config.isUseDigitalSignature()) {
				this.sign();
			} else {
				this.showReport(new InternshipJuryRequestBO().getInternshipJuryRequestForm(this.request.getIdInternshipJuryRequest()));
				
				this.close();	
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Solicitação de Banca", e.getMessage());
		}
	}

	private void loadGridAppraisers() {
		this.gridAppraisers.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.request.getAppraisers() != null) {
			this.gridAppraisers.setItems(JuryAppraiserDataSource.loadInternshipJuryRequest(this.request.getAppraisers(), false, true, false));
		}
	}
	
	private void loadGridSubstitutes() {
		this.gridSubstitutes.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.request.getAppraisers() != null) {
			this.gridSubstitutes.setItems(JuryAppraiserDataSource.loadInternshipJuryRequest(this.request.getAppraisers(), false, false, true));
		}
	}
	
	private void addAppraiser() {
		EditInternshipJuryAppraiserWindow window = new EditInternshipJuryAppraiserWindow(this, false);
		window.open();
	}
	
	public void addAppraiser(InternshipJuryAppraiserRequest appraiser) throws Exception {
		InternshipJuryRequestBO bo = new InternshipJuryRequestBO();
		
		if(bo.canAddAppraiser(this.request, appraiser.getAppraiser())) {
			this.request.getAppraisers().add(appraiser);
			
			if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
			
			this.loadGridAppraisers();
			this.loadGridSubstitutes();
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
		    		for(int i = 0; i < this.request.getAppraisers().size(); i++) {
                		if(this.request.getAppraisers().get(i).getAppraiser().getIdUser() == this.members.get(index).getAppraiser().getIdUser()) {
                			this.request.getAppraisers().remove(i);
                			break;
                		}
                	}
                	
                	this.members.remove(index);
                	loadGridAppraisers();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
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
		EditInternshipJuryAppraiserWindow window = new EditInternshipJuryAppraiserWindow(this, true);
		window.open();
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
		    		for(int i = 0; i < this.request.getAppraisers().size(); i++) {
                		if(this.request.getAppraisers().get(i).getAppraiser().getIdUser() == this.substitutes.get(index).getAppraiser().getIdUser()) {
                			this.request.getAppraisers().remove(i);
                			break;
                		}
                	}
                	
                	this.substitutes.remove(index);
                	loadGridSubstitutes();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
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
