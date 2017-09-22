package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class MenuEntry extends HorizontalLayout {

	public MenuEntry(String caption, int level, String navigateTo){
		Link label = new Link(caption, null);
		
		this.addLayoutClickListener(new LayoutClickListener() {            
			@Override
	        	public void layoutClick(LayoutClickEvent event) {
					UI.getCurrent().getNavigator().navigateTo(navigateTo);
	            }
		});
		
		this.addLabel(label, level);
	}
	
	public MenuEntry(String caption, int level){
		Label label = new Label(caption);
		
		this.addLabel(label, level);
	}
	
	public MenuEntry(String caption, int level, Window window){
		Link label = new Link(caption, null);
		
		this.addLayoutClickListener(new LayoutClickListener() {            
			@Override
	        	public void layoutClick(LayoutClickEvent event) {
					UI.getCurrent().addWindow(window);
	            }
		});
		
		this.addLabel(label, level);
	}
	
	public MenuEntry(String caption, int level, MenuEntryClickListener clickListener){
		Link label = new Link(caption, null);
		
		this.addLayoutClickListener(new LayoutClickListener() {            
			@Override
	        	public void layoutClick(LayoutClickEvent event) {
					clickListener.menuClick();
	            }
		});
		
		this.addLabel(label, level);
	}
	
	private Label getIdent(int level){
		Label label = new Label("");
		
		label.setWidth(String.valueOf(level * 10) + "px");
		
		return label;
	}
	
	private void addLabel(Component label, int level){
		if(level > 0){
			this.addComponent(this.getIdent(level));
		}
		
		this.addComponent(label);
	}
	
}
