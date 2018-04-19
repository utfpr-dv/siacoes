package br.edu.utfpr.dv.siacoes.window;

import java.util.UUID;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.view.PDFView;

public class BasicWindow extends Window {
	
	public BasicWindow(String title) {
		super(title);
		this.setModal(true);
		this.setResizable(false);
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
	
}
