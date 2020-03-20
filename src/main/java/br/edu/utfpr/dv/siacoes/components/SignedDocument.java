package br.edu.utfpr.dv.siacoes.components;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Signature;

public class SignedDocument extends VerticalLayout {
	
	private final Panel panelDocument;
	private final Panel panelSignatures;
	private final Grid gridSignatures;
	
	public SignedDocument(Document document) {
		this.gridSignatures = new Grid();
    	this.gridSignatures.setSizeFull();
    	this.gridSignatures.addColumn("Assinante", String.class);
    	this.gridSignatures.addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
    	this.gridSignatures.addColumn("Situação", String.class);
    	this.gridSignatures.getColumns().get(1).setWidth(150);
    	this.gridSignatures.getColumns().get(2).setWidth(120);
    	
    	this.panelDocument = new Panel("Documento");
		this.panelDocument.setSizeFull();
		
		this.panelSignatures = new Panel("Assinaturas");
		this.panelSignatures.setContent(this.gridSignatures);
		this.panelSignatures.setSizeFull();
		
		this.addComponent(this.panelDocument);
		this.addComponent(this.panelSignatures);
		this.setExpandRatio(this.panelDocument, 0.7f);
		this.setExpandRatio(this.panelSignatures, 0.3f);
		
		this.setSizeFull();
		
		this.loadDocument(document);
	}
	
	private void loadDocument(Document doc) {
		try {
			byte[] report = Document.getSignedDocument(doc.getIdDocument());
			
			if((report == null) || (doc == null)) {
				Notification.showErrorNotification("Autenticar Assinaturas", "Documento não encontrado. Verifique se o código de autenticação está correto.");
			} else {
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
				
				this.panelDocument.setContent(e);
				
				for(Signature sign : doc.getSignatures()) {
					if(sign.getSignature() != null) {
						this.gridSignatures.addRow(sign.getUser().getName(), sign.getSignatureDate(), sign.getStatus().toString());
					}
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Autenticar Assinaturas", e.getMessage());
		}
	}

}
