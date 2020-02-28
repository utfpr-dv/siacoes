package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Signature;
import br.edu.utfpr.dv.siacoes.window.SignatureWindow;

public class SignatureView extends BasicView {
	
	public static final String NAME = "signature";
	
	private Grid gridPending;
	private Grid gridSigned;
	
	private List<Document> listPending;
	private List<Document> listSigned;
	
	private final Button buttonSign;
	private final HorizontalLayout layoutPending;
	private final HorizontalLayout layoutSigned;
	private final VerticalLayout layoutGridPending;
	private final VerticalLayout layoutGridSigned;
	private final VerticalLayout layoutFramePending;
	private final VerticalLayout layoutFrameSigned;
	private final TabSheet tab;
	
	public SignatureView() {
		this.setCaption("Central de Assinaturas");
		
		this.layoutGridPending = new VerticalLayout();
		this.layoutGridPending.setSizeFull();
		
		this.layoutFramePending = new VerticalLayout();
		this.layoutFramePending.setSizeFull();
		
		this.buttonSign = new Button("Assinar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sign();
            }
        });
    	this.buttonSign.setWidth("100%");
		this.buttonSign.setIcon(FontAwesome.PENCIL);
		this.buttonSign.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonSign.setClickShortcut(KeyCode.ENTER);
		this.buttonSign.setDisableOnClick(true);
		
		VerticalLayout vl = new VerticalLayout(this.layoutFramePending, this.buttonSign);
		vl.setExpandRatio(this.layoutFramePending, 1.0f);
		vl.setSpacing(true);
		vl.setSizeFull();
		
		this.layoutPending = new HorizontalLayout(this.layoutGridPending, vl);
		this.layoutPending.setSpacing(true);
		this.layoutPending.setMargin(true);
		this.layoutPending.setSizeFull();
		
		this.layoutGridSigned = new VerticalLayout();
		this.layoutGridSigned.setSizeFull();
		
		this.layoutFrameSigned = new VerticalLayout();
		this.layoutFrameSigned.setSizeFull();
		
		this.layoutSigned = new HorizontalLayout(this.layoutGridSigned, this.layoutFrameSigned);
		this.layoutSigned.setSpacing(true);
		this.layoutSigned.setMargin(true);
		this.layoutSigned.setSizeFull();
		
		this.tab = new TabSheet();
		this.tab.setSizeFull();
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		this.tab.addTab(this.layoutPending, "Aguardando Assinatura");
		this.tab.addTab(this.layoutSigned, "Assinados");
		
		this.setContent(this.tab);
		
		this.loadGrids();
	}
	
	public void loadGrids() {
		this.loadGridPending();
		this.loadGridSigned();
	}
	
	private void loadGridPending() {
		try {
			this.layoutFramePending.removeAllComponents();
			
			this.listPending = Document.listPending(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			this.gridPending = new Grid();
	    	this.gridPending.setSizeFull();
	    	this.gridPending.setSelectionMode(SelectionMode.SINGLE);
	    	this.gridPending.addSelectionListener(new SelectionListener() {
				@Override
				public void select(SelectionEvent event) {
					loadPendingDocument();
				}
			});
	    	this.layoutGridPending.removeAllComponents();
	    	
	    	this.gridPending.addColumn("Documento", String.class);
	    	this.gridPending.addColumn("Gerado em", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
	    	this.gridPending.getColumns().get(1).setWidth(150);
	    	
	    	for(Document doc : this.listPending) {
	    		this.gridPending.addRow(doc.getType().toString(), doc.getGeneratedDate());
	    	}
	    	
	    	this.layoutGridPending.addComponent(this.gridPending);
			this.layoutGridPending.setExpandRatio(this.gridPending, 1.0f);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Documentos", e.getMessage());
		}
	}
	
	private void loadGridSigned() {
		try {
			this.layoutFrameSigned.removeAllComponents();
			
			this.listSigned = Document.listSigned(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			this.gridSigned = new Grid();
	    	this.gridSigned.setSizeFull();
	    	this.gridSigned.setSelectionMode(SelectionMode.SINGLE);
	    	this.gridSigned.addSelectionListener(new SelectionListener() {
				@Override
				public void select(SelectionEvent event) {
					loadSignedDocument();
				}
			});
	    	this.layoutGridSigned.removeAllComponents();
	    	
	    	this.gridSigned.addColumn("Documento", String.class);
	    	this.gridSigned.addColumn("Gerado em", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
	    	this.gridSigned.addColumn("Assinado em", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
	    	this.gridSigned.addColumn("Situação", String.class);
	    	this.gridSigned.getColumns().get(1).setWidth(150);
	    	this.gridSigned.getColumns().get(2).setWidth(150);
	    	this.gridSigned.getColumns().get(3).setWidth(120);
	    	
	    	for(Document doc : this.listSigned) {
	    		for(Signature sign : doc.getSignatures()) {
	    			if(sign.getUser().getIdUser() == Session.getUser().getIdUser()) {
	    				this.gridSigned.addRow(doc.getType().toString(), doc.getGeneratedDate(), sign.getSignatureDate(), sign.getStatus().toString());
	    			}
	    		}
	    	}
	    	
	    	this.layoutGridSigned.addComponent(this.gridSigned);
			this.layoutGridSigned.setExpandRatio(this.gridSigned, 1.0f);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Documentos", e.getMessage());
		}
	}
	
	private void sign() {
		int index = -1;
		Object itemId = this.gridPending.getSelectedRow();
		
		if(itemId != null) {
			index = (int)itemId - 1;
		}
		
		if((index >= 0) && (index < this.listPending.size())) {
			try {
				UI.getCurrent().addWindow(new SignatureWindow(this.listPending.get(index).getIdDocument(), null, this));	
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Documento", e.getMessage());
			}
		} else {
			this.showWarningNotification("Selecionar Documento", "Selecione o documento para assinar.");
		}
		
		this.buttonSign.setEnabled(true);
	}
	
	private void loadPendingDocument() {
		int index = -1;
		Object itemId = this.gridPending.getSelectedRow();
		
		if(itemId != null) {
			index = (int)itemId - 1;
		}
		
		if((index >= 0) && (index < this.listPending.size())) {
			this.layoutFramePending.removeAllComponents();
			
			try {
				byte[] report = Document.getSignedDocument(this.listPending.get(index).getIdDocument());
				
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
				
				this.layoutFramePending.addComponent(e);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Documento", e.getMessage());
			}
		}
	}

	private void loadSignedDocument() {
		int index = -1;
		Object itemId = this.gridSigned.getSelectedRow();
		
		if(itemId != null) {
			index = (int)itemId - 1;
		}
		
		if((index >= 0) && (index < this.listSigned.size())) {
			this.layoutFrameSigned.removeAllComponents();
			
			try {
				byte[] report = Document.getSignedDocument(this.listSigned.get(index).getIdDocument());
				
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
				
				this.layoutFrameSigned.addComponent(e);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Documento", e.getMessage());
			}
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
