package br.edu.utfpr.dv.siacoes.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.MenuBar;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.window.EditUserWindow;

public class MainView extends CustomComponent implements View {
	
	public static final String NAME = "home";
	
	private final MenuBar menu;
	private final Button buttonSigac;
    private final Button buttonSiges;
    private final Button buttonSiget;
    private final Label label;
    
    public MainView(){
    	this.setCaption(SystemModule.GENERAL.getDescription());
    	
    	this.menu = new MenuBar(SystemModule.GENERAL);
    	
    	this.label = new Label("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setStyleName("Title");
    	
    	this.buttonSigac = new Button(null, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().getNavigator().navigateTo(SigacView.NAME);
            }
        });
    	this.buttonSigac.setIcon(new ThemeResource("images/sigac.png"));
    	this.buttonSigac.setWidth("350px");
    	this.buttonSigac.setHeight("220px");
    	
    	this.buttonSiges = new Button(null, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().getNavigator().navigateTo(SigesView.NAME);
            }
        });
    	this.buttonSiges.setIcon(new ThemeResource("images/siges.png"));
    	this.buttonSiges.setWidth("350px");
    	this.buttonSiges.setHeight("220px");
    	
    	this.buttonSiget = new Button(null, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().getNavigator().navigateTo(SigetView.NAME);
            }
        });
    	this.buttonSiget.setIcon(new ThemeResource("images/siget.png"));
    	this.buttonSiget.setWidth("350px");
    	this.buttonSiget.setHeight("220px");
    	
    	HorizontalLayout layoutButton = new HorizontalLayout(this.buttonSigac, this.buttonSiges, this.buttonSiget);
    	layoutButton.setSpacing(true);
    	layoutButton.setSizeFull();
    	layoutButton.setComponentAlignment(this.buttonSigac, Alignment.MIDDLE_CENTER);
    	layoutButton.setComponentAlignment(this.buttonSiges, Alignment.MIDDLE_CENTER);
    	layoutButton.setComponentAlignment(this.buttonSiget, Alignment.MIDDLE_CENTER);
    	
    	VerticalLayout layoutMain = new VerticalLayout(this.menu, this.label, layoutButton);
    	layoutMain.setSpacing(true);
    	layoutMain.setExpandRatio(layoutButton, 1);
    	layoutMain.setSizeFull();
    	
    	this.setSizeFull();
    	this.setCompositionRoot(layoutMain);
    }

	@Override
	public void enter(ViewChangeEvent event) {
		try{
			if((Session.getUser().getIdUser() != 0) && ((Session.getUser().getDepartment() == null) || (Session.getUser().getDepartment().getIdDepartment() == 0))){
				UI.getCurrent().addWindow(new EditUserWindow(Session.getUser(), null));
				
				Notification.show("Completar Cadastro", "Complete seu cadastro informando os dados necessários.", Notification.Type.WARNING_MESSAGE);
			}	
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
