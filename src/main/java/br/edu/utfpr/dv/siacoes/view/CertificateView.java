package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CustomComponent;

import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.model.Certificate;

public class CertificateView extends CustomComponent implements View {
	
	public static final String NAME = "certificate";
	
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
			
			if(!guid.isEmpty()){
				try {
					CertificateBO bo = new CertificateBO();
					Certificate certificate = bo.findByGuid(guid.trim());
					
					this.showPdf(certificate.getFile());
				} catch (Exception e) {
					Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

}
