package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Notification {
	
	private static final int SUCCESS = 0;
	private static final int WARNING = 1;
	private static final int ERROR = 2;
	
	public static void showSuccessNotification(String title, String message) {
		Notification.buildNotificationDialog(title, message, SUCCESS);
	}
	
	public static void showWarningNotification(String title, String message) {
		Notification.buildNotificationDialog(title, message, WARNING);
	}
	
	public static void showErrorNotification(String title, String message) {
		if((message == null) || (message.isEmpty())) {
			message = "Erro inesperado.";
		}
		
		Notification.buildNotificationDialog(title, message, ERROR);
	}
	
	private static void buildNotificationDialog(String title, String message, int type) {
		H4 t = new H4(title);
		Span msg = new Span(message);
		Icon icon;
		Button buttonClose = new Button("OK");
		
		if(type == WARNING) {
			icon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
			icon.setColor("yellow");
		} else if(type == ERROR) {
			icon = new Icon(VaadinIcon.CLOSE_CIRCLE);
			icon.setColor("red");
		} else {
			icon = new Icon(VaadinIcon.CHECK_CIRCLE);
			icon.setColor("green");
		}
		icon.setSize("64px");
		icon.getElement().getStyle().set("padding-right", "10px");
		
		HorizontalLayout h1 = new HorizontalLayout(icon, msg);
		h1.setSpacing(true);
		h1.setVerticalComponentAlignment(Alignment.CENTER, icon);
		h1.setVerticalComponentAlignment(Alignment.CENTER, msg);
		h1.setMinWidth("400px");
		HorizontalLayout h2 = new HorizontalLayout();
		h2.setWidth("100%");
		h2.addAndExpand(new Div());
		h2.add(buttonClose);
		
		if(type == SUCCESS) {
			VerticalLayout vl = new VerticalLayout(t, h1);
			com.vaadin.flow.component.notification.Notification notification = new com.vaadin.flow.component.notification.Notification(vl);
			notification.setDuration(2000);
			notification.setPosition(com.vaadin.flow.component.notification.Notification.Position.MIDDLE);
			notification.open();
		} else {
			Dialog dialog = new Dialog(t, h1, h2);
			dialog.setModal(true);
			dialog.setCloseOnEsc(false);
			dialog.setCloseOnOutsideClick(false);
			dialog.open();
			
			buttonClose.addClickListener(event -> {
				dialog.close();
			});
		}
	}

}
