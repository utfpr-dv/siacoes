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

import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.ui.grid.ProposalDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProjectWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProposalWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditThesisWindow;

public class StudentSigetHistory extends HorizontalLayout {
	
	private final Grid<ProposalDataSource> gridProposals;
	private final Tabs tabDocuments;
	private final Button buttonProposal;
	private final Button buttonProject;
	private final Button buttonJury1;
	private final Button buttonThesis;
	private final Button buttonJury2;
	private final Button buttonFinalDocument;
	
	private final List<Proposal> listProposals;
	private SigetConfig config;
	
	private final Map<Tab, Component> tabsToPages;
	private final Div pages;
	
	public StudentSigetHistory(int idStudent, int idDepartment) throws Exception {
		this.config = new SigetConfigBO().findByDepartment(idDepartment);
		
		this.listProposals = new ArrayList<Proposal>();
		
		this.gridProposals = new Grid<ProposalDataSource>();
		this.gridProposals.setSelectionMode(SelectionMode.SINGLE);
		this.gridProposals.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridProposals.addColumn(ProposalDataSource::getTitle).setHeader("Título da Proposta");
		this.gridProposals.addColumn(ProposalDataSource::getSemester).setHeader("Semestre").setFlexGrow(0).setWidth("100px");
		this.gridProposals.addColumn(ProposalDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("100px");
		this.gridProposals.setSizeFull();
		this.gridProposals.addSelectionListener(event -> {
			loadDocuments();
		});
		
		this.buttonProposal = new Button((this.config.isRegisterProposal() ? "Proposta" : "Registro") + " de TCC 1", event -> {
            editProposal();
        });
		this.buttonProposal.setIcon(new Icon(this.config.isRegisterProposal() ? VaadinIcon.FILE_TEXT_O : VaadinIcon.USERS));
		this.buttonProposal.setWidthFull();
		
		this.buttonProject = new Button("Projeto de TCC 1", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            editProject();
        });
		this.buttonProject.setSizeFull();
		
		this.buttonJury1 = new Button("Banca de TCC 1", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
            editJury1();
        });
		this.buttonJury1.setSizeFull();
		
		HorizontalLayout layoutButtons1 = new HorizontalLayout(this.buttonProposal, this.buttonProject, this.buttonJury1);
		layoutButtons1.setSpacing(true);
		layoutButtons1.setMargin(false);
		layoutButtons1.setPadding(false);
		layoutButtons1.setWidth("100%");
		
