package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;

public class InternshipPosterRequestDataSource extends BasicDataSource {

	private String student;
	private String company;
	private String supervisor;
	private String confirmed;
	
	public InternshipPosterRequestDataSource(InternshipPosterRequest request) {
		this.setId(request.getIdInternshipPosterRequest());
		this.setStudent(request.getInternship().getStudent().getName());
		this.setCompany(request.getInternship().getCompany().getName());
		this.setSupervisor(request.getInternship().getSupervisor().getName());
		this.setConfirmed(request.isConfirmed() ? "Sim" : "Não");
	}
	
	public static List<InternshipPosterRequestDataSource> load(List<InternshipPosterRequest> list) {
		List<InternshipPosterRequestDataSource> ret = new ArrayList<InternshipPosterRequestDataSource>();
		
		for(InternshipPosterRequest request : list) {
			ret.add(new InternshipPosterRequestDataSource(request));
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
	public String getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}
	
}
