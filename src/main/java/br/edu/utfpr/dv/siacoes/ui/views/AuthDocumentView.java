package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.ui.grid.SignatureDataSource;

@PageTitle("Autenticar Documento")
@Route(value = "authdocument")
public class AuthDocumentView extends ViewFrame implements HasUrlParameter<String> {
	
	public static final String NAME = "authdocument";
	
	private final TextField textGuid;
	private final H1 label;
	private final Details panel;
	private final Button buttonAuthenticate;
	private final VerticalLayout layoutGuid;
	
	private final HorizontalLayout layoutDocument;
	private final Details panelDocument;
	private final Details panelSignatures;
	private final Grid<SignatureDataSource> gridSignatures;
	
	public AuthDocumentView() {
		this.label = new H1("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
		
		this.gridSignatures = new Grid<SignatureDataSource>();
		this.gridSignatures.setSelectionMode(SelectionMode.SINGLE);
		this.gridSignatures.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSignatures.addColumn(SignatureDataSource::getName).setHeader("Assinante");
		this.gridSignatures.addColumn(SignatureDataSource::getDate).setHeader("Data e Hora").setFlexGrow(0).setWidth("170px");
		this.gridSignatures.addColumn(SignatureDataSource::getStatus).setHeader("Situação").setFlexGrow(0).setWidth("120px");
		this.gridSignatures.setSizeFull();
    	
		this.panel = new Details();
		this.panel.setSummaryText("Autenticação de Assinatura Eletrônica");
		this.panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panel.setOpened(true);
		this.panel.getElement().getStyle().set("width", "500px");
		
		this.textGuid = new TextField("Código de Autenticação");
		this.textGuid.setWidth("350px");
		this.textGuid.setMaxLength(36);
		
		this.buttonAuthenticate = new Button("Autenticar", event -> {
            validate();
        });
		this.buttonAuthenticate.setWidth("200px");
		this.buttonAuthenticate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		VerticalLayout vl = new VerticalLayout(this.textGuid, this.buttonAuthenticate);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setMargin(false);
		vl.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		this.panel.setContent(vl);
		
		this.layoutGuid = new VerticalLayout(this.label, this.panel);
		this.layoutGuid.setSpacing(false);
		this.layoutGuid.setMargin(false);
		this.layoutGuid.setPadding(false);
		this.layoutGuid.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		this.setViewContent(this.layoutGuid);
		
		this.panelDocument = new Details();
		this.panelDocument.setSummaryText("Documento");
		this.panelDocument.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelDocument.setOpened(true);
		this.panelDocument.getElement().getStyle().set("width", "100%");
		this.panelDocument.getElement().getStyle().set("height", "100%");
		
		this.panelSignatures = new Details();
		this.panelSignatures.setSummaryText("Assinaturas");
		this.panelSignatures.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelSignatures.setOpened(true);
		this.panelSignatures.getElement().getStyle().set("width", "100%");
		this.panelSignatures.getElement().getStyle().set("height", "100%");
		this.panelSignatures.setContent(this.gridSignatures);
		
		this.layoutDocument = new HorizontalLayout(this.panelDocument, this.panelSignatures);
		this.layoutDocument.setSizeFull();
		this.layoutDocument.setSpacing(true);
		this.layoutDocument.setMargin(false);
		this.layoutDocument.setPadding(false);
		
		this.textGuid.focus();
	}
	
	private void validate() {
		this.gridSignatures.setItems(new ArrayList<SignatureDataSource>());
		
		try {
			byte[] report = Document.getSignedDocument(this.textGuid.getValue().trim());
			Document doc = Document.find(this.textGuid.getValue().trim());
			
			if((report == null) || (doc == null)) {
				this.showErrorNotification("Autenticar Assinaturas", "Documento não encontrado. Verifique se o código de autenticação está correto.");
			} else {
				StreamResource s = new StreamResource("report.pdf", () -> new ByteArrayInputStream(report));
				
				PdfBrowserViewer viewer = new PdfBrowserViewer(s);
				viewer.setWidth("100%");
				viewer.setHeight("100%");
				
				this.panelDocument.setContent(viewer);
				
				this.gridSignatures.setItems(SignatureDataSource.load(doc.getSignatures(), true));
				
				this.panelSignatures.setContent(this.gridSignatures);
				
				this.setViewContent(this.layoutDocument);				
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Autenticar Assinaturas", e.getMessage());
		}
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if((parameter != null) && (!parameter.trim().isEmpty())){
			this.textGuid.setValue(parameter);
			
			this.validate();
		}
	}

}
