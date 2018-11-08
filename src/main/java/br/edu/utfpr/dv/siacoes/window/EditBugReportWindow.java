package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.BugReportBO;
import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditBugReportWindow extends EditWindow {
	
	private final BugReport bug;
	
	private final TextField textUser;
	private final TextField textTitle;
	private final NativeSelect comboModule;
	private final TextArea textDescription;
	private final DateField textReportDate;
	private final NativeSelect comboType;
	private final NativeSelect comboStatus;
	private final DateField textStatusDate;
	private final TextArea textStatus;
	private final TabSheet tab;

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
		
		this.comboModule = new NativeSelect("Módulo");
		this.comboModule.setWidth("800px");
		this.comboModule.setNullSelectionAllowed(false);
		this.comboModule.addItem(SystemModule.GENERAL);
		this.comboModule.addItem(SystemModule.SIGAC);
		this.comboModule.addItem(SystemModule.SIGES);
		this.comboModule.addItem(SystemModule.SIGET);
		this.comboModule.select(SystemModule.GENERAL);
		this.comboModule.setRequired(true);
		
		this.textDescription = new TextArea("Descrição Detalhada");
		this.textDescription.setWidth("800px");
		this.textDescription.setHeight("150px");
		this.textDescription.setRequired(true);
		
		this.comboType = new NativeSelect("Tipo");
		this.comboType.setWidth("150px");
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.addItem(BugType.ERROR);
		this.comboType.addItem(BugType.SUGESTION);
		this.comboType.select(BugType.ERROR);
		this.comboType.setRequired(true);
		
		this.textReportDate = new DateField("Data");
		this.textReportDate.setDateFormat("dd/MM/yyyy");
		this.textReportDate.setEnabled(false);
		this.textReportDate.setRequired(true);
		
		this.comboStatus = new NativeSelect("Status");
		this.comboStatus.setWidth("200px");
		this.comboStatus.setNullSelectionAllowed(false);
		this.comboStatus.addItem(BugStatus.REPORTED);
		this.comboStatus.addItem(BugStatus.DEVELOPMENT);
		this.comboStatus.addItem(BugStatus.SOLVED);
		this.comboStatus.addItem(BugStatus.REFUSED);
		this.comboStatus.select(BugStatus.REPORTED);
		
		this.textStatusDate = new DateField("Data do Status");
		this.textStatusDate.setDateFormat("dd/MM/yyyy");
		this.textStatusDate.setEnabled(false);
		
		this.textStatus = new TextArea("Descrição do Status");
		this.textStatus.setWidth("800px");
		this.textStatus.setHeight("330px");
		
		this.tab = new TabSheet();
		this.tab.setWidth("810px");
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboType, this.textReportDate);
		h1.setSpacing(true);
		
		VerticalLayout tab1 = new VerticalLayout(this.textUser, this.textTitle, this.comboModule, this.textDescription, h1);
		tab1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboStatus, this.textStatusDate);
		h2.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(h2, this.textStatus);
		tab2.setSpacing(true);
		
		this.tab.addTab(tab1, "Informações");
		if(this.bug.getIdBugReport() > 0){
			this.tab.addTab(tab2, "Feedback");
		}
		
		this.addField(this.tab);
		
		this.loadBug();
		
		this.textTitle.focus();
	}
	
	private void loadBug(){
		this.textUser.setValue(this.bug.getUser().getName());
		this.textTitle.setValue(this.bug.getTitle());
		this.comboModule.select(this.bug.getModule());
		this.textDescription.setValue(this.bug.getDescription());
		this.comboType.select(this.bug.getType());
		this.textReportDate.setValue(this.bug.getReportDate());
		this.comboStatus.select(this.bug.getStatus());
		this.textStatusDate.setValue(this.bug.getStatusDate());
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Reportar Bug", e.getMessage());
		}
	}

}
