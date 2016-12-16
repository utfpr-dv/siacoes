package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditDepartmentWindow extends EditWindow {
	
	private final Department department;
	
	private final CampusComboBox comboCampus;
	private final TextField textName;
	private final TextField textFullName;
	private final CheckBox checkActive;
	private final Upload uploadLogo;
	private final Image imageLogo;
	private final TextField textSite;

	public EditDepartmentWindow(Department department, ListView parentView){
		super("Editar Departamento", parentView);
		
		if(department == null){
			this.department = new Department();
		}else{
			this.department = department;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setFilterOnlyActives(false);
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		
		this.textFullName = new TextField("Nome Detalhado");
		this.textFullName.setWidth("800px");
		this.textFullName.setMaxLength(255);
		
		this.textSite = new TextField("Site");
		this.textSite.setWidth("800px");
		this.textSite.setMaxLength(255);
		
		this.checkActive = new CheckBox("Ativo");
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadLogo = new Upload("Enviar Logotipo", listener);
		this.uploadLogo.addSucceededListener(listener);
		this.uploadLogo.setButtonCaption("Enviar");
		
		this.imageLogo = new Image();
		this.imageLogo.setStyleName("ImageLogo");
		this.imageLogo.setWidth("400px");
		this.imageLogo.setHeight("200px");
		
		this.addField(new HorizontalLayout(this.textName, this.comboCampus));
		this.addField(this.textFullName);
		this.addField(this.textSite);
		this.addField(this.checkActive);
		this.addField(new HorizontalLayout(this.uploadLogo, this.imageLogo));
		
		this.loadDepartment();
		this.textName.focus();
	}
	
	private void loadDepartment(){
		this.comboCampus.setCampus(this.department.getCampus());
		this.textName.setValue(this.department.getName());
		this.checkActive.setValue(this.department.isActive());
		this.textSite.setValue(this.department.getSite());
		this.textFullName.setValue(this.department.getFullName());
		
		this.loadLogo();
	}
	
	private void loadLogo(){
		if(this.department.getLogo() != null){
			StreamResource resource = new StreamResource(
	            new StreamResource.StreamSource() {
	                @Override
	                public InputStream getStream() {
	                    return new ByteArrayInputStream(department.getLogo());
	                }
	            }, "filename.png");
	
		    this.imageLogo.setSource(resource);
		}
	}
	
	@Override
	public void save() {
		try{
			DepartmentBO bo = new DepartmentBO();
			
			this.department.setCampus(this.comboCampus.getCampus());
			this.department.setName(this.textName.getValue());
			this.department.setActive(this.checkActive.getValue());
			this.department.setSite(this.textSite.getValue());
			this.department.setFullName(this.textFullName.getValue());
			
			bo.save(this.department);
			
			Notification.show("Salvar Departamento", "Departamento salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Departamento", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				if(!mimeType.equals("image/jpeg") && !mimeType.equals("image/png")){
					throw new Exception("O arquivo enviado é inválido. São aceitos apenas arquivos JPG e PNG.");
				}
				
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            department.setLogo(buffer);
	            
	            loadLogo();
	        } catch (IOException e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
