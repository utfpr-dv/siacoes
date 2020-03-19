package br.edu.utfpr.dv.siacoes.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.window.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipWindow;

public class StudentSigesHistory extends HorizontalLayout {
	
	private final Grid gridInternship;
	private final TabSheet tabDocuments;
	private final Button buttonInternship;
	private final Button buttonJury;
	
	private final List<Internship> listInternship;
	private SigesConfig config;
	
	public StudentSigesHistory(int idStudent, int idDepartment) throws Exception {
		this.config = new SigesConfigBO().findByDepartment(idDepartment);
		
		this.listInternship = new ArrayList<Internship>();
		
		this.gridInternship = new Grid();
		this.gridInternship.addColumn("Empresa", String.class);
		this.gridInternship.addColumn("Tipo", String.class);
		this.gridInternship.addColumn("Data Início", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.gridInternship.setSizeFull();
		this.gridInternship.getColumns().get(1).setWidth(150);
		this.gridInternship.getColumns().get(2).setWidth(125);
		this.gridInternship.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				loadDocuments();
			}
		});
		
		this.buttonInternship = new Button("Registro de Estágio", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editInternship();
            }
        });
		this.buttonInternship.setIcon(FontAwesome.STICKY_NOTE_O);
		this.buttonInternship.setSizeFull();
		
		this.buttonJury = new Button("Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editJury();
            }
        });
		this.buttonJury.setIcon(FontAwesome.CALENDAR_CHECK_O);
		this.buttonJury.setSizeFull();
		
		HorizontalLayout layoutButtons = new HorizontalLayout(this.buttonInternship, this.buttonJury);
		layoutButtons.setSpacing(true);
		layoutButtons.setWidth("100%");
		
		VerticalLayout layoutGrid = new VerticalLayout(this.gridInternship, layoutButtons);
		layoutGrid.setSpacing(true);
		layoutGrid.setExpandRatio(this.gridInternship, 1.0f);
		layoutGrid.setSizeFull();
		
		this.tabDocuments = new TabSheet();
		this.tabDocuments.setSizeFull();
		
		this.setSpacing(true);
		this.setMargin(true);
		this.setSizeFull();
		
		this.addComponent(layoutGrid);
		
		if(this.config.isUseDigitalSignature()) {
			this.addComponent(this.tabDocuments);
		}
		
		this.loadInternships(idStudent, idDepartment);
	}
	
	private void loadInternships(int idStudent, int idDepartment) throws Exception {
		List<Internship> list = new InternshipBO().listByStudent(idStudent, idDepartment);
		
		this.listInternship.addAll(list);
		
		for(Internship internship : this.listInternship) {
			this.gridInternship.addRow(internship.getCompany().getName(), internship.getType().toString(), internship.getStartDate());
		}
	}
	
	private void loadDocuments() {
		Internship internship = this.getSelectedInternship();
		
		if((internship == null) || (internship.getIdInternship() == 0)) {
			return;
		}
		
		this.tabDocuments.removeAllComponents();
		
		try {
			InternshipPosterRequest request = new InternshipPosterRequestBO().findByInternship(internship.getIdInternship());
			
			if((request != null) && (request.getIdInternshipPosterRequest() != 0)) {
				List<Document> list = Document.list(DocumentType.INTERNSHIPPOSTERREQUEST, request.getIdInternshipPosterRequest());
			
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Requisição de Banca");
				}
			}
			
			InternshipJury jury = new InternshipJuryBO().findByInternship(internship.getIdInternship());
			
			if((jury != null) && (jury.getIdInternshipJury() != 0)) {
				List<Document> list = Document.list(DocumentType.INTERNSHIPJURY, jury.getIdInternshipJury());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Ata da Defesa");
				}
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Carregar Documentos", e.getMessage());
		}
	}
	
	private Internship getSelectedInternship() {
		Object itemId = this.gridInternship.getSelectedRow();
		int index = -1;

    	if(itemId == null) {
    		index = -1;
    	} else {
    		index = ((int)itemId) - 1;	
    	}
    	
    	if((index >= 0) && (index < this.listInternship.size())) {
    		return this.listInternship.get(index);
    	} else {
    		return null;
    	}
	}
	
	private void editInternship() {
		Internship internship = this.getSelectedInternship();
		
		if((internship == null) || (internship.getIdInternship() == 0)) {
			Notification.showWarningNotification("Visualizar Estágio", "Selecione o estágio para visualizar o registro.");
		} else {
			UI.getCurrent().addWindow(new EditInternshipWindow(internship, null));
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
					UI.getCurrent().addWindow(new EditInternshipJuryWindow(jury, null));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Banca", e.getMessage());
			}
		}
	}

}
