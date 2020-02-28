package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.components.Notification;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Signature;

public class AuthDocumentView extends CustomComponent implements View {
	
	public static final String NAME = "authdocument";
	
	private final TextField textGuid;
	private final Label label;
	private final Panel panel;
	private final Button buttonAuthenticate;
	private final VerticalLayout layoutGuid;
	
	private final HorizontalLayout layoutDocument;
	private final Panel panelDocument;
	private final Panel panelSignatures;
	private Grid gridSignatures;
	
	public AuthDocumentView() {
		this.label = new Label("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setStyleName("Title");
    	
		this.panel = new Panel("Autenticação de Documentos");
		this.panel.setWidth("500px");
		
		this.textGuid = new TextField("Código de Autenticação");
		this.textGuid.setWidth("350px");
		this.textGuid.setMaxLength(36);
		
		this.buttonAuthenticate = new Button("Autenticar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	validate();
            }
        });
		this.buttonAuthenticate.setWidth("200px");
		
		VerticalLayout vl = new VerticalLayout(this.textGuid, this.buttonAuthenticate);
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setComponentAlignment(this.textGuid, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(this.buttonAuthenticate, Alignment.MIDDLE_CENTER);
		
		this.panel.setContent(vl);
		
		this.layoutGuid = new VerticalLayout(this.label, this.panel);
		this.layoutGuid.setSpacing(true);
		this.layoutGuid.setMargin(true);
		this.layoutGuid.setComponentAlignment(this.label, Alignment.MIDDLE_CENTER);
		this.layoutGuid.setComponentAlignment(this.panel, Alignment.MIDDLE_CENTER);
		
		this.setCompositionRoot(this.layoutGuid);
		
		this.panelDocument = new Panel("Documento");
		this.panelDocument.setSizeFull();
		
		this.panelSignatures = new Panel("Assinaturas");
		this.panelSignatures.setSizeFull();
		
		this.layoutDocument = new HorizontalLayout(this.panelDocument, this.panelSignatures);
		this.layoutDocument.setSizeFull();
		
		this.textGuid.focus();
	}
	
	private void validate() {
		try {
			byte[] report = Document.getSignedDocument(this.textGuid.getValue().trim());
			Document doc = Document.find(this.textGuid.getValue().trim());
			
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
				
				this.gridSignatures = new Grid();
		    	this.gridSignatures.setSizeFull();
		    	this.gridSignatures.addColumn("Assinante", String.class);
		    	this.gridSignatures.addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
		    	this.gridSignatures.addColumn("Situação", String.class);
		    	this.gridSignatures.getColumns().get(1).setWidth(150);
		    	this.gridSignatures.getColumns().get(2).setWidth(120);
				
				for(Signature sign : doc.getSignatures()) {
					if(sign.getSignature() != null) {
						this.gridSignatures.addRow(sign.getUser().getName(), sign.getSignatureDate(), sign.getStatus().toString());
					}
				}
				
				this.panelSignatures.setContent(this.gridSignatures);
				
				this.setCompositionRoot(this.layoutDocument);
				this.setSizeFull();
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Autenticar Assinaturas", e.getMessage());
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null){
			String guid = event.getParameters();
			
			if(!guid.isEmpty()){
				this.textGuid.setValue(guid);
				
				this.validate();	
			}
		}
	}

}
