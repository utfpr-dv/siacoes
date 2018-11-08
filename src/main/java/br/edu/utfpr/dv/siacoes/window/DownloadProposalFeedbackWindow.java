package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ImageRenderer;

import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;

public class DownloadProposalFeedbackWindow extends BasicWindow {

	private final Proposal proposal;
	
	private final Grid grid;
	private final Button buttonComments;
	private final Button buttonDownload;
	
	private List<ProposalAppraiser> appraisers;
	
	public DownloadProposalFeedbackWindow(Proposal proposal) {
		super("Feedback dos Avaliadores");
		
		if(proposal == null) {
			this.proposal = new Proposal();
		} else {
			this.proposal = proposal;
		}
		
		this.grid = new Grid();
		this.grid.addColumn("Avaliador", String.class);
		this.grid.addColumn("Parecer", String.class);
		this.grid.addColumn("Arq. Compl.", Resource.class).setRenderer(new ImageRenderer());
		this.grid.getColumns().get(2).setWidth(100);
		this.grid.setWidth("500px");
		this.grid.setHeight("200px");
		
		this.buttonComments = new Button("Observações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	viewComments();
            }
        });
		this.buttonComments.setIcon(FontAwesome.SEARCH);
		this.buttonComments.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.buttonComments.setWidth("150px");
		
		this.buttonDownload = new Button("Down. Arq. Comentado", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFeedback();
            }
        });
		this.buttonDownload.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownload.setWidth("250px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.buttonComments, this.buttonDownload);
		h1.setSpacing(true);
		
		VerticalLayout vl = new VerticalLayout(this.grid, h1);
		vl.setSpacing(true);
		vl.setMargin(true);
		
		this.setContent(vl);
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
        
        this.loadFeedback();
	}
	
	private void loadFeedback() {
		try {
			ProposalAppraiserBO abo = new ProposalAppraiserBO();
			this.appraisers = abo.listAppraisers(this.proposal.getIdProposal());
    		
    		for(ProposalAppraiser a : appraisers){
    			this.grid.addRow(a.getAppraiser().getName(), a.getFeedback().toString(), new ThemeResource("images/" + (a.getFile() != null ? "PDF" : "UNDEFINED") + ".png"));
    		}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Feedback", e.getMessage());
		}
	}
	
	private void downloadFeedback() {
		Object value = this.grid.getSelectedRow();
		
		if(value != null) {
			int id = this.appraisers.get((int)value - 1).getIdProposalAppraiser();
			
			try {
				ProposalAppraiser appraiser = new ProposalAppraiserBO().findById(id);
				
				if(appraiser.getFile() != null) {
					this.showReport(appraiser.getFile());
				} else {
					this.showWarningNotification("Download de Arquivo", "O membro " + appraiser.getAppraiser().getName() + " não enviou nenhum arquivo de comentários.");
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
				this.showErrorNotification("Download de Arquivo", e.getMessage());
			}
		} else {
			this.showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
		}
	}
	
	private void viewComments() {
		Object value = this.grid.getSelectedRow();
		
		if(value != null){
			int id = this.appraisers.get((int)value - 1).getIdProposalAppraiser();
			
			try {
				ProposalAppraiser appraiser = new ProposalAppraiserBO().findById(id);
				
				UI.getCurrent().addWindow(new CommentWindow("Observações", appraiser.getComments()));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Observações", e.getMessage());
			}
		} else {
			this.showWarningNotification("Carregar Observações", "Selecione o avaliador para visualizar as observações.");
		}
	}
	
}
