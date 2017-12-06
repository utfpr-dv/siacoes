package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class EditInternshipJuryAppraiserFeedbackWindow extends EditWindow {
	
	private final InternshipJuryAppraiser appraiser;
	
	private final Upload uploadFile;
	private final Image imageFileUploaded;
	
	public EditInternshipJuryAppraiserFeedbackWindow(InternshipJuryAppraiser appraiser){
		super("Enviar Feedback", null);
		
		if(appraiser == null){
			this.appraiser = new InternshipJuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadFile = new Upload("Enviar Arquivo (Formato PDF, Tam. Máx. 5 MB)", listener);
		this.uploadFile.addSucceededListener(listener);
		this.uploadFile.setButtonCaption("Enviar");
		this.uploadFile.setImmediate(true);
		
		this.imageFileUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageFileUploaded.setVisible(false);
		
		this.addField(new HorizontalLayout(this.uploadFile, this.imageFileUploaded));
	}
	
	@Override
	public void save() {
		if(this.appraiser.getFile() == null){
			Notification.show("Enviar Feedback", "É necessário submeter o arquivo.", Notification.Type.ERROR_MESSAGE);
		}else{
			try{
				InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
				
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
	
	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageFileUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
				appraiser.setFileType(DocumentType.fromMimeType(mimeType));
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
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            appraiser.setFile(buffer);
	            
	            imageFileUploaded.setVisible(true);
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
