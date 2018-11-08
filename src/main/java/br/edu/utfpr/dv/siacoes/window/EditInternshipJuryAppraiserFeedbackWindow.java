package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;

import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class EditInternshipJuryAppraiserFeedbackWindow extends EditWindow {
	
	private final InternshipJuryAppraiser appraiser;
	
	private final FileUploader uploadFile;
	private final FileUploader uploadAdditionalFile;
	
	public EditInternshipJuryAppraiserFeedbackWindow(InternshipJuryAppraiser appraiser){
		super("Enviar Feedback", null);
		
		if(appraiser == null){
			this.appraiser = new InternshipJuryAppraiser();
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
			this.showErrorNotification("Enviar Feedback", "É necessário submeter ao menos um arquivo.");
		}else{
			try{
				InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
				
				bo.save(this.appraiser);
				
				this.showSuccessNotification("Enviar Feedback", "Feedback enviado com sucesso.");
				
				this.parentViewRefreshGrid();
				this.close();
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Enviar Feedback", e.getMessage());
			}
		}
	}
	
}
