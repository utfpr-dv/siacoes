package br.edu.utfpr.dv.siacoes.ui.components;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.ui.grid.SignatureDataSource;

public class SignedDocument extends VerticalLayout {
	
	private final HorizontalLayout panelDocument;
	private Grid<SignatureDataSource> gridSignatures;
	private final Button buttonRevokeSignatures;
	
	public SignedDocument(Document document, boolean showRevoke) {
		this.gridSignatures = new Grid<SignatureDataSource>();
		this.gridSignatures.setSelectionMode(SelectionMode.SINGLE);
		this.gridSignatures.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSignatures.addColumn(SignatureDataSource::getName).setHeader("Assinante");
		this.gridSignatures.addColumn(new LocalDateTimeRenderer<>(SignatureDataSource::getDate, "dd/MM/yyyy HH:mm")).setHeader("Data e Hora").setFlexGrow(0).setWidth("170px");
		this.gridSignatures.addColumn(SignatureDataSource::getStatus).setHeader("Situação").setFlexGrow(0).setWidth("120px");
		this.gridSignatures.setWidthFull();
		
		this.buttonRevokeSignatures = new Button("Revogar Assinaturas", new Icon(VaadinIcon.TRASH), event -> {
            revoke(document);
        });
		this.buttonRevokeSignatures.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRevokeSignatures.setWidthFull();
		
		this.panelDocument = new HorizontalLayout();
		this.panelDocument.setMargin(false);
		this.panelDocument.setPadding(false);
		this.panelDocument.setSpacing(false);
		this.panelDocument.setSizeFull();
		
		//this.add(new H4("Documento"));
		this.add(this.panelDocument);
		//this.add(new H4("Assinaturas"));
		this.add(this.gridSignatures);
		if(showRevoke) {
			this.add(this.buttonRevokeSignatures);
		}
		this.setFlexGrow(0.7, this.panelDocument);
		this.setFlexGrow(0.2, this.gridSignatures);
		
		this.setSizeFull();
		
		this.loadDocument(document);
	}
	
	private void loadDocument(Document doc) {
		this.gridSignatures.setItems(new ArrayList<SignatureDataSource>());
		this.panelDocument.removeAll();
		
		try {
			byte[] report = Document.getSignedDocument(doc.getIdDocument());
			
			if((report == null) || (doc == null)) {
				Notification.showErrorNotification("Autenticar Assinaturas", "Documento não encontrado. Verifique se o código de autenticação está correto.");
			} else {
				StreamResource s = new StreamResource("report.pdf", () -> new ByteArrayInputStream(report));
				
				PdfBrowserViewer viewer = new PdfBrowserViewer(s);
				viewer.setWidth("100%");
				viewer.setHeight("100%");
				
				this.panelDocument.add(viewer);
				
				this.gridSignatures.setItems(SignatureDataSource.load(doc.getSignatures()));
				
				if(Document.hasRevokedSignature(doc.getIdDocument())) {
					this.buttonRevokeSignatures.setVisible(false);
				}
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Autenticar Assinaturas", e.getMessage());
		}
	}
	
	private void revoke(Document doc) {
		if((doc != null) && (doc.getIdDocument() != 0)) {
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Revogar Assinatura")
		    	.withMessage("Confirma a revogação das assinaturas?\n\nEssa operação não poderá ser desfeita!")
		    	.withOkButton(() -> {
		    		try {
						Document.revoke(doc.getIdDocument(), Session.getUser().getIdUser());
						
						doc.setSignatures(Document.find(doc.getIdDocument()).getSignatures());
						loadDocument(doc);
					} catch (SQLException e) {
						Logger.log(Level.SEVERE, e.getMessage(), e);
						
						Notification.showErrorNotification("Revogar Assinaturas", e.getMessage());
					}
		    	}, ButtonOption.caption("Revogar"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
		} else {
			Notification.showWarningNotification("Selecionar Documento", "Selecione o documento para revogar as assinaturas.");
		}
	}

}
