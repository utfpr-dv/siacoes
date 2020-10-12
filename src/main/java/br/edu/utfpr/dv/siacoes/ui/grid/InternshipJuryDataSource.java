package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipJuryDataSource extends BasicDataSource {

	private LocalDateTime date;
	private String local;
	private String member;
	private String student;
	private String company;
	
	public InternshipJuryDataSource(InternshipJury jury) {
		this.setId(jury.getIdInternshipJury());
		this.setDate(DateUtils.convertToLocalDateTime(jury.getDate()));
		this.setLocal(jury.getLocal());
		this.setMember("");
		this.setStudent(jury.getInternship().getStudent().getName());
		this.setCompany(jury.getInternship().getCompany().getName());
	}
	
	public static List<InternshipJuryDataSource> load(List<InternshipJury> list) {
		List<InternshipJuryDataSource> ret = new ArrayList<InternshipJuryDataSource>();
		
		for(InternshipJury jury : list) {
			ret.add(new InternshipJuryDataSource(jury));
		}
		
		return ret;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
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
	
}
