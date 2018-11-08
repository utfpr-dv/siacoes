package br.edu.utfpr.dv.siacoes.window;

import java.util.UUID;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.Notification;
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
