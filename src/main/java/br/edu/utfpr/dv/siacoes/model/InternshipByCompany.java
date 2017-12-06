package br.edu.utfpr.dv.siacoes.model;

public class InternshipByCompany {

	private int idCompany;
	private String companyName;
	private int totalStudents;
	
	public InternshipByCompany(){
		this.setIdCompany(0);
		this.setCompanyName("");
		this.setTotalStudents(0);
	}
	
	public int getIdCompany() {
		return idCompany;
	}
	public void setIdCompany(int idCompany) {
		this.idCompany = idCompany;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public int getTotalStudents() {
		return totalStudents;
	}
	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}
	
}
