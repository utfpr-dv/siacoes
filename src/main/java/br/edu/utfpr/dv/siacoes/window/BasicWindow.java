package br.edu.utfpr.dv.siacoes.window;

import java.util.UUID;

import com.vaadin.ui.Window;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.view.PDFView;

public class BasicWindow extends Window {
	
	public BasicWindow(String title) {
		super(title);
		this.setModal(true);
		this.setResizable(false);
	}

	protected void showReport(byte[] pdfReport){
    	String id = UUID.randomUUID().toString();
    	
    	Session.putReport(pdfReport, id);
		
		getUI().getPage().open("#!" + PDFView.NAME + "/session/" + id, "_blank");
    }
	
}
