package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.SignedDocumentDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.SignatureWindow;

@PageTitle("Central de Assinaturas")
@Route(value = "signature", layout = MainLayout.class)
public class SignatureView extends LoggedView {
	
	private final Grid<Document> gridPending;
	private final Grid<SignedDocumentDataSource> gridSigned;
	
	private List<Document> listPending;
	private List<Document> listSigned;
	
	private final Button buttonSign;
	private final HorizontalLayout layoutPending;
	private final HorizontalLayout layoutSigned;
	private final VerticalLayout layoutGridPending;
	private final VerticalLayout layoutGridSigned;
	private final VerticalLayout layoutFramePending;
	private final VerticalLayout layoutFrameSigned;
	private final Tabs tab;
	
	public SignatureView() {
		this.gridPending = new Grid<Document>();
		this.gridPending.setSelectionMode(SelectionMode.SINGLE);
		this.gridPending.setSizeFull();
		this.gridPending.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridPending.addItemClickListener(event -> {
			loadPendingDocument(event.getItem());
		});
		this.gridPending.addColumn(Document::getType).setHeader("Documento");
		this.gridPending.addColumn(Document::getGeneratedDate).setHeader("Gerado em").setFlexGrow(0).setWidth("150px");
		
		this.layoutGridPending = new VerticalLayout();
		this.layoutGridPending.setSizeFull();
		this.layoutGridPending.setSpacing(false);
		this.layoutGridPending.setMargin(false);
		this.layoutGridPending.setPadding(false);
		this.layoutGridPending.addAndExpand(this.gridPending);
		
		this.layoutFramePending = new VerticalLayout();
		this.layoutFramePending.setSizeFull();
		
		this.buttonSign = new Button("Assinar", event -> {
            sign();
        });
    	this.buttonSign.setWidth("100%");
		this.buttonSign.setIcon(new Icon(VaadinIcon.PENCIL));
		this.buttonSign.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonSign.setDisableOnClick(true);
		this.buttonSign.setEnabled(false);
		
		VerticalLayout vl = new VerticalLayout(this.layoutFramePending, this.buttonSign);
		vl.expand(this.layoutFramePending);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		vl.setSizeFull();
		
		this.layoutPending = new HorizontalLayout(this.layoutGridPending, vl);
		this.layoutPending.setSpacing(true);
		this.layoutPending.setMargin(false);
		this.layoutPending.setPadding(false);
		this.layoutPending.setSizeFull();
		
		this.gridSigned = new Grid<SignedDocumentDataSource>();
		this.gridSigned.setSelectionMode(SelectionMode.SINGLE);
		this.gridSigned.setSizeFull();
		this.gridSigned.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSigned.addItemClickListener(event -> {
			loadSignedDocument(event.getItem());
		});
		this.gridSigned.addColumn(SignedDocumentDataSource::getType).setHeader("Documento");
		this.gridSigned.addColumn(SignedDocumentDataSource::getGeneratedDate).setHeader("Gerado em").setFlexGrow(0).setWidth("150px");
		this.gridSigned.addColumn(SignedDocumentDataSource::getSignatureDate).setHeader("Assinado em").setFlexGrow(0).setWidth("150px");
		this.gridSigned.addColumn(SignedDocumentDataSource::getStatus).setHeader("Situação").setFlexGrow(0).setWidth("120px");
		
		this.layoutGridSigned = new VerticalLayout();
		this.layoutGridSigned.setSizeFull();
		this.layoutGridSigned.setSpacing(false);
		this.layoutGridSigned.setPadding(false);
		this.layoutGridSigned.setMargin(false);
		this.layoutGridSigned.addAndExpand(this.gridSigned);
		
		this.layoutFrameSigned = new VerticalLayout();
		this.layoutFrameSigned.setSizeFull();
		
		this.layoutSigned = new HorizontalLayout(this.layoutGridSigned, this.layoutFrameSigned);
		this.layoutSigned.setSpacing(true);
		this.layoutSigned.setMargin(false);
		this.layoutSigned.setPadding(false);
		this.layoutSigned.setSizeFull();
		this.layoutSigned.setVisible(false);
		
		Tab tabPending = new Tab("Aguardando Assinatura");
		Tab tabSigned = new Tab("Assinados");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tabPending, this.layoutPending);
		tabsToPages.put(tabSigned, this.layoutSigned);
		Div pages = new Div(this.layoutPending, this.layoutSigned);
		pages.setSizeFull();
		
		this.tab = new Tabs(tabPending, tabSigned);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tabPending);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setSizeFull();
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.setViewContent(layout);
		
		this.loadGrids();
	}
	
	public void loadGrids() {
		this.loadGridPending();
		this.loadGridSigned();
	}
	
	private void loadGridPending() {
		try {
			this.listPending = Document.listPending(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			this.gridPending.setItems(this.listPending);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Documentos", e.getMessage());
		}
	}
	
	private void loadGridSigned() {
		try {
			this.listSigned = Document.listSigned(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			this.gridSigned.setItems(SignedDocumentDataSource.load(this.listSigned));
	    	
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Documentos", e.getMessage());
		}
	}
	
	private void sign() {
		Document doc = this.gridPending.asSingleSelect().getValue();
		
		if(doc != null) {
			try {
				SignatureWindow window = new SignatureWindow(doc.getIdDocument(), null, this);
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Documento", e.getMessage());
			}
		} else {
			this.showWarningNotification("Selecionar Documento", "Selecione o documento para assinar.");
		}
		
		this.buttonSign.setEnabled(true);
	}
	
	private void loadPendingDocument(Document doc) {
		this.layoutFramePending.removeAll();
		
		if(doc == null)
			return;
			
		try {
			byte[] report = Document.getSignedDocument(doc.getIdDocument());
			
			StreamResource s = new StreamResource("report.pdf", () -> new ByteArrayInputStream(report));
			
			PdfBrowserViewer viewer = new PdfBrowserViewer(s);
			viewer.setWidth("100%");
			viewer.setHeight("100%");
			
			this.layoutFramePending.addAndExpand(viewer);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Documento", e.getMessage());
		}
	}

	private void loadSignedDocument(SignedDocumentDataSource doc) {
		this.layoutFrameSigned.removeAll();
		
		if(doc == null)
			return;
			
		try {
			byte[] report = Document.getSignedDocument(doc.getIdDocument());
			
			StreamResource s = new StreamResource("report.pdf", () -> new ByteArrayInputStream(report));
			
			PdfBrowserViewer viewer = new PdfBrowserViewer(s);
			viewer.setWidth("100%");
			viewer.setHeight("100%");
			
			this.layoutFrameSigned.addAndExpand(viewer);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Documento", e.getMessage());
		}
	}

}
