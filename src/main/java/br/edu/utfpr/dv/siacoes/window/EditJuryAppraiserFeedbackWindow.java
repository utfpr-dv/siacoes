package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class EditJuryAppraiserFeedbackWindow extends EditWindow {
	
	private final JuryAppraiser appraiser;
	
	private final FileUploader uploadFile;
	private final FileUploader uploadAdditionalFile;
	
	private SigetConfig config;

	public EditJuryAppraiserFeedbackWindow(JuryAppraiser appraiser){
		super("Enviar Feedback", null);
		
		if(appraiser == null){
			this.appraiser = new JuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		try {
			this.config = new SigetConfigBO().findByDepartment(new JuryAppraiserBO().findIdDepartment(this.appraiser.getIdJuryAppraiser()));
		} catch (Exception e) {
			this.config = new SigetConfig();
		}
		
		this.uploadFile = new FileUploader("Arquivo Comentado (Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		
		this.uploadAdditionalFile = new FileUploader("Arquivos Complementares (Formato ZIP, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadAdditionalFile.getAcceptedDocumentTypes().add(DocumentType.ZIP);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		
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
				JuryAppraiserBO bo = new JuryAppraiserBO();
				
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
