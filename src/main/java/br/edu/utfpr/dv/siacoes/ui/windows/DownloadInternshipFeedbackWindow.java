package br.edu.utfpr.dv.siacoes.ui.windows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryAppraiserDataSource;

public class DownloadInternshipFeedbackWindow extends BasicWindow {
	
	private final InternshipJury jury;
	
	private final Grid<JuryAppraiserDataSource> grid;
	private final Button buttonDownload;
	private final Button buttonDownloadAdditional;
	
	private final Anchor anchorDownloadAdditional;
	
	private List<InternshipJuryAppraiser> appraisers;
	
	public DownloadInternshipFeedbackWindow(InternshipJury jury){
		super("Feedback da Banca Examinadora");
		
		if(jury == null){
			this.jury = new InternshipJury();
		}else{
			this.jury = jury;
		}
		
		this.buttonDownload = new Button("Down. Arq. Comentado", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFeedback();
        });
		this.buttonDownload.setWidth("250px");
		
		this.buttonDownloadAdditional = new Button("Down. Arq. Complementar");
		this.buttonDownloadAdditional.setIcon(new Icon(VaadinIcon.CLOUD_DOWNLOAD));
		this.buttonDownloadAdditional.setWidth("250px");
		
		this.anchorDownloadAdditional = new Anchor();
		this.anchorDownloadAdditional.getElement().setAttribute("download", true);
		this.anchorDownloadAdditional.add(this.buttonDownloadAdditional);
		
		this.grid = new Grid<JuryAppraiserDataSource>();
		this.grid.setSelectionMode(SelectionMode.SINGLE);
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid.addColumn(JuryAppraiserDataSource::getAppraiser).setHeader("Avaliador");
		this.grid.addComponentColumn(item -> createFileIcon(this.grid, item)).setHeader("Arq. Comen.").setFlexGrow(0).setWidth("100px");
		this.grid.addComponentColumn(item -> createAdditionalFileIcon(this.grid, item)).setHeader("Arq. Compl.").setFlexGrow(0).setWidth("100px");
		this.grid.setWidth("510px");
		this.grid.setHeight("200px");
		this.grid.addSelectionListener(event -> {
			this.anchorDownloadAdditional.setHref(new StreamResource(this.additionalFeedbackName(), this::makeDownloadAdditionalFeedback));
		});
		
		HorizontalLayout h1 = new HorizontalLayout(this.buttonDownload, this.buttonDownloadAdditional);
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
	
	private Image createFileIcon(Grid<JuryAppraiserDataSource> grid, JuryAppraiserDataSource item) {
    	Image img = new Image();
    	
    	img.setSrc("images/" + item.getFileType() + ".png");
    	img.setHeight("24px");
    	img.setWidth("24px");
    	
    	return img;
    }
	
	private Image createAdditionalFileIcon(Grid<JuryAppraiserDataSource> grid, JuryAppraiserDataSource item) {
    	Image img = new Image();
    	
    	img.setSrc("images/" + item.getAdditionalFileType() + ".png");
    	img.setHeight("24px");
    	img.setWidth("24px");
    	
    	return img;
    }
	
	private void loadFeedback(){
		try {
			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
	    	this.appraisers = bo.listAppraisers(this.jury.getIdInternshipJury());
	    	
	    	this.grid.setItems(JuryAppraiserDataSource.loadInternshipJury(this.appraisers));
	    	
	    	this.anchorDownloadAdditional.setHref(new StreamResource(this.additionalFeedbackName(), this::makeDownloadAdditionalFeedback));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Feedback", e.getMessage());
		}
	}
	
	private void downloadFeedback() {
		JuryAppraiserDataSource a = this.grid.asSingleSelect().getValue();
		
		if(a == null) {
			this.showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
		} else {
			try {
				InternshipJuryAppraiser ja = new InternshipJuryAppraiserBO().findById(a.getId());
				
				if(ja.getFile() != null) {
            		this.showReport(ja.getFile());
            	} else {
            		this.showWarningNotification("Download de Arquivo", "O membro " + ja.getAppraiser().getName() + " não enviou nenhum arquivo de comentários.");
            	}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
            	
				this.showErrorNotification("Download de Arquivo", e.getMessage());
			}
		}
	}
	
	private String additionalFeedbackName() {
		JuryAppraiserDataSource a = this.grid.asSingleSelect().getValue();
		
		if(a == null) {
			return "";
		} else {
			return a.getAppraiser() + ".zip";
		}
	}
	
	private InputStream makeDownloadAdditionalFeedback() {
		JuryAppraiserDataSource a = this.grid.asSingleSelect().getValue();
		
		if(a == null) {
			this.showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
			
			return null;
		} else {
			try {
				InternshipJuryAppraiser ja = new InternshipJuryAppraiserBO().findById(a.getId());
				
				if(ja.getFile() != null) {
					return new ByteArrayInputStream(ja.getAdditionalFile());	
				} else {
					this.showWarningNotification("Download de Arquivo", "O membro " + ja.getAppraiser().getName() + " não enviou nenhum arquivo complementar.");
					
					return null;
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
            	
				this.showErrorNotification("Download de Arquivo", e.getMessage());
				
				return null;
			}
		}
	}

}
