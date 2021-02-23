package br.edu.utfpr.dv.siacoes.ui.windows;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public abstract class EditWindow extends BasicWindow {

	private final ListView parentView;
	private final Button buttonSave;
	private final Button buttonSign;
	private final VerticalLayout layoutFields;
	private final HorizontalLayout layoutButtons;
	
	public EditWindow(String title, ListView parentView){
		super(title);
		
		this.parentView = parentView;
		
		this.buttonSave = new Button("Salvar");
		this.buttonSave.addClickListener(event -> {
        	save();
        	buttonSave.setEnabled(true);
        });
		this.buttonSave.setWidth("150px");
		this.buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.buttonSave.setIcon(new Icon(VaadinIcon.CHECK));
		this.buttonSave.setDisableOnClick(true);
		
		this.buttonSign = new Button("Assinar");
		this.buttonSign.addClickListener(event -> {
        	clickSign();
        	buttonSign.setEnabled(true);
        });
		this.buttonSign.setWidth("150px");
		this.buttonSign.setIcon(new Icon(VaadinIcon.PENCIL));
		this.buttonSign.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonSign.setVisible(false);
		this.buttonSign.setDisableOnClick(true);
		
		this.layoutFields = new VerticalLayout();
		this.layoutFields.setSpacing(false);
		this.layoutFields.setMargin(false);
		this.layoutFields.setPadding(false);
		
		this.layoutButtons = new HorizontalLayout(this.buttonSave, this.buttonSign);
		this.layoutButtons.setSpacing(true);
		this.layoutButtons.setMargin(false);
		this.layoutButtons.setPadding(false);
		
		VerticalLayout vl = new VerticalLayout(this.layoutFields, this.layoutButtons);
		vl.setSpacing(false);
		vl.setPadding(false);
		vl.setMargin(false);
		
		this.add(vl);
		
		this.setModal(true);
        this.setResizable(false);
        this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
	}
	
	public void disableButtons() {
		this.setSaveButtonEnabled(false);
		this.setSignButtonEnabled(false);
	}
	
	public void setSaveButtonEnabled(boolean enabled) {
		this.buttonSave.setEnabled(enabled);
	}
	
	public boolean isSaveButtonEnabled() {
		return this.buttonSave.isEnabled();
	}
	
	public void setSignButtonEnabled(boolean enabled) {
		this.buttonSign.setEnabled(enabled);
	}
	
	public boolean isSignButtonEnabled() {
		return this.buttonSign.isEnabled();
	}
	
	public void setSaveButtonVisible(boolean visible) {
		this.buttonSave.setVisible(visible);
	}
	
	public boolean isSaveButtonVisible() {
		return this.buttonSave.isVisible();
	}
	
	public void setSignButtonVisible(boolean visible) {
		this.buttonSign.setVisible(visible);
	}
	
	public boolean isSignButtonVisible() {
		return this.buttonSign.isVisible();
	}
	
	public void parentViewRefreshGrid() {
		if(this.parentView != null) {
			this.parentView.refreshGrid();
		}
	}
	
	public void addField(Component c) {
		if(c instanceof HorizontalLayout) {
			((HorizontalLayout)c).setSpacing(true);
			((HorizontalLayout)c).setPadding(false);
			((HorizontalLayout)c).setMargin(false);
		} else if(c instanceof VerticalLayout) {
			((VerticalLayout)c).setSpacing(false);
			((VerticalLayout)c).setPadding(false);
			((VerticalLayout)c).setMargin(false);
		}
		
		this.layoutFields.add(c);
	}
	
	public Component getField(int index) {
		return this.layoutFields.getComponentAt(index);
	}
	
	public void addButton(Component c) {
		if(c instanceof Button) {
			((Button)c).setWidth("150px");
		}
		
		this.layoutButtons.add(c);
	}
	
	public abstract void save();
	
	private void clickSign() {
		this.sign();
	}
	
	public void sign() { }
	
}
