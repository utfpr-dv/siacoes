package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StudentSigesHistory;
import br.edu.utfpr.dv.siacoes.ui.components.StudentSigetHistory;

@PageTitle("Histórico do Acadêmico")
@Route(value = "studenthistory", layout = MainLayout.class)
public class StudentHistoryView extends LoggedView implements HasUrlParameter<String> {
	
	private final StudentComboBox comboStudent;
	private final Button buttonLoadHistory;
	private final Tabs tabContainer;
	
	private final Map<Tab, Component> tabsToPages;
	private final Div pages;
	
	public StudentHistoryView() {
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.buttonLoadHistory = new Button("Carregar Histórico", new Icon(VaadinIcon.ARCHIVE));
		this.buttonLoadHistory.addClickListener(event -> {
			loadHistory();
            buttonLoadHistory.setEnabled(true);
		});
		this.buttonLoadHistory.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    	this.buttonLoadHistory.setWidth("200px");
    	this.buttonLoadHistory.setDisableOnClick(true);
    	
    	HorizontalLayout layoutFilter = new HorizontalLayout(this.comboStudent, this.buttonLoadHistory);
    	layoutFilter.setSpacing(true);
    	layoutFilter.setMargin(false);
    	layoutFilter.setPadding(false);
    	
    	Details panelFilter = new Details();
    	panelFilter.setSummaryText("Filtro");
    	panelFilter.setContent(layoutFilter);
    	panelFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelFilter.setOpened(true);
		
		this.tabsToPages = new HashMap<>();
		
		this.pages = new Div();
		this.pages.setSizeFull();
		
		this.tabContainer = new Tabs();
		this.tabContainer.setWidthFull();
		this.tabContainer.setFlexGrowForEnclosedTabs(1);
		
		this.tabContainer.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tabContainer.getSelectedTab());
		    if(selectedPage != null) {
		    	selectedPage.setVisible(true);	
		    }
		});
		
		VerticalLayout layoutTab = new VerticalLayout(this.tabContainer, pages);
		layoutTab.setSizeFull();
		layoutTab.setSpacing(false);
		layoutTab.setMargin(false);
		layoutTab.setPadding(false);
    	
		Details panelHistory = new Details();
		panelHistory.setSummaryText("Histórico");
    	panelHistory.setContent(layoutTab);
    	panelHistory.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	panelHistory.setOpened(true);
    	
    	VerticalLayout layout = new VerticalLayout(panelFilter, panelHistory);
    	layout.expand(panelHistory);
    	layout.setSizeFull();
    	layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
    	
    	this.setViewContent(layout);
	}
	
	private void loadHistory() {
		if((this.comboStudent.getStudent() == null) || (this.comboStudent.getStudent().getIdUser() == 0)) {
			this.showWarningNotification("Carregar Histórico", "Selecione o acadêmico parar carregar o histórico.");
		} else {
			int idStudent = this.comboStudent.getStudent().getIdUser();
			int idDepartment = Session.getSelectedDepartment().getDepartment().getIdDepartment();
			
			this.tabContainer.removeAll();
			this.pages.removeAll();
			this.tabsToPages.clear();
			
			if(Session.isUserManager(SystemModule.SIGES) || Session.isUserDepartmentManager()) {
				this.loadSigesHistory(idStudent, idDepartment);
			}
			
			if(Session.isUserManager(SystemModule.SIGET) || Session.isUserDepartmentManager()) {
				this.loadSigetHistory(idStudent, idDepartment);
			}
		}
	}
	
	private void loadSigesHistory(int idStudent, int idDepartment) {
		try {
			StudentSigesHistory layout = new StudentSigesHistory(idStudent, idDepartment);
			Tab tabSiges = new Tab("Estágio");
			
			this.tabContainer.add(tabSiges);
			this.pages.add(layout);
			this.tabsToPages.put(tabSiges, layout);
			
			if(this.getModule() == SystemModule.SIGES) {
				this.tabContainer.setSelectedTab(tabSiges);
			} else {
				layout.setVisible(false);
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Estágios", e.getMessage());
		}
	}
	
	private void loadSigetHistory(int idStudent, int idDepartment) {
		try {
			StudentSigetHistory layout = new StudentSigetHistory(idStudent, idDepartment);
			Tab tabSiget = new Tab("TCC");
			
			this.tabContainer.add(tabSiget);
			this.pages.add(layout);
			this.tabsToPages.put(tabSiget, layout);
			
			if(this.getModule() == SystemModule.SIGET) {
				this.tabContainer.setSelectedTab(tabSiget);
			} else {
				layout.setVisible(false);
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Estágios", e.getMessage());
		}
	}
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(parameter != null && !parameter.isEmpty()) {
			try {
				SystemModule module = SystemModule.valueOf(Integer.parseInt(parameter.trim()));
				
				this.setModule(module);
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				event.rerouteTo("403");
			}
		}
		
		if(!Session.isUserManager(this.getModule()) && !Session.isUserDepartmentManager()) {
			event.rerouteTo("403");
		}
	}

}
