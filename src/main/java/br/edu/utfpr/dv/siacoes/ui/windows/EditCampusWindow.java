package br.edu.utfpr.dv.siacoes.ui.windows;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditCampusWindow extends EditWindow {
	
	private final Campus campus;
	
	private final TextField textName;
	private final TextField textAddress;
	private final Checkbox checkActive;
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
		
		this.checkActive = new Checkbox();
		this.checkActive.setLabel("Ativo");
		
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
			StreamResource resource = new StreamResource("filename" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(DateUtils.getNow().getTime()) + ".png", () -> new ByteArrayInputStream(this.campus.getLogo()));
			resource.setCacheTime(0);
			this.imageLogo.setSrc(resource);
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Câmpus", e.getMessage());
		}
	}
	
}
