package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
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

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditCampusWindow extends EditWindow {
	
	private final Campus campus;
	
	private final TextField textName;
	private final TextField textAddress;
	private final CheckBox checkActive;
	private final Upload uploadLogo;
	private final Image imageLogo;
	private final TextField textSite;
	private final TextField textInitials;
	
	public EditCampusWindow(Campus campus, ListView parentView){
		super("Editar Câmpus", parentView);
		
		if(campus == null){
			this.campus = new Campus();
		}else{
			this.campus = campus;
		}
		
		this.textName = new TextField("Câmpus");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		
		this.textAddress = new TextField("Endereço");
		this.textAddress.setWidth("800px");
		this.textAddress.setMaxLength(255);
		
		this.textSite = new TextField("Site");
		this.textSite.setWidth("800px");
		this.textSite.setMaxLength(255);
		
		this.textInitials = new TextField("Sigla");
		this.textInitials.setWidth("400px");
		this.textInitials.setMaxLength(50);
		
		this.checkActive = new CheckBox("Ativo");
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadLogo = new Upload("Enviar Logotipo", listener);
		this.uploadLogo.addSucceededListener(listener);
		this.uploadLogo.setButtonCaption("Enviar");
		this.uploadLogo.setImmediate(true);
		
		this.imageLogo = new Image();
		this.imageLogo.setStyleName("ImageLogo");
		this.imageLogo.setSizeUndefined();
		
		this.addField(new HorizontalLayout(this.textName, this.checkActive));
		this.addField(this.textInitials);
		this.addField(this.textAddress);
		this.addField(this.textSite);
		this.addField(new HorizontalLayout(this.uploadLogo, this.imageLogo));
		
		this.loadCampus();
		this.textName.focus();
	}
	
	private void loadCampus(){
		this.textName.setValue(this.campus.getName());
		this.textAddress.setValue(this.campus.getAddress());
		this.checkActive.setValue(this.campus.isActive());
		this.textSite.setValue(this.campus.getSite());
		this.textInitials.setValue(this.campus.getInitials());
		
		this.loadLogo();
	}
	
	private void loadLogo(){
		if(this.campus.getLogo() != null){
			StreamResource resource = new StreamResource(
	            new StreamResource.StreamSource() {
	                @Override
	                public InputStream getStream() {
	                    return new ByteArrayInputStream(campus.getLogo());
	                }
	            }, "filename" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(DateUtils.getNow().getTime()) + ".png");
	
			resource.setCacheTime(0);
		    this.imageLogo.setSource(resource);
		}
	}

	@Override
	public void save() {
		try{
			CampusBO bo = new CampusBO();
			
			this.campus.setName(this.textName.getValue());
			this.campus.setAddress(this.textAddress.getValue());
			this.campus.setActive(this.checkActive.getValue());
			this.campus.setSite(this.textSite.getValue());
			this.campus.setInitials(this.textInitials.getValue());
			
			bo.save(this.campus);
			
			Notification.show("Salvar Câmpus", "Câmpus salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Câmpus", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
	            
	            campus.setLogo(buffer);
	            
	            loadLogo();
	        } catch (IOException e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
