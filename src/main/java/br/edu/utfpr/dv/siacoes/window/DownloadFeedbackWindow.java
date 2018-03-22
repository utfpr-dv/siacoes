package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ImageRenderer;

import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;

public class DownloadFeedbackWindow extends BasicWindow {
	
	private final Jury jury;
	
	private final Grid grid;
	private final Button buttonDownload;
	
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
		
		this.buttonDownload = new Button("Download", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFeedback();
            }
        });
		this.buttonDownload.setWidth("150px");
		
		VerticalLayout vl = new VerticalLayout(this.grid, this.buttonDownload);
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
				this.grid.addRow(new ThemeResource("images/" + ja.getFileType().name() + ".png"), ja.getAppraiser().getName());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Feedback", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void downloadFeedback() {
		Object value = this.grid.getSelectedRow();
		
		if(value != null){
			int id = this.appraisers.get((int)value - 1).getIdJuryAppraiser();
			
			try {
				JuryAppraiserBO bo = new JuryAppraiserBO();
            	JuryAppraiser ja = bo.findById(id);
            	
            	if(ja.getFile() != null) {
            		this.showReport(ja.getFile());
            	} else {
            		Notification.show("Download de Arquivo", "O membro " + ja.getAppraiser().getName() + " não enviou nenhum arquivo.", Notification.Type.WARNING_MESSAGE);
            	}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	Notification.show("Download de Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		} else {
			Notification.show("Download de Arquivo", "Selecione o arquivo para baixar.", Notification.Type.WARNING_MESSAGE);
		}
	}
	
}
