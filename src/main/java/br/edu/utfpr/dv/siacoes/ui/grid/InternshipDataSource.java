package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Internship;

public class InternshipDataSource extends BasicDataSource {

	private String student;
	private String company;
	private String supervisor;
	private Date startDate;
	private String type;
	private String status;
	
	public InternshipDataSource(Internship internship) {
		this.setId(internship.getIdInternship());
		this.setStudent(internship.getStudent().getName());
		this.setCompany(internship.getCompany().getName());
		this.setSupervisor(internship.getSupervisor().getName());
		this.setStartDate(internship.getStartDate());
		this.setType(internship.getType().toString());
		this.setStatus(internship.getStatus().toString());
	}
	
	public static List<InternshipDataSource> load(List<Internship> list) {
		List<InternshipDataSource> ret = new ArrayList<InternshipDataSource>();
		
		for(Internship internship : list) {
			ret.add(new InternshipDataSource(internship));
		}
		
		return ret;
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
