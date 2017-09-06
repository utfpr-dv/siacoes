package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditProposalAppraiserWindow;

public class ProposalFeedbackView extends ListView {
	
	public static final String NAME = "proposalsfeedback";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonPrintFeedback;
	private final Button buttonDownloadProposal;
	
	private Button.ClickListener listenerClickFeedback;
	private Button.ClickListener listenerClickDownload;
	
	public ProposalFeedbackView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setFiltersVisible(false);
		
		this.setEditCaption("Parecer");
		
		this.buttonPrintFeedback = new Button("Imprimir Parecer");
		this.buttonPrintFeedback.setWidth("150px");
		
		this.buttonDownloadProposal = new Button("Proposta");
		this.buttonDownloadProposal.setWidth("150px");
		
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
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(4).setWidth(125);
		
		this.prepareDownload();
		
		try {
			ProposalBO bo = new ProposalBO();
	    	List<Proposal> list = bo.listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Proposal p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getTitle(), p.getStudent().getName(), p.getSubmissionDate());
				this.addRowId(itemId, p.getIdProposal());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			Notification.show("Listar Propostas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
    }
	
	private void prepareDownload(){
		Object value = getIdSelected();
		
		this.buttonDownloadProposal.removeClickListener(this.listenerClickDownload);
		this.buttonPrintFeedback.removeClickListener(this.listenerClickFeedback);
    	
    	if(value != null){
			try {
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				ProposalAppraiser appraiser = bo.findByAppraiser((int)value, Session.getUser().getIdUser());
				
				UserBO userBo = new UserBO();
				appraiser.setAppraiser(userBo.findById(appraiser.getAppraiser().getIdUser()));
				
				ProposalBO proposalBo = new ProposalBO();
				appraiser.setProposal(proposalBo.findById(appraiser.getProposal().getIdProposal()));
				
				List<ProposalAppraiser> list = new ArrayList<ProposalAppraiser>();
				list.add(appraiser);
				
				new ReportUtils().prepareForPdfReport("ProposalFeedback", "Parecer", list, this.buttonPrintFeedback);
			} catch (Exception e) {
				this.listenerClickFeedback = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Imprimir Parecer", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
				this.buttonPrintFeedback.addClickListener(this.listenerClickFeedback);
			}
			
			try {
            	ProposalBO bo = new ProposalBO();
            	Proposal p = bo.findById((int)value);
            	
            	if(p.getFile() != null){
            		new ExtensionUtils().extendToDownload(p.getTitle() + p.getFileType().getExtension(), p.getFile(), this.buttonDownloadProposal);	
            	}else{
            		this.listenerClickDownload = new Button.ClickListener() {
    		            @Override
    		            public void buttonClick(ClickEvent event) {
    		            	Notification.show("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.", Notification.Type.WARNING_MESSAGE);
    		            }
    		        };
    		        
            		this.buttonDownloadProposal.addClickListener(this.listenerClickDownload);
            	}
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download da Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownloadProposal.addClickListener(this.listenerClickDownload);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonDownloadProposal);
    		new ExtensionUtils().removeAllExtensions(this.buttonPrintFeedback);
    		
    		this.listenerClickFeedback = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Imprimir Parecer", "Selecione um registro para imprimir o parecer", Notification.Type.WARNING_MESSAGE);
	            }
	        };
	        
	        this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download da Proposta", "Selecione um registro para baixar a proposta.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
	        
    		this.buttonPrintFeedback.addClickListener(this.listenerClickFeedback);
    		
    		this.buttonDownloadProposal.addClickListener(this.listenerClickDownload);
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
			
			Notification.show("Cadastrar Parecer", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
