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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class InternshipUploadFinalReportWindow extends EditWindow {
	
	private final Internship internship;
	
	private final TextField textReportTitle;
	private final Upload uploadFinalReport;
	private final Image imageFinalReportUploaded;
	
	public InternshipUploadFinalReportWindow(Internship internship, ListView parentView){
		super("Enviar Relatório Final", parentView);
		
		this.internship = internship;
		
		this.textReportTitle = new TextField("Título do Relatório Final");
		this.textReportTitle.setWidth("400px");
		
		FinalReportUploader finalReportUploader = new FinalReportUploader();
		this.uploadFinalReport = new Upload("(Formato PDF, Tam. Máx. 5 MB)", finalReportUploader);
		this.uploadFinalReport.addSucceededListener(finalReportUploader);
		this.uploadFinalReport.setButtonCaption("Relatório Final");
		this.uploadFinalReport.setImmediate(true);
		
		this.imageFinalReportUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageFinalReportUploaded.setVisible(false);
		
		HorizontalLayout h5 = new HorizontalLayout(this.uploadFinalReport, this.imageFinalReportUploaded);
		h5.setSpacing(true);
		
		this.addField(this.textReportTitle);
		this.addField(h5);
		
		this.loadInternship();
		this.textReportTitle.focus();
	}
	
	private void loadInternship(){
		this.textReportTitle.setValue(this.internship.getReportTitle());
		
		this.imageFinalReportUploaded.setVisible(this.internship.getFinalReport() != null);
	}
	
	@Override
	public void save() {
		try{
			InternshipBO bo = new InternshipBO();
			
			this.internship.setReportTitle(this.textReportTitle.getValue());
			
			bo.save(this.internship);
			
			Notification.show("Salvar Relatório de Estágio", "Relatório salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Relatório de Estágio", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@SuppressWarnings("serial")
	class FinalReportUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageFinalReportUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
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
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            internship.setFinalReport(buffer);
	            
	            imageFinalReportUploaded.setVisible(true);
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
