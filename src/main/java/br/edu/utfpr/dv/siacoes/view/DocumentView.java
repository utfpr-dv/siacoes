package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ImageRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DocumentBO;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.window.EditDocumentWindow;

public class DocumentView extends ListView {

	public static final String NAME = "documents";
	
	private final Button buttonDownload;
	private final Button buttonDownloadAll;
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	private Button.ClickListener listenerClickDownload;
	private Button.ClickListener listenerClickDownloadAll;
    
    public DocumentView(){
    	super(SystemModule.GENERAL);
    	
    	this.setCaption("Regulamentos e Anexos");
		
    	this.buttonDownload = new Button("Baixar Arquivo");
    	this.buttonDownload.setIcon(FontAwesome.DOWNLOAD);
    	
    	this.buttonDownloadAll = new Button("Baixar Todos");
    	this.buttonDownloadAll.setIcon(FontAwesome.FILE_ZIP_O);
    	
    	this.buttonMoveUp = new Button("Para Cima", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	moveUp();
            }
        });
    	this.buttonMoveUp.setIcon(FontAwesome.ARROW_UP);
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	moveDown();
            }
        });
    	this.buttonMoveDown.setIcon(FontAwesome.ARROW_DOWN);
    	
    	this.addActionButton(this.buttonDownload);
    	this.addActionButton(this.buttonDownloadAll);
    	
    	//if(Session.isUserStudent()){
    		this.setFiltersVisible(false);	
    	//}
    }
    
    protected void loadGrid(){
    	this.getGrid().addColumn("Tipo", Resource.class).setRenderer(new ImageRenderer());
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().getColumns().get(0).setWidth(70);
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.prepareDownload();
		this.prepareDownloadAll();
		
		try {
			DocumentBO bo = new DocumentBO();
	    	List<Document> list = bo.listByModule(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.getModule());
	    	
	    	for(Document d : list){
				Object itemId = this.getGrid().addRow(new ThemeResource("images/" + d.getType().name() + ".png"), d.getName());
				this.addRowId(itemId, d.getIdDocument());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Documentos", e.getMessage());
		}
    }
    
    private void prepareDownloadAll() {
    	this.buttonDownloadAll.removeClickListener(this.listenerClickDownloadAll);
    	new ExtensionUtils().removeAllExtensions(this.buttonDownloadAll);
    	
    	try {
    		new ExtensionUtils().extendToDownload(this.getModule().getShortDescription() + ".zip", new DocumentBO().downloadAllDocuments(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.getModule()), this.buttonDownloadAll);
    	} catch (Exception e) {
    		this.listenerClickDownloadAll = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            	
	            	showErrorNotification("Download dos Arquivos", e.getMessage());
	            }
	        };
	        
    		this.buttonDownloadAll.addClickListener(this.listenerClickDownloadAll);
    	}
    }
    
    private void prepareDownload(){
    	Object value = getIdSelected();
    	
    	this.buttonDownload.removeClickListener(this.listenerClickDownload);
    	new ExtensionUtils().removeAllExtensions(this.buttonDownload);
    	
    	if(value != null){
    		try {
    			DocumentBO bo = new DocumentBO();
            	Document doc = bo.findById((int)value);
            	
            	new ExtensionUtils().extendToDownload(doc.getName() + doc.getType().getExtension(), doc.getFile(), this.buttonDownload);
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	showErrorNotification("Download de Arquivo", e.getMessage());
		            }
		        };
		        
        		this.buttonDownload.addClickListener(this.listenerClickDownload);
			}
    	}else{
    		this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
	            }
	        };
	        
    		this.buttonDownload.addClickListener(this.listenerClickDownload);
    	}
    }
    
	@Override
	public void addClick() {
		Document doc = new Document();
		
		doc.setDepartment(Session.getSelectedDepartment().getDepartment());
		doc.setModule(this.getModule());
		
		UI.getCurrent().addWindow(new EditDocumentWindow(doc, this));
	}

	@Override
	public void editClick(Object id) {
		try {
			DocumentBO bo = new DocumentBO();
			Document doc = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditDocumentWindow(doc, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Documento", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			DocumentBO bo = new DocumentBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			this.refreshGrid();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Documento", e.getMessage());
		}
	}
	
	private void moveUp(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			DocumentBO bo = new DocumentBO();
    			
    			bo.moveUp((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Documento", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Documento", "Selecione o registro.");
    	}
	}
	
	private void moveDown(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			DocumentBO bo = new DocumentBO();
    			
    			bo.moveDown((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Documento", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Documento", "Selecione o registro.");
    	}
	}
	
	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void enter(ViewChangeEvent event){
		super.enter(event);
		
		if((event.getParameters() != null) && (!event.getParameters().isEmpty())){
			try{
				SystemModule module = SystemModule.valueOf(Integer.parseInt(event.getParameters()));
				
				this.setModule(module);
				
				if(Session.isUserManager(this.getModule())){
		    		this.addActionButton(this.buttonMoveUp);
		        	this.addActionButton(this.buttonMoveDown);
		    	}else{
		    		this.setAddVisible(false);
		    		this.setEditVisible(false);
		    		this.setDeleteVisible(false);
		    	}
				
				this.refreshGrid();
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
}
