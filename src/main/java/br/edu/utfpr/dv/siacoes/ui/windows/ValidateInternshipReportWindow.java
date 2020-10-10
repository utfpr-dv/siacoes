package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipReportBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;

public class ValidateInternshipReportWindow extends EditWindow {
	
	private final EditInternshipWindow parentWindow;
	private final InternshipReport report;
	
	private final Button buttonDownload;
	private final Select<ReportFeedback> comboFeedback;
	
	public ValidateInternshipReportWindow(InternshipReport report, EditInternshipWindow parentWindow) {
		super("Validar Relatório de Estágio", null);
		
		this.parentWindow = parentWindow;
		this.report = report;
		
		this.buttonDownload = new Button("Download do Relatório", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadReport();
        });
		
		this.comboFeedback = new Select<ReportFeedback>();
		this.comboFeedback.setLabel("Parecer");
		this.comboFeedback.setWidth("250px");
		this.comboFeedback.setItems(ReportFeedback.NONE, ReportFeedback.APPROVED, ReportFeedback.DISAPPROVED);
		this.comboFeedback.setValue(ReportFeedback.NONE);
		
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Relatório de Estágio", e.getMessage());
		}
	}
	
	private void downloadReport() {
		try {
        	this.showReport(this.report.getReport());
    	} catch (Exception e) {
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	this.showErrorNotification("Download do Arquivo", e.getMessage());
		}
	}

}
