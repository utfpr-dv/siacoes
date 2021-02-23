package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DocumentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.DocumentDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditDocumentWindow;

@PageTitle("Regulamentos e Anexos")
@Route(value = "documents", layout = MainLayout.class)
public class DocumentView extends ListView<DocumentDataSource> implements HasUrlParameter<String> {
	
	private final Button buttonDownload;
	private final Button buttonDownloadAll;
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	private final Anchor anchorDownload;
    
    public DocumentView(){
    	super(SystemModule.GENERAL);

    	this.buttonDownload = new Button("Baixar Arquivo", new Icon(VaadinIcon.CLOUD_DOWNLOAD));
    	this.buttonDownload.setWidth("210px");
    	
    	this.anchorDownload = new Anchor();
    	this.anchorDownload.getElement().setAttribute("download", true);
    	this.anchorDownload.add(this.buttonDownload);
    	
    	this.getGrid().addComponentColumn(item -> createDocIcon(this.getGrid(), item)).setHeader("Tipo").setFlexGrow(0).setWidth("70px");
		this.getGrid().addColumn(DocumentDataSource::getName).setHeader("Nome");
		this.getGrid().addSelectionListener(event -> {
			this.anchorDownload.setHref(new StreamResource(this.downloadFileName(), this::makeDownload));
		});
    	
    	this.buttonDownloadAll = new Button("Baixar Todos", new Icon(VaadinIcon.FILE_ZIP));
    	this.buttonDownloadAll.setWidth("210px");
    	
    	this.buttonMoveUp = new Button("Para Cima", new Icon(VaadinIcon.ARROW_UP), event -> {
            moveUp();
        });
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Icon(VaadinIcon.ARROW_DOWN), event -> {
            moveDown();
        });
    	
    	this.setFiltersVisible(false);	
    }
    
    private Image createDocIcon(Grid<DocumentDataSource> grid, DocumentDataSource item) {
    	Image img = new Image();
    	
    	img.setSrc("images/" + item.getType() + ".png");
    	img.setHeight("24px");
    	img.setWidth("24px");
    	
    	return img;
    }
    
    protected void loadGrid(){
    	try {
			DocumentBO bo = new DocumentBO();
	    	List<Document> list = bo.listByModule(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.getModule());
	    	
	    	this.getGrid().setItems(DocumentDataSource.load(list));
	    	
	    	this.anchorDownload.setHref(new StreamResource(this.downloadFileName(), this::makeDownload));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Documentos", e.getMessage());
		}
    }
    
    private InputStream makeDownloadAll() {
    	try {
    		byte[] data = new DocumentBO().downloadAllDocuments(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.getModule());
    		
    		return new ByteArrayInputStream(data);
    	} catch (Exception e) {
    		Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	showErrorNotification("Download de Arquivos", e.getMessage());
        	
        	return null;
    	}
    }
    
    private String downloadFileName() {
    	DocumentDataSource doc = this.getGrid().asSingleSelect().getValue();
    	
    	if(doc == null) {
    		return "";
    	} else {
    		return doc.getName() + "." + doc.getType().toLowerCase();
    	}
    }
    
    private InputStream makeDownload() {
    	DocumentDataSource doc = this.getGrid().asSingleSelect().getValue();
    	
    	if(doc == null) {
    		showWarningNotification("Download de Arquivo", "Selecione o arquivo para baixar.");
    		
    		return null;
    	} else {
    		try {
            	Document document = new DocumentBO().findById(doc.getId());
            	
            	return new ByteArrayInputStream(document.getFile());
    		} catch (Exception e) {
    			Logger.log(Level.SEVERE, e.getMessage(), e);
            	
    			showErrorNotification("Download de Arquivo", e.getMessage());
    			
    			return null;
    		}
    	}
    }
    
	@Override
	public void addClick() {
		Document doc = new Document();
		
		doc.setDepartment(Session.getSelectedDepartment().getDepartment());
		doc.setModule(this.getModule());
		
		EditDocumentWindow window = new EditDocumentWindow(doc, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			DocumentBO bo = new DocumentBO();
			Document doc = bo.findById((int)id);
			
			EditDocumentWindow window = new EditDocumentWindow(doc, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Documento", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		try {
			DocumentBO bo = new DocumentBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			this.refreshGrid();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
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
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
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
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
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
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(parameter != null && !parameter.trim().isEmpty()) {
			try{
				SystemModule module = SystemModule.valueOf(Integer.parseInt(parameter.trim()));
				
				this.setModule(module);
		    	
		    	this.addActionButton(this.anchorDownload);
				
				Anchor downloadAll = new Anchor(new StreamResource(this.getModule().getShortDescription() + ".zip", this::makeDownloadAll), "");
		    	downloadAll.getElement().setAttribute("download", true);
		    	downloadAll.add(this.buttonDownloadAll);
		    	
		    	this.addActionButton(downloadAll);
				
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
				Logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
}
