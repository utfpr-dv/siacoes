package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;

public class BugReportDataSource extends BasicDataSource {

	private String description;
	private String type;
	private String status;
	
	public BugReportDataSource(BugReport bug) {
		this.setId(bug.getIdBugReport());
		this.setDescription(bug.getDescription());
		this.setType(bug.getType().toString());
		this.setStatus(bug.getStatus().toString());
	}
	
	public static List<BugReportDataSource> load(List<BugReport> list) {
		List<BugReportDataSource> ret = new ArrayList<BugReportDataSource>();
		
		for(BugReport bug : list) {
			ret.add(new BugReportDataSource(bug));
		}
		
		return ret;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
