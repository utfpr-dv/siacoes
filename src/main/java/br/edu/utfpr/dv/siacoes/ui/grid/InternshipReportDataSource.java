package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;

public class InternshipReportDataSource extends BasicDataSource {
	
	private String title;
	private Date date;
	private String feedback;
	private ReportType type;
	private byte[] file;
	
	public InternshipReportDataSource(InternshipReport report) {
		this.setId(report.getIdInternshipReport());
		this.setTitle(report.isFinalReport() ? "Final" : "Parcial");
		this.setDate(report.getDate());
		this.setFeedback(report.getFeedback().toString());
		this.setType(report.getType());
		this.setFile(report.getReport());
	}
	
	public static List<InternshipReportDataSource> load(List<InternshipReport> list, ReportType type) {
		List<InternshipReportDataSource> ret = new ArrayList<InternshipReportDataSource>();
		int index = 1;
		
		for(InternshipReport report : list) {
			if(report.getType() == type) {
				InternshipReportDataSource rep = new InternshipReportDataSource(report);
				
				if(!report.isFinalReport()) {
					rep.setTitle("Parcial " + String.valueOf(index));
					index++;
				}
				
				ret.add(rep);
			}
		}
		
		return ret;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public ReportType getType() {
		return type;
	}
	public void setType(ReportType type) {
		this.type = type;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}

}
