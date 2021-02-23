package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.ThemeSuggestionBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class EditThemeSuggestionWindow extends EditWindow {

	private final ThemeSuggestion theme;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final TextField textProponent;
	private final TextArea textObjetives;
	private final TextArea textProposal;
	private final Checkbox checkActive;
	private final DatePicker textSubmissionDate;
	
	private final Tabs tab;
	
	public EditThemeSuggestionWindow(ThemeSuggestion theme, ListView parentView){
		super("Editar Sugestão", parentView);
		
		if(theme == null){
			this.theme = new ThemeSuggestion();
			this.theme.setDepartment(Session.getSelectedDepartment().getDepartment());
			this.theme.setUser(Session.getUser());
		}else{
			this.theme = theme;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("810px");
		this.textTitle.setMaxLength(255);
		this.textTitle.setRequired(true);
		
		this.textProponent = new TextField("Proponente");
		this.textProponent.setWidth("810px");
		this.textProponent.setMaxLength(255);
		this.textProponent.setRequired(true);
		
		this.textObjetives = new TextArea();
		this.textObjetives.setWidth("810px");
		this.textObjetives.setHeight("280px");
		this.textObjetives.setVisible(false);
		
		this.textProposal = new TextArea();
		this.textProposal.setWidth("810px");
		this.textProposal.setHeight("280px");
		this.textProposal.setVisible(false);
		
		this.checkActive = new Checkbox("Ativo");
		
		this.textSubmissionDate = new DatePicker("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setRequired(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textSubmissionDate, this.checkActive);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		tab1.add(h1);
		tab1.add(this.textTitle);
		tab1.add(this.textProponent);
		tab1.add(h2);
		
		if(!Session.isUserProfessor() || (!Session.isUserManager(SystemModule.SIGET) && (Session.getUser().getIdUser() != this.theme.getUser().getIdUser()))){
			this.checkActive.setVisible(false);
			this.setSaveButtonEnabled(false);
			this.setTitle("Visualizar Sugestão");
		}
		
		Tab t1 = new Tab("Descrição");
		Tab t2 = new Tab("Proposta");
		Tab t3 = new Tab("Objetivos");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(t1, tab1);
		tabsToPages.put(t2, this.textProposal);
		tabsToPages.put(t3, this.textObjetives);
		Div pages = new Div(tab1, this.textProposal, this.textObjetives);
		
		this.tab = new Tabs(t1, t2, t3);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(t1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("820px");
		layout.setHeight("310px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		this.loadTheme();
	}
	
	private void loadTheme(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.theme.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.theme.getDepartment());
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textTitle.setValue(this.theme.getTitle());
		this.textProponent.setValue(this.theme.getProponent());
		this.textObjetives.setValue(this.theme.getObjectives());
		this.textProposal.setValue(this.theme.getProposal());
		this.textSubmissionDate.setValue(DateUtils.convertToLocalDate(this.theme.getSubmissionDate()));
		this.checkActive.setValue(this.theme.isActive());
	}
	
	@Override
	public void save() {
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
			
			this.theme.setTitle(this.textTitle.getValue());
			this.theme.setProponent(this.textProponent.getValue());
			this.theme.setProposal(this.textProposal.getValue());
			this.theme.setObjectives(this.textObjetives.getValue());
			this.theme.setSubmissionDate(DateUtils.convertToDate(this.textSubmissionDate.getValue()));
			this.theme.setActive(this.checkActive.getValue());
			
			bo.save(Session.getIdUserLog(), this.theme);
			
			this.showSuccessNotification("Salvar Sugestão", "Sugestão salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Sugestão", e.getMessage());
		}
	}

}
