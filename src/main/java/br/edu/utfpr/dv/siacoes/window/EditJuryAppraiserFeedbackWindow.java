package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;

import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class EditJuryAppraiserFeedbackWindow extends EditWindow {
	
	private final JuryAppraiser appraiser;
	
	private final FileUploader uploadFile;
	private final FileUploader uploadAdditionalFile;

	public EditJuryAppraiserFeedbackWindow(JuryAppraiser appraiser){
		super("Enviar Feedback", null);
		
		if(appraiser == null){
			this.appraiser = new JuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		this.uploadFile = new FileUploader("Arquivo Comentado (Formato PDF, Tam. Máx. 5 MB)");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(6 * 1024 * 1024);
		
		this.uploadAdditionalFile = new FileUploader("Arquivos Complementares (Formato ZIP, Tam. Máx. 5 MB)");
		this.uploadAdditionalFile.getAcceptedDocumentTypes().add(DocumentType.ZIP);
		this.uploadFile.setMaxBytesLength(6 * 1024 * 1024);
		
		this.addField(this.uploadFile);
		this.addField(this.uploadAdditionalFile);
	}
	
	@Override
	public void save() {
		if(this.uploadFile.getUploadedFile() != null) {
			this.appraiser.setFile(this.uploadFile.getUploadedFile());
		}
		if(this.uploadAdditionalFile.getUploadedFile() != null) {
			this.appraiser.setAdditionalFile(this.uploadAdditionalFile.getUploadedFile());
		}
		
		if((this.appraiser.getFile() == null) && (this.appraiser.getAdditionalFile() == null)){
			Notification.show("Enviar Feedback", "É necessário submeter ao menos um arquivo.", Notification.Type.ERROR_MESSAGE);
		}else{
			try{
				JuryAppraiserBO bo = new JuryAppraiserBO();
				
				bo.save(this.appraiser);
				
				Notification.show("Enviar Feedback", "Feedback enviado com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
				
				this.parentViewRefreshGrid();
				this.close();
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Enviar Feedback", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
}
