package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.BugReportBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditBugReportWindow extends EditWindow {
	
	private final BugReport bug;
	
	private final TextField textUser;
	private final TextField textTitle;
	private final Select<SystemModule> comboModule;
	private final TextArea textDescription;
	private final DatePicker textReportDate;
	private final Select<BugType> comboType;
	private final Select<BugStatus> comboStatus;
	private final DatePicker textStatusDate;
	private final TextArea textStatus;
	private final Tabs tab;

	public EditBugReportWindow(BugReport bug, ListView parentView){
		super("Editar Bug", parentView);
		
		if(bug == null){
			this.bug = new BugReport();
			this.bug.setUser(Session.getUser());
		}else{
			this.bug = bug;
		}
		
		this.textUser = new TextField("Usuário");
		this.textUser.setWidth("800px");
		this.textUser.setEnabled(false);
		this.textUser.setRequired(true);
		
		this.textTitle = new TextField("Descrição Resumida");
		this.textTitle.setWidth("800px");
		this.textTitle.setRequired(true);
		
		this.comboModule = new Select<SystemModule>();
		this.comboModule.setLabel("Módulo");
		this.comboModule.setWidth("800px");
		this.comboModule.setItems(SystemModule.GENERAL, SystemModule.SIGAC, SystemModule.SIGES, SystemModule.SIGET);
		this.comboModule.setValue(SystemModule.GENERAL);
		
		this.textDescription = new TextArea("Descrição Detalhada");
		this.textDescription.setWidth("800px");
		this.textDescription.setHeight("150px");
		this.textDescription.setRequired(true);
		
		this.comboType = new Select<BugType>();
		this.comboType.setLabel("Tipo");
		this.comboType.setWidth("150px");
		this.comboType.setItems(BugType.ERROR, BugType.SUGESTION);
		this.comboType.setValue(BugType.ERROR);
		
		this.textReportDate = new DatePicker("Data");
		//this.textReportDate.setDateFormat("dd/MM/yyyy");
		this.textReportDate.setEnabled(false);
		this.textReportDate.setRequired(true);
		
		this.comboStatus = new Select<BugStatus>();
		this.comboStatus.setLabel("Status");
		this.comboStatus.setWidth("200px");
		this.comboStatus.setItems(BugStatus.REPORTED, BugStatus.DEVELOPMENT, BugStatus.SOLVED, BugStatus.REFUSED);
		this.comboStatus.setValue(BugStatus.REPORTED);
		
		this.textStatusDate = new DatePicker("Data do Status");
		//this.textStatusDate.setDateFormat("dd/MM/yyyy");
		this.textStatusDate.setEnabled(false);
		
		this.textStatus = new TextArea("Descrição do Status");
		this.textStatus.setWidth("800px");
		this.textStatus.setHeight("330px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboType, this.textReportDate);
		h1.setSpacing(true);
		
		VerticalLayout tab1 = new VerticalLayout(this.textUser, this.textTitle, this.comboModule, this.textDescription, h1);
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboStatus, this.textStatusDate);
		h2.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(h2, this.textStatus);
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		
		Tab tabBug = new Tab("Informações");
		Tab tabFeedback = new Tab("Feedback");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tabBug, tab1);
		tabsToPages.put(tabFeedback, tab2);
		Div pages = new Div(tab1, tab2);
		
		this.tab = new Tabs(tabBug, tabFeedback);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tabBug);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("820px");
		layout.setHeight("530px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		if(this.bug.getIdBugReport() <= 0){
			tabFeedback.setVisible(false);
		}
		
		this.addField(layout);
		
		this.loadBug();
		
		this.textTitle.focus();
	}
	
	private void loadBug(){
		this.textUser.setValue(this.bug.getUser().getName());
		this.textTitle.setValue(this.bug.getTitle());
		this.comboModule.setValue(this.bug.getModule());
		this.textDescription.setValue(this.bug.getDescription());
		this.comboType.setValue(this.bug.getType());
		this.textReportDate.setValue(DateUtils.convertToLocalDate(this.bug.getReportDate()));
		this.comboStatus.setValue(this.bug.getStatus());
		this.textStatusDate.setValue(DateUtils.convertToLocalDate(this.bug.getStatusDate()));
		this.textStatus.setValue(this.bug.getStatusDescription());
		
		if((this.bug.getIdBugReport() == 0) || (!Session.isUserAdministrator())){
			this.comboStatus.setEnabled(false);
		}
		
		if(this.bug.getStatus() != BugStatus.REPORTED){
			this.textTitle.setEnabled(false);
			this.comboModule.setEnabled(false);
			this.textDescription.setEnabled(false);
			this.comboType.setEnabled(false);
		}
		
		if(!Session.isUserAdministrator()){
			this.textStatus.setEnabled(false);
			
			if(this.bug.getUser().getIdUser() != Session.getUser().getIdUser()){
				this.textTitle.setEnabled(false);
				this.comboModule.setEnabled(false);
				this.textDescription.setEnabled(false);
				this.comboType.setEnabled(false);
			}
		}
	}
	
	@Override
	public void save() {
		try{
			BugReportBO bo = new BugReportBO();
			
			this.bug.setTitle(this.textTitle.getValue());
			this.bug.setModule((SystemModule)this.comboModule.getValue());
			this.bug.setDescription(this.textDescription.getValue());
			this.bug.setType((BugType)this.comboType.getValue());
			this.bug.setStatus((BugStatus)this.comboStatus.getValue());
			this.bug.setStatusDescription(this.textStatus.getValue());
			
			bo.save(this.bug);
			
			this.showSuccessNotification("Reportar Bug", "Bug reportado com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Reportar Bug", e.getMessage());
		}
	}

}
