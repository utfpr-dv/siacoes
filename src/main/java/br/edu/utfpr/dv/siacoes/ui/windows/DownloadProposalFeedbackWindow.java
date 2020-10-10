package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.ui.grid.ProposalAppraiserDataSource;

public class DownloadProposalFeedbackWindow extends BasicWindow {

	private final Proposal proposal;
	
	private final Grid<ProposalAppraiserDataSource> grid;
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
		
		this.grid = new Grid<ProposalAppraiserDataSource>();
		this.grid.setSelectionMode(SelectionMode.SINGLE);
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid.addColumn(ProposalAppraiserDataSource::getName).setHeader("Avaliador");
		this.grid.addColumn(ProposalAppraiserDataSource::getFeedback).setHeader("Parecer");
		this.grid.addComponentColumn(item -> createFileIcon(this.grid, item)).setHeader("Arq. Compl.").setFlexGrow(0).setWidth("100px");
		this.grid.setWidth("510px");
		this.grid.setHeight("200px");
		
		this.buttonComments = new Button("Observações", new Icon(VaadinIcon.SEARCH), event -> {
            viewComments();
        });
		this.buttonComments.setWidth("150px");
		
		this.buttonDownload = new Button("Down. Arq. Comentado", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFeedback();
        });
		this.buttonDownload.setWidth("250px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.buttonComments, this.buttonDownload);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		VerticalLayout vl = new VerticalLayout(this.grid, h1);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		this.add(vl);
        
        this.loadFeedback();
	}
	
	private void loadFeedback() {
		try {
			ProposalAppraiserBO abo = new ProposalAppraiserBO();
			this.appraisers = abo.listAppraisers(this.proposal.getIdProposal());
			
			this.grid.setItems(ProposalAppraiserDataSource.load(this.appraisers));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Feedback", e.getMessage());
		}
	}
	
	private Image createFileIcon(Grid<ProposalAppraiserDataSource> grid, ProposalAppraiserDataSource item) {
    	Image img = new Image();
    	
    	img.setSrc("images/" + item.getFileType() + ".png");
    	img.setHeight("24px");
    	img.setWidth("24px");
    	
    	return img;
    }
	
	private void downloadFeedback() {
		ProposalAppraiserDataSource a = this.grid.asSingleSelect().getValue();
		
		if(a == null) {
			this.showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
		} else {
			try {
				ProposalAppraiser appraiser = new ProposalAppraiserBO().findById(a.getId());
				
				if(appraiser.getFile() != null) {
					this.showReport(appraiser.getFile());
				} else {
					this.showWarningNotification("Download de Arquivo", "O membro " + appraiser.getAppraiser().getName() + " não enviou nenhum arquivo de comentários.");
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
            	
				this.showErrorNotification("Download de Arquivo", e.getMessage());
			}
		}
	}
	
	private void viewComments() {
		ProposalAppraiserDataSource a = this.grid.asSingleSelect().getValue();
		
		if(a == null) {
			this.showWarningNotification("Carregar Observações", "Selecione o avaliador para visualizar as observações.");
		} else {
			try {
				ProposalAppraiser appraiser = new ProposalAppraiserBO().findById(a.getId());
				
				CommentWindow window = new CommentWindow("Observações", appraiser.getComments());
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Observações", e.getMessage());
			}
		}
	}
	
}
