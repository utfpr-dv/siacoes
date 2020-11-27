package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipJuryRequestDataSource extends BasicDataSource {

	private String student;
	private String supervisor;
	private String company;
	private String local;
	private LocalDateTime date;
	private String confirmed;
	
	public InternshipJuryRequestDataSource(InternshipJuryRequest jury) {
		this.setId(jury.getIdInternshipJuryRequest());
		this.setDate(DateUtils.convertToLocalDateTime(jury.getDate()));
		this.setLocal(jury.getLocal());
		this.setStudent(jury.getInternship().getStudent().getName());
		this.setSupervisor(jury.getInternship().getSupervisor().getName());
		this.setCompany(jury.getInternship().getCompany().getName());
		this.setConfirmed(jury.isConfirmed() ? "Sim" : "Não");
	}
	
	public InternshipJuryRequestDataSource(InternshipPosterRequest request) {
		this.setId(request.getIdInternshipPosterRequest());
		this.setStudent(request.getInternship().getStudent().getName());
		this.setCompany(request.getInternship().getCompany().getName());
		this.setSupervisor(request.getInternship().getSupervisor().getName());
		this.setConfirmed(request.isConfirmed() ? "Sim" : "Não");
	}
	
	public static List<InternshipJuryRequestDataSource> load(List<InternshipJuryRequest> list) {
		List<InternshipJuryRequestDataSource> ret = new ArrayList<InternshipJuryRequestDataSource>();
		
		for(InternshipJuryRequest jury : list) {
			ret.add(new InternshipJuryRequestDataSource(jury));
		}
		
		return ret;
	}
	
	public static List<InternshipJuryRequestDataSource> load(List<InternshipJuryRequest> list, List<InternshipPosterRequest> list2) {
		List<InternshipJuryRequestDataSource> ret = new ArrayList<InternshipJuryRequestDataSource>();
		
		for(InternshipJuryRequest jury : list) {
			ret.add(new InternshipJuryRequestDataSource(jury));
		}
		
		for(InternshipPosterRequest request : list2) {
			ret.add(new InternshipJuryRequestDataSource(request));
		}
		
		return ret;
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	
}
