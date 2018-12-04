package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

public class Notification {
	
	public static void showSuccessNotification(String title, String message) {
		com.vaadin.ui.Notification notification = new com.vaadin.ui.Notification(title, message);
		
		notification.setStyleName(ValoTheme.NOTIFICATION_SUCCESS);
		notification.setDelayMsec(2000);
		
		notification.show(UI.getCurrent().getPage());
	}
	
	public static void showWarningNotification(String title, String message) {
		com.vaadin.ui.Notification notification = new com.vaadin.ui.Notification(title, message);
		
		notification.setStyleName(ValoTheme.NOTIFICATION_WARNING);
		notification.setDelayMsec(2000);
		
		notification.show(UI.getCurrent().getPage());
	}
	
	public static void showErrorNotification(String title, String message) {
		com.vaadin.ui.Notification notification = new com.vaadin.ui.Notification(title, message);
		
		notification.setStyleName(ValoTheme.NOTIFICATION_ERROR);
		notification.setDelayMsec(3000);
		
		notification.show(UI.getCurrent().getPage());
	}

}
