package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CustomComponent;

import br.edu.utfpr.dv.siacoes.Session;

public class PDFView extends CustomComponent implements View {
	
	public static final String NAME = "pdf";
	
	private void showPdf(byte[] report){
		StreamSource s = new StreamResource.StreamSource() {
			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(report);
			}
		};
		
		StreamResource r = new StreamResource(s, "document.pdf");
		r.setMIMEType("application/pdf");
		r.setCacheTime(0);
		
		BrowserFrame e = new BrowserFrame(null, r);
		e.setSizeFull();
		
		this.setCompositionRoot(e);
		this.setSizeFull();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null){
			String guid = event.getParameters();
			
			if(guid.startsWith("session")){
				this.showPdf(Session.getReport(guid.replace("session/", "")));
			}
		}
	}

}
