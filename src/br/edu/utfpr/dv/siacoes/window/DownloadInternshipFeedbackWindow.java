package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ImageRenderer;

import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;

public class DownloadInternshipFeedbackWindow extends Window {
	
	private final InternshipJury jury;
	
	private final Grid grid;
	private final Button buttonDownload;
	
	private Button.ClickListener listenerClickDownload;
	private List<InternshipJuryAppraiser> appraisers;
	
	public DownloadInternshipFeedbackWindow(InternshipJury jury){
		super("Feedback da Banca Examinadora");
		
		if(jury == null){
			this.jury = new InternshipJury();
		}else{
			this.jury = jury;
		}
		
		this.grid = new Grid();
		this.grid.addColumn("Arquivo", Resource.class).setRenderer(new ImageRenderer());
		this.grid.addColumn("Avaliador", String.class);
		this.grid.getColumns().get(0).setWidth(70);
		this.grid.setWidth("500px");
		this.grid.setHeight("200px");
		this.grid.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.buttonDownload = new Button("Download");
		this.buttonDownload.setWidth("150px");
		this.prepareDownload();
		
		VerticalLayout vl = new VerticalLayout(this.grid, this.buttonDownload);
		vl.setSpacing(true);
		vl.setMargin(true);
		
		this.setContent(vl);
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
        
        this.loadFeedback();
	}
	
	private void loadFeedback(){
		try {
			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
	    	this.appraisers = bo.listAppraisers(this.jury.getIdInternshipJury());
	    	
	    	for(InternshipJuryAppraiser ja: this.appraisers){
				this.grid.addRow(new ThemeResource("images/" + ja.getFileType().name() + ".png"), ja.getAppraiser().getName());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Feedback", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownload(){
    	Object value = this.grid.getSelectedRow();
    	
    	this.buttonDownload.removeClickListener(this.listenerClickDownload);
    	new ExtensionUtils().removeAllExtensions(this.buttonDownload);
    	
    	if(value != null){
    		int id = this.appraisers.get((int)value - 1).getIdInternshipJuryAppraiser();
    		
    		try {
    			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
            	InternshipJuryAppraiser ja = bo.findById(id);
            	
            	if(ja.getFile() != null){
            		new ExtensionUtils().extendToDownload(ja.getAppraiser().getName() + ja.getFileType().getExtension(), ja.getFile(), this.buttonDownload);
            	}else{
            		this.listenerClickDownload = new Button.ClickListener() {
        	            @Override
        	            public void buttonClick(ClickEvent event) {
        	            	Notification.show("Download de Arquivo", "O membro " + ja.getAppraiser().getName() + " não enviou nenhum arquivo.", Notification.Type.WARNING_MESSAGE);
        	            }
        	        };
        	        
            		this.buttonDownload.addClickListener(this.listenerClickDownload);
            	}
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download de Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownload.addClickListener(this.listenerClickDownload);
			}
    	}else{
    		this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download de Arquivo", "Selecione o arquivo para baixar.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
	        
    		this.buttonDownload.addClickListener(this.listenerClickDownload);
    	}
    }

}
