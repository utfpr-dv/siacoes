package br.edu.utfpr.dv.siacoes.ui.windows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class EditInternshipJuryAppraiserFeedbackWindow extends EditWindow {
	
	private final InternshipJuryAppraiser appraiser;
	
	private final FileUploader uploadFile;
	private final FileUploader uploadAdditionalFile;
	private final Button buttonDownload;
	private final Button buttonDownloadAdditional;
	
	private final Anchor anchorDownloadAdditional;
	
	private SigesConfig config;
	
	public EditInternshipJuryAppraiserFeedbackWindow(InternshipJuryAppraiser appraiser){
		super("Enviar Feedback", null);
		
		if(appraiser == null){
			this.appraiser = new InternshipJuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		try {
			this.config = new SigesConfigBO().findByDepartment(new InternshipJuryAppraiserBO().findIdDepartment(this.appraiser.getIdInternshipJuryAppraiser()));
		} catch (Exception e) {
			this.config = new SigesConfig();
		}
		
		this.uploadFile = new FileUploader("Arquivo Comentado (Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					appraiser.setFile(uploadFile.getUploadedFile());
					
					buttonDownload.setVisible(true);
				}
			}
		});
		
		this.uploadAdditionalFile = new FileUploader("Arquivos Complementares (Formato ZIP, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadAdditionalFile.getAcceptedDocumentTypes().add(DocumentType.ZIP);
		this.uploadAdditionalFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadAdditionalFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadAdditionalFile.getUploadedFile() != null) {
					appraiser.setAdditionalFile(uploadAdditionalFile.getUploadedFile());
				}
			}
		});
		
		this.buttonDownload = new Button("Download", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFeedback();
        });
		this.buttonDownload.setWidth("150px");
		this.buttonDownload.setVisible(this.appraiser.getFile() != null);
		
		this.buttonDownloadAdditional = new Button("Download", new Icon(VaadinIcon.CLOUD_DOWNLOAD));
		this.buttonDownloadAdditional.setWidth("150px");
		this.buttonDownloadAdditional.setVisible(this.appraiser.getAdditionalFile() != null);
		
		this.anchorDownloadAdditional = new Anchor();
		this.anchorDownloadAdditional.getElement().setAttribute("download", true);
		this.anchorDownloadAdditional.add(this.buttonDownloadAdditional);
		this.anchorDownloadAdditional.setHref(new StreamResource(this.appraiser.getAppraiser().getName() + ".zip", this::makeDownloadAdditional));
		
		this.addField(new HorizontalLayout(this.uploadFile, this.buttonDownload));
		this.addField(new HorizontalLayout(this.uploadAdditionalFile, this.buttonDownloadAdditional));
	}
	
	@Override
	public void save() {
		if((this.appraiser.getFile() == null) && (this.appraiser.getAdditionalFile() == null)){
			this.showErrorNotification("Enviar Feedback", "É necessário submeter ao menos um arquivo.");
		}else{
			try{
				InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
				
				bo.save(Session.getIdUserLog(), this.appraiser);
				
				this.showSuccessNotification("Enviar Feedback", "Feedback enviado com sucesso.");
				
				this.parentViewRefreshGrid();
				this.close();
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Enviar Feedback", e.getMessage());
			}
		}
	}
	
	private void downloadFeedback() {
		if(this.appraiser.getFile() != null) {
			this.showReport(this.appraiser.getFile());
		} else {
			this.showWarningNotification("Download do Arquivo", "Nenhum arquivo foi enviado.");
		}
	}
	
	private InputStream makeDownloadAdditional() {
		try {
			if(this.appraiser.getAdditionalFile() != null) {
				return new ByteArrayInputStream(this.appraiser.getAdditionalFile());	
			} else {
				this.showWarningNotification("Download de Arquivo", "Nenhum arquivo foi enviado.");
				
				return null;
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Download de Arquivo", e.getMessage());
			
			return null;
		}
	}
	
}
