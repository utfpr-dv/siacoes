package br.edu.utfpr.dv.siacoes.window;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.view.ListView;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class EditWindow extends BasicWindow {

	private final ListView parentView;
	private final Button buttonSave;
	private final Button buttonSign;
	private final VerticalLayout layoutFields;
	private final HorizontalLayout layoutButtons;
	
	public EditWindow(String title, ListView parentView){
		super(title);
		
		this.parentView = parentView;
		
		this.buttonSave = new Button("Salvar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	save();
            	buttonSave.setEnabled(true);
            }
        });
		this.buttonSave.setWidth("150px");
		this.buttonSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.buttonSave.setIcon(FontAwesome.SAVE);
		this.buttonSave.setDisableOnClick(true);
		
		this.buttonSign = new Button("Assinar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	clickSign();
            	buttonSign.setEnabled(true);
            }
        });
		this.buttonSign.setWidth("150px");
		this.buttonSign.setIcon(FontAwesome.PENCIL);
		this.buttonSign.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonSign.setVisible(false);
		this.buttonSign.setDisableOnClick(true);
		
		this.layoutFields = new VerticalLayout();
		this.layoutFields.setSpacing(true);
		
		this.layoutButtons = new HorizontalLayout(this.buttonSave, this.buttonSign);
		this.layoutButtons.setSpacing(true);
		
		VerticalLayout vl = new VerticalLayout(this.layoutFields, this.layoutButtons);
		vl.setSpacing(true);
		vl.setMargin(true);
		
		this.setContent(vl);
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
	}
	
	public void disableButtons() {
		this.setSaveButtonEnabled(false);
		this.setSignButtonEnabled(false);
	}
	
	public void setSaveButtonEnabled(boolean enabled){
		this.buttonSave.setEnabled(enabled);
	}
	
	public boolean isSaveButtonEnabled(){
		return this.buttonSave.isEnabled();
	}
	
	public void setSignButtonEnabled(boolean enabled){
		this.buttonSign.setEnabled(enabled);
	}
	
	public boolean isSignButtonEnabled(){
		return this.buttonSign.isEnabled();
	}
	
	public void setSaveButtonVisible(boolean visible){
		this.buttonSave.setVisible(visible);
	}
	
	public boolean isSaveButtonVisible(){
		return this.buttonSave.isVisible();
	}
	
	public void setSignButtonVisible(boolean visible){
		this.buttonSign.setVisible(visible);
	}
	
	public boolean isSignButtonVisible(){
		return this.buttonSign.isVisible();
	}
	
	public void parentViewRefreshGrid(){
		if(this.parentView != null){
			this.parentView.refreshGrid();
		}
	}
	
	public void addField(Component c){
		if(c instanceof HorizontalLayout){
			((HorizontalLayout)c).setSpacing(true);
		}else if(c instanceof VerticalLayout){
			((VerticalLayout)c).setSpacing(true);
		}
		
		this.layoutFields.addComponent(c);
	}
	
	public Component getField(int index) {
		return this.layoutFields.getComponent(index);
	}
	
	public void addButton(Component c){
		c.setWidth("150px");
		this.layoutButtons.addComponent(c);
	}
	
	public abstract void save();
	
	private void clickSign() {
		this.sign();
	}
	
	public void sign() { }
	
}
