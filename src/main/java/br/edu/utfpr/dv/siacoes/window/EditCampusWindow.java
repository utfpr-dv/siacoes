package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditCampusWindow extends EditWindow {
	
	private final Campus campus;
	
	private final TextField textName;
	private final TextField textAddress;
	private final CheckBox checkActive;
	private final FileUploader uploadLogo;
	private final Image imageLogo;
	private final TextField textSite;
	private final TextField textInitials;
	
	public EditCampusWindow(Campus c, ListView parentView){
		super("Editar Câmpus", parentView);
		
		if(c == null){
			this.campus = new Campus();
		}else{
			this.campus = c;
		}
		
		this.textName = new TextField("Câmpus");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
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
		
		this.uploadLogo = new FileUploader("Enviar Logotipo");
		this.uploadLogo.getAcceptedDocumentTypes().add(DocumentType.JPEG);
		this.uploadLogo.getAcceptedDocumentTypes().add(DocumentType.PNG);
		this.uploadLogo.setMaxBytesLength(300 * 1024);
		this.uploadLogo.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadLogo.getFileUploadListener() != null) {
					campus.setLogo(uploadLogo.getUploadedFile());
				}
				
				loadLogo();
			}
		});
		
		this.imageLogo = new Image();
		this.imageLogo.setSizeUndefined();
		this.imageLogo.setHeight("200px");
		
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
			
			if(this.uploadLogo.getUploadedFile() != null) {
				this.campus.setLogo(this.uploadLogo.getUploadedFile());
			}
			
			bo.save(Session.getIdUserLog(), this.campus);
			
			this.showSuccessNotification("Salvar Câmpus", "Câmpus salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Câmpus", e.getMessage());
		}
	}
	
}
