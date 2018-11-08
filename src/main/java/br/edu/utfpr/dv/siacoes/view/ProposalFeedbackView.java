package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditProposalAppraiserWindow;

public class ProposalFeedbackView extends ListView {
	
	public static final String NAME = "proposalfeedback";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonPrintFeedback;
	private final Button buttonDownloadProposal;
	
	public ProposalFeedbackView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Parecer da Proposta de TCC 1");
		
		this.setProfilePerimissions(UserProfile.SUPERVISOR);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Emitir Parecer");
		this.setEditIcon(FontAwesome.CHECK);
		
		this.buttonPrintFeedback = new Button("Imprimir Parecer", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	printFeedback();
            }
        });
		this.buttonPrintFeedback.setWidth("150px");
		this.buttonPrintFeedback.setIcon(FontAwesome.PRINT);
		
		this.buttonDownloadProposal = new Button("Baixar Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProposal();
            }
        });
		this.buttonDownloadProposal.setWidth("150px");
		this.buttonDownloadProposal.setIcon(FontAwesome.DOWNLOAD);
		
		this.addActionButton(this.buttonPrintFeedback);
		this.addActionButton(this.buttonDownloadProposal);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}
	
	protected void loadGrid(){
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Parecer", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(4).setWidth(125);
		this.getGrid().getColumns().get(5).setWidth(150);
		
		try {
			ProposalBO bo = new ProposalBO();
			ProposalAppraiserBO abo = new ProposalAppraiserBO();
	    	List<Proposal> list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Proposal p : list){
	    		ProposalAppraiser appraiser = abo.findByAppraiser(p.getIdProposal(), Session.getUser().getIdUser());
	    		
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getTitle(), p.getStudent().getName(), p.getSubmissionDate(), appraiser.getFeedback().toString());
				this.addRowId(itemId, p.getIdProposal());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			this.showErrorNotification("Listar Propostas", e.getMessage());
		}
    }
	
	private void downloadProposal() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
            	Proposal proposal = new ProposalBO().findById((int)value);
            	SigetConfig config = new SigetConfigBO().findByDepartment(proposal.getDepartment().getIdDepartment());
            	
            	if(config.isSupervisorAgreement() && (proposal.getSupervisorFeedback() != ProposalFeedback.APPROVED)) {
            		this.showWarningNotification("Download da Proposta", "O Professor Orientador ainda não preencheu o Termo de Concordância de Orientação.");
            	} else if(proposal.getFile() != null) {
            		this.showReport(proposal.getFile());
            	} else {
            		this.showWarningNotification("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.");
            	}
        	} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Download da Proposta", e.getMessage());
			}
		} else {
			this.showWarningNotification("Download da Proposta", "Selecione um registro para efetuar do download da proposta.");
		}
	}
	
	private void printFeedback() {
		Object value = getIdSelected();
		
		if(value != null){
			try {
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				ProposalAppraiser appraiser = bo.findByAppraiser((int)value, Session.getUser().getIdUser());
				
				if(appraiser.getFeedback() == ProposalFeedback.NONE) {
					this.showWarningNotification("Imprimir Parecer", "É necessário preencher o parecer antes de imprimir.");
				} else {
					this.showReport(bo.getFeedbackReport(appraiser));	
				}
			} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Parecer", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Parecer", "Selecione um registro para imprimir o parecer.");
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			ProposalAppraiserBO bo = new ProposalAppraiserBO();
			ProposalAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
			
			UI.getCurrent().addWindow(new EditProposalAppraiserWindow(appraiser, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Cadastrar Parecer", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}

}
