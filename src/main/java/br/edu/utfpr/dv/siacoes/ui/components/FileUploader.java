package br.edu.utfpr.dv.siacoes.ui.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.verapdf.core.VeraPDFException;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class FileUploader extends HorizontalLayout {

	private final MemoryBuffer buffer;
	private final Upload uploadFile;
	private final List<DocumentType> acceptedDocumentTypes;
	private FileUploaderListener fileUploadListener;
	private byte[] uploadedFile;
	private DocumentType fileType;
	private boolean validatePDFAFile;
	
	private File tempFile = null;
	
	public FileUploader(String caption) {
		//DocumentUploader listener = new DocumentUploader();
		
		this.buffer = new MemoryBuffer();
		
		this.uploadFile = new Upload(this.buffer);
		this.uploadFile.setMaxFiles(1);
		this.uploadFile.setDropLabel(new Label("Upload a 300 bytes file in .csv format"));
		
		this.uploadFile.addFileRejectedListener(event -> {
			Notification.showErrorNotification("Carregamento do Arquivo", event.getErrorMessage());
		});
		
		this.uploadFile.addSucceededListener(event -> {
			try {
	            if((getMaxBytesLength() > 0) && (event.getContentLength() > getMaxBytesLength())) {
	            	throw new Exception("O arquivo precisa ter um tamanho máximo de " + getStringMaxBytesLength() + ".");
	            }
	            if(!isDocumentTypeAccept(DocumentType.fromMimeType(event.getMIMEType()))) {
					throw new Exception("O arquivo precisa estar em um dos seguintes formatos: " + getStringAcceptedDocumentTypes() + ".");
				}
	            
	            byte[] buf = new byte[(int) event.getContentLength()];
	            
	            buffer.getInputStream().read(buf);
	            
	            if(validatePDFAFile && !validatePDFA(buf)) {
	            	throw new Exception("O arquivo precisa estar no formato PDF/A (padrão utilizado para arquivamento de longo prazo de documentos eletrônicos).");
	            }
	            
	            uploadedFile = buf;
	            
	            //setSuccess();
	            
	            if(fileUploadListener != null) {
	            	fileUploadListener.uploadSucceeded();
	            }
	            
	            Notification.showSuccessNotification("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.");
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	//setError(e.getMessage());
	            
	        	Notification.showErrorNotification("Carregamento do Arquivo", e.getMessage());
	        }
		});
		
		this.add(this.uploadFile);
		
		this.acceptedDocumentTypes = new ArrayList<DocumentType>();
		this.setMaxBytesLength(0);
	}
	
	public void setDropLabel(String label) {
		this.uploadFile.setDropLabel(new Label(label));
	}
	
	public FileUploaderListener getFileUploadListener() {
		return fileUploadListener;
	}

	public void setFileUploadListener(FileUploaderListener fileUploadListener) {
		this.fileUploadListener = fileUploadListener;
	}

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public DocumentType getFileType() {
		return fileType;
	}

	public int getMaxBytesLength() {
		return this.uploadFile.getMaxFileSize();
	}

	public void setMaxBytesLength(int maxBytesLength) {
		this.uploadFile.setMaxFileSize(maxBytesLength);
	}

	public List<DocumentType> getAcceptedDocumentTypes() {
		return acceptedDocumentTypes;
	}
	
	private String getStringMaxBytesLength() {
		double value = (double)this.getMaxBytesLength();
		String[] units = {"bytes", "KB", "MB", "GB", "TB", "PB", "YB"};
		int index = 0;
		
		while(value >= 1024) {
			value = value / 1024;
			index++;
		}
		
		return String.format("%.2f", value) + " " + units[index];
	}
	
	private boolean isDocumentTypeAccept(DocumentType docType) {
		this.validatePDFAFile = false;
		
		if((this.getAcceptedDocumentTypes() == null) || (this.getAcceptedDocumentTypes().size() == 0)) {
			return true;
		}
		
		for(DocumentType doc : this.getAcceptedDocumentTypes()) {
			if(doc == docType) {
				return true;
			}
		}
		
		if(docType == DocumentType.PDF) {
			for(DocumentType doc : this.getAcceptedDocumentTypes()) {
				if(doc == DocumentType.PDFA) {
					this.validatePDFAFile = true;
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean validatePDFA(byte[] file) {
		FileOutputStream fos = null;
		
		this.validatePDFAFile = false;
		
		try {
			File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".pdf");
			tempFile.deleteOnExit();
			fos = new FileOutputStream(tempFile);
			fos.write(file);
			fos.flush();
			
			VeraGreenfieldFoundryProvider.initialise();
			PDFAParser parser = Foundries.defaultInstance().createParser(tempFile);
			return !parser.getFlavour().toString().equals("0");
			
			/*System.out.println(parser.getFlavour().toString());
			PDFAValidator validator = Foundries.defaultInstance().createValidator(parser.getFlavour(), false);
			ValidationResult result = validator.validate(parser);
		    return result.isCompliant();*/
		} catch (IOException e1) {
			Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
			
			return false;
		} catch (VeraPDFException e2) {
			Logger.getGlobal().log(Level.SEVERE, e2.getMessage(), e2);
			
			return false;
		} catch (NoSuchElementException e3) {
			Logger.getGlobal().log(Level.SEVERE, e3.getMessage(), e3);
			
			return false;
		} finally {
			try {
				if(fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private String getStringAcceptedDocumentTypes() {
		if((this.getAcceptedDocumentTypes() == null) || (this.getAcceptedDocumentTypes().size() == 0)) {
			return "(Todos os documentos)";
		}
		
		String ret = this.getAcceptedDocumentTypes().get(0).toString();
		
		for(int i = 1; i < this.getAcceptedDocumentTypes().size(); i++) {
			ret = ret + ", " + this.getAcceptedDocumentTypes().get(i).toString();
		}
		
		return ret;
	}
	
	/*private void setSuccess() {
		this.progressBar.setVisible(false);
		this.imageFileUploaded.setSource(new ThemeResource("images/ok.png"));
		this.imageFileUploaded.setVisible(true);
		this.imageFileUploaded.setDescription(null);
	}
	
	private void setError(String errorMessage) {
		this.progressBar.setVisible(false);
		this.imageFileUploaded.setSource(new ThemeResource("images/error.png"));
		this.imageFileUploaded.setVisible(true);
		this.imageFileUploaded.setDescription(errorMessage);
	}
	
	private void setProgress() {
		this.progressBar.setValue(0.0f);
		this.progressBar.setVisible(true);
		this.imageFileUploaded.setVisible(false);
	}

	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener, StartedListener, ProgressListener {
		private File tempFile = null;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				if(!isDocumentTypeAccept(DocumentType.fromMimeType(mimeType))) {
					throw new Exception("O arquivo precisa estar em um dos seguintes formatos: " + getStringAcceptedDocumentTypes() + ".");
				}

				fileType = DocumentType.fromMimeType(mimeType);
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	setError(e.getMessage());
	            
	        	Notification.showErrorNotification("Carregamento do Arquivo", e.getMessage());
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            
	            if((getMaxBytesLength() > 0) && (input.available() > getMaxBytesLength())) {
	            	throw new Exception("O arquivo precisa ter um tamanho máximo de " + getStringMaxBytesLength() + ".");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            if(validatePDFAFile && !validatePDFA(buffer)) {
	            	throw new Exception("O arquivo precisa estar no formato PDF/A (padrão utilizado para arquivamento de longo prazo de documentos eletrônicos).");
	            }
	            
	            uploadedFile = buffer;
	            
	            setSuccess();
	            
	            if(fileUploadListener != null) {
	            	fileUploadListener.uploadSucceeded();
	            }
	            
	            Notification.showSuccessNotification("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.");
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	setError(e.getMessage());
	            
	        	Notification.showErrorNotification("Carregamento do Arquivo", e.getMessage());
	        }
		}

		@Override
		public void updateProgress(long readBytes, long contentLength) {
			progressBar.setValue((float)readBytes / contentLength);
		}

		@Override
		public void uploadStarted(StartedEvent event) {
			setProgress();
		}
	}*/
	
}
