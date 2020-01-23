package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipReportBO;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;

public class ValidateInternshipReportWindow extends EditWindow {
	
	private final EditInternshipWindow parentWindow;
	private final InternshipReport report;
	
	private final Button buttonDownload;
	private final NativeSelect comboFeedback;
	
	public ValidateInternshipReportWindow(InternshipReport report, EditInternshipWindow parentWindow) {
		super("Validar Relatório de Estágio", null);
		
		this.parentWindow = parentWindow;
		this.report = report;
		
		this.buttonDownload = new Button("Download do Relatório", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadReport();
            }
        });
		this.buttonDownload.setIcon(FontAwesome.DOWNLOAD);
		
		this.comboFeedback = new NativeSelect("Parecer");
		this.comboFeedback.setWidth("200px");
		this.comboFeedback.setNullSelectionAllowed(false);
		this.comboFeedback.addItem(ReportFeedback.NONE);
		this.comboFeedback.addItem(ReportFeedback.APPROVED);
		this.comboFeedback.addItem(ReportFeedback.DISAPPROVED);
		this.comboFeedback.select(ReportFeedback.NONE);
		
		this.addField(this.buttonDownload);
		this.addField(this.comboFeedback);
		
		this.comboFeedback.setValue(this.report.getFeedback());
	}

	@Override
	public void save() {
		try {
			this.report.setFeedback((ReportFeedback)this.comboFeedback.getValue());
			this.report.setFeedbackUser(Session.getUser());
			
			new InternshipReportBO().validateReport(Session.getIdUserLog(), this.report);
			
			this.parentWindow.editReport(this.report);
			
			this.close();
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Relatório de Estágio", e.getMessage());
		}
	}
	
	private void downloadReport() {
		try {
        	this.showReport(this.report.getReport());
    	} catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}

}
