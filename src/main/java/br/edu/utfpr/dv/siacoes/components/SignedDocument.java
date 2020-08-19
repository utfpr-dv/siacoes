package br.edu.utfpr.dv.siacoes.components;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Signature;

public class SignedDocument extends VerticalLayout {
	
	private final Panel panelDocument;
	private final Panel panelSignatures;
	private Grid gridSignatures;
	private final Button buttonRevokeSignatures;
	
	public SignedDocument(Document document, boolean showRevoke) {
		this.panelDocument = new Panel("Documento");
		this.panelDocument.setSizeFull();
		
		this.panelSignatures = new Panel("Assinaturas");
		this.panelSignatures.setSizeFull();
		
		this.buttonRevokeSignatures = new Button("Revogar Assinaturas", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	revoke(document);
            }
        });
		this.buttonRevokeSignatures.setIcon(FontAwesome.TRASH_O);
		this.buttonRevokeSignatures.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRevokeSignatures.setSizeFull();
		this.buttonRevokeSignatures.setHeight("30px");
		
		this.addComponent(this.panelDocument);
		this.addComponent(this.panelSignatures);
		if(showRevoke) {
			this.addComponent(this.buttonRevokeSignatures);
		}
		this.setExpandRatio(this.panelDocument, 0.7f);
		this.setExpandRatio(this.panelSignatures, 0.3f);
		
		this.setSizeFull();
		
		this.loadDocument(document);
	}
	
	private void loadDocument(Document doc) {
		try {
			this.gridSignatures = new Grid();
			this.gridSignatures.setSizeFull();
			this.gridSignatures.addColumn("Assinante", String.class);
			this.gridSignatures.addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
			this.gridSignatures.addColumn("Situação", String.class);
			this.gridSignatures.getColumns().get(1).setWidth(150);
			this.gridSignatures.getColumns().get(2).setWidth(120);
			
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
				
				this.panelSignatures.setContent(this.gridSignatures);
				
				if(Document.hasRevokedSignature(doc.getIdDocument())) {
					this.buttonRevokeSignatures.setVisible(false);
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Autenticar Assinaturas", e.getMessage());
		}
	}
	
	private void revoke(Document doc) {
		if((doc != null) && (doc.getIdDocument() != 0)) {
			ConfirmDialog.show(UI.getCurrent(), "Confirma a revogação das assinaturas?\n\nEssa operação não poderá ser desfeita!", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	try {
							Document.revoke(doc.getIdDocument(), Session.getUser().getIdUser());
							
							doc.setSignatures(Document.find(doc.getIdDocument()).getSignatures());
							loadDocument(doc);
						} catch (SQLException e) {
							Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
							
							Notification.showErrorNotification("Revogar Assinaturas", e.getMessage());
						}
                    }
                }
            });
		} else {
			Notification.showWarningNotification("Selecionar Documento", "Selecione o documento para revogar as assinaturas.");
		}
	}

}
