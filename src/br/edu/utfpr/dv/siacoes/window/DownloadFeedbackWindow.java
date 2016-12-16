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

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;

public class DownloadFeedbackWindow extends Window {
	
	private final Jury jury;
	
	private final Grid grid;
	private final Button buttonDownload;
	
	private Button.ClickListener listenerClickDownload;
	private List<JuryAppraiser> appraisers;
	
	public DownloadFeedbackWindow(Jury jury){
		super("Feedback da Banca Examinadora");
		
		if(jury == null){
			this.jury = new Jury();
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
			JuryAppraiserBO bo = new JuryAppraiserBO();
	    	this.appraisers = bo.listAppraisers(this.jury.getIdJury());
	    	
	    	for(JuryAppraiser ja: this.appraisers){
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
    		int id = this.appraisers.get((int)value - 1).getIdJuryAppraiser();
    		
    		try {
    			JuryAppraiserBO bo = new JuryAppraiserBO();
            	JuryAppraiser ja = bo.findById(id);
            	
            	new ExtensionUtils().extendToDownload(ja.getAppraiser().getName() + ja.getFileType().getExtension(), ja.getFile(), this.buttonDownload);
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
