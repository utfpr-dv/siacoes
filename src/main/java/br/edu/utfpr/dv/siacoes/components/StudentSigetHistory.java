package br.edu.utfpr.dv.siacoes.components;

import java.util.ArrayList;
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
import br.edu.utfpr.dv.siacoes.window.EditFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditProjectWindow;
import br.edu.utfpr.dv.siacoes.window.EditProposalWindow;
import br.edu.utfpr.dv.siacoes.window.EditThesisWindow;

public class StudentSigetHistory extends HorizontalLayout {
	
	private final Grid gridProposals;
	private final TabSheet tabDocuments;
	private final Button buttonProposal;
	private final Button buttonProject;
	private final Button buttonJury1;
	private final Button buttonThesis;
	private final Button buttonJury2;
	private final Button buttonFinalDocument;
	
	private final List<Proposal> listProposals;
	private SigetConfig config;
	
	public StudentSigetHistory(int idStudent, int idDepartment) throws Exception {
		this.config = new SigetConfigBO().findByDepartment(idDepartment);
		
		this.listProposals = new ArrayList<Proposal>();
		
		this.gridProposals = new Grid();
		this.gridProposals.addColumn("Título da Proposta", String.class);
		this.gridProposals.addColumn("Semestre", Integer.class);
		this.gridProposals.addColumn("Ano", Integer.class);
		this.gridProposals.setSizeFull();
		this.gridProposals.getColumns().get(1).setWidth(100);
		this.gridProposals.getColumns().get(2).setWidth(100);
		this.gridProposals.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				loadDocuments();
			}
		});
		
		this.buttonProposal = new Button((this.config.isRegisterProposal() ? "Proposta" : "Registro") + " de TCC 1", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editProposal();
            }
        });
		this.buttonProposal.setIcon(this.config.isRegisterProposal() ? FontAwesome.FILE_PDF_O : FontAwesome.USERS);
		this.buttonProposal.setSizeFull();
		
		this.buttonProject = new Button("Projeto de TCC 1", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editProject();
            }
        });
		this.buttonProject.setIcon(FontAwesome.FILE_PDF_O);
		this.buttonProject.setSizeFull();
		
		this.buttonJury1 = new Button("Banca de TCC 1", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editJury1();
            }
        });
		this.buttonJury1.setIcon(FontAwesome.CALENDAR_CHECK_O);
		this.buttonJury1.setSizeFull();
		
		HorizontalLayout layoutButtons1 = new HorizontalLayout(this.buttonProposal, this.buttonProject, this.buttonJury1);
		layoutButtons1.setSpacing(true);
		layoutButtons1.setWidth("100%");
		
		this.buttonThesis = new Button("Monografia de TCC 2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editThesis();
            }
        });
		this.buttonThesis.setIcon(FontAwesome.FILE_PDF_O);
		this.buttonThesis.setSizeFull();
		
		this.buttonJury2 = new Button("Banca de TCC 2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editJury2();
            }
        });
		this.buttonJury2.setIcon(FontAwesome.CALENDAR_CHECK_O);
		this.buttonJury2.setSizeFull();
		
		this.buttonFinalDocument = new Button("Versão Final de TCC 2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editFinalDocument();
            }
        });
		this.buttonFinalDocument.setIcon(FontAwesome.FILE_PDF_O);
		this.buttonFinalDocument.setSizeFull();
		
		HorizontalLayout layoutButtons2 = new HorizontalLayout(this.buttonThesis, this.buttonJury2, this.buttonFinalDocument);
		layoutButtons2.setSpacing(true);
		layoutButtons2.setWidth("100%");
		
		VerticalLayout layoutGrid = new VerticalLayout(this.gridProposals, layoutButtons1, layoutButtons2);
		layoutGrid.setSpacing(true);
		layoutGrid.setExpandRatio(this.gridProposals, 1.0f);
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
		
		this.loadProposals(idStudent, idDepartment);
	}
	
	private void loadProposals(int idStudent, int idDepartment) throws Exception {
		List<Proposal> list = new ProposalBO().listByStudent(idStudent, idDepartment);
		
		this.listProposals.addAll(list);
		
		for(Proposal proposal : this.listProposals) {
			this.gridProposals.addRow(proposal.getTitle(), proposal.getSemester(), proposal.getYear());
		}
	}
	
	private void loadDocuments() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			return;
		}
		
		this.tabDocuments.removeAllComponents();
		
		try {
			List<Document> list = Document.list(DocumentType.SUPERVISORAGREEMENT, proposal.getIdProposal());
			
			for(Document doc : list) {
				this.tabDocuments.addTab(new SignedDocument(doc), "Concordância do Orientador");
			}
			
			List<ProposalAppraiser> appraisers = new ProposalAppraiserBO().listAppraisers(proposal.getIdProposal());
			for(ProposalAppraiser appraiser : appraisers) {
				list = Document.list(DocumentType.APPRAISERFEEDBACK, appraiser.getIdProposalAppraiser());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Parecer da Proposta");
				}
			}
			
			JuryRequest request = new JuryRequestBO().findByStage(proposal.getIdProposal(), 1);
			if((request != null) && (request.getIdJuryRequest() != 0)) {
				list = Document.list(DocumentType.JURYREQUEST, request.getIdJuryRequest());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Requisição de Banca de TCC 1");
				}
			}
			
			List<Integer> attendances = new AttendanceBO().listIdGroup(proposal.getIdProposal(), 1);
			for(int id : attendances) {
				list = Document.list(DocumentType.ATTENDANCE, id);
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Reuniões de TCC 1");
				}
			}
			
			Jury jury = new JuryBO().findByStage(proposal.getIdProposal(), 1);
			if((jury != null) && (jury.getIdJury() != 0)) {
				list = Document.list(DocumentType.JURY, jury.getIdJury());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Banca de TCC 1");
				}
			}
			
			request = new JuryRequestBO().findByStage(proposal.getIdProposal(), 2);
			if((request != null) && (request.getIdJuryRequest() != 0)) {
				list = Document.list(DocumentType.JURYREQUEST, request.getIdJuryRequest());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Requisição de Banca de TCC 2");
				}
			}
			
			attendances = new AttendanceBO().listIdGroup(proposal.getIdProposal(), 2);
			for(int id : attendances) {
				list = Document.list(DocumentType.ATTENDANCE, id);
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Reuniões de TCC 2");
				}
			}
			
			jury = new JuryBO().findByStage(proposal.getIdProposal(), 2);
			if((jury != null) && (jury.getIdJury() != 0)) {
				list = Document.list(DocumentType.JURY, jury.getIdJury());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Banca de TCC 2");
				}
			}
			
			List<SupervisorChange> changes = new SupervisorChangeBO().listByProposal(proposal.getIdProposal());
			for(SupervisorChange change : changes) {
				list = Document.list(DocumentType.SUPERVISORCHANGE, change.getIdSupervisorChange());
				
				for(Document doc : list) {
					this.tabDocuments.addTab(new SignedDocument(doc), "Alteração de Orientador");
				}
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Carregar Documentos", e.getMessage());
		}
	}
	
	private Proposal getSelectedProposal() {
		Object itemId = this.gridProposals.getSelectedRow();
		int index = -1;

    	if(itemId == null) {
    		index = -1;
    	} else {
    		index = ((int)itemId) - 1;	
    	}
    	
    	if((index >= 0) && (index < this.listProposals.size())) {
    		return this.listProposals.get(index);
    	} else {
    		return null;
    	}
	}
	
	private void editProposal() {
		Proposal proposal = this.getSelectedProposal();
		
		if((proposal == null) || (proposal.getIdProposal() == 0)) {
			Notification.showWarningNotification("Visualizar " + (this.config.isRegisterProposal() ? "Proposta" : "Registro") + " de TCC 1", "Selecione o registro para visualizar.");
		} else {
			UI.getCurrent().addWindow(new EditProposalWindow(proposal, null, this.config.isRegisterProposal()));
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
					UI.getCurrent().addWindow(new EditProjectWindow(project, null));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
					UI.getCurrent().addWindow(new EditJuryWindow(jury, null));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
					UI.getCurrent().addWindow(new EditThesisWindow(thesis, null));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
					UI.getCurrent().addWindow(new EditJuryWindow(jury, null));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
						UI.getCurrent().addWindow(new EditFinalDocumentWindow(doc, null));
					}
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.showErrorNotification("Visualizar Versão Final de TCC 2", e.getMessage());
			}
		}
	}

}