		this.buttonThesis = new Button("Monografia de TCC 2", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            editThesis();
        });
		this.buttonThesis.setSizeFull();
		
		this.buttonJury2 = new Button("Banca de TCC 2", new Icon(VaadinIcon.CALENDAR_CLOCK), event -> {
            editJury2();
        });
		this.buttonJury2.setSizeFull();
		
		this.buttonFinalDocument = new Button("Versão Final de TCC 2", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            editFinalDocument();
        });
		this.buttonFinalDocument.setSizeFull();
		
		HorizontalLayout layoutButtons2 = new HorizontalLayout(this.buttonThesis, this.buttonJury2, this.buttonFinalDocument);
		layoutButtons2.setSpacing(true);
		layoutButtons2.setMargin(false);
		layoutButtons2.setPadding(false);
		layoutButtons2.setWidth("100%");
		
		VerticalLayout vl = new VerticalLayout(this.gridProposals);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		VerticalLayout layoutGrid = new VerticalLayout(vl, layoutButtons1, layoutButtons2);
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
		
		this.loadProposals(idStudent, idDepartment);
	}
	
	private void loadProposals(int idStudent, int idDepartment) throws Exception {
		List<Proposal> list = new ProposalBO().listByStudent(idStudent, idDepartment);
		
		this.listProposals.addAll(list);
		
		this.gridProposals.setItems(ProposalDataSource.load(this.listProposals));
	}
	
	private void loadDocuments() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			return;
		}
		
		this.tabDocuments.removeAll();
		this.pages.removeAll();
		this.tabsToPages.clear();
		
		try {
			List<Document> list = Document.list(DocumentType.SUPERVISORAGREEMENT, proposal.getIdProposal());
			
			for(Document doc : list) {
				Tab tab = new Tab("Concordância do Orientador");
				SignedDocument page = new SignedDocument(doc, false);
				page.setVisible(false);
				
				this.tabDocuments.add(tab);
				this.pages.add(page);
				this.tabsToPages.put(tab, page);
			}
			
			List<ProposalAppraiser> appraisers = new ProposalAppraiserBO().listAppraisers(proposal.getIdProposal());
			for(ProposalAppraiser appraiser : appraisers) {
				list = Document.list(DocumentType.APPRAISERFEEDBACK, appraiser.getIdProposalAppraiser());
				
				for(Document doc : list) {
					Tab tab = new Tab("Parecer da Proposta");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			JuryRequest request = new JuryRequestBO().findByStage(proposal.getIdProposal(), 1);
			if((request != null) && (request.getIdJuryRequest() != 0)) {
				list = Document.list(DocumentType.JURYREQUEST, request.getIdJuryRequest());
				
				for(Document doc : list) {
					Tab tab = new Tab("Requisição de Banca de TCC 1");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			List<Integer> attendances = new AttendanceBO().listIdGroup(proposal.getIdProposal(), 1);
			for(int id : attendances) {
				list = Document.list(DocumentType.ATTENDANCE, id);
				
				for(Document doc : list) {
					Tab tab = new Tab("Reuniões de TCC 1");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			Jury jury = new JuryBO().findByStage(proposal.getIdProposal(), 1);
			if((jury != null) && (jury.getIdJury() != 0)) {
				list = Document.list(DocumentType.JURY, jury.getIdJury());
				
				for(Document doc : list) {
					Tab tab = new Tab("Banca de TCC 1");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			request = new JuryRequestBO().findByStage(proposal.getIdProposal(), 2);
			if((request != null) && (request.getIdJuryRequest() != 0)) {
				list = Document.list(DocumentType.JURYREQUEST, request.getIdJuryRequest());
				
				for(Document doc : list) {
					Tab tab = new Tab("Requisição de Banca de TCC 2");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			attendances = new AttendanceBO().listIdGroup(proposal.getIdProposal(), 2);
			for(int id : attendances) {
				list = Document.list(DocumentType.ATTENDANCE, id);
				
				for(Document doc : list) {
					Tab tab = new Tab("Reuniões de TCC 2");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			jury = new JuryBO().findByStage(proposal.getIdProposal(), 2);
			if((jury != null) && (jury.getIdJury() != 0)) {
				list = Document.list(DocumentType.JURY, jury.getIdJury());
				
				for(Document doc : list) {
					Tab tab = new Tab("Banca de TCC 2");
					SignedDocument page = new SignedDocument(doc, false);
					page.setVisible(false);
					
					this.tabDocuments.add(tab);
					this.pages.add(page);
					this.tabsToPages.put(tab, page);
				}
			}
			
			List<SupervisorChange> changes = new SupervisorChangeBO().listByProposal(proposal.getIdProposal());
			for(SupervisorChange change : changes) {
				list = Document.list(DocumentType.SUPERVISORCHANGE, change.getIdSupervisorChange());
				
				for(Document doc : list) {
					Tab tab = new Tab("Alteração de Orientador");
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
	
	private Proposal getSelectedProposal() {
		ProposalDataSource p = this.gridProposals.asSingleSelect().getValue();
		
		if(p == null) {
			return null;
		} else {
			for(Proposal proposal : this.listProposals) {
				if(proposal.getIdProposal() == p.getId()) {
					return proposal;
				}
			}
			
			return null;
		}
	}
	
	private void editProposal() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar " + (this.config.isRegisterProposal() ? "Proposta" : "Registro") + " de TCC 1", "Selecione o registro para visualizar.");
		} else {
			EditProposalWindow window = new EditProposalWindow(proposal, null, this.config.isRegisterProposal());
			window.open();
		}
	}
	
	private void editProject() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar Projeto de TCC 1", "Selecione o registro para visualizar o Projeto de TCC 1.");
		} else {
			try {
				Project project = new ProjectBO().findByProposal(proposal.getIdProposal());
				
				if((project == null) || (project.getIdProject() == 0)) {
					Notification.showWarningNotification("Visualizar Projeto de TCC 1", "O Projeto de TCC 1 ainda não foi submetido pelo acadêmico.");
				} else {
					EditProjectWindow window = new EditProjectWindow(project, null);
					window.open();
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Projeto de TCC 1", e.getMessage());
			}
		}
	}
	
	private void editJury1() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar Banca de TCC 1", "Selecione o registro para visualizar a Banca de TCC 1.");
		} else {
			try {
				Jury jury = new JuryBO().findByStage(proposal.getIdProposal(), 1);
				
				if((jury == null) || (jury.getIdJury() == 0)) {
					Notification.showWarningNotification("Visualizar Banca de TCC 1", "A Banca de TCC 1 ainda não agendada.");
				} else {
					EditJuryWindow window = new EditJuryWindow(jury, null);
					window.open();
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Banca de TCC 1", e.getMessage());
			}
		}
	}
	
	private void editThesis() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar Monografia de TCC 2", "Selecione o registro para visualizar a Monografia de TCC 2.");
		} else {
			try {
				Thesis thesis = new ThesisBO().findByProposal(proposal.getIdProposal());
				
				if((thesis == null) || (thesis.getIdThesis() == 0)) {
					Notification.showWarningNotification("Visualizar Monografia de TCC 2", "A Monografia de TCC 2 ainda não foi submetida pelo acadêmico.");
				} else {
					EditThesisWindow window = new EditThesisWindow(thesis, null);
					window.open();
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Monografia de TCC 2", e.getMessage());
			}
		}
	}
	
	private void editJury2() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar Banca de TCC 2", "Selecione o registro para visualizar a Banca de TCC 2.");
		} else {
			try {
				Jury jury = new JuryBO().findByStage(proposal.getIdProposal(), 2);
				
				if((jury == null) || (jury.getIdJury() == 0)) {
					Notification.showWarningNotification("Visualizar Banca de TCC 2", "A Banca de TCC 2 ainda não agendada.");
				} else {
					EditJuryWindow window = new EditJuryWindow(jury, null);
					window.open();
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Banca de TCC 2", e.getMessage());
			}
		}
	}
	
	private void editFinalDocument() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar Versão Final de TCC 2", "Selecione o registro para visualizar a Versão Final da Monografia de TCC 2.");
		} else {
			try {
				Thesis thesis = new ThesisBO().findByProposal(proposal.getIdProposal());
				
				if((thesis == null) || (thesis.getIdThesis() == 0)) {
					Notification.showWarningNotification("Visualizar Versão Final de TCC 2", "A Versão Final da Monografia de TCC 2 ainda não foi submetida pelo acadêmico.");
				} else {
					FinalDocument doc = new FinalDocumentBO().findByThesis(thesis.getIdThesis());
					
					if((doc == null) || (doc.getIdFinalDocument() == 0)) {
						Notification.showWarningNotification("Visualizar Versão Final de TCC 2", "A Versão Final da Monografia de TCC 2 ainda não foi submetida pelo acadêmico.");
					} else {
						EditFinalDocumentWindow window = new EditFinalDocumentWindow(doc, null);
						window.open();
					}
				}
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Versão Final de TCC 2", e.getMessage());
			}
		}
	}

}
