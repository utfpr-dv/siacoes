package br.edu.utfpr.dv.siacoes.view;

import java.util.UUID;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.Notification;
import br.edu.utfpr.dv.siacoes.components.SideMenu;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public abstract class BasicView extends CustomComponent implements View {
	
	private final Label labelCaption;
	private final ComponentContainer content;
	private final SideMenu menu;
	
	private UserProfile profilePermissions;
    
    private SystemModule module;
	
	public BasicView(){
		VerticalLayout vertical = new VerticalLayout();
		
		vertical.setSizeFull();
		vertical.setSpacing(false);
		
		HorizontalLayout layoutCaption = new HorizontalLayout();
		
		layoutCaption.setStyleName(ValoTheme.MENU_ROOT);
		layoutCaption.setWidth("100%");
		layoutCaption.setHeight("30px");
		layoutCaption.setSpacing(true);
		
		Link linkHamburguer = new Link(null, null);
		linkHamburguer.setIcon(new ThemeResource("images/menu.png"));
		
		VerticalLayout layoutHamburguer = new VerticalLayout(linkHamburguer);
		layoutHamburguer.setHeight("30px");
		layoutHamburguer.setWidth("50px");
		layoutHamburguer.setComponentAlignment(linkHamburguer, Alignment.MIDDLE_CENTER);
		layoutHamburguer.addLayoutClickListener(new LayoutClickListener() {
			@Override
        	public void layoutClick(LayoutClickEvent event) {
				menu.toggleMenu();
            }
		});
		
		this.labelCaption = new Label();
		this.labelCaption.setStyleName("Caption");
		
		Link logoUTFPR = new Link(null, new ExternalResource("http://www.utfpr.edu.br"));
		logoUTFPR.setIcon(new ThemeResource("images/logo_UTFPR_24.png"));
		logoUTFPR.setTargetName("_blank");
		Link logoES = new Link(null, new ExternalResource("http://coens.dv.utfpr.edu.br"));
		logoES.setIcon(new ThemeResource("images/logo_ES_24.png"));
		logoES.setTargetName("_blank");
		
		layoutCaption.addComponent(layoutHamburguer);
		layoutCaption.addComponent(this.labelCaption);
		layoutCaption.addComponent(logoUTFPR);
		layoutCaption.addComponent(logoES);
		layoutCaption.setComponentAlignment(this.labelCaption, Alignment.MIDDLE_LEFT);
		layoutCaption.setComponentAlignment(logoUTFPR, Alignment.MIDDLE_RIGHT);
		layoutCaption.setComponentAlignment(logoES, Alignment.MIDDLE_RIGHT);
		layoutCaption.setExpandRatio(this.labelCaption, 1);
		
		vertical.addComponent(layoutCaption);
		
		HorizontalLayout horizontal = new HorizontalLayout();
		
		horizontal.setSizeFull();
		horizontal.addStyleName("mainview");
		horizontal.setSpacing(false);

		this.menu = new SideMenu();
		horizontal.addComponent(this.menu);

        this.content = new CssLayout();
        this.content.addStyleName("view-content");
        this.content.setSizeFull();
        horizontal.addComponent(this.content);
        horizontal.setExpandRatio(this.content, 1.0f);
        
        vertical.addComponent(horizontal);
        vertical.setExpandRatio(horizontal, 1.0f);
        
        this.setCompositionRoot(vertical);
        this.setSizeFull();
        
        this.setProfilePerimissions(UserProfile.STUDENT);
		this.setModule(SystemModule.GENERAL);
	}
	
	public void setCaption(String caption){
		super.setCaption(caption);
		
		this.labelCaption.setCaption(caption);
	}
	
	public void setContent(Component component){
		this.content.removeAllComponents();
		this.content.addComponent(component);
	}
	
	public void setOpenMenu(SystemModule module){
		this.menu.setOpenMenu(module);
	}
	
	public void setModule(SystemModule module){
    	if((this.getCaption() == null) || this.getCaption().trim().isEmpty()){
    		this.setCaption(module.getDescription());
    	}
    	
    	this.module = module;
    	this.setOpenMenu(module);
    }
    
    public SystemModule getModule(){
    	return this.module;
    }
    
    public void setProfilePerimissions(UserProfile profile){
    	this.profilePermissions = profile;
    }
    
    public UserProfile getProfilePermissions(){
    	return this.profilePermissions;
    }
    
    protected void showReport(byte[] pdfReport){
    	if(pdfReport == null) {
    		this.showErrorNotification("Visualizar Arquivo", "O arquivo solicitado não foi encontrado.");
    	} else {
        	String id = UUID.randomUUID().toString();
        	
        	Session.putReport(pdfReport, id);
    		
    		getUI().getPage().open("#!" + PDFView.NAME + "/session/" + id, "_blank");    		
    	}
    }
    
    protected void showSuccessNotification(String title, String message) {
		Notification.showSuccessNotification(title, message);
	}
	
	protected void showWarningNotification(String title, String message) {
		Notification.showWarningNotification(title, message);
	}
	
	protected void showErrorNotification(String title, String message) {
		Notification.showErrorNotification(title, message);
	}

}
