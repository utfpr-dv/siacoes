package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;

public class InternshipLibraryView extends ListView {
	
	public static final String NAME = "internshiplibrary";
	
	private final Button buttonDownloadFile;
	
	private Button.ClickListener listenerClickDownload;
	
	public InternshipLibraryView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Relatórios de Estágio");
		
		this.buttonDownloadFile = new Button("Download");
		this.addActionButton(this.buttonDownloadFile);
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		this.setFiltersVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(120);
		
		this.prepareDownload();
		
		try {
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
	    	List<InternshipFinalDocument> list = bo.listByDepartment(Session.getUser().getDepartment().getIdDepartment(), false);
	    	
	    	for(InternshipFinalDocument p : list){
				Object itemId = this.getGrid().addRow(p.getSubmissionDate(), p.getTitle(), p.getInternship().getStudent().getName(), p.getInternship().getCompany().getName());
				this.addRowId(itemId, p.getIdInternshipFinalDocument());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Monografias", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void prepareDownload(){
		Object value = getIdSelected();
		
		this.buttonDownloadFile.removeClickListener(this.listenerClickDownload);
    	
    	if(value != null){
			try {
				InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
            	InternshipFinalDocument p = bo.findById((int)value);
            	
            	new ExtensionUtils().extendToDownload(p.getTitle() + ".pdf", p.getFile(), this.buttonDownloadFile);
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download do Relatório de Estágio", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownloadFile.addClickListener(this.listenerClickDownload);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonDownloadFile);
    		
    		this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download do Relatório de Estágio", "Selecione um registro para baixar o relatório de estágio.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonDownloadFile.addClickListener(this.listenerClickDownload);
    	}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		// TODO Auto-generated method stub
		
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
