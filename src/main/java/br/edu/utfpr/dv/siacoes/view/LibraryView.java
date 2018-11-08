package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;

public class LibraryView extends ListView {
	
	public static final String NAME = "library";
	
	private final Button buttonDownloadThesis;
	
	private Button.ClickListener listenerClickDownload;
	
	public LibraryView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Monografias de TCC");
		
		this.buttonDownloadThesis = new Button("Download");
		this.addActionButton(this.buttonDownloadThesis);
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		this.setFiltersVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		
		this.prepareDownload();
		
		try {
			FinalDocumentBO bo = new FinalDocumentBO();
	    	List<FinalDocument> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), true, false);
	    	
	    	for(FinalDocument p : list){
				Object itemId = this.getGrid().addRow(p.getThesis().getSemester(), p.getThesis().getYear(), p.getTitle(), (p.getThesis().getIdThesis() != 0 ? p.getThesis().getStudent().getName() : p.getProject().getStudent().getName()));
				this.addRowId(itemId, p.getIdFinalDocument());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Monografias", e.getMessage());
		}
	}
	
	private void prepareDownload(){
		Object value = getIdSelected();
		
		this.buttonDownloadThesis.removeClickListener(this.listenerClickDownload);
		new ExtensionUtils().removeAllExtensions(this.buttonDownloadThesis);
    	
    	if(value != null){
			try {
				FinalDocumentBO bo = new FinalDocumentBO();
            	FinalDocument p = bo.findById((int)value);
            	
            	if(p.isPrivate()){
            		this.listenerClickDownload = new Button.ClickListener() {
        	            @Override
        	            public void buttonClick(ClickEvent event) {
        	            	showWarningNotification("Download da Monografia", "Este documento foi marcado como sigiloso no ato de sua submissão.\\n\\nVocê pode efetuar a consulta do documento na biblioteca do câmpus.");
        	            }
        	        };
            		
            		this.buttonDownloadThesis.addClickListener(this.listenerClickDownload);
            	}else{
            		new ExtensionUtils().extendToDownload(p.getTitle() + ".pdf", p.getFile(), this.buttonDownloadThesis);	
            	}
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	showErrorNotification("Download da Monografia", e.getMessage());
		            }
		        };
		        
        		this.buttonDownloadThesis.addClickListener(this.listenerClickDownload);
			}
    	}else{
    		this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	showWarningNotification("Download da Monografia", "Selecione um registro para baixar a monografia.");
	            }
	        };
    		
    		this.buttonDownloadThesis.addClickListener(this.listenerClickDownload);
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
