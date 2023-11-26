package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipDataSource extends BasicDataSource {

	private String student;
	private String company;
	private String supervisor;
	private LocalDate startDate;
	private LocalDate endDate;
	private String type;
	private String status;
	
	public InternshipDataSource(Internship internship) {
		this.setId(internship.getIdInternship());
		this.setStudent(internship.getStudent().getName());
		this.setCompany(internship.getCompany().getName());
		this.setSupervisor(internship.getSupervisor().getName());
		this.setStartDate(DateUtils.convertToLocalDate(internship.getStartDate()));
		this.setEndDate(DateUtils.convertToLocalDate(internship.getEndDate()));
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
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
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
