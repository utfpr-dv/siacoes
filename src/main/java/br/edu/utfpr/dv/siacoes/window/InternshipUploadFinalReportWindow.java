package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class InternshipUploadFinalReportWindow extends EditWindow {
	
	private final Internship internship;
	
	private final TextField textReportTitle;
	private final FileUploader uploadFinalReport;
	
	private SigesConfig config;
	
	public InternshipUploadFinalReportWindow(Internship internship, ListView parentView){
		super("Enviar Relatório Final", parentView);
		
		this.internship = internship;
		
		try {
			this.config = new SigesConfigBO().findByDepartment(this.internship.getDepartment().getIdDepartment());
		} catch (Exception e) {
			this.config = new SigesConfig();
		}
		
		this.textReportTitle = new TextField("Título do Relatório Final");
		this.textReportTitle.setWidth("400px");
		
		this.uploadFinalReport = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFinalReport.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFinalReport.setMaxBytesLength(this.config.getMaxFileSize());
		
		this.addField(this.textReportTitle);
		this.addField(this.uploadFinalReport);
		
		this.loadInternship();
		this.textReportTitle.focus();
	}
	
	private void loadInternship(){
		this.textReportTitle.setValue(this.internship.getReportTitle());
	}
	
	@Override
	public void save() {
		try{
			InternshipBO bo = new InternshipBO();
			
			this.internship.setReportTitle(this.textReportTitle.getValue());
			if(this.uploadFinalReport.getUploadedFile() != null) {
				this.internship.setFinalReport(this.uploadFinalReport.getUploadedFile());
			} else if(this.internship.getFinalReport() == null) {
				throw new Exception("É necessário enviar o relatório final.");
			}
			this.internship.setReports(null);
			
			bo.save(Session.getIdUserLog(), this.internship);
			
			this.showSuccessNotification("Salvar Relatório de Estágio", "Relatório salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Relatório de Estágio", e.getMessage());
		}
	}
	
}
