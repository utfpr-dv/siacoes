package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
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

import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipWindow;

public class StudentSigesHistory extends HorizontalLayout {
	
	private final Grid<InternshipDataSource> gridInternship;
	private final Tabs tabDocuments;
	private final Button buttonInternship;
	private final Button buttonJury;
	
	private final List<Internship> listInternship;
	private SigesConfig config;
	
	private final Map<Tab, Component> tabsToPages;
	private final Div pages;
	
	public StudentSigesHistory(int idStudent, int idDepartment) throws Exception {
		this.config = new SigesConfigBO().findByDepartment(idDepartment);
		
		this.listInternship = new ArrayList<Internship>();
		
		this.gridInternship = new Grid<InternshipDataSource>();
		this.gridInternship.setSelectionMode(SelectionMode.SINGLE);
		this.gridInternship.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridInternship.addColumn(InternshipDataSource::getCompany).setHeader("Empresa");
		this.gridInternship.addColumn(InternshipDataSource::getStartDate).setHeader("Data Início").setFlexGrow(0).setWidth("125px");
		this.gridInternship.addColumn(InternshipDataSource::getType).setHeader("Tipo").setFlexGrow(0).setWidth("160px");
		this.gridInternship.setSizeFull();
		this.gridInternship.addSelectionListener(event -> {
			loadDocuments();
		});
		
		this.buttonInternship = new Button("Registro de Estágio", new Icon(VaadinIcon.CLIPBOARD), event -> {
            editInternship();
        });
		this.buttonInternship.setSizeFull();
		
		this.buttonJury = new Button("Banca", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
            editJury();
        });
		this.buttonJury.setSizeFull();
		
		HorizontalLayout layoutButtons = new HorizontalLayout(this.buttonInternship, this.buttonJury);
		layoutButtons.setSpacing(true);
		layoutButtons.setMargin(false);
		layoutButtons.setPadding(false);
		layoutButtons.setWidth("100%");
		
		VerticalLayout vl = new VerticalLayout(this.gridInternship);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		VerticalLayout layoutGrid = new VerticalLayout(vl, layoutButtons);
		layoutGrid.setSpacing(false);
		layoutGrid.setMargin(false);
		layoutGrid.setPadding(false);
		layoutGrid.expand(vl);
		layoutGrid.setSizeFull();
		
		this.tabsToPages = new HashMap<>();
		
		this.pages = new Div();
		this.pages.setSizeFull();
		
		this.tabDocuments = new Tabs();
		this.tabDocuments.setWidthFull();
		this.tabDocuments.setFlexGrowForEnclosedTabs(1);
		
		this.tabDocuments.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tabDocuments.getSelectedTab());
		    if(selectedPage != null) {
		    	selectedPage.setVisible(true);
		    }
		});
		
		this.setSpacing(true);
		this.setMargin(false);
		this.setPadding(false);
		this.setSizeFull();
		
		this.add(layoutGrid);
		
		if(this.config.isUseDigitalSignature()) {
			VerticalLayout layoutTab = new VerticalLayout(this.tabDocuments, this.pages);
			layoutTab.setSpacing(false);
			layoutTab.setMargin(false);
			layoutTab.setPadding(false);
			
			this.add(layoutTab);
		}
		
		this.loadInternships(idStudent, idDepartment);
	}
	
	private void loadInternships(int idStudent, int idDepartment) throws Exception {
		List<Internship> list = new InternshipBO().listByStudent(idStudent, idDepartment);
		
		this.listInternship.addAll(list);
		
		this.gridInternship.setItems(InternshipDataSource.load(this.listInternship));
	}
	
	private void loadDocuments() {
		Internship internship = this.getSelectedInternship();
		
		if((internship == null) || (internship.getIdInternship() == 0)) {
			return;
		}
		
		this.tabDocuments.removeAll();
		this.pages.removeAll();
		this.tabsToPages.clear();
		
		try {
			InternshipPosterRequest request = new InternshipPosterRequestBO().findByInternship(internship.getIdInternship());
			
			if((request != null) && (request.getIdInternshipPosterRequest() != 0)) {
				List<Document> list = Document.list(DocumentType.INTERNSHIPPOSTERREQUEST, request.getIdInternshipPosterRequest());
			
				for(Document doc : list) {
					Tab tab = new Tab("Requisição de Banca");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			InternshipJury jury = new InternshipJuryBO().findByInternship(internship.getIdInternship());
			
			if((jury != null) && (jury.getIdInternshipJury() != 0)) {
				List<Document> list = Document.list(DocumentType.INTERNSHIPJURY, jury.getIdInternshipJury());
				
				for(Document doc : list) {
					Tab tab = new Tab("Ata da Defesa");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			if(this.tabsToPages.size() > 0) {
				((Component)this.tabsToPages.values().toArray()[0]).setVisible(true);
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Carregar Documentos", e.getMessage());
		}
	}
	
	private Internship getSelectedInternship() {
		InternshipDataSource internship = this.gridInternship.asSingleSelect().getValue();
		
		if(internship == null) {
			return null;
		} else {
			for(Internship i : this.listInternship) {
				if(i.getIdInternship() == internship.getId()) {
					return i;
				}
			}
			
			return null;
		}
	}
	
	private void editInternship() {
		Internship internship = this.getSelectedInternship();
		
		if((internship == null) || (internship.getIdInternship() == 0)) {
			Notification.showWarningNotification("Visualizar Estágio", "Selecione o estágio para visualizar o registro.");
		} else {
			EditInternshipWindow window = new EditInternshipWindow(internship, null);
			window.open();
		}
	}
	
	private void editJury() {
		Internship internship = this.getSelectedInternship();
		
		if((internship == null) || (internship.getIdInternship() == 0)) {
			Notification.showWarningNotification("Visualizar Banca", "Selecione o estágio para visualizar a banca.");
		} else {
			try {
				InternshipJury jury = new InternshipJuryBO().findByInternship(internship.getIdInternship());
				
				if((jury == null) || (jury.getIdInternshipJury() == 0)) {
					Notification.showWarningNotification("Visualizar Banca", "Não foi agendada banca para este estágio.");
				} else {
					EditInternshipJuryWindow window = new EditInternshipJuryWindow(jury, null);
					window.open();
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Banca", e.getMessage());
			}
		}
	}

}
