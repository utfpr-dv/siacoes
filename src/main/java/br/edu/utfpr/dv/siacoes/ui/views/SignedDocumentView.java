package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SignedDocument;
import br.edu.utfpr.dv.siacoes.ui.components.UserComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.SignedDocumentDataSource;

@PageTitle("Documentos Assinados")
@Route(value = "signeddocument", layout = MainLayout.class)
public class SignedDocumentView extends LoggedView implements BeforeLeaveObserver {
	
	private final Grid<SignedDocumentDataSource> gridDocuments;
	private final HorizontalLayout layoutFields;
    private final VerticalLayout layoutFilter;
    private final Button buttonFilter;
    private final Label labelGridRecords;
    private final Details panelFilter;
    private final Details panelList;
    private final VerticalLayout layoutDocument;
    private final Select<DocumentType> comboDocumentType;
    private final Select<String> comboStatus;
    private final UserComboBox comboUser;
	
	private final List<Document> listDocuments;
	
	private static final String ALL = "Todas";
	private static final String PENDING = "Pendente";
	private static final String SIGNED = "Assinada";
	private static final String REVOKED = "Revogada";
	
	public SignedDocumentView() {
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		this.setModule(SystemModule.GENERAL);
		
		this.gridDocuments = new Grid<SignedDocumentDataSource>();
		this.gridDocuments.setSelectionMode(SelectionMode.SINGLE);
		this.gridDocuments.setSizeFull();
		this.gridDocuments.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridDocuments.addSelectionListener(event -> {
			this.loadDocument();
		});
		this.gridDocuments.addColumn(SignedDocumentDataSource::getType).setHeader("Documento");
		this.gridDocuments.addColumn(SignedDocumentDataSource::getGeneratedDate).setHeader("Gerado em").setFlexGrow(0).setWidth("150px");
		
		this.listDocuments = new ArrayList<Document>();
		
		this.buttonFilter = new Button("Filtrar", new Icon(VaadinIcon.FILTER), event -> {
            refreshGrid();
        });
		this.buttonFilter.setWidth("150px");
		
		this.labelGridRecords = new Label();
		
		this.comboDocumentType = new Select<DocumentType>();
		this.comboDocumentType.setLabel("Documento");
		this.comboDocumentType.setWidth("300px");
		this.comboDocumentType.setItems(DocumentType.values());
		this.comboDocumentType.setValue(DocumentType.NONE);
		
		this.comboUser = new UserComboBox("Assinante");
		
		this.comboStatus = new Select<String>();
		this.comboStatus.setLabel("Situação da Assinatura");
		this.comboStatus.setWidth("150px");
		this.comboStatus.setItems(ALL, PENDING, SIGNED, REVOKED);
		this.comboStatus.setValue(ALL);
		
		this.layoutFields = new HorizontalLayout(this.comboDocumentType, this.comboUser, this.comboStatus);
		this.layoutFields.setSpacing(true);
		
		this.layoutFilter = new VerticalLayout(this.layoutFields, this.buttonFilter);
		this.layoutFilter.setSpacing(false);
		this.layoutFilter.setMargin(false);
		this.layoutFilter.setPadding(false);
		
		this.panelFilter = new Details();
		this.panelFilter.setSummaryText("Filtros");
		this.panelFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelFilter.setOpened(true);
		this.panelFilter.setContent(this.layoutFilter);
		
		this.panelList = new Details();
		this.panelList.setSummaryText("Lista de Documentos");
		this.panelList.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelList.setOpened(true);
		
		this.labelGridRecords.setText(String.valueOf(this.listDocuments.size()) + " registro(s)");
		VerticalLayout layout = new VerticalLayout(this.gridDocuments, this.labelGridRecords);
		layout.expand(this.gridDocuments);
		layout.setSizeFull();
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		this.panelList.setContent(layout);
		
		this.layoutDocument = new VerticalLayout();
		this.layoutDocument.setSpacing(false);
		this.layoutDocument.setMargin(false);
		this.layoutDocument.setPadding(false);
		this.layoutDocument.setSizeFull();
		
		HorizontalLayout h1 = new HorizontalLayout(this.panelList, this.layoutDocument);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		h1.setSizeFull();
		
		VerticalLayout content = new VerticalLayout(this.panelFilter, h1);
		content.setSpacing(false);
		content.setMargin(false);
		content.setPadding(false);
		content.setSizeFull();
		content.expand(h1);
		
		this.setViewContent(content);
	}
	
	private Document getSelectedDocument() {
		SignedDocumentDataSource doc = this.gridDocuments.asSingleSelect().getValue();
		
		if(doc == null) {
			return null;
		} else {
			for(Document document : this.listDocuments) {
				if(document.getIdDocument() == doc.getIdDocument()) {
					return document;
				}
			}
			
			return null;
		}
	}
	
	private void refreshGrid() {
		int status;
		
		this.listDocuments.clear();
		
		if(this.comboStatus.getValue().equals(PENDING)) {
			status = 0;
		} else if(this.comboStatus.getValue().equals(SIGNED)) {
			status = 1;
		} else if(this.comboStatus.getValue().equals(REVOKED)) {
			status = 2;
		} else {
			status = -1;
		}
		
		try {
			this.listDocuments.addAll(Document.list((DocumentType)this.comboDocumentType.getValue(), (this.comboUser.getUser() == null ? 0 : this.comboUser.getUser().getIdUser()), status));
			
			this.gridDocuments.setItems(SignedDocumentDataSource.load(this.listDocuments));
			
			if(this.listDocuments.size() == 0) {
				showWarningNotification("Listar Registros", "Não há registros para serem exibidos.");
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
    		
    		showErrorNotification("Filtrar", e.getMessage());
		}
	}
	
	private void loadDocument() {
		this.layoutDocument.removeAll();
		Document doc = this.getSelectedDocument();
		
		if((doc != null) && (doc.getIdDocument() != 0)) {
			this.layoutDocument.add(new SignedDocument(doc, true));
		}
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		MainLayout.reloadNaviItems();
	}

}
