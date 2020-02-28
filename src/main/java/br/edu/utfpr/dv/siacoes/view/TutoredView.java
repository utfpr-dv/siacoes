package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.bo.TutoredBO;
import br.edu.utfpr.dv.siacoes.components.Notification;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Tutored;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryRequestWindow;
import br.edu.utfpr.dv.siacoes.window.EditSupervisorAgreementWindow;
import br.edu.utfpr.dv.siacoes.window.EditSupervisorChangeWindow;
import br.edu.utfpr.dv.siacoes.window.EditSupervisorIndicationWindow;
import br.edu.utfpr.dv.siacoes.window.EditTutoredWindow;

public class TutoredView extends ListView {
	
	public static final String NAME = "tutored";
	
	private SigetConfig config;
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	private final Button buttonSupervisorAgreement;
	private final Button buttonPrintSupervisorAgreement;
	private final Button buttonSupervisorIndication;
	private final Button buttonJuryRequest;
	private final Button buttonPrintJuryRequest;
	private final Button buttonStatementStage1;
	private final Button buttonStatementStage2;
	private final Button buttonSupervisorChange;
	
	public TutoredView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Orientações de TCC");
		
		this.setProfilePerimissions(UserProfile.SUPERVISOR);
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		this.buttonSupervisorAgreement = new Button("Termo de Concord.", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	supervisorAgreement();
            }
        });
		this.buttonSupervisorAgreement.setIcon(FontAwesome.CHECK);
		this.buttonSupervisorAgreement.setDescription("Preencher Termo de Concordância de Orientação");
		
		this.buttonPrintSupervisorAgreement = new Button("Imp. Termo de Conc.", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadSupervisorAgreement();
            }
        });
		this.buttonPrintSupervisorAgreement.setDescription("Imprimir Termo de Concordância de Orientação");
		
		this.buttonSupervisorIndication = new Button("Indicar Avaliadores", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	supervisorIndication();
            }
        });
		this.buttonSupervisorIndication.setIcon(FontAwesome.USER_PLUS);
		this.buttonSupervisorIndication.setDescription("Indicar avaliadores para a Proposta de TCC 1");
		
		this.buttonJuryRequest = new Button("Solic. Agend. Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	juryRequest();
            }
        });
		this.buttonJuryRequest.setIcon(FontAwesome.CALENDAR_PLUS_O);
		this.buttonJuryRequest.setDescription("Solicitar agendamento de banca");
		
		this.buttonPrintJuryRequest = new Button("Imp. Agend. de Banca", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadJuryRequest();
            }
        });
		this.buttonPrintJuryRequest.setDescription("Imprimir solicitação de agendamento de banca");
		
		this.buttonSupervisorChange = new Button("Alterar Orientador", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	supervisorChange();
            }
        });
		this.buttonSupervisorChange.setIcon(FontAwesome.USERS);
		this.buttonSupervisorChange.setDescription("Solicitar alteração de orientador");
		
		this.buttonStatementStage1 = new Button("Declaração TCC 1", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement(1);
            }
        });
		this.buttonStatementStage1.setIcon(FontAwesome.FILE_PDF_O);
		this.buttonStatementStage1.setDescription("Imprimir Declaração de Orientação de TCC 1");
		
		this.buttonStatementStage2 = new Button("Declaração TCC 2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement(2);
            }
        });
		this.buttonStatementStage2.setIcon(FontAwesome.FILE_PDF_O);
		this.buttonStatementStage2.setDescription("Imprimir Declaração de Orientação de TCC 2");
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
		}
		
		if(this.config.isSupervisorAgreement()) {
			this.addActionButton(this.buttonSupervisorAgreement);
			this.addActionButton(this.buttonPrintSupervisorAgreement);
		}
		
		if(this.config.isRegisterProposal() && (this.config.getSupervisorIndication() > 0)) {
			this.addActionButton(this.buttonSupervisorIndication);
		}
		
		if(this.config.isSupervisorJuryRequest()) {
			this.addActionButton(this.buttonJuryRequest);
			this.addActionButton(this.buttonPrintJuryRequest);
		}
		
		this.addActionButton(this.buttonSupervisorChange);
		this.addActionButton(this.buttonStatementStage1);
		this.addActionButton(this.buttonStatementStage2);
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(FontAwesome.SEARCH);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Sem.", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		
		this.getGrid().getColumns().get(1).setWidth(75);
		this.getGrid().getColumns().get(3).setWidth(75);
		this.getGrid().getColumns().get(4).setWidth(100);
		
		try {
			TutoredBO bo = new TutoredBO();
	    	List<Tutored> list = bo.listBySupervisor(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Tutored t : list){
				Object itemId = this.getGrid().addRow(t.getStudent().getName(), t.getStage(), t.getTitle(), t.getSemester(), t.getYear());
				this.addRowId(itemId, t.getProposal().getIdProposal());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Orientandos", e.getMessage());
		}
	}
	
	private void downloadProfessorStatement(int stage){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Gerar Declaração", "Selecione um registro para gerar a declaração.");
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getThesisProfessorStatement(Session.getUser().getIdUser(), (int)value, stage);
				
				this.showReport(report);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Gerar Declaração", e.getMessage());
			}
		}
	}
	
	private void supervisorIndication() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Indicação de Avaliadores", "Selecione um registro para indicar os avaliadores.");
		} else {
			try {
				Proposal proposal = new ProposalBO().findById((int)value);

				if(proposal.getSupervisor().getIdUser() != Session.getUser().getIdUser()) {
					this.showWarningNotification("Indicação de Avaliadores", "Apenas o Professor Orientador pode fazer a indicação de avaliadores.");
				} else if(this.config.isSupervisorAgreement() && (proposal.getSupervisorFeedback() != ProposalFeedback.APPROVED)) {
					this.showWarningNotification("Indicação de Avaliadores", "É necessário preencher o Termo de Concordância de Orientação antes de indicar os avaliadores.");
				} else if(this.config.isUseDigitalSignature() && !Document.hasSignature(DocumentType.SUPERVISORAGREEMENT, proposal.getIdProposal(), proposal.getSupervisor().getIdUser())) {
					this.showWarningNotification("Indicação de Avaliadores", "É necessário assinar o Termo de Condordância de Orientação antes de indicar os avaliadores.");
				} else {
					UI.getCurrent().addWindow(new EditSupervisorIndicationWindow(proposal, this));	
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Indicação de Avaliadores", e.getMessage());
			}
		}
	}
	
	private void juryRequest() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Agendamento de Banca", "Selecione um registro para solicitar o agendamento de banca.");
		} else {
			try {
				User supervisor = new SupervisorChangeBO().findCurrentSupervisor((int)value);
				
				if(supervisor.getIdUser() != Session.getUser().getIdUser()) {
					this.showWarningNotification("Agendamento de Banca", "Apenas o Professor Orientador pode solicitar o agendamento de banca.");
				} else {
					JuryRequest jury = new JuryRequestBO().prepareJuryRequest((int)value, this.comboSemester.getSemester(), this.textYear.getYear());
					
					UI.getCurrent().addWindow(new EditJuryRequestWindow(jury, this));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Agendamento de Banca", e.getMessage());
			}
		}
	}
	
	private void downloadJuryRequest() {
		Object value = getIdSelected();
		
		if(value != null){
			try {
				User supervisor = new SupervisorChangeBO().findCurrentSupervisor((int)value);
				
				if(supervisor.getIdUser() != Session.getUser().getIdUser()) {
					this.showWarningNotification("Imprimir Agendamento de Banca", "Apenas o Professor Orientador pode imprimir o agendamento de banca.");
				} else {
					JuryRequestBO bo = new JuryRequestBO();
					
					this.showReport(bo.getJuryRequestForm((int)value, this.comboSemester.getSemester(), this.textYear.getYear()));
				}
			} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Agendamento de Banca", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Agendamento de Banca", "Selecione um registro para imprimir a Solicitação de Agendamento de Banca.");
		}
	}
	
	private void supervisorAgreement() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Termo de Concordância", "Selecione um registro para preencher o termo de concordância de orientação.");
		} else {
			try {
				User supervisor = new SupervisorChangeBO().findCurrentSupervisor((int)value);
				
				if(supervisor.getIdUser() != Session.getUser().getIdUser()) {
					this.showWarningNotification("Termo de Concordância", "Apenas o Professor Orientador pode preencher o Termo de Concordância de Orientação.");
				} else {
					Proposal proposal = new ProposalBO().findById((int)value);
					
					UI.getCurrent().addWindow(new EditSupervisorAgreementWindow(proposal, this));
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Termo de Concordância", e.getMessage());
			}
		}
	}
	
	private void downloadSupervisorAgreement() {
		Object value = getIdSelected();
		
		if(value != null){
			try {
				User supervisor = new SupervisorChangeBO().findCurrentSupervisor((int)value);
				
				if(supervisor.getIdUser() != Session.getUser().getIdUser()) {
					this.showWarningNotification("Imprimir Termo de Concordância", "Apenas o Professor Orientador pode imprimir o Termo de Concordância de Orientação.");
				} else {
					ProposalBO bo = new ProposalBO();
					
					this.showReport(bo.getSupervisorFeedbackReport((int)value));
				}
			} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Termo de Concordância", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Termo de Concordância", "Selecione um registro para imprimir o Termo de Concordância de Orientação.");
		}
	}
	
	private void supervisorChange() {
		Object value = getIdSelected();
		
		if(value != null){
			try {
				if(new SupervisorChangeBO().findCurrentSupervisor((int)value).getIdUser() == Session.getUser().getIdUser()) {
					Proposal proposal = new ProposalBO().findById((int)value);
					Thesis thesis = new ThesisBO().findByProposal(proposal.getIdProposal());
					Jury jury = null;
					
					if((thesis != null) && (thesis.getIdThesis() != 0)) {
						jury = new JuryBO().findByThesis(0);
					}
					
					if((jury != null) && (jury.getIdJury() != 0)) {
						Notification.showWarningNotification("Alterar Orientador", "Não é possível efetuar a alteração de orientador pois a banca de TCC 2 já foi agendada.");
					} else {
						UI.getCurrent().addWindow(new EditSupervisorChangeWindow(proposal, this, true));	
					}
				} else {
					this.showWarningNotification("Alteração de Orientador", "Apenas o Professor Orientador pode solicitar a alteração de orientador.");
				}
			} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Alteração de Orientador", e.getMessage());
			}
		} else {
			this.showWarningNotification("Alteração de Orientador", "Selecione um acadêmico para solicitar a alteração de Professor Orientador.");
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		UI.getCurrent().addWindow(new EditTutoredWindow((int)id, this));
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
