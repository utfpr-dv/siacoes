package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Notification {
	
	public static void showSuccessNotification(String title, String message) {
		Dialog dialog = new Dialog(new VerticalLayout(new H6(title), new Text(message)));
		dialog.setModal(true);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
	}
	
	public static void showWarningNotification(String title, String message) {
		Dialog dialog = new Dialog(new H6(title), new Text(message));
		dialog.setModal(true);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
	}
	
	public static void showErrorNotification(String title, String message) {
		if((message == null) || (message.isEmpty())) {
			message = "Erro inesperado.";
		}
		
		Dialog dialog = new Dialog(new H6(title), new Text(message));
		dialog.setModal(true);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
	}

}
