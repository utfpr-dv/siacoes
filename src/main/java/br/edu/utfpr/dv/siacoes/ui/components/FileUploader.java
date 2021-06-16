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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class FileUploader extends HorizontalLayout {
	
	public enum AcceptedDocumentType{
		ANY(0), PDF(1), PDFA(2), FORMATTED_DOC(3), PRESENTATION(4), IMAGE(5), ZIP(6);
		
		private final int value; 
		AcceptedDocumentType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public String getTypeDescription() {
			switch(this) {
				case PDF:
					return "do tipo PDF";
				case PDFA:
					return "do tipo PDF/A";
				case FORMATTED_DOC:
					return "do tipo DOC, DOCX ou ODT";
				case PRESENTATION:
					return "do tipo PPT ou PPTX";
				case IMAGE:
					return "do tipo JPG ou PNG";
				case ZIP:
					return "do tipo ZIP";
				default:
					return "de qualquer tipo";
			}
		}
	}

	private final MemoryBuffer buffer;
	private final Upload uploadFile;
	private final List<DocumentType> acceptedDocumentTypes;
	private AcceptedDocumentType acceptedType;
	private FileUploaderListener fileUploadListener;
	private byte[] uploadedFile;
	private DocumentType fileType;
	private boolean validatePDFAFile;
	
	public FileUploader(String caption) {
		this.buffer = new MemoryBuffer();
		
		this.acceptedType = AcceptedDocumentType.ANY;
		
		this.uploadFile = new Upload(this.buffer);
		this.uploadFile.setMaxFiles(1);
		
		Button uploadButton = new Button("Enviar");
		this.uploadFile.setUploadButton(uploadButton);
		
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
	            fileType = DocumentType.fromMimeType(event.getMIMEType());
	            
	            if(fileUploadListener != null) {
	            	fileUploadListener.uploadSucceeded();
	            }
	            
	            Notification.showSuccessNotification("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.");
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	        	Notification.showErrorNotification("Carregamento do Arquivo", e.getMessage());
	        }
		});
		
		this.add(this.uploadFile);
		
		this.acceptedDocumentTypes = new ArrayList<DocumentType>();
	}
	
	private void updateDropLabel() {
		if(this.getMaxBytesLength() > 0) {
			this.uploadFile.setDropLabel(new Label("Envie um arquivo " + this.getAcceptedType().getTypeDescription() + " de até " + this.getStringMaxBytesLength()));	
		} else {
			this.uploadFile.setDropLabel(new Label("Envie um arquivo " + this.getAcceptedType().getTypeDescription()));
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private void updateFileTypes() {
		this.acceptedDocumentTypes.clear();
		
		switch(this.getAcceptedType()) {
			case PDF:
				this.acceptedDocumentTypes.add(DocumentType.PDF);
				this.uploadFile.setAcceptedFileTypes("application/pdf", "application/wps-office.pdf");
				break;
			case PDFA:
				this.acceptedDocumentTypes.add(DocumentType.PDFA);
				this.uploadFile.setAcceptedFileTypes("application/pdf", "application/wps-office.pdf");
				break;
			case FORMATTED_DOC:
				this.acceptedDocumentTypes.add(DocumentType.DOC);
				this.acceptedDocumentTypes.add(DocumentType.DOCX);
				this.acceptedDocumentTypes.add(DocumentType.ODT);
				this.uploadFile.setAcceptedFileTypes("application/msword", "application/vnd.ms-word", "application/x-msword", "application/wps-office.doc", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/wps-office.docx", "application/vnd.oasis.opendocument.text");
				break;
			case PRESENTATION:
				this.acceptedDocumentTypes.add(DocumentType.PPT);
				this.acceptedDocumentTypes.add(DocumentType.PPTX);
				this.uploadFile.setAcceptedFileTypes("application/vnd.ms-powerpoint", "application/powerpoint", "application/mspowerpoint", "application/x-mspowerpoint", "application/wps-office.ppt", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "application/wps-office.pptx");
				break;
			case IMAGE:
				this.acceptedDocumentTypes.add(DocumentType.JPEG);
				this.acceptedDocumentTypes.add(DocumentType.PNG);
				this.uploadFile.setAcceptedFileTypes("image/jpeg", "image/png");
				break;
			case ZIP:
				this.acceptedDocumentTypes.add(DocumentType.ZIP);
				this.uploadFile.setAcceptedFileTypes("application/zip", "application/x-zip-compressed");
				break;
		}
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
		this.updateDropLabel();
	}

	public AcceptedDocumentType getAcceptedType() {
		return this.acceptedType;
	}
	
	public void setAcceptedType(AcceptedDocumentType type) {
		this.acceptedType = type;
		this.updateFileTypes();
		this.updateDropLabel();
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
		
		if((this.acceptedDocumentTypes == null) || (this.acceptedDocumentTypes.size() == 0)) {
			return true;
		}
		
		for(DocumentType doc : this.acceptedDocumentTypes) {
			if(doc == docType) {
				return true;
			}
		}
		
		if(docType == DocumentType.PDF) {
			for(DocumentType doc : this.acceptedDocumentTypes) {
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
		if((this.acceptedDocumentTypes == null) || (this.acceptedDocumentTypes.size() == 0)) {
			return "(Todos os documentos)";
		}
		
		String ret = this.acceptedDocumentTypes.get(0).toString();
		
		for(int i = 1; i < this.acceptedDocumentTypes.size(); i++) {
			ret = ret + ", " + this.acceptedDocumentTypes.get(i).toString();
		}
		
		return ret;
	}
	
}
