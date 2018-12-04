package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ImageRenderer;

import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;

public class DownloadFeedbackWindow extends BasicWindow {
	
	private final Jury jury;
	
	private final Grid grid;
	private final Button buttonDownload;
	private final Button buttonDownloadAdditional;
	
	private Button.ClickListener listenerClickDownloadAdditional;
	
	private List<JuryAppraiser> appraisers;
	
	public DownloadFeedbackWindow(Jury jury){
		super("Feedback da Banca Examinadora");
		
		if(jury == null){
			this.jury = new Jury();
		}else{
			this.jury = jury;
		}
		
		this.grid = new Grid();
		this.grid.addColumn("Avaliador", String.class);
		this.grid.addColumn("Arq. Comen.", Resource.class).setRenderer(new ImageRenderer());
		this.grid.addColumn("Arq. Compl.", Resource.class).setRenderer(new ImageRenderer());
		this.grid.getColumns().get(1).setWidth(100);
		this.grid.getColumns().get(2).setWidth(100);
		this.grid.setWidth("510px");
		this.grid.setHeight("200px");
		this.grid.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownloadAdditionalFeedback();
			}
		});
		
		this.buttonDownload = new Button("Down. Arq. Comentado", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFeedback();
            }
        });
		this.buttonDownload.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownload.setWidth("250px");
		
		this.buttonDownloadAdditional = new Button("Down. Arq. Complementar");
		this.buttonDownloadAdditional.setIcon(FontAwesome.DOWNLOAD);
		this.buttonDownloadAdditional.setWidth("250px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.buttonDownload, this.buttonDownloadAdditional);
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
			JuryAppraiserBO bo = new JuryAppraiserBO();
	    	this.appraisers = bo.listAppraisers(this.jury.getIdJury());
	    	
	    	for(JuryAppraiser ja: this.appraisers){
	    		this.grid.addRow(ja.getAppraiser().getName(), new ThemeResource("images/" + (ja.getFile() != null ? "PDF" : "UNDEFINED") + ".png"), new ThemeResource("images/" + (ja.getAdditionalFile() != null ? "ZIP" : "UNDEFINED") + ".png"));
			}
	    	
	    	this.prepareDownloadAdditionalFeedback();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Feedback", e.getMessage());
		}
	}
	
	private void downloadFeedback() {
		Object value = this.grid.getSelectedRow();
		
		if(value != null) {
			int id = this.appraisers.get((int)value - 1).getIdJuryAppraiser();
			
			try {
            	JuryAppraiser ja = new JuryAppraiserBO().findById(id);
            	
            	if(ja.getFile() != null) {
            		this.showReport(ja.getFile());
            	} else {
            		this.showWarningNotification("Download de Arquivo", "O membro " + ja.getAppraiser().getName() + " não enviou nenhum arquivo de comentários.");
            	}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
				this.showErrorNotification("Download de Arquivo", e.getMessage());
			}
		} else {
			this.showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
		}
	}
	
	private void prepareDownloadAdditionalFeedback() {
		Object value = this.grid.getSelectedRow();
		this.buttonDownloadAdditional.removeClickListener(this.listenerClickDownloadAdditional);
		new ExtensionUtils().removeAllExtensions(this.buttonDownloadAdditional);
		
		if(value != null) {
			int id = this.appraisers.get((int)value - 1).getIdJuryAppraiser();
			
			try {
				JuryAppraiser ja = new JuryAppraiserBO().findById(id);
            	
            	if(ja.getAdditionalFile() != null) {
            		new ExtensionUtils().extendToDownload(ja.getAppraiser().getName() + ".zip", ja.getAdditionalFile(), this.buttonDownloadAdditional);
            	} else {
            		this.listenerClickDownloadAdditional = new Button.ClickListener() {
        	            @Override
        	            public void buttonClick(ClickEvent event) {
        	            	showWarningNotification("Download de Arquivo", "O membro " + ja.getAppraiser().getName() + " não enviou nenhum arquivo complementar.");
        	            }
        	        };
        	        
            		this.buttonDownloadAdditional.addClickListener(this.listenerClickDownloadAdditional);
            	}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
				this.listenerClickDownloadAdditional = new Button.ClickListener() {
    	            @Override
    	            public void buttonClick(ClickEvent event) {
    	            	showErrorNotification("Download de Arquivo", e.getMessage());
    	            }
    	        };
    	        
        		this.buttonDownloadAdditional.addClickListener(this.listenerClickDownloadAdditional);
			}
		} else {
			this.listenerClickDownloadAdditional = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
	            }
	        };
	        
    		this.buttonDownloadAdditional.addClickListener(this.listenerClickDownloadAdditional);
		}
	}
	
}
