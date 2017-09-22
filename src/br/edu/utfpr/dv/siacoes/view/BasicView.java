package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.components.SideMenu;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public abstract class BasicView extends CustomComponent implements View {
	
	private final Label labelCaption;
	private final ComponentContainer content;
	private final SideMenu menu;
	
	public BasicView(){
		HorizontalLayout horizontal = new HorizontalLayout();
		
		horizontal.setSizeFull();
		horizontal.addStyleName("mainview");
		horizontal.setSpacing(false);

		this.menu = new SideMenu();
		horizontal.addComponent(this.menu);

		VerticalLayout vertical = new VerticalLayout();
		
		vertical.setSizeFull();
		vertical.setSpacing(false);
		
		HorizontalLayout layoutCaption = new HorizontalLayout();
		
		layoutCaption.setStyleName(ValoTheme.MENU_ROOT);
		layoutCaption.setWidth("100%");
		layoutCaption.setHeight("25px");
		
		this.labelCaption = new Label();
		layoutCaption.addComponent(this.labelCaption);
		this.labelCaption.setStyleName("Caption");
		
		vertical.addComponent(layoutCaption);
		
        this.content = new CssLayout();
        this.content.addStyleName("view-content");
        this.content.setSizeFull();
        vertical.addComponent(this.content);
        vertical.setExpandRatio(this.content, 1.0f);
        
        horizontal.addComponent(vertical);
        horizontal.setExpandRatio(vertical, 1.0f);
        
        this.setCompositionRoot(horizontal);
        this.setSizeFull();
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

}
