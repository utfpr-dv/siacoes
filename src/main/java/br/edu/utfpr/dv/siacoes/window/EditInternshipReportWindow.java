package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipReportBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;

public class EditInternshipReportWindow extends EditWindow {
	
	private final EditInternshipWindow parentWindow;
	
	private final FileUploader uploadFile;
	private final CheckBox checkFinal;
	
	private final InternshipReport report;
	
	private SigesConfig config;
	
	public EditInternshipReportWindow(EditInternshipWindow parentWindow, Internship internship, ReportType type) {
		super("Relatório de Estágio", null);
		
		this.parentWindow = parentWindow;
		
		this.report = new InternshipReport();
		this.report.setInternship(internship);
		this.report.setType(type);
		
		try {
			this.config = new SigesConfigBO().findByDepartment(internship.getDepartment().getIdDepartment());
		} catch (Exception e1) {
			this.config = new SigesConfig();
		}
		
		this.uploadFile = new FileUploader("(Formato PDF, " + this.config.getMaxFileSizeAsString() + ")");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					report.setReport(uploadFile.getUploadedFile());
				}
			}
		});
		
		this.checkFinal = new CheckBox("Relatório Final");
		
		if(type == ReportType.STUDENT) {
			this.checkFinal.setVisible(false);
		}
		
		this.addField(this.uploadFile);
		this.addField(this.checkFinal);
	}

	@Override
	public void save() {
		try {
			this.report.setFinalReport(this.checkFinal.getValue());
			
			if(this.report.getInternship().getIdInternship() != 0) {
				new InternshipReportBO().save(Session.getIdUserLog(), this.report);
				
				this.showSuccessNotification("Relatório de Estágio", "Relatório de estágio salvo com sucesso.");
			}
			
			if(this.parentWindow != null) {
				this.parentWindow.addReport(this.report);	
			}
			
			this.close();
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Relatório de Estágio", e.getMessage());
		}
	}

}
