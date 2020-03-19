package br.edu.utfpr.dv.siacoes.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentSigesHistory;
import br.edu.utfpr.dv.siacoes.components.StudentSigetHistory;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class StudentHistoryView extends BasicView {
	
	public static final String NAME = "studenthistory";
	
	private final StudentComboBox comboStudent;
	private final Button buttonLoadHistory;
	private final TabSheet tabContainer;
	
	public StudentHistoryView() {
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setCaption("Histórico do Acadêmico");
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.buttonLoadHistory = new Button("Carregar Histórico", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	loadHistory();
            	buttonLoadHistory.setEnabled(true);
            }
        });
    	this.buttonLoadHistory.setIcon(FontAwesome.HISTORY);
    	this.buttonLoadHistory.setStyleName(ValoTheme.BUTTON_PRIMARY);
    	this.buttonLoadHistory.setWidth("200px");
    	this.buttonLoadHistory.setDisableOnClick(true);
    	
    	HorizontalLayout layoutFilter = new HorizontalLayout(this.comboStudent, this.buttonLoadHistory);
    	layoutFilter.setSpacing(true);
    	layoutFilter.setMargin(true);
    	layoutFilter.setComponentAlignment(this.buttonLoadHistory, Alignment.MIDDLE_LEFT);
    	
    	Panel panelFilter = new Panel("Filtro");
    	panelFilter.setContent(layoutFilter);
    	
    	this.tabContainer = new TabSheet();
    	this.tabContainer.setSizeFull();
    	this.tabContainer.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    	
    	Panel panelHistory = new Panel("Histórico");
    	panelHistory.setContent(this.tabContainer);
    	panelHistory.setSizeFull();
    	
    	VerticalLayout layout = new VerticalLayout(panelFilter, panelHistory);
    	layout.setExpandRatio(panelHistory, 1.0f);
    	layout.setSizeFull();
    	
    	this.setContent(layout);
	}
	
	private void loadHistory() {
		if((this.comboStudent.getStudent() == null) || (this.comboStudent.getStudent().getIdUser() == 0)) {
			this.showWarningNotification("Carregar Histórico", "Selecione o acadêmico parar carregar o histórico.");
		} else {
			int idStudent = this.comboStudent.getStudent().getIdUser();
			int idDepartment = Session.getSelectedDepartment().getDepartment().getIdDepartment();
			
			this.tabContainer.removeAllComponents();
			
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
			
			this.tabContainer.addTab(layout, "Estágio");
			
			if(this.getModule() == SystemModule.SIGES) {
				this.tabContainer.setSelectedTab(layout);
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Estágios", e.getMessage());
		}
	}
	
	private void loadSigetHistory(int idStudent, int idDepartment) {
		try {
			StudentSigetHistory layout = new StudentSigetHistory(idStudent, idDepartment);
			
			this.tabContainer.addTab(layout, "TCC");
			
			if(this.getModule() == SystemModule.SIGET) {
				this.tabContainer.setSelectedTab(layout);
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Estágios", e.getMessage());
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if((event.getParameters() != null) && (!event.getParameters().isEmpty())) {
			try {
				SystemModule module = SystemModule.valueOf(Integer.parseInt(event.getParameters()));
				
				this.setModule(module);
				
				if(!Session.isUserManager(this.getModule()) && !Session.isUserDepartmentManager()) {
					UI.getCurrent().getNavigator().navigateTo(Error403View.NAME);
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				UI.getCurrent().getNavigator().navigateTo(Error403View.NAME);
			}
		} else {
			UI.getCurrent().getNavigator().navigateTo(Error403View.NAME);
		}
	}

}
