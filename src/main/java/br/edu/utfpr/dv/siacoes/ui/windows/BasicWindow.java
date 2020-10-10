package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.UUID;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.ui.components.Notification;

public class BasicWindow extends Dialog {
	
	private final HorizontalLayout layoutTitle;
	private final Label title;
	private final Button buttonClose;
	
	public BasicWindow(String title) {
		this.title = new Label(title);
		
		this.buttonClose = new Button(new Icon(VaadinIcon.CLOSE));
		this.buttonClose.addClickListener(event -> {
			this.close();
		});
		this.buttonClose.getElement().getStyle().set("margin-left", "auto");
		this.buttonClose.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
		
		this.layoutTitle = new HorizontalLayout(this.title, this.buttonClose);
		this.layoutTitle.setWidthFull();
		this.layoutTitle.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		this.layoutTitle.setSpacing(false);
		this.layoutTitle.setMargin(false);
		this.layoutTitle.setPadding(false);
		
		this.add(this.layoutTitle);
		
		this.setModal(true);
		this.setResizable(false);
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	public String getTitle() {
		return this.title.getText();
	}
	
	public void setClosable(boolean closable) {
		this.buttonClose.setVisible(closable);
	}
	
	public boolean isClosable() {
		return this.buttonClose.isVisible();
	}

	protected void showReport(byte[] pdfReport) {
		if(pdfReport == null) {
			this.showErrorNotification("Visualizar Arquivo", "O arquivo solicitado não foi encontrado.");
    	} else {
	    	String id = UUID.randomUUID().toString();
	    	
	    	Session.putReport(pdfReport, id);
			
	    	UI.getCurrent().getPage().open("pdf/session-" + id, "_blank");
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
