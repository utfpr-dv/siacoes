package br.edu.utfpr.dv.siacoes.window;

import java.util.UUID;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.view.PDFView;

public class BasicWindow extends Window {
	
	public BasicWindow(String title) {
		super(title);
		this.setModal(true);
		this.setResizable(false);
		this.center();
	}

	protected void showReport(byte[] pdfReport) {
		if(pdfReport == null) {
    		Notification.show("Visualizar Arquivo", "O arquivo solicitado não foi encontrado.", Notification.Type.ERROR_MESSAGE);
    	} else {
	    	String id = UUID.randomUUID().toString();
	    	
	    	Session.putReport(pdfReport, id);
			
			getUI().getPage().open("#!" + PDFView.NAME + "/session/" + id, "_blank");
    	}
    }
	
	protected void showSuccessNotification(String title, String message) {
		Notification notification = new Notification(title, message);
		
		notification.setStyleName(ValoTheme.NOTIFICATION_SUCCESS);
		
		notification.show(UI.getCurrent().getPage());
	}
	
	protected void showWarningNotification(String title, String message) {
		Notification notification = new Notification(title, message);
		
		notification.setStyleName(ValoTheme.NOTIFICATION_WARNING);
		
		notification.show(UI.getCurrent().getPage());
	}
	
	protected void showErrorNotification(String title, String message) {
		Notification notification = new Notification(title, message);
		
		notification.setStyleName(ValoTheme.NOTIFICATION_ERROR);
		notification.setDelayMsec(3000);
		
		notification.show(UI.getCurrent().getPage());
	}
	
}
