package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.components.SignedDocument;
import br.edu.utfpr.dv.siacoes.components.UserComboBox;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class SignedDocumentView extends BasicView {
	
	public static final String NAME = "signeddocument";
	
	private Grid gridDocuments;
	private final HorizontalLayout layoutFields;
    private final VerticalLayout layoutFilter;
    private final Button buttonFilter;
    private final Label labelGridRecords;
    private final Panel panelFilter;
    private final Panel panelList;
    private final VerticalLayout layoutDocument;
    private final NativeSelect comboDocumentType;
    private final NativeSelect comboStatus;
    private final UserComboBox comboUser;
	
	private final List<Document> listDocuments;
	
	public SignedDocumentView() {
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		this.setModule(SystemModule.GENERAL);
		
		this.setCaption("Documentos Assinados");
		
		this.listDocuments = new ArrayList<Document>();
		
		this.buttonFilter = new Button("Filtrar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	refreshGrid();
            }
        });
		this.buttonFilter.setWidth("150px");
		this.buttonFilter.setIcon(FontAwesome.FILTER);
		
		this.labelGridRecords = new Label();
		
		this.comboDocumentType = new NativeSelect("Documento");
		this.comboDocumentType.setWidth("300px");
		this.comboDocumentType.addItems(DocumentType.values());
		this.comboDocumentType.setNullSelectionAllowed(false);
		this.comboDocumentType.select(DocumentType.NONE);
		
		this.comboUser = new UserComboBox("Assinante");
		
		this.comboStatus = new NativeSelect("Situação da Assinatura");
		this.comboStatus.setWidth("150px");
		this.comboStatus.addItem(-1);
		this.comboStatus.addItem(0);
		this.comboStatus.addItem(1);
		this.comboStatus.addItem(2);
		this.comboStatus.setItemCaption(-1, "Todas");
		this.comboStatus.setItemCaption(0, "Pendente");
		this.comboStatus.setItemCaption(1, "Assinada");
		this.comboStatus.setItemCaption(2, "Revogada");
		this.comboStatus.setNullSelectionAllowed(false);
		this.comboStatus.select(-1);
		
		this.layoutFields = new HorizontalLayout(this.comboDocumentType, this.comboUser, this.comboStatus);
		this.layoutFields.setSpacing(true);
		
		this.layoutFilter = new VerticalLayout(this.layoutFields, this.buttonFilter);
		this.layoutFilter.setSpacing(true);
		this.layoutFilter.setMargin(true);
		
		this.panelFilter = new Panel("Filtros");
		this.panelFilter.setContent(this.layoutFilter);
		
		this.panelList = new Panel("Lista de Documentos");
		this.panelList.setSizeFull();
		
		this.layoutDocument = new VerticalLayout();
		this.layoutDocument.setSizeFull();
		
		HorizontalLayout h1 = new HorizontalLayout(this.panelList, this.layoutDocument);
		h1.setSizeFull();
		
		VerticalLayout content = new VerticalLayout(this.panelFilter, h1);
		content.setSizeFull();
		content.setExpandRatio(h1, 1.0f);
		
		this.setContent(content);
	}
	
	private Document getSelectedDocument() {
		Object itemId = this.gridDocuments.getSelectedRow();
		int index = -1;

    	if(itemId == null) {
    		index = -1;
    	} else {
    		index = ((int)itemId) - 1;	
    	}
    	
    	if((index >= 0) && (index < this.listDocuments.size())) {
    		return this.listDocuments.get(index);
    	} else {
    		return null;
    	}
	}
	
	private void refreshGrid() {
		this.gridDocuments = new Grid();
		this.gridDocuments.addColumn("Documento", String.class);
    	this.gridDocuments.addColumn("Gerado em", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
    	this.gridDocuments.getColumns().get(1).setWidth(150);
		this.gridDocuments.setSizeFull();
		this.gridDocuments.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				loadDocument();
			}
		});
		
		this.listDocuments.clear();
		
		try {
			this.listDocuments.addAll(Document.list((DocumentType)this.comboDocumentType.getValue(), (this.comboUser.getUser() == null ? 0 : this.comboUser.getUser().getIdUser()), (int)this.comboStatus.getValue()));
			
			for(Document doc : this.listDocuments) {
	    		this.gridDocuments.addRow(doc.getType().toString(), doc.getGeneratedDate());
	    	}
			
			if(this.listDocuments.size() == 0) {
				showWarningNotification("Listar Registros", "Não há registros para serem exibidos.");
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    		
    		showErrorNotification("Filtrar", e.getMessage());
		}
		
		this.labelGridRecords.setValue(String.valueOf(this.listDocuments.size()) + " registro(s)");
		VerticalLayout layout = new VerticalLayout(this.gridDocuments, this.labelGridRecords);
		layout.setExpandRatio(this.gridDocuments, 1);
		layout.setExpandRatio(this.labelGridRecords, 0);
		layout.setSizeFull();
		this.panelList.setContent(layout);
	}
	
	private void loadDocument() {
		this.layoutDocument.removeAllComponents();
		Document doc = this.getSelectedDocument();
		
		if((doc != null) && (doc.getIdDocument() != 0)) {
			this.layoutDocument.addComponent(new SignedDocument(doc));
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
